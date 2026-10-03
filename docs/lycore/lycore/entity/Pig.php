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

use lycore\level\Position;
use lycore\network\protocol\AddEntityPacket;
use lycore\network\protocol\MoveEntityPacket;
use lycore\network\protocol\MovePlayerPacket;
use lycore\network\protocol\RemoveEntityPacket;
use lycore\network\protocol\SetEntityLinkPacket;
use lycore\network\protocol\ProtocolCompatibility;
use lycore\math\Vector3;
use lycore\Player;
use lycore\item\Item as ItemItem;
use lycore\item\Lead;
use lycore\entity\behavior\{findFoodBehavior, inLoveBehavior};
use lycore\scheduler\CallbackTask;

use lycore\nbt\tag\ByteTag;
use lycore\nbt\tag\CompoundTag;
use lycore\level\format\FullChunk;

class Pig extends Animal{
	const NETWORK_ID = 12;

	public $width = 0.3;
	public $length = 0.9;
	public $height = 0.3;

	public $dropExp = [1, 3];
	protected $riderInputX = 0.0;
	protected $riderInputY = 0.0;
	protected $riderJumping = false;
	protected $riderSneaking = false;
	protected $riderSeatEntityId = null;
	
	public function __construct(FullChunk $chunk, CompoundTag $nbt){
		parent::__construct($chunk, $nbt);
	}
	
	public function getName() : string{
		return "Pig";
	}
	
	public function initEntity(){
		
		$this->addBehavior(new inLoveBehavior($this));
		$this->addBehavior(new findFoodBehavior($this, 391)); //CARROT
		
		$this->setMaxHealth(10);
		parent::initEntity();

		$saddled = (isset($this->namedtag->Saddled) and (int) $this->namedtag["Saddled"] > 0)
			|| (isset($this->namedtag->saddled) and (int) $this->namedtag["saddled"] > 0);
		$this->setSaddled($saddled);
	}

