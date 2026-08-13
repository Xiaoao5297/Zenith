<?php

namespace pocketmine\entity\ai\behavior;

use pocketmine\entity\Mob;
use pocketmine\math\Vector3;
use pocketmine\block\Air;

class SlimeBehavior extends Behavior{

    public $duration;
    public $timeLeft;
    public $speed;
    public $speedMultiplier;

    public function __construct(Mob $entity, int $duration = 80, float $speed = 0.30, float $speedMultiplier = 0.75){
        parent::__construct($entity);

        $this->duration = $duration;
        $this->speed = $speed;
        $this->speedMultiplier = $speedMultiplier;
        $this->timeLeft = $duration;
    }

    public function getPriority(): int{
        return 7;
    }

    public function getName() : string{
        return "SlimeMove";
    }

    public function shouldStart() : bool{
        return true;
    }

    public function canContinue() : bool{
        return $this->timeLeft-- > 0;
    }

    public function onTick(){
        $speedFactor = (float) ($this->speed*$this->speedMultiplier*0.55*($this->entity->isInsideOfWater() ? 0.04 : 0.06)); // 0.7 is a general mob base factor
		$level = $this->entity->getLevel();
		$coordinates = $this->entity->getPosition();
		$direction = $this->entity->getDirectionVector();
		$direction->y = 0;
		$entity = $this->entity;

		$blockDown = $level->getBlock($coordinates->add(0,-1,0));
		if ($entity->getMotion()->y < 0 and $blockDown instanceof Air)
		{
			//$this->timeLeft = 0;
			$entity->motionY -= 0.1;
			return;
		}

	    $coord = ($coordinates->add($direction->multiply($speedFactor))->add($direction->multiply(0.5)));

		$players = $entity->getViewers();

		$block = $level->getBlock($coord);

		// 需检查的头顶层数按史莱姆大小动态计算（大小 1 需 1 层，越大越多）
		$headLayers = max(1, $entity->getSize());
		$colliding = false;
		for($i = 1; $i <= $headLayers; $i++){
			if($level->getBlock($coord->add(0, $i, 0))->isSolid()){
				$colliding = true;
				break;
			}
		}
		if (!$colliding){
			$motion = $direction->multiply($speedFactor);
			$pm = $entity->getMotion();
			if ($pm->length() < $motion->length()){
				$entity->setMotion($pm->add($motion->x - $pm->x, 0.45 * $entity->getSize(), $motion->z - $pm->z));
			}else{
				$entity->setMotion($motion);
			}
		}
		else
		{
			$entity->yaw += 180;
		}
		
		if($entity->isInsideOfWater()){ //实体游泳
			$entity->motionY = 0.5;
		}
    }

    public function onEnd(){
        $this->timeLeft = $this->duration;
        $this->entity->setMotion(new Vector3(0,0,0));
    }
}
