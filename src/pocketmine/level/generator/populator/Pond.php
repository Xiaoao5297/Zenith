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

namespace pocketmine\level\generator\populator;

use pocketmine\block\Block;
use pocketmine\block\Lava;
use pocketmine\block\Water;
use pocketmine\level\ChunkManager;
use pocketmine\level\generator\biome\Biome;
use pocketmine\level\Level;
use pocketmine\math\Vector3;
use pocketmine\utils\Random;

class Pond extends Populator{
	const PNX_FEATURE_NAME = "minecraft:overworld_surface_springs_feature";
	const PNX_FEATURE_NAME_HASH = -1618120803;
	const WATER_SPRING_ATTEMPTS = 25;
	const LAVA_SPRING_ATTEMPTS = 20;
	const SURFACE_LAVA_POOL_REGION_SIZE = 8;
	const SURFACE_LAVA_POOL_SALT = 14357621;
	const SURFACE_LAVA_POOL_ATTEMPTS = 4;
	const LAKE_SIZE_X = 16;
	const LAKE_SIZE_Y = 8;
	const LAKE_SIZE_Z = 16;
	const LAKE_FLUID_LEVEL = 4;

	private $waterOdd = 4;
	private $lavaOdd = 4;
	private $lavaSurfaceOdd = 4;

	public function populate(ChunkManager $level, $chunkX, $chunkZ, Random $random){
		$random->setSeed((int) $level->getSeed() ^ Level::chunkHash((int) $chunkX, (int) $chunkZ) ^ self::PNX_FEATURE_NAME_HASH);

		new Water();
		$this->placeSprings($level, (int) $chunkX, (int) $chunkZ, self::WATER_SPRING_ATTEMPTS, 0, 127, Block::WATER, $random);

		new Lava();
		$this->placeSprings($level, (int) $chunkX, (int) $chunkZ, self::LAVA_SPRING_ATTEMPTS, 0, 119, Block::LAVA, $random);

		$this->placeRegionalSurfaceLavaPool($level, (int) $chunkX, (int) $chunkZ);
	}

	public function placeLavaPoolAt(ChunkManager $level, int $x, int $y, int $z){
		$this->placeLiquidPoolAt($level, $x, $y, $z, Block::LAVA);
	}

	public function placeSurfaceLavaPoolAt(ChunkManager $level, int $x, int $z) : bool{
		$surfaceY = $this->getSurfaceY($level, $x, $z) - 1;
		if($surfaceY < 4){
			return false;
		}

		$random = new Random(0);
		$random->setSeed(((int) $level->getSeed()) ^ Level::chunkHash($x, $z) ^ self::SURFACE_LAVA_POOL_SALT);
		return $this->placeLakeAt($level, $x, $surfaceY, $z, Block::LAVA, $random);
	}

	public function placeSpringAt(ChunkManager $level, int $x, int $y, int $z, int $id) : bool{
		if(!$this->canPlaceSpring($level, $x, $y, $z)){
			return false;
		}

		$level->setBlockIdAt($x, $y, $z, $id);
		$level->setBlockDataAt($x, $y, $z, 0);
		$this->scheduleSpringUpdate($level, $x, $y, $z);
		return true;
	}

	private function placeSprings(ChunkManager $level, int $chunkX, int $chunkZ, int $count, int $minY, int $maxY, int $id, Random $random){
		if($maxY < $minY){
			return;
		}

		$sourceX = $chunkX << 4;
		$sourceZ = $chunkZ << 4;
		for($i = 0; $i < $count; ++$i){
			$x = $sourceX + $random->nextBoundedInt(14) + 1;
			$y = $id === Block::LAVA ? $this->nextVeryBiasedToBottomY($random, $minY, $maxY) : $random->nextRange($minY, $maxY);
			$z = $sourceZ + $random->nextBoundedInt(14) + 1;
			$this->placeSpringAt($level, $x, $y, $z, $id);
		}
	}

