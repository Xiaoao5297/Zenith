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
use pocketmine\block\Block;
use pocketmine\level\Location;
use pocketmine\math\Vector3;

class NoFallCheck extends Check{

	/** @var array */
	private $violations = [];

	/** @var array */
	private $wasOnGround = [];

	/** @var array */
	private $airTicks = [];

	/** @var array */
	private $lastFallDistance = [];

	public function __construct(AntiCheat $antiCheat, array $config){
		parent::__construct($antiCheat, $config);
	}

	public function clearPlayerData(string $playerName){
		$lower = strtolower($playerName);
		unset($this->violations[$lower]);
		unset($this->wasOnGround[$lower]);
		unset($this->airTicks[$lower]);
		unset($this->lastFallDistance[$lower]);
	}

	/**
	 * @param Player   $player
	 * @param Location $from
	 * @param Location $to
	 */
	public function check(Player $player, $from, $to){
		if(!$this->enabled) return;

		if($player->hasPermission("fpacheat.bypass")) return;
		if($player->isCreative() || $player->isSpectator()) return;

		if($player->hasEffect(8)) return;

		$name = $player->getName();
		$lower = strtolower($name);

		$onGround = $this->isOnGround($player);
		$wasGrounded = isset($this->wasOnGround[$lower]) ? $this->wasOnGround[$lower] : true;

		$this->wasOnGround[$lower] = $onGround;

		if(!$onGround){
			$currentAirTicks = isset($this->airTicks[$lower]) ? $this->airTicks[$lower] + 1 : 1;
			$this->airTicks[$lower] = $currentAirTicks;
			return;
		}

		if($wasGrounded){
			$this->airTicks[$lower] = 0;
			return;
		}

		$airTicksValue = isset($this->airTicks[$lower]) ? $this->airTicks[$lower] : 0;
		if($airTicksValue < 3){
			$this->airTicks[$lower] = 0;
			return;
		}

		$expectedDamage = $this->calculateFallDamage($airTicksValue);
		$actualDamage = isset($this->lastFallDistance[$lower]) ? $this->lastFallDistance[$lower] : 0.0;

		if($expectedDamage > 2.0 && $actualDamage < $expectedDamage * 0.5){
			$violation = isset($this->violations[$lower]) ? $this->violations[$lower] + 1 : 1;
			$this->violations[$lower] = $violation;

			$detail = sprintf("预期跌落伤害: %.2f, 实际: %.2f", $expectedDamage, $actualDamage);
			$this->antiCheat->logCheat($name, "NoFallCheck", $detail);

			$maxViolations = (int) ($this->getConfig()["max-violations"] ?? 5);
			if($violation >= $maxViolations){
				$this->antiCheat->punish($player, "NoFallCheck", $violation);
			}
		}

		$this->airTicks[$lower] = 0;
	}

	/**
	 * @param Player $player
	 * @param float  $damage
	 */
	public function checkFallDamage(Player $player, $damage){
		$lower = strtolower($player->getName());
		$this->lastFallDistance[$lower] = (double) $damage;
	}

	/**
	 * @param Player $player
	 * @return bool
	 */
	private function isOnGround(Player $player){
		$loc = $player->getLocation();
		$blockBelow = $player->getLevel()->getBlock(new Vector3($loc->getFloorX(), $loc->getFloorY() - 1, $loc->getFloorZ()));

		if($blockBelow->getId() !== Block::AIR){
			return true;
		}

		for($x = -1; $x <= 1; $x++){
			for($z = -1; $z <= 1; $z++){
				$block = $player->getLevel()->getBlock(new Vector3($loc->getFloorX() + $x, $loc->getFloorY() - 1, $loc->getFloorZ() + $z));
				if($block->getId() !== Block::AIR){
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * @param int $airTicks
	 * @return float
	 */
	private function calculateFallDamage($airTicks){
		if($airTicks < 3){
			return 0;
		}

		$fallDistance = ($airTicks - 3) * 0.1;
		return $fallDistance * 2;
	}
}
