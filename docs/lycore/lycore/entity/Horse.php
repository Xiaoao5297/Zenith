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

namespace lycore\entity;

use lycore\inventory\HorseInventory;
use lycore\inventory\InventoryHolder;
use lycore\item\Item as ItemItem;
use lycore\item\Lead;
use lycore\level\format\FullChunk;
use lycore\level\Position;
use lycore\nbt\NBT;
use lycore\nbt\tag\ByteTag;
use lycore\nbt\tag\CompoundTag;
use lycore\nbt\tag\IntTag;
use lycore\network\protocol\AddEntityPacket;
use lycore\network\protocol\MoveEntityPacket;
use lycore\network\protocol\MovePlayerPacket;
use lycore\network\protocol\ProtocolCompatibility;
use lycore\network\protocol\SetEntityLinkPacket;
use lycore\Player;
use lycore\scheduler\CallbackTask;

class Horse extends Animal implements InventoryHolder, Rideable{
	const NETWORK_ID = 23;

	const VARIANT_WHITE = 0;
	const VARIANT_CREAMY = 1;
	const VARIANT_CHESTNUT = 2;
	const VARIANT_BROWN = 3;
	const VARIANT_BLACK = 4;
	const VARIANT_GRAY = 5;
	const VARIANT_DARK_BROWN = 6;

	const MARK_NONE = 0;
	const MARK_WHITE = 1;
	const MARK_WHITE_FIELD = 2;
	const MARK_WHITE_DOTS = 3;
	const MARK_BLACK_DOTS = 4;

	const RIDER_INPUT_DEADZONE = 0.08;
	const RIDER_BRAKE_PER_TICK = 0.45;
	const RIDER_JUMP_INPUT_STRENGTH = 90.0;
	const DATA_HORSE_FLAGS = 16;
	const DATA_HORSE_TYPE = 19;
	const DATA_HORSE_VARIANT = 20;

	public $width = 1.4;
	public $length = 1.4;
	public $height = 1.6;
	public $dropExp = [1, 3];

	/** @var HorseInventory */
	protected $inventory;
	protected $riderInputX = 0.0;
	protected $riderInputY = 0.0;
	protected $riderJumping = false;
	protected $riderSneaking = false;
	protected $jumpPower = 0.0;
	protected $horseJumping = false;
	protected $variant = self::VARIANT_WHITE;
	protected $markVariant = self::MARK_NONE;
	public function __construct(FullChunk $chunk, CompoundTag $nbt){
		parent::__construct($chunk, $nbt);
	}

	public function getName() : string{
		return "Horse";
	}

	public function initEntity(){
		$this->setMaxHealth(30);
		parent::initEntity();
		$this->stepHeight = 1.0625;
		$this->variant = isset($this->namedtag->Variant) ? max(0, min(self::VARIANT_DARK_BROWN, (int) $this->namedtag["Variant"])) : mt_rand(0, self::VARIANT_DARK_BROWN);
		$this->markVariant = isset($this->namedtag->MarkVariant) ? max(0, min(self::MARK_BLACK_DOTS, (int) $this->namedtag["MarkVariant"])) : mt_rand(0, self::MARK_BLACK_DOTS);
		$this->syncHorseAppearanceData();

		$this->inventory = new HorseInventory($this);
		if(isset($this->namedtag->Inventory) and $this->namedtag->Inventory instanceof \lycore\nbt\tag\ListTag){
			$this->inventory->loadFromNbt($this->namedtag->Inventory);
		}else{
			if(isset($this->namedtag->SaddleItem) and $this->namedtag->SaddleItem instanceof CompoundTag){
				$this->inventory->setSaddle(NBT::getItemHelper($this->namedtag->SaddleItem));
			}
			if(isset($this->namedtag->ArmorItem) and $this->namedtag->ArmorItem instanceof CompoundTag){
				$this->inventory->setArmor(NBT::getItemHelper($this->namedtag->ArmorItem));
			}
		}

		if(isset($this->namedtag->Saddled) and (int) $this->namedtag["Saddled"] > 0 and $this->inventory->getSaddle()->getId() !== ItemItem::SADDLE){
			$this->inventory->setSaddle(ItemItem::get(ItemItem::SADDLE, 0, 1));
		}
	}

	public function usesPm1eGroundAi() : bool{
		return true;
	}

