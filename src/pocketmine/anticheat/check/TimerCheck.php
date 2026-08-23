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
use pocketmine\Player;

class TimerCheck extends Check{

	/** @var array */
	private $lastMoveTime = [];

	/** @var array */
	private $violations = [];

	/** @var array */
	private $fastMoveCount = [];

	public function __construct(AntiCheat $antiCheat, array $config){
		parent::__construct($antiCheat, $config);
	}

	public function clearPlayerData(string $playerName){
		unset($this->lastMoveTime[$playerName]);
		unset($this->violations[$playerName]);
		unset($this->fastMoveCount[$playerName]);
	}

	public function check(Player $player, $from, $to, float $elapsed){
		if(!$this->enabled) return;

		if($player->hasPermission("fpacheat.bypass")) return;
		if($player->isCreative() || $player->isSpectator()) return;

		$name = $player->getName();

		if($player->getLevel() === null){
			$this->lastMoveTime[$name] = round(microtime(true) * 1000);
			return;
		}

		$distance = $from->distance($to);
		if($distance < 0.01){
			return;
		}

		$currentTime = round(microtime(true) * 1000);
		$lastTime = isset($this->lastMoveTime[$name]) ? $this->lastMoveTime[$name] : null;

		if($lastTime === null){
			$this->lastMoveTime[$name] = $currentTime;
			return;
		}

		$timeDiff = $currentTime - $lastTime;
		$this->lastMoveTime[$name] = $currentTime;

		if($timeDiff < 40){
			$fastMoves = isset($this->fastMoveCount[$name]) ? $this->fastMoveCount[$name] + 1 : 1;
			$this->fastMoveCount[$name] = $fastMoves;

			if($fastMoves > 5){
				$violation = isset($this->violations[$name]) ? $this->violations[$name] + 1 : 1;
				$this->violations[$name] = $violation;

				$detail = sprintf("移动间隔过短: %d ms, 连续次数: %d", $timeDiff, $fastMoves);
				$this->antiCheat->logCheat($name, "TimerCheck", $detail);

				if($violation >= $this->maxViolations){
					$this->antiCheat->punish($player, "TimerCheck", $violation);
				}
			}
		}else{
			$this->fastMoveCount[$name] = 0;
			$current = isset($this->violations[$name]) ? $this->violations[$name] : 0;
			if($current > 0){
				$this->violations[$name] = $current - 1;
			}
		}
	}
}
