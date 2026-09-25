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

require_once __DIR__ . "/../object/VillageTemplates.php";
require_once __DIR__ . "/../object/PnxVillageStructure.php";

use pocketmine\block\Block;
use pocketmine\level\ChunkManager;
use pocketmine\level\generator\biome\Biome;
use pocketmine\level\generator\biome\BiomeSelector;
use pocketmine\level\generator\normal\Normal;
use pocketmine\level\generator\normal\object\PmVillageBookHouse;
use pocketmine\level\generator\normal\object\PmVillageDoubleFarmland;
use pocketmine\level\generator\normal\object\PmVillageFarmland;
use pocketmine\level\generator\normal\object\PmVillageLightPost;
use pocketmine\level\generator\normal\object\PmVillagePigHouse;
use pocketmine\level\generator\normal\object\PmVillagePiece;
use pocketmine\level\generator\normal\object\PmVillageSimpleHouse;
use pocketmine\level\generator\normal\object\PmVillageSmallHut;
use pocketmine\level\generator\normal\object\PmVillageSmallTemple;
use pocketmine\level\generator\normal\object\PmVillageSmithy;
use pocketmine\level\generator\normal\object\PmVillageTwoRoomHouse;
use pocketmine\level\generator\normal\object\PmVillageWell;
use pocketmine\level\generator\normal\object\PnxVillagePlacer;
use pocketmine\level\generator\populator\Populator;
use pocketmine\utils\Random;

class VillagePopulator extends Populator{

	const MARKER_VILLAGE_CENTER = 0x7a11;
	const MARKER_VILLAGER = 0x7a12;
	const MARKER_IRON_GOLEM = 0x7a13;
	const REGION_SIZE = 34;
	const MIN_DISTANCE = 8;
	const CANDIDATE_SPAN = 26;
	const SALT = 10387312;
	const MIN_VILLAGE_BIOME_SAMPLES = 21;
	const TERRAFORM_RADIUS = 56;
	const TERRAFORM_CLEARANCE = 16;
	const MAX_DISTANCE = 112;
	const POPULATION_WINDOW_RADIUS_CHUNKS = 4;
	const MAX_GEN_DEPTH = 3;
	const DETECT_TERRAFORM_REQUIRED_SCORE = 72;
	const DETECT_TERRAFORM_REQUIRED_MATCHES = 14;
	const DETECT_WELL_REQUIRED_SCORE = 260;
	const DETECT_ROAD_REQUIRED_SCORE = 420;
	const DETECT_ROAD_REQUIRED_DIRECTIONS = 4;
	const DETECT_FEATURE_REQUIRED_SCORE = 90;
	const DETECT_FEATURE_REQUIRED_TYPES = 3;

	/** @var BiomeSelector[] */
	private static $biomeSelectorCache = [];
	/** @var array<string, array|null> */
	private static $regionCandidateCache = [];

	/** @var ChunkManager */
	private $level;
	/** @var array<string, int> */
	private $surfaceYCache = [];

	public function populate(ChunkManager $level, $chunkX, $chunkZ, Random $random){
		$this->placeVillageAtCandidate($level, (int) $chunkX, (int) $chunkZ, $random);
	}

	private function generateVillage(int $originX, int $baseY, int $originZ, Random $random, string $villageType = "plains") : bool{
		$placer = new PnxVillagePlacer();
		return $placer->place(
			$this->level,
			$originX - 2,
			$baseY,
			$originZ - 2,
			$villageType,
			$random,
			self::MARKER_VILLAGER,
			self::MARKER_IRON_GOLEM,
			self::POPULATION_WINDOW_RADIUS_CHUNKS
		);
	}

	public static function findRegionVillageCandidate(int $seed, int $regionX, int $regionZ){
		$cacheKey = $seed . ":" . $regionX . ":" . $regionZ;
		if(!array_key_exists($cacheKey, self::$regionCandidateCache)){
			$populator = new self();
			self::$regionCandidateCache[$cacheKey] = $populator->computeRegionVillageCandidate($seed, $regionX, $regionZ);
		}

		return self::$regionCandidateCache[$cacheKey];
	}

	public function placeVillageAtCandidate(ChunkManager $level, int $chunkX, int $chunkZ, Random $random){
		if(!$this->canPopulateOverworldStructure($level)){
			return null;
		}

		$this->level = $level;
		$this->resetSurfaceYCache();
		$candidate = $this->getRegionVillageCandidateForChunk($chunkX, $chunkZ);
		if($candidate === null || (int) $candidate["chunkX"] !== $chunkX || (int) $candidate["chunkZ"] !== $chunkZ){
			return null;
		}

		$centerX = (int) $candidate["centerX"];
		$centerZ = (int) $candidate["centerZ"];
		$targetCenterX = $centerX + 3;
		$targetCenterZ = $centerZ + 3;

		$markedCenter = $this->findMarkedVillageCenter($level, $targetCenterX, $targetCenterZ, 4);
		if($markedCenter !== null || $this->isGeneratedVillageStructure($level, $targetCenterX, $targetCenterZ, self::TERRAFORM_RADIUS)){
			$baseY = $markedCenter !== null && isset($markedCenter["y"]) ? (int) $markedCenter["y"] : $this->findGeneratedWellBaseY($level, $centerX, $centerZ);
			if($baseY < 1){
				$baseY = $this->getVillageTerrainBaseY($level, $targetCenterX, $targetCenterZ);
			}
			return $this->getSafeVillagePlacement($targetCenterX, $baseY, $targetCenterZ);
		}

		$baseY = $this->getVillageTerrainBaseY($level, $targetCenterX, $targetCenterZ);
		if($baseY < 1){
			return null;
		}
		$isDesertVillage = $this->isDesertVillage($candidate);
		$villageType = $this->getVillageTypeForBiome((int) $candidate["biomeId"]);

		$this->terraformVillageArea($targetCenterX, $baseY, $targetCenterZ, $villageType);
		if(!$this->generateVillage($centerX, $baseY, $centerZ, $random, $villageType)){
			return null;
		}
		$this->applyVillageBiome($targetCenterX, $targetCenterZ);
		$this->storeVillageMarker($targetCenterX, $baseY, $targetCenterZ);
		$this->storeVillageMobMarkers($targetCenterX, $baseY, $targetCenterZ);
		$footprint = self::getVillageFootprintChunks($level->getSeed(), $chunkX, $chunkZ);
		$this->markStructureFootprint($level, $footprint);
		$this->processLiveLevelFootprint($level, $footprint);

		return $this->getSafeVillagePlacement($targetCenterX, $baseY, $targetCenterZ);
	}

	private function getVillageTerrainBaseY(ChunkManager $level, int $centerX, int $centerZ) : int{
		if($this->level !== $level){
			$this->resetSurfaceYCache();
		}
		$this->level = $level;
		$samples = [];
		for($dx = -16; $dx <= 16; $dx += 8){
			for($dz = -16; $dz <= 16; $dz += 8){
				$sample = $this->getSurfaceY($centerX + $dx, $centerZ + $dz);
				if($sample >= 1){
					$samples[] = $sample;
				}
			}
		}

		if(count($samples) === 0){
			return $this->getSurfaceY($centerX, $centerZ);
		}

		sort($samples, SORT_NUMERIC);
		$middle = (int) floor(count($samples) / 2);
		$baseY = (int) $samples[$middle];
		if((count($samples) & 1) === 0){
			$baseY = (int) floor(($samples[$middle - 1] + $samples[$middle]) / 2);
		}

		return $baseY;
	}

	private function resetSurfaceYCache(){
		$this->surfaceYCache = [];
	}

