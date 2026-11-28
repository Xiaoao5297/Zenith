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
 * it under the terms of the GNU General Public License as published by
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
use pocketmine\level\generator\gorigional\biome\Biome;
use pocketmine\level\generator\gorigional\biome\BiomeRegistry;
use pocketmine\level\generator\gorigional\Int64;
use pocketmine\level\generator\gorigional\layer\GenLayer;
use pocketmine\level\generator\gorigional\layer\LayerFactory;
use pocketmine\level\generator\gorigional\noise\OctavesNoise;
use pocketmine\level\generator\gorigional\noise\PerlinSimplexGenerator;
use pocketmine\level\generator\gorigional\object\BlockPos;
use pocketmine\level\generator\gorigional\object\ChunkManagerAdapter;
use pocketmine\level\generator\gorigional\object\Decorator;
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
 * Java 1.7.10 风格 ChunkGeneratorOverworld，移植自 SCAXE-GO-CE pkg/level/generator/gorigional/generator.go。
 *
 * 高度场按 `docs/remapped_server_1.7.10` 的 `ChunkProviderGenerate.func_147423_a` 对齐；
 * 0.14 客户端的 128 格上限用"截断"处理（见 STRETCH_Y 处的注释），不做垂直压缩。
 */
class Gorigional extends Generator{
	const NAME = "gorigional";

	/**
	 * Java 1.7.10 ChunkProviderGenerate 的常量（`func_147423_a`）。
	 * 世界只有 128 高，所以高度场取 Java 的 y=0..127 截断：
	 * 噪声列用 17 个点（每 8 格一个），与 Java 的 33 个点在 y<128 上逐点相同。
	 */
	const COORDINATE_SCALE = 684.412;
	const HEIGHT_SCALE = 684.412;
	// Java 1.7.10 ChunkProviderGenerate 的原始常量。
	const LOWER_LIMIT_SCALE = 512.0;
	const UPPER_LIMIT_SCALE = 512.0;
	const DEPTH_NOISE_SCALE_X = 200.0;
	const DEPTH_NOISE_SCALE_Z = 200.0;
	const MAIN_NOISE_SCALE_X = 80.0;
	const MAIN_NOISE_SCALE_Y = 160.0;
	const MAIN_NOISE_SCALE_Z = 80.0;
	const BASE_SIZE = 8.5;
	// Java: (j2 - d8) * 12.0 * 128.0 / 256.0，128 高世界取 6.0。
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
	/** @var bool 是否输出地形生成阶段耗时 */
	private $debugTiming = false;
	/** @var bool 使用 info 级别输出（否则走 debug 级别） */
	private $debugTimingInfo = false;
	/** @var \pocketmine\utils\MainLogger|null */
	private $debugLogger = null;

	public function __construct(array $settings = []){

	}

	public function init(ChunkManager $level, Random $random){
		$initStart = microtime(true);
		$initMemory = memory_get_usage(false);
		$this->level = $level;
		$this->seed = $level->getSeed();

		if($level instanceof Level){
			$this->debugLogger = $level->getServer()->getLogger();
			$this->debugTiming = (bool) $level->getServer()->getProperty("debug.generator-timing", false);
			$this->debugTimingInfo = $this->debugTiming;
			if(!$this->debugTiming && defined("pocketmine\\DEBUG") && \pocketmine\DEBUG > 1){
				$this->debugTiming = true;
			}
		}

		// Java 1.7.10 的七个地形噪声共用同一个 java.util.Random(seed)。
		$rnd = new JavaRandom($this->seed);

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
		Decorator::setTimingCallback(function($stage, $startTime, $startMemory, $chunkX, $chunkZ){
			$this->logTiming($chunkX, $chunkZ, $stage, $startTime, $startMemory);
		});
		$this->mineshaft = new MapGenMineshaft($this->seed);
		$this->scatteredFeature = new MapGenScatteredFeature($this->seed);
		$this->village = new MapGenVillage($this->seed);

		// Java 1.7.10 默认生成 3 个要塞；可用 world.generate-stronghold 关闭。
		$generateStronghold = true;
		if($level instanceof Level){
			$value = $level->getServer()->getAdvancedProperty("world.generate-stronghold", true);
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
			$this->logTiming(null, null, "init", $initStart, $initMemory);
		}
	}

