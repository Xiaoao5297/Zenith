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
use lycore\item\Item as ItemItem;
use lycore\network\Network;
use lycore\network\protocol\AddEntityPacket;
use lycore\network\protocol\MobArmorEquipmentPacket;
use lycore\network\protocol\MobEquipmentPacket;
use lycore\Player;
use lycore\event\entity\EntityDamageByEntityEvent;
use lycore\entity\behavior\attackEnemyBehavior;

class Zombie extends Monster implements Ageable{
	const NETWORK_ID = 32;
	const BABY_ZOMBIE_PM1E_BASE_SPEED = 0.35;

	public $width = 0.6;
	public $length = 0.6;
	public $height = 1.8;
	
	public $dropExp = [5, 5];
	
	private $hurt = 8;
	/** @var ItemItem[] */
	protected $armor = [];
	/** @var ItemItem */
	protected $weapon;
	
	public function getName() : string{
		return "Zombie";
	}
	
	public function initEntity(){
		$this->setMaxHealth(20);
		
		if(!isset($this->namedtag->IsBaby)){
			$this->namedtag->IsBaby = new ByteTag("IsBaby", 1);
			$this->setBaby(false);
		}
		$this->setBaby($this->isBaby());
		
		$this->addBehavior(new attackEnemyBehavior($this, [15, 20, 21], true));
		
		parent::initEntity();

		$equipment = VanillaMobEquipment::generateZombieEquipment($this->server->getDifficulty());
		$this->weapon = $equipment["weapon"];
		$this->armor = $equipment["armor"];
	}

	public function usesPm1eGroundAi() : bool{
		return true;
	}

	protected function getPm1eBaseSpeed($liquidType = null) : float{
		$speed = parent::getPm1eBaseSpeed($liquidType);
		return $this->isBaby() && $liquidType === null ? self::BABY_ZOMBIE_PM1E_BASE_SPEED : $speed;
	}
	
	public function getHurt(){
		return $this->hurt;
	}
	
	public function setHurt($hurt){
		$this->hurt = $hurt;
	}

	public function getWeapon(){
		return $this->weapon instanceof ItemItem ? $this->weapon : ItemItem::get(ItemItem::AIR, 0, 0);
	}

	public function getArmorContents(){
		return count($this->armor) === 4 ? $this->armor : VanillaMobEquipment::emptyArmor();
	}

	public function setMobEquipment(array $armor, ItemItem $weapon = null){
		$normalizedArmor = VanillaMobEquipment::emptyArmor();
		for($slot = 0; $slot < 4; ++$slot){
			if(isset($armor[$slot]) && $armor[$slot] instanceof ItemItem){
				$normalizedArmor[$slot] = $armor[$slot];
			}
		}

		$this->armor = $normalizedArmor;
		$this->weapon = $weapon instanceof ItemItem ? $weapon : ItemItem::get(ItemItem::AIR, 0, 0);
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

		$this->sendMobEquipment($player);
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

	public function getDrops(){
		$rareDrops = [];
		$looting = $this->getLastDamageLootingLevel();
		if(mt_rand(1, 1000) <= VanillaMobEquipment::rareDropChance($looting)){
			switch(mt_rand(0, 2)){
				case 0:
					$rareDrops[] = ItemItem::get(ItemItem::IRON_INGOT, 0, 1);
					break;
				case 1:
					$rareDrops[] = ItemItem::get(ItemItem::CARROT, 0, 1);
					break;
				case 2:
					$rareDrops[] = ItemItem::get(ItemItem::POTATO, 0, 1);
					break;
			}
		}
		$drops = [ItemItem::get(ItemItem::ROTTEN_FLESH, 0, mt_rand(0, 2))];
		$drops = VanillaMobEquipment::applyLootingToCommonDrops($drops, $looting);
		foreach($rareDrops as $drop){
			$drops[] = $drop;
		}
		foreach(VanillaMobEquipment::maybeDropEquipment($this->getArmorContents(), $this->getWeapon(), $looting) as $drop){
			$drops[] = $drop;
		}
		return $drops;
	}
	
	public function isBaby(){
		return $this->namedtag["IsBaby"] == 0 ? false : true;
	}
	
	public function setBaby(bool $resting){
		$this->setDataProperty(self::DATA_ZOMBIE_IS_BABY, self::DATA_TYPE_BYTE, $resting ? 1 : 0);
		$this->namedtag->IsBaby = new ByteTag("IsBaby", $resting ? 1 : 0);
	}
}
