<?php
namespace pocketmine\entity;

use pocketmine\event\entity\EntityDamageEvent;

abstract class Creature extends Living{
    public $attackingTick = 0;

    public function onUpdate($currentTick){
        if(!$this instanceof Human){
            if($this->attackingTick > 0){
                $this->attackingTick--;
            }
            if(!$this->isAlive() and $this->hasSpawned){
                ++$this->deadTicks;
                if($this->deadTicks >= 20){
                    $this->despawnFromAll();
                    $this->close();
                    return false;
                }
                return true;
            }
            if($this->isAlive()){

                // 贴地时不再持续施加重力，避免落地瞬间的微小下沉；
                // move() 前后都尝试恢复“站在半砖/台阶上”的高度，防止走下台阶时悬空抖动。
                $this->recoverPartialBlockStandingHeight();

                if($this->onGround and !$this->isKnockedBack()){
                    $this->motionY = 0;
                }else{
                    // 击退期间即使贴着地面也要继续施加重力，让 0.4 的竖直
                    // 击退速度形成正常的抛物线，而不是被就地清零。
                    $this->motionY -= $this->gravity;
                }

                $this->move($this->motionX, $this->motionY, $this->motionZ);

                $this->recoverPartialBlockStandingHeight();

                $friction = 1 - $this->drag;

                if($this->onGround and (abs($this->motionX) > 0.00001 or abs($this->motionZ) > 0.00001)){
                    $friction = $this->getLevel()->getBlock($this->temporalVector->setComponents((int) floor($this->x), (int) floor($this->y - 1), (int) floor($this->z)))->getFrictionFactor() * $friction;
                }

                $this->motionX *= $friction;
                $this->motionY *= 1 - $this->drag;
                $this->motionZ *= $friction;

                if($this->onGround){
                    $this->motionY *= -0.5;
                }
            }
        }
        return parent::onUpdate($currentTick);
    }

    public function willMove($distance = 36){
        foreach($this->getViewers() as $player){
            if($player->distance($this->getLocation()) <= $distance) return true;
        }
        return false;
    }

    public function attack($damage, EntityDamageEvent $source){
        parent::attack($damage, $source);
        if(!$source->isCancelled() and $source->getCause() == EntityDamageEvent::CAUSE_ENTITY_ATTACK){
            $this->attackingTick = 20;
        }
    }

}
