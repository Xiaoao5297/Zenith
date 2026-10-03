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

use lycore\entity\behavior\attackEnemyBehavior;
use lycore\network\protocol\AddEntityPacket;
use lycore\network\protocol\MobEquipmentPacket;
use lycore\Player;
use lycore\item\Item as ItemItem;

class PigZombie extends Monster{
	const NETWORK_ID = 36;

	public $width = 0.6;
	public $length = 0.6;
	public $height = 1.8;

	public $drag = 0.2;
	public $gravity = 0.3;

	public $dropExp = [5, 5];
	
	private $hurt = 10;
	/** @var ItemItem */
	protected $weapon;
	
	public function getName() : string{
		return "PigZombie";
	}
	
	public function initEntity(){
		$this->setMaxHealth(20);
		$this->addBehavior(new attackEnemyBehavior($this, [20, 21], true));
		parent::initEntity();

		$equipment = VanillaMobEquipment::generatePigZombieEquipment($this->server->getDifficulty());
		$this->weapon = $equipment["weapon"];
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

	public function getWeapon(){
		return $this->weapon instanceof ItemItem ? $this->weapon : ItemItem::get(ItemItem::GOLD_SWORD, 0, 1);
	}

	protected function handlesLootingDrops() : bool{
		return true;
	}
	
	public function spawnTo(Player $player){
		$pk = new AddEntityPacket();
		$pk->eid = $this->getId();
		$pk->type = PigZombie::NETWORK_ID;
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
		
		$pk = new MobEquipmentPacket();
		$pk->eid = $this->getId();
		$pk->item = $this->getWeapon();
		$pk->slot = 0;
		$pk->selectedSlot = 0;

		$player->dataPacket($pk);
	}

	public function getDrops(){
		$looting = $this->getLastDamageLootingLevel();
		$drops = [
			ItemItem::get(ItemItem::ROTTEN_FLESH, 0, mt_rand(0, 1)),
			ItemItem::get(ItemItem::GOLD_NUGGET, 0, mt_rand(0, 1))
		];
		$drops = VanillaMobEquipment::applyLootingToCommonDrops($drops, $looting);
		if(mt_rand(1, 1000) <= VanillaMobEquipment::rareDropChance($looting)){
			$drops[] = ItemItem::get(ItemItem::GOLD_INGOT, 0, 1);
		}
		foreach(VanillaMobEquipment::maybeDropEquipment([], $this->getWeapon(), $looting) as $drop){
			$drops[] = $drop;
		}
		return $drops;
	}
}
