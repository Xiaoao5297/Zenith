<?php

namespace pocketmine\level\generator\gorigional\layer;

/**
 * 移植自 SCAXE-GO-CE pkg/level/generator/gorigional/layer/add_mushroom.go。
 */
class GenLayerAddMushroomIsland extends GenLayer{

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
				$k1 = $parentInts[($dx + 0) + ($dz + 0) * $wOff];
				$l1 = $parentInts[($dx + 2) + ($dz + 0) * $wOff];
				$i2 = $parentInts[($dx + 0) + ($dz + 2) * $wOff];
				$j2 = $parentInts[($dx + 2) + ($dz + 2) * $wOff];
				$k2 = $parentInts[($dx + 1) + ($dz + 1) * $wOff];

				$this->initChunkSeed($dx + $x, $dz + $z);

				if($k2 === 0 && $k1 === 0 && $l1 === 0 && $i2 === 0 && $j2 === 0 && $this->nextInt(100) === 0){
					$out[$dx + $dz * $width] = 14;
				}else{
					$out[$dx + $dz * $width] = $k2;
				}
			}
		}
		return $out;
	}
}
