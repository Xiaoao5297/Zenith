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
namespace lycore\network;

use lycore\network\protocol as protocol;
use lycore\network\protocol\AddEntityPacket;
use lycore\network\protocol\AddItemEntityPacket;
use lycore\network\protocol\AddPaintingPacket;
use lycore\network\protocol\AddPlayerPacket;
use lycore\network\protocol\AdventureSettingsPacket;
use lycore\network\protocol\AnimatePacket;
use lycore\network\protocol\BatchPacket;
use lycore\network\protocol\ChunkRadiusUpdatePacket;
use lycore\network\protocol\ContainerClosePacket;
use lycore\network\protocol\ContainerOpenPacket;
use lycore\network\protocol\ContainerSetContentPacket;
use lycore\network\protocol\ContainerSetDataPacket;
use lycore\network\protocol\ContainerSetSlotPacket;
use lycore\network\protocol\CraftingDataPacket;
use lycore\network\protocol\CraftingEventPacket;
use lycore\network\protocol\ChangeDimensionPacket;
use lycore\network\protocol\DataPacket;
use lycore\network\protocol\DropItemPacket;
use lycore\network\protocol\FullChunkDataPacket;
use lycore\network\protocol\Info;
use lycore\network\protocol\ItemFrameDropItemPacket;
use lycore\network\protocol\RequestChunkRadiusPacket;
use lycore\network\protocol\RiderJumpPacket;
use lycore\network\protocol\SetEntityLinkPacket;
use lycore\network\protocol\BlockEntityDataPacket;
use lycore\network\protocol\EntityEventPacket;
use lycore\network\protocol\ExplodePacket;
use lycore\network\protocol\HurtArmorPacket;
use lycore\network\protocol\Info as ProtocolInfo;
use lycore\network\protocol\InteractPacket;
use lycore\network\protocol\LevelEventPacket;
use lycore\network\protocol\DisconnectPacket;
use lycore\network\protocol\LoginPacket;
use lycore\network\protocol\PlayStatusPacket;
use lycore\network\protocol\TextPacket;
use lycore\network\protocol\TelemetryEventPacket;
use lycore\network\protocol\MoveEntityPacket;
use lycore\network\protocol\MovePlayerPacket;
use lycore\network\protocol\PlayerActionPacket;
use lycore\network\protocol\MobArmorEquipmentPacket;
use lycore\network\protocol\MobEffectPacket;
use lycore\network\protocol\MobEquipmentPacket;
use lycore\network\protocol\RemoveBlockPacket;
use lycore\network\protocol\RemoveEntityPacket;
use lycore\network\protocol\RemovePlayerPacket;
use lycore\network\protocol\RespawnPacket;
use lycore\network\protocol\SetDifficultyPacket;
use lycore\network\protocol\SetEntityDataPacket;
use lycore\network\protocol\SetEntityMotionPacket;
use lycore\network\protocol\SetHealthPacket;
use lycore\network\protocol\SetPlayerGameTypePacket;
use lycore\network\protocol\SetSpawnPositionPacket;
use lycore\network\protocol\SetTimePacket;
use lycore\network\protocol\StartGamePacket;
use lycore\network\protocol\TakeItemEntityPacket;
use lycore\network\protocol\BlockEventPacket;
use lycore\network\protocol\UpdateAttributesPacket;
use lycore\network\protocol\UpdateBlockPacket;
use lycore\network\protocol\UseItemPacket;
use lycore\network\protocol\PlayerListPacket;
use lycore\network\protocol\PlayerInputPacket;
use lycore\network\protocol\MapInfoRequestPacket;
use lycore\network\protocol\ProtocolCompatibility;
use lycore\network\protocol\v11\BatchPacket as BatchPacketV11;
use lycore\network\protocol\v11\DataPacket as DataPacketV11;
use lycore\Player;
use lycore\Server;
use lycore\utils\Binary;
use lycore\utils\MainLogger;

class Network {

	public static $BATCH_THRESHOLD = 512;