	public static function getVillageFootprintChunks(int $seed, int $chunkX, int $chunkZ) : array{
		$chunks = [];
		for($x = $chunkX - self::POPULATION_WINDOW_RADIUS_CHUNKS; $x <= $chunkX + self::POPULATION_WINDOW_RADIUS_CHUNKS; ++$x){
			for($z = $chunkZ - self::POPULATION_WINDOW_RADIUS_CHUNKS; $z <= $chunkZ + self::POPULATION_WINDOW_RADIUS_CHUNKS; ++$z){
				$chunks[] = ["chunkX" => $x, "chunkZ" => $z];
			}
		}

		return $chunks;
	}

	public static function getPopulationRadiusForChunk(int $seed, int $chunkX, int $chunkZ) : int{
		$candidate = self::findRegionVillageCandidate(
			$seed,
			self::regionCoord($chunkX, self::REGION_SIZE),
			self::regionCoord($chunkZ, self::REGION_SIZE)
		);
		if(
			is_array($candidate) &&
			(int) $candidate["chunkX"] === $chunkX &&
			(int) $candidate["chunkZ"] === $chunkZ
		){
			return self::POPULATION_WINDOW_RADIUS_CHUNKS;
		}

		return 1;
	}

	public static function findGeneratedVillageCenter(ChunkManager $level, int $x, int $z, int $radius){
		$populator = new self();
		$marked = $populator->findMarkedVillageCenter($level, $x, $z, $radius);
		if($marked !== null && $populator->isGeneratedVillageStructure($level, $marked["x"], $marked["z"], self::TERRAFORM_RADIUS)){
			return $marked;
		}

		$chunkX = $x >> 4;
		$chunkZ = $z >> 4;
		$regionX = self::regionCoord($chunkX, self::REGION_SIZE);
		$regionZ = self::regionCoord($chunkZ, self::REGION_SIZE);
		$radiusSq = $radius * $radius;
		$best = null;
		$bestDistanceSq = PHP_INT_MAX;

		for($rx = $regionX - 1; $rx <= $regionX + 1; ++$rx){
			for($rz = $regionZ - 1; $rz <= $regionZ + 1; ++$rz){
				$candidate = self::findRegionVillageCandidate($level->getSeed(), $rx, $rz);
				if(!is_array($candidate)){
					continue;
				}

				$centerX = ((int) $candidate["centerX"]) + 3;
				$centerZ = ((int) $candidate["centerZ"]) + 3;
				$dx = $x - $centerX;
				$dz = $z - $centerZ;
				$distanceSq = ($dx * $dx + $dz * $dz);
				if($distanceSq > $radiusSq || $distanceSq >= $bestDistanceSq){
					continue;
				}
				if(!$populator->isGeneratedVillageStructure($level, $centerX, $centerZ, self::TERRAFORM_RADIUS)){
					continue;
				}

				$bestDistanceSq = $distanceSq;
				$best = ["x" => $centerX, "z" => $centerZ];
			}
		}

		return $best;
	}

	public static function isGeneratedVillageCenter(ChunkManager $level, int $centerX, int $centerZ) : bool{
		$populator = new self();
		return $populator->isGeneratedVillageStructure($level, $centerX, $centerZ, self::TERRAFORM_RADIUS);
	}

	private function getRegionVillageCandidateForChunk(int $chunkX, int $chunkZ){
		return self::findRegionVillageCandidate(
			$this->level->getSeed(),
			self::regionCoord($chunkX, self::REGION_SIZE),
			self::regionCoord($chunkZ, self::REGION_SIZE)
		);
	}

	private function computeRegionVillageCandidate(int $seed, int $regionX, int $regionZ){
		$random = new Random(0);
		$random->setSeed(($seed ^ self::SALT) + \pocketmine\level\Level::chunkHash($regionX, $regionZ));
		$chunkX = ($regionX * self::REGION_SIZE) + $random->nextBoundedInt(self::CANDIDATE_SPAN);
		$chunkZ = ($regionZ * self::REGION_SIZE) + $random->nextBoundedInt(self::CANDIDATE_SPAN);
		$biomeId = $this->getVillageBiomeIdForChunk($seed, $chunkX, $chunkZ);
		if(!$this->isVillageBiome($biomeId)){
			return null;
		}

		return [
			"chunkX" => $chunkX,
			"chunkZ" => $chunkZ,
			"centerX" => ($chunkX << 4) + 2,
			"centerZ" => ($chunkZ << 4) + 2,
			"biomeCheckX" => ($chunkX << 4) + 8,
			"biomeCheckZ" => ($chunkZ << 4) + 8,
			"biomeId" => $biomeId,
		];
	}

	private function getVillageChunkScore(int $seed, int $chunkX, int $chunkZ) : int{
		$selector = $this->getBiomeSelector($seed);
		$centerX = ($chunkX << 4) + 8;
		$centerZ = ($chunkZ << 4) + 8;
		$centerBiomeId = $selector->pickBiome($centerX, $centerZ)->getId();
		if(!$this->isVillageBiome($centerBiomeId)){
			return 0;
		}

		$score = 0;
		for($sx = -2; $sx <= 2; ++$sx){
			for($sz = -2; $sz <= 2; ++$sz){
				if($selector->pickBiome($centerX + ($sx * 16), $centerZ + ($sz * 16))->getId() === $centerBiomeId){
					++$score;
				}
			}
		}

		return $score >= self::MIN_VILLAGE_BIOME_SAMPLES ? $score : 0;
	}

	private function getVillageBiomeIdForChunk(int $seed, int $chunkX, int $chunkZ) : int{
		return $this->getBiomeSelector($seed)->pickBiome(($chunkX << 4) + 8, ($chunkZ << 4) + 8)->getId();
	}

	private function isVillageBiome(int $biomeId) : bool{
		return $biomeId === Biome::PLAINS ||
			$biomeId === Biome::DESERT ||
			$biomeId === Biome::SAVANNA ||
			$biomeId === Biome::TAIGA ||
			$biomeId === Biome::ICE_PLAINS ||
			$biomeId === Biome::COLD_TAIGA;
	}

	private function isDesertVillage(array $candidate) : bool{
		return isset($candidate["biomeId"]) && (int) $candidate["biomeId"] === Biome::DESERT;
	}

	private function getVillageTypeForBiome(int $biomeId) : string{
		switch($biomeId){
			case Biome::DESERT:
				return "desert";
			case Biome::SAVANNA:
				return "savanna";
			case Biome::TAIGA:
				return "taiga";
			case Biome::ICE_PLAINS:
			case Biome::COLD_TAIGA:
				return "snowy";
			case Biome::PLAINS:
			default:
				return "plains";
		}
	}

	private function getBiomeSelector(int $seed) : BiomeSelector{
		if(!isset(self::$biomeSelectorCache[$seed])){
			self::$biomeSelectorCache[$seed] = Normal::createBiomeSelector(new Random($seed));
		}

		return self::$biomeSelectorCache[$seed];
	}

	private function getChunkPriority(int $seed, int $chunkX, int $chunkZ) : int{
		return ($seed ^ ($chunkX * 73428767) ^ ($chunkZ * 912931)) & 0x7fffffff;
	}

	public static function regionCoord(int $chunk, int $spacing) : int{
		return $chunk < 0 ? intdiv($chunk - $spacing - 1, $spacing) : intdiv($chunk, $spacing);
	}

	private function floorDiv(int $value, int $divisor) : int{
		$result = intdiv($value, $divisor);
		if($value < 0 && ($value % $divisor) !== 0){
			--$result;
		}

		return $result;
	}

	private static function getVillagePlacement(int $centerX, int $targetY, int $centerZ) : array{
		return [
			"targetX" => $centerX,
			"targetY" => $targetY,
			"targetZ" => $centerZ,
		];
	}

