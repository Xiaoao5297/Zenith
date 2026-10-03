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

class Mooshroom extends Animal{
	const NETWORK_ID = 16;

	public $width = 0.3;
	public $length = 0.9;
	public $height = 1.8;
	
	public function getName() : string{
		return "Mooshroom";
	}

	public function usesPm1eGroundAi() : bool{
		return true;
	}

	protected function getBreedingItemIds() : array{
		return [ItemItem::WHEAT];
	}

	public function onInteract(Player $player, ItemItem $item) : bool{
		if($this->isBaby()){
			return parent::onInteract($player, $item);
		}

		if($item->getId() === ItemItem::BOWL){
			return $this->replaceInteractionItem($player, $item, ItemItem::get(ItemItem::MUSHROOM_STEW, 0, 1));
		}

		if($item->getId() === ItemItem::BUCKET and $item->getDamage() === 0){
			return $this->replaceInteractionItem($player, $item, ItemItem::get(ItemItem::BUCKET, 1, 1));
		}

		return parent::onInteract($player, $item);
	}
	
	public function spawnTo(Player $player){
		$pk = new AddEntityPacket();
		$pk->eid = $this->getId();
		$pk->type = Mooshroom::NETWORK_ID;
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
		$drops = array(ItemItem::get(ItemItem::RED_MUSHROOM, 0, 2));
		if ($this->lastDamageCause instanceof EntityDamageByEntityEvent and $this->lastDamageCause->getEntity() instanceof Player) {
			if (\mt_rand(0, 199) < 5) {
				switch (\mt_rand(0, 2)) {
					case 0:
						$drops[] = ItemItem::get(ItemItem::BROWN_MUSHROOM, 0, 1);
						break;
					case 1:
						$drops[] = ItemItem::get(ItemItem::CARROT, 0, 1);
						break;
					case 2:
						$drops[] = ItemItem::get(ItemItem::POTATO, 0, 1);
						break;
				}
			}
		}
		return $drops;
	}
}
