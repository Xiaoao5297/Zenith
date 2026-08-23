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
use pocketmine\entity\Entity;
use pocketmine\event\entity\EntityDamageByEntityEvent;

class ReachCheck extends Check{

	/** @var array */
	private $violations = [];

	/** @var array */
	private $lastCheckTime = [];

	public function __construct(AntiCheat $antiCheat, array $config){
		parent::__construct($antiCheat, $config);
	}

	public function clearPlayerData(string $playerName){
		unset($this->violations[$playerName]);
		unset($this->lastCheckTime[$playerName]);
	}

	public function checkAttack(Player $player, Entity $target, EntityDamageByEntityEvent $event){
		if(!$this->enabled) return;

		if($player->isOp() || $player->isCreative() || $player->isSpectator()){
			return;
		}

		$maxReach = (float) ($this->getConfig()["max-attack-reach"] ?? 4.5);
		$maxViolations = (int) ($this->getConfig()["max-violations"] ?? 4);

		$distance = $this->calculateDistance($player, $target);

		$name = $player->getName();

		if($distance > $maxReach){
			$violation = isset($this->violations[$name]) ? $this->violations[$name] + 1 : 1;
			$this->violations[$name] = $violation;

			$detail = sprintf("攻击距离: %.2f, 限制: %.2f", $distance, $maxReach);
			$this->antiCheat->logCheat($player->getName(), "ReachCheck-Attack", $detail);

			if($violation >= $maxViolations){
				$event->setCancelled(true);
				$this->antiCheat->punish($player, "ReachCheck", $violation);
				$this->violations[$name] = 0;
			}
		}else{
			// 正常距离时衰减违规
			if(isset($this->violations[$name]) && $this->violations[$name] > 0){
				$this->violations[$name] = max(0, $this->violations[$name] - 1);
			}
		}
	}

	public function checkBlockBreak(Player $player, Block $block){
		if(!$this->enabled) return;

		if($player->isOp() || $player->isCreative() || $player->isSpectator()){
			return;
		}

		$maxReach = (float) ($this->getConfig()["max-block-reach"] ?? 5.5);
		$maxViolations = (int) ($this->getConfig()["max-violations"] ?? 4);

		$distance = $this->calculateBlockDistance($player, $block);

		$name = $player->getName();

		if($distance > $maxReach){
			$violation = isset($this->violations[$name]) ? $this->violations[$name] + 1 : 1;
			$this->violations[$name] = $violation;

			$detail = sprintf("破坏距离: %.2f, 限制: %.2f", $distance, $maxReach);
			$this->antiCheat->logCheat($player->getName(), "ReachCheck-Break", $detail);

			if($violation >= $maxViolations){
				$this->antiCheat->punish($player, "ReachCheck", $violation);
				$this->violations[$name] = 0;
			}
		}
	}

	public function checkBlockPlace(Player $player, Block $block){
		if(!$this->enabled) return;

		if($player->isOp() || $player->isCreative() || $player->isSpectator()){
			return;
		}

		$maxReach = (float) ($this->getConfig()["max-block-reach"] ?? 5.5);
		$maxViolations = (int) ($this->getConfig()["max-violations"] ?? 4);

		$distance = $this->calculateBlockDistance($player, $block);

		$name = $player->getName();

		if($distance > $maxReach){
			$violation = isset($this->violations[$name]) ? $this->violations[$name] + 1 : 1;
			$this->violations[$name] = $violation;

			$detail = sprintf("放置距离: %.2f, 限制: %.2f", $distance, $maxReach);
			$this->antiCheat->logCheat($player->getName(), "ReachCheck-Place", $detail);

			if($violation >= $maxViolations){
				$this->antiCheat->punish($player, "ReachCheck", $violation);
				$this->violations[$name] = 0;
			}
		}
	}

	public function checkInteract(Player $player, Block $block){
		if(!$this->enabled) return;

		if($player->isOp() || $player->isCreative() || $player->isSpectator()){
			return;
		}

		$maxReach = (float) ($this->getConfig()["max-interact-reach"] ?? 5.5);
		$maxViolations = (int) ($this->getConfig()["max-violations"] ?? 4);

		$distance = $this->calculateBlockDistance($player, $block);

		$name = $player->getName();

		if($distance > $maxReach){
			$violation = isset($this->violations[$name]) ? $this->violations[$name] + 1 : 1;
			$this->violations[$name] = $violation;

			$detail = sprintf("交互距离: %.2f, 限制: %.2f", $distance, $maxReach);
			$this->antiCheat->logCheat($player->getName(), "ReachCheck-Interact", $detail);

			if($violation >= $maxViolations){
				$this->antiCheat->punish($player, "ReachCheck", $violation);
				$this->violations[$name] = 0;
			}
		}
	}

	public function checkContainerAccess(Player $player, $holder){
		if(!$this->enabled) return;

		if($player->isOp() || $player->isCreative() || $player->isSpectator()){
			return;
		}

		$maxReach = (float) ($this->getConfig()["max-container-reach"] ?? 5.5);
		$maxViolations = (int) ($this->getConfig()["max-violations"] ?? 4);

		$bx = 0; $by = 0; $bz = 0;

		if($holder instanceof Block){
			$bx = $holder->x + 0.5;
			$by = $holder->y + 0.5;
			$bz = $holder->z + 0.5;
		}else if($holder instanceof Entity){
			$bx = $holder->x;
			$by = $holder->y;
			$bz = $holder->z;
		}else{
			return;
		}

		$px = $player->x;
		$py = $player->y + $player->getEyeHeight();
		$pz = $player->z;

		$distance = sqrt(pow($bx - $px, 2) + pow($by - $py, 2) + pow($bz - $pz, 2));

		$name = $player->getName();

		if($distance > $maxReach){
			$violation = isset($this->violations[$name]) ? $this->violations[$name] + 1 : 1;
			$this->violations[$name] = $violation;

			$detail = sprintf("容器访问距离: %.2f, 限制: %.2f", $distance, $maxReach);
			$this->antiCheat->logCheat($player->getName(), "ReachCheck-Container", $detail);

			if($violation >= $maxViolations){
				$this->antiCheat->punish($player, "ReachCheck", $violation);
				$this->violations[$name] = 0;
			}
		}
	}

	private function calculateDistance(Player $player, Entity $target){
		$px = $player->x;
		$py = $player->y + $player->getEyeHeight();
		$pz = $player->z;

		$tx = $target->x;
		// 某些自定义实体没有 getHeight() 方法，安全兜底
		$ty = $target->y + (method_exists($target, 'getHeight') ? $target->getHeight() / 2 : 0.5);
		$tz = $target->z;

		return sqrt(pow($tx - $px, 2) + pow($ty - $py, 2) + pow($tz - $pz, 2));
	}

	private function calculateBlockDistance(Player $player, Block $block){
		$px = $player->x;
		$py = $player->y + $player->getEyeHeight();
		$pz = $player->z;

		$bx = $block->x + 0.5;
		$by = $block->y + 0.5;
		$bz = $block->z + 0.5;

		return sqrt(pow($bx - $px, 2) + pow($by - $py, 2) + pow($bz - $pz, 2));
	}
}
