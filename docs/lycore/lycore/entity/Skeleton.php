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

use lycore\network\protocol\AddEntityPacket;
use lycore\Player;
use lycore\network\protocol\MobArmorEquipmentPacket;
use lycore\network\protocol\MobEquipmentPacket;
use lycore\item\Item as ItemItem;
use lycore\entity\behavior\ShootPlayerBehavior;

class Skeleton extends Monster implements ProjectileSource{
	const NETWORK_ID = 34;
	
	public $width = 0.6;
	public $length = 0.6;
	public $height = 1.8;
	
	public $dropExp = [5, 5];
	/** @var ItemItem[] */
	protected $armor = [];
	/** @var ItemItem */
	protected $weapon;
	
	public function getName() : string{
		return "Skeleton";
	}
	
	public function initEntity(){
		$this->setMaxHealth(20);
		
		$this->addBehavior(new ShootPlayerBehavior($this, 80));
		
		parent::initEntity();

		$equipment = VanillaMobEquipment::generateSkeletonEquipment($this->server->getDifficulty());
		$this->weapon = $equipment["weapon"];
		$this->armor = $equipment["armor"];
	}

	public function usesPm1eGroundAi() : bool{
		return true;
	}

	public function getWeapon(){
		return $this->weapon instanceof ItemItem ? $this->weapon : ItemItem::get(ItemItem::BOW, 0, 1);
	}

	public function getArmorContents(){
		return count($this->armor) === 4 ? $this->armor : VanillaMobEquipment::emptyArmor();
	}

	protected function handlesLootingDrops() : bool{
		return true;
	}

	public function spawnTo(Player $player){
		$pk = new AddEntityPacket();
		$pk->eid = $this->getId();
		$pk->type = static::NETWORK_ID;
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

		$pk = new MobArmorEquipmentPacket();
		$pk->eid = $this->getId();
		$pk->slots = $this->getArmorContents();
		$player->dataPacket($pk);
	}
	
	public function getDrops(){
		$looting = $this->getLastDamageLootingLevel();
		$drops = [
			ItemItem::get(ItemItem::BONE, 0, mt_rand(0, 2)),
			ItemItem::get(ItemItem::ARROW, 0, mt_rand(0, 2))
		];
		$drops = VanillaMobEquipment::applyLootingToCommonDrops($drops, $looting);
		if(mt_rand(1, 1000) <= VanillaMobEquipment::rareDropChance($looting)){
			$drops[] = ItemItem::get(ItemItem::BOW, mt_rand(250, 384), 1);
		}
		foreach(VanillaMobEquipment::maybeDropEquipment($this->getArmorContents(), $this->getWeapon(), $looting) as $drop){
			$drops[] = $drop;
		}
		return $drops;
	}
}
