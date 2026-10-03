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

use lycore\event\entity\EntityDamageEvent;
use lycore\math\Vector3;

abstract class FlyingAnimal extends Creature{

    protected $gravity = 0;
    protected $drag = 0.02;

    /** @var Vector3 */
    public $flyDirection = null;
    public $flySpeed = 0.5;
    public $highestY = 128;
    /** @var Vector3|null */
    protected $pm1eFlyFollowTarget = null;
    /** @var int */
    private $pm1eFlyKnockbackTicks = 0;

    private $switchDirectionTicker = 0;
    public $switchDirectionTicks = 300;

    public function onUpdate($currentTick){
        if($this->closed !== false){
            return false;
        }
        if($this->attackingTick > 0){
            --$this->attackingTick;
        }
        if(!$this->isAlive() and $this->hasSpawned){
            ++$this->deadTicks;
            if($this->deadTicks >= 20){
                $this->despawnFromAll();
            }
            return true;
        }

        $tickDiff = $currentTick - $this->lastUpdate;
        if($tickDiff <= 0){
            return false;
        }

        $this->lastUpdate = $currentTick;
        $this->timings->startTiming();
        $hasUpdate = $this->entityBaseTick($tickDiff);

        if($this->willMove(100) and $this->getLevel()->getServer()->aiEnabled and $this->isAlive()){
            $hasUpdate = $this->tickPm1eFlyingAi($tickDiff) || $hasUpdate;
        }

        $this->timings->stopTiming();

        return $hasUpdate or !$this->onGround or abs($this->motionX) > 0.00001 or abs($this->motionY) > 0.00001 or abs($this->motionZ) > 0.00001;
    }

    public function setPm1eFlyFollowTarget(Vector3 $target = null){
        $this->pm1eFlyFollowTarget = $target;
    }

    public function getPm1eFlyFollowTarget(){
        return $this->pm1eFlyFollowTarget;
    }

    protected function tickPm1eFlyingAi(int $tickDiff){
        if($this->pm1eFlyKnockbackTicks > 0 and (abs($this->motionX) > 0.00001 or abs($this->motionY) > 0.00001 or abs($this->motionZ) > 0.00001)){
            $this->pm1eFlyKnockbackTicks = max(0, $this->pm1eFlyKnockbackTicks - max(1, $tickDiff));
            $this->move($this->motionX, $this->motionY, $this->motionZ);
            $friction = 1 - $this->drag;
            $this->motionX *= $friction;
            $this->motionY *= $friction;
            $this->motionZ *= $friction;
            $this->updateMovement();
            return true;
        }

        if($this->pm1eFlyFollowTarget instanceof Entity){
            if($this->pm1eFlyFollowTarget->closed || !$this->pm1eFlyFollowTarget->isAlive()){
                $this->pm1eFlyFollowTarget = null;
            }
        }

        if(++$this->switchDirectionTicker === $this->switchDirectionTicks){
            $this->switchDirectionTicker = 0;
            if(mt_rand(0, 100) < 50){
                $this->flyDirection = null;
            }
        }

        $target = $this->pm1eFlyFollowTarget;
        if($target instanceof Vector3){
            $dx = $target->x - $this->x;
            $dy = $target->y - $this->y;
            $dz = $target->z - $this->z;
            $diff = abs($dx) + abs($dz);
            $distanceSquared = ($dx * $dx) + ($dy * $dy) + ($dz * $dz);

            if($distanceSquared <= 4){
                $this->motionX = 0;
                $this->motionY = 0;
                $this->motionZ = 0;
                return true;
            }elseif($diff > 0.001){
                $this->motionX = 0.15 * ($dx / $diff);
                $this->motionY = 0.27 * ($dy / max(0.001, $diff));
                $this->motionZ = 0.15 * ($dz / $diff);
                $this->yaw = (-atan2($this->motionX, $this->motionZ) * 180 / M_PI);
                $f = sqrt(($this->motionX ** 2) + ($this->motionZ ** 2));
                $this->pitch = (-atan2($f, $this->motionY) * 180 / M_PI);
                $this->move($this->motionX, $this->motionY, $this->motionZ);
                $this->updateMovement();
                return true;
            }
        }

        if($this->y > $this->highestY and $this->flyDirection !== null){
            $this->flyDirection->y = -0.5;
        }

        $inAir = !$this->isInsideOfSolid() and !$this->isInsideOfWater();
        if(!$inAir){
            $this->flyDirection = null;
        }
        if($this->flyDirection instanceof Vector3){
            $this->setMotion($this->flyDirection->multiply($this->flySpeed));
        }else{
            $this->flyDirection = $this->generateRandomDirection();
            $this->flySpeed = mt_rand(50, 100) / 500;
            $this->setMotion($this->flyDirection);
        }

        $this->move($this->motionX, $this->motionY, $this->motionZ);
        $this->updateMovement();

        $f = sqrt(($this->motionX ** 2) + ($this->motionZ ** 2));
        $this->yaw = (-atan2($this->motionX, $this->motionZ) * 180 / M_PI);
        $this->pitch = (-atan2($f, $this->motionY) * 180 / M_PI);

        if($this->onGround and $this->flyDirection instanceof Vector3){
            $this->flyDirection->y *= -1;
        }

        return true;
    }

    private function generateRandomDirection(){
        return new Vector3(mt_rand(-1000, 1000) / 1000, mt_rand(-500, 500) / 1000, mt_rand(-1000, 1000) / 1000);
    }

	public function initEntity(){
		parent::initEntity();
		if($this->getDataProperty(self::DATA_AGEABLE_FLAGS) === null){
			$this->setDataProperty(self::DATA_AGEABLE_FLAGS, self::DATA_TYPE_BYTE, 0);
		}
	}

    public function knockBack(Entity $attacker, $damage, $x, $z, $base = 0.4){
        parent::knockBack($attacker, $damage, $x, $z, $base);
        if(abs($this->motionX) > 0.00001 or abs($this->motionY) > 0.00001 or abs($this->motionZ) > 0.00001){
            $this->pm1eFlyKnockbackTicks = 6;
        }
    }

    public function attack($damage, EntityDamageEvent $source){
        if($source->isCancelled()){
            return false;
        }
        if ($source->getCause() == EntityDamageEvent::CAUSE_FALL) {
            $source->setCancelled();
            return false;
        }
        return parent::attack($damage, $source);
    }

}
