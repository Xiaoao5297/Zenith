<?php

namespace pocketmine\level\generator\normal\populator;

require_once dirname(__DIR__) . "/object/WoodlandMansion.php";

use pocketmine\block\Block;
use pocketmine\level\ChunkManager;
use pocketmine\level\Level;
use pocketmine\level\generator\biome\Biome;
use pocketmine\level\generator\normal\object\WoodlandMansion as WoodlandMansionObject;
use pocketmine\level\generator\populator\Populator;
use pocketmine\utils\Random;

class WoodlandMansion extends Populator{
	const REGION_SIZE = 80;
	const MIN_DISTANCE = 20;
	const CANDIDATE_SPAN = 60;
	const SALT = 10387319;

	/** @var ChunkManager */
	private $level;
	/** @var array<string, array> */
	private static $regionCandidateCache = [];

	public function populate(ChunkManager $level, $chunkX, $chunkZ, Random $random){
		$this->level = $level;
		$chunk = $level->getChunk((int) $chunkX, (int) $chunkZ);
		$biomeId = $chunk !== null && method_exists($chunk, "getBiomeId") ? $chunk->getBiomeId(7, 7) : Biome::PLAINS;
		if(!self::isValidMansionBiome($biomeId)){
			return;
		}

		$candidate = self::findRegionMansionCandidate(
			(int) $level->getSeed(),
			self::regionCoord((int) $chunkX, self::REGION_SIZE),
			self::regionCoord((int) $chunkZ, self::REGION_SIZE)
		);
		if($candidate["chunkX"] !== (int) $chunkX || $candidate["chunkZ"] !== (int) $chunkZ){
			return;
		}

		$this->placeMansionAtCandidate($level, (int) $chunkX, (int) $chunkZ, $random);
	}

	public function placeMansionAtCandidate(ChunkManager $level, int $chunkX, int $chunkZ, Random $random, bool $forcePlace = false){
		if(!$this->canPopulateOverworldStructure($level)){
			return null;
		}

		$this->level = $level;
		$candidate = self::findRegionMansionCandidate(
			(int) $level->getSeed(),
			self::regionCoord($chunkX, self::REGION_SIZE),
			self::regionCoord($chunkZ, self::REGION_SIZE)
		);
		if($candidate["chunkX"] !== $chunkX || $candidate["chunkZ"] !== $chunkZ){
			return null;
		}

		$chunk = $level->getChunk($chunkX, $chunkZ);
		if($chunk !== null && method_exists($chunk, "getBiomeId") && !self::isValidMansionBiome($chunk->getBiomeId(7, 7))){
			return null;
		}

		$origin = self::getMansionOrigin($level, $chunkX, $chunkZ);
		if($origin === null){
			return null;
		}

		$random->setSeed((int) $level->getSeed() ^ self::chunkHash($chunkX, $chunkZ));
		$expectedPlacement = self::createMansionPlacementFromPieces((int) $origin["x"], (int) $origin["y"], (int) $origin["z"], $random);
		if(!$forcePlace){
			$existing = $this->findExistingMansion($level, $expectedPlacement);
			if($existing !== null){
				return $existing;
			}
		}

		$random->setSeed((int) $level->getSeed() ^ self::chunkHash($chunkX, $chunkZ));
		$object = new \pocketmine\level\generator\normal\object\WoodlandMansion();
		$placement = $object->placeObject($level, (int) $origin["x"], (int) $origin["y"], (int) $origin["z"], $random);
		$footprint = self::getMansionFootprintChunksFromBounds($placement);
		$this->markStructureFootprint($level, $footprint);
		$this->processLiveLevelFootprint($level, $footprint);
		return $placement;
	}

