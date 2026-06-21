<?php

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
