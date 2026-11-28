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
 * 移植自 Java 1.7.10 WorldGenMelon。
 */
class Melon implements Feature{
	public function generate(ObjectChunkManager $level, JavaRandom $random, BlockPos $pos){
		for($i = 0; $i < 64; $i++){
			$x = $pos->x + $random->nextBoundedInt(8) - $random->nextBoundedInt(8);
			$y = $pos->y + $random->nextBoundedInt(4) - $random->nextBoundedInt(4);
			$z = $pos->z + $random->nextBoundedInt(8) - $random->nextBoundedInt(8);

			if($y >= 0 && $y < 128 &&
				$level->getBlockId($x, $y, $z) === Block::AIR &&
				$level->getBlockId($x, $y - 1, $z) === Block::GRASS){
				$level->setBlock($x, $y, $z, Block::MELON_BLOCK, 0);
			}
		}
		return true;
	}
}