	private function getSafeVillagePlacement(int $centerX, int $baseY, int $centerZ) : array{
		$safe = $this->findSafeVillageTeleportPosition($centerX, $baseY, $centerZ);
		return self::getVillagePlacement($safe["x"], $safe["y"], $safe["z"]);
	}

	private function findSafeVillageTeleportPosition(int $centerX, int $baseY, int $centerZ) : array{
		$preferredY = max(1, min(126, $baseY + 1));
		$best = null;
		$bestScore = PHP_INT_MAX;

		for($radius = 0; $radius <= 18; ++$radius){
			for($dx = -$radius; $dx <= $radius; ++$dx){
				for($dz = -$radius; $dz <= $radius; ++$dz){
					if(max(abs($dx), abs($dz)) !== $radius){
						continue;
					}
					$x = $centerX + $dx;
					$z = $centerZ + $dz;
					$y = $this->findSafeVillageTeleportYAt($x, $preferredY, $z);
					if($y === null){
						continue;
					}
					$score = ($dx * $dx + $dz * $dz) * 8 + abs($y - $preferredY);
					if($score < $bestScore){
						$bestScore = $score;
						$best = ["x" => $x, "y" => $y, "z" => $z];
					}
				}
			}
			if($best !== null && $radius >= 2){
				break;
			}
		}

		return $best ?? ["x" => $centerX, "y" => $preferredY, "z" => $centerZ];
	}

	private function findSafeVillageTeleportYAt(int $x, int $preferredY, int $z){
		$bestY = null;
		$bestScore = PHP_INT_MAX;
		for($y = $preferredY; $y <= 126; ++$y){
			if(!$this->canPlaceVillageMobAt($x, $y, $z)){
				continue;
			}
			$score = abs($y - $preferredY);
			if($score < $bestScore){
				$bestScore = $score;
				$bestY = $y;
			}
		}

		return $bestY;
	}

	private function getStructureVillageWeightedPieceList(Random $random, int $size) : array{
		$list = [
			["class" => PmVillageSimpleHouse::class, "weight" => 4, "max" => $random->nextRange(2 + $size, 4 + ($size << 1)), "count" => 0],
			["class" => PmVillageSmallTemple::class, "weight" => 20, "max" => $random->nextRange($size, 1 + $size), "count" => 0],
			["class" => PmVillageBookHouse::class, "weight" => 20, "max" => $random->nextRange($size, 2 + $size), "count" => 0],
			["class" => PmVillageSmallHut::class, "weight" => 3, "max" => $random->nextRange(2 + $size, 5 + $size * 3), "count" => 0],
			["class" => PmVillagePigHouse::class, "weight" => 15, "max" => $random->nextRange($size, 2 + $size), "count" => 0],
			["class" => PmVillageDoubleFarmland::class, "weight" => 3, "max" => $random->nextRange(1 + $size, 4 + $size), "count" => 0],
			["class" => PmVillageFarmland::class, "weight" => 3, "max" => $random->nextRange(2 + $size, 4 + ($size << 1)), "count" => 0],
			["class" => PmVillageSmithy::class, "weight" => 15, "max" => $random->nextRange(0, 1 + $size), "count" => 0],
			["class" => PmVillageTwoRoomHouse::class, "weight" => 8, "max" => $random->nextRange($size, 3 + ($size << 1)), "count" => 0],
		];

		return array_values(array_filter($list, function(array $entry){
			return $entry["max"] > 0;
		}));
	}

	private function updatePieceWeight(array $weights) : int{
		$success = false;
		$total = 0;
		foreach($weights as $weight){
			if($weight["max"] > 0 && $weight["count"] < $weight["max"]){
				$success = true;
			}
			$total += $weight["weight"];
		}

		return $success ? $total : -1;
	}

	private function generateAndAddPiece(array &$start, array &$pieces, Random $random, int $x, int $y, int $z, int $orientation, int $genDepth, array &$occupied){
		$total = $this->updatePieceWeight($start["weights"]);
		if($total > 0){
			for($i = 0; $i < 5; ++$i){
				$target = $random->nextBoundedInt($total);
				foreach($start["weights"] as $index => $weight){
					$target -= $weight["weight"];
					if($target < 0){
						if(!$this->doPlace($weight, $genDepth) || ($start["previous"] !== null && $weight["class"] === $start["previous"] && count($start["weights"]) > 1)){
							break;
						}

						$class = $weight["class"];
						/** @var PmVillagePiece $piece */
						$piece = new $class($orientation);
						$origin = $piece->getPlacementOriginFromFrontStep($x, $z);
						$bounds = $piece->getWorldBoundsAt($origin["x"], $origin["z"]);
						if($this->isValidPiecePosition($start, $bounds) && !$this->intersects($bounds, $occupied) && $piece->canPlaceObject($this->level, $origin["x"], $y, $origin["z"], $random)){
							$start["weights"][$index]["count"]++;
							$start["previous"] = $weight["class"];
							if($start["weights"][$index]["count"] >= $start["weights"][$index]["max"]){
								array_splice($start["weights"], $index, 1);
							}

							return [
								"piece" => $piece,
								"x" => $origin["x"],
								"z" => $origin["z"],
								"depth" => $genDepth + 1,
								"kind" => "house",
								"bounds" => $bounds,
								"orientation" => $orientation,
							];
						}
					}
				}
			}

			$light = new PmVillageLightPost($orientation);
			$origin = $light->getPlacementOriginFromFrontStep($x, $z);
			$bounds = $light->getWorldBoundsAt($origin["x"], $origin["z"]);
			if($this->isValidPiecePosition($start, $bounds) && !$this->intersects($bounds, $occupied) && $light->canPlaceObject($this->level, $origin["x"], $y, $origin["z"], $random)){
				return [
					"piece" => $light,
					"x" => $origin["x"],
					"z" => $origin["z"],
					"depth" => $genDepth + 1,
					"kind" => "house",
					"bounds" => $bounds,
					"orientation" => $orientation,
				];
			}
		}

		return null;
	}

	private function doPlace(array $weight, int $genDepth) : bool{
		return $weight["max"] === 0 || $weight["count"] < $weight["max"];
	}

	private function tryCreateRoad(array $start, array $pieces, Random $random, int $x, int $y, int $z, int $orientation, int $genDepth, array $occupied){
		$length = 7 * $random->nextRange(3, 5);
		for($i = $length; $i >= 7; $i -= 7){
			$bounds = $this->roadBounds($x, $z, $orientation, $i);
			if($this->isValidPiecePosition($start, $bounds) && !$this->intersects($bounds, $occupied)){
				return [
					"piece" => null,
					"x" => $x,
					"z" => $z,
					"depth" => $genDepth + 1,
					"kind" => "road",
					"bounds" => $bounds,
					"orientation" => $orientation,
					"length" => max($bounds[1] - $bounds[0] + 1, $bounds[3] - $bounds[2] + 1),
				];
			}
		}

		return null;
	}

