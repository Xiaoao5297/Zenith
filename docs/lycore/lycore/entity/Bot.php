<?php

namespace lycore\entity;

use lycore\block\Block;
use lycore\entity\behavior\BotCombatBehavior;
use lycore\entity\behavior\BotMovementAI;
use lycore\event\entity\EntityDamageByChildEntityEvent;
use lycore\event\entity\EntityDamageByEntityEvent;
use lycore\event\entity\EntityDamageEvent;
use lycore\event\entity\EntityShootBowEvent;
use lycore\event\entity\ProjectileLaunchEvent;
use lycore\event\player\PlayerAnimationEvent;
use lycore\item\enchantment\Enchantment;
use lycore\item\Item as ItemItem;
use lycore\item\Potion;
use lycore\item\Tool;
use lycore\level\particle\SpellParticle;
use lycore\level\sound\EndermanTeleportSound;
use lycore\level\sound\LaunchSound;
use lycore\level\Position;
use lycore\math\AxisAlignedBB;
use lycore\math\Vector3;
use lycore\nbt\tag\ByteTag;
use lycore\nbt\tag\CompoundTag;
use lycore\nbt\tag\DoubleTag;
use lycore\nbt\tag\FloatTag;
use lycore\nbt\tag\IntTag;
use lycore\nbt\tag\ListTag;
use lycore\nbt\tag\StringTag;
use lycore\network\protocol\AddPlayerPacket;
use lycore\network\protocol\AnimatePacket;
use lycore\network\protocol\EntityEventPacket;
use lycore\network\protocol\MobArmorEquipmentPacket;
use lycore\network\protocol\MobEquipmentPacket;
use lycore\network\protocol\MovePlayerPacket;
use lycore\network\protocol\RemovePlayerPacket;
use lycore\network\protocol\Info as ProtocolInfo;
use lycore\Player;
use lycore\scheduler\CallbackTask;
use lycore\utils\UUID;
use lycore\utils\TextFormat;
use lycore\math\Vector2;

class Bot extends Mob implements NPC{
	const NETWORK_ID = -1;
	const DEFAULT_NAME = "PVPbot";
	const DEFAULT_SKIN_NAME = "Standard_Steve";
	const SEARCH_DISTANCE = 48;
	const PVPBOT_TARGET_LOSE_DISTANCE = 64;
	const PVPBOT_INITIAL_HEALING_POTIONS = 3;
	const PVPBOT_ELITE_INITIAL_HEALING_POTIONS = 6;
	const PVPBOT_INITIAL_ARROW_COUNT = 128;
	const PVPBOT_HEALING_HEALTH_THRESHOLD = 8;
	const PVPBOT_HEALING_COOLDOWN_TICKS = 20;
	const PVPBOT_MELEE_RANGE = 1.8;
	const PVPBOT_MELEE_COOLDOWN_TICKS = 10;
	const PVPBOT_BOW_MIN_RANGE = 6.0;
	const PVPBOT_BOW_RANGE = 24.0;
	const PVPBOT_BOW_FORCE = 2.0;
	const PVPBOT_BOW_COOLDOWN_TICKS = 30;
	const PVPBOT_BOW_EXPERT_RANGE = 32.0;
	const PVPBOT_BOW_EXPERT_FORCE = 3.0;
	const PVPBOT_BOW_EXPERT_COOLDOWN_TICKS = 16;
	const PVPBOT_ARROW_GRAVITY = 0.05;
	const PVPBOT_EXPERT_ARROW_MAX_LEAD_TICKS = 18.0;
	const PVPBOT_INITIAL_ENCHANTED_GOLDEN_APPLES = 3;
	const PVPBOT_INITIAL_NORMAL_GOLDEN_APPLES = 3;
	const PVPBOT_INITIAL_ENDER_PEARLS = 3;
	const PVPBOT_INITIAL_DIRT_STACKS = 3;
	const PVPBOT_INITIAL_DIRT_STACK_SIZE = 64;
	const PVPBOT_ELITE_INITIAL_ENCHANTED_GOLDEN_APPLES = 6;
	const PVPBOT_ELITE_INITIAL_ENDER_PEARLS = 6;
	const PVPBOT_ELITE_THORNS_CHANCE = 50;
	const PVPBOT_MANAGEMENT_ID_MAX = 9999;
	const PVPBOT_INSPECTOR_WAND_NAME = "Bot查阅器";
	const PVPBOT_INSPECTOR_WAND_INFINITY_LEVEL = 10;
	const PVPBOT_ENCHANTED_GOLDEN_APPLE_EAT_TICKS = 32;
	const PVPBOT_ENCHANTED_GOLDEN_APPLE_REEAT_THRESHOLD_TICKS = 100;
	const PVPBOT_NORMAL_GOLDEN_APPLE_EAT_TICKS = 32;
	const PVPBOT_NORMAL_GOLDEN_APPLE_REEAT_THRESHOLD_TICKS = 100;
	const PVPBOT_TYPE_CONFIG_RELOAD_INTERVAL_TICKS = 20;
	const PVPBOT_ENCHANTED_GOLDEN_APPLE_EATING_MOVE_MULTIPLIER = 0.3;
	const PVPBOT_ENCHANTED_GOLDEN_APPLE_EMERGENCY_HEALTH_THRESHOLD = 12;
	const PVPBOT_PICKUP_RADIUS = 1.4;
	const PVPBOT_MINING_ACTION_TICKS = 6;

	public $width = 0.6;
	public $length = 0.6;
	public $height = 1.8;
	public $eyeHeight = 1.62;
	public $dropExp = [0, 0];
	public $lastBreak = PHP_INT_MAX;

	/** @var ItemItem[] */
	protected $armor = [];
	/** @var ItemItem */
	protected $weapon;
	/** @var int */
	protected $difficulty = 1;
	/** @var UUID */
	protected $uuid;
	/** @var string */
	protected $rawUUID;
	/** @var string */
	protected $skinName = "Bot";
	/** @var string */
	protected $skin = "";
	/** @var BotMovementAI|null */
	protected $pvpBotMovementAi = null;
	/** @var int */
	protected $pvpBotHealingPotions = self::PVPBOT_INITIAL_HEALING_POTIONS;
	/** @var int */
	protected $pvpBotHealingCooldownTicks = 0;
	/** @var int */
	protected $pvpBotMeleeCooldownTicks = 0;
	/** @var int */
	protected $pvpBotBowCooldownTicks = 0;
	/** @var int */
	protected $pvpBotJumpRunTicks = 0;
	/** @var int */
	protected $pvpBotEnchantedGoldenApples = 0;
	/** @var int */
	protected $pvpBotEnderPearls = 0;
	/** @var int */
	protected $pvpBotArrows = 0;
	/** @var ItemItem|null */
	protected $pvpBotBow = null;
	/** @var int */
	protected $pvpBotEnchantedGoldenAppleEatTicks = 0;
	/** @var int */
	protected $pvpBotNormalGoldenAppleEatTicks = 0;
	/** @var ItemItem|null */
	protected $pvpBotPreEatingWeapon = null;
	/** @var Entity|null */
	protected $pvpBotLookTarget = null;
	/** @var Entity|null */
	protected $pvpBotEmergencyGoldenAppleTarget = null;
	/** @var ItemItem[] */
	protected $pvpBotInventory = [];
	/** @var ItemItem|null */
	protected $pvpBotActionItem = null;
	/** @var BotInventoryView|null */
	protected $pvpBotInventoryView = null;
	/** @var int */
	protected $pvpBotMiningActionTicks = 0;
	/** @var ItemItem|null */
	protected $pvpBotPreMiningWeapon = null;
	/** @var ItemItem|null */
	protected $pvpBotMiningPickaxe = null;
	/** @var int */
	protected $pvpBotManagementId = -1;
	/** @var string */
	protected $pvpBotTypeName = "pvpbot";
	/** @var bool[] */
	protected $pvpBotTypeSettings = [];
	/** @var BotTypeManager|null */
	protected $pvpBotTypeManager = null;
	/** @var string */
	protected $pvpBotTypeConfigRevision = "";
	/** @var string|null */
	protected $pvpBotTypeRuntimeSignature = null;
	/** @var string|null */
	protected $pvpBotTypeEquipmentSignature = null;
	/** @var string|null */
	protected $pvpBotTypeSkinSignature = null;
	/** @var int */
	protected $pvpBotTypeConfigReloadTicks = 0;
	/** @var bool */
	protected $botWalkEnabled = true;
	/** @var string */
	protected $botWalkMode = "random";
	/** @var array|null */
	protected $botWalkArea = null;
	/** @var array */
	protected $pvpBotRespawnConfig = ["enabled" => false, "delay" => 0, "point" => null];
	/** @var bool */
	protected $pvpBotRespawnScheduled = false;
	/** @var bool */
	private static $pvpBotManagementIdDisplayEnabled = false;

	public static function createNBT(float $x, float $y, float $z, int $difficulty = 1, float $yaw = 0.0, float $pitch = 0.0) : CompoundTag{
		$difficulty = self::normalizeDifficulty($difficulty);
		return new CompoundTag("", [
			"Pos" => new ListTag("Pos", [
				new DoubleTag("", $x),
				new DoubleTag("", $y),
				new DoubleTag("", $z)
			]),
			"Motion" => new ListTag("Motion", [
				new DoubleTag("", 0),
				new DoubleTag("", 0),
				new DoubleTag("", 0)
			]),
			"Rotation" => new ListTag("Rotation", [
				new FloatTag("", $yaw),
				new FloatTag("", $pitch)
			]),
			"NameTag" => new StringTag("NameTag", self::DEFAULT_NAME),
			"PVPBotDifficulty" => new IntTag("PVPBotDifficulty", $difficulty),
			"PVPBotHealingPotions" => new IntTag("PVPBotHealingPotions", self::getPVPBotInitialHealingPotionCount($difficulty)),
			"PVPBotEnchantedGoldenApples" => new IntTag("PVPBotEnchantedGoldenApples", self::getPVPBotInitialEnchantedGoldenAppleCount($difficulty)),
			"PVPBotEnderPearls" => new IntTag("PVPBotEnderPearls", self::getPVPBotInitialEnderPearlCount($difficulty)),
			"PVPBotArrowCount" => new IntTag("PVPBotArrowCount", $difficulty >= 3 ? self::PVPBOT_INITIAL_ARROW_COUNT : 0),
			"PVPBotManagementId" => new IntTag("PVPBotManagementId", mt_rand(0, self::PVPBOT_MANAGEMENT_ID_MAX)),
			"Skin" => new CompoundTag("Skin", [
				"Name" => new StringTag("Name", self::DEFAULT_SKIN_NAME),
				"Data" => new StringTag("Data", self::defaultSkinData())
			])
		]);
	}

	public static function normalizeDifficulty($difficulty) : int{
		return max(1, min(6, (int) $difficulty));
	}

	private static function getPVPBotInitialHealingPotionCount(int $difficulty) : int{
		return $difficulty >= 5 ? self::PVPBOT_ELITE_INITIAL_HEALING_POTIONS : self::PVPBOT_INITIAL_HEALING_POTIONS;
	}

	private static function getPVPBotInitialEnchantedGoldenAppleCount(int $difficulty) : int{
		return $difficulty >= 5 ? self::PVPBOT_ELITE_INITIAL_ENCHANTED_GOLDEN_APPLES : ($difficulty >= 4 ? self::PVPBOT_INITIAL_ENCHANTED_GOLDEN_APPLES : 0);
	}

	private static function getPVPBotInitialEnderPearlCount(int $difficulty) : int{
		return $difficulty >= 5 ? self::PVPBOT_ELITE_INITIAL_ENDER_PEARLS : ($difficulty >= 4 ? self::PVPBOT_INITIAL_ENDER_PEARLS : 0);
	}

	public static function defaultSkinData() : string{
		return str_repeat("\x00", 64 * 32 * 4);
	}

	public function initEntity(){
		$this->difficulty = isset($this->namedtag->PVPBotDifficulty) ? self::normalizeDifficulty($this->namedtag["PVPBotDifficulty"]) : 1;
		$this->setMaxHealth(20);

		parent::initEntity();
		$this->initializePVPBotManagementId();

		if(isset($this->namedtag->NameTag)){
			$this->setNameTag($this->namedtag["NameTag"]);
		}
		if(isset($this->namedtag->Skin) and $this->namedtag->Skin instanceof CompoundTag){
			$this->skinName = (string) $this->namedtag->Skin["Name"];
			$this->skin = (string) $this->namedtag->Skin["Data"];
		}
		if(strlen($this->skin) < 64 * 32 * 4){
			$this->skinName = self::DEFAULT_SKIN_NAME;
			$this->skin = self::defaultSkinData();
		}

		$this->uuid = UUID::fromData($this->getId(), $this->skin, $this->getName());
		$this->rawUUID = $this->uuid->toBinary();
		$this->initializePVPBotLoadout();
		$this->setMobEquipment($this->getDifficultyArmor($this->difficulty), $this->getDifficultyWeapon($this->difficulty));
		$this->loadPVPBotTypeConfiguration();
		$this->installBotCombatBehavior();
		$this->recoverPVPBotStandingHeight(true);
	}

	private function initializePVPBotManagementId(){
		$id = isset($this->namedtag->PVPBotManagementId) ? (int) $this->namedtag["PVPBotManagementId"] : -1;
		if($id < 0 or $id > self::PVPBOT_MANAGEMENT_ID_MAX or self::isPVPBotManagementIdInUse($id, $this)){
			$id = $this->generatePVPBotManagementId();
		}

		$this->pvpBotManagementId = $id;
		$this->namedtag->PVPBotManagementId = new IntTag("PVPBotManagementId", $id);
	}

	private function generatePVPBotManagementId() : int{
		for($attempt = 0; $attempt <= self::PVPBOT_MANAGEMENT_ID_MAX; ++$attempt){
			$id = mt_rand(0, self::PVPBOT_MANAGEMENT_ID_MAX);
			if(!self::isPVPBotManagementIdInUse($id, $this)){
				return $id;
			}
		}

		for($id = 0; $id <= self::PVPBOT_MANAGEMENT_ID_MAX; ++$id){
			if(!self::isPVPBotManagementIdInUse($id, $this)){
				return $id;
			}
		}

		return self::PVPBOT_MANAGEMENT_ID_MAX;
	}

	private static function isPVPBotManagementIdInUse(int $id, Bot $except = null) : bool{
		if($except === null or $except->server === null){
			return false;
		}

		foreach($except->server->getLevels() as $level){
			foreach($level->getEntities() as $entity){
				if($entity instanceof self and $entity !== $except and !$entity->closed and $entity->getPVPBotManagementId() === $id){
					return true;
				}
			}
		}

		return false;
	}

	public function getPVPBotManagementId() : int{
		return $this->pvpBotManagementId;
	}

	private function loadPVPBotTypeConfiguration(){
		if(!isset($this->namedtag->PVPBotType) or $this->server === null or !method_exists($this->server, "getDataPath")){
			return;
		}

		$this->pvpBotTypeManager = new BotTypeManager($this->server->getDataPath());
		$this->refreshPVPBotTypeFromManager($this->pvpBotTypeManager, true);
	}

	public function applyPVPBotType(string $name, array $type, array $equipment, array $skin = null){
		$this->applyPVPBotTypeRuntimeConfiguration($name, $type);
		$this->applyPVPBotTypeEquipment($equipment);
		$this->applyPVPBotTypeSkin($skin);
		$this->rememberPVPBotTypeConfiguration($name, $type);
	}

	public function refreshPVPBotTypeConfiguration(bool $force = false) : bool{
		if(!isset($this->namedtag->PVPBotType) or $this->server === null or !method_exists($this->server, "getDataPath")){
			return false;
		}
		if(!($this->pvpBotTypeManager instanceof BotTypeManager)){
			$this->pvpBotTypeManager = new BotTypeManager($this->server->getDataPath());
		}
		return $this->refreshPVPBotTypeFromManager($this->pvpBotTypeManager, $force);
	}

