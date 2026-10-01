<?php

/***
 *  _____                _  __   __
 * /__  /  ___   ____   (_)/ /_ / /_
 *   / /  / _ \ / __ \ / // __// __ \
 *  / /__/  __// / / // // /_ / / / /
 * /____/\___//_/ /_//_/ \__//_/ /_/
 *
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * @author Xiaoao
 * @link https://github.com/Xiaoao5297/Zenith
 *
 *
*/

namespace pocketmine\network\compat;

use pocketmine\network\protocol as protocol;
use pocketmine\network\protocol\Info;
use pocketmine\network\protocol\v11\Info as InfoV11;
use pocketmine\network\protocol\v84\InfoV84;

/**
 * 各协议家族的 (packetId => packet class) 注册表。
 *
 * 集中原先散落在 DataPacketManager 中的 5 张映射表：
 *  - protocol011() : 0.11 包 id -> v11 包类
 *  - protocol015() : 0.15 包 id -> v84 包类
 *  - coreToV84()   : 核心包 id -> v84 包类（出站转换）
 *  - v84ToCore()   : v84 包 id -> 核心包类（入站转换）
 *  - corePacket()  : 核心包 id -> 核心包类
 */
final class PacketCodecRegistry{

	/** @var array[]|null */
	private static $protocol011 = null;
	/** @var array[]|null */
	private static $protocol015 = null;
	/** @var array[]|null */
	private static $coreToV84 = null;
	/** @var array[]|null */
	private static $v84ToCore = null;
	/** @var array[]|null */
	private static $corePacket = null;

	private function __construct(){
	}

	public static function protocol011() : array{
		if(self::$protocol011 === null){
			self::$protocol011 = [
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

		return self::$protocol011;
	}

	public static function protocol015() : array{
		if(self::$protocol015 === null){
			self::$protocol015 = [
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

		return self::$protocol015;
	}

	public static function coreToV84() : array{
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

	public static function v84ToCore() : array{
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

	public static function corePacket() : array{
		if(self::$corePacket === null){
			self::$corePacket = [
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

		return self::$corePacket;
	}
}