	public function getInventory(){
		return $this->inventory;
	}

	public function getVariant() : int{
		return $this->variant;
	}

	public function getMarkVariant() : int{
		return $this->markVariant;
	}

	protected function syncHorseAppearanceData(){
		$this->setDataProperty(self::DATA_HORSE_TYPE, self::DATA_TYPE_BYTE, 0);
		$this->setDataProperty(self::DATA_HORSE_VARIANT, self::DATA_TYPE_INT, $this->variant | ($this->markVariant << 8));
	}

	public function canBeSaddled() : bool{
		return !$this->isBaby();
	}

	public function isSaddled() : bool{
		return $this->getDataFlag(self::DATA_FLAGS, self::DATA_FLAG_SADDLED);
	}

	public function setSaddled(bool $saddled) : bool{
		if($saddled and !$this->canBeSaddled()){
			return false;
		}

		$changed = $this->isSaddled() !== $saddled;
		$this->setDataFlag(self::DATA_FLAGS, self::DATA_FLAG_SADDLED, $saddled);
		$this->namedtag->Saddled = new ByteTag("Saddled", $saddled ? 1 : 0);
		return $changed;
	}

	public function canBeRidden() : bool{
		return !$this->isBaby() and $this->isSaddled();
	}

	public function canBeMounted() : bool{
		return !$this->isBaby();
	}

	public function canBeControlledBy($rider) : bool{
		return $this->canBeRidden() and $rider instanceof Player;
	}

	public function supportsPlayerProtocol(Player $player) : bool{
		return ProtocolCompatibility::isProtocol015((int) $player->getProtocol());
	}

	protected function getBreedingItemIds() : array{
		return [
			ItemItem::GOLDEN_APPLE,
			ItemItem::ENCHANTED_GOLDEN_APPLE,
			ItemItem::GOLDEN_CARROT,
		];
	}

	public static function isHorseArmorItem(ItemItem $item) : bool{
		return in_array($item->getId(), [
			ItemItem::LEATHER_HORSE_ARMOR,
			ItemItem::IRON_HORSE_ARMOR,
			ItemItem::GOLDEN_HORSE_ARMOR,
			ItemItem::DIAMOND_HORSE_ARMOR,
		], true);
	}

	public function onInteract(Player $player, ItemItem $item) : bool{
		if(!$this->supportsPlayerProtocol($player)){
			return false;
		}

		if($item instanceof Lead){
			return $item->leashEntity($player, $this);
		}

		if($this->isBaby()){
			return false;
		}

		if($item->getId() === ItemItem::SADDLE and !$this->isSaddled()){
			if($this->inventory->setSaddle($item)){
				$this->consumeInteractionItem($player, $item);
				return true;
			}
		}

		if(self::isHorseArmorItem($item) and $this->inventory->getArmor()->getId() === ItemItem::AIR){
			if($this->inventory->setArmor($item)){
				$this->consumeInteractionItem($player, $item);
				return true;
			}
		}

		if($this->canBeMounted()){
			return $this->mountPlayer($player);
		}

		return false;
	}

	public function handleRiderInventoryButton(Player $player) : bool{
		if($this->getLinkedEntity() !== $player or !$this->supportsPlayerProtocol($player)){
			return false;
		}

		return $this->equipHeldHorseEquipment($player);
	}

	private function equipHeldHorseEquipment(Player $player) : bool{
		$item = $player->getInventory()->getItemInHand();
		if($item->getCount() <= 0){
			return false;
		}
		if($item->getId() === ItemItem::SADDLE){
			return $this->equipSaddleFromPlayer($player, $item);
		}
		if(self::isHorseArmorItem($item)){
			return $this->equipHorseArmorFromPlayer($player, $item);
		}

		return false;
	}

	private function equipSaddleFromPlayer(Player $player, ItemItem $item) : bool{
		if(!$this->canBeSaddled()){
			return false;
		}

		$oldSaddle = $this->inventory->getSaddle();
		$equipment = clone $item;
		$equipment->setCount(1);
		if(!$this->inventory->setSaddle($equipment)){
			return false;
		}

		$this->consumeInteractionItem($player, $item);
		$this->returnReplacedEquipmentToPlayer($player, $oldSaddle);
		return true;
	}

