<?php

namespace pocketmine\level\generator\gorigional\structure;

use pocketmine\level\generator\gorigional\JavaRandom;
use pocketmine\level\generator\gorigional\McpeRandom;

/**
 * MCPE 0.14.3 StrongholdFeature：每个世界 3 个要塞。
 *
 * 选址逻辑按反编译：
 * - 初始角度 = genrandFloat * 2π，半径 = 40 + (genrand & 0xf)
 * - 在 ±8 区块内寻找合适群系
 * - 找到后角度 +108°、半径 +8；没找到则角度 +45°、半径 +4
 */
class MapGenStronghold extends MapGenStructure{
	/** @var int[][]|null */
	private $positions = null;

	/** 适合要塞的陆地群系，排除海洋、河流、沙滩、蘑菇岛。 */
	const LAND_BIOMES = [
		1, 2, 3, 4, 5, 6,
		12, 13, 17, 18, 19, 20, 21, 22, 23,
		27, 28, 29, 30, 31, 32, 33, 34, 35, 36,
		37, 38, 39, 129, 140
	];

	protected function canSpawnStructureAtCoords($chunkX, $chunkZ){
		$this->buildPositions();
		foreach($this->positions as $pos){
			if($pos[0] === $chunkX && $pos[1] === $chunkZ){
				return true;
			}
		}
		return false;
	}

	protected function getStructureStart($chunkX, $chunkZ){
		return new StrongholdStart($this->world, $this->rand, $chunkX, $chunkZ);
	}

	private function buildPositions(){
		if($this->positions !== null){
			return;
		}
		$this->positions = [];

		$rnd = new McpeRandom($this->worldSeed);
		$angle = $rnd->nextFloat() * M_PI * 2.0;
		$radius = 40 + ($rnd->nextInt() & 0xF);
		$attempts = 0;

		while(count($this->positions) < 3 && $attempts < 128){
			$attempts++;
			$cx = (int) round(cos($angle) * $radius);
			$cz = (int) round(sin($angle) * $radius);

			if($this->isValidArea($cx, $cz) && !$this->hasPosition($cx, $cz)){
				$this->positions[] = [$cx, $cz];
				$angle += 1.8849555921538759; // 108°
				$radius += 8;
			}else{
				$angle += 0.7853981633974483; // 45°
				$radius += 4;
			}
		}
	}

	private function hasPosition($x, $z){
		foreach($this->positions as $pos){
			if($pos[0] === $x && $pos[1] === $z){
				return true;
			}
		}
		return false;
	}

	private function isValidArea($chunkX, $chunkZ){
		$centerX = ($chunkX << 4) + 8;
		$centerZ = ($chunkZ << 4) + 8;
		$samples = [
			[$centerX, $centerZ],
			[$centerX - 32, $centerZ - 32],
			[$centerX + 32, $centerZ - 32],
			[$centerX - 32, $centerZ + 32],
			[$centerX + 32, $centerZ + 32],
		];
		foreach($samples as $p){
			if(!in_array($this->world->getBiome($p[0], $p[1]), self::LAND_BIOMES, true)){
				return false;
			}
		}
		return true;
	}
}
