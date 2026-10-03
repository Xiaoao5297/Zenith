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

namespace lycore\level\generator\normal;

use lycore\block\Block;
use lycore\block\CoalOre;
use lycore\block\DiamondOre;
use lycore\block\Dirt;
use lycore\block\GoldOre;
use lycore\block\Gravel;
use lycore\block\IronOre;
use lycore\block\LapisOre;
use lycore\block\RedstoneOre;
use lycore\block\Stone;
use lycore\level\ChunkManager;
use lycore\level\generator\biome\Biome;
use lycore\level\generator\biome\BiomeSelector;
use lycore\level\generator\Generator;
use lycore\level\generator\noise\Perlin;
use lycore\level\generator\noise\Simplex;
use lycore\level\generator\normal\biome\NormalBiome;
use lycore\level\generator\normal\populator\DesertStructures;
use lycore\level\generator\normal\populator\JungleTemple;
use lycore\level\generator\normal\populator\PillagerOutpost;
use lycore\level\generator\normal\populator\RuinedPortal;
use lycore\level\generator\normal\populator\Stronghold;
use lycore\level\generator\normal\populator\WoodlandMansion;
use lycore\level\generator\object\OreType;
use lycore\level\generator\populator\GroundCover;
use lycore\level\generator\populator\Cave;
use lycore\level\generator\populator\Dungeon;
use lycore\level\generator\populator\Mineshaft;
use lycore\level\generator\populator\Ore;
use lycore\level\generator\populator\Pond;
use lycore\level\generator\populator\Populator;
use lycore\level\generator\populator\TallGrass;
use lycore\level\generator\populator\Tree;
use lycore\level\Level;
use lycore\math\Vector3 as Vector3;
use lycore\utils\Random;

class Normal extends Generator{
	const NAME = "Normal";

	/** @var Populator[] */
	private $populators = [];
	/** @var ChunkManager */
	private $level;
	/** @var Random */
	private $random;
	private $waterHeight = 50;
	private $bedrockDepth = 6;

	/** @var Populator[] */
	private $generationPopulators = [];
	/** @var Simplex */
	private $noiseBase;

	/** @var BiomeSelector */
	private $selector;

	private static $GAUSSIAN_KERNEL = null;
	private static $SMOOTH_SIZE = 4;

	public function __construct(array $options = []){
		if(self::$GAUSSIAN_KERNEL === null){
			self::generateKernel();
		}
	}

	private static function generateKernel(){
		self::$GAUSSIAN_KERNEL = [];

		$bellSize = 1 / self::$SMOOTH_SIZE;
		$bellHeight = 2 * self::$SMOOTH_SIZE;

		for($sx = -self::$SMOOTH_SIZE; $sx <= self::$SMOOTH_SIZE; ++$sx){
			self::$GAUSSIAN_KERNEL[$sx + self::$SMOOTH_SIZE] = [];

			for($sz = -self::$SMOOTH_SIZE; $sz <= self::$SMOOTH_SIZE; ++$sz){
				$bx = $bellSize * $sx;
				$bz = $bellSize * $sz;
				self::$GAUSSIAN_KERNEL[$sx + self::$SMOOTH_SIZE][$sz + self::$SMOOTH_SIZE] = $bellHeight * exp(-($bx * $bx + $bz * $bz) / 2);
			}
		}
	}

	public function getName() : string{
		return self::NAME;
	}

	public function getWaterHeight() : int{
		return $this->waterHeight;
	}

	public function getSettings(){
		return [];
	}

	public function getTemperature($x, $z){
		$hash = $x * 2345803 ^ $z * 9236449 ^ $this->level->getSeed();
		$hash *= $hash + 223;
		$xNoise = $hash >> 20 & 3;
		$zNoise = $hash >> 22 & 3;
		if($xNoise == 3){
			$xNoise = 1;
		}
		if($zNoise == 3){
			$zNoise = 1;
		}

		return $this->selector->getTemperature($x + $xNoise - 1, $z + $zNoise - 1);
	}

