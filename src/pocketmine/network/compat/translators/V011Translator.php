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
use pocketmine\item\Item;
use pocketmine\network\compat\mappings\EntityMapping;
use pocketmine\network\compat\mappings\MetadataMapping;
use pocketmine\network\protocol\DataPacket;
use pocketmine\network\protocol\ProtocolCompatibility;
use pocketmine\network\protocol\v11\BatchPacket as BatchPacketV11;
use pocketmine\network\protocol\v11\DataPacket as DataPacketV11;
use pocketmine\network\protocol\v11\Info as InfoV11;
use pocketmine\network\protocol\v11\LoginPacket as LoginPacketV11;
use pocketmine\network\protocol as protocol;
use pocketmine\Player;
use pocketmine\utils\Binary;

/**
 * 0.11 (v11) 协议的数据包翻译器：核心 <-> v11，以及批处理/聚合包的字节级重映射与 legacy 元数据。
 */
final class V011Translator{

	private function __construct(){
	}

	public static function toCorePacketV11(DataPacketV11 $packet){
		if($packet instanceof LoginPacketV11){
			return $packet->toCurrentPacket();
		}

		if($packet instanceof BatchPacketV11){
			$pk = new protocol\BatchPacket();
			$pk->payload = $packet->payload;
			return $pk;
		}

		$core = $packet->toCurrentPacket();
		return $core !== null ? $core : $packet;
	}

