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
use lycore\entity\Rabbit;
use lycore\entity\Sheep;
use lycore\entity\Skeleton;
use lycore\entity\VanillaMobEquipment;
use lycore\entity\Wolf;
use lycore\event\entity\EntityDamageByEntityEvent;
use lycore\event\entity\EntityDamageEvent;
use lycore\item\Tool;

class WolfAttackBehavior extends attackEnemyBehavior{
	const PNX_ATTACK_SPEED = 0.7;
	const PNX_ATTACK_COOLDOWN = 20;
	const ATTACK_RANGE = 1.6;
	const ATTACK_SPEED_MULTIPLIER = 2.3333333333;
	const POUNCE_MIN_DISTANCE = 2.0;
	const POUNCE_MAX_DISTANCE = 4.0;

	public function __construct(Mob $entity, array $NetWorkID = [], bool $attackPlayer = false, float $speed = self::PNX_ATTACK_SPEED, float $speedMultiplier = self::ATTACK_SPEED_MULTIPLIER){
		parent::__construct($entity, $NetWorkID, $attackPlayer, $speed, $speedMultiplier);
		$this->lookDistance = 33.0;
	}

	public function getName() : string{
		return "Wolf attack";
	}

	public function shouldStart() : bool{
		if($this->entity instanceof Wolf && $this->entity->isSitting()){
			return false;
		}

		if(parent::shouldStart()){
			return true;
		}

		if($this->entity instanceof Wolf && $this->entity->isTamed()){
			return false;
		}

		return $this->findSuitableWolfTarget();
	}

	public function canContinue() : bool{
		if($this->entity instanceof Wolf && $this->entity->isSitting()){
			return false;
		}

		return parent::canContinue();
	}

	public function onTick(){
		if($this->timeLeft > 0){
			--$this->timeLeft;
		}

		if(!($this->enemy instanceof Entity) or !$this->enemy->isAlive()){
			return;
		}

		$entity = $this->entity;
		if(method_exists($entity, "setAngry")){
			$entity->setAngry(true);
		}

		$distance = $entity->distance($this->enemy);
		if($distance > self::ATTACK_RANGE){
			$entity->setPm1eFollowTarget($this->enemy);
			$entity->setPm1eMoveMultiplier($this->speedMultiplier);
			$entity->setPm1eStayTime(0);

			if(
				$distance >= self::POUNCE_MIN_DISTANCE &&
				$distance <= self::POUNCE_MAX_DISTANCE &&
				method_exists($entity, "requestPm1eAttackLeap")
			){
				$entity->requestPm1eAttackLeap($this->enemy);
			}
		}elseif($this->timeLeft <= 0){
			$damage = $entity->getHurt();
			$knockBack = 0.4;
			if(method_exists($entity, "getWeapon")){
				$damage += max(0, VanillaMobEquipment::getWeaponBaseDamage($entity->getWeapon()) - 1);
				$damage += Tool::getWeaponEnchantmentDamageBonus($entity, $this->enemy);
				$knockBack = Tool::getWeaponKnockBackStrength($knockBack, $entity);
			}
			$this->enemy->attack($damage, new EntityDamageByEntityEvent($entity, $this->enemy, EntityDamageEvent::CAUSE_ENTITY_ATTACK, $damage, $knockBack));
			$this->timeLeft = self::PNX_ATTACK_COOLDOWN;
		}

		$this->swimming();
	}

	private function findSuitableWolfTarget() : bool{
		$level = $this->entity->getLevel();
		if($level === null){
			return false;
		}

		$closest = null;
		$closestDistance = $this->lookDistance;
		foreach($level->getEntities() as $entity){
			if(!($entity instanceof Entity) || $entity === $this->entity || !$this->isSuitableWolfTarget($entity)){
				continue;
			}
			if($entity->closed || !$entity->isAlive()){
				continue;
			}

			$distance = $this->entity->distance($entity);
			if($distance < $closestDistance){
				$closest = $entity;
				$closestDistance = $distance;
			}
		}

		if($closest instanceof Entity){
			$this->enemy = $closest;
			return true;
		}

		return false;
	}

	private function isSuitableWolfTarget(Entity $entity) : bool{
		return $entity instanceof Skeleton || $entity instanceof Rabbit || $entity instanceof Sheep;
	}

	public function onEnd(){
		$enemy = $this->enemy;
		parent::onEnd();
		if($enemy instanceof Entity && method_exists($this->entity, "getPm1eRetaliationTarget") && $this->entity->getPm1eRetaliationTarget() === $enemy){
			$this->entity->setPm1eRetaliationTarget(null);
		}
		$this->enemy = null;
		if(method_exists($this->entity, "setAngry")){
			$this->entity->setAngry(false);
		}
	}
}
