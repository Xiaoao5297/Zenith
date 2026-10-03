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

namespace lycore\block;

use lycore\item\Item;
use lycore\Player;

class PoweredRepeater extends RedstoneDiode{
	protected $id = self::POWERED_REPEATER;
	protected $isPowered = true;

	public function getName() : string{
		return "Powered Repeater";
	}

	protected function getDelay(){
		return (1 + ($this->meta >> 2)) << 1;
	}

	public function getDelayLevel() : int{
		return (($this->meta >> 2) & 0x03) + 1;
	}

	protected function getPowered(){
		return Block::get(self::POWERED_REPEATER, $this->meta);
	}

	protected function getUnpowered(){
		return Block::get(self::UNPOWERED_REPEATER, $this->meta);
	}

	public function isLocked(){
		return $this->getPowerOnSides() > 0;
	}

	protected function isAlternateInput(Block $block){
		return self::isDiode($block);
	}

	public function onActivate(Item $item, Player $player = null){
		$this->meta += 4;
		if($this->meta > 15){
			$this->meta %= 4;
		}
		$this->level->setBlock($this, $this, true, true);
		return true;
	}

	public function getDrops(Item $item) : array{
		return [
			[Item::REPEATER, 0, 1],
		];
	}
}
