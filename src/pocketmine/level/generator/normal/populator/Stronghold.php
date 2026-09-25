<?php

namespace pocketmine\level\generator\normal\populator;

use pocketmine\level\ChunkManager;
use pocketmine\level\generator\normal\object\Stronghold as StrongholdObject;
use pocketmine\level\generator\populator\Populator;
use pocketmine\level\Level;
use pocketmine\utils\Random;

require_once __DIR__ . "/../object/Stronghold.php";

class Stronghold extends Populator{
	const MIN_DISTANCE = 3;
	const MAX_DISTANCE = 32;
	const FOOTPRINT_CHUNK_RADIUS = 5;
	const POPULATION_WINDOW_RADIUS_CHUNKS = 5;
	const SALT = 0x76694565C616765;

	public function populate(ChunkManager $level, $chunkX, $chunkZ, Random $random){
		if(!self::canGenerateAt((int) $level->getSeed(), (int) $chunkX, (int) $chunkZ)){
			return;
		}

		$this->placeStrongholdAtCandidate($level, (int) $chunkX, (int) $chunkZ);
	}

	public function placeStrongholdAtCandidate(ChunkManager $level, int $chunkX, int $chunkZ) : array{
		if(!$this->canPopulateOverworldStructure($level)){
			return [];
		}

		$object = new StrongholdObject();
		$placement = $object->placeStrongholdAtCandidate($level, $chunkX, $chunkZ, self::createPlacementRandom((int) $level->getSeed(), $chunkX, $chunkZ));
		$footprint = self::getStrongholdFootprintChunks((int) $level->getSeed(), $chunkX, $chunkZ);
		$this->markStructureFootprint($level, $footprint);
		$this->processLiveLevelFootprint($level, $footprint);
		return $placement;
	}

	public static function canGenerateAt(int $seed, int $chunkX, int $chunkZ, int $biome = null) : bool{
		$regionX = self::regionCoord($chunkX, self::MAX_DISTANCE);
		$regionZ = self::regionCoord($chunkZ, self::MAX_DISTANCE);
		$random = self::createRegionRandom($seed, $regionX, $regionZ);
		$spread = self::MAX_DISTANCE - self::MIN_DISTANCE;
		return $regionX * self::MAX_DISTANCE + $random->nextBoundedInt($spread) === $chunkX &&
			$regionZ * self::MAX_DISTANCE + $random->nextBoundedInt($spread) === $chunkZ;
	}

	public static function findCandidateInRegion(int $seed, int $regionX, int $regionZ) : array{
		$random = self::createRegionRandom($seed, $regionX, $regionZ);
		$spread = self::MAX_DISTANCE - self::MIN_DISTANCE;
		return [
			"chunkX" => $regionX * self::MAX_DISTANCE + $random->nextBoundedInt($spread),
			"chunkZ" => $regionZ * self::MAX_DISTANCE + $random->nextBoundedInt($spread)
		];
	}

	public static function getStrongholdFootprintChunks(int $seed, int $chunkX, int $chunkZ) : array{
		$chunks = [];
		for($x = $chunkX - self::FOOTPRINT_CHUNK_RADIUS; $x <= $chunkX + self::FOOTPRINT_CHUNK_RADIUS; ++$x){
			for($z = $chunkZ - self::FOOTPRINT_CHUNK_RADIUS; $z <= $chunkZ + self::FOOTPRINT_CHUNK_RADIUS; ++$z){
				$chunks[] = ["chunkX" => $x, "chunkZ" => $z];
			}
		}

		return $chunks;
	}

	public static function getPopulationRadiusForChunk(int $seed, int $chunkX, int $chunkZ) : int{
		$candidate = self::findCandidateInRegion(
			$seed,
			self::regionCoord($chunkX, self::MAX_DISTANCE),
			self::regionCoord($chunkZ, self::MAX_DISTANCE)
		);
		if((int) $candidate["chunkX"] === $chunkX && (int) $candidate["chunkZ"] === $chunkZ){
			return self::POPULATION_WINDOW_RADIUS_CHUNKS;
		}

		return 1;
	}

	private static function createRegionRandom(int $seed, int $regionX, int $regionZ) : Random{
		return new Random(($seed ^ self::SALT) + self::chunkHash($regionX, $regionZ));
	}

	public static function createPlacementRandom(int $seed, int $chunkX, int $chunkZ) : Random{
		$random = new Random($seed);
		$r1 = $random->nextInt();
		$r2 = $random->nextInt();
		$random->setSeed(($chunkX * $r1) ^ ($chunkZ * $r2) ^ $seed);
		return $random;
	}

	private function processLiveLevelFootprint(ChunkManager $level, array $chunks){
		if(!($level instanceof Level)){
			return;
		}

		foreach($chunks as $chunkPos){
			$chunk = $level->getChunk((int) $chunkPos["chunkX"], (int) $chunkPos["chunkZ"], false);
			if($chunk !== null && method_exists($level, "processDeferredStructureContainers")){
				$level->processDeferredStructureContainers($chunk);
			}
		}

		if(method_exists($level, "finalizeStructureFootprintChunks")){
			$level->finalizeStructureFootprintChunks($chunks);
		}
	}

	private function markStructureFootprint(ChunkManager $level, array $chunks){
		if(method_exists($level, "markStructureFootprintChunks")){
			$level->markStructureFootprintChunks($chunks);
		}
	}

	private static function regionCoord(int $chunk, int $spacing) : int{
		return $chunk < 0 ? intdiv($chunk - $spacing + 1, $spacing) : intdiv($chunk, $spacing);
	}

	private static function chunkHash(int $x, int $z) : int{
		return (($x & 0xffffffff) << 32) ^ ($z & 0xffffffff);
	}
}
