<?php

/*
 * ██╗   ██╗    ██████╗ ██████╗ ██████╗ ███████╗
 * ██║   ██║   ██╔════╝██╔═══██╗██╔══██╗██╔════╝
 * ██║   ██║   ██║     ██║   ██║██████╔╝█████╗
 * ██║   ██║   ██║     ██║   ██║██╔══██╗██╔══╝
 * ╚██████╔╝██╗╚██████╗╚██████╔╝██║  ██║███████╗
 *  ╚═════╝ ╚═╝ ╚═════╝ ╚═════╝ ╚═╝  ╚═╝╚══════╝
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * @Author: U core
 *
 * @Links:
 *  > LY Core
 *  > LY Core Project
*/

/*
 * 移植自 lycore\event\redstone\BlockRedstoneEvent，命名空间改为 pocketmine\event\redstone。
 */

namespace pocketmine\event\redstone;

use pocketmine\block\Block;
use pocketmine\event\block\BlockEvent;

class BlockRedstoneEvent extends BlockEvent{
	public static $handlerList = null;

	/** @var int */
	protected $oldPower;
	/** @var int */
	protected $newPower;

	public function __construct(Block $block, $oldPower, $newPower){
		parent::__construct($block);
		$this->oldPower = (int) $oldPower;
		$this->newPower = (int) $newPower;
	}

	public function getOldPower(){
		return $this->oldPower;
	}

	public function getNewPower(){
		return $this->newPower;
	}
}
