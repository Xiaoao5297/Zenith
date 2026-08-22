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

namespace pocketmine\network;

use pocketmine\entity\Entity;
use pocketmine\item\Item;
use pocketmine\network\protocol as protocol;
use pocketmine\network\protocol\BatchPacket;
use pocketmine\network\protocol\DataPacket;
use pocketmine\network\protocol\Info;
use pocketmine\network\protocol\ProtocolCompatibility;
use pocketmine\network\protocol\v11\BatchPacket as BatchPacketV11;
use pocketmine\network\protocol\v11\DataPacket as DataPacketV11;
use pocketmine\network\protocol\v11\Info as InfoV11;
use pocketmine\network\protocol\v11\LoginPacket as LoginPacketV11;
use pocketmine\network\protocol\v84\BatchPacketV84;
use pocketmine\network\protocol\v84\DataPacketV84;
use pocketmine\network\protocol\v84\InfoV84;
use pocketmine\network\protocol\v84\LoginPacketV84;
use pocketmine\Player;
use pocketmine\utils\Binary;
use pocketmine\utils\UUID;

class DataPacketManager{
	private const ENTITY_TYPE_CREEPER = 33;
	private const ENTITY_DATA_CREEPER_POWERED = 19;

	private static $v11PacketMap = null;
	private static $v84PacketMap = null;
	private static $coreToV84 = null;
	private static $v84ToCore = null;

	public static function getProtocol011PacketMap() : array{
		if(self::$v11PacketMap === null){
			self::$v11PacketMap = [
				InfoV11::LOGIN_PACKET => protocol\v11\LoginPacket::class,
				InfoV11::PLAY_STATUS_PACKET => protocol\v11\PlayStatusPacket::class,
				InfoV11::DISCONNECT_PACKET => protocol\v11\DisconnectPacket::class,
				InfoV11::TEXT_PACKET => protocol\v11\TextPacket::class,
				InfoV11::SET_TIME_PACKET => protocol\v11\SetTimePacket::class,
				InfoV11::START_GAME_PACKET => protocol\v11\StartGamePacket::class,
				InfoV11::ADD_PLAYER_PACKET => protocol\v11\AddPlayerPacket::class,
				InfoV11::REMOVE_PLAYER_PACKET => protocol\v11\RemovePlayerPacket::class,
				InfoV11::ADD_ENTITY_PACKET => protocol\v11\AddEntityPacket::class,
				InfoV11::REMOVE_ENTITY_PACKET => protocol\v11\RemoveEntityPacket::class,
				InfoV11::ADD_ITEM_ENTITY_PACKET => protocol\v11\AddItemEntityPacket::class,
				InfoV11::TAKE_ITEM_ENTITY_PACKET => protocol\v11\TakeItemEntityPacket::class,
				InfoV11::MOVE_ENTITY_PACKET => protocol\v11\MoveEntityPacket::class,
				InfoV11::MOVE_PLAYER_PACKET => protocol\v11\MovePlayerPacket::class,
				InfoV11::REMOVE_BLOCK_PACKET => protocol\v11\RemoveBlockPacket::class,
				InfoV11::UPDATE_BLOCK_PACKET => protocol\v11\UpdateBlockPacket::class,
				InfoV11::ADD_PAINTING_PACKET => protocol\v11\AddPaintingPacket::class,
				InfoV11::EXPLODE_PACKET => protocol\v11\ExplodePacket::class,
				InfoV11::LEVEL_EVENT_PACKET => protocol\v11\LevelEventPacket::class,
				InfoV11::TILE_EVENT_PACKET => protocol\v11\TileEventPacket::class,
				InfoV11::ENTITY_EVENT_PACKET => protocol\v11\EntityEventPacket::class,
				InfoV11::MOB_EFFECT_PACKET => protocol\v11\MobEffectPacket::class,
				InfoV11::PLAYER_EQUIPMENT_PACKET => protocol\v11\PlayerEquipmentPacket::class,
				InfoV11::PLAYER_ARMOR_EQUIPMENT_PACKET => protocol\v11\PlayerArmorEquipmentPacket::class,
				InfoV11::INTERACT_PACKET => protocol\v11\InteractPacket::class,
				InfoV11::USE_ITEM_PACKET => protocol\v11\UseItemPacket::class,
				InfoV11::PLAYER_ACTION_PACKET => protocol\v11\PlayerActionPacket::class,
				InfoV11::HURT_ARMOR_PACKET => protocol\v11\HurtArmorPacket::class,
				InfoV11::SET_ENTITY_DATA_PACKET => protocol\v11\SetEntityDataPacket::class,
				InfoV11::SET_ENTITY_MOTION_PACKET => protocol\v11\SetEntityMotionPacket::class,
				InfoV11::SET_ENTITY_LINK_PACKET => protocol\v11\SetEntityLinkPacket::class,
				InfoV11::SET_HEALTH_PACKET => protocol\v11\SetHealthPacket::class,
				InfoV11::SET_SPAWN_POSITION_PACKET => protocol\v11\SetSpawnPositionPacket::class,
				InfoV11::ANIMATE_PACKET => protocol\v11\AnimatePacket::class,
				InfoV11::RESPAWN_PACKET => protocol\v11\RespawnPacket::class,
				InfoV11::DROP_ITEM_PACKET => protocol\v11\DropItemPacket::class,
				InfoV11::CONTAINER_OPEN_PACKET => protocol\v11\ContainerOpenPacket::class,
				InfoV11::CONTAINER_CLOSE_PACKET => protocol\v11\ContainerClosePacket::class,
				InfoV11::CONTAINER_SET_SLOT_PACKET => protocol\v11\ContainerSetSlotPacket::class,
				InfoV11::CONTAINER_SET_DATA_PACKET => protocol\v11\ContainerSetDataPacket::class,
				InfoV11::CONTAINER_SET_CONTENT_PACKET => protocol\v11\ContainerSetContentPacket::class,
				InfoV11::ADVENTURE_SETTINGS_PACKET => protocol\v11\AdventureSettingsPacket::class,
				InfoV11::TILE_ENTITY_DATA_PACKET => protocol\v11\TileEntityDataPacket::class,
				InfoV11::FULL_CHUNK_DATA_PACKET => protocol\v11\FullChunkDataPacket::class,
				InfoV11::SET_DIFFICULTY_PACKET => protocol\v11\SetDifficultyPacket::class,
				InfoV11::BATCH_PACKET => protocol\v11\BatchPacket::class,
			];
		}

		return self::$v11PacketMap;
	}

