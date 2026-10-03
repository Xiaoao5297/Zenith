<?php

namespace lycore\level\generator\populator;

use lycore\block\Block;
use lycore\level\ChunkManager;
use lycore\level\Level;
use lycore\level\generator\noise\Simplex;
use lycore\level\generator\normal\object\MineshaftLoot;
use lycore\utils\Random;

require_once dirname(__DIR__) . "/noise/Noise.php";
require_once dirname(__DIR__) . "/noise/Perlin.php";
require_once dirname(__DIR__) . "/noise/Simplex.php";
require_once dirname(__DIR__) . "/normal/object/MineshaftLoot.php";

class Mineshaft extends Populator{
	const PROBABILITY = 4;
	const SEARCH_SPAN = 64;
	const MAX_DEPTH = 8;
	const POPULATION_WINDOW_RADIUS_CHUNKS = 7;
	const SPAWNER_MARKER_CAVE_SPIDER = 6;

	const NORTH = 0;
	const SOUTH = 1;
	const WEST = 2;
	const EAST = 3;

	const TYPE_NORMAL = "normal";
	const TYPE_MESA = "mesa";

	/** @var ChunkManager */
	private $level;
	/** @var Random */
	private $random;
	private $placedChestMinecart = false;
	private $placedCaveSpiderSpawner = false;
	private $activeBox = null;
	private $pendingAirBackfill = [];
	/** @var Simplex[] */
	private $sedimentNoise = [];

	public static function shouldStartAt(int $seed, int $chunkX, int $chunkZ) : bool{
		$random = self::createChunkRandom($seed, $chunkX, $chunkZ);
		return self::pnxNextBoundedInt($random, 1000) < self::PROBABILITY;
	}

	public static function getPopulationRadiusForChunk(int $seed, int $chunkX, int $chunkZ) : int{
		return self::shouldStartAt($seed, $chunkX, $chunkZ) ? self::POPULATION_WINDOW_RADIUS_CHUNKS : 1;
	}

	public static function findCandidateInRegion(int $seed, int $regionX, int $regionZ, int $span = self::SEARCH_SPAN) : array{
		$startX = $regionX * $span;
		$startZ = $regionZ * $span;
		for($cx = $startX; $cx < $startX + $span; ++$cx){
			for($cz = $startZ; $cz < $startZ + $span; ++$cz){
				if(self::shouldStartAt($seed, $cx, $cz)){
					return ["chunkX" => $cx, "chunkZ" => $cz];
				}
			}
		}

		for($radius = 1; $radius <= 4; ++$radius){
			for($cx = $startX - ($radius * $span); $cx < $startX + (($radius + 1) * $span); ++$cx){
				for($cz = $startZ - ($radius * $span); $cz < $startZ + (($radius + 1) * $span); ++$cz){
					if(self::shouldStartAt($seed, $cx, $cz)){
						return ["chunkX" => $cx, "chunkZ" => $cz];
					}
				}
			}
		}

		return ["chunkX" => $startX, "chunkZ" => $startZ];
	}

	public function populate(ChunkManager $level, $chunkX, $chunkZ, Random $random){
		if(!self::shouldStartAt((int) $level->getSeed(), (int) $chunkX, (int) $chunkZ)){
			return;
		}

		$this->placeMineshaftAtCandidate($level, (int) $chunkX, (int) $chunkZ, self::createChunkRandom((int) $level->getSeed(), (int) $chunkX, (int) $chunkZ));
	}

	public function placeMineshaftAtCandidate(ChunkManager $level, int $chunkX, int $chunkZ, Random $random) : array{
		if(!$this->canPopulateOverworldStructure($level)){
			return [];
		}

		if(Block::$solid === null && method_exists(Block::class, "init")){
			Block::init();
		}

		$this->level = $level;
		$this->random = $random;
		$this->placedChestMinecart = false;
		$this->placedCaveSpiderSpawner = false;
		$this->pendingAirBackfill = [];

		$type = $this->detectType($chunkX, $chunkZ);
		$pieces = $this->buildPieces($chunkX, $chunkZ, $random, $type);
		if(count($pieces) === 0){
			return [];
		}

		$this->movePiecesBelowSeaLevel($pieces, $random, 10, $type);
		$structureBox = $this->calculateBoundingBox($pieces);
		for($cx = $structureBox[0] >> 4; $cx <= ($structureBox[3] >> 4); ++$cx){
			for($cz = $structureBox[2] >> 4; $cz <= ($structureBox[5] >> 4); ++$cz){
				$x = $cx << 4;
				$z = $cz << 4;
				$this->activeBox = [$x, -63, $z, $x + 15, 512, $z + 15];
				$removed = false;
				foreach($pieces as $index => $piece){
					if(!$this->boxesIntersect($piece["box"], $this->activeBox)){
						continue;
					}
					$placed = true;
					switch($piece["type"]){
						case "room":
							$placed = $this->placeRoom($piece);
							break;
						case "crossing":
							$placed = $this->placeCrossing($piece);
							break;
						case "stairs":
							$placed = $this->placeStairs($piece);
							break;
						default:
							$placed = $this->placeCorridor($piece);
							break;
					}
					if(!$placed){
						unset($pieces[$index]);
						$removed = true;
					}
				}
				if($removed){
					$pieces = array_values($pieces);
				}
			}
		}
		$this->activeBox = null;
		$this->applyPendingAirBackfill();
		$footprint = self::getFootprintChunksFromBounds($structureBox);
		$this->markStructureFootprint($level, $footprint);
		$this->processLiveLevelFootprint($level, $footprint);

		$metadataPieces = $this->metadataPieces($pieces);
		return [
			"pieceCount" => count($pieces),
			"pieceTypes" => array_values(array_unique(array_column($metadataPieces, "type"))),
			"pieces" => $metadataPieces,
			"hasChestMinecart" => $this->placedChestMinecart,
			"hasCaveSpiderSpawner" => $this->placedCaveSpiderSpawner,
			"boundingBox" => $this->calculateBoundingBox($pieces)
		];
	}

	public static function createChunkRandom(int $seed, int $chunkX, int $chunkZ) : Random{
		$random = new MineshaftXoroshiroRandom($seed);
		$r1 = $random->nextInt();
		$r2 = $random->nextInt();
		$random->setSeed(($chunkX * $r1) ^ ($chunkZ * $r2) ^ $seed);
		return $random;
	}

	private static function getFootprintChunksFromBounds(array $box) : array{
		$chunks = [];
		for($chunkX = ((int) $box[0]) >> 4; $chunkX <= (((int) $box[3]) >> 4); ++$chunkX){
			for($chunkZ = ((int) $box[2]) >> 4; $chunkZ <= (((int) $box[5]) >> 4); ++$chunkZ){
				$chunks[] = ["chunkX" => $chunkX, "chunkZ" => $chunkZ];
			}
		}
		return $chunks;
	}

