<?php

namespace pocketmine\anticheat\check;

use pocketmine\anticheat\AntiCheat;
use pocketmine\Player;

class AutoClickerCheck extends Check{

	/** @var array */
	private $clickTimestamps = [];

	/** @var array */
	private $violations = [];

	/** @var array */
	private $lastClickTime = [];

	public function __construct(AntiCheat $antiCheat, array $config){
		parent::__construct($antiCheat, $config);
	}

	public function clearPlayerData(string $playerName){
		$lower = strtolower($playerName);
		unset($this->clickTimestamps[$lower]);
		unset($this->violations[$lower]);
		unset($this->lastClickTime[$lower]);
	}

	/**
	 * @param Player $player
	 */
	public function checkAttack(Player $player){
		if(!$this->enabled) return;

		if($player->hasPermission("fpacheat.bypass")) return;
		if($player->isCreative() || $player->isSpectator()) return;

		$name = $player->getName();
		$lower = strtolower($name);
		$currentTime = round(microtime(true) * 1000);

		if(!isset($this->clickTimestamps[$lower])){
			$this->clickTimestamps[$lower] = [];
		}

		$timestamps = &$this->clickTimestamps[$lower];

		$newTimestamps = [];
		foreach($timestamps as $ts){
			if($currentTime - $ts <= 1000){
				$newTimestamps[] = $ts;
			}
		}
		$newTimestamps[] = $currentTime;
		$timestamps = $newTimestamps;

		$clicksPerSecond = count($timestamps);
		$maxCPS = (int) ($this->getConfig()["max-cps"] ?? 20);

		if($clicksPerSecond > $maxCPS){
			$violation = isset($this->violations[$lower]) ? $this->violations[$lower] + 1 : 1;
			$this->violations[$lower] = $violation;

			$detail = sprintf("点击速度: %d CPS, 限制: %d", $clicksPerSecond, $maxCPS);
			$this->antiCheat->logCheat($name, "AutoClickerCheck", $detail);

			$maxViolations = (int) ($this->getConfig()["max-violations"] ?? 5);
			if($violation >= $maxViolations){
				$this->antiCheat->punish($player, "AutoClickerCheck", $violation);
			}
		}else{
			$current = isset($this->violations[$lower]) ? $this->violations[$lower] : 0;
			if($current > 0){
				$this->violations[$lower] = $current - 1;
			}
		}

		$this->checkClickConsistency($player, $timestamps);
	}

	/**
	 * @param Player $player
	 * @param array  $timestamps
	 */
	private function checkClickConsistency(Player $player, array $timestamps){
		if(count($timestamps) < 10){
			return;
		}

		$times = array_values($timestamps);
		$intervals = [];

		for($i = 0; $i < count($times) - 1; $i++){
			$intervals[] = $times[$i + 1] - $times[$i];
		}

		$mean = 0;
		foreach($intervals as $interval){
			$mean += $interval;
		}
		$mean /= count($intervals);

		$variance = 0;
		foreach($intervals as $interval){
			$variance += pow($interval - $mean, 2);
		}
		$variance /= count($intervals);
		$stdDev = sqrt($variance);

		$consistencyThreshold = (float) ($this->getConfig()["consistency-threshold"] ?? 10.0);

		if($stdDev < $consistencyThreshold){
			$name = $player->getName();
			$lower = strtolower($name);
			$violation = isset($this->violations[$lower]) ? $this->violations[$lower] + 1 : 1;
			$this->violations[$lower] = $violation;

			$detail = sprintf("点击过于一致: 标准差 %.2f ms", $stdDev);
			$this->antiCheat->logCheat($name, "AutoClickerCheck-Consistency", $detail);

			$maxViolations = (int) ($this->getConfig()["max-violations"] ?? 5);
			if($violation >= $maxViolations){
				$this->antiCheat->punish($player, "AutoClickerCheck", $violation);
			}
		}
	}
}
