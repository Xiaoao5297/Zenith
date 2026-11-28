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
 * 生物击退与“踩上台阶后卡进地里”的回归测试。
 *
 * 背景：
 * 1) 寻路/移动控制层每 tick 都会重写 motionX/motionZ，Creature 又会在
 *    onGround 时把 motionY 清零，导致 Living::knockBack() 写入的击退速度
 *    在一个 tick 内被完全抹掉，玩家打怪看不到击退。
 *    现在 knockBack() 会开一个 10 tick 的击退窗口，期间移动控制层不覆盖
 *    motion，重力照常生效。
 * 2) Entity::move() 只在真正发生位移时衰减 $ySize（抬台阶的平滑偏移），
 *    如果生物踩上 1 格台阶后立刻停下，ySize 会永远停在 0.5，y 比 boundingBox
 *    的脚底低半格，客户端就把生物画进地里。现在静止分支也会继续收敛 ySize。
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

class MobKnockbackTestLevel extends \pocketmine\level\Level{

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
		// 必须带 Position：方块碰撞盒按世界坐标计算。
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

class MobKnockbackTestMob extends \pocketmine\entity\Mob{

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
		return "MobKnockbackTestMob";
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

class MobKnockbackTest{

	private static function newMob(callable $groundFn, float $x = 0.5, float $z = 0.5){
		$level = new MobKnockbackTestLevel($groundFn);
		$mob = new MobKnockbackTestMob();
		$mob->setLevelForTest($level);
		$mob->closed = false;
		$startY = $groundFn($x, $z);
		$mob->setPosition(new \pocketmine\level\Position($x, $startY, $z, $level));
		$mob->onGround = true;
		$mob->ticksLived = 0;
		return $mob;
	}