	public function refreshPVPBotTypeFromManager(BotTypeManager $manager, bool $force = false) : bool{
		if(!isset($this->namedtag->PVPBotType)){
			return false;
		}

		$name = (string) $this->namedtag["PVPBotType"];
		$revision = $manager->getRevision();
		if(!$force and $revision === $this->pvpBotTypeConfigRevision){
			return false;
		}

		$type = $manager->getType($name);
		$this->pvpBotTypeConfigRevision = $revision;
		if($type === null or strtolower($type["type"]) !== "pvpbot"){
			return false;
		}

		$runtimeSignature = self::getPVPBotTypeConfigurationSignature([
			"name" => $name,
			"display" => isset($type["name"]) ? $type["name"] : "",
			"settings" => isset($type["settings"]) ? $type["settings"] : [],
			"walk" => isset($type["walk"]) ? $type["walk"] : [],
			"respawn" => isset($type["respawn"]) ? $type["respawn"] : []
		]);
		$equipmentSignature = self::getPVPBotTypeConfigurationSignature(isset($type["equipment"]) ? $type["equipment"] : []);
		$skinSignature = self::getPVPBotTypeConfigurationSignature(isset($type["skin"]) ? $type["skin"] : BotTypeManager::DEFAULT_SKIN);
		$initial = $this->pvpBotTypeRuntimeSignature === null;
		$runtimeChanged = $initial or $runtimeSignature !== $this->pvpBotTypeRuntimeSignature;
		$equipmentChanged = $initial or $equipmentSignature !== $this->pvpBotTypeEquipmentSignature;
		$skinChanged = $initial or $skinSignature !== $this->pvpBotTypeSkinSignature;
		if(!$runtimeChanged and !$equipmentChanged and !$skinChanged){
			return false;
		}

		$skin = $manager->loadSkinData($type["skin"]);
		$respawnForSkin = $skinChanged and !empty($this->hasSpawned);
		if($respawnForSkin){
			$this->despawnFromAll();
		}
		if($runtimeChanged){
			$this->applyPVPBotTypeRuntimeConfiguration($name, $type);
		}
		if($equipmentChanged){
			$this->applyPVPBotTypeEquipment($manager->getEquipment($name));
		}
		if($skinChanged){
			$this->applyPVPBotTypeSkin($skin);
		}
		$this->rememberPVPBotTypeConfiguration($name, $type);
		if($respawnForSkin){
			$this->spawnToAll();
		}
		return true;
	}

	public static function refreshPVPBotTypeInstances($server, BotTypeManager $manager, string $name) : int{
		if(!is_object($server) or !method_exists($server, "getLevels")){
			return 0;
		}

		$updated = 0;
		foreach($server->getLevels() as $level){
			if(!is_object($level) or !method_exists($level, "getEntities")){
				continue;
			}
			foreach($level->getEntities() as $entity){
				if($entity instanceof self and !$entity->closed and strcasecmp($entity->getPVPBotTypeName(), $name) === 0 and $entity->refreshPVPBotTypeFromManager($manager, true)){
					++$updated;
				}
			}
		}
		return $updated;
	}

	private function applyPVPBotTypeRuntimeConfiguration(string $name, array $type){
		$this->pvpBotTypeName = $name;
		$this->pvpBotTypeSettings = [];
		foreach(BotTypeManager::SETTINGS as $setting){
			$this->pvpBotTypeSettings[$setting] = isset($type["settings"][$setting]) && (bool) $type["settings"][$setting];
		}
		$this->applyBotWalkConfig(isset($type["walk"]) && is_array($type["walk"]) ? $type["walk"] : []);
		$respawn = isset($type["respawn"]) && is_array($type["respawn"]) ? $type["respawn"] : [];
		$this->pvpBotRespawnConfig = [
			"enabled" => !empty($respawn["enabled"]) && isset($respawn["point"]) && is_array($respawn["point"]),
			"delay" => max(0, min(86400, isset($respawn["delay"]) ? (int) $respawn["delay"] : 0)),
			"point" => isset($respawn["point"]) && is_array($respawn["point"]) ? $respawn["point"] : null
		];
		$this->namedtag->PVPBotType = new StringTag("PVPBotType", $name);
		$displayName = isset($type["name"]) and is_string($type["name"]) and $type["name"] !== "" ? $type["name"] : self::DEFAULT_NAME;
		$this->setNameTag($displayName);
		$this->namedtag->NameTag = new StringTag("NameTag", $displayName);
		$this->refreshPVPBotManagementIdDisplay();
	}

	private function applyPVPBotTypeEquipment(array $equipment){
		$this->clearPVPBotDifficultyLoadout();
		$this->activatePVPBotConfiguredInventory(isset($equipment["armor"]) && is_array($equipment["armor"]) ? $equipment["armor"] : []);
		$this->activatePVPBotConfiguredInventory(isset($equipment["inventory"]) && is_array($equipment["inventory"]) ? $equipment["inventory"] : []);
		$this->broadcastPVPBotEquipment();
	}

	private function applyPVPBotTypeSkin(array $skin = null){
		$skinName = self::DEFAULT_SKIN_NAME;
		$skinData = self::defaultSkinData();
		if($skin !== null and isset($skin["name"], $skin["data"]) and is_string($skin["name"]) and is_string($skin["data"])){
			$skinName = $skin["name"];
			$skinData = $skin["data"];
		}
		$this->skin = $skinData;
		$this->skinName = $skinName;
		$this->namedtag->Skin = new CompoundTag("Skin", [
			"Name" => new StringTag("Name", $skinName),
			"Data" => new StringTag("Data", $skinData)
		]);
		$this->uuid = null;
		$this->rawUUID = null;
	}

	private function rememberPVPBotTypeConfiguration(string $name, array $type){
		$this->pvpBotTypeRuntimeSignature = self::getPVPBotTypeConfigurationSignature([
			"name" => $name,
			"display" => isset($type["name"]) ? $type["name"] : "",
			"settings" => isset($type["settings"]) ? $type["settings"] : [],
			"walk" => isset($type["walk"]) ? $type["walk"] : [],
			"respawn" => isset($type["respawn"]) ? $type["respawn"] : []
		]);
		$this->pvpBotTypeEquipmentSignature = self::getPVPBotTypeConfigurationSignature(isset($type["equipment"]) ? $type["equipment"] : []);
		$this->pvpBotTypeSkinSignature = self::getPVPBotTypeConfigurationSignature(isset($type["skin"]) ? $type["skin"] : BotTypeManager::DEFAULT_SKIN);
	}

	private static function getPVPBotTypeConfigurationSignature($value) : string{
		return sha1(serialize($value));
	}

	private function clearPVPBotDifficultyLoadout(){
		$this->difficulty = 0;
		$this->setMobEquipment([], ItemItem::get(ItemItem::AIR, 0, 0));
		$this->setPVPBotInventoryContents([]);
		$this->pvpBotHealingPotions = 0;
		$this->pvpBotEnchantedGoldenApples = 0;
		$this->pvpBotEnderPearls = 0;
		$this->pvpBotArrows = 0;
		$this->pvpBotBow = null;
		$this->pvpBotEnchantedGoldenAppleEatTicks = 0;
		$this->pvpBotNormalGoldenAppleEatTicks = 0;
		$this->pvpBotPreEatingWeapon = null;
		$this->namedtag->PVPBotDifficulty = new IntTag("PVPBotDifficulty", 0);
		$this->namedtag->PVPBotHealingPotions = new IntTag("PVPBotHealingPotions", 0);
		$this->namedtag->PVPBotEnchantedGoldenApples = new IntTag("PVPBotEnchantedGoldenApples", 0);
		$this->namedtag->PVPBotEnderPearls = new IntTag("PVPBotEnderPearls", 0);
		$this->namedtag->PVPBotArrowCount = new IntTag("PVPBotArrowCount", 0);
	}

	private function applyBotWalkConfig(array $walk){
		$this->botWalkEnabled = isset($walk["enabled"]) ? (bool) $walk["enabled"] : true;
		$this->botWalkMode = isset($walk["mode"]) && $walk["mode"] === "area" ? "area" : "random";
		$this->botWalkArea = isset($walk["area"]) && is_array($walk["area"]) ? $walk["area"] : null;
		if(!$this->botWalkEnabled){
			$this->motionX = 0.0;
			$this->motionZ = 0.0;
			$this->getBotMovementAI()->clear();
		}elseif($this->botWalkMode === "area"){
			$this->enforceBotWalkArea();
		}
	}

	public function isBotWalkEnabled() : bool{
		return $this->botWalkEnabled;
	}

	public function constrainBotWalkTarget(Vector3 $target) : Vector3{
		if($this->botWalkMode !== "area" or !is_array($this->botWalkArea) or $this->level === null or !isset($this->botWalkArea["world"], $this->botWalkArea["min"], $this->botWalkArea["max"]) or $this->botWalkArea["world"] !== $this->level->getName()){
			return $target;
		}
		return new Vector3(max($this->botWalkArea["min"]["x"], min($this->botWalkArea["max"]["x"], $target->x)), $target->y, max($this->botWalkArea["min"]["z"], min($this->botWalkArea["max"]["z"], $target->z)));
	}

	private function enforceBotWalkArea() : bool{
		if($this->botWalkMode !== "area" or !is_array($this->botWalkArea) or !isset($this->botWalkArea["world"], $this->botWalkArea["min"], $this->botWalkArea["max"], $this->botWalkArea["return"]) or $this->server === null){
			return false;
		}
		$area = $this->botWalkArea;
		if($this->level !== null and $this->level->getName() === $area["world"] and $this->x >= $area["min"]["x"] and $this->x <= $area["max"]["x"] + 1 and $this->z >= $area["min"]["z"] and $this->z <= $area["max"]["z"] + 1){
			return false;
		}
		$level = method_exists($this->server, "getLevelByName") ? $this->server->getLevelByName($area["world"]) : null;
		if(!($level instanceof \lycore\level\Level)){
			return false;
		}
		$return = $area["return"];
		$safeReturn = \lycore\command\defaults\BotCommand::resolvePVPBotSpawnPosition($level, new Vector3($return["x"] + 0.5, $return["y"] + 1, $return["z"] + 0.5));
		$this->teleport($safeReturn);
		$this->recoverPVPBotStandingHeight(true);
		return true;
	}

	private function activatePVPBotConfiguredInventory(array $items){
		foreach($items as $item){
			if(!($item instanceof ItemItem) or $item->getId() === ItemItem::AIR or $item->getCount() <= 0){
				continue;
			}

			if($item->getId() === ItemItem::ENCHANTED_GOLDEN_APPLE){
				$this->pvpBotEnchantedGoldenApples += $item->getCount();
				continue;
			}
			if($item->getId() === ItemItem::SPLASH_POTION and $item->getDamage() === Potion::HEALING){
				$this->pvpBotHealingPotions += $item->getCount();
				continue;
			}
			if($item->getId() === ItemItem::SNOWBALL and $item->getDamage() === 1){
				$this->pvpBotEnderPearls += $item->getCount();
				continue;
			}

			$equipItem = clone $item;
			if(self::getArmorSlot($item) >= 0 or self::isBow($item) or self::getSwordScore($item) > 0){
				$equipItem->setCount(1);
			}
			if(!$this->tryPickupPVPBotItem($equipItem)){
				$this->addPVPBotInventoryStack($item);
				continue;
			}
			if($item->getCount() > $equipItem->getCount()){
				$remaining = clone $item;
				$remaining->setCount($item->getCount() - $equipItem->getCount());
				$this->addPVPBotInventoryStack($remaining);
			}
		}

		$this->namedtag->PVPBotHealingPotions = new IntTag("PVPBotHealingPotions", $this->pvpBotHealingPotions);
		$this->namedtag->PVPBotEnchantedGoldenApples = new IntTag("PVPBotEnchantedGoldenApples", $this->pvpBotEnchantedGoldenApples);
		$this->namedtag->PVPBotEnderPearls = new IntTag("PVPBotEnderPearls", $this->pvpBotEnderPearls);
		$this->namedtag->PVPBotArrowCount = new IntTag("PVPBotArrowCount", $this->pvpBotArrows);
	}

	public function getPVPBotTypeName() : string{
		return $this->pvpBotTypeName;
	}

	public function hasPVPBotTypeSetting(string $setting) : bool{
		return !empty($this->pvpBotTypeSettings[$setting]);
	}

	private function isConfiguredBotType() : bool{
		return isset($this->namedtag->PVPBotType) && (string) $this->namedtag["PVPBotType"] !== "";
	}

	public static function togglePVPBotManagementIdDisplay() : bool{
		self::$pvpBotManagementIdDisplayEnabled = !self::$pvpBotManagementIdDisplayEnabled;
		return self::$pvpBotManagementIdDisplayEnabled;
	}

	public static function isPVPBotManagementIdDisplayEnabled() : bool{
		return self::$pvpBotManagementIdDisplayEnabled;
	}

	public function getPVPBotManagementDisplayName(Player $player) : string{
		$name = $this->getName();
		if(self::$pvpBotManagementIdDisplayEnabled and $player->isOp()){
			return $name . "\n" . TextFormat::GREEN . "机器人ID：" . $this->getPVPBotManagementId();
		}

		return $name;
	}

	public function refreshPVPBotManagementIdDisplay(){
		foreach($this->hasSpawned as $player){
			if($player instanceof Player){
				$this->sendData($player, [
					self::DATA_NAMETAG => [self::DATA_TYPE_STRING, $this->getPVPBotManagementDisplayName($player)]
				]);
			}
		}
	}

	public static function createPVPBotInspectorWand() : ItemItem{
		$wand = new ItemItem(ItemItem::STICK, 0, 1, "Stick");
		$infinity = Enchantment::getEnchantment(Enchantment::TYPE_BOW_INFINITY);
		$infinity->setLevel(self::PVPBOT_INSPECTOR_WAND_INFINITY_LEVEL);
		$wand->addEnchantment($infinity);
		$wand->setCustomName(TextFormat::ITALIC . TextFormat::DARK_GREEN . self::PVPBOT_INSPECTOR_WAND_NAME);
		return $wand;
	}

	public static function isPVPBotInspectorWand(ItemItem $item) : bool{
		$infinity = $item->getEnchantment(Enchantment::TYPE_BOW_INFINITY);
		return $item->getId() === ItemItem::STICK
			and $item->getCustomName() === TextFormat::ITALIC . TextFormat::DARK_GREEN . self::PVPBOT_INSPECTOR_WAND_NAME
			and $infinity !== null
			and $infinity->getLevel() >= self::PVPBOT_INSPECTOR_WAND_INFINITY_LEVEL;
	}

	public function onInteract(Player $player, ItemItem $item) : bool{
		if(!$player->isOp() or !self::isPVPBotInspectorWand($item)){
			return false;
		}

		$player->sendMessage($this->getPVPBotManagementInfo());
		return true;
	}

	public function getPVPBotManagementInfo() : string{
		$level = $this->getLevel();
		$worldName = $level !== null ? $level->getName() : "未加载";
		return implode("\n", [
			"名称：" . TextFormat::clean($this->getName()),
			"机器人ID：" . $this->getPVPBotManagementId(),
			"机器人类型：PVP机器人",
			"盔甲栏：" . self::formatPVPBotManagementItems($this->getArmorContents()),
			"背包：" . self::formatPVPBotManagementItems($this->getPVPBotInventoryContents()),
			"位置：" . round($this->x, 2) . ", " . round($this->y, 2) . ", " . round($this->z, 2) . ", " . $worldName,
		]);
	}

	private static function formatPVPBotManagementItems(array $items) : string{
		$formatted = [];
		foreach($items as $item){
			if(!($item instanceof ItemItem) or $item->getId() === ItemItem::AIR or $item->getCount() <= 0){
				continue;
			}
			$formatted[] = ($item->hasEnchantments() ? "已附魔 " : "") . TextFormat::clean($item->getName()) . " x" . $item->getCount();
		}

		return count($formatted) > 0 ? implode(", ", $formatted) : "无";
	}

