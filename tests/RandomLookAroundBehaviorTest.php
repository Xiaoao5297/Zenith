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
 * 随机张望行为：只转头，不能抢身体 yaw。
 *
 * 原版 EntityAILookIdle 通过 EntityLookHelper 改 rotationYawHead，
 * 身体 yaw 仍由移动/寻路决定。否则边走边随机扭身体会走出蛇形路线。
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

require_once __DIR__ . "/MobStepTest.php";

use pocketmine\entity\ai\behavior\RandomLookAroundBehavior;

class RandomLookAroundBehaviorTest{

	public static function run() : array{
		$out = [];

		$out[] = [
			"start_chance_is_50",
			RandomLookAroundBehavior::START_CHANCE === 50,
			"原版 EntityAILookIdle 为 0.02（1/50）"
		];

		$mob = new MobStepTestMob();
		$mob->yaw = 10.0;
		$mob->headYaw = 10.0;
		$mob->pitch = 0.0;

		$behavior = new RandomLookAroundBehavior($mob);
		$behavior->onTick();

		$out[] = ["keeps_body_yaw", abs($mob->yaw - 10.0) < 0.000001, "身体 yaw 应保持 10，实际 = " . $mob->yaw];
		$out[] = ["turns_head", abs($mob->headYaw - 10.0) > 0.001, "头应转向目标，实际 = " . $mob->headYaw];

		return $out;
	}
}
