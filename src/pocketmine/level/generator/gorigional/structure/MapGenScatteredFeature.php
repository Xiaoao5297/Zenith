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
 * it under the terms of the GNU General Public License as published by
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

		// Java World.func_72843_D：用网格坐标和 salt 派生随机数。
		$random = $this->world->getRandomAt($k, $l, 14357617);

		$k *= $this->maxDistanceBetweenScatteredFeatures;
		$l *= $this->maxDistanceBetweenScatteredFeatures;

		$k += $random->nextBoundedInt($this->maxDistanceBetweenScatteredFeatures - $this->minDistanceBetweenScatteredFeatures);
		$l += $random->nextBoundedInt($this->maxDistanceBetweenScatteredFeatures - $this->minDistanceBetweenScatteredFeatures);

		if($i === $k && $j === $l){
			return $this->isStructureBiome($this->world->getBiome($i * 16 + 8, $j * 16 + 8));
		}

		return false;
	}

	/**
	 * Java MapGenScatteredFeature.field_75061_e：
	 * Desert(2)、DesertHills(17)、Swampland(6)、Jungle(21)、JungleHills(22)。
	 */
	private function isStructureBiome($id){
		switch($id){
			case 2:
			case 17:
			case 6:
			case 21:
			case 22:
				return true;
		}
		return false;
	}

	protected function getStructureStart($chunkX, $chunkZ){
		return new ScatteredFeatureStart($this->world, $this->rand, $chunkX, $chunkZ);
	}
}
