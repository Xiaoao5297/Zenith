<?php

namespace pocketmine\level\generator\gorigional\layer;

/**
 * 移植自 SCAXE-GO-CE pkg/level/generator/gorigional/layer/shore.go。
 */
class GenLayerShore extends GenLayer{

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

				if($k === self::BIOME_MUSHROOM_ISLAND){
					$j2 = $parentInts[$j + 1 + ($i + 0) * $parentWidth];
					$i3 = $parentInts[$j + 2 + ($i + 1) * $parentWidth];
					$l3 = $parentInts[$j + 0 + ($i + 1) * $parentWidth];
					$k4 = $parentInts[$j + 1 + ($i + 2) * $parentWidth];

					if($j2 !== self::BIOME_OCEAN && $i3 !== self::BIOME_OCEAN && $l3 !== self::BIOME_OCEAN && $k4 !== self::BIOME_OCEAN){
						$output[$j + $i * $width] = $k;
					}else{
						$output[$j + $i * $width] = self::BIOME_MUSHROOM_ISLAND_SHORE;
					}
				}elseif(self::isJungle($k)){
					$i2 = $parentInts[$j + 1 + ($i + 0) * $parentWidth];
					$l2 = $parentInts[$j + 2 + ($i + 1) * $parentWidth];
					$k3 = $parentInts[$j + 0 + ($i + 1) * $parentWidth];
					$j4 = $parentInts[$j + 1 + ($i + 2) * $parentWidth];

					if($this->isJungleCompatible($i2) && $this->isJungleCompatible($l2) && $this->isJungleCompatible($k3) && $this->isJungleCompatible($j4)){
						if(!self::isOceanic($i2) && !self::isOceanic($l2) && !self::isOceanic($k3) && !self::isOceanic($j4)){
							$output[$j + $i * $width] = $k;
						}else{
							$output[$j + $i * $width] = self::BIOME_BEACH;
						}
					}else{
						$output[$j + $i * $width] = self::BIOME_JUNGLE_EDGE;
					}
				}elseif($k !== self::BIOME_EXTREME_HILLS && $k !== self::BIOME_EXTREME_HILLS_PLUS && $k !== self::BIOME_EXTREME_HILLS_EDGE){
					if(self::isSnowy($k)){
						$this->replaceIfNeighborOcean($parentInts, $output, $j, $i, $parentWidth, $k, self::BIOME_COLD_BEACH, $width);
					}elseif($k !== self::BIOME_MESA && $k !== self::BIOME_MESA_PLATEAU_F){
						if($k !== self::BIOME_OCEAN && $k !== self::BIOME_DEEP_OCEAN && $k !== self::BIOME_RIVER && $k !== self::BIOME_SWAMPLAND){
							$l1 = $parentInts[$j + 1 + ($i + 0) * $parentWidth];
							$k2 = $parentInts[$j + 2 + ($i + 1) * $parentWidth];
							$j3 = $parentInts[$j + 0 + ($i + 1) * $parentWidth];
							$i4 = $parentInts[$j + 1 + ($i + 2) * $parentWidth];

							if(!self::isOceanic($l1) && !self::isOceanic($k2) && !self::isOceanic($j3) && !self::isOceanic($i4)){
								$output[$j + $i * $width] = $k;
							}else{
								$output[$j + $i * $width] = self::BIOME_BEACH;
							}
						}else{
							$output[$j + $i * $width] = $k;
						}
					}else{
						$lVal = $parentInts[$j + 1 + ($i + 0) * $parentWidth];
						$i1 = $parentInts[$j + 2 + ($i + 1) * $parentWidth];
						$j1 = $parentInts[$j + 0 + ($i + 1) * $parentWidth];
						$k1 = $parentInts[$j + 1 + ($i + 2) * $parentWidth];

						if(!self::isOceanic($lVal) && !self::isOceanic($i1) && !self::isOceanic($j1) && !self::isOceanic($k1)){
							if(self::isMesa($lVal) && self::isMesa($i1) && self::isMesa($j1) && self::isMesa($k1)){
								$output[$j + $i * $width] = $k;
							}else{
								$output[$j + $i * $width] = self::BIOME_DESERT;
							}
						}else{
							$output[$j + $i * $width] = $k;
						}
					}
				}else{
					$this->replaceIfNeighborOcean($parentInts, $output, $j, $i, $parentWidth, $k, self::BIOME_STONE_BEACH, $width);
				}
			}
		}
		return $output;
	}

	private function replaceIfNeighborOcean(array $parentInts, array &$output, $x, $z, $parentWidth, $current, $replace, $outWidth){
		if(self::isOceanic($current)){
			$output[$x + $z * $outWidth] = $current;
		}else{
			$i = $parentInts[$x + 1 + ($z + 0) * $parentWidth];
			$j = $parentInts[$x + 2 + ($z + 1) * $parentWidth];
			$k = $parentInts[$x + 0 + ($z + 1) * $parentWidth];
			$m = $parentInts[$x + 1 + ($z + 2) * $parentWidth];

			if(!self::isOceanic($i) && !self::isOceanic($j) && !self::isOceanic($k) && !self::isOceanic($m)){
				$output[$x + $z * $outWidth] = $current;
			}else{
				$output[$x + $z * $outWidth] = $replace;
			}
		}
	}

	private function isJungleCompatible($id){
		if(self::isJungle($id)){
			return true;
		}
		return $id === self::BIOME_JUNGLE_EDGE || $id === self::BIOME_JUNGLE || $id === self::BIOME_JUNGLE_HILLS || $id === self::BIOME_FOREST || $id === self::BIOME_TAIGA || self::isOceanic($id);
	}

	private static function isJungle($id){
		return $id === self::BIOME_JUNGLE || $id === self::BIOME_JUNGLE_HILLS || $id === self::BIOME_JUNGLE_EDGE;
	}

	private static function isMesa($id){
		return $id === self::BIOME_MESA || $id === self::BIOME_MESA_PLATEAU_F || $id === self::BIOME_MESA_PLATEAU;
	}

	private static function isSnowy($id){
		return $id === self::BIOME_ICE_PLAINS || $id === self::BIOME_ICE_MOUNTAINS || $id === self::BIOME_COLD_TAIGA || $id === self::BIOME_COLD_TAIGA_HILLS;
	}

	private static function isOceanic($id){
		return $id === self::BIOME_OCEAN || $id === self::BIOME_DEEP_OCEAN || $id === self::BIOME_FROZEN_OCEAN;
	}
}
