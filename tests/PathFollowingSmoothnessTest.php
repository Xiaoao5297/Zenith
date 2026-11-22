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
 * A* 路径跟随的平滑性回归测试。
 *
 * 背景：旧 A* 在拐角处直接朝路点写 motion，身体 yaw 每 tick 最多转 30 度，
 * 两者不同步时客户端会看到“横着滑”“一顿一顿”。现在要求：
 * 1. motion 方向始终等于身体 yaw；
 * 2. yaw 每 tick 变化不超过 30 度；
 * 3. 开阔地形每 tick 从当前位置拉直视线，直接奔终点；
 * 4. 到达终点前不应出现零位移的“停顿 tick”。
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

use pocketmine\block\Block;
use pocketmine\entity\ai\control\MoveControl;
use pocketmine\entity\ai\navigation\NodeEvaluator;
use pocketmine\entity\ai\navigation\PathNavigate;
use pocketmine\level\Position;
use pocketmine\math\Vector3;

/** 在平地桩上额外挡一道墙：x=3，z=-2..2，y=64..66。 */
class PathFollowingWallLevel extends MobStepTestLevel{

	public function getBlockIdAt(int $x, int $y, int $z) : int{
		if($x === 3 and $z >= -2 and $z <= 2 and $y >= 64 and $y <= 66){
			return 1;
		}
		return parent::getBlockIdAt($x, $y, $z);
	}
}

class PathFollowingSmoothnessTest{

	private static function near(float $a, float $b, float $epsilon = 0.0001) : bool{
		return abs($a - $b) < $epsilon;
	}

	private static function normalizeAngle(float $angle) : float{
		while($angle > 180){
			$angle -= 360;
		}
		while($angle < -180){
			$angle += 360;
		}
		return $angle;
	}

	/**
	 * 用反射绕过 PathNavigate::__construct()，避免依赖 Server。
	 */
	private static function newNavigator(\pocketmine\entity\Mob $mob, \pocketmine\level\Level $level) : PathNavigate{
		$class = new \ReflectionClass(PathNavigate::class);
		$nav = $class->newInstanceWithoutConstructor();
		$set = function(string $name, $value) use($class, $nav) : void{
			$prop = $class->getProperty($name);
			$prop->setAccessible(true);
			$prop->setValue($nav, $value);
		};

		$set("entity", $mob);
		$set("evaluator", new NodeEvaluator($level));
		$set("horizontalRange", 32);
		$set("verticalRange", 8);
		$set("maxIterations", 800);
		$set("waterCost", 2.0);
		$set("state", 1);
		$set("speed", 0.21);
		$set("tickCounter", 0);
		$set("lastProgressTick", 0);
		$set("retryCount", 0);
		$set("recalcCooldown", 0);

		return $nav;
	}