	private function equipHorseArmorFromPlayer(Player $player, ItemItem $item) : bool{
		$oldArmor = $this->inventory->getArmor();
		$equipment = clone $item;
		$equipment->setCount(1);
		if(!$this->inventory->setArmor($equipment)){
			return false;
		}

		$this->consumeInteractionItem($player, $item);
		$this->returnReplacedEquipmentToPlayer($player, $oldArmor);
		return true;
	}

	private function returnReplacedEquipmentToPlayer(Player $player, ItemItem $item){
		if($item->getId() === ItemItem::AIR or $item->getCount() <= 0){
			return;
		}
		if($player->isCreative()){
			return;
		}

		$remaining = $player->getInventory()->addItem($item);
		foreach($remaining as $drop){
			$player->getLevel()->dropItem($player->getPosition(), $drop);
		}
	}

	private function consumeInteractionItem(Player $player, ItemItem $item){
		if($player->isCreative()){
			return;
		}

		$item->setCount($item->getCount() - 1);
		$player->getInventory()->setItemInHand($item->getCount() > 0 ? $item : ItemItem::get(ItemItem::AIR, 0, 0));
	}

	public function mountPlayer(Player $player) : bool{
		if(!$this->canBeMounted()){
			return false;
		}
		if($this->getLinkedEntity() instanceof Entity and $this->getLinkedEntity() !== $player){
			return false;
		}
		if($player->getLinkedEntity() instanceof Entity and $player->getLinkedEntity() !== $this){
			return false;
		}

		$this->linkedEntity = $player;
		$this->linkedType = 1;
		$player->linkedEntity = $this;
		$player->linkedType = 1;
		$this->passenger = $player;
		$player->vehicle = $this;
		$player->setDataFlag(self::DATA_FLAGS, self::DATA_FLAG_RIDING, true);
		$this->syncRiderMount($player);
		return true;
	}

	public function dismountPlayer(Player $player, bool $scheduleDelayedTeleport = true, callable $afterTeleport = null) : bool{
		if($this->getLinkedEntity() !== $player and $player->getLinkedEntity() !== $this){
			return false;
		}

		if($scheduleDelayedTeleport){
			$dismountPosition = $this->getRiderDismountPosition();
			$horse = $this;
			$this->server->getScheduler()->scheduleDelayedTask(new CallbackTask(function(Player $player, Position $position, $yaw, $pitch, $afterTeleport = null, CallbackTask $task = null) use ($horse){
				if($player->isOnline()){
					$player->teleportImmediate($position, $yaw, $pitch);
				}
				if($afterTeleport !== null){
					$afterTeleport($player, $horse);
				}
			}, [$player, $dismountPosition, $player->getYaw(), $player->getPitch(), $afterTeleport]), 5);
		}

		$this->sendHorseLinkPacket($player, $player->getId(), SetEntityLinkPacket::TYPE_REMOVE);
		$this->sendHorseLinkPacket($player, 0, SetEntityLinkPacket::TYPE_REMOVE);

		$this->linkedEntity = null;
		$this->linkedType = 0;
		$this->passenger = null;
		$this->riderInputX = 0.0;
		$this->riderInputY = 0.0;
		$this->riderJumping = false;
		$this->riderSneaking = false;
		$this->jumpPower = 0.0;
		$this->horseJumping = false;

		$player->linkedEntity = null;
		$player->linkedType = 0;
		$player->vehicle = null;
		$player->setDataFlag(self::DATA_FLAGS, self::DATA_FLAG_RIDING, false);
		return true;
	}

	public function attack($damage, \lycore\event\entity\EntityDamageEvent $source){
		return parent::attack($damage, $source);
	}

	public function handleRiderInput(Player $player, float $motX, float $motY, bool $jumping = false, bool $sneaking = false){
		if($this->getLinkedEntity() !== $player){
			return false;
		}

		if($sneaking){
			return $this->dismountPlayer($player, true);
		}

		$pressedJump = $jumping && !$this->riderJumping;
		$this->riderInputX = max(-1.0, min(1.0, $motX));
		$this->riderInputY = max(-1.0, min(1.0, $motY));
		if($pressedJump){
			$this->handleRiderJump($player);
		}
		$this->riderJumping = $jumping;
		$this->riderSneaking = $sneaking;
		return true;
	}

	public function handleRiderJump(Player $player) : bool{
		if($this->getLinkedEntity() !== $player or !$this->supportsPlayerProtocol($player)){
			return false;
		}

		$this->queueRiderJump(self::RIDER_JUMP_INPUT_STRENGTH);
		return true;
	}

