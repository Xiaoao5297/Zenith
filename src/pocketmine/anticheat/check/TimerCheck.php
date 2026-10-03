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
 * 游戏加速（Timer）检测（tick 驱动）。
 *
 * 旧实现用墙钟 40ms 阈值，移动包抖动必然误判。现改为统计一个 tick 窗口内
 * 产生的有效移动采样数：正常客户端每 tick 至多一次，超量才进入缓冲。
 */
class TimerCheck extends MovementCheck{

	public function getDisplayName() : string{
		return "Timer";
	}

	public function clearPlayerData(string $playerName){
	}

	public function checkMovement(Player $player, MovementSnapshot $snapshot){
		if(!$this->enabled) return;
		if($player->hasPermission("fpacheat.bypass")) return;
		if($player->isCreative() or $player->isSpectator()) return;
		if($snapshot->isExempt()) return;

		// 只有真正的位移才计入，忽略纯视角/极小抖动
		if($snapshot->getDistance() < 0.05){
			return;
		}

		$data = $this->getPlayerData($player);
		$tick = $snapshot->getTick();

		$window = (int) ($this->config["window-ticks"] ?? 20);
		$maxSamples = (int) ($this->config["max-samples"] ?? 30);

		$samples = $data->getState("timer.samples", []);
		$samples[] = $tick;

		$cutoff = $tick - $window;
		$samples = array_values(array_filter($samples, function($t) use ($cutoff){
			return $t > $cutoff;
		}));
		$data->setState("timer.samples", $samples);

		if(count($samples) > $maxSamples){
			$detail = sprintf("%d tick 内 %d 次移动采样 (限制 %d)", $window, count($samples), $maxSamples);
			$this->flag($player, $detail, 1.0);
			$data->setState("timer.samples", []);
		}else{
			$this->decay($player, 0.5);
		}
	}
}
