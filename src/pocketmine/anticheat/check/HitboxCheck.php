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
use pocketmine\entity\Entity;
use pocketmine\math\Vector3;

/**
 * 命中框检测：攻击距离、背身角度、穿墙。
 *
 * 眼高使用实体真实值，命中进入统一缓冲。
 */
class HitboxCheck extends Check{

	public function getDisplayName() : string{
		return "Hitbox";
	}

	public function clearPlayerData(string $playerName){
	}

	public function checkHitbox(Player $player, Entity $target){
		if(!$this->enabled) return;
		if($player->hasPermission("fpacheat.bypass")) return;
		if($player->isCreative() or $player->isSpectator()) return;

		$eyeX = $player->x;
		$eyeY = $player->y + $player->getEyeHeight();
		$eyeZ = $player->z;

		$targetHeight = method_exists($target, 'getHeight') ? $target->getHeight() : 1.8;

		$minX = $target->x - 0.3;
		$maxX = $target->x + 0.3;
		$minY = $target->y;
		$maxY = $target->y + $targetHeight;
		$minZ = $target->z - 0.3;
		$maxZ = $target->z + 0.3;

		$closestX = max($minX, min($maxX, $eyeX));
		$closestY = max($minY, min($maxY, $eyeY));
		$closestZ = max($minZ, min($maxZ, $eyeZ));

		$distance = sqrt(
			pow($closestX - $eyeX, 2) +
			pow($closestY - $eyeY, 2) +
			pow($closestZ - $eyeZ, 2)
		);

		$maxReach = (float) ($this->config["max-attack-reach"] ?? 6.0);
		$threshold = (float) ($this->config["max-horizontal-range"] ?? 0.5);

		if($distance > $maxReach + $threshold){
			$detail = sprintf("命中框距离 %.2f, 限制 %.2f", $distance, $maxReach);
			$this->flag($player, $detail, 1.0);
			return;
		}

		$angleDiff = $this->getAngleDifference($player, $target);
		if($angleDiff > 130 and $distance > 2.5){
			$detail = sprintf("背身角度 %.1f 度, 距离 %.2f", $angleDiff, $distance);
			$this->flag($player, $detail, 1.0);
			return;
		}

		if($distance > 3.0 and !$this->hasLineOfSight($player, $target)){
			$this->flag($player, "穿墙攻击", 1.0);
			return;
		}

		$this->decay($player, 1.0);
	}

	private function getAngleDifference(Player $player, Entity $target) : float{
		$dx = $target->x - $player->x;
		$dz = $target->z - $player->z;

		$yawToTarget = rad2deg(atan2(-$dx, $dz));
		$playerYaw = $player->getYaw();

		$diff = abs($yawToTarget - $playerYaw);
		if($diff > 180){
			$diff = 360 - $diff;
		}

		return $diff;
	}

	private function hasLineOfSight(Player $player, Entity $target) : bool{
		$level = $player->getLevel();
		if($level === null){
			return true;
		}

		$eyeX = $player->x;
		$eyeY = $player->y + $player->getEyeHeight();
		$eyeZ = $player->z;

		$targetX = $target->x;
		$targetY = $target->y + (method_exists($target, 'getHeight') ? $target->getHeight() / 2 : 0.9);
		$targetZ = $target->z;

		$distance = sqrt(
			pow($targetX - $eyeX, 2) +
			pow($targetY - $eyeY, 2) +
			pow($targetZ - $eyeZ, 2)
		);

		if($distance < 1.5){
			return true;
		}

		$steps = (int) ceil($distance);
		$dx = ($targetX - $eyeX) / $steps;
		$dy = ($targetY - $eyeY) / $steps;
		$dz = ($targetZ - $eyeZ) / $steps;

		$solidCount = 0;
		for($i = 1; $i < $steps; $i++){
			$block = $level->getBlock(new Vector3($eyeX + $dx * $i, $eyeY + $dy * $i, $eyeZ + $dz * $i));
			if($block !== null and $block->isSolid()){
				$solidCount++;
				if($solidCount >= 2){
					return false;
				}
			}
		}

		return true;
	}
}
