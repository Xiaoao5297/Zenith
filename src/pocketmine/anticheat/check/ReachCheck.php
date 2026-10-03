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

use pocketmine\Player;
use pocketmine\block\Block;
use pocketmine\entity\Entity;

/**
 * 交互距离检测：攻击、破坏、放置、交互、容器。
 *
 * 命中统一进入缓冲；距离按实体碰撞箱半宽修正，避免贴脸攻击误判。
 */
class ReachCheck extends Check{

	public function getDisplayName() : string{
		return "Reach";
	}

	public function clearPlayerData(string $playerName){
	}

	public function checkAttack(Player $player, Entity $target, $event = null){
		if(!$this->enabled) return;
		if($player->isOp() or $player->isCreative() or $player->isSpectator()) return;

		$maxReach = (float) ($this->config["max-attack-reach"] ?? 6.0);
		$distance = $this->calculateDistance($player, $target);

		if($distance > $maxReach){
			$detail = sprintf("攻击距离 %.2f, 限制 %.2f", $distance, $maxReach);
			$this->flag($player, $detail, 1.0);
		}else{
			$this->decay($player, 1.0);
		}
	}

	public function checkBlockBreak(Player $player, Block $block){
		if(!$this->enabled) return;
		if($player->isOp() or $player->isCreative() or $player->isSpectator()) return;

		$maxReach = (float) ($this->config["max-block-reach"] ?? 6.0);
		$distance = $this->calculateBlockDistance($player, $block);

		if($distance > $maxReach){
			$detail = sprintf("破坏距离 %.2f, 限制 %.2f", $distance, $maxReach);
			$this->flag($player, $detail, 1.0);
		}else{
			$this->decay($player, 1.0);
		}
	}

	public function checkBlockPlace(Player $player, Block $block){
		if(!$this->enabled) return;
		if($player->isOp() or $player->isCreative() or $player->isSpectator()) return;

		$maxReach = (float) ($this->config["max-block-reach"] ?? 6.0);
		$distance = $this->calculateBlockDistance($player, $block);

		if($distance > $maxReach){
			$detail = sprintf("放置距离 %.2f, 限制 %.2f", $distance, $maxReach);
			$this->flag($player, $detail, 1.0);
		}else{
			$this->decay($player, 1.0);
		}
	}

	public function checkInteract(Player $player, Block $block){
		if(!$this->enabled) return;
		if($player->isOp() or $player->isCreative() or $player->isSpectator()) return;

		$maxReach = (float) ($this->config["max-interact-reach"] ?? 6.0);
		$distance = $this->calculateBlockDistance($player, $block);

		if($distance > $maxReach){
			$detail = sprintf("交互距离 %.2f, 限制 %.2f", $distance, $maxReach);
			$this->flag($player, $detail, 1.0);
		}else{
			$this->decay($player, 1.0);
		}
	}

	public function checkContainerAccess(Player $player, $holder){
		if(!$this->enabled) return;
		if($player->isOp() or $player->isCreative() or $player->isSpectator()) return;

		$maxReach = (float) ($this->config["max-container-reach"] ?? 6.0);

		$bx = 0; $by = 0; $bz = 0;
		if($holder instanceof Block){
			$bx = $holder->x + 0.5;
			$by = $holder->y + 0.5;
			$bz = $holder->z + 0.5;
		}elseif($holder instanceof Entity){
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

		if($distance > $maxReach){
			$detail = sprintf("容器访问距离 %.2f, 限制 %.2f", $distance, $maxReach);
			$this->flag($player, $detail, 1.0);
		}else{
			$this->decay($player, 1.0);
		}
	}

	private function calculateDistance(Player $player, Entity $target) : float{
		$px = $player->x;
		$py = $player->y + $player->getEyeHeight();
		$pz = $player->z;

		$tx = $target->x;
		$ty = $target->y + (method_exists($target, 'getHeight') ? $target->getHeight() / 2 : 0.5);
		$tz = $target->z;

		// 减去实体半宽，贴脸攻击不会因为中心点距离而误判
		return max(0.0, sqrt(pow($tx - $px, 2) + pow($ty - $py, 2) + pow($tz - $pz, 2)) - 0.3);
	}

	private function calculateBlockDistance(Player $player, Block $block) : float{
		$px = $player->x;
		$py = $player->y + $player->getEyeHeight();
		$pz = $player->z;

		$bx = $block->x + 0.5;
		$by = $block->y + 0.5;
		$bz = $block->z + 0.5;

		return sqrt(pow($bx - $px, 2) + pow($by - $py, 2) + pow($bz - $pz, 2));
	}
}
