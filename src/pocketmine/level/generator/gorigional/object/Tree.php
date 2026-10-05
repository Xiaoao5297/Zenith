<?php

namespace pocketmine\level\generator\gorigional\object;

use pocketmine\block\Block;
use pocketmine\level\generator\gorigional\JavaRandom;

/**
 * 移植自 SCAXE-GO-CE pkg/level/generator/object/tree.go 的 Tree 基类。
 */
class Tree implements Feature{
	/** @var int */
	public $trunkBlock;
	/** @var int */
	public $leafBlock;
	/** @var int */
	public $type;
	/** @var int */
	public $treeHeight = 0;
	/** @var bool[] */
	public $overrides;

	public function __construct($trunk, $leaf, $typeData){
		$this->trunkBlock = $trunk;
		$this->leafBlock = $leaf;
		$this->type = $typeData;
		$this->overrides = [
			0 => true,
			Block::SAPLING => true,
			Block::LOG => true,
			Block::LEAVES => true,
			Block::SNOW_LAYER => true,
			Block::LEAVES2 => true,
			Block::WOOD2 => true,
		];
	}

	public function canPlaceObject(ObjectChunkManager $level, $x, $y, $z, JavaRandom $random){
		$radiusToCheck = 0;

		for($yy = 0; $yy < $this->treeHeight + 3; $yy++){
			if($yy === 1 || $yy === $this->treeHeight){
				$radiusToCheck++;
			}

			for($xx = -$radiusToCheck; $xx < $radiusToCheck + 1; $xx++){
				for($zz = -$radiusToCheck; $zz < $radiusToCheck + 1; $zz++){
					$checkY = $y + $yy;
					if($checkY >= 0 && $checkY < 256){
						$id = $level->getBlockId($x + $xx, $checkY, $z + $zz);

						if(!isset($this->overrides[$id])){
							return false;
						}
					}else{
						return false;
					}
				}
			}
		}
		return true;
	}

	public function generate(ObjectChunkManager $level, JavaRandom $random, BlockPos $pos){
		$x = $pos->x;
		$y = $pos->y;
		$z = $pos->z;

		$soilBlock = $level->getBlockId($x, $y - 1, $z);
		if($soilBlock !== Block::GRASS && $soilBlock !== Block::DIRT && $soilBlock !== Block::FARMLAND){
			return false;
		}

		$level->setBlock($x, $y - 1, $z, Block::DIRT, 0);

		for($i3 = $y - 3 + $this->treeHeight; $i3 <= $y + $this->treeHeight; $i3++){
			$i4 = $i3 - ($y + $this->treeHeight);
			$j1 = 1 - intdiv($i4, 2);

			for($k1 = $x - $j1; $k1 <= $x + $j1; $k1++){
				$l1 = $k1 - $x;
				for($i2 = $z - $j1; $i2 <= $z + $j1; $i2++){
					$j2 = $i2 - $z;

					$absL1 = abs($l1);
					$absJ2 = abs($j2);

					if($absL1 !== $j1 || $absJ2 !== $j1 || ($random->nextBoundedInt(2) !== 0 && $i4 !== 0)){
						$blockId = $level->getBlockId($k1, $i3, $i2);
						if($blockId === 0 || $blockId === Block::LEAVES || $blockId === Block::VINE){
							$level->setBlock($k1, $i3, $i2, $this->leafBlock, $this->type);
						}
					}
				}
			}
		}

		for($j3 = 0; $j3 < $this->treeHeight; $j3++){
			$blockId = $level->getBlockId($x, $y + $j3, $z);
			if($blockId === 0 || $blockId === Block::LEAVES || $blockId === Block::VINE || $blockId === Block::LEAVES2){
				$level->setBlock($x, $y + $j3, $z, $this->trunkBlock, $this->type);
			}
		}

		return true;
	}

	public function placeTrunk(ObjectChunkManager $level, $x, $y, $z, JavaRandom $random, $trunkHeight){
		$level->setBlock($x, $y - 1, $z, Block::DIRT, 0);

		for($yy = 0; $yy < $trunkHeight; $yy++){
			$blockId = $level->getBlockId($x, $y + $yy, $z);

			if(isset($this->overrides[$blockId])){
				$level->setBlock($x, $y + $yy, $z, $this->trunkBlock, $this->type);
			}
		}
	}
}
