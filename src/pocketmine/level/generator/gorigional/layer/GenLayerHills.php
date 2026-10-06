<?php

namespace pocketmine\level\generator\gorigional\layer;

/**
 * 移植自 SCAXE-GO-CE pkg/level/generator/gorigional/layer/hills.go。
 */
class GenLayerHills extends GenLayer{
	/** @var GenLayer */
	private $riverLayer;

	public function __construct($baseSeed, GenLayer $parent, GenLayer $riverLayer){
		parent::__construct($baseSeed, $parent);
		$this->riverLayer = $riverLayer;
	}

	public function initWorldGenSeed($seed){
		parent::initWorldGenSeed($seed);
		$this->riverLayer->initWorldGenSeed($seed);
	}

	protected function getIntsInternal($x, $z, $width, $depth){
		$parentInts = $this->parent->getInts($x - 1, $z - 1, $width + 2, $depth + 2);
		$riverInts = $this->riverLayer->getInts($x - 1, $z - 1, $width + 2, $depth + 2);
		$output = array_fill(0, $width * $depth, 0);
		$parentWidth = $width + 2;

		for($i = 0; $i < $depth; $i++){
			for($j = 0; $j < $width; $j++){
				$this->initChunkSeed($x + $j, $z + $i);

				$k = $parentInts[$j + 1 + ($i + 1) * $parentWidth];
				$riverVal = $riverInts[$j + 1 + ($i + 1) * $parentWidth];

				$flag = ($riverVal - 2) % 29 === 0;

				if($k !== 0 && $riverVal >= 2 && ($riverVal - 2) % 29 === 1 && $k < 128){
					$mutatedID = self::getMutationForBiome($k);
					if($mutatedID !== $k){
						$k = $mutatedID;
					}
					$output[$j + $i * $width] = $k;
				}elseif($this->nextInt(3) !== 0 && !$flag){
					$output[$j + $i * $width] = $k;
				}else{
					$i1 = $k;

					if($k === self::BIOME_DESERT){
						$i1 = self::BIOME_DESERT_HILLS;
					}elseif($k === self::BIOME_FOREST){
						$i1 = self::BIOME_FOREST_HILLS;
					}elseif($k === self::BIOME_BIRCH_FOREST){
						$i1 = self::BIOME_BIRCH_FOREST_HILLS;
					}elseif($k === self::BIOME_ROOFED_FOREST){
						$i1 = self::BIOME_PLAINS;
					}elseif($k === self::BIOME_TAIGA){
						$i1 = self::BIOME_TAIGA_HILLS;
					}elseif($k === self::BIOME_MEGA_TAIGA){
						$i1 = self::BIOME_MEGA_TAIGA_HILLS;
					}elseif($k === self::BIOME_COLD_TAIGA){
						$i1 = self::BIOME_COLD_TAIGA_HILLS;
					}elseif($k === self::BIOME_PLAINS){
						if($this->nextInt(3) === 0){
							$i1 = self::BIOME_FOREST_HILLS;
						}else{
							$i1 = self::BIOME_FOREST;
						}
					}elseif($k === self::BIOME_ICE_PLAINS){
						$i1 = self::BIOME_ICE_MOUNTAINS;
					}elseif($k === self::BIOME_JUNGLE){
						$i1 = self::BIOME_JUNGLE_HILLS;
					}elseif($k === self::BIOME_OCEAN){
						$i1 = self::BIOME_DEEP_OCEAN;
					}elseif($k === self::BIOME_EXTREME_HILLS){
						$i1 = self::BIOME_EXTREME_HILLS_PLUS;
					}elseif($k === self::BIOME_SAVANNA){
						$i1 = self::BIOME_SAVANNA_PLATEAU;
					}elseif(GenLayerBiomeEdge::biomesEqualOrMesaPlateau($k, self::BIOME_MESA_PLATEAU_F)){
						$i1 = self::BIOME_MESA;
					}elseif($k === self::BIOME_DEEP_OCEAN && $this->nextInt(3) === 0){
						if($this->nextInt(2) === 0){
							$i1 = self::BIOME_PLAINS;
						}else{
							$i1 = self::BIOME_FOREST;
						}
					}

					if($flag && $i1 !== $k){
						$mutatedOfVariant = self::getMutationForBiome($i1);
						if($mutatedOfVariant !== $i1){
							$i1 = $mutatedOfVariant;
						}else{
							$i1 = $k;
						}
					}

					if($i1 === $k){
						$output[$j + $i * $width] = $k;
					}else{
						$k2 = $parentInts[$j + 1 + ($i + 0) * $parentWidth];
						$j1Neighbor = $parentInts[$j + 2 + ($i + 1) * $parentWidth];
						$k1 = $parentInts[$j + 0 + ($i + 1) * $parentWidth];
						$l1 = $parentInts[$j + 1 + ($i + 2) * $parentWidth];

						$countCompatible = 0;
						if(GenLayerBiomeEdge::biomesEqualOrMesaPlateau($k2, $k)){
							$countCompatible++;
						}
						if(GenLayerBiomeEdge::biomesEqualOrMesaPlateau($j1Neighbor, $k)){
							$countCompatible++;
						}
						if(GenLayerBiomeEdge::biomesEqualOrMesaPlateau($k1, $k)){
							$countCompatible++;
						}
						if(GenLayerBiomeEdge::biomesEqualOrMesaPlateau($l1, $k)){
							$countCompatible++;
						}

						if($countCompatible >= 3){
							$output[$j + $i * $width] = $i1;
						}else{
							$output[$j + $i * $width] = $k;
						}
					}
				}
			}
		}
		return $output;
	}

	private static function getMutationForBiome($id){
		switch($id){
			case self::BIOME_PLAINS:
			case self::BIOME_DESERT:
			case self::BIOME_EXTREME_HILLS:
			case self::BIOME_FOREST:
			case self::BIOME_TAIGA:
			case self::BIOME_SWAMPLAND:
			case self::BIOME_ICE_PLAINS:
			case self::BIOME_JUNGLE:
			case self::BIOME_JUNGLE_EDGE:
			case self::BIOME_BIRCH_FOREST:
			case self::BIOME_BIRCH_FOREST_HILLS:
			case self::BIOME_ROOFED_FOREST:
			case self::BIOME_COLD_TAIGA:
			case self::BIOME_MEGA_TAIGA:
			case self::BIOME_MEGA_TAIGA_HILLS:
			case self::BIOME_EXTREME_HILLS_PLUS:
			case self::BIOME_SAVANNA:
			case self::BIOME_SAVANNA_PLATEAU:
			case self::BIOME_MESA:
			case self::BIOME_MESA_PLATEAU_F:
			case self::BIOME_MESA_PLATEAU:
				return $id + 128;
		}
		return $id;
	}
}
