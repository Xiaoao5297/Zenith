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
 * 移植自 SCAXE-GO-CE pkg/level/generator/object/cactus.go 的 Reed。
 */
class Reed implements Feature{

	public function generate(ObjectChunkManager $w, JavaRandom $r, BlockPos $pos){
		for($i = 0; $i < 4; $i++){
			$target = $pos->add(
				$r->nextBoundedInt(4) - $r->nextBoundedInt(4),
				0,
				$r->nextBoundedInt(4) - $r->nextBoundedInt(4)
			);

			if($target->y < 0 || $target->y >= 256){
				continue;
			}

			if($w->getBlockId($target->x, $target->y, $target->z) === 0){
				$below = $w->getBlockId($target->x, $target->y - 1, $target->z);
				if($below === Block::GRASS || $below === Block::DIRT || $below === Block::SAND){

					$hasWater = false;
					if($w->getBlockId($target->x - 1, $target->y - 1, $target->z) === Block::WATER || $w->getBlockId($target->x - 1, $target->y - 1, $target->z) === Block::STILL_WATER){
						$hasWater = true;
					}elseif($w->getBlockId($target->x + 1, $target->y - 1, $target->z) === Block::WATER || $w->getBlockId($target->x + 1, $target->y - 1, $target->z) === Block::STILL_WATER){
						$hasWater = true;
					}elseif($w->getBlockId($target->x, $target->y - 1, $target->z - 1) === Block::WATER || $w->getBlockId($target->x, $target->y - 1, $target->z - 1) === Block::STILL_WATER){
						$hasWater = true;
					}elseif($w->getBlockId($target->x, $target->y - 1, $target->z + 1) === Block::WATER || $w->getBlockId($target->x, $target->y - 1, $target->z + 1) === Block::STILL_WATER){
						$hasWater = true;
					}

					if($hasWater){
						$h = 2 + $r->nextBoundedInt($r->nextBoundedInt(3) + 1);
						for($j = 0; $j < $h; $j++){
							if($w->getBlockId($target->x, $target->y + $j, $target->z) === 0){
								$w->setBlock($target->x, $target->y + $j, $target->z, Block::SUGARCANE_BLOCK, 0);
							}
						}
					}
				}
			}
		}
		return true;
	}
}
