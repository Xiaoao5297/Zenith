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

use pocketmine\anticheat\MovementSnapshot;
use pocketmine\Player;

/**
 * 移动类检测器基类：统一接收 tick 驱动的 MovementSnapshot。
 */
abstract class MovementCheck extends Check{

	abstract public function checkMovement(Player $player, MovementSnapshot $snapshot);

	/**
	 * 是否由 AntiCheat 的逐 tick 采样任务驱动（而非 PlayerMoveEvent）。
	 *
	 * Fly/NoFall 需要每 tick 的滞空/下落数据，若只依赖位移事件，
	 * 静止悬停不会产生样本。
	 */
	public function sampledPerTick() : bool{
		return false;
	}
}
