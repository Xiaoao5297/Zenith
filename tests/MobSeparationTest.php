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
 * 生物 / 玩家重叠分离的回归测试。
 *
 * 背景：Entity::move() 只与方块碰撞，生物可以直接走进玩家身体里；
 * 第一人称相机落进 Enderman 模型后，客户端把被近裁剪面切开的黑色模型
 * 渲染成「从屏幕边缘蔓延到准心的黑色三角面」。
 * 现在 Mob::onUpdate() 每 tick 调用 pushAwayFromPlayers()，用这里的纯函数
 * separationPush() 计算水平分离量。
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

class MobSeparationTest{

	public static function run() : array{
		$results = [];

		$results[] = self::check("separation_far_apart", \pocketmine\entity\Mob::separationPush(5.0, 0.0, 0.65) === null, "距离 5 格不应推开");
		$results[] = self::check("separation_exactly_min", \pocketmine\entity\Mob::separationPush(0.65, 0.0, 0.65) === null, "正好等于最小距离时不需要推");

		$push = \pocketmine\entity\Mob::separationPush(0.25, 0.0, 0.65);
		$results[] = self::check(
			"separation_axis_x",
			$push !== null and abs($push[0] - 1.0) < 1.0E-6 and abs($push[1]) < 1.0E-6 and abs($push[2] - 0.4) < 1.0E-6,
			"沿 +X 重叠 0.4 格应沿 +X 推开 0.4 格"
		);

		$push = \pocketmine\entity\Mob::separationPush(0.0, -0.5, 0.65);
		$results[] = self::check(
			"separation_axis_negative_z",
			$push !== null and abs($push[0]) < 1.0E-6 and abs($push[1] + 1.0) < 1.0E-6 and abs($push[2] - 0.15) < 1.0E-6,
			"沿 -Z 重叠 0.15 格应沿 -Z 推开"
		);

		$push = \pocketmine\entity\Mob::separationPush(0.3, 0.4, 1.0);
		$results[] = self::check(
			"separation_diagonal_normalized",
			$push !== null and abs($push[0] - 0.6) < 1.0E-6 and abs($push[1] - 0.8) < 1.0E-6 and abs($push[2] - 0.5) < 1.0E-6,
			"对角线方向应归一化后推开 0.5 格"
		);

		$push = \pocketmine\entity\Mob::separationPush(0.0, 0.0, 0.65);
		$results[] = self::check(
			"separation_zero_distance_no_nan",
			$push !== null and $push === [1.0, 0.0, 0.65],
			"中心完全重合时应返回有限值，不能出现除零"
		);

		$results[] = self::check("separation_zero_min", \pocketmine\entity\Mob::separationPush(0.1, 0.1, 0.0) === null, "最小距离为 0 时无需推开");
		$results[] = self::check("separation_negative_min", \pocketmine\entity\Mob::separationPush(0.1, 0.1, -1.0) === null, "负最小距离应直接忽略");

		// 分离后两者的中心距离必须等于 minDistance。
		$push = \pocketmine\entity\Mob::separationPush(-0.2, 0.3, 0.8);
		if($push !== null){
			$newX = -0.2 + $push[0] * $push[2];
			$newZ = 0.3 + $push[1] * $push[2];
			$results[] = self::check(
				"separation_reaches_min_distance",
				abs(sqrt($newX * $newX + $newZ * $newZ) - 0.8) < 1.0E-6,
				"推开后的中心距离应正好等于最小距离"
			);
		}else{
			$results[] = self::check("separation_reaches_min_distance", false, "重叠输入不应返回 null");
		}

		// 结构检查：推送逻辑必须挂在 Mob::onUpdate() 上，且批量生成入口使用带守卫的方法。
		$mob = new \ReflectionClass(\pocketmine\entity\Mob::class);
		$results[] = self::check("mob_has_push_method", $mob->hasMethod("pushAwayFromPlayers"), "Mob 必须提供 pushAwayFromPlayers()");
		if($mob->hasMethod("pushAwayFromPlayers")){
			$results[] = self::check("push_method_is_protected", $mob->getMethod("pushAwayFromPlayers")->isProtected(), "pushAwayFromPlayers() 应为 protected");
		}else{
			$results[] = self::check("push_method_is_protected", false, "缺少 pushAwayFromPlayers()");
		}

		$results[] = self::check(
			"player_clearance_covers_client_model",
			\pocketmine\entity\Mob::PLAYER_CLEARANCE >= 0.35 and (0.3 + 0.6) / 2 + \pocketmine\entity\Mob::PLAYER_CLEARANCE >= 0.6,
			"玩家与末影人的中心间距必须大于客户端模型宽度（0.6）"
		);

		$mobSource = file_get_contents(dirname(__DIR__) . "/src/pocketmine/entity/Mob.php");
		$results[] = self::check(
			"push_uses_clearance",
			$mobSource !== false and substr_count($mobSource, "self::PLAYER_CLEARANCE") >= 1,
			"pushAwayFromPlayers() 必须使用 PLAYER_CLEARANCE，而不是只判断包围盒相交"
		);
		$results[] = self::check(
			"push_uses_collision_aware_move",
			$mobSource !== false and strpos($mobSource, '$this->move($normalX * $distance, 0, $normalZ * $distance)') !== false,
			"推开时必须走 Entity::move()，避免把生物塞进方块里"
		);

		$entity = new \ReflectionClass(\pocketmine\entity\Entity::class);
		$results[] = self::check("entity_has_try_spawn_to", $entity->hasMethod("trySpawnTo"), "Entity 必须提供 trySpawnTo()");
		if($entity->hasMethod("trySpawnTo")){
			$results[] = self::check("try_spawn_to_is_public", $entity->getMethod("trySpawnTo")->isPublic(), "trySpawnTo() 应可从 Player 调用");
		}else{
			$results[] = self::check("try_spawn_to_is_public", false, "缺少 trySpawnTo()");
		}

		$playerSource = file_get_contents(dirname(__DIR__) . "/src/pocketmine/Player.php");
		$results[] = self::check(
			"player_uses_try_spawn_to",
			$playerSource !== false and substr_count($playerSource, "trySpawnTo(\$this)") === 2,
			"Player 的区块发送 / 重生两处都应调用 trySpawnTo()"
		);
		$results[] = self::check(
			"player_never_collides",
			$playerSource !== false and preg_match('/function canCollideWith\\(Entity \\$entity\\) : bool\\{\\s*return false;/', $playerSource) === 1,
			"Player::canCollideWith() 仍应返回 false（推送逻辑不能依赖它）"
		);

		$levelSource = file_get_contents(dirname(__DIR__) . "/src/pocketmine/level/Level.php");
		$results[] = self::check(
			"level_drops_non_finite_packets",
			$levelSource !== false and substr_count($levelSource, "isFiniteEntityState(") >= 3,
			"addEntityMovement/addEntityMotion 必须过滤 NaN/INF"
		);

		return $results;
	}

	/**
	 * @param string $name
	 * @param bool   $pass
	 * @param string $detail
	 *
	 * @return array
	 */
	private static function check(string $name, bool $pass, string $detail) : array{
		return [$name, $pass, $detail];
	}
}
