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

class JungleBush extends Populator{
	private $baseAmount = 0;
	private $randomAmount = 0;
	/** @var ChunkManager */
	private $level;

	public function setBaseAmount($amount){
		$this->baseAmount = (int) $amount;
	}

	public function setRandomAmount($amount){
		$this->randomAmount = (int) $amount;
	}

	public function populate(ChunkManager $level, $chunkX, $chunkZ, Random $random){
		$this->level = $level;
		$amount = $this->baseAmount + ($this->randomAmount > 0 ? $random->nextBoundedInt($this->randomAmount + 1) : 0);
		for($i = 0; $i < $amount; ++$i){
			$x = $random->nextRange($chunkX << 4, ($chunkX << 4) + 15);
			$z = $random->nextRange($chunkZ << 4, ($chunkZ << 4) + 15);
			$y = $this->getHighestWorkableBlock($x, $z);
			if($y === -1){
				continue;
			}

			$bush = new \lycore\level\generator\object\JungleBush();
			if($bush->canPlaceObject($level, $x, $y, $z, $random)){
				$bush->placeObject($level, $x, $y, $z, $random);
			}
		}
	}

	private function getHighestWorkableBlock($x, $z){
		for($y = 127; $y > 0; --$y){
			$b = $this->level->getBlockIdAt($x, $y, $z);
			if($b === Block::DIRT || $b === Block::GRASS || $b === Block::PODZOL){
				break;
			}
			if($b !== Block::AIR && $b !== Block::SNOW_LAYER && $b !== Block::LEAVES && $b !== Block::VINE){
				return -1;
			}
		}

		return ++$y;
	}
}
