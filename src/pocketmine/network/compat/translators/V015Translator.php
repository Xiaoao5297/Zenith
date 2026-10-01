<?php

/*
 * ██╗   ██╗    ██████╗ ██████╗ ██████╗ ███████╗
 * ██║   ██║   ██╔════╝██╔═══██╗██╔══██╗██╔════╝
 * ██║   ██║   ██║     ██║   ██║██████╔╝█████╗
 * ██║   ██║   ██║     ██║   ██║██╔══██╗██╔══╝
 * ╚██████╔╝██╗╚██████╗╚██████╔╝██║  ██║███████╗
 *  ╚═════╝ ╚═╝ ╚═════╝ ╚═════╝ ╚═╝  ╚═╝╚══════╝
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * @Author: U core
 *
 * @Links:
 *  > LY Core
 *  > LY Core Project
*/

namespace pocketmine\network\compat\translators;

use pocketmine\entity\Entity;
use pocketmine\network\compat\PacketCodecRegistry;
use pocketmine\network\protocol\DataPacket;
use pocketmine\network\protocol\ProtocolCompatibility;
use pocketmine\network\protocol\v84\BatchPacketV84;
use pocketmine\network\protocol\v84\DataPacketV84;
use pocketmine\network\protocol\v84\InfoV84;
use pocketmine\network\protocol\v84\LoginPacketV84;
use pocketmine\network\protocol as protocol;
use pocketmine\Player;
use pocketmine\utils\Binary;
use pocketmine\utils\UUID;

/**
 * 0.15 (v84) 协议的数据包翻译器：核心 <-> v84，以及批处理/区块负载的字节级重映射。
 */
final class V015Translator{

	private function __construct(){
	}

	public static function toCorePacketV84(DataPacketV84 $packet){
		if($packet instanceof LoginPacketV84){
			return self::convertLoginPacket($packet);
		}

		if($packet instanceof BatchPacketV84){
			$pk = new protocol\BatchPacket();
			$pk->payload = $packet->payload;
			return $pk;
		}

		$class = PacketCodecRegistry::v84ToCore()[$packet::NETWORK_ID] ?? null;
		if($class === null){
			return $packet;
		}

		/** @var DataPacket $pk */
		$pk = new $class;
		self::copyPublicProperties($packet, $pk);
		if(property_exists($pk, "protocol")){
			$pk->protocol = isset($packet->protocol) ? (int) $packet->protocol : InfoV84::CURRENT_PROTOCOL;
		}

		return $pk;
	}

	public static function sanitizeProtocol015V84Packet(DataPacketV84 $packet, Player $player = null) : DataPacketV84{
		$filtered = $packet;
		$changed = false;

		if($packet instanceof BatchPacketV84 and is_string($packet->payload)){
			$payload = self::sanitizeProtocol015V84BatchPayload($packet->payload, $player);
			if($payload !== $packet->payload){
				$filtered = clone $packet;
				$filtered->payload = $payload;
				$changed = true;
			}
		}

		if($filtered instanceof protocol\v84\SetEntityLinkPacketV84){
			$type = self::remapProtocol015LinkType((int) $filtered->type);
			if($type !== (int) $filtered->type){
				if(!$changed){
					$filtered = clone $packet;
					$changed = true;
				}
				$filtered->type = $type;
			}
		}

		if($filtered instanceof protocol\v84\AddEntityPacketV84 and is_array($filtered->links)){
			$links = self::remapProtocol015Links($filtered->links);
			if($links !== $filtered->links){
				if(!$changed){
					$filtered = clone $packet;
					$changed = true;
				}
				$filtered->links = $links;
			}
		}

		if(property_exists($filtered, "metadata") and is_array($filtered->metadata)){
			$metadata = $filtered instanceof protocol\v84\AddEntityPacketV84 ?
				ProtocolCompatibility::filterEntityMetadataForProtocol(InfoV84::CURRENT_PROTOCOL, (int) $filtered->type, $filtered->metadata) :
				ProtocolCompatibility::filterMetadataForProtocol(InfoV84::CURRENT_PROTOCOL, $filtered->metadata);
			$metadata = self::remapProtocol015LeadHolderForViewer($metadata, $player);

			if($metadata !== $filtered->metadata){
				if(!$changed){
					$filtered = clone $packet;
					$changed = true;
				}
				$filtered->metadata = $metadata;
			}
		}

		if($changed){
			self::markV84PacketDirty($filtered);
		}

		return $filtered;
	}

