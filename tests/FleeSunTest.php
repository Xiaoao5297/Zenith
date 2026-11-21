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
 * 僵尸/骷髅白天躲太阳行为的离线约束。
 *
 * 回归背景：原版 EntityAIFleeSun / EntityAIRestrictSun 依赖
 * navigator.setAvoidSun() 与三维 topSolidOrLiquidBlock 搜索，我们的寻路层
 * 没有这些能力，因此把判定抽成纯函数，避免以后改实现时把昼夜/光照边界弄错。
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
use pocketmine\entity\ai\behavior\FleeSunGoal;
use pocketmine\entity\ai\behavior\RestrictSunGoal;

class FleeSunTest{

	public static function run() : array{
		$out = [];

		// ===== 白天 / 夜晚 × 天空光照 15 / 14 =====
		$out[] = ["sun_day_light15", FleeSunGoal::isExposedToSun(15, 6000), "白天 + 天空光照 15 应被晒"];
		$out[] = ["sun_day_light14", !FleeSunGoal::isExposedToSun(14, 6000), "白天 + 天空光照 14 已被遮蔽"];
		$out[] = ["sun_night_light15", !FleeSunGoal::isExposedToSun(15, 14000), "夜晚即使光照 15 也不算被晒"];
		$out[] = ["sun_night_light14", !FleeSunGoal::isExposedToSun(14, 14000), "夜晚 + 光照 14"];

		// ===== 昼夜边界（原版 World.isDaytime: time % 24000 < 12000）=====
		$out[] = ["daytime_last_tick", FleeSunGoal::isDaytime(11999), "11999 仍是白天"];
		$out[] = ["daytime_sunset", !FleeSunGoal::isDaytime(12000), "12000 开始不是白天"];
		$out[] = ["daytime_next_day", FleeSunGoal::isDaytime(24000 + 100), "跨天时间要取模"];

		// ===== 阴影点选择 =====
		$lit = ["x" => 2, "y" => 64, "z" => 0, "skyLight" => 15, "distance" => 2.0];
		$shadeSix = ["x" => 6, "y" => 64, "z" => 0, "skyLight" => 8, "distance" => 6.0];
		$shadeFar = ["x" => 9, "y" => 64, "z" => 0, "skyLight" => 12, "distance" => 9.0];

		$picked = FleeSunGoal::pickShadeColumn([$lit, $shadeFar, $shadeSix]);
		$out[] = ["shade_picks_near_six", $picked !== null and $picked["x"] === 6, $picked === null ? "null" : "x = " . $picked["x"]];
		$out[] = ["shade_ignores_lit", FleeSunGoal::pickShadeColumn([$lit]) === null, "光照 15 的列不能当阴影"];
		$out[] = ["shade_empty", FleeSunGoal::pickShadeColumn([]) === null, "空候选返回 null"];

		// 距离相同时保留先遇到的候选（确定性，避免测试抖动）
		$a = ["x" => 3, "y" => 64, "z" => 0, "skyLight" => 4, "distance" => 6.0];
		$b = ["x" => -3, "y" => 64, "z" => 0, "skyLight" => 4, "distance" => 6.0];
		$picked = FleeSunGoal::pickShadeColumn([$a, $b]);
		$out[] = ["shade_tie_is_deterministic", $picked !== null and $picked["x"] === 3, "同分取第一个"];

		// ===== 行为元数据 =====
		$flee = (new \ReflectionClass(FleeSunGoal::class))->newInstanceWithoutConstructor();
		$restrict = (new \ReflectionClass(RestrictSunGoal::class))->newInstanceWithoutConstructor();

		$out[] = ["flee_is_behavior", is_a($flee, Behavior::class), get_class($flee)];
		$out[] = ["flee_priority", $flee->getPriority() === 2, "priority = " . $flee->getPriority()];
		$out[] = ["flee_controls_move", ($flee->getControlFlags() & Behavior::CONTROL_MOVE) !== 0, "flags = " . $flee->getControlFlags()];

		$out[] = ["restrict_is_behavior", is_a($restrict, Behavior::class), get_class($restrict)];
		$out[] = ["restrict_priority", $restrict->getPriority() === 1, "priority = " . $restrict->getPriority()];
		$out[] = ["restrict_no_control", $restrict->getControlFlags() === 0, "原版 mutex = 0，不能抢移动位"];
		$out[] = ["restrict_not_active_by_default", !$restrict->isRestricting(), "未 onStart 时 restricting = false"];

		return $out;
	}
}
