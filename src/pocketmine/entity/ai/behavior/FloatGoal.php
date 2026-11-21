<?php

/*
 *   _  __   __  _   _  ___  _  _  _
 *  | |/ /  / / | | | ||_ _|| \| || |
 *  | ' /  / /  | |_| | | | | .` || |__
 *  |_|\_\ /_/   \___/ |___||_|\_||____|
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * @author Xiaoao
 * @link https://github.com/Xiaoao5297/Zenith
 */

namespace pocketmine\entity\ai\behavior;

use pocketmine\block\Block;

/**
 * 原版 EntityAIFloatInWater / FloatGoal。
 *
 * 生物在水里且脚没有踩在方块上时，每个 tick 给一个很小的向上速度，
 * 防止它因为重力一路沉到水底。头部（y + height）已经露出水面后停止施力，
 * 这样生物会停在水面附近，而不是像火箭一样冲出水面。
 *
 * 优先级 0（原版 float 的优先级），但不占用任何控制位：
 * float 只改 motionY，不影响水平移动和头部朝向，因此可以和闲逛、
 * 看玩家等行为同时运行。
 */
class FloatGoal extends Behavior{

	/** 每 tick 补充的向上加速度（原版约 0.04） */
	public const FLOAT_ACCELERATION = 0.04;

	/**
	 * motionY 低于该值才补力。
	 * Mob::moveInDirection() / Behavior::swimming() 在水中会把 motionY 抬到
	 * 0.12，这个阈值可以避免 FloatGoal 在它们之上重复施力。
	 */
	public const FLOAT_MOTION_THRESHOLD = 0.02;

	public function getPriority() : int{
		return 0;
	}

	/** 原版 float 不抢移动/看向控制位 */
	public function getControlFlags() : int{
		return 0;
	}

	public function getName() : string{
		return "Float";
	}

	public function shouldStart() : bool{
		return self::shouldFloat(
			$this->entity->isInsideOfWater(),
			$this->entity->isOnGround(),
			$this->isHeadAboveWater()
		);
	}

	public function canContinue() : bool{
		return $this->shouldStart();
	}

	public function onTick(){
		if(!$this->shouldStart()){
			return;
		}

		// 只补力，不直接设置固定速度：避免覆盖寻路/游泳给的上浮速度。
		if($this->entity->motionY < self::FLOAT_MOTION_THRESHOLD){
			$this->entity->motionY += self::FLOAT_ACCELERATION;
		}
	}

	public function onEnd(){
	}

	/**
	 * 纯函数形式的水面判定，方便离线测试。
	 *
	 * @param bool $inWater       眼睛/身体是否在水里（Entity::isInsideOfWater）
	 * @param bool $onGround      脚是否踩在方块上
	 * @param bool $headAboveWater 头部（y + height）所在方块是否已经不是水
	 */
	public static function shouldFloat(bool $inWater, bool $onGround, bool $headAboveWater) : bool{
		return $inWater and !$onGround and !$headAboveWater;
	}

	/** 头部所在方块是否已经露出水面 */
	public function isHeadAboveWater() : bool{
		$level = $this->entity->getLevel();
		if($level === null){
			return false;
		}

		$x = (int) floor($this->entity->x);
		$y = (int) floor($this->entity->y + $this->entity->height);
		$z = (int) floor($this->entity->z);
		$id = $level->getBlockIdAt($x, $y, $z);

		return $id !== Block::WATER and $id !== Block::STILL_WATER;
	}
}
