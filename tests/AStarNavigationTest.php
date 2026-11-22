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
 * A* 寻路的离线回归测试。
 *
 * 回归背景：地面移动曾经绕开 A* 直接朝目标施加速度（InCore Ground AI），
 * 换回 A* 后需要保证平地、绕墙、上一格台阶都能生成正确的路点。
 *
 * 这里用 newInstanceWithoutConstructor() 构造 PathNavigate，注入桩 Level 的
 * NodeEvaluator 与寻路参数，再反射调用私有 findPath()，不启动服务器运行时。
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

use pocketmine\block\Block;
use pocketmine\entity\ai\navigation\NodeEvaluator;
use pocketmine\entity\ai\navigation\Path;
use pocketmine\entity\ai\navigation\PathNavigate;
use pocketmine\level\Level;
use pocketmine\level\Position;
use pocketmine\math\Vector3;

/**
 * 只提供方块查询的桩 Level。
 *
 * $groundFn(x, z) 返回该列的地面 y（该 y 及以上为空气）；
 * $solidFn(x, y, z) 可选，返回 true 时该坐标为固体，用来搭墙。
 */
class AStarTestLevel extends Level{

	/** @var callable (x, z) => 地面 y */
	public $groundFn;

	/** @var callable|null (x, y, z) => bool */
	public $solidFn;

	public function __construct($groundFn, $solidFn = null){
		$this->groundFn = $groundFn;
		$this->solidFn = $solidFn;
	}

	public function getBlockIdAt(int $x, int $y, int $z) : int{
		if($this->solidFn !== null and ($this->solidFn)($x, $y, $z)){
			return 1;
		}
		return $y < ($this->groundFn)($x, $z) ? 1 : 0;
	}

	public function getBlockDataAt(int $x, int $y, int $z) : int{
		return 0;
	}

	public function getBlock(Vector3 $pos, $cached = true){
		return Block::get(
			$this->getBlockIdAt((int) floor($pos->x), (int) floor($pos->y), (int) floor($pos->z)),
			0,
			new Position($pos->x, $pos->y, $pos->z, $this)
		);
	}

	public function getTickRate(){
		return 1;
	}

	public function getCollisionCubes(\pocketmine\entity\Entity $entity, \pocketmine\math\AxisAlignedBB $bb, $entities = true){
		// findPath 只用 NodeEvaluator 查询方块，不走碰撞。
		return [];
	}

	/** 该坐标是否有地面支撑（脚下是固体、身体空间是空气）。 */
	public function hasGroundSupport(float $x, int $y, float $z) : bool{
		$bx = (int) floor($x);
		$bz = (int) floor($z);
		return $this->getBlockIdAt($bx, $y - 1, $bz) !== 0 and $this->getBlockIdAt($bx, $y, $bz) === 0;
	}
}

class AStarNavigationTest{

	private static function near(float $a, float $b) : bool{
		return abs($a - $b) < 0.000001;
	}

	/**
	 * 用反射绕过 PathNavigate::__construct()（它会读取 Server::getInstance()），
	 * 只注入 findPath() 需要的字段。
	 */
	private static function newNavigator(Level $level) : PathNavigate{
		$class = new \ReflectionClass(PathNavigate::class);
		$nav = $class->newInstanceWithoutConstructor();

		$set = function(string $name, $value) use ($class, $nav) : void{
			$prop = $class->getProperty($name);
			$prop->setAccessible(true);
			$prop->setValue($nav, $value);
		};

		$set("evaluator", new NodeEvaluator($level));
		$set("horizontalRange", 32);
		$set("verticalRange", 8);
		$set("maxIterations", 800);
		$set("waterCost", 2.0);

		return $nav;
	}

	private static function findPath(PathNavigate $nav, Vector3 $from, Vector3 $to) : ?Path{
		$method = (new \ReflectionClass(PathNavigate::class))->getMethod("findPath");
		$method->setAccessible(true);
		/** @var Path|null $path */
		$path = $method->invoke($nav, $from, $to, 1.8);
		return $path;
	}

	/** 所有路点都站得住（脚下有方块、身体是空气）。 */
	private static function allPointsSupported(AStarTestLevel $level, array $points) : bool{
		foreach($points as $point){
			if(!$level->hasGroundSupport($point->x, (int) $point->y, $point->z)){
				return false;
			}
		}
		return true;
	}

