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


namespace pocketmine\anticheat\check;

use pocketmine\anticheat\AntiCheat;
use pocketmine\anticheat\PlayerData;
use pocketmine\Player;

abstract class Check{

	/** @var AntiCheat */
	protected $antiCheat;

	/** @var bool */
	protected $enabled = true;

	/** @var array */
	protected $config = [];

	/** @var int */
	protected $maxViolations = 5;

	/** @var string|null */
	private $idCache = null;

	public function __construct(AntiCheat $antiCheat, array $config){
		$this->antiCheat = $antiCheat;
		$this->config = $config;
		$this->enabled = (bool) ($config["enabled"] ?? true);
		$this->maxViolations = (int) ($config["max-violations"] ?? 5);
	}

	/**
	 * 检测器内部唯一标识，默认取短类名。
	 */
	public function getId() : string{
		if($this->idCache === null){
			$class = static::class;
			$pos = strrpos($class, "\\");
			$this->idCache = $pos === false ? $class : substr($class, $pos + 1);
		}
		return $this->idCache;
	}

	public function isEnabled() : bool{
		return $this->enabled;
	}

	public function getMaxViolations() : int{
		return $this->maxViolations;
	}

	public function getConfig() : array{
		return $this->config;
	}

	public function getAntiCheat() : AntiCheat{
		return $this->antiCheat;
	}

	protected function getPlayerData(Player $player) : PlayerData{
		return $this->antiCheat->getPlayerData($player);
	}

	/**
	 * 上报一次可疑行为：缓冲累积由 ViolationManager 统一处理。
	 */
	protected function flag(Player $player, string $detail, float $severity = 1.0){
		$this->antiCheat->getViolationManager()->flag(
			$player,
			$this->getId(),
			$this->getDisplayName(),
			$detail,
			$severity,
			$this->maxViolations
		);
	}

	/**
	 * 正常行为，衰减缓冲。
	 */
	protected function decay(Player $player, float $amount = 1.0){
		$this->antiCheat->getViolationManager()->decay($player, $this->getId(), $amount);
	}

	/**
	 * 是否允许执行实际惩罚（回退/踢出/封禁）。alert 模式下恒为 false。
	 */
	protected function shouldEnforce() : bool{
		return $this->antiCheat->isPunishMode();
	}

	public function getDisplayName() : string{
		return $this->getId();
	}

	abstract public function clearPlayerData(string $playerName);
}
