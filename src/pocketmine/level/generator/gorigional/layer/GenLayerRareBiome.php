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
 * 移植自 SCAXE-GO-CE pkg/level/generator/gorigional/layer/rare_biome.go。
 */
class GenLayerRareBiome extends GenLayer{

	public function __construct($baseSeed, GenLayer $parent){
		parent::__construct($baseSeed, $parent);
	}

	protected function getIntsInternal($x, $z, $width, $depth){
		$xOff = $x - 1;
		$zOff = $z - 1;
		$wOff = $width + 2;
		$dOff = $depth + 2;

		$parentInts = $this->parent->getInts($xOff, $zOff, $wOff, $dOff);
		$out = array_fill(0, $width * $depth, 0);

		for($dz = 0; $dz < $depth; $dz++){
			for($dx = 0; $dx < $width; $dx++){
				$this->initChunkSeed($dx + $x, $dz + $z);

				$centerID = $parentInts[($dx + 1) + ($dz + 1) * $wOff];

				if($this->nextInt(57) === 0){
					if($centerID === 1){
						$out[$dx + $dz * $width] = 129;
					}else{
						$out[$dx + $dz * $width] = $centerID;
					}
				}else{
					$out[$dx + $dz * $width] = $centerID;
				}
			}
		}
		return $out;
	}
}
