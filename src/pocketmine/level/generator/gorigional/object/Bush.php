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
 * 移植自 SCAXE-GO-CE pkg/level/generator/object/bush.go 的 Bush。
 */
class Bush implements Feature{
	/** @var int */
	private $blockID;
	/** @var int */
	private $blockMeta;

	public function __construct($id, $meta){
		$this->blockID = $id;
		$this->blockMeta = $meta;
	}

	public function generate(ObjectChunkManager $w, JavaRandom $r, BlockPos $pos){
		$isMushroom = $this->blockID === Block::BROWN_MUSHROOM || $this->blockID === Block::RED_MUSHROOM;
		for($i = 0; $i < 64; $i++){
			$x = $pos->x + $r->nextBoundedInt(8) - $r->nextBoundedInt(8);
			$y = $pos->y + $r->nextBoundedInt(4) - $r->nextBoundedInt(4);
			$z = $pos->z + $r->nextBoundedInt(8) - $r->nextBoundedInt(8);
			if($y < 1 || $y >= 128){
				continue;
			}

			$at = $w->getBlockId($x, $y, $z);
			$below = $w->getBlockId($x, $y - 1, $z);
			if($isMushroom){
				$validSoil = $below === Block::MYCELIUM || $below === Block::GRASS || $below === Block::DIRT;
			}else{
				$validSoil = $below === Block::GRASS || $below === Block::DIRT || $below === Block::FARMLAND;
			}

			if($at === Block::AIR && $validSoil){
				$w->setBlock($x, $y, $z, $this->blockID, $this->blockMeta);
			}
		}
		return true;
	}

}
