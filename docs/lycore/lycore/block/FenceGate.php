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
use lycore\item\Tool;
use lycore\level\Level;
use lycore\math\AxisAlignedBB;
use lycore\Player;
use lycore\level\sound\DoorSound;

class FenceGate extends Transparent implements ElectricalAppliance{

	protected $id = self::FENCE_GATE;

	public function __construct($meta = 0){
		$this->meta = $meta;
	}

	public function getName() : string{
		return "Oak Fence Gate";
	}

	public function getHardness() {
		return 2;
	}

	public function canBeActivated() : bool {
		return true;
	}

	public function getToolType(){
		return Tool::TYPE_AXE;
	}


	protected function recalculateBoundingBox() {

		if(($this->getDamage() & 0x04) > 0){
			return null;
		}

		$i = ($this->getDamage() & 0x03);
		if($i === 2 or $i === 0){
			return new AxisAlignedBB(
				$this->x,
				$this->y,
				$this->z + 0.375,
				$this->x + 1,
				$this->y + 1.5,
				$this->z + 0.625
			);
		}else{
			return new AxisAlignedBB(
				$this->x + 0.375,
				$this->y,
				$this->z,
				$this->x + 0.625,
				$this->y + 1.5,
				$this->z + 1
			);
		}
	}

	public function place(Item $item, Block $block, Block $target, $face, $fx, $fy, $fz, Player $player = null){
		$faces = [
			0 => 3,
			1 => 0,
			2 => 1,
			3 => 2,
		];
		$this->meta = $faces[$player instanceof Player ? $player->getDirection() : 0] & 0x03;
		$this->getLevel()->setBlock($block, $this, true, true);

		return true;
	}

	public function isOpened(){
		return (($this->getDamage() & 0x04) > 0);
	}

	public function getDrops(Item $item) : array {
		return [
			[$this->id, 0, 1],
		];
	}

	public function setOpen($open){
		if($this->isOpened() !== (bool) $open){
			$this->meta ^= 0x04;
			$this->getLevel()->setBlock($this, $this, true);
			$this->level->addSound(new DoorSound($this));
		}

		return true;
	}

	public function onUpdate($type){
		if($type === Level::BLOCK_UPDATE_REDSTONE){
			$this->setOpen($this->getLevel()->isBlockPowered($this));
			return Level::BLOCK_UPDATE_REDSTONE;
		}

		return false;
	}

	public function onActivate(Item $item, Player $player = null){
		$faces = [
			0 => 3,
			1 => 0,
			2 => 1,
			3 => 2,
		];
		if($player !== null){
			$this->meta = ($faces[$player instanceof Player ? $player->getDirection() : 0] & 0x03) | ($this->meta & 0x04);
		}

		return $this->setOpen(!$this->isOpened());
	}
}
