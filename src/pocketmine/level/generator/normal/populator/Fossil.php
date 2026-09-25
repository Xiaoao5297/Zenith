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

namespace pocketmine\level\generator\normal\populator;

require_once dirname(__DIR__) . "/object/Fossil.php";

use pocketmine\block\Block;
use pocketmine\level\ChunkManager;
use pocketmine\level\generator\biome\Biome;
use pocketmine\level\generator\populator\Populator;
use pocketmine\utils\Random;

class Fossil extends Populator{
	const RARITY = 64;
	const MAX_EMPTY_CORNERS_ALLOWED = 4;

	/** @var ChunkManager */
	private $level;

	public function populate(ChunkManager $level, $chunkX, $chunkZ, Random $random){
		if(!$this->canPopulateOverworldStructure($level)){
			return;
		}

		$this->level = $level;
		$chunk = $level->getChunk((int) $chunkX, (int) $chunkZ);
		if($chunk !== null && method_exists($chunk, "getBiomeId")){
			$biome = $chunk->getBiomeId(3, 3);
			if(!self::isFossilBiome($biome)){
				return;
			}
		}

		$random->setSeed($level->getSeed() ^ \pocketmine\level\Level::chunkHash((int) $chunkX, (int) $chunkZ));
		if($random->nextBoundedInt(self::RARITY) !== 0){
			return;
		}

		$surfaceY = $this->getHighestWorkableBlock($chunkX << 4, $chunkZ << 4);
		if($surfaceY <= 0){
			return;
		}

		$y = max(10, min(64, $surfaceY) - 15 - $random->nextBoundedInt(10));
		$this->placeFossilAt($level, (int) $chunkX << 4, $y, (int) $chunkZ << 4, $random);
	}

	public function placeFossilAt(ChunkManager $level, int $x, int $y, int $z, Random $random){
		if(!$this->canPopulateOverworldStructure($level)){
			return null;
		}

		if($this->countEmptyCorners($level, $x, $y, $z - 2) > self::MAX_EMPTY_CORNERS_ALLOWED){
			return null;
		}

		$fossil = new \pocketmine\level\generator\normal\object\Fossil();
		$fossil->placeObject($level, $x, $y, $z - 2, $random, $y < 16);
		return ["x" => $x + 4, "y" => $y, "z" => $z + 2];
	}

	public static function isFossilBiome($biomeId) : bool{
		return (int) $biomeId === Biome::DESERT || (int) $biomeId === Biome::DESERT_HILLS;
	}

	private function countEmptyCorners(ChunkManager $level, int $originX, int $originY, int $originZ) : int{
		$count = 0;
		$minX = $originX;
		$maxX = $originX + \pocketmine\level\generator\normal\object\Fossil::WIDTH_X - 1;
		$minY = $originY;
		$maxY = $originY + \pocketmine\level\generator\normal\object\Fossil::HEIGHT - 1;
		$minZ = $originZ;
		$maxZ = $originZ + \pocketmine\level\generator\normal\object\Fossil::WIDTH_Z - 1;

		foreach([$minX, $maxX] as $x){
			foreach([$minY, $maxY] as $y){
				foreach([$minZ, $maxZ] as $z){
					if($this->isEmptyCorner($level->getBlockIdAt($x, $y, $z))){
						++$count;
					}
				}
			}
		}

		return $count;
	}

	private function isEmptyCorner(int $id) : bool{
		return $id === Block::AIR || $id === Block::WATER || $id === Block::STILL_WATER || $id === Block::LAVA || $id === Block::STILL_LAVA;
	}

	private function getHighestWorkableBlock($x, $z){
		for($y = 127; $y > 0; --$y){
			$id = $this->level->getBlockIdAt((int) $x, $y, (int) $z);
			if($id === Block::SAND || $id === Block::SANDSTONE || $id === Block::STONE){
				return $y;
			}
		}
		return -1;
	}
}