	public static function findRegionMansionCandidate(int $seed, int $regionX, int $regionZ) : array{
		$cacheKey = $seed . ":" . $regionX . ":" . $regionZ;
		if(!isset(self::$regionCandidateCache[$cacheKey])){
			$random = new Random(0);
			$random->setSeed(($seed ^ self::SALT) + self::chunkHash($regionX, $regionZ));
			self::$regionCandidateCache[$cacheKey] = [
				"chunkX" => ($regionX * self::REGION_SIZE) + $random->nextBoundedInt(self::CANDIDATE_SPAN),
				"chunkZ" => ($regionZ * self::REGION_SIZE) + $random->nextBoundedInt(self::CANDIDATE_SPAN),
			];
		}
		return self::$regionCandidateCache[$cacheKey];
	}

	public static function getPopulationRadiusForChunk(int $seed, int $chunkX, int $chunkZ) : int{
		$candidate = self::findRegionMansionCandidate(
			$seed,
			self::regionCoord($chunkX, self::REGION_SIZE),
			self::regionCoord($chunkZ, self::REGION_SIZE)
		);
		if((int) $candidate["chunkX"] !== $chunkX || (int) $candidate["chunkZ"] !== $chunkZ){
			return 1;
		}

		return self::getFootprintPopulationRadius(self::getMansionFootprintChunks($seed, $chunkX, $chunkZ), $chunkX, $chunkZ);
	}

	public static function getMansionFootprintChunks(int $seed, int $chunkX, int $chunkZ) : array{
		$random = new Random(0);
		$random->setSeed($seed ^ self::chunkHash($chunkX, $chunkZ));
		$pieces = WoodlandMansionObject::generatePieces(
			[($chunkX << 4) + 8, 64, ($chunkZ << 4) + 8],
			WoodlandMansionObject::ROTATE_NONE,
			$random
		);
		if(count($pieces) === 0){
			return [["chunkX" => $chunkX, "chunkZ" => $chunkZ]];
		}

		$minX = PHP_INT_MAX;
		$minZ = PHP_INT_MAX;
		$maxX = PHP_INT_MIN;
		$maxZ = PHP_INT_MIN;
		foreach($pieces as $piece){
			$box = $piece["boundingBox"];
			$minX = min($minX, (int) $box[0]);
			$minZ = min($minZ, (int) $box[2]);
			$maxX = max($maxX, (int) $box[3]);
			$maxZ = max($maxZ, (int) $box[5]);
		}

		return self::getMansionFootprintChunksFromBounds([
			"minX" => $minX,
			"minZ" => $minZ,
			"maxX" => $maxX,
			"maxZ" => $maxZ,
		]);
	}

	public static function isValidMansionBiome($biomeId) : bool{
		return (int) $biomeId === Biome::ROOFED_FOREST;
	}

	public static function regionCoord(int $value, int $divisor) : int{
		$result = intdiv($value, $divisor);
		if($value < 0 && ($value % $divisor) !== 0){
			--$result;
		}
		return $result;
	}

	public static function getMansionFootprintChunksFromBounds(array $placement) : array{
		$chunks = [];
		$minChunkX = ((int) $placement["minX"]) >> 4;
		$maxChunkX = ((int) $placement["maxX"]) >> 4;
		$minChunkZ = ((int) $placement["minZ"]) >> 4;
		$maxChunkZ = ((int) $placement["maxZ"]) >> 4;
		for($chunkX = $minChunkX; $chunkX <= $maxChunkX; ++$chunkX){
			for($chunkZ = $minChunkZ; $chunkZ <= $maxChunkZ; ++$chunkZ){
				$chunks[] = ["chunkX" => $chunkX, "chunkZ" => $chunkZ];
			}
		}
		return $chunks;
	}

