<?php

namespace pocketmine\entity\ai\behavior;

use pocketmine\entity\Mob;
use pocketmine\entity\Entity;
use pocketmine\math\Vector3;
use pocketmine\block\Air;

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

    /** 行为被选中时调用 */
    public function onStart(): void{
    }

    /** 行为结束后调用 */
    public abstract function onEnd();

    public abstract function onTick();

	/**
	 * 让实体看向目标实体
	 */
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
	 * 抛物线瞄准 pitch（用于弓箭/药水投掷）
	 * @return float 计算出的 pitch 角度
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

	/**
	 * 按当前朝向移动，处理碰撞检测和自动跳跃
	 * @param float $speedFactor 基础速度（0.1~1.0），方法内部会乘以 mob 通用缩放因子
	 * @return bool true=移动成功, false=被阻挡
	 */
	protected function moveForward(float $speedFactor, bool $alwaysJump = true): bool{
		$level = $this->entity->getLevel();
		$coordinates = $this->entity->getPosition();
		$direction = $this->entity->getDirectionVector();
		$direction->y = 0;
		$entity = $this->entity;

		$blockDown = $level->getBlock($coordinates->add(0, -1, 0));
		if($entity->getMotion()->y < 0 and $blockDown instanceof Air){
			return false;
		}

		$mult = 0.7 * ($entity->isInsideOfWater() ? 0.3 : 0.4);
		$step = $speedFactor * $mult;

		$coord = $coordinates->add($direction->multiply($step))->add($direction->multiply(0.5));
		$block = $level->getBlock($coord);
		$blockUp = $level->getBlock($coord->add(0, 1, 0));
		$blockUpUp = $level->getBlock($coord->add(0, 2, 0));

		$colliding = $block->isSolid() or $blockUp->isSolid();
		if(!$colliding){
			$motion = $direction->multiply($step);
			$pm = $entity->getMotion();
			$pm->y = 0;
			if($pm->length() < $motion->length()){
				$entity->setMotion($pm->add($motion->x - $pm->x, 0, $motion->z - $pm->z));
			}else{
				$entity->setMotion($motion);
			}
			return true;
		}else{
			if(!$blockUpUp->isSolid()){
				if($alwaysJump or mt_rand(0, 5) != 0){
					$entity->motionY = 0.42;
				}
			}
			return false;
		}
	}

	public function swimming(){
		if($this->entity->isInsideOfWater()){ //实体游泳
			$airTicks = $this->entity->getDataProperty(1); //DATA_AIR
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