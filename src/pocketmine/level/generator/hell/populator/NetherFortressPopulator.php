<?php

/*
 * ██╗   ██╗    ██████╗ ██████╗ ██████╗ ███████╗
 * ██║   ██║   ██╔════╝██╔═══██╗██╔══██╗██╔════╝
 * ██║   ██║   ██║     ██║   ██║██████╔╝█████╗
 * ██║   ██║   ██║     ██║   ██║██╔══██╗██╔══╝
 * ╚██████╔╝██╗╚██████╗╚██████╔╝██║  ██║███████╗
 *  ╚═════╝ ╚═╝ ╚═════╝ ╚═════╝ ╚═╝  ╚═╝╚══════╝
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * @Author: U core
 *
 * @Links:
 *  > LY Core
 *  > LY Core Project
*/

namespace pocketmine\level\generator\hell\populator;

require_once __DIR__ . "/../object/NetherFortressPieces.php";

use pocketmine\level\ChunkManager;
use pocketmine\level\Level;
use pocketmine\level\generator\hell\object\PmNetherBoundingBox;
use pocketmine\level\generator\hell\object\PmNetherFortressStart;
use pocketmine\level\generator\populator\Populator;
use pocketmine\utils\Random;

class NetherFortressPopulator extends Populator{

	const REGION_SIZE = 16;
	const INNER_OFFSET = 4;
	const INNER_SPAN = 8;
	const SALT = 0x51d8e999;
	const ACTIVE_RADIUS_CHUNKS = 3;
	const SPAWNER_MARKER_BLAZE = 4;

	public function populate(ChunkManager $level, $chunkX, $chunkZ, Random $random){
		if(!$this->canPopulateNetherStructure($level)){
			return;
		}

		$seed = $level->getSeed();
		$currentRegionX = self::floorDiv((int) $chunkX, self::REGION_SIZE);
		$currentRegionZ = self::floorDiv((int) $chunkZ, self::REGION_SIZE);

		for($regionX = $currentRegionX - 1; $regionX <= $currentRegionX + 1; ++$regionX){
			for($regionZ = $currentRegionZ - 1; $regionZ <= $currentRegionZ + 1; ++$regionZ){
				$candidate = self::getFortressCandidate($seed, $regionX, $regionZ);
				if($candidate === null){
					continue;
				}
				if(abs($chunkX - $candidate["chunkX"]) > self::ACTIVE_RADIUS_CHUNKS || abs($chunkZ - $candidate["chunkZ"]) > self::ACTIVE_RADIUS_CHUNKS){
					continue;
				}

				$start = new PmNetherFortressStart($level, $candidate["chunkX"], $candidate["chunkZ"]);
				if(!$start->isValid() || !$start->intersectsChunk((int) $chunkX, (int) $chunkZ)){
					continue;
				}

				$chunkRandom = self::createChunkRandom($seed, (int) $chunkX, (int) $chunkZ);
				$start->postProcess($level, $chunkRandom, new PmNetherBoundingBox($chunkX << 4, 1, $chunkZ << 4, ($chunkX << 4) + 15, 127, ($chunkZ << 4) + 15), (int) $chunkX, (int) $chunkZ);
			}
		}
	}

	public static function getFortressCandidate(int $seed, int $regionX, int $regionZ){
		$random = new Random(0);
		$random->setSeed($regionX ^ ($regionZ << 4) ^ $seed);
		$random->nextInt();
		$matchesSalt = $random->nextBoundedInt(3) === (self::SALT & 3);
		$chunkX = ($regionX << 4) + self::INNER_OFFSET + $random->nextBoundedInt(self::INNER_SPAN);
		$chunkZ = ($regionZ << 4) + self::INNER_OFFSET + $random->nextBoundedInt(self::INNER_SPAN);
		if(!$matchesSalt){
			return null;
		}

		return [
			"chunkX" => $chunkX,
			"chunkZ" => $chunkZ,
			"centerX" => ($chunkX << 4) + 9,
			"centerZ" => ($chunkZ << 4) + 9,
		];
	}

	public static function createChunkRandom(int $seed, int $chunkX, int $chunkZ) : Random{
		$random = new Random($seed);
		$r1 = $random->nextInt();
		$r2 = $random->nextInt();

		return new Random(($chunkX * $r1) ^ ($chunkZ * $r2) ^ $seed);
	}

	public static function forceGenerateFortress(ChunkManager $level, int $chunkX, int $chunkZ, int $radius = 6){
		if(method_exists($level, "getDimension") && $level->getDimension() !== Level::DIMENSION_NETHER){
			return;
		}

		$start = new PmNetherFortressStart($level, $chunkX, $chunkZ);
		if(!$start->isValid()){
			return;
		}

		$boundingBox = $start->getBoundingBox();
		if(!$boundingBox instanceof PmNetherBoundingBox){
			return;
		}

		$minChunkX = max($chunkX - $radius, $boundingBox->x0 >> 4);
		$maxChunkX = min($chunkX + $radius, $boundingBox->x1 >> 4);
		$minChunkZ = max($chunkZ - $radius, $boundingBox->z0 >> 4);
		$maxChunkZ = min($chunkZ + $radius, $boundingBox->z1 >> 4);
		$seed = $level->getSeed();

		for($targetChunkX = $minChunkX; $targetChunkX <= $maxChunkX; ++$targetChunkX){
			for($targetChunkZ = $minChunkZ; $targetChunkZ <= $maxChunkZ; ++$targetChunkZ){
				$random = self::createChunkRandom($seed, $targetChunkX, $targetChunkZ);
				$start->postProcess($level, $random, new PmNetherBoundingBox($targetChunkX << 4, 1, $targetChunkZ << 4, ($targetChunkX << 4) + 15, 127, ($targetChunkZ << 4) + 15), $targetChunkX, $targetChunkZ);
			}
		}
	}

	public static function floorDiv(int $value, int $divisor) : int{
		$result = intdiv($value, $divisor);
		if($value < 0 && ($value % $divisor) !== 0){
			--$result;
		}

		return $result;
	}
}
