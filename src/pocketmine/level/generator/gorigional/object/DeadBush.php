<?php

namespace pocketmine\level\generator\gorigional\object;

use pocketmine\block\Block;
use pocketmine\level\generator\gorigional\JavaRandom;

/**
 * 移植自 SCAXE-GO-CE pkg/level/generator/object/bush.go 的 DeadBush。
 */
class DeadBush implements Feature{

	public function generate(ObjectChunkManager $w, JavaRandom $r, BlockPos $pos){
		for($i = 0; $i < 4; $i++){
			$target = $pos->add(
				$r->nextBoundedInt(8) - $r->nextBoundedInt(8),
				$r->nextBoundedInt(4) - $r->nextBoundedInt(4),
				$r->nextBoundedInt(8) - $r->nextBoundedInt(8)
			);

			if($target->y < 0 || $target->y >= 256){
				continue;
			}

			if($w->getBlockId($target->x, $target->y, $target->z) === 0){
				$below = $w->getBlockId($target->x, $target->y - 1, $target->z);
				if($below === Block::SAND || $below === Block::DIRT || $below === Block::HARDENED_CLAY || $below === Block::STAINED_CLAY){
					$w->setBlock($target->x, $target->y, $target->z, Block::DEAD_BUSH, 0);
				}
			}
		}
		return true;
	}
}
