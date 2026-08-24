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

namespace pocketmine\level\generator\normal\populator;

require_once dirname(__DIR__) . "/object/PillagerOutpost.php";

use pocketmine\block\Block;
use pocketmine\level\ChunkManager;
use pocketmine\level\Level;
use pocketmine\level\generator\biome\Biome;
use pocketmine\level\generator\populator\Populator;
use pocketmine\utils\Random;

class PillagerOutpost extends Populator{
	const REGION_SIZE = 32;
	const MIN_DISTANCE = 8;
	const CANDIDATE_SPAN = 24;
	const SALT = 165745296;

	/** @var ChunkManager */
	private $level;
	/** @var array<string, array> */
	private static $regionCandidateCache = [];

	public function populate(ChunkManager $level, $chunkX, $chunkZ, Random $random){
		$this->level = $level;
		$chunk = $level->getChunk((int) $chunkX, (int) $chunkZ);
		if($chunk !== null && method_exists($chunk, "getBiomeId") && !self::isValidOutpostBiome($chunk->getBiomeId(7, 7))){
			return;
		}

		$candidate = self::findRegionOutpostCandidate(
			(int) $level->getSeed(),
			self::regionCoord((int) $chunkX, self::REGION_SIZE),
			self::regionCoord((int) $chunkZ, self::REGION_SIZE)
		);
		if($candidate["chunkX"] !== (int) $chunkX || $candidate["chunkZ"] !== (int) $chunkZ){
			return;
		}

		$origin = $this->getOutpostOrigin($level, (int) $chunkX, (int) $chunkZ);
		if($origin === null){
			return;
		}

		$random->setSeed((int) $level->getSeed() ^ Level::chunkHash((int) $chunkX, (int) $chunkZ));
		$this->placeOutpostAt($level, (int) $origin["x"], (int) $origin["y"], (int) $origin["z"], $random);
	}

	public function placeOutpostAt(ChunkManager $level, int $x, int $y, int $z, Random $random){
		if(!$this->canPopulateOverworldStructure($level)){
			return null;
		}

		$outpost = new \pocketmine\level\generator\normal\object\PillagerOutpost();
		$outpost->placeObject($level, $x, $y, $z, $random);
		$footprint = self::getOutpostFootprintChunks((int) $level->getSeed(), $x >> 4, $z >> 4);
		$this->markStructureFootprint($level, $footprint);
		$spawnedEntityIds = $this->processLiveLevelFootprint($level, $footprint);
		return ["targetX" => $x + 8, "targetY" => $y + 13, "targetZ" => $z + 8, "originX" => $x, "originY" => $y, "originZ" => $z, "spawnedEntityIds" => $spawnedEntityIds];
	}

	public function placeOutpostAtCandidate(ChunkManager $level, int $chunkX, int $chunkZ, Random $random){
		if(!$this->canPopulateOverworldStructure($level)){
			return null;
		}

		$this->level = $level;
		$candidate = self::findRegionOutpostCandidate(
			(int) $level->getSeed(),
			self::regionCoord($chunkX, self::REGION_SIZE),
			self::regionCoord($chunkZ, self::REGION_SIZE)
		);
		if((int) $candidate["chunkX"] !== $chunkX || (int) $candidate["chunkZ"] !== $chunkZ){
			return null;
		}

		$origin = $this->getOutpostOrigin($level, $chunkX, $chunkZ);
		if($origin === null){
			return null;
		}

		$existing = $this->findExistingOutpost($level, (int) $origin["x"], (int) $origin["y"], (int) $origin["z"]);
		if($existing !== null){
			return $existing;
		}

		$random->setSeed((int) $level->getSeed() ^ Level::chunkHash($chunkX, $chunkZ));
		return $this->placeOutpostAt($level, (int) $origin["x"], (int) $origin["y"], (int) $origin["z"], $random);
	}

	public static function findRegionOutpostCandidate(int $seed, int $regionX, int $regionZ) : array{
		$cacheKey = $seed . ":" . $regionX . ":" . $regionZ;
		if(!isset(self::$regionCandidateCache[$cacheKey])){
			$random = new Random(0);
			$random->setSeed(($seed ^ self::SALT) + Level::chunkHash($regionX, $regionZ));
			self::$regionCandidateCache[$cacheKey] = [
				"chunkX" => ($regionX * self::REGION_SIZE) + $random->nextBoundedInt(self::CANDIDATE_SPAN),
				"chunkZ" => ($regionZ * self::REGION_SIZE) + $random->nextBoundedInt(self::CANDIDATE_SPAN),
			];
		}
		return self::$regionCandidateCache[$cacheKey];
	}

	public static function getOutpostFootprintChunks(int $seed, int $chunkX, int $chunkZ) : array{
		$chunks = [];
		for($dx = -1; $dx <= 1; ++$dx){
			for($dz = -1; $dz <= 1; ++$dz){
				$chunks[] = ["chunkX" => $chunkX + $dx, "chunkZ" => $chunkZ + $dz];
			}
		}

		return $chunks;
	}

