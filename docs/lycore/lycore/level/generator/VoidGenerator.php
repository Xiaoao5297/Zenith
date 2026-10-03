<?php

/*
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

namespace lycore\level\generator;

use lycore\block\Block;
use lycore\level\ChunkManager;
use lycore\level\format\FullChunk;
use lycore\level\generator\biome\Biome;
use lycore\math\Vector3;
use lycore\utils\Random;

class VoidGenerator extends Generator{
	/** @var ChunkManager */
	private $level;
	/** @var Random */
	private $random;
	/** @var array */
	private $options;

	public function __construct(array $settings = []){
		$this->options = $settings;
	}

	public function getSettings(){
		return $this->options;
	}

	public function getName() : string{
		return "void";
	}

	public function init(ChunkManager $level, Random $random){
		$this->level = $level;
		$this->random = $random;
	}

	public function generateChunk($chunkX, $chunkZ){
		/** @var FullChunk $chunk */
		$chunk = clone $this->level->getChunk($chunkX, $chunkZ);
		$chunk->setGenerated();

		$biome = Biome::getBiome(Biome::PLAINS);
		$color = $biome->getColor();
		$r = $color >> 16;
		$g = ($color >> 8) & 0xff;
		$b = $color & 0xff;

		for($z = 0; $z < 16; ++$z){
			for($x = 0; $x < 16; ++$x){
				$chunk->setBiomeId($x, $z, Biome::PLAINS);
				$chunk->setBiomeColor($x, $z, $r, $g, $b);
				for($y = 0; $y < 128; ++$y){
					$chunk->setBlockId($x, $y, $z, Block::AIR);
				}
			}
		}

		$spawn = $this->getSpawn();
		$spawnGroundX = (int) floor($spawn->x);
		$spawnGroundY = (int) floor($spawn->y) - 1;
		$spawnGroundZ = (int) floor($spawn->z);
		if($spawnGroundY >= 0 and $spawnGroundY < 128 and ($spawnGroundX >> 4) === $chunkX and ($spawnGroundZ >> 4) === $chunkZ){
			$chunk->setBlockId($spawnGroundX & 0x0f, $spawnGroundY, $spawnGroundZ & 0x0f, Block::GRASS);
		}
		$chunk->setX($chunkX);
		$chunk->setZ($chunkZ);
		$this->level->setChunk($chunkX, $chunkZ, $chunk);
	}

	public function populateChunk($chunkX, $chunkZ){
	}

	public function getSpawn(){
		return new Vector3(128, 72, 128);
	}
}
