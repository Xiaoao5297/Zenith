<?php

namespace pocketmine\level\generator\gorigional\layer;

/**
 * 移植自 SCAXE-GO-CE pkg/level/generator/gorigional/layer/rare_biome.go。
 */
class GenLayerRareBiome extends GenLayer{

	public function __construct($baseSeed, GenLayer $parent){
		parent::__construct($baseSeed, $parent);
	}

	public function getInts($x, $z, $width, $depth){
		$xOff = $x - 1;
		$zOff = $z - 1;
		$wOff = $width + 2;
		$dOff = $depth + 2;

		$parentInts = $this->parent->getInts($xOff, $zOff, $wOff, $dOff);
		$out = array_fill(0, $width * $depth, 0);

		for($dz = 0; $dz < $depth; $dz++){
			for($dx = 0; $dx < $width; $dx++){
				$this->initChunkSeed($dx + $x, $dz + $z);

				$centerID = $parentInts[($dx + 1) + ($dz + 1) * $wOff];

				if($this->nextInt(57) === 0){
					if($centerID === 1){
						$out[$dx + $dz * $width] = 129;
					}else{
						$out[$dx + $dz * $width] = $centerID;
					}
				}else{
					$out[$dx + $dz * $width] = $centerID;
				}
			}
		}
		return $out;
	}
}