	protected function initializePVPBotLoadout(){
		$this->pvpBotHealingPotions = self::getPVPBotInitialHealingPotionCount($this->difficulty);
		if(isset($this->namedtag) and isset($this->namedtag->PVPBotHealingPotions)){
			$this->pvpBotHealingPotions = max(0, (int) $this->namedtag["PVPBotHealingPotions"]);
		}
		$this->pvpBotHealingCooldownTicks = 0;
		$this->pvpBotMeleeCooldownTicks = 0;
		$this->pvpBotBowCooldownTicks = 0;
		$this->pvpBotJumpRunTicks = 0;
		$this->pvpBotEnchantedGoldenApples = self::getPVPBotInitialEnchantedGoldenAppleCount($this->difficulty);
		if(isset($this->namedtag) and isset($this->namedtag->PVPBotEnchantedGoldenApples)){
			$this->pvpBotEnchantedGoldenApples = max(0, (int) $this->namedtag["PVPBotEnchantedGoldenApples"]);
		}
		$this->pvpBotEnderPearls = self::getPVPBotInitialEnderPearlCount($this->difficulty);
		if(isset($this->namedtag) and isset($this->namedtag->PVPBotEnderPearls)){
			$this->pvpBotEnderPearls = max(0, (int) $this->namedtag["PVPBotEnderPearls"]);
		}
		$this->pvpBotArrows = $this->difficulty >= 3 ? self::PVPBOT_INITIAL_ARROW_COUNT : 0;
		if(isset($this->namedtag) and isset($this->namedtag->PVPBotArrowCount)){
			$this->pvpBotArrows = max(0, (int) $this->namedtag["PVPBotArrowCount"]);
		}
		$this->pvpBotBow = $this->getDifficultyBow($this->difficulty);
		$this->pvpBotEnchantedGoldenAppleEatTicks = 0;
		$this->pvpBotNormalGoldenAppleEatTicks = 0;
		$this->pvpBotPreEatingWeapon = null;
		$this->pvpBotLookTarget = null;
		$this->pvpBotEmergencyGoldenAppleTarget = null;
		$this->pvpBotInventory = [];
		if($this->difficulty === 3){
			$this->addPVPBotInventoryItem(ItemItem::get(ItemItem::GOLDEN_APPLE, 0, self::PVPBOT_INITIAL_NORMAL_GOLDEN_APPLES));
		}
		if($this->difficulty === 5){
			$this->addPVPBotInventoryItem(ItemItem::get(ItemItem::GOLDEN_APPLE, 0, self::PVPBOT_INITIAL_NORMAL_GOLDEN_APPLES));
		}
		if($this->difficulty === 6){
			$this->addPVPBotInventoryItem(ItemItem::get(ItemItem::GOLDEN_APPLE, 0, self::PVPBOT_INITIAL_NORMAL_GOLDEN_APPLES));
			for($stack = 0; $stack < self::PVPBOT_INITIAL_DIRT_STACKS; ++$stack){
				$this->addPVPBotInventoryStack(ItemItem::get(ItemItem::DIRT, 0, self::PVPBOT_INITIAL_DIRT_STACK_SIZE));
			}
			$this->addPVPBotInventoryStack(ItemItem::get(ItemItem::DIAMOND_PICKAXE, 0, 1));
		}
		$this->pvpBotMiningActionTicks = 0;
		$this->pvpBotPreMiningWeapon = null;
		$this->pvpBotMiningPickaxe = null;
		$this->pvpBotMovementAi = new BotMovementAI($this);
	}

	protected function installBotCombatBehavior(){
		$this->behaviors = [];
		$behavior = new BotCombatBehavior($this, true);
		$behavior->lookDistance = self::SEARCH_DISTANCE;
		$this->addBehavior($behavior);
		$this->setBehaviorsEnabled(true);
	}

	public static function isPVPBotHostileMobThreat(Entity $entity) : bool{
		if($entity instanceof Player or $entity instanceof self){
			return false;
		}
		if($entity->closed or !$entity->isAlive()){
			return false;
		}

		$networkId = $entity::NETWORK_ID;
		if($networkId >= 0 and NaturalMobSpawnRules::isHostileEntityId((int) $networkId)){
			return true;
		}

		return $entity instanceof Monster or
			$entity instanceof Slime or
			$entity instanceof LavaSlime or
			$entity instanceof Blaze or
			$entity instanceof Ghast;
	}

	public function usesPm1eGroundAi() : bool{
		return true;
	}

	protected function getPm1eBaseSpeed($liquidType = null) : float{
		if($liquidType !== null){
			return parent::getPm1eBaseSpeed($liquidType);
		}

		return 0.24;
	}

	protected function usesPm1eJumpingAi() : bool{
		return $this->pvpBotJumpRunTicks > 0;
	}

	public function getBotMovementAI() : BotMovementAI{
		if(!($this->pvpBotMovementAi instanceof BotMovementAI)){
			$this->pvpBotMovementAi = new BotMovementAI($this);
		}

		return $this->pvpBotMovementAi;
	}

	public function setPVPBotJumpRunEnabled(bool $enabled){
		$this->pvpBotJumpRunTicks = $enabled ? 4 : 0;
	}

	public function setPVPBotLookTarget(Entity $target = null){
		$this->pvpBotLookTarget = $target;
	}

	private function updatePVPBotCombatLookTarget(){
		if(!($this->pvpBotLookTarget instanceof Entity) or $this->pvpBotLookTarget->closed or !$this->pvpBotLookTarget->isAlive()){
			$this->pvpBotLookTarget = null;
			return;
		}

		$dx = $this->pvpBotLookTarget->x - $this->x;
		$dz = $this->pvpBotLookTarget->z - $this->z;
		$horizontalDistance = sqrt(($dx * $dx) + ($dz * $dz));
		if($horizontalDistance > 0.0001){
			$this->yaw = -atan2($dx, $dz) * 180 / M_PI;
		}

		$dy = ($this->pvpBotLookTarget->y + $this->pvpBotLookTarget->eyeHeight) - ($this->y + $this->eyeHeight);
		$this->pitch = -atan2($dy, max(0.0001, $horizontalDistance)) * 180 / M_PI;
	}

	protected function tickPVPBotCombatTimers(int $tickDiff){
		if($this->pvpBotMeleeCooldownTicks > 0){
			$this->pvpBotMeleeCooldownTicks = max(0, $this->pvpBotMeleeCooldownTicks - $tickDiff);
		}
		if($this->pvpBotBowCooldownTicks > 0){
			$this->pvpBotBowCooldownTicks = max(0, $this->pvpBotBowCooldownTicks - $tickDiff);
		}
		if($this->pvpBotHealingCooldownTicks > 0){
			$this->pvpBotHealingCooldownTicks = max(0, $this->pvpBotHealingCooldownTicks - $tickDiff);
		}
		if($this->pvpBotJumpRunTicks > 0){
			$this->pvpBotJumpRunTicks = max(0, $this->pvpBotJumpRunTicks - $tickDiff);
		}
		if($this->pvpBotMiningActionTicks > 0){
			$this->pvpBotMiningActionTicks = max(0, $this->pvpBotMiningActionTicks - $tickDiff);
			if($this->pvpBotMiningActionTicks === 0){
				$this->finishPVPBotMiningAction();
			}
		}
		if($this->pvpBotEnchantedGoldenAppleEatTicks > 0){
			$this->pvpBotEnchantedGoldenAppleEatTicks = max(0, $this->pvpBotEnchantedGoldenAppleEatTicks - $tickDiff);
			if($this->pvpBotEnchantedGoldenAppleEatTicks === 0){
				$this->finishPVPBotEnchantedGoldenAppleEating();
			}
		}
		if($this->pvpBotNormalGoldenAppleEatTicks > 0){
			$this->pvpBotNormalGoldenAppleEatTicks = max(0, $this->pvpBotNormalGoldenAppleEatTicks - $tickDiff);
			if($this->pvpBotNormalGoldenAppleEatTicks === 0){
				$this->finishPVPBotNormalGoldenAppleEating();
			}
		}
	}

	public function getPVPBotHealingPotionCount() : int{
		return max(0, (int) $this->pvpBotHealingPotions);
	}

	public function getPVPBotHealingPotionItem() : ItemItem{
		return ItemItem::get(ItemItem::SPLASH_POTION, Potion::HEALING, $this->getPVPBotHealingPotionCount());
	}

	public function getPVPBotEnchantedGoldenAppleCount() : int{
		return max(0, (int) $this->pvpBotEnchantedGoldenApples);
	}

	public function getPVPBotEnchantedGoldenAppleItem() : ItemItem{
		return ItemItem::get(ItemItem::ENCHANTED_GOLDEN_APPLE, 0, $this->getPVPBotEnchantedGoldenAppleCount());
	}

	public function getPVPBotEnderPearlCount() : int{
		return max(0, (int) $this->pvpBotEnderPearls);
	}

	public function getPVPBotEnderPearlItem() : ItemItem{
		return ItemItem::get(ItemItem::SNOWBALL, 1, $this->getPVPBotEnderPearlCount());
	}

	public function getPVPBotArrowCount() : int{
		return max(0, (int) $this->pvpBotArrows);
	}

	public function getPVPBotArrowItem() : ItemItem{
		return ItemItem::get(ItemItem::ARROW, 0, $this->getPVPBotArrowCount());
	}

	public function hasPVPBotBow() : bool{
		return $this->pvpBotBow instanceof ItemItem and $this->pvpBotBow->getId() === ItemItem::BOW;
	}

	public function getPVPBotBowItem() : ItemItem{
		return $this->hasPVPBotBow() ? clone $this->pvpBotBow : ItemItem::get(ItemItem::AIR, 0, 0);
	}

	private function isPVPBotExpertArcher() : bool{
		return $this->difficulty >= 4;
	}

	private function getPVPBotBowRange() : float{
		return $this->isPVPBotExpertArcher() ? self::PVPBOT_BOW_EXPERT_RANGE : self::PVPBOT_BOW_RANGE;
	}

	private function getPVPBotBowForce() : float{
		return $this->isPVPBotExpertArcher() ? self::PVPBOT_BOW_EXPERT_FORCE : self::PVPBOT_BOW_FORCE;
	}

	private function getPVPBotBowCooldownTicks() : int{
		return $this->isPVPBotExpertArcher() ? self::PVPBOT_BOW_EXPERT_COOLDOWN_TICKS : self::PVPBOT_BOW_COOLDOWN_TICKS;
	}

	public function canUsePVPBotBowRangedModeAgainst(Entity $target, float $distance = null) : bool{
		if($this->isPVPBotEatingGoldenApple() or $this->isPVPBotMiningObstacle()){
			return false;
		}
		if(!$this->hasPVPBotBow() or $this->pvpBotArrows <= 0){
			return false;
		}
		if($target->closed or !$target->isAlive()){
			return false;
		}

		$distance = $distance === null ? $this->distance($target) : $distance;
		return $distance >= self::PVPBOT_BOW_MIN_RANGE and $distance <= $this->getPVPBotBowRange() and $this->canPVPBotArrowReach($target);
	}

	public function canUsePVPBotBowAgainst(Entity $target, float $distance = null) : bool{
		if($this->pvpBotBowCooldownTicks > 0){
			return false;
		}

		return $this->canUsePVPBotBowRangedModeAgainst($target, $distance);
	}

	public function enterPVPBotBowCombatStance() : bool{
		if(!$this->hasPVPBotBow() or $this->isPVPBotEatingGoldenApple()){
			return false;
		}

		$bow = $this->getPVPBotBowItem();
		if($this->getWeapon()->getId() === $bow->getId()){
			return true;
		}

		$this->weapon = $bow;
		$this->broadcastPVPBotEquipment();
		return true;
	}

	public function leavePVPBotBowCombatStance(){
		if($this->isPVPBotEatingGoldenApple()){
			return;
		}
		if($this->getWeapon()->getId() !== ItemItem::BOW){
			return;
		}

		$this->weapon = $this->getDifficultyWeapon($this->difficulty);
		$this->broadcastPVPBotEquipment();
	}

	public function tryPVPBotBowAttack(Entity $target) : bool{
		if($this->hasPVPBotTypeSetting("noshoot")){
			return false;
		}
		$distance = $this->distance($target);
		if(!$this->canUsePVPBotBowAgainst($target, $distance)){
			return false;
		}

		$this->enterPVPBotBowCombatStance();
		$this->broadcastPVPBotEntityEvent(EntityEventPacket::USE_ITEM);
		$this->setDataFlag(self::DATA_FLAGS, self::DATA_FLAG_ACTION, true);

		$force = $this->getPVPBotBowForce();
		$launched = $this->launchPVPBotArrowAt($target, $this->createPVPBotArrowNBT($target, $force), $force);
		$this->setDataFlag(self::DATA_FLAGS, self::DATA_FLAG_ACTION, false);
		if($launched){
			if(!$this->hasPVPBotTypeSetting("noconsume")){
				--$this->pvpBotArrows;
			}
			$this->pvpBotBowCooldownTicks = $this->getPVPBotBowCooldownTicks();
			if(isset($this->namedtag)){
				$this->namedtag->PVPBotArrowCount = new IntTag("PVPBotArrowCount", $this->pvpBotArrows);
			}
		}

		return $launched;
	}

	public function isPVPBotEatingEnchantedGoldenApple() : bool{
		return $this->pvpBotEnchantedGoldenAppleEatTicks > 0;
	}

	public function isPVPBotEatingGoldenApple() : bool{
		return $this->isPVPBotEatingEnchantedGoldenApple() or $this->pvpBotNormalGoldenAppleEatTicks > 0;
	}

	public function tickPVPBotGoldenAppleCombat(Entity $target = null){
		if($this->hasPVPBotTypeSetting("noeat")){
			return;
		}
		$appleTarget = $target instanceof Entity ? $target : ($this->hasPendingPVPBotEmergencyGoldenAppleTarget() ? $this->pvpBotEmergencyGoldenAppleTarget : null);
		if($appleTarget instanceof Entity){
			$this->setPVPBotLookTarget($appleTarget);
		}
		if($this->shouldStartPVPBotEnchantedGoldenApple()){
			$this->tryUsePVPBotEnderPearlBeforeGoldenApple($appleTarget);
			$this->pvpBotEmergencyGoldenAppleTarget = null;
			$this->startPVPBotEnchantedGoldenAppleEating();
			return;
		}
		if($target instanceof Entity and !$target->closed and $target->isAlive() and $this->shouldStartPVPBotNormalGoldenApple()){
			$this->startPVPBotNormalGoldenAppleEating();
		}
	}

	public function tickPVPBotEnchantedGoldenAppleCombat(Entity $target = null){
		$this->tickPVPBotGoldenAppleCombat($target);
	}

	private function shouldStartPVPBotEnchantedGoldenApple() : bool{
		if(($this->difficulty < 4 and !$this->isConfiguredBotType()) or $this->pvpBotEnchantedGoldenApples <= 0 or $this->isPVPBotEatingGoldenApple()){
			return false;
		}
		if($this->hasPendingPVPBotEmergencyGoldenAppleTarget()){
			return true;
		}

		foreach($this->getPVPBotEnchantedGoldenAppleEffectIds() as $effectId){
			$effect = $this->getEffect($effectId);
			if(!($effect instanceof Effect)){
				return true;
			}
			if($effect->getDuration() <= self::PVPBOT_ENCHANTED_GOLDEN_APPLE_REEAT_THRESHOLD_TICKS){
				return true;
			}
		}

		return false;
	}

	private function hasPendingPVPBotEmergencyGoldenAppleTarget() : bool{
		if(!($this->pvpBotEmergencyGoldenAppleTarget instanceof Entity)){
			return false;
		}
		if($this->pvpBotEmergencyGoldenAppleTarget->closed or !$this->pvpBotEmergencyGoldenAppleTarget->isAlive()){
			$this->pvpBotEmergencyGoldenAppleTarget = null;
			return false;
		}

		return true;
	}

	private function queuePVPBotEmergencyGoldenApple(Entity $target) : bool{
		if(($this->difficulty < 4 and !$this->isConfiguredBotType()) or $this->pvpBotEnchantedGoldenApples <= 0 or $this->isPVPBotEatingGoldenApple()){
			return false;
		}
		if($target->closed or !$target->isAlive()){
			return false;
		}

		$this->pvpBotEmergencyGoldenAppleTarget = $target;
		return true;
	}

	private function startPVPBotEnchantedGoldenAppleEating(){
		$this->cancelPVPBotMiningAction();
		$this->pvpBotEnchantedGoldenAppleEatTicks = self::PVPBOT_ENCHANTED_GOLDEN_APPLE_EAT_TICKS;
		$this->pvpBotPreEatingWeapon = clone $this->getWeapon();
		$this->weapon = ItemItem::get(ItemItem::ENCHANTED_GOLDEN_APPLE, 0, 1);
		$this->broadcastPVPBotEquipment();
		$this->broadcastPVPBotEntityEvent(EntityEventPacket::USE_ITEM);
	}

