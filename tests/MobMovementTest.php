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
 * 生物移动的台阶判定。
 *
 * 回归背景：moveInDirection() 原来用 (groundY - floor(feetY)) 判断台阶，
 * 贴地时一点点浮点误差就会被当成 1 格台阶，导致所有生物边走边跳。
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

class MobMovementTest{

	public static function run() : array{
		$out = [];

		// 贴地时脚底 y 会比方块顶面低一点点，不能算台阶
		$out[] = ["no_jump_on_epsilon", !\pocketmine\entity\Mob::needsStepJump(63.999, 64, true, false), "feet 63.999 -> ground 64"];
		$out[] = ["no_jump_on_epsilon_2", !\pocketmine\entity\Mob::needsStepJump(63.99999, 64, true, false), "feet 63.99999 -> ground 64"];
		$out[] = ["no_jump_on_flat", !\pocketmine\entity\Mob::needsStepJump(64.0, 64, true, false), "feet 64 -> ground 64"];

		// 真正的 1 格台阶要跳
		$out[] = ["jump_on_one_block", \pocketmine\entity\Mob::needsStepJump(64.0, 65, true, false), "feet 64 -> ground 65"];
		$out[] = ["jump_on_one_block_epsilon", \pocketmine\entity\Mob::needsStepJump(64.001, 65, true, false), "feet 64.001 -> ground 65"];

		// 2 格高是墙，不是台阶（moveInDirection 会直接返回 false）
		$out[] = ["no_jump_on_wall", !\pocketmine\entity\Mob::needsStepJump(64.0, 66, true, false), "feet 64 -> ground 66"];

		// 空中、水里不跳
		$out[] = ["no_jump_in_air", !\pocketmine\entity\Mob::needsStepJump(64.0, 65, false, false), "airborne"];
		$out[] = ["no_jump_in_water", !\pocketmine\entity\Mob::needsStepJump(64.0, 65, true, true), "in water"];

		// 寻路层不再自己塞 0.42 的跳跃，台阶统一交给 moveInDirection
		$nav = file_get_contents(dirname(__DIR__) . "/src/pocketmine/entity/ai/navigation/PathNavigate.php");
		$out[] = ["navigator_has_no_own_jump", strpos($nav, "0.42") === false, "PathNavigate 不应再设置 0.42 的跳跃速度"];

		return $out;
	}
}
