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

/**
 * FloatGoal（原版 EntityAIFloatInWater）的关键约束。
 *
 * 回归背景：
 *  - float 优先级最高 (0)，但不能抢闲逛/看玩家的控制位；
 *  - 只有“在水里 + 没踩方块 + 头没露出水面”三个条件同时成立才上浮；
 *  - motionY 已经被寻路/游泳抬起来时不再重复施力。
 */
spl_autoload_register(function($class){
	$prefix = "pocketmine\\";
	if(strpos($class, $prefix) !== 0){
		return;
	}
	$path = dirname(__DIR__) . "/src/pocketmine/" . str_replace("\\", "/", substr($class, strlen($prefix))) . ".php";
	if(is_file($path)){
		require_once $path;
	}
});

use pocketmine\entity\ai\behavior\Behavior;
use pocketmine\entity\ai\behavior\FloatGoal;

class FloatGoalTest{

	public static function run() : array{
		$out = [];

		$out[] = ["class_exists", class_exists(FloatGoal::class), "FloatGoal 类存在"];
		$out[] = ["extends_behavior", is_a(FloatGoal::class, Behavior::class, true), "FloatGoal 必须继承 Behavior"];

		$float = (new \ReflectionClass(FloatGoal::class))->newInstanceWithoutConstructor();

		$out[] = ["priority_zero", $float->getPriority() === 0, "原版 float 优先级 = " . $float->getPriority()];
		$out[] = ["name_is_float", $float->getName() === "Float", "getName() = " . $float->getName()];

		// 不占用任何控制位：可以和 MOVE / LOOK / JUMP 行为共存
		$flags = $float->getControlFlags();
		$out[] = ["no_move_control", ($flags & Behavior::CONTROL_MOVE) === 0, "flags = " . $flags];
		$out[] = ["no_look_control", ($flags & Behavior::CONTROL_LOOK) === 0, "flags = " . $flags];
		$out[] = ["no_jump_control", ($flags & Behavior::CONTROL_JUMP) === 0, "flags = " . $flags];
		$out[] = ["no_control_flags", $flags === 0, "float 不占任何控制位"];

		// 纯函数：inWater / onGround / headAboveWater 的 8 种组合
		$out[] = ["float_in_water", FloatGoal::shouldFloat(true, false, false), "水里 + 没踩地 + 头没入水 -> 上浮"];
		$out[] = ["float_on_ground", !FloatGoal::shouldFloat(true, true, false), "踩在水底方块上 -> 不主动上浮"];
		$out[] = ["float_head_out_of_water", !FloatGoal::shouldFloat(true, false, true), "头已露出水面 -> 停止上浮"];
		$out[] = ["float_not_in_water", !FloatGoal::shouldFloat(false, false, false), "不在水里 -> 不上浮"];
		$out[] = ["float_ground_and_head_out", !FloatGoal::shouldFloat(true, true, true), "踩地且头露出 -> 不上浮"];
		$out[] = ["float_dry_on_ground", !FloatGoal::shouldFloat(false, true, false), "干燥且踩地 -> 不上浮"];
		$out[] = ["float_dry_head_out", !FloatGoal::shouldFloat(false, false, true), "干燥且头露出 -> 不上浮"];
		$out[] = ["float_nothing", !FloatGoal::shouldFloat(false, true, true), "三条件都不满足 -> 不上浮"];

		// 施力参数：与原版同量级，并且阈值低于游泳逻辑的 0.12
		$out[] = ["acceleration_in_range", FloatGoal::FLOAT_ACCELERATION >= 0.04 and FloatGoal::FLOAT_ACCELERATION <= 0.06, "acceleration = " . FloatGoal::FLOAT_ACCELERATION];
		$out[] = ["threshold_below_swimming", FloatGoal::FLOAT_MOTION_THRESHOLD < 0.12, "threshold = " . FloatGoal::FLOAT_MOTION_THRESHOLD . "，避免和 swimming() 的 0.12 叠力"];

		return $out;
	}
}
