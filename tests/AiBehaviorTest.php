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
 * 逃跑 / 闲逛行为的关键约束。
 *
 * 回归背景：
 *  - 恐慌只占用 MOVE，LookAtPlayer 继续把头转向玩家，生物变成倒退着跑；
 *  - 恐慌目标是一个固定点，逃跑永远走直线；
 *  - 闲逛行为 canContinue() 依赖 navigator->isDone()，路径失败就立刻结束，生物站着不动。
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

use pocketmine\entity\ai\behavior\PanicBehavior;
use pocketmine\math\Vector3;

class AiBehaviorTest{

	public static function run() : array{
		$out = [];

		$panic = (new \ReflectionClass(PanicBehavior::class))->newInstanceWithoutConstructor();

		$flags = $panic->getControlFlags();
		$out[] = ["panic_controls_move", ($flags & PanicBehavior::CONTROL_MOVE) !== 0, "flags = " . $flags];
		$out[] = ["panic_controls_look", ($flags & PanicBehavior::CONTROL_LOOK) !== 0, "占住朝向，避免 LookAtPlayer 抢 yaw"];
		$out[] = ["panic_is_top_priority", $panic->getPriority() === 1, "priority = " . $panic->getPriority()];

		// 候选点选择：离攻击者更远、半径接近 10 格的胜出
		$origin = new Vector3(0, 64, 0);
		$attacker = new Vector3(0, 64, 0);
		$far = new Vector3(10, 64, 0);      // 距攻击者 10，距原点 10 -> 10
		$veryFar = new Vector3(14, 64, 0);  // 距攻击者 14，距原点 14 -> 14 - 2.4 = 11.6
		$close = new Vector3(3, 64, 0);     // 距攻击者 3，距原点 3 -> 3 - 4.2 = -1.2

		$picked = PanicBehavior::pickFleeCandidate([$close, $far, $veryFar], $origin, $attacker);
		$out[] = ["flee_picks_farthest", $picked !== null and $picked->equals($veryFar), $picked === null ? "null" : "picked " . $picked->x];

		// 距离原点 10 格的权重会把过远的点压下去，但不会让它输给明显更近的点
		$picked = PanicBehavior::pickFleeCandidate([$close, $far], $origin, $attacker);
		$out[] = ["flee_prefers_near_ten", $picked !== null and $picked->equals($far), $picked === null ? "null" : "picked " . $picked->x];

		$out[] = ["flee_empty_candidates", PanicBehavior::pickFleeCandidate([], $origin, $attacker) === null, "空候选应为 null"];

		return $out;
	}
}
