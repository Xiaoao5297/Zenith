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
 * InCore 风格地面移动（Ground AI）的回归测试。
 *
 * 覆盖：基础速度、速度倍率换算、默认开关、PathNavigate 转发，
 * 以及用真实 Entity::move() 跑的「朝目标走」与「跨越 1 格台阶」模拟。
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

class GroundAiTestLevel extends \pocketmine\level\Level{

	/** @var callable (x,z) => 地面 y（该 y 及其上方为空气） */
	public $groundFn;

	public function __construct($groundFn){
		$this->groundFn = $groundFn;
	}

	public function getBlockIdAt(int $x, int $y, int $z) : int{
		return $y < ($this->groundFn)($x, $z) ? 1 : 0;
	}

	public function getBlockDataAt(int $x, int $y, int $z) : int{
		return 0;
	}

	public function getBlock(\pocketmine\math\Vector3 $pos, $cached = true){
		$x = (int) floor($pos->x);
		$y = (int) floor($pos->y);
		$z = (int) floor($pos->z);
		// 必须带 Position，方块碰撞盒是按世界坐标计算的（Block::recalculateBoundingBox）。
		return \pocketmine\block\Block::get($this->getBlockIdAt($x, $y, $z), 0, new \pocketmine\level\Position($x, $y, $z, $this));
	}

	public function getTickRate(){
		return 1;
	}

	public function getCollisionCubes(\pocketmine\entity\Entity $entity, \pocketmine\math\AxisAlignedBB $bb, $entities = true){
		$cubes = [];
		$minX = (int) floor($bb->minX);
		$maxX = (int) ceil($bb->maxX);
		$minY = (int) floor($bb->minY);
		$maxY = (int) ceil($bb->maxY);
		$minZ = (int) floor($bb->minZ);
		$maxZ = (int) ceil($bb->maxZ);
		for($y = $minY; $y <= $maxY; ++$y){
			for($z = $minZ; $z <= $maxZ; ++$z){
				for($x = $minX; $x <= $maxX; ++$x){
					if($this->getBlockIdAt($x, $y, $z) !== 0){
						$cubes[] = new \pocketmine\math\AxisAlignedBB($x, $y, $z, $x + 1, $y + 1, $z + 1);
					}
				}
			}
		}
		return $cubes;
	}
}

class GroundAiTestMob extends \pocketmine\entity\Mob{

	public function __construct(){
		// 绕过 Entity::__construct()：只初始化移动需要的字段。
		$this->closed = true;
		$this->width = 0.6;
		$this->height = 1.8;
		$this->behaviorsEnabled = false;
		$this->justCreated = true;
		$this->boundingBox = new \pocketmine\math\AxisAlignedBB(0, 0, 0, 0.6, 1.8, 0.6);
	}

	public function setLevelForTest(\pocketmine\level\Level $level) : void{
		$this->level = $level;
	}

	public function getName(){
		return "GroundAiTestMob";
	}

	public function close(){
		$this->closed = true;
	}

	public function isInsideOfWater(){
		return false;
	}

	protected function checkChunks(){
	}

	protected function updateFallState($distanceThisTick, $onGround){
	}
}

class GroundAiTestBlaze extends \pocketmine\entity\Blaze{

	public function __construct(){
		$this->closed = true;
	}

	public function close(){
		$this->closed = true;
	}
}

class GroundAiTest{

	private static function tickGroundAi(\pocketmine\entity\Mob $mob, int $ticks) : void{
		$method = new \ReflectionMethod(\pocketmine\entity\Mob::class, "tickGroundAi");
		$method->setAccessible(true);
		for($i = 0; $i < $ticks; ++$i){
			$method->invoke($mob, 1);
		}
	}

	private static function spawnMob(callable $groundFn) : array{
		$level = new GroundAiTestLevel($groundFn);
		$mob = new GroundAiTestMob();
		$mob->setLevelForTest($level);
		$mob->closed = false;
		$mob->setPosition(new \pocketmine\level\Position(0.5, $groundFn(0, 0), 0.5, $level));
		$mob->onGround = true;
		$mob->ticksLived = 0;
		return [$mob, $level];
	}

	private static function angleDiff(float $a, float $b) : float{
		$d = fmod(abs($a - $b), 360.0);
		return $d > 180.0 ? 360.0 - $d : $d;
	}