	private function shouldStartPVPBotNormalGoldenApple() : bool{
		if($this->isPVPBotEatingGoldenApple() or !($this->getPVPBotNormalGoldenAppleItem() instanceof ItemItem)){
			return false;
		}
		if($this->getHealth() <= self::PVPBOT_ENCHANTED_GOLDEN_APPLE_EMERGENCY_HEALTH_THRESHOLD){
			return true;
		}

		$absorption = $this->getEffect(Effect::ABSORPTION);
		return !($absorption instanceof Effect) or $absorption->getDuration() <= self::PVPBOT_NORMAL_GOLDEN_APPLE_REEAT_THRESHOLD_TICKS;
	}

	private function startPVPBotNormalGoldenAppleEating() : bool{
		$apple = $this->getPVPBotNormalGoldenAppleItem();
		if(!($apple instanceof ItemItem) or $this->isPVPBotEatingGoldenApple()){
			return false;
		}

		$this->cancelPVPBotMiningAction();
		$this->pvpBotNormalGoldenAppleEatTicks = self::PVPBOT_NORMAL_GOLDEN_APPLE_EAT_TICKS;
		$this->pvpBotPreEatingWeapon = clone $this->getWeapon();
		$this->weapon = ItemItem::get(ItemItem::GOLDEN_APPLE, 0, 1);
		$this->broadcastPVPBotEquipment();
		$this->broadcastPVPBotEntityEvent(EntityEventPacket::USE_ITEM);
		return true;
	}

	private function tryUsePVPBotEnderPearlBeforeGoldenApple(Entity $target = null) : bool{
		if($this->hasPVPBotTypeSetting("noteleport")){
			return false;
		}
		if(!($target instanceof Entity) or ($this->difficulty < 4 and !$this->isConfiguredBotType()) or $this->pvpBotEnderPearls <= 0){
			return false;
		}

		$nbt = $this->createPVPBotEnderPearlNBT($target);
		if(!($nbt instanceof CompoundTag)){
			return false;
		}

		$previousWeapon = clone $this->getWeapon();
		$this->weapon = ItemItem::get(ItemItem::SNOWBALL, 1, 1);
		$this->broadcastPVPBotEquipment();

		$launched = $this->launchPVPBotEnderPearlAt($target, $nbt);
		if($launched){
			--$this->pvpBotEnderPearls;
			if(isset($this->namedtag)){
				$this->namedtag->PVPBotEnderPearls = new IntTag("PVPBotEnderPearls", $this->pvpBotEnderPearls);
			}
		}

		$this->weapon = $previousWeapon;
		return $launched;
	}

	private function createPVPBotArrowNBT(Entity $target, float $force) : CompoundTag{
		$startY = $this->y + $this->eyeHeight;
		$motion = $this->isPVPBotExpertArcher() ? $this->calculatePVPBotExpertArrowMotion($target, $force, $startY) : $this->calculatePVPBotDirectArrowMotion($target, $startY);
		$motionX = $motion[0];
		$motionY = $motion[1];
		$motionZ = $motion[2];
		$horizontal = sqrt(($motionX * $motionX) + ($motionZ * $motionZ));

		return new CompoundTag("", [
			"Pos" => new ListTag("Pos", [
				new DoubleTag("", $this->x),
				new DoubleTag("", $startY),
				new DoubleTag("", $this->z)
			]),
			"Motion" => new ListTag("Motion", [
				new DoubleTag("", $motionX),
				new DoubleTag("", $motionY),
				new DoubleTag("", $motionZ)
			]),
			"Rotation" => new ListTag("Rotation", [
				new FloatTag("", -atan2($motionX, $motionZ) * 180 / M_PI),
				new FloatTag("", -atan2($motionY, max(0.0001, $horizontal)) * 180 / M_PI)
			]),
			"PVPBotArrow" => new ByteTag("PVPBotArrow", 1),
			"PVPBotArrowNoPickup" => new ByteTag("PVPBotArrowNoPickup", 1)
		]);
	}

	private function calculatePVPBotDirectArrowMotion(Entity $target, float $startY) : array{
		$targetEyeHeight = $target->eyeHeight !== null ? $target->eyeHeight : ($target->height / 2);
		$dx = $target->x - $this->x;
		$dy = ($target->y + $targetEyeHeight) - $startY;
		$dz = $target->z - $this->z;

		return $this->normalizePVPBotArrowMotion($dx, $dy, $dz);
	}

	private function calculatePVPBotExpertArrowMotion(Entity $target, float $force, float $startY) : array{
		$targetY = $this->getPVPBotArrowTargetY($target);
		$velocity = $this->getPVPBotTargetLeadVelocity($target);
		$aimX = $target->x;
		$aimY = $targetY;
		$aimZ = $target->z;
		$travelTicks = $this->estimatePVPBotArrowTravelTicks($target->x - $this->x, $target->z - $this->z, $force);

		for($i = 0; $i < 3; ++$i){
			$leadTicks = min(self::PVPBOT_EXPERT_ARROW_MAX_LEAD_TICKS, max(1.0, $travelTicks));
			$aimX = $target->x + $this->clampPVPBotFloat($velocity[0] * $leadTicks, -7.0, 7.0);
			$aimY = $targetY + $this->clampPVPBotFloat($velocity[1] * $leadTicks, -1.5, 1.5);
			$aimZ = $target->z + $this->clampPVPBotFloat($velocity[2] * $leadTicks, -7.0, 7.0);

			$dx = $aimX - $this->x;
			$dy = $aimY - $startY;
			$dz = $aimZ - $this->z;
			$horizontal = sqrt(($dx * $dx) + ($dz * $dz));
			$angle = $this->solvePVPBotArrowLaunchAngle($horizontal, $dy, $force);
			if($angle !== null){
				$travelTicks = $horizontal / max(0.0001, $force * cos($angle));
			}else{
				$travelTicks = $this->estimatePVPBotArrowTravelTicks($dx, $dz, $force);
			}
		}

		$dx = $aimX - $this->x;
		$dy = $aimY - $startY;
		$dz = $aimZ - $this->z;
		$motion = $this->calculatePVPBotBallisticArrowMotion($dx, $dy, $dz, $force);
		if($motion !== null){
			return $motion;
		}

		$travelTicks = $this->estimatePVPBotArrowTravelTicks($dx, $dz, $force);
		$dropCompensation = self::PVPBOT_ARROW_GRAVITY * $travelTicks * ($travelTicks + 1.0) / 2.0;
		return $this->normalizePVPBotArrowMotion($dx, $dy + $dropCompensation, $dz);
	}

	private function calculatePVPBotBallisticArrowMotion(float $dx, float $dy, float $dz, float $force){
		$horizontal = sqrt(($dx * $dx) + ($dz * $dz));
		$angle = $this->solvePVPBotArrowLaunchAngle($horizontal, $dy, $force);
		if($angle === null){
			return null;
		}

		if($horizontal <= 0.0001){
			return $this->normalizePVPBotArrowMotion($dx, $dy, $dz);
		}

		$horizontalMotion = cos($angle);
		return [
			($dx / $horizontal) * $horizontalMotion,
			sin($angle),
			($dz / $horizontal) * $horizontalMotion
		];
	}

	private function solvePVPBotArrowLaunchAngle(float $horizontal, float $dy, float $force){
		if($horizontal <= 0.0001 or $force <= 0.0001){
			return null;
		}

		$low = -0.45;
		$high = 1.15;
		$lowError = $this->getPVPBotArrowVerticalError($low, $horizontal, $dy, $force);
		$highError = $this->getPVPBotArrowVerticalError($high, $horizontal, $dy, $force);
		if($lowError >= 0.0){
			return $low;
		}
		if($highError < 0.0){
			return null;
		}

		for($i = 0; $i < 28; ++$i){
			$mid = ($low + $high) / 2.0;
			$error = $this->getPVPBotArrowVerticalError($mid, $horizontal, $dy, $force);
			if($error >= 0.0){
				$high = $mid;
			}else{
				$low = $mid;
			}
		}

		return $high;
	}

	private function getPVPBotArrowVerticalError(float $angle, float $horizontal, float $dy, float $force) : float{
		$cos = cos($angle);
		if($cos <= 0.0001){
			return -INF;
		}

		$ticks = $horizontal / max(0.0001, $force * $cos);
		$vertical = ($force * sin($angle) * $ticks) - (self::PVPBOT_ARROW_GRAVITY * $ticks * ($ticks + 1.0) / 2.0);
		return $vertical - $dy;
	}

	private function getPVPBotArrowTargetY(Entity $target) : float{
		$targetEyeHeight = $target->eyeHeight !== null ? $target->eyeHeight : ($target->height / 2);
		$upperBodyHeight = max($target->height * 0.72, $targetEyeHeight - 0.18);
		return $target->y + min($targetEyeHeight, $upperBodyHeight);
	}

	private function getPVPBotTargetLeadVelocity(Entity $target) : array{
		$velocityX = is_numeric($target->motionX) ? (float) $target->motionX : 0.0;
		$velocityY = is_numeric($target->motionY) ? (float) $target->motionY : 0.0;
		$velocityZ = is_numeric($target->motionZ) ? (float) $target->motionZ : 0.0;
		$hasPositionVelocity = false;

		if($target instanceof Player and $target->speed instanceof Vector3){
			$playerVelocityX = is_numeric($target->speed->x) ? -(float) $target->speed->x : 0.0;
			$playerVelocityY = is_numeric($target->speed->y) ? -(float) $target->speed->y : 0.0;
			$playerVelocityZ = is_numeric($target->speed->z) ? -(float) $target->speed->z : 0.0;
			$playerVelocityLengthSquared = ($playerVelocityX * $playerVelocityX) + ($playerVelocityY * $playerVelocityY) + ($playerVelocityZ * $playerVelocityZ);
			if($playerVelocityLengthSquared > 0.0001 and $playerVelocityLengthSquared <= 2.25){
				$velocityX = $playerVelocityX;
				$velocityY = $playerVelocityY;
				$velocityZ = $playerVelocityZ;
				$hasPositionVelocity = true;
			}
		}

		if(!$hasPositionVelocity and $target->lastX !== null and $target->lastY !== null and $target->lastZ !== null){
			$deltaX = $target->x - $target->lastX;
			$deltaY = $target->y - $target->lastY;
			$deltaZ = $target->z - $target->lastZ;
			$deltaLengthSquared = ($deltaX * $deltaX) + ($deltaY * $deltaY) + ($deltaZ * $deltaZ);
			if($deltaLengthSquared > 0.0001 and $deltaLengthSquared <= 2.25){
				$velocityX = $deltaX;
				$velocityY = $deltaY;
				$velocityZ = $deltaZ;
			}
		}

		return [
			$this->clampPVPBotFloat($velocityX, -0.45, 0.45),
			$this->clampPVPBotFloat($velocityY, -0.25, 0.35),
			$this->clampPVPBotFloat($velocityZ, -0.45, 0.45)
		];
	}

	private function estimatePVPBotArrowTravelTicks(float $dx, float $dz, float $force) : float{
		$horizontal = sqrt(($dx * $dx) + ($dz * $dz));
		return min(self::PVPBOT_EXPERT_ARROW_MAX_LEAD_TICKS, max(1.0, $horizontal / max(0.1, $force * 0.9)));
	}

	private function normalizePVPBotArrowMotion(float $dx, float $dy, float $dz) : array{
		$length = sqrt(($dx * $dx) + ($dy * $dy) + ($dz * $dz));
		if($length <= 0.0001){
			$dx = -sin($this->yaw / 180 * M_PI) * cos($this->pitch / 180 * M_PI);
			$dy = -sin($this->pitch / 180 * M_PI);
			$dz = cos($this->yaw / 180 * M_PI) * cos($this->pitch / 180 * M_PI);
			$length = max(0.0001, sqrt(($dx * $dx) + ($dy * $dy) + ($dz * $dz)));
		}

		return [$dx / $length, $dy / $length, $dz / $length];
	}

	private function clampPVPBotFloat(float $value, float $min, float $max) : float{
		return max($min, min($max, $value));
	}

	protected function launchPVPBotArrowAt(Entity $target, CompoundTag $nbt, float $force) : bool{
		if($this->chunk === null or $this->level === null or $this->server === null){
			return false;
		}

		$arrow = Entity::createEntity("Arrow", $this->chunk, $nbt, $this, true);
		if(!($arrow instanceof Arrow)){
			return false;
		}
		$arrow->setArrowItem(ItemItem::get(ItemItem::ARROW, 0, 1));
		$this->applyPVPBotBowEnchantmentsToArrow($arrow);

		$this->server->getPluginManager()->callEvent($ev = new EntityShootBowEvent($this, $this->getPVPBotBowItem(), $arrow, $force));
		if($ev->isCancelled()){
			$ev->getProjectile()->kill();
			return false;
		}

		$projectile = $ev->getProjectile();
		if($projectile instanceof Projectile){
			$projectile->setMotion($projectile->getMotion()->multiply($ev->getForce()));
			$this->server->getPluginManager()->callEvent($projectileEv = new ProjectileLaunchEvent($projectile));
			if($projectileEv->isCancelled()){
				$projectile->kill();
				return false;
			}
			$projectile->spawnToAll();
			$this->level->addSound(new LaunchSound($this), $this->getViewers());
			return true;
		}

		$projectile->spawnToAll();
		return true;
	}

	private function applyPVPBotBowEnchantmentsToArrow(Arrow $arrow){
		$bow = $this->getPVPBotBowItem();
		$power = $bow->getEnchantmentLevel(Enchantment::TYPE_BOW_POWER);
		if($power > 0){
			$arrow->setBaseDamage($arrow->getBaseDamage() + 0.5 * $power + 0.5);
		}
		$punch = $bow->getEnchantmentLevel(Enchantment::TYPE_BOW_KNOCKBACK);
		if($punch > 0){
			$arrow->setKnockBack(0.4 + 0.5 * $punch);
		}
		if($bow->getEnchantmentLevel(Enchantment::TYPE_BOW_FLAME) > 0){
			$arrow->setOnFire(100);
		}
	}

	private function canPVPBotArrowReach(Entity $target) : bool{
		if($this->level === null or $target->level !== $this->level){
			return false;
		}
		if(!$this->hasLineOfSight($target)){
			return false;
		}

		$start = new Vector3($this->x, $this->y + $this->eyeHeight, $this->z);
		$end = new Vector3($target->x, $target->y + ($target->eyeHeight !== null ? $target->eyeHeight : ($target->height / 2)), $target->z);
		$dx = $end->x - $start->x;
		$dy = $end->y - $start->y;
		$dz = $end->z - $start->z;
		$steps = max(1, (int) ceil(max(abs($dx), abs($dy), abs($dz)) * 4));

		for($i = 1; $i < $steps; ++$i){
			$t = $i / $steps;
			$x = (int) floor($start->x + $dx * $t);
			$y = (int) floor($start->y + $dy * $t);
			$z = (int) floor($start->z + $dz * $t);
			$id = $this->level->getBlockIdAt($x, $y, $z);
			if($id !== Block::AIR){
				$data = method_exists($this->level, "getBlockDataAt") ? $this->level->getBlockDataAt($x, $y, $z) : 0;
				if(!Block::get($id, $data)->canPassThrough()){
					return false;
				}
			}
		}

		return true;
	}