	private static function sanitizeProtocol015V84BatchPayload(string $payload, Player $player = null) : string{
		$str = zlib_decode($payload, 1024 * 1024 * 64);
		if($str === false){
			return $payload;
		}

		$out = "";
		$changed = false;
		$len = strlen($str);
		$offset = 0;
		while($offset < $len){
			if($offset + 4 > $len){
				break;
			}
			$pkLen = Binary::readInt(substr($str, $offset, 4));
			$offset += 4;
			$buffer = substr($str, $offset, $pkLen);
			$offset += $pkLen;
			$sanitized = self::sanitizeProtocol015V84PacketBuffer($buffer, $player);
			if($sanitized !== $buffer){
				$changed = true;
			}
			$out .= Binary::writeInt(strlen($sanitized)) . $sanitized;
		}

		return $changed ? zlib_encode($out, ZLIB_ENCODING_DEFLATE, 7) : $payload;
	}

	private static function sanitizeProtocol015V84PacketBuffer(string $buffer, Player $player = null) : string{
		$header = ProtocolCompatibility::readPacketHeader($buffer);
		if($header === null){
			return $buffer;
		}

		[$pid, $packetOffset] = $header;
		$remapped = self::remapProtocol015V84PacketBufferThroughCore($buffer, $pid, $packetOffset, $player);
		if($remapped !== null){
			return $remapped;
		}

		if($pid === InfoV84::SET_ENTITY_LINK_PACKET){
			return self::remapProtocol015SetEntityLinkBuffer($buffer, $packetOffset);
		}

		if($pid === InfoV84::ADD_ENTITY_PACKET){
			return self::remapProtocol015AddEntityLinksBuffer($buffer, $packetOffset);
		}

		return $buffer;
	}

	private static function remapProtocol015V84PacketBufferThroughCore(string $buffer, int $pid, int $packetOffset, Player $player = null) : ?string{
		if($pid !== InfoV84::ADD_PLAYER_PACKET and
				$pid !== InfoV84::ADD_ENTITY_PACKET and
				$pid !== InfoV84::SET_ENTITY_DATA_PACKET and
				$pid !== InfoV84::SET_ENTITY_LINK_PACKET){
			return null;
		}

		$coreClass = PacketCodecRegistry::v84ToCore()[$pid] ?? null;
		if($coreClass === null){
			return null;
		}

		try{
			/** @var DataPacket $packet */
			$packet = new $coreClass;
			$packet->protocol = protocol\Info::V014_CURRENT_PROTOCOL;
			$packet->setBuffer($buffer, $packetOffset);
			$packet->decode();
			$v84 = self::toProtocol015Packet($packet, $player);
			if(!$v84 instanceof DataPacketV84){
				return null;
			}

			$v84->encode();
			$v84->isEncoded = true;
			return $v84->buffer;
		}catch(\Throwable $e){
			return null;
		}
	}

	private static function remapProtocol015SetEntityLinkBuffer(string $buffer, int $packetOffset) : string{
		$typeOffset = $packetOffset + 16;
		if(!isset($buffer[$typeOffset])){
			return $buffer;
		}

		$type = self::remapProtocol015LinkType(ord($buffer[$typeOffset]));
		if($type === ord($buffer[$typeOffset])){
			return $buffer;
		}

		$buffer[$typeOffset] = chr($type);
		return $buffer;
	}

	private static function remapProtocol015AddEntityLinksBuffer(string $buffer, int $packetOffset) : string{
		$metadataOffset = $packetOffset + 8 + 4 + (8 * 4) + 4;
		$linksOffset = self::skipProtocol015Metadata($buffer, $metadataOffset);
		if($linksOffset === null or strlen($buffer) < $linksOffset + 2){
			return $buffer;
		}

		$count = Binary::readShort(substr($buffer, $linksOffset, 2));
		$linkOffset = $linksOffset + 2;
		for($i = 0; $i < $count; ++$i){
			$typeOffset = $linkOffset + 16;
			if(!isset($buffer[$typeOffset])){
				break;
			}
			$type = self::remapProtocol015LinkType(ord($buffer[$typeOffset]));
			if($type !== ord($buffer[$typeOffset])){
				$buffer[$typeOffset] = chr($type);
			}
			$linkOffset += 17;
		}

		return $buffer;
	}

