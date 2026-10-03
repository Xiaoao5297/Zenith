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

use lycore\entity\behavior\WolfAttackBehavior;
use lycore\item\Dye;
use lycore\item\Item as ItemItem;
use lycore\item\Lead;
use lycore\level\format\FullChunk;
use lycore\math\Vector3;
use lycore\nbt\tag\ByteTag;
use lycore\nbt\tag\CompoundTag;
use lycore\nbt\tag\StringTag;
use lycore\network\protocol\AddEntityPacket;
use lycore\network\protocol\EntityEventPacket;
use lycore\Player;
use lycore\Server;
use lycore\utils\TextFormat;

class Wolf extends Animal{
	const NETWORK_ID = 14;
	const DEFAULT_COLLAR_COLOR = 14;
	const SITTING_NAME_SUFFIX = " - 待命中";
	const FOLLOW_START_DISTANCE_SQUARED = 16.0;
	const FOLLOW_STOP_DISTANCE_SQUARED = 9.0;
	const FOLLOW_TELEPORT_DISTANCE_SQUARED = 225.0;

	public $width = 0.6;
	public $length = 0.9;
	public $height = 0.8;

	public $dropExp = [1, 3];

	private $hurt = 4;
	private $angry = false;
	private $ownerName = "";
	private $collarColor = 14;
	private $pm1eAttackLeapTarget = null;
	private $pm1eAttackLeapCooldown = 0;
	
	public function __construct(FullChunk $chunk, CompoundTag $nbt){
		parent::__construct($chunk, $nbt);
	}

	public function initEntity(){
		$loadedTamed = isset($this->namedtag->Tamed) && (int) $this->namedtag["Tamed"] > 0;
		$this->setMaxHealth($loadedTamed ? 20 : 8);
		$this->addBehavior(new WolfAttackBehavior($this, [], false));

		parent::initEntity();

		$this->ownerName = isset($this->namedtag->OwnerName) ? (string) $this->namedtag["OwnerName"] : "";
		$this->collarColor = isset($this->namedtag->CollarColor) ? ((int) $this->namedtag["CollarColor"] & 0x0f) : self::DEFAULT_COLLAR_COLOR;
		$this->setTamed($loadedTamed);
		$this->setSitting(isset($this->namedtag->Sitting) && (int) $this->namedtag["Sitting"] > 0);
		$this->setAngry(false);
		$this->setCollarColor($this->collarColor);
	}

	public function saveNBT(){
		parent::saveNBT();
		$this->namedtag->Tamed = new ByteTag("Tamed", $this->isTamed() ? 1 : 0);
		$this->namedtag->Sitting = new ByteTag("Sitting", $this->isSitting() ? 1 : 0);
		$this->namedtag->OwnerName = new StringTag("OwnerName", $this->ownerName);
		$this->namedtag->CollarColor = new ByteTag("CollarColor", $this->collarColor);
	}

	public function usesPm1eGroundAi() : bool{
		return true;
	}

	public function getHurt(){
		return $this->hurt;
	}

	public function setHurt($hurt){
		$this->hurt = $hurt;
	}

	public function isTamed() : bool{
		return $this->getDataFlag(self::DATA_FLAGS, self::DATA_FLAG_TAMED);
	}

	public function setTamed(bool $tamed){
		$this->setDataFlag(self::DATA_FLAGS, self::DATA_FLAG_TAMED, $tamed);
		$this->setMaxHealth($tamed ? 20 : 8);
		$this->updatePetNameTag();
	}

	public function isSitting() : bool{
		return $this->getDataFlag(self::DATA_FLAGS, self::DATA_FLAG_SITTING);
	}

	public function setSitting(bool $sitting){
		$this->setDataFlag(self::DATA_FLAGS, self::DATA_FLAG_SITTING, $sitting);
		if($sitting){
			$this->setPm1eRetaliationTarget(null);
			$this->setPm1eFollowTarget(null);
			$this->setPm1eMoveTarget(null);
			$this->setPm1eMoveMultiplier(1.0);
			$this->pm1eAttackLeapTarget = null;
			$this->setAngry(false);
		}
		$this->updatePetNameTag();
	}

	public function isAngry() : bool{
		return $this->angry;
	}

	public function setAngry(bool $angry){
		$this->angry = $angry;
		$this->setDataFlag(self::DATA_FLAGS, self::DATA_FLAG_ANGRY, $angry);
	}

	public function setOwner(Player $player){
		$this->ownerName = $player->getName();
		$this->updatePetNameTag();
	}