	public function pickBiome($x, $z){
		$hash = $x * 2345803 ^ $z * 9236449 ^ $this->level->getSeed();
		$hash *= $hash + 223;
		$xNoise = $hash >> 20 & 3;
		$zNoise = $hash >> 22 & 3;
		if($xNoise == 3){
			$xNoise = 1;
		}
		if($zNoise == 3){
			$zNoise = 1;
		}

		return $this->selector->pickBiome($x + $xNoise - 1, $z + $zNoise - 1);
	}

	private static function pickRawBiomeId($temperature, $rainfall, $River, $ocean, $hills){
		if ($ocean < -0.15) {
			if ($ocean < -0.91) {
				if ($ocean < -0.92) {
					return Biome::MUSHROOM_ISLAND;
				}else{
					return Biome::MUSHROOM_ISLAND_SHORE;
				}
			}else{
				if($temperature < -0.4){
					return Biome::FROZEN_OCEAN;
				}else{
					if ($rainfall < 0) {
						return Biome::OCEAN;
					}else{
						return Biome::DEEP_OCEAN;
					}
				}
			}
		}elseif(abs($River) <= 0.02){
			if($temperature < -0.36){
				return Biome::FROZEN_RIVER;
			}else{
				return Biome::RIVER;
			}
		} elseif($ocean < -0.12) {
			if($temperature < -0.379){
				return Biome::COLD_BEACH;
			}else{
				return Biome::BEACH;
			}
		}else{
			return self::pickLandBiomeId($temperature, $rainfall, $hills);
		}
	}

	private static function pickLandBiomeId($temperature, $rainfall, $hills){
		if($temperature < -0.379){
			if ($rainfall < 0) {
				if($hills < -0.45){
					return Biome::COLD_TAIGA_HILLS;
				}else{
					return Biome::COLD_TAIGA;
				}
			}else{
				if($hills > 0.75){
					return Biome::ICE_MOUNTAINS;
				}else{
					return Biome::ICE_PLAINS;
				}
			}
		}elseif($temperature < 0){
			if($hills < -0.65){
				return Biome::MOUNTAINS;
			}elseif($hills > 0.65){
				return Biome::TAIGA_HILLS;
			}else{
				if ($rainfall < 0.6) {
					return Biome::TAIGA;
				}else{
					return Biome::TAIGA;
				}
			}
		}elseif($temperature < 0.5){
			if ($temperature < 0.25) {
				if ($rainfall < 0) {
					return Biome::PLAINS;
				}elseif($rainfall < 0.25){
					return Biome::FOREST;
				}elseif($rainfall < 0.35){
					return Biome::PINK_FOREST;
				}elseif($rainfall < 0.45){
					return Biome::GOLDEN_FOREST;
				}else{
					return Biome::BIRCH_FOREST;
				}
			}else{
				if ($rainfall < -0.3) {
					return Biome::SWAMP;
				}elseif($rainfall > 0){
					if($hills < 0){
						return Biome::JUNGLE;
					}else{
						return Biome::JUNGLE;
					}
				}else{
					return Biome::ROOFED_FOREST;
				}
			}
		}else{
			if ($rainfall < 0) {
				if($hills > 0.65){
					return Biome::DESERT_HILLS;
				}
				return Biome::DESERT;
			}elseif($rainfall > 0.4){
				return Biome::SAVANNA;
			}else{
				return Biome::MESA;
			}
		}
	}

	private static function postProcessBiomeId($biomeId, $temperature, $rainfall, $River, $ocean, $hills, $x, $z, BiomeSelector $selector){
		if(($biomeId === Biome::BEACH or $biomeId === Biome::COLD_BEACH) and !self::hasRawOceanBiomeNear($selector, $x, $z)){
			return self::pickLandBiomeId($temperature, $rainfall, $hills);
		}

		return $biomeId;
	}

