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

use pocketmine\level\generator\gorigional\Int64;
use pocketmine\level\generator\gorigional\JavaRandom;

/**
 * Java 1.7.10 MapGenVillage：32 区块网格，8 区块间隔，种子来自 World.setRandomSeed。
 */
class MapGenVillage extends MapGenStructure{
	const SPACING = 32;
	const SEPARATION = 8;
	const ALLOWED_BIOMES = [1, 2, 35];

	/** @var JavaRandom|null 与 Java 一样，村庄展开沿用 MapGenBase 为本原点播种的随机流 */
	private $structureRand = null;

	protected function canSpawnStructureAtCoords($chunkX, $chunkZ){
		$originalX = $chunkX;
		$originalZ = $chunkZ;

		/*
		 * Java 1.7.10 MapGenVillage.func_75047_a：
		 * 负坐标先减 spacing-1，再按整数除法截断（等价 floorDiv）。
		 */
		if($chunkX < 0){
			$chunkX -= self::SPACING - 1;
		}
		if($chunkZ < 0){
			$chunkZ -= self::SPACING - 1;
		}

		$gridIndexX = intdiv($chunkX, self::SPACING);
		$gridIndexZ = intdiv($chunkZ, self::SPACING);

		// World.func_72843_D(i, j, 10387312)：i * 341873128712 + j * 132897987541 + randomSeed + 10387312。
		// 普通世界的 WorldInfo.randomSeed 为 0。
		$seed = Int64::add(Int64::add(Int64::mul($gridIndexX, 341873128712), Int64::mul($gridIndexZ, 132897987541)), 10387312);
		$rnd = new JavaRandom($seed);

		$offsetX = $rnd->nextBoundedInt(self::SPACING - self::SEPARATION);
		$offsetZ = $rnd->nextBoundedInt(self::SPACING - self::SEPARATION);

		$gridX = $gridIndexX * self::SPACING + $offsetX;
		$gridZ = $gridIndexZ * self::SPACING + $offsetZ;

		if($gridX !== $originalX || $gridZ !== $originalZ){
			return false;
		}

		return in_array($this->world->getBiome(($originalX << 4) + 8, ($originalZ << 4) + 8), self::ALLOWED_BIOMES, true);
	}

	protected function getStructureStart($chunkX, $chunkZ){
		return new VillageStart($this->world, $this->structureRand !== null ? $this->structureRand : $this->rand, $chunkX, $chunkZ);
	}
}
