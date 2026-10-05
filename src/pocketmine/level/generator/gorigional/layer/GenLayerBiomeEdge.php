<?php

namespace pocketmine\level\generator\gorigional\layer;

/**
 * 移植自 SCAXE-GO-CE pkg/level/generator/gorigional/layer/biome_edge.go。
 */
class GenLayerBiomeEdge extends GenLayer{
	const TEMP_OCEAN = 0;
	const TEMP_COLD = 1;
	const TEMP_MEDIUM = 2;
	const TEMP_WARM = 3;

	public function __construct($baseSeed, GenLayer $parent){
		parent::__construct($baseSeed, $parent);
	}

	public function getInts($x, $z, $width, $depth){
		$parentInts = $this->parent->getInts($x - 1, $z - 1, $width + 2, $depth + 2);
		$output = array_fill(0, $width * $depth, 0);
		$parentWidth = $width + 2;

		for($i = 0; $i < $depth; $i++){
			for($j = 0; $j < $width; $j++){
				$this->initChunkSeed($x + $j, $z + $i);
				$k = $parentInts[$j + 1 + ($i + 1) * $parentWidth];

				$replaced = $this->replaceBiomeEdgeIfNecessary($parentInts, $output, $j, $i, $width, $k, self::BIOME_EXTREME_HILLS, self::BIOME_EXTREME_HILLS_EDGE);
				if(!$replaced){
					$replaced = $this->replaceBiomeEdge($parentInts, $output, $j, $i, $width, $k, self::BIOME_MESA_PLATEAU, self::BIOME_MESA);
				}
				if(!$replaced){
					$replaced = $this->replaceBiomeEdge($parentInts, $output, $j, $i, $width, $k, self::BIOME_MESA_PLATEAU_F, self::BIOME_MESA);
				}
				if(!$replaced){
					$replaced = $this->replaceBiomeEdge($parentInts, $output, $j, $i, $width, $k, self::BIOME_MEGA_TAIGA, self::BIOME_TAIGA);
				}

				if(!$replaced){
					if($k === self::BIOME_DESERT){
						$l1 = $parentInts[$j + 1 + ($i) * $parentWidth];
						$i2 = $parentInts[$j + 1 + ($i + 2) * $parentWidth];
						$j2 = $parentInts[$j + ($i + 1) * $parentWidth];
						$k2 = $parentInts[$j + 2 + ($i + 1) * $parentWidth];

						if($l1 !== self::BIOME_ICE_PLAINS && $i2 !== self::BIOME_ICE_PLAINS && $j2 !== self::BIOME_ICE_PLAINS && $k2 !== self::BIOME_ICE_PLAINS){
							$output[$j + $i * $width] = $k;
						}else{
							$output[$j + $i * $width] = self::BIOME_EXTREME_HILLS_PLUS;
						}
					}elseif($k === self::BIOME_SWAMPLAND){
						$l1 = $parentInts[$j + 1 + ($i) * $parentWidth];
						$i2 = $parentInts[$j + 1 + ($i + 2) * $parentWidth];
						$j2 = $parentInts[$j + ($i + 1) * $parentWidth];
						$k2 = $parentInts[$j + 2 + ($i + 1) * $parentWidth];

						$isDesert = $l1 === self::BIOME_DESERT || $i2 === self::BIOME_DESERT || $j2 === self::BIOME_DESERT || $k2 === self::BIOME_DESERT;
						$isColdTaiga = $l1 === self::BIOME_COLD_TAIGA || $i2 === self::BIOME_COLD_TAIGA || $j2 === self::BIOME_COLD_TAIGA || $k2 === self::BIOME_COLD_TAIGA;
						$isIcePlains = $l1 === self::BIOME_ICE_PLAINS || $i2 === self::BIOME_ICE_PLAINS || $j2 === self::BIOME_ICE_PLAINS || $k2 === self::BIOME_ICE_PLAINS;

						if(!$isDesert && !$isColdTaiga && !$isIcePlains){
							$isJungle = $l1 === self::BIOME_JUNGLE || $i2 === self::BIOME_JUNGLE || $j2 === self::BIOME_JUNGLE || $k2 === self::BIOME_JUNGLE;
							if(!$isJungle){
								$output[$j + $i * $width] = $k;
							}else{
								$output[$j + $i * $width] = self::BIOME_JUNGLE_EDGE;
							}
						}else{
							$output[$j + $i * $width] = self::BIOME_PLAINS;
						}
					}else{
						$output[$j + $i * $width] = $k;
					}
				}
			}
		}
		return $output;
	}

	private function replaceBiomeEdgeIfNecessary(array $parentInts, array &$output, $x, $z, $width, $currentBiome, $targetBiome, $edgeBiome){
		if(!self::biomesEqualOrMesaPlateau($currentBiome, $targetBiome)){
			return false;
		}

		$parentWidth = $width + 2;
		$i = $parentInts[$x + 1 + ($z) * $parentWidth];
		$j = $parentInts[$x + 1 + ($z + 2) * $parentWidth];
		$k = $parentInts[$x + ($z + 1) * $parentWidth];
		$m = $parentInts[$x + 2 + ($z + 1) * $parentWidth];

		if(self::canBiomesBeNeighbors($i, $targetBiome) && self::canBiomesBeNeighbors($j, $targetBiome) && self::canBiomesBeNeighbors($k, $targetBiome) && self::canBiomesBeNeighbors($m, $targetBiome)){
			$output[$x + $z * $width] = $currentBiome;
		}else{
			$output[$x + $z * $width] = $edgeBiome;
		}
		return true;
	}

