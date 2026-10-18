<?php

/***
 *  _____                _  __   __
 * /__  /  ___   ____   (_)/ /_ / /_
 *   / /  / _ \ / __ \ / // __// __ \
 *  / /__/  __// / / // // /_ / / / /
 * /____/\___//_/ /_//_/ \__//_/ /_/
 *
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * @author Xiaoao
 * @link https://github.com/Xiaoao5297/Zenith
 *
 *
*/

namespace pocketmine\level\generator\gorigional;

use pocketmine\block\Block;
use pocketmine\level\ChunkManager;
use pocketmine\level\format\FullChunk;
use pocketmine\level\generator\Generator;
use pocketmine\level\Level;
use pocketmine\level\generator\gorigional\biome\BiomeRegistry;
use pocketmine\level\generator\gorigional\layer\GenLayer;
use pocketmine\level\generator\gorigional\layer\LayerFactory;
use pocketmine\level\generator\gorigional\McpeRandom;
use pocketmine\level\generator\gorigional\noise\OctavesNoise;
use pocketmine\level\generator\gorigional\noise\PerlinSimplexGenerator;
use pocketmine\level\generator\gorigional\object\BlockPos;
use pocketmine\level\generator\gorigional\object\ChunkManagerAdapter;
use pocketmine\level\generator\gorigional\object\Dungeon;
use pocketmine\level\generator\gorigional\object\Lake;
use pocketmine\level\generator\gorigional\structure\GeneratorWorld;
use pocketmine\level\generator\gorigional\structure\MapGenCaves;
use pocketmine\level\generator\gorigional\structure\MapGenMineshaft;
use pocketmine\level\generator\gorigional\structure\MapGenRavine;
use pocketmine\level\generator\gorigional\structure\MapGenScatteredFeature;
use pocketmine\level\generator\gorigional\structure\MapGenStronghold;
use pocketmine\level\generator\gorigional\structure\MapGenVillage;
use pocketmine\level\generator\gorigional\structure\WorldChunkManager;
use pocketmine\math\Vector3;
use pocketmine\utils\Random;

/**
 * Java 1.7 风格 ChunkGeneratorOverworld，对应 MCPE 0.14 地形。
 * 移植自 SCAXE-GO-CE pkg/level/generator/gorigional/generator.go。
 */
class Gorigional extends Generator{
	const NAME = "gorigional";

	const COORDINATE_SCALE = 684.412;
	const HEIGHT_SCALE = 684.412;
	// MCPE 的高度 128 修正：min limit 的 Y 缩放为 855.515，输出除以 256。
	const MIN_LIMIT_HEIGHT_SCALE = 855.515;
	const LOWER_LIMIT_SCALE = 256.0;
	const UPPER_LIMIT_SCALE = 512.0;
	const DEPTH_NOISE_SCALE_X = 200.0;
	const DEPTH_NOISE_SCALE_Z = 200.0;
	const MAIN_NOISE_SCALE_X = 80.0;
	const MAIN_NOISE_SCALE_Y = 160.0;
	const MAIN_NOISE_SCALE_Z = 80.0;
	const BASE_SIZE = 8.5;
	const STRETCH_Y = 12.0;
	const BIOME_DEPTH_OFFSET = 0.0;
	const BIOME_DEPTH_WEIGHT = 1.0;
	const BIOME_SCALE_OFFSET = 0.0;
	const BIOME_SCALE_WEIGHT = 1.0;
	const SEA_LEVEL = 63;
	const MAX_HEIGHT = 128;

	/** @var ChunkManager */
	private $level;
	/** @var int */
	private $seed;

	/** @var OctavesNoise */
	private $minLimitPerlinNoise;
	/** @var OctavesNoise */
	private $maxLimitPerlinNoise;
	/** @var OctavesNoise */
	private $mainPerlinNoise;
	/** @var PerlinSimplexGenerator */
	private $surfaceNoise;
	/** @var OctavesNoise */
	private $scaleNoise;
	/** @var OctavesNoise */
	private $depthNoise;
	/** @var OctavesNoise */
	private $forestNoise;

	/** @var \pocketmine\level\generator\gorigional\layer\GenLayer */
	private $genLayer;
	/** @var \pocketmine\level\generator\gorigional\layer\GenLayer */
	private $biomeGen;

	/** @var float[] */
	private $biomeWeights = [];

