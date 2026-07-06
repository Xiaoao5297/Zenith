<?php

namespace pocketmine\entity\ai\behavior;

use pocketmine\entity\Mob;
use pocketmine\entity\Entity;
use pocketmine\level\Level;
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
	 * 按当前朝向移动，处理碰撞检测、自动跳跃和地形适应
	 * @param float $speedFactor 基础速度，内部乘以 mob 通用缩放因子
	 * @param bool  $alwaysJump  遇障碍是否自动跳跃
	 * @return bool true=移动成功, false=被阻挡
	 */
	protected function moveForward(float $speedFactor, bool $alwaysJump = true): bool{
		$level = $this->entity->getLevel();
		$entity = $this->entity;
		$direction = $entity->getDirectionVector();
		$direction->y = 0;

		$mult = 0.7 * ($entity->isInsideOfWater() ? 0.3 : 0.4);
		$step = $speedFactor * $mult;

		// 目标脚部整数坐标（前方 step+0.5 处）
		$tx = (int) floor($entity->x + $direction->x * ($step + 0.5));
		$ty = (int) floor($entity->y);
		$tz = (int) floor($entity->z + $direction->z * ($step + 0.5));

		// 水中先处理浮力
		if($entity->isInsideOfWater()){
			$entity->motionY = 0.8;
		}

		$targetY = $this->pickGroundY($level, $tx, $ty, $tz);
		if($targetY === null){
			return false;
		}

		// 检查身体和头部空间
		$headRoom = $entity->height >= 1.0 ? !$level->getBlock(new Vector3($tx, $targetY + 1, $tz))->isSolid() : true;
		if(!$headRoom){
			return false;
		}

		// 水面检测：如果目标位置的脚下方块是水，陆生生物不应进入
		$blockBelow = $level->getBlock(new Vector3($tx, $targetY - 1, $tz));
		$id = $blockBelow->getId();
		if($id === 8 or $id === 9){
			return false;
		}

		// 应用水平运动
		$entity->motionX = $direction->x * $step;
		$entity->motionZ = $direction->z * $step;

		// 地形高度差补偿
		$diff = $targetY - $ty;
		if($diff > 0){
			$entity->motionY = 0.42;
		}elseif($diff < 0){
			$entity->motionY = -0.2;
		}

		return true;
	}

	/**
	 * 找目标位置的地面 Y：返回实体应站立的 Y（整数层），不可行走返回 null
	 */
	private function pickGroundY(Level $level, int $tx, int $ty, int $tz): ?int{
		$footBlock = $level->getBlock(new Vector3($tx, $ty, $tz));

		// 前方是固体 → 尝试向上跳 1 格
		if($footBlock->isSolid()){
			$above = $level->getBlock(new Vector3($tx, $ty + 1, $tz));
			$above2 = $level->getBlock(new Vector3($tx, $ty + 2, $tz));
			if(!$above->isSolid() and !$above2->isSolid()){
				return $ty + 1;
			}
			return null;
		}

		// 前方是空气/透明 → 找下方地面
		for($dy = 0; $dy >= -2; $dy--){
			$block = $level->getBlock(new Vector3($tx, $ty + $dy - 1, $tz));
			if($block->isSolid()){
				return $ty + $dy;
			}
		}
		// 下面 3 格都没有地面（悬崖）
		return null;
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