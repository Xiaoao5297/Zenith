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
use lycore\block\Wood2;
use lycore\level\ChunkManager;
use lycore\utils\Random;

class DrakOakTree extends Tree{

	public function __construct(){
		$this->trunkBlock = Block::WOOD2;
		$this->leafBlock = Block::LEAVES2;
		$this->type = Wood2::DARK_OAK;
	}
	
	public function placeObject(ChunkManager $level, $x, $y, $z, Random $random){
		$this->treeHeight = $random->nextBoundedInt(3) + $random->nextBoundedInt(2) + 6;
		$treeWithVines = $random->nextBoundedInt(20) === 0;

		if(!$this->placeTreeOfHeight($level, $x, $y, $z, $this->treeHeight)){
			return false;
		}

		$level->setBlockIdAt($x, $y - 1, $z, Block::DIRT);
		$level->setBlockIdAt($x + 1, $y - 1, $z, Block::DIRT);
		$level->setBlockIdAt($x, $y - 1, $z + 1, Block::DIRT);
		$level->setBlockIdAt($x + 1, $y - 1, $z + 1, Block::DIRT);

		$face = $this->randomHorizontalDirection($random);
		$leanStart = $this->treeHeight - $random->nextBoundedInt(4);
		$leanSteps = 2 - $random->nextBoundedInt(3);
		$centerX = $x;
		$centerZ = $z;
		$topY = $y + $this->treeHeight - 1;

		for($yy = 0; $yy < $this->treeHeight; ++$yy){
			if($yy >= $leanStart and $leanSteps > 0){
				$centerX += $face[0];
				$centerZ += $face[1];
				--$leanSteps;
			}

			$currentY = $y + $yy;
			$this->placeLogAt($level, $centerX, $currentY, $centerZ, $treeWithVines);
			$this->placeLogAt($level, $centerX + 1, $currentY, $centerZ, $treeWithVines);
			$this->placeLogAt($level, $centerX, $currentY, $centerZ + 1, $treeWithVines);
			$this->placeLogAt($level, $centerX + 1, $currentY, $centerZ + 1, $treeWithVines);
		}

		for($xx = -2; $xx <= 0; ++$xx){
			for($zz = -2; $zz <= 0; ++$zz){
				$this->placeLeafAt($level, $centerX + $xx, $topY - 1, $centerZ + $zz);
				$this->placeLeafAt($level, $centerX + 1 - $xx, $topY - 1, $centerZ + $zz);
				$this->placeLeafAt($level, $centerX + $xx, $topY - 1, $centerZ + 1 - $zz);
				$this->placeLeafAt($level, $centerX + 1 - $xx, $topY - 1, $centerZ + 1 - $zz);

				if(($xx > -2 or $zz > -1) and ($xx !== -1 or $zz !== -2)){
					$this->placeLeafAt($level, $centerX + $xx, $topY + 1, $centerZ + $zz);
					$this->placeLeafAt($level, $centerX + 1 - $xx, $topY + 1, $centerZ + $zz);
					$this->placeLeafAt($level, $centerX + $xx, $topY + 1, $centerZ + 1 - $zz);
					$this->placeLeafAt($level, $centerX + 1 - $xx, $topY + 1, $centerZ + 1 - $zz);
				}
			}
		}

		if($random->nextBoolean()){
			$this->placeLeafAt($level, $centerX, $topY + 2, $centerZ);
			$this->placeLeafAt($level, $centerX + 1, $topY + 2, $centerZ);
			$this->placeLeafAt($level, $centerX + 1, $topY + 2, $centerZ + 1);
			$this->placeLeafAt($level, $centerX, $topY + 2, $centerZ + 1);
		}

		for($xx = -3; $xx <= 4; ++$xx){
			for($zz = -3; $zz <= 4; ++$zz){
				if(($xx !== -3 or $zz !== -3) and ($xx !== -3 or $zz !== 4) and ($xx !== 4 or $zz !== -3) and ($xx !== 4 or $zz !== 4) and (abs($xx) < 3 or abs($zz) < 3)){
					$this->placeLeafAt($level, $centerX + $xx, $topY, $centerZ + $zz);
				}
			}
		}

		for($xx = -1; $xx <= 2; ++$xx){
			for($zz = -1; $zz <= 2; ++$zz){
				if(($xx < 0 or $xx > 1 or $zz < 0 or $zz > 1) and $random->nextBoundedInt(3) <= 0){
					$branchHeight = $random->nextBoundedInt(3) + 2;
					for($yy = 0; $yy < $branchHeight; ++$yy){
						$this->placeLogAt($level, $x + $xx, $topY - $yy - 1, $z + $zz);
					}

					for($leafX = -1; $leafX <= 1; ++$leafX){
						for($leafZ = -1; $leafZ <= 1; ++$leafZ){
							$this->placeLeafAt($level, $centerX + $xx + $leafX, $topY, $centerZ + $zz + $leafZ);
						}
					}

					for($leafX = -2; $leafX <= 2; ++$leafX){
						for($leafZ = -2; $leafZ <= 2; ++$leafZ){
							if(abs($leafX) !== 2 or abs($leafZ) !== 2){
								$this->placeLeafAt($level, $centerX + $xx + $leafX, $topY - 1, $centerZ + $zz + $leafZ);
							}
						}
					}
				}
			}
		}

		return true;
	}