	/**
	 * 输出单个地形阶段的耗时与内存信息。
	 * $chunkX/$chunkZ 为 null 时用于 init 等全局阶段。
	 */
	private function logTiming($chunkX, $chunkZ, $stage, $startTime, $startMemory){
		if(!$this->debugTiming || $this->debugLogger === null || $startTime === null){
			return;
		}
		$ms = (microtime(true) - $startTime) * 1000.0;
		$memDiff = (memory_get_usage(false) - $startMemory) / 1048576.0;
		$peak = memory_get_peak_usage(false) / 1048576.0;
		$where = $chunkX === null ? "global" : "chunk(" . $chunkX . "," . $chunkZ . ")";
		$message = sprintf(
			"[Gorigional] %s %-22s %8.3f ms  mem %+6.2f MB (peak %.2f MB)",
			$where, $stage, $ms, $memDiff, $peak
		);
		if($this->debugTimingInfo){
			$this->debugLogger->info($message);
		}else{
			$this->debugLogger->debug($message);
		}
	}

	/**
	 * 供 PopulationTask 等外部调用者记录阶段耗时；未开启计时时直接返回。
	 */
	public function logExternalTiming($chunkX, $chunkZ, $stage, $startTime, $startMemory){
		$this->logTiming($chunkX, $chunkZ, $stage, $startTime, $startMemory);
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

	/**
	 * 由 GeneratorRegisterTask 在主线程把日志器传给异步 worker。
	 * worker 里的 init() 拿到的是 SimpleChunkManager，无法读取 Server 配置。
	 *
	 * @param \ThreadedLogger $logger
	 */
	public function setDebugLogger($logger){
		$this->debugLogger = $logger;
		$this->debugTiming = true;
		$this->debugTimingInfo = true;
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
		$minLimitRegion = $this->minLimitPerlinNoise->generateNoiseOctaves($minLimitRegion, $x, $y, $z, $horizontalPoints, $verticalPoints, $horizontalPoints, $f, $f1, $f);
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
					// Java: (j2 - d8) * 12 * 128 / 256 / d9。128 高的世界取 y=0..127 这一段（截断，不压缩）。
					$d1 = ($l1 - $d0) * self::STRETCH_Y * self::MAX_HEIGHT / 256.0 / $d9;

					if($d1 < 0.0){
						$d1 *= 4.0;
					}

					$minVal = $minLimitRegion[$i] / self::LOWER_LIMIT_SCALE;
					$maxVal = $maxLimitRegion[$i] / self::UPPER_LIMIT_SCALE;
					$mainVal = ($mainNoiseRegion[$i] / 10.0 + 1.0) / 2.0;

					$d5 = self::clampedLerp($minVal, $maxVal, $mainVal) - $d1;

					$heightMap[$i] = $d5;
					$i++;
				}
			}
		}
	}

	private function setBlocksInChunk($chunkX, $chunkZ, FullChunk $chunk){
		$stageStart = microtime(true);
		$stageMemory = memory_get_usage(false);
		$biomesForGeneration = $this->genLayer->getInts($chunkX * 4 - 2, $chunkZ * 4 - 2, 10, 10);
		$this->logTiming($chunkX, $chunkZ, "biome-sample", $stageStart, $stageMemory);

		$verticalPoints = intdiv(self::MAX_HEIGHT, 8) + 1;
		$horizontalPoints = 5;
		$bufferSize = $horizontalPoints * $horizontalPoints * $verticalPoints;

		$heightMap = array_fill(0, $bufferSize, 0.0);
		$depthRegion = [];
		$mainNoiseRegion = [];
		$minLimitRegion = [];
		$maxLimitRegion = [];

		$stageStart = microtime(true);
		$stageMemory = memory_get_usage(false);
		$this->generateHeightmap($heightMap, $depthRegion, $mainNoiseRegion, $minLimitRegion, $maxLimitRegion, $chunkX * 4, 0, $chunkZ * 4, $biomesForGeneration);
		$this->logTiming($chunkX, $chunkZ, "heightmap/noise", $stageStart, $stageMemory);

		$stageStart = microtime(true);
		$stageMemory = memory_get_usage(false);
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
		$this->logTiming($chunkX, $chunkZ, "density/fill", $stageStart, $stageMemory);
	}

	private function replaceBiomeBlocks($x, $z, FullChunk $c, JavaRandom $rnd){
		$stageStart = microtime(true);
		$stageMemory = memory_get_usage(false);
		$chunkX = $x * 16;
		$chunkZ = $z * 16;

		$biomeIDs = $this->biomeGen->getInts($chunkX, $chunkZ, 16, 16);
		$this->world->getBiomeManager()->primeBiomeCache($x, $z, $biomeIDs);

		$noiseArray = [];
		// Java func_147422_a: d0 = 0.03125，缩放取 d0 * 2 = 0.0625。
		$noiseArray = $this->surfaceNoise->getRegion($noiseArray, $chunkX, $chunkZ, 16, 16, 0.0625, 0.0625, 1.0);

		$biomeCache = [];
		$colorCache = [];
		for($i = 0; $i < 16; $i++){
			for($j = 0; $j < 16; $j++){
				$idx = $j * 16 + $i;
				$biomeID = $biomeIDs[($j + 1) * 18 + ($i + 1)] & 0xFF;

				if(!isset($biomeCache[$biomeID])){
					$biomeCache[$biomeID] = BiomeRegistry::getBiome($biomeID);
					$colorCache[$biomeID] = $biomeCache[$biomeID]->getColor();
				}
				$b = $biomeCache[$biomeID];

				$nv = $noiseArray[$idx];

				$gx = $x * 16 + $i;
				$gz = $z * 16 + $j;

				$b->genTerrainBlocks($c, $rnd, $gx, $gz, $nv);

				// Java 1.7.10 BlockGrass.colorMultiplier：周围 3x3 群系颜色取平均，
				// 客户端再按顶点插值，群系交界处才会渐变而不是硬切换。
				$neighborColors = [];
				for($di = -1; $di <= 1; $di++){
					for($dj = -1; $dj <= 1; $dj++){
						$neighborID = $biomeIDs[($j + 1 + $dj) * 18 + ($i + 1 + $di)] & 0xFF;
						if(!isset($colorCache[$neighborID])){
							$biomeCache[$neighborID] = BiomeRegistry::getBiome($neighborID);
							$colorCache[$neighborID] = $biomeCache[$neighborID]->getColor();
						}
						$neighborColors[] = $colorCache[$neighborID];
					}
				}
				$biomeColor = self::blendBiomeColors($neighborColors);
				$c->setBiomeColor($i, $j, ($biomeColor >> 16) & 0xFF, ($biomeColor >> 8) & 0xFF, $biomeColor & 0xFF);
				$c->setBiomeId($i, $j, $b->getID());
			}
		}
		$this->logTiming($x, $z, "surface/biomes", $stageStart, $stageMemory);
	}

	/**
	 * Java 1.7.10 BlockGrass.colorMultiplier 的颜色平均：R/G/B 分别取算术平均。
	 *
	 * @param int[] $colors 0xRRGGBB 列表（通常是 3x3 的 9 个群系颜色）
	 * @return int
	 */
	public static function blendBiomeColors(array $colors) : int{
		$count = count($colors);
		if($count === 0){
			return 0;
		}

		$r = 0;
		$g = 0;
		$b = 0;
		foreach($colors as $color){
			$r += ($color >> 16) & 0xFF;
			$g += ($color >> 8) & 0xFF;
			$b += $color & 0xFF;
		}

		return ((intdiv($r, $count) & 0xFF) << 16) | ((intdiv($g, $count) & 0xFF) << 8) | (intdiv($b, $count) & 0xFF);
	}

	/**
	 * Java 1.7.10 ChunkProviderGenerate.func_147424_a 的区块随机种子：
	 * 只由区块坐标决定，不含世界种子。
	 */
	public static function chunkSeed($chunkX, $chunkZ){
		return Int64::add(Int64::mul($chunkX, 341873128712), Int64::mul($chunkZ, 132897987541));
	}

	/**
	 * Java 1.7.10 ChunkProviderGenerate.func_73153_a 的 populate 种子：
	 * worldSeed 下取两个奇数乘子，再与区块坐标线性组合后异或世界种子。
	 */
	public static function populateSeed($chunkX, $chunkZ, $worldSeed){
		$seedRnd = new JavaRandom($worldSeed);
		$k = intdiv($seedRnd->nextLong(), 2) * 2 + 1;
		$l = intdiv($seedRnd->nextLong(), 2) * 2 + 1;

		return Int64::add(Int64::mul($chunkX, $k), Int64::mul($chunkZ, $l)) ^ $worldSeed;
	}

	public function generateChunk($chunkX, $chunkZ){
		$chunkStart = microtime(true);
		$chunkMemory = memory_get_usage(false);
		$chunk = $this->level->getChunk($chunkX, $chunkZ);
		if($chunk === null){
			return;
		}

		GenLayer::newEpoch();

		// Java func_73154_d: rand.setSeed(x * 341873128712 + z * 132897987541)
		$seed = Int64::add(Int64::mul($chunkX, 341873128712), Int64::mul($chunkZ, 132897987541));
		$rnd = new JavaRandom($seed);

		$stageStart = microtime(true); $stageMemory = memory_get_usage(false);
		$this->setBlocksInChunk($chunkX, $chunkZ, $chunk);
		$this->logTiming($chunkX, $chunkZ, "terrain", $stageStart, $stageMemory);

		$stageStart = microtime(true); $stageMemory = memory_get_usage(false);
		$this->replaceBiomeBlocks($chunkX, $chunkZ, $chunk, $rnd);
		$this->logTiming($chunkX, $chunkZ, "biome-surface", $stageStart, $stageMemory);

		$stageStart = microtime(true); $stageMemory = memory_get_usage(false);
		$this->caves->generateChunk($chunkX, $chunkZ, $chunk);
		$this->logTiming($chunkX, $chunkZ, "caves", $stageStart, $stageMemory);

		$stageStart = microtime(true); $stageMemory = memory_get_usage(false);
		$this->ravines->generateChunk($chunkX, $chunkZ, $chunk);
		$this->logTiming($chunkX, $chunkZ, "ravines", $stageStart, $stageMemory);

		$stageStart = microtime(true); $stageMemory = memory_get_usage(false);
		$this->mineshaft->prepare($this->world, $chunkX, $chunkZ, $chunk);
		$this->logTiming($chunkX, $chunkZ, "struct-mineshaft", $stageStart, $stageMemory);

		$stageStart = microtime(true); $stageMemory = memory_get_usage(false);
		$this->village->prepare($this->world, $chunkX, $chunkZ, $chunk);
		$this->logTiming($chunkX, $chunkZ, "struct-village", $stageStart, $stageMemory);

		if($this->stronghold !== null){
			$stageStart = microtime(true); $stageMemory = memory_get_usage(false);
			$this->stronghold->prepare($this->world, $chunkX, $chunkZ, $chunk);
			$this->logTiming($chunkX, $chunkZ, "struct-stronghold", $stageStart, $stageMemory);
		}

		$stageStart = microtime(true); $stageMemory = memory_get_usage(false);
		$this->scatteredFeature->prepare($this->world, $chunkX, $chunkZ, $chunk);
		$this->logTiming($chunkX, $chunkZ, "struct-scattered", $stageStart, $stageMemory);

		$this->logTiming($chunkX, $chunkZ, "generate TOTAL", $chunkStart, $chunkMemory);
	}

	public function populateChunk($chunkX, $chunkZ){
		$populateStart = microtime(true);
		$populateMemory = memory_get_usage(false);
		$biomeStart = microtime(true);
		$biomeMemory = memory_get_usage(false);
		GenLayer::newEpoch();

		$x = $chunkX * 16;
		$z = $chunkZ * 16;

		// Java func_73153_a 取的是区块东南角（+16）的群系。
		// 人口任务已经加载/生成 3×3 邻居，优先直接读东南角邻居的 (0,0)；
		// 邻居缺失时才回退到 replaceBiomeBlocks() 预填的 WorldChunkManager 缓存。
		$neighbor = $this->level->getChunk($chunkX + 1, $chunkZ + 1);
		if($neighbor !== null && $neighbor->isGenerated()){
			$biomeID = $neighbor->getBiomeId(0, 0);
		}else{
			$biomeID = $this->world->getBiome($x + 16, $z + 16);
		}
		$b = BiomeRegistry::getBiome($biomeID);
		$this->logTiming($chunkX, $chunkZ, "populate-biome", $biomeStart, $biomeMemory);

		// Java: i1 = nextLong() / 2 * 2 + 1, j1 同理；
		//       setSeed(chunkX * i1 + chunkZ * j1 ^ worldSeed)
		$rnd = new JavaRandom($this->seed);
		$k = intdiv($rnd->nextLong(), 2) * 2 + 1;
		$l = intdiv($rnd->nextLong(), 2) * 2 + 1;
		$seed = Int64::add(Int64::mul($chunkX, $k), Int64::mul($chunkZ, $l)) ^ $this->seed;
		$rnd->setSeed($seed);

		$manager = new ChunkManagerAdapter($this->level);

		$stageStart = microtime(true); $stageMemory = memory_get_usage(false);
		$flag = false;
		$this->mineshaft->generateStructure($this->world, $rnd, $chunkX, $chunkZ);
		$flag = $this->village->generateStructure($this->world, $rnd, $chunkX, $chunkZ);
		if($this->stronghold !== null){
			$this->stronghold->generateStructure($this->world, $rnd, $chunkX, $chunkZ);
		}
		$this->scatteredFeature->generateStructure($this->world, $rnd, $chunkX, $chunkZ);
		$this->logTiming($chunkX, $chunkZ, "struct-populate", $stageStart, $stageMemory);

		$isDesert = $biomeID === 2 || $biomeID === 17;

		// Java WorldGenLakes（水）
		$stageStart = microtime(true); $stageMemory = memory_get_usage(false);
		if(!$isDesert && !$flag && $rnd->nextBoundedInt(4) === 0){
			$lx = $x + $rnd->nextBoundedInt(16) + 8;
			$ly = $rnd->nextBoundedInt(256);
			$lz = $z + $rnd->nextBoundedInt(16) + 8;

			$lake = new Lake(Block::STILL_WATER);
			$lake->generate($manager, $rnd, new BlockPos($lx, $ly, $lz));
		}
		$this->logTiming($chunkX, $chunkZ, "lake-water", $stageStart, $stageMemory);

		// Java WorldGenLakes（岩浆）
		$stageStart = microtime(true); $stageMemory = memory_get_usage(false);
		if(!$flag && $rnd->nextBoundedInt(8) === 0){
			$lx = $x + $rnd->nextBoundedInt(16) + 8;
			$ly = $rnd->nextBoundedInt($rnd->nextBoundedInt(248) + 8);
			$lz = $z + $rnd->nextBoundedInt(16) + 8;

			if($ly < 63 || $rnd->nextBoundedInt(10) === 0){
				$lake = new Lake(Block::STILL_LAVA);
				$lake->generate($manager, $rnd, new BlockPos($lx, $ly, $lz));
			}
		}
		$this->logTiming($chunkX, $chunkZ, "lake-lava", $stageStart, $stageMemory);

		// Java WorldGenDungeons：8 次，y = nextInt(256)。
		$stageStart = microtime(true); $stageMemory = memory_get_usage(false);
		for($j2 = 0; $j2 < 8; $j2++){
			$i3 = $x + $rnd->nextBoundedInt(16) + 8;
			$l3 = $rnd->nextBoundedInt(256);
			$l1 = $z + $rnd->nextBoundedInt(16) + 8;
			$dungeon = new Dungeon();
			$dungeon->generate($this->world, $rnd, new BlockPos($i3, $l3, $l1));
		}
		$this->logTiming($chunkX, $chunkZ, "dungeons x8", $stageStart, $stageMemory);

		$stageStart = microtime(true); $stageMemory = memory_get_usage(false);
		$b->decorate($manager, $rnd, new BlockPos($x, 0, $z));
		$this->logTiming($chunkX, $chunkZ, "decorate", $stageStart, $stageMemory);

		$stageStart = microtime(true); $stageMemory = memory_get_usage(false);
		$this->freezeChunk($manager, $chunkX, $chunkZ);
		$this->logTiming($chunkX, $chunkZ, "freeze/snow", $stageStart, $stageMemory);

		$this->logTiming($chunkX, $chunkZ, "populate TOTAL", $populateStart, $populateMemory);
	}

	/**
	 * Java ChunkProviderGenerate.func_73153_a 末尾的 16×16 结冰/积雪。
	 * 区域是 x+8..x+23、z+8..z+23，而不是本区块 0..15。
	 */
	private function freezeChunk(ChunkManagerAdapter $manager, $chunkX, $chunkZ){
		$x0 = $chunkX * 16 + 8;
		$z0 = $chunkZ * 16 + 8;

		for($i = 0; $i < 16; $i++){
			for($j = 0; $j < 16; $j++){
				$wx = $x0 + $i;
				$wz = $z0 + $j;
				$biome = BiomeRegistry::getBiome($this->world->getBiome($wx, $wz));
				// 128 高世界里，温度 > 0.3 的群系即使叠加高度降温和噪声也不可能低于 0.15；
				// 直接跳过，避免每列都做高度扫描和两次 Perlin 温度计算。
				if($biome->getTemperatureAt($wx, 64, $wz) > 0.3){
					continue;
				}
				$top = $manager->getHeight($wx, $wz);

				if($this->canBlockFreeze($manager, $wx, $top, $wz, $biome)){
					$manager->setBlock($wx, $top, $wz, Block::ICE, 0);
				}
				if($this->canSnowAt($manager, $wx, $top + 1, $wz, $biome)){
					$manager->setBlock($wx, $top + 1, $wz, Block::SNOW_LAYER, 0);
				}
			}
		}
	}

	/**
	 * Java World.func_72884_u / func_72834_c(..., false)。
	 */
	private function canBlockFreeze(ChunkManagerAdapter $manager, $x, $y, $z, Biome $biome = null){
		if($y < 0 || $y >= 128){
			return false;
		}
		if($biome === null){
			$biome = BiomeRegistry::getBiome($this->world->getBiome($x, $z));
		}
		if($biome->getTemperatureAt($x, $y, $z) > 0.15){
			return false;
		}

		$chunk = $manager->getChunkAt($x, $z);
		if($chunk === null || $chunk->getBlockLight($x & 15, $y, $z & 15) >= 10){
			return false;
		}

		$id = $chunk->getBlockId($x & 15, $y, $z & 15);
		return ($id === Block::STILL_WATER || $id === Block::WATER)
			&& $chunk->getBlockData($x & 15, $y, $z & 15) === 0;
	}

	/**
	 * Java World.func_147478_e(..., true)。
	 */
	private function canSnowAt(ChunkManagerAdapter $manager, $x, $y, $z, Biome $biome = null){
		if($y < 0 || $y >= 128){
			return false;
		}
		if($biome === null){
			$biome = BiomeRegistry::getBiome($this->world->getBiome($x, $z));
		}
		if($biome->getTemperatureAt($x, $y, $z) > 0.15){
			return false;
		}

		$chunk = $manager->getChunkAt($x, $z);
		if($chunk === null || $chunk->getBlockLight($x & 15, $y, $z & 15) >= 10){
			return false;
		}
		if($chunk->getBlockId($x & 15, $y, $z & 15) !== Block::AIR){
			return false;
		}

		$below = $y > 0 ? $chunk->getBlockId($x & 15, $y - 1, $z & 15) : 0;
		return $below !== Block::AIR && Block::get($below)->isSolid();
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
