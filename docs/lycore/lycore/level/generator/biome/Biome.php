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

use lycore\block\Block;
use lycore\level\ChunkManager;
use lycore\level\generator\normal\biome\SwampBiome;
use lycore\level\generator\normal\biome\SavannaBiome;
use lycore\level\generator\normal\biome\BeachBiome;
use lycore\level\generator\normal\biome\ColdBeachBiome;
use lycore\level\generator\normal\biome\DesertBiome;
use lycore\level\generator\normal\biome\DesertHillsBiome;
use lycore\level\generator\normal\biome\ForestBiome;
use lycore\level\generator\normal\biome\IcePlainsBiome;
use lycore\level\generator\normal\biome\IceMountainsBiome;
use lycore\level\generator\normal\biome\MountainsBiome;
use lycore\level\generator\normal\biome\MushroomIsland;
use lycore\level\generator\normal\biome\OceanBiome;
use lycore\level\generator\normal\biome\FrozenOceanBiome;
use lycore\level\generator\normal\biome\DeepOceanBiome;
use lycore\level\generator\normal\biome\PlainBiome;
use lycore\level\generator\normal\biome\RiverBiome;
use lycore\level\generator\normal\biome\FrozenRiverBiome;
use lycore\level\generator\normal\biome\SmallMountainsBiome;
use lycore\level\generator\normal\biome\TaigaBiome;
use lycore\level\generator\normal\biome\TaigaHellBiome;
use lycore\level\generator\normal\biome\ColdTaigaBiome;
use lycore\level\generator\normal\biome\ColdTaigaHellBiome;
use lycore\level\generator\normal\biome\JungleBiome;
use lycore\level\generator\normal\biome\JungleHillsBiome;
use lycore\level\generator\normal\biome\MesaBiome;
use lycore\level\generator\normal\biome\DrakOakBiome;
use lycore\level\generator\normal\biome\PinkForestBiome;
use lycore\level\generator\normal\biome\GoldenForestBiome;
use lycore\level\generator\normal\biome\VillageBiome;
use lycore\level\generator\ender\EndBiome;
use lycore\level\generator\hell\HellBiome;
use lycore\level\generator\populator\Populator;
use lycore\utils\Random;

use lycore\level\generator\populator\Flower;

abstract class Biome{

	const OCEAN = 0; //海洋
	const PLAINS = 1;  //平原
	const DESERT = 2; //沙漠
	const MOUNTAINS = 3; //风袭丘陵(山)
	const FOREST = 4; //森林
	const TAIGA = 5; //针叶林
	const SWAMP = 6; //沼泽
	const RIVER = 7; //河流
	const HELL = 8; //地狱
	const END = 9; //末地
	const FROZEN_OCEAN = 10; //冻洋
	const FROZEN_RIVER = 11; //冻河
	const ICE_PLAINS = 12; //积雪平原
	const ICE_MOUNTAINS = 13;  //雪山
	const MUSHROOM_ISLAND = 14; //蘑菇岛
	const MUSHROOM_ISLAND_SHORE = 15;  //蘑菇岛岸
	const BEACH = 16; //沙滩
	const DESERT_HILLS = 17; //沙漠丘陵
	const FOREST_HILLS = 18; //繁茂丘陵
	const TAIGA_HILLS = 19; //针叶林丘陵
	const SMALL_MOUNTAINS = 20; //风袭丘陵边缘
	const JUNGLE = 21; //热带雨林(丛林)
	const JUNGLE_HILLS = 22; //热带雨林丘陵
	const JUNGLE_EDGE = 23; //稀疏热带雨林
	const DEEP_OCEAN = 24; //深海
	const STONE_BEACH = 25; //石岸
	const COLD_BEACH = 26; //积雪沙滩
	const BIRCH_FOREST = 27; //白桦木森林
	const BIRCH_FOREST_HILLS = 28; //桦木森林丘陵
	const ROOFED_FOREST = 29; //黑森林
	const COLD_TAIGA = 30; //积雪针叶林
	const COLD_TAIGA_HILLS = 31; //积雪针叶林丘陵
	const MEGA_TAIGA = 32; //原始针叶林
	const MEGA_TAIGA_HILLS = 33; //原始针叶林丘陵
	const EXTREME_HILLS_PLUS_TREES = 34; //风袭丘陵
	const SAVANNA = 35; //热带草原
	const SAVANNA_PLATEAU = 36; //热带高原
	const MESA = 37; //恶地（黏土山）
	const MESA_PLATEAU_F = 38; //繁茂恶地
	const MESA_PLATEAU = 39; //恶地高原
	const PINK_FOREST = 40; //樱花树林
	const GOLDEN_FOREST = 41; //枫树林
	const SKY = 42;
	const VILLAGE = 43; //村庄
	