	private function placeTreeOfHeight(ChunkManager $level, $x, $y, $z, $height){
		for($yy = 0; $yy <= $height + 1; ++$yy){
			$radius = 1;
			if($yy === 0){
				$radius = 0;
			}
			if($yy >= $height - 1){
				$radius = 2;
			}

			for($xx = -$radius; $xx <= $radius; ++$xx){
				for($zz = -$radius; $zz <= $radius; ++$zz){
					if(!$this->canGrowInto($level->getBlockIdAt($x + $xx, $y + $yy, $z + $zz))){
						return false;
					}
				}
			}
		}

		return true;
	}

	private function placeLogAt(ChunkManager $level, $x, $y, $z, $treeWithVines = false){
		if($this->canGrowInto($level->getBlockIdAt($x, $y, $z))){
			$level->setBlockIdAt($x, $y, $z, $this->trunkBlock);
			$level->setBlockDataAt($x, $y, $z, $this->type);
			if($treeWithVines){
				$this->addVinesAroundLog($level, $x, $y, $z);
			}
		}
	}

	private function placeLeafAt(ChunkManager $level, $x, $y, $z){
		$blockId = $level->getBlockIdAt($x, $y, $z);
		if($blockId === Block::AIR or $blockId === Block::VINE){
			$level->setBlockIdAt($x, $y, $z, $this->leafBlock);
			$level->setBlockDataAt($x, $y, $z, $this->type);
		}
	}

	private function canGrowInto($blockId){
		return isset($this->overridable[$blockId]) || $blockId === Block::VINE;
	}

	private function addVinesAroundLog(ChunkManager $level, $x, $y, $z){
		$this->addVine($level, $x - 1, $y, $z, 8);
		$this->addVine($level, $x + 1, $y, $z, 2);
		$this->addVine($level, $x, $y, $z - 1, 1);
		$this->addVine($level, $x, $y, $z + 1, 4);
	}

	private function addVine(ChunkManager $level, $x, $y, $z, $meta){
		if($level->getBlockIdAt($x, $y, $z) === Block::AIR){
			$level->setBlockIdAt($x, $y, $z, Block::VINE);
			$level->setBlockDataAt($x, $y, $z, $meta);
		}
	}

	private function randomHorizontalDirection(Random $random){
		switch($random->nextBoundedInt(4)){
			case 0:
				return [1, 0];
			case 1:
				return [-1, 0];
			case 2:
				return [0, 1];
			default:
				return [0, -1];
		}
	}
}
