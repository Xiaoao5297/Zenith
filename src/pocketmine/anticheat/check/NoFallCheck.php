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
use pocketmine\anticheat\MovementSnapshot;

/**
 * 摔落伤害规避检测（tick 驱动）。
 *
 * 服务端按 tick 累积下落距离，落地时与本次实际受到的摔落伤害对比；
 * 明显应受伤害却几乎未受伤时进入缓冲。水中/梯子/黏液块等合法缓冲会重置累计。
 */
class NoFallCheck extends MovementCheck{

	const SAFE_FALL_DISTANCE = 3.0;

	public function getDisplayName() : string{
		return "NoFall";
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
		if($snapshot->isInVehicle()) return;
		if($snapshot->isExempt()) return;

		$data = $this->getPlayerData($player);

		// 合法缓冲环境：重置累计
		if($snapshot->isInWater() or $snapshot->isOnLadder() or $snapshot->isOnSlime()){
			$data->setState("nofall.fallDistance", 0.0);
			$data->setState("nofall.fallTicks", 0);
			$data->setState("nofall.lastDamage", 0.0);
			return;
		}

		if(!$snapshot->isOnGround()){
			$dy = $snapshot->getDy();
			if($dy < 0){
				$fallDistance = (float) $data->getState("nofall.fallDistance", 0.0) - $dy;
				$data->setState("nofall.fallDistance", $fallDistance);
			}
			$data->setState("nofall.fallTicks", (int) $data->getState("nofall.fallTicks", 0) + $snapshot->getTickDelta());
			return;
		}

		// 落地结算
		$fallDistance = (float) $data->getState("nofall.fallDistance", 0.0);
		$actualDamage = (float) $data->getState("nofall.lastDamage", 0.0);
		$data->setState("nofall.fallDistance", 0.0);
		$data->setState("nofall.fallTicks", 0);
		$data->setState("nofall.lastDamage", 0.0);

		$expected = $fallDistance - self::SAFE_FALL_DISTANCE;
		$minFallDamage = (float) ($this->config["min-fall-damage"] ?? 4.0);

		if($expected >= $minFallDamage and $actualDamage < $expected * 0.25){
			$detail = sprintf("预期摔落伤害 %.2f, 实际 %.2f (下落 %.2f 格)", $expected, $actualDamage, $fallDistance);
			$this->flag($player, $detail, 1.0);
		}else{
			$this->decay($player, 1.0);
		}
	}

	/**
	 * 由 EntityDamageEvent(CAUSE_FALL) 调用，记录本次实际摔落伤害。
	 */
	public function checkFallDamage(Player $player, $damage){
		$this->getPlayerData($player)->setState("nofall.lastDamage", (float) $damage);
	}
}
