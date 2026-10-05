<?php

namespace pocketmine\level\generator\gorigional\object;

use pocketmine\block\Block;
use pocketmine\level\generator\gorigional\JavaRandom;

/**
 * 移植自 SCAXE-GO-CE pkg/level/generator/object/tree_pine.go。
 */
class PineTree extends Tree{

	public function __construct(){
		parent::__construct(Block::LOG, Block::LEAVES, 1);
	}

	public function generate(ObjectChunkManager $level, JavaRandom $random, BlockPos $pos){
		$height = $random->nextBoundedInt(5) + 7;
		$foliageStart = $height - $random->nextBoundedInt(2) - 3;

		if($pos->y < 1 || $pos->y + $height + 1 > 256){
			return false;
		}

		$x = $pos->x;
		$y = $pos->y;
		$z = $pos->z;

		$soil = $level->getBlockId($x, $y - 1, $z);
		if($soil !== Block::GRASS && $soil !== Block::DIRT && $soil !== Block::FARMLAND && $soil !== Block::PODZOL){
			return false;
		}

		if($soil === Block::GRASS){
			$level->setBlock($x, $y - 1, $z, Block::DIRT, 0);
		}

		$radius = 0;
		for($yl = $pos->y + $height; $yl >= $pos->y + $foliageStart; $yl--){
			for($xx = $x - $radius; $xx <= $x + $radius; $xx++){
				for($zz = $z - $radius; $zz <= $z + $radius; $zz++){
					$dx = abs($xx - $x);
					$dz = abs($zz - $z);

					if($dx !== $radius || $dz !== $radius || $radius <= 0){
						$id = $level->getBlockId($xx, $yl, $zz);
						if($id === 0 || $id === Block::LEAVES || $id === Block::SNOW_LAYER){
							$level->setBlock($xx, $yl, $zz, Block::LEAVES, 1);
						}
					}
				}
			}

			if($radius >= 1 && $yl === $pos->y + $foliageStart + 1){
				$radius--;
			}elseif($radius < 2){
				$radius++;
			}
		}

		for($i = 0; $i < $height - $random->nextBoundedInt(3); $i++){
			$id = $level->getBlockId($x, $y + $i, $z);
			if($id === 0 || $id === Block::LEAVES || $id === Block::SNOW_LAYER){
				$level->setBlock($x, $y + $i, $z, Block::LOG, 1);
			}
		}

		return true;
	}
}
