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
 * 移植自 SCAXE-GO-CE pkg/level/generator/object/tree.go 的 OakTree。
 */
class OakTree extends Tree{

	public function __construct(){
		parent::__construct(Block::LOG, Block::LEAVES, 0);
	}

	public function generate(ObjectChunkManager $level, JavaRandom $random, BlockPos $pos){
		$this->treeHeight = $random->nextBoundedInt(3) + 4;

		if(!$this->canPlaceObject($level, $pos->x, $pos->y, $pos->z, $random)){
			return false;
		}
		return parent::generate($level, $random, $pos);
	}
}