	/** @var MapGenCaves */
	private $caves;
	/** @var MapGenRavine */
	private $ravines;
	/** @var MapGenMineshaft */
	private $mineshaft;
	/** @var MapGenScatteredFeature */
	private $scatteredFeature;
	/** @var MapGenVillage */
	private $village;
	/** @var MapGenStronghold|null */
	private $stronghold;
	/** @var GeneratorWorld */
	private $world;

	public function __construct(array $settings = []){

	}

	public function init(ChunkManager $level, Random $random){
		$this->level = $level;
		$this->seed = $level->getSeed();

		$rnd = new McpeRandom($this->seed);

		$this->minLimitPerlinNoise = new OctavesNoise($rnd, 16);
		$this->maxLimitPerlinNoise = new OctavesNoise($rnd, 16);
		$this->mainPerlinNoise = new OctavesNoise($rnd, 8);
		$this->surfaceNoise = new PerlinSimplexGenerator($rnd, 4);
		$this->scaleNoise = new OctavesNoise($rnd, 10);
		$this->depthNoise = new OctavesNoise($rnd, 16);
		// MCPE 也会构造第 7 个噪声。即使 getHeights 不使用它，也必须消费同一段随机流。
		$this->forestNoise = new OctavesNoise($rnd, 8);

		BiomeRegistry::refreshMesa($this->seed);

		$layers = LayerFactory::initializeAll($this->seed);
		$this->genLayer = $layers[0];
		$this->biomeGen = $layers[1];

		$this->biomeWeights = array_fill(0, 25, 0.0);
		for($i = -2; $i <= 2; $i++){
			for($j = -2; $j <= 2; $j++){
				$f = 10.0 / sqrt($i * $i + $j * $j + 0.2);
				$this->biomeWeights[$i + 2 + ($j + 2) * 5] = $f;
			}
		}

		$this->caves = new MapGenCaves($this->seed);
		$this->ravines = new MapGenRavine($this->seed);
		$this->caves->maxHeight = self::MAX_HEIGHT;
		$this->ravines->maxHeight = self::MAX_HEIGHT;
		$this->world = new GeneratorWorld($level, $this->seed, new WorldChunkManager($this->genLayer, $this->biomeGen));
		$this->mineshaft = new MapGenMineshaft($this->seed);
		$this->scatteredFeature = new MapGenScatteredFeature($this->seed);
		$this->village = new MapGenVillage($this->seed);

		// MCPE 0.14 原版默认不自然生成要塞；仅在世界配置显式开启时创建。
		$generateStronghold = false;
		if($level instanceof Level){
			$value = $level->getServer()->getAdvancedProperty("world.generate-stronghold", false);
			if(is_bool($value)){
				$generateStronghold = $value;
			}elseif(is_string($value)){
				$generateStronghold = in_array(strtolower($value), ["1", "true", "on", "yes"], true);
			}elseif(is_int($value)){
				$generateStronghold = $value !== 0;
			}
		}
		$this->stronghold = $generateStronghold ? new MapGenStronghold($this->seed) : null;

		if($level instanceof Level){
			$level->getServer()->getLogger()->info("[Gorigional] MCPE terrain generator active, seed=" . $this->seed);
		}
	}

	public function takeDeferredTiles(){
		return $this->world->takeDeferredTiles();
	}

	public function getName(){
		return self::NAME;
	}

	public function getSettings(){
		return [];
	}

	public function getWaterHeight() : int{
		return self::SEA_LEVEL;
	}

	public function getSpawn(){
		$spawnX = 0;
		$spawnZ = 0;

		if($this->level !== null){
			$chunk = $this->level->getChunk(0, 0);
			if($chunk !== null){
				for($y = self::MAX_HEIGHT - 1; $y >= 0; $y--){
					$blockId = $chunk->getBlockId(0, $y, 0);

					if($blockId === 0 || $blockId === 8 || $blockId === 9 || $blockId === 10 || $blockId === 11 || $blockId === 18 || $blockId === 161){
						continue;
					}

					$hasSpace = true;
					for($check = 1; $check <= 2; $check++){
						if($y + $check >= self::MAX_HEIGHT || $chunk->getBlockId(0, $y + $check, 0) !== 0){
							$hasSpace = false;
							break;
						}
					}
					if($hasSpace){
						return new Vector3($spawnX, $y + 1, $spawnZ);
					}
				}
			}
		}

		return new Vector3(0, 65, 0);
	}

