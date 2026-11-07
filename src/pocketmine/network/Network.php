<?php

/**
 * 来自SCAXE核心的AXENET Network
 * Network-related classes
 */
namespace pocketmine\network;

use pocketmine\network\protocol\AddEntityPacket;
use pocketmine\network\protocol\AddItemEntityPacket;
use pocketmine\network\protocol\AddPaintingPacket;
use pocketmine\network\protocol\AddPlayerPacket;
use pocketmine\network\protocol\AdventureSettingsPacket;
use pocketmine\network\protocol\AnimatePacket;
use pocketmine\network\protocol\BatchPacket;
use pocketmine\network\protocol\ChunkRadiusUpdatePacket;
use pocketmine\network\protocol\ContainerClosePacket;
use pocketmine\network\protocol\ContainerOpenPacket;
use pocketmine\network\protocol\ContainerSetContentPacket;
use pocketmine\network\protocol\ContainerSetDataPacket;
use pocketmine\network\protocol\ContainerSetSlotPacket;
use pocketmine\network\protocol\CraftingDataPacket;
use pocketmine\network\protocol\CraftingEventPacket;
use pocketmine\network\protocol\ChangeDimensionPacket;
use pocketmine\network\protocol\DataPacket;
use pocketmine\network\protocol\DropItemPacket;
use pocketmine\network\protocol\FullChunkDataPacket;
use pocketmine\network\protocol\Info;
use pocketmine\network\protocol\ItemFrameDropItemPacket;
use pocketmine\network\protocol\RequestChunkRadiusPacket;
use pocketmine\network\protocol\SetEntityLinkPacket;
use pocketmine\network\protocol\BlockEntityDataPacket;
use pocketmine\network\protocol\EntityEventPacket;
use pocketmine\network\protocol\ExplodePacket;
use pocketmine\network\protocol\HurtArmorPacket;
use pocketmine\network\protocol\Info as ProtocolInfo;
use pocketmine\network\protocol\InteractPacket;
use pocketmine\network\protocol\LevelEventPacket;
use pocketmine\network\protocol\DisconnectPacket;
use pocketmine\network\protocol\LoginPacket;
use pocketmine\network\protocol\PlayStatusPacket;
use pocketmine\network\protocol\TextPacket;
use pocketmine\network\protocol\TelemetryEventPacket;
use pocketmine\network\protocol\MoveEntityPacket;
use pocketmine\network\protocol\MovePlayerPacket;
use pocketmine\network\protocol\PlayerActionPacket;
use pocketmine\network\protocol\MobArmorEquipmentPacket;
use pocketmine\network\protocol\MobEquipmentPacket;
use pocketmine\network\protocol\RemoveBlockPacket;
use pocketmine\network\protocol\RemoveEntityPacket;
use pocketmine\network\protocol\RemovePlayerPacket;
use pocketmine\network\protocol\RespawnPacket;
use pocketmine\network\protocol\SetDifficultyPacket;
use pocketmine\network\protocol\SetEntityDataPacket;
use pocketmine\network\protocol\SetEntityMotionPacket;
use pocketmine\network\protocol\SetHealthPacket;
use pocketmine\network\protocol\SetPlayerGameTypePacket;
use pocketmine\network\protocol\SetSpawnPositionPacket;
use pocketmine\network\protocol\SetTimePacket;
use pocketmine\network\protocol\StartGamePacket;
use pocketmine\network\protocol\TakeItemEntityPacket;
use pocketmine\network\protocol\BlockEventPacket;
use pocketmine\network\protocol\UpdateBlockPacket;
use pocketmine\network\protocol\UseItemPacket;
use pocketmine\network\protocol\PlayerListPacket;
use pocketmine\network\protocol\PlayerInputPacket;
use pocketmine\network\protocol\MapInfoRequestPacket;
use pocketmine\network\protocol\ProtocolCompatibility;
use pocketmine\network\compat\BatchCodec;
use pocketmine\network\protocol\v11\BatchPacket as BatchPacketV11;
use pocketmine\network\protocol\v11\Info as InfoV11;
use pocketmine\Player;
use pocketmine\Server;
use pocketmine\utils\MainLogger;

class Network {

	public static $BATCH_THRESHOLD = 512;

	/** 单包解压上限见 BatchCodec::MAX_PAYLOAD (4MB) */
	const BATCH_MAX_PAYLOAD = BatchCodec::MAX_PAYLOAD;

	/** 每玩家每秒解压配额（解压后字节数）。正常客户端入站批包远小于此（单包理论上限约 180KB），8MB/s 已非常宽松 */
	const BATCH_DECOMPRESS_RATE_LIMIT = 8388608; // 8MB/s

	/** 60 秒窗口内解压违规达到该次数则踢出 */
	const BATCH_RATE_KICK_THRESHOLD = 3;

	/** 解压违规计数窗口（秒） */
	const BATCH_VIOLATION_WINDOW = 60;