	public static function run() : array{
		$out = [];

		if(\pocketmine\block\Block::$fullList === null){
			\pocketmine\block\Block::init();
		}
		if(\pocketmine\event\Timings::$entityMoveTimer === null){
			\pocketmine\event\Timings::$entityMoveTimer = new class{
				public function startTiming($id = null){}
				public function stopTiming($id = null){}
			};
		}

		// 1. 基础速度（InCore 的 0.15 / 0.05 / 0.02）
		$out[] = ["base_speed_land", \pocketmine\entity\Mob::getGroundBaseSpeed(null) === 0.15, "陆地 0.15"];
		$out[] = ["base_speed_water", \pocketmine\entity\Mob::getGroundBaseSpeed("water") === 0.05, "水中 0.05"];
		$out[] = ["base_speed_lava", \pocketmine\entity\Mob::getGroundBaseSpeed("lava") === 0.02, "岩浆 0.02"];

		// 2. 行为速度 -> InCore multiplier
		$out[] = ["multiplier_wander", abs(\pocketmine\entity\Mob::groundSpeedMultiplier(0.40) - 1.6) < 1.0E-6, "0.40 -> 1.6"];
		$out[] = ["multiplier_attack", abs(\pocketmine\entity\Mob::groundSpeedMultiplier(0.35) - 1.4) < 1.0E-6, "0.35 -> 1.4"];
		$out[] = ["multiplier_clamped_max", \pocketmine\entity\Mob::groundSpeedMultiplier(10.0) === 2.0, "上限 2.0"];
		$out[] = ["multiplier_clamped_min", \pocketmine\entity\Mob::groundSpeedMultiplier(0.01) === 0.4, "下限 0.4"];

		// 3. 默认开关：普通生物启用，烈焰人关闭
		$mob = new GroundAiTestMob();
		$out[] = ["mob_uses_ground_ai", $mob->usesGroundAi() === true, "Mob 默认启用地面 AI"];
		$blaze = new GroundAiTestBlaze();
		$out[] = ["blaze_disables_ground_ai", $blaze->usesGroundAi() === false, "烈焰人不走地面 AI"];

		// 4. PathNavigate 把目标转发给地面 AI（不再自己 A*）
		$navSource = file_get_contents(dirname(__DIR__) . "/src/pocketmine/entity/ai/navigation/PathNavigate.php");
		$out[] = [
			"navigate_delegates_to_ground_ai",
			strpos($navSource, "usesGroundAi()") !== false and strpos($navSource, "setGroundFollowTarget") !== false,
			"PathNavigate 应检测 usesGroundAi 并转发目标"
		];

		// 5. 平地朝 +X 目标走：接近目标、朝向正确、最终停下
		list($walker) = self::spawnMob(function($x, $z){
			return 64;
		});
		$walker->setGroundFollowTarget(new \pocketmine\math\Vector3(6.5, 64, 0.5), 0.40);
		self::tickGroundAi($walker, 80);
		$out[] = ["walks_toward_target", $walker->x > 4.8, "80 tick 后 x=" . round($walker->x, 2)];
		$out[] = ["faces_target", self::angleDiff($walker->yaw, -90.0) < 2.0, "yaw=" . round($walker->yaw, 1)];
		$out[] = ["keeps_flat_height", abs($walker->y - 64.0) < 0.08, "y=" . round($walker->y, 3)];
		self::tickGroundAi($walker, 60);
		$out[] = ["stops_before_target", $walker->x <= 6.5 and abs($walker->motionX) < 0.001, "x=" . round($walker->x, 2) . " motionX=" . round($walker->motionX, 4)];

		// 6. 1 格台阶：会跳一次，但能继续前进并站上台阶
		list($climber) = self::spawnMob(function($x, $z){
			return $x >= 2 ? 65 : 64;
		});
		$climber->setGroundFollowTarget(new \pocketmine\math\Vector3(6.5, 65, 0.5), 0.40);
		self::tickGroundAi($climber, 80);
		$out[] = ["climbs_one_block_step", $climber->x > 3.0 and abs($climber->y - 65.0) < 0.1, "x=" . round($climber->x, 2) . " y=" . round($climber->y, 2)];

		// 7. 没有行为目标时会自己漫游（随机目标）
		list($wanderer) = self::spawnMob(function($x, $z){
			return 64;
		});
		self::tickGroundAi($wanderer, 200);
		$moved = sqrt(($wanderer->x - 0.5) ** 2 + ($wanderer->z - 0.5) ** 2);
		$out[] = ["wanders_without_target", $moved > 0.5, "200 tick 位移=" . round($moved, 2)];

		return $out;
	}
}