	/** @deprecated */
	const CHANNEL_NONE = 0;
	/** @deprecated */
	const CHANNEL_PRIORITY = 1; //Priority channel, only to be used when it matters
	/** @deprecated */
	const CHANNEL_WORLD_CHUNKS = 2; //Chunk sending
	/** @deprecated */
	const CHANNEL_MOVEMENT = 3; //Movement sending
	/** @deprecated */
	const CHANNEL_BLOCKS = 4; //Block updates or explosions
	/** @deprecated */
	const CHANNEL_WORLD_EVENTS = 5; //Entity, level or tile entity events
	/** @deprecated */
	const CHANNEL_ENTITY_SPAWNING = 6; //Entity spawn/despawn channel
	/** @deprecated */
	const CHANNEL_TEXT = 7; //Chat and other text stuff
	/** @deprecated */
	const CHANNEL_END = 31;

	/** @var \SplFixedArray */
	private $packetPool;

	/** @var \SplFixedArray */
	private $v11PacketPool;

	/** @var Server */
	private $server;

	/** @var SourceInterface[] */
	private $interfaces = [];

	/** @var AdvancedSourceInterface[] */
	private $advancedInterfaces = [];

	private $upload = 0;
	private $download = 0;
	private $cleaned = 0;

	private $name;

	public function __construct(Server $server) {

		$this->registerPackets();

		$this->server = $server;
	}

	public function addStatistics($upload, $download, $cleaned = 0) {
		$this->upload += $upload;
		$this->download += $download;
		$this->cleaned += $cleaned;
	}

	public function getUpload() {
		return $this->upload;
	}

	public function getDownload() {
		return $this->download;
	}
	
	public function getCleaned() {
		return $this->cleaned;
	}

	public function resetStatistics() {
		$this->upload = 0;
		$this->download = 0;
		$this->cleaned = 0;
	}

	/**
	 * @return SourceInterface[]
	 */
	public function getInterfaces() {
		return $this->interfaces;
	}

	public function processInterfaces() {
		foreach ($this->interfaces as $interface) {
			try {
				$interface->process();
			} catch (\Throwable $e) {
				$logger = $this->server->getLogger();
				if (\lycore\DEBUG > 1) {
					if ($logger instanceof MainLogger) {
						$logger->logException($e);
					}
				}

				$interface->emergencyShutdown();
				$this->unregisterInterface($interface);
				$logger->critical($this->server->getLanguage()->translateString("pocketmine.server.networkError", [get_class($interface), $e->getMessage()]));
			}
		}
	}

	/**
	 * @param SourceInterface $interface
	 */
	public function registerInterface(SourceInterface $interface) {
		$this->interfaces[$hash = spl_object_hash($interface)] = $interface;
		if ($interface instanceof AdvancedSourceInterface) {
			$this->advancedInterfaces[$hash] = $interface;
			$interface->setNetwork($this);
		}
		$interface->setName($this->name);
	}

	/**
	 * @param SourceInterface $interface
	 */
	public function unregisterInterface(SourceInterface $interface) {
		unset($this->interfaces[$hash = spl_object_hash($interface)],
			$this->advancedInterfaces[$hash]);
	}

	/**
	 * Sets the server name shown on each interface Query
	 *
	 * @param string $name
	 */
	public function setName($name) {
		$this->name = (string)$name;
		foreach ($this->interfaces as $interface) {
			$interface->setName($this->name);
		}
	}

	public function getName() {
		return $this->name;
	}

	public function updateName() {
		foreach ($this->interfaces as $interface) {
			$interface->setName($this->name);
		}
	}

	/**
	 * @param int        $id 0-255
	 * @param DataPacket $class
	 */
	public function registerPacket($id, $class) {
		$this->packetPool[$id] = new $class;
	}

	public function registerV11Packet($id, $class) {
		$this->v11PacketPool[$id] = new $class;
	}

	public function getServer() {
		return $this->server;
	}

