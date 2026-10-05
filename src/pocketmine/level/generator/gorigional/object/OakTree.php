<?php

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