	private static function skipProtocol015Metadata(string $buffer, int $offset) : ?int{
		$len = strlen($buffer);
		while($offset < $len){
			$header = ord($buffer[$offset]);
			++$offset;
			if($header === 0x7f){
				return $offset;
			}

			$type = $header >> 5;
			switch($type){
				case Entity::DATA_TYPE_BYTE:
					$offset += 1;
					break;
				case Entity::DATA_TYPE_SHORT:
					$offset += 2;
					break;
				case Entity::DATA_TYPE_INT:
				case Entity::DATA_TYPE_FLOAT:
					$offset += 4;
					break;
				case Entity::DATA_TYPE_STRING:
					if($offset + 2 > $len){
						return null;
					}
					$stringLength = Binary::readLShort(substr($buffer, $offset, 2));
					$offset += 2 + $stringLength;
					break;
				case Entity::DATA_TYPE_SLOT:
					$offset += 5;
					break;
				case Entity::DATA_TYPE_POS:
					$offset += 12;
					break;
				case Entity::DATA_TYPE_LONG:
					$offset += 8;
					break;
				default:
					return null;
			}
		}

		return null;
	}

	private static function markV84PacketDirty(DataPacketV84 $packet) : void{
		$packet->isEncoded = false;
		$packet->buffer = "";
		$packet->offset = 0;
		$packet->clearEncapsulatedPacketCache();
	}

	public static function toProtocol015Packet(DataPacket $packet, Player $player = null){
		if($packet instanceof protocol\BatchPacket){
			$pk = new BatchPacketV84();
			$pk->payload = self::sanitizeProtocol015V84BatchPayload(self::remapBatchPayloadToProtocol015($packet->payload, $player), $player);
			$pk->isEncoded = false;
			return $pk;
		}

		$class = PacketCodecRegistry::coreToV84()[$packet::NETWORK_ID] ?? null;
		if($class === null){
			return $packet;
		}

		/** @var DataPacketV84 $pk */
		$pk = new $class;
		self::copyPublicProperties($packet, $pk);
		self::patchProtocol015Packet($pk, $packet, $player);
		return $pk;
	}

	public static function toProtocol015Packets(DataPacket $packet, Player $player = null) : array{
		if($packet instanceof protocol\UpdateBlockPacket){
			$packets = [];
			foreach($packet->records as $record){
				$pk = new protocol\v84\UpdateBlockPacketV84();
				$pk->x = $record[0];
				$pk->z = $record[1];
				$pk->y = $record[2];
				$pk->blockId = $record[3];
				$pk->blockData = $record[4];
				$pk->flags = $record[5];
				$packets[] = $pk;
			}
			return $packets;
		}

		if($packet instanceof protocol\MoveEntityPacket){
			$packets = [];
			foreach($packet->entities as $entity){
				$pk = new protocol\v84\MoveEntityPacketV84();
				$pk->eid = $entity[0];
				$pk->x = $entity[1];
				$pk->y = $entity[2];
				$pk->z = $entity[3];
				$pk->yaw = $entity[4];
				$pk->headYaw = $entity[5];
				$pk->pitch = $entity[6];
				$packets[] = $pk;
			}
			return $packets;
		}

		$packet015 = self::toProtocol015Packet($packet, $player);
		return $packet015 instanceof DataPacketV84 ? [$packet015] : [];
	}

	public static function remapBatchPayloadToProtocol015(string $payload, Player $player = null) : string{
		$str = zlib_decode($payload, 1024 * 1024 * 64);
		if($str === false){
			return $payload;
		}

		$out = "";
		$changed = false;
		$len = strlen($str);
		$offset = 0;
		while($offset < $len){
			if($offset + 4 > $len){
				break;
			}
			$pkLen = Binary::readInt(substr($str, $offset, 4));
			$offset += 4;
			$buffer = substr($str, $offset, $pkLen);
			$offset += $pkLen;
			$remappedBuffer = self::remapPacketBufferToProtocol015($buffer, $player);
			if($remappedBuffer !== $buffer){
				$changed = true;
			}
			$out .= Binary::writeInt(strlen($remappedBuffer)) . $remappedBuffer;
		}

		return $changed ? zlib_encode($out, ZLIB_ENCODING_DEFLATE, 7) : $payload;
	}

	public static function remapPacketBufferToProtocol015(string $buffer, Player $player = null) : string{
		$header = ProtocolCompatibility::readPacketHeader($buffer);
		if($header === null){
			return $buffer;
		}

		[$pid, $packetOffset] = $header;
		$class = PacketCodecRegistry::coreToV84()[$pid] ?? null;
		if($class === null){
			return $buffer;
		}

		$coreClass = PacketCodecRegistry::corePacket()[$pid] ?? null;
		if($coreClass === null){
			return $buffer;
		}

		/** @var DataPacket $packet */
		$packet = new $coreClass;
		$packet->protocol = protocol\Info::V014_CURRENT_PROTOCOL;
		$packet->setBuffer($buffer, $packetOffset);
		$packet->decode();
		$v84 = self::toProtocol015Packet($packet, $player);
		if(!$v84 instanceof DataPacketV84){
			return $buffer;
		}

		$v84->encode();
		$v84->isEncoded = true;
		return $v84->buffer;
	}

