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

/**
 * 防击退检测（tick 驱动）。
 *
 * 受伤时记录位置与 tick，之后短暂窗口内检查是否产生了应有的位移。
 * 使用服务器 tick 而非墙钟，并只在连续多次未位移时进入缓冲。
 */
class KnockbackCheck extends Check{

	public function getDisplayName() : string{
		return "Knockback";
	}

	public function clearPlayerData(string $playerName){
	}

	public function checkKnockback(Player $player){
		if(!$this->enabled) return;
		if($player->hasPermission("fpacheat.bypass")) return;
		if($player->isCreative() or $player->isSpectator()) return;

		$data = $this->getPlayerData($player);
		$data->setState("knockback.pre", clone $player->getLocation());
		$data->setState("knockback.tick", $this->antiCheat->getServer()->getTick());
	}

	public function checkKnockbackMovement(Player $player, $from, $to){
		if(!$this->enabled) return;
		if($player->hasPermission("fpacheat.bypass")) return;
		if($player->isCreative() or $player->isSpectator()) return;

		$data = $this->getPlayerData($player);

		$pre = $data->getState("knockback.pre");
		$damageTick = $data->getState("knockback.tick");
		if($pre === null or $damageTick === null){
			return;
		}

		$elapsed = $this->antiCheat->getServer()->getTick() - $damageTick;
		if($elapsed > 10 or $elapsed < 1){
			$data->removeState("knockback.pre");
			$data->removeState("knockback.tick");
			return;
		}

		if($pre->getLevel() !== $player->getLevel()){
			$data->removeState("knockback.pre");
			$data->removeState("knockback.tick");
			return;
		}

		$distance = $pre->distance($to);
		$minKnockback = (float) ($this->config["min-knockback-distance"] ?? 0.3);
		$requiredCount = (int) ($this->config["required-count"] ?? 3);

		if($distance < $minKnockback){
			$misses = (int) $data->getState("knockback.misses", 0) + 1;
			$data->setState("knockback.misses", $misses);

			if($misses >= $requiredCount){
				$detail = sprintf("连续 %d 次未产生击退 (位移 %.2f, 最小 %.2f)", $misses, $distance, $minKnockback);
				$this->flag($player, $detail, 1.0);
				$data->setState("knockback.misses", 0);
			}
		}else{
			$data->setState("knockback.misses", 0);
			$this->decay($player, 1.0);
		}

		$data->removeState("knockback.pre");
		$data->removeState("knockback.tick");
	}
}