	private function expandRoad(array &$start, array $road, array &$pieces, array &$pendingRoads, array &$pendingHouses, Random $random, array &$occupied, int $baseY){
		$success = false;
		$length = $road["length"];
		$orientation = $road["orientation"];

		for($offset = $random->nextBoundedInt(5); $offset < $length - 8; $offset += 2 + $random->nextBoundedInt(5)){
			$lot = $this->roadChildPlacement($road, $orientation, true, $offset);
			$result = $this->generateAndAddPiece($start, $pieces, $random, $lot["x"], $baseY, $lot["z"], $lot["orientation"], $road["depth"], $occupied);
			if($result !== null){
				$pieces[] = $result;
				$pendingHouses[] = $result;
				$occupied[] = $result["bounds"];
				$offset += max($result["bounds"][1] - $result["bounds"][0] + 1, $result["bounds"][3] - $result["bounds"][2] + 1);
				$success = true;
			}
		}

		for($offset = $random->nextBoundedInt(5); $offset < $length - 8; $offset += 2 + $random->nextBoundedInt(5)){
			$lot = $this->roadChildPlacement($road, $orientation, false, $offset);
			$result = $this->generateAndAddPiece($start, $pieces, $random, $lot["x"], $baseY, $lot["z"], $lot["orientation"], $road["depth"], $occupied);
			if($result !== null){
				$pieces[] = $result;
				$pendingHouses[] = $result;
				$occupied[] = $result["bounds"];
				$offset += max($result["bounds"][1] - $result["bounds"][0] + 1, $result["bounds"][3] - $result["bounds"][2] + 1);
				$success = true;
			}
		}

		if($success && $random->nextBoundedInt(3) > 0){
			$branch = $this->roadBranchPlacement($road, true);
			$newRoad = $this->tryCreateRoad($start, $pieces, $random, $branch["x"], $baseY, $branch["z"], $branch["orientation"], $road["depth"], $occupied);
			if($newRoad !== null){
				$pieces[] = $newRoad;
				$pendingRoads[] = $newRoad;
				$occupied[] = $newRoad["bounds"];
			}
		}
		if($success && $random->nextBoundedInt(3) > 0){
			$branch = $this->roadBranchPlacement($road, false);
			$newRoad = $this->tryCreateRoad($start, $pieces, $random, $branch["x"], $baseY, $branch["z"], $branch["orientation"], $road["depth"], $occupied);
			if($newRoad !== null){
				$pieces[] = $newRoad;
				$pendingRoads[] = $newRoad;
				$occupied[] = $newRoad["bounds"];
			}
		}
	}

	private function roadBounds(int $x, int $z, int $orientation, int $length) : array{
		switch($orientation){
			case PmVillagePiece::NORTH:
				return [$x, $x + 2, $z - $length + 1, $z];
			case PmVillagePiece::WEST:
				return [$x - $length + 1, $x, $z, $z + 2];
			case PmVillagePiece::EAST:
				return [$x, $x + $length - 1, $z, $z + 2];
			case PmVillagePiece::SOUTH:
			default:
				return [$x, $x + 2, $z, $z + $length - 1];
		}
	}

	private function roadChildPlacement(array $road, int $orientation, bool $left, int $offset) : array{
		list($x0, $x1, $z0, $z1) = $road["bounds"];
		switch($orientation){
			case PmVillagePiece::NORTH:
				return $left ? ["x" => $x0 - 1, "z" => $z1 - $offset, "orientation" => PmVillagePiece::EAST] : ["x" => $x1 + 1, "z" => $z1 - $offset, "orientation" => PmVillagePiece::WEST];
			case PmVillagePiece::SOUTH:
				return $left ? ["x" => $x0 - 1, "z" => $z0 + $offset, "orientation" => PmVillagePiece::EAST] : ["x" => $x1 + 1, "z" => $z0 + $offset, "orientation" => PmVillagePiece::WEST];
			case PmVillagePiece::WEST:
				return $left ? ["x" => $x1 - $offset, "z" => $z0 - 1, "orientation" => PmVillagePiece::SOUTH] : ["x" => $x1 - $offset, "z" => $z1 + 1, "orientation" => PmVillagePiece::NORTH];
			case PmVillagePiece::EAST:
			default:
				return $left ? ["x" => $x0 + $offset, "z" => $z0 - 1, "orientation" => PmVillagePiece::SOUTH] : ["x" => $x0 + $offset, "z" => $z1 + 1, "orientation" => PmVillagePiece::NORTH];
		}
	}

	private function placeRoad(array $road, int $baseY, bool $isDesertVillage = false){
		list($x0, $x1, $z0, $z1) = $road["bounds"];
		$roadBlock = $isDesertVillage ? Block::SANDSTONE : Block::GRAVEL;
		$fillBlock = $isDesertVillage ? Block::SANDSTONE : Block::DIRT;
		for($x = $x0; $x <= $x1; ++$x){
			for($z = $z0; $z <= $z1; ++$z){
				$this->level->setBlockIdAt($x, $baseY, $z, $roadBlock);
				$this->level->setBlockDataAt($x, $baseY, $z, 0);
				for($y = $baseY - 1; $y >= max(0, $baseY - 4); --$y){
					$id = $this->level->getBlockIdAt($x, $y, $z);
					if($id !== Block::AIR && $id !== Block::WATER && $id !== Block::STILL_WATER && $id !== Block::LEAVES && $id !== Block::LEAVES2 && $id !== Block::TALL_GRASS && $id !== Block::DOUBLE_PLANT){
						break;
					}
					$this->level->setBlockIdAt($x, $y, $z, $fillBlock);
					$this->level->setBlockDataAt($x, $y, $z, 0);
				}
			}
		}
	}

	private function roadBranchPlacement(array $road, bool $first) : array{
		list($x0, $x1, $z0, $z1) = $road["bounds"];
		switch($road["orientation"]){
			case PmVillagePiece::NORTH:
				return $first ? ["x" => $x0 - 1, "z" => $z0, "orientation" => PmVillagePiece::WEST] : ["x" => $x1 + 1, "z" => $z0, "orientation" => PmVillagePiece::EAST];
			case PmVillagePiece::SOUTH:
				return $first ? ["x" => $x0 - 1, "z" => $z1 - 2, "orientation" => PmVillagePiece::WEST] : ["x" => $x1 + 1, "z" => $z1 - 2, "orientation" => PmVillagePiece::EAST];
			case PmVillagePiece::WEST:
				return $first ? ["x" => $x0, "z" => $z0 - 1, "orientation" => PmVillagePiece::NORTH] : ["x" => $x0, "z" => $z1 + 1, "orientation" => PmVillagePiece::SOUTH];
			case PmVillagePiece::EAST:
			default:
				return $first ? ["x" => $x1 - 2, "z" => $z0 - 1, "orientation" => PmVillagePiece::NORTH] : ["x" => $x1 - 2, "z" => $z1 + 1, "orientation" => PmVillagePiece::SOUTH];
		}
	}

	private function isValidPiecePosition(array $start, array $bounds) : bool{
		$originX = $start["originX"];
		$originZ = $start["originZ"];
		if(
			abs($bounds[0] - $originX) > self::MAX_DISTANCE ||
			abs($bounds[1] - $originX) > self::MAX_DISTANCE ||
			abs($bounds[2] - $originZ) > self::MAX_DISTANCE ||
			abs($bounds[3] - $originZ) > self::MAX_DISTANCE
		){
			return false;
		}

		$minChunkX = ($originX >> 4) - self::POPULATION_WINDOW_RADIUS_CHUNKS;
		$maxChunkX = ($originX >> 4) + self::POPULATION_WINDOW_RADIUS_CHUNKS;
		$minChunkZ = ($originZ >> 4) - self::POPULATION_WINDOW_RADIUS_CHUNKS;
		$maxChunkZ = ($originZ >> 4) + self::POPULATION_WINDOW_RADIUS_CHUNKS;

		return
			($bounds[0] >> 4) >= $minChunkX &&
			($bounds[1] >> 4) <= $maxChunkX &&
			($bounds[2] >> 4) >= $minChunkZ &&
			($bounds[3] >> 4) <= $maxChunkZ;
	}

	private function intersects(array $candidate, array $occupied) : bool{
		foreach($occupied as $bounds){
			if($candidate[0] <= $bounds[1] && $candidate[1] >= $bounds[0] && $candidate[2] <= $bounds[3] && $candidate[3] >= $bounds[2]){
				return true;
			}
		}
		return false;
	}