	public static function getPopulationRadiusForChunk(int $seed, int $chunkX, int $chunkZ) : int{
		$candidate = self::findRegionOutpostCandidate(
			$seed,
			self::regionCoord($chunkX, self::REGION_SIZE),
			self::regionCoord($chunkZ, self::REGION_SIZE)
		);
		if((int) $candidate["chunkX"] !== $chunkX || (int) $candidate["chunkZ"] !== $chunkZ){
			return 1;
		}

		return self::getFootprintPopulationRadius(self::getOutpostFootprintChunks($seed, $chunkX, $chunkZ), $chunkX, $chunkZ);
	}

	public static function getOutpostOrigin(ChunkManager $level, int $chunkX, int $chunkZ){
		$probeX = $chunkX << 4;
		$probeZ = $chunkZ << 4;
		$chunk = $level->getChunk($chunkX, $chunkZ);
		if($chunk !== null && method_exists($chunk, "getHeightMap")){
			return ["x" => $probeX, "y" => (int) $chunk->getHeightMap(0, 0), "z" => $probeZ];
		}
		if(method_exists($level, "getHeightMap")){
			return ["x" => $probeX, "y" => (int) $level->getHeightMap($probeX, $probeZ), "z" => $probeZ];
		}
		for($y = 127; $y > 0; --$y){
			$id = $level->getBlockIdAt($probeX, $y, $probeZ);
			if($id !== Block::AIR && $id !== Block::WATER && $id !== Block::STILL_WATER && $id !== Block::LAVA && $id !== Block::STILL_LAVA){
				return ["x" => $probeX, "y" => $y, "z" => $probeZ];
			}
		}

		return null;
	}

	public static function isValidOutpostBiome($biomeId) : bool{
		return in_array((int) $biomeId, [
			Biome::PLAINS,
			Biome::DESERT,
			Biome::SAVANNA,
			Biome::TAIGA,
			Biome::ICE_PLAINS,
			Biome::COLD_TAIGA,
		], true);
	}

	public static function regionCoord(int $value, int $divisor) : int{
		$result = intdiv($value, $divisor);
		if($value < 0 && ($value % $divisor) !== 0){
			--$result;
		}
		return $result;
	}

	private function getSurfaceY($x, $z){
		if(method_exists($this->level, "getHeightMap")){
			return (int) $this->level->getHeightMap((int) $x, (int) $z);
		}
		for($y = 127; $y > 0; --$y){
			$id = $this->level->getBlockIdAt((int) $x, $y, (int) $z);
			if($id !== Block::AIR && $id !== Block::WATER && $id !== Block::STILL_WATER){
				return $y;
			}
		}
		return -1;
	}

	private function processLiveLevelFootprint(ChunkManager $level, array $chunks) : array{
		if(!($level instanceof Level) || !method_exists($level, "processDeferredStructureContainers")){
			return [];
		}

		$before = $this->collectChunkEntityIds($level, $chunks);
		foreach($chunks as $chunkPos){
			$chunk = $level->getChunk((int) $chunkPos["chunkX"], (int) $chunkPos["chunkZ"], false);
			if($chunk !== null){
				$level->processDeferredStructureContainers($chunk);
			}
		}

		if(method_exists($level, "finalizeStructureFootprintChunks")){
			$level->finalizeStructureFootprintChunks($chunks);
		}

		$after = $this->collectChunkEntityIds($level, $chunks);
		return array_values(array_diff($after, $before));
	}

	private function collectChunkEntityIds(ChunkManager $level, array $chunks) : array{
		$ids = [];
		foreach($chunks as $chunkPos){
			$chunk = $level->getChunk((int) $chunkPos["chunkX"], (int) $chunkPos["chunkZ"], false);
			if($chunk === null || !method_exists($chunk, "getEntities")){
				continue;
			}

			foreach($chunk->getEntities() as $entity){
				if(method_exists($entity, "getId")){
					$ids[] = (int) $entity->getId();
				}
			}
		}

		return array_values(array_unique($ids));
	}

	private function findExistingOutpost(ChunkManager $level, int $originX, int $originY, int $originZ){
		if($level->getBlockIdAt($originX + 9, $originY + 14, $originZ + 10) !== Block::CHEST){
			return null;
		}

		return [
			"targetX" => $originX + 8,
			"targetY" => $originY + 13,
			"targetZ" => $originZ + 8,
			"originX" => $originX,
			"originY" => $originY,
			"originZ" => $originZ,
			"spawnedEntityIds" => [],
		];
	}

	private function markStructureFootprint(ChunkManager $level, array $chunks){
		if(method_exists($level, "markStructureFootprintChunks")){
			$level->markStructureFootprintChunks($chunks);
		}
	}

	private static function getFootprintPopulationRadius(array $chunks, int $centerChunkX, int $centerChunkZ) : int{
		$radius = 1;
		foreach($chunks as $chunkPos){
			$radius = max(
				$radius,
				abs((int) $chunkPos["chunkX"] - $centerChunkX),
				abs((int) $chunkPos["chunkZ"] - $centerChunkZ)
			);
		}
		return $radius;
	}
}
