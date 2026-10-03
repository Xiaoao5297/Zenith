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
use lycore\entity\Bot;
use lycore\Player;

use lycore\event\entity\EntityDamageEvent;
use lycore\event\entity\EntityDamageByEntityEvent;
use lycore\entity\VanillaMobEquipment;
use lycore\item\Tool;

class attackEnemyBehavior extends Behavior{

    public $speed;
    public $speedMultiplier;
	
	public $lookDistance = 16.0;
	public $NetworkID;
	public $enemy = null;
	public $timeLeft = 0;
	public $attackPlayer = false;

    public function __construct(Mob $entity, array $NetWorkID, bool $attackPlayer = false, float $speed = 0.25, float $speedMultiplier = 0.75){
        parent::__construct($entity);

        $this->speed = $speed;
        $this->speedMultiplier = $speedMultiplier;
		$this->NetworkID = $NetWorkID;
		$this->attackPlayer = $attackPlayer;
    }

    public function getName() : string{
        return "一般敌对实体攻击";
    }

	public function shouldStart() : bool{
		if($this->canUseCurrentTarget()){
			return true;
		}

        $entities = $this->entity->level->getEntities();

        $find = false;
		$MinDistance = 9999;
		foreach($entities as $entity){
			if(!($entity instanceof Entity) or $entity === $this->entity){
				continue;
			}
			if(in_array($entity::NETWORK_ID, $this->NetworkID) or ($this->attackPlayer and $entity instanceof Bot)){
				$this->trySelectEnemy($entity, $MinDistance, $find);
			}
        }
		if($this->attackPlayer){
			 $players = $this->entity->level->getPlayers();
			foreach($players as $p){
				$this->trySelectEnemy($p, $MinDistance, $find);
			}
		}
		return $find;
		
    }

	private function trySelectEnemy(Entity $target, &$MinDistance, &$find){
		if($target === $this->entity or $target->closed or !$target->isAlive()){
			return;
		}
		if($this->shouldIgnoreTarget($target)){
			return;
		}
		if($target instanceof Player and method_exists($target, "isSurvival") and !$target->isSurvival()){
			return;
		}

		$distance = $this->entity->distance($target);
		if($distance < $this->lookDistance and $distance < $MinDistance){
			$this->enemy = $target;
			$MinDistance = $distance;
			$find = true;
		}
	}

	private function canUseCurrentTarget() : bool{
		if($this->enemy instanceof Entity and $this->enemy->isAlive()){
			if(($this->enemy instanceof Player) and (!$this->enemy->isConnected())){
				return false;
			}
			if($this->shouldIgnoreTarget($this->enemy)){
				$this->enemy = null;
				return false;
			}
			return $this->entity->distance($this->enemy) < $this->lookDistance;
		}

		return false;
	}

	private function shouldIgnoreTarget(Entity $target) : bool{
		if(!($target instanceof Player)){
			return false;
		}
		if(!method_exists($this->entity, "isPm1eNeutralToPlayers") or !$this->entity->isPm1eNeutralToPlayers()){
			return false;
		}
		if(method_exists($this->entity, "getPm1eRetaliationTarget") and $this->entity->getPm1eRetaliationTarget() === $target){
			return false;
		}

		return true;
	}

    public function canContinue() : bool{
		return $this->canUseCurrentTarget();
        
    }

    public function onTick(){
		if(!($this->enemy instanceof Entity) or !$this->enemy->isAlive()){
			return;
		}

		$distance = $this->entity->distance($this->enemy);
		$entity = $this->entity;
		if($distance >= 1.5){
			$entity->setPm1eFollowTarget($this->enemy);
			$entity->setPm1eMoveMultiplier($this->speedMultiplier);
			$entity->setPm1eStayTime(0);
		}elseif($this->timeLeft == 0){
			$damage = $entity->getHurt();
			$knockBack = 0.4;
			if(method_exists($entity, "getWeapon")){
				$damage += max(0, VanillaMobEquipment::getWeaponBaseDamage($entity->getWeapon()) - 1);
				$damage += Tool::getWeaponEnchantmentDamageBonus($entity, $this->enemy);
				$knockBack = Tool::getWeaponKnockBackStrength($knockBack, $entity);
			}
			$source = new EntityDamageByEntityEvent($entity, $this->enemy, EntityDamageEvent::CAUSE_ENTITY_ATTACK, $damage, $knockBack);
			if($this->enemy->attack($damage, $source) and method_exists($entity, "onSuccessfulMeleeAttack")){
				$entity->onSuccessfulMeleeAttack($this->enemy, $source);
			}
			$this->timeLeft = mt_rand(30, 40);
		}
		
		if($this->timeLeft > 0){
			--$this->timeLeft;
		}
		$this->swimming();
    }
	
    public function onEnd(){
        $this->entity->setPm1eFollowTarget(null);
        $this->entity->setPm1eMoveMultiplier(1.0);
    }
	
	public function bowAimPitch($palyer, $entity, $distance = 0.07){
		
		$_0x2bf6x17f = 1;
		
		$x = $palyer->x - $entity->x;
		$y = $palyer->y - $entity->y;
		$z = $palyer->z - $entity->z;
		
		$_0x2bf6x183 = sqrt($x * $x + $z * $z);
		$_0x2bf6x184 = $distance;
		$_0x2bf6x185 = ($_0x2bf6x17f * $_0x2bf6x17f * $_0x2bf6x17f * $_0x2bf6x17f - $_0x2bf6x184 * ($_0x2bf6x184 * ($_0x2bf6x183 * $_0x2bf6x183) + 2 * $y * ($_0x2bf6x17f * $_0x2bf6x17f)));
		$pitch = -(180 / M_PI) * (atan(($_0x2bf6x17f * $_0x2bf6x17f - sqrt($_0x2bf6x185)) / ($_0x2bf6x184 * $_0x2bf6x183)));
		if(is_nan($pitch)){
			$pitch = 0;
		}
		$entity->pitch = $pitch;
		
		return $pitch;
	}
	
	
}
