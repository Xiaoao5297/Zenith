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

class KnockbackCheck extends Check{

	/** @var array */
	private $violations = [];

	/** @var array */
	private $preDamageLocation = [];

	/** @var array */
	private $lastDamageTime = [];

	/** @var array */
	private $noKnockbackCount = [];

	public function __construct(AntiCheat $antiCheat, array $config){
		parent::__construct($antiCheat, $config);
	}

	public function clearPlayerData(string $playerName){
		unset($this->violations[$playerName]);
		unset($this->preDamageLocation[$playerName]);
		unset($this->lastDamageTime[$playerName]);
		unset($this->noKnockbackCount[$playerName]);
	}

	public function checkKnockback(Player $player){
		if(!$this->enabled) return;

		if($player->hasPermission("fpacheat.bypass")) return;
		if($player->isCreative() || $player->isSpectator()) return;

		$name = $player->getName();

		$this->preDamageLocation[$name] = $player->getLocation();
		$this->lastDamageTime[$name] = round(microtime(true) * 1000);
	}

	public function checkKnockbackMovement(Player $player, $from, $to){
		if(!$this->enabled) return;

		if($player->hasPermission("fpacheat.bypass")) return;
		if($player->isCreative() || $player->isSpectator()) return;

		$name = $player->getName();

		$damageTime = isset($this->lastDamageTime[$name]) ? $this->lastDamageTime[$name] : null;
		if($damageTime === null){
			return;
		}

		$currentTime = round(microtime(true) * 1000);
		$timeSinceDamage = $currentTime - $damageTime;

		if($timeSinceDamage > 500 || $timeSinceDamage < 50){
			return;
		}

		$preDamage = isset($this->preDamageLocation[$name]) ? $this->preDamageLocation[$name] : null;
		if($preDamage === null){
			return;
		}

		if($preDamage->getLevel() !== $player->getLevel()){
			unset($this->preDamageLocation[$name]);
			unset($this->lastDamageTime[$name]);
			return;
		}

		$distance = $preDamage->distance($to);

		$minKnockback = (float) ($this->getConfig()["min-knockback-distance"] ?? 0.3);
		$requiredCount = (int) ($this->getConfig()["required-count"] ?? 3);

		if($distance < $minKnockback){
			$count = isset($this->noKnockbackCount[$name]) ? $this->noKnockbackCount[$name] + 1 : 1;
			$this->noKnockbackCount[$name] = $count;

			if($count >= $requiredCount){
				$violation = isset($this->violations[$name]) ? $this->violations[$name] + 1 : 1;
				$this->violations[$name] = $violation;

				$detail = sprintf("无击退检测: 移动距离 %.2f, 最小 %.2f, 连续 %d 次", $distance, $minKnockback, $count);
				$this->antiCheat->logCheat($name, "KnockbackCheck", $detail);

				if($violation >= $this->maxViolations){
					$this->antiCheat->punish($player, "KnockbackCheck", $violation);
				}

				$this->noKnockbackCount[$name] = 0;
			}
		}else{
			$this->noKnockbackCount[$name] = 0;
		}

		unset($this->preDamageLocation[$name]);
		unset($this->lastDamageTime[$name]);
	}
}
