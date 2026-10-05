<?php

namespace pocketmine\level\generator\gorigional\layer;

use pocketmine\level\generator\gorigional\Int64;

/**
 * 移植自 SCAXE-GO-CE pkg/level/generator/gorigional/layer/layer.go。
 */
abstract class GenLayer{
	const BIOME_OCEAN = 0;
	const BIOME_PLAINS = 1;
	const BIOME_DESERT = 2;
	const BIOME_EXTREME_HILLS = 3;
	const BIOME_FOREST = 4;
	const BIOME_TAIGA = 5;
	const BIOME_SWAMPLAND = 6;
	const BIOME_RIVER = 7;
	const BIOME_HELL = 8;
	const BIOME_SKY = 9;
	const BIOME_FROZEN_OCEAN = 10;
	const BIOME_FROZEN_RIVER = 11;
	const BIOME_ICE_PLAINS = 12;
	const BIOME_ICE_MOUNTAINS = 13;
	const BIOME_MUSHROOM_ISLAND = 14;
	const BIOME_MUSHROOM_ISLAND_SHORE = 15;
	const BIOME_BEACH = 16;
	const BIOME_DESERT_HILLS = 17;
	const BIOME_FOREST_HILLS = 18;
	const BIOME_TAIGA_HILLS = 19;
	const BIOME_EXTREME_HILLS_EDGE = 20;
	const BIOME_JUNGLE = 21;
	const BIOME_JUNGLE_HILLS = 22;
	const BIOME_JUNGLE_EDGE = 23;
	const BIOME_DEEP_OCEAN = 24;
	const BIOME_STONE_BEACH = 25;
	const BIOME_COLD_BEACH = 26;
	const BIOME_BIRCH_FOREST = 27;
	const BIOME_BIRCH_FOREST_HILLS = 28;
	const BIOME_ROOFED_FOREST = 29;
	const BIOME_COLD_TAIGA = 30;
	const BIOME_COLD_TAIGA_HILLS = 31;
	const BIOME_MEGA_TAIGA = 32;
	const BIOME_MEGA_TAIGA_HILLS = 33;
	const BIOME_EXTREME_HILLS_PLUS = 34;
	const BIOME_SAVANNA = 35;
	const BIOME_SAVANNA_PLATEAU = 36;
	const BIOME_MESA = 37;
	const BIOME_MESA_PLATEAU_F = 38;
	const BIOME_MESA_PLATEAU = 39;

	/** @var GenLayer|null */
	protected $parent;
	/** @var int */
	protected $baseSeed;
	/** @var int */
	protected $worldGenSeed;
	/** @var int */
	protected $chunkSeed;

	public function __construct($baseSeed, GenLayer $parent = null){
		$this->baseSeed = $baseSeed;
		$this->baseSeed = self::mixSeed($this->baseSeed, $baseSeed);
		$this->baseSeed = self::mixSeed($this->baseSeed, $baseSeed);
		$this->baseSeed = self::mixSeed($this->baseSeed, $baseSeed);
		$this->parent = $parent;
	}

	public static function mixSeed($current, $add){
		$inner = Int64::add(Int64::mul($current, 6364136223846793005), 1442695040888963407);
		$current = Int64::mul($current, $inner);
		$current = Int64::add($current, $add);
		return $current;
	}

	public function initWorldGenSeed($seed){
		$this->worldGenSeed = $seed;
		if($this->parent !== null){
			$this->parent->initWorldGenSeed($seed);
		}

		$this->worldGenSeed = self::mixSeed($this->worldGenSeed, $this->baseSeed);
		$this->worldGenSeed = self::mixSeed($this->worldGenSeed, $this->baseSeed);
		$this->worldGenSeed = self::mixSeed($this->worldGenSeed, $this->baseSeed);
	}

	protected function initChunkSeed($x, $z){
		$this->chunkSeed = $this->worldGenSeed;
		$this->chunkSeed = self::mixSeed($this->chunkSeed, $x);
		$this->chunkSeed = self::mixSeed($this->chunkSeed, $z);
		$this->chunkSeed = self::mixSeed($this->chunkSeed, $x);
		$this->chunkSeed = self::mixSeed($this->chunkSeed, $z);
	}

	protected function nextInt($max){
		$val = ($this->chunkSeed >> 24) % $max;
		if($val < 0){
			$val += $max;
		}

		$inner = Int64::add(Int64::mul($this->chunkSeed, 6364136223846793005), 1442695040888963407);
		$this->chunkSeed = Int64::mul($this->chunkSeed, $inner);
		$this->chunkSeed = Int64::add($this->chunkSeed, $this->worldGenSeed);

		return $val;
	}

	protected function selectRandom(...$choices){
		return $choices[$this->nextInt(count($choices))];
	}

	public static function magnify($seed, GenLayer $parent, $times){
		$layer = $parent;
		for($i = 0; $i < $times; $i++){
			$layer = new GenLayerZoom($seed + $i, $layer);
		}
		return $layer;
	}

	/**
	 * @return int[]
	 */
	abstract public function getInts($x, $z, $width, $depth);
}
