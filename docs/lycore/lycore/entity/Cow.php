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
use lycore\event\entity\EntityDamageByEntityEvent;
use lycore\item\Item as ItemItem;
use lycore\entity\behavior\{findFoodBehavior, inLoveBehavior};

class Cow extends Animal{
	const NETWORK_ID = 11;

	public $width = 0.3;
	public $length = 0.9;
	public $height = 1.4;

	public $dropExp = [1, 3];
	
	public function getName() : string{
		return "Cow";
	}
	
	public function initEntity(){
		$this->setMaxHealth(8);
		
		$this->addBehavior(new inLoveBehavior($this));
		$this->addBehavior(new findFoodBehavior($this, 296));
		
		parent::initEntity();
	}

	public function usesPm1eGroundAi() : bool{
		return true;
	}

	protected function getBreedingItemIds() : array{
		return [ItemItem::WHEAT];
	}

	public function onInteract(Player $player, ItemItem $item) : bool{
		if(!$this->isBaby() and $item->getId() === ItemItem::BUCKET and $item->getDamage() === 0){
			return $this->replaceInteractionItem($player, $item, ItemItem::get(ItemItem::BUCKET, 1, 1));
		}

		return parent::onInteract($player, $item);
	}
	
	public function spawnTo(Player $player){
		$pk = new AddEntityPacket();
		$pk->eid = $this->getId();
		$pk->type = Cow::NETWORK_ID;
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
		switch (\mt_rand(0, 1)) {
					case 0:
						if($this->isOnFire()){
							$drops[] = ItemItem::get(ItemItem::COOKED_BEEF, 0, mt_rand(1,2));
						}else{
							$drops[] = ItemItem::get(ItemItem::RAW_BEEF, 0, mt_rand(1,2));
						}
						break;
					case 1:
						$drops[] = ItemItem::get(ItemItem::LEATHER, 0, mt_rand(1,2));
						break;
				}
		return $drops;
	}
}