	protected function queueRiderJump(float $jumpPowerIn){
		$this->jumpPower = $this->normalizeRiderJumpPower($jumpPowerIn);
	}

	protected function normalizeRiderJumpPower(float $jumpPowerIn) : float{
		if($jumpPowerIn < 0){
			return 0.0;
		}

		return $jumpPowerIn >= self::RIDER_JUMP_INPUT_STRENGTH ? 1.0 : 0.4 + (0.4 * $jumpPowerIn / self::RIDER_JUMP_INPUT_STRENGTH);
	}

	public function setJumpPower(float $jumpPowerIn){
		if(!$this->isSaddled() or $jumpPowerIn < 0){
			$this->jumpPower = 0.0;
			return;
		}

		$this->jumpPower = $this->normalizeRiderJumpPower($jumpPowerIn);
	}

	public function getJumpPower() : float{
		return $this->jumpPower;
	}

	public function isHorseJumping() : bool{
		return $this->horseJumping;
	}

	public function setHorseJumping(bool $jumping){
		$this->horseJumping = $jumping;
	}

	protected function tickPm1eGroundAi(int $tickDiff) : bool{
		$rider = $this->getLinkedEntity();
		if($rider instanceof Player){
			return $this->tickRiddenHorse($rider, $tickDiff);
		}

		return parent::tickPm1eGroundAi($tickDiff);
	}

	protected function tickRiddenHorse(Player $rider, int $tickDiff) : bool{
		$level = $this->getLevel();
		if($level === null){
			return false;
		}

		$chunksReady = $this->preloadRiderChunks(2);
		if(!$chunksReady){
			$this->syncRiderSeatPosition($rider, MovePlayerPacket::MODE_NORMAL, false);
			$this->syncLegacyRiderSeatPosition($rider);
			return true;
		}

		$liquidType = $this->getPm1eLiquidType();
		$inLiquid = $liquidType !== null;
		if(!$this->canBeControlledBy($rider)){
			if(!$inLiquid){
				$this->tryApplyRiderJump(0.0);
			}
			$updated = parent::tickPm1eGroundAi($tickDiff);
			$this->preloadRiderChunks(1);
			$this->syncRiderSeatPosition($rider, MovePlayerPacket::MODE_NORMAL, false);
			$this->syncLegacyRiderSeatPosition($rider);
			return $updated;
		}

		$inputX = $this->riderInputX;
		$inputY = $this->riderInputY;
		$adjustedInputY = $inputY < 0 ? $inputY * 0.5 : $inputY;
		$inputLength = sqrt(($inputX * $inputX) + ($adjustedInputY * $adjustedInputY));

		if($inputLength <= self::RIDER_INPUT_DEADZONE){
			$this->motionX *= 1.0 - self::RIDER_BRAKE_PER_TICK;
			$this->motionZ *= 1.0 - self::RIDER_BRAKE_PER_TICK;
			if(($this->motionX * $this->motionX) + ($this->motionZ * $this->motionZ) < 0.000025){
				$this->motionX = 0.0;
				$this->motionZ = 0.0;
			}
		}else{
			$yaw = deg2rad($rider->yaw);
			$dirX = $inputX / $inputLength;
			$dirY = $adjustedInputY / $inputLength;
			$wishX = (-sin($yaw) * $dirY) + (cos($yaw) * $dirX);
			$wishZ = (cos($yaw) * $dirY) + (sin($yaw) * $dirX);
			$strength = min(1.0, $inputLength);
			$strength = max(0.0, ($strength - self::RIDER_INPUT_DEADZONE) / (1.0 - self::RIDER_INPUT_DEADZONE));
			$strength = pow($strength, 1.6);

			$speed = $this->getHorseRidingSpeed($liquidType);

			$targetX = $wishX * $speed * $strength;
			$targetZ = $wishZ * $speed * $strength;
			$this->motionX = $targetX;
			$this->motionZ = $targetZ;

			$currentSpeed = sqrt(($this->motionX * $this->motionX) + ($this->motionZ * $this->motionZ));
			if($currentSpeed > $speed and $currentSpeed > 0.00001){
				$this->motionX *= $speed / $currentSpeed;
				$this->motionZ *= $speed / $currentSpeed;
			}
		}
		$this->yaw = $rider->yaw;

		$this->normalizePm1eGroundCollisionBox();
		$this->recoverPartialBlockStandingHeight();

		if($inLiquid){
			$this->motionY = max($this->motionY, $this->getPm1eLiquidBuoyancy());
		}else{
			if(!$this->tryApplyRiderJump($inputY)){
				if($this->onGround){
				$this->motionY = 0.0;
				$this->horseJumping = false;
				$this->jumpPower = 0.0;
				}else{
					$this->motionY -= $this->gravity;
				}
			}
		}

		$this->move($this->motionX, $this->motionY, $this->motionZ);
		$this->recoverPartialBlockStandingHeight();

		$friction = 1 - $this->drag;
		if($this->onGround and (abs($this->motionX) > 0.00001 or abs($this->motionZ) > 0.00001)){
			$friction = $this->getLevel()->getBlock($this->temporalVector->setComponents((int) floor($this->x), (int) floor($this->y - 1), (int) floor($this->z) - 1))->getFrictionFactor() * $friction;
		}

		$this->motionX *= $friction;
		$this->motionZ *= $friction;
		$this->motionY *= $inLiquid ? 0.8 : 1 - $this->drag;
		if($this->onGround and !$inLiquid){
			$this->motionY *= -0.5;
		}

		$chunksReady = $this->preloadRiderChunks(1);
		if(!$chunksReady){
			$this->syncRiderSeatPosition($rider, MovePlayerPacket::MODE_NORMAL, false);
			$this->syncLegacyRiderSeatPosition($rider);
			return true;
		}

		$this->syncRiderSeatPosition($rider, MovePlayerPacket::MODE_NORMAL, false);
		$this->syncLegacyRiderSeatPosition($rider);
		return true;
	}