	private static function hasRawOceanBiomeNear(BiomeSelector $selector, $x, $z){
		for($sx = -48; $sx <= 48; $sx += 16){
			for($sz = -48; $sz <= 48; $sz += 16){
				$biomeId = $selector->pickRawBiomeId($x + $sx, $z + $sz);
				if($biomeId === Biome::OCEAN or $biomeId === Biome::DEEP_OCEAN or $biomeId === Biome::FROZEN_OCEAN){
					return true;
				}
			}
		}

		return false;
	}

	public static function normalizeTerrainElevationRange($biomeId, $minSum, $maxSum) : array{
		switch((int) $biomeId){
			case Biome::DESERT:
				return self::normalizeLowReliefTerrainRange((float) $minSum, 63.0, 3.0, 67.0);
			case Biome::PLAINS:
				return self::normalizeLowReliefTerrainRange((float) $minSum, 61.0, 4.0, 66.0);
		}

		return [(float) $minSum, (float) $maxSum];
	}

	public static function normalizeWeightedTerrainElevationRange(array $samples) : array{
		$minSum = 0.0;
		$maxSum = 0.0;
		$weightSum = 0.0;

		foreach($samples as $sample){
			if(count($sample) < 4){
				continue;
			}

			$weight = (float) $sample[3];
			if($weight <= 0.0){
				continue;
			}

			list($min, $max) = self::normalizeTerrainElevationRange($sample[0], $sample[1], $sample[2]);
			$minSum += $min * $weight;
			$maxSum += $max * $weight;
			$weightSum += $weight;
		}

		if($weightSum <= 0.0){
			return [0.0, 1.0];
		}

		return [$minSum / $weightSum, $maxSum / $weightSum];
	}

	private static function normalizeLowReliefTerrainRange(float $minSum, float $targetMin, float $relief, float $maxCap) : array{
		$base = max($targetMin, min($targetMin + 1.0, $minSum));
		$top = min($maxCap, $base + $relief);
		if($top <= $base){
			$top = $base + 1.0;
		}

		return [$base, $top];
	}

	private static function addNormalBiomes(BiomeSelector $selector){

		$selector->addBiome(Biome::getBiome(Biome::OCEAN));
		$selector->addBiome(Biome::getBiome(Biome::FROZEN_OCEAN));
		$selector->addBiome(Biome::getBiome(Biome::DEEP_OCEAN));
		$selector->addBiome(Biome::getBiome(Biome::PLAINS));
		$selector->addBiome(Biome::getBiome(Biome::DESERT));
		$selector->addBiome(Biome::getBiome(Biome::DESERT_HILLS));
		$selector->addBiome(Biome::getBiome(Biome::MOUNTAINS));
		$selector->addBiome(Biome::getBiome(Biome::FOREST));
		$selector->addBiome(Biome::getBiome(Biome::TAIGA));
		$selector->addBiome(Biome::getBiome(Biome::TAIGA_HILLS));
		$selector->addBiome(Biome::getBiome(Biome::COLD_TAIGA));
		$selector->addBiome(Biome::getBiome(Biome::COLD_TAIGA_HILLS));
		$selector->addBiome(Biome::getBiome(Biome::SWAMP));
		$selector->addBiome(Biome::getBiome(Biome::RIVER));
		$selector->addBiome(Biome::getBiome(Biome::FROZEN_RIVER));
		$selector->addBiome(Biome::getBiome(Biome::ICE_PLAINS));
		$selector->addBiome(Biome::getBiome(Biome::ICE_MOUNTAINS));
		$selector->addBiome(Biome::getBiome(Biome::SMALL_MOUNTAINS));
		$selector->addBiome(Biome::getBiome(Biome::BIRCH_FOREST));
		$selector->addBiome(Biome::getBiome(Biome::BEACH));
		$selector->addBiome(Biome::getBiome(Biome::COLD_BEACH));
		$selector->addBiome(Biome::getBiome(Biome::SAVANNA));
		$selector->addBiome(Biome::getBiome(Biome::JUNGLE));
		$selector->addBiome(Biome::getBiome(Biome::MESA));
		$selector->addBiome(Biome::getBiome(Biome::MUSHROOM_ISLAND));
		$selector->addBiome(Biome::getBiome(Biome::MUSHROOM_ISLAND_SHORE));
		$selector->addBiome(Biome::getBiome(Biome::ROOFED_FOREST));
		$selector->addBiome(Biome::getBiome(Biome::PINK_FOREST));
		$selector->addBiome(Biome::getBiome(Biome::GOLDEN_FOREST));
	}