	public function getOwnerName() : string{
		return $this->ownerName;
	}

	public function isOwner(Player $player) : bool{
		return $this->ownerName !== "" && strtolower($this->ownerName) === strtolower($player->getName());
	}

	public function getOwnerPlayer(){
		if($this->ownerName === ""){
			return null;
		}

		$owner = $this->server->getPlayerExact($this->ownerName);
		if(!($owner instanceof Player)){
			return null;
		}
		if(method_exists($owner, "isConnected") && !$owner->isConnected()){
			return null;
		}
		if(!$owner->isAlive() || $owner->closed){
			return null;
		}

		return $owner;
	}

	public function getCollarColor() : int{
		return $this->collarColor;
	}

	public function setCollarColor(int $color){
		$this->collarColor = $color & 0x0f;
		$this->namedtag->CollarColor = new ByteTag("CollarColor", $this->collarColor);
		$this->setDataProperty(self::DATA_COLOR_INFO, self::DATA_TYPE_BYTE, $this->collarColor);
		$this->updatePetNameTag();
	}

	public function onInteract(Player $player, ItemItem $item) : bool{
		if($item instanceof Lead){
			return $item->leashEntity($player, $this);
		}

		if($this->isTamed()){
			if(!$this->isOwner($player)){
				return false;
			}

			if($item->getId() === ItemItem::DYE){
				$newColor = self::dyeMetaToWoolColor((int) $item->getDamage());
				if($newColor !== $this->getCollarColor()){
					$this->setCollarColor(self::dyeMetaToWoolColor((int) $item->getDamage()));
					$this->consumeHeldItem($player, $item);
				}
				return true;
			}

			if(!$this->isInsideOfWater()){
				$this->setSitting(!$this->isSitting());
				return true;
			}

			return false;
		}

		if($item->getId() === ItemItem::BONE){
			$this->consumeHeldItem($player, $item);
			if(mt_rand(0, 2) === 0){
				$this->setOwner($player);
				$this->setTamed(true);
				$this->setSitting(true);
				$this->setAngry(false);
				$this->setCollarColor(self::DEFAULT_COLLAR_COLOR);
				$this->setHealth($this->getMaxHealth());
				$this->broadcastTameEvent(EntityEventPacket::TAME_SUCCESS);
			}else{
				$this->broadcastTameEvent(EntityEventPacket::TAME_FAIL);
			}
			return true;
		}

		return parent::onInteract($player, $item);
	}

	private static function dyeMetaToWoolColor(int $meta) : int{
		switch($meta & 0x0f){
			case Dye::BLACK:
				return 15;
			case Dye::RED:
				return 14;
			case Dye::GREEN:
				return 13;
			case Dye::BROWN:
				return 12;
			case Dye::BLUE:
				return 11;
			case Dye::PURPLE:
				return 10;
			case Dye::CYAN:
				return 9;
			case Dye::SILVER:
				return 8;
			case Dye::GRAY:
				return 7;
			case Dye::PINK:
				return 6;
			case Dye::LIME:
				return 5;
			case Dye::YELLOW:
				return 4;
			case Dye::LIGHT_BLUE:
				return 3;
			case Dye::MAGENTA:
				return 2;
			case Dye::ORANGE:
				return 1;
			case Dye::WHITE:
			default:
				return 0;
		}
	}

	private function updatePetNameTag(){
		if(!$this->isTamed() || $this->ownerName === ""){
			$this->setNameTagVisible(false);
			return;
		}

		$name = self::woolColorToTextFormat($this->collarColor) . $this->ownerName . "'s Dog";
		if($this->isSitting()){
			$name .= self::SITTING_NAME_SUFFIX;
		}

		$this->setNameTag($name);
		$this->setNameTagVisible(true);
	}

	private static function woolColorToTextFormat(int $color) : string{
		switch($color & 0x0f){
			case 0:
				return TextFormat::WHITE;
			case 1:
				return TextFormat::GOLD;
			case 2:
				return TextFormat::LIGHT_PURPLE;
			case 3:
				return TextFormat::AQUA;
			case 4:
				return TextFormat::YELLOW;
			case 5:
				return TextFormat::GREEN;
			case 6:
				return TextFormat::LIGHT_PURPLE;
			case 7:
				return TextFormat::DARK_GRAY;
			case 8:
				return TextFormat::GRAY;
			case 9:
				return TextFormat::DARK_AQUA;
			case 10:
				return TextFormat::DARK_PURPLE;
			case 11:
				return TextFormat::BLUE;
			case 12:
				return TextFormat::GOLD;
			case 13:
				return TextFormat::DARK_GREEN;
			case self::DEFAULT_COLLAR_COLOR:
				return TextFormat::RED;
			case 15:
				return TextFormat::BLACK;
			default:
				return TextFormat::WHITE;
		}
	}

