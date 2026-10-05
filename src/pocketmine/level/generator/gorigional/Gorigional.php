<?php

namespace pocketmine\level\generator\gorigional;

use pocketmine\block\Block;
use pocketmine\level\ChunkManager;
use pocketmine\level\format\FullChunk;
use pocketmine\level\generator\Generator;
use pocketmine\level\generator\gorigional\biome\BiomeRegistry;
use pocketmine\level\generator\gorigional\layer\LayerFactory;
use pocketmine\level\generator\gorigional\noise\OctavesNoise;
use pocketmine\level\generator\gorigional\noise\PerlinSimplexGenerator;
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
	const LOWER_LIMIT_SCALE = 512.0;
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

	/** @var \pocketmine\level\generator\gorigional\layer\GenLayer */
	private $genLayer;
	/** @var \pocketmine\level\generator\gorigional\layer\GenLayer */
	private $biomeGen;

	/** @var float[] */
	private $biomeWeights = [];

	public function __construct(array $settings = []){

	}

	public function init(ChunkManager $level, Random $random){
		$this->level = $level;
		$this->seed = $level->getSeed();

		$rnd = new JavaRandom($this->seed);

		$this->minLimitPerlinNoise = new OctavesNoise($rnd, 16);
		$this->maxLimitPerlinNoise = new OctavesNoise($rnd, 16);
		$this->mainPerlinNoise = new OctavesNoise($rnd, 8);
		$this->surfaceNoise = new PerlinSimplexGenerator($rnd, 4);
		$this->scaleNoise = new OctavesNoise($rnd, 10);
		$this->depthNoise = new OctavesNoise($rnd, 16);

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
				for($y = 255; $y >= 0; $y--){
					$blockId = $chunk->getBlockId(0, $y, 0);

					if($blockId === 0 || $blockId === 8 || $blockId === 9 || $blockId === 10 || $blockId === 11 || $blockId === 18 || $blockId === 161){
						continue;
					}

					$hasSpace = true;
					for($check = 1; $check <= 2; $check++){
						if($chunk->getBlockId(0, $y + $check, 0) !== 0){
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

		for($k = 0; $k < $horizontalPoints; $k++){
			for($l = 0; $l < $horizontalPoints; $l++){
				$f2 = 0.0;
				$f3 = 0.0;
				$f4 = 0.0;

				$centerBiomeID = $biomesForGeneration[$k + 2 + ($l + 2) * 10];
				$centerBiome = BiomeRegistry::getBiome($centerBiomeID);
				$centerBaseHeight = $centerBiome->getMinElevation();

				for($j1 = -2; $j1 <= 2; $j1++){
					for($k1 = -2; $k1 <= 2; $k1++){
						$biomeID = $biomesForGeneration[$k + $j1 + 2 + ($l + $k1 + 2) * 10];
						$neighborBiome = BiomeRegistry::getBiome($biomeID);
						$baseHeight = $neighborBiome->getMinElevation();
						$heightVar = $neighborBiome->getMaxElevation();

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
					$d1 = ($l1 - $d0) * self::STRETCH_Y * 128.0 / 256.0 / $d9;

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
		$noiseArray = $this->surfaceNoise->getRegion($noiseArray, $chunkX, $chunkZ, 16, 16, 0.0625, 0.0625, 1.0);

		for($i = 0; $i < 16; $i++){
			for($j = 0; $j < 16; $j++){
				$idx = $j * 16 + $i;
				$biomeID = $biomeIDs[$idx] & 0xFF;

				$b = BiomeRegistry::getBiome($biomeID);

				$nv = $noiseArray[$idx];

				$gx = $x * 16 + $i;
				$gz = $z * 16 + $j;

				$b->genTerrainBlocks($c, $rnd, $gx, $gz, $nv);

				$biomeColor = $b->getColor();
				$c->setBiomeColor($i, $j, ($biomeColor >> 16) & 0xFF, ($biomeColor >> 8) & 0xFF, $biomeColor & 0xFF);
				$c->setBiomeId($i, $j, $biomeID);
			}
		}
	}

	public function generateChunk($chunkX, $chunkZ){
		$chunk = $this->level->getChunk($chunkX, $chunkZ);
		if($chunk === null){
			return;
		}

		$seed = Int64::add(Int64::mul($chunkX, 341873128712), Int64::mul($chunkZ, 132897987541));
		$rnd = new JavaRandom($seed);

		$this->setBlocksInChunk($chunkX, $chunkZ, $chunk);
		$this->replaceBiomeBlocks($chunkX, $chunkZ, $chunk, $rnd);
	}

	public function populateChunk($chunkX, $chunkZ){

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
