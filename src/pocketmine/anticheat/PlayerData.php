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

/**
 * 每个在线玩家一份的反作弊运行时状态。
 *
 * 取代过去散落在各个 Check 里的平行数组（大小写不一致、清理漏项、内存泄漏），
 * 并集中保存移动采样与豁免状态，供所有检测器共享。
 */
class PlayerData{

	/** @var string */
	private $name;

	/** @var string */
	private $lower;

	/** @var int 玩家进入反作弊视野的服务器 tick */
	private $joinTick;

	/** @var MovementSnapshot|null 上一次移动采样 */
	private $lastSample = null;

	/** @var int 最近一次传送的 tick */
	private $lastTeleportTick = -1;

	/** @var int 最近一次受伤的 tick */
	private $lastDamageTick = -1;

	/** @var int 最近一次切换世界的 tick */
	private $lastWorldChangeTick = -1;

	/** @var array 各检测器私有状态（checkId => mixed） */
	private $state = [];

	/** @var array 各检测器违规缓冲（checkId => float） */
	private $buffers = [];

	/** @var array 各检测器累计违规（checkId => int） */
	private $violations = [];

	public function __construct(string $name, int $joinTick){
		$this->name = $name;
		$this->lower = strtolower($name);
		$this->joinTick = $joinTick;
	}

	public function getName() : string{
		return $this->name;
	}

	public function getLowerName() : string{
		return $this->lower;
	}

	public function getJoinTick() : int{
		return $this->joinTick;
	}

	// ---- 移动采样 ----

	public function getLastSample() : ?MovementSnapshot{
		return $this->lastSample;
	}

	public function setLastSample(?MovementSnapshot $sample){
		$this->lastSample = $sample;
	}

	// ---- 豁免时间戳 ----

	public function markTeleport(int $tick){
		$this->lastTeleportTick = $tick;
	}

	public function getLastTeleportTick() : int{
		return $this->lastTeleportTick;
	}

	public function markDamage(int $tick){
		$this->lastDamageTick = $tick;
	}

	public function getLastDamageTick() : int{
		return $this->lastDamageTick;
	}

	public function markWorldChange(int $tick){
		$this->lastWorldChangeTick = $tick;
	}

	public function getLastWorldChangeTick() : int{
		return $this->lastWorldChangeTick;
	}

	// ---- 检测器状态 ----

	public function getState(string $key, $default = null){
		return $this->state[$key] ?? $default;
	}

	public function setState(string $key, $value){
		$this->state[$key] = $value;
	}

	public function removeState(string $key){
		unset($this->state[$key]);
	}

	// ---- 违规缓冲 ----

	public function getBuffer(string $checkId) : float{
		return (float) ($this->buffers[$checkId] ?? 0.0);
	}

	public function setBuffer(string $checkId, float $value){
		$this->buffers[$checkId] = $value < 0 ? 0.0 : $value;
	}

	public function addBuffer(string $checkId, float $amount){
		$this->setBuffer($checkId, $this->getBuffer($checkId) + $amount);
	}

	public function resetBuffer(string $checkId){
		unset($this->buffers[$checkId]);
	}

	public function getViolations(string $checkId) : int{
		return (int) ($this->violations[$checkId] ?? 0);
	}

	public function addViolation(string $checkId) : int{
		$count = $this->getViolations($checkId) + 1;
		$this->violations[$checkId] = $count;
		return $count;
	}

	public function resetViolations(string $checkId){
		unset($this->violations[$checkId]);
	}
}
