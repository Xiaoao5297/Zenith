<?php

namespace pocketmine\level\generator\gorigional\object;

use pocketmine\block\Block;
use pocketmine\level\generator\gorigional\JavaRandom;

/**
 * 移植自 SCAXE-GO-CE pkg/level/generator/object/cactus.go 的 Cactus。
 */
class Cactus implements Feature{

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
				if($below === Block::SAND || $below === Block::CACTUS){
					$h = 1 + $r->nextBoundedInt($r->nextBoundedInt(3) + 1);
					for($j = 0; $j < $h; $j++){
						if($w->getBlockId($target->x, $target->y + $j, $target->z) === 0){
							$w->setBlock($target->x, $target->y + $j, $target->z, Block::CACTUS, 0);
						}
					}
				}
			}
		}
		return true;
	}
}
