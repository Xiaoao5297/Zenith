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
use lycore\level\Level;
use lycore\Player;
use lycore\math\Vector3;

class Redstone extends RedstoneSource{

	protected $id = self::REDSTONE_BLOCK;
	public function __construct($meta = 0){
		$this->meta = $meta;
	}

	public function getLightLevel(){
		return 7;
	}

	public function getName() : string{
		return "Redstone Block";
	}

	public function isSolid(){
		return true;
	}

	public function isTransparent(){
		return false;
	}

	public function canBeFlowedInto(){
		return false;
	}

	public function getBoundingBox(){
		if($this->boundingBox === null){
			$this->boundingBox = $this->recalculateBoundingBox();
		}
		return $this->boundingBox;
	}

	public function isPowerSource(){
		return true;
	}

	public function getWeakPower($side){
		return 15;
	}

	public function getStrongPower($side){
		return 15;
	}

	public function breaksWhenMovedByPiston(){
		return false;
	}

	public function sticksToPiston(){
		return true;
	}

	public function onUpdate($type){
		return false;
	}

	public function onBreak(Item $item){
		$this->getLevel()->setBlock($this, new Air(), true, false);
		$this->getLevel()->updateAroundRedstone($this);
		return true;
	}

	public function place(Item $item, Block $block, Block $target, $face, $fx, $fy, $fz, Player $player = null){
		$this->getLevel()->setBlock($block, $this, true, true);
		$this->getLevel()->updateAroundRedstone($block);
		return true;
	}

	public function getDrops(Item $item) : array{
		return [
			[Item::REDSTONE_BLOCK, 0, 1],
		];
	}

	public function isActivated(Block $from = null){
		return true;
	}
}
