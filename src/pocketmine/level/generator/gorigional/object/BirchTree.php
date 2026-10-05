<?php

namespace pocketmine\level\generator\gorigional\object;

use pocketmine\block\Block;
use pocketmine\level\generator\gorigional\JavaRandom;

/**
 * 移植自 SCAXE-GO-CE pkg/level/generator/object/tree.go 的 BirchTree。
 */
class BirchTree extends Tree{
	/** @var bool */
	private $superBirch;

	public function __construct($super){
		parent::__construct(Block::LOG, Block::LEAVES, 2);
		$this->superBirch = $super;
	}

	public function generate(ObjectChunkManager $level, JavaRandom $random, BlockPos $pos){
		$this->treeHeight = $random->nextBoundedInt(3) + 5;
		if($this->superBirch){
			$this->treeHeight += $random->nextBoundedInt(7);
		}

		if(!$this->canPlaceObject($level, $pos->x, $pos->y, $pos->z, $random)){
			return false;
		}
		return parent::generate($level, $random, $pos);
	}
}
