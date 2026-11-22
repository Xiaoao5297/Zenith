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

namespace pocketmine\entity\ai\control;

use pocketmine\entity\Mob;
use pocketmine\math\Vector3;

/**
 * 统一的生物移动控制层。
 *
 * 对应原版 Java 的 EntityMoveHelper / MCPE 的 MoveControl：
 * 行为层与寻路层只调用 setWantedPosition() 声明“想去哪、多快”，
 * 真正的 motionX/motionZ 由 tick() 每 tick 统一写一次。
 * 这样同一 tick 里多个来源不会互相覆盖，消除抖动与回弹。
 */
class MoveControl{

	/** 到达判定：水平距离小于该值视为已到达 */
	private const ARRIVE_DISTANCE = 0.35;

	/** 连续被挡多少 tick 后通知导航层重算 */
	private const BLOCKED_TICKS_BEFORE_RECALC = 10;

	/** @var Mob */
	private $entity;

	/** @var float 原始速度（未乘地形系数） */
	private $speed = 0.0;

	/** @var float|null */
	private $wantedX = null;

	/** @var float|null */
	private $wantedY = null;

	/** @var float|null */
	private $wantedZ = null;

	/** @var bool 是否有待执行的目标点 */
	private $hasWanted = false;

	/** @var int 连续被挡 tick 数 */
	private $blockedTicks = 0;

	/** @var int 上一次真正执行移动的 tick（防止同一 tick 重复施加 motion） */
	private $lastMoveTick = -1;

	/** @var callable|null 连续被挡时的回调（由 PathNavigate 注册） */
	private $blockedHandler = null;

	public function __construct(Mob $entity){
		$this->entity = $entity;
	}

	/**
	 * 声明目标点与速度。只记录，不移动。
	 */
	public function setWantedPosition(Vector3 $pos, float $speed) : void{
		$this->wantedX = $pos->x;
		$this->wantedY = $pos->y;
		$this->wantedZ = $pos->z;
		$this->speed = $speed;
		$this->hasWanted = true;
	}

	/**
	 * 连续被挡达到阈值时的回调，参数为 MoveControl 自身。
	 * 导航层用它触发路径重算，避免和 MoveControl 各记一份 blockedTicks。
	 */
	public function setBlockedHandler(callable $handler) : void{
		$this->blockedHandler = $handler;
	}

	/**
	 * 每 tick 调用一次。同一 tick 内重复调用不会重复施加 motion。
	 */
	public function tick() : void{
		$tick = $this->entity->ticksLived;
		if($this->lastMoveTick === $tick){
			return;
		}
		$this->lastMoveTick = $tick;

		if(!$this->hasWanted or $this->wantedX === null or $this->wantedZ === null){
			return;
		}

		$dx = $this->wantedX - $this->entity->x;
		$dz = $this->wantedZ - $this->entity->z;
		$horizontalDist = sqrt($dx * $dx + $dz * $dz);

		if($horizontalDist < self::ARRIVE_DISTANCE){
			// 已到达：不再施加水平速度，避免下一 tick 朝反方向抖一下。
			$this->hasWanted = false;
			$this->blockedTicks = 0;
			return;
		}

		$step = self::normalizeSpeed($this->speed, $this->entity->isInsideOfWater());
		// 沿身体朝向移动，而不是直接朝路点移动。原版 EntityMoveHelper 只设置
		// yaw（每 tick 最多 30 度）和速度，实体沿 yaw 前进；这样拐角处身体和
		// 速度方向始终一致，不会出现“身子还没转过去，人已经横着滑出去”的抖动。
		$direction = self::directionFromYaw($this->entity->yaw ?? 0.0);

		if($this->entity->moveInDirection($direction, $step)){
			$this->blockedTicks = 0;
			return;
		}

		++$this->blockedTicks;
		if($this->blockedTicks >= self::BLOCKED_TICKS_BEFORE_RECALC){
			$this->blockedTicks = 0;
			if($this->blockedHandler !== null){
				call_user_func($this->blockedHandler, $this);
			}
		}
	}

	/**
	 * 由身体 yaw 得到水平单位方向。
	 *
	 * 与 Entity::getDirectionVector() 的水平分量保持一致：
	 * yaw = 0 朝 +Z，yaw = -90 朝 +X。
	 */
	public static function directionFromYaw(float $yaw) : Vector3{
		$rad = deg2rad($yaw);
		return new Vector3(-sin($rad), 0, cos($rad));
	}

	/**
	 * 地形速度系数：陆上 0.6、水中 0.3，负数与 0 保持 0。
	 * 纯函数，方便离线测试。
	 */
	public static function normalizeSpeed(float $speed, bool $inWater) : float{
		if($speed <= 0.0){
			return 0.0;
		}
		return $speed * ($inWater ? 0.3 : 0.6);
	}

	public function getSpeed() : float{
		return $this->speed;
	}

	public function hasWantedPosition() : bool{
		return $this->hasWanted;
	}

	public function getBlockedTicks() : int{
		return $this->blockedTicks;
	}

	public function clearWanted() : void{
		$this->hasWanted = false;
		$this->blockedTicks = 0;
	}
}
