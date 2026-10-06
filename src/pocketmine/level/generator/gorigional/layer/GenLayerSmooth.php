<?php

namespace pocketmine\level\generator\gorigional\layer;

/**
 * 移植自 SCAXE-GO-CE pkg/level/generator/gorigional/layer/smooth.go。
 */
class GenLayerSmooth extends GenLayer{

	public function __construct($baseSeed, GenLayer $parent){
		parent::__construct($baseSeed, $parent);
	}

	protected function getIntsInternal($x, $z, $width, $depth){
		$parentInts = $this->parent->getInts($x - 1, $z - 1, $width + 2, $depth + 2);
		$output = array_fill(0, $width * $depth, 0);
		$parentWidth = $width + 2;

		for($i = 0; $i < $depth; $i++){
			for($j = 0; $j < $width; $j++){
				$center = $parentInts[$j + 1 + ($i + 1) * $parentWidth];
				$right = $parentInts[$j + 2 + ($i + 1) * $parentWidth];
				$left = $parentInts[$j + ($i + 1) * $parentWidth];
				$down = $parentInts[$j + 1 + ($i + 2) * $parentWidth];
				$up = $parentInts[$j + 1 + ($i) * $parentWidth];

				if($left === $right && $up === $down){
					$this->initChunkSeed($x + $j, $z + $i);
					if($this->nextInt(2) === 0){
						$output[$j + $i * $width] = $left;
					}else{
						$output[$j + $i * $width] = $up;
					}
				}else{
					if($left === $right){
						$output[$j + $i * $width] = $left;
					}elseif($up === $down){
						$output[$j + $i * $width] = $up;
					}else{
						$output[$j + $i * $width] = $center;
					}
				}
			}
		}
		return $output;
	}
}
