<?php

namespace pocketmine\level\generator\gorigional\noise;

use pocketmine\level\generator\gorigional\JavaRandom;

/**
 * 移植自 SCAXE-GO-CE pkg/level/generator/gorigional/noise/perlin_simplex.go。
 */
class PerlinSimplexGenerator{
	/** @var int */
	private $levels;
	/** @var SimplexNoise[] */
	private $noiseLevels = [];

	public function __construct(JavaRandom $r, $levels){
		$this->levels = $levels;
		for($i = 0; $i < $levels; $i++){
			$this->noiseLevels[$i] = new SimplexNoise($r);
		}
	}

	public function getValue($x, $y){
		$d0 = 0.0;
		$d1 = 1.0;

		for($i = 0; $i < $this->levels; $i++){
			$d0 += $this->noiseLevels[$i]->getValue($x * $d1, $y * $d1) / $d1;
			$d1 /= 2.0;
		}

		return $d0;
	}

	public function getRegion(array &$noiseArray = null, $x, $z, $sizeX, $sizeZ, $scaleX, $scaleZ, $scaleExp){
		return $this->getRegionWithDivide($noiseArray, $x, $z, $sizeX, $sizeZ, $scaleX, $scaleZ, $scaleExp, 0.5);
	}

	public function getRegionWithDivide(array &$noiseArray = null, $x, $z, $sizeX, $sizeZ, $scaleX, $scaleZ, $scaleExp, $val){
		$size = $sizeX * $sizeZ;
		if($noiseArray !== null && count($noiseArray) >= $size){
			for($i = 0; $i < count($noiseArray); $i++){
				$noiseArray[$i] = 0.0;
			}
		}else{
			$noiseArray = array_fill(0, $size, 0.0);
		}

		$d1 = 1.0;
		$d0 = 1.0;

		for($j = 0; $j < $this->levels; $j++){
			$this->noiseLevels[$j]->add($noiseArray, $x, $z, $sizeX, $sizeZ, $scaleX * $d0 * $d1, $scaleZ * $d0 * $d1, 0.55 / $d1);
			$d0 *= $scaleExp;
			$d1 *= $val;
		}

		return $noiseArray;
	}
}