	public static function createBiomeSelector(Random $random) : BiomeSelector{
		$selector = new BiomeSelector(
			$random,
			function($temperature, $rainfall, $River, $ocean, $hills){
				return self::pickRawBiomeId($temperature, $rainfall, $River, $ocean, $hills);
			},
			Biome::getBiome(Biome::PLAINS),
			function($biomeId, $temperature, $rainfall, $River, $ocean, $hills, $x, $z, BiomeSelector $selector){
				return self::postProcessBiomeId($biomeId, $temperature, $rainfall, $River, $ocean, $hills, $x, $z, $selector);
			}
		);

		self::addNormalBiomes($selector);
		$selector->recalculate();

		return $selector;
	}

	public function init(ChunkManager $level, Random $random){
		$this->level = $level;
		$this->random = $random;
		$this->random->setSeed($this->level->getSeed());
		$this->noiseBase = new Simplex($this->random, 4, 1 / 4, 1 / 32);
		$this->random->setSeed($this->level->getSeed());
		$this->selector = self::createBiomeSelector($this->random);

		$cover = new GroundCover();
		$this->generationPopulators[] = $cover;
		
		$Cave = new Cave();
		$this->generationPopulators[] = $Cave;
		
		$Mineshaft = new Mineshaft();
		$this->populators[] = $Mineshaft;

		$this->populators[] = new Stronghold();

		$Dungeon = new Dungeon();
		$this->populators[] = $Dungeon;

		$pond = new Pond();
		$this->populators[] = $pond;

		$this->populators[] = new DesertStructures();
		$this->populators[] = new JungleTemple();
		$this->populators[] = new PillagerOutpost();
		$this->populators[] = new WoodlandMansion();
		$this->populators[] = new RuinedPortal();

		$ores = new Ore();
		$ores->setOreTypes([
			new OreType(new CoalOre(), 20, 16, 0, 128),
			new OreType(New IronOre(), 20, 8, 0, 64),
			new OreType(new RedstoneOre(), 8, 7, 0, 16),
			new OreType(new LapisOre(), 1, 6, 0, 32),
			new OreType(new GoldOre(), 2, 8, 0, 32),
			new OreType(new DiamondOre(), 1, 7, 0, 16),
			new OreType(new Dirt(), 20, 32, 0, 128),
			new OreType(new Stone(Stone::GRANITE), 20, 32, 0, 128),
			new OreType(new Stone(Stone::DIORITE), 20, 32, 0, 128),
			new OreType(new Stone(Stone::ANDESITE), 20, 32, 0, 128),
			new OreType(new Gravel(), 10, 16, 0, 128)
		]);
		$this->populators[] = $ores;
	}