	private function generateHeightmap(array &$heightMap, array &$depthRegion, array &$mainNoiseRegion, array &$minLimitRegion, array &$maxLimitRegion, $x, $y, $z, array $biomesForGeneration){
		$horizontalPoints = 5;
		$verticalPoints = intdiv(self::MAX_HEIGHT, 8) + 1;

		$depthRegion = $this->depthNoise->generateNoiseOctaves2D($depthRegion, $x, $z, $horizontalPoints, $horizontalPoints, self::DEPTH_NOISE_SCALE_X, self::DEPTH_NOISE_SCALE_Z);

		$f = self::COORDINATE_SCALE;
		$f1 = self::HEIGHT_SCALE;

		$mainNoiseRegion = $this->mainPerlinNoise->generateNoiseOctaves($mainNoiseRegion, $x, $y, $z, $horizontalPoints, $verticalPoints, $horizontalPoints, $f / self::MAIN_NOISE_SCALE_X, $f1 / self::MAIN_NOISE_SCALE_Y, $f / self::MAIN_NOISE_SCALE_Z);
		$minLimitRegion = $this->minLimitPerlinNoise->generateNoiseOctaves($minLimitRegion, $x, $y, $z, $horizontalPoints, $verticalPoints, $horizontalPoints, $f, self::MIN_LIMIT_HEIGHT_SCALE, $f);
		$maxLimitRegion = $this->maxLimitPerlinNoise->generateNoiseOctaves($maxLimitRegion, $x, $y, $z, $horizontalPoints, $verticalPoints, $horizontalPoints, $f, $f1, $f);

		$i = 0;
		$j = 0;

		// 10×10 群系 ID 先缓存成高度参数，避免 625 次静态查表。
		$minHeights = [];
		$maxHeights = [];
		for($biomeIndex = 0; $biomeIndex < 100; $biomeIndex++){
			$biomeID = $biomesForGeneration[$biomeIndex] & 0xFF;
			if(!isset($minHeights[$biomeID])){
				$biome = BiomeRegistry::getBiome($biomeID);
				$minHeights[$biomeID] = $biome->getMinElevation();
				$maxHeights[$biomeID] = $biome->getMaxElevation();
			}
		}

		for($k = 0; $k < $horizontalPoints; $k++){
			for($l = 0; $l < $horizontalPoints; $l++){
				$f2 = 0.0;
				$f3 = 0.0;
				$f4 = 0.0;

				$centerBiomeID = $biomesForGeneration[$k + 2 + ($l + 2) * 10] & 0xFF;
				$centerBaseHeight = $minHeights[$centerBiomeID];

				for($j1 = -2; $j1 <= 2; $j1++){
					for($k1 = -2; $k1 <= 2; $k1++){
						$biomeID = $biomesForGeneration[$k + $j1 + 2 + ($l + $k1 + 2) * 10] & 0xFF;
						$baseHeight = $minHeights[$biomeID];
						$heightVar = $maxHeights[$biomeID];

						$f5 = self::BIOME_DEPTH_OFFSET + $baseHeight * self::BIOME_DEPTH_WEIGHT;
						$f6 = self::BIOME_SCALE_OFFSET + $heightVar * self::BIOME_SCALE_WEIGHT;

						$f7 = $this->biomeWeights[$j1 + 2 + ($k1 + 2) * 5] / ($f5 + 2.0);
						if($baseHeight > $centerBaseHeight){
							$f7 /= 2.0;
						}

						$f2 += $f6 * $f7;
						$f3 += $f5 * $f7;
						$f4 += $f7;
					}
				}

				$f2 /= $f4;
				$f3 /= $f4;
				$f2 = $f2 * 0.9 + 0.1;
				$f3 = ($f3 * 4.0 - 1.0) / 8.0;

				$d7 = $depthRegion[$j] / 8000.0;
				if($d7 < 0.0){
					$d7 = -$d7 * 0.3;
				}
				$d7 = $d7 * 3.0 - 2.0;
				if($d7 < 0.0){
					$d7 /= 2.0;
					if($d7 < -1.0){
						$d7 = -1.0;
					}
					$d7 /= 1.4;
					$d7 /= 2.0;
				}else{
					if($d7 > 1.0){
						$d7 = 1.0;
					}
					$d7 /= 8.0;
				}
				$j++;

				$d8 = $f3;
				$d9 = $f2;
				$d8 += $d7 * 0.2;
				$d8 = $d8 * self::BASE_SIZE / 8.0;
				$d0 = self::BASE_SIZE + $d8 * 4.0;

				for($l1 = 0; $l1 < $verticalPoints; $l1++){
					$d1 = ($l1 - $d0) * self::STRETCH_Y / $d9;

					if($d1 < 0.0){
						$d1 *= 4.0;
					}

					$minVal = $minLimitRegion[$i] / self::LOWER_LIMIT_SCALE;
					$maxVal = $maxLimitRegion[$i] / self::UPPER_LIMIT_SCALE;
					$mainVal = ($mainNoiseRegion[$i] / 10.0 + 1.0) / 2.0;

					$d5 = self::clampedLerp($minVal, $maxVal, $mainVal) - $d1;

					if($l1 > $verticalPoints - 4){
						$d6 = ($l1 - ($verticalPoints - 4)) / 3.0;
						$d5 = $d5 * (1.0 - $d6) + -10.0 * $d6;
					}

					$heightMap[$i] = $d5;
					$i++;
				}
			}
		}
	}

