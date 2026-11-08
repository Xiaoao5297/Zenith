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
 *
 * 落地宽限状态机（消除服务端伤害与客户端落地报告的 1-2 tick 时序差误报）：
 *
 *   空中累积 --落地(预期伤害达阈值, lastDamage==0)--> PENDING(3 tick 宽限)
 *   PENDING --伤害入账--> 按正常公式结算并清零
 *   PENDING --3 tick 内仍无伤害--> 结算并 FLAG（真 NoFall）
 *   PENDING --再次离地(落地即跳)--> 放弃 pending，回到空中累积
 *   任意时刻进入水/梯子/黏液块 --> 全部重置
 *
 * 客户端先报落地时服务端可能尚未走 Entity::attack(CAUSE_FALL)，lastDamage 仍为 0；
 * 宽限期内保留 fallDistance 不清零，伤害入账后按正常公式结算，
 * 3 tick 后仍无伤害入账才判定为规避摔落伤害。
 */
class NoFallCheck extends MovementCheck{

	const SAFE_FALL_DISTANCE = 3.0;

	/** @var int 落地后等待服务端摔落伤害入账的宽限 tick 数 */
	const PENDING_GRACE_TICKS = 3;

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

		// 合法缓冲环境：重置累计（含 pending 状态）
		if($snapshot->isInWater() or $snapshot->isOnLadder() or $snapshot->isOnSlime()){
			$this->resetAccumulation($data);
			return;
		}

		if(!$snapshot->isOnGround()){
			// 落地宽限期内再次离地（落地即跳）：放弃 pending，回到空中累积
			if((int) $data->getState("nofall.pendingTicks", 0) > 0){
				$data->setState("nofall.pendingTicks", 0);
			}
			$dy = $snapshot->getDy();
			if($dy < 0){
				$fallDistance = (float) $data->getState("nofall.fallDistance", 0.0) - $dy;
				$data->setState("nofall.fallDistance", $fallDistance);
			}
			$data->setState("nofall.fallTicks", (int) $data->getState("nofall.fallTicks", 0) + $snapshot->getTickDelta());
			return;
		}

		// ---- 落地（isOnGround）分支：每 tick 维护 pending 状态机 ----

		$pendingTicks = (int) $data->getState("nofall.pendingTicks", 0);

		if($pendingTicks > 0){
			// 宽限期中：伤害是否已入账？
			if((float) $data->getState("nofall.lastDamage", 0.0) > 0.0){
				// 伤害已到账，按正常公式结算并清零
				$data->setState("nofall.pendingTicks", 0);
				$this->settle($player, $data);
				return;
			}

			$pendingTicks--;
			if($pendingTicks > 0){
				// 仍在宽限内：保留 fallDistance，继续等待服务端伤害入账
				$data->setState("nofall.pendingTicks", $pendingTicks);
				return;
			}

			// 宽限耗尽仍无伤害：真 NoFall，结算并清零
			$data->setState("nofall.pendingTicks", 0);
			$this->settle($player, $data);
			return;
		}

		// 首次落地结算
		$fallDistance = (float) $data->getState("nofall.fallDistance", 0.0);
		$actualDamage = (float) $data->getState("nofall.lastDamage", 0.0);

		$expected = $fallDistance - self::SAFE_FALL_DISTANCE;
		$minFallDamage = (float) ($this->config["min-fall-damage"] ?? 4.0);

		if($expected >= $minFallDamage and $actualDamage < $expected * 0.25){
			// lastDamage 可能为 0（客户端先报落地、服务端伤害晚 1-2 tick）：
			// 不立即 flag，进入宽限期，保留 fallDistance 等待伤害入账
			$data->setState("nofall.pendingTicks", self::PENDING_GRACE_TICKS);
			return;
		}

		// 无嫌疑（伤害已到账或未达阈值）：正常结算并清零
		$this->settle($player, $data);
	}

	/**
	 * 落地结算：按累积 fallDistance 与已入账伤害对比，然后清空全部状态。
	 */
	private function settle(Player $player, $data){
		$fallDistance = (float) $data->getState("nofall.fallDistance", 0.0);
		$actualDamage = (float) $data->getState("nofall.lastDamage", 0.0);
		$this->resetAccumulation($data);

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
	 * 清空下落累积与 pending 状态（落地结算完成或进入合法缓冲环境时调用）。
	 */
	private function resetAccumulation($data){
		$data->setState("nofall.fallDistance", 0.0);
		$data->setState("nofall.fallTicks", 0);
		$data->setState("nofall.lastDamage", 0.0);
		$data->setState("nofall.pendingTicks", 0);
	}

	/**
	 * 由 EntityDamageEvent(CAUSE_FALL) 调用，记录本次实际摔落伤害。
	 */
	public function checkFallDamage(Player $player, $damage){
		$this->getPlayerData($player)->setState("nofall.lastDamage", (float) $damage);
	}
}
