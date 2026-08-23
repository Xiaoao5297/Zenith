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

class FlyCheck extends Check{

	/** @var array */
	private $violations = [];

	/** @var array */
	private $lastCheckTime = [];

	/** @var array */
	private $lastOnGround = [];

	/** @var array */
	private $airTicks = [];

	/** @var array */
	private $lastY = [];

	public function __construct(AntiCheat $antiCheat, array $config){
		parent::__construct($antiCheat, $config);
	}

	public function clearPlayerData(string $playerName){
		$lower = strtolower($playerName);
		unset($this->violations[$lower]);
		unset($this->lastCheckTime[$lower]);
		unset($this->lastOnGround[$lower]);
		unset($this->airTicks[$lower]);
		unset($this->lastY[$lower]);
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

		if($player->getAllowFlight()) return;

		$name = $player->getName();
		$lower = strtolower($name);

		$maxViolations = (int) ($this->getConfig()["max-violations"] ?? 5);
		$maxAirTicks = (int) ($this->getConfig()["max-air-ticks"] ?? 20);
		$maxHoverHeight = (float) ($this->getConfig()["max-hover-height"] ?? 2.0);

		if($player->getLevel() === null){
			$this->clearPlayerData($name);
			return;
		}

		$onGround = $this->isOnGround($player);

		if($onGround){
			$this->lastOnGround[$lower] = clone $to;
			$this->airTicks[$lower] = 0;
			$current = isset($this->violations[$lower]) ? $this->violations[$lower] : 0;
			$this->violations[$lower] = max(0, $current - 1);
			$this->lastY[$lower] = $to->y;
			return;
		}

		$currentAirTicks = isset($this->airTicks[$lower]) ? $this->airTicks[$lower] + 1 : 1;
		$this->airTicks[$lower] = $currentAirTicks;

		if($currentAirTicks > $maxAirTicks){
			if($this->checkHoverFlight($player, $from, $to, $maxHoverHeight)){
				$violation = isset($this->violations[$lower]) ? $this->violations[$lower] + 1 : 1;
				$this->violations[$lower] = $violation;

				$detail = sprintf("空中滞留: %d tick, 限制: %d", $currentAirTicks, $maxAirTicks);
				$this->antiCheat->logCheat($name, "FlyCheck-Hover", $detail);

				if($violation >= $maxViolations){
					$player->teleport($from);
					$this->antiCheat->punish($player, "FlyCheck", $violation);
					return;
				}
			}
		}

		if($this->checkVerticalFlight($player, $from, $to)){
			$violation = isset($this->violations[$lower]) ? $this->violations[$lower] + 1 : 1;
			$this->violations[$lower] = $violation;

			$detail = sprintf("异常垂直移动: %.2f", $to->y - $from->y);
			$this->antiCheat->logCheat($name, "FlyCheck-Vertical", $detail);

			if($violation >= $maxViolations){
				$player->teleport($from);
				$this->antiCheat->punish($player, "FlyCheck", $violation);
				return;
			}
		}

		$this->lastY[$lower] = $to->y;
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
	 * @param Player   $player
	 * @param Location $from
	 * @param Location $to
	 * @param float    $maxHoverHeight
	 * @return bool
	 */
	private function checkHoverFlight(Player $player, Location $from, Location $to, $maxHoverHeight){
		if($player->hasEffect(8)){
			return false;
		}

		$lower = strtolower($player->getName());
		$lastGround = isset($this->lastOnGround[$lower]) ? $this->lastOnGround[$lower] : null;
		if($lastGround === null){
			return false;
		}

		$heightAboveGround = $to->y - $lastGround->y;

		if($heightAboveGround > $maxHoverHeight){
			return true;
		}

		return false;
	}

	/**
	 * @param Player   $player
	 * @param Location $from
	 * @param Location $to
	 * @return bool
	 */
	private function checkVerticalFlight(Player $player, Location $from, Location $to){
		if($player->hasEffect(8)){
			return false;
		}

		$lower = strtolower($player->getName());
		$prevY = isset($this->lastY[$lower]) ? $this->lastY[$lower] : null;
		if($prevY === null){
			return false;
		}

		$lastGround = isset($this->lastOnGround[$lower]) ? $this->lastOnGround[$lower] : null;

		$dy = $to->y - $from->y;
		$prevDy = $from->y - $prevY;

		// 连续两个tick高速上升
		if($dy > 0.6 && $prevDy > 0.5){
			return true;
		}

		// 单次超高上升
		if($dy > 0.85){
			return true;
		}

		// 长时间持续上升未下降
		if($dy > 0.1 && $prevDy > 0.1 && $lastGround !== null && ($to->y - $lastGround->y) > 4.0){
			return true;
		}

		return false;
	}
}
