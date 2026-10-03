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

namespace lycore\level\generator\biome;

use lycore\level\generator\normal\Normal;
use lycore\utils\Random;

class BiomeLocator{
	/** @var int */
	private $seed;
	/** @var BiomeSelector */
	private $selector;

	private static $nameMap = [
		"ocean" => Biome::OCEAN,
		"frozen_ocean" => Biome::FROZEN_OCEAN,
		"deep_ocean" => Biome::DEEP_OCEAN,
		"plains" => Biome::PLAINS,
		"plain" => Biome::PLAINS,
		"desert" => Biome::DESERT,
		"desert_hills" => Biome::DESERT_HILLS,
		"deserthills" => Biome::DESERT_HILLS,
		"mountains" => Biome::MOUNTAINS,
		"forest" => Biome::FOREST,
		"taiga" => Biome::TAIGA,
		"swamp" => Biome::SWAMP,
		"river" => Biome::RIVER,
		"frozen_river" => Biome::FROZEN_RIVER,
		"ice_plains" => Biome::ICE_PLAINS,
		"ice_mountains" => Biome::ICE_MOUNTAINS,
		"small_mountains" => Biome::SMALL_MOUNTAINS,
		"jungle" => Biome::JUNGLE,
		"jungle_hills" => Biome::JUNGLE_HILLS,
		"beach" => Biome::BEACH,
		"cold_beach" => Biome::COLD_BEACH,
		"birch_forest" => Biome::BIRCH_FOREST,
		"roofed_forest" => Biome::ROOFED_FOREST,
		"cold_taiga" => Biome::COLD_TAIGA,
		"savanna" => Biome::SAVANNA,
		"mesa" => Biome::MESA,
		"mushroom_island" => Biome::MUSHROOM_ISLAND,
		"mushroom_shore" => Biome::MUSHROOM_ISLAND_SHORE,
		"pink_forest" => Biome::PINK_FOREST,
		"golden_forest" => Biome::GOLDEN_FOREST,
		"village" => Biome::VILLAGE,
		"海洋" => Biome::OCEAN,
		"冰冻海洋" => Biome::FROZEN_OCEAN,
		"深海" => Biome::DEEP_OCEAN,
		"平原" => Biome::PLAINS,
		"草原" => Biome::PLAINS,
		"沙漠" => Biome::DESERT,
		"沙漠丘陵" => Biome::DESERT_HILLS,
		"山地" => Biome::MOUNTAINS,
		"森林" => Biome::FOREST,
		"针叶林" => Biome::TAIGA,
		"沼泽" => Biome::SWAMP,
		"河流" => Biome::RIVER,
		"冰河" => Biome::FROZEN_RIVER,
		"冰原" => Biome::ICE_PLAINS,
		"冰山" => Biome::ICE_MOUNTAINS,
		"小山" => Biome::SMALL_MOUNTAINS,
		"丛林" => Biome::JUNGLE,
		"丛林丘陵" => Biome::JUNGLE_HILLS,
		"沙滩" => Biome::BEACH,
		"冷沙滩" => Biome::COLD_BEACH,
		"桦木森林" => Biome::BIRCH_FOREST,
		"黑森林" => Biome::ROOFED_FOREST,
		"冷针叶林" => Biome::COLD_TAIGA,
		"热带草原" => Biome::SAVANNA,
		"恶地" => Biome::MESA,
		"蘑菇岛" => Biome::MUSHROOM_ISLAND,
		"蘑菇岸" => Biome::MUSHROOM_ISLAND_SHORE,
		"粉色森林" => Biome::PINK_FOREST,
		"金色森林" => Biome::GOLDEN_FOREST,
		"村庄" => Biome::VILLAGE
	];

	public function __construct(int $seed){
		$this->seed = $seed;
		$this->selector = Normal::createBiomeSelector(new Random($seed));
	}

	public function getSeed() : int{
		return $this->seed;
	}

	public function getBiomeIdAt(int $x, int $z) : int{
		$hash = $x * 2345803 ^ $z * 9236449 ^ $this->seed;
		$hash *= $hash + 223;
		$xNoise = $hash >> 20 & 3;
		$zNoise = $hash >> 22 & 3;
		if($xNoise === 3){
			$xNoise = 1;
		}
		if($zNoise === 3){
			$zNoise = 1;
		}

		return $this->selector->pickBiome($x + $xNoise - 1, $z + $zNoise - 1)->getId();
	}

	public function findNearestByName(string $name, int $originX, int $originZ, int $radius = 4096, int $step = 16){
		$biomeId = self::parseBiomeId($name);
		if($biomeId === null){
			return null;
		}

		return $this->findNearest($biomeId, $originX, $originZ, $radius, $step);
	}

	public function findNearest(int $biomeId, int $originX, int $originZ, int $radius = 4096, int $step = 16){
		$radius = max($step, $radius);
		$step = max(1, $step);

		$originX = (int) floor($originX / $step) * $step;
		$originZ = (int) floor($originZ / $step) * $step;

		if($this->getBiomeIdAt($originX, $originZ) === $biomeId){
			return ["x" => $originX, "z" => $originZ, "biome" => $biomeId];
		}

		for($r = $step; $r <= $radius; $r += $step){
			for($x = $originX - $r; $x <= $originX + $r; $x += $step){
				if($this->getBiomeIdAt($x, $originZ - $r) === $biomeId){
					return ["x" => $x, "z" => $originZ - $r, "biome" => $biomeId];
				}
				if($this->getBiomeIdAt($x, $originZ + $r) === $biomeId){
					return ["x" => $x, "z" => $originZ + $r, "biome" => $biomeId];
				}
			}
			for($z = $originZ - $r + $step; $z <= $originZ + $r - $step; $z += $step){
				if($this->getBiomeIdAt($originX - $r, $z) === $biomeId){
					return ["x" => $originX - $r, "z" => $z, "biome" => $biomeId];
				}
				if($this->getBiomeIdAt($originX + $r, $z) === $biomeId){
					return ["x" => $originX + $r, "z" => $z, "biome" => $biomeId];
				}
			}
		}

		return null;
	}

	public static function parseBiomeId(string $input){
		if(is_numeric($input)){
			return (int) $input;
		}

		$key = strtolower(str_replace([" ", "-"], "_", trim($input)));
		return self::$nameMap[$key] ?? null;
	}

	protected function generateChunk($chunkX, $chunkZ){
	}

	protected function populateChunk($chunkX, $chunkZ){
	}
}
