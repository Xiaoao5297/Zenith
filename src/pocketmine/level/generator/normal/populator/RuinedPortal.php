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

require_once dirname(__DIR__) . "/object/RuinedPortal.php";

class RuinedPortal extends Populator{
	const REGION_SIZE = 40;
	const MIN_DISTANCE = 15;
	const CANDIDATE_SPAN = 25;
	const SALT = 34222645;

	/** @var ChunkManager */
	private $level;

	public function populate(ChunkManager $level, $chunkX, $chunkZ, Random $random){
		$this->level = $level;
		$chunk = $level->getChunk((int) $chunkX, (int) $chunkZ);
		$biomeId = $chunk !== null && method_exists($chunk, "getBiomeId") ? $chunk->getBiomeId(7, 7) : Biome::PLAINS;
		if(!self::isValidRuinedPortalBiome($biomeId)){
			return;
		}

		$candidate = self::findRegionRuinedPortalCandidate(
			(int) $level->getSeed(),
			self::regionCoord((int) $chunkX, self::REGION_SIZE),
			self::regionCoord((int) $chunkZ, self::REGION_SIZE)
		);
		if($candidate["chunkX"] !== (int) $chunkX || $candidate["chunkZ"] !== (int) $chunkZ){
			return;
		}

		$this->placeRuinedPortalAtCandidate($level, (int) $chunkX, (int) $chunkZ, $random);
	}

	public function placeRuinedPortalAtCandidate(ChunkManager $level, int $chunkX, int $chunkZ, Random $random){
		if(!$this->canPopulateOverworldStructure($level)){
			return null;
		}

		$this->level = $level;
		$origin = self::getRuinedPortalOrigin((int) $level->getSeed(), $chunkX, $chunkZ);
		$x = $origin["x"];
		$z = $origin["z"];

		$existing = $this->findExistingRuinedPortal($level, $x, $z);
		if($existing !== null){
			return $existing;
		}

		$y = $this->getRuinedPortalBaseY($x, $z);
		if($y <= 0){
			return null;
		}

		$portal = new \pocketmine\level\generator\normal\object\RuinedPortal();
		$portal->placeObject($level, $x, $y, $z, $random);
		$placement = self::getRuinedPortalPlacement($x, $y, $z);
		$footprint = self::getRuinedPortalFootprintChunksFromOrigin($x, $z);
		$this->markStructureFootprint($level, $footprint);
		$this->processLiveLevelFootprint($level, $footprint);

		return $placement;
	}

	public static function findRegionRuinedPortalCandidate(int $seed, int $regionX, int $regionZ) : array{
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

	public static function getRuinedPortalOrigin(int $seed, int $chunkX, int $chunkZ) : array{
		return [
			"x" => ($chunkX << 4) + 7,
			"z" => ($chunkZ << 4) + 7,
		];
	}

	public static function getRuinedPortalFootprintChunks(int $seed, int $chunkX, int $chunkZ) : array{
		$origin = self::getRuinedPortalOrigin($seed, $chunkX, $chunkZ);
		return self::getRuinedPortalFootprintChunksFromOrigin($origin["x"], $origin["z"]);
	}

	public static function getPopulationRadiusForChunk(int $seed, int $chunkX, int $chunkZ) : int{
		$candidate = self::findRegionRuinedPortalCandidate(
			$seed,
			self::regionCoord($chunkX, self::REGION_SIZE),
			self::regionCoord($chunkZ, self::REGION_SIZE)
		);
		if((int) $candidate["chunkX"] !== $chunkX || (int) $candidate["chunkZ"] !== $chunkZ){
			return 1;
		}

		return self::getFootprintPopulationRadius(self::getRuinedPortalFootprintChunks($seed, $chunkX, $chunkZ), $chunkX, $chunkZ);
	}

	public static function getRuinedPortalFootprintChunksFromOrigin(int $originX, int $originZ) : array{
		$chunks = [];
		$minChunkX = $originX >> 4;
		$maxChunkX = ($originX + \pocketmine\level\generator\normal\object\RuinedPortal::WIDTH_X - 1) >> 4;
		$minChunkZ = $originZ >> 4;
		$maxChunkZ = ($originZ + \pocketmine\level\generator\normal\object\RuinedPortal::WIDTH_Z - 1) >> 4;
		for($chunkX = $minChunkX; $chunkX <= $maxChunkX; ++$chunkX){
			for($chunkZ = $minChunkZ; $chunkZ <= $maxChunkZ; ++$chunkZ){
				$chunks[] = ["chunkX" => $chunkX, "chunkZ" => $chunkZ];
			}
		}

		return $chunks;
	}

	public static function isValidRuinedPortalBiome($biomeId) : bool{
		return (int) $biomeId !== Biome::HELL && (int) $biomeId !== Biome::END;
	}

	public static function regionCoord(int $chunk, int $spacing) : int{
		return $chunk < 0 ? intdiv($chunk - $spacing - 1, $spacing) : intdiv($chunk, $spacing);
	}

	private static function getRuinedPortalPlacement(int $originX, int $originY, int $originZ) : array{
		return [
			"originX" => $originX,
			"originY" => $originY,
			"originZ" => $originZ,
			"targetX" => $originX + 4,
			"targetY" => min(126, $originY + 1),
			"targetZ" => $originZ + 4,
		];
	}

	private function findExistingRuinedPortal(ChunkManager $level, int $originX, int $originZ){
		for($originY = 1; $originY <= 121; ++$originY){
			if(
				$level->getBlockIdAt($originX + 2, $originY + 1, $originZ + 4) === Block::OBSIDIAN &&
				$level->getBlockIdAt($originX + 1, $originY, $originZ + 6) === Block::CHEST
			){
				return self::getRuinedPortalPlacement($originX, $originY, $originZ);
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

	protected function getRuinedPortalBaseY($x, $z){
		$surface = [];
		for($xx = $x; $xx <= $x + 8; $xx += 4){
			for($zz = $z; $zz <= $z + 8; $zz += 4){
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
			if(
				$b === Block::GRASS ||
				$b === Block::DIRT ||
				$b === Block::SAND ||
				$b === Block::STONE ||
				$b === Block::GRAVEL ||
				$b === Block::NETHERRACK ||
				$b === Block::PODZOL
			){
				break;
			}
			if($b !== Block::AIR && $b !== Block::WATER && $b !== Block::STILL_WATER && $b !== Block::LEAVES && $b !== Block::LEAVES2 && $b !== Block::LOG && $b !== Block::LOG2){
				return -1;
			}
		}

		return ++$y;
	}
}
