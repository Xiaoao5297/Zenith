<?php

namespace pocketmine\level\generator\gorigional\object;

use pocketmine\block\Block;
use pocketmine\level\generator\gorigional\JavaRandom;

/**
 * 移植自 SCAXE-GO-CE pkg/level/generator/object/tree_dark_oak.go。
 */
class DarkOakTree implements Feature{
	/** @var int */
	private $trunkBlock;
	/** @var int */
	private $leafBlock;
	/** @var int */
	private $type;

	public function __construct(){
		$this->trunkBlock = Block::WOOD2;
		$this->leafBlock = Block::LEAVES2;
		$this->type = 1;
	}

	public function generate(ObjectChunkManager $level, JavaRandom $random, BlockPos $pos){
		$height = $random->nextBoundedInt(3) + $random->nextBoundedInt(2) + 6;

		$j = $pos->x;
		$k = $pos->y;
		$l = $pos->z;

		if($k < 1 || $k + $height + 1 >= 256){
			return false;
		}

		$blockDown = $pos->down();
		$soil = $level->getBlockId($blockDown->x, $blockDown->y, $blockDown->z);
		if($soil !== Block::GRASS && $soil !== Block::DIRT){
			return false;
		}

		if(!$this->placeTreeOfHeight($level, $pos, $height)){
			return false;
		}

		$this->setDirtAt($level, $blockDown);
		$this->setDirtAt($level, $blockDown->east());
		$this->setDirtAt($level, $blockDown->south());
		$this->setDirtAt($level, $blockDown->south()->east());

		$enumfacing = $random->nextBoundedInt(4);
		list($xOffset, $zOffset) = $this->getDirectionOffsets($enumfacing);

		$i1 = $height - $random->nextBoundedInt(4);
		$j1 = 2 - $random->nextBoundedInt(3);

		$k1 = $j;
		$l1 = $l;
		$i2 = $k + $height - 1;

		for($j2 = 0; $j2 < $height; $j2++){
			if($j2 >= $i1 && $j1 > 0){
				$k1 += $xOffset;
				$l1 += $zOffset;
				$j1--;
			}

			$k2 = $k + $j2;
			$blockpos1 = new BlockPos($k1, $k2, $l1);

			$mat = $level->getBlockId($blockpos1->x, $blockpos1->y, $blockpos1->z);
			if($mat === 0 || $mat === Block::LEAVES || $mat === Block::LEAVES2){
				$this->placeLogAt($level, $blockpos1);
				$this->placeLogAt($level, $blockpos1->east());
				$this->placeLogAt($level, $blockpos1->south());
				$this->placeLogAt($level, $blockpos1->east()->south());
			}
		}

		for($i3 = -2; $i3 <= 0; $i3++){
			for($l3 = -2; $l3 <= 0; $l3++){
				$k4 = -1;
				$this->placeLeafAt($level, $k1 + $i3, $i2 + $k4, $l1 + $l3);
				$this->placeLeafAt($level, 1 + $k1 - $i3, $i2 + $k4, $l1 + $l3);
				$this->placeLeafAt($level, $k1 + $i3, $i2 + $k4, 1 + $l1 - $l3);
				$this->placeLeafAt($level, 1 + $k1 - $i3, $i2 + $k4, 1 + $l1 - $l3);

				if(($i3 > -2 || $l3 > -1) && ($i3 !== -1 || $l3 !== -2)){
					$k4 = 1;
					$this->placeLeafAt($level, $k1 + $i3, $i2 + $k4, $l1 + $l3);
					$this->placeLeafAt($level, 1 + $k1 - $i3, $i2 + $k4, $l1 + $l3);
					$this->placeLeafAt($level, $k1 + $i3, $i2 + $k4, 1 + $l1 - $l3);
					$this->placeLeafAt($level, 1 + $k1 - $i3, $i2 + $k4, 1 + $l1 - $l3);
				}
			}
		}

		if($random->nextBoolean()){
			$this->placeLeafAt($level, $k1, $i2 + 2, $l1);
			$this->placeLeafAt($level, $k1 + 1, $i2 + 2, $l1);
			$this->placeLeafAt($level, $k1 + 1, $i2 + 2, $l1 + 1);
			$this->placeLeafAt($level, $k1, $i2 + 2, $l1 + 1);
		}

		for($j3 = -3; $j3 <= 4; $j3++){
			for($i4 = -3; $i4 <= 4; $i4++){
				if(($j3 !== -3 || $i4 !== -3) && ($j3 !== -3 || $i4 !== 4) &&
					($j3 !== 4 || $i4 !== -3) && ($j3 !== 4 || $i4 !== 4) &&
					(abs($j3) < 3 || abs($i4) < 3)){
					$this->placeLeafAt($level, $k1 + $j3, $i2, $l1 + $i4);
				}
			}
		}

		for($k3 = -1; $k3 <= 2; $k3++){
			for($j4 = -1; $j4 <= 2; $j4++){
				if(($k3 < 0 || $k3 > 1 || $j4 < 0 || $j4 > 1) && $random->nextBoundedInt(3) <= 0){
					$l4 = $random->nextBoundedInt(3) + 2;

					for($i5 = 0; $i5 < $l4; $i5++){
						$this->placeLogAt($level, new BlockPos($j + $k3, $i2 - $i5 - 1, $l + $j4));
					}

					for($j5 = -1; $j5 <= 1; $j5++){
						for($l2 = -1; $l2 <= 1; $l2++){
							$this->placeLeafAt($level, $k1 + $k3 + $j5, $i2, $l1 + $j4 + $l2);
						}
					}

					for($k5 = -2; $k5 <= 2; $k5++){
						for($l5 = -2; $l5 <= 2; $l5++){
							if(abs($k5) !== 2 || abs($l5) !== 2){
								$this->placeLeafAt($level, $k1 + $k3 + $k5, $i2 - 1, $l1 + $j4 + $l5);
							}
						}
					}
				}
			}
		}

		return true;
	}