	private function markStructureFootprint(ChunkManager $level, array $chunks){
		if(method_exists($level, "markStructureFootprintChunks")){
			$level->markStructureFootprintChunks($chunks);
		}
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

	private static function pnxNextBoundedInt(Random $random, int $max) : int{
		if($max <= 0){
			return 0;
		}
		if($random instanceof MineshaftXoroshiroRandom){
			return $random->nextBoundedInt($max);
		}
		return $random->nextBoundedInt($max + 1);
	}

	private function detectType(int $chunkX, int $chunkZ) : string{
		$chunk = $this->level->getChunk($chunkX, $chunkZ);
		if($chunk !== null && method_exists($chunk, "getBiomeId")){
			$height = method_exists($chunk, "getHeightMap") ? (int) $chunk->getHeightMap(7, 7) : 64;
			$method = new \ReflectionMethod($chunk, "getBiomeId");
			$biome = $method->getNumberOfParameters() >= 3 ? (int) $chunk->getBiomeId(7, $height, 7) : (int) $chunk->getBiomeId(7, 7);
			if(($biome >= 37 && $biome <= 39) || ($biome >= 165 && $biome <= 167)){
				return self::TYPE_MESA;
			}
		}
		return self::TYPE_NORMAL;
	}

	private function buildPieces(int $chunkX, int $chunkZ, Random $random, string $type) : array{
		$x = ($chunkX << 4) + 2;
		$z = ($chunkZ << 4) + 2;
		$room = [
			"type" => "room",
			"box" => [$x, 50, $z, $x + 7 + self::pnxNextBoundedInt($random, 6), 54 + self::pnxNextBoundedInt($random, 6), $z + 7 + self::pnxNextBoundedInt($random, 6)],
			"depth" => 0,
			"direction" => null,
			"mineType" => $type,
			"childEntrances" => []
		];

		$pieces = [$room];
		$this->addRoomChildren(0, $pieces, $random);
		return $pieces;
	}

	private function addRoomChildren(int $roomIndex, array &$pieces, Random $random){
		$room = $pieces[$roomIndex];
		list($x0, $y0, $z0, $x1, $y1, $z1) = $room["box"];
		$heightOffset = $y1 - $y0 + 1 - 3 - 1;
		if($heightOffset <= 0){
			$heightOffset = 1;
		}
		$depth = $room["depth"];

		for($x = 0; $x < ($x1 - $x0 + 1); $x += 4){
			$x += self::pnxNextBoundedInt($random, $x1 - $x0 + 1);
			if($x + 3 > ($x1 - $x0 + 1)){
				break;
			}
			$next = $this->generateAndAddPiece($room, $pieces, $random, $x0 + $x, $y0 + self::pnxNextBoundedInt($random, $heightOffset) + 1, $z0 - 1, self::NORTH, $depth);
			if($next !== null){
				$box = $next["box"];
				$pieces[$roomIndex]["childEntrances"][] = [$box[0], $box[1], $z0, $box[3], $box[4], $z0 + 1];
			}
		}

		for($x = 0; $x < ($x1 - $x0 + 1); $x += 4){
			$x += self::pnxNextBoundedInt($random, $x1 - $x0 + 1);
			if($x + 3 > ($x1 - $x0 + 1)){
				break;
			}
			$next = $this->generateAndAddPiece($room, $pieces, $random, $x0 + $x, $y0 + self::pnxNextBoundedInt($random, $heightOffset) + 1, $z1 + 1, self::SOUTH, $depth);
			if($next !== null){
				$box = $next["box"];
				$pieces[$roomIndex]["childEntrances"][] = [$box[0], $box[1], $z1 - 1, $box[3], $box[4], $z1];
			}
		}

		for($z = 0; $z < ($z1 - $z0 + 1); $z += 4){
			$z += self::pnxNextBoundedInt($random, $z1 - $z0 + 1);
			if($z + 3 > ($z1 - $z0 + 1)){
				break;
			}
			$next = $this->generateAndAddPiece($room, $pieces, $random, $x0 - 1, $y0 + self::pnxNextBoundedInt($random, $heightOffset) + 1, $z0 + $z, self::WEST, $depth);
			if($next !== null){
				$box = $next["box"];
				$pieces[$roomIndex]["childEntrances"][] = [$x0, $box[1], $box[2], $x0 + 1, $box[4], $box[5]];
			}
		}

		for($z = 0; $z < ($z1 - $z0 + 1); $z += 4){
			$z += self::pnxNextBoundedInt($random, $z1 - $z0 + 1);
			if($z + 3 > ($z1 - $z0 + 1)){
				break;
			}
			$next = $this->generateAndAddPiece($room, $pieces, $random, $x1 + 1, $y0 + self::pnxNextBoundedInt($random, $heightOffset) + 1, $z0 + $z, self::EAST, $depth);
			if($next !== null){
				$box = $next["box"];
				$pieces[$roomIndex]["childEntrances"][] = [$x1 - 1, $box[1], $box[2], $x1, $box[4], $box[5]];
			}
		}
	}

	private function generateAndAddPiece(array $start, array &$pieces, Random $random, int $x, int $y, int $z, int $direction, int $depth){
		if($depth > self::MAX_DEPTH || abs($x - $start["box"][0]) > 80 || abs($z - $start["box"][2]) > 80){
			return null;
		}

		$piece = $this->createRandomShaftPiece($pieces, $random, $x, $y, $z, $direction, $depth + 1, $start["mineType"]);
		if($piece === null){
			return null;
		}

		$pieces[] = $piece;
		$pieceIndex = count($pieces) - 1;
		$this->addPieceChildren($pieceIndex, $start, $pieces, $random);
		return $pieces[$pieceIndex];
	}

	private function createRandomShaftPiece(array $pieces, Random $random, int $x, int $y, int $z, int $direction, int $depth, string $type){
		$chance = self::pnxNextBoundedInt($random, 100);
		if($chance >= 80){
			return $this->createCrossing($pieces, $random, $x, $y, $z, $direction, $depth, $type);
		}
		if($chance >= 70){
			return $this->createStairs($pieces, $x, $y, $z, $direction, $depth, $type);
		}
		return $this->createCorridor($pieces, $random, $x, $y, $z, $direction, $depth, $type);
	}

	private function createCorridor(array $pieces, Random $random, int $x, int $y, int $z, int $direction, int $depth, string $type){
		$box = [$x, $y, $z, $x, $y + 2, $z];
		$count = self::pnxNextBoundedInt($random, 3) + 2;
		for(; $count > 0; --$count){
			$length = $count * 5;
			switch($direction){
				case self::NORTH:
					$box = [$x, $y, $z - ($length - 1), $x + 2, $y + 2, $z];
					break;
				case self::SOUTH:
					$box = [$x, $y, $z, $x + 2, $y + 2, $z + $length - 1];
					break;
				case self::WEST:
					$box = [$x - ($length - 1), $y, $z, $x, $y + 2, $z + 2];
					break;
				case self::EAST:
				default:
					$box = [$x, $y, $z, $x + $length - 1, $y + 2, $z + 2];
					break;
			}
			if(!$this->collides($pieces, $box)){
				break;
			}
		}

		if($count <= 0){
			return null;
		}

		$hasRails = self::pnxNextBoundedInt($random, 3) === 0;
		return [
			"type" => "corridor",
			"box" => $box,
			"direction" => $direction,
			"depth" => $depth,
			"mineType" => $type,
			"sections" => $count,
			"rails" => $hasRails,
			"spider" => !$hasRails && self::pnxNextBoundedInt($random, 23) === 0,
			"hasPlacedSpider" => false
		];
	}

	private function createCrossing(array $pieces, Random $random, int $x, int $y, int $z, int $direction, int $depth, string $type){
		$y1 = $y + 2;
		if(self::pnxNextBoundedInt($random, 4) === 0){
			$y1 += 4;
		}
		switch($direction){
			case self::NORTH:
				$box = [$x - 1, $y, $z - 4, $x + 3, $y1, $z];
				break;
			case self::SOUTH:
				$box = [$x - 1, $y, $z, $x + 3, $y1, $z + 4];
				break;
			case self::WEST:
				$box = [$x - 4, $y, $z - 1, $x, $y1, $z + 3];
				break;
			case self::EAST:
			default:
				$box = [$x, $y, $z - 1, $x + 4, $y1, $z + 3];
				break;
		}

		if($this->collides($pieces, $box)){
			return null;
		}

		return ["type" => "crossing", "box" => $box, "direction" => $direction, "depth" => $depth, "mineType" => $type, "twoFloored" => ($y1 - $y + 1) > 3];
	}

	private function createStairs(array $pieces, int $x, int $y, int $z, int $direction, int $depth, string $type){
		switch($direction){
			case self::NORTH:
				$box = [$x, $y - 5, $z - 8, $x + 2, $y + 2, $z];
				break;
			case self::SOUTH:
				$box = [$x, $y - 5, $z, $x + 2, $y + 2, $z + 8];
				break;
			case self::WEST:
				$box = [$x - 8, $y - 5, $z, $x, $y + 2, $z + 2];
				break;
			case self::EAST:
			default:
				$box = [$x, $y - 5, $z, $x + 8, $y + 2, $z + 2];
				break;
		}

		if($this->collides($pieces, $box)){
			return null;
		}

		return ["type" => "stairs", "box" => $box, "direction" => $direction, "depth" => $depth, "mineType" => $type];
	}

	private function addPieceChildren(int $pieceIndex, array $start, array &$pieces, Random $random){
		$piece = $pieces[$pieceIndex];
		switch($piece["type"]){
			case "corridor":
				$this->addCorridorChildren($piece, $start, $pieces, $random);
				break;
			case "crossing":
				$this->addCrossingChildren($piece, $start, $pieces, $random);
				break;
			case "stairs":
				$this->addStairsChildren($piece, $start, $pieces, $random);
				break;
		}
	}

	private function addCorridorChildren(array $piece, array $start, array &$pieces, Random $random){
		list($x0, $y0, $z0, $x1, $y1, $z1) = $piece["box"];
		$depth = $piece["depth"];
		$target = self::pnxNextBoundedInt($random, 4);
		switch($piece["direction"]){
			case self::NORTH:
				if($target <= 1){
					$this->generateAndAddPiece($start, $pieces, $random, $x0, $y0 - 1 + self::pnxNextBoundedInt($random, 3), $z0 - 1, self::NORTH, $depth);
				}elseif($target === 2){
					$this->generateAndAddPiece($start, $pieces, $random, $x0 - 1, $y0 - 1 + self::pnxNextBoundedInt($random, 3), $z0, self::WEST, $depth);
				}else{
					$this->generateAndAddPiece($start, $pieces, $random, $x1 + 1, $y0 - 1 + self::pnxNextBoundedInt($random, 3), $z0, self::EAST, $depth);
				}
				break;
			case self::SOUTH:
				if($target <= 1){
					$this->generateAndAddPiece($start, $pieces, $random, $x0, $y0 - 1 + self::pnxNextBoundedInt($random, 3), $z1 + 1, self::SOUTH, $depth);
				}elseif($target === 2){
					$this->generateAndAddPiece($start, $pieces, $random, $x0 - 1, $y0 - 1 + self::pnxNextBoundedInt($random, 3), $z1 - 3, self::WEST, $depth);
				}else{
					$this->generateAndAddPiece($start, $pieces, $random, $x1 + 1, $y0 - 1 + self::pnxNextBoundedInt($random, 3), $z1 - 3, self::EAST, $depth);
				}
				break;
			case self::WEST:
				if($target <= 1){
					$this->generateAndAddPiece($start, $pieces, $random, $x0 - 1, $y0 - 1 + self::pnxNextBoundedInt($random, 3), $z0, self::WEST, $depth);
				}elseif($target === 2){
					$this->generateAndAddPiece($start, $pieces, $random, $x0, $y0 - 1 + self::pnxNextBoundedInt($random, 3), $z0 - 1, self::NORTH, $depth);
				}else{
					$this->generateAndAddPiece($start, $pieces, $random, $x0, $y0 - 1 + self::pnxNextBoundedInt($random, 3), $z1 + 1, self::SOUTH, $depth);
				}
				break;
			case self::EAST:
			default:
				if($target <= 1){
					$this->generateAndAddPiece($start, $pieces, $random, $x1 + 1, $y0 - 1 + self::pnxNextBoundedInt($random, 3), $z0, self::EAST, $depth);
				}elseif($target === 2){
					$this->generateAndAddPiece($start, $pieces, $random, $x1 - 3, $y0 - 1 + self::pnxNextBoundedInt($random, 3), $z0 - 1, self::NORTH, $depth);
				}else{
					$this->generateAndAddPiece($start, $pieces, $random, $x1 - 3, $y0 - 1 + self::pnxNextBoundedInt($random, 3), $z1 + 1, self::SOUTH, $depth);
				}
				break;
		}

		if($depth < self::MAX_DEPTH){
			if($piece["direction"] !== self::NORTH && $piece["direction"] !== self::SOUTH){
				for($x = $x0 + 3; $x + 3 <= $x1; $x += 5){
					$type = self::pnxNextBoundedInt($random, 5);
					if($type === 0){
						$this->generateAndAddPiece($start, $pieces, $random, $x, $y0, $z0 - 1, self::NORTH, $depth + 1);
					}elseif($type === 1){
						$this->generateAndAddPiece($start, $pieces, $random, $x, $y0, $z1 + 1, self::SOUTH, $depth + 1);
					}
				}
			}else{
				for($z = $z0 + 3; $z + 3 <= $z1; $z += 5){
					$type = self::pnxNextBoundedInt($random, 5);
					if($type === 0){
						$this->generateAndAddPiece($start, $pieces, $random, $x0 - 1, $y0, $z, self::WEST, $depth + 1);
					}elseif($type === 1){
						$this->generateAndAddPiece($start, $pieces, $random, $x1 + 1, $y0, $z, self::EAST, $depth + 1);
					}
				}
			}
		}
	}

	private function addCrossingChildren(array $piece, array $start, array &$pieces, Random $random){
		list($x0, $y0, $z0, $x1, $y1, $z1) = $piece["box"];
		$depth = $piece["depth"];
		switch($piece["direction"]){
			case self::NORTH:
				$this->generateAndAddPiece($start, $pieces, $random, $x0 + 1, $y0, $z0 - 1, self::NORTH, $depth);
				$this->generateAndAddPiece($start, $pieces, $random, $x0 - 1, $y0, $z0 + 1, self::WEST, $depth);
				$this->generateAndAddPiece($start, $pieces, $random, $x1 + 1, $y0, $z0 + 1, self::EAST, $depth);
				break;
			case self::SOUTH:
				$this->generateAndAddPiece($start, $pieces, $random, $x0 + 1, $y0, $z1 + 1, self::SOUTH, $depth);
				$this->generateAndAddPiece($start, $pieces, $random, $x0 - 1, $y0, $z0 + 1, self::WEST, $depth);
				$this->generateAndAddPiece($start, $pieces, $random, $x1 + 1, $y0, $z0 + 1, self::EAST, $depth);
				break;
			case self::WEST:
				$this->generateAndAddPiece($start, $pieces, $random, $x0 + 1, $y0, $z0 - 1, self::NORTH, $depth);
				$this->generateAndAddPiece($start, $pieces, $random, $x0 + 1, $y0, $z1 + 1, self::SOUTH, $depth);
				$this->generateAndAddPiece($start, $pieces, $random, $x0 - 1, $y0, $z0 + 1, self::WEST, $depth);
				break;
			case self::EAST:
			default:
				$this->generateAndAddPiece($start, $pieces, $random, $x0 + 1, $y0, $z0 - 1, self::NORTH, $depth);
				$this->generateAndAddPiece($start, $pieces, $random, $x0 + 1, $y0, $z1 + 1, self::SOUTH, $depth);
				$this->generateAndAddPiece($start, $pieces, $random, $x1 + 1, $y0, $z0 + 1, self::EAST, $depth);
				break;
		}

		if($piece["twoFloored"]){
			if($random->nextBoolean()){
				$this->generateAndAddPiece($start, $pieces, $random, $x0 + 1, $y0 + 4, $z0 - 1, self::NORTH, $depth);
			}
			if($random->nextBoolean()){
				$this->generateAndAddPiece($start, $pieces, $random, $x0 - 1, $y0 + 4, $z0 + 1, self::WEST, $depth);
			}
			if($random->nextBoolean()){
				$this->generateAndAddPiece($start, $pieces, $random, $x1 + 1, $y0 + 4, $z0 + 1, self::EAST, $depth);
			}
			if($random->nextBoolean()){
				$this->generateAndAddPiece($start, $pieces, $random, $x0 + 1, $y0 + 4, $z1 + 1, self::SOUTH, $depth);
			}
		}
	}

	private function addStairsChildren(array $piece, array $start, array &$pieces, Random $random){
		list($x0, $y0, $z0, $x1, $y1, $z1) = $piece["box"];
		$depth = $piece["depth"];
		switch($piece["direction"]){
			case self::NORTH:
				$this->generateAndAddPiece($start, $pieces, $random, $x0, $y0, $z0 - 1, self::NORTH, $depth);
				break;
			case self::SOUTH:
				$this->generateAndAddPiece($start, $pieces, $random, $x0, $y0, $z1 + 1, self::SOUTH, $depth);
				break;
			case self::WEST:
				$this->generateAndAddPiece($start, $pieces, $random, $x0 - 1, $y0, $z0, self::WEST, $depth);
				break;
			case self::EAST:
			default:
				$this->generateAndAddPiece($start, $pieces, $random, $x1 + 1, $y0, $z0, self::EAST, $depth);
				break;
		}
	}

	private function collides(array $pieces, array $box) : bool{
		foreach($pieces as $piece){
			$other = $piece["box"];
			if($box[3] >= $other[0] && $box[0] <= $other[3] && $box[5] >= $other[2] && $box[2] <= $other[5] && $box[4] >= $other[1] && $box[1] <= $other[4]){
				return true;
			}
		}
		return false;
	}

	private function movePiecesBelowSeaLevel(array &$pieces, Random $random, int $min, string $type){
		$box = $this->calculateBoundingBox($pieces);
		if($type === self::TYPE_MESA){
			$offset = 64 - $box[4] + (int) floor(($box[4] - $box[1] + 1) / 2) + 5;
		}else{
			$range = 64 - $min;
			$y = ($box[4] - $box[1] + 1) - 64 + 1;
			if($y < $range){
				$y += self::pnxNextBoundedInt($random, $range - $y);
			}
			$offset = $y - $box[4];
		}
		foreach($pieces as &$piece){
			$piece["box"][1] += $offset;
			$piece["box"][4] += $offset;
			if(isset($piece["childEntrances"])){
				foreach($piece["childEntrances"] as &$entrance){
					$entrance[1] += $offset;
					$entrance[4] += $offset;
				}
			}
		}
	}

	private function calculateBoundingBox(array $pieces) : array{
		$box = [PHP_INT_MAX, PHP_INT_MAX, PHP_INT_MAX, PHP_INT_MIN, PHP_INT_MIN, PHP_INT_MIN];
		foreach($pieces as $piece){
			$pieceBox = $piece["box"];
			$box[0] = min($box[0], $pieceBox[0]);
			$box[1] = min($box[1], $pieceBox[1]);
			$box[2] = min($box[2], $pieceBox[2]);
			$box[3] = max($box[3], $pieceBox[3]);
			$box[4] = max($box[4], $pieceBox[4]);
			$box[5] = max($box[5], $pieceBox[5]);
		}
		return $box;
	}

	private function placeRoom(array $piece) : bool{
		if($this->edgesLiquid($piece["box"])){
			return false;
		}
		list($x0, $y0, $z0, $x1, $y1, $z1) = $piece["box"];
		$this->fillBox($x0, $y0 + 1, $z0, $x1, min($y0 + 3, $y1), $z1, Block::AIR, 0);
		foreach($piece["childEntrances"] as $entrance){
			$this->fillBox($entrance[0], $entrance[4] - 2, $entrance[2], $entrance[3], $entrance[4], $entrance[5], Block::AIR, 0);
		}
		$this->generateUpperHalfSphere($x0, $y0 + 4, $z0, $x1, $y1, $z1, Block::AIR, 0);
		return true;
	}

	private function placeCorridor(array $piece) : bool{
		if($this->edgesLiquid($piece["box"])){
			return false;
		}
		$z1 = $piece["sections"] * 5 - 1;
		$this->fillLocalBox($piece, 0, 0, 0, 2, 1, $z1, Block::AIR, 0);
		$this->generateMaybeLocalBox($piece, 80, 0, 2, 0, 2, 2, $z1, Block::AIR, 0, false);
		if($piece["spider"]){
			$this->generateMaybeLocalBox($piece, 60, 0, 0, 0, 2, 1, $z1, Block::COBWEB, 0, true);
		}

		for($i = 0; $i < $piece["sections"]; ++$i){
			$z = 2 + $i * 5;
			$this->placeSupport($piece, 0, 0, $z, 2, 2);
			$this->placeCobWeb($piece, 10, 0, 2, $z - 1);
			$this->placeCobWeb($piece, 10, 2, 2, $z - 1);
			$this->placeCobWeb($piece, 10, 0, 2, $z + 1);
			$this->placeCobWeb($piece, 10, 2, 2, $z + 1);
			$this->placeCobWeb($piece, 5, 0, 2, $z - 2);
			$this->placeCobWeb($piece, 5, 2, 2, $z - 2);
			$this->placeCobWeb($piece, 5, 0, 2, $z + 2);
			$this->placeCobWeb($piece, 5, 2, 2, $z + 2);

			if(self::pnxNextBoundedInt($this->random, 100) === 0){
				$this->createChest($piece, 2, 0, $z - 1);
			}
			if(self::pnxNextBoundedInt($this->random, 100) === 0){
				$this->createChest($piece, 0, 0, $z + 1);
			}

			if($piece["spider"] && !$this->placedCaveSpiderSpawner){
				$pz = $z - 1 + self::pnxNextBoundedInt($this->random, 3);
				if($this->isInteriorLocal($piece, 1, 0, $pz)){
					list($x, $y, $worldZ) = $this->localToWorld($piece, 1, 0, $pz);
					$this->setBlock($x, $y, $worldZ, Block::MONSTER_SPAWNER, self::SPAWNER_MARKER_CAVE_SPIDER);
					$this->placedCaveSpiderSpawner = true;
				}
			}
		}

		$planks = $this->planksBlock($piece["mineType"]);
		for($x = 0; $x <= 2; ++$x){
			for($z = 0; $z <= $z1; ++$z){
				$this->setPlanksBlock($piece, $planks, $x, -1, $z);
			}
		}
		$this->placeDoubleLowerOrUpperSupport($piece, 0, -1, 2);
		if($piece["sections"] > 1){
			$this->placeDoubleLowerOrUpperSupport($piece, 0, -1, $z1 - 2);
		}

		if($piece["rails"]){
			$railMeta = $this->railMetaForDirection($piece["direction"]);
			for($z = 0; $z <= $z1; ++$z){
				list($x, $y, $worldZ) = $this->localToWorld($piece, 1, -1, $z);
				if($this->isSolidBlockId($this->getBlockId($x, $y, $worldZ))){
					$probability = $this->isInteriorLocal($piece, 1, 0, $z) ? 70 : 90;
					if(self::pnxNextBoundedInt($this->random, 100) < $probability){
						$this->placeLocal($piece, 1, 0, $z, Block::RAIL, $railMeta);
					}
				}
			}
		}
		return true;
	}

	private function placeSupport(array $piece, int $x0, int $y0, int $z, int $y1, int $x1){
		if(!$this->isSupportingBox($piece, $x0, $x1, $y1, $z)){
			return;
		}
		$fence = $this->fenceBlock($piece["mineType"]);
		$this->fillLocalBox($piece, $x0, $y0, $z, $x0, $y1 - 1, $z, $fence[0], $fence[1]);
		$this->fillLocalBox($piece, $x1, $y0, $z, $x1, $y1 - 1, $z, $fence[0], $fence[1]);

		$planks = $this->planksBlock($piece["mineType"]);
		if(self::pnxNextBoundedInt($this->random, 4) === 0){
			$this->fillLocalBox($piece, $x0, $y1, $z, $x0, $y1, $z, $planks[0], $planks[1]);
			$this->fillLocalBox($piece, $x1, $y1, $z, $x1, $y1, $z, $planks[0], $planks[1]);
		}else{
			$this->fillLocalBox($piece, $x0, $y1, $z, $x1, $y1, $z, $planks[0], $planks[1]);
			$this->maybePlaceLocal($piece, 5, $x0 + 1, $y1, $z - 1, Block::TORCH, $this->torchMeta($piece["direction"], self::SOUTH));
			$this->maybePlaceLocal($piece, 5, $x0 + 1, $y1, $z + 1, Block::TORCH, $this->torchMeta($piece["direction"], self::NORTH));
		}
	}

	private function placeCobWeb(array $piece, int $probability, int $x, int $y, int $z){
		if(!$this->isInteriorLocal($piece, $x, $y, $z)){
			return;
		}
		list($worldX, $worldY, $worldZ) = $this->localToWorld($piece, $x, $y, $z);
		if($this->hasSturdyNeighbours($worldX, $worldY, $worldZ, 2)){
			$this->maybePlaceLocal($piece, $probability, $x, $y, $z, Block::COBWEB, 0);
		}
	}

	private function createChest(array $piece, int $x, int $y, int $z) : bool{
		list($worldX, $worldY, $worldZ) = $this->localToWorld($piece, $x, $y, $z);
		if(!$this->activeContains($worldX, $worldY, $worldZ) || $this->getBlockId($worldX, $worldY, $worldZ) !== Block::AIR || $this->getBlockId($worldX, $worldY - 1, $worldZ) === Block::AIR){
			return false;
		}
		$localRailMeta = $this->random->nextBoolean() ? 0 : 1;
		$this->setBlock($worldX, $worldY, $worldZ, Block::RAIL, $this->railMetaForLocalMeta($piece["direction"], $localRailMeta));
		$this->setExtraData($worldX, $worldY, $worldZ, MineshaftLoot::CHEST_MINECART_MARKER_ID, MineshaftLoot::CHEST_MINECART_MARKER_DATA, MineshaftLoot::CHEST_MINECART_MARKER);
		$this->placedChestMinecart = true;
		return true;
	}

	private function placeDoubleLowerOrUpperSupport(array $piece, int $x, int $y, int $z){
		list($planksId, $planksMeta) = $this->planksBlock($piece["mineType"]);
		list($leftX, $leftY, $leftZ) = $this->localToWorld($piece, $x, $y, $z);
		if($this->getBlockId($leftX, $leftY, $leftZ) === $planksId && $this->getBlockData($leftX, $leftY, $leftZ) === $planksMeta){
			$this->fillPillarDownOrChainUp($piece, $x, $y, $z);
		}
		list($rightX, $rightY, $rightZ) = $this->localToWorld($piece, $x + 2, $y, $z);
		if($this->getBlockId($rightX, $rightY, $rightZ) === $planksId && $this->getBlockData($rightX, $rightY, $rightZ) === $planksMeta){
			$this->fillPillarDownOrChainUp($piece, $x + 2, $y, $z);
		}
	}

	private function fillPillarDownOrChainUp(array $piece, int $x, int $y, int $z){
		list($worldX, $worldY, $worldZ) = $this->localToWorld($piece, $x, $y, $z);
		if(!$this->activeContains($worldX, $worldY, $worldZ)){
			return;
		}
		for($distance = 1; $distance <= 20 && $worldY - $distance > 1; ++$distance){
			$ny = $worldY - $distance;
			$id = $this->level->getBlockIdAt($worldX, $ny, $worldZ);
			if(!$this->isReplaceableSupportId($id)){
				if($this->isSolidBlockId($id)){
					$wood = $this->woodBlock($piece["mineType"]);
					for($py = $ny + 1; $py < $worldY; ++$py){
						$this->setBlock($worldX, $py, $worldZ, $wood[0], $wood[1]);
					}
				}
				return;
			}
			if($id === Block::LAVA || (defined(Block::class . "::FLOWING_LAVA") && $id === constant(Block::class . "::FLOWING_LAVA"))){
				return;
			}
		}

		for($distance = 1; $distance <= 50 && $worldY + $distance < 127; ++$distance){
			$ny = $worldY + $distance;
			$id = $this->level->getBlockIdAt($worldX, $ny, $worldZ);
			if(!$this->isReplaceableSupportId($id)){
				if($this->isSolidBlockId($id) && !$this->isFallableBlockId($id)){
					$fence = $this->fenceBlock($piece["mineType"]);
					$this->setBlock($worldX, $worldY + 1, $worldZ, $fence[0], $fence[1]);
					$chain = $this->chainBlock();
					for($py = $worldY + 2; $py < $ny; ++$py){
						$this->setBlock($worldX, $py, $worldZ, $chain[0], $chain[1]);
					}
				}
				return;
			}
		}
	}

	private function isSupportingBox(array $piece, int $x0, int $x1, int $y, int $z) : bool{
		for($x = $x0; $x <= $x1; ++$x){
			list($worldX, $worldY, $worldZ) = $this->localToWorld($piece, $x, $y + 1, $z);
			if($this->getBlockId($worldX, $worldY, $worldZ) === Block::AIR){
				return false;
			}
		}
		return true;
	}

	private function placeCrossing(array $piece) : bool{
		if($this->edgesLiquid($piece["box"])){
			return false;
		}
		list($x0, $y0, $z0, $x1, $y1, $z1) = $piece["box"];
		if($piece["twoFloored"]){
			$this->fillBox($x0 + 1, $y0, $z0, $x1 - 1, $y0 + 2, $z1, Block::AIR, 0);
			$this->fillBox($x0, $y0, $z0 + 1, $x1, $y0 + 2, $z1 - 1, Block::AIR, 0);
			$this->fillBox($x0 + 1, $y1 - 2, $z0, $x1 - 1, $y1, $z1, Block::AIR, 0);
			$this->fillBox($x0, $y1 - 2, $z0 + 1, $x1, $y1, $z1 - 1, Block::AIR, 0);
			$this->fillBox($x0 + 1, $y0 + 3, $z0 + 1, $x1 - 1, $y0 + 3, $z1 - 1, Block::AIR, 0);
		}else{
			$this->fillBox($x0 + 1, $y0, $z0, $x1 - 1, $y1, $z1, Block::AIR, 0);
			$this->fillBox($x0, $y0, $z0 + 1, $x1, $y1, $z1 - 1, Block::AIR, 0);
		}

		$this->placeSupportPillar($piece, $x0 + 1, $y0, $z0 + 1, $y1);
		$this->placeSupportPillar($piece, $x0 + 1, $y0, $z1 - 1, $y1);
		$this->placeSupportPillar($piece, $x1 - 1, $y0, $z0 + 1, $y1);
		$this->placeSupportPillar($piece, $x1 - 1, $y0, $z1 - 1, $y1);
		return true;
	}

	private function placeSupportPillar(array $piece, int $x, int $y0, int $z, int $y1){
		if($this->getBlockId($x, $y1 + 1, $z) !== Block::AIR){
			$planks = $this->planksBlock($piece["mineType"]);
			$this->fillBox($x, $y0, $z, $x, $y1, $z, $planks[0], $planks[1]);
		}
	}

	private function placeStairs(array $piece) : bool{
		if($this->edgesLiquid($piece["box"])){
			return false;
		}
		$this->fillLocalBox($piece, 0, 5, 0, 2, 7, 1, Block::AIR, 0);
		$this->fillLocalBox($piece, 0, 0, 7, 2, 2, 8, Block::AIR, 0);
		for($i = 0; $i < 5; ++$i){
			$this->fillLocalBox($piece, 0, 5 - $i - ($i < 4 ? 1 : 0), 2 + $i, 2, 7 - $i, 2 + $i, Block::AIR, 0);
		}
		return true;
	}

	private function localToWorld(array $piece, int $x, int $y, int $z) : array{
		$box = $piece["box"];
		switch($piece["direction"]){
			case self::NORTH:
				return [$box[0] + $x, $box[1] + $y, $box[5] - $z];
			case self::SOUTH:
				return [$box[0] + $x, $box[1] + $y, $box[2] + $z];
			case self::WEST:
				return [$box[3] - $z, $box[1] + $y, $box[2] + $x];
			case self::EAST:
			default:
				return [$box[0] + $z, $box[1] + $y, $box[2] + $x];
		}
	}

	private function fillLocalBox(array $piece, int $x0, int $y0, int $z0, int $x1, int $y1, int $z1, int $id, int $meta){
		for($y = $y0; $y <= $y1; ++$y){
			for($x = $x0; $x <= $x1; ++$x){
				for($z = $z0; $z <= $z1; ++$z){
					$this->placeLocal($piece, $x, $y, $z, $id, $meta);
				}
			}
		}
	}

	private function generateMaybeLocalBox(array $piece, int $probability, int $x0, int $y0, int $z0, int $x1, int $y1, int $z1, int $id, int $meta, bool $checkInterior){
		for($y = $y0; $y <= $y1; ++$y){
			for($x = $x0; $x <= $x1; ++$x){
				for($z = $z0; $z <= $z1; ++$z){
					if(self::pnxNextBoundedInt($this->random, 100) <= $probability && (!$checkInterior || $this->isInteriorLocal($piece, $x, $y, $z))){
						$this->placeLocal($piece, $x, $y, $z, $id, $meta);
					}
				}
			}
		}
	}

	private function maybePlaceLocal(array $piece, int $probability, int $x, int $y, int $z, int $id, int $meta){
		if(self::pnxNextBoundedInt($this->random, 100) < $probability){
			$this->placeLocal($piece, $x, $y, $z, $id, $meta);
		}
	}

	private function placeLocal(array $piece, int $x, int $y, int $z, int $id, int $meta){
		list($worldX, $worldY, $worldZ) = $this->localToWorld($piece, $x, $y, $z);
		$this->setBlock($worldX, $worldY, $worldZ, $id, $meta);
	}

	private function fillBox(int $x0, int $y0, int $z0, int $x1, int $y1, int $z1, int $id, int $meta){
		for($x = $x0; $x <= $x1; ++$x){
			for($y = $y0; $y <= $y1; ++$y){
				for($z = $z0; $z <= $z1; ++$z){
					$this->setBlock($x, $y, $z, $id, $meta);
				}
			}
		}
	}

	private function generateUpperHalfSphere(int $x0, int $y0, int $z0, int $x1, int $y1, int $z1, int $id, int $meta){
		$xLen = $x1 - $x0 + 1;
		$yLen = $y1 - $y0 + 1;
		$zLen = $z1 - $z0 + 1;
		$xHalf = $x0 + $xLen / 2;
		$zHalf = $z0 + $zLen / 2;
		for($y = $y0; $y <= $y1; ++$y){
			$dy = ($y - $y0) / $yLen;
			for($x = $x0; $x <= $x1; ++$x){
				$dx = ($x - $xHalf) / ($xLen * 0.5);
				for($z = $z0; $z <= $z1; ++$z){
					$dz = ($z - $zHalf) / ($zLen * 0.5);
					if(($dx * $dx + $dy * $dy + $dz * $dz) <= 1.05){
						$this->setBlock($x, $y, $z, $id, $meta);
					}
				}
			}
		}
	}

	private function setPlanksBlock(array $piece, array $planks, int $x, int $y, int $z){
		if(!$this->isInteriorLocal($piece, $x, $y, $z)){
			return;
		}
		list($worldX, $worldY, $worldZ) = $this->localToWorld($piece, $x, $y, $z);
		$this->setPlanksBlockWorld($planks, $worldX, $worldY, $worldZ);
	}

	private function setPlanksBlockWorld(array $planks, int $x, int $y, int $z){
		if(!$this->activeContains($x, $y, $z)){
			return;
		}
		if(!$this->isSolidBlockId($this->getBlockId($x, $y, $z))){
			$this->setBlock($x, $y, $z, $planks[0], $planks[1]);
		}
	}

	private function setBlock(int $x, int $y, int $z, int $id, int $meta){
		if($y < 1 || $y > 126){
			return;
		}
		if(!$this->activeContains($x, $y, $z)){
			return;
		}
		if($id === Block::AIR){
			if($this->isWaterId($this->level->getBlockIdAt($x, $y, $z))){
				$this->pendingAirBackfill[$this->blockKey($x, $y, $z)] = [$x, $y, $z, "water"];
			}elseif($this->isWaterId($this->level->getBlockIdAt($x, $y + 1, $z))){
				$this->pendingAirBackfill[$this->blockKey($x, $y, $z)] = [$x, $y, $z, "sea"];
			}
		}else{
			unset($this->pendingAirBackfill[$this->blockKey($x, $y, $z)]);
		}
		$this->level->setBlockIdAt($x, $y, $z, $id);
		$this->level->setBlockDataAt($x, $y, $z, $meta);
	}

	private function applyPendingAirBackfill(){
		foreach($this->pendingAirBackfill as $entry){
			list($x, $y, $z, $type) = $entry;
			if($type !== "water" || $this->level->getBlockIdAt($x, $y, $z) !== Block::AIR){
				continue;
			}
			$this->level->setBlockIdAt($x, $y, $z, Block::WATER);
			$this->level->setBlockDataAt($x, $y, $z, 0);
		}

		foreach($this->pendingAirBackfill as $entry){
			list($x, $y, $z, $type) = $entry;
			if($type !== "sea" || $this->level->getBlockIdAt($x, $y, $z) !== Block::AIR){
				continue;
			}
			list($id, $meta) = $this->seaFloorBlock($x, $z);
			$this->level->setBlockIdAt($x, $y, $z, $id);
			$this->level->setBlockDataAt($x, $y, $z, $meta);
		}

		$this->pendingAirBackfill = [];
	}

	private function blockKey(int $x, int $y, int $z) : string{
		return $x . ":" . $y . ":" . $z;
	}

	private function seaFloorBlock(int $x, int $z) : array{
		$seed = (int) $this->level->getSeed();
		$chunkX = $x >> 4;
		$chunkZ = $z >> 4;
		$localX = $x & 0x0f;
		$localZ = $z & 0x0f;
		$biomeId = 0;
		$chunk = $this->level->getChunk($chunkX, $chunkZ);
		if($chunk !== null && method_exists($chunk, "getBiomeId")){
			$biomeId = (int) $chunk->getBiomeId($localX, $localZ);
		}

		return [$this->pickUnderwaterSedimentId($this->getSedimentNoise($seed), $biomeId, $chunkX, $chunkZ, $localX, $localZ), 0];
	}

	private function getSedimentNoise(int $seed) : Simplex{
		if(!isset($this->sedimentNoise[$seed])){
			$random = new Random($seed ^ 0x6d2b79f5);
			$this->sedimentNoise[$seed] = new Simplex($random, 2, 1 / 2, 1 / 64);
		}

		return $this->sedimentNoise[$seed];
	}

	private function pickUnderwaterSedimentId(Simplex $noise, int $biomeId, int $chunkX, int $chunkZ, int $x, int $z) : int{
		$worldX = ($chunkX << 4) + $x;
		$worldZ = ($chunkZ << 4) + $z;
		$value = $noise->noise2D($worldX, $worldZ, true);
		switch($biomeId){
			case 7:
			case 11:
			case 6:
				if($value > 0.34){
					return Block::SAND;
				}
				if($value > 0.10){
					return Block::GRAVEL;
				}
				if($value > -0.24){
					return Block::DIRT;
				}
				return Block::CLAY_BLOCK;

			case 24:
			case 10:
				if($value > 0.30){
					return Block::SAND;
				}
				if($value > -0.10){
					return Block::GRAVEL;
				}
				if($value > -0.34){
					return Block::CLAY_BLOCK;
				}
				return Block::DIRT;

			case 0:
			default:
				if($value > 0.22){
					return Block::SAND;
				}
				if($value > -0.06){
					return Block::GRAVEL;
				}
				if($value > -0.30){
					return Block::DIRT;
				}
				return Block::CLAY_BLOCK;
		}
	}

	private function setExtraData(int $x, int $y, int $z, int $id, int $data, int $marker){
		if($y < 1 || $y > 126){
			return;
		}
		if(!$this->activeContains($x, $y, $z)){
			return;
		}
		if(method_exists($this->level, "setBlockExtraDataAt")){
			$this->level->setBlockExtraDataAt($x, $y, $z, $id, $data);
			return;
		}

		$chunk = $this->level->getChunk($x >> 4, $z >> 4);
		if($chunk !== null && method_exists($chunk, "setBlockExtraData")){
			$chunk->setBlockExtraData($x & 0x0f, $y & 0x7f, $z & 0x0f, $marker);
		}
	}

	private function edgesLiquid(array $box) : bool{
		list($x0, $y0, $z0, $x1, $y1, $z1) = $box;
		if($this->activeBox !== null){
			$x0 = max($x0 - 1, $this->activeBox[0]);
			$y0 = max($y0 - 1, $this->activeBox[1]);
			$z0 = max($z0 - 1, $this->activeBox[2]);
			$x1 = min($x1 + 1, $this->activeBox[3]);
			$y1 = min($y1 + 1, $this->activeBox[4]);
			$z1 = min($z1 + 1, $this->activeBox[5]);
			if($x0 > $x1 || $y0 > $y1 || $z0 > $z1){
				return false;
			}
		}else{
			--$x0;
			--$y0;
			--$z0;
			++$x1;
			++$y1;
			++$z1;
		}
		for($x = $x0; $x <= $x1; ++$x){
			for($z = $z0; $z <= $z1; ++$z){
				if($this->isLiquidId($this->level->getBlockIdAt($x, $y0, $z)) || $this->isLiquidId($this->level->getBlockIdAt($x, $y1, $z))){
					return true;
				}
			}
		}
		for($x = $x0; $x <= $x1; ++$x){
			for($y = $y0; $y <= $y1; ++$y){
				if($this->isLiquidId($this->level->getBlockIdAt($x, $y, $z0)) || $this->isLiquidId($this->level->getBlockIdAt($x, $y, $z1))){
					return true;
				}
			}
		}
		for($z = $z0; $z <= $z1; ++$z){
			for($y = $y0; $y <= $y1; ++$y){
				if($this->isLiquidId($this->level->getBlockIdAt($x0, $y, $z)) || $this->isLiquidId($this->level->getBlockIdAt($x1, $y, $z))){
					return true;
				}
			}
		}
		return false;
	}

	private function isInteriorLocal(array $piece, int $x, int $y, int $z) : bool{
		list($worldX, $worldY, $worldZ) = $this->localToWorld($piece, $x, $y + 1, $z);
		if(!$this->activeContains($worldX, $worldY, $worldZ)){
			return false;
		}
		$chunk = $this->level->getChunk($worldX >> 4, $worldZ >> 4);
		if($chunk !== null && method_exists($chunk, "getHeightMap")){
			return $worldY < (int) $chunk->getHeightMap($worldX & 0x0f, $worldZ & 0x0f);
		}
		return true;
	}

	private function hasSturdyNeighbours(int $x, int $y, int $z, int $required) : bool{
		$sturdy = 0;
		foreach([[0, 1, 0], [0, -1, 0], [0, 0, -1], [0, 0, 1], [-1, 0, 0], [1, 0, 0]] as $offset){
			$nx = $x + $offset[0];
			$ny = $y + $offset[1];
			$nz = $z + $offset[2];
			if($this->activeContains($nx, $ny, $nz) && $this->isSolidBlockId($this->level->getBlockIdAt($nx, $ny, $nz))){
				++$sturdy;
				if($sturdy >= $required){
					return true;
				}
			}
		}
		return false;
	}

	private function getBlockId(int $x, int $y, int $z) : int{
		if(!$this->activeContains($x, $y, $z)){
			return Block::AIR;
		}
		return $this->level->getBlockIdAt($x, $y, $z);
	}

	private function getBlockData(int $x, int $y, int $z) : int{
		if(!$this->activeContains($x, $y, $z)){
			return 0;
		}
		return $this->level->getBlockDataAt($x, $y, $z);
	}

	private function activeContains(int $x, int $y, int $z) : bool{
		return $this->activeBox === null || ($x >= $this->activeBox[0] && $x <= $this->activeBox[3] && $y >= $this->activeBox[1] && $y <= $this->activeBox[4] && $z >= $this->activeBox[2] && $z <= $this->activeBox[5]);
	}

	private function boxesIntersect(array $a, array $b) : bool{
		return $a[3] >= $b[0] && $a[0] <= $b[3] && $a[5] >= $b[2] && $a[2] <= $b[5] && $a[4] >= $b[1] && $a[1] <= $b[4];
	}

	private function isSolidBlockId(int $id) : bool{
		return isset(Block::$solid[$id]) ? (bool) Block::$solid[$id] : $id !== Block::AIR;
	}

	private function isLiquidId(int $id) : bool{
		return $id === Block::WATER || $id === Block::LAVA || (defined(Block::class . "::FLOWING_WATER") && $id === constant(Block::class . "::FLOWING_WATER")) || (defined(Block::class . "::STILL_WATER") && $id === constant(Block::class . "::STILL_WATER")) || (defined(Block::class . "::FLOWING_LAVA") && $id === constant(Block::class . "::FLOWING_LAVA")) || (defined(Block::class . "::STILL_LAVA") && $id === constant(Block::class . "::STILL_LAVA"));
	}

	private function isWaterId(int $id) : bool{
		return $id === Block::WATER || (defined(Block::class . "::FLOWING_WATER") && $id === constant(Block::class . "::FLOWING_WATER")) || (defined(Block::class . "::STILL_WATER") && $id === constant(Block::class . "::STILL_WATER"));
	}

	private function isReplaceableSupportId(int $id) : bool{
		return $id === Block::AIR || $this->isLiquidId($id);
	}

	private function isFallableBlockId(int $id) : bool{
		return (defined(Block::class . "::SAND") && $id === constant(Block::class . "::SAND")) || (defined(Block::class . "::GRAVEL") && $id === constant(Block::class . "::GRAVEL")) || (defined(Block::class . "::ANVIL") && $id === constant(Block::class . "::ANVIL"));
	}

	private function planksBlock(string $type) : array{
		if($type === self::TYPE_MESA){
			return [Block::PLANKS, 5];
		}
		return [Block::PLANKS, 0];
	}

	private function fenceBlock(string $type) : array{
		if($type === self::TYPE_MESA){
			return [Block::FENCE, 5];
		}
		return [Block::FENCE, 0];
	}

	private function chainBlock() : array{
		if(defined(Block::class . "::CHAIN")){
			return [constant(Block::class . "::CHAIN"), 0];
		}
		return [Block::IRON_BARS, 0];
	}

	private function woodBlock(string $type) : array{
		if($type === self::TYPE_MESA && defined(Block::class . "::WOOD2")){
			return [constant(Block::class . "::WOOD2"), 1];
		}
		return [Block::WOOD, 0];
	}

	private function railMetaForDirection(int $direction) : int{
		return ($direction === self::NORTH || $direction === self::SOUTH) ? 0 : 1;
	}

	private function railMetaForLocalMeta(int $roomDirection, int $localMeta) : int{
		if($localMeta === 0){
			return $this->railMetaForDirection($roomDirection);
		}
		return ($roomDirection === self::NORTH || $roomDirection === self::SOUTH) ? 1 : 0;
	}

	private function torchMeta(int $roomDirection, int $localSupportDirection) : int{
		switch($this->rotateDirection($localSupportDirection, $roomDirection)){
			case self::WEST:
				return 1;
			case self::EAST:
				return 2;
			case self::NORTH:
				return 3;
			case self::SOUTH:
			default:
				return 4;
		}
	}

	private function rotateDirection(int $localDirection, int $roomDirection) : int{
		$order = [self::NORTH, self::EAST, self::SOUTH, self::WEST];
		$index = array_search($localDirection, $order, true);
		$turns = 0;
		switch($roomDirection){
			case self::EAST:
				$turns = 1;
				break;
			case self::SOUTH:
				$turns = 2;
				break;
			case self::WEST:
				$turns = 3;
				break;
		}
		return $order[($index + $turns) % 4];
	}

	private function directionName($direction){
		switch($direction){
			case self::NORTH:
				return "north";
			case self::SOUTH:
				return "south";
			case self::WEST:
				return "west";
			case self::EAST:
				return "east";
			default:
				return null;
		}
	}

	private function metadataPieces(array $pieces) : array{
		$result = [];
		foreach($pieces as $piece){
			$entry = [
				"type" => $piece["type"],
				"box" => $piece["box"],
				"direction" => $this->directionName($piece["direction"]),
				"depth" => $piece["depth"]
			];
			foreach(["sections", "rails", "spider", "twoFloored", "mineType"] as $key){
				if(array_key_exists($key, $piece)){
					$entry[$key] = $piece[$key];
				}
			}
			$result[] = $entry;
		}
		return $result;
	}
}

class MineshaftXoroshiroRandom extends Random{
	private $s0 = [0, 0, 0, 0];
	private $s1 = [0, 0, 0, 0];
	private $originalSeed = 0;

