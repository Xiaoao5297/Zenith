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
 * 自动连点检测：CPS 与点击间隔一致性。
 *
 * CPS 属于真实时间维度，保留墙钟；一致性判定大幅提高门槛（需要更多样本、
 * 更小的标准差），避免正常手速被误判。
 */
class AutoClickerCheck extends Check{

	public function getDisplayName() : string{
		return "AutoClicker";
	}

	public function clearPlayerData(string $playerName){
	}

	public function checkAttack(Player $player){
		if(!$this->enabled) return;
		if($player->hasPermission("fpacheat.bypass")) return;
		if($player->isCreative() or $player->isSpectator()) return;

		$data = $this->getPlayerData($player);
		$currentTime = round(microtime(true) * 1000);

		$timestamps = $data->getState("autoclicker.timestamps", []);
		$timestamps = array_values(array_filter($timestamps, function($ts) use ($currentTime){
			return $currentTime - $ts <= 1000;
		}));
		$timestamps[] = $currentTime;
		$data->setState("autoclicker.timestamps", $timestamps);

		$maxCps = (int) ($this->config["max-cps"] ?? 18);

		if(count($timestamps) > $maxCps){
			$detail = sprintf("%d CPS, 限制 %d", count($timestamps), $maxCps);
			$this->flag($player, $detail, 1.0);
		}else{
			$this->decay($player, 1.0);
		}

		$this->checkClickConsistency($player, $timestamps);
	}

	private function checkClickConsistency(Player $player, array $timestamps){
		$minSamples = (int) ($this->config["consistency-min-samples"] ?? 20);
		if(count($timestamps) < $minSamples){
			return;
		}

		$times = array_values($timestamps);
		$intervals = [];
		for($i = 0; $i < count($times) - 1; $i++){
			$intervals[] = $times[$i + 1] - $times[$i];
		}

		$mean = array_sum($intervals) / count($intervals);

		$variance = 0;
		foreach($intervals as $interval){
			$variance += pow($interval - $mean, 2);
		}
		$variance /= count($intervals);
		$stdDev = sqrt($variance);

		$consistencyThreshold = (float) ($this->config["consistency-threshold"] ?? 2.5);

		if($stdDev < $consistencyThreshold){
			$detail = sprintf("点击间隔标准差 %.2f ms (阈值 %.2f)", $stdDev, $consistencyThreshold);
			$this->flag($player, $detail, 1.0);
		}
	}
}