	public function processBatch($packet, Player $p) {
		if($packet instanceof BatchPacketV11 or ProtocolCompatibility::isProtocol011((int) $p->getProtocol())){
			$this->processProtocol011Batch($packet, $p);
			return;
		}
		if(!$packet instanceof BatchPacket){
			return;
		}

		$str = zlib_decode($packet->payload, 1024 * 1024 * 64); //Max 64MB
		$len = strlen($str);
		$offset = 0;
		try {
			while ($offset < $len) {
				$pkLen = Binary::readInt(substr($str, $offset, 4));
				$offset += 4;

				$buf = substr($str, $offset, $pkLen);

				$offset += $pkLen;

				$header = ProtocolCompatibility::readPacketHeader($buf);
				if($header === null){
					continue;
				}
				[$pid, $packetOffset] = $header;
				if(($pk = $this->getPacket($pid, (int) $p->getProtocol())) !== null){
					if ($pk::NETWORK_ID === Info::BATCH_PACKET or $pk::NETWORK_ID === protocol\v84\InfoV84::BATCH_PACKET) {
						throw new \InvalidStateException("Invalid BatchPacket inside BatchPacket");
					}

					$pk->protocol = (int) $p->getProtocol();
					$pk->setBuffer($buf, $packetOffset);

					$pk->decode();
					$decodedOffset = $pk->getOffset();
					$pk = DataPacketManager::toCorePacket($pk);
					$p->handleDataPacket($pk);

					if ($decodedOffset <= 0) {
						return;
					}
				}
			}
		} catch (\Throwable $e) {
			if (\lycore\DEBUG > 1) {
				$logger = $this->server->getLogger();
				if ($logger instanceof MainLogger) {
					$logger->debug("BatchPacket " . " 0x" . bin2hex($packet->payload));
					$logger->logException($e);
				}
			}
		}
	}

	private function processProtocol011Batch($packet, Player $p) {
		if(!$packet instanceof BatchPacket and !$packet instanceof BatchPacketV11){
			return;
		}

		$str = zlib_decode($packet->payload, 1024 * 1024 * 64);
		if($str === false){
			return;
		}

		$len = strlen($str);
		$offset = 0;
		$protocol = ProtocolCompatibility::isProtocol011((int) $p->getProtocol()) ? (int) $p->getProtocol() : protocol\v11\Info::CURRENT_PROTOCOL;
		try{
			while($offset < $len){
				$pid = ord($str[$offset++]);
				if(($pk = $this->getPacket($pid, $protocol)) === null){
					continue;
				}

				$decodedOffset = $this->handleProtocol011BatchPacket($pk, $str, $offset, $p);
				if($decodedOffset <= $offset){
					break;
				}
				$offset = $decodedOffset;
			}
		}catch(\Throwable $e){
			if(\lycore\DEBUG > 1){
				$logger = $this->server->getLogger();
				if($logger instanceof MainLogger){
					$logger->debug("V11 BatchPacket 0x" . bin2hex($packet->payload));
					$logger->logException($e);
				}
			}
		}
	}

	private function handleProtocol011BatchPacket($pk, string $buffer, int $packetOffset, Player $p) : int{
		if($pk::NETWORK_ID === protocol\v11\Info::BATCH_PACKET){
			throw new \InvalidStateException("Invalid v11 BatchPacket inside BatchPacket");
		}

		$pk->setBuffer($buffer, $packetOffset);
		$pk->decode();
		$decodedOffset = $pk->getOffset();
		if($decodedOffset <= $packetOffset){
			return $decodedOffset;
		}

		$corePacket = DataPacketManager::toCorePacket($pk);
		if($corePacket instanceof DataPacket){
			$p->handleDataPacket($corePacket);
		}

		return $decodedOffset;
	}

	/**
	 * @param $id
	 *
	 * @return DataPacket
	 */
	public function getPacket($id, int $protocol = null) {
		$isInitialV11Packet = $protocol !== null && $protocol < 0 && $id === protocol\v11\Info::BATCH_PACKET;
		/** @var DataPacket $class */
		$class = (ProtocolCompatibility::isProtocol011((int) $protocol) or $isInitialV11Packet) ? $this->v11PacketPool[$id] : $this->packetPool[$id];
		if ($class !== null) {
			return clone $class;
		}
		return null;
	}