	/**
	 * 跑一段真实的 A* + MoveControl + Entity::move 循环。
	 *
	 * @return array{lastIndexTick:int, pathDoneTick:int, maxYawStep:float, zeroMoveTicks:int, maxDirectionError:float, jumps:int, maxY:float, endY:float, startDist:float, endDist:float}
	 */
	private static function simulate(float $targetX, float $targetZ, int $ticks, bool $wall = false, bool $step = false) : array{
		$groundFn = function(int $x, int $z) use($step) : int{
			return ($step && $x >= 3) ? 65 : 64;
		};
		$level = $wall ? new PathFollowingWallLevel($groundFn) : new MobStepTestLevel($groundFn);
		$mob = new MobStepTestMob();
		$mob->setLevelForTest($level);
		$mob->closed = false;
		$mob->setPosition(new Position(0.5, 64.0, 0.5, $level));
		$mob->onGround = true;
		$mob->ticksLived = 0;
		$mob->yaw = -45.0;

		$nav = self::newNavigator($mob, $level);
		$navProp = (new \ReflectionClass(\pocketmine\entity\Mob::class))->getProperty("navigator");
		$navProp->setAccessible(true);
		$navProp->setValue($mob, $nav);

		$target = new Vector3($targetX, $step ? 65.0 : 64.0, $targetZ);
		$targetProp = (new \ReflectionClass(PathNavigate::class))->getProperty("target");
		$targetProp->setAccessible(true);
		$targetProp->setValue($nav, $target);
		$recalc = (new \ReflectionClass(PathNavigate::class))->getMethod("recalculatePath");
		$recalc->setAccessible(true);
		$recalc->invoke($nav);

		$pathProp = (new \ReflectionClass(PathNavigate::class))->getProperty("path");
		$pathProp->setAccessible(true);
		$path = $pathProp->getValue($nav);

		$startDist = $mob->distance($target);
		$maxYawStep = 0.0;
		$maxDirectionError = 0.0;
		$zeroMoveTicks = 0;
		$jumps = 0;
		$maxY = $mob->y;
		$lastIndexTick = -1;
		$pathDoneTick = -1;
		$prevYaw = (float) $mob->yaw;
		$prevX = $mob->x;
		$prevZ = $mob->z;

		for($t = 1; $t <= $ticks; ++$t){
			$mob->ticksLived = $t;
			$nav->update();
			$mob->getMoveControl()->tick();

			$motionLength = sqrt($mob->motionX * $mob->motionX + $mob->motionZ * $mob->motionZ);
			if($motionLength > 0.0001){
				$motionYaw = rad2deg(atan2(-$mob->motionX, $mob->motionZ));
				$maxDirectionError = max($maxDirectionError, abs(self::normalizeAngle($motionYaw - (float) $mob->yaw)));
			}

			$maxYawStep = max($maxYawStep, abs(self::normalizeAngle((float) $mob->yaw - $prevYaw)));
			$prevYaw = (float) $mob->yaw;
			if($mob->motionY > 0.4){
				++$jumps;
			}

			$mob->motionY = 0.0;
			$mob->move($mob->motionX, 0.0, $mob->motionZ);
			$mob->onGround = true;
			$maxY = max($maxY, $mob->y);

			$moved = sqrt(($mob->x - $prevX) ** 2 + ($mob->z - $prevZ) ** 2);
			$prevX = $mob->x;
			$prevZ = $mob->z;

			if($mob->distance($target) > 0.7 and $moved < 0.001){
				++$zeroMoveTicks;
			}

			if($path !== null and $path->getPointCount() > 0){
				if($lastIndexTick < 0 and $path->getCurrentIndex() >= $path->getPointCount() - 1){
					$lastIndexTick = $t;
				}
				if($pathDoneTick < 0 and $path->isDone()){
					$pathDoneTick = $t;
				}
			}

			// 每 tick 只保留 motion 给下一 tick 重新计算，模拟 Creature 的地面阻尼。
			$mob->motionX *= 0.588;
			$mob->motionZ *= 0.588;
		}

		return [
			"lastIndexTick" => $lastIndexTick,
			"pathDoneTick" => $pathDoneTick,
			"maxYawStep" => $maxYawStep,
			"zeroMoveTicks" => $zeroMoveTicks,
			"maxDirectionError" => $maxDirectionError,
			"jumps" => $jumps,
			"maxY" => $maxY,
			"endY" => $mob->y,
			"startDist" => $startDist,
			"endDist" => $mob->distance($target)
		];
	}

