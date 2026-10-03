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

class AcaciaTree extends Tree{

	public function __construct(){
		$this->trunkBlock = Block::WOOD2;
		$this->leafBlock = Block::LEAVES2;
		$this->type = Wood2::ACACIA;
	}

	public function placeObject(ChunkManager $world, $source_x, $source_y, $source_z, Random $random){
		$this->treeHeight = $random->nextBoundedInt(3) + $random->nextBoundedInt(3) + 5;

		$d = ($random->nextFloat() * M_PI * 2.0); // random direction
		$dx = (int) (cos($d) + 1.5) - 1;
		$dz = (int) (sin($d) + 1.5) - 1;
		if(abs($dx) > 0 && abs($dz) > 0){ // reduce possible directions to NESW
			if($random->nextBoolean()){
				$dx = 0;
			}else{
				$dz = 0;
			}
		}
		$twist_height = $this->treeHeight - 1 - $random->nextBoundedInt(4);
		$twist_count = $random->nextBoundedInt(3) + 1;
		$center_x = $source_x;
		$center_z = $source_z;
		$trunk_top_y = 0;
		// generates the trunk
		for($y = 0; $y < $this->treeHeight; ++$y){

			// trunk twists
			if($twist_count > 0 && $y >= $twist_height){
				$center_x += $dx;
				$center_z += $dz;
				--$twist_count;
			}

			$material = $world->getBlockIdAt($center_x, $source_y + $y, $center_z);
			if($this->canGrowInto($material)){
				$trunk_top_y = $source_y + $y;
				$world->setBlockIdAt($center_x, $source_y + $y, $center_z, $this->trunkBlock);
				$world->setBlockDataAt($center_x, $source_y + $y, $center_z, $this->type);
			}
		}

		// generates leaves
		for($x = -3; $x <= 3; ++$x){
			$abs_x = abs($x);
			for($z = -3; $z <= 3; ++$z){
				$abs_z = abs($z);
				if($abs_x < 3 || $abs_z < 3){
					$this->setLeaves($center_x + $x, $trunk_top_y, $center_z + $z, $world);
				}
				if($abs_x < 2 && $abs_z < 2){
					$this->setLeaves($center_x + $x, $trunk_top_y + 1, $center_z + $z, $world);
				}
				if(($abs_x === 2 && $abs_z === 0) || ($abs_x === 0 && $abs_z === 2)){
					$this->setLeaves($center_x + $x, $trunk_top_y + 1, $center_z + $z, $world);
				}
			}
		}

		// try to choose a different direction for second branching and canopy
		$d = $random->nextFloat() * M_PI * 2.0;
		$dx_b = (int) (cos($d) + 1.5) - 1;
		$dz_b = (int) (sin($d) + 1.5) - 1;
		if(abs($dx_b) > 0 && abs($dz_b) > 0){
			if($random->nextBoolean()){
				$dx_b = 0;
			}else{
				$dz_b = 0;
			}
		}
		if($dx !== $dx_b || $dz !== $dz_b){
			$center_x = $source_x;
			$center_z = $source_z;
			$branch_height = $twist_height - 1 - $random->nextBoundedInt(2);
			$twist_count = $random->nextBoundedInt(3) + 1;
			$trunk_top_y = 0;

			// generates the trunk
			for($y = $branch_height + 1; $y < $this->treeHeight; ++$y){
				if($twist_count > 0){
					$center_x += $dx_b;
					$center_z += $dz_b;
					$material = $world->getBlockIdAt($center_x, $source_y + $y, $center_z);
					if($this->canGrowInto($material)){
						$trunk_top_y = $source_y + $y;
						$world->setBlockIdAt($center_x, $source_y + $y, $center_z, $this->trunkBlock);
						$world->setBlockDataAt($center_x, $source_y + $y, $center_z, $this->type);
					}
					--$twist_count;
				}
			}

			// generates the leaves
			if($trunk_top_y > 0){
				for($x = -2; $x <= 2; ++$x){
					for($z = -2; $z <= 2; ++$z){
						if(abs($x) < 2 || abs($z) < 2){
							$this->setLeaves($center_x + $x, $trunk_top_y, $center_z + $z, $world);
						}
					}
				}
				for($x = -1; $x <= 1; ++$x){
					for($z = -1; $z <= 1; ++$z){
						$this->setLeaves($center_x + $x, $trunk_top_y + 1, $center_z + $z, $world);
					}
				}
			}
		}

		$world->setBlockIdAt($source_x, $source_y - 1, $source_z, Block::DIRT);
		$world->setBlockIdAt($source_x, $source_y, $source_z, $this->trunkBlock);
		$world->setBlockDataAt($source_x, $source_y, $source_z, 0);

		return true;
	}

	private function setLeaves(int $x, int $y, int $z, ChunkManager $world) : void{
		if($this->canReplaceWithLeaves($world->getBlockIdAt($x, $y, $z))){
			$world->setBlockIdAt($x, $y, $z, $this->leafBlock);
			$world->setBlockDataAt($x, $y, $z, $this->type);
		}
	}

	private function canGrowInto($blockId){
		return isset($this->overridable[$blockId]) || $blockId === Block::VINE;
	}

	private function canReplaceWithLeaves($blockId){
		return $blockId === Block::AIR || $blockId === Block::LEAVES || $blockId === Block::LEAVES2 || $blockId === Block::VINE;
	}
}
