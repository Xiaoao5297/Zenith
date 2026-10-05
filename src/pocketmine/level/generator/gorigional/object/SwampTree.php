<?php

namespace pocketmine\level\generator\gorigional\object;

use pocketmine\block\Block;
use pocketmine\level\generator\gorigional\JavaRandom;

/**
 * 移植自 SCAXE-GO-CE pkg/level/generator/object/tree_swamp.go。
 */
class SwampTree extends Tree{

	public function __construct(){
		parent::__construct(Block::LOG, Block::LEAVES, 0);
	}

	public function generate(ObjectChunkManager $level, JavaRandom $random, BlockPos $pos){
		$height = $random->nextBoundedInt(4) + 5;

		while($level->getBlockId($pos->x, $pos->y - 1, $pos->z) === Block::STILL_WATER || $level->getBlockId($pos->x, $pos->y - 1, $pos->z) === Block::WATER){
			$pos = $pos->down();
		}

		$x = $pos->x;
		$y = $pos->y;
		$z = $pos->z;

		if($y < 1 || $y + $height + 1 > 256){
			return false;
		}

		for($yy = $y; $yy <= $y + 1 + $height; $yy++){
			$radius = 1;
			if($yy === $y){
				$radius = 0;
			}
			if($yy >= $y + 1 + $height - 2){
				$radius = 3;
			}

			for($xx = $x - $radius; $xx <= $x + $radius; $xx++){
				for($zz = $z - $radius; $zz <= $z + $radius; $zz++){
					if($yy >= 0 && $yy < 256){
						$id = $level->getBlockId($xx, $yy, $zz);

						if($id !== 0 && $id !== Block::LEAVES){
							if($id !== Block::STILL_WATER && $id !== Block::WATER){
								return false;
							}elseif($yy > $y){
								return false;
							}
						}
					}else{
						return false;
					}
				}
			}
		}

		$soil = $level->getBlockId($x, $y - 1, $z);
		if(($soil === Block::GRASS || $soil === Block::DIRT) && $y < 256 - $height - 1){
			$level->setBlock($x, $y - 1, $z, Block::DIRT, 0);

			for($yy = $y - 3 + $height; $yy <= $y + $height; $yy++){
				$yOff = $yy - ($y + $height);
				$mid = 2 - intdiv($yOff, 2);

				for($xx = $x - $mid; $xx <= $x + $mid; $xx++){
					$xOff = $xx - $x;
					for($zz = $z - $mid; $zz <= $z + $mid; $zz++){
						$zOff = $zz - $z;

						if(abs($xOff) !== $mid || abs($zOff) !== $mid || ($random->nextBoundedInt(2) !== 0 && $yOff !== 0)){
							$level->setBlock($xx, $yy, $zz, Block::LEAVES, 0);
						}
					}
				}
			}

			for($yy = 0; $yy < $height; $yy++){
				$id = $level->getBlockId($x, $y + $yy, $z);
				if($id === 0 || $id === Block::LEAVES || $id === Block::STILL_WATER || $id === Block::WATER){
					$level->setBlock($x, $y + $yy, $z, Block::LOG, 0);
				}
			}

			for($yy = $y - 3 + $height; $yy <= $y + $height; $yy++){
				$yOff = $yy - ($y + $height);
				$mid = 2 - intdiv($yOff, 2);

				for($xx = $x - $mid; $xx <= $x + $mid; $xx++){
					for($zz = $z - $mid; $zz <= $z + $mid; $zz++){
						if($level->getBlockId($xx, $yy, $zz) === Block::LEAVES){
							if($random->nextBoundedInt(4) === 0 && $level->getBlockId($xx - 1, $yy, $zz) === 0){
								$this->addVine($level, $xx - 1, $yy, $zz, 8);
							}
							if($random->nextBoundedInt(4) === 0 && $level->getBlockId($xx + 1, $yy, $zz) === 0){
								$this->addVine($level, $xx + 1, $yy, $zz, 2);
							}
							if($random->nextBoundedInt(4) === 0 && $level->getBlockId($xx, $yy, $zz - 1) === 0){
								$this->addVine($level, $xx, $yy, $zz - 1, 1);
							}
							if($random->nextBoundedInt(4) === 0 && $level->getBlockId($xx, $yy, $zz + 1) === 0){
								$this->addVine($level, $xx, $yy, $zz + 1, 4);
							}
						}
					}
				}
			}

			return true;
		}
		return false;
	}

	private function addVine(ObjectChunkManager $level, $x, $y, $z, $meta){
		$level->setBlock($x, $y, $z, Block::VINE, $meta);

		for($i = 0; $i < 4; $i++){
			$y--;
			if($level->getBlockId($x, $y, $z) === 0){
				$level->setBlock($x, $y, $z, Block::VINE, $meta);
			}else{
				break;
			}
		}
	}
}
