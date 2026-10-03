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
 * 异常传送检测（tick 驱动）。
 *
 * 传送、切世界、加入、受伤击退都会由核心写入豁免窗口，因此插件/命令传送不再误判。
 * 仅当"每 tick 位移"超过物理上限时才进入缓冲。
 */
class TeleportCheck extends MovementCheck{

	public function getDisplayName() : string{
		return "Teleport";
	}

	public function clearPlayerData(string $playerName){
	}

	public function checkMovement(Player $player, MovementSnapshot $snapshot){
		if(!$this->enabled) return;
		if($player->hasPermission("fpacheat.bypass")) return;
		if($player->isCreative() or $player->isSpectator()) return;
		if($player->getAllowFlight()) return;
		if($snapshot->isExempt()) return;

		// 受伤后短时间内允许被击退/爆炸推动
		$data = $this->getPlayerData($player);
		$lastDamage = $data->getLastDamageTick();
		if($lastDamage >= 0 and $snapshot->getTick() - $lastDamage < 20){
			$this->decay($player, 1.0);
			return;
		}

		$distance = $snapshot->getDistance();
		if($distance < 0.001){
			return;
		}

		$blocksPerTick = $distance / $snapshot->getTickDelta();
		$maxPerTick = (float) ($this->config["max-blocks-per-tick"] ?? 10.0);

		if($blocksPerTick > $maxPerTick){
			$detail = sprintf("%.2f 格/tick (限制 %.2f)", $blocksPerTick, $maxPerTick);
			$this->flag($player, $detail, 1.0);
		}else{
			$this->decay($player, 1.0);
		}
	}
}