	private function consumeHeldItem(Player $player, ItemItem $item){
		if(!$player->isSurvival()){
			return;
		}

		$item->setCount($item->getCount() - 1);
		$player->getInventory()->setItemInHand($item->getCount() > 0 ? $item : ItemItem::get(ItemItem::AIR, 0, 0));
	}

	private function broadcastTameEvent(int $event){
		$pk = new EntityEventPacket();
		$pk->eid = $this->getId();
		$pk->event = $event;
		Server::broadcastPacket($this->hasSpawned, $pk);
	}

	protected function tickPm1eGroundAi(int $tickDiff) : bool{
		$this->updateOwnerFollowTarget($tickDiff);
		return parent::tickPm1eGroundAi($tickDiff);
	}

	private function updateOwnerFollowTarget(int $tickDiff = 1){
		if(!$this->isTamed()){
			return;
		}

		if($this->isSitting()){
			$this->setPm1eRetaliationTarget(null);
			$this->setPm1eFollowTarget(null);
			$this->setPm1eMoveTarget(null);
			$this->setPm1eMoveMultiplier(0.0);
			$this->setPm1eStayTime($this->isInsideOfWater() ? 0 : $tickDiff + 20);
			return;
		}

		$retaliationTarget = $this->getPm1eRetaliationTarget();
		if($retaliationTarget instanceof Entity && !$retaliationTarget->closed && $retaliationTarget->isAlive()){
			return;
		}

		$owner = $this->getOwnerPlayer();
		if(!($owner instanceof Player) || $owner->getLevel() !== $this->getLevel()){
			$this->setPm1eFollowTarget(null);
			$this->setPm1eMoveMultiplier(1.0);
			return;
		}

		$distanceSquared = $this->distanceSquared($owner);
		if($distanceSquared > self::FOLLOW_TELEPORT_DISTANCE_SQUARED){
			$this->teleport(new Vector3($owner->x, $owner->y, $owner->z), $this->yaw, $this->pitch);
			$this->setPm1eFollowTarget(null);
			$this->setPm1eMoveMultiplier(1.0);
			$distanceSquared = $this->distanceSquared($owner);
		}

		if($distanceSquared > self::FOLLOW_START_DISTANCE_SQUARED){
			$this->setPm1eFollowTarget($owner);
			$this->setPm1eMoveMultiplier(1.35);
			$this->setPm1eStayTime(0);
		}elseif($distanceSquared <= self::FOLLOW_STOP_DISTANCE_SQUARED){
			if($this->getPm1eFollowTarget() === $owner){
				$this->setPm1eFollowTarget(null);
			}
			$this->setPm1eMoveMultiplier(1.0);
		}
	}

	public function requestPm1eAttackLeap(Entity $target){
		if(!$this->onGround || $this->pm1eAttackLeapCooldown > 0 || $this->isSitting()){
			return;
		}

		$distanceSquared = $this->distanceSquared($target);
		if($distanceSquared >= 4.0 && $distanceSquared <= 16.0){
			$this->pm1eAttackLeapTarget = $target;
		}
	}

	protected function checkPm1eJump() : bool{
		if($this->pm1eAttackLeapCooldown > 0){
			--$this->pm1eAttackLeapCooldown;
		}

		if($this->pm1eAttackLeapTarget instanceof Entity){
			$target = $this->pm1eAttackLeapTarget;
			$this->pm1eAttackLeapTarget = null;

			if($this->onGround && !$target->closed && $target->isAlive()){
				$distanceSquared = $this->distanceSquared($target);
				if($distanceSquared >= 4.0 && $distanceSquared <= 16.0){
					$this->motionY = max($this->motionY, $this->getPm1eJumpStrength());
					$this->pm1eAttackLeapCooldown = 10;
					return true;
				}
			}
		}

		return parent::checkPm1eJump();
	}
	
	public function getName() : string{
		return "Wolf";
	}
	
	public function spawnTo(Player $player){
		$pk = new AddEntityPacket();
		$pk->eid = $this->getId();
		$pk->type = Wolf::NETWORK_ID;
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

		parent::spawnTo($player);
	}
}