	public function usesPm1eGroundAi() : bool{
		return true;
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

	public function getItemControllable() : int{
		return ItemItem::CARROT_ON_A_STICK;
	}

	protected function getBreedingItemIds() : array{
		return [ItemItem::CARROT];
	}

	public function canBeControlledBy($rider) : bool{
		if(!$this->canBeRidden() or !($rider instanceof Player)){
			return false;
		}

		$item = $rider->getInventory()->getItemInHand();
		if($item instanceof ItemItem){
			$item = ProtocolCompatibility::normalizeClientItemForProtocol((int) $rider->getProtocol(), $item);
		}
		return $item instanceof ItemItem and $item->getId() === $this->getItemControllable();
	}

	public function mountPlayer(Player $player) : bool{
		if(!$this->canBeRidden()){
			return false;
		}

		$item = $player->getInventory()->getItemInHand();
		if($item instanceof ItemItem){
			$item = ProtocolCompatibility::normalizeClientItemForProtocol((int) $player->getProtocol(), $item);
		}
		if(!($item instanceof ItemItem) or $item->getId() !== $this->getItemControllable()){
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

	public function dismountPlayer(Player $player, bool $scheduleDelayedTeleport = false) : bool{
        $this->server->getScheduler()->scheduleDelayedTask(new CallbackTask(function(Player $player, Position $position, $yaw, $pitch, CallbackTask $task = null){
            if($player->isOnline()){
                $player->teleportImmediate($position, $yaw, $pitch);
            }
        }, [$player, $player->getPosition(), $player->getYaw(), $player->getPitch()]), 5);

		if($this->getLinkedEntity() !== $player and $player->getLinkedEntity() !== $this){
			return false;
		}

		if($this->riderSeatEntityId !== null){
			$this->removeRiderSeatEntity($player, $this->riderSeatEntityId);
		}

		$this->linkedEntity = null;
		$this->linkedType = 0;
		$this->passenger = null;
		$this->riderSeatEntityId = null;
		$this->riderInputX = 0.0;
		$this->riderInputY = 0.0;
		$this->riderJumping = false;
		$this->riderSneaking = false;

		$player->linkedEntity = null;
		$player->linkedType = 0;
		$player->vehicle = null;
		$player->setDataFlag(self::DATA_FLAGS, self::DATA_FLAG_RIDING, false);

		return true;
	}

	public function kill(){
		$rider = $this->getLinkedEntity();
		if($rider instanceof Player){
			$this->dismountPlayer($rider, false);
		}

		parent::kill();
	}

	public function onInteract(Player $player, ItemItem $item) : bool{
		$item = ProtocolCompatibility::normalizeClientItemForProtocol((int) $player->getProtocol(), $item);

		if($item instanceof Lead){
			return $item->leashEntity($player, $this);
		}

		if($this->isBaby()){
			return false;
		}

		if($item->getId() === ItemItem::SADDLE and !$this->isSaddled()){
			if($this->setSaddled(true) and !$player->isCreative()){
				$item->setCount($item->getCount() - 1);
				$player->getInventory()->setItemInHand($item->getCount() > 0 ? $item : ItemItem::get(ItemItem::AIR, 0, 1));
			}
			return true;
		}

		if($item->getId() === $this->getItemControllable() and $this->canBeRidden()){
			return $this->mountPlayer($player);
		}

		return false;
	}

	public function handleRiderInput(Player $player, float $motX, float $motY, bool $jumping = false, bool $sneaking = false){
		if($this->getLinkedEntity() !== $player){
			return false;
		}

		$this->riderInputX = max(-1.0, min(1.0, $motX));
		$this->riderInputY = max(-1.0, min(1.0, $motY));
		$this->riderJumping = $jumping;
		$this->riderSneaking = $sneaking;
		return true;
	}

	protected function tickPm1eGroundAi(int $tickDiff) : bool{
		$rider = $this->getLinkedEntity();
		if($rider instanceof Player){
			return $this->tickRiddenPig($rider, $tickDiff);
		}

		return parent::tickPm1eGroundAi($tickDiff);
	}

	protected function tickRiddenPig(Player $rider, int $tickDiff) : bool{
		$level = $this->getLevel();
		if($level === null){
			return false;
		}

		$chunksReady = $this->preloadRiderChunks(2);
		if(!$chunksReady){
			$this->syncRiderSeatPosition($rider, MovePlayerPacket::MODE_NORMAL, false);
			$this->syncRiderSeatEntityPosition($rider);
			return true;
		}

		$liquidType = $this->getPm1eLiquidType();
		$inLiquid = $liquidType !== null;
		$inputX = 0.0;
		$inputY = 0.0;
		$controlledByRider = $this->canBeControlledBy($rider);

		if($controlledByRider){
			$this->pm1eStayTime = 0;
			$inputX = $this->riderInputX;
			$inputY = abs($this->riderInputX) <= 0.001 and abs($this->riderInputY) <= 0.001 ? 1.0 : $this->riderInputY;
		}

		if(abs($inputX) <= 0.001 and abs($inputY) <= 0.001){
			$this->motionX = 0.0;
			$this->motionZ = 0.0;
		}else{
			$yaw = deg2rad($rider->yaw);
			$forwardX = -sin($yaw);
			$forwardZ = cos($yaw);
			$strafeX = cos($yaw);
			$strafeZ = sin($yaw);
			$moveX = ($forwardX * $inputY) + ($strafeX * $inputX);
			$moveZ = ($forwardZ * $inputY) + ($strafeZ * $inputX);
			$length = sqrt(($moveX * $moveX) + ($moveZ * $moveZ));

			if($length > 0.001){
				$speed = $this->getPigRidingSpeed($liquidType);
				if($inputY < 0){
					$speed *= 0.5;
				}

				$this->motionX = ($moveX / $length) * $speed;
				$this->motionZ = ($moveZ / $length) * $speed;

				$diff = abs($this->motionX) + abs($this->motionZ);
				if($diff > 0.001){
					$this->yaw = -atan2($this->motionX / $diff, $this->motionZ / $diff) * (180 / M_PI);
				}
			}
		}

		$this->normalizePm1eGroundCollisionBox();
		$this->recoverPartialBlockStandingHeight();

		if($inLiquid){
			$this->motionY = max($this->motionY, $this->getPm1eLiquidBuoyancy());
		}else{
			$jumped = false;
			if(abs($this->motionX) > 0.00001 or abs($this->motionZ) > 0.00001){
				$jumped = $this->checkPm1eJump();
			}

			if(!$jumped){
				if($this->onGround){
					$this->motionY = 0.0;
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
			$this->syncRiderSeatEntityPosition($rider);
			return true;
		}

		$this->syncRiderSeatPosition($rider, MovePlayerPacket::MODE_NORMAL, false);
		$this->syncRiderSeatEntityPosition($rider);
		return true;
	}

	protected function getPigRidingSpeed($liquidType = null) : float{
		if($liquidType === "lava"){
			return 0.02;
		}
		if($liquidType === "water"){
			return 0.05;
		}

		return 0.25;
	}

	protected function shouldAvoidPm1eCliff($block, $down) : bool{
		return !($this->getLinkedEntity() instanceof Player) && parent::shouldAvoidPm1eCliff($block, $down);
	}

	protected function syncRiderMount(Player $rider){
		$this->spawnRiderSeatEntity($rider);
		$this->sendRidePacket($rider, $rider->getId());
		$this->sendRidePacket($rider, 0);
		$this->syncRiderSeatPosition($rider, MovePlayerPacket::MODE_RESET);
	}

	protected function sendRidePacket(Player $rider, $riderId){
		$seatEntityId = $this->getRiderSeatEntityId();
		if($seatEntityId === null or $rider->getId() === null){
			return;
		}

		$this->sendRiderSeatLinkPacket($rider, $seatEntityId, $riderId, SetEntityLinkPacket::TYPE_PASSENGER);
	}

	protected function sendRiderSeatLinkPacket(Player $rider, $seatEntityId, $riderId, $type){
		if($seatEntityId === null or $rider->getId() === null){
			return;
		}

		$pk = new SetEntityLinkPacket();
		$pk->from = $seatEntityId;
		$pk->to = $riderId;
		$pk->type = $type;

		if($riderId === 0){
			$rider->dataPacket($pk);
		}elseif($this->server !== null and $this->level !== null and method_exists($this->level, "getPlayers")){
			$this->server->broadcastPacket($this->level->getPlayers(), $pk);
		}elseif($riderId === $rider->getId()){
			$rider->dataPacket($pk);
		}
	}

	protected function removeRiderSeatEntity(Player $rider, $seatEntityId){
		$this->sendRiderSeatLinkPacket($rider, $seatEntityId, $rider->getId(), SetEntityLinkPacket::TYPE_REMOVE);
		$this->sendRiderSeatLinkPacket($rider, $seatEntityId, 0, SetEntityLinkPacket::TYPE_REMOVE);

		$pk = new RemoveEntityPacket();
		$pk->eid = $seatEntityId;

		if($this->server !== null and $this->level !== null and method_exists($this->level, "getPlayers")){
			$this->server->broadcastPacket($this->level->getPlayers(), $pk);
		}else{
			$rider->dataPacket($pk);
		}
	}

	protected function spawnRiderSeatEntity(Player $rider){
		$seatEntityId = $this->getRiderSeatEntityId();
		if($seatEntityId === null){
			return;
		}

		$pk = new AddEntityPacket();
		$pk->eid = $seatEntityId;
		$pk->type = Minecart::NETWORK_ID;
		$pk->x = $this->getRiderSeatX();
		$pk->y = $this->getRiderSeatY();
		$pk->z = $this->getRiderSeatZ();
		$pk->speedX = 0;
		$pk->speedY = 0;
		$pk->speedZ = 0;
		$pk->yaw = $this->yaw;
		$pk->pitch = $this->pitch;
		$pk->metadata = [
			self::DATA_FLAGS => [self::DATA_TYPE_BYTE, 1 << self::DATA_FLAG_INVISIBLE],
			self::DATA_NAMETAG => [self::DATA_TYPE_STRING, ""],
			self::DATA_SHOW_NAMETAG => [self::DATA_TYPE_BYTE, 1],
			self::DATA_NO_AI => [self::DATA_TYPE_BYTE, 1],
		];

		if($this->server !== null and $this->level !== null and method_exists($this->level, "getPlayers")){
			$this->server->broadcastPacket($this->level->getPlayers(), $pk);
		}else{
			$rider->dataPacket($pk);
		}
	}

	protected function syncRiderSeatEntityPosition(Player $rider){
		$seatEntityId = $this->getRiderSeatEntityId();
		if($seatEntityId === null){
			return;
		}

		$pk = new MoveEntityPacket();
		$seatX = $this->getRiderSeatX();
		$seatY = $this->getRiderSeatY();
		$seatZ = $this->getRiderSeatZ();
		$pk->entities[] = [$seatEntityId, $seatX, $seatY, $seatZ, $this->yaw, $this->yaw, $this->pitch];

		if($this->server !== null and $this->level !== null and method_exists($this->level, "getPlayers")){
			$this->server->broadcastPacket($this->level->getPlayers(), $pk);
		}else{
			$rider->dataPacket($pk);
		}
	}

	protected function getRiderSeatEntityId(){
		if($this->riderSeatEntityId === null){
			$this->riderSeatEntityId = Entity::$entityCount++;
		}

		return $this->riderSeatEntityId;
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
		return $this->y + $this->getRiderSeatYOffset();
	}

	protected function getRiderSeatYOffset() : float{
		return max(0.0, $this->height - 0.15) + 0.45;
	}

	protected function getRiderSeatX() : float{
		return $this->x - (sin(deg2rad($this->yaw)) * $this->getRiderSeatForwardOffset());
	}

	protected function getRiderSeatZ() : float{
		return $this->z + (cos(deg2rad($this->yaw)) * $this->getRiderSeatForwardOffset());
	}

	protected function getRiderSeatForwardOffset() : float{
		return 0.6;
	}
	
	public function spawnTo(Player $player){
		$pk = new AddEntityPacket();
		$pk->eid = $this->getId();
		$pk->type = Pig::NETWORK_ID;
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
	
	public function getDrops(){
		if($this->isOnFire()){
			$drops = [ItemItem::get(ItemItem::COOKED_PORKCHOP, 0, mt_rand(1,2))];
		}else{
			$drops = [ItemItem::get(ItemItem::RAW_PORKCHOP, 0, mt_rand(1,2))];
		}
		if($this->isSaddled()){
			$drops[] = ItemItem::get(ItemItem::SADDLE, 0, 1);
		}
		return $drops;
	}
}