	public function __construct($seed = 0){
		$this->setSeed($seed);
	}

	public function setSeed($seed){
		$this->originalSeed = (int) $seed;
		$state = self::splitMix64Seed(self::fromInt((int) $seed));
		$this->s0 = $state[0];
		$this->s1 = $state[1];
	}

	public function nextInt(){
		$long = $this->nextLongLimbs();
		$low = $long[0] | ($long[1] << 16);
		return $low & 0x7fffffff;
	}

	public function nextBoundedInt($max){
		if($max <= 0){
			return 0;
		}
		return $this->nextInt() % ($max + 1);
	}

	public function nextBoolean(){
		$long = $this->nextLongLimbs();
		return ($long[0] & 1) !== 0;
	}

	private function nextLongLimbs() : array{
		$i = $this->s0;
		$j = $this->s1;
		$k = self::add(self::rotateLeft(self::add($i, $j), 17), $i);
		$j = self::xor64($j, $i);
		$this->s0 = self::xor64(self::xor64(self::rotateLeft($i, 49), $j), self::shl($j, 21));
		$this->s1 = self::rotateLeft($j, 28);
		return $k;
	}

	private static function splitMix64Seed(array $seed) : array{
		$out = [];
		$z = $seed;
		$gamma = self::fromHex("9E3779B97F4A7C15");
		$mul1 = self::fromHex("BF58476D1CE4E5B9");
		$mul2 = self::fromHex("94D049BB133111EB");
		for($i = 0; $i < 2; ++$i){
			$z = self::add($z, $gamma);
			$r = $z;
			$r = self::mul(self::xor64($r, self::shr($r, 30)), $mul1);
			$r = self::mul(self::xor64($r, self::shr($r, 27)), $mul2);
			$r = self::xor64($r, self::shr($r, 31));
			$out[] = $r;
		}
		if(self::isZero($out[0]) && self::isZero($out[1])){
			$out[0] = $gamma;
			$out[1] = self::not64($gamma);
		}
		return $out;
	}

