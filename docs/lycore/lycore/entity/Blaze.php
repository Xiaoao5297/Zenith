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

use lycore\event\entity\ProjectileLaunchEvent;
use lycore\item\Item as ItemItem;
use lycore\nbt\tag\CompoundTag;
use lycore\nbt\tag\DoubleTag;
use lycore\nbt\tag\FloatTag;
use lycore\nbt\tag\ListTag;
use lycore\network\protocol\AddEntityPacket;
use lycore\Player;

class Blaze extends FlyingAnimal{
	const NETWORK_ID = 43;

	public $width = 0.3;
	public $length = 0.9;
	public $height = 1.8;

	public $dropExp = [10, 10];
	private $attackDelay = 0;
	private $fireballBurst = 0;
	
	public function getName() : string{
		return "Blaze";
	}

	public function initEntity(){
		$this->setMaxHealth(20);
		parent::initEntity();
		$this->fireProof = true;
	}

	public function onUpdate($currentTick){
		$hasUpdate = parent::onUpdate($currentTick);
		if(!$this->closed and $this->isAlive()){
			if($this->attackDelay < 200){
				++$this->attackDelay;
			}
			$this->attackNearestPlayer();
		}

		return $hasUpdate;
	}

	protected function attackNearestPlayer(){
		$target = null;
		$bestDistance = 2304;

		foreach($this->getLevel()->getPlayers() as $player){
			if(method_exists($player, "isAlive") and !$player->isAlive()){
				continue;
			}
			if(method_exists($player, "isSurvival") and method_exists($player, "isAdventure")){
				if(!$player->isSurvival() and !$player->isAdventure()){
					continue;
				}
			}
			if(property_exists($player, "closed") and $player->closed){
				continue;
			}
			$distance = $this->distanceSquared($player);
			if($distance <= $bestDistance){
				$bestDistance = $distance;
				$target = $player;
			}
		}
		if(method_exists($this->getLevel(), "getEntities")){
			foreach($this->getLevel()->getEntities() as $entity){
				if(!($entity instanceof Bot) or $entity === $this or $entity->closed or !$entity->isAlive()){
					continue;
				}
				$distance = $this->distanceSquared($entity);
				if($distance <= $bestDistance){
					$bestDistance = $distance;
					$target = $entity;
				}
			}
		}

		if($target instanceof Entity){
			$this->setPm1eFlyFollowTarget($target);
			$this->attackEntity($target);
		}else{
			$this->setPm1eFlyFollowTarget(null);
		}
	}

	public function attackEntity(Entity $player){
		if($this->attackDelay == 60 and $this->fireballBurst == 0){
			$this->setDataFlag(self::DATA_FLAGS, self::DATA_FLAG_ACTION, true);
		}

		if((($this->fireballBurst > 0 and $this->fireballBurst < 3 and $this->attackDelay > 5) or ($this->attackDelay > 120 and mt_rand(1, 32) < 4)) and $this->distanceSquared($player) <= 256){
			$this->attackDelay = 0;
			++$this->fireballBurst;

			if($this->fireballBurst >= 3){
				$this->fireballBurst = 0;
				$this->setDataFlag(self::DATA_FLAGS, self::DATA_FLAG_ACTION, false);
			}

			$nbt = new CompoundTag("", [
				"Pos" => new ListTag("Pos", [
					new DoubleTag("", $this->x),
					new DoubleTag("", $this->y + $this->getEyeHeight()),
					new DoubleTag("", $this->z)
				]),
				"Motion" => new ListTag("Motion", [
					new DoubleTag("", 0),
					new DoubleTag("", 0),
					new DoubleTag("", 0)
				]),
				"Rotation" => new ListTag("Rotation", [
					new FloatTag("", $this->yaw),
					new FloatTag("", $this->pitch)
				]),
			]);
			$fireball = Entity::createEntity("SmallFireball", $this->chunk, $nbt, $this);
			if($fireball instanceof SmallFireball){
				$fireball->setMotion($player->add(0, 0.3, 0)->subtract($this)->normalize()->multiply(1.2));
				$ev = new ProjectileLaunchEvent($fireball);
				$this->server->getPluginManager()->callEvent($ev);
				if($ev->isCancelled()){
					$fireball->close();
				}else{
					$fireball->spawnToAll();
				}
			}
		}
	}

	public function getDrops(){
		return [ItemItem::get(ItemItem::BLAZE_ROD, 0, mt_rand(0, 1))];
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
