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
use pocketmine\entity\Effect;
use pocketmine\anticheat\MovementSnapshot;

/**
 * 移动速度检测（tick 驱动）。
 *
 * 不再使用墙钟时间，速度按服务器 tick 折算；并考虑药水、方块、载具等上下文，
 * 命中只累积缓冲，由 ViolationManager 决定是否告警/惩罚。
 */
class SpeedCheck extends MovementCheck{

	const TICKS_PER_SECOND = 20;

	public function getDisplayName() : string{
		return "Speed";
	}

	public function clearPlayerData(string $playerName){
	}

	public function checkMovement(Player $player, MovementSnapshot $snapshot){
		if(!$this->enabled) return;
		if($player->hasPermission("fpacheat.bypass")) return;
		if($player->isCreative() or $player->isSpectator()) return;
		if($player->getAllowFlight()) return;
		if($snapshot->isInVehicle()) return;
		if($snapshot->isExempt()) return;

		$dist = $snapshot->getHorizontalDistance();

		// 极小移动忽略
		if($dist < 0.001) return;

		// 单次大位移视为传送，清空缓冲而不是判定
		if($dist > 4.0){
			$this->decay($player, 10.0);
			return;
		}

		$tickDelta = $snapshot->getTickDelta();
		$speedPerSecond = ($dist / $tickDelta) * self::TICKS_PER_SECOND;

		$limit = $this->calcSpeedLimit($player, $snapshot);

		if($speedPerSecond > $limit){
			$detail = sprintf("速度 %.2f b/s, 限制 %.2f b/s", $speedPerSecond, $limit);
			$this->flag($player, $detail, 1.0);
		}else{
			$this->decay($player, 1.0);
		}
	}

	private function calcSpeedLimit(Player $player, MovementSnapshot $snapshot) : float{
		if($player->isSprinting()){
			$base = (float) ($this->config["max-sprint-speed"] ?? 8.0);
		}else{
			$base = (float) ($this->config["max-walk-speed"] ?? 6.0);
		}

		if($player->hasEffect(Effect::SPEED)){
			$amplifier = $player->getEffect(Effect::SPEED)->getAmplifier();
			$base *= 1.2 + ($amplifier * 0.2);
		}

		if($snapshot->isInWater()) $base *= 1.4;
		if($snapshot->isOnLadder()) $base *= 1.6;
		if($snapshot->isOnIce()) $base *= 2.0;
		if($snapshot->isOnSlime()) $base *= 1.6;

		if($player->motionY > 0.1 or $player->motionY < -0.1){
			$base *= 1.2;
		}

		return $base;
	}
}
