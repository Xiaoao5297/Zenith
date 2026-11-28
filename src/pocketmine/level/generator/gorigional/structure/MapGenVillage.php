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
 * 移植自 Java 1.7.10 MapGenVillage：32 区块网格，8 区块间隔。
 */
class MapGenVillage extends MapGenStructure{
	const SPACING = 32;
	const SEPARATION = 8;
	const SIZE = 0;
	const ALLOWED_BIOMES = [1, 2, 35];

	protected function canSpawnStructureAtCoords($chunkX, $chunkZ){
		$originalX = $chunkX;
		$originalZ = $chunkZ;

		/*
		 * Java 对负坐标先减去 spacing-1，再按整数除法向零截断，
		 * 等价于 floorDiv(chunk, 32)。
		 */
		if($chunkX < 0){
			$chunkX -= self::SPACING - 1;
		}
		if($chunkZ < 0){
			$chunkZ -= self::SPACING - 1;
		}

		$gridX = intdiv($chunkX, self::SPACING);
		$gridZ = intdiv($chunkZ, self::SPACING);

		// Java: world.func_72843_D(gridX, gridZ, 10387312)
		$rnd = $this->world->getRandomAt($gridX, $gridZ, 10387312);

		$gridX *= self::SPACING;
		$gridZ *= self::SPACING;
		$gridX += $rnd->nextBoundedInt(self::SPACING - self::SEPARATION);
		$gridZ += $rnd->nextBoundedInt(self::SPACING - self::SEPARATION);

		if($originalX !== $gridX || $originalZ !== $gridZ){
			return false;
		}

		// Java: world.func_72959_q().func_76940_a(x * 16 + 8, z * 16 + 8, 0, list)
		return $this->world->getBiomeManager()->isBiomeAllowed($originalX * 16 + 8, $originalZ * 16 + 8, 0, self::ALLOWED_BIOMES);
	}

	protected function getStructureStart($chunkX, $chunkZ){
		return new VillageStart($this->world, $this->rand, $chunkX, $chunkZ, self::SIZE);
	}
}