	protected function tryApplyRiderJump(float $inputY = 0.0) : bool{
		if($this->onGround and $this->jumpPower > 0 and !$this->horseJumping){
			$jumpPower = $this->jumpPower;
			$this->motionY = $this->getPm1eJumpStrength() * $jumpPower;
			$this->horseJumping = true;
			$this->onGround = false;

			if($inputY > 0){
				$yaw = deg2rad($this->yaw);
				$this->motionX += -sin($yaw) * 0.4 * $jumpPower;
				$this->motionZ += cos($yaw) * 0.4 * $jumpPower;
			}
			$this->jumpPower = 0.0;
			return true;
		}

		return false;
	}

	protected function getPm1eBaseSpeed($liquidType = null) : float{
		return $this->getHorseRidingSpeed($liquidType) * 0.6;
	}

	protected function getHorseRidingSpeed($liquidType = null) : float{
		if($liquidType === "lava"){
			return 0.02;
		}
		if($liquidType === "water"){
			return 0.05;
		}

		return 0.3375;
	}

	protected function getPm1eJumpStrength() : float{
		return 0.6;
	}

	protected function syncRiderMount(Player $rider){
		$this->sendHorseLinkPacket($rider, $rider->getId(), SetEntityLinkPacket::TYPE_RIDE);
		$this->sendHorseLinkPacket($rider, 0, SetEntityLinkPacket::TYPE_RIDE);
		$this->syncRiderSeatPosition($rider, MovePlayerPacket::MODE_RESET);
		$this->sendLegacyRiderSeatMetadata($rider);
		$this->syncLegacyRiderSeatPosition($rider);
	}

	protected function sendHorseLinkPacket(Player $rider, $riderId, $type){
		if($this->getId() === null or $rider->getId() === null){
			return;
		}

		$pk = new SetEntityLinkPacket();
		$pk->from = $this->getId();
		$pk->to = $riderId;
		$pk->type = $type;

		if($riderId === 0){
			$rider->dataPacket($pk);
		}elseif($this->server !== null and $this->level !== null and method_exists($this->level, "getPlayers")){
			$targets = $this->getProtocol015HorseLinkViewers($rider);
			if(count($targets) > 0){
				$this->server->broadcastPacket($targets, $pk);
			}
		}elseif($riderId === $rider->getId()){
			$rider->dataPacket($pk);
		}
	}

	protected function getProtocol015HorseLinkViewers(Player $rider) : array{
		$targets = [];
		if($this->level === null or !method_exists($this->level, "getPlayers")){
			return $targets;
		}

		foreach($this->level->getPlayers() as $viewer){
			if(ProtocolCompatibility::isProtocol015((int) $viewer->getProtocol())){
				$targets[] = $viewer;
			}
		}

		return $targets;
	}

