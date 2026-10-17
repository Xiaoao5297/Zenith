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
		for($i = 0; $i < 8; $i++){
			$target = $pos->add(
				$r->nextBoundedInt(8) - $r->nextBoundedInt(8),
				0,
				$r->nextBoundedInt(8) - $r->nextBoundedInt(8)
			);
			$target->y = $w->getHeight($target->x, $target->z) + 1;
			if($target->y < 1 || $target->y >= 128){
				continue;
			}

			$at = $w->getBlockId($target->x, $target->y, $target->z);
			$below = $w->getBlockId($target->x, $target->y - 1, $target->z);

			$validSoil = $below === Block::GRASS || $below === Block::DIRT || $below === Block::FARMLAND;

			if($at === 0 && $validSoil){
				$w->setBlock($target->x, $target->y, $target->z, $this->blockID, $this->blockMeta);
			}
		}
		return true;
	}
}
