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

namespace pocketmine\entity\ai\behavior;

use pocketmine\entity\Husk;
use pocketmine\entity\Mob;
use pocketmine\entity\Stray;
use pocketmine\level\Level;
use pocketmine\math\Vector3;

/**
 * 对应原版 EntityAIFleeSun：白天被太阳直射时，就近找有方块遮蔽的落点躲进去。
 *
 * 简化点：
 *  - 原版用 world.getTopSolidOrLiquidBlock() 在 ±10 / y±10 内做三维搜索；
 *    这里改为在地面高度附近按 2 格步长抽样列，用 pickGroundY() 找落脚点。
 *  - 原版区分 canBlockSeeSky(pos) 与 !canSeeSky(pos.down())，这里统一用
 *    “头部所在方块的天空光照是否为 15”判断，效果一致且不依赖区块高度表。
 *  - 寻路层没有 avoidSun 开关，所以不设置 navigator.setAvoidSun，直接走去阴影列。
 */
class FleeSunGoal extends Behavior{

	/** @var int 单次躲太阳最长持续 tick，避免一直追着阴影跑 */
	public $timeout = 200;

	/** @var int */
	public $timeLeft = 200;

	/** @var int 找不到阴影后的冷却，避免每 tick 重新抽样 */
	public $cooldown = 0;

	/** @var Vector3|null */
	public $target = null;

	/** @var float */
	public $speed = 0.45;

	public function __construct(Mob $entity, float $speed = 0.45, int $timeout = 200){
		parent::__construct($entity);
		$this->speed = $speed;
		$this->timeout = $timeout;
		$this->timeLeft = $timeout;
	}

	public function getPriority() : int{
		return 2;
	}

	public function getControlFlags() : int{
		return self::CONTROL_MOVE;
	}

	public function getName() : string{
		return "FleeSun";
	}

	public function shouldStart() : bool{
		if($this->cooldown > 0){
			--$this->cooldown;
			return false;
		}

		return $this->isInSun();
	}

	public function onStart() : void{
		$this->timeLeft = $this->timeout;
		$this->cooldown = 0;
		$this->target = $this->pickShadeTarget();
		if($this->target === null){
			$this->cooldown = 40;
		}
	}

	public function onTick(){
		if($this->target === null){
			return;
		}
		$this->entity->getNavigator()->moveTo($this->target, $this->speed);
	}

	public function canContinue() : bool{
		if($this->timeLeft-- <= 0){
			return false;
		}

		// 已经走进阴影（或天黑了）就没必要继续躲
		if(!$this->isInSun()){
			return false;
		}

		// 到达目标后如果仍被晒到，就再挑一个更近的阴影点
		if($this->target === null or $this->entity->distance($this->target) < 1.0){
			$this->target = $this->pickShadeTarget();
			if($this->target === null){
				$this->cooldown = 40;
				return false;
			}
		}

		return true;
	}

	public function onEnd(){
		$this->target = null;
		$this->timeLeft = $this->timeout;
		$this->entity->getNavigator()->clearPath();
	}

	/**
	 * 实体当前是否正被太阳直射（白天 + 头顶天空光照 15）。
	 */
	protected function isInSun() : bool{
		$level = $this->entity->getLevel();
		if($level === null or !$this->entity->isAlive()){
			return false;
		}

		if(!self::isSunSensitive($this->entity)){
			return false;
		}

		$x = (int) floor($this->entity->x);
		$y = (int) floor($this->entity->y);
		$z = (int) floor($this->entity->z);

		return self::isExposedToSun($level->getBlockSkyLightAt($x, $y + 1, $z), $level->getTime());
	}

	/**
	 * 原版 Husk / Stray 不惧阳光，白天不应该躲阴影（它们继承自 Zombie / Skeleton，
	 * 会被一起注册本行为，所以在这里排除）。
	 */
	public static function isSunSensitive(Mob $entity) : bool{
		return !($entity instanceof Husk) && !($entity instanceof Stray);
	}

	/**
	 * 在附近抽样，找一个有遮蔽、可站立的阴影列。
	 */
	protected function pickShadeTarget() : ?Vector3{
		$level = $this->entity->getLevel();
		if($level === null){
			return null;
		}

		$baseX = (int) floor($this->entity->x);
		$baseY = (int) floor($this->entity->y);
		$baseZ = (int) floor($this->entity->z);
		$columns = [];

		for($dx = -10; $dx <= 10; $dx += 2){
			for($dz = -10; $dz <= 10; $dz += 2){
				if($dx === 0 and $dz === 0){
					continue;
				}
				$distance = sqrt($dx * $dx + $dz * $dz);
				if($distance > 10.0){
					continue;
				}

				$x = $baseX + $dx;
				$z = $baseZ + $dz;
				$groundY = Mob::pickGroundY($level, $x, $baseY, $z);
				if($groundY === null){
					continue;
				}

				// 身体和头部空间
				$headLayers = max(1, (int) ceil($this->entity->height));
				$blocked = false;
				for($layer = 0; $layer < $headLayers; ++$layer){
					if($level->getBlock(new Vector3($x, $groundY + $layer, $z))->isSolid()){
						$blocked = true;
						break;
					}
				}
				if($blocked){
					continue;
				}

				$groundId = $level->getBlockIdAt($x, $groundY - 1, $z);
				$bodyId = $level->getBlockIdAt($x, $groundY, $z);
				if(in_array($groundId, [8, 9, 10, 11, 51, 81], true) or in_array($bodyId, [8, 9, 10, 11, 51, 81], true)){
					continue;
				}

				$skyLight = $level->getBlockSkyLightAt($x, $groundY + 1, $z);
				if($skyLight >= 15){
					continue;
				}

				$columns[] = [
					"x" => $x,
					"y" => $groundY,
					"z" => $z,
					"skyLight" => $skyLight,
					"distance" => $distance
				];
			}
		}

		$column = self::pickShadeColumn($columns);
		if($column === null){
			return null;
		}

		return new Vector3($column["x"] + 0.5, $column["y"], $column["z"] + 0.5);
	}

	/**
	 * 白天判定，和原版 World.isDaytime() 一致（time % 24000 < 12000）。
	 */
	public static function isDaytime(int $timeOfDay) : bool{
		$time = $timeOfDay % Level::TIME_FULL;
		if($time < 0){
			$time += Level::TIME_FULL;
		}
		return $time < Level::TIME_SUNSET;
	}

	/**
	 * 纯函数：给定天空光照与时间，判断是否被太阳直射。
	 */
	public static function isExposedToSun(int $skyLight, int $timeOfDay) : bool{
		return $skyLight >= 15 and self::isDaytime($timeOfDay);
	}

	/**
	 * 纯函数：从候选列里挑一个阴影点。
	 * 每列需要 x/y/z/skyLight/distance 五个键；打分方式与
	 * StrollBehavior::pickTarget() 一致，偏好 6 格左右的点。
	 *
	 * @param array $columns
	 * @return array|null
	 */
	public static function pickShadeColumn(array $columns) : ?array{
		$best = null;
		$bestScore = -INF;

		foreach($columns as $column){
			if(!isset($column["skyLight"]) or $column["skyLight"] >= 15){
				continue;
			}

			$distance = isset($column["distance"]) ? (float) $column["distance"] : 0.0;
			$score = -abs($distance - 6.0);
			if($score > $bestScore){
				$bestScore = $score;
				$best = $column;
			}
		}

		return $best;
	}
}
