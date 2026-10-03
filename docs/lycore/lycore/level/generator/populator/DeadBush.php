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

namespace lycore\level\generator\populator;

use lycore\block\Block;
use lycore\level\ChunkManager;
use lycore\utils\Random;

class DeadBush extends Populator{
	const PATCH_MIN_RADIUS = 3;
	const PATCH_MAX_RADIUS = 4;
	const PATCH_PROBABILITY = 0.2;

	/** @var ChunkManager */
	private $level;
	private $randomAmount = 0;
	private $baseAmount = 0;

	public function setRandomAmount($amount){
		$this->randomAmount = $amount;
	}

	public function setBaseAmount($amount){
		$this->baseAmount = $amount;
	}

	public function populate(ChunkManager $level, $chunkX, $chunkZ, Random $random){
		$this->level = $level;
		$amount = $this->getAmount($random);
		for($i = 0; $i < $amount; ++$i){
			$x = $random->nextRange($chunkX * 16, $chunkX * 16 + 15);
			$z = $random->nextRange($chunkZ * 16, $chunkZ * 16 + 15);
			$this->placeDeadBushPatchAt($level, $x, $z, $random);
		}
	}

	public function placeDeadBushPatchAt(ChunkManager $level, int $centerX, int $centerZ, Random $random) : int{
		$this->level = $level;
		$radius = $this->getPatchRadius($random);
		$radiusSquared = $radius * $radius;
		$placed = 0;

		for($x = $centerX - $radius; $x <= $centerX + $radius; ++$x){
			$dx = $x - $centerX;
			for($z = $centerZ - $radius; $z <= $centerZ + $radius; ++$z){
				$dz = $z - $centerZ;
				if(($dx * $dx) + ($dz * $dz) > $radiusSquared){
					continue;
				}
				if($random->nextFloat() >= self::PATCH_PROBABILITY){
					continue;
				}

				$y = $this->getHighestWorkableBlock($x, $z);
				if($y !== -1 and $this->canDeadBushStay($x, $y, $z)){
					$this->level->setBlockIdAt($x, $y, $z, Block::DEAD_BUSH);
					$this->level->setBlockDataAt($x, $y, $z, 1);
					++$placed;
				}
			}
		}

		return $placed;
	}

	private function getAmount(Random $random) : int{
		$amount = (int) $this->baseAmount;
		if($this->randomAmount > 0){
			$amount += $random->nextBoundedInt((int) $this->randomAmount);
		}

		return max(0, $amount);
	}

	private function getPatchRadius(Random $random) : int{
		return $random->nextRange(self::PATCH_MIN_RADIUS, self::PATCH_MAX_RADIUS);
	}

	private function canDeadBushStay($x, $y, $z){
		$b = $this->level->getBlockIdAt($x, $y, $z);
		return ($b === Block::AIR or $b === Block::SNOW_LAYER) and $this->isValidSupport($this->level->getBlockIdAt($x, $y - 1, $z));
	}

	private function isValidSupport($id) : bool{
		return $id === Block::SAND || $id === Block::DIRT || $id === Block::GRASS;
	}

	private function getHighestWorkableBlock($x, $z){
		for($y = 127; $y >= 0; --$y){
			$b = $this->level->getBlockIdAt($x, $y, $z);
			if($b !== Block::AIR and $b !== Block::LEAVES and $b !== Block::LEAVES2 and $b !== Block::SNOW_LAYER){
				break;
			}
		}

		return $y === 0 ? -1 : ++$y;
	}
}