	private function createPVPBotEnderPearlNBT(Entity $target){
		$startY = $this->y + $this->eyeHeight;
		$ambush = $this->resolvePVPBotEnderPearlAmbushPosition($target);
		if(!($ambush instanceof Vector3) or !$this->isPVPBotEnderPearlPathClear($ambush)){
			return null;
		}
		$dx = $ambush->x - $this->x;
		$dy = ($ambush->y + ($this->eyeHeight / 2)) - $startY;
		$dz = $ambush->z - $this->z;
		$length = sqrt(($dx * $dx) + ($dy * $dy) + ($dz * $dz));
		if($length <= 0.0001){
			$dx = -sin($this->yaw / 180 * M_PI) * cos($this->pitch / 180 * M_PI);
			$dy = -sin($this->pitch / 180 * M_PI);
			$dz = cos($this->yaw / 180 * M_PI) * cos($this->pitch / 180 * M_PI);
			$length = max(0.0001, sqrt(($dx * $dx) + ($dy * $dy) + ($dz * $dz)));
		}

		$motionX = $dx / $length;
		$motionY = $dy / $length;
		$motionZ = $dz / $length;
		$horizontal = sqrt(($motionX * $motionX) + ($motionZ * $motionZ));

		return new CompoundTag("", [
			"Pos" => new ListTag("Pos", [
				new DoubleTag("", $this->x),
				new DoubleTag("", $startY),
				new DoubleTag("", $this->z)
			]),
			"Motion" => new ListTag("Motion", [
				new DoubleTag("", $motionX),
				new DoubleTag("", $motionY),
				new DoubleTag("", $motionZ)
			]),
			"Rotation" => new ListTag("Rotation", [
				new FloatTag("", -atan2($motionX, $motionZ) * 180 / M_PI),
				new FloatTag("", -atan2($motionY, max(0.0001, $horizontal)) * 180 / M_PI)
			]),
			"EnderPearlSnowball" => new ByteTag("EnderPearlSnowball", 1),
			"PVPBotDirectAmbush" => new ByteTag("PVPBotDirectAmbush", 1),
			"PVPBotAmbushX" => new DoubleTag("PVPBotAmbushX", $ambush->x),
			"PVPBotAmbushY" => new DoubleTag("PVPBotAmbushY", $ambush->y),
			"PVPBotAmbushZ" => new DoubleTag("PVPBotAmbushZ", $ambush->z)
		]);
	}

	protected function resolvePVPBotEnderPearlAmbushPosition(Entity $target){
		if($this->level === null){
			return null;
		}

		$yaw = $target->yaw / 180 * M_PI;
		$forwardX = -sin($yaw);
		$forwardZ = cos($yaw);
		$rightX = $forwardZ;
		$rightZ = -$forwardX;
		$distances = [2.0, 1.5, 2.5, 3.0, 1.0];
		$sideOffsets = [0.0, 0.75, -0.75, 1.25, -1.25];

		foreach($distances as $distance){
			foreach($sideOffsets as $sideOffset){
				$x = $target->x - ($forwardX * $distance) + ($rightX * $sideOffset);
				$z = $target->z - ($forwardZ * $distance) + ($rightZ * $sideOffset);
				$standing = $this->resolvePVPBotEnderPearlAmbushStandingPosition($x, $z);
				if($standing instanceof Vector3 and $this->isPVPBotEnderPearlPathClear($standing)){
					return $standing;
				}
			}
		}

		return null;
	}

	private function resolvePVPBotEnderPearlAmbushStandingPosition(float $x, float $z){
		if($this->level === null){
			return null;
		}

		$blockX = (int) floor($x);
		$blockZ = (int) floor($z);
		try{
			$standingY = (int) $this->level->getHighestBlockAt($blockX, $blockZ) + 1;
		}catch(\Throwable $e){
			return null;
		}

		if($standingY < 1 or $standingY > 126){
			return null;
		}
		if(self::canPVPBotStandAt($this->level, $blockX, $standingY, $blockZ)){
			return new Vector3($blockX + 0.5, $standingY, $blockZ + 0.5);
		}

		return null;
	}

	private function isPVPBotEnderPearlAmbushPositionSafe(Vector3 $ambush) : bool{
		$standing = $this->resolvePVPBotEnderPearlAmbushStandingPosition($ambush->x, $ambush->z);
		return $standing instanceof Vector3
			and abs($standing->x - $ambush->x) <= 0.0001
			and abs($standing->y - $ambush->y) <= 0.0001
			and abs($standing->z - $ambush->z) <= 0.0001;
	}

	private function isPVPBotEnderPearlPathClear(Vector3 $ambush) : bool{
		if($this->level === null){
			return false;
		}

		$dx = $ambush->x - $this->x;
		$dy = $ambush->y - $this->y;
		$dz = $ambush->z - $this->z;
		$steps = max(1, (int) ceil(max(abs($dx), abs($dy), abs($dz)) * 8));
		$radius = max(0.05, ($this->width / 2) - 0.02);
		$horizontalOffsets = [[0.0, 0.0], [$radius, 0.0], [-$radius, 0.0], [0.0, $radius], [0.0, -$radius]];
		$verticalOffsets = [0.05, $this->height / 2, max(0.05, $this->height - 0.05)];

		for($i = 0; $i <= $steps; ++$i){
			$t = $i / $steps;
			$centerX = $this->x + ($dx * $t);
			$centerY = $this->y + ($dy * $t);
			$centerZ = $this->z + ($dz * $t);
			foreach($horizontalOffsets as $offset){
				foreach($verticalOffsets as $verticalOffset){
					if(self::isPVPBotPassableAt($this->level, (int) floor($centerX + $offset[0]), (int) floor($centerY + $verticalOffset), (int) floor($centerZ + $offset[1])) !== true){
						return false;
					}
				}
			}
		}

		return true;
	}

	protected function launchPVPBotEnderPearlAt(Entity $target, CompoundTag $nbt) : bool{
		if($this->hasPVPBotTypeSetting("noteleport")){
			return false;
		}
		if($this->chunk === null or $this->level === null or $this->server === null){
			return false;
		}

		$ambush = $this->getPVPBotEnderPearlAmbushPositionFromNBT($nbt);
		if(!($ambush instanceof Vector3) or !$this->isPVPBotEnderPearlAmbushPositionSafe($ambush) or !$this->isPVPBotEnderPearlPathClear($ambush)){
			return false;
		}

		$pearl = Entity::createEntity("Snowball", $this->chunk, $nbt, $this);
		if(!($pearl instanceof Projectile)){
			return false;
		}

		$pearl->setMotion($pearl->getMotion()->multiply(1.5));
		$this->server->getPluginManager()->callEvent($ev = new ProjectileLaunchEvent($pearl));
		if($ev->isCancelled()){
			$pearl->kill();
			return false;
		}

		if($this->level === null or !$this->isPVPBotEnderPearlAmbushPositionSafe($ambush)){
			$pearl->kill();
			return false;
		}
		if(!$this->isPVPBotEnderPearlPathClear($ambush)){
			$pearl->kill();
			return false;
		}

		$from = new Vector3($this->x, $this->y, $this->z);
		if(!$this->teleport($ambush)){
			$pearl->kill();
			return false;
		}

		$pearl->spawnToAll();
		$viewers = $this->getViewers();
		$this->level->addSound(new LaunchSound($from), $viewers);
		$this->level->addSound(new EndermanTeleportSound($from), $viewers);
		$this->level->addSound(new EndermanTeleportSound($ambush), $viewers);
		return true;
	}

	private function getPVPBotEnderPearlAmbushPositionFromNBT(CompoundTag $nbt){
		if(!isset($nbt->PVPBotDirectAmbush) or (int) $nbt["PVPBotDirectAmbush"] !== 1){
			return null;
		}
		if(!isset($nbt->PVPBotAmbushX) or !isset($nbt->PVPBotAmbushY) or !isset($nbt->PVPBotAmbushZ)){
			return null;
		}

		return new Vector3((float) $nbt["PVPBotAmbushX"], (float) $nbt["PVPBotAmbushY"], (float) $nbt["PVPBotAmbushZ"]);
	}

	private function finishPVPBotEnchantedGoldenAppleEating(){
		if($this->pvpBotEnchantedGoldenApples <= 0){
			$this->restorePVPBotPreEatingWeapon();
			return;
		}

		--$this->pvpBotEnchantedGoldenApples;
		if(isset($this->namedtag)){
			$this->namedtag->PVPBotEnchantedGoldenApples = new IntTag("PVPBotEnchantedGoldenApples", $this->pvpBotEnchantedGoldenApples);
		}

		$this->applyPVPBotEnchantedGoldenAppleEffects();
		$this->restorePVPBotPreEatingWeapon();
	}

	private function finishPVPBotNormalGoldenAppleEating(){
		$apple = $this->getPVPBotNormalGoldenAppleItem();
		if($apple instanceof ItemItem and $this->removePVPBotInventoryItem($apple, 1)){
			$this->applyPVPBotNormalGoldenAppleEffects();
		}

		$this->restorePVPBotPreEatingWeapon();
	}

	private function restorePVPBotPreEatingWeapon(){
		$this->weapon = $this->pvpBotPreEatingWeapon instanceof ItemItem ? $this->pvpBotPreEatingWeapon : $this->getDifficultyWeapon($this->difficulty);
		$this->pvpBotPreEatingWeapon = null;
		$this->broadcastPVPBotEquipment();
	}

	private function applyPVPBotEnchantedGoldenAppleEffects(){
		foreach($this->getPVPBotEnchantedGoldenAppleEffectIds() as $effectId){
			if($this->hasEffect($effectId)){
				$this->removeEffect($effectId);
			}
		}

		$this->addEffect(Effect::getEffect(Effect::HEALTH_BOOST)->setAmplifier(0)->setDuration(2 * 60 * 20));
		$this->addEffect(Effect::getEffect(Effect::REGENERATION)->setAmplifier(4)->setDuration(30 * 20));
		$this->addEffect(Effect::getEffect(Effect::FIRE_RESISTANCE)->setAmplifier(0)->setDuration(5 * 60 * 20));
		$this->addEffect(Effect::getEffect(Effect::DAMAGE_RESISTANCE)->setAmplifier(0)->setDuration(5 * 60 * 20));
		$this->addEffect(Effect::getEffect(Effect::ABSORPTION)->setDuration(2 * 60 * 20));
	}

	private function applyPVPBotNormalGoldenAppleEffects(){
		foreach([Effect::REGENERATION, Effect::ABSORPTION] as $effectId){
			if($this->hasEffect($effectId)){
				$this->removeEffect($effectId);
			}
		}

		$this->addEffect(Effect::getEffect(Effect::REGENERATION)->setAmplifier(1)->setDuration(100));
		$this->addEffect(Effect::getEffect(Effect::ABSORPTION)->setDuration(2400));
	}

	private function getPVPBotEnchantedGoldenAppleEffectIds() : array{
		return [
			Effect::HEALTH_BOOST,
			Effect::REGENERATION,
			Effect::FIRE_RESISTANCE,
			Effect::DAMAGE_RESISTANCE,
			Effect::ABSORPTION,
		];
	}

	private function broadcastPVPBotEntityEvent(int $event){
		$pk = new EntityEventPacket();
		$pk->eid = $this->getId();
		$pk->event = $event;
		foreach($this->hasSpawned as $player){
			$player->dataPacket(clone $pk);
		}
	}

	private function broadcastPVPBotEquipment(){
		foreach($this->hasSpawned as $player){
			$this->sendMobEquipment($player);
		}
	}

	private function broadcastPVPBotArmSwingAnimation(){
		$pk = new AnimatePacket();
		$pk->eid = $this->getId();
		$pk->action = PlayerAnimationEvent::ARM_SWING;
		foreach($this->hasSpawned as $player){
			$player->dataPacket(clone $pk);
		}
	}

	public function kill(){
		$wasAlive = $this->isAlive();
		parent::kill();
		if($wasAlive and !$this->isAlive()){
			$this->schedulePVPBotRespawn();
		}
	}

	private function schedulePVPBotRespawn(){
		if($this->pvpBotRespawnScheduled or $this->server === null or !method_exists($this->server, "getScheduler")){
			return;
		}
		$this->refreshPVPBotTypeConfiguration();
		if(empty($this->pvpBotRespawnConfig["enabled"]) or !is_array($this->pvpBotRespawnConfig["point"])){
			return;
		}

		$this->pvpBotRespawnScheduled = true;
		$delay = max(0, (int) $this->pvpBotRespawnConfig["delay"]);
		$this->server->getScheduler()->scheduleDelayedTask(new CallbackTask(function(CallbackTask $task){
			$this->pvpBotRespawnScheduled = false;
			$this->respawnPVPBotFromConfiguration();
		}), $delay * 20);
	}

	private function respawnPVPBotFromConfiguration() : bool{
		if($this->closed or $this->isAlive() or $this->server === null or !isset($this->namedtag->PVPBotType) or !method_exists($this->server, "getDataPath")){
			return false;
		}

		$this->refreshPVPBotTypeConfiguration();
		$name = (string) $this->namedtag["PVPBotType"];
		$manager = $this->pvpBotTypeManager instanceof BotTypeManager ? $this->pvpBotTypeManager : new BotTypeManager($this->server->getDataPath());
		$this->pvpBotTypeManager = $manager;
		$type = $manager->getType($name);
		if($type === null or strtolower($type["type"]) !== "pvpbot" or empty($type["respawn"]["enabled"]) or !isset($type["respawn"]["point"]) or !is_array($type["respawn"]["point"])){
			return false;
		}

		$point = $type["respawn"]["point"];
		$level = method_exists($this->server, "getLevelByName") ? $this->server->getLevelByName($point["world"]) : null;
		if(!($level instanceof \lycore\level\Level) and method_exists($this->server, "loadLevel")){
			$this->server->loadLevel($point["world"]);
			$level = method_exists($this->server, "getLevelByName") ? $this->server->getLevelByName($point["world"]) : null;
		}
		if(!($level instanceof \lycore\level\Level)){
			return false;
		}

		$safePosition = \lycore\command\defaults\BotCommand::resolvePVPBotSpawnPosition($level, new Vector3((int) $point["x"] + 0.5, (int) $point["y"] + 1, (int) $point["z"] + 0.5));
		$this->despawnFromAll();
		if(!$this->teleport($safePosition)){
			return false;
		}
		$this->deadTicks = 0;
		$this->setHealth($this->getMaxHealth());
		$this->applyPVPBotType($name, $type, $manager->getEquipment($name), $manager->loadSkinData($type["skin"]));
		$this->pvpBotTypeConfigRevision = $manager->getRevision();
		$this->scheduleUpdate();
		$this->spawnToAll();
		return true;
	}

	public function attack($damage, EntityDamageEvent $source){
		$healthBefore = $this->getHealth();
		$result = parent::attack($damage, $source);
		if($result !== true or $source->isCancelled() or $this->closed or !$this->isAlive()){
			return $result;
		}
		$this->applyPVPBotThornsDamage($source);
		$this->damagePVPBotArmorFromHit();

		$attacker = $this->getPVPBotCombatAttackerFromDamage($source);
		if($attacker instanceof Entity and $this->shouldQueuePVPBotEmergencyGoldenAppleAfterHit($healthBefore)){
			if($this->queuePVPBotEmergencyGoldenApple($attacker)){
				$this->tickPVPBotGoldenAppleCombat($attacker);
			}
		}

		return $result;
	}

	private function applyPVPBotThornsDamage(EntityDamageEvent $source){
		if(!($source instanceof EntityDamageByEntityEvent) or $source->getCause() === EntityDamageEvent::CAUSE_MAGIC){
			return;
		}

		$attacker = $source->getDamager();
		if(!($attacker instanceof Entity) or $attacker === $this or $attacker->closed or !$attacker->isAlive()){
			return;
		}

		$changed = false;
		foreach($this->armor as $slot => $item){
			$level = ($item instanceof ItemItem and $item->isArmor()) ? min(3, $item->getEnchantmentLevel(Enchantment::TYPE_ARMOR_THORNS)) : 0;
			if($level <= 0 or mt_rand(1, 100) > (15 * $level)){
				continue;
			}

			$damage = mt_rand(1, 4);
			$ev = new EntityDamageEvent($attacker, EntityDamageEvent::CAUSE_MAGIC, $damage);
			$attacker->attack($ev->getFinalDamage(), $ev);

			if(!$this->hasPVPBotTypeSetting("noconsume") and method_exists($item, "isUnbreakable") and !$item->isUnbreakable()){
				$item->setDamage($item->getDamage() + 3);
				if($item->getMaxDurability() !== false and $item->getDamage() >= $item->getMaxDurability()){
					$this->armor[$slot] = ItemItem::get(ItemItem::AIR, 0, 0);
				}else{
					$this->armor[$slot] = $item;
				}
				$changed = true;
			}

			break;
		}

		if($changed){
			$this->broadcastPVPBotEquipment();
		}
	}