	private function setBlocksInChunk($chunkX, $chunkZ, FullChunk $chunk){
		$biomesForGeneration = $this->genLayer->getInts($chunkX * 4 - 2, $chunkZ * 4 - 2, 10, 10);

		$verticalPoints = intdiv(self::MAX_HEIGHT, 8) + 1;
		$horizontalPoints = 5;
		$bufferSize = $horizontalPoints * $horizontalPoints * $verticalPoints;

		$heightMap = array_fill(0, $bufferSize, 0.0);
		$depthRegion = [];
		$mainNoiseRegion = [];
		$minLimitRegion = [];
		$maxLimitRegion = [];

		$this->generateHeightmap($heightMap, $depthRegion, $mainNoiseRegion, $minLimitRegion, $maxLimitRegion, $chunkX * 4, 0, $chunkZ * 4, $biomesForGeneration);

		$verticalSegments = intdiv(self::MAX_HEIGHT, 8);

		for($i = 0; $i < 4; $i++){
			$j = $i * 5;
			$k = ($i + 1) * 5;

			for($l = 0; $l < 4; $l++){
				$i1 = ($j + $l) * $verticalPoints;
				$j1 = ($j + $l + 1) * $verticalPoints;
				$k1 = ($k + $l) * $verticalPoints;
				$l1 = ($k + $l + 1) * $verticalPoints;

				for($i2 = 0; $i2 < $verticalSegments; $i2++){
					$d0 = 0.125;
					$d1 = $heightMap[$i1 + $i2];
					$d2 = $heightMap[$j1 + $i2];
					$d3 = $heightMap[$k1 + $i2];
					$d4 = $heightMap[$l1 + $i2];

					$d5 = ($heightMap[$i1 + $i2 + 1] - $d1) * $d0;
					$d6 = ($heightMap[$j1 + $i2 + 1] - $d2) * $d0;
					$d7 = ($heightMap[$k1 + $i2 + 1] - $d3) * $d0;
					$d8 = ($heightMap[$l1 + $i2 + 1] - $d4) * $d0;

					for($j2 = 0; $j2 < 8; $j2++){
						$d9 = 0.25;
						$d10 = $d1;
						$d11 = $d2;
						$d12 = ($d3 - $d1) * $d9;
						$d13 = ($d4 - $d2) * $d9;

						for($k2 = 0; $k2 < 4; $k2++){
							$d14 = 0.25;
							$d16 = ($d11 - $d10) * $d14;
							$lvt45 = $d10 - $d16;

							for($l2 = 0; $l2 < 4; $l2++){
								$lvt45 += $d16;

								$y = $i2 * 8 + $j2;
								if($y >= 128){
									continue;
								}

								if($lvt45 > 0.0){
									$chunk->setBlockId($i * 4 + $k2, $y, $l * 4 + $l2, Block::STONE);
								}elseif($y < self::SEA_LEVEL){
									$chunk->setBlockId($i * 4 + $k2, $y, $l * 4 + $l2, Block::STILL_WATER);
								}
							}
							$d10 += $d12;
							$d11 += $d13;
						}

						$d1 += $d5;
						$d2 += $d6;
						$d3 += $d7;
						$d4 += $d8;
					}
				}
			}
		}
	}

