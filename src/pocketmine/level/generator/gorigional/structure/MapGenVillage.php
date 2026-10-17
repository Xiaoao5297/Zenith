<?php

namespace pocketmine\level\generator\gorigional\structure;

use pocketmine\level\generator\gorigional\JavaRandom;
use pocketmine\level\generator\gorigional\McpeRandom;

/**
 * MCPE 0.14.3 VillageFeature：40 区块网格，28 区块有效范围。
 */
class MapGenVillage extends MapGenStructure{
	const SPACING = 40;
	const SEPARATION = 28;
	const ALLOWED_BIOMES = [1, 2, 35];

	/** @var JavaRandom|null 与 MCPE 一样，村庄展开使用村庄自己的种子流 */
	private $structureRand = null;

	protected function canSpawnStructureAtCoords($chunkX, $chunkZ){
		$originalX = $chunkX;
		$originalZ = $chunkZ;

		/*
		 * MCPE 对负坐标先减去 spacing-1，再按整数除法截断，
		 * 等价于 floorDiv(chunk, 40)。
		 */
		if($chunkX < 0){
			$chunkX -= self::SPACING - 1;
		}
		if($chunkZ < 0){
			$chunkZ -= self::SPACING - 1;
		}

		$seed = (($chunkX * 0x9939F508) + ($chunkZ * 0xF1565BD5) + $this->worldSeed + 0x9E7F70) & 0xFFFFFFFF;
		$rnd = new McpeRandom($seed);
		$r1 = $rnd->nextInt();
		$r2 = $rnd->nextInt();

		// MCPE 在判定成功后，直接把同一个 Random 继续传给 VillageStart。
		// VillageStart 及其部件仍使用 JavaRandom 接口，这里用同一个村庄种子
		// 并跳过判定已消费的两个值，保证布局由村庄种子决定。
		$this->structureRand = new JavaRandom($seed);
		$this->structureRand->nextInt();
		$this->structureRand->nextInt();

		// 反编译里是 C++ 的整数除法（向零截断）：负数已经先减 39，
		// 因此这里不能再用 floorDiv，否则 -59/40 会得到 -80 而不是 -40。
		$gridX = intdiv($chunkX, self::SPACING) * self::SPACING;
		$gridZ = intdiv($chunkZ, self::SPACING) * self::SPACING;

		if(($r1 % self::SEPARATION) + $gridX !== $originalX){
			return false;
		}
		if(($r2 % self::SEPARATION) + $gridZ !== $originalZ){
			return false;
		}

		return in_array($this->world->getBiome(($originalX << 4) + 8, ($originalZ << 4) + 8), self::ALLOWED_BIOMES, true);
	}

	protected function getStructureStart($chunkX, $chunkZ){
		return new VillageStart($this->world, $this->structureRand !== null ? $this->structureRand : $this->rand, $chunkX, $chunkZ);
	}
}