	private static function createMansionPlacementFromPieces(int $originX, int $originY, int $originZ, Random $random) : array{
		$pieces = WoodlandMansionObject::generatePieces(
			[$originX, $originY, $originZ],
			WoodlandMansionObject::ROTATE_NONE,
			$random
		);
		if(count($pieces) === 0){
			return [
				"originX" => $originX,
				"originY" => $originY,
				"originZ" => $originZ,
				"targetX" => $originX,
				"targetY" => min(126, $originY + 1),
				"targetZ" => $originZ,
				"minX" => $originX,
				"minY" => $originY,
				"minZ" => $originZ,
				"maxX" => $originX,
				"maxY" => $originY,
				"maxZ" => $originZ,
			];
		}

		$minX = PHP_INT_MAX;
		$minY = PHP_INT_MAX;
		$minZ = PHP_INT_MAX;
		$maxX = PHP_INT_MIN;
		$maxY = PHP_INT_MIN;
		$maxZ = PHP_INT_MIN;
		foreach($pieces as $piece){
			$box = $piece["boundingBox"];
			$minX = min($minX, (int) $box[0]);
			$minY = min($minY, (int) $box[1]);
			$minZ = min($minZ, (int) $box[2]);
			$maxX = max($maxX, (int) $box[3]);
			$maxY = max($maxY, (int) $box[4]);
			$maxZ = max($maxZ, (int) $box[5]);
		}

		return [
			"originX" => $originX,
			"originY" => $originY,
			"originZ" => $originZ,
			"targetX" => $originX,
			"targetY" => min(126, $originY + 1),
			"targetZ" => $originZ,
			"minX" => $minX,
			"minY" => $minY,
			"minZ" => $minZ,
			"maxX" => $maxX,
			"maxY" => $maxY,
			"maxZ" => $maxZ,
		];
	}

	public static function getMansionOrigin(ChunkManager $level, int $chunkX, int $chunkZ){
		$startX = ($chunkX << 4) + 8;
		$startZ = ($chunkZ << 4) + 8;
		$minY = 127;
		for($dx = 0; $dx < 5; ++$dx){
			for($dz = 0; $dz < 5; ++$dz){
				$sampleX = $startX + $dx + 7;
				$sampleZ = $startZ + $dz + 7;
				$minY = min($minY, max(self::getSurfaceY($level, $sampleX, $sampleZ), 64));
			}
		}
		if($minY <= 1){
			return null;
		}
		return ["x" => $startX, "y" => $minY, "z" => $startZ];
	}

	private static function getSurfaceY(ChunkManager $level, int $x, int $z) : int{
		if(method_exists($level, "getHeightMap")){
			return (int) $level->getHeightMap($x, $z);
		}
		for($y = 127; $y > 0; --$y){
			$id = $level->getBlockIdAt($x, $y, $z);
			if($id !== Block::AIR && $id !== Block::WATER && $id !== Block::STILL_WATER && $id !== Block::LAVA && $id !== Block::STILL_LAVA && $id !== Block::LEAVES && $id !== Block::LEAVES2){
				return $y;
			}
		}
		return -1;
	}

	private function processLiveLevelFootprint(ChunkManager $level, array $chunks){
		if(!($level instanceof Level) || !method_exists($level, "processDeferredStructureContainers")){
			return;
		}
		foreach($chunks as $chunkPos){
			$chunk = $level->getChunk((int) $chunkPos["chunkX"], (int) $chunkPos["chunkZ"], false);
			if($chunk !== null){
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

	private function findExistingMansion(ChunkManager $level, array $placement){
		$matches = 0;
		for($x = (int) $placement["minX"]; $x <= (int) $placement["maxX"]; $x += 4){
			for($z = (int) $placement["minZ"]; $z <= (int) $placement["maxZ"]; $z += 4){
				for($y = (int) $placement["minY"]; $y <= (int) $placement["maxY"]; $y += 4){
					$id = $level->getBlockIdAt($x, $y, $z);
					if($id === Block::PLANKS || $id === Block::WOOD2 || $id === Block::DARK_OAK_WOOD_STAIRS || $id === Block::COBBLESTONE){
						++$matches;
						if($matches >= 24){
							return $placement;
						}
					}
				}
			}
		}

		return null;
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

	private static function chunkHash(int $x, int $z){
		return PHP_INT_SIZE === 8 ? (($x & 0xffffffff) << 32) | ($z & 0xffffffff) : $x . ":" . $z;
	}
}