	private function replaceBiomeBlocks($x, $z, FullChunk $c, JavaRandom $rnd){
		$chunkX = $x * 16;
		$chunkZ = $z * 16;

		$biomeIDs = $this->biomeGen->getInts($chunkX, $chunkZ, 16, 16);

		$noiseArray = [];
		$noiseArray = $this->surfaceNoise->getRegion($noiseArray, $chunkX, $chunkZ, 16, 16, 0.125, 0.125, 1.0);

		$biomeCache = [];
		$colorCache = [];
		for($i = 0; $i < 16; $i++){
			for($j = 0; $j < 16; $j++){
				$idx = $j * 16 + $i;
				$biomeID = $biomeIDs[$idx] & 0xFF;

				if(!isset($biomeCache[$biomeID])){
					$biomeCache[$biomeID] = BiomeRegistry::getBiome($biomeID);
					$colorCache[$biomeID] = $biomeCache[$biomeID]->getColor();
				}
				$b = $biomeCache[$biomeID];

				$nv = $noiseArray[$idx];

				$gx = $x * 16 + $i;
				$gz = $z * 16 + $j;

				$b->genTerrainBlocks($c, $rnd, $gx, $gz, $nv);

				$biomeColor = $colorCache[$biomeID];
				$c->setBiomeColor($i, $j, ($biomeColor >> 16) & 0xFF, ($biomeColor >> 8) & 0xFF, $biomeColor & 0xFF);
				$c->setBiomeId($i, $j, $b->getID());
			}
		}
	}

	public function generateChunk($chunkX, $chunkZ){
		$chunk = $this->level->getChunk($chunkX, $chunkZ);
		if($chunk === null){
			return;
		}

		GenLayer::newEpoch();

		// MCPE loadChunk 使用 32 位区块种子，喂给 MT19937；不是 Java 的 64 位 LCG。
		$seed = (($chunkX * 0x14609048) + ($chunkZ * 0x7EBE2D5)) & 0xFFFFFFFF;
		$rnd = new McpeRandom($seed);

		$this->setBlocksInChunk($chunkX, $chunkZ, $chunk);
		$this->replaceBiomeBlocks($chunkX, $chunkZ, $chunk, $rnd);
		$this->caves->generateChunk($chunkX, $chunkZ, $chunk);
		// MCPE 0.14.3 没有独立的峡谷特征；保留 MapGenRavine 类但默认不调用。

		$this->mineshaft->prepare($this->world, $chunkX, $chunkZ, $chunk);
		$this->scatteredFeature->prepare($this->world, $chunkX, $chunkZ, $chunk);
		$this->village->prepare($this->world, $chunkX, $chunkZ, $chunk);
		if($this->stronghold !== null){
			$this->stronghold->prepare($this->world, $chunkX, $chunkZ, $chunk);
		}
	}

	public function populateChunk($chunkX, $chunkZ){
		GenLayer::newEpoch();

		$x = $chunkX * 16;
		$z = $chunkZ * 16;

		$ints = $this->biomeGen->getInts($x + 8, $z + 8, 1, 1);
		$biomeID = $ints[0] & 0xFF;
		$b = BiomeRegistry::getBiome($biomeID);

		// MCPE postProcess：用 MT 从世界种子取两个值，再按 32 位区块种子重置。
		$seedRnd = new McpeRandom($this->seed);
		$k = (($seedRnd->nextInt() >> 2) << 1) + 1;
		$l = (($seedRnd->nextInt() >> 2) << 1) + 1;
		$seed = (($this->seed & 0xFFFFFFFF) ^ ((($chunkX * $k + $chunkZ * $l) & 0xFFFFFFFF))) & 0xFFFFFFFF;
		$rnd = new McpeRandom($seed);

		$manager = new ChunkManagerAdapter($this->level);

		$isOceanOrRiver = $biomeID === 0 || $biomeID === 10 || $biomeID === 24 || $biomeID === 7 || $biomeID === 11;

		/*
		 * MCPE 0.14.3 RandomLevelSource::postProcess:
		 * 湖泊 -> Village -> Mineshaft -> Stronghold -> Scattered -> 地牢 -> 结冰 -> decorate。
		 */
		if(!$isOceanOrRiver){
			$this->generateLakes($manager, $rnd, $x, $z, $b->getID());
		}

		// MCPE 在湖泊之后、结构之前重新播种同一个区块 MT。
		$rnd->setSeed($seed);

		$this->village->generateStructure($this->world, $rnd, $chunkX, $chunkZ);
		$this->mineshaft->generateStructure($this->world, $rnd, $chunkX, $chunkZ);
		if($this->stronghold !== null){
			$this->stronghold->generateStructure($this->world, $rnd, $chunkX, $chunkZ);
		}
		$this->scatteredFeature->generateStructure($this->world, $rnd, $chunkX, $chunkZ);

		for($j2 = 0; $j2 < 8; $j2++){
			$i3 = $rnd->nextBoundedInt(16) + 8;
			$l3 = $rnd->nextBoundedInt(128);
			$l1 = $rnd->nextBoundedInt(16) + 8;
			$dungeon = new Dungeon();
			$dungeon->generate($manager, $rnd, new BlockPos($x + $i3, $l3, $z + $l1));
		}

		$this->freezeChunk($manager, $chunkX, $chunkZ);

		// MCPE 在 decorate 前把随机数重置到区块种子。
		$rnd->setSeed($seed);
		$b->decorate($manager, $rnd, new BlockPos($x, 0, $z));
	}