	private static function fromInt(int $value) : array{
		$lo = $value & 0xffffffff;
		$hi = ($value >> 32) & 0xffffffff;
		return [$lo & 0xffff, ($lo >> 16) & 0xffff, $hi & 0xffff, ($hi >> 16) & 0xffff];
	}

	private static function fromHex(string $hex) : array{
		$hex = str_pad($hex, 16, "0", STR_PAD_LEFT);
		return [
			hexdec(substr($hex, 12, 4)),
			hexdec(substr($hex, 8, 4)),
			hexdec(substr($hex, 4, 4)),
			hexdec(substr($hex, 0, 4))
		];
	}

	private static function add(array $a, array $b) : array{
		$out = [0, 0, 0, 0];
		$carry = 0;
		for($i = 0; $i < 4; ++$i){
			$sum = $a[$i] + $b[$i] + $carry;
			$out[$i] = $sum & 0xffff;
			$carry = $sum >> 16;
		}
		return $out;
	}

	private static function mul(array $a, array $b) : array{
		$tmp = [0, 0, 0, 0, 0];
		for($i = 0; $i < 4; ++$i){
			for($j = 0; $j < 4 && $i + $j < 4; ++$j){
				$tmp[$i + $j] += $a[$i] * $b[$j];
			}
		}
		$out = [0, 0, 0, 0];
		for($i = 0; $i < 4; ++$i){
			if($tmp[$i] >= 0x10000){
				$tmp[$i + 1] += intdiv($tmp[$i], 0x10000);
			}
			$out[$i] = $tmp[$i] & 0xffff;
		}
		return $out;
	}

