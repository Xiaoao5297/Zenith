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
 * ShootEnemyBehavior（雪傀儡投掷雪球）的离线约束。
 *
 * 回归背景：
 *  - 雪傀儡原来注册近战 AttackEnemyBehavior，和 InCore/原版的远程投掷不一致；
 *  - 目标过滤、距离边界如果散落在 shouldStart/canContinue 里，改一处容易漏另一处；
 *  - 行为优先级和控制位必须允许它接管移动与视线，否则会被闲逛行为压住。
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

use pocketmine\entity\ai\behavior\ShootEnemyBehavior;

class ShootEnemyBehaviorTest{

	public static function run() : array{
		$out = [];

		// ===== acceptsTarget：存活 / 自己 / 白名单 / 距离边界 =====
		$ids = [32, 33, 34, 35, 36, 40, 44];

		$out[] = ["allowed_id", ShootEnemyBehavior::acceptsTarget(true, false, 32, $ids, 8.0, 16.0), "32 在白名单内且 8 格可达"];
		$out[] = ["disallowed_id", !ShootEnemyBehavior::acceptsTarget(true, false, 63, $ids, 8.0, 16.0), "玩家 63 不在白名单，不应成为目标"];
		$out[] = ["self_excluded", !ShootEnemyBehavior::acceptsTarget(true, true, 21, $ids, 0.0, 16.0), "自己即使类型命中也要排除"];
		$out[] = ["dead_excluded", !ShootEnemyBehavior::acceptsTarget(false, false, 32, $ids, 4.0, 16.0), "死亡目标不应被锁定"];
		$out[] = ["boundary_included", ShootEnemyBehavior::acceptsTarget(true, false, 32, $ids, 16.0, 16.0), "距离恰好等于 lookDistance 仍算范围内（<=）"];
		$out[] = ["beyond_excluded", !ShootEnemyBehavior::acceptsTarget(true, false, 32, $ids, 16.01, 16.0), "超出 lookDistance 后不再锁定"];
		$out[] = ["empty_whitelist", !ShootEnemyBehavior::acceptsTarget(true, false, 32, [], 2.0, 16.0), "空白名单不锁定任何实体"];

		// ===== canShoot：射程 / 视线 / 冷却三个条件 =====
		$out[] = ["can_shoot_ready", ShootEnemyBehavior::canShoot(true, 10.0, 10.0, 0), "有视线、射程边界、冷却结束时应开火"];
		$out[] = ["cannot_shoot_no_sight", !ShootEnemyBehavior::canShoot(false, 4.0, 10.0, 0), "没有视线不应开火"];
		$out[] = ["cannot_shoot_far", !ShootEnemyBehavior::canShoot(true, 10.01, 10.0, 0), "超出射程不应开火"];
		$out[] = ["cannot_shoot_cooldown", !ShootEnemyBehavior::canShoot(true, 4.0, 10.0, 1), "冷却未结束不应开火"];
		$out[] = ["shoot_cooldown_is_40", ShootEnemyBehavior::SHOOT_COOLDOWN === 40, "冷却常量应为 40 tick"];

		// ===== 行为元数据：优先级 2，占用 MOVE|LOOK，索敌/射程默认值 =====
		$behavior = (new \ReflectionClass(ShootEnemyBehavior::class))->newInstanceWithoutConstructor();

		$out[] = ["priority_is_two", $behavior->getPriority() === 2, "priority = " . $behavior->getPriority()];
		$flags = $behavior->getControlFlags();
		$out[] = [
			"controls_move_and_look",
			$flags === (ShootEnemyBehavior::CONTROL_MOVE | ShootEnemyBehavior::CONTROL_LOOK),
			"flags = " . $flags
		];
		$out[] = ["look_distance_is_16", $behavior->lookDistance === 16.0, "lookDistance = " . $behavior->lookDistance];
		$out[] = ["shoot_distance_is_10", $behavior->shootDistance === 10.0, "shootDistance = " . $behavior->shootDistance];

		// ===== 雪傀儡注册：远程行为 + 雪球，且不再注册近战 =====
		$src = file_get_contents(dirname(__DIR__) . "/src/pocketmine/entity/SnowGolem.php");
		$out[] = [
			"snowgolem_registers_shoot_enemy",
			$src !== false and strpos($src, "new ShootEnemyBehavior(") !== false,
			"SnowGolem::initEntity() 应注册 ShootEnemyBehavior"
		];
		$out[] = [
			"snowgolem_uses_snowball",
			$src !== false and strpos($src, "\"Snowball\"") !== false,
			"雪傀儡应投掷 Snowball"
		];
		$out[] = [
			"snowgolem_no_melee_attack",
			$src !== false and strpos($src, "new AttackEnemyBehavior") === false,
			"雪傀儡不应再注册近战 AttackEnemyBehavior"
		];
		$out[] = [
			"snowgolem_exact_whitelist",
			$src !== false and preg_match('/new\s+ShootEnemyBehavior\(\s*\$this\s*,\s*\[32,\s*33,\s*34,\s*35,\s*36,\s*40,\s*44\]\s*,\s*"Snowball"\s*\)/', $src) === 1,
			"注册参数必须精确为 [32,33,34,35,36,40,44] + Snowball"
		];

		// ===== 投掷实现：眼睛高度、实体生成、速度与同步 =====
		$shootSrc = file_get_contents(dirname(__DIR__) . "/src/pocketmine/entity/ai/behavior/ShootEnemyBehavior.php");
		$out[] = [
			"shoot_from_eye_height",
			$shootSrc !== false and strpos($shootSrc, "getEyeHeight()") !== false,
			"投掷物起点应在眼睛高度"
		];
		$out[] = [
			"shoot_creates_configured_projectile",
			$shootSrc !== false and strpos($shootSrc, "Entity::createEntity(\$this->projectileClass") !== false,
			"应通过 Entity::createEntity 生成配置的投掷物"
		];
		$out[] = [
			"shoot_spawns_projectile",
			$shootSrc !== false and strpos($shootSrc, "spawnToAll()") !== false,
			"生成后必须 spawnToAll() 才会被客户端看到"
		];
		$out[] = [
			"shoot_applies_velocity_multiplier",
			$shootSrc !== false and preg_match('/->multiply\(1\.5\)/', $shootSrc) === 1,
			"投掷物应乘 1.5 初速度"
		];
		$out[] = [
			"shoot_resets_cooldown_constant",
			$shootSrc !== false and strpos($shootSrc, '$this->shootCooldown = self::SHOOT_COOLDOWN;') !== false,
			"开火后应把冷却重置为 SHOOT_COOLDOWN"
		];
		$out[] = [
			"find_target_uses_strict_boundary",
			$shootSrc !== false and strpos($shootSrc, 'if($distance > $bestDistance){') !== false and strpos($shootSrc, 'if($distance >= $bestDistance){') === false,
			"findTarget 的边界必须与 acceptsTarget 的 <= 语义一致"
		];

		return $out;
	}
}
