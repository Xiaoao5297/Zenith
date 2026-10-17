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
 * 移植自 SCAXE-GO-CE pkg/level/generator/object/clay.go。
 */
class Clay implements Feature{
	/** @var int */
	private $blockID;
	/** @var int */
	private $blockCount;

	public function __construct($count){
		$this->blockID = Block::CLAY_BLOCK;
		$this->blockCount = $count;
	}

	public function generate(ObjectChunkManager $w, JavaRandom $r, BlockPos $pos){
		$cur = $w->getBlockId($pos->x, $pos->y, $pos->z);
		if($cur !== Block::WATER && $cur !== Block::STILL_WATER){
			return false;
		}

		$i = $r->nextBoundedInt($this->blockCount - 2) + 2;
		$j = 1;

		for($k = $pos->x - $i; $k <= $pos->x + $i; $k++){
			for($l = $pos->z - $i; $l <= $pos->z + $i; $l++){
				$dx = $k - $pos->x;
				$dz = $l - $pos->z;

				if($dx * $dx + $dz * $dz <= $i * $i){
					for($m = $pos->y - $j; $m <= $pos->y + $j; $m++){
						$id = $w->getBlockId($k, $m, $l);

						if($id === Block::DIRT || $id === Block::CLAY_BLOCK){
							$w->setBlock($k, $m, $l, $this->blockID, 0);
						}
					}
				}
			}
		}
		return true;
	}
}
