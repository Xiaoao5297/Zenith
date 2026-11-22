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
 * 被攻击反击（Java EntityAIHurtByTarget / InCore retaliation）的离线约束。
 *
 * 回归背景：
 *  - Zenith 的 AttackEnemyBehavior 只看附近实体，被打后不会立即锁定攻击者；
 *  - 反击目标必须和普通索敌规则一致：存活、非自己、在 lookDistance + 4 内；
 *  - attackPlayer=false 的怪不能被玩家打了就反手打玩家。
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

use pocketmine\entity\ai\behavior\AttackEnemyBehavior;

class AttackEnemyRetaliationTest{

	public static function run() : array{
		$out = [];

		// ===== 非玩家：NETWORK_ID 列表 =====
		$hostiles = [32, 33, 34, 35, 36];
		$out[] = [
			"hostile_id_allowed",
			AttackEnemyBehavior::acceptsRetaliationTarget(true, false, false, true, true, 32, $hostiles, false, 5.0, 20.0),
			"僵尸(32) 在列表内，20 格内可反击"
		];
		$out[] = [
			"hostile_id_disallowed",
			!AttackEnemyBehavior::acceptsRetaliationTarget(true, false, false, true, true, 10, $hostiles, false, 5.0, 20.0),
			"鸡(10) 不在列表内，不能反击"
		];

		// ===== 玩家：attackPlayer / 生存模式 / 在线 =====
		$out[] = [
			"player_allowed",
			AttackEnemyBehavior::acceptsRetaliationTarget(true, false, true, true, true, 0, $hostiles, true, 5.0, 20.0),
			"attackPlayer=true 且生存模式在线 -> 可反击"
		];
		$out[] = [
			"player_attack_disabled",
			!AttackEnemyBehavior::acceptsRetaliationTarget(true, false, true, true, true, 0, $hostiles, false, 5.0, 20.0),
			"attackPlayer=false 时被打也不还手"
		];
		$out[] = [
			"player_creative",
			!AttackEnemyBehavior::acceptsRetaliationTarget(true, false, true, false, true, 0, $hostiles, true, 5.0, 20.0),
			"创造模式玩家不是合法反击目标"
		];
		$out[] = [
			"player_disconnected",
			!AttackEnemyBehavior::acceptsRetaliationTarget(true, false, true, true, false, 0, $hostiles, true, 5.0, 20.0),
			"已断线玩家不是合法反击目标"
		];

		// ===== 存活 / 自己 =====
		$out[] = [
			"dead_damager",
			!AttackEnemyBehavior::acceptsRetaliationTarget(false, false, false, true, true, 32, $hostiles, false, 5.0, 20.0),
			"已死亡的攻击者不再被锁定"
		];
		$out[] = [
			"self_damager",
			!AttackEnemyBehavior::acceptsRetaliationTarget(true, true, false, true, true, 32, $hostiles, false, 5.0, 20.0),
			"自己不能成为反击目标"
		];

		// ===== 距离边界：maxDistance 含边界 =====
		$out[] = [
			"distance_equal_max",
			AttackEnemyBehavior::acceptsRetaliationTarget(true, false, false, true, true, 32, $hostiles, false, 20.0, 20.0),
			"距离恰好等于 maxDistance 仍可反击（含边界）"
		];
		$out[] = [
			"distance_beyond_max",
			!AttackEnemyBehavior::acceptsRetaliationTarget(true, false, false, true, true, 32, $hostiles, false, 20.001, 20.0),
			"距离超过 maxDistance 不能反击"
		];

		// ===== 源码接线检查 =====
		$src = file_get_contents(dirname(__DIR__) . "/src/pocketmine/entity/ai/behavior/AttackEnemyBehavior.php");
		$out[] = ["source_reads_last_damage_cause", $src !== false and strpos($src, "getLastDamageCause") !== false, "shouldStart 应读取 getLastDamageCause()"];
		$out[] = ["source_checks_entity_damage_event", $src !== false and strpos($src, "EntityDamageByEntityEvent") !== false, "应从 EntityDamageByEntityEvent 取攻击者"];
		$out[] = ["source_uses_get_damager", $src !== false and strpos($src, "getDamager") !== false, "应调用 getDamager()"];
		$out[] = ["source_uses_helper", $src !== false and strpos($src, "acceptsRetaliationTarget") !== false, "shouldStart 应经过 acceptsRetaliationTarget()"];
		$out[] = [
			"source_assigns_damager",
			$src !== false and preg_match('/\$this->enemy\s*=\s*\$damager\s*;/', $src) === 1,
			"命中后必须把 enemy 设为攻击者"
		];
		$out[] = [
			"source_uses_range_bonus",
			$src !== false and strpos($src, '$this->lookDistance + self::RETALIATION_RANGE_BONUS') !== false,
			"反击距离应为 lookDistance + RETALIATION_RANGE_BONUS"
		];
		$out[] = ["range_bonus_is_four", AttackEnemyBehavior::RETALIATION_RANGE_BONUS === 4, "反击额外距离应为 4"];
		$retaliationPos = strpos($src, "getLastDamageCause");
		$cooldownPos = strpos($src, 'if($this->searchCooldown > 0){');
		$out[] = [
			"retaliation_before_search_cooldown",
			$retaliationPos !== false and $cooldownPos !== false and $retaliationPos < $cooldownPos,
			"反击分支必须在搜索冷却之前，否则被打后最多延迟 10 tick"
		];
		$out[] = [
			"find_target_uses_strict_boundary",
			$src !== false and strpos($src, 'if($distance > $bestDistance){') !== false and strpos($src, 'if($distance >= $bestDistance){') === false,
			"findTarget 的边界应与 acceptsRetaliationTarget 的 <= 语义一致"
		];

		// ===== 辅助方法签名 =====
		$method = new \ReflectionMethod(AttackEnemyBehavior::class, "acceptsRetaliationTarget");
		$out[] = ["helper_is_public_static", $method->isPublic() and $method->isStatic(), "acceptsRetaliationTarget 应为 public static"];

		return $out;
	}
}
