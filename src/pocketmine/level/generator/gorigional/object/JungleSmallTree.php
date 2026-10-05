<?php

namespace pocketmine\level\generator\gorigional\object;

use pocketmine\block\Block;
use pocketmine\level\generator\gorigional\JavaRandom;

/**
 * 移植自 SCAXE-GO-CE pkg/level/generator/object/tree_jungle_small.go。
 */
class JungleSmallTree extends Tree{
	/** @var bool */
	private $generateVines = true;

	public function __construct(){
		parent::__construct(Block::LOG, Block::LEAVES, 3);
	}

	public function generate(ObjectChunkManager $level, JavaRandom $random, BlockPos $pos){
		$this->treeHeight = 4 + $random->nextBoundedInt(7);

		$x = $pos->x;
		$y = $pos->y;
		$z = $pos->z;

		$groundY = self::findGround($level, $x, $y, $z);
		if($groundY < 1 || $groundY > 127 - $this->treeHeight){
			return false;
		}

		$belowId = $level->getBlockId($x, $groundY - 1, $z);
		if($belowId !== Block::GRASS && $belowId !== Block::DIRT){
			return false;
		}

		$level->setBlock($x, $groundY - 1, $z, Block::DIRT, 0);

		for($ty = 0; $ty < $this->treeHeight; $ty++){
			$level->setBlock($x, $groundY + $ty, $z, $this->trunkBlock, $this->type);

			if($this->generateVines && $ty > 0){
				$this->tryPlaceVine($level, $random, $x - 1, $groundY + $ty, $z, 8);
				$this->tryPlaceVine($level, $random, $x + 1, $groundY + $ty, $z, 2);
				$this->tryPlaceVine($level, $random, $x, $groundY + $ty, $z - 1, 1);
				$this->tryPlaceVine($level, $random, $x, $groundY + $ty, $z + 1, 4);
			}
		}

		for($ly = $groundY + $this->treeHeight - 3; $ly <= $groundY + $this->treeHeight; $ly++){
			$yOff = $ly - ($groundY + $this->treeHeight);
			$radius = 1 - intdiv($yOff, 2);

			for($lx = $x - $radius; $lx <= $x + $radius; $lx++){
				for($lz = $z - $radius; $lz <= $z + $radius; $lz++){
					$xDist = abs($lx - $x);
					$zDist = abs($lz - $z);

					if($xDist === $radius && $zDist === $radius && ($yOff === 0 || $random->nextBoundedInt(2) === 0)){
						continue;
					}

					if($level->getBlockId($lx, $ly, $lz) === 0){
						$level->setBlock($lx, $ly, $lz, $this->leafBlock, $this->type);

						if($this->generateVines && $yOff < 0){
							$this->tryGrowVineBelow($level, $random, $lx, $ly, $lz);
						}
					}
				}
			}
		}

		return true;
	}

	private function tryPlaceVine(ObjectChunkManager $level, JavaRandom $random, $x, $y, $z, $meta){
		if($random->nextBoundedInt(3) > 0 && $level->getBlockId($x, $y, $z) === 0){
			$level->setBlock($x, $y, $z, Block::VINE, $meta);
		}
	}

	private function tryGrowVineBelow(ObjectChunkManager $level, JavaRandom $random, $x, $y, $z){
		if($random->nextBoundedInt(4) === 0){
			$dir = $random->nextBoundedInt(4);
			$meta = 0;
			switch($dir){
				case 0: $meta = 1; break;
				case 1: $meta = 2; break;
				case 2: $meta = 4; break;
				case 3: $meta = 8; break;
			}

			$maxLen = $random->nextBoundedInt(4) + 1;
			for($vy = $y - 1; $vy >= $y - $maxLen && $vy > 0; $vy--){
				if($level->getBlockId($x, $vy, $z) === 0){
					$level->setBlock($x, $vy, $z, Block::VINE, $meta);
				}else{
					break;
				}
			}
		}
	}

	private static function findGround(ObjectChunkManager $level, $x, $startY, $z){
		for($y = $startY; $y > 0; $y--){
			$id = $level->getBlockId($x, $y - 1, $z);
			if($id === Block::GRASS || $id === Block::DIRT || $id === Block::SAND){
				return $y;
			}

			if($id !== 0 && $id !== Block::TALL_GRASS && $id !== Block::LEAVES){
				return -1;
			}
		}
		return -1;
	}
}