	private function findMarkedVillageCenter(ChunkManager $level, int $x, int $z, int $radius){
		$minChunkX = ((int) floor($x - $radius)) >> 4;
		$maxChunkX = ((int) floor($x + $radius)) >> 4;
		$minChunkZ = ((int) floor($z - $radius)) >> 4;
		$maxChunkZ = ((int) floor($z + $radius)) >> 4;
		$radiusSq = $radius * $radius;

		for($chunkX = $minChunkX; $chunkX <= $maxChunkX; ++$chunkX){
			for($chunkZ = $minChunkZ; $chunkZ <= $maxChunkZ; ++$chunkZ){
				$chunk = $level->getChunk($chunkX, $chunkZ);
				if($chunk === null || !method_exists($chunk, "getBlockExtraDataArray")){
					continue;
				}

				foreach($chunk->getBlockExtraDataArray() as $hash => $marker){
					if($marker !== self::MARKER_VILLAGE_CENTER){
						continue;
					}

					$localX = ($hash >> 11) & 0x0f;
					$localZ = ($hash >> 7) & 0x0f;
					$localY = $hash & 0x7f;
					$centerX = ($chunkX << 4) + $localX;
					$centerZ = ($chunkZ << 4) + $localZ;
					$dx = $x - $centerX;
					$dz = $z - $centerZ;
					if(($dx * $dx + $dz * $dz) <= $radiusSq){
						return ["x" => $centerX, "y" => $localY, "z" => $centerZ];
					}
				}
			}
		}

		return null;
	}

	private function isGeneratedVillageStructure(ChunkManager $level, int $centerX, int $centerZ, int $radius) : bool{
		if($this->findMarkedVillageCenter($level, $centerX, $centerZ, 4) !== null){
			return true;
		}

		$originX = $centerX - 3;
		$originZ = $centerZ - 3;
		$baseY = $this->findGeneratedWellBaseY($level, $originX, $originZ);
		if($baseY < 1){
			return false;
		}

		$terrain = $this->scoreGeneratedTerraform($level, $centerX, $baseY, $centerZ);
		if($terrain["score"] < self::DETECT_TERRAFORM_REQUIRED_SCORE || $terrain["matches"] < self::DETECT_TERRAFORM_REQUIRED_MATCHES){
			return false;
		}

		if($this->scoreGeneratedWell($level, $originX, $baseY, $originZ) < self::DETECT_WELL_REQUIRED_SCORE){
			return false;
		}

		$roads = $this->scoreGeneratedRoads($level, $originX, $baseY, $originZ);
		if($roads["score"] < self::DETECT_ROAD_REQUIRED_SCORE || $roads["directions"] < self::DETECT_ROAD_REQUIRED_DIRECTIONS){
			return false;
		}

		$features = $this->scoreGeneratedVillageFeatures($level, $centerX, $centerZ, $originX, $baseY, $originZ, $radius);
		return $features["score"] >= self::DETECT_FEATURE_REQUIRED_SCORE && $features["types"] >= self::DETECT_FEATURE_REQUIRED_TYPES;
	}

	private function findGeneratedWellBaseY(ChunkManager $level, int $originX, int $originZ) : int{
		$candidates = [];
		$samples = [
			[$originX + 3, $originZ + 3],
			[$originX, $originZ],
			[$originX + 5, $originZ],
			[$originX, $originZ + 5],
			[$originX + 5, $originZ + 5]
		];

		foreach($samples as $sample){
			$highest = $this->getLoadedSurfaceY($level, $sample[0], $sample[1]);
			if($highest < 1){
				continue;
			}
			foreach([0, -4, -11, -12, -15] as $offset){
				for($delta = -2; $delta <= 2; ++$delta){
					$y = $highest + $offset + $delta;
					if($y >= 1 && $y <= 112){
						$candidates[$y] = true;
					}
				}
			}
		}

		$bestY = -1;
		$bestScore = 0;
		foreach(array_keys($candidates) as $y){
			$score = $this->scoreGeneratedWell($level, $originX, (int) $y, $originZ);
			if($score > $bestScore){
				$bestScore = $score;
				$bestY = (int) $y;
			}
		}

		if($bestScore >= self::DETECT_WELL_REQUIRED_SCORE){
			return $bestY;
		}

		for($y = 1; $y <= 112; ++$y){
			if(isset($candidates[$y])){
				continue;
			}
			$score = $this->scoreGeneratedWell($level, $originX, $y, $originZ);
			if($score > $bestScore){
				$bestScore = $score;
				$bestY = $y;
			}
		}

		return $bestScore >= self::DETECT_WELL_REQUIRED_SCORE ? $bestY : -1;
	}

	private function scoreGeneratedTerraform(ChunkManager $level, int $centerX, int $baseY, int $centerZ) : array{
		$score = 0;
		$matches = 0;
		$samples = [];
		$offsets = [-54, -36, -18, 18, 36, 54];

		foreach($offsets as $offset){
			$samples[] = [$centerX - 54, $centerZ + $offset];
			$samples[] = [$centerX + 54, $centerZ + $offset];
			$samples[] = [$centerX + $offset, $centerZ - 54];
			$samples[] = [$centerX + $offset, $centerZ + 54];
		}

		foreach($samples as $sample){
			$surfaceY = $this->getLoadedSurfaceY($level, $sample[0], $sample[1]);
			if($surfaceY === $baseY){
				$score += 3;
				++$matches;
			}elseif(abs($surfaceY - $baseY) <= 1){
				$score += 1;
			}

			$id = $this->getLoadedBlockId($level, $sample[0], $baseY, $sample[1]);
			if($id === Block::GRASS || $id === Block::SAND || $id === Block::PODZOL || $id === Block::SNOW_LAYER || $id === Block::SNOW_BLOCK){
				$score += 2;
			}elseif($id === Block::DIRT || $id === Block::GRAVEL || $id === Block::SANDSTONE || $id === Block::COBBLESTONE || $id === Block::PLANK || $id === Block::FARMLAND || $id === Block::MOSSY_STONE){
				$score += 1;
			}

			if($this->hasGeneratedVillageClearance($level, $sample[0], $baseY, $sample[1])){
				$score += 1;
			}
		}

		return ["score" => $score, "matches" => $matches];
	}

	private function scoreGeneratedWell(ChunkManager $level, int $originX, int $baseY, int $originZ) : int{
		$score = 0;
		$foundation = 0;
		$water = 0;
		$body = 0;
		$ring = 0;
		$roof = 0;
		$fence = 0;

		for($dx = 0; $dx <= 5; ++$dx){
			for($dz = 0; $dz <= 5; ++$dz){
				$id = $this->getLoadedBlockId($level, $originX + $dx, $baseY, $originZ + $dz);
				if($this->isVillageStoneBlock($id)){
					++$foundation;
					$score += 4;
				}elseif($dx >= 2 && $dx <= 3 && $dz >= 2 && $dz <= 3 && $this->isWaterBlock($id)){
					++$water;
					$score += 4;
				}

				if($dx === 0 || $dx === 5 || $dz === 0 || $dz === 5){
					$id = $this->getLoadedBlockId($level, $originX + $dx, $baseY, $originZ + $dz);
					if($this->isVillageStoneBlock($id)){
						++$ring;
						$score += 3;
					}
				}
			}
		}

		for($dy = -11; $dy <= 1; ++$dy){
			for($dx = 1; $dx <= 4; ++$dx){
				for($dz = 1; $dz <= 4; ++$dz){
					$id = $this->getLoadedBlockId($level, $originX + $dx, $baseY + $dy, $originZ + $dz);
					if($dx >= 2 && $dx <= 3 && $dz >= 2 && $dz <= 3){
						if($dy <= 0 && $this->isWaterBlock($id)){
							++$water;
							$score += 4;
						}
					}elseif($this->isVillageStoneBlock($id)){
						++$body;
						$score += 2;
					}
				}
			}
		}

		for($dx = 1; $dx <= 4; ++$dx){
			for($dz = 1; $dz <= 4; ++$dz){
				if($this->isVillageStoneBlock($this->getLoadedBlockId($level, $originX + $dx, $baseY + 4, $originZ + $dz))){
					++$roof;
					$score += 5;
				}
			}
		}

		foreach([[1, 2, 1], [1, 3, 1], [4, 2, 1], [4, 3, 1], [1, 2, 4], [1, 3, 4], [4, 2, 4], [4, 3, 4]] as $pos){
			if($this->getLoadedBlockId($level, $originX + $pos[0], $baseY + $pos[1], $originZ + $pos[2]) === Block::FENCE){
				++$fence;
				$score += 6;
			}
		}

		if($foundation < 24 || $water < 28 || $body < 40 || $ring < 10 || $roof < 8 || $fence < 4){
			return 0;
		}

		return $score;
	}

