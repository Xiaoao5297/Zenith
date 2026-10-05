<?php

namespace pocketmine\level\generator\gorigional\layer;

/**
 * 移植自 SCAXE-GO-CE pkg/level/generator/gorigional/layer/zoom.go。
 */
class GenLayerZoom extends GenLayer{
	/** @var bool */
	private $fuzzy;

	public function __construct($seed, GenLayer $parent, $fuzzy = false){
		parent::__construct($seed, $parent);
		$this->fuzzy = $fuzzy;
	}

	public function getInts($areaX, $areaY, $width, $height){
		$parentX = $areaX >> 1;
		$parentY = $areaY >> 1;
		$parentWidth = ($width >> 1) + 2;
		$parentHeight = ($height >> 1) + 2;

		$parentInts = $this->parent->getInts($parentX, $parentY, $parentWidth, $parentHeight);

		$outWidth = ($parentWidth - 1) << 1;
		$outHeight = ($parentHeight - 1) << 1;

		$tempInts = array_fill(0, $outWidth * $outHeight, 0);

		for($y = 0; $y < $parentHeight - 1; $y++){
			$index = ($y << 1) * $outWidth;
			$parentIndex = $y * $parentWidth;

			$val0 = $parentInts[$parentIndex];
			$parentIndex++;
			$val1 = $parentInts[$parentIndex];

			for($x = 0; $x < $parentWidth - 1; $x++){
				$val2 = $parentInts[$parentIndex + $parentWidth - 1];
				$val3 = $parentInts[$parentIndex + $parentWidth];

				$this->initChunkSeed(($x + $parentX) << 1, ($y + $parentY) << 1);

				$tempInts[$index] = $val0;
				$tempInts[$index + $outWidth] = $this->selectRandom($val0, $val2);

				$index++;
				$tempInts[$index] = $this->selectRandom($val0, $val1);

				$tempInts[$index + $outWidth] = $this->fuzzy ? $this->selectRandom($val0, $val1, $val2, $val3) : self::selectModeOrRandom($val0, $val1, $val2, $val3, $this);
				$index++;

				$val0 = $val1;
				$parentIndex++;
				if($x < $parentWidth - 2){
					$val1 = $parentInts[$parentIndex];
				}
			}
		}

		$result = array_fill(0, $width * $height, 0);
		for($y = 0; $y < $height; $y++){
			$srcY = $y + ($areaY & 1);
			$srcOffset = $srcY * $outWidth + ($areaX & 1);
			for($i = 0; $i < $width; $i++){
				$result[$y * $width + $i] = $tempInts[$srcOffset + $i];
			}
		}

		return $result;
	}

	private static function selectModeOrRandom($v1, $v2, $v3, $v4, GenLayerZoom $self){
		if($v2 === $v3 && $v3 === $v4){
			return $v2;
		}
		if($v1 === $v2 && $v1 === $v3){
			return $v1;
		}
		if($v1 === $v2 && $v1 === $v4){
			return $v1;
		}
		if($v1 === $v3 && $v1 === $v4){
			return $v1;
		}
		if($v1 === $v2 && $v3 !== $v4){
			return $v1;
		}
		if($v1 === $v3 && $v2 !== $v4){
			return $v1;
		}
		if($v1 === $v4 && $v2 !== $v3){
			return $v1;
		}
		if($v2 === $v3 && $v1 !== $v4){
			return $v2;
		}
		if($v2 === $v4 && $v1 !== $v3){
			return $v2;
		}
		if($v3 === $v4 && $v1 !== $v2){
			return $v3;
		}

		return $self->selectRandom($v1, $v2, $v3, $v4);
	}
}