	private static function xor64(array $a, array $b) : array{
		return [($a[0] ^ $b[0]) & 0xffff, ($a[1] ^ $b[1]) & 0xffff, ($a[2] ^ $b[2]) & 0xffff, ($a[3] ^ $b[3]) & 0xffff];
	}

	private static function not64(array $a) : array{
		return [(~$a[0]) & 0xffff, (~$a[1]) & 0xffff, (~$a[2]) & 0xffff, (~$a[3]) & 0xffff];
	}

	private static function shl(array $a, int $bits) : array{
		$bits %= 64;
		$words = intdiv($bits, 16);
		$shift = $bits % 16;
		$out = [0, 0, 0, 0];
		for($i = 3; $i >= 0; --$i){
			$source = $i - $words;
			if($source < 0){
				continue;
			}
			$value = ($a[$source] << $shift) & 0xffff;
			if($shift > 0 && $source > 0){
				$value |= ($a[$source - 1] >> (16 - $shift)) & 0xffff;
			}
			$out[$i] = $value & 0xffff;
		}
		return $out;
	}

	private static function shr(array $a, int $bits) : array{
		$bits %= 64;
		$words = intdiv($bits, 16);
		$shift = $bits % 16;
		$out = [0, 0, 0, 0];
		for($i = 0; $i < 4; ++$i){
			$source = $i + $words;
			if($source > 3){
				continue;
			}
			$value = ($a[$source] >> $shift) & 0xffff;
			if($shift > 0 && $source < 3){
				$value |= ($a[$source + 1] << (16 - $shift)) & 0xffff;
			}
			$out[$i] = $value & 0xffff;
		}
		return $out;
	}

	private static function rotateLeft(array $a, int $bits) : array{
		return self::or64(self::shl($a, $bits), self::shr($a, 64 - $bits));
	}

	private static function or64(array $a, array $b) : array{
		return [($a[0] | $b[0]) & 0xffff, ($a[1] | $b[1]) & 0xffff, ($a[2] | $b[2]) & 0xffff, ($a[3] | $b[3]) & 0xffff];
	}

	private static function isZero(array $a) : bool{
		return ($a[0] | $a[1] | $a[2] | $a[3]) === 0;
	}
}