	private function scoreGeneratedRoads(ChunkManager $level, int $originX, int $baseY, int $originZ) : array{
		$score = 0;
		$directions = 0;
		$roads = [
			["x" => $originX + 2, "z" => $originZ - 1, "orientation" => PmVillagePiece::NORTH],
			["x" => $originX + 2, "z" => $originZ + 6, "orientation" => PmVillagePiece::SOUTH],
			["x" => $originX - 1, "z" => $originZ + 2, "orientation" => PmVillagePiece::WEST],
			["x" => $originX + 6, "z" => $originZ + 2, "orientation" => PmVillagePiece::EAST]
		];

		foreach($roads as $road){
			$gravel = $this->scoreGeneratedRoadLine($level, $road["x"], $baseY, $road["z"], $road["orientation"]);
			if($gravel >= 21){
				++$directions;
			}
			$score += $gravel * 5;
		}

		return ["score" => $score, "directions" => $directions];
	}

	private function scoreGeneratedRoadLine(ChunkManager $level, int $x, int $baseY, int $z, int $orientation) : int{
		$best = 0;
		foreach([21, 28, 35] as $length){
			$bounds = $this->roadBounds($x, $z, $orientation, $length);
			$count = 0;
			for($xx = $bounds[0]; $xx <= $bounds[1]; ++$xx){
				for($zz = $bounds[2]; $zz <= $bounds[3]; ++$zz){
					if($this->isVillageRoadBlock($this->getLoadedBlockId($level, $xx, $baseY, $zz))){
						++$count;
					}
				}
			}
			$best = max($best, $count);
		}

		return $best;
	}

	private function scoreGeneratedVillageFeatures(ChunkManager $level, int $centerX, int $centerZ, int $originX, int $baseY, int $originZ, int $radius) : array{
		$score = 0;
		$types = [];
		$radiusSq = $radius * $radius;

		for($x = $centerX - $radius; $x <= $centerX + $radius; $x += 2){
			for($z = $centerZ - $radius; $z <= $centerZ + $radius; $z += 2){
				$dx = $x - $centerX;
				$dz = $z - $centerZ;
				if(($dx * $dx + $dz * $dz) > $radiusSq){
					continue;
				}
				if($x >= $originX - 1 && $x <= $originX + 6 && $z >= $originZ - 1 && $z <= $originZ + 6){
					continue;
				}

				for($y = $baseY; $y <= min(127, $baseY + 12); ++$y){
					$type = null;
					$weight = $this->getGeneratedFeatureWeight($this->getLoadedBlockId($level, $x, $y, $z), $type);
					if($weight <= 0){
						continue;
					}
					$score += $weight;
					if($type !== null){
						$types[$type] = true;
					}
				}

				if($score >= self::DETECT_FEATURE_REQUIRED_SCORE && count($types) >= self::DETECT_FEATURE_REQUIRED_TYPES){
					return ["score" => $score, "types" => count($types)];
				}
			}
		}

		return ["score" => $score, "types" => count($types)];
	}

	private function getGeneratedFeatureWeight(int $id, &$type) : int{
		$type = null;
		switch($id){
			case Block::FARMLAND:
				$type = "farm";
				return 8;
			case Block::WHEAT_BLOCK:
			case Block::CARROT_BLOCK:
				$type = "crop";
				return 8;
			case Block::PLANK:
			case Block::WOOD:
			case Block::WOOD_STAIRS:
			case Block::DOUBLE_WOOD_SLAB:
			case Block::WOODEN_DOOR_BLOCK:
				$type = "wood";
				return 3;
			case Block::COBBLESTONE:
			case Block::COBBLESTONE_STAIRS:
			case Block::SANDSTONE:
			case Block::SANDSTONE_STAIRS:
			case Block::STONE_SLAB:
			case Block::DOUBLE_STONE_SLAB:
				$type = "stone";
				return 3;
			case Block::FENCE:
			case Block::GLASS_PANE:
			case Block::BOOKSHELF:
			case Block::WORKBENCH:
			case Block::FURNACE:
			case Block::CHEST:
			case Block::TORCH:
			case Block::LADDER:
			case Block::IRON_BARS:
			case Block::CARPET:
				$type = "detail";
				return 6;
			case Block::LAVA:
			case Block::STILL_LAVA:
				$type = "smithy";
				return 10;
			case Block::WATER:
			case Block::STILL_WATER:
				$type = "water";
				return 2;
		}

		return 0;
	}

	private function getLoadedSurfaceY(ChunkManager $level, int $x, int $z) : int{
		$chunk = $level->getChunk($x >> 4, $z >> 4);
		if($chunk === null || !method_exists($chunk, "getBlockId")){
			return -1;
		}

		$localX = $x & 0x0f;
		$localZ = $z & 0x0f;
		for($y = 127; $y >= 0; --$y){
			$id = $chunk->getBlockId($localX, $y, $localZ);
			if(!$this->isIgnoredSurfaceBlock($id)){
				return $y;
			}
		}

		return -1;
	}

	private function getLoadedBlockId(ChunkManager $level, int $x, int $y, int $z) : int{
		if($y < 0 || $y > 127){
			return Block::AIR;
		}

		$chunk = $level->getChunk($x >> 4, $z >> 4);
		if($chunk === null || !method_exists($chunk, "getBlockId")){
			return Block::AIR;
		}

		return $chunk->getBlockId($x & 0x0f, $y & 0x7f, $z & 0x0f);
	}

	private function hasGeneratedVillageClearance(ChunkManager $level, int $x, int $baseY, int $z) : bool{
		for($y = $baseY + 1; $y <= min(127, $baseY + 4); ++$y){
			$id = $this->getLoadedBlockId($level, $x, $y, $z);
			if($id !== Block::AIR && $id !== Block::TALL_GRASS && $id !== Block::RED_FLOWER && $id !== Block::DANDELION && $id !== Block::DOUBLE_PLANT && $id !== Block::SNOW_LAYER){
				return false;
			}
		}

		return true;
	}

	private function isIgnoredSurfaceBlock(int $id) : bool{
		return $id === Block::AIR ||
			$id === Block::LEAVES ||
			$id === Block::LEAVES2 ||
			$id === Block::TALL_GRASS ||
			$id === Block::RED_FLOWER ||
			$id === Block::DANDELION ||
			$id === Block::DOUBLE_PLANT ||
			$id === Block::SNOW_LAYER ||
			$id === Block::WATER_LILY ||
			$id === Block::WATER ||
			$id === Block::STILL_WATER;
	}

	private function isWaterBlock(int $id) : bool{
		return $id === Block::WATER || $id === Block::STILL_WATER;
	}

	private function isVillageStoneBlock(int $id) : bool{
		return $id === Block::COBBLESTONE || $id === Block::SANDSTONE;
	}

