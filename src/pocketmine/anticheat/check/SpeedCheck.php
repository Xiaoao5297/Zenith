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
use pocketmine\anticheat\PlayerData;

/**
 * 移动速度检测（tick 驱动）。
 *
 * 不再使用墙钟时间，速度按服务器 tick 折算；并考虑药水、方块、载具等上下文，
 * 命中只累积缓冲，由 ViolationManager 决定是否告警/惩罚。
 *
 * 网络抖动/低帧率容错：单样本速度受包间隔影响大（两包间隔 2-3 tick 但位移正常、
 * 或一包覆盖多 tick 位移），因此使用滑动窗口——最近 N 个样本的累计位移/累计 tick
 * 折算平均速度，仅窗口平均超限才 flag，单尖峰不报。
 */
class SpeedCheck extends MovementCheck{

	const TICKS_PER_SECOND = 20;

	/** @var string PlayerData state 键：滑动窗口 [tick, dist, tickDelta] 样本队列 */
	const STATE_WINDOW = "speed.window";

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

		// 单次超大位移（超过 TeleportCheck 的每 tick 上限）视为传送，清空缓冲并重置窗口
		$maxPerTick = (float) ($this->config["max-blocks-per-tick"] ?? 10.0);
		if($dist > $maxPerTick){
			$this->decay($player, 10.0);
			$this->resetWindow($player);
			return;
		}

		$tickDelta = $snapshot->getTickDelta();

		// 单次位移上限内的多 tick 大位移（疾跳/TNT/击退）：交给窗口平均速度判定，
		// 多 tick 折算后不会虚高；真正的 10 格/1tick 瞬移会被 TeleportCheck 捕获。

		$this->pushSample($player, $snapshot->getTick(), $dist, $tickDelta);
		$window = $this->getWindowAvg($player);

		$limit = $this->calcSpeedLimit($player, $snapshot);

		$avgSpeed = $window["dist"] > 0 ? ($window["dist"] / $window["ticks"]) * self::TICKS_PER_SECOND : 0.0;

		if($avgSpeed > $limit){
			$detail = sprintf("窗口速度 %.2f b/s, 限制 %.2f b/s (%d 样本/%d tick)",
				$avgSpeed, $limit, $window["samples"], $window["ticks"]);
			$this->flag($player, $detail, 1.0);
		}else{
			$this->decay($player, 1.0);
		}
	}

	/**
	 * 追加一个移动样本并维护窗口大小（按 tick 数上限裁剪旧样本）。
	 */
	private function pushSample(Player $player, int $tick, float $dist, int $tickDelta){
		$data = $this->getPlayerData($player);
		$window = $data->getState(self::STATE_WINDOW);
		if(!is_array($window)){
			$window = [];
		}

		$window[] = [$tick, $dist, $tickDelta];

		$maxTicks = (int) ($this->config["window-ticks"] ?? 10);
		$oldest = $tick - $maxTicks;
		$kept = [];
		foreach($window as $sample){
			if($sample[0] >= $oldest){
				$kept[] = $sample;
			}
		}
		$window = $kept;

		$data->setState(self::STATE_WINDOW, $window);
	}

	/**
	 * 汇总窗口内累计位移/累计 tick，返回 [dist, ticks, samples]。
	 */
	private function getWindowAvg(Player $player) : array{
		$window = $this->getPlayerData($player)->getState(self::STATE_WINDOW);
		if(!is_array($window)){
			return ["dist" => 0.0, "ticks" => 1, "samples" => 0];
		}

		$totalDist = 0.0;
		$totalTicks = 0;
		foreach($window as $sample){
			$totalDist += $sample[1];
			$totalTicks += $sample[2];
		}
		if($totalTicks < 1) $totalTicks = 1;

		return ["dist" => $totalDist, "ticks" => $totalTicks, "samples" => count($window)];
	}

	/**
	 * 大位移（视为传送）后清空窗口，避免旧样本污染后续判定。
	 */
	private function resetWindow(Player $player){
		$this->getPlayerData($player)->removeState(self::STATE_WINDOW);
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