	public function generateChunk($chunkX, $chunkZ){
		$this->random->setSeed(0xdeadbeef ^ ($chunkX << 8) ^ $chunkZ ^ $this->level->getSeed());

		$noise = Generator::getFastNoise3D($this->noiseBase, 16, 128, 16, 4, 8, 4, $chunkX * 16, 0, $chunkZ * 16);

		$chunk = $this->level->getChunk($chunkX, $chunkZ);

		$biomeCache = [];

		for($x = 0; $x < 16; ++$x){
			for($z = 0; $z < 16; ++$z){
				$weightSum = 0;
				$elevationSamples = [];

				$biome = $this->pickBiome($chunkX * 16 + $x, $chunkZ * 16 + $z);
				$chunk->setBiomeId($x, $z, $biome->getId());
				$color = [0, 0, 0];

				for($sx = -self::$SMOOTH_SIZE; $sx <= self::$SMOOTH_SIZE; ++$sx){
					for($sz = -self::$SMOOTH_SIZE; $sz <= self::$SMOOTH_SIZE; ++$sz){

						$weight = self::$GAUSSIAN_KERNEL[$sx + self::$SMOOTH_SIZE][$sz + self::$SMOOTH_SIZE];

						if($sx === 0 and $sz === 0){
							$adjacent = $biome;
						}else{
							$index = Level::chunkHash($chunkX * 16 + $x + $sx, $chunkZ * 16 + $z + $sz);
							if(isset($biomeCache[$index])){
								$adjacent = $biomeCache[$index];
							}else{
								$biomeCache[$index] = $adjacent = $this->pickBiome($chunkX * 16 + $x + $sx, $chunkZ * 16 + $z + $sz);
							}
						}

						$elevationSamples[] = [$adjacent->getId(), $adjacent->getMinElevation() - 1, $adjacent->getMaxElevation(), $weight];
						$bColor = $adjacent->getColor();
						$color[0] += (($bColor >> 16) ** 2) * $weight;
						$color[1] += ((($bColor >> 8) & 0xff) ** 2) * $weight;
						$color[2] += (($bColor & 0xff) ** 2) * $weight;

						$weightSum += $weight;
					}
				}

				list($minSum, $maxSum) = self::normalizeWeightedTerrainElevationRange($elevationSamples);

				$chunk->setBiomeColor($x, $z, sqrt($color[0] / $weightSum), sqrt($color[1] / $weightSum), sqrt($color[2] / $weightSum));

				$solidLand = false;
				for($y = 127; $y >= 0; --$y){
					if($y <= 5 && ($y == 0 or $this->random->nextRange(1, 5) == 1)){
						$chunk->setBlockId($x, $y, $z, Block::BEDROCK);
						continue;
					}
					$noiseAdjustment = 2 * (($maxSum - $y) / ($maxSum - $minSum)) - 1;

					$noiseAdjustment = min($noiseAdjustment, 0.4 + ($y / 10));
					$noiseValue = $noise[$x][$z][$y] + $noiseAdjustment;

					if($noiseValue > 0){
						$chunk->setBlockId($x, $y, $z, Block::STONE);
						$solidLand = true;
					}elseif($y <= $this->waterHeight && $solidLand == false){
						if($y == $this->waterHeight){
							$ID = $adjacent->getID();
							switch($ID){
								case 10:
								case 11:
								case 12:
								case 13:
								case 26:
								case 30:
								case 31:
									$chunk->setBlockId($x, $y, $z, Block::ICE);
									break;
								default:
									$chunk->setBlockId($x, $y, $z, Block::STILL_WATER);
									break;
							}
							
						}else{
							$chunk->setBlockId($x, $y, $z, Block::STILL_WATER);
						}
					}
				}
			}
		}

		foreach($this->generationPopulators as $populator){
			$populator->populate($this->level, $chunkX, $chunkZ, $this->random);
		}
	}

	public function populateChunk($chunkX, $chunkZ){
		$this->random->setSeed(0xdeadbeef ^ ($chunkX << 8) ^ $chunkZ ^ $this->level->getSeed());
		foreach($this->populators as $populator){
			$populator->populate($this->level, $chunkX, $chunkZ, $this->random);
		}

		$chunk = $this->level->getChunk($chunkX, $chunkZ);
		$biome = Biome::getBiome($chunk->getBiomeId(7, 7));
		$biome->populateChunk($this->level, $chunkX, $chunkZ, $this->random);
	}

	public function getSpawn(){
		return new Vector3(127.5, 128, 127.5);
	}

}