	public static function getProtocol015PacketMap() : array{
		if(self::$v84PacketMap === null){
			self::$v84PacketMap = [
				InfoV84::LOGIN_PACKET => protocol\v84\LoginPacketV84::class,
				InfoV84::PLAY_STATUS_PACKET => protocol\v84\PlayStatusPacketV84::class,
				InfoV84::DISCONNECT_PACKET => protocol\v84\DisconnectPacketV84::class,
				InfoV84::BATCH_PACKET => protocol\v84\BatchPacketV84::class,
				InfoV84::TEXT_PACKET => protocol\v84\TextPacketV84::class,
				InfoV84::SET_TIME_PACKET => protocol\v84\SetTimePacketV84::class,
				InfoV84::START_GAME_PACKET => protocol\v84\StartGamePacketV84::class,
				InfoV84::ADD_PLAYER_PACKET => protocol\v84\AddPlayerPacketV84::class,
				InfoV84::ADD_ENTITY_PACKET => protocol\v84\AddEntityPacketV84::class,
				InfoV84::REMOVE_ENTITY_PACKET => protocol\v84\RemoveEntityPacketV84::class,
				InfoV84::ADD_ITEM_ENTITY_PACKET => protocol\v84\AddItemEntityPacketV84::class,
				InfoV84::TAKE_ITEM_ENTITY_PACKET => protocol\v84\TakeItemEntityPacketV84::class,
				InfoV84::MOVE_ENTITY_PACKET => protocol\v84\MoveEntityPacketV84::class,
				InfoV84::MOVE_PLAYER_PACKET => protocol\v84\MovePlayerPacketV84::class,
				InfoV84::REMOVE_BLOCK_PACKET => protocol\v84\RemoveBlockPacketV84::class,
				InfoV84::UPDATE_BLOCK_PACKET => protocol\v84\UpdateBlockPacketV84::class,
				InfoV84::ADD_PAINTING_PACKET => protocol\v84\AddPaintingPacketV84::class,
				InfoV84::EXPLODE_PACKET => protocol\v84\ExplodePacketV84::class,
				InfoV84::LEVEL_EVENT_PACKET => protocol\v84\LevelEventPacketV84::class,
				InfoV84::RIDER_JUMP_PACKET => protocol\v84\RiderJumpPacketV84::class,
				InfoV84::BLOCK_EVENT_PACKET => protocol\v84\BlockEventPacketV84::class,
				InfoV84::ENTITY_EVENT_PACKET => protocol\v84\EntityEventPacketV84::class,
				InfoV84::MOB_EFFECT_PACKET => protocol\v84\MobEffectPacketV84::class,
				InfoV84::UPDATE_ATTRIBUTES_PACKET => protocol\v84\UpdateAttributesPacketV84::class,
				InfoV84::MOB_EQUIPMENT_PACKET => protocol\v84\MobEquipmentPacketV84::class,
				InfoV84::MOB_ARMOR_EQUIPMENT_PACKET => protocol\v84\MobArmorEquipmentPacketV84::class,
				InfoV84::INTERACT_PACKET => protocol\v84\InteractPacketV84::class,
				InfoV84::USE_ITEM_PACKET => protocol\v84\UseItemPacketV84::class,
				InfoV84::PLAYER_ACTION_PACKET => protocol\v84\PlayerActionPacketV84::class,
				InfoV84::HURT_ARMOR_PACKET => protocol\v84\HurtArmorPacketV84::class,
				InfoV84::SET_ENTITY_DATA_PACKET => protocol\v84\SetEntityDataPacketV84::class,
				InfoV84::SET_ENTITY_MOTION_PACKET => protocol\v84\SetEntityMotionPacketV84::class,
				InfoV84::SET_ENTITY_LINK_PACKET => protocol\v84\SetEntityLinkPacketV84::class,
				InfoV84::SET_HEALTH_PACKET => protocol\v84\SetHealthPacketV84::class,
				InfoV84::SET_SPAWN_POSITION_PACKET => protocol\v84\SetSpawnPositionPacketV84::class,
				InfoV84::ANIMATE_PACKET => protocol\v84\AnimatePacketV84::class,
				InfoV84::RESPAWN_PACKET => protocol\v84\RespawnPacketV84::class,
				InfoV84::DROP_ITEM_PACKET => protocol\v84\DropItemPacketV84::class,
				InfoV84::CONTAINER_OPEN_PACKET => protocol\v84\ContainerOpenPacketV84::class,
				InfoV84::CONTAINER_CLOSE_PACKET => protocol\v84\ContainerClosePacketV84::class,
				InfoV84::CONTAINER_SET_SLOT_PACKET => protocol\v84\ContainerSetSlotPacketV84::class,
				InfoV84::CONTAINER_SET_DATA_PACKET => protocol\v84\ContainerSetDataPacketV84::class,
				InfoV84::CONTAINER_SET_CONTENT_PACKET => protocol\v84\ContainerSetContentPacketV84::class,
				InfoV84::CRAFTING_DATA_PACKET => protocol\v84\CraftingDataPacketV84::class,
				InfoV84::CRAFTING_EVENT_PACKET => protocol\v84\CraftingEventPacketV84::class,
				InfoV84::ADVENTURE_SETTINGS_PACKET => protocol\v84\AdventureSettingsPacketV84::class,
				InfoV84::BLOCK_ENTITY_DATA_PACKET => protocol\v84\BlockEntityDataPacketV84::class,
				InfoV84::PLAYER_INPUT_PACKET => protocol\v84\PlayerInputPacketV84::class,
				InfoV84::FULL_CHUNK_DATA_PACKET => protocol\v84\FullChunkDataPacketV84::class,
				InfoV84::SET_DIFFICULTY_PACKET => protocol\v84\SetDifficultyPacketV84::class,
				InfoV84::CHANGE_DIMENSION_PACKET => protocol\v84\ChangeDimensionPacketV84::class,
				InfoV84::SET_PLAYER_GAMETYPE_PACKET => protocol\v84\SetPlayerGameTypePacketV84::class,
				InfoV84::PLAYER_LIST_PACKET => protocol\v84\PlayerListPacketV84::class,
				InfoV84::TELEMETRY_EVENT_PACKET => protocol\v84\TelemetryEventPacketV84::class,
				InfoV84::CLIENTBOUND_MAP_ITEM_DATA_PACKET => protocol\v84\ClientboundMapItemDataPacketV84::class,
				InfoV84::MAP_INFO_REQUEST_PACKET => protocol\v84\MapInfoRequestPacketV84::class,
				InfoV84::REQUEST_CHUNK_RADIUS_PACKET => protocol\v84\RequestChunkRadiusPacketV84::class,
				InfoV84::CHUNK_RADIUS_UPDATE_PACKET => protocol\v84\ChunkRadiusUpdatedPacketV84::class,
				InfoV84::ITEM_FRAME_DROP_ITEM_PACKET => protocol\v84\ItemFrameDropItemPacketV84::class,
			];
		}

		return self::$v84PacketMap;
	}

	public static function parsePacket(Player $player, $packet){
		if($packet instanceof DataPacketV11){
			return ProtocolCompatibility::isProtocol011((int) $player->getProtocol()) ? $packet : self::toCorePacket($packet);
		}

		if($packet instanceof DataPacketV84){
			return ProtocolCompatibility::isProtocol015((int) $player->getProtocol()) ? self::sanitizeProtocol015V84Packet($packet, $player) : $packet;
		}

		if(!$packet instanceof DataPacket){
			return $packet;
		}

		$protocol = (int) $player->getProtocol();
		if(ProtocolCompatibility::isProtocol011($protocol)){
			return self::toProtocol011Packet($packet, $player);
		}

		if(ProtocolCompatibility::isProtocol015($protocol)){
			return self::toProtocol015Packet($packet, $player);
		}

		return $packet;
	}

