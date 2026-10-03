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
use lycore\level\generator\biome\Biome;
use lycore\level\generator\noise\Simplex;
use lycore\level\Level;
use lycore\level\SimpleChunkManager;
use lycore\utils\Random;

class GroundCover extends Populator{

	/** @var Simplex[] */
	private $sedimentNoise = [];

	public function populate(ChunkManager $level, $chunkX, $chunkZ, Random $random){
		$chunk = $level->getChunk($chunkX, $chunkZ);
		if($level instanceof Level or $level instanceof SimpleChunkManager){
			$waterHeight = $level->getWaterHeight();
		} else $waterHeight = 0;
		$seed = $level->getSeed();
		$noise = $this->getSedimentNoise($seed);
		for($x = 0; $x < 16; ++$x){
			for($z = 0; $z < 16; ++$z){
				$biomeId = $chunk->getBiomeId($x, $z);
				$biome = Biome::getBiome($biomeId);
				$cover = $biome->getGroundCover();
				if(count($cover) > 0){
					$diffY = 0;
					if(!$cover[0]->isSolid()){
						$diffY = 1;
					}

					$column = $chunk->getBlockIdColumn($x, $z);
					for($y = 127; $y > 0; --$y){
						if($column[$y] !== "\x00" and !Block::get(ord($column[$y]))->isTransparent()){
							break;
						}
					}
					$startY = min(127, $y + $diffY);
					$endY = $startY - count($cover);
					for($y = $startY; $y > $endY and $y >= 0; --$y){
						$b = $cover[$startY - $y];
						if($column[$y] === "\x00" and $b->isSolid()){
							break;
						}
						$above = $chunk->getBlockId($x, $y + 1, $z);
						if($y <= $waterHeight and ($b->getId() == Block::GRASS or $b->getId() == Block::SNOW_LAYER) and ($above == Block::WATER or $above == Block::STILL_WATER)){
							$b = Block::get($this->pickUnderwaterSedimentId($noise, $biomeId, $chunkX, $chunkZ, $x, $z));
						}
						if($y == $waterHeight and $b->getId() == Block::SNOW_LAYER and $chunk->getBlockId($x, $y, $z) == Block::ICE){
							$b = Block::get(Block::ICE);
						}
						if($b->getId() == Block::SNOW_LAYER and $this->hasSurfacePlantInColumn($chunk, $x, $y, $z)){
							continue;
						}
						if($b->getDamage() === 0){
							$chunk->setBlockId($x, $y, $z, $b->getId());
						}else{
							$chunk->setBlock($x, $y, $z, $b->getId(), $b->getDamage());
						}
					}
				}
			}
		}
	}

	private function hasSurfacePlantInColumn($chunk, $x, $y, $z) : bool{
		if($this->isSurfacePlant($chunk->getBlockId($x, $y, $z))){
			return true;
		}
		if($y > 0 and $this->isSurfacePlant($chunk->getBlockId($x, $y - 1, $z))){
			return true;
		}
		if($y < 127 and $this->isSurfacePlant($chunk->getBlockId($x, $y + 1, $z))){
			return true;
		}

		return false;
	}

	private function isSurfacePlant($id) : bool{
		switch($id){
			case Block::TALL_GRASS:
			case Block::DEAD_BUSH:
			case Block::DANDELION:
			case Block::RED_FLOWER:
			case Block::BROWN_MUSHROOM:
			case Block::RED_MUSHROOM:
			case Block::DOUBLE_PLANT:
			case Block::SAPLING:
			case Block::WHEAT_BLOCK:
			case Block::CACTUS:
			case Block::SUGARCANE_BLOCK:
			case Block::PUMPKIN_STEM:
			case Block::MELON_STEM:
			case Block::VINE:
			case Block::WATER_LILY:
			case Block::CARROT_BLOCK:
			case Block::POTATO_BLOCK:
			case Block::BEETROOT_BLOCK:
				return true;
			default:
				return false;
		}
	}

	private function getSedimentNoise(int $seed) : Simplex{
		if(!isset($this->sedimentNoise[$seed])){
			$random = new Random($seed ^ 0x6d2b79f5);
			$this->sedimentNoise[$seed] = new Simplex($random, 2, 1 / 2, 1 / 64);
		}

		return $this->sedimentNoise[$seed];
	}

	private function pickUnderwaterSedimentId(Simplex $noise, int $biomeId, int $chunkX, int $chunkZ, int $x, int $z) : int{
		$worldX = ($chunkX << 4) + $x;
		$worldZ = ($chunkZ << 4) + $z;
		$value = $noise->noise2D($worldX, $worldZ, true);
		switch($biomeId){
			case Biome::RIVER:
			case Biome::FROZEN_RIVER:
			case Biome::SWAMP:
				if($value > 0.34){
					return Block::SAND;
				}
				if($value > 0.10){
					return Block::GRAVEL;
				}
				if($value > -0.24){
					return Block::DIRT;
				}
				return Block::CLAY_BLOCK;

			case Biome::DEEP_OCEAN:
			case Biome::FROZEN_OCEAN:
				if($value > 0.30){
					return Block::SAND;
				}
				if($value > -0.10){
					return Block::GRAVEL;
				}
				if($value > -0.34){
					return Block::CLAY_BLOCK;
				}
				return Block::DIRT;

			case Biome::OCEAN:
			default:
				if($value > 0.22){
					return Block::SAND;
				}
				if($value > -0.06){
					return Block::GRAVEL;
				}
				if($value > -0.30){
					return Block::DIRT;
				}
				return Block::CLAY_BLOCK;
		}
	}
}