	private function damagePVPBotArmorFromHit(){
		if($this->hasPVPBotTypeSetting("noconsume")){
			return;
		}
		$changed = false;
		foreach($this->armor as $slot => $item){
			if(!($item instanceof ItemItem) or !$item->isArmor()){
				continue;
			}
			$item->useOn($item);
			if($item->getCount() <= 0 or ($item->getMaxDurability() !== false and $item->getDamage() >= $item->getMaxDurability())){
				$this->armor[$slot] = ItemItem::get(ItemItem::AIR, 0, 0);
			}else{
				$this->armor[$slot] = $item;
			}
			$changed = true;
		}

		if($changed){
			foreach($this->hasSpawned as $player){
				$this->sendMobEquipment($player);
			}
		}
	}

	private function getPVPBotCombatAttackerFromDamage(EntityDamageEvent $source){
		if(!($source instanceof EntityDamageByEntityEvent)){
			return null;
		}

		$attacker = $source->getDamager();
		if($source instanceof EntityDamageByChildEntityEvent){
			$attacker = $source->getDamager();
		}

		if($attacker instanceof Player or ($attacker instanceof Entity and self::isPVPBotHostileMobThreat($attacker))){
			return $attacker;
		}

		return null;
	}

	private function shouldQueuePVPBotEmergencyGoldenAppleAfterHit(int $healthBefore) : bool{
		if($this->difficulty < 4 or $this->pvpBotEnchantedGoldenApples <= 0 or $this->isPVPBotEatingGoldenApple()){
			return false;
		}
		if($healthBefore <= 0 or $this->getHealth() <= 0){
			return false;
		}

		return $this->getHealth() <= self::PVPBOT_ENCHANTED_GOLDEN_APPLE_EMERGENCY_HEALTH_THRESHOLD;
	}

	public function tryUsePVPBotHealingPotion() : bool{
		if($this->hasPVPBotTypeSetting("nosplash")){
			return false;
		}
		if($this->pvpBotHealingPotions <= 0 or $this->pvpBotHealingCooldownTicks > 0){
			return false;
		}
		if(!$this->isAlive() or $this->getHealth() <= 0 or $this->getHealth() > self::PVPBOT_HEALING_HEALTH_THRESHOLD){
			return false;
		}
		if($this->getHealth() >= $this->getMaxHealth()){
			return false;
		}

		--$this->pvpBotHealingPotions;
		$this->pvpBotHealingCooldownTicks = self::PVPBOT_HEALING_COOLDOWN_TICKS;
		if(isset($this->namedtag)){
			$this->namedtag->PVPBotHealingPotions = new IntTag("PVPBotHealingPotions", $this->pvpBotHealingPotions);
		}

		$before = $this->getHealth();
		$this->setHealth($this->getMaxHealth());
		$this->spawnPVPBotHealingSplashEffect();

		return $this->getHealth() > $before;
	}

	private function spawnPVPBotHealingSplashEffect(){
		if($this->level === null){
			return;
		}

		$color = Potion::getColor(Potion::HEALING);
		$this->level->addParticle(new SpellParticle($this, $color[0], $color[1], $color[2]));
	}

	public function tryPVPBotMeleeAttack(Entity $target) : bool{
		if($this->isPVPBotEatingGoldenApple() or $this->isPVPBotMiningObstacle()){
			return false;
		}
		if($this->pvpBotMeleeCooldownTicks > 0){
			return false;
		}
		if($target->closed or !$target->isAlive()){
			return false;
		}

		$damage = $this->getHurt();
		$knockBack = 0.4;
		$weapon = $this->getWeapon();
		$damage += max(0, VanillaMobEquipment::getWeaponBaseDamage($weapon) - 1);
		$damage += Tool::getWeaponEnchantmentDamageBonus($this, $target);
		$knockBack = Tool::getWeaponKnockBackStrength($knockBack, $this);

		$source = new EntityDamageByEntityEvent($this, $target, EntityDamageEvent::CAUSE_ENTITY_ATTACK, $damage, $knockBack);
		if(!$this->canInteract($target, 8)){
			$source->setCancelled();
		}elseif($target instanceof Player){
			if(($target->getGamemode() & 0x01) > 0){
				return false;
			}elseif($this->server->getConfigBoolean("pvp") !== true or $this->server->getDifficulty() === 0){
				$source->setCancelled();
			}
		}

		$this->pvpBotMeleeCooldownTicks = self::PVPBOT_MELEE_COOLDOWN_TICKS;
		$this->broadcastPVPBotArmSwingAnimation();
		if($target->attack($source->getFinalDamage(), $source)){
			$source->useArmors();
			$this->damagePVPBotWeaponFromAttack($target);
			if(method_exists($this, "onSuccessfulMeleeAttack")){
				$this->onSuccessfulMeleeAttack($target, $source);
			}
			return true;
		}

		return false;
	}

	private function damagePVPBotWeaponFromAttack(Entity $target){
		if($this->hasPVPBotTypeSetting("noconsume")){
			return;
		}
		$item = $this->getWeapon();
		if(!$item->isTool()){
			return;
		}

		if($item->useOn($target) and $item->getDamage() >= $item->getMaxDurability()){
			$this->weapon = ItemItem::get(ItemItem::AIR, 0, 1);
		}else{
			$this->weapon = $item;
		}
		$this->broadcastPVPBotEquipment();
	}

	protected function tickPm1eGroundAi(int $tickDiff) : bool{
		$configUpdated = $this->refreshPVPBotTypeConfigurationIfDue($tickDiff);
		if(!$this->isBotWalkEnabled()){
			$this->motionX = 0.0;
			$this->motionZ = 0.0;
			$this->getBotMovementAI()->clear();
			return $configUpdated;
		}
		$areaCorrected = $this->enforceBotWalkArea();
		$this->tickPVPBotCombatTimers($tickDiff);
		$pickedUp = $this->collectNearbyPVPBotPickups() > 0;
		$this->tryUsePVPBotHealingPotion();
		$recovered = $this->recoverPVPBotStandingHeight($this->onGround and abs($this->motionY) <= 0.0001);
		$updated = parent::tickPm1eGroundAi($tickDiff) || $recovered;
		$this->updatePVPBotCombatLookTarget();
		return $updated || $pickedUp || $areaCorrected || $configUpdated;
	}

	private function refreshPVPBotTypeConfigurationIfDue(int $tickDiff) : bool{
		if(!isset($this->namedtag->PVPBotType)){
			return false;
		}
		$this->pvpBotTypeConfigReloadTicks -= $tickDiff;
		if($this->pvpBotTypeConfigReloadTicks > 0){
			return false;
		}
		$this->pvpBotTypeConfigReloadTicks = self::PVPBOT_TYPE_CONFIG_RELOAD_INTERVAL_TICKS;
		return $this->refreshPVPBotTypeConfiguration();
	}

	protected function recoverPVPBotStandingHeight(bool $allowDownwardSearch = true) : bool{
		if($this->level === null){
			return false;
		}

		$x = (int) floor($this->x);
		$z = (int) floor($this->z);
		$startY = max(1, min(126, (int) floor($this->y)));
		$feetPassable = self::isPVPBotPassableAt($this->level, $x, $startY, $z);
		$headPassable = self::isPVPBotPassableAt($this->level, $x, $startY + 1, $z);
		$groundPassable = self::isPVPBotPassableAt($this->level, $x, $startY - 1, $z);

		if($groundPassable === false and $feetPassable === true and $headPassable === true){
			if(!$allowDownwardSearch and abs($this->y - $startY) > 0.0001){
				return false;
			}
			if($this->onGround !== true){
				$this->onGround = true;
				return true;
			}
			return false;
		}

		$searchUpFirst = $feetPassable !== true or $headPassable !== true;
		if(!$searchUpFirst and !$allowDownwardSearch){
			return false;
		}
		$y = $searchUpFirst ? $this->findPVPBotStandingYUp($x, $startY, $z) : $this->findPVPBotStandingYDown($x, $startY, $z);
		if($y === null and ($searchUpFirst or $allowDownwardSearch)){
			$y = $searchUpFirst ? $this->findPVPBotStandingYDown($x, $startY, $z) : $this->findPVPBotStandingYUp($x, $startY, $z);
		}
		if($y === null){
			return false;
		}

		$changed = abs($this->y - $y) > 0.0001 or !$this->onGround;
		$this->x = $this->x;
		$this->y = (float) $y;
		$this->z = $this->z;
		$this->motionY = 0.0;
		$this->onGround = true;
		$halfWidth = $this->width / 2;
		$this->boundingBox->setBounds(
			$this->x - $halfWidth,
			$this->y,
			$this->z - $halfWidth,
			$this->x + $halfWidth,
			$this->y + $this->height,
			$this->z + $halfWidth
		);

		return $changed;
	}

	private function findPVPBotStandingYUp(int $x, int $startY, int $z){
		for($y = $startY; $y <= 126; ++$y){
			$standingY = $this->getPVPBotStandingYOnBlock($x, $y, $z);
			if($standingY !== null){
				return $standingY;
			}
		}

		return null;
	}

	private function findPVPBotStandingYDown(int $x, int $startY, int $z){
		for($y = $startY - 1; $y >= 0; --$y){
			$standingY = $this->getPVPBotStandingYOnBlock($x, $y, $z);
			if($standingY !== null){
				return $standingY;
			}
		}

		return null;
	}

	private function getPVPBotStandingYOnBlock(int $x, int $y, int $z){
		$block = self::getPVPBotBlockAt($this->level, $x, $y, $z);
		if($block === null or $block->canPassThrough()){
			return null;
		}

		$bb = $block->getBoundingBox();
		if($bb === null){
			return null;
		}

		$standingY = (float) $bb->maxY;
		return $this->canPVPBotOccupyAt($standingY) ? $standingY : null;
	}

	private function canPVPBotOccupyAt(float $standingY) : bool{
		if($this->level === null){
			return false;
		}

		$halfWidth = $this->width / 2;
		$bb = new AxisAlignedBB(
			$this->x - $halfWidth,
			$standingY,
			$this->z - $halfWidth,
			$this->x + $halfWidth,
			$standingY + $this->height,
			$this->z + $halfWidth
		);

		if(method_exists($this->level, "getCollisionCubes")){
			return count($this->level->getCollisionCubes($this, $bb, false)) === 0;
		}

		$x = (int) floor($this->x);
		$z = (int) floor($this->z);
		$minY = (int) floor($standingY + 0.001);
		$maxY = (int) floor($standingY + $this->height - 0.001);
		for($y = $minY; $y <= $maxY; ++$y){
			if(self::isPVPBotPassableAt($this->level, $x, $y, $z) !== true){
				return false;
			}
		}

		return true;
	}

	private static function canPVPBotStandAt($level, int $x, int $y, int $z) : bool{
		$groundPassable = self::isPVPBotPassableAt($level, $x, $y - 1, $z);
		$feetPassable = self::isPVPBotPassableAt($level, $x, $y, $z);
		$headPassable = self::isPVPBotPassableAt($level, $x, $y + 1, $z);

		return $groundPassable === false and $feetPassable === true and $headPassable === true;
	}

	private static function getPVPBotBlockAt($level, int $x, int $y, int $z){
		if($y < 0 or $y >= 128){
			return null;
		}

		try{
			if(method_exists($level, "getBlock")){
				return $level->getBlock(new Vector3($x, $y, $z));
			}

			$id = $level->getBlockIdAt($x, $y, $z);
			$data = method_exists($level, "getBlockDataAt") ? $level->getBlockDataAt($x, $y, $z) : 0;
			return Block::get($id, $data, new Position($x, $y, $z));
		}catch(\Throwable $e){
			return null;
		}
	}

	private static function isPVPBotPassableAt($level, int $x, int $y, int $z){
		if($y < 0 or $y >= 128){
			return false;
		}

		try{
			$id = $level->getBlockIdAt($x, $y, $z);
			$data = method_exists($level, "getBlockDataAt") ? $level->getBlockDataAt($x, $y, $z) : 0;
		}catch(\Throwable $e){
			return null;
		}

		return Block::get($id, $data)->canPassThrough();
	}

	public function getName(){
		$name = $this->getNameTag();
		return $name !== "" ? $name : self::DEFAULT_NAME;
	}

	public function isOp() : bool{
		return false;
	}

	public function isSurvival() : bool{
		return true;
	}

	public function isCreative() : bool{
		return false;
	}

	public function isSpectator() : bool{
		return false;
	}

	public function isAdventure() : bool{
		return false;
	}

	public function getGamemode() : int{
		return 0;
	}

	public function getProtocol(){
		return ProtocolInfo::CURRENT_PROTOCOL;
	}

	public function getNextPosition(){
		return $this->getPosition();
	}

	public function canInteract(Vector3 $pos, $maxDistance, $maxDiff = 0.5){
		if($this->distanceSquared($pos) > $maxDistance ** 2){
			return false;
		}

		$dV = $this->getDirectionPlane();
		$dot = $dV->dot(new Vector2($this->x, $this->z));
		$dot1 = $dV->dot(new Vector2($pos->x, $pos->z));
		return ($dot1 - $dot) >= -$maxDiff;
	}

	public function sendMessage($message){
	}

	public function getInventory(){
		if(!($this->pvpBotInventoryView instanceof BotInventoryView)){
			$this->pvpBotInventoryView = new BotInventoryView($this);
		}

		return $this->pvpBotInventoryView;
	}

	public function getSkinData(){
		return $this->skin;
	}

	public function getSkinName(){
		return $this->skinName;
	}

	public function getUniqueId(){
		if(!($this->uuid instanceof UUID)){
			$this->uuid = UUID::fromData($this->getId(), $this->getSkinData(), $this->getName());
		}

		return $this->uuid;
	}

	public function getRawUniqueId(){
		if($this->rawUUID === null){
			$this->rawUUID = $this->getUniqueId()->toBinary();
		}

		return $this->rawUUID;
	}

	public function getHurt(){
		return $this->difficulty;
	}

	public function getWeapon(){
		return $this->weapon instanceof ItemItem ? $this->weapon : ItemItem::get(ItemItem::AIR, 0, 0);
	}

	public function getPVPBotActionItem(){
		if($this->pvpBotActionItem instanceof ItemItem){
			return clone $this->pvpBotActionItem;
		}

		return $this->getWeapon();
	}

	private function setPVPBotActionItem(ItemItem $item = null){
		$this->pvpBotActionItem = $item instanceof ItemItem ? clone $item : null;
	}

	public function setPVPBotWeapon(ItemItem $item){
		$this->weapon = clone $item;
		$this->broadcastPVPBotEquipment();
	}

	public function setPVPBotArmorItem(int $slot, ItemItem $item){
		$armor = $this->getArmorContents();
		if($slot >= 0 and $slot < 4){
			$armor[$slot] = clone $item;
			$this->setMobEquipment($armor, $this->getWeapon());
			$this->broadcastPVPBotEquipment();
		}
	}

	public function getArmorContents(){
		return count($this->armor) === 4 ? $this->armor : VanillaMobEquipment::emptyArmor();
	}

	public function setMobEquipment(array $armor, ItemItem $weapon = null){
		$normalizedArmor = VanillaMobEquipment::emptyArmor();
		for($slot = 0; $slot < 4; ++$slot){
			if(isset($armor[$slot]) and $armor[$slot] instanceof ItemItem){
				$normalizedArmor[$slot] = $armor[$slot];
			}
		}

		$this->armor = $normalizedArmor;
		$this->weapon = $weapon instanceof ItemItem ? $weapon : ItemItem::get(ItemItem::AIR, 0, 0);
	}

	public function getPVPBotInventoryContents() : array{
		$items = [];
		foreach($this->pvpBotInventory as $item){
			$items[] = clone $item;
		}
		return $items;
	}

	public function setPVPBotInventoryContents(array $items){
		$this->pvpBotInventory = [];
		foreach($items as $item){
			if($item instanceof ItemItem and $item->getId() !== ItemItem::AIR and $item->getCount() > 0){
				$this->pvpBotInventory[] = clone $item;
			}
		}
	}

