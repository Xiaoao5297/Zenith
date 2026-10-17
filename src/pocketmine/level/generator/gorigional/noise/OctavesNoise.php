<?php

/***
 *  _____                _  __   __
 * /__  /  ___   ____   (_)/ /_ / /_
 *   / /  / _ \ / __ \ / // __// __ \
 *  / /__/  __// / / // // /_ / / / /
 * /____/\___//_/ /_//_/ \__//_/ /_/
 *
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * @author Xiaoao
 * @link https://github.com/Xiaoao5297/Zenith
 *
 *
*/


namespace pocketmine\level\generator\gorigional\noise;

use pocketmine\level\generator\gorigional\RandomSource;

/**
 * 移植自 SCAXE-GO-CE pkg/level/generator/gorigional/noise/octaves.go。
 */
class OctavesNoise{
	/** @var ImprovedNoise[] */
	private $generators = [];
	/** @var int */
	private $octaves;

	public function __construct(RandomSource $rnd, $octaves){
		$this->octaves = $octaves;
		for($i = 0; $i < $octaves; $i++){
			$this->generators[$i] = new ImprovedNoise($rnd);
		}
	}

	public function generateNoiseOctaves(array &$noiseArray = null, $xOffset, $yOffset, $zOffset, $xSize, $ySize, $zSize, $xScale, $yScale, $zScale){
		$size = $xSize * $ySize * $zSize;
		if($noiseArray === null || count($noiseArray) < $size){
			$noiseArray = array_fill(0, $size, 0.0);
		}else{
			for($i = 0; $i < $size; $i++){
				$noiseArray[$i] = 0.0;
			}
		}

		$amp = 1.0;

		for($j = 0; $j < $this->octaves; $j++){
			$d0 = $xOffset * $amp * $xScale;
			$d1 = $yOffset * $amp * $yScale;
			$d2 = $zOffset * $amp * $zScale;

			$k = (int) floor($d0);
			$l = (int) floor($d2);

			$d0 -= $k;
			$d2 -= $l;

			$k %= 16777216;
			$l %= 16777216;

			$d0 += $k;
			$d2 += $l;

			$this->generators[$j]->populateNoiseArray($noiseArray, $d0, $d1, $d2, $xSize, $ySize, $zSize, $xScale * $amp, $yScale * $amp, $zScale * $amp, $amp);
			$amp /= 2.0;
		}

		return $noiseArray;
	}

	public function generateNoiseOctaves2D(array &$noiseArray = null, $xOffset, $zOffset, $xSize, $zSize, $xScale, $zScale){
		return $this->generateNoiseOctaves($noiseArray, $xOffset, 10, $zOffset, $xSize, 1, $zSize, $xScale, 1.0, $zScale);
	}
}
