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

use pocketmine\level\generator\gorigional\JavaRandom;

/**
 * 移植自 Java 1.7.10 MapGenStronghold：每个世界默认 3 个要塞。
 *
 * 选址逻辑与 Java 完全一致：
 * - 使用 worldSeed 构造 java.util.Random
 * - 初始角度 = nextDouble() * 2π
 * - 第 k 个要塞距离 = (1.25 * k + nextDouble()) * 32 * k
 * - 在半径 112 格内用 WorldChunkManager.findBiomePosition 找合适群系
 * - 每找到一个，角度增加 2π * k / spread
 */
class MapGenStronghold extends MapGenStructure{
	/** @var int[][]|null */
	private $positions = null;

	/**
	 * Java: BiomeGenBase.field_76748_D > 0.0F。
	 * 排除海洋、河流、沼泽、沙滩、蘑菇岛岸等 rootHeight <= 0 的群系。
	 */
	const LAND_BIOMES = [
		1, 2, 3, 4, 5,
		12, 13, 14,
		17, 18, 19, 20, 21, 22, 23,
		25,
		27, 28, 29, 30, 31, 32, 33, 34,
		35, 36, 37, 38, 39,
		// GenLayerHills / GenLayerRareBiome 可能输出的 +128 变种（rootHeight > 0）。
		129, 130, 131, 132, 133,
		140, 141, 142,
		145, 146, 147, 148, 149, 150, 151,
		153,
		155, 156, 157, 158, 159, 160, 161, 162,
		163, 164, 165, 166, 167
	];

	protected function canSpawnStructureAtCoords($chunkX, $chunkZ){
		$this->buildPositions();
		foreach($this->positions as $pos){
			if($pos[0] === $chunkX && $pos[1] === $chunkZ){
				return true;
			}
		}
		return false;
	}

	protected function getStructureStart($chunkX, $chunkZ){
		// Java MapGenStronghold.func_75049_b：一直重建 Start，直到拥有传送门房间。
		// 10 次都失败时 Java 返回 null（该要塞不生成）。
		for($attempt = 0; $attempt < 10; $attempt++){
			$start = new StrongholdStart($this->world, $this->rand, $chunkX, $chunkZ);
			if($start->isValid() && $start->hasPortalRoom()){
				return $start;
			}
		}
		return null;
	}

	private function buildPositions(){
		if($this->positions !== null){
			return;
		}
		$this->positions = [];

		$rnd = new JavaRandom($this->worldSeed);
		$angle = $rnd->nextDouble() * M_PI * 2.0;
		$distance = 32.0;
		$spread = 3;
		$k = 1;

		for($i = 0; $i < 3; $i++){
			$d = (1.25 * $k + $rnd->nextDouble()) * $distance * $k;
			$centerX = self::javaRound(cos($angle) * $d);
			$centerZ = self::javaRound(sin($angle) * $d);

			$pos = $this->world->findBiomePosition(($centerX << 4) + 8, ($centerZ << 4) + 8, 112, self::LAND_BIOMES, $rnd);
			if($pos !== null){
				$centerX = $pos[0] >> 4;
				$centerZ = $pos[2] >> 4;
			}

			$this->positions[] = [$centerX, $centerZ];

			$angle += 2.0 * M_PI * $k / $spread;
			if($i === $spread){
				$k += 2 + $rnd->nextBoundedInt(5);
				$spread += 1 + $rnd->nextBoundedInt(2);
			}
		}
	}

	/**
	 * Java Math.round(double)：floor(x + 0.5)，不是 PHP round() 的远离零舍入。
	 */
	private static function javaRound($value){
		return (int) floor($value + 0.5);
	}
}
