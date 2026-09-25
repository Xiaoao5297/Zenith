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

use pocketmine\block\Block;
use pocketmine\level\ChunkManager;
use pocketmine\level\generator\biome\Biome;
use pocketmine\level\generator\populator\Populator;
use pocketmine\level\Level;
use pocketmine\utils\Random;

require_once dirname(__DIR__) . "/object/JungleTemple.php";

class JungleTemple extends Populator{
	const REGION_SIZE = 32;
	const MIN_DISTANCE = 8;
	const CANDIDATE_SPAN = 24;
	const SALT = 14357619;

	/** @var ChunkManager */
	private $level;

	public function populate(ChunkManager $level, $chunkX, $chunkZ, Random $random){
		$this->level = $level;
		$chunk = $level->getChunk((int) $chunkX, (int) $chunkZ);
		$biomeId = $chunk !== null && method_exists($chunk, "getBiomeId") ? $chunk->getBiomeId(7, 7) : Biome::JUNGLE;
		if(!self::isJungleTempleBiome($biomeId)){
			return;
		}

		$candidate = self::findRegionJungleTempleCandidate(
			(int) $level->getSeed(),
			self::floorDiv((int) $chunkX, self::REGION_SIZE),
			self::floorDiv((int) $chunkZ, self::REGION_SIZE)
		);
		if($candidate["chunkX"] !== (int) $chunkX || $candidate["chunkZ"] !== (int) $chunkZ){
			return;
		}

		$this->placeJungleTempleAtCandidate($level, (int) $chunkX, (int) $chunkZ, $random);
	}

	public function placeJungleTempleAtCandidate(ChunkManager $level, int $chunkX, int $chunkZ, Random $random){
		if(!$this->canPopulateOverworldStructure($level)){
			return null;
		}

		$this->level = $level;
		$origin = self::getJungleTempleOrigin((int) $level->getSeed(), (int) $chunkX, (int) $chunkZ);
		$x = $origin["x"];
		$z = $origin["z"];

		$existing = $this->findExistingJungleTemple($level, $x, $z);
		if($existing !== null){
			return $existing;
		}

		$y = $this->getJungleTempleBaseY($x, $z);
		if($y <= 0){
			return null;
		}

		$temple = new \pocketmine\level\generator\normal\object\JungleTemple();
		$temple->placeObject($level, $x, $y, $z, $random);
		$placement = self::getJungleTemplePlacement($x, $y - 3, $z);
		$footprint = self::getJungleTempleFootprintChunksFromOrigin($x, $z);
		$this->markStructureFootprint($level, $footprint);
		$this->processLiveLevelFootprint($level, $footprint);

		return $placement;
	}

	public static function getJungleTempleOrigin(int $seed, int $chunkX, int $chunkZ) : array{
		$random = new Random(0);
		$random->setSeed($seed ^ Level::chunkHash($chunkX, $chunkZ));
		return [
			"x" => ($chunkX << 4) + $random->nextBoundedInt(15),
			"z" => ($chunkZ << 4) + $random->nextBoundedInt(15),
		];
	}

	public static function getJungleTempleFootprintChunks(int $seed, int $chunkX, int $chunkZ) : array{
		$origin = self::getJungleTempleOrigin($seed, $chunkX, $chunkZ);
		return self::getJungleTempleFootprintChunksFromOrigin($origin["x"], $origin["z"]);
	}

	public static function getPopulationRadiusForChunk(int $seed, int $chunkX, int $chunkZ) : int{
		$candidate = self::findRegionJungleTempleCandidate(
			$seed,
			self::floorDiv($chunkX, self::REGION_SIZE),
			self::floorDiv($chunkZ, self::REGION_SIZE)
		);
		if((int) $candidate["chunkX"] !== $chunkX || (int) $candidate["chunkZ"] !== $chunkZ){
			return 1;
		}

		return self::getFootprintPopulationRadius(self::getJungleTempleFootprintChunks($seed, $chunkX, $chunkZ), $chunkX, $chunkZ);
	}

