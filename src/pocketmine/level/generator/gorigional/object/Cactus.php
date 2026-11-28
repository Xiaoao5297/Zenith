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
 * 移植自 SCAXE-GO-CE pkg/level/generator/object/cactus.go 的 Cactus。
 */
class Cactus implements Feature{

	public function generate(ObjectChunkManager $w, JavaRandom $r, BlockPos $pos){
		for($i = 0; $i < 10; $i++){
			$x = $pos->x + $r->nextBoundedInt(8) - $r->nextBoundedInt(8);
			$y = $pos->y + $r->nextBoundedInt(4) - $r->nextBoundedInt(4);
			$z = $pos->z + $r->nextBoundedInt(8) - $r->nextBoundedInt(8);
			if($y < 1 || $y >= 128){
				continue;
			}

			if($w->getBlockId($x, $y, $z) === Block::AIR){
				$h = 1 + $r->nextBoundedInt($r->nextBoundedInt(3) + 1);
				for($j = 0; $j < $h; $j++){
					if(!$this->canPlaceCactus($w, $x, $y + $j, $z)){
						continue;
					}
					$w->setBlock($x, $y + $j, $z, Block::CACTUS, 0);
				}
			}
		}
		return true;
	}

	private function canPlaceCactus(ObjectChunkManager $w, $x, $y, $z){
		if($y < 1 || $y >= 128 || $w->getBlockId($x, $y, $z) !== Block::AIR){
			return false;
		}
		$below = $w->getBlockId($x, $y - 1, $z);
		if($below !== Block::SAND && $below !== Block::CACTUS){
			return false;
		}
		$neighbors = [
			$w->getBlockId($x - 1, $y, $z),
			$w->getBlockId($x + 1, $y, $z),
			$w->getBlockId($x, $y, $z - 1),
			$w->getBlockId($x, $y, $z + 1),
		];
		foreach($neighbors as $id){
			if(Block::get($id)->isSolid()){
				return false;
			}
		}
		return true;
	}

}