	private function isVillageRoadBlock(int $id) : bool{
		return $id === Block::GRAVEL || $id === Block::SANDSTONE;
	}

	private function terraformVillageArea(int $centerX, int $baseY, int $centerZ, string $villageType = "plains"){
		$centerChunkX = $centerX >> 4;
		$centerChunkZ = $centerZ >> 4;
		// 根据村庄类型选择合适的地表方块和填充方块
		switch($villageType){
			case "desert":
				$surfaceBlock = Block::SAND;
				$fillBlock = Block::SANDSTONE;
				$topCoverBlock = null; // 无额外顶层覆盖
				break;
			case "taiga":
				// 针叶林/云杉林村庄：地表为灰化土(PODZOL)，填充用泥土
				$surfaceBlock = Block::PODZOL;
				$fillBlock = Block::DIRT;
				$topCoverBlock = null;
				break;
			case "snowy":
				// 雪原村庄：地表为草方块，顶层覆盖雪层(SNOW_LAYER)，填充用泥土
				// 这样既有与群系匹配的雪覆盖，又保持了村庄地面的稳固性
				$surfaceBlock = Block::GRASS;
				$fillBlock = Block::DIRT;
				$topCoverBlock = Block::SNOW_LAYER;
				break;
			case "savanna":
				// 稀树草原村庄：地表为普通草方块，填充泥土
				$surfaceBlock = Block::GRASS;
				$fillBlock = Block::DIRT;
				$topCoverBlock = null;
				break;
			case "plains":
			default:
				// 平原村庄：标准草方块+泥土
				$surfaceBlock = Block::GRASS;
				$fillBlock = Block::DIRT;
				$topCoverBlock = null;
				break;
		}
		for($xx = -self::TERRAFORM_RADIUS; $xx <= self::TERRAFORM_RADIUS; ++$xx){
			for($zz = -self::TERRAFORM_RADIUS; $zz <= self::TERRAFORM_RADIUS; ++$zz){
				$worldX = $centerX + $xx;
				$worldZ = $centerZ + $zz;
				$chunkX = $worldX >> 4;
				$chunkZ = $worldZ >> 4;
				if(
					abs($chunkX - $centerChunkX) > self::POPULATION_WINDOW_RADIUS_CHUNKS ||
					abs($chunkZ - $centerChunkZ) > self::POPULATION_WINDOW_RADIUS_CHUNKS
				){
					continue;
				}
				$targetY = $this->getVillageTerrainTargetY($centerX, $centerZ, $worldX, $worldZ, $baseY);
				for($yy = $targetY + 1; $yy <= $targetY + self::TERRAFORM_CLEARANCE; ++$yy){
					$this->level->setBlockIdAt($worldX, $yy, $worldZ, Block::AIR);
					$this->level->setBlockDataAt($worldX, $yy, $worldZ, 0);
				}
				$this->level->setBlockIdAt($worldX, $targetY, $worldZ, $surfaceBlock);
				$this->level->setBlockDataAt($worldX, $targetY, $worldZ, 0);
				// 对于雪原村庄，在地表方块上方加一层雪层
				if($topCoverBlock !== null){
					$this->level->setBlockIdAt($worldX, $targetY + 1, $worldZ, $topCoverBlock);
					$this->level->setBlockDataAt($worldX, $targetY + 1, $worldZ, 0);
				}
				for($yy = $targetY - 1; $yy >= max(0, $targetY - 8); --$yy){
					$id = $this->level->getBlockIdAt($worldX, $yy, $worldZ);
					if($id !== Block::AIR && $id !== Block::WATER && $id !== Block::STILL_WATER && $id !== Block::LEAVES && $id !== Block::LEAVES2 && $id !== Block::LOG && $id !== Block::WOOD && $id !== Block::LOG2 && $id !== Block::TALL_GRASS && $id !== Block::DOUBLE_PLANT){
						break;
					}
					$this->level->setBlockIdAt($worldX, $yy, $worldZ, $fillBlock);
					$this->level->setBlockDataAt($worldX, $yy, $worldZ, 0);
				}
			}
		}
	}

	private function getVillageTerrainTargetY(int $centerX, int $centerZ, int $worldX, int $worldZ, int $baseY) : int{
		$distance = max(abs($worldX - $centerX), abs($worldZ - $centerZ));
		if($distance <= 12){
			return $baseY;
		}

		if($distance >= self::TERRAFORM_RADIUS){
			return $this->getSurfaceY($worldX, $worldZ);
		}

		$surfaceY = $this->getSurfaceY($worldX, $worldZ);
		if($surfaceY < 1){
			return $baseY;
		}

		$targetY = $this->getVillageTerrainTransitionY($baseY, $surfaceY, $distance);

		if($surfaceY > $baseY){
			$targetY = min($surfaceY, max($baseY, $targetY));
		}else{
			$targetY = max($surfaceY, min($baseY, $targetY));
		}

		return $targetY;
	}

	private function getVillageTerrainTransitionY(int $baseY, int $surfaceY, int $distance) : int{
		$edgeDistance = $distance - 12;
		$blendSpan = self::TERRAFORM_RADIUS - 12;
		$ratio = max(0.0, min(1.0, $edgeDistance / $blendSpan));
		return (int) round($baseY + (($surfaceY - $baseY) * $ratio));
	}

	private function applyDesertVillagePalette(int $centerX, int $baseY, int $centerZ){
		$centerChunkX = $centerX >> 4;
		$centerChunkZ = $centerZ >> 4;
		for($xx = -self::TERRAFORM_RADIUS; $xx <= self::TERRAFORM_RADIUS; ++$xx){
			for($zz = -self::TERRAFORM_RADIUS; $zz <= self::TERRAFORM_RADIUS; ++$zz){
				$worldX = $centerX + $xx;
				$worldZ = $centerZ + $zz;
				$chunkX = $worldX >> 4;
				$chunkZ = $worldZ >> 4;
				if(
					abs($chunkX - $centerChunkX) > self::POPULATION_WINDOW_RADIUS_CHUNKS ||
					abs($chunkZ - $centerChunkZ) > self::POPULATION_WINDOW_RADIUS_CHUNKS
				){
					continue;
				}

				for($yy = max(0, $baseY - 12); $yy <= min(127, $baseY + self::TERRAFORM_CLEARANCE); ++$yy){
					$id = $this->level->getBlockIdAt($worldX, $yy, $worldZ);
					$meta = method_exists($this->level, "getBlockDataAt") ? $this->level->getBlockDataAt($worldX, $yy, $worldZ) : 0;
					switch($id){
						case Block::COBBLESTONE:
						case Block::PLANK:
						case Block::WOOD:
							$this->level->setBlockIdAt($worldX, $yy, $worldZ, Block::SANDSTONE);
							$this->level->setBlockDataAt($worldX, $yy, $worldZ, 0);
							break;
						case Block::COBBLESTONE_STAIRS:
						case Block::WOOD_STAIRS:
							$this->level->setBlockIdAt($worldX, $yy, $worldZ, Block::SANDSTONE_STAIRS);
							$this->level->setBlockDataAt($worldX, $yy, $worldZ, $meta);
							break;
						case Block::STONE_SLAB:
							$this->level->setBlockDataAt($worldX, $yy, $worldZ, 1);
							break;
						case Block::DOUBLE_WOOD_SLAB:
							$this->level->setBlockIdAt($worldX, $yy, $worldZ, Block::DOUBLE_STONE_SLAB);
							$this->level->setBlockDataAt($worldX, $yy, $worldZ, 1);
							break;
					}
				}
			}
		}
	}

