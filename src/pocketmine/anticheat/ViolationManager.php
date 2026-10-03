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

use pocketmine\Player;
use pocketmine\utils\Config;

/**
 * 统一违规处理。
 *
 * 检测器只负责判定并产出 flag，缓冲累积、衰减、告警/惩罚分级全部在这里完成。
 * 默认 alert 模式只记录与提示，不执行回退/踢出/封禁；只有显式开启 punish 才执行。
 */
class ViolationManager{

	const MODE_ALERT = "alert";
	const MODE_PUNISH = "punish";

	/** @var AntiCheat */
	private $antiCheat;

	/** @var string */
	private $mode;

	/** @var int */
	private $maxDailyViolations;

	/** @var int */
	private $punishThreshold;

	/** @var Config */
	private $dailyWarnings;

	public function __construct(AntiCheat $antiCheat, string $mode, int $maxDailyViolations, int $punishThreshold, Config $dailyWarnings){
		$this->antiCheat = $antiCheat;
		$this->mode = $mode === self::MODE_PUNISH ? self::MODE_PUNISH : self::MODE_ALERT;
		$this->maxDailyViolations = $maxDailyViolations;
		$this->punishThreshold = $punishThreshold;
		$this->dailyWarnings = $dailyWarnings;
	}

	public function getMode() : string{
		return $this->mode;
	}

	public function isPunishMode() : bool{
		return $this->mode === self::MODE_PUNISH;
	}

	public function setMode(string $mode){
		$this->mode = $mode === self::MODE_PUNISH ? self::MODE_PUNISH : self::MODE_ALERT;
	}

	public function getPunishThreshold() : int{
		return $this->punishThreshold;
	}

	/**
	 * 检测器命中一次。
	 *
	 * @param Player $player
	 * @param string $checkId    内部唯一标识（用于缓冲/计数）
	 * @param string $checkName  展示名（用于日志/消息）
	 * @param string $detail
	 * @param float  $severity   本次增加的缓冲量
	 * @param int    $maxViolations 缓冲达到该值视为一次完整违规
	 */
	public function flag(Player $player, string $checkId, string $checkName, string $detail, float $severity, int $maxViolations){
		$data = $this->antiCheat->getPlayerData($player);
		$data->addBuffer($checkId, $severity);

		if($data->getBuffer($checkId) < $maxViolations){
			if($this->antiCheat->isDebug()){
				$this->antiCheat->logCheat($player->getName(), $checkName, "[buffer " . round($data->getBuffer($checkId), 2) . "/" . $maxViolations . "] " . $detail);
			}
			return;
		}

		$data->resetBuffer($checkId);
		$count = $data->addViolation($checkId);

		$this->antiCheat->logCheat($player->getName(), $checkName, $detail);

		if(!$this->isPunishMode()){
			return;
		}

		$this->antiCheat->punish($player, $checkName, $count);
	}

	/**
	 * 正常行为，衰减缓冲。
	 */
	public function decay(Player $player, string $checkId, float $amount = 1.0){
		$data = $this->antiCheat->getPlayerData($player);
		$buffer = $data->getBuffer($checkId);
		if($buffer <= 0){
			return;
		}
		$data->setBuffer($checkId, $buffer - $amount);
	}

	public function getBuffer(Player $player, string $checkId) : float{
		return $this->antiCheat->getPlayerData($player)->getBuffer($checkId);
	}

	public function reset(Player $player, string $checkId){
		$data = $this->antiCheat->getPlayerData($player);
		$data->resetBuffer($checkId);
	}

	// ---- 每日违规 ----

	public function addDailyViolation(string $playerName, string $checkName) : int{
		$today = date("Y-m-d");
		$daily = $this->dailyWarnings->get($today, []);
		$key = strtolower($playerName);
		$daily[$key] = ($daily[$key] ?? 0) + 1;
		$this->dailyWarnings->set($today, $daily);
		$this->dailyWarnings->save();
		return (int) $daily[$key];
	}

	public function getDailyViolations(string $playerName) : int{
		$today = date("Y-m-d");
		$daily = $this->dailyWarnings->get($today, []);
		return (int) ($daily[strtolower($playerName)] ?? 0);
	}

	public function getMaxDailyViolations() : int{
		return $this->maxDailyViolations;
	}
}
