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
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * @author Xiaoao
 * @link https://github.com/Xiaoao5297/Zenith
 *
 *
*/


namespace pocketmine\level\generator\gorigional\object;

use pocketmine\block\Block;
use pocketmine\level\generator\gorigional\JavaRandom;

/**
 * 移植自 SCAXE-GO-CE pkg/level/generator/object/cactus.go 的 Reed。
 */
class Reed implements Feature{

	public function generate(ObjectChunkManager $w, JavaRandom $r, BlockPos $pos){
		for($i = 0; $i < 20; $i++){
			$x = $pos->x + $r->nextBoundedInt(4) - $r->nextBoundedInt(4);
			$y = $pos->y;
			$z = $pos->z + $r->nextBoundedInt(4) - $r->nextBoundedInt(4);
			if($y < 1 || $y >= 128){
				continue;
			}

			if($w->getBlockId($x, $y, $z) === Block::AIR && $this->hasWaterNeighbor($w, $x, $y - 1, $z)){
				$h = 2 + $r->nextBoundedInt($r->nextBoundedInt(3) + 1);
				for($j = 0; $j < $h; $j++){
					if(!$this->canPlaceReed($w, $x, $y + $j, $z)){
						continue;
					}
					$w->setBlock($x, $y + $j, $z, Block::SUGARCANE_BLOCK, 0);
				}
			}
		}
		return true;
	}

	private function canPlaceReed(ObjectChunkManager $w, $x, $y, $z){
		if($y < 0 || $y >= 128 || $w->getBlockId($x, $y, $z) !== Block::AIR){
			return false;
		}
		$below = $w->getBlockId($x, $y - 1, $z);
		if($below === Block::SUGARCANE_BLOCK){
			return true;
		}
		return ($below === Block::GRASS || $below === Block::DIRT || $below === Block::SAND) && $this->hasWaterNeighbor($w, $x, $y - 1, $z);
	}

	private function hasWaterNeighbor(ObjectChunkManager $w, $x, $y, $z){
		$left = $w->getBlockId($x - 1, $y, $z);
		$right = $w->getBlockId($x + 1, $y, $z);
		$north = $w->getBlockId($x, $y, $z - 1);
		$south = $w->getBlockId($x, $y, $z + 1);
		return $left === Block::WATER || $left === Block::STILL_WATER
			|| $right === Block::WATER || $right === Block::STILL_WATER
			|| $north === Block::WATER || $north === Block::STILL_WATER
			|| $south === Block::WATER || $south === Block::STILL_WATER;
	}

}
