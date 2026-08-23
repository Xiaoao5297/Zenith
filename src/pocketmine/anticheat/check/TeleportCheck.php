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

class TeleportCheck extends Check{

	/** @var array */
	private $lastLocation = [];

	/** @var array */
	private $violations = [];

	/** @var array */
	private $lastTeleportTime = [];

	/** @var array */
	private $lastMoveTime = [];

	/** @var array */
	private $lastDamageTime = [];

	public function __construct(AntiCheat $antiCheat, array $config){
		parent::__construct($antiCheat, $config);
	}

	public function clearPlayerData(string $playerName){
		unset($this->lastLocation[$playerName]);
		unset($this->violations[$playerName]);
		unset($this->lastTeleportTime[$playerName]);
		unset($this->lastMoveTime[$playerName]);
		unset($this->lastDamageTime[$playerName]);
	}

	public function check(Player $player, $from, $to, float $elapsed){
		if(!$this->enabled) return;

		if($player->hasPermission("fpacheat.bypass")) return;
		if($player->isCreative() || $player->isSpectator()){
			$this->lastLocation[$player->getName()] = $to;
			return;
		}

		if($player->getAllowFlight()){
			$this->lastLocation[$player->getName()] = $to;
			return;
		}

		$name = $player->getName();

		if($player->getLevel() === null){
			$this->lastLocation[$name] = $to;
			return;
		}

		// 首次移动（进服/重生后第一次），保存位置但跳过检测
		if(!isset($this->lastLocation[$name])){
			$this->lastLocation[$name] = $to;
			return;
		}

		$maxDistance = (float) ($this->getConfig()["max-distance"] ?? 10.0);

		$distance = $from->distance($to);

		$currentTime = round(microtime(true) * 1000);
		$lastTeleport = isset($this->lastTeleportTime[$name]) ? $this->lastTeleportTime[$name] : 0;
		$lastMove = isset($this->lastMoveTime[$name]) ? $this->lastMoveTime[$name] : $currentTime;
		$lastDamage = isset($this->lastDamageTime[$name]) ? $this->lastDamageTime[$name] : 0;
		$moveDiff = $currentTime - $lastMove;
		$this->lastMoveTime[$name] = $currentTime;

		if($currentTime - $lastDamage < 1000){
			$this->lastLocation[$name] = $to;
			return;
		}

		if($distance > $maxDistance){
			if($currentTime - $lastTeleport < 3000){
				$this->lastLocation[$name] = $to;
				return;
			}

			$violation = isset($this->violations[$name]) ? $this->violations[$name] + 1 : 1;
			$this->violations[$name] = $violation;

			$detail = sprintf("异常传送: %.2f 方块, 限制: %.2f", $distance, $maxDistance);
			$this->antiCheat->logCheat($name, "TeleportCheck", $detail);

			if($violation >= $this->maxViolations){
				$this->antiCheat->punish($player, "TeleportCheck", $violation);
			}
		}else if($distance > 3.0 && $moveDiff < 30 && $moveDiff > 0){
			$speed = $distance / ($moveDiff / 1000.0);
			$maxSpeed = 50.0;

			if($speed > $maxSpeed){
				$violation = isset($this->violations[$name]) ? $this->violations[$name] + 1 : 1;
				$this->violations[$name] = $violation;

				$detail = sprintf("瞬间传送: %.2f 方块, 时间: %dms", $distance, $moveDiff);
				$this->antiCheat->logCheat($name, "TeleportCheck-Instant", $detail);

				if($violation >= $this->maxViolations){
					$this->antiCheat->punish($player, "TeleportCheck", $violation);
				}
			}else{
				$this->lastLocation[$name] = $to;
			}
		}else{
			// 正常移动时衰减违规计数
			if(isset($this->violations[$name]) && $this->violations[$name] > 0){
				$this->violations[$name] = max(0, $this->violations[$name] - 1);
			}
			$this->lastLocation[$name] = $to;
		}
	}

	public function recordTeleport(string $playerName){
		$this->lastTeleportTime[$playerName] = round(microtime(true) * 1000);
	}

	public function recordDamage(string $playerName){
		$this->lastDamageTime[$playerName] = round(microtime(true) * 1000);
	}
}