	public static function run() : array{
		$out = [];

		if(Block::$fullList === null){
			Block::init();
		}
		if(\pocketmine\event\Timings::$entityMoveTimer === null){
			\pocketmine\event\Timings::$entityMoveTimer = new class{
				public function startTiming($id = null) : void{}
				public function stopTiming($id = null) : void{}
			};
		}

		// ===== 1. 斜向直线：应尽早拉直，motion 与 yaw 同步 =====
		$diagonal = self::simulate(5.5, 5.5, 80);
		$out[] = ["diagonal_reaches_target", $diagonal["endDist"] < 1.2,
			"80 tick 后距目标 = " . round($diagonal["endDist"], 2)];
		$out[] = ["diagonal_shortcut_early", $diagonal["lastIndexTick"] > 0 and $diagonal["lastIndexTick"] <= 2,
			"路径在第 " . $diagonal["lastIndexTick"] . " tick 被拉直到终点"];
		$out[] = ["diagonal_no_zero_move_ticks", $diagonal["zeroMoveTicks"] === 0,
			"零位移 tick = " . $diagonal["zeroMoveTicks"]];
		$out[] = ["diagonal_motion_matches_yaw", $diagonal["maxDirectionError"] < 0.01,
			"最大方向误差 = " . round($diagonal["maxDirectionError"], 3) . " 度"];
		$out[] = ["diagonal_yaw_step_bounded", $diagonal["maxYawStep"] <= 30.001,
			"单 tick 最大 yaw 变化 = " . round($diagonal["maxYawStep"], 2) . " 度"];

		// ===== 2. 轴向折线：A* 路点仍可能拐弯，但 motion 不能横着滑 =====
		$axis = self::simulate(8.5, 0.5, 80);
		$out[] = ["axis_reaches_target", $axis["endDist"] < 1.2,
			"80 tick 后距目标 = " . round($axis["endDist"], 2)];
		$out[] = ["axis_motion_matches_yaw", $axis["maxDirectionError"] < 0.01,
			"最大方向误差 = " . round($axis["maxDirectionError"], 3) . " 度"];
		$out[] = ["axis_no_zero_move_ticks", $axis["zeroMoveTicks"] === 0,
			"零位移 tick = " . $axis["zeroMoveTicks"]];
		$out[] = ["axis_yaw_turns_gradually", $axis["maxYawStep"] > 0.0 and $axis["maxYawStep"] <= 30.001,
			"单 tick 最大 yaw 变化 = " . round($axis["maxYawStep"], 2) . " 度"];

		// ===== 3. 绕墙：不能停在墙前等重算，也不能横着滑过拐角 =====
		$wall = self::simulate(5.5, 0.5, 160, true);
		$out[] = ["wall_reaches_target", $wall["endDist"] < 1.2,
			"160 tick 后距目标 = " . round($wall["endDist"], 2)];
		$out[] = ["wall_motion_matches_yaw", $wall["maxDirectionError"] < 0.01,
			"最大方向误差 = " . round($wall["maxDirectionError"], 3) . " 度"];
		$out[] = ["wall_no_zero_move_ticks", $wall["zeroMoveTicks"] === 0,
			"零位移 tick = " . $wall["zeroMoveTicks"]];
		$out[] = ["wall_yaw_step_bounded", $wall["maxYawStep"] <= 30.001,
			"单 tick 最大 yaw 变化 = " . round($wall["maxYawStep"], 2) . " 度"];

		// ===== 4. 一格台阶：自动抬升，不靠 0.42 起跳，也不能卡住 =====
		$step = self::simulate(5.5, 0.5, 100, false, true);
		$out[] = ["step_reaches_target", $step["endDist"] < 1.2,
			"100 tick 后距目标 = " . round($step["endDist"], 2)];
		$out[] = ["step_climbs_to_top", $step["endY"] >= 65.0,
			"终点 y = " . round($step["endY"], 3)];
		$out[] = ["step_never_jumps", $step["jumps"] === 0,
			"0.42 跳跃次数 = " . $step["jumps"]];
		$out[] = ["step_height_bounded", $step["maxY"] <= 65.001,
			"最大 y = " . round($step["maxY"], 3)];
		$out[] = ["step_no_zero_move_ticks", $step["zeroMoveTicks"] === 0,
			"零位移 tick = " . $step["zeroMoveTicks"]];

		// ===== 5. 单 tick 方向变化有界：不能从 +X 瞬间翻到 -X =====
		$mob = new MobStepTestMob();
		$mob->yaw = -90.0;
		$direction = MoveControl::directionFromYaw($mob->yaw);
		$out[] = ["move_direction_from_body_yaw", self::near($direction->x, 1.0) and self::near($direction->z, 0.0),
			"yaw=-90 时方向 = (" . round($direction->x, 3) . ", " . round($direction->z, 3) . ")"];

		return $out;
	}
}
