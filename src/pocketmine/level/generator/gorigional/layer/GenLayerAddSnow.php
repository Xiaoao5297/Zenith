<?php

namespace pocketmine\level\generator\gorigional\layer;

/**
 * 移植自 SCAXE-GO-CE pkg/level/generator/gorigional/layer/add_snow.go。
 */
class GenLayerAddSnow extends GenLayer{

	public function __construct($seed, GenLayer $parent){
		parent::__construct($seed, $parent);
	}

	public function getInts($areaX, $areaY, $width, $height){
		$parentX = $areaX - 1;
		$parentY = $areaY - 1;
		$parentWidth = $width + 2;
		$parentHeight = $height + 2;

		$parentInts = $this->parent->getInts($parentX, $parentY, $parentWidth, $parentHeight);
		$result = array_fill(0, $width * $height, 0);

		$k = $parentWidth;

		for($y = 0; $y < $height; $y++){
			for($x = 0; $x < $width; $x++){
				$k1 = $parentInts[$x + 1 + ($y + 1) * $k];

				$this->initChunkSeed($x + $areaX, $y + $areaY);

				if($k1 === 0){
					$result[$x + $y * $width] = 0;
				}else{
					$r = $this->nextInt(6);
					if($r === 0){
						$r = 4;
					}elseif($r <= 1){
						$r = 3;
					}else{
						$r = 1;
					}
					$result[$x + $y * $width] = $r;
				}
			}
		}

		return $result;
	}
}
