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

use pocketmine\entity\Mob;

/**
 * 对应原版 EntityAIRestrictSun：白天暴露在阳光下时，给寻路层一个“避开阳光”的提示。
 *
 * 简化点：
 *  - 原版通过 navigator.setAvoidSun(true) 让 A* 绕开暴露在天空下的节点；
 *    我们的 PathNavigate 没有这个开关，本类不再直接改路径。
 *  - 这里保留原版的行为骨架（白天 + 暴露 = shouldStart/canContinue）并暴露
 *    isSunBurning() / isRestricting()，真正的躲太阳交给 FleeSunGoal。
 *  - 不占用控制位（原版 mutex 为 0），因此可以与移动/闲逛行为同时运行。
 */
class RestrictSunGoal extends Behavior{

	/** @var bool 当前是否处于“限制阳光”状态 */
	public $restricting = false;

	public function getPriority() : int{
		return 1;
	}

	public function getControlFlags() : int{
		return 0;
	}

	public function getName() : string{
		return "RestrictSun";
	}

	public function shouldStart() : bool{
		return $this->isSunBurning();
	}

	public function canContinue() : bool{
		return $this->isSunBurning();
	}

	public function onStart() : void{
		$this->restricting = true;
	}

	public function onTick(){
	}

	public function onEnd(){
		$this->restricting = false;
	}

	/**
	 * 是否正在被太阳灼烧：存活、白天、且头部暴露在天空下。
	 */
	public function isSunBurning() : bool{
		$level = $this->entity->getLevel();
		if($level === null or !$this->entity->isAlive()){
			return false;
		}

		// Husk / Stray 不惧阳光，白天正常游荡。
		if(!FleeSunGoal::isSunSensitive($this->entity)){
			return false;
		}

		$x = (int) floor($this->entity->x);
		$y = (int) floor($this->entity->y);
		$z = (int) floor($this->entity->z);

		return FleeSunGoal::isExposedToSun($level->getBlockSkyLightAt($x, $y + 1, $z), $level->getTime());
	}

	public function isRestricting() : bool{
		return $this->restricting;
	}
}
