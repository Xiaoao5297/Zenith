<?php

namespace pocketmine\level\generator\gorigional\object;

use pocketmine\block\Block;
use pocketmine\level\generator\gorigional\JavaRandom;

/**
 * 移植自 SCAXE-GO-CE pkg/level/generator/object/tree_mega_pine.go。
 */
class MegaPineTree implements Feature{
	/** @var bool */
	private $hasPodzol;

	public function __construct($hasPodzol){
		$this->hasPodzol = $hasPodzol;
	}

	public function generate(ObjectChunkManager $level, JavaRandom $random, BlockPos $pos){
		$x = $pos->x;
		$y = $pos->y;
		$z = $pos->z;

		$groundY = self::findGround($level, $x, $y, $z);
		if($groundY < 1 || $groundY > 110){
			return false;
		}

		for($dx = 0; $dx <= 1; $dx++){
			for($dz = 0; $dz <= 1; $dz++){
				$belowId = $level->getBlockId($x + $dx, $groundY - 1, $z + $dz);
				if($belowId !== Block::GRASS && $belowId !== Block::DIRT && $belowId !== Block::PODZOL){
					return false;
				}
			}
		}

		$height = 13 + $random->nextBoundedInt(15);

		if($this->hasPodzol){
			$this->placePodzol($level, $random, $x, $groundY, $z);
		}

		for($ty = 0; $ty < $height; $ty++){
			for($dx = 0; $dx <= 1; $dx++){
				for($dz = 0; $dz <= 1; $dz++){
					$level->setBlock($x + $dx, $groundY + $ty, $z + $dz, Block::LOG, 1);
				}
			}
		}

		$leafStart = $groundY + $height - $random->nextBoundedInt(3) - 3;
		$radius = 0;

		for($ly = $groundY + $height; $ly >= $leafStart; $ly--){
			$yFromTop = ($groundY + $height) - $ly;

			if($yFromTop < 2){
				$radius = 0;
			}elseif($yFromTop < 6){
				$radius = 1 + intdiv($yFromTop, 2);
			}else{
				$radius = 2 + $random->nextBoundedInt(2);
			}

			for($lx = $x - $radius; $lx <= $x + 1 + $radius; $lx++){
				for($lz = $z - $radius; $lz <= $z + 1 + $radius; $lz++){
					$xDist = abs($lx - $x);
					$zDist = abs($lz - $z);

					if($xDist + $zDist > $radius + 2){
						continue;
					}

					$id = $level->getBlockId($lx, $ly, $lz);
					if($id === 0 || $id === Block::LEAVES){
						$level->setBlock($lx, $ly, $lz, Block::LEAVES, 1);
					}
				}
			}
		}

		return true;
	}

	private function placePodzol(ObjectChunkManager $level, JavaRandom $random, $x, $y, $z){
		$radius = 2 + $random->nextBoundedInt(2);
		for($dx = -$radius; $dx <= 1 + $radius; $dx++){
			for($dz = -$radius; $dz <= 1 + $radius; $dz++){
				$dist = $dx * $dx + $dz * $dz;
				if($dist > ($radius + 1) * ($radius + 1)){
					continue;
				}

				$checkY = $y - 1;
				$id = $level->getBlockId($x + $dx, $checkY, $z + $dz);
				if($id === Block::GRASS || $id === Block::DIRT){
					$level->setBlock($x + $dx, $checkY, $z + $dz, Block::DIRT, 2);
				}
			}
		}
	}

	private static function findGround(ObjectChunkManager $level, $x, $startY, $z){
		for($y = $startY; $y > 0; $y--){
			$id = $level->getBlockId($x, $y - 1, $z);
			if($id === Block::GRASS || $id === Block::DIRT || $id === Block::PODZOL){
				return $y;
			}
			if($id !== 0 && $id !== Block::TALL_GRASS && $id !== Block::LEAVES){
				return -1;
			}
		}
		return -1;
	}
}
