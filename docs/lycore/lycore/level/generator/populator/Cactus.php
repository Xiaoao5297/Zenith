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

class Cactus extends Populator{
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
			$x = ((int) $chunkX << 4) + $random->nextBoundedInt(13) + 1;
			$z = ((int) $chunkZ << 4) + $random->nextBoundedInt(13) + 1;
			$y = $this->getHighestWorkableBlock($x, $z);

			if($y !== -1){
				$this->placeCactusAt($level, $x, $y, $z, $this->getCactusBlockCount($random));
			}
		}
	}

	public function placeCactusAt(ChunkManager $level, int $x, int $y, int $z, int $height) : bool{
		$this->level = $level;
		$height = max(1, min(4, $height));
		for($yy = $y; $yy < $y + $height; ++$yy){
			if(!$this->canCactusStay($x, $yy, $z, $yy === $y)){
				return false;
			}
		}

		for($yy = $y; $yy < $y + $height; ++$yy){
			$this->level->setBlockIdAt($x, $yy, $z, Block::CACTUS);
			$this->level->setBlockDataAt($x, $yy, $z, 1);
		}

		return true;
	}

	private function getAmount(Random $random) : int{
		$amount = (int) $this->baseAmount;
		if($this->randomAmount > 0){
			$amount += $random->nextBoundedInt((int) $this->randomAmount);
		}

		return max(0, $amount);
	}

	private function getCactusBlockCount(Random $random) : int{
		$height = 1;
		$range = $random->nextBoundedInt(18);
		if($range >= 16){
			$height = 3;
		}elseif($range >= 11){
			$height = 2;
		}

		return $height + 1;
	}

	private function canCactusStay($x, $y, $z, bool $base){
		$b = $this->level->getBlockIdAt($x, $y, $z);
		if($b !== Block::AIR && $b !== Block::SNOW_LAYER){
			return false;
		}

		$below = $this->level->getBlockIdAt($x, $y - 1, $z);
		if($base && $below !== Block::SAND){
			return false;
		}

		foreach([[1, 0], [-1, 0], [0, 1], [0, -1]] as $offset){
			if(!$this->isFlowable($this->level->getBlockIdAt($x + $offset[0], $y, $z + $offset[1]))){
				return false;
			}
		}

		return true;
	}

	private function isFlowable($id) : bool{
		return $id === Block::AIR || $id === Block::SNOW_LAYER;
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