	private function placeRegionalSurfaceLavaPool(ChunkManager $level, int $chunkX, int $chunkZ) : bool{
		if(!$this->isDesertChunk($level, $chunkX, $chunkZ)){
			return false;
		}

		$regionX = self::floorDiv($chunkX, self::SURFACE_LAVA_POOL_REGION_SIZE);
		$regionZ = self::floorDiv($chunkZ, self::SURFACE_LAVA_POOL_REGION_SIZE);
		$candidate = self::findSurfaceLavaPoolCandidate((int) $level->getSeed(), $regionX, $regionZ);
		if($candidate["chunkX"] !== $chunkX || $candidate["chunkZ"] !== $chunkZ){
			return false;
		}

		$random = new Random(0);
		$random->setSeed(((int) $level->getSeed()) ^ Level::chunkHash($chunkX, $chunkZ) ^ self::SURFACE_LAVA_POOL_SALT);
		for($attempt = 0; $attempt < self::SURFACE_LAVA_POOL_ATTEMPTS; ++$attempt){
			$x = ($chunkX << 4) + 2 + $random->nextBoundedInt(12);
			$z = ($chunkZ << 4) + 2 + $random->nextBoundedInt(12);
			if($this->placeSurfaceLavaPoolAt($level, $x, $z)){
				return true;
			}
		}

		return false;
	}

	public static function findSurfaceLavaPoolCandidate(int $seed, int $regionX, int $regionZ) : array{
		$random = new Random(0);
		$random->setSeed(($seed ^ self::SURFACE_LAVA_POOL_SALT) + Level::chunkHash($regionX, $regionZ));
		return [
			"chunkX" => ($regionX * self::SURFACE_LAVA_POOL_REGION_SIZE) + $random->nextBoundedInt(self::SURFACE_LAVA_POOL_REGION_SIZE),
			"chunkZ" => ($regionZ * self::SURFACE_LAVA_POOL_REGION_SIZE) + $random->nextBoundedInt(self::SURFACE_LAVA_POOL_REGION_SIZE),
		];
	}

	private function isDesertChunk(ChunkManager $level, int $chunkX, int $chunkZ) : bool{
		$chunk = $level->getChunk($chunkX, $chunkZ);
		if($chunk !== null && method_exists($chunk, "getBiomeId")){
			return in_array((int) $chunk->getBiomeId(7, 7), [Biome::DESERT, Biome::DESERT_HILLS], true);
		}

		return false;
	}

	private static function floorDiv(int $value, int $divisor) : int{
		$result = intdiv($value, $divisor);
		if(($value ^ $divisor) < 0 && $result * $divisor !== $value){
			--$result;
		}
		return $result;
	}

	private function nextVeryBiasedToBottomY(Random $random, int $minY, int $maxY) : int{
		if($maxY - $minY - 8 + 1 <= 0){
			return $minY;
		}

		$upperInclusive = $random->nextRange($minY + 8, $maxY);
		$biasedUpperInclusive = $random->nextRange($minY, $upperInclusive - 1);
		return $random->nextRange($minY, $biasedUpperInclusive - 1 + 8);
	}

	private function placeLiquidPoolAt(ChunkManager $level, int $x, int $y, int $z, int $id){
		foreach([
			[0, 0], [1, 0], [-1, 0], [0, 1], [0, -1],
			[1, 1], [1, -1], [-1, 1], [-1, -1],
			[2, 0], [-2, 0], [0, 2], [0, -2],
		] as $offset){
			$wx = $x + $offset[0];
			$wz = $z + $offset[1];
			if(abs($offset[0]) + abs($offset[1]) > 2 && (($wx + $wz) & 1) !== 0){
				continue;
			}
			$level->setBlockIdAt($wx, $y, $wz, $id);
			$level->setBlockDataAt($wx, $y, $wz, 0);
		}
	}

	private function placeLakeAt(ChunkManager $level, int $centerX, int $surfaceY, int $centerZ, int $liquidId, Random $random) : bool{
		$originX = $centerX - 8;
		$originY = $surfaceY - (self::LAKE_FLUID_LEVEL - 1);
		$originZ = $centerZ - 8;
		$mask = $this->buildLakeMask($random);

		if(!$this->canPlaceLakeMask($level, $originX, $originY, $originZ, $mask, $liquidId)){
			return false;
		}

		for($localX = 0; $localX < self::LAKE_SIZE_X; ++$localX){
			for($localZ = 0; $localZ < self::LAKE_SIZE_Z; ++$localZ){
				for($localY = 0; $localY < self::LAKE_SIZE_Y; ++$localY){
					if(!$this->isLakeMaskSet($mask, $localX, $localY, $localZ)){
						continue;
					}

					$blockId = $localY >= self::LAKE_FLUID_LEVEL ? Block::AIR : $liquidId;
					$level->setBlockIdAt($originX + $localX, $originY + $localY, $originZ + $localZ, $blockId);
					$level->setBlockDataAt($originX + $localX, $originY + $localY, $originZ + $localZ, 0);
				}
			}
		}

		if($liquidId === Block::LAVA || $liquidId === Block::STILL_LAVA){
			$this->stoneLavaLakeBorder($level, $originX, $originY, $originZ, $mask, $random);
		}

		return true;
	}