	private static function sanitizeProtocol015V84Packet(DataPacketV84 $packet, Player $player = null) : DataPacketV84{
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

		$coreClass = self::getV84ToCoreMap()[$pid] ?? null;
		if($coreClass === null){
			return null;
		}

		try{
			/** @var DataPacket $packet */
			$packet = new $coreClass;
			$packet->protocol = Info::V014_CURRENT_PROTOCOL;
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
		if($packet instanceof BatchPacket){
			$pk = new BatchPacketV84();
			$pk->payload = self::sanitizeProtocol015V84BatchPayload(self::remapBatchPayloadToProtocol015($packet->payload, $player), $player);
			$pk->isEncoded = false;
			return $pk;
		}

		$class = self::getCoreToV84Map()[$packet::NETWORK_ID] ?? null;
		if($class === null){
			return $packet;
		}

		/** @var DataPacketV84 $pk */
		$pk = new $class;
		self::copyPublicProperties($packet, $pk);
		self::patchProtocol015Packet($pk, $packet, $player);
		return $pk;
	}

	public static function toCorePacket($packet){
		if($packet instanceof LoginPacketV11){
			return $packet->toCurrentPacket();
		}

		if($packet instanceof BatchPacketV11){
			$pk = new BatchPacket();
			$pk->payload = $packet->payload;
			return $pk;
		}

		if($packet instanceof DataPacketV11){
			$core = $packet->toCurrentPacket();
			return $core !== null ? $core : $packet;
		}

		if($packet instanceof LoginPacketV84){
			return self::convertLoginPacket($packet);
		}

		if($packet instanceof BatchPacketV84){
			$pk = new BatchPacket();
			$pk->payload = $packet->payload;
			return $pk;
		}

		if(!$packet instanceof DataPacketV84){
			return $packet;
		}

		$class = self::getV84ToCoreMap()[$packet::NETWORK_ID] ?? null;
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

	public static function toProtocol011Packet(DataPacket $packet, Player $player = null) : ?DataPacketV11{
		if($packet instanceof BatchPacket){
			$pk = new BatchPacketV11();
			$pk->payload = self::remapBatchPayloadToProtocol011($packet->payload, $player);
			$pk->isEncoded = false;
			return $pk;
		}

		switch($packet::NETWORK_ID){
			case Info::PLAY_STATUS_PACKET:
				$pk = new protocol\v11\PlayStatusPacket();
				$pk->status = $packet->status;
				return $pk;
			case Info::DISCONNECT_PACKET:
				$pk = new protocol\v11\DisconnectPacket();
				$pk->message = $packet->message;
				return $pk;
			case Info::TEXT_PACKET:
				$pk = new protocol\v11\TextPacket();
				$pk->type = $packet->type;
				$pk->source = $packet->source;
				$pk->message = $packet->message;
				$pk->parameters = $packet->parameters;
				return $pk;
			case Info::SET_TIME_PACKET:
				$pk = new protocol\v11\SetTimePacket();
				$pk->time = $packet->time;
				$pk->started = $packet->started;
				return $pk;
			case Info::START_GAME_PACKET:
				$pk = new protocol\v11\StartGamePacket();
				$pk->seed = $packet->seed;
				$pk->generator = $packet->generator;
				$pk->gamemode = $packet->gamemode;
				$pk->eid = $packet->eid;
				$pk->spawnX = $packet->spawnX;
				$pk->spawnY = $packet->spawnY;
				$pk->spawnZ = $packet->spawnZ;
				$pk->x = $packet->x;
				$pk->y = $packet->y;
				$pk->z = $packet->z;
				return $pk;
			case Info::ADD_PLAYER_PACKET:
				$pk = new protocol\v11\AddPlayerPacket();
				$pk->clientID = $packet->eid;
				$pk->username = $packet->username;
				$pk->eid = $packet->eid;
				$pk->x = $packet->x;
				$pk->y = $packet->y;
				$pk->z = $packet->z;
				$pk->speedX = $packet->speedX;
				$pk->speedY = $packet->speedY;
				$pk->speedZ = $packet->speedZ;
				$pk->pitch = $packet->pitch;
				$pk->yaw = $packet->yaw;
				$pk->item = self::legacyV11ItemId($packet->item);
				$pk->meta = self::legacyV11ItemMeta($packet->item);
				$pk->metadata = self::legacyV11Metadata(is_array($packet->metadata) ? $packet->metadata : []);
				$pk->slim = false;
				$pk->skin = str_repeat("\x00", 64 * 32 * 4);
				return $pk;
			case Info::REMOVE_PLAYER_PACKET:
				$pk = new protocol\v11\RemovePlayerPacket();
				$pk->eid = $packet->eid;
				$pk->clientID = $packet->eid;
				return $pk;
			case Info::ADD_ENTITY_PACKET:
				$entityType = ProtocolCompatibility::mapEntityTypeForProtocol(InfoV11::CURRENT_PROTOCOL, (int) $packet->type);
				if(!self::isLegacyV11AddEntityType($entityType)){
					return null;
				}
				$pk = new protocol\v11\AddEntityPacket();
				$pk->eid = $packet->eid;
				$pk->type = $entityType;
				$pk->x = $packet->x;
				$pk->y = $packet->y;
				$pk->z = $packet->z;
				$pk->speedX = $packet->speedX;
				$pk->speedY = $packet->speedY;
				$pk->speedZ = $packet->speedZ;
				$pk->yaw = $packet->yaw;
				$pk->pitch = $packet->pitch;
				$metadata = is_array($packet->metadata) ? $packet->metadata : [];
				$metadata = ProtocolCompatibility::applyLegacyMappedEntityNameForProtocol(InfoV11::CURRENT_PROTOCOL, (int) $packet->type, $metadata);
				$pk->metadata = self::legacyV11Metadata($metadata, (int) $packet->type);
				$pk->links = [];
				return $pk;
			case Info::REMOVE_ENTITY_PACKET:
				$pk = new protocol\v11\RemoveEntityPacket();
				$pk->eid = $packet->eid;
				return $pk;
			case Info::ADD_ITEM_ENTITY_PACKET:
				$pk = new protocol\v11\AddItemEntityPacket();
				$pk->eid = $packet->eid;
				$pk->item = self::legacyV11Item($packet->item);
				$pk->x = $packet->x;
				$pk->y = $packet->y;
				$pk->z = $packet->z;
				$pk->speedX = $packet->speedX;
				$pk->speedY = $packet->speedY;
				$pk->speedZ = $packet->speedZ;
				return $pk;
			case Info::TAKE_ITEM_ENTITY_PACKET:
				$pk = new protocol\v11\TakeItemEntityPacket();
				$pk->target = $packet->target;
				$pk->eid = $packet->eid;
				return $pk;
			case Info::MOVE_ENTITY_PACKET:
				$pk = new protocol\v11\MoveEntityPacket();
				$pk->entities = isset($packet->entities) ? $packet->entities : [[$packet->eid, $packet->x, $packet->y, $packet->z, $packet->yaw, $packet->headYaw, $packet->pitch]];
				return $pk;
			case Info::MOVE_PLAYER_PACKET:
				$pk = new protocol\v11\MovePlayerPacket();
				$pk->eid = $packet->eid;
				$pk->x = $packet->x;
				$pk->y = $packet->y;
				$pk->z = $packet->z;
				$pk->yaw = $packet->yaw;
				$pk->bodyYaw = $packet->bodyYaw;
				$pk->pitch = $packet->pitch;
				$pk->mode = $packet->mode;
				$pk->onGround = $packet->onGround;
				return $pk;
			case Info::REMOVE_BLOCK_PACKET:
				$pk = new protocol\v11\RemoveBlockPacket();
				$pk->eid = $packet->eid;
				$pk->x = $packet->x;
				$pk->y = $packet->y;
				$pk->z = $packet->z;
				return $pk;
			case Info::UPDATE_BLOCK_PACKET:
				$pk = new protocol\v11\UpdateBlockPacket();
				$pk->records = self::legacyV11BlockRecords(isset($packet->records) ? $packet->records : [[$packet->x, $packet->z, $packet->y, $packet->blockId, $packet->blockData, $packet->flags]]);
				return $pk;
			case Info::ADD_PAINTING_PACKET:
				$pk = new protocol\v11\AddPaintingPacket();
				$pk->eid = $packet->eid;
				$pk->x = $packet->x;
				$pk->y = $packet->y;
				$pk->z = $packet->z;
				$pk->direction = $packet->direction;
				$pk->title = $packet->title;
				return $pk;
			case Info::EXPLODE_PACKET:
				$pk = new protocol\v11\ExplodePacket();
				$pk->x = $packet->x;
				$pk->y = $packet->y;
				$pk->z = $packet->z;
				$pk->radius = $packet->radius;
				$pk->records = $packet->records;
				return $pk;
			case Info::LEVEL_EVENT_PACKET:
				$pk = new protocol\v11\LevelEventPacket();
				$pk->evid = $packet->evid;
				$pk->x = $packet->x;
				$pk->y = $packet->y;
				$pk->z = $packet->z;
				$pk->data = $packet->data;
				return $pk;
			case Info::BLOCK_EVENT_PACKET:
				$pk = new protocol\v11\TileEventPacket();
				$pk->x = $packet->x;
				$pk->y = $packet->y;
				$pk->z = $packet->z;
				$pk->case1 = $packet->case1;
				$pk->case2 = $packet->case2;
				return $pk;
			case Info::ENTITY_EVENT_PACKET:
				$pk = new protocol\v11\EntityEventPacket();
				$pk->eid = $packet->eid;
				$pk->event = $packet->event;
				return $pk;
			case Info::MOB_EFFECT_PACKET:
				if(!ProtocolCompatibility::canSendMobEffectIdForProtocol(InfoV11::CURRENT_PROTOCOL, (int) $packet->effectId)){
					return null;
				}
				$pk = new protocol\v11\MobEffectPacket();
				$pk->eid = $packet->eid;
				$pk->eventId = $packet->eventId;
				$pk->effectId = $packet->effectId;
				$pk->amplifier = $packet->amplifier;
				$pk->particles = $packet->particles;
				$pk->duration = $packet->duration;
				return $pk;
			case Info::MOB_EQUIPMENT_PACKET:
				$pk = new protocol\v11\PlayerEquipmentPacket();
				$pk->eid = $packet->eid;
				$pk->item = self::legacyV11ItemId($packet->item);
				$pk->meta = self::legacyV11ItemMeta($packet->item);
				$pk->slot = $packet->slot;
				$pk->selectedSlot = $packet->selectedSlot;
				return $pk;
			case Info::MOB_ARMOR_EQUIPMENT_PACKET:
				$pk = new protocol\v11\PlayerArmorEquipmentPacket();
				$pk->eid = $packet->eid;
				foreach([0, 1, 2, 3] as $i){
					$slot = $packet->slots[$i] ?? null;
					$pk->slots[$i] = $slot instanceof Item && $slot->getId() > 0 ? self::legacyV11ItemId($slot) : 255;
				}
				return $pk;
			case Info::INTERACT_PACKET:
				$pk = new protocol\v11\InteractPacket();
				$pk->action = $packet->action;
				$pk->eid = $packet->eid;
				$pk->target = $packet->target;
				return $pk;
			case Info::USE_ITEM_PACKET:
				$pk = new protocol\v11\UseItemPacket();
				$pk->x = $packet->x;
				$pk->y = $packet->y;
				$pk->z = $packet->z;
				$pk->face = $packet->face;
				$pk->item = self::legacyV11ItemId($packet->item);
				$pk->meta = self::legacyV11ItemMeta($packet->item);
				$pk->eid = $packet->eid;
				$pk->fx = $packet->fx;
				$pk->fy = $packet->fy;
				$pk->fz = $packet->fz;
				$pk->posX = $packet->posX;
				$pk->posY = $packet->posY;
				$pk->posZ = $packet->posZ;
				return $pk;
			case Info::PLAYER_ACTION_PACKET:
				$pk = new protocol\v11\PlayerActionPacket();
				$pk->eid = $packet->eid;
				$pk->action = $packet->action;
				$pk->x = $packet->x;
				$pk->y = $packet->y;
				$pk->z = $packet->z;
				$pk->face = $packet->face;
				return $pk;
			case Info::HURT_ARMOR_PACKET:
				$pk = new protocol\v11\HurtArmorPacket();
				$pk->health = $packet->health;
				return $pk;
			case Info::SET_ENTITY_DATA_PACKET:
				$metadata = self::legacyV11Metadata(is_array($packet->metadata) ? $packet->metadata : [], isset($packet->entityType) ? (int) $packet->entityType : null);
				if(count($metadata) === 0){
					return null;
				}
				$pk = new protocol\v11\SetEntityDataPacket();
				$pk->eid = $packet->eid;
				$pk->metadata = $metadata;
				return $pk;
			case Info::SET_ENTITY_MOTION_PACKET:
				$pk = new protocol\v11\SetEntityMotionPacket();
				$pk->entities = $packet->entities;
				return $pk;
			case Info::SET_ENTITY_LINK_PACKET:
				$pk = new protocol\v11\SetEntityLinkPacket();
				$pk->from = $packet->from;
				$pk->to = $packet->to;
				$pk->type = $packet->type;
				return $pk;
			case Info::SET_HEALTH_PACKET:
				$pk = new protocol\v11\SetHealthPacket();
				$pk->health = $packet->health;
				return $pk;
			case Info::SET_SPAWN_POSITION_PACKET:
				$pk = new protocol\v11\SetSpawnPositionPacket();
				$pk->x = $packet->x;
				$pk->y = $packet->y;
				$pk->z = $packet->z;
				return $pk;
			case Info::ANIMATE_PACKET:
				$pk = new protocol\v11\AnimatePacket();
				$pk->action = $packet->action;
				$pk->eid = $packet->eid;
				return $pk;
			case Info::RESPAWN_PACKET:
				$pk = new protocol\v11\RespawnPacket();
				$pk->x = $packet->x;
				$pk->y = $packet->y;
				$pk->z = $packet->z;
				return $pk;
			case Info::CONTAINER_OPEN_PACKET:
				$pk = new protocol\v11\ContainerOpenPacket();
				$pk->windowid = $packet->windowid;
				$pk->type = $packet->type;
				$pk->slots = $packet->slots;
				$pk->x = $packet->x;
				$pk->y = $packet->y;
				$pk->z = $packet->z;
				return $pk->setChannel($packet->getChannel());
			case Info::CONTAINER_CLOSE_PACKET:
				$pk = new protocol\v11\ContainerClosePacket();
				$pk->windowid = $packet->windowid;
				return $pk->setChannel($packet->getChannel());
			case Info::CONTAINER_SET_SLOT_PACKET:
				$pk = new protocol\v11\ContainerSetSlotPacket();
				$pk->windowid = $packet->windowid;
				$pk->slot = $packet->slot;
				$pk->item = self::legacyV11Item($packet->item);
				return $pk->setChannel($packet->getChannel());
			case Info::CONTAINER_SET_DATA_PACKET:
				$pk = new protocol\v11\ContainerSetDataPacket();
				$pk->windowid = $packet->windowid;
				$pk->property = $packet->property;
				$pk->value = $packet->value;
				return $pk->setChannel($packet->getChannel());
			case Info::CONTAINER_SET_CONTENT_PACKET:
				$pk = new protocol\v11\ContainerSetContentPacket();
				$pk->windowid = $packet->windowid;
				$pk->slots = self::legacyV11Slots(is_array($packet->slots) ? $packet->slots : []);
				$pk->hotbar = $packet->hotbar;
				return $pk->setChannel($packet->getChannel());
			case Info::ADVENTURE_SETTINGS_PACKET:
				$pk = new protocol\v11\AdventureSettingsPacket();
				$pk->flags = $packet->flags;
				return $pk;
			case Info::BLOCK_ENTITY_DATA_PACKET:
				$namedtag = is_string($packet->namedtag ?? null) ? protocol\v11\ChunkAdapter::adaptTileEntityNamedTag($packet->namedtag, isset($packet->x) ? ((int) $packet->x >> 4) : null, isset($packet->z) ? ((int) $packet->z >> 4) : null) : null;
				if($namedtag === null){
					return null;
				}
				$pk = new protocol\v11\TileEntityDataPacket();
				$pk->x = $packet->x;
				$pk->y = $packet->y;
				$pk->z = $packet->z;
				$pk->namedtag = $namedtag;
				return $pk;
			case Info::FULL_CHUNK_DATA_PACKET:
				$pk = new protocol\v11\FullChunkDataPacket();
				$pk->chunkX = $packet->chunkX;
				$pk->chunkZ = $packet->chunkZ;
				$pk->data = is_string($packet->data) ? protocol\v11\ChunkAdapter::adaptPayload($packet->data, $packet->order ?? protocol\FullChunkDataPacket::ORDER_COLUMNS, (int) $packet->chunkX, (int) $packet->chunkZ) : $packet->data;
				return $pk;
			case Info::SET_DIFFICULTY_PACKET:
				$pk = new protocol\v11\SetDifficultyPacket();
				$pk->difficulty = $packet->difficulty;
				return $pk;
		}

		return null;
	}

	public static function toProtocol011Packets(DataPacket $packet, Player $player = null) : array{
		$packet011 = self::toProtocol011Packet($packet, $player);
		return $packet011 instanceof DataPacketV11 ? [$packet011] : [];
	}

	public static function remapBatchPayloadToProtocol011(string $payload, Player $player = null) : string{
		$str = zlib_decode($payload, 1024 * 1024 * 64);
		if($str === false){
			return $payload;
		}

		$out = "";
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
			$out .= self::remapPacketBufferToProtocol011($buffer, $player);
		}

		return zlib_encode($out, ZLIB_ENCODING_DEFLATE, 7);
	}

	public static function remapPacketBufferToProtocol011(string $buffer, Player $player = null) : string{
		$header = ProtocolCompatibility::readPacketHeader($buffer);
		if($header === null){
			return "";
		}

		[$pid, $packetOffset] = $header;
		if($pid === Info::BATCH_PACKET){
			return "";
		}

		$aggregate = self::remapAggregatePacketBufferToProtocol011($pid, $buffer, $packetOffset);
		if($aggregate !== null){
			return $aggregate;
		}

		$coreClass = self::getCorePacketClassMap()[$pid] ?? null;
		if($coreClass === null){
			return "";
		}

		try{
			/** @var DataPacket $packet */
			$packet = new $coreClass;
			$packet->protocol = Info::V014_CURRENT_PROTOCOL;
			$packet->setBuffer($buffer, $packetOffset);
			$packet->decode();
			$out = "";
			foreach(self::toProtocol011Packets($packet, $player) as $packet011){
				$packet011->encode();
				$packet011->isEncoded = true;
				$out .= $packet011->buffer;
			}

			return $out;
		}catch(\Throwable $e){
			return "";
		}
	}

	private static function remapAggregatePacketBufferToProtocol011(int $pid, string $buffer, int $packetOffset) : ?string{
		switch($pid){
			case Info::UPDATE_BLOCK_PACKET:
				if(strlen($buffer) < $packetOffset + 4){
					return null;
				}

				$count = Binary::readInt(substr($buffer, $packetOffset, 4));
				$offset = $packetOffset + 4;
				$records = [];
				for($i = 0; $i < $count; ++$i){
					if(strlen($buffer) < $offset + 11){
						return null;
					}

					$x = Binary::readInt(substr($buffer, $offset, 4));
					$z = Binary::readInt(substr($buffer, $offset + 4, 4));
					$y = ord($buffer[$offset + 8]);
					$blockId = ord($buffer[$offset + 9]);
					$flagsAndMeta = ord($buffer[$offset + 10]);
					$flags = $flagsAndMeta >> 4;
					$blockMeta = $flagsAndMeta & 0x0f;
					[$mappedId, $mappedMeta] = ProtocolCompatibility::mapBlockForProtocol(InfoV11::CURRENT_PROTOCOL, $blockId, $blockMeta);
					$records[] = [$x, $z, $y, $mappedId, $mappedMeta, $flags];
					$offset += 11;
				}

				$pk = new protocol\v11\UpdateBlockPacket();
				$pk->records = $records;
				$pk->encode();
				$pk->isEncoded = true;
				return $pk->buffer;

			case Info::MOVE_ENTITY_PACKET:
				return self::remapFloatEntityListPacketBufferToProtocol011($buffer, $packetOffset, 7, protocol\v11\MoveEntityPacket::class);

			case Info::SET_ENTITY_MOTION_PACKET:
				return self::remapFloatEntityListPacketBufferToProtocol011($buffer, $packetOffset, 4, protocol\v11\SetEntityMotionPacket::class);
		}

		return null;
	}

	private static function remapFloatEntityListPacketBufferToProtocol011(string $buffer, int $packetOffset, int $fieldCount, string $class) : ?string{
		if(strlen($buffer) < $packetOffset + 4){
			return null;
		}

		$count = Binary::readInt(substr($buffer, $packetOffset, 4));
		$offset = $packetOffset + 4;
		$recordLength = 8 + (($fieldCount - 1) * 4);
		$entities = [];
		for($i = 0; $i < $count; ++$i){
			if(strlen($buffer) < $offset + $recordLength){
				return null;
			}

			$record = [Binary::readLong(substr($buffer, $offset, 8))];
			$offset += 8;
			for($field = 1; $field < $fieldCount; ++$field){
				$record[] = Binary::readFloat(substr($buffer, $offset, 4));
				$offset += 4;
			}
			$entities[] = $record;
		}

		/** @var DataPacketV11 $pk */
		$pk = new $class;
		$pk->entities = $entities;
		$pk->encode();
		$pk->isEncoded = true;
		return $pk->buffer;
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
		$class = self::getCoreToV84Map()[$pid] ?? null;
		if($class === null){
			return $buffer;
		}

		$coreClass = self::getCorePacketClassMap()[$pid] ?? null;
		if($coreClass === null){
			return $buffer;
		}

		/** @var DataPacket $packet */
		$packet = new $coreClass;
		$packet->protocol = Info::V014_CURRENT_PROTOCOL;
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

	private static function getCoreToV84Map() : array{
		if(self::$coreToV84 === null){
			self::$coreToV84 = [
				Info::PLAY_STATUS_PACKET => protocol\v84\PlayStatusPacketV84::class,
				Info::DISCONNECT_PACKET => protocol\v84\DisconnectPacketV84::class,
				Info::BATCH_PACKET => protocol\v84\BatchPacketV84::class,
				Info::TEXT_PACKET => protocol\v84\TextPacketV84::class,
				Info::SET_TIME_PACKET => protocol\v84\SetTimePacketV84::class,
				Info::START_GAME_PACKET => protocol\v84\StartGamePacketV84::class,
				Info::ADD_PLAYER_PACKET => protocol\v84\AddPlayerPacketV84::class,
				Info::REMOVE_PLAYER_PACKET => protocol\v84\RemoveEntityPacketV84::class,
				Info::ADD_ENTITY_PACKET => protocol\v84\AddEntityPacketV84::class,
				Info::REMOVE_ENTITY_PACKET => protocol\v84\RemoveEntityPacketV84::class,
				Info::ADD_ITEM_ENTITY_PACKET => protocol\v84\AddItemEntityPacketV84::class,
				Info::TAKE_ITEM_ENTITY_PACKET => protocol\v84\TakeItemEntityPacketV84::class,
				Info::MOVE_ENTITY_PACKET => protocol\v84\MoveEntityPacketV84::class,
				Info::MOVE_PLAYER_PACKET => protocol\v84\MovePlayerPacketV84::class,
				Info::REMOVE_BLOCK_PACKET => protocol\v84\RemoveBlockPacketV84::class,
				Info::UPDATE_BLOCK_PACKET => protocol\v84\UpdateBlockPacketV84::class,
				Info::ADD_PAINTING_PACKET => protocol\v84\AddPaintingPacketV84::class,
				Info::EXPLODE_PACKET => protocol\v84\ExplodePacketV84::class,
				Info::LEVEL_EVENT_PACKET => protocol\v84\LevelEventPacketV84::class,
				Info::BLOCK_EVENT_PACKET => protocol\v84\BlockEventPacketV84::class,
				Info::ENTITY_EVENT_PACKET => protocol\v84\EntityEventPacketV84::class,
				Info::MOB_EFFECT_PACKET => protocol\v84\MobEffectPacketV84::class,
				Info::UPDATE_ATTRIBUTES_PACKET => protocol\v84\UpdateAttributesPacketV84::class,
				Info::MOB_EQUIPMENT_PACKET => protocol\v84\MobEquipmentPacketV84::class,
				Info::MOB_ARMOR_EQUIPMENT_PACKET => protocol\v84\MobArmorEquipmentPacketV84::class,
				Info::INTERACT_PACKET => protocol\v84\InteractPacketV84::class,
				Info::USE_ITEM_PACKET => protocol\v84\UseItemPacketV84::class,
				Info::PLAYER_ACTION_PACKET => protocol\v84\PlayerActionPacketV84::class,
				Info::HURT_ARMOR_PACKET => protocol\v84\HurtArmorPacketV84::class,
				Info::SET_ENTITY_DATA_PACKET => protocol\v84\SetEntityDataPacketV84::class,
				Info::SET_ENTITY_MOTION_PACKET => protocol\v84\SetEntityMotionPacketV84::class,
				Info::SET_ENTITY_LINK_PACKET => protocol\v84\SetEntityLinkPacketV84::class,
				Info::SET_HEALTH_PACKET => protocol\v84\SetHealthPacketV84::class,
				Info::SET_SPAWN_POSITION_PACKET => protocol\v84\SetSpawnPositionPacketV84::class,
				Info::ANIMATE_PACKET => protocol\v84\AnimatePacketV84::class,
				Info::RESPAWN_PACKET => protocol\v84\RespawnPacketV84::class,
				Info::DROP_ITEM_PACKET => protocol\v84\DropItemPacketV84::class,
				Info::CONTAINER_OPEN_PACKET => protocol\v84\ContainerOpenPacketV84::class,
				Info::CONTAINER_CLOSE_PACKET => protocol\v84\ContainerClosePacketV84::class,
				Info::CONTAINER_SET_SLOT_PACKET => protocol\v84\ContainerSetSlotPacketV84::class,
				Info::CONTAINER_SET_DATA_PACKET => protocol\v84\ContainerSetDataPacketV84::class,
				Info::CONTAINER_SET_CONTENT_PACKET => protocol\v84\ContainerSetContentPacketV84::class,
				Info::CRAFTING_DATA_PACKET => protocol\v84\CraftingDataPacketV84::class,
				Info::CRAFTING_EVENT_PACKET => protocol\v84\CraftingEventPacketV84::class,
				Info::ADVENTURE_SETTINGS_PACKET => protocol\v84\AdventureSettingsPacketV84::class,
				Info::BLOCK_ENTITY_DATA_PACKET => protocol\v84\BlockEntityDataPacketV84::class,
				Info::PLAYER_INPUT_PACKET => protocol\v84\PlayerInputPacketV84::class,
				Info::FULL_CHUNK_DATA_PACKET => protocol\v84\FullChunkDataPacketV84::class,
				Info::SET_DIFFICULTY_PACKET => protocol\v84\SetDifficultyPacketV84::class,
				Info::CHANGE_DIMENSION_PACKET => protocol\v84\ChangeDimensionPacketV84::class,
				Info::SET_PLAYER_GAMETYPE_PACKET => protocol\v84\SetPlayerGameTypePacketV84::class,
				Info::PLAYER_LIST_PACKET => protocol\v84\PlayerListPacketV84::class,
				Info::TELEMETRY_EVENT_PACKET => protocol\v84\TelemetryEventPacketV84::class,
				Info::CLIENTBOUND_MAP_ITEM_DATA_PACKET => protocol\v84\ClientboundMapItemDataPacketV84::class,
				Info::MAP_INFO_REQUEST_PACKET => protocol\v84\MapInfoRequestPacketV84::class,
				Info::REQUEST_CHUNK_RADIUS_PACKET => protocol\v84\RequestChunkRadiusPacketV84::class,
				Info::CHUNK_RADIUS_UPDATE_PACKET => protocol\v84\ChunkRadiusUpdatedPacketV84::class,
				Info::ITEM_FRAME_DROP_ITEM_PACKET => protocol\v84\ItemFrameDropItemPacketV84::class,
			];
		}

		return self::$coreToV84;
	}

	private static function getV84ToCoreMap() : array{
		if(self::$v84ToCore === null){
			self::$v84ToCore = [
				InfoV84::PLAY_STATUS_PACKET => protocol\PlayStatusPacket::class,
				InfoV84::DISCONNECT_PACKET => protocol\DisconnectPacket::class,
				InfoV84::TEXT_PACKET => protocol\TextPacket::class,
				InfoV84::SET_TIME_PACKET => protocol\SetTimePacket::class,
				InfoV84::START_GAME_PACKET => protocol\StartGamePacket::class,
				InfoV84::ADD_PLAYER_PACKET => protocol\AddPlayerPacket::class,
				InfoV84::ADD_ENTITY_PACKET => protocol\AddEntityPacket::class,
				InfoV84::REMOVE_ENTITY_PACKET => protocol\RemoveEntityPacket::class,
				InfoV84::ADD_ITEM_ENTITY_PACKET => protocol\AddItemEntityPacket::class,
				InfoV84::TAKE_ITEM_ENTITY_PACKET => protocol\TakeItemEntityPacket::class,
				InfoV84::MOVE_ENTITY_PACKET => protocol\MoveEntityPacket::class,
				InfoV84::MOVE_PLAYER_PACKET => protocol\MovePlayerPacket::class,
				InfoV84::REMOVE_BLOCK_PACKET => protocol\RemoveBlockPacket::class,
				InfoV84::UPDATE_BLOCK_PACKET => protocol\UpdateBlockPacket::class,
				InfoV84::ADD_PAINTING_PACKET => protocol\AddPaintingPacket::class,
				InfoV84::EXPLODE_PACKET => protocol\ExplodePacket::class,
				InfoV84::LEVEL_EVENT_PACKET => protocol\LevelEventPacket::class,
				InfoV84::RIDER_JUMP_PACKET => protocol\RiderJumpPacket::class,
				InfoV84::BLOCK_EVENT_PACKET => protocol\BlockEventPacket::class,
				InfoV84::ENTITY_EVENT_PACKET => protocol\EntityEventPacket::class,
				InfoV84::MOB_EFFECT_PACKET => protocol\MobEffectPacket::class,
				InfoV84::UPDATE_ATTRIBUTES_PACKET => protocol\UpdateAttributesPacket::class,
				InfoV84::MOB_EQUIPMENT_PACKET => protocol\MobEquipmentPacket::class,
				InfoV84::MOB_ARMOR_EQUIPMENT_PACKET => protocol\MobArmorEquipmentPacket::class,
				InfoV84::INTERACT_PACKET => protocol\InteractPacket::class,
				InfoV84::USE_ITEM_PACKET => protocol\UseItemPacket::class,
				InfoV84::PLAYER_ACTION_PACKET => protocol\PlayerActionPacket::class,
				InfoV84::HURT_ARMOR_PACKET => protocol\HurtArmorPacket::class,
				InfoV84::SET_ENTITY_DATA_PACKET => protocol\SetEntityDataPacket::class,
				InfoV84::SET_ENTITY_MOTION_PACKET => protocol\SetEntityMotionPacket::class,
				InfoV84::SET_ENTITY_LINK_PACKET => protocol\SetEntityLinkPacket::class,
				InfoV84::SET_HEALTH_PACKET => protocol\SetHealthPacket::class,
				InfoV84::SET_SPAWN_POSITION_PACKET => protocol\SetSpawnPositionPacket::class,
				InfoV84::ANIMATE_PACKET => protocol\AnimatePacket::class,
				InfoV84::RESPAWN_PACKET => protocol\RespawnPacket::class,
				InfoV84::DROP_ITEM_PACKET => protocol\DropItemPacket::class,
				InfoV84::CONTAINER_OPEN_PACKET => protocol\ContainerOpenPacket::class,
				InfoV84::CONTAINER_CLOSE_PACKET => protocol\ContainerClosePacket::class,
				InfoV84::CONTAINER_SET_SLOT_PACKET => protocol\ContainerSetSlotPacket::class,
				InfoV84::CONTAINER_SET_DATA_PACKET => protocol\ContainerSetDataPacket::class,
				InfoV84::CONTAINER_SET_CONTENT_PACKET => protocol\ContainerSetContentPacket::class,
				InfoV84::CRAFTING_DATA_PACKET => protocol\CraftingDataPacket::class,
				InfoV84::CRAFTING_EVENT_PACKET => protocol\CraftingEventPacket::class,
				InfoV84::ADVENTURE_SETTINGS_PACKET => protocol\AdventureSettingsPacket::class,
				InfoV84::BLOCK_ENTITY_DATA_PACKET => protocol\BlockEntityDataPacket::class,
				InfoV84::PLAYER_INPUT_PACKET => protocol\PlayerInputPacket::class,
				InfoV84::FULL_CHUNK_DATA_PACKET => protocol\FullChunkDataPacket::class,
				InfoV84::SET_DIFFICULTY_PACKET => protocol\SetDifficultyPacket::class,
				InfoV84::CHANGE_DIMENSION_PACKET => protocol\ChangeDimensionPacket::class,
				InfoV84::SET_PLAYER_GAMETYPE_PACKET => protocol\SetPlayerGameTypePacket::class,
				InfoV84::PLAYER_LIST_PACKET => protocol\PlayerListPacket::class,
				InfoV84::TELEMETRY_EVENT_PACKET => protocol\TelemetryEventPacket::class,
				InfoV84::CLIENTBOUND_MAP_ITEM_DATA_PACKET => protocol\ClientboundMapItemDataPacket::class,
				InfoV84::MAP_INFO_REQUEST_PACKET => protocol\MapInfoRequestPacket::class,
				InfoV84::REQUEST_CHUNK_RADIUS_PACKET => protocol\RequestChunkRadiusPacket::class,
				InfoV84::CHUNK_RADIUS_UPDATE_PACKET => protocol\ChunkRadiusUpdatePacket::class,
				InfoV84::ITEM_FRAME_DROP_ITEM_PACKET => protocol\ItemFrameDropItemPacket::class,
			];
		}

		return self::$v84ToCore;
	}

	private static function getCorePacketClassMap() : array{
		return [
			Info::LOGIN_PACKET => protocol\LoginPacket::class,
			Info::PLAY_STATUS_PACKET => protocol\PlayStatusPacket::class,
			Info::DISCONNECT_PACKET => protocol\DisconnectPacket::class,
			Info::BATCH_PACKET => protocol\BatchPacket::class,
			Info::TEXT_PACKET => protocol\TextPacket::class,
			Info::SET_TIME_PACKET => protocol\SetTimePacket::class,
			Info::START_GAME_PACKET => protocol\StartGamePacket::class,
			Info::ADD_PLAYER_PACKET => protocol\AddPlayerPacket::class,
			Info::REMOVE_PLAYER_PACKET => protocol\RemovePlayerPacket::class,
			Info::ADD_ENTITY_PACKET => protocol\AddEntityPacket::class,
			Info::REMOVE_ENTITY_PACKET => protocol\RemoveEntityPacket::class,
			Info::ADD_ITEM_ENTITY_PACKET => protocol\AddItemEntityPacket::class,
			Info::TAKE_ITEM_ENTITY_PACKET => protocol\TakeItemEntityPacket::class,
			Info::MOVE_ENTITY_PACKET => protocol\MoveEntityPacket::class,
			Info::MOVE_PLAYER_PACKET => protocol\MovePlayerPacket::class,
			Info::REMOVE_BLOCK_PACKET => protocol\RemoveBlockPacket::class,
			Info::UPDATE_BLOCK_PACKET => protocol\UpdateBlockPacket::class,
			Info::ADD_PAINTING_PACKET => protocol\AddPaintingPacket::class,
			Info::EXPLODE_PACKET => protocol\ExplodePacket::class,
			Info::LEVEL_EVENT_PACKET => protocol\LevelEventPacket::class,
			Info::BLOCK_EVENT_PACKET => protocol\BlockEventPacket::class,
			Info::ENTITY_EVENT_PACKET => protocol\EntityEventPacket::class,
			Info::MOB_EFFECT_PACKET => protocol\MobEffectPacket::class,
			Info::UPDATE_ATTRIBUTES_PACKET => protocol\UpdateAttributesPacket::class,
			Info::MOB_EQUIPMENT_PACKET => protocol\MobEquipmentPacket::class,
			Info::MOB_ARMOR_EQUIPMENT_PACKET => protocol\MobArmorEquipmentPacket::class,
			Info::INTERACT_PACKET => protocol\InteractPacket::class,
			Info::USE_ITEM_PACKET => protocol\UseItemPacket::class,
			Info::PLAYER_ACTION_PACKET => protocol\PlayerActionPacket::class,
			Info::HURT_ARMOR_PACKET => protocol\HurtArmorPacket::class,
			Info::SET_ENTITY_DATA_PACKET => protocol\SetEntityDataPacket::class,
			Info::SET_ENTITY_MOTION_PACKET => protocol\SetEntityMotionPacket::class,
			Info::SET_ENTITY_LINK_PACKET => protocol\SetEntityLinkPacket::class,
			Info::SET_HEALTH_PACKET => protocol\SetHealthPacket::class,
			Info::SET_SPAWN_POSITION_PACKET => protocol\SetSpawnPositionPacket::class,
			Info::ANIMATE_PACKET => protocol\AnimatePacket::class,
			Info::RESPAWN_PACKET => protocol\RespawnPacket::class,
			Info::DROP_ITEM_PACKET => protocol\DropItemPacket::class,
			Info::CONTAINER_OPEN_PACKET => protocol\ContainerOpenPacket::class,
			Info::CONTAINER_CLOSE_PACKET => protocol\ContainerClosePacket::class,
			Info::CONTAINER_SET_SLOT_PACKET => protocol\ContainerSetSlotPacket::class,
			Info::CONTAINER_SET_DATA_PACKET => protocol\ContainerSetDataPacket::class,
			Info::CONTAINER_SET_CONTENT_PACKET => protocol\ContainerSetContentPacket::class,
			Info::CRAFTING_DATA_PACKET => protocol\CraftingDataPacket::class,
			Info::CRAFTING_EVENT_PACKET => protocol\CraftingEventPacket::class,
			Info::ADVENTURE_SETTINGS_PACKET => protocol\AdventureSettingsPacket::class,
			Info::BLOCK_ENTITY_DATA_PACKET => protocol\BlockEntityDataPacket::class,
			Info::PLAYER_INPUT_PACKET => protocol\PlayerInputPacket::class,
			Info::FULL_CHUNK_DATA_PACKET => protocol\FullChunkDataPacket::class,
			Info::SET_DIFFICULTY_PACKET => protocol\SetDifficultyPacket::class,
			Info::CHANGE_DIMENSION_PACKET => protocol\ChangeDimensionPacket::class,
			Info::SET_PLAYER_GAMETYPE_PACKET => protocol\SetPlayerGameTypePacket::class,
			Info::PLAYER_LIST_PACKET => protocol\PlayerListPacket::class,
			Info::TELEMETRY_EVENT_PACKET => protocol\TelemetryEventPacket::class,
			Info::CLIENTBOUND_MAP_ITEM_DATA_PACKET => protocol\ClientboundMapItemDataPacket::class,
			Info::MAP_INFO_REQUEST_PACKET => protocol\MapInfoRequestPacket::class,
			Info::REQUEST_CHUNK_RADIUS_PACKET => protocol\RequestChunkRadiusPacket::class,
			Info::CHUNK_RADIUS_UPDATE_PACKET => protocol\ChunkRadiusUpdatePacket::class,
			Info::ITEM_FRAME_DROP_ITEM_PACKET => protocol\ItemFrameDropItemPacket::class,
		];
	}

	private static function legacyV11Item($item){
		if(!$item instanceof Item){
			return $item;
		}

		return ProtocolCompatibility::mapItemForProtocol(InfoV11::CURRENT_PROTOCOL, $item, true);
	}

	private static function legacyV11Slots(array $slots) : array{
		foreach($slots as $slot => $item){
			$slots[$slot] = self::legacyV11Item($item);
		}

		return $slots;
	}

	private static function legacyV11ItemId($item) : int{
		$item = self::legacyV11Item($item);
		return $item instanceof Item ? $item->getId() : (int) $item;
	}

	private static function legacyV11ItemMeta($item) : int{
		$item = self::legacyV11Item($item);
		if($item instanceof Item){
			$damage = $item->getDamage();
			return $damage === null ? 0 : (int) $damage;
		}

		return 0;
	}

	private static function legacyV11BlockRecords(array $records) : array{
		$mappedRecords = [];
		foreach($records as $record){
			if(!is_array($record) or count($record) < 6){
				continue;
			}

			[$blockId, $blockMeta] = ProtocolCompatibility::mapBlockForProtocol(InfoV11::CURRENT_PROTOCOL, (int) $record[3], (int) $record[4]);
			$mappedRecords[] = [$record[0], $record[1], $record[2], $blockId, $blockMeta, $record[5]];
		}

		return $mappedRecords;
	}

	private static function isLegacyV11AddEntityType($type) : bool{
		static $types = [
			10 => true,
			11 => true,
			12 => true,
			13 => true,
			15 => true,
			16 => true,
			17 => true,
			32 => true,
			33 => true,
			34 => true,
			35 => true,
			36 => true,
			37 => true,
			38 => true,
			42 => true,
			64 => true,
		];

		return isset($types[(int) $type]);
	}

	private static function legacyV11Metadata(array $metadata, $entityType = null) : array{
		static $types = [
			0 => [Entity::DATA_TYPE_BYTE => true],
			1 => [Entity::DATA_TYPE_SHORT => true],
			2 => [Entity::DATA_TYPE_STRING => true],
			3 => [Entity::DATA_TYPE_BYTE => true],
			4 => [Entity::DATA_TYPE_BYTE => true],
			7 => [Entity::DATA_TYPE_INT => true],
			8 => [Entity::DATA_TYPE_BYTE => true],
			14 => [Entity::DATA_TYPE_BYTE => true],
			15 => [Entity::DATA_TYPE_BYTE => true],
			16 => [Entity::DATA_TYPE_BYTE => true],
			17 => [
				Entity::DATA_TYPE_BYTE => true,
				Entity::DATA_TYPE_POS => true,
			],
			20 => [Entity::DATA_TYPE_INT => true],
		];

		$result = [];
		foreach($metadata as $index => $entry){
			$index = (int) $index;
			if(!isset($types[$index]) or !is_array($entry) or count($entry) < 2){
				continue;
			}

			$type = (int) $entry[0];
			if(!isset($types[$index][$type])){
				continue;
			}

			$value = $entry[1];
			if($type === Entity::DATA_TYPE_SLOT){
				if(!is_array($value) or count($value) < 3){
					continue;
				}
				$value = [(int) $value[0], (int) $value[1], (int) $value[2]];
			}elseif($type === Entity::DATA_TYPE_POS){
				if(!is_array($value) or count($value) < 3){
					continue;
				}
				$value = [(int) $value[0], (int) $value[1], (int) $value[2]];
			}

			$result[$index] = [$type, $value];
		}

		if($entityType === self::ENTITY_TYPE_CREEPER){
			$creeperMetadata = ProtocolCompatibility::filterEntityMetadataForProtocol(InfoV11::CURRENT_PROTOCOL, $entityType, $metadata);
			foreach([
				Entity::DATA_CREPPER_SWELL_DIRECTION,
				Entity::DATA_CREPPER_SWELL,
				Entity::DATA_CREPPER_SWELL_2,
				self::ENTITY_DATA_CREEPER_POWERED,
			] as $index){
				if(isset($creeperMetadata[$index])){
					$result[$index] = $creeperMetadata[$index];
				}
			}
		}

		return $result;
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
