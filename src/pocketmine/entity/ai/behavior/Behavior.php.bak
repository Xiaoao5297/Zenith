<?php

namespace pocketmine\entity\ai\behavior;

use pocketmine\entity\Mob;
use pocketmine\entity\Entity;
use pocketmine\math\Vector3;

abstract class Behavior{

    /** @var Mob */
    public $entity;
	public $swimmingTick;

    public function __construct(Mob $entity){
        $this->entity = $entity;
    }

    public abstract function getName() : string;

    public abstract function shouldStart() : bool;

    public abstract function canContinue() : bool;

    /** 行为优先级（越小越优先），由子类覆盖定义 */
    public function getPriority(): int{
        return 10;
    }

    /** 行为被选中时调�?*/
    public function onStart(): void{
    }

    /** 行为结束后调�?*/
    public abstract function onEnd();

    public abstract function onTick();

	/**
	 * 让实体看向目标实�?	 */
	protected function lookAt(Entity $target, bool $includePitch = true): void{
		$x = $target->x - $this->entity->x;
		$y = $target->y - $this->entity->y;
		$z = $target->z - $this->entity->z;

		$a = $target->x + 0.5;
		$c = $target->z + 0.5;

		if($includePitch){
			$len = sqrt($x * $x + $y * $y + $z * $z);
			$y = $y / $len;
			$pitch = asin($y);
			$this->entity->pitch = -($pitch * 180 / M_PI);
		}

		$this->entity->yaw = -atan2($a - ($this->entity->x + 0.5), $c - ($this->entity->z + 0.5)) * (180 / M_PI);
	}

	/**
	 * 抛物线瞄�?pitch（用于弓�?药水投掷�?	 * @return float 计算出的 pitch 角度
	 */
	protected function bowAimPitch(Entity $target, float $velocity = 0.04): float{
		$g = 1;

		$x = $target->x - $this->entity->x;
		$y = $target->y - $this->entity->y;
		$z = $target->z - $this->entity->z;

		$horizontalDist = sqrt($x * $x + $z * $z);
		$discriminant = ($g * $g * $g * $g - $velocity * ($velocity * ($horizontalDist * $horizontalDist) + 2 * $y * ($g * $g)));
		$pitch = -(180 / M_PI) * (atan(($g * $g - sqrt($discriminant)) / ($velocity * $horizontalDist)));
		if(is_nan($pitch)){
			$pitch = 0;
		}
		$this->entity->pitch = $pitch;
		return $pitch;
	}

	public $stuckTicks = 0;

	/**
	 * 沿当前朝向移动（委托�?Mob::moveInDirection�?	 * @param float $speedFactor 基础速度，内部乘�?mob 通用缩放因子
	 * @return bool true=移动成功, false=被阻�?	 */
	protected function moveForward(float $speedFactor): bool{
		$entity = $this->entity;
		$direction = $entity->getDirectionVector();
		$direction->y = 0;

		$mult = 0.55 * ($entity->isInsideOfWater() ? 0.04 : 0.06);
		return $entity->moveInDirection($direction, $speedFactor * $mult);
	}

	public function swimming(){
		if($this->entity->isInsideOfWater()){
			$airTicks = $this->entity->getDataProperty(Entity::DATA_AIR);
			if($this->swimmingTick <= 0){
				if($airTicks <= 175){
					$this->entity->motionY = 0.3;
					$this->swimmingTick = 0;
				}else{
					$this->entity->motionY = 0.8;
					$this->swimmingTick = 10;
				}
			}else{
				--$this->swimmingTick;
			}
		}
	}
}