	private function placeTreeOfHeight(ObjectChunkManager $level, BlockPos $pos, $height){
		$i = $pos->x;
		$j = $pos->y;
		$k = $pos->z;

		for($l = 0; $l <= $height + 1; $l++){
			$i1 = 1;
			if($l === 0){
				$i1 = 0;
			}
			if($l >= $height - 1){
				$i1 = 2;
			}

			for($j1 = -$i1; $j1 <= $i1; $j1++){
				for($k1 = -$i1; $k1 <= $i1; $k1++){
					$blockId = $level->getBlockId($i + $j1, $j + $l, $k + $k1);
					if(!$this->canGrowInto($blockId)){
						return false;
					}
				}
			}
		}
		return true;
	}

	private function canGrowInto($blockId){
		switch($blockId){
			case 0:
			case Block::LEAVES:
			case Block::LEAVES2:
			case Block::LOG:
			case Block::WOOD2:
			case Block::SAPLING:
			case Block::VINE:
				return true;
		}
		return false;
	}

	private function setDirtAt(ObjectChunkManager $level, BlockPos $pos){
		$blockId = $level->getBlockId($pos->x, $pos->y, $pos->z);
		if($blockId === Block::GRASS || $blockId === Block::FARMLAND){
			$level->setBlock($pos->x, $pos->y, $pos->z, Block::DIRT, 0);
		}
	}

	private function placeLogAt(ObjectChunkManager $level, BlockPos $pos){
		$blockId = $level->getBlockId($pos->x, $pos->y, $pos->z);
		if($this->canGrowInto($blockId)){
			$level->setBlock($pos->x, $pos->y, $pos->z, $this->trunkBlock, $this->type);
		}
	}

	private function placeLeafAt(ObjectChunkManager $level, $x, $y, $z){
		$blockId = $level->getBlockId($x, $y, $z);
		if($blockId === 0){
			$level->setBlock($x, $y, $z, $this->leafBlock, $this->type);
		}
	}

	private function getDirectionOffsets($dir){
		switch($dir){
			case 0: return [0, -1];
			case 1: return [0, 1];
			case 2: return [-1, 0];
			case 3: return [1, 0];
		}
		return [0, 0];
	}
}