	private static function copyPublicProperties($from, $to) : void{
		foreach(get_object_vars($from) as $name => $value){
			if($name === "buffer" or $name === "offset" or $name === "isEncoded"){
				continue;
			}
			$to->{$name} = $value;
		}
	}

	private static function patchProtocol015Packet(DataPacketV84 $packet, DataPacket $source, Player $player = null) : void{
		if($packet instanceof protocol\v84\RemoveEntityPacketV84 and $source instanceof protocol\RemovePlayerPacket){
			$packet->eid = $source->eid;
		}

		if($packet instanceof protocol\v84\ChangeDimensionPacketV84){
			if(isset($source->x, $source->y, $source->z)){
				$packet->x = $source->x;
				$packet->y = $source->y;
				$packet->z = $source->z;
			}elseif($player !== null){
				$packet->x = $player->x;
				$packet->y = $player->y;
				$packet->z = $player->z;
			}else{
				$packet->x = 0;
				$packet->y = 0;
				$packet->z = 0;
			}
		}

		if($packet instanceof protocol\v84\StartGamePacketV84 and !is_string($packet->unknown)){
			$packet->unknown = "";
		}

		if(property_exists($packet, "metadata") and is_array($packet->metadata)){
			$packet->metadata = $packet instanceof protocol\v84\AddEntityPacketV84 ?
				ProtocolCompatibility::filterEntityMetadataForProtocol(InfoV84::CURRENT_PROTOCOL, (int) $packet->type, $packet->metadata) :
				ProtocolCompatibility::filterMetadataForProtocol(InfoV84::CURRENT_PROTOCOL, $packet->metadata);
			$packet->metadata = self::remapProtocol015LeadHolderForViewer($packet->metadata, $player);
		}

		if($packet instanceof protocol\v84\SetEntityLinkPacketV84){
			$packet->type = self::remapProtocol015LinkType((int) $packet->type);
		}

		if($packet instanceof protocol\v84\AddEntityPacketV84 and is_array($packet->links)){
			$packet->links = self::remapProtocol015Links($packet->links);
		}

		if(property_exists($packet, "protocol")){
			$packet->protocol = InfoV84::CURRENT_PROTOCOL;
		}
	}

	private static function remapProtocol015Links(array $links) : array{
		$remapped = [];
		foreach($links as $link){
			if(is_array($link) and array_key_exists(2, $link)){
				$link[2] = self::remapProtocol015LinkType((int) $link[2]);
			}
			$remapped[] = $link;
		}

		return $remapped;
	}

	private static function remapProtocol015LinkType(int $type) : int{
		if($type === protocol\SetEntityLinkPacket::TYPE_RIDE){
			return protocol\v84\SetEntityLinkPacketV84::TYPE_PASSENGER;
		}

		if($type === protocol\SetEntityLinkPacket::TYPE_REMOVE){
			return 3;
		}

		return $type;
	}

	private static function remapProtocol015LeadHolderForViewer(array $metadata, Player $player = null) : array{
		if($player === null or !isset($metadata[Entity::DATA_LEAD_HOLDER]) or !is_array($metadata[Entity::DATA_LEAD_HOLDER])){
			return $metadata;
		}

		$holder = $metadata[Entity::DATA_LEAD_HOLDER];
		if(!array_key_exists(0, $holder) or !array_key_exists(1, $holder)){
			return $metadata;
		}

		if((int) $holder[0] === Entity::DATA_TYPE_LONG and (int) $holder[1] === (int) $player->getId()){
			$metadata[Entity::DATA_LEAD_HOLDER] = [Entity::DATA_TYPE_LONG, 0];
		}

		return $metadata;
	}

	private static function convertLoginPacket(LoginPacketV84 $packet) : protocol\LoginPacket{
		$pk = new protocol\LoginPacket();
		$pk->username = isset($packet->username) ? $packet->username : "";
		$pk->protocol1 = (int) $packet->protocol;
		$pk->protocol2 = (int) $packet->protocol;
		$pk->clientId = isset($packet->clientId) ? $packet->clientId : 0;
		$pk->clientUUID = isset($packet->clientUUID) ? UUID::fromString($packet->clientUUID) : new UUID();
		$pk->serverAddress = isset($packet->serverAddress) ? $packet->serverAddress : "";
		$pk->clientSecret = "";
		$pk->skinName = isset($packet->skinId) ? $packet->skinId : "";
		$pk->skin = isset($packet->skin) ? $packet->skin : "";
		return $pk;
	}
}
