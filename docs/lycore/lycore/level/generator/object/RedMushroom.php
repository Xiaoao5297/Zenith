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

class RedMushroom extends Mushroom{

	public function __construct(){
		$this->type = 14;
		$this->type2 = 10;
		$this->trunkBlock = Block::BROWN_MUSHROOM_BLOCK;
		$this->leafBlock = Block::RED_MUSHROOM_BLOCK;
	}
	
	public function placeObject(ChunkManager $level, $x, $y, $z, Random $random){
		$this->treeHeight = $random->nextRange(0,2) + 5;

		$this->placeTrunk($level, $x, $y, $z, $random, $this->treeHeight - 1);
		$yyy = $this->treeHeight + $y;

		for($xx = $x - 2; $xx <= $x + 2; ++$xx){
			for($zz = $z - 2; $zz <= $z + 2; ++$zz){
				for($yy = $yyy - 3; $yy <= $yyy; ++$yy){

					if((!Block::$solid[$level->getBlockIdAt($xx, $yy, $zz)] || isset($this->overridable[$level->getBlockIdAt($xx, $yy, $zz)]))){
						if($yy != $yyy && ($xx == $x - 2 || $xx == $x + 2 || $zz == $z - 2 || $zz == $z + 2) && !(($xx == $x - 2 || $xx == $x + 2) && ($zz == $z - 2 || $zz == $z + 2))){

							$level->setBlockIdAt($xx, $yy, $zz, $this->leafBlock);
							$level->setBlockDataAt($xx, $yy, $zz, 14);
						}elseif ($yy == $yyy && !($xx == $x - 2 || $xx == $x + 2 || $zz == $z - 2 || $zz == $z + 2)){
							$level->setBlockIdAt($xx, $yy, $zz, $this->leafBlock);
							$level->setBlockDataAt($xx, $yy, $zz, 14);
						}
					}
				}
			}
		}
	}
}