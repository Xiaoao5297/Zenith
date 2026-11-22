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
 * 随机漫步行为的原版节奏测试。
 *
 * Java EntityAIWander / MCPE RandomStrollGoal：
 * - 每 tick 1/120 概率开始；
 * - 走到路径终点就结束，等下一次随机启动，而不是立刻挑下一个点连续走。
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

use pocketmine\entity\ai\behavior\StrollBehavior;
use pocketmine\entity\ai\navigation\PathNavigate;

class StrollBehaviorTest{

	private static function newMobWithNavigator(int $state) : MobStepTestMob{
		$mob = new MobStepTestMob();

		$class = new \ReflectionClass(PathNavigate::class);
		$nav = $class->newInstanceWithoutConstructor();
		$stateProp = $class->getProperty("state");
		$stateProp->setAccessible(true);
		$stateProp->setValue($nav, $state);

		$navProp = (new \ReflectionClass(\pocketmine\entity\Mob::class))->getProperty("navigator");
		$navProp->setAccessible(true);
		$navProp->setValue($mob, $nav);

		return $mob;
	}

	public static function run() : array{
		$out = [];

		$out[] = ["start_roll_zero", StrollBehavior::shouldStartRoll(0) === true, "roll=0 应启动"];
		$out[] = ["start_roll_one", StrollBehavior::shouldStartRoll(1) === false, "roll=1 不应启动"];
		$out[] = ["start_roll_max", StrollBehavior::shouldStartRoll(119) === false, "roll=119 不应启动"];
		$out[] = ["start_chance_is_120", StrollBehavior::START_CHANCE === 120, "启动概率应为 1/120"];

		$mob = self::newMobWithNavigator(0); // STATE_IDLE：无路径
		$behavior = new StrollBehavior($mob);
		$out[] = ["default_speed_is_025", abs($behavior->speed - 0.25) < 0.000001, "默认速度 = " . $behavior->speed];
		$out[] = [
			"move_speed_is_015",
			abs(\pocketmine\entity\ai\control\MoveControl::normalizeSpeed($behavior->speed, false) - 0.15) < 0.000001,
			"陆上实际速度 = " . \pocketmine\entity\ai\control\MoveControl::normalizeSpeed($behavior->speed, false)
		];
		$out[] = ["timeout_is_120", $behavior->timeout === 120, "超时 = " . $behavior->timeout];

		// 路径已结束：行为应结束，而不是立刻刷新目标继续走。
		$behavior->timeLeft = 10;
		$behavior->target = new \pocketmine\math\Vector3(0.5, 64, 0.5);
		$out[] = ["ends_when_navigation_done", $behavior->canContinue() === false, "导航结束时应结束漫步"];

		// FALLBACK 状态下导航层仍在直线移动，行为不能提前结束。
		$mob = self::newMobWithNavigator(2); // STATE_FALLBACK
		$behavior = new StrollBehavior($mob);
		$behavior->timeLeft = 10;
		$out[] = ["continues_during_fallback", $behavior->canContinue() === true, "FALLBACK 直线移动时仍继续"];

		return $out;
	}
}
