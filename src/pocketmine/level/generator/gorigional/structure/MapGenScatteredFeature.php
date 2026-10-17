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


namespace pocketmine\level\generator\gorigional\structure;

/**
 * 移植自 Java 1.7.10 MapGenScatteredFeature。
 */
class MapGenScatteredFeature extends MapGenStructure{
	/** @var int */
	private $maxDistanceBetweenScatteredFeatures = 32;
	/** @var int */
	private $minDistanceBetweenScatteredFeatures = 8;

	protected function canSpawnStructureAtCoords($chunkX, $chunkZ){
		$i = $chunkX;
		$j = $chunkZ;

		if($chunkX < 0){
			$chunkX -= $this->maxDistanceBetweenScatteredFeatures - 1;
		}
		if($chunkZ < 0){
			$chunkZ -= $this->maxDistanceBetweenScatteredFeatures - 1;
		}

		$k = intdiv($chunkX, $this->maxDistanceBetweenScatteredFeatures);
		$l = intdiv($chunkZ, $this->maxDistanceBetweenScatteredFeatures);

		// MapGenBase 已按 MCPE LargeFeature::apply 用区块种子初始化 $this->rand，
		// 这里直接消费两个随机数，不能再用 Java 的 getRandomAt。
		$k *= $this->maxDistanceBetweenScatteredFeatures;
		$l *= $this->maxDistanceBetweenScatteredFeatures;

		$k += $this->rand->nextBoundedInt($this->maxDistanceBetweenScatteredFeatures - $this->minDistanceBetweenScatteredFeatures);
		$l += $this->rand->nextBoundedInt($this->maxDistanceBetweenScatteredFeatures - $this->minDistanceBetweenScatteredFeatures);

		if($i === $k && $j === $l){
			return $this->isStructureBiome($this->world->getBiome($i * 16 + 8, $j * 16 + 8));
		}

		return false;
	}

	private function isStructureBiome($id){
		switch($id){
			case 2:
			case 17:
			case 6:
			case 21:
			case 22:
			case 23:
			case 134:
				return true;
		}
		return false;
	}

	protected function getStructureStart($chunkX, $chunkZ){
		return new ScatteredFeatureStart($this->world, $this->rand, $chunkX, $chunkZ);
	}
}
