<?php
namespace pocketmine\entity;

use pocketmine\event\entity\EntityDamageEvent;

abstract class Creature extends Living{
	public \ = 0;

	public function onUpdate(\){
		if(!\ instanceof Human){
			if(\->attackingTick > 0){
				\->attackingTick--;
			}
			if(!\->isAlive() and \->hasSpawned){
				++\->deadTicks;
				if(\->deadTicks >= 20){
					\->despawnFromAll();
				}
				return true;
			}
			if(\->isAlive()){

				\->motionY -= \->gravity;

				\->move(\->motionX, \->motionY, \->motionZ);

				\ = 1 - \->drag;

				if(\->onGround and (abs(\->motionX) > 0.00001 or abs(\->motionZ) > 0.00001)){
					\ = \->getLevel()->getBlock(\->temporalVector->setComponents((int) floor(\->x), (int) floor(\->y - 1), (int) floor(\->z)))->getFrictionFactor() * \;
				}

				\->motionX *= \;
				\->motionY *= 1 - \->drag;
				\->motionZ *= \;
				// 移除 motionY = 0 强制归零，让重力与导航器自然协调
				// updateMovement() 由 Entity::onUpdate 统一调用
			}
		}
		parent::entityBaseTick();
		return parent::onUpdate(\);
	}

	public function willMove(\ = 36){
		foreach(\->getViewers() as \){
			if(\->distance(\->getLocation()) <= \) return true;
		}
		return false;
	}

	public function attack(\, EntityDamageEvent \){
		parent::attack(\, \);
		if(!\->isCancelled() and \->getCause() == EntityDamageEvent::CAUSE_ENTITY_ATTACK){
			\->attackingTick = 20;
		}
	}

}
