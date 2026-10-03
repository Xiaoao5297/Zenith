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

class Slime extends Mob{
	const NETWORK_ID = 37;

	public $width = 0.3;
	public $length = 0.9;
	public $height = 2.04;

	public $dropExp = [1, 4];
	
	public function getName() : string{
		return "Slime";
	}
	
	public function initEntity(){
		$this->setDataProperty(self::DATA_SLIME_SIZE, self::DATA_TYPE_BYTE, mt_rand(1,4));
		parent::initEntity();
	}

	public function usesPm1eGroundAi() : bool{
		return true;
	}

	protected function usesPm1eJumpingAi() : bool{
		return true;
	}

	protected function getPm1eJumpStrength() : float{
		if($this->getSize() >= 4){
			return 0.42;
		}elseif($this->getSize() >= 2){
			return 0.4;
		}

		return 0.38;
	}
	
	public function spawnTo(Player $player){
		$pk = new AddEntityPacket();
		$pk->eid = $this->getId();
		$pk->type = Slime::NETWORK_ID;
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
		$drops = array(ItemItem::get(ItemItem::SLIMEBALL, 0, 1));
		if ($this->lastDamageCause instanceof EntityDamageByEntityEvent and $this->lastDamageCause->getEntity() instanceof Player) {
			if (\mt_rand(0, 199) < 5) {
				switch (\mt_rand(0, 2)) {
					case 0:
						$drops[] = ItemItem::get(ItemItem::IRON_INGOT, 0, 1);
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
	
	public function getSize(){
		return $this->getDataProperty(self::DATA_SLIME_SIZE);
	}
	
	public function setSize(int $resting){
		$this->setDataProperty(self::DATA_SLIME_SIZE, self::DATA_TYPE_BYTE, $resting);
	}
}
