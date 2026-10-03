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
 * 飞行检测（tick 驱动）。
 *
 * 只依据服务器 tick 累积的滞空时间与相对地面的悬停高度判定，
 * 并豁免跳跃提升、水中、梯子、黏液块、载具、传送窗口等合法情形。
 */
class FlyCheck extends MovementCheck{

	public function getDisplayName() : string{
		return "Fly";
	}

	public function clearPlayerData(string $playerName){
	}

	public function sampledPerTick() : bool{
		return true;
	}

	public function checkMovement(Player $player, MovementSnapshot $snapshot){
		if(!$this->enabled) return;
		if($player->hasPermission("fpacheat.bypass")) return;
		if($player->isCreative() or $player->isSpectator()) return;
		if($player->getAllowFlight()) return;
		if($snapshot->isInVehicle()) return;
		if($snapshot->isExempt()) return;

		$data = $this->getPlayerData($player);

		// 合法滞空环境：重置计数
		if($snapshot->isInWater() or $snapshot->isOnLadder() or $snapshot->isOnSlime()){
			$data->setState("fly.airTicks", 0);
			return;
		}

		// 跳跃提升会改变垂直运动，豁免
		if($player->hasEffect(Effect::JUMP)){
			$data->setState("fly.airTicks", 0);
			return;
		}

		if($snapshot->isOnGround()){
			$data->setState("fly.airTicks", 0);
			$data->setState("fly.lastGroundY", $snapshot->getTo()->y);
			$this->decay($player, 1.0);
			return;
		}

		$airTicks = (int) $data->getState("fly.airTicks", 0) + $snapshot->getTickDelta();
		$data->setState("fly.airTicks", $airTicks);

		$maxAirTicks = (int) ($this->config["max-air-ticks"] ?? 40);
		if($airTicks < $maxAirTicks){
			return;
		}

		$lastGroundY = (float) $data->getState("fly.lastGroundY", $snapshot->getTo()->y);
		$hoverHeight = $snapshot->getTo()->y - $lastGroundY;
		$maxHoverHeight = (float) ($this->config["max-hover-height"] ?? 3.0);

		// 持续滞空、高于地面且没有明显下落 => 可疑
		if($hoverHeight > $maxHoverHeight and $snapshot->getDy() > -0.08){
			$detail = sprintf("悬空 %d tick, 高度 %.2f, dy %.2f", $airTicks, $hoverHeight, $snapshot->getDy());
			$this->flag($player, $detail, 1.0);
		}else{
			$this->decay($player, 1.0);
		}
	}
}
