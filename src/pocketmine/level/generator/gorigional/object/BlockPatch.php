<?php

namespace pocketmine\level\generator\gorigional\object;

use pocketmine\block\Block;
use pocketmine\level\generator\gorigional\JavaRandom;

/**
 * 移植自 SCAXE-GO-CE pkg/level/generator/object/block_patch.go。
 */
class BlockPatch implements Feature{
	/** @var int */
	private $blockID;

	public function __construct($id){
		$this->blockID = $id;
	}

	public function generate(ObjectChunkManager $level, JavaRandom $random, BlockPos $pos){
		for($i = 0; $i < 64; $i++){
			$target = $pos->add(
				$random->nextBoundedInt(8) - $random->nextBoundedInt(8),
				$random->nextBoundedInt(4) - $random->nextBoundedInt(4),
				$random->nextBoundedInt(8) - $random->nextBoundedInt(8)
			);

			if($target->y < 0 || $target->y >= 256){
				continue;
			}

			if($level->getBlockId($target->x, $target->y, $target->z) === 0 &&
				$level->getBlockId($target->x, $target->y - 1, $target->z) === Block::GRASS){

				$level->setBlock($target->x, $target->y, $target->z, $this->blockID, 0);

				$meta = $random->nextBoundedInt(4);
				$level->setBlock($target->x, $target->y, $target->z, $this->blockID, $meta);
			}
		}
		return true;
	}
}