	const MAX_BIOMES = 317;

	/** @var Biome[] */
	private static $biomes = [];

	private $id;
	private $registered = false;
	/** @var Populator[] */
	private $populators = [];

	private $minElevation;
	private $maxElevation;

	private $groundCover = [];

	protected $rainfall = 0.5;
	protected $temperature = 0.5;
	protected $grassColor = 0;

	protected static function register($id, Biome $biome){
		self::$biomes[(int) $id] = $biome;
		$biome->setId((int) $id);
		$biome->grassColor = self::generateBiomeColor($biome->getTemperature(), $biome->getRainfall());

		$flowerPopFound = false;

		foreach($biome->getPopulators() as $populator){
			if($populator instanceof Flower){
				$flowerPopFound = true;
				break;
			}
		}

		$isSnowySurface = self::isSnowyBiomeId((int) $id);
		if(!$isSnowySurface){
			$groundCover = $biome->getGroundCover();
			$isSnowySurface = isset($groundCover[0]) and $groundCover[0] instanceof Block and $groundCover[0]->getId() === Block::SNOW_LAYER;
		}

		if($flowerPopFound === false and !$isSnowySurface){
			$flower = new Flower();
			$flower->setBaseAmount(2);
			$biome->addPopulator($flower);
		}
	}

	private static function isSnowyBiomeId($id){
		switch((int) $id){
			case self::FROZEN_OCEAN:
			case self::FROZEN_RIVER:
			case self::ICE_PLAINS:
			case self::ICE_MOUNTAINS:
			case self::COLD_BEACH:
			case self::COLD_TAIGA:
			case self::COLD_TAIGA_HILLS:
				return true;
			default:
				return false;
		}
	}

	public static function init(){
		self::register(self::BEACH, new BeachBiome());
		self::register(self::COLD_BEACH, new ColdBeachBiome());
		self::register(self::OCEAN, new OceanBiome());
		self::register(self::FROZEN_OCEAN, new FrozenOceanBiome());
		self::register(self::DEEP_OCEAN, new DeepOceanBiome());
		self::register(self::PLAINS, new PlainBiome());
		self::register(self::DESERT, new DesertBiome());
		self::register(self::DESERT_HILLS, new DesertHillsBiome());
		self::register(self::MOUNTAINS, new MountainsBiome());
		self::register(self::FOREST, new ForestBiome());
		self::register(self::TAIGA, new TaigaBiome());
		self::register(self::TAIGA_HILLS, new TaigaHellBiome());
		self::register(self::COLD_TAIGA, new ColdTaigaBiome());
		self::register(self::COLD_TAIGA_HILLS, new ColdTaigaHellBiome());
		self::register(self::SWAMP, new SwampBiome());
		self::register(self::RIVER, new RiverBiome());
		self::register(self::FROZEN_RIVER, new FrozenRiverBiome());
		
		self::register(self::ICE_PLAINS, new IcePlainsBiome());
		self::register(self::ICE_MOUNTAINS, new IceMountainsBiome());


		self::register(self::SMALL_MOUNTAINS, new SmallMountainsBiome());
		self::register(self::JUNGLE, new JungleBiome());
		self::register(self::JUNGLE_HILLS, new JungleHillsBiome());
		self::register(self::SAVANNA, new SavannaBiome());
		self::register(self::MESA, new MesaBiome());
		self::register(self::MUSHROOM_ISLAND, new MushroomIsland());
		self::register(self::MUSHROOM_ISLAND_SHORE, new MushroomIsland());
		self::register(self::ROOFED_FOREST, new DrakOakBiome());
		self::register(self::HELL, new HellBiome());
		self::register(self::END, new EndBiome());
		self::register(self::PINK_FOREST, new PinkForestBiome());
		self::register(self::GOLDEN_FOREST, new GoldenForestBiome());
		self::register(self::VILLAGE, new VillageBiome());

		self::register(self::BIRCH_FOREST, new ForestBiome(ForestBiome::TYPE_BIRCH));
	}

	/**
	 * @param $id
	 *
	 * @return Biome
	 */
	public static function getBiome($id){
		return isset(self::$biomes[$id]) ? self::$biomes[$id] : self::$biomes[self::OCEAN];
	}

