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

namespace pocketmine\level\generator\normal\object;

use pocketmine\block\Block;
use pocketmine\level\ChunkManager;
use pocketmine\level\generator\object\PopulatorObject;
use pocketmine\utils\Random;

class Fossil extends PopulatorObject{
	const WIDTH_X = 9;
	const WIDTH_Z = 5;
	const HEIGHT = 3;

	/** @var ChunkManager */
	private $level;
	/** @var Random */
	private $random;
	private $originX;
	private $originY;
	private $originZ;
	private $deep;

	public function placeObject(ChunkManager $level, $x, $y, $z, Random $random, $deep = false){
		$this->level = $level;
		$this->random = $random;
		$this->originX = (int) $x;
		$this->originY = (int) $y;
		$this->originZ = (int) $z;
		$this->deep = (bool) $deep;

		if($random->nextBoundedInt(2) === 0){
			$this->placeSpine();
		}else{
			$this->placeSkull();
		}
	}

	private function placeSpine(){
		for($x = 0; $x < 9; ++$x){
			$this->place($x, 0, 2, Block::QUARTZ_BLOCK);
			if($x % 2 === 0){
				$this->place($x, 0, 1, Block::QUARTZ_BLOCK);
				$this->place($x, 0, 3, Block::QUARTZ_BLOCK);
			}
		}
		$this->placeOre(4, 0, 2);
	}

	private function placeSkull(){
		foreach([
			[0, 0, 1], [0, 0, 2], [0, 0, 3],
			[1, 0, 0], [1, 0, 1], [1, 0, 3], [1, 0, 4],
			[2, 0, 0], [2, 0, 4],
			[3, 0, 1], [3, 0, 2], [3, 0, 3],
			[1, 1, 1], [1, 1, 3], [2, 1, 1], [2, 1, 3],
		] as $part){
			$this->place($part[0], $part[1], $part[2], Block::QUARTZ_BLOCK);
		}
		$this->placeOre(4, 0, 2);
	}

	private function placeOre($x, $y, $z){
		$this->place($x, $y, $z, $this->deep ? Block::DIAMOND_ORE : Block::COAL_ORE);
	}

	private function place($x, $y, $z, $id, $meta = 0){
		if($this->random->nextBoundedInt(10) === 0 && $id === Block::QUARTZ_BLOCK){
			return;
		}

		$this->level->setBlockIdAt($this->originX + $x, $this->originY + $y, $this->originZ + $z, $id);
		$this->level->setBlockDataAt($this->originX + $x, $this->originY + $y, $this->originZ + $z, $meta);
	}
}