	public function tryPickupPVPBotItem(ItemItem $item) : bool{
		if($item->getId() === ItemItem::AIR or $item->getCount() <= 0){
			return false;
		}
		if(!$this->isPVPBotPickupAllowedItem($item)){
			return false;
		}

		$item = clone $item;
		$item->setCount(max(1, $item->getCount()));

		if(($slot = self::getArmorSlot($item)) >= 0){
			$this->pickupPVPBotArmor($slot, $item);
			return true;
		}
		if($item->getId() === ItemItem::ARROW){
			$this->pvpBotArrows += $item->getCount();
			return true;
		}
		if(self::isBow($item)){
			$this->pickupPVPBotBow($item);
			return true;
		}
		if(self::isPVPBotPickaxe($item)){
			$this->addPVPBotInventoryItem(self::makePVPBotPickaxeUnbreakable($item));
			return true;
		}
		if(self::getSwordScore($item) > 0){
			$this->pickupPVPBotWeapon($item);
			return true;
		}

		$this->addPVPBotInventoryItem($item);
		return true;
	}

	private function pickupPVPBotArmor(int $slot, ItemItem $item){
		$armor = $this->getArmorContents();
		$current = $armor[$slot];
		if(self::shouldReplaceArmor($current, $item)){
			if($current->getId() !== ItemItem::AIR and $current->getCount() > 0){
				$this->addPVPBotInventoryItem($current);
			}
			$equip = clone $item;
			$equip->setCount(1);
			$armor[$slot] = $equip;
			$this->setMobEquipment($armor, $this->getWeapon());
			$this->broadcastPVPBotEquipment();
			return;
		}

		$this->addPVPBotInventoryItem($item);
	}

	private function pickupPVPBotWeapon(ItemItem $item){
		if(self::getPVPBotWeaponScore($item) > self::getPVPBotWeaponScore($this->getWeapon())){
			$current = $this->getWeapon();
			if($current->getId() !== ItemItem::AIR and $current->getCount() > 0){
				$this->addPVPBotInventoryItem($current);
			}
			$equip = clone $item;
			$equip->setCount(1);
			$this->weapon = $equip;
			$this->broadcastPVPBotEquipment();
			return;
		}

		$this->addPVPBotInventoryItem($item);
	}

	private function pickupPVPBotBow(ItemItem $item){
		if(!($this->pvpBotBow instanceof ItemItem) or self::getPVPBotBowScore($item) > self::getPVPBotBowScore($this->pvpBotBow)){
			if($this->pvpBotBow instanceof ItemItem and $this->pvpBotBow->getId() !== ItemItem::AIR){
				$this->addPVPBotInventoryItem($this->pvpBotBow);
			}
			$bow = clone $item;
			$bow->setCount(1);
			$this->pvpBotBow = $bow;
			return;
		}

		$this->addPVPBotInventoryItem($item);
	}

	private function addPVPBotInventoryItem(ItemItem $item){
		if($item->getId() === ItemItem::AIR or $item->getCount() <= 0){
			return;
		}

		foreach($this->pvpBotInventory as $index => $stored){
			if($stored->deepEquals($item, true, true, false)){
				$stored->setCount($stored->getCount() + $item->getCount());
				$this->pvpBotInventory[$index] = $stored;
				return;
			}
		}

		$this->pvpBotInventory[] = clone $item;
	}

	private function addPVPBotInventoryStack(ItemItem $item){
		if($item->getId() === ItemItem::AIR or $item->getCount() <= 0){
			return;
		}

		$this->pvpBotInventory[] = clone $item;
	}

	private static function isPVPBotPickaxe(ItemItem $item) : bool{
		return $item->isPickaxe() !== false;
	}

	private static function makePVPBotPickaxeUnbreakable(ItemItem $item) : ItemItem{
		$item = clone $item;
		$tag = $item->getNamedTag();
		if(!($tag instanceof CompoundTag)){
			$tag = new CompoundTag("", []);
		}
		$tag->Unbreakable = new ByteTag("Unbreakable", 1);
		$item->setNamedTag($tag);
		return $item;
	}

	private function getBestPVPBotPickaxe(){
		$best = null;
		$bestTier = -1;
		foreach($this->pvpBotInventory as $item){
			if($item->getCount() <= 0){
				continue;
			}
			$tier = $item->isPickaxe();
			if($tier === false){
				continue;
			}
			if($tier > $bestTier){
				$best = self::makePVPBotPickaxeUnbreakable($item);
				$best->setCount(1);
				$bestTier = $tier;
			}
		}

		return $best;
	}

	public function tryMinePVPBotObstacleToward(Vector3 $target) : bool{
		if($this->hasPVPBotTypeSetting("nobreak")){
			return false;
		}
		if($this->level === null){
			return false;
		}

		$pickaxe = $this->getBestPVPBotPickaxe();
		if(!($pickaxe instanceof ItemItem)){
			return false;
		}

		$currentX = (int) floor($this->x);
		$currentZ = (int) floor($this->z);
		$targetX = (int) floor($target->x);
		$targetZ = (int) floor($target->z);
		$stepX = $targetX > $currentX ? 1 : ($targetX < $currentX ? -1 : 0);
		$stepZ = $targetZ > $currentZ ? 1 : ($targetZ < $currentZ ? -1 : 0);
		if($stepX === 0 and $stepZ === 0){
			return false;
		}

		$nextX = $currentX + $stepX;
		$nextZ = $currentZ + $stepZ;
		$feetY = (int) floor($this->y);
		foreach([$feetY, $feetY + 1] as $blockY){
			$block = self::getPVPBotBlockAt($this->level, $nextX, $blockY, $nextZ);
			if($block === null or $block->getId() === Block::AIR or $block->canPassThrough()){
				continue;
			}
			if(!$block->isBreakable($pickaxe)){
				continue;
			}

			if($this->pvpBotMiningActionTicks > 0){
				$this->maintainPVPBotMiningEquipment();
				return true;
			}

			$this->startPVPBotMiningAction($pickaxe);
			$this->tryBreakPVPBotBlock($block, $pickaxe, true);
			return true;
		}

		return false;
	}

	private function startPVPBotMiningAction(ItemItem $pickaxe){
		if(!($this->pvpBotPreMiningWeapon instanceof ItemItem)){
			$this->pvpBotPreMiningWeapon = clone $this->getWeapon();
		}
		$this->pvpBotMiningPickaxe = clone $pickaxe;
		$this->weapon = clone $pickaxe;
		$this->pvpBotMiningActionTicks = self::PVPBOT_MINING_ACTION_TICKS;
		$this->broadcastPVPBotEquipment();
		$this->setDataFlag(self::DATA_FLAGS, self::DATA_FLAG_ACTION, true);
		$this->broadcastPVPBotArmSwingAnimation();
		$this->setDataFlag(self::DATA_FLAGS, self::DATA_FLAG_ACTION, false);
	}

	public function isPVPBotMiningObstacle() : bool{
		return $this->pvpBotMiningActionTicks > 0;
	}

	private function maintainPVPBotMiningEquipment(){
		if(!($this->pvpBotMiningPickaxe instanceof ItemItem)){
			return;
		}
		if($this->getWeapon()->deepEquals($this->pvpBotMiningPickaxe, true, true, false)){
			return;
		}

		$this->weapon = clone $this->pvpBotMiningPickaxe;
		$this->broadcastPVPBotEquipment();
	}

	private function finishPVPBotMiningAction(){
		$this->pvpBotMiningActionTicks = 0;
		if($this->pvpBotPreMiningWeapon instanceof ItemItem){
			$this->weapon = $this->pvpBotPreMiningWeapon;
			$this->broadcastPVPBotEquipment();
		}
		$this->pvpBotPreMiningWeapon = null;
		$this->pvpBotMiningPickaxe = null;
	}

	private function cancelPVPBotMiningAction(){
		if(!$this->isPVPBotMiningObstacle() and !($this->pvpBotPreMiningWeapon instanceof ItemItem)){
			return;
		}

		$this->finishPVPBotMiningAction();
	}

	public function tryBreakPVPBotBlock(Vector3 $target, ItemItem $item = null, bool $createParticles = true) : bool{
		if($this->level === null or !method_exists($this->level, "useBreakOn")){
			return false;
		}

		$usedWeapon = $item === null;
		if($item === null){
			$item = $this->getWeapon();
		}
		$block = $this->level->getBlock($target);
		$this->lastBreak = microtime(true) - max(0.2, $block->getBreakTime($item));

		$this->setPVPBotActionItem($item);
		try{
			$broken = $this->level->useBreakOn($target, $item, $this, $createParticles);
		}finally{
			$this->setPVPBotActionItem(null);
		}

		if($broken and $usedWeapon){
			$this->weapon = $item instanceof ItemItem ? $item : ItemItem::get(ItemItem::AIR, 0, 0);
			$this->broadcastPVPBotEquipment();
		}

		return (bool) $broken;
	}

	public function tryPlacePVPBotBridgeBlockToward(Vector3 $target) : bool{
		if($this->hasPVPBotTypeSetting("noplace")){
			return false;
		}
		if($this->level === null or !method_exists($this->level, "useItemOn")){
			return false;
		}

		$dx = $target->x - $this->x;
		$dz = $target->z - $this->z;
		if(abs($dx) < 0.0001 and abs($dz) < 0.0001){
			return false;
		}

		$stepX = abs($dx) >= abs($dz) ? ($dx > 0 ? 1 : -1) : 0;
		$stepZ = abs($dz) > abs($dx) ? ($dz > 0 ? 1 : -1) : 0;
		$x = (int) floor($this->x) + $stepX;
		$z = (int) floor($this->z) + $stepZ;
		$feetY = $this->getPVPBotBridgeFeetY();
		$placeY = null;
		$placementTarget = null;
		foreach($this->getPVPBotBridgePlacementCandidates($feetY, $target) as $candidateY){
			if($this->canPlacePVPBotBridgeBlockAt($x, $candidateY, $z) and ($candidateTarget = $this->findPVPBotBridgePlacementTarget($x, $candidateY, $z, $stepX, $stepZ)) !== null){
				$placeY = $candidateY;
				$placementTarget = $candidateTarget;
				break;
			}
		}
		if($placeY === null or $placementTarget === null){
			return false;
		}

		$sourceItem = $this->getPVPBotBridgeBlockItem();
		$item = $sourceItem instanceof ItemItem ? clone $sourceItem : null;
		if(!($item instanceof ItemItem)){
			return false;
		}
		$item->setCount(1);

		$block = $item->getBlock();
		if($block->getId() === Block::AIR){
			return false;
		}

		$this->setPVPBotActionItem($item);
		try{
			$placed = $this->level->useItemOn($placementTarget["target"], $item, $placementTarget["face"], 0.5, 0.5, 0.5, $this);
		}finally{
			$this->setPVPBotActionItem(null);
		}
		if($placed){
			$this->removePVPBotInventoryItem($sourceItem, 1);
			$this->setDataFlag(self::DATA_FLAGS, self::DATA_FLAG_ACTION, true);
			$this->broadcastPVPBotArmSwingAnimation();
			$this->setDataFlag(self::DATA_FLAGS, self::DATA_FLAG_ACTION, false);
		}

		return (bool) $placed;
	}

	private function findPVPBotBridgePlacementTarget(int $x, int $placeY, int $z, int $stepX, int $stepZ){
		$faces = [];
		if($stepX > 0){
			$faces[] = Vector3::SIDE_EAST;
		}elseif($stepX < 0){
			$faces[] = Vector3::SIDE_WEST;
		}
		if($stepZ > 0){
			$faces[] = Vector3::SIDE_SOUTH;
		}elseif($stepZ < 0){
			$faces[] = Vector3::SIDE_NORTH;
		}
		$faces = array_values(array_unique(array_merge($faces, [
			Vector3::SIDE_UP,
			Vector3::SIDE_NORTH,
			Vector3::SIDE_SOUTH,
			Vector3::SIDE_WEST,
			Vector3::SIDE_EAST,
			Vector3::SIDE_DOWN,
		])));

		$place = new Vector3($x, $placeY, $z);
		foreach($faces as $face){
			$targetPos = $place->getSide(Vector3::getOppositeSide($face));
			$target = self::getPVPBotBlockAt($this->level, (int) $targetPos->x, (int) $targetPos->y, (int) $targetPos->z);
			if($target !== null and $target->getId() !== Block::AIR){
				return [
					"target" => $target,
					"face" => $face,
				];
			}
		}

		return null;
	}

	private function getPVPBotBridgeFeetY() : int{
		$feetY = (int) floor($this->y);
		if(!$this->onGround and $this->motionY <= 0.0){
			$ceilY = (int) ceil($this->y);
			if($ceilY > $feetY and ($ceilY - $this->y) <= 0.35){
				return $ceilY;
			}
		}

		return $feetY;
	}

	private function getPVPBotBridgePlacementCandidates(int $feetY, Vector3 $target) : array{
		$candidates = [$feetY - 1];
		if($target->y > $this->y + 0.5){
			$candidates[] = $feetY;
		}

		return array_values(array_unique($candidates));
	}

	private function canPlacePVPBotBridgeBlockAt(int $x, int $placeY, int $z) : bool{
		$standingY = $placeY + 1;
		if(self::isPVPBotPassableAt($this->level, $x, $placeY, $z) !== true){
			return false;
		}
		if(self::isPVPBotPassableAt($this->level, $x, $standingY, $z) !== true){
			return false;
		}
		if(self::isPVPBotPassableAt($this->level, $x, $standingY + 1, $z) !== true){
			return false;
		}

		return true;
	}

	private function getPVPBotBridgeBlockItem(){
		foreach($this->pvpBotInventory as $item){
			if(!$item->canBePlaced() or $item->getCount() <= 0){
				continue;
			}
			$block = $item->getBlock();
			if($block->getId() === Block::AIR){
				continue;
			}

			$taken = clone $item;
			$taken->setCount(1);
			return $taken;
		}

		return null;
	}

	private static function isPVPBotNormalGoldenApple(ItemItem $item) : bool{
		return $item->getId() === ItemItem::GOLDEN_APPLE and $item->getDamage() === 0;
	}

	private function getPVPBotNormalGoldenAppleItem(){
		foreach($this->pvpBotInventory as $item){
			if(!self::isPVPBotNormalGoldenApple($item) or $item->getCount() <= 0){
				continue;
			}

			$apple = clone $item;
			$apple->setCount(1);
			return $apple;
		}

		return null;
	}

	private function removePVPBotInventoryItem(ItemItem $match, int $count) : bool{
		if($count <= 0){
			return true;
		}

		foreach($this->pvpBotInventory as $index => $item){
			if(!$item->deepEquals($match, true, true, false)){
				continue;
			}

			$item->setCount($item->getCount() - $count);
			if($item->getCount() <= 0){
				unset($this->pvpBotInventory[$index]);
				$this->pvpBotInventory = array_values($this->pvpBotInventory);
			}else{
				$this->pvpBotInventory[$index] = $item;
			}
			return true;
		}

		return false;
	}

	public static function isPVPBotPickupAllowedItem(ItemItem $item) : bool{
		if(self::getArmorSlot($item) >= 0){
			return true;
		}
		if(self::isPVPBotNormalGoldenApple($item)){
			return true;
		}
		if(self::getSwordScore($item) > 0 or self::isBow($item) or self::isPVPBotPickaxe($item) or $item->getId() === ItemItem::ARROW){
			return true;
		}
		if($item->canBePlaced()){
			$block = $item->getBlock();
			return $block->getId() !== Block::AIR;
		}

		return false;
	}

	protected function collectNearbyPVPBotPickups() : int{
		if($this->hasPVPBotTypeSetting("nopickup")){
			return 0;
		}
		if($this->level === null or !($this->boundingBox instanceof AxisAlignedBB) or !method_exists($this->level, "getNearbyEntities")){
			return 0;
		}

		$pickedUp = 0;
		$bb = $this->boundingBox->grow(self::PVPBOT_PICKUP_RADIUS, 0.5, self::PVPBOT_PICKUP_RADIUS);
		foreach($this->level->getNearbyEntities($bb, $this) as $entity){
			if(!($entity instanceof Item) or $entity->closed or $entity->getPickupDelay() > 0){
				continue;
			}
			if(!$this->tryPickupPVPBotItem($entity->getItem())){
				continue;
			}

			$entity->close();
			++$pickedUp;
		}

		return $pickedUp;
	}