	private function replaceBiomeEdge(array $parentInts, array &$output, $x, $z, $width, $currentBiome, $targetBiome, $edgeBiome){
		if($currentBiome !== $targetBiome){
			return false;
		}

		$parentWidth = $width + 2;
		$i = $parentInts[$x + 1 + ($z) * $parentWidth];
		$j = $parentInts[$x + 1 + ($z + 2) * $parentWidth];
		$k = $parentInts[$x + ($z + 1) * $parentWidth];
		$m = $parentInts[$x + 2 + ($z + 1) * $parentWidth];

		if(self::biomesEqualOrMesaPlateau($i, $targetBiome) && self::biomesEqualOrMesaPlateau($j, $targetBiome) && self::biomesEqualOrMesaPlateau($k, $targetBiome) && self::biomesEqualOrMesaPlateau($m, $targetBiome)){
			$output[$x + $z * $width] = $currentBiome;
		}else{
			$output[$x + $z * $width] = $edgeBiome;
		}
		return true;
	}

	private static function canBiomesBeNeighbors($id1, $id2){
		if(self::biomesEqualOrMesaPlateau($id1, $id2)){
			return true;
		}

		$t1 = self::getTempCategory($id1);
		$t2 = self::getTempCategory($id2);

		return $t1 === $t2 || $t1 === self::TEMP_MEDIUM || $t2 === self::TEMP_MEDIUM;
	}

	public static function biomesEqualOrMesaPlateau($a, $b){
		if($a === $b){
			return true;
		}

		$isMesaVariantA = $a === self::BIOME_MESA_PLATEAU || $a === self::BIOME_MESA_PLATEAU_F;
		$isMesaVariantB = $b === self::BIOME_MESA_PLATEAU || $b === self::BIOME_MESA_PLATEAU_F;

		if(!$isMesaVariantA){
			return self::getBiomeClass($a) === self::getBiomeClass($b);
		}else{
			return $isMesaVariantB;
		}
	}

	private static function getTempCategory($id){
		switch($id){
			case self::BIOME_OCEAN:
			case self::BIOME_DEEP_OCEAN:
			case self::BIOME_RIVER:
			case self::BIOME_SWAMPLAND:
			case self::BIOME_MUSHROOM_ISLAND:
			case self::BIOME_MUSHROOM_ISLAND_SHORE:
			case self::BIOME_BEACH:
				return self::TEMP_MEDIUM;
			case self::BIOME_FROZEN_OCEAN:
			case self::BIOME_FROZEN_RIVER:
			case self::BIOME_ICE_PLAINS:
			case self::BIOME_ICE_MOUNTAINS:
			case self::BIOME_COLD_BEACH:
			case self::BIOME_COLD_TAIGA:
			case self::BIOME_COLD_TAIGA_HILLS:
				return self::TEMP_COLD;
			case self::BIOME_PLAINS:
			case self::BIOME_FOREST:
			case self::BIOME_EXTREME_HILLS:
			case self::BIOME_TAIGA:
			case self::BIOME_EXTREME_HILLS_EDGE:
			case self::BIOME_FOREST_HILLS:
			case self::BIOME_TAIGA_HILLS:
			case self::BIOME_EXTREME_HILLS_PLUS:
			case self::BIOME_BIRCH_FOREST:
			case self::BIOME_BIRCH_FOREST_HILLS:
			case self::BIOME_ROOFED_FOREST:
			case self::BIOME_MEGA_TAIGA:
			case self::BIOME_MEGA_TAIGA_HILLS:
			case self::BIOME_STONE_BEACH:
				return self::TEMP_MEDIUM;
			case self::BIOME_DESERT:
			case self::BIOME_DESERT_HILLS:
			case self::BIOME_JUNGLE:
			case self::BIOME_JUNGLE_HILLS:
			case self::BIOME_JUNGLE_EDGE:
			case self::BIOME_SAVANNA:
			case self::BIOME_SAVANNA_PLATEAU:
			case self::BIOME_MESA:
			case self::BIOME_MESA_PLATEAU_F:
			case self::BIOME_MESA_PLATEAU:
			case self::BIOME_HELL:
				return self::TEMP_WARM;
		}
		return self::TEMP_MEDIUM;
	}

	private static function getBiomeClass($id){
		switch($id){
			case self::BIOME_MESA:
			case self::BIOME_MESA_PLATEAU_F:
			case self::BIOME_MESA_PLATEAU:
				return 1001;
			case self::BIOME_JUNGLE:
			case self::BIOME_JUNGLE_HILLS:
			case self::BIOME_JUNGLE_EDGE:
				return 1002;
			case self::BIOME_MEGA_TAIGA:
			case self::BIOME_MEGA_TAIGA_HILLS:
			case self::BIOME_TAIGA:
			case self::BIOME_TAIGA_HILLS:
			case self::BIOME_COLD_TAIGA:
			case self::BIOME_COLD_TAIGA_HILLS:
				return 1003;
			case self::BIOME_OCEAN:
			case self::BIOME_DEEP_OCEAN:
			case self::BIOME_FROZEN_OCEAN:
				return 1004;
		}
		return $id;
	}
}