	private function applyVillageBiome(int $centerX, int $centerZ){
		$centerChunkX = $centerX >> 4;
		$centerChunkZ = $centerZ >> 4;
		$biome = Biome::getBiome(Biome::VILLAGE);
		$color = $biome->getColor();
		$red = ($color >> 16) & 0xff;
		$green = ($color >> 8) & 0xff;
		$blue = $color & 0xff;

		for($xx = -self::TERRAFORM_RADIUS; $xx <= self::TERRAFORM_RADIUS; ++$xx){
			for($zz = -self::TERRAFORM_RADIUS; $zz <= self::TERRAFORM_RADIUS; ++$zz){
				$worldX = $centerX + $xx;
				$worldZ = $centerZ + $zz;
				$chunkX = $worldX >> 4;
				$chunkZ = $worldZ >> 4;
				if(
					abs($chunkX - $centerChunkX) > self::POPULATION_WINDOW_RADIUS_CHUNKS ||
					abs($chunkZ - $centerChunkZ) > self::POPULATION_WINDOW_RADIUS_CHUNKS
				){
					continue;
				}

				$chunk = $this->level->getChunk($chunkX, $chunkZ);
				if($chunk === null){
					continue;
				}

				$chunk->setBiomeId($worldX & 0x0f, $worldZ & 0x0f, Biome::VILLAGE);
				$chunk->setBiomeColor($worldX & 0x0f, $worldZ & 0x0f, $red, $green, $blue);
			}
		}
	}

	private function getSurfaceY(int $x, int $z) : int{
		$cacheKey = $x . ":" . $z;
		if(isset($this->surfaceYCache[$cacheKey])){
			return $this->surfaceYCache[$cacheKey];
		}
		for($y = $this->getSurfaceScanStartY($x, $z); $y >= 0; --$y){
			$id = $this->level->getBlockIdAt($x, $y, $z);
			if(
				$id !== Block::AIR &&
				$id !== Block::LEAVES &&
				$id !== Block::LEAVES2 &&
				$id !== Block::LOG &&
				$id !== Block::WOOD &&
				$id !== Block::LOG2 &&
				$id !== Block::TALL_GRASS &&
				$id !== Block::RED_FLOWER &&
				$id !== Block::DANDELION &&
				$id !== Block::DOUBLE_PLANT &&
				$id !== Block::SNOW_LAYER &&
				$id !== Block::WATER_LILY &&
				$id !== Block::WATER &&
				$id !== Block::STILL_WATER
			){
				$this->surfaceYCache[$cacheKey] = $y;
				return $y;
			}
		}

		$this->surfaceYCache[$cacheKey] = -1;
		return -1;
	}

	private function getSurfaceScanStartY(int $x, int $z) : int{
		$chunk = $this->level->getChunk($x >> 4, $z >> 4);
		if($chunk !== null && method_exists($chunk, "getHeightMap")){
			$height = (int) $chunk->getHeightMap($x & 0x0f, $z & 0x0f);
			if($height > 0 && $height <= 127){
				return $height;
			}
		}
		if(method_exists($this->level, "getHeightMap")){
			$height = (int) $this->level->getHeightMap($x, $z);
			if($height > 0 && $height <= 127){
				return $height;
			}
		}

		return 127;
	}

	private function storeVillageMarker(int $centerX, int $baseY, int $centerZ){
		$chunk = $this->level->getChunk($centerX >> 4, $centerZ >> 4);
		if($chunk !== null){
			$chunk->setBlockExtraData($centerX & 0x0f, $baseY & 0x7f, $centerZ & 0x0f, self::MARKER_VILLAGE_CENTER);
		}
	}

	private function storeVillageMobMarkers(int $centerX, int $baseY, int $centerZ){
		$villagerOffsets = [
			[-7, -2],
			[7, 2],
			[-2, 7],
			[2, -7],
		];
		$markedVillagers = 0;
		foreach($villagerOffsets as $offset){
			$pos = $this->findSafeMobMarkerPosition($centerX + $offset[0], $baseY + 1, $centerZ + $offset[1], 2);
			if($pos !== null && $this->storeMobMarker($pos["x"], $pos["y"], $pos["z"], self::MARKER_VILLAGER)){
				++$markedVillagers;
				if($markedVillagers >= 2){
					break;
				}
			}
		}

		if($markedVillagers < 2){
			$fallbacks = [
				[-10, 0],
				[10, 0],
				[0, -10],
				[0, 10],
			];
			foreach($fallbacks as $offset){
				$pos = $this->findSafeMobMarkerPosition($centerX + $offset[0], $baseY + 1, $centerZ + $offset[1], 3);
				if($pos !== null && $this->storeMobMarker($pos["x"], $pos["y"], $pos["z"], self::MARKER_VILLAGER)){
					++$markedVillagers;
					if($markedVillagers >= 2){
						break;
					}
				}
			}
		}

		$golemPos = $this->findSafeMobMarkerPosition($centerX, $baseY + 1, $centerZ + 9, 4);
		if($golemPos === null){
			$golemPos = $this->findSafeMobMarkerPosition($centerX + 9, $baseY + 1, $centerZ, 4);
		}
		if($golemPos !== null){
			$this->storeMobMarker($golemPos["x"], $golemPos["y"], $golemPos["z"], self::MARKER_IRON_GOLEM);
		}
	}

	private function findSafeMobMarkerPosition(int $x, int $preferredY, int $z, int $verticalRange){
		for($dy = 0; $dy <= $verticalRange; ++$dy){
			foreach([$preferredY + $dy, $preferredY - $dy] as $y){
				if($y < 1 || $y > 125){
					continue;
				}
				if($this->canPlaceVillageMobAt($x, $y, $z)){
					return ["x" => $x, "y" => $y, "z" => $z];
				}
			}
		}

		$surfaceY = $this->getSurfaceY($x, $z);
		if($surfaceY >= 1 && $surfaceY <= 124 && $this->canPlaceVillageMobAt($x, $surfaceY + 1, $z)){
			return ["x" => $x, "y" => $surfaceY + 1, "z" => $z];
		}

		return null;
	}

	private function canPlaceVillageMobAt(int $x, int $y, int $z) : bool{
		if(!$this->isVillageMobSupportBlock($this->level->getBlockIdAt($x, $y - 1, $z))){
			return false;
		}

		return $this->isVillageMobPassableBlock($this->level->getBlockIdAt($x, $y, $z)) &&
			$this->isVillageMobPassableBlock($this->level->getBlockIdAt($x, $y + 1, $z));
	}

	private function isVillageMobSupportBlock(int $id) : bool{
		return $id !== Block::AIR &&
			$id !== Block::WATER &&
			$id !== Block::STILL_WATER &&
			$id !== Block::LAVA &&
			$id !== Block::STILL_LAVA &&
			$id !== Block::LEAVES &&
			$id !== Block::LEAVES2 &&
			$id !== Block::TALL_GRASS &&
			$id !== Block::DOUBLE_PLANT &&
			$id !== Block::SNOW_LAYER &&
			$id !== Block::WATER_LILY;
	}

	private function isVillageMobPassableBlock(int $id) : bool{
		return $id === Block::AIR ||
			$id === Block::TALL_GRASS ||
			$id === Block::RED_FLOWER ||
			$id === Block::DANDELION ||
			$id === Block::DOUBLE_PLANT ||
			$id === Block::SNOW_LAYER;
	}

	private function storeMobMarker(int $x, int $y, int $z, int $marker) : bool{
		$chunk = $this->level->getChunk($x >> 4, $z >> 4);
		if($chunk === null){
			return false;
		}

		$chunk->setBlockExtraData($x & 0x0f, $y & 0x7f, $z & 0x0f, $marker);
		return true;
	}

	private function processLiveLevelFootprint(ChunkManager $level, array $chunks){
		if(!($level instanceof \pocketmine\level\Level) || !method_exists($level, "processDeferredStructureContainers")){
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
}
