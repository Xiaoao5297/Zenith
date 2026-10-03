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

use lycore\nbt\tag\ByteTag;
use lycore\network\protocol\AddEntityPacket;
use lycore\level\format\FullChunk;
use lycore\nbt\tag\CompoundTag;
use lycore\item\Item as ItemItem;
use lycore\item\Lead;
use lycore\math\Vector3;
use lycore\Player;
class Ocelot extends Animal{
	const NETWORK_ID = 22;

	const TYPE_WILD = 0;
	const TYPE_TUXEDO = 1;
	const TYPE_TABBY = 2;
	const TYPE_SIAMESE = 3;

	public $width = 0.312;
	public $length = 2.188;
	public $height = 0.75;//0.75 为了适配Default AI

	public $dropExp = [1, 3];

	const PM1E_TEMPT_RADIUS_SQUARED = 256;
	const PM1E_SCARE_RADIUS_SQUARED = 36;
	const PM1E_WALK_MULTIPLIER = 2.0; // PowerNukkitX cat speed 0.3 / PM1E base 0.15
	const PM1E_TEMPT_MULTIPLIER = 10 / 3; // PowerNukkitX tempt speed 0.5 / PM1E base 0.15
	const PM1E_SCARED_MULTIPLIER = 3.6; // PowerNukkitX scare speed is default speed * 1.8
	
	public function getName() : string{
		return "Ocelot";
	}

	public function __construct(FullChunk $chunk, CompoundTag $nbt){
		if(!isset($nbt->CatType)){
			$nbt->CatType = new ByteTag("CatType", mt_rand(0, 3));
		}
		parent::__construct($chunk, $nbt);

		$this->setDataProperty(self::DATA_CAT_TYPE, self::DATA_TYPE_BYTE, $this->getCatType());
	}

	public function usesPm1eGroundAi() : bool{
		return true;
	}

	public function onUpdate($currentTick){
		if($this->closed){
			return false;
		}

		if(!$this->closed and $this->isAlive()){
			$this->tickPm1eOcelotLogic();
		}

		$hasUpdate = parent::onUpdate($currentTick);

		return $hasUpdate;
	}

	protected function tickPm1eOcelotLogic(){
		$fishHolder = $this->findPm1eFishHoldingPlayer();
		if($fishHolder instanceof Player){
			if($this->doesPm1ePlayerScare($fishHolder)){
				$this->setPm1eFollowTarget(null);
				$this->setPm1eMoveTarget($this->getPm1eFleeTarget($fishHolder, 12.0));
				$this->setPm1eMoveMultiplier(self::PM1E_SCARED_MULTIPLIER);
				$this->setPm1eStayTime(0);
				return;
			}

			$this->setPm1eFollowTarget($fishHolder);
			$this->setPm1eMoveMultiplier(self::PM1E_TEMPT_MULTIPLIER);
			$this->setPm1eStayTime(0);
			return;
		}

		if($this->getPm1eFollowTarget() instanceof Player){
			$this->setPm1eFollowTarget(null);
		}
		$this->setPm1eMoveMultiplier(self::PM1E_WALK_MULTIPLIER);
	}

	protected function findPm1eFishHoldingPlayer(){
		$level = $this->getLevel();
		if($level === null || !method_exists($level, "getPlayers")){
			return null;
		}

		$closest = null;
		$bestDistance = self::PM1E_TEMPT_RADIUS_SQUARED;
		foreach($level->getPlayers() as $player){
			if(!$this->canPm1ePlayerHoldFish($player)){
				continue;
			}
			$distance = $this->distanceSquared($player);
			if($distance <= $bestDistance){
				$bestDistance = $distance;
				$closest = $player;
			}
		}

		return $closest;
	}

	protected function canPm1ePlayerHoldFish(Player $player) : bool{
		if($player->closed || !$player->isAlive()){
			return false;
		}
		if(method_exists($player, "isSurvival") && method_exists($player, "isAdventure") && !$player->isSurvival() && !$player->isAdventure()){
			return false;
		}

		return $this->isPm1eTemptFish($player->getInventory()->getItemInHand());
	}

	protected function doesPm1ePlayerScare(Player $player) : bool{
		if($this->distanceSquared($player) > self::PM1E_SCARE_RADIUS_SQUARED){
			return false;
		}

		return $player->isSprinting() || !$player->isSneaking();
	}

	protected function getPm1eFleeTarget(Player $player, float $distance) : Vector3{
		$dx = $this->x - $player->x;
		$dz = $this->z - $player->z;
		$length = sqrt(($dx * $dx) + ($dz * $dz));
		if($length < 0.0001){
			$dx = 1.0;
			$dz = 0.0;
			$length = 1.0;
		}

		return new Vector3(
			$this->x + ($dx / $length) * $distance,
			$this->y,
			$this->z + ($dz / $length) * $distance
		);
	}

	protected function isPm1eTemptFish(ItemItem $item) : bool{
		return $item->getId() === ItemItem::RAW_SALMON || ($item->getId() === ItemItem::RAW_FISH && ($item->getDamage() === 0 || $item->getDamage() === 1));
	}

	public function onInteract(Player $player, ItemItem $item) : bool{
		if($item instanceof Lead){
			return $item->leashEntity($player, $this);
		}

		if(!$this->isPm1eTemptFish($item)){
			return false;
		}

		if($this->getHealth() < $this->getMaxHealth()){
			$this->setHealth(min($this->getMaxHealth(), $this->getHealth() + 2));
		}

		if($player->isSurvival()){
			$item->setCount($item->getCount() - 1);
			$player->getInventory()->setItemInHand($item->getCount() > 0 ? $item : ItemItem::get(ItemItem::AIR, 0, 0));
		}

		return true;
	}

	public function setCatType(int $type){
		$this->namedtag->CatType = new ByteTag("CatType", $type);
	}

	public function getCatType() : int{
		return (int) $this->namedtag["CatType"];
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

		parent::spawnTo($player);
	}
}