	public static function getChineseNameById(int $id) : string{
		switch($id){
			case self::OCEAN:
				return "海洋";
			case self::PLAINS:
				return "平原";
			case self::DESERT:
				return "沙漠";
			case self::MOUNTAINS:
				return "山地";
			case self::FOREST:
				return "森林";
			case self::TAIGA:
				return "针叶林";
			case self::SWAMP:
				return "沼泽";
			case self::RIVER:
				return "河流";
			case self::HELL:
				return "地狱";
			case self::END:
				return "末地";
			case self::FROZEN_OCEAN:
				return "冻洋";
			case self::FROZEN_RIVER:
				return "冻河";
			case self::ICE_PLAINS:
				return "冰原";
			case self::ICE_MOUNTAINS:
				return "冰山";
			case self::MUSHROOM_ISLAND:
				return "蘑菇岛";
			case self::MUSHROOM_ISLAND_SHORE:
				return "蘑菇岛岸";
			case self::BEACH:
				return "沙滩";
			case self::DESERT_HILLS:
				return "沙漠丘陵";
			case self::FOREST_HILLS:
				return "森林丘陵";
			case self::TAIGA_HILLS:
				return "针叶林丘陵";
			case self::SMALL_MOUNTAINS:
				return "小型山地";
			case self::JUNGLE:
				return "丛林";
			case self::JUNGLE_HILLS:
				return "丛林丘陵";
			case self::JUNGLE_EDGE:
				return "丛林边缘";
			case self::DEEP_OCEAN:
				return "深海";
			case self::STONE_BEACH:
				return "石滩";
			case self::COLD_BEACH:
				return "冷沙滩";
			case self::BIRCH_FOREST:
				return "桦木森林";
			case self::BIRCH_FOREST_HILLS:
				return "桦木森林丘陵";
			case self::ROOFED_FOREST:
				return "黑森林";
			case self::COLD_TAIGA:
				return "冷针叶林";
			case self::COLD_TAIGA_HILLS:
				return "冷针叶林丘陵";
			case self::MEGA_TAIGA:
				return "巨型针叶林";
			case self::MEGA_TAIGA_HILLS:
				return "巨型针叶林丘陵";
			case self::EXTREME_HILLS_PLUS_TREES:
				return "山地森林";
			case self::SAVANNA:
				return "热带草原";
			case self::SAVANNA_PLATEAU:
				return "热带高原";
			case self::MESA:
				return "恶地";
			case self::MESA_PLATEAU_F:
				return "繁茂恶地高原";
			case self::MESA_PLATEAU:
				return "恶地高原";
			case self::PINK_FOREST:
				return "樱花森林";
			case self::GOLDEN_FOREST:
				return "金色森林";
			case self::SKY:
				return "天空";
			case self::VILLAGE:
				return "村庄";
			default:
				return "未知生物群系(" . $id . ")";
		}
	}

	public function clearPopulators(){
		$this->populators = [];
	}

	public function addPopulator(Populator $populator){
		$this->populators[get_class($populator)] = $populator;
	}
	
	public function removePopulator($class){
		if(isset($this->populators[$class])){
			unset($this->populators[$class]);
		}
	}

	public function populateChunk(ChunkManager $level, $chunkX, $chunkZ, Random $random){
		foreach($this->populators as $populator){
			$populator->populate($level, $chunkX, $chunkZ, $random);
		}
	}

	public function getPopulators(){
		return $this->populators;
	}

	public function setId($id){
		if(!$this->registered){
			$this->registered = true;
			$this->id = $id;
		}
	}

	public function getId(){
		return $this->id;
	}

	public abstract function getName();

	public function getMinElevation(){
		return $this->minElevation;
	}

	public function getMaxElevation(){
		return $this->maxElevation;
	}

	public function setElevation($min, $max){
		$this->minElevation = $min;
		$this->maxElevation = $max;
	}

	/**
	 * @return Block[]
	 */
	public function getGroundCover(){
		return $this->groundCover;
	}

	/**
	 * @param Block[] $covers
	 */
	public function setGroundCover(array $covers){
		$this->groundCover = $covers;
	}

	public function getTemperature(){
		return $this->temperature;
	}

	public function getRainfall(){
		return $this->rainfall;
	}

	private static function generateBiomeColor($temperature, $rainfall){
		$x = (1 - $temperature) * 255;
		$z = (1 - $rainfall * $temperature) * 255;
		$c = self::interpolateColor(256, $x, $z, [0x47, 0xd0, 0x33], [0x6c, 0xb4, 0x93], [0xbf, 0xb6, 0x55], [0x80, 0xb4, 0x97]);
		return ((int) ($c[0] << 16)) | (int) (($c[1] << 8)) | (int) ($c[2]);
	}


	private static function interpolateColor($size, $x, $z, $c1, $c2, $c3, $c4){
		$l1 = self::lerpColor($c1, $c2, $x / $size);
		$l2 = self::lerpColor($c3, $c4, $x / $size);

		return self::lerpColor($l1, $l2, $z / $size);
	}

	private static function lerpColor($a, $b, $s){
		$invs = 1 - $s;
		return [$a[0] * $invs + $b[0] * $s, $a[1] * $invs + $b[1] * $s, $a[2] * $invs + $b[2] * $s];
	}


	/**
	 * @return int (Red|Green|Blue)
	 */
	abstract public function getColor();
}
