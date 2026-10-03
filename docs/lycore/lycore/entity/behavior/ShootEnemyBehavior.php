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

namespace lycore\entity\behavior;

use lycore\entity\Entity;
use lycore\entity\Mob;
use lycore\entity\Projectile;
use lycore\event\entity\ProjectileLaunchEvent;
use lycore\nbt\tag\CompoundTag;
use lycore\nbt\tag\DoubleTag;
use lycore\nbt\tag\FloatTag;
use lycore\nbt\tag\ListTag;

class ShootEnemyBehavior extends Behavior{

	public $speed;
	public $speedMultiplier;

	public $lookDistance = 16.0;
	public $shootDistance = 10.0;
	public $NetworkID;
	public $enemy = null;
	public $timeLeft = 0;
	public $projectileName;

	public function __construct(Mob $entity, array $NetWorkID, string $projectileName, float $speed = 0.25, float $speedMultiplier = 0.75){
		parent::__construct($entity);

		$this->speed = $speed;
		$this->speedMultiplier = $speedMultiplier;
		$this->NetworkID = $NetWorkID;
		$this->projectileName = $projectileName;
	}

	public function getName() : string{
		return "投掷类敌对实体攻击";
	}

	public function shouldStart() : bool{
		$entities = $this->entity->level->getEntities();

		$find = false;
		$MinDistance = 9999;
		foreach($entities as $entity){
			if(in_array($entity::NETWORK_ID, $this->NetworkID) and $this->entity->distance($entity) < $this->lookDistance){
				if($this->entity->distance($entity) < $MinDistance){
					$this->enemy = $entity;
					$MinDistance = $this->entity->distance($entity);
					$find = true;
				}
			}
		}

		return $find;
	}

	public function canContinue() : bool{
		if($this->enemy instanceof Entity and $this->enemy->isAlive()){
			return $this->entity->distance($this->enemy) < $this->lookDistance;
		}

		return false;
	}

	public function onTick(){
		if(!($this->enemy instanceof Entity) or !$this->enemy->isAlive()){
			return;
		}

		$distance = $this->entity->distance($this->enemy);
		$entity = $this->entity;
		$entity->setPm1eFollowTarget($this->enemy);
		$entity->setPm1eMoveMultiplier($distance < 4 ? -$this->speedMultiplier : $this->speedMultiplier);
		$entity->setPm1eStayTime(0);

		if($distance <= $this->shootDistance and $this->timeLeft <= 0){
			$this->shootProjectile();
			$this->timeLeft = 40;
		}elseif($this->timeLeft > 0){
			--$this->timeLeft;
		}

		$this->swimming();
	}

	public function onEnd(){
		$this->entity->setPm1eFollowTarget(null);
		$this->entity->setPm1eMoveMultiplier(1.0);
	}

	protected function shootProjectile(){
		$entity = $this->entity;
		$target = $this->enemy;
		$originY = $entity->y + $entity->getEyeHeight();
		$targetY = $target->y + $target->getEyeHeight();
		$motionX = $target->x - $entity->x;
		$motionY = $targetY - $originY;
		$motionZ = $target->z - $entity->z;
		$length = sqrt(($motionX * $motionX) + ($motionY * $motionY) + ($motionZ * $motionZ));
		if($length <= 0){
			return;
		}

		$motionX /= $length;
		$motionY /= $length;
		$motionZ /= $length;
		$yaw = atan2($motionX, $motionZ) * 180 / M_PI;
		$pitch = atan2($motionY, sqrt(($motionX * $motionX) + ($motionZ * $motionZ))) * 180 / M_PI;

		$nbt = new CompoundTag("", [
			"Pos" => new ListTag("Pos", [
				new DoubleTag("", $entity->x),
				new DoubleTag("", $originY),
				new DoubleTag("", $entity->z)
			]),
			"Motion" => new ListTag("Motion", [
				new DoubleTag("", $motionX),
				new DoubleTag("", $motionY),
				new DoubleTag("", $motionZ)
			]),
			"Rotation" => new ListTag("Rotation", [
				new FloatTag("", $yaw),
				new FloatTag("", $pitch)
			])
		]);

		$projectile = $this->createProjectile($nbt);
		if($projectile instanceof Projectile){
			$projectile->setMotion($projectile->getMotion()->multiply(1.5));
			$entity->level->getServer()->getPluginManager()->callEvent($ev = new ProjectileLaunchEvent($projectile));
			if($ev->isCancelled()){
				$projectile->close();
			}else{
				$projectile->spawnToAll();
			}
		}
	}

	protected function createProjectile(CompoundTag $nbt){
		return Entity::createEntity($this->projectileName, $this->entity->chunk, $nbt, $this->entity);
	}
}
