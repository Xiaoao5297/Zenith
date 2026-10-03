<?php

/*
 * ██╗   ██╗    ██████╗ ██████╗ ██████╗ ███████╗
 * ██║   ██║   ██╔════╝██╔═══██╗██╔══██╗██╔════╝
 * ██║   ██║   ██║     ██║   ██║██████╔╝█████╗
 * ██║   ██║   ██║     ██║   ██║██╔══██╗██╔══╝
 * ╚██████╔╝██╗╚██████╗╚██████╔╝██║  ██║███████╗
 *  ╚═════╝ ╚═╝ ╚═════╝ ╚═════╝ ╚═╝  ╚═╝╚══════╝
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * @Author: U core
 *
 * @Links:
 *  > LY Core
 *  > LY Core Project
*/

namespace lycore\level\generator\object;

use lycore\block\Block;
use lycore\block\Wood;
use lycore\level\ChunkManager;
use lycore\utils\Random;

class JungleTree extends Tree{
	private $minTreeHeight;
	private $maxTreeHeight;

	public function __construct($minTreeHeight = 4, $maxTreeHeight = 6){
		$this->minTreeHeight = $minTreeHeight;
		$this->maxTreeHeight = $maxTreeHeight;
		$this->trunkBlock = Block::LOG;
		$this->leafBlock = Block::LEAVES;
		$this->type = Wood::JUNGLE;
		$this->treeHeight = 8;
	}

	public function placeObject(ChunkManager $level, $x, $y, $z, Random $random){
		$this->treeHeight = $random->nextBoundedInt($this->maxTreeHeight) + $this->minTreeHeight;
		$treeWithVines = $random->nextBoundedInt(20) === 0;

		$level->setBlockIdAt($x, $y - 1, $z, Block::DIRT);

		for($yy = $y - 3 + $this->treeHeight; $yy <= $y + $this->treeHeight; ++$yy){
			$yOff = $yy - ($y + $this->treeHeight);
			$mid = (int) (1 - $yOff / 2);

			for($xx = $x - $mid; $xx <= $x + $mid; ++$xx){
				$xOff = $xx - $x;
				for($zz = $z - $mid; $zz <= $z + $mid; ++$zz){
					$zOff = $zz - $z;
					if(abs($xOff) !== $mid || abs($zOff) !== $mid || ($random->nextBoundedInt(2) !== 0 && $yOff !== 0)){
						$blockId = $level->getBlockIdAt($xx, $yy, $zz);
						if($this->canReplaceWithLeaves($blockId)){
							$level->setBlockIdAt($xx, $yy, $zz, $this->leafBlock);
							$level->setBlockDataAt($xx, $yy, $zz, $this->type);
						}
					}
				}
			}
		}

		for($yy = 0; $yy < $this->treeHeight; ++$yy){
			$currentY = $y + $yy;
			$blockId = $level->getBlockIdAt($x, $currentY, $z);
			if($this->canGrowInto($blockId)){
				$level->setBlockIdAt($x, $currentY, $z, $this->trunkBlock);
				$level->setBlockDataAt($x, $currentY, $z, $this->type);

				if($yy > 0){
					if($treeWithVines){
						$this->addVinesAroundLog($level, $x, $currentY, $z);
					}else{
						if($random->nextBoundedInt(3) > 0 and $this->isAirBlock($level, $x - 1, $currentY, $z)){
							$this->addVine($level, $x - 1, $currentY, $z, 8);
						}
						if($random->nextBoundedInt(3) > 0 and $this->isAirBlock($level, $x + 1, $currentY, $z)){
							$this->addVine($level, $x + 1, $currentY, $z, 2);
						}
						if($random->nextBoundedInt(3) > 0 and $this->isAirBlock($level, $x, $currentY, $z - 1)){
							$this->addVine($level, $x, $currentY, $z - 1, 1);
						}
						if($random->nextBoundedInt(3) > 0 and $this->isAirBlock($level, $x, $currentY, $z + 1)){
							$this->addVine($level, $x, $currentY, $z + 1, 4);
						}
					}
				}
			}
		}

		for($yy = $y - 3 + $this->treeHeight; $yy <= $y + $this->treeHeight; ++$yy){
			$yOff = $yy - ($y + $this->treeHeight);
			$radius = (int) (2 - $yOff / 2);
			for($xx = $x - $radius; $xx <= $x + $radius; ++$xx){
				for($zz = $z - $radius; $zz <= $z + $radius; ++$zz){
					if($level->getBlockIdAt($xx, $yy, $zz) === Block::LEAVES){
						if($random->nextBoundedInt(4) === 0 and $this->isAirBlock($level, $xx - 1, $yy, $zz)){
							$this->addHangingVine($level, $xx - 1, $yy, $zz, 8);
						}
						if($random->nextBoundedInt(4) === 0 and $this->isAirBlock($level, $xx + 1, $yy, $zz)){
							$this->addHangingVine($level, $xx + 1, $yy, $zz, 2);
						}
						if($random->nextBoundedInt(4) === 0 and $this->isAirBlock($level, $xx, $yy, $zz - 1)){
							$this->addHangingVine($level, $xx, $yy, $zz - 1, 1);
						}
						if($random->nextBoundedInt(4) === 0 and $this->isAirBlock($level, $xx, $yy, $zz + 1)){
							$this->addHangingVine($level, $xx, $yy, $zz + 1, 4);
						}
					}
				}
			}
		}

		if($random->nextBoundedInt(5) === 0 and $this->treeHeight > 5){
			for($offset = 0; $offset < 2; ++$offset){
				foreach($this->horizontalFaces() as $face){
					if($random->nextBoundedInt(4 - $offset) === 0){
						$this->placeCocoa($level, $random->nextBoundedInt(2), $x + $face[0], $y + $this->treeHeight - 5 + $offset, $z + $face[1], $face[2]);
					}
				}
			}
		}

		return true;
	}

