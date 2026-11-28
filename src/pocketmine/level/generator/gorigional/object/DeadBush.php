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
 * 移植自 SCAXE-GO-CE pkg/level/generator/object/bush.go 的 DeadBush。
 */
class DeadBush implements Feature{

	public function generate(ObjectChunkManager $w, JavaRandom $r, BlockPos $pos){
		$j = $pos->y;
		while($j > 0){
			$id = $w->getBlockId($pos->x, $j, $pos->z);
			if($id !== Block::AIR && $id !== Block::LEAVES && $id !== Block::LEAVES2){
				break;
			}
			$j--;
		}

		for($i = 0; $i < 4; $i++){
			$x = $pos->x + $r->nextBoundedInt(8) - $r->nextBoundedInt(8);
			$y = $j + $r->nextBoundedInt(4) - $r->nextBoundedInt(4);
			$z = $pos->z + $r->nextBoundedInt(8) - $r->nextBoundedInt(8);
			if($y < 1 || $y >= 128){
				continue;
			}

			if($w->getBlockId($x, $y, $z) === Block::AIR){
				$below = $w->getBlockId($x, $y - 1, $z);
				if($below === Block::SAND || $below === Block::DIRT || $below === Block::HARDENED_CLAY || $below === Block::STAINED_CLAY){
					$w->setBlock($x, $y, $z, Block::DEAD_BUSH, 0);
				}
			}
		}
		return true;
	}

}
