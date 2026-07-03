<?php

namespace pocketmine\entity;

use pocketmine\event\entity\EntityDamageEvent;
use pocketmine\math\Vector3;

abstract class FlyingAnimal extends Mob{

    protected $gravity = 0;
    protected $drag = 0.02;

    /** @var Vector3 */
    public $flyDirection = null;
    public $flySpeed = 0.5;
    public $highestY = 128;

    private $switchDirectionTicker = 0;
    public $switchDirectionTicks = 300;

    public function onUpdate($currentTick){
        if($this->closed !== false){
            return false;
        }
        if ($this->willMove(100) and $this->getLevel()->getServer()->aiEnabled) {
            if(++$this->switchDirectionTicker === $this->switchDirectionTicks){
                $this->switchDirectionTicker = 0;
                if(mt_rand(0, 100) < 50){
                    $this->flyDirection = null;
                }
            }

            $this->lastUpdate = $currentTick;

            $this->timings->startTiming();

            if($this->isAlive()){

                if($this->y > $this->highestY and $this->flyDirection !== null){
                    $this->flyDirection->y = -0.5;
                }

                $inAir = !$this->isInsideOfSolid() and !$this->isInsideOfWater();
                if(!$inAir){
                    $this->flyDirection = null;
                }

                // 当有活跃 Behavior 时，由 Behavior 控制 motion
                if($this->getCurrentBehavior() === null){
                    if($this->flyDirection instanceof Vector3){
                        $this->setMotion($this->flyDirection->multiply($this->flySpeed));
                    }else{
                        $this->flyDirection = $this->generateRandomDirection();
                        $this->flySpeed = mt_rand(50, 100) / 500;
                        $this->setMotion($this->flyDirection);
                    }
                }

                // 移动和更新由 parent::onUpdate (Mob → Creature) 处理
            }
        }

        // Mob::onUpdate → Creature::onUpdate (物理移动 + EntityBaseTick) → Behavior tick
        $hasUpdate = parent::onUpdate($currentTick);
        $this->timings->stopTiming();

        // 撞地反弹 (移动后 onGround 已更新) - 仅在无活跃 Behavior 时处理
        if($this->getCurrentBehavior() === null){
            if($this->onGround and $this->flyDirection instanceof Vector3){
                $this->flyDirection->y *= -1;
            }
        }

        // 根据 motion 计算朝向
        $f = sqrt(($this->motionX ** 2) + ($this->motionZ ** 2));
        $this->yaw = (-atan2($this->motionX, $this->motionZ) * 180 / M_PI);
        $this->pitch = (-atan2($f, $this->motionY) * 180 / M_PI);

        return !$this->onGround or abs($this->motionX) > 0.00001 or abs($this->motionY) > 0.00001 or abs($this->motionZ) > 0.00001;
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

    public function attack($damage, EntityDamageEvent $source){
        if($source->isCancelled()){
            return;
        }
        if ($source->getCause() == EntityDamageEvent::CAUSE_FALL) {
            $source->setCancelled();
            return;
        }
        parent::attack($damage, $source);
    }

}