	public static function getJungleTempleFootprintChunksFromOrigin(int $originX, int $originZ) : array{
		$chunks = [];
		$minChunkX = $originX >> 4;
		$maxChunkX = ($originX + \pocketmine\level\generator\normal\object\JungleTemple::WIDTH_X - 1) >> 4;
		$minChunkZ = $originZ >> 4;
		$maxChunkZ = ($originZ + \pocketmine\level\generator\normal\object\JungleTemple::WIDTH_Z - 1) >> 4;
		for($chunkX = $minChunkX; $chunkX <= $maxChunkX; ++$chunkX){
			for($chunkZ = $minChunkZ; $chunkZ <= $maxChunkZ; ++$chunkZ){
				$chunks[] = ["chunkX" => $chunkX, "chunkZ" => $chunkZ];
			}
		}

		return $chunks;
	}

	public static function findRegionJungleTempleCandidate(int $seed, int $regionX, int $regionZ) : array{
		$random = new Random(0);
		$random->setSeed(($seed ^ self::SALT) + Level::chunkHash($regionX, $regionZ));
		$chunkX = ($regionX * self::REGION_SIZE) + $random->nextBoundedInt(self::CANDIDATE_SPAN);
		$chunkZ = ($regionZ * self::REGION_SIZE) + $random->nextBoundedInt(self::CANDIDATE_SPAN);
		return [
			"chunkX" => $chunkX,
			"chunkZ" => $chunkZ,
			"centerX" => ($chunkX << 4) + 8,
			"centerZ" => ($chunkZ << 4) + 8,
		];
	}

	public static function isJungleTempleBiome($biomeId) : bool{
		return (int) $biomeId === Biome::JUNGLE;
	}

	public static function floorDiv(int $value, int $divisor) : int{
		$result = intdiv($value, $divisor);
		if($value < 0 && ($value % $divisor) !== 0){
			--$result;
		}
		return $result;
	}

	private static function getJungleTemplePlacement(int $originX, int $originY, int $originZ) : array{
		return [
			"originX" => $originX,
			"originY" => $originY,
			"originZ" => $originZ,
			"targetX" => $originX + 5,
			"targetY" => min(126, $originY + 15),
			"targetZ" => $originZ + 7,
		];
	}

	private function findExistingJungleTemple(ChunkManager $level, int $originX, int $originZ){
		for($originY = 1; $originY <= 112; ++$originY){
			if(
				$level->getBlockIdAt($originX + 8, $originY + 1, $originZ + 3) === Block::CHEST &&
				$level->getBlockIdAt($originX + 3, $originY + 2, $originZ + 1) === Block::DISPENSER &&
				$level->getBlockIdAt($originX + 9, $originY + 2, $originZ + 3) === Block::DISPENSER
			){
				return self::getJungleTemplePlacement($originX, $originY, $originZ);
			}
		}

		return null;
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

	protected function getJungleTempleBaseY($x, $z){
		$surface = [];
		for($xx = $x; $xx <= $x + 11; $xx += 5){
			for($zz = $z; $zz <= $z + 14; $zz += 5){
				$y = $this->getHighestWorkableBlock($xx, $zz);
				if($y <= 0){
					return -1;
				}
				$surface[] = $y;
			}
		}

		sort($surface);
		return $surface[(int) floor(count($surface) / 2)];
	}

	protected function getHighestWorkableBlock($x, $z){
		for($y = 127; $y > 0; --$y){
			$b = $this->level->getBlockIdAt($x, $y, $z);
			if($b === Block::GRASS || $b === Block::DIRT || $b === Block::PODZOL){
				break;
			}
			if($b !== Block::AIR && $b !== Block::LEAVES && $b !== Block::LEAVES2 && $b !== Block::LOG && $b !== Block::LOG2 && $b !== Block::VINE){
				return -1;
			}
		}

		return ++$y;
	}
}