	protected function getLegacyRiderViewers(Player $rider) : array{
		$targets = [];
		foreach($rider->getViewers() as $viewer){
			if($viewer instanceof Player and !ProtocolCompatibility::isProtocol015((int) $viewer->getProtocol())){
				$targets[] = $viewer;
			}
		}

		return $targets;
	}

	protected function sendLegacyRiderSeatMetadata(Player $rider){
		$targets = $this->getLegacyRiderViewers($rider);
		if(count($targets) === 0){
			return;
		}

		$rider->sendData($targets, [
			self::DATA_FLAGS => [self::DATA_TYPE_LONG, (int) $rider->getDataProperty(self::DATA_FLAGS)],
		]);
	}

	protected function syncLegacyRiderSeatPosition(Player $rider){
		if($this->server === null or $rider->getId() === null){
			return;
		}

		$targets = $this->getLegacyRiderViewers($rider);
		if(count($targets) === 0){
			return;
		}

		$pk = new MoveEntityPacket();
		$pk->entities[] = [$rider->getId(), $rider->x, $rider->y + $rider->getEyeHeight(), $rider->z, $rider->yaw, $rider->yaw, $rider->pitch];
		$this->server->broadcastPacket($targets, $pk);
	}

	protected function syncRiderSeatPosition(Player $rider, int $mode = MovePlayerPacket::MODE_NORMAL, bool $sendPacket = true){
		$rider->x = $this->getRiderSeatX();
		$rider->y = $this->getRiderSeatY();
		$rider->z = $this->getRiderSeatZ();

		if(!$sendPacket or $this->getId() === null or $rider->getId() === null){
			return;
		}

		$pk = new MovePlayerPacket();
		$pk->eid = 0;
		$pk->x = $rider->x;
		$pk->y = $rider->y + $rider->getEyeHeight();
		$pk->z = $rider->z;
		$pk->yaw = $rider->yaw;
		$pk->bodyYaw = $rider->yaw;
		$pk->pitch = $rider->pitch;
		$pk->mode = $mode;
		$pk->onGround = $this->onGround;
		$rider->dataPacket($pk);
	}

	protected function getRiderSeatY() : float{
		return $this->y + 1.1;
	}

	protected function getRiderSeatX() : float{
		return $this->x - (sin(deg2rad($this->yaw)) * -0.2);
	}

	protected function getRiderSeatZ() : float{
		return $this->z + (cos(deg2rad($this->yaw)) * -0.2);
	}

	protected function getRiderDismountPosition() : Position{
		$yaw = deg2rad($this->yaw);
		$distance = ($this->width / 2) + 0.6;
		return new Position($this->x + cos($yaw) * $distance, $this->y, $this->z + sin($yaw) * $distance, $this->level);
	}

	public function spawnTo(Player $player){
		$pk = new AddEntityPacket();
		$pk->eid = $this->getId();
		$pk->type = self::NETWORK_ID;
		$pk->x = $this->x;
		$pk->y = $this->y;
		$pk->z = $this->z;
		$pk->speedX = $this->motionX;
		$pk->speedY = $this->motionY;
		$pk->speedZ = $this->motionZ;
		$pk->yaw = $this->yaw;
		$pk->pitch = $this->pitch;
		$pk->metadata = $this->dataProperties;
		$player->dataPacket($pk);
		$this->inventory->sendArmor($player);

		parent::spawnTo($player);
	}

	public function getDrops(){
		$drops = [];
		$leather = mt_rand(0, 2);
		if($leather > 0){
			$drops[] = ItemItem::get(ItemItem::LEATHER, 0, $leather);
		}
		foreach($this->inventory->getInventoryDrops() as $item){
			$drops[] = $item;
		}

		return $drops;
	}

	public function saveNBT(){
		parent::saveNBT();
		$this->namedtag->Saddled = new ByteTag("Saddled", $this->isSaddled() ? 1 : 0);
		$this->namedtag->Variant = new IntTag("Variant", $this->variant);
		$this->namedtag->MarkVariant = new IntTag("MarkVariant", $this->markVariant);
		$this->namedtag->Inventory = $this->inventory->saveToNbt();
	}
}
