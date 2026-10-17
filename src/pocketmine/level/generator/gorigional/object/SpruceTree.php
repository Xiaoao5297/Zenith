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


namespace pocketmine\level\generator\gorigional\object;

use pocketmine\block\Block;
use pocketmine\level\generator\gorigional\JavaRandom;

/**
 * 移植自 SCAXE-GO-CE pkg/level/generator/object/tree_spruce.go。
 */
class SpruceTree extends Tree{

	public function __construct(){
		parent::__construct(Block::LOG, Block::LEAVES, 1);
	}

	public function generate(ObjectChunkManager $level, JavaRandom $random, BlockPos $pos){
		$x = $pos->x;
		$y = $pos->y;
		$z = $pos->z;

		$soil = $level->getBlockId($x, $y - 1, $z);
		if($soil !== Block::GRASS && $soil !== Block::DIRT && $soil !== Block::PODZOL){
			return false;
		}

		$this->treeHeight = $random->nextBoundedInt(4) + 6;

		$topSize = $this->treeHeight - (1 + $random->nextBoundedInt(2));
		$lRadius = 2 + $random->nextBoundedInt(2);

		$this->placeTrunk($level, $x, $y, $z, $random, $this->treeHeight - $random->nextBoundedInt(3));

		$radius = $random->nextBoundedInt(2);
		$maxR = 1;
		$minR = 0;

		for($yy = 0; $yy <= $topSize; $yy++){
			$yyy = $y + $this->treeHeight - $yy;

			for($xx = $x - $radius; $xx <= $x + $radius; $xx++){
				$xOff = abs($xx - $x);
				for($zz = $z - $radius; $zz <= $z + $radius; $zz++){
					$zOff = abs($zz - $z);

					if($xOff === $radius && $zOff === $radius && $radius > 0){
						continue;
					}

					$id = $level->getBlockId($xx, $yyy, $zz);
					if($id === 0 || $id === Block::LEAVES || $id === Block::SNOW_LAYER){
						$level->setBlock($xx, $yyy, $zz, $this->leafBlock, $this->type);
					}
				}
			}

			if($radius >= $maxR){
				$radius = $minR;
				$minR = 1;
				$maxR++;
				if($maxR > $lRadius){
					$maxR = $lRadius;
				}
			}else{
				$radius++;
			}
		}
		return true;
	}
}