	/** @var array[] 每玩家批包解压速率桶，键为 spl_object_hash(Player) */
	private $batchDecompressBuckets = [];

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
				if (\pocketmine\DEBUG > 1) {
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

		$str = $this->decompressBatchPayload($packet->payload, $p);
		if($str === false){
			return;
		}

		try {
			BatchCodec::forEachFramed($str, function($buf) use ($p){
				$header = ProtocolCompatibility::readPacketHeader($buf);
				if($header === null){
					return;
				}
				[$pid, $packetOffset] = $header;
				$pk = $this->getPacket($pid, (int) $p->getProtocol());
				if($pk === null){
					return;
				}
				if($pk::NETWORK_ID === Info::BATCH_PACKET or $pk::NETWORK_ID === protocol\v84\InfoV84::BATCH_PACKET){
					throw new \InvalidStateException("Invalid BatchPacket inside BatchPacket");
				}

				$pk->protocol = (int) $p->getProtocol();
				$pk->setBuffer($buf, $packetOffset);
				$pk->decode();
				$decodedOffset = $pk->getOffset();
				$pk = DataPacketManager::toCorePacket($pk);
				$p->handleDataPacket($pk);

				return $decodedOffset <= 0 ? false : null;
			});
		} catch (\Throwable $e) {
			if (\pocketmine\DEBUG > 1) {
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

		$str = $this->decompressBatchPayload($packet->payload, $p);
		if($str === false){
			return;
		}

		$protocol = ProtocolCompatibility::isProtocol011((int) $p->getProtocol()) ? (int) $p->getProtocol() : InfoV11::CURRENT_PROTOCOL;
		try{
			BatchCodec::forEachV11($str, function($pid, $packetOffset) use ($str, $p, $protocol){
				$pk = $this->getPacket($pid, $protocol);
				if($pk === null){
					return null;
				}

				return $this->handleProtocol011BatchPacket($pk, $str, $packetOffset, $p);
			});
		}catch(\Throwable $e){
			if(\pocketmine\DEBUG > 1){
				$logger = $this->server->getLogger();
				if($logger instanceof MainLogger){
					$logger->debug("V11 BatchPacket 0x" . bin2hex($packet->payload));
					$logger->logException($e);
				}
			}
		}
	}

	private function handleProtocol011BatchPacket($pk, string $buffer, int $packetOffset, Player $p) : int{
		if($pk::NETWORK_ID === InfoV11::BATCH_PACKET){
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
	 * 流式解压批包负载，带每玩家速率限制（压缩炸弹防护）。
	 *
	 * 单包解压输出超过 BatchCodec::MAX_PAYLOAD（4MB）或该玩家当秒解压配额
	 * （BATCH_DECOMPRESS_RATE_LIMIT，8MB/s）时立即中止解压、丢弃该包并计一次违规；
	 * BATCH_VIOLATION_WINDOW（60 秒）内违规达到 BATCH_RATE_KICK_THRESHOLD（3 次）则踢出。
	 *
	 * @param string $payload
	 * @param Player $p
	 * @return string|bool 成功返回解压结果，超限或解压失败返回 false
	 */
	private function decompressBatchPayload(string $payload, Player $p){
		$hash = spl_object_hash($p);
		$now = time();
		if(!isset($this->batchDecompressBuckets[$hash])){
			$this->batchDecompressBuckets[$hash] = [
				"second" => $now,
				"bytes" => 0,
				"violations" => 0,
				"firstViolation" => 0,
				"lastActive" => $now,
			];
		}
		$bucket = &$this->batchDecompressBuckets[$hash];
		$bucket["lastActive"] = $now;
		if(count($this->batchDecompressBuckets) > 256){
			foreach($this->batchDecompressBuckets as $k => $b){
				if($now - $b["lastActive"] > 120){
					unset($this->batchDecompressBuckets[$k]);
				}
			}
		}

		if($bucket["second"] !== $now){
			$bucket["second"] = $now;
			$bucket["bytes"] = 0;
		}
		$budget = self::BATCH_DECOMPRESS_RATE_LIMIT - $bucket["bytes"];
		if($budget < 4096){
			// 本秒配额已用尽，直接拒绝
			$this->onBatchDecompressViolation($p, $bucket);
			return false;
		}

		$limit = min(self::BATCH_MAX_PAYLOAD, $budget);
		$str = BatchCodec::streamingDecompress($payload, $limit);
		if($str === false){
			$this->onBatchDecompressViolation($p, $bucket);
			return false;
		}
		$bucket["bytes"] += strlen($str);
		return $str;
	}

	/**
	 * 记一次解压违规；窗口内累计达到阈值则踢出该玩家。
	 *
	 * @param Player $p
	 * @param array  $bucket
	 */
	private function onBatchDecompressViolation(Player $p, array &$bucket){
		$now = time();
		if($bucket["firstViolation"] === 0 or $now - $bucket["firstViolation"] > self::BATCH_VIOLATION_WINDOW){
			$bucket["firstViolation"] = $now;
			$bucket["violations"] = 0;
		}
		++$bucket["violations"];
		if($bucket["violations"] >= self::BATCH_RATE_KICK_THRESHOLD){
			$bucket["violations"] = 0;
			$bucket["firstViolation"] = 0;
			$p->kick("批包解压速率超限");
		}
	}

	/**
	 * @param $id
	 *
	 * @return DataPacket
	 */
	public function getPacket($id, int $protocol = null) {
		$isInitialV11Packet = $protocol !== null && $protocol < 0 && $id === InfoV11::BATCH_PACKET;
		/** @var DataPacket $class */
		$class = (ProtocolCompatibility::isProtocol011((int) $protocol) or $isInitialV11Packet) ? $this->v11PacketPool[$id] : $this->packetPool[$id];
		if($class !== null){
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

		foreach(DataPacketManager::getProtocol011PacketMap() as $id => $class){
			$this->registerV11Packet($id, $class);
		}

		// 0.15 (v84) 包按 v84 包 id 注册进主包池; v84 id (0x01-0x41) 与核心 id (0x8f+) 不冲突
		foreach(DataPacketManager::getProtocol015PacketMap() as $id => $class){
			$this->registerPacket($id, $class);
		}

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
	}
}
