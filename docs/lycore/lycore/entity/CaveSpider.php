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

use lycore\network\Network;
use lycore\network\protocol\AddEntityPacket;
use lycore\Player;
use lycore\event\entity\EntityDamageByEntityEvent;
use lycore\event\entity\EntityDamageEvent;
use lycore\item\Item as ItemItem;
use lycore\entity\behavior\attackEnemyBehavior;

class CaveSpider extends Monster{
	const NETWORK_ID = 40;
	const DAYLIGHT_NEUTRAL_LIGHT = 12;

	public $width = 1;
	public $length = 1;
	public $height = 0.5;

	public $dropExp = [5, 5];
	private $hurt = 4;

	public function getName() : string{
		return "Cave Spider";
	}
	
	public function initEntity(){
		$this->setMaxHealth(12);
		
		$this->addBehavior(new attackEnemyBehavior($this, [20, 21], true));
		
		parent::initEntity();
	}

	public function usesPm1eGroundAi() : bool{
		return true;
	}

	public function isPm1eNeutralToPlayers() : bool{
		return $this->isInBrightDaylight(self::DAYLIGHT_NEUTRAL_LIGHT);
	}

	public function attack($damage, EntityDamageEvent $source){
		$result = parent::attack($damage, $source);
		if($result and !$source->isCancelled() and $source instanceof EntityDamageByEntityEvent){
			$damager = $source->getDamager();
			if($damager instanceof Player){
				$this->setPm1eRetaliationTarget($damager);
			}
		}

		return $result;
	}
	
	public function getHurt(){
		return $this->hurt;
	}
	
	public function spawnTo(Player $player){
		$pk = new AddEntityPacket();
		$pk->eid = $this->getId();
		$pk->type = CaveSpider::NETWORK_ID;
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
		$drops = [];
		if(mt_rand(0, 2) < 1){
			$drops[] = ItemItem::get(ItemItem::SPIDER_EYE, 0, 1);
		}else{
			$drops[] = ItemItem::get(ItemItem::STRING, 0, 1);
		}
		return $drops;
	}
}
