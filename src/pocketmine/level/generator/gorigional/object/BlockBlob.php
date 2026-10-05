<?php

namespace pocketmine\level\generator\gorigional\object;

use pocketmine\block\Block;
use pocketmine\level\generator\gorigional\JavaRandom;

/**
 * 移植自 SCAXE-GO-CE pkg/level/generator/object/block_blob.go。
 */
class BlockBlob implements Feature{
	/** @var int */
	private $block;
	/** @var int */
	private $size;

	public function __construct($blockId, $startRadius){
		$this->block = $blockId;
		$this->size = $startRadius;
	}

	public function generate(ObjectChunkManager $level, JavaRandom $random, BlockPos $pos){
		$x = $pos->x;
		$y = $pos->y;
		$z = $pos->z;

		while($y > 3){
			$id = $level->getBlockId($x, $y - 1, $z);
			if($id !== 0 && $id !== Block::LEAVES && $id !== Block::LEAVES2){
				break;
			}
			$y--;
		}

		if($y <= 3){
			return false;
		}

		for($i = 0; $i < 3; $i++){
			$xRadius = $this->size + $random->nextBoundedInt(2);
			$yRadius = $this->size + $random->nextBoundedInt(2);
			$zRadius = $this->size + $random->nextBoundedInt(2);

			$f = ($xRadius + $yRadius + $zRadius) * 0.333 + 0.5;
			$fSq = $f * $f;

			for($bx = $x - $xRadius; $bx <= $x + $xRadius; $bx++){
				$xDist = $bx - $x + 0.5;
				for($bz = $z - $zRadius; $bz <= $z + $zRadius; $bz++){
					$zDist = $bz - $z + 0.5;
					for($by = $y - $yRadius; $by <= $y + $yRadius; $by++){
						$yDist = $by - $y + 0.5;

						$distSq = $xDist * $xDist + $yDist * $yDist + $zDist * $zDist;
						if($distSq <= $fSq){
							$level->setBlock($bx, $by, $bz, $this->block, 0);
						}
					}
				}
			}

			$x += -(1 + $random->nextBoundedInt(2)) + $random->nextBoundedInt(1 + $random->nextBoundedInt(2));
			$y -= $random->nextBoundedInt(2);
			$z += -(1 + $random->nextBoundedInt(2)) + $random->nextBoundedInt(1 + $random->nextBoundedInt(2));
		}

		return true;
	}
}
