<?php

namespace pocketmine\entity\ai\behavior;

use pocketmine\entity\Entity;

class RandomLookAroundBehavior extends Behavior{

	/** 原版 EntityAILookIdle：每 tick 0.02（1/50）概率开始 */
	public const START_CHANCE = 50;

	/** @var int */
	private $duration = 0;

	/** @var float */
	private $targetYaw = 0;

	/** @var float */
	private $targetPitch = 0;

	public function getPriority() : int{
		return 8;
	}

	public function getControlFlags() : int{
		return self::CONTROL_LOOK;
	}

	public function getName() : string{
		return "RandomLookAround";
	}

	public function shouldStart() : bool{
		if(mt_rand(0, self::START_CHANCE - 1) !== 0){
			return false;
		}

		$this->duration = 20 + mt_rand(0, 20);
		$this->targetYaw = $this->normalizeAngle($this->entity->yaw + mt_rand(-120, 120));
		$this->targetPitch = mt_rand(-20, 20);
		return true;
	}

	public function canContinue() : bool{
		return $this->duration-- > 0;
	}

	public function onTick(){
		// 只转头，不抢身体 yaw。身体朝向由移动层/A* 控制；否则边走边随机扭身体
		// 会让生物走出诡异的蛇形路线（原版 EntityAILookIdle 也是转 rotationYawHead）。
		if($this->entity->headYaw === null){
			$this->entity->headYaw = $this->entity->yaw;
		}
		$clamped = Entity::clampHeadYaw($this->entity->yaw, $this->targetYaw, 75.0);
		$this->entity->headYaw = $this->turnToward($this->entity->headYaw, $clamped, 15.0);
		$this->entity->pitch = $this->turnToward($this->entity->pitch, $this->targetPitch, 10.0);
		$this->swimming();
	}

	public function onEnd(){
		$this->entity->pitch = 0;
	}
}
