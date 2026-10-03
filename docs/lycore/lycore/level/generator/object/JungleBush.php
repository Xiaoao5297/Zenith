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

namespace lycore\level\generator\object;

use lycore\block\Block;
use lycore\block\Wood;
use lycore\level\ChunkManager;
use lycore\utils\Random;

class JungleBush extends Tree{
	public function __construct(){
		$this->trunkBlock = Block::LOG;
		$this->leafBlock = Block::LEAVES;
		$this->type = Wood::JUNGLE;
	}

	public function canPlaceObject(ChunkManager $level, $x, $y, $z, Random $random){
		$soil = $level->getBlockIdAt($x, $y - 1, $z);
		return $soil === Block::GRASS || $soil === Block::DIRT || $soil === Block::PODZOL;
	}

	public function placeObject(ChunkManager $level, $x, $y, $z, Random $random){
		$level->setBlockIdAt($x, $y, $z, $this->trunkBlock);
		$level->setBlockDataAt($x, $y, $z, $this->type);

		for($yy = -2; $yy <= 1; ++$yy){
			$radius = 2 - $yy;
			for($xx = -$radius; $xx <= $radius; ++$xx){
				for($zz = -$radius; $zz <= $radius; ++$zz){
					if(($xx * $xx) + ($zz * $zz) > $radius * $radius){
						continue;
					}
					$blockId = $level->getBlockIdAt($x + $xx, $y + $yy, $z + $zz);
					if($blockId === Block::AIR || $blockId === Block::LEAVES || $blockId === Block::LEAVES2 || $blockId === Block::VINE || isset($this->overridable[$blockId])){
						$level->setBlockIdAt($x + $xx, $y + $yy, $z + $zz, $this->leafBlock);
						$level->setBlockDataAt($x + $xx, $y + $yy, $z + $zz, $this->type);
					}
				}
			}
		}

		return true;
	}
}