	/**
	 * 模拟一 tick 的生物物理：MoveControl 施力 -> Creature 重力/移动/摩擦。
	 * knockbackTicks 在 entityBaseTick 里递减，这里手动模拟。
	 */
	private static function tick(MobKnockbackTestMob $mob, int $tick, ?\pocketmine\math\Vector3 $wanted = null){
		$mob->ticksLived = $tick;

		if($wanted !== null){
			$mob->getMoveControl()->setWantedPosition($wanted, 0.35);
		}
		$mob->getMoveControl()->tick();

		if($mob->onGround and !$mob->isKnockedBack()){
			$mob->motionY = 0;
		}else{
			$mob->motionY -= 0.08;
		}

		$mob->move($mob->motionX, $mob->motionY, $mob->motionZ);

		$friction = 1 - 0.02;
		if($mob->onGround and (abs($mob->motionX) > 0.00001 or abs($mob->motionZ) > 0.00001)){
			$friction *= 0.6;
		}
		$mob->motionX *= $friction;
		$mob->motionZ *= $friction;

		if($mob->onGround and !$mob->isKnockedBack()){
			$mob->motionY *= -0.5;
		}

		$prop = new \ReflectionProperty(\pocketmine\entity\Entity::class, "knockbackTicks");
		$prop->setAccessible(true);
		$prop->setValue($mob, max(0, $prop->getValue($mob) - 1));
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

		// 1) knockBack() 必须开启击退窗口并写入向上/向外的 motion。
		$mob = self::newMob(function($x, $z){ return 64; });
		$mob->knockBack($mob, 0, 1.0, 0.0, 0.4);
		$out[] = ["knockback_opens_window", $mob->isKnockedBack(), "ticks=" . $mob->getKnockbackTicks()];
		$out[] = ["knockback_window_is_ten", $mob->getKnockbackTicks() === 10, "ticks=" . $mob->getKnockbackTicks()];
		$out[] = ["knockback_pushes_outward", $mob->motionX > 0.2, "motionX=" . round($mob->motionX, 3)];
		$out[] = ["knockback_lifts_upward", $mob->motionY > 0.2, "motionY=" . round($mob->motionY, 3)];

		// 2) 击退窗口内，行为/寻路调用 moveInDirection 不能覆盖 motion。
		$beforeX = $mob->motionX;
		$beforeY = $mob->motionY;
		$moved = $mob->moveInDirection(new \pocketmine\math\Vector3(-1, 0, 0), 0.21);
		$out[] = ["move_in_direction_skips_during_knockback", $moved === true and $mob->motionX === $beforeX and $mob->motionY === $beforeY,
			"motionX=" . round($mob->motionX, 3) . " motionY=" . round($mob->motionY, 3)];

		// 3) MoveControl 也不能覆盖：目标设在反方向，motion 仍应保持击退方向。
		$mob->getMoveControl()->setWantedPosition(new \pocketmine\math\Vector3(-5, 64, 0.5), 0.35);
		$mob->getMoveControl()->tick();
		$out[] = ["move_control_skips_during_knockback", $mob->motionX > 0.2, "motionX=" . round($mob->motionX, 3)];

		// 4) 完整模拟：击退应让生物水平移动、短暂离地，窗口结束后 AI 重新接管。
		$sim = self::newMob(function($x, $z){ return 64; });
		$startX = $sim->x;
		$startY = $sim->y;
		$sim->knockBack($sim, 0, 1.0, 0.0, 0.4);
		$maxY = $sim->y;
		$wanted = new \pocketmine\math\Vector3(-5, 64, 0.5);
		for($t = 1; $t <= 20; ++$t){
			self::tick($sim, $t, $wanted);
			$maxY = max($maxY, $sim->boundingBox->minY);
		}
		$out[] = ["knockback_moves_mob_horizontally", $sim->x - $startX > 0.5, "位移=" . round($sim->x - $startX, 2)];
		$out[] = ["knockback_lifts_mob_off_ground", $maxY > $startY + 0.3, "maxY=" . round($maxY, 2)];
		$out[] = ["knockback_window_expires", !$sim->isKnockedBack(), "ticks=" . $sim->getKnockbackTicks()];

		// 窗口结束后，MoveControl 应该能重新把生物拉回 -x 方向
		// （MoveControl 沿身体 yaw 前进，真实流程里 PathNavigate 每 tick 会把 yaw 转向目标）。
		$motionAfter = $sim->motionX;
		$sim->yaw = 90.0; // directionFromYaw(90) = -X
		self::tick($sim, 21, $wanted);
		$out[] = ["ai_resumes_after_knockback", $sim->motionX < 0, "motionX=" . round($sim->motionX, 3) . "（击退结束时 " . round($motionAfter, 3) . "）"];

		// 5) 踩上 1 格台阶后立刻停下：ySize 必须收敛，不能让 y 永远比脚底低半格。
		$step = self::newMob(function($x, $z){ return $x >= 2 ? 65 : 64; });
		$ySizeProp = new \ReflectionProperty(\pocketmine\entity\Entity::class, "ySize");
		$ySizeProp->setAccessible(true);
		$stepped = false;
		for($t = 1; $t <= 12; ++$t){
			++$step->ticksLived;
			$step->moveInDirection(new \pocketmine\math\Vector3(1, 0, 0), 0.21);
			$step->motionY -= 0.08;
			$step->move($step->motionX, $step->motionY, $step->motionZ);
			$f = (1 - 0.02) * 0.6;
			$step->motionX *= $f;
			$step->motionZ *= $f;
			if($ySizeProp->getValue($step) > 0.1){
				$stepped = true;
				break;
			}
		}
		$sunkRightAfterStep = $step->boundingBox->minY - $step->y;

		$step->motionX = 0;
		$step->motionZ = 0;
		$step->motionY = 0;
		for($t = 1; $t <= 8; ++$t){
			++$step->ticksLived;
			if($step->onGround){
				$step->motionY = 0;
			}else{
				$step->motionY -= 0.08;
			}
			$step->move(0, 0, 0);
		}
		$sunkAfterIdle = $step->boundingBox->minY - $step->y;

		$out[] = ["step_up_happened", $stepped, "ySize>0.1"];
		$out[] = ["step_up_offset_is_temporary", $sunkRightAfterStep > 0.1, "停下瞬间 y 比脚底低 " . round($sunkRightAfterStep, 3)];
		$out[] = ["idle_recovers_from_step_offset", abs($sunkAfterIdle) < 0.01, "静止 8 tick 后 y 比脚底低 " . round($sunkAfterIdle, 3)];

		return $out;
	}
}
