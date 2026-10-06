<?php

namespace pocketmine\level\generator\gorigional\layer;

/**
 * 移植自 SCAXE-GO-CE pkg/level/generator/gorigional/layer/river_init.go。
 */
class GenLayerRiverInit extends GenLayer{

	public function __construct($baseSeed, GenLayer $parent){
		parent::__construct($baseSeed, $parent);
	}

	protected function getIntsInternal($x, $z, $width, $depth){
		$parentInts = $this->parent->getInts($x, $z, $width, $depth);
		$output = array_fill(0, $width * $depth, 0);

		for($i = 0; $i < $depth; $i++){
			for($j = 0; $j < $width; $j++){
				$original = $parentInts[$j + $i * $width];
				$this->initChunkSeed($x + $j, $z + $i);

				if($original > 0){
					$output[$j + $i * $width] = $this->nextInt(299999) + 2;
				}else{
					$output[$j + $i * $width] = 0;
				}
			}
		}
		return $output;
	}
}