	/**
	 * @param string $address
	 * @param int    $port
	 * @param string $payload
	 */
	public function sendPacket($address, $port, $payload) {
		foreach ($this->advancedInterfaces as $interface) {
			$interface->sendRawPacket($address, $port, $payload);
		}
	}

	/**
	 * Blocks an IP address from the main interface. Setting timeout to -1 will block it forever
	 *
	 * @param string $address
	 * @param int    $timeout
	 */
	public function blockAddress($address, $timeout = 300) {
		foreach ($this->advancedInterfaces as $interface) {
			$interface->blockAddress($address, $timeout);
		}
	}

	private function registerPackets() {
		$this->packetPool = new \SplFixedArray(256);
		$this->v11PacketPool = new \SplFixedArray(256);

		$this->registerPacket(ProtocolInfo::LOGIN_PACKET, LoginPacket::class);
		$this->registerPacket(ProtocolInfo::PLAY_STATUS_PACKET, PlayStatusPacket::class);
		$this->registerPacket(ProtocolInfo::DISCONNECT_PACKET, DisconnectPacket::class);
		$this->registerPacket(ProtocolInfo::BATCH_PACKET, BatchPacket::class);
		$this->registerPacket(ProtocolInfo::TEXT_PACKET, TextPacket::class);
		$this->registerPacket(ProtocolInfo::SET_TIME_PACKET, SetTimePacket::class);
		$this->registerPacket(ProtocolInfo::START_GAME_PACKET, StartGamePacket::class);
		$this->registerPacket(ProtocolInfo::ADD_PLAYER_PACKET, AddPlayerPacket::class);
		$this->registerPacket(ProtocolInfo::REMOVE_PLAYER_PACKET, RemovePlayerPacket::class);
		$this->registerPacket(ProtocolInfo::ADD_ENTITY_PACKET, AddEntityPacket::class);
		$this->registerPacket(ProtocolInfo::REMOVE_ENTITY_PACKET, RemoveEntityPacket::class);
		$this->registerPacket(ProtocolInfo::ADD_ITEM_ENTITY_PACKET, AddItemEntityPacket::class);
		$this->registerPacket(ProtocolInfo::TAKE_ITEM_ENTITY_PACKET, TakeItemEntityPacket::class);
		$this->registerPacket(ProtocolInfo::MOVE_ENTITY_PACKET, MoveEntityPacket::class);
		$this->registerPacket(ProtocolInfo::MOVE_PLAYER_PACKET, MovePlayerPacket::class);
		$this->registerPacket(ProtocolInfo::REMOVE_BLOCK_PACKET, RemoveBlockPacket::class);
		$this->registerPacket(ProtocolInfo::UPDATE_BLOCK_PACKET, UpdateBlockPacket::class);
		$this->registerPacket(ProtocolInfo::ADD_PAINTING_PACKET, AddPaintingPacket::class);
		$this->registerPacket(ProtocolInfo::EXPLODE_PACKET, ExplodePacket::class);
		$this->registerPacket(ProtocolInfo::LEVEL_EVENT_PACKET, LevelEventPacket::class);
		$this->registerPacket(ProtocolInfo::BLOCK_EVENT_PACKET, BlockEventPacket::class);
		$this->registerPacket(ProtocolInfo::ENTITY_EVENT_PACKET, EntityEventPacket::class);
		$this->registerPacket(ProtocolInfo::MOB_EFFECT_PACKET, MobEffectPacket::class);
		$this->registerPacket(ProtocolInfo::UPDATE_ATTRIBUTES_PACKET, UpdateAttributesPacket::class);
		$this->registerPacket(ProtocolInfo::MOB_EQUIPMENT_PACKET, MobEquipmentPacket::class);
		$this->registerPacket(ProtocolInfo::MOB_ARMOR_EQUIPMENT_PACKET, MobArmorEquipmentPacket::class);
		$this->registerPacket(ProtocolInfo::INTERACT_PACKET, InteractPacket::class);
		$this->registerPacket(ProtocolInfo::USE_ITEM_PACKET, UseItemPacket::class);
		$this->registerPacket(ProtocolInfo::PLAYER_ACTION_PACKET, PlayerActionPacket::class);
		$this->registerPacket(ProtocolInfo::HURT_ARMOR_PACKET, HurtArmorPacket::class);
		$this->registerPacket(ProtocolInfo::SET_ENTITY_DATA_PACKET, SetEntityDataPacket::class);
		$this->registerPacket(ProtocolInfo::SET_ENTITY_MOTION_PACKET, SetEntityMotionPacket::class);
		$this->registerPacket(ProtocolInfo::SET_ENTITY_LINK_PACKET, SetEntityLinkPacket::class);
		$this->registerPacket(ProtocolInfo::SET_HEALTH_PACKET, SetHealthPacket::class);
		$this->registerPacket(ProtocolInfo::SET_SPAWN_POSITION_PACKET, SetSpawnPositionPacket::class);
		$this->registerPacket(ProtocolInfo::ANIMATE_PACKET, AnimatePacket::class);
		$this->registerPacket(ProtocolInfo::RESPAWN_PACKET, RespawnPacket::class);
		$this->registerPacket(ProtocolInfo::DROP_ITEM_PACKET, DropItemPacket::class);
		$this->registerPacket(ProtocolInfo::CONTAINER_OPEN_PACKET, ContainerOpenPacket::class);
		$this->registerPacket(ProtocolInfo::CONTAINER_CLOSE_PACKET, ContainerClosePacket::class);
		$this->registerPacket(ProtocolInfo::CONTAINER_SET_SLOT_PACKET, ContainerSetSlotPacket::class);
		$this->registerPacket(ProtocolInfo::CONTAINER_SET_DATA_PACKET, ContainerSetDataPacket::class);
		$this->registerPacket(ProtocolInfo::CONTAINER_SET_CONTENT_PACKET, ContainerSetContentPacket::class);
		$this->registerPacket(ProtocolInfo::CRAFTING_DATA_PACKET, CraftingDataPacket::class);
		$this->registerPacket(ProtocolInfo::CRAFTING_EVENT_PACKET, CraftingEventPacket::class);
		$this->registerPacket(ProtocolInfo::ADVENTURE_SETTINGS_PACKET, AdventureSettingsPacket::class);
		$this->registerPacket(ProtocolInfo::BLOCK_ENTITY_DATA_PACKET, BlockEntityDataPacket::class);
		$this->registerPacket(ProtocolInfo::FULL_CHUNK_DATA_PACKET, FullChunkDataPacket::class);
		$this->registerPacket(ProtocolInfo::SET_DIFFICULTY_PACKET, SetDifficultyPacket::class);
		$this->registerPacket(ProtocolInfo::PLAYER_LIST_PACKET, PlayerListPacket::class);
		$this->registerPacket(ProtocolInfo::PLAYER_INPUT_PACKET, PlayerInputPacket::class);
		$this->registerPacket(ProtocolInfo::SET_PLAYER_GAMETYPE_PACKET, SetPlayerGameTypePacket::class);
		$this->registerPacket(ProtocolInfo::CHANGE_DIMENSION_PACKET, ChangeDimensionPacket::class);
		$this->registerPacket(ProtocolInfo::REQUEST_CHUNK_RADIUS_PACKET, RequestChunkRadiusPacket::class);
		$this->registerPacket(ProtocolInfo::CHUNK_RADIUS_UPDATE_PACKET, ChunkRadiusUpdatePacket::class);
		$this->registerPacket(ProtocolInfo::ITEM_FRAME_DROP_ITEM_PACKET, ItemFrameDropItemPacket::class);
		$this->registerPacket(ProtocolInfo::MAP_INFO_REQUEST_PACKET, MapInfoRequestPacket::class);
		$this->registerPacket(ProtocolInfo::TELEMETRY_EVENT_PACKET, TelemetryEventPacket::class);
		$this->registerPacket(ProtocolInfo::RIDER_JUMP_PACKET, RiderJumpPacket::class);

		foreach(DataPacketManager::getProtocol015PacketMap() as $id => $class){
			$this->registerPacket($id, $class);
		}

		foreach(DataPacketManager::getProtocol011PacketMap() as $id => $class){
			$this->registerV11Packet($id, $class);
		}
	}
}