	public static function run() : array{
		$out = [];

		if(Block::$fullList === null){
			Block::init();
		}

		// ===== 1. 平地：直接走到目标 =====
		$flat = new AStarTestLevel(function(int $x, int $z) : int{
			return 64;
		});
		$nav = self::newNavigator($flat);
		$path = self::findPath($nav, new Vector3(0.5, 64, 0.5), new Vector3(5.5, 64, 0.5));

		$end = $path !== null ? $path->getEndPoint() : null;
		$out[] = ["flat_path_found", $path !== null and $path->getPointCount() > 0,
			$path === null ? "findPath() 返回 null" : "路点数 = " . $path->getPointCount()];
		$out[] = ["flat_endpoint", $end !== null and self::near($end->x, 5.5) and self::near($end->y, 64.0) and self::near($end->z, 0.5),
			$end === null ? "无终点" : "终点 = ({$end->x}, {$end->y}, {$end->z})"];
		$flatYs = [];
		if($path !== null){
			foreach($path->getPoints() as $point){
				$flatYs[] = $point->y;
			}
		}
		$out[] = ["flat_all_points_at_ground_level", count($flatYs) > 0 and count(array_unique($flatYs)) === 1 and $flatYs[0] === 64,
			"y 值 = " . implode(",", array_unique($flatYs))];
		$out[] = ["flat_points_standable", $path !== null and self::allPointsSupported($flat, $path->getPoints()),
			"每个路点脚下有方块且身体为空气"];

		// ===== 2. 绕墙：x=3、z=-2..2、y=64..66 的三格高墙 =====
		$walled = new AStarTestLevel(
			function(int $x, int $z) : int{
				return 64;
			},
			function(int $x, int $y, int $z) : bool{
				return $x === 3 and $z >= -2 and $z <= 2 and $y >= 64 and $y <= 66;
			}
		);
		$nav = self::newNavigator($walled);
		$path = self::findPath($nav, new Vector3(0.5, 64, 0.5), new Vector3(5.5, 64, 0.5));

		$end = $path !== null ? $path->getEndPoint() : null;
		$out[] = ["wall_path_found", $path !== null and $path->getPointCount() > 0,
			$path === null ? "findPath() 返回 null" : "路点数 = " . $path->getPointCount()];
		$out[] = ["wall_endpoint", $end !== null and self::near($end->x, 5.5) and self::near($end->y, 64.0) and self::near($end->z, 0.5),
			$end === null ? "无终点" : "终点 = ({$end->x}, {$end->y}, {$end->z})"];

		$maxDeviation = 0.0;
		if($path !== null){
			foreach($path->getPoints() as $point){
				$maxDeviation = max($maxDeviation, abs($point->z - 0.5));
			}
		}
		$out[] = ["wall_path_goes_around", $path !== null and $maxDeviation >= 2.5,
			"最大 |z-0.5| = " . $maxDeviation . "（需要绕到墙外）"];
		$out[] = ["wall_points_standable", $path !== null and self::allPointsSupported($walled, $path->getPoints()),
			"每个路点脚下有方块且身体为空气"];

		$crossedWall = false;
		if($path !== null){
			foreach($path->getPoints() as $point){
				$x = (int) floor($point->x);
				$y = (int) floor($point->y);
				$z = (int) floor($point->z);
				if($x === 3 and $z >= -2 and $z <= 2 and $y >= 64 and $y <= 66){
					$crossedWall = true;
				}
			}
		}
		$out[] = ["wall_path_never_crosses_wall", $path !== null and !$crossedWall,
			"路点不应落在墙体内"];

		// ===== 3. 上一格台阶：x>=2 的地面从 64 抬到 65 =====
		$stepped = new AStarTestLevel(function(int $x, int $z) : int{
			return $x >= 2 ? 65 : 64;
		});
		$nav = self::newNavigator($stepped);
		$path = self::findPath($nav, new Vector3(0.5, 64, 0.5), new Vector3(5.5, 65, 0.5));

		$end = $path !== null ? $path->getEndPoint() : null;
		$out[] = ["step_path_found", $path !== null and $path->getPointCount() > 0,
			$path === null ? "findPath() 返回 null" : "路点数 = " . $path->getPointCount()];
		$out[] = ["step_endpoint_y65", $end !== null and self::near($end->x, 5.5) and self::near($end->y, 65.0) and self::near($end->z, 0.5),
			$end === null ? "无终点" : "终点 = ({$end->x}, {$end->y}, {$end->z})"];
		$out[] = ["step_points_standable", $path !== null and self::allPointsSupported($stepped, $path->getPoints()),
			"每个路点脚下有方块且身体为空气"];

		$maxVerticalStep = 0;
		if($path !== null){
			$points = $path->getPoints();
			for($i = 1; $i < count($points); ++$i){
				$maxVerticalStep = max($maxVerticalStep, abs($points[$i]->y - $points[$i - 1]->y));
			}
		}
		$out[] = ["step_path_vertical_steps", $path !== null and $maxVerticalStep <= 1.0,
			"相邻路点最大垂直跨度 = " . $maxVerticalStep . "（不允许瞬移上台阶）"];

		// ===== 4. 边界：起点=终点、目标不可站立都返回 null =====
		$nav = self::newNavigator($flat);
		$sameStartGoal = self::findPath($nav, new Vector3(0.5, 64, 0.5), new Vector3(0.5, 64, 0.5));
		$out[] = ["same_start_and_goal_returns_null", $sameStartGoal === null,
			"起点=终点时不需要路径"];

		$blockedGoal = new AStarTestLevel(
			function(int $x, int $z) : int{
				return 64;
			},
			function(int $x, int $y, int $z) : bool{
				return $x === 5 and $z === 0 and $y === 64;
			}
		);
		$nav = self::newNavigator($blockedGoal);
		$unwalkable = self::findPath($nav, new Vector3(0.5, 64, 0.5), new Vector3(5.5, 64, 0.5));
		$out[] = ["unwalkable_goal_returns_null", $unwalkable === null,
			"目标格身体位置是实心方块时不应返回路径"];

		// ===== 5. 跳跃只由 Mob 兜底，PathNavigate 不再自带 0.42 =====
		$source = file_get_contents(dirname(__DIR__) . "/src/pocketmine/entity/ai/navigation/PathNavigate.php");
		$out[] = ["navigator_has_no_own_jump", $source !== false and strpos($source, "0.42") === false,
			"PathNavigate.php 不应包含 0.42 跳跃常量"];

		return $out;
	}
}
