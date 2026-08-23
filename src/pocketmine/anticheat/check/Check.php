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

abstract class Check{

	/** @var AntiCheat */
	protected $antiCheat;

	/** @var bool */
	protected $enabled = true;

	/** @var array */
	protected $config = [];

	/** @var int */
	protected $maxViolations = 5;

	public function __construct(AntiCheat $antiCheat, array $config){
		$this->antiCheat = $antiCheat;
		$this->config = $config;
		$this->enabled = (bool) ($config["enabled"] ?? true);
		$this->maxViolations = (int) ($config["max-violations"] ?? 5);
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

	abstract public function clearPlayerData(string $playerName);
}
