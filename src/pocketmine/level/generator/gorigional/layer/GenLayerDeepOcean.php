<?php

namespace pocketmine\level\generator\gorigional\layer;

/**
 * 移植自 SCAXE-GO-CE pkg/level/generator/gorigional/layer/deep_ocean.go。
 */
class GenLayerDeepOcean extends GenLayer{

	public function __construct($seed, GenLayer $parent){
		parent::__construct($seed, $parent);
	}

	protected function getIntsInternal($areaX, $areaY, $width, $height){
		$parentX = $areaX - 1;
		$parentY = $areaY - 1;
		$parentWidth = $width + 2;
		$parentHeight = $height + 2;

		$parentInts = $this->parent->getInts($parentX, $parentY, $parentWidth, $parentHeight);
		$result = array_fill(0, $width * $height, 0);

		$k = $parentWidth;

		for($y = 0; $y < $height; $y++){
			for($x = 0; $x < $width; $x++){
				$center = $parentInts[$x + 1 + ($y + 1) * $k];

				$up = $parentInts[$x + 1 + ($y + 0) * $k];
				$right = $parentInts[$x + 2 + ($y + 1) * $k];
				$left = $parentInts[$x + 0 + ($y + 1) * $k];
				$down = $parentInts[$x + 1 + ($y + 2) * $k];

				$count = 0;
				if($up === 0){
					$count++;
				}
				if($right === 0){
					$count++;
				}
				if($left === 0){
					$count++;
				}
				if($down === 0){
					$count++;
				}

				if($center === 0 && $count === 4){
					$result[$x + $y * $width] = 24;
				}else{
					$result[$x + $y * $width] = $center;
				}
			}
		}

		return $result;
	}
}
