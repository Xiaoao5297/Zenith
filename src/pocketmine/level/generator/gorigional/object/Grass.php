<?php

namespace pocketmine\level\generator\gorigional\object;

use pocketmine\block\Block;
use pocketmine\level\generator\gorigional\JavaRandom;

/**
 * 移植自 SCAXE-GO-CE pkg/level/generator/object/vegetation.go。
 */
class Grass implements Feature{
	/** @var int */
	private $type;

	public function __construct($t){
		$this->type = $t;
	}

	public function generate(ObjectChunkManager $w, JavaRandom $r, BlockPos $pos){
		for($i = 0; $i < 128; $i++){
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
				if($below === Block::GRASS || $below === Block::DIRT){
					$w->setBlock($target->x, $target->y, $target->z, Block::TALL_GRASS, $this->type);
				}
			}
		}
		return true;
	}
}
