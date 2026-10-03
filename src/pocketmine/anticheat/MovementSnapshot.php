<?php

/***
 *  _____                _  __   __
 * /__  /  ___   ____   (_)/ /_ / /_
 *   / /  / _ \ / __ \ / // __// __ \
 *  / /__/  __// / / // // /_ / / / /
 * /____/\___//_/ /_//_/ \__//_/ /_/
 *
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * @author Xiaoao
 * @link https://github.com/Xiaoao5297/Zenith
 *
 *
*/

namespace pocketmine\anticheat;

use pocketmine\math\Vector3;

/**
 * 统一的移动采样。
 *
 * 所有移动类检测器共享同一份样本，避免各自用 microtime() 计算时间差导致误判。
 * 时间维度只使用服务器 tick（tickDelta），与客户端发包速率解耦。
 */
class MovementSnapshot{

	/** @var int */
	private $tick;

	/** @var int 距上次采样的 tick 数（至少 1） */
	private $tickDelta;

	/** @var Vector3 */
	private $from;

	/** @var Vector3 */
	private $to;

	/** @var float */
	private $dx;

	/** @var float */
	private $dy;

	/** @var float */
	private $dz;

	/** @var float 水平位移 */
	private $horizontalDistance;

	/** @var float 三维位移 */
	private $distance;

	/** @var bool */
	private $onGround;

	/** @var bool */
	private $inWater;

	/** @var bool */
	private $onLadder;

	/** @var bool */
	private $onIce;

	/** @var bool */
	private $onSlime;

	/** @var bool */
	private $inVehicle;

	/** @var bool 是否处于传送/受伤/加入等豁免窗口 */
	private $exempt;

	public function __construct(
		int $tick,
		int $tickDelta,
		Vector3 $from,
		Vector3 $to,
		bool $onGround,
		bool $inWater,
		bool $onLadder,
		bool $onIce,
		bool $onSlime,
		bool $inVehicle,
		bool $exempt
	){
		$this->tick = $tick;
		$this->tickDelta = $tickDelta < 1 ? 1 : $tickDelta;
		$this->from = $from;
		$this->to = $to;

		$this->dx = $to->x - $from->x;
		$this->dy = $to->y - $from->y;
		$this->dz = $to->z - $from->z;
		$this->horizontalDistance = sqrt($this->dx * $this->dx + $this->dz * $this->dz);
		$this->distance = sqrt($this->dx * $this->dx + $this->dy * $this->dy + $this->dz * $this->dz);

		$this->onGround = $onGround;
		$this->inWater = $inWater;
		$this->onLadder = $onLadder;
		$this->onIce = $onIce;
		$this->onSlime = $onSlime;
		$this->inVehicle = $inVehicle;
		$this->exempt = $exempt;
	}

	public function getTick() : int{
		return $this->tick;
	}

	public function getTickDelta() : int{
		return $this->tickDelta;
	}

	/**
	 * 距上次采样的秒数（按 20 TPS 折算，不使用墙钟）。
	 */
	public function getElapsedSeconds() : float{
		return $this->tickDelta / 20.0;
	}

	public function getFrom() : Vector3{
		return $this->from;
	}

	public function getTo() : Vector3{
		return $this->to;
	}

	public function getDx() : float{
		return $this->dx;
	}

	public function getDy() : float{
		return $this->dy;
	}

	public function getDz() : float{
		return $this->dz;
	}

	public function getHorizontalDistance() : float{
		return $this->horizontalDistance;
	}

	public function getDistance() : float{
		return $this->distance;
	}

	public function isOnGround() : bool{
		return $this->onGround;
	}

	public function isInWater() : bool{
		return $this->inWater;
	}

	public function isOnLadder() : bool{
		return $this->onLadder;
	}

	public function isOnIce() : bool{
		return $this->onIce;
	}

	public function isOnSlime() : bool{
		return $this->onSlime;
	}

	public function isInVehicle() : bool{
		return $this->inVehicle;
	}

	public function isExempt() : bool{
		return $this->exempt;
	}
}