	private function canGrowInto($blockId){
		return isset($this->overridable[$blockId]) || $blockId === Block::VINE;
	}

	private function canReplaceWithLeaves($blockId){
		return $blockId === Block::AIR || $blockId === Block::LEAVES || $blockId === Block::LEAVES2 || $blockId === Block::VINE;
	}

	private function isAirBlock(ChunkManager $level, $x, $y, $z){
		return $level->getBlockIdAt($x, $y, $z) === Block::AIR;
	}

	private function addVinesAroundLog(ChunkManager $level, $x, $y, $z){
		$this->addVine($level, $x - 1, $y, $z, 8);
		$this->addVine($level, $x + 1, $y, $z, 2);
		$this->addVine($level, $x, $y, $z - 1, 1);
		$this->addVine($level, $x, $y, $z + 1, 4);
	}

	private function addVine(ChunkManager $level, $x, $y, $z, $meta){
		if($this->isAirBlock($level, $x, $y, $z)){
			$level->setBlockIdAt($x, $y, $z, Block::VINE);
			$level->setBlockDataAt($x, $y, $z, $meta);
		}
	}

	private function addHangingVine(ChunkManager $level, $x, $y, $z, $meta){
		$this->addVine($level, $x, $y, $z, $meta);
		for($length = 4, --$y; $length > 0 and $this->isAirBlock($level, $x, $y, $z); --$length, --$y){
			$this->addVine($level, $x, $y, $z, $meta);
		}
	}

	private function placeCocoa(ChunkManager $level, $age, $x, $y, $z, $directionMeta){
		if($this->isAirBlock($level, $x, $y, $z)){
			$level->setBlockIdAt($x, $y, $z, Block::COCOA_BLOCK);
			$level->setBlockDataAt($x, $y, $z, ($age << 2) | $directionMeta);
		}
	}

	private function horizontalFaces(){
		return [
			[0, -1, 0],
			[0, 1, 2],
			[-1, 0, 3],
			[1, 0, 1],
		];
	}
}
