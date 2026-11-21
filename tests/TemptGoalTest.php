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
 * 原版 TemptGoal（玩家手持食物吸引动物）的离线约束。
 *
 * 回归背景：
 *  - 旧 FindFoodBehavior 只支持单一食物，鸡对种子、猪对马铃薯都失效；
 *  - 吸引半径和停止距离写死在不同行为里，容易和原版 (10 / 2) 不一致；
 *  - scaredByPlayerMovement 需要能单独关闭，否则玩家一走动动物就放弃。
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

use pocketmine\entity\ai\behavior\TemptGoal;

class TemptGoalTest{

	public static function run() : array{
		$out = [];

		// ===== isTemptedBy：食物命中 / 距离 / 玩家移动 / scared 开关 =====
		$wheat = [296];

		$out[] = ["item_hit_within_range", TemptGoal::isTemptedBy(296, $wheat, 9.0, false, false), "手持小麦 9 格内"];
		$out[] = ["item_miss", !TemptGoal::isTemptedBy(391, $wheat, 9.0, false, false), "手持胡萝卜不是牛的食物"];
		$out[] = ["too_far", !TemptGoal::isTemptedBy(296, $wheat, 10.5, false, false), "10.5 格超出吸引半径"];
		$out[] = ["boundary_excluded", !TemptGoal::isTemptedBy(296, $wheat, 10.0, false, false), "距离恰好 10.0 不算（原版 < 10）"];
		$out[] = ["scared_while_moving", !TemptGoal::isTemptedBy(296, $wheat, 9.0, true, true), "scared 开启且玩家移动 -> 放弃"];
		$out[] = ["not_scared_while_moving", TemptGoal::isTemptedBy(296, $wheat, 9.0, true, false), "scared 关闭时移动仍吸引"];
		$out[] = ["scared_while_still", TemptGoal::isTemptedBy(296, $wheat, 9.0, false, true), "scared 开启但玩家静止 -> 仍然吸引"];
		$out[] = ["miss_beats_movement", !TemptGoal::isTemptedBy(391, $wheat, 9.0, true, false), "食物不匹配时移动与否都不吸引"];

		// ===== 多种食物：猪 / 鸡 =====
		$pigFood = [391, 392];
		$out[] = ["pig_carrot", TemptGoal::isTemptedBy(391, $pigFood, 5.0, false, false), "猪吃胡萝卜"];
		$out[] = ["pig_potato", TemptGoal::isTemptedBy(392, $pigFood, 5.0, false, false), "猪也吃马铃薯"];

		$chickenFood = TemptGoal::normalizeItems([295, 361, 362]);
		$out[] = ["chicken_seeds", TemptGoal::isTemptedBy(295, $chickenFood, 5.0, false, false), "鸡吃种子"];
		$out[] = ["chicken_pumpkin_seeds", TemptGoal::isTemptedBy(361, $chickenFood, 5.0, false, false), "鸡吃南瓜种子"];
		$out[] = ["chicken_melon_seeds", TemptGoal::isTemptedBy(362, $chickenFood, 5.0, false, false), "鸡吃西瓜种子"];

		// ===== 行为元数据 =====
		$goal = (new \ReflectionClass(TemptGoal::class))->newInstanceWithoutConstructor();
		$out[] = ["priority_is_four", $goal->getPriority() === 4, "priority = " . $goal->getPriority()];
		$flags = $goal->getControlFlags();
		$out[] = [
			"controls_move_and_look",
			($flags & TemptGoal::CONTROL_MOVE) !== 0 and ($flags & TemptGoal::CONTROL_LOOK) !== 0,
			"flags = " . $flags
		];

		// ===== 四个动物类确实注册了 TemptGoal，并带上对应食物 =====
		$root = dirname(__DIR__) . "/src/pocketmine/entity/";
		$registrations = [
			"Cow" => "[296]",
			"Pig" => "[391, 392]",
			"Sheep" => "[296]",
			"Chicken" => "[295, 361, 362]",
		];
		foreach($registrations as $animal => $foodList){
			$src = file_get_contents($root . $animal . ".php");
			$hasGoal = $src !== false and strpos($src, "addBehavior(new TemptGoal(\$this, " . $foodList) !== false;
			$out[] = [
				"registered_" . strtolower($animal),
				$hasGoal,
				$animal . " 应注册 TemptGoal(" . $foodList . ")"
			];
		}

		return $out;
	}
}
