<?php

namespace pocketmine\level\generator\gorigional\layer;

/**
 * 移植自 SCAXE-GO-CE pkg/level/generator/gorigional/layer/biome.go。
 */
class GenLayerBiome extends GenLayer{
	/** @var int[] */
	private $warmBiomes;
	/** @var int[] */
	private $mediumBiomes;
	/** @var int[] */
	private $coldBiomes;
	/** @var int[] */
	private $iceBiomes;

	public function __construct($seed, GenLayer $parent){
		parent::__construct($seed, $parent);

		$this->warmBiomes = [self::BIOME_DESERT, self::BIOME_DESERT, self::BIOME_DESERT, self::BIOME_SAVANNA, self::BIOME_SAVANNA, self::BIOME_PLAINS];
		$this->mediumBiomes = [self::BIOME_FOREST, self::BIOME_ROOFED_FOREST, self::BIOME_EXTREME_HILLS, self::BIOME_PLAINS, self::BIOME_BIRCH_FOREST, self::BIOME_SWAMPLAND];
		$this->coldBiomes = [self::BIOME_FOREST, self::BIOME_EXTREME_HILLS, self::BIOME_TAIGA, self::BIOME_PLAINS];
		$this->iceBiomes = [self::BIOME_ICE_PLAINS, self::BIOME_ICE_PLAINS, self::BIOME_ICE_PLAINS, self::BIOME_COLD_TAIGA];
	}

	public function getInts($areaX, $areaY, $width, $height){
		$parentInts = $this->parent->getInts($areaX, $areaY, $width, $height);
		$result = array_fill(0, $width * $height, 0);

		for($i = 0; $i < count($parentInts); $i++){
			$this->initChunkSeed($i % $width + $areaX, intdiv($i, $width) + $areaY);

			$k = $parentInts[$i];

			$val = ($k & 3840) >> 8;
			$k = $k & ~3840;

			if(self::isOceanic($k)){
				$result[$i] = $k;
			}elseif($k === self::BIOME_MUSHROOM_ISLAND){
				$result[$i] = $k;
			}elseif($k === 1){
				if($val > 0){
					if($this->nextInt(3) === 0){
						$result[$i] = self::BIOME_MESA_PLATEAU;
					}else{
						$result[$i] = self::BIOME_MESA_PLATEAU_F;
					}
				}else{
					$result[$i] = $this->warmBiomes[$this->nextInt(count($this->warmBiomes))];
				}
			}elseif($k === 2){
				if($val > 0){
					$result[$i] = self::BIOME_JUNGLE;
				}else{
					$result[$i] = $this->mediumBiomes[$this->nextInt(count($this->mediumBiomes))];
				}
			}elseif($k === 3){
				if($val > 0){
					$result[$i] = self::BIOME_MEGA_TAIGA;
				}else{
					$result[$i] = $this->coldBiomes[$this->nextInt(count($this->coldBiomes))];
				}
			}elseif($k === 4){
				$result[$i] = $this->iceBiomes[$this->nextInt(count($this->iceBiomes))];
			}else{
				$result[$i] = self::BIOME_MUSHROOM_ISLAND;
			}
		}

		return $result;
	}

	private static function isOceanic($id){
		return $id === self::BIOME_OCEAN || $id === self::BIOME_DEEP_OCEAN || $id === self::BIOME_FROZEN_OCEAN;
	}
}
