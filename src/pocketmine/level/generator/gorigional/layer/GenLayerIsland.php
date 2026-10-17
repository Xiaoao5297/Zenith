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
 * 移植自 SCAXE-GO-CE pkg/level/generator/gorigional/layer/island.go。
 */
class GenLayerIsland extends GenLayer{

	public function __construct($seed){
		parent::__construct($seed, null);
	}

	protected function getIntsInternal($x, $z, $width, $depth){
		$result = array_fill(0, $width * $depth, 0);

		for($i = 0; $i < $depth; $i++){
			for($j = 0; $j < $width; $j++){
				$this->initChunkSeed($x + $j, $z + $i);

				$val = 0;
				if($this->nextInt(10) === 0){
					$val = 1;
				}
				$result[$j + $i * $width] = $val;
			}
		}

		if($x > -$width && $x <= 0 && $z > -$depth && $z <= 0){
			$result[(-$x) + (-$z) * $width] = 1;
		}

		return $result;
	}
}