	public static function toProtocol011Packet(DataPacket $packet, Player $player = null) : ?DataPacketV11{
		if($packet instanceof protocol\BatchPacket){
			$pk = new BatchPacketV11();
			$pk->payload = self::remapBatchPayloadToProtocol011($packet->payload, $player);
			$pk->isEncoded = false;
			return $pk;
		}

		switch($packet::NETWORK_ID){
			case protocol\Info::PLAY_STATUS_PACKET:
				$pk = new protocol\v11\PlayStatusPacket();
				$pk->status = $packet->status;
				return $pk;
			case protocol\Info::DISCONNECT_PACKET:
				$pk = new protocol\v11\DisconnectPacket();
				$pk->message = $packet->message;
				return $pk;
			case protocol\Info::TEXT_PACKET:
				$pk = new protocol\v11\TextPacket();
				$pk->type = $packet->type;
				$pk->source = $packet->source;
				$pk->message = $packet->message;
				$pk->parameters = $packet->parameters;
				return $pk;
			case protocol\Info::SET_TIME_PACKET:
				$pk = new protocol\v11\SetTimePacket();
				$pk->time = $packet->time;
				$pk->started = $packet->started;
				return $pk;
			case protocol\Info::START_GAME_PACKET:
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
			case protocol\Info::ADD_PLAYER_PACKET:
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
			case protocol\Info::REMOVE_PLAYER_PACKET:
				$pk = new protocol\v11\RemovePlayerPacket();
				$pk->eid = $packet->eid;
				$pk->clientID = $packet->eid;
				return $pk;
			case protocol\Info::ADD_ENTITY_PACKET:
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
			case protocol\Info::REMOVE_ENTITY_PACKET:
				$pk = new protocol\v11\RemoveEntityPacket();
				$pk->eid = $packet->eid;
				return $pk;
			case protocol\Info::ADD_ITEM_ENTITY_PACKET:
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
			case protocol\Info::TAKE_ITEM_ENTITY_PACKET:
				$pk = new protocol\v11\TakeItemEntityPacket();
				$pk->target = $packet->target;
				$pk->eid = $packet->eid;
				return $pk;
			case protocol\Info::MOVE_ENTITY_PACKET:
				$pk = new protocol\v11\MoveEntityPacket();
				$pk->entities = isset($packet->entities) ? $packet->entities : [[$packet->eid, $packet->x, $packet->y, $packet->z, $packet->yaw, $packet->headYaw, $packet->pitch]];
				return $pk;
			case protocol\Info::MOVE_PLAYER_PACKET:
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
			case protocol\Info::REMOVE_BLOCK_PACKET:
				$pk = new protocol\v11\RemoveBlockPacket();
				$pk->eid = $packet->eid;
				$pk->x = $packet->x;
				$pk->y = $packet->y;
				$pk->z = $packet->z;
				return $pk;
			case protocol\Info::UPDATE_BLOCK_PACKET:
				$pk = new protocol\v11\UpdateBlockPacket();
				$pk->records = self::legacyV11BlockRecords(isset($packet->records) ? $packet->records : [[$packet->x, $packet->z, $packet->y, $packet->blockId, $packet->blockData, $packet->flags]]);
				return $pk;
			case protocol\Info::ADD_PAINTING_PACKET:
				$pk = new protocol\v11\AddPaintingPacket();
				$pk->eid = $packet->eid;
				$pk->x = $packet->x;
				$pk->y = $packet->y;
				$pk->z = $packet->z;
				$pk->direction = $packet->direction;
				$pk->title = $packet->title;
				return $pk;
			case protocol\Info::EXPLODE_PACKET:
				$pk = new protocol\v11\ExplodePacket();
				$pk->x = $packet->x;
				$pk->y = $packet->y;
				$pk->z = $packet->z;
				$pk->radius = $packet->radius;
				$pk->records = $packet->records;
				return $pk;
			case protocol\Info::LEVEL_EVENT_PACKET:
				$pk = new protocol\v11\LevelEventPacket();
				$pk->evid = $packet->evid;
				$pk->x = $packet->x;
				$pk->y = $packet->y;
				$pk->z = $packet->z;
				$pk->data = $packet->data;
				return $pk;
			case protocol\Info::BLOCK_EVENT_PACKET:
				$pk = new protocol\v11\TileEventPacket();
				$pk->x = $packet->x;
				$pk->y = $packet->y;
				$pk->z = $packet->z;
				$pk->case1 = $packet->case1;
				$pk->case2 = $packet->case2;
				return $pk;
			case protocol\Info::ENTITY_EVENT_PACKET:
				$pk = new protocol\v11\EntityEventPacket();
				$pk->eid = $packet->eid;
				$pk->event = $packet->event;
				return $pk;
			case protocol\Info::MOB_EFFECT_PACKET:
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
			case protocol\Info::MOB_EQUIPMENT_PACKET:
				$pk = new protocol\v11\PlayerEquipmentPacket();
				$pk->eid = $packet->eid;
				$pk->item = self::legacyV11ItemId($packet->item);
				$pk->meta = self::legacyV11ItemMeta($packet->item);
				$pk->slot = $packet->slot;
				$pk->selectedSlot = $packet->selectedSlot;
				return $pk;
			case protocol\Info::MOB_ARMOR_EQUIPMENT_PACKET:
				$pk = new protocol\v11\PlayerArmorEquipmentPacket();
				$pk->eid = $packet->eid;
				foreach([0, 1, 2, 3] as $i){
					$slot = $packet->slots[$i] ?? null;
					$pk->slots[$i] = $slot instanceof Item && $slot->getId() > 0 ? self::legacyV11ItemId($slot) : 255;
				}
				return $pk;
			case protocol\Info::INTERACT_PACKET:
				$pk = new protocol\v11\InteractPacket();
				$pk->action = $packet->action;
				$pk->eid = $packet->eid;
				$pk->target = $packet->target;
				return $pk;
			case protocol\Info::USE_ITEM_PACKET:
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
			case protocol\Info::PLAYER_ACTION_PACKET:
				$pk = new protocol\v11\PlayerActionPacket();
				$pk->eid = $packet->eid;
				$pk->action = $packet->action;
				$pk->x = $packet->x;
				$pk->y = $packet->y;
				$pk->z = $packet->z;
				$pk->face = $packet->face;
				return $pk;
			case protocol\Info::HURT_ARMOR_PACKET:
				$pk = new protocol\v11\HurtArmorPacket();
				$pk->health = $packet->health;
				return $pk;
			case protocol\Info::SET_ENTITY_DATA_PACKET:
				$metadata = self::legacyV11Metadata(is_array($packet->metadata) ? $packet->metadata : [], isset($packet->entityType) ? (int) $packet->entityType : null);
				if(count($metadata) === 0){
					return null;
				}
				$pk = new protocol\v11\SetEntityDataPacket();
				$pk->eid = $packet->eid;
				$pk->metadata = $metadata;
				return $pk;
			case protocol\Info::SET_ENTITY_MOTION_PACKET:
				$pk = new protocol\v11\SetEntityMotionPacket();
				$pk->entities = $packet->entities;
				return $pk;
			case protocol\Info::SET_ENTITY_LINK_PACKET:
				$pk = new protocol\v11\SetEntityLinkPacket();
				$pk->from = $packet->from;
				$pk->to = $packet->to;
				$pk->type = $packet->type;
				return $pk;
			case protocol\Info::SET_HEALTH_PACKET:
				$pk = new protocol\v11\SetHealthPacket();
				$pk->health = $packet->health;
				return $pk;
			case protocol\Info::SET_SPAWN_POSITION_PACKET:
				$pk = new protocol\v11\SetSpawnPositionPacket();
				$pk->x = $packet->x;
				$pk->y = $packet->y;
				$pk->z = $packet->z;
				return $pk;
			case protocol\Info::ANIMATE_PACKET:
				$pk = new protocol\v11\AnimatePacket();
				$pk->action = $packet->action;
				$pk->eid = $packet->eid;
				return $pk;
			case protocol\Info::RESPAWN_PACKET:
				$pk = new protocol\v11\RespawnPacket();
				$pk->x = $packet->x;
				$pk->y = $packet->y;
				$pk->z = $packet->z;
				return $pk;
			case protocol\Info::CONTAINER_OPEN_PACKET:
				$pk = new protocol\v11\ContainerOpenPacket();
				$pk->windowid = $packet->windowid;
				$pk->type = $packet->type;
				$pk->slots = $packet->slots;
				$pk->x = $packet->x;
				$pk->y = $packet->y;
				$pk->z = $packet->z;
				return $pk->setChannel($packet->getChannel());
			case protocol\Info::CONTAINER_CLOSE_PACKET:
				$pk = new protocol\v11\ContainerClosePacket();
				$pk->windowid = $packet->windowid;
				return $pk->setChannel($packet->getChannel());
			case protocol\Info::CONTAINER_SET_SLOT_PACKET:
				$pk = new protocol\v11\ContainerSetSlotPacket();
				$pk->windowid = $packet->windowid;
				$pk->slot = $packet->slot;
				$pk->item = self::legacyV11Item($packet->item);
				return $pk->setChannel($packet->getChannel());
			case protocol\Info::CONTAINER_SET_DATA_PACKET:
				$pk = new protocol\v11\ContainerSetDataPacket();
				$pk->windowid = $packet->windowid;
				$pk->property = $packet->property;
				$pk->value = $packet->value;
				return $pk->setChannel($packet->getChannel());
			case protocol\Info::CONTAINER_SET_CONTENT_PACKET:
				$pk = new protocol\v11\ContainerSetContentPacket();
				$pk->windowid = $packet->windowid;
				$pk->slots = self::legacyV11Slots(is_array($packet->slots) ? $packet->slots : []);
				$pk->hotbar = $packet->hotbar;
				return $pk->setChannel($packet->getChannel());
			case protocol\Info::ADVENTURE_SETTINGS_PACKET:
				$pk = new protocol\v11\AdventureSettingsPacket();
				$pk->flags = $packet->flags;
				return $pk;
			case protocol\Info::BLOCK_ENTITY_DATA_PACKET:
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
			case protocol\Info::FULL_CHUNK_DATA_PACKET:
				$pk = new protocol\v11\FullChunkDataPacket();
				$pk->chunkX = $packet->chunkX;
				$pk->chunkZ = $packet->chunkZ;
				$pk->data = is_string($packet->data) ? protocol\v11\ChunkAdapter::adaptPayload($packet->data, $packet->order ?? protocol\FullChunkDataPacket::ORDER_COLUMNS, (int) $packet->chunkX, (int) $packet->chunkZ) : $packet->data;
				return $pk;
			case protocol\Info::SET_DIFFICULTY_PACKET:
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
		if($pid === protocol\Info::BATCH_PACKET){
			return "";
		}

		$aggregate = self::remapAggregatePacketBufferToProtocol011($pid, $buffer, $packetOffset);
		if($aggregate !== null){
			return $aggregate;
		}

		$coreClass = \pocketmine\network\compat\PacketCodecRegistry::corePacket()[$pid] ?? null;
		if($coreClass === null){
			return "";
		}

		try{
			/** @var DataPacket $packet */
			$packet = new $coreClass;
			$packet->protocol = protocol\Info::V014_CURRENT_PROTOCOL;
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
			case protocol\Info::UPDATE_BLOCK_PACKET:
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

			case protocol\Info::MOVE_ENTITY_PACKET:
				return self::remapFloatEntityListPacketBufferToProtocol011($buffer, $packetOffset, 7, protocol\v11\MoveEntityPacket::class);

			case protocol\Info::SET_ENTITY_MOTION_PACKET:
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

		if($entityType === EntityMapping::ENTITY_TYPE_CREEPER){
			$creeperMetadata = ProtocolCompatibility::filterEntityMetadataForProtocol(InfoV11::CURRENT_PROTOCOL, $entityType, $metadata);
			foreach([
				Entity::DATA_CREPPER_SWELL_DIRECTION,
				Entity::DATA_CREPPER_SWELL,
				Entity::DATA_CREPPER_SWELL_2,
				MetadataMapping::ENTITY_DATA_CREEPER_POWERED,
			] as $index){
				if(isset($creeperMetadata[$index])){
					$result[$index] = $creeperMetadata[$index];
				}
			}
		}

		return $result;
	}
}
