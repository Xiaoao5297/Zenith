<?php

namespace pocketmine\level\generator\gorigional\object;

use pocketmine\block\Block;
use pocketmine\level\generator\gorigional\JavaRandom;

/**
 * 移植自 SCAXE-GO-CE pkg/level/generator/object/vines.go。
 */
class Vines implements Feature{

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

			if($level->getBlockId($target->x, $target->y, $target->z) === 0){
				$meta = 0;

				if($this->isSolid($level, $target->north())){
					$meta |= 1;
				}
				if($this->isSolid($level, $target->south())){
					$meta |= 4;
				}
				if($this->isSolid($level, $target->west())){
					$meta |= 8;
				}
				if($this->isSolid($level, $target->east())){
					$meta |= 2;
				}

				if($meta !== 0){
					$level->setBlock($target->x, $target->y, $target->z, Block::VINE, $meta);
				}
			}
		}
		return true;
	}

	private function isSolid(ObjectChunkManager $level, BlockPos $pos){
		$id = $level->getBlockId($pos->x, $pos->y, $pos->z);
		return $id !== 0 && $id !== Block::VINE && $id !== Block::TALL_GRASS && $id !== Block::DEAD_BUSH;
	}
}
