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
