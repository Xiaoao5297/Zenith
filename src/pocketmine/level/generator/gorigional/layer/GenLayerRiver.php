<?php

namespace pocketmine\level\generator\gorigional\layer;

/**
 * 移植自 SCAXE-GO-CE pkg/level/generator/gorigional/layer/river.go。
 */
class GenLayerRiver extends GenLayer{

	public function __construct($baseSeed, GenLayer $parent){
		parent::__construct($baseSeed, $parent);
	}

	protected function getIntsInternal($x, $z, $width, $depth){
		$parentInts = $this->parent->getInts($x - 1, $z - 1, $width + 2, $depth + 2);
		$output = array_fill(0, $width * $depth, 0);

		$parentWidth = $width + 2;

		for($i = 0; $i < $depth; $i++){
			for($j = 0; $j < $width; $j++){
				$center = $this->riverFilter($parentInts[$j + 1 + ($i + 1) * $parentWidth]);

				$left = $this->riverFilter($parentInts[$j + ($i + 1) * $parentWidth]);
				$right = $this->riverFilter($parentInts[$j + 2 + ($i + 1) * $parentWidth]);
				$up = $this->riverFilter($parentInts[$j + 1 + ($i) * $parentWidth]);
				$down = $this->riverFilter($parentInts[$j + 1 + ($i + 2) * $parentWidth]);

				if($center === $left && $center === $right && $center === $up && $center === $down){
					$output[$j + $i * $width] = -1;
				}else{
					$output[$j + $i * $width] = 7;
				}
			}
		}
		return $output;
	}

	private function riverFilter($val){
		if($val >= 2){
			return 2 + ($val & 1);
		}
		return $val;
	}
}