	private function buildLakeMask(Random $random) : array{
		$mask = array_fill(0, self::LAKE_SIZE_X * self::LAKE_SIZE_Y * self::LAKE_SIZE_Z, false);
		$ellipsoidCount = $random->nextBoundedInt(4) + 4;
		for($i = 0; $i < $ellipsoidCount; ++$i){
			$sizeX = $this->nextUnitFloat($random) * 6.0 + 3.0;
			$sizeY = $this->nextUnitFloat($random) * 4.0 + 2.0;
			$sizeZ = $this->nextUnitFloat($random) * 6.0 + 3.0;
			$centerX = $this->nextUnitFloat($random) * (self::LAKE_SIZE_X - $sizeX - 2.0) + 1.0 + ($sizeX / 2.0);
			$centerY = $this->nextUnitFloat($random) * (self::LAKE_SIZE_Y - $sizeY - 4.0) + 2.0 + ($sizeY / 2.0);
			$centerZ = $this->nextUnitFloat($random) * (self::LAKE_SIZE_Z - $sizeZ - 2.0) + 1.0 + ($sizeZ / 2.0);
			$this->addLakeEllipsoid($mask, $centerX, $centerY, $centerZ, $sizeX, $sizeY, $sizeZ);
		}

		$this->addLakeEllipsoid($mask, 8.0, 3.0, 8.0, 7.0, 3.0, 7.0);
		return $mask;
	}

	private function addLakeEllipsoid(array &$mask, float $centerX, float $centerY, float $centerZ, float $sizeX, float $sizeY, float $sizeZ){
		for($localX = 1; $localX < self::LAKE_SIZE_X - 1; ++$localX){
			for($localZ = 1; $localZ < self::LAKE_SIZE_Z - 1; ++$localZ){
				for($localY = 1; $localY < self::LAKE_SIZE_Y - 1; ++$localY){
					$normalizedX = ((float) $localX - $centerX) / ($sizeX / 2.0);
					$normalizedY = ((float) $localY - $centerY) / ($sizeY / 2.0);
					$normalizedZ = ((float) $localZ - $centerZ) / ($sizeZ / 2.0);
					if(($normalizedX * $normalizedX) + ($normalizedY * $normalizedY) + ($normalizedZ * $normalizedZ) < 1.0){
						$mask[$this->lakeMaskIndex($localX, $localY, $localZ)] = true;
					}
				}
			}
		}
	}

	private function canPlaceLakeMask(ChunkManager $level, int $originX, int $originY, int $originZ, array $mask, int $liquidId) : bool{
		for($localX = 0; $localX < self::LAKE_SIZE_X; ++$localX){
			for($localZ = 0; $localZ < self::LAKE_SIZE_Z; ++$localZ){
				for($localY = 0; $localY < self::LAKE_SIZE_Y; ++$localY){
					if($this->isLakeMaskSet($mask, $localX, $localY, $localZ) || !$this->hasLakeMaskNeighbor($mask, $localX, $localY, $localZ)){
						continue;
					}

					$id = $level->getBlockIdAt($originX + $localX, $originY + $localY, $originZ + $localZ);
					if($localY >= self::LAKE_FLUID_LEVEL){
						if($this->isLiquidBlock($id)){
							return false;
						}
					}elseif($id !== $liquidId && !$this->isLakeReplaceableSolid($id)){
						return false;
					}
				}
			}
		}

		return true;
	}

	private function stoneLavaLakeBorder(ChunkManager $level, int $originX, int $originY, int $originZ, array $mask, Random $random){
		for($localX = 0; $localX < self::LAKE_SIZE_X; ++$localX){
			for($localZ = 0; $localZ < self::LAKE_SIZE_Z; ++$localZ){
				for($localY = 0; $localY < self::LAKE_SIZE_Y; ++$localY){
					if($this->isLakeMaskSet($mask, $localX, $localY, $localZ) || !$this->hasLakeMaskNeighbor($mask, $localX, $localY, $localZ)){
						continue;
					}

					$worldX = $originX + $localX;
					$worldY = $originY + $localY;
					$worldZ = $originZ + $localZ;
					$id = $level->getBlockIdAt($worldX, $worldY, $worldZ);
					if($this->isLakeReplaceableSolid($id) && ($localY < self::LAKE_FLUID_LEVEL || $random->nextBoundedInt(2) === 0)){
						$level->setBlockIdAt($worldX, $worldY, $worldZ, Block::STONE);
						$level->setBlockDataAt($worldX, $worldY, $worldZ, 0);
					}
				}
			}
		}
	}

