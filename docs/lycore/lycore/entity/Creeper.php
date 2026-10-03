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

use lycore\event\entity\CreeperPowerEvent;
use lycore\item\Item as ItemItem;
use lycore\nbt\tag\ByteTag;
use lycore\network\protocol\AddEntityPacket;
use lycore\network\protocol\EntityEventPacket;
use lycore\Player;
use lycore\level\Explosion;
use lycore\level\Position;
use lycore\math\Vector3;

class Creeper extends Monster{
	const NETWORK_ID = 33;

	const DATA_SWELL = 16;
	const DATA_POWERED = 19;
	
	public $width = 0.6;
    public $length = 0.6;
    public $height = 1.8;

	public $dropExp = [5, 5];
	private $pm1eSeenTarget = null;
	private $pm1eBombTime = 0;
	private $forceExploding = false;
	
	public function getName() : string{
		return "Creeper";
	}

	public function initEntity(){
		$this->setMaxHealth(20);
		parent::initEntity();
		if(!isset($this->namedtag->powered)){
			$this->setPowered(false);
		}
	}

	public function setPowered(bool $powered, Lightning $lightning = null){
		if($lightning != null){
			$powered = true;
			$cause = CreeperPowerEvent::CAUSE_LIGHTNING;
		}else $cause = $powered ? CreeperPowerEvent::CAUSE_SET_ON : CreeperPowerEvent::CAUSE_SET_OFF;

		$this->getLevel()->getServer()->getPluginManager()->callEvent($ev = new CreeperPowerEvent($this, $lightning, $cause));

		if(!$ev->isCancelled()){
			$this->namedtag->powered = new ByteTag("powered", $powered ? 1 : 0);
			$this->setDataProperty(self::DATA_POWERED, self::DATA_TYPE_BYTE, $powered ? 1 : 0);
		}
	}

	public function isPowered() : bool{
		return $this->namedtag["powered"] == 0 ? false : true;
	}
	
	public function setSwelled(bool $swelled){
		$this->setDataProperty(self::DATA_CREPPER_SWELL_DIRECTION, self::DATA_TYPE_BYTE, $swelled ? 1 : 0);
		if(!$swelled){
			$this->forceExploding = false;
			$this->setDataProperty(self::DATA_CREPPER_SWELL, self::DATA_TYPE_BYTE, 0);
			$this->setDataProperty(self::DATA_CREPPER_SWELL_2, self::DATA_TYPE_BYTE, 0);
		}
	}
	
	public function isSwelled(){
		return $this->getDataProperty(self::DATA_CREPPER_SWELL_DIRECTION) == 1;
	}

	public function onInteract(Player $player, ItemItem $item) : bool{
		if($item->getId() !== ItemItem::FLINT_STEEL){
			return false;
		}

		$this->forceExploding = true;
		$this->setSwelled(true);
		if($player->isSurvival() and method_exists($item, "useOn")){
			$item->useOn($this, 2);
			$player->getInventory()->setItemInHand($item);
		}

		return true;
	}
	
	public function onUpdate($currentTick){
		if($this->closed){
			return false;
		}
		
		$hasUpdate = parent::onUpdate($currentTick);
		if(!$this->closed and $this->isAlive()){
			$this->tickPm1eCreeperLogic();
		}
		
		if($this->isSwelled()){ //爆炸动画
			$num = $this->getDataProperty(self::DATA_CREPPER_SWELL);
			if($num < 30){
				$num++;
				$this->setDataProperty(self::DATA_CREPPER_SWELL, self::DATA_TYPE_BYTE, $num);
				$this->setDataProperty(self::DATA_CREPPER_SWELL_2, self::DATA_TYPE_BYTE, $num);
			}else{
				$this->setSwelled(false);
                $e = new Explosion(new Position($this->getX() , $this->getY() , $this->getZ() , $this->getLevel()) , 5, $this);
                if ($this->getLevel()->getServer()->aiConfig["creeperexplode"] and !$this->getLevel()->getServer()->isWorldCreeperBlockDamageDisabled($this->getLevel())) $e->explode();
                else $e->explodeB();
				$this->getLevel()->removeEntity($this);
			}
		}
		return $hasUpdate;
	}

	public function usesPm1eGroundAi() : bool{
		return true;
	}

	protected function tickPm1eCreeperLogic(){
		if($this->forceExploding and $this->isSwelled()){
			$this->setPm1eFollowTarget(null);
			$this->setPm1eMoveMultiplier(1.0);
			return;
		}

		$cat = $this->findPm1eCatThreat();
		if($cat instanceof Ocelot){
			$this->pm1eSeenTarget = $cat->getId();
			$this->setPm1eFollowTarget($cat);
			$this->setPm1eMoveMultiplier(-1.5);
			$this->setPm1eStayTime(0);
			$this->resetPm1eCreeperCharge();
			return;
		}

		$target = $this->findPm1eCreeperTarget();
		if($target instanceof Vector3){
			$this->pm1eSeenTarget = $target->getId();
			$this->setPm1eFollowTarget($target);
			$this->setPm1eMoveMultiplier(0.4);
			$distance = $this->distanceSquared($target);
			if($distance <= 16){
				if(!$this->isSwelled()){
					$this->setSwelled(true);
				}
				++$this->pm1eBombTime;
				if($distance <= 1){
					$this->setPm1eStayTime(10);
				}
			}else{
				$this->resetPm1eCreeperCharge();
			}
		}else{
			$this->setPm1eFollowTarget(null);
			$this->setPm1eMoveMultiplier(1.0);
			$this->resetPm1eCreeperCharge();
		}
	}

	protected function findPm1eCatThreat(float $maxDistanceSquared = 16.0){
		$level = $this->getLevel();
		if($level === null || !method_exists($level, "getEntities")){
			return null;
		}

		$closest = null;
		$bestDistance = $maxDistanceSquared;
		foreach($level->getEntities() as $entity){
			if(!($entity instanceof Ocelot) || $entity === $this || $entity->closed || !$entity->isAlive()){
				continue;
			}

			$distance = $this->distanceSquared($entity);
			if($distance <= $bestDistance){
				$bestDistance = $distance;
				$closest = $entity;
			}
		}

		return $closest;
	}

	protected function findPm1eCreeperTarget(){
		return $this->findPm1eMobTarget(256);
	}

	protected function resetPm1eCreeperCharge(){
		$this->pm1eBombTime = 0;
		if($this->isSwelled()){
			$this->setSwelled(false);
		}
	}

	public function spawnTo(Player $player){
		$pk = new AddEntityPacket();
		$pk->eid = $this->getId();
		$pk->type = Creeper::NETWORK_ID;
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
        $drops[] = ItemItem::get(ItemItem::GUNPOWDER, 0, 1);
        return $drops;
    }
}
