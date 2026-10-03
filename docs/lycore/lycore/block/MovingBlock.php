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

class MovingBlock extends Transparent{
	protected $id = self::MOVING_BLOCK;

	public function __construct($meta = 0){
		$this->meta = $meta & 0x0f;
	}

	public function getName() : string{
		return "Moving Block";
	}

	public function isSolid(){
		return false;
	}

	public function canPassThrough(){
		return true;
	}

	public function canBeFlowedInto(){
		return false;
	}

	public function canBePushedByPiston(){
		return false;
	}

	public function canBePulledByPiston(){
		return false;
	}

	public function isBreakable(Item $item){
		return false;
	}

	public function getHardness(){
		return -1;
	}

	public function getResistance(){
		return -1;
	}

	public function getDrops(Item $item) : array{
		return [];
	}
}
