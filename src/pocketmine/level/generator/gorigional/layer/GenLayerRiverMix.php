<?php

namespace pocketmine\level\generator\gorigional\layer;

/**
 * 移植自 SCAXE-GO-CE pkg/level/generator/gorigional/layer/river_mix.go。
 */
class GenLayerRiverMix extends GenLayer{
	/** @var GenLayer */
	private $biomePatternGeneratorChain;
	/** @var GenLayer */
	private $riverPatternGeneratorChain;

	public function __construct($baseSeed, GenLayer $biomeChain, GenLayer $riverChain){
		parent::__construct($baseSeed, null);
		$this->biomePatternGeneratorChain = $biomeChain;
		$this->riverPatternGeneratorChain = $riverChain;
	}

	public function initWorldGenSeed($seed){
		parent::initWorldGenSeed($seed);
		$this->biomePatternGeneratorChain->initWorldGenSeed($seed);
		$this->riverPatternGeneratorChain->initWorldGenSeed($seed);
	}

	public function getInts($x, $z, $width, $depth){
		$biomeInts = $this->biomePatternGeneratorChain->getInts($x, $z, $width, $depth);
		$riverInts = $this->riverPatternGeneratorChain->getInts($x, $z, $width, $depth);
		$output = array_fill(0, $width * $depth, 0);

		for($i = 0; $i < $width * $depth; $i++){
			$b = $biomeInts[$i];
			$r = $riverInts[$i];

			if($b !== self::BIOME_OCEAN && $b !== self::BIOME_DEEP_OCEAN){
				if($r === self::BIOME_RIVER){
					if($b === self::BIOME_ICE_PLAINS){
						$output[$i] = self::BIOME_FROZEN_RIVER;
					}elseif($b !== self::BIOME_MUSHROOM_ISLAND && $b !== self::BIOME_MUSHROOM_ISLAND_SHORE){
						$output[$i] = $r & 255;
					}else{
						$output[$i] = self::BIOME_MUSHROOM_ISLAND_SHORE;
					}
				}else{
					$output[$i] = $b;
				}
			}else{
				$output[$i] = $b;
			}
		}
		return $output;
	}
}
