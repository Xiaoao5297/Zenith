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
 * 生物跨越台阶的回归测试。
 *
 * 背景：Mob 之前沿用 Entity 的 $stepHeight = 0，所有生物都走不上 1 格台阶，
 * 只能靠 needsStepJump() 的 0.42 强制起跳；起跳过程中水平 motion 会被台阶
 * 侧面清零，于是生物在台阶前反复跳跃、原地回弹、速度极慢。
 *
 * 这里用真实的 Entity::move() 与一个只提供方块碰撞的桩 Level 跑物理循环，
 * 断言：1 格台阶需要一次 0.42 的跳跃才能上去（InCore 的 stepHeight = 0.6），
 * 但跳跃后能继续前进、不倒退；2 格墙仍然过不去。
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

class MobStepTestLevel extends \pocketmine\level\Level{

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
		return \pocketmine\block\Block::get($this->getBlockIdAt((int) floor($pos->x), (int) floor($pos->y), (int) floor($pos->z)));
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

class MobStepTestMob extends \pocketmine\entity\Mob{

	public function __construct(){
		// 绕过 Entity::__construct()：测试只需要 movement 相关的字段。
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
		return "MobStepTestMob";
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

class MobStepTest{

	private static function simulate(callable $groundFn, int $ticks){
		$level = new MobStepTestLevel($groundFn);
		$mob = new MobStepTestMob();
		$mob->setLevelForTest($level);
		$mob->closed = false;

		$startY = $groundFn(0, 0);
		$mob->setPosition(new \pocketmine\level\Position(0.5, $startY, 0.5, $level));
		$mob->onGround = true;
		$mob->ticksLived = 0;

		$jumps = 0;
		$backWards = 0.0;
		$prevX = $mob->x;
		$maxY = $mob->y;

		for($t = 1; $t <= $ticks; ++$t){
			$mob->ticksLived = $t;

			$mob->moveInDirection(new \pocketmine\math\Vector3(1, 0, 0), 0.21);
			if($mob->motionY > 0.4){
				++$jumps;
			}

			$mob->motionY -= 0.08;
			$mob->move($mob->motionX, $mob->motionY, $mob->motionZ);

			$friction = 1 - 0.02;
			if($mob->onGround and ($mob->motionX != 0 or $mob->motionZ != 0)){
				$friction *= 0.6;
			}
			$mob->motionX *= $friction;
			$mob->motionZ *= $friction;

			$backWards = max($backWards, $prevX - $mob->x);
			$prevX = $mob->x;
			$maxY = max($maxY, $mob->y);
		}

		return [$mob->x, $mob->y, $maxY, $jumps, $backWards];
	}

	public static function run() : array{
		$out = [];

		// Block 注册表必须先初始化，否则 getBlockIdAt 返回的不是固体方块。
		if(\pocketmine\block\Block::$fullList === null){
			\pocketmine\block\Block::init();
		}

		// Entity::move() 需要计时器，测试环境用空实现代替。
		if(\pocketmine\event\Timings::$entityMoveTimer === null){
			\pocketmine\event\Timings::$entityMoveTimer = new class{
				public function startTiming($id = null){}
				public function stopTiming($id = null){}
			};
		}

		// 平地对照组：应该稳定前进，不起跳、不倒退。
		list($flatX, , , $flatJumps, $flatBack) = self::simulate(function($x, $z){ return 64; }, 20);
		$out[] = ["flat_ground_advances", $flatX > 4.0, "20 tick 后 x=" . round($flatX, 2)];
		$out[] = ["flat_ground_never_jumps", $flatJumps === 0, "jumps=" . $flatJumps];
		$out[] = ["flat_ground_never_reverses", $flatBack < 0.001, "max backward=" . round($flatBack, 4)];

		// 1 格台阶：stepHeight = 0.6 时需要一次跳跃，但必须能继续前进、不倒退。
		list($stepX, $stepY, $stepMaxY, $stepJumps, $stepBack) = self::simulate(function($x, $z){
			return $x >= 2 ? 65 : 64;
		}, 40);
		$out[] = ["walks_up_one_block_step", $stepX > 4.5, "40 tick 后 x=" . round($stepX, 2)];
		$out[] = ["stands_on_step_top", abs($stepY - 65.0) < 0.05, "y=" . round($stepY, 2)];
		$out[] = ["step_climb_jumps_once", $stepJumps >= 1 and $stepJumps <= 2, "jumps=" . $stepJumps];
		$out[] = ["step_climb_never_reverses", $stepBack < 0.001, "max backward=" . round($stepBack, 4)];
		$out[] = ["step_jump_height_is_bounded", $stepMaxY <= 65.3, "maxY=" . round($stepMaxY, 3)];

		// 2 格墙：不应尝试起跳，也不应穿墙。
		list($wallX, , , $wallJumps) = self::simulate(function($x, $z){
			return $x >= 2 ? 66 : 64;
		}, 40);
		$out[] = ["two_block_wall_blocks", $wallX < 2.0, "x=" . round($wallX, 2)];
		$out[] = ["two_block_wall_never_jumps", $wallJumps === 0, "jumps=" . $wallJumps];

		return $out;
	}
}
