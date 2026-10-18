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
 * 大型蘑菇：沼泽/蘑菇岛装饰用的简化巨型蘑菇。
 */
class BigMushroom implements Feature{
	/** @var bool */
	private $red;

	public function __construct($red){
		$this->red = (bool) $red;
	}

	public function generate(ObjectChunkManager $w, JavaRandom $r, BlockPos $pos){
		$ground = $w->getBlockId($pos->x, $pos->y - 1, $pos->z);
		if($ground !== Block::GRASS && $ground !== Block::DIRT && $ground !== Block::PODZOL && $ground !== Block::MYCELIUM){
			return false;
		}

		$height = 4 + $r->nextBoundedInt(3);
		if($pos->y + $height + 3 >= 128){
			return false;
		}

		for($dy = 0; $dy < $height; $dy++){
			if($w->getBlockId($pos->x, $pos->y + $dy, $pos->z) !== 0){
				return false;
			}
		}

		$stemBlock = $this->red ? Block::RED_MUSHROOM_BLOCK : Block::BROWN_MUSHROOM_BLOCK;
		for($dy = 0; $dy < $height; $dy++){
			$w->setBlock($pos->x, $pos->y + $dy, $pos->z, $stemBlock, 0);
		}

		$capY = $pos->y + $height;
		for($dy = 0; $dy <= 2; $dy++){
			$radius = $dy === 2 ? 1 : 2;
			for($dx = -$radius; $dx <= $radius; $dx++){
				for($dz = -$radius; $dz <= $radius; $dz++){
					if(abs($dx) === $radius && abs($dz) === $radius){
						continue;
					}
					$x = $pos->x + $dx;
					$y = $capY + $dy;
					$z = $pos->z + $dz;
					if($w->getBlockId($x, $y, $z) === 0){
						$w->setBlock($x, $y, $z, $stemBlock, $dy === 2 ? 0 : 14);
					}
				}
			}
		}

		return true;
	}
}
