<?php

namespace pocketmine\level\generator\gorigional\object;

use pocketmine\block\Block;
use pocketmine\level\generator\gorigional\JavaRandom;

/**
 * 移植自 SCAXE-GO-CE pkg/level/generator/object/tree_mega_jungle.go。
 */
class MegaJungleTree implements Feature{
	/** @var int */
	private $baseHeight = 10;
	/** @var int */
	private $woodMeta = 3;
	/** @var int */
	private $leafMeta = 3;

	public function generate(ObjectChunkManager $level, JavaRandom $random, BlockPos $pos){
		$height = $random->nextBoundedInt(20) + $this->baseHeight;

		if(!$this->ensureGrowable($level, $random, $pos, $height)){
			return false;
		}

		$this->createCrown($level, $pos->up($height), 2);

		for($j = $height - 2 - $random->nextBoundedInt(4); $j > intdiv($height, 2); $j -= 2 + $random->nextBoundedInt(4)){
			$f = $random->nextFloat() * M_PI * 2.0;

			$k = $pos->x + (int) (0.5 + cos($f) * 4.0);
			$l = $pos->z + (int) (0.5 + sin($f) * 4.0);

			for($i1 = 0; $i1 < 5; $i1++){
				$k = $pos->x + (int) (1.5 + cos($f) * $i1);
				$l = $pos->z + (int) (1.5 + sin($f) * $i1);

				$level->setBlock($k, $pos->y + $j - 3 + intdiv($i1, 2), $l, Block::LOG, $this->woodMeta);
			}

			$j2 = 1 + $random->nextBoundedInt(2);
			$j1 = $j;

			for($k1 = $j - $j2; $k1 <= $j1; $k1++){
				$l1 = $k1 - $j1;
				$this->growLeavesLayer($level, new BlockPos($k, $pos->y + $k1, $l), 1 - $l1);
			}
		}

		for($i2 = 0; $i2 < $height; $i2++){
			$blockpos = $pos->up($i2);

			if($this->canGrowInto($level, $blockpos)){
				$level->setBlock($blockpos->x, $blockpos->y, $blockpos->z, Block::LOG, $this->woodMeta);

				if($i2 > 0){
					$this->placeVine($level, $random, $blockpos->west(), 8);
					$this->placeVine($level, $random, $blockpos->north(), 1);
				}
			}

			if($i2 < $height - 1){
				$blockpos1 = $blockpos->east();
				if($this->canGrowInto($level, $blockpos1)){
					$level->setBlock($blockpos1->x, $blockpos1->y, $blockpos1->z, Block::LOG, $this->woodMeta);
					if($i2 > 0){
						$this->placeVine($level, $random, $blockpos1->east(), 2);
						$this->placeVine($level, $random, $blockpos1->north(), 1);
					}
				}

				$blockpos2 = $blockpos->south()->east();
				if($this->canGrowInto($level, $blockpos2)){
					$level->setBlock($blockpos2->x, $blockpos2->y, $blockpos2->z, Block::LOG, $this->woodMeta);
					if($i2 > 0){
						$this->placeVine($level, $random, $blockpos2->east(), 2);
						$this->placeVine($level, $random, $blockpos2->south(), 4);
					}
				}

				$blockpos3 = $blockpos->south();
				if($this->canGrowInto($level, $blockpos3)){
					$level->setBlock($blockpos3->x, $blockpos3->y, $blockpos3->z, Block::LOG, $this->woodMeta);
					if($i2 > 0){
						$this->placeVine($level, $random, $blockpos3->west(), 8);
						$this->placeVine($level, $random, $blockpos3->south(), 4);
					}
				}
			}
		}

		return true;
	}

	private function ensureGrowable(ObjectChunkManager $level, JavaRandom $random, BlockPos $pos, $height){
		if($pos->y < 1 || $pos->y + $height + 1 > 256){
			return false;
		}

		$soil1 = $level->getBlockId($pos->x, $pos->y - 1, $pos->z);
		$soil2 = $level->getBlockId($pos->x + 1, $pos->y - 1, $pos->z);
		$soil3 = $level->getBlockId($pos->x, $pos->y - 1, $pos->z + 1);
		$soil4 = $level->getBlockId($pos->x + 1, $pos->y - 1, $pos->z + 1);

		$valid = function($id){
			return $id === Block::GRASS || $id === Block::DIRT;
		};

		if($valid($soil1) && $valid($soil2) && $valid($soil3) && $valid($soil4)){
			$level->setBlock($pos->x, $pos->y - 1, $pos->z, Block::DIRT, 0);
			$level->setBlock($pos->x + 1, $pos->y - 1, $pos->z, Block::DIRT, 0);
			$level->setBlock($pos->x, $pos->y - 1, $pos->z + 1, Block::DIRT, 0);
			$level->setBlock($pos->x + 1, $pos->y - 1, $pos->z + 1, Block::DIRT, 0);
			return true;
		}

		return false;
	}

	private function createCrown(ObjectChunkManager $level, BlockPos $pos, $width){
		for($j = -2; $j <= 0; $j++){
			$this->growLeavesLayerStrict($level, $pos->up($j), $width + 1 - (-$j));
		}
	}

	private function growLeavesLayer(ObjectChunkManager $level, BlockPos $pos, $width){
		$radius = $width;
		for($x = -$radius; $x <= $radius; $x++){
			for($z = -$radius; $z <= $radius; $z++){
				if(abs($x) !== $radius || abs($z) !== $radius){
					$this->placeLeafAt($level, $pos->add($x, 0, $z));
				}
			}
		}
	}

	private function growLeavesLayerStrict(ObjectChunkManager $level, BlockPos $pos, $width){
		$radius = $width;
		for($x = -$radius; $x <= $radius; $x++){
			for($z = -$radius; $z <= $radius; $z++){
				$dSq = $x * $x + $z * $z;
				if($dSq <= $radius * $radius){
					$this->placeLeafAt($level, $pos->add($x, 0, $z));
				}
			}
		}
	}

	private function placeLeafAt(ObjectChunkManager $level, BlockPos $pos){
		$id = $level->getBlockId($pos->x, $pos->y, $pos->z);
		if($id === 0 || $id === Block::LEAVES){
			$level->setBlock($pos->x, $pos->y, $pos->z, Block::LEAVES, $this->leafMeta);
		}
	}

	private function canGrowInto(ObjectChunkManager $level, BlockPos $pos){
		$id = $level->getBlockId($pos->x, $pos->y, $pos->z);
		return $id === 0 || $id === Block::LEAVES || $id === Block::SAPLING || $id === Block::VINE;
	}

	private function placeVine(ObjectChunkManager $level, JavaRandom $random, BlockPos $pos, $meta){
		if($random->nextBoundedInt(3) > 0 && $level->getBlockId($pos->x, $pos->y, $pos->z) === 0){
			$level->setBlock($pos->x, $pos->y, $pos->z, Block::VINE, $meta);
		}
	}
}
