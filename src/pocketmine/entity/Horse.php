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

/*
 * 移植自 lycore\entity\Horse，命名空间改为 pocketmine\entity。
 * 骑手输入沿用 lycore 方案：Player::PLAYER_INPUT_PACKET -> handleRiderInput，
 * 运动逻辑按核心 Mob/Creature 更新流程改写（不再依赖 lycore 的 PM1E AI）。
 */

namespace pocketmine\entity;

use pocketmine\inventory\HorseInventory;
use pocketmine\inventory\InventoryHolder;
use pocketmine\item\Item as ItemItem;
use pocketmine\level\format\FullChunk;
use pocketmine\math\Vector3;
use pocketmine\nbt\NBT;
use pocketmine\nbt\tag\ByteTag;
use pocketmine\nbt\tag\CompoundTag;
use pocketmine\nbt\tag\IntTag;
use pocketmine\nbt\tag\ListTag;
use pocketmine\network\protocol\AddEntityPacket;
use pocketmine\network\protocol\MovePlayerPacket;
use pocketmine\Player;

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

	const DATA_HORSE_TYPE = 19;
	const DATA_HORSE_VARIANT = 20;
	const DATA_FLAG_SADDLED = 4;

	public $width = 1.4;
	public $length = 1.4;
	public $height = 1.6;
	public $dropExp = [1, 3];

	/** @var HorseInventory */
	protected $inventory;
	protected $variant = self::VARIANT_WHITE;
	protected $markVariant = self::MARK_NONE;

	protected $riderInputX = 0.0;
	protected $riderInputY = 0.0;
	protected $riderJumping = false;
	protected $riderSneaking = false;
	protected $jumpPower = 0.0;
	protected $horseJumping = false;

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
		if(isset($this->namedtag->Inventory) and $this->namedtag->Inventory instanceof ListTag){
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

	public static function isHorseArmorItem(ItemItem $item) : bool{
		return in_array($item->getId(), [
			ItemItem::LEATHER_HORSE_ARMOR,
			ItemItem::IRON_HORSE_ARMOR,
			ItemItem::GOLDEN_HORSE_ARMOR,
			ItemItem::DIAMOND_HORSE_ARMOR,
		], true);
	}

	public function onInteract(Player $player, ItemItem $item) : bool{
		if($this->isBaby()){
			return false;
		}

		if($item->getId() === ItemItem::SADDLE and !$this->isSaddled()){
			$equipment = clone $item;
			$equipment->setCount(1);
			if($this->inventory->setSaddle($equipment)){
				$this->consumeInteractionItem($player, $item);
				return true;
			}
		}

		if(self::isHorseArmorItem($item) and $this->inventory->getArmor()->getId() === ItemItem::AIR){
			$equipment = clone $item;
			$equipment->setCount(1);
			if($this->inventory->setArmor($equipment)){
				$this->consumeInteractionItem($player, $item);
				return true;
			}
		}

		if($this->canBeMounted()){
			return $this->mountPlayer($player);
		}

		return false;
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

		$this->setBehaviorsEnabled(false);
		$this->getNavigator()->clearPath();
		$this->riderInputX = 0.0;
		$this->riderInputY = 0.0;
		$this->riderJumping = false;
		$this->riderSneaking = false;
		$this->jumpPower = 0.0;
		$this->horseJumping = false;

		$player->linkEntity($this);
		$player->setDataFlag(self::DATA_FLAGS, self::DATA_FLAG_RIDING, true);
		$this->syncRiderSeatPosition($player, MovePlayerPacket::MODE_RESET);
		return true;
	}

	public function dismountPlayer(Player $player) : bool{
		if($this->getLinkedEntity() !== $player and $player->getLinkedEntity() !== $this){
			return false;
		}

		$player->setLinked(0, $this);
		$player->setDataFlag(self::DATA_FLAGS, self::DATA_FLAG_RIDING, false);

		$this->riderInputX = 0.0;
		$this->riderInputY = 0.0;
		$this->riderJumping = false;
		$this->riderSneaking = false;
		$this->jumpPower = 0.0;
		$this->horseJumping = false;
		$this->setBehaviorsEnabled(true);
		return true;
	}

	public function handleRiderInput(Player $player, float $motX, float $motY, bool $jumping = false, bool $sneaking = false){
		if($this->getLinkedEntity() !== $player){
			return false;
		}

		if($sneaking){
			return $this->dismountPlayer($player);
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
		if($this->getLinkedEntity() !== $player){
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

	public function onUpdate($currentTick){
		$rider = $this->getLinkedEntity();
		if($rider instanceof Player and $this->canBeControlledBy($rider)){
			$this->applyRiderMovement($rider);
			$hasUpdate = parent::onUpdate($currentTick);
			$this->syncRiderSeatPosition($rider, MovePlayerPacket::MODE_NORMAL);
			return $hasUpdate;
		}

		return parent::onUpdate($currentTick);
	}

	protected function applyRiderMovement(Player $rider){
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

			$speed = $this->getHorseRidingSpeed();
			$this->motionX = $wishX * $speed * $strength;
			$this->motionZ = $wishZ * $speed * $strength;
		}

		$this->yaw = $rider->yaw;

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
		}elseif($this->onGround){
			$this->horseJumping = false;
		}
	}

	protected function getHorseRidingSpeed() : float{
		return 0.3375;
	}

	protected function getPm1eJumpStrength() : float{
		return 0.6;
	}

	protected function syncRiderSeatPosition(Player $rider, int $mode = MovePlayerPacket::MODE_NORMAL){
		$rider->x = $this->getRiderSeatX();
		$rider->y = $this->getRiderSeatY();
		$rider->z = $this->getRiderSeatZ();

		if($rider->getId() === null){
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
