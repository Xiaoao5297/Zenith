<?php

namespace pocketmine\entity\ai\behavior;

use pocketmine\entity\Entity;
use pocketmine\entity\Mob;
use pocketmine\math\Vector3;

abstract class Behavior{

	/** 行为占用的控制位，控制位不重叠的行为可以同时运行 */
	public const CONTROL_MOVE = 1;
	public const CONTROL_LOOK = 2;
	public const CONTROL_JUMP = 4;
	public const CONTROL_TARGET = 8;

	/** @var Mob */
	public $entity;

	/** @var int */
	public $swimmingTick = 0;

	/** @var int */
	public $stuckTicks = 0;

	public function __construct(Mob $entity){
		$this->entity = $entity;
	}

	public abstract function getName() : string;

	public abstract function shouldStart() : bool;

	public abstract function canContinue() : bool;

	/** 行为优先级（越小越优先），由子类覆盖定义 */
	public function getPriority() : int{
		return 10;
	}

	/**
	 * 行为占用的控制位。
	 * 两个行为只有在控制位完全不重叠时才能同时运行。
	 */
	public function getControlFlags() : int{
		return self::CONTROL_MOVE | self::CONTROL_LOOK;
	}

	/** 低优先级行为是否允许被高优先级行为打断 */
	public function canInterrupt() : bool{
		return true;
	}

	/** 行为被选中时调用 */
	public function onStart() : void{
	}

	/** 行为结束后调用 */
	public abstract function onEnd();

	public abstract function onTick();

	/**
	 * 平滑地看向目标。
	 * 原版 LookControl 每 tick 最多转 30 度，避免生物瞬间扭头。
	 */
	protected function lookAt(Entity $target, bool $includePitch = true, float $maxTurn = 30.0) : void{
		$dx = ($target->x + 0.5) - ($this->entity->x + 0.5);
		$dz = ($target->z + 0.5) - ($this->entity->z + 0.5);
		$targetYaw = -atan2($dx, $dz) * (180 / M_PI);
		$this->entity->yaw = $this->turnToward($this->entity->yaw, $targetYaw, $maxTurn);

		if($includePitch){
			$dy = ($target->y + $target->getEyeHeight()) - ($this->entity->y + $this->entity->getEyeHeight());
			$horizontal = sqrt($dx * $dx + $dz * $dz);
			$targetPitch = -atan2($dy, max(0.001, $horizontal)) * (180 / M_PI);
			$this->entity->pitch = $this->turnToward($this->entity->pitch, $targetPitch, $maxTurn);
		}
	}

	/** 把当前角度朝目标角度转动，单次不超过 maxTurn 度 */
	protected function turnToward(float $current, float $target, float $maxTurn) : float{
		$delta = $this->normalizeAngle($target - $current);
		if($delta > $maxTurn){
			$delta = $maxTurn;
		}elseif($delta < -$maxTurn){
			$delta = -$maxTurn;
		}
		return $this->normalizeAngle($current + $delta);
	}

	/** 把角度规范到 [-180, 180] */
	protected function normalizeAngle(float $angle) : float{
		while($angle > 180){
			$angle -= 360;
		}
		while($angle < -180){
			$angle += 360;
		}
		return $angle;
	}

	/**
	 * 抛物线瞄准 pitch（用于弓、药水投掷）
	 * @return float 计算出的 pitch 角度
	 */
	protected function bowAimPitch(Entity $target, float $velocity = 0.04) : float{
		$g = 1;

		$x = $target->x - $this->entity->x;
		$y = ($target->y + $target->getEyeHeight()) - ($this->entity->y + $this->entity->getEyeHeight());
		$z = $target->z - $this->entity->z;

		$horizontalDist = sqrt($x * $x + $z * $z);
		if($horizontalDist < 0.001){
			return 0;
		}
		$discriminant = ($g * $g * $g * $g - $velocity * ($velocity * ($horizontalDist * $horizontalDist) + 2 * $y * ($g * $g)));
		if($discriminant < 0){
			return 0;
		}
		$pitch = -(180 / M_PI) * (atan(($g * $g - sqrt($discriminant)) / ($velocity * $horizontalDist)));
		if(is_nan($pitch)){
			$pitch = 0;
		}
		$this->entity->pitch = $pitch;
		return $pitch;
	}

	/**
	 * 沿当前朝向移动（保留给旧行为使用）
	 */
	protected function moveForward(float $speedFactor) : bool{
		$entity = $this->entity;
		$dir = $entity->getDirectionVector();
		$dir = new Vector3($dir->x, 0, $dir->z);
		if($dir->length() > 0.001){
			$dir = $dir->normalize();
		}

		$mult = 0.55 * ($entity->isInsideOfWater() ? 0.04 : 0.06);
		return $entity->moveInDirection($dir, $speedFactor * $mult);
	}

	/**
	 * 生物在水中时缓慢上浮。
	 * 不再直接设置 0.8 的 motionY，避免生物像火箭一样弹出水面。
	 */
	public function swimming() : void{
		if($this->entity->isInsideOfWater()){
			if($this->entity->motionY < 0.05){
				$this->entity->motionY = 0.12;
			}
		}
	}

	/**
	 * 简单的视线检测：从眼睛到目标眼睛之间是否被固体方块阻挡。
	 */
	protected function canSee(Entity $target) : bool{
		$level = $this->entity->getLevel();
		if($level === null){
			return false;
		}

		$fromX = $this->entity->x;
		$fromY = $this->entity->y + $this->entity->getEyeHeight();
		$fromZ = $this->entity->z;
		$toX = $target->x;
		$toY = $target->y + $target->getEyeHeight();
		$toZ = $target->z;

		$dx = $toX - $fromX;
		$dy = $toY - $fromY;
		$dz = $toZ - $fromZ;
		$distance = sqrt($dx * $dx + $dy * $dy + $dz * $dz);
		if($distance < 0.001){
			return true;
		}

		$steps = (int) ceil($distance / 0.5);
		for($i = 1; $i < $steps; ++$i){
			$t = $i / $steps;
			$x = (int) floor($fromX + $dx * $t);
			$y = (int) floor($fromY + $dy * $t);
			$z = (int) floor($fromZ + $dz * $t);
			if($level->getBlock(new Vector3($x, $y, $z))->isSolid()){
				return false;
			}
		}

		return true;
	}
}