	/**
	 * MCPE 0.14.3 湖泊分支：
	 * - 沙漠/沙漠丘陵：1/8 概率尝试岩浆湖
	 * - 其他群系：1/4 概率直接尝试水湖，3/4 概率再以 1/8 尝试岩浆湖
	 * 水湖 y=rand&0x7f；岩浆湖 y=rand%(rand%120+8)，且需 y>59。
	 */
	private function generateLakes(ChunkManagerAdapter $manager, JavaRandom $rnd, $x, $z, $biomeID){
		$isDesert = $biomeID === 2 || $biomeID === 17;

		if($isDesert){
			if($rnd->nextBoundedInt(8) !== 0){
				return;
			}
			$this->tryLavaLake($manager, $rnd, $x, $z);
			return;
		}

		if($rnd->nextBoundedInt(4) === 0){
			$i = $rnd->nextBoundedInt(9) + 3;
			$y = $rnd->nextBoundedInt(128);
			$k = $rnd->nextBoundedInt(9) + 3;

			$lake = new Lake(Block::STILL_WATER);
			$lake->generate($manager, $rnd, new BlockPos($x + $i, $y, $z + $k));
			return;
		}

		if($rnd->nextBoundedInt(8) === 0){
			$this->tryLavaLake($manager, $rnd, $x, $z);
		}
	}

	private function tryLavaLake(ChunkManagerAdapter $manager, JavaRandom $rnd, $x, $z){
		$i = $rnd->nextBoundedInt(9) + 3;
		$y = $rnd->nextBoundedInt($rnd->nextBoundedInt(120) + 8);
		$k = $rnd->nextBoundedInt(9) + 3;

		if($y <= 59){
			return;
		}
		if($y >= 64 && $rnd->nextBoundedInt(10) !== 0){
			return;
		}

		$lake = new Lake(Block::STILL_LAVA);
		$lake->generate($manager, $rnd, new BlockPos($x + $i, $y, $z + $k));
	}

	/**
	 * MCPE postProcess 末尾的 16x16 结冰：温度 < 0.15 的水面放冰。
	 */
	private function freezeChunk(ChunkManagerAdapter $manager, $chunkX, $chunkZ){
		$biomeIDs = $this->biomeGen->getInts($chunkX * 16, $chunkZ * 16, 16, 16);

		for($i = 0; $i < 16; $i++){
			for($j = 0; $j < 16; $j++){
				$biome = BiomeRegistry::getBiome($biomeIDs[$j * 16 + $i] & 0xFF);
				if($biome->temperature >= 0.15){
					continue;
				}

				$wx = $chunkX * 16 + $i;
				$wz = $chunkZ * 16 + $j;

				for($y = 127; $y > 0; $y--){
					$id = $manager->getBlockId($wx, $y, $wz);
					if($id === 0){
						continue;
					}

					if($id === Block::STILL_WATER || $id === Block::WATER){
						$manager->setBlock($wx, $y, $wz, Block::ICE, 0);
					}
					break;
				}
			}
		}
	}

	private static function clampedLerp($lowerB, $upperB, $t){
		if($t < 0.0){
			return $lowerB;
		}elseif($t > 1.0){
			return $upperB;
		}
		return $lowerB + ($upperB - $lowerB) * $t;
	}
}
