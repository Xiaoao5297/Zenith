<?php

namespace pocketmine\level\generator\gorigional\object;

use pocketmine\block\Block;
use pocketmine\level\generator\gorigional\JavaRandom;

/**
 * 移植自 SCAXE-GO-CE pkg/level/generator/object/waterlily.go。
 */
class Waterlily implements Feature{

	public function generate(ObjectChunkManager $w, JavaRandom $r, BlockPos $pos){
		for($i = 0; $i < 10; $i++){
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
				if($below === Block::WATER || $below === Block::STILL_WATER){
					$w->setBlock($target->x, $target->y, $target->z, Block::WATER_LILY, 0);
				}
			}
		}
		return true;
	}
}