	private function getDifficultyWeapon(int $difficulty) : ItemItem{
		switch($difficulty){
			case 6:
			case 5:
				$sword = ItemItem::get(ItemItem::DIAMOND_SWORD, 0, 1);
				$sword->addEnchantment(Enchantment::getEnchantment(Enchantment::TYPE_WEAPON_SHARPNESS)->setLevel(5));
				$sword->addEnchantment(Enchantment::getEnchantment(Enchantment::TYPE_WEAPON_KNOCKBACK)->setLevel(2));
				$sword->addEnchantment(Enchantment::getEnchantment(Enchantment::TYPE_WEAPON_FIRE_ASPECT)->setLevel(2));
				return $sword;
			case 4:
			case 3:
				return ItemItem::get(ItemItem::DIAMOND_SWORD, 0, 1);
			case 2:
				return ItemItem::get(ItemItem::IRON_SWORD, 0, 1);
			default:
				return ItemItem::get(ItemItem::STONE_SWORD, 0, 1);
		}
	}

	private function getDifficultyArmor(int $difficulty) : array{
		switch($difficulty){
			case 6:
			case 5:
				return [
					$this->createPVPBotEliteArmorItem(ItemItem::DIAMOND_HELMET),
					$this->createPVPBotEliteArmorItem(ItemItem::DIAMOND_CHESTPLATE),
					$this->createPVPBotEliteArmorItem(ItemItem::DIAMOND_LEGGINGS),
					$this->createPVPBotEliteArmorItem(ItemItem::DIAMOND_BOOTS),
				];
			case 4:
			case 3:
				return [
					ItemItem::get(ItemItem::DIAMOND_HELMET, 0, 1),
					ItemItem::get(ItemItem::DIAMOND_CHESTPLATE, 0, 1),
					ItemItem::get(ItemItem::DIAMOND_LEGGINGS, 0, 1),
					ItemItem::get(ItemItem::DIAMOND_BOOTS, 0, 1),
				];
			case 2:
				return [
					ItemItem::get(ItemItem::IRON_HELMET, 0, 1),
					ItemItem::get(ItemItem::IRON_CHESTPLATE, 0, 1),
					ItemItem::get(ItemItem::IRON_LEGGINGS, 0, 1),
					ItemItem::get(ItemItem::IRON_BOOTS, 0, 1),
				];
			default:
				return [
					ItemItem::get(ItemItem::LEATHER_CAP, 0, 1),
					ItemItem::get(ItemItem::LEATHER_TUNIC, 0, 1),
					ItemItem::get(ItemItem::LEATHER_PANTS, 0, 1),
					ItemItem::get(ItemItem::LEATHER_BOOTS, 0, 1),
				];
		}
	}

	private function createPVPBotEliteArmorItem(int $itemId) : ItemItem{
		$item = ItemItem::get($itemId, 0, 1);
		$item->addEnchantment(Enchantment::getEnchantment(Enchantment::TYPE_ARMOR_PROTECTION)->setLevel(4));
		if(mt_rand(1, 100) <= self::PVPBOT_ELITE_THORNS_CHANCE){
			$item->addEnchantment(Enchantment::getEnchantment(Enchantment::TYPE_ARMOR_THORNS)->setLevel(3));
		}
		return $item;
	}

	private function getDifficultyBow(int $difficulty){
		if($difficulty < 3){
			return null;
		}

		$bow = ItemItem::get(ItemItem::BOW, 0, 1);
		if($difficulty >= 5){
			$bow->addEnchantment(Enchantment::getEnchantment(Enchantment::TYPE_BOW_POWER)->setLevel(5));
			$bow->addEnchantment(Enchantment::getEnchantment(Enchantment::TYPE_BOW_KNOCKBACK)->setLevel(2));
			$bow->addEnchantment(Enchantment::getEnchantment(Enchantment::TYPE_BOW_FLAME)->setLevel(1));
		}
		return $bow;
	}

	public static function getArmorSlot(ItemItem $item) : int{
		switch($item->getId()){
			case ItemItem::LEATHER_CAP:
			case ItemItem::CHAIN_HELMET:
			case ItemItem::IRON_HELMET:
			case ItemItem::DIAMOND_HELMET:
			case ItemItem::GOLD_HELMET:
				return 0;
			case ItemItem::LEATHER_TUNIC:
			case ItemItem::CHAIN_CHESTPLATE:
			case ItemItem::IRON_CHESTPLATE:
			case ItemItem::DIAMOND_CHESTPLATE:
			case ItemItem::GOLD_CHESTPLATE:
				return 1;
			case ItemItem::LEATHER_PANTS:
			case ItemItem::CHAIN_LEGGINGS:
			case ItemItem::IRON_LEGGINGS:
			case ItemItem::DIAMOND_LEGGINGS:
			case ItemItem::GOLD_LEGGINGS:
				return 2;
			case ItemItem::LEATHER_BOOTS:
			case ItemItem::CHAIN_BOOTS:
			case ItemItem::IRON_BOOTS:
			case ItemItem::DIAMOND_BOOTS:
			case ItemItem::GOLD_BOOTS:
				return 3;
		}

		return -1;
	}

	public static function getArmorTier(ItemItem $item) : int{
		switch($item->getId()){
			case ItemItem::LEATHER_CAP:
			case ItemItem::LEATHER_TUNIC:
			case ItemItem::LEATHER_PANTS:
			case ItemItem::LEATHER_BOOTS:
				return 1;
			case ItemItem::GOLD_HELMET:
			case ItemItem::GOLD_CHESTPLATE:
			case ItemItem::GOLD_LEGGINGS:
			case ItemItem::GOLD_BOOTS:
				return 2;
			case ItemItem::CHAIN_HELMET:
			case ItemItem::CHAIN_CHESTPLATE:
			case ItemItem::CHAIN_LEGGINGS:
			case ItemItem::CHAIN_BOOTS:
				return 3;
			case ItemItem::IRON_HELMET:
			case ItemItem::IRON_CHESTPLATE:
			case ItemItem::IRON_LEGGINGS:
			case ItemItem::IRON_BOOTS:
				return 4;
			case ItemItem::DIAMOND_HELMET:
			case ItemItem::DIAMOND_CHESTPLATE:
			case ItemItem::DIAMOND_LEGGINGS:
			case ItemItem::DIAMOND_BOOTS:
				return 5;
		}

		return 0;
	}

	public static function shouldReplaceArmor(ItemItem $current, ItemItem $candidate) : bool{
		$candidateSlot = self::getArmorSlot($candidate);
		if($candidateSlot < 0){
			return false;
		}
		if($current->getId() === ItemItem::AIR){
			return true;
		}

		return self::getArmorSlot($current) === $candidateSlot and self::getArmorScore($candidate) > self::getArmorScore($current);
	}

	public static function getArmorScore(ItemItem $item) : int{
		if(self::getArmorSlot($item) < 0 or !$item->isArmor()){
			return 0;
		}

		$score = ((int) $item->getArmorValue()) * 100;
		$score += self::getArmorTier($item) * 10;
		$score += $item->getEnchantmentLevel(Enchantment::TYPE_ARMOR_PROTECTION) * 8;
		$score += $item->getEnchantmentLevel(Enchantment::TYPE_ARMOR_FIRE_PROTECTION) * 4;
		$score += $item->getEnchantmentLevel(Enchantment::TYPE_ARMOR_EXPLOSION_PROTECTION) * 4;
		$score += $item->getEnchantmentLevel(Enchantment::TYPE_ARMOR_PROJECTILE_PROTECTION) * 4;
		$score += $item->getEnchantmentLevel(Enchantment::TYPE_ARMOR_FALL_PROTECTION) * 3;
		$score += $item->getEnchantmentLevel(Enchantment::TYPE_ARMOR_THORNS) * 3;
		$score += $item->getEnchantmentLevel(Enchantment::TYPE_MINING_DURABILITY) * 2;

		$max = $item->getMaxDurability();
		if(is_int($max) and $max > 0){
			$score += (int) max(0, min(10, (($max - $item->getDamage()) / $max) * 10));
		}

		return $score;
	}

	public static function getSwordScore(ItemItem $item) : int{
		switch($item->getId()){
			case ItemItem::WOODEN_SWORD:
			case ItemItem::GOLD_SWORD:
				return 4;
			case ItemItem::STONE_SWORD:
				return 5;
			case ItemItem::IRON_SWORD:
				return 6;
			case ItemItem::DIAMOND_SWORD:
				return 7;
		}

		return 0;
	}

	public static function isBow(ItemItem $item) : bool{
		return $item->getId() === ItemItem::BOW;
	}

	public static function getPVPBotWeaponScore(ItemItem $item) : int{
		if(self::getSwordScore($item) <= 0){
			return 0;
		}

		$score = self::getSwordScore($item) * 100;
		$score += $item->getEnchantmentLevel(Enchantment::TYPE_WEAPON_SHARPNESS) * 20;
		$score += $item->getEnchantmentLevel(Enchantment::TYPE_WEAPON_SMITE) * 10;
		$score += $item->getEnchantmentLevel(Enchantment::TYPE_WEAPON_ARTHROPODS) * 10;
		$score += $item->getEnchantmentLevel(Enchantment::TYPE_WEAPON_KNOCKBACK) * 6;
		$score += $item->getEnchantmentLevel(Enchantment::TYPE_WEAPON_FIRE_ASPECT) * 12;
		$score += $item->getEnchantmentLevel(Enchantment::TYPE_MINING_DURABILITY) * 2;

		return $score;
	}

	public static function getPVPBotBowScore(ItemItem $item) : int{
		if(!self::isBow($item)){
			return 0;
		}

		return 100 +
			$item->getEnchantmentLevel(Enchantment::TYPE_BOW_POWER) * 20 +
			$item->getEnchantmentLevel(Enchantment::TYPE_BOW_KNOCKBACK) * 8 +
			$item->getEnchantmentLevel(Enchantment::TYPE_BOW_FLAME) * 12 +
			$item->getEnchantmentLevel(Enchantment::TYPE_BOW_INFINITY) * 10 +
			$item->getEnchantmentLevel(Enchantment::TYPE_MINING_DURABILITY) * 2;
	}

	public static function findGridRoute(array $start, array $target, callable $resolveY, int $maxNodes = 160, int $range = 16) : array{
		return BotMovementAI::findGridRoute($start, $target, $resolveY, $maxNodes, $range);
	}

	public function hasLineOfSight(Entity $entity){
		if($this->level === null or $entity->level !== $this->level){
			return false;
		}

		$start = new Vector3($this->x, $this->y + $this->eyeHeight, $this->z);
		$end = new Vector3($entity->x, $entity->y + $entity->eyeHeight, $entity->z);
		$dx = $end->x - $start->x;
		$dy = $end->y - $start->y;
		$dz = $end->z - $start->z;
		$steps = max(1, (int) ceil(max(abs($dx), abs($dy), abs($dz)) * 4));

		for($i = 1; $i < $steps; ++$i){
			$t = $i / $steps;
			$x = (int) floor($start->x + $dx * $t);
			$y = (int) floor($start->y + $dy * $t);
			$z = (int) floor($start->z + $dz * $t);
			$id = $this->level->getBlockIdAt($x, $y, $z);
			if($id !== Block::AIR){
				$data = method_exists($this->level, "getBlockDataAt") ? $this->level->getBlockDataAt($x, $y, $z) : 0;
				if(!Block::get($id, $data)->canPassThrough()){
					return false;
				}
			}
		}

		return true;
	}

	public function getDrops(){
		if($this->hasPVPBotTypeSetting("nodrops")){
			return [];
		}
		return [];
	}

	protected function updateMovement(){
		$diffPosition = ($this->x - $this->lastX) ** 2 + ($this->y - $this->lastY) ** 2 + ($this->z - $this->lastZ) ** 2;
		$diffRotation = ($this->yaw - $this->lastYaw) ** 2 + ($this->pitch - $this->lastPitch) ** 2;
		$diffMotion = ($this->motionX - $this->lastMotionX) ** 2 + ($this->motionY - $this->lastMotionY) ** 2 + ($this->motionZ - $this->lastMotionZ) ** 2;

		if($diffPosition > 0.04 or ($diffRotation > 2.25 and ($diffMotion > 0.0001 and $this->getMotion()->lengthSquared() <= 0.00001))){
			$pk = new MovePlayerPacket();
			$pk->eid = $this->getId();
			$pk->x = $this->x;
			$pk->y = $this->y + $this->eyeHeight;
			$pk->z = $this->z;
			$pk->yaw = $this->yaw;
			$pk->bodyYaw = $this->yaw;
			$pk->pitch = $this->pitch;
			$pk->mode = MovePlayerPacket::MODE_NORMAL;
			$pk->onGround = $this->onGround;
			foreach($this->hasSpawned as $player){
				$player->dataPacket(clone $pk);
			}

			$this->lastX = $this->x;
			$this->lastY = $this->y;
			$this->lastZ = $this->z;
			$this->lastYaw = $this->yaw;
			$this->lastPitch = $this->pitch;
		}

		if($diffMotion > 0.0025 or ($diffMotion > 0.0001 and $this->getMotion()->lengthSquared() <= 0.0001)){
			$this->lastMotionX = $this->motionX;
			$this->lastMotionY = $this->motionY;
			$this->lastMotionZ = $this->motionZ;
			if($this->level !== null and $this->chunk !== null){
				$this->level->addEntityMotion($this->chunk->getX(), $this->chunk->getZ(), $this->id, $this->motionX, $this->motionY, $this->motionZ);
			}
		}
	}

	public function close(){
		if($this->level === null or $this->server === null){
			$this->closed = true;
			$this->hasSpawned = [];
			$this->attributeMap = null;
			return;
		}

		parent::close();
	}

	public function spawnTo(Player $player){
		if(!$this->server->isBotEnabled()){
			$this->despawnFrom($player);
			return;
		}
		if($player !== $this and !isset($this->hasSpawned[$player->getLoaderId()])){
			$this->hasSpawned[$player->getLoaderId()] = $player;

			$pk = new AddPlayerPacket();
			$pk->uuid = $this->getUniqueId();
			$pk->username = $this->getPVPBotManagementDisplayName($player);
			$pk->eid = $this->getId();
			$pk->x = $this->x;
			$pk->y = $this->y;
			$pk->z = $this->z;
			$pk->speedX = $this->motionX;
			$pk->speedY = $this->motionY;
			$pk->speedZ = $this->motionZ;
			$pk->yaw = $this->yaw;
			$pk->pitch = $this->pitch;
			$pk->item = $this->getWeapon();
			$pk->metadata = $this->dataProperties;
			$pk->metadata[self::DATA_NAMETAG] = [self::DATA_TYPE_STRING, $this->getPVPBotManagementDisplayName($player)];
			$player->dataPacket($pk);

			$this->sendMobEquipment($player);
		}
	}

	public function despawnFrom(Player $player){
		if(isset($this->hasSpawned[$player->getLoaderId()])){
			$pk = new RemovePlayerPacket();
			$pk->eid = $this->getId();
			$pk->clientId = $this->getUniqueId();
			$player->dataPacket($pk);
			unset($this->hasSpawned[$player->getLoaderId()]);
		}
	}

	protected function sendMobEquipment(Player $player){
		$pk = new MobEquipmentPacket();
		$pk->eid = $this->getId();
		$pk->item = $this->getWeapon();
		$pk->slot = 0;
		$pk->selectedSlot = 0;
		$player->dataPacket($pk);

		$pk = new MobArmorEquipmentPacket();
		$pk->eid = $this->getId();
		$pk->slots = $this->getArmorContents();
		$player->dataPacket($pk);
	}
}

class BotInventoryView{
	/** @var Bot */
	private $bot;

	public function __construct(Bot $bot){
		$this->bot = $bot;
	}

	public function getItemInHand(){
		return $this->bot->getPVPBotActionItem();
	}

	public function setItemInHand(ItemItem $item){
		$this->bot->setPVPBotWeapon($item);
		return true;
	}

	public function getHelmet(){
		$armor = $this->bot->getArmorContents();
		return isset($armor[0]) ? $armor[0] : ItemItem::get(ItemItem::AIR, 0, 0);
	}

	public function getArmorContents(){
		return $this->bot->getArmorContents();
	}

	public function setArmorItem($index, ItemItem $item){
		$this->bot->setPVPBotArmorItem((int) $index, $item);
		return true;
	}

	public function sendContents($target = null){
	}
}
