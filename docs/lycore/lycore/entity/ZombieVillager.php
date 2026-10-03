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

use lycore\item\Item as ItemItem;
use lycore\level\format\FullChunk;
use lycore\level\sound\ZombieHealSound;
use lycore\nbt\tag\ByteTag;
use lycore\nbt\tag\CompoundTag;
use lycore\nbt\tag\DoubleTag;
use lycore\nbt\tag\FloatTag;
use lycore\nbt\tag\IntTag;
use lycore\nbt\tag\ListTag;

class ZombieVillager extends Zombie{
	const NETWORK_ID = 44;
	const CURE_TIME_MIN = 600;
	const CURE_TIME_MAX = 1800;

	public $width = 1.031;
	public $length = 0.891;
	public $height = 2.125;

	public function initEntity(){
		$this->setMaxHealth(20);
		parent::initEntity();
		if(!isset($this->namedtag->Profession)){
			$this->setProfession(mt_rand(0, 4));
		}
		if(isset($this->namedtag->CureTime)){
			$this->setCureTime((int) $this->namedtag["CureTime"]);
		}
		$this->setBaby($this->isBaby());
		$this->setDataProperty(self::DATA_PROFESSION_ID, self::DATA_TYPE_BYTE, $this->getProfession());
	}

	public function getName() : string{
		return "Zombie Villager";
	}

	public function entityBaseTick($tickDiff = 1){
		$hasUpdate = parent::entityBaseTick($tickDiff);

		if($this->isAlive() and $this->isCuring()){
			$this->setCureTime($this->getCureTime() - $tickDiff);
			if($this->getCureTime() <= 0){
				$this->convertToVillager();
				return true;
			}
			$hasUpdate = true;
		}

		return $hasUpdate;
	}

	public function saveNBT(){
		parent::saveNBT();
		$this->namedtag->Profession = new ByteTag("Profession", $this->getProfession());
		if($this->isCuring()){
			$this->namedtag->CureTime = new IntTag("CureTime", $this->getCureTime());
		}else{
			unset($this->namedtag->CureTime);
		}
	}

	public function setProfession(int $profession){
		$this->namedtag->Profession = new ByteTag("Profession", min(4, max(0, $profession)));
		$this->setDataProperty(self::DATA_PROFESSION_ID, self::DATA_TYPE_BYTE, $this->getProfession());
	}

	public function getProfession() : int{
		$profession = isset($this->namedtag->Profession) ? (int) $this->namedtag["Profession"] : 0;
		return min(4, max(0, $profession));
	}

	public function canStartCure() : bool{
		return !$this->isCuring() and $this->hasEffect(Effect::WEAKNESS);
	}

	public function attemptCureWith(ItemItem $item) : bool{
		if($item->getId() !== ItemItem::GOLDEN_APPLE or !$this->canStartCure()){
			return false;
		}

		$this->startCure();
		return true;
	}

	public function startCure(int $time = null){
		$this->setCureTime($time === null ? mt_rand(self::CURE_TIME_MIN, self::CURE_TIME_MAX) : $time);
		$this->getLevel()->addSound(new ZombieHealSound($this));
	}

	public function isCuring() : bool{
		return $this->getCureTime() > 0;
	}

	public function getCureTime() : int{
		return isset($this->namedtag->CureTime) ? (int) $this->namedtag["CureTime"] : 0;
	}

	protected function setCureTime(int $time){
		if($time > 0){
			$this->namedtag->CureTime = new IntTag("CureTime", $time);
		}else{
			unset($this->namedtag->CureTime);
		}
	}

	protected function convertToVillager(){
		$chunk = $this->getLevel()->getChunk($this->x >> 4, $this->z >> 4, true);
		if($chunk === null){
			return;
		}

		$nbt = new CompoundTag("", [
			"Pos" => new ListTag("Pos", [
				new DoubleTag(0, $this->x),
				new DoubleTag(1, $this->y),
				new DoubleTag(2, $this->z)
			]),
			"Motion" => new ListTag("Motion", [
				new DoubleTag(0, $this->motionX),
				new DoubleTag(1, $this->motionY),
				new DoubleTag(2, $this->motionZ)
			]),
			"Rotation" => new ListTag("Rotation", [
				new FloatTag(0, $this->yaw),
				new FloatTag(1, $this->pitch)
			]),
			"Profession" => new ByteTag("Profession", $this->getProfession()),
			"IsBaby" => new ByteTag("IsBaby", $this->isBaby() ? 1 : 0)
		]);
		if(isset($this->namedtag->Offers)){
			$nbt->Offers = clone $this->namedtag->Offers;
		}

		$villager = new Villager($chunk, $nbt);
		$villager->setHealth($villager->getMaxHealth());
		$villager->spawnToAll();

		$this->close();
	}
}
