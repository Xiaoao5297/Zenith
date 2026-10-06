<?php

namespace pocketmine\level\generator\gorigional\layer;

/**
 * 移植自 SCAXE-GO-CE pkg/level/generator/gorigional/layer/remove_ocean.go。
 */
class GenLayerRemoveTooMuchOcean extends GenLayer{

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
				$up = $parentInts[$x + 1 + ($y + 0) * $k];
				$right = $parentInts[$x + 2 + ($y + 1) * $k];
				$left = $parentInts[$x + 0 + ($y + 1) * $k];
				$down = $parentInts[$x + 1 + ($y + 2) * $k];
				$center = $parentInts[$x + 1 + ($y + 1) * $k];

				$this->initChunkSeed($x + $areaX, $y + $areaY);

				$result[$x + $y * $width] = $center;

				if($center === 0 && $up === 0 && $right === 0 && $left === 0 && $down === 0 && $this->nextInt(2) === 0){
					$result[$x + $y * $width] = 1;
				}
			}
		}

		return $result;
	}
}