	private function hasLakeMaskNeighbor(array $mask, int $localX, int $localY, int $localZ) : bool{
		foreach([[1, 0, 0], [-1, 0, 0], [0, 1, 0], [0, -1, 0], [0, 0, 1], [0, 0, -1]] as $offset){
			if($this->isLakeMaskSet($mask, $localX + $offset[0], $localY + $offset[1], $localZ + $offset[2])){
				return true;
			}
		}
		return false;
	}

	private function isLakeMaskSet(array $mask, int $localX, int $localY, int $localZ) : bool{
		if($localX < 0 || $localX >= self::LAKE_SIZE_X || $localY < 0 || $localY >= self::LAKE_SIZE_Y || $localZ < 0 || $localZ >= self::LAKE_SIZE_Z){
			return false;
		}
		return $mask[$this->lakeMaskIndex($localX, $localY, $localZ)];
	}

	private function lakeMaskIndex(int $localX, int $localY, int $localZ) : int{
		return (($localX * self::LAKE_SIZE_Z) + $localZ) * self::LAKE_SIZE_Y + $localY;
	}

	private function canPlaceSpring(ChunkManager $level, int $x, int $y, int $z) : bool{
		if(!$this->isValidSpringBlock($level->getBlockIdAt($x, $y + 1, $z)) || !$this->isValidSpringBlock($level->getBlockIdAt($x, $y - 1, $z))){
			return false;
		}

		$current = $level->getBlockIdAt($x, $y, $z);
		if($current !== Block::AIR && !$this->isValidSpringBlock($current)){
			return false;
		}

		$rockCount = 0;
		$holeCount = 0;
		foreach([[0, -1, 0], [0, 0, -1], [0, 0, 1], [-1, 0, 0], [1, 0, 0]] as $offset){
			$id = $level->getBlockIdAt($x + $offset[0], $y + $offset[1], $z + $offset[2]);
			if($this->isValidSpringBlock($id)){
				++$rockCount;
			}
			if($id === Block::AIR){
				++$holeCount;
			}
		}

		return $rockCount === 4 && $holeCount === 1;
	}

	private function isValidSpringBlock(int $id) : bool{
		return $id === Block::STONE || $id === Block::DIRT || $id === Block::GRASS;
	}

	private function scheduleSpringUpdate(ChunkManager $level, int $x, int $y, int $z){
		if(method_exists($level, "scheduleUpdate")){
			$level->scheduleUpdate(new Vector3($x, $y, $z), 1);
		}
	}

	private function isLakeReplaceableSolid(int $id) : bool{
		return $id === Block::STONE || $id === Block::DIRT || $id === Block::GRASS || $id === Block::SAND || $id === Block::SANDSTONE || $id === Block::GRAVEL;
	}

	private function isLiquidBlock(int $id) : bool{
		return $id === Block::WATER || $id === Block::STILL_WATER || $id === Block::LAVA || $id === Block::STILL_LAVA;
	}

	private function nextUnitFloat(Random $random) : float{
		return $random->nextBoundedInt(1000000) / 1000000.0;
	}

	private function getSurfaceY(ChunkManager $level, int $x, int $z) : int{
		for($y = 127; $y > 0; --$y){
			$id = $level->getBlockIdAt($x, $y, $z);
			if($id !== Block::AIR && $id !== Block::WATER && $id !== Block::STILL_WATER && $id !== Block::LAVA && $id !== Block::STILL_LAVA){
				return $y + 1;
			}
		}
		return -1;
	}

	public function setWaterOdd($waterOdd){
		$this->waterOdd = $waterOdd;
	}

	public function setLavaOdd($lavaOdd){
		$this->lavaOdd = $lavaOdd;
	}

	public function setLavaSurfaceOdd($lavaSurfaceOdd){
		$this->lavaSurfaceOdd = $lavaSurfaceOdd;
	}
}
