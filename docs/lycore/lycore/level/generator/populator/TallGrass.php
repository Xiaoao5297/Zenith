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

class TallGrass extends Populator{
	/** @var ChunkManager */
	private $level;
	private $randomAmount = 0;
	private $baseAmount = 0;

	private $plantTypes = [
		2, 2, 2, 2, 2,
		3, 3,
		4, 5,
		1, 0
	];

	public function setRandomAmount($amount){
		$this->randomAmount = $amount;
	}

	public function setBaseAmount($amount){
		$this->baseAmount = $amount;
	}

	public function populate(ChunkManager $level, $chunkX, $chunkZ, Random $random){
		$this->level = $level;
		$amount = ($this->randomAmount > 0 ? $random->nextRange(0, $this->randomAmount) : 0) + $this->baseAmount;
		for($i = 0; $i < $amount; ++$i){
			$this->populatePatch($chunkX, $chunkZ, $random, $i);
		}
	}

	private function populatePatch($chunkX, $chunkZ, Random $random, $patchIndex){
		$minX = $chunkX * 16;
		$minZ = $chunkZ * 16;
		$maxX = $minX + 15;
		$maxZ = $minZ + 15;
		$centerX = $random->nextRange($minX, $maxX);
		$centerZ = $random->nextRange($minZ, $maxZ);
		$radius = $random->nextRange(2, 4);
		$attempts = $random->nextRange(4, 7);
		$type = $this->pickPlantType($random, $patchIndex);

		for($i = 0; $i < $attempts; ++$i){
			$dx = $random->nextRange(-$radius, $radius);
			$dz = $random->nextRange(-$radius, $radius);
			if(($dx * $dx) + ($dz * $dz) > ($radius * $radius) + $random->nextRange(0, $radius)){
				continue;
			}

			$x = $centerX + $dx;
			$z = $centerZ + $dz;
			if($x < $minX or $x > $maxX or $z < $minZ or $z > $maxZ){
				continue;
			}

			$y = $this->getHighestWorkableBlock($x, $z);

			if($y !== -1 and $this->canTallGrassStay($x, $y, $z)){
				$this->placeDoublePlant($x, $y, $z, $type);
			}
		}
	}

	private function pickPlantType(Random $random, $patchIndex){
		return $this->plantTypes[($random->nextRange(0, count($this->plantTypes) - 1) + $patchIndex) % count($this->plantTypes)];
	}

	private function placeDoublePlant($x, $y, $z, $type){
		$this->level->setBlockIdAt($x, $y, $z, Block::DOUBLE_PLANT);
		$this->level->setBlockDataAt($x, $y, $z, $type);
		$this->level->setBlockIdAt($x, $y + 1, $z, Block::DOUBLE_PLANT);
		$this->level->setBlockDataAt($x, $y + 1, $z, $type | 0x08);
	}

	private function canTallGrassStay($x, $y, $z){
		$b = $this->level->getBlockIdAt($x, $y, $z);
		$above = $this->level->getBlockIdAt($x, $y + 1, $z);
		return $y < 127 and ($b === Block::AIR or $b === Block::SNOW_LAYER) and ($above === Block::AIR or $above === Block::SNOW_LAYER) and $this->level->getBlockIdAt($x, $y - 1, $z) === Block::GRASS;
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
