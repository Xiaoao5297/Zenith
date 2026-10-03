<?php

namespace lycore\level\generator\populator;

use lycore\block\Block;
use lycore\level\ChunkManager;
use lycore\level\generator\biome\Biome;
use lycore\math\Vector3;
use lycore\utils\Random;

class Cave extends Populator{
	const CHUNK_SIZE = 16;
	const WORLD_MIN_Y = 0;
	const WORLD_MAX_Y = 127;
	const LAVA_LEVEL = 8;
	const CARVING_RANGE_CHUNKS = 8;

	const NORMAL_CAVE_PROBABILITY = 0.15;
	const EXTRA_UNDERGROUND_CAVE_PROBABILITY = 0.07;
	const CANYON_PROBABILITY = 0.01;

	const NORMAL_CAVE_BOUND = 15;
	const NORMAL_CAVE_MAX_Y = 180;
	const EXTRA_UNDERGROUND_CAVE_MAX_Y = 47;

	const CANYON_MIN_Y = 10;
	const CANYON_MAX_Y = 67;
	const CANYON_Y_SCALE = 3.0;

	const SALT_NORMAL_CAVE = 0x4f1bbcdc;
	const SALT_EXTRA_UNDERGROUND = 0x6d2b79f5;
	const SALT_CANYON = 0x2f78f3ad;

	/** @var int */
	private $biome = Biome::PLAINS;

	public function populate(ChunkManager $level, $chunkX, $chunkZ, Random $random){
		$chunkX = (int) $chunkX;
		$chunkZ = (int) $chunkZ;
		$chunk = $level->getChunk($chunkX, $chunkZ);
		if($chunk !== null && method_exists($chunk, "getBiomeId")){
			$this->biome = (int) $chunk->getBiomeId(0, 0);
		}

		$this->applyCaveFeature(
			$level,
			$chunkX,
			$chunkZ,
			$random,
			self::SALT_NORMAL_CAVE,
			self::NORMAL_CAVE_PROBABILITY,
			self::NORMAL_CAVE_BOUND,
			self::NORMAL_CAVE_MAX_Y
		);
		$this->applyCaveFeature(
			$level,
			$chunkX,
			$chunkZ,
			$random,
			self::SALT_EXTRA_UNDERGROUND,
			self::EXTRA_UNDERGROUND_CAVE_PROBABILITY,
			self::NORMAL_CAVE_BOUND,
			self::EXTRA_UNDERGROUND_CAVE_MAX_Y
		);
		$this->applyCanyonFeature($level, $chunkX, $chunkZ, $random);
	}

	public function carveCaveAt(ChunkManager $level, Random $random, $x, $y, $z, $length = 96, $thickness = 2.0, $horizontalRotation = null, $verticalRotation = 0.0, $targetChunkX = null, $targetChunkZ = null){
		$x = (float) $x;
		$y = $this->clampFloat((float) $y, self::WORLD_MIN_Y + 1, self::WORLD_MAX_Y - 1);
		$z = (float) $z;
		$length = max(1, (int) $length);
		$thickness = max(0.5, (float) $thickness);
		if($horizontalRotation === null){
			$horizontalRotation = $random->nextFloat() * M_PI * 2.0;
		}else{
			$horizontalRotation = (float) $horizontalRotation;
		}

		$tunnelSeed = $this->nextLongSeed($random);
		if($targetChunkX !== null && $targetChunkZ !== null){
			$this->createTunnel(
				$tunnelSeed,
				$level,
				(int) $targetChunkX,
				(int) $targetChunkZ,
				$x,
				$y,
				$z,
				1.0,
				1.0,
				$thickness,
				$horizontalRotation,
				(float) $verticalRotation,
				0,
				$length,
				1.0,
				-1.0,
				self::WORLD_MIN_Y,
				self::WORLD_MAX_Y,
				self::LAVA_LEVEL
			);
			return;
		}

		$originChunkX = ((int) floor($x)) >> 4;
		$originChunkZ = ((int) floor($z)) >> 4;
		for($chunkX = $originChunkX - self::CARVING_RANGE_CHUNKS; $chunkX <= $originChunkX + self::CARVING_RANGE_CHUNKS; ++$chunkX){
			for($chunkZ = $originChunkZ - self::CARVING_RANGE_CHUNKS; $chunkZ <= $originChunkZ + self::CARVING_RANGE_CHUNKS; ++$chunkZ){
				$this->createTunnel(
					$tunnelSeed,
					$level,
					$chunkX,
					$chunkZ,
					$x,
					$y,
					$z,
					1.0,
					1.0,
					$thickness,
					$horizontalRotation,
					(float) $verticalRotation,
					0,
					$length,
					1.0,
					-1.0,
					self::WORLD_MIN_Y,
					self::WORLD_MAX_Y,
					self::LAVA_LEVEL
				);
			}
		}
	}

	public function carveEllipsoid(ChunkManager $level, int $chunkX, int $chunkZ, float $x, float $y, float $z, float $horizontalRadius, float $verticalRadius, float $floorLevel = -1.0, int $lavaLevel = self::LAVA_LEVEL) : bool{
		$horizontalRadius = max(0.1, $horizontalRadius);
		$verticalRadius = max(0.1, $verticalRadius);
		$chunkXBlock = $chunkX * self::CHUNK_SIZE;
		$chunkZBlock = $chunkZ * self::CHUNK_SIZE;

		$xFrom = (int) floor($x - $horizontalRadius) - $chunkXBlock - 1;
		$xTo = (int) floor($x + $horizontalRadius) - $chunkXBlock + 1;
		$yFrom = (int) floor($y - $verticalRadius) - 1;
		$yTo = (int) floor($y + $verticalRadius) + 1;
		$zFrom = (int) floor($z - $horizontalRadius) - $chunkZBlock - 1;
		$zTo = (int) floor($z + $horizontalRadius) - $chunkZBlock + 1;

		if($xFrom < 0) $xFrom = 0;
		if($xTo > self::CHUNK_SIZE) $xTo = self::CHUNK_SIZE;
		if($zFrom < 0) $zFrom = 0;
		if($zTo > self::CHUNK_SIZE) $zTo = self::CHUNK_SIZE;
		if($yFrom < self::WORLD_MIN_Y + 1) $yFrom = self::WORLD_MIN_Y + 1;
		if($yTo > self::WORLD_MAX_Y - 1) $yTo = self::WORLD_MAX_Y - 1;
		if($xFrom >= $xTo || $zFrom >= $zTo || $yFrom >= $yTo){
			return false;
		}

		if($this->hasLiquid($level, $chunkX, $chunkZ, $xFrom, $xTo, $yFrom, $yTo, $zFrom, $zTo)){
			return false;
		}

		$carved = false;
		$invHorizontalRadius = 1.0 / $horizontalRadius;
		$invVerticalRadius = 1.0 / $verticalRadius;
		for($xx = $xFrom; $xx < $xTo; ++$xx){
			$worldX = $chunkXBlock + $xx;
			$xd = ($worldX + 0.5 - $x) * $invHorizontalRadius;
			$xdSq = $xd * $xd;
			for($zz = $zFrom; $zz < $zTo; ++$zz){
				$worldZ = $chunkZBlock + $zz;
				$zd = ($worldZ + 0.5 - $z) * $invHorizontalRadius;
				$horizontalSq = $xdSq + $zd * $zd;
				if($horizontalSq >= 1.0){
					continue;
				}

				$grassFound = false;
				for($yy = $yTo; $yy > $yFrom; --$yy){
					$yd = ($yy - 0.5 - $y) * $invVerticalRadius;
					if($yd <= $floorLevel || $horizontalSq + $yd * $yd >= 1.0){
						continue;
					}

					$currentId = $level->getBlockIdAt($worldX, $yy, $worldZ);
					if($currentId === Block::GRASS){
						$grassFound = true;
					}

					if($yy <= $lavaLevel){
						if($currentId !== Block::LAVA){
							$level->setBlockIdAt($worldX, $yy, $worldZ, Block::LAVA);
							$level->setBlockDataAt($worldX, $yy, $worldZ, 0);
						}
					}else{
						if($currentId !== Block::AIR){
							$level->setBlockIdAt($worldX, $yy, $worldZ, Block::AIR);
							$level->setBlockDataAt($worldX, $yy, $worldZ, 0);
						}
						$this->restoreSurfaceIfNeeded($level, $worldX, $yy - 1, $worldZ, $grassFound);
						$this->normalizeCaveExposure($level, $worldX, $yy, $worldZ);
					}
					$carved = true;
				}
			}
		}

		return $carved;
	}

	public function carveCanyon(ChunkManager $level, Random $random, int $chunkX, int $chunkZ, float $x, float $y, float $z, float $thickness, float $horizontalRotation, float $verticalRotation, int $distance){
		$distance = max(1, $distance);
		$widthFactorPerHeight = $this->initCanyonWidthFactors($random);
		$xRota = 0.0;
		$yRota = 0.0;
		$centerX = $chunkX * self::CHUNK_SIZE + 8;
		$centerZ = $chunkZ * self::CHUNK_SIZE + 8;

		for($currentStep = 0; $currentStep < $distance; ++$currentStep){
			$horizontalRadius = 1.5 + sin($currentStep * M_PI / $distance) * $thickness;
			$verticalRadius = $this->updateCanyonVerticalRadius($random, $horizontalRadius * self::CANYON_Y_SCALE, $distance, $currentStep);
			$horizontalRadius *= 0.75 + $random->nextFloat() * 0.25;

			$xzCos = cos($verticalRotation);
			$ySin = sin($verticalRotation);
			$x += cos($horizontalRotation) * $xzCos;
			$y += $ySin;
			$z += sin($horizontalRotation) * $xzCos;

			$verticalRotation *= 0.7;
			$verticalRotation += $xRota * 0.05;
			$horizontalRotation += $yRota * 0.05;
			$xRota *= 0.8;
			$yRota *= 0.5;
			$xRota += ($random->nextFloat() - $random->nextFloat()) * $random->nextFloat() * 2.0;
			$yRota += ($random->nextFloat() - $random->nextFloat()) * $random->nextFloat() * 4.0;

			if($random->nextBoundedInt(4) === 0){
				continue;
			}
			if(!$this->canReach($centerX, $centerZ, $x, $z, $currentStep, $distance, $thickness)){
				return;
			}

			$xFrom = (int) floor($x - $horizontalRadius) - $chunkX * self::CHUNK_SIZE - 1;
			$xTo = (int) floor($x + $horizontalRadius) - $chunkX * self::CHUNK_SIZE + 1;
			$yFrom = (int) floor($y - $verticalRadius) - 1;
			$yTo = (int) floor($y + $verticalRadius) + 1;
			$zFrom = (int) floor($z - $horizontalRadius) - $chunkZ * self::CHUNK_SIZE - 1;
			$zTo = (int) floor($z + $horizontalRadius) - $chunkZ * self::CHUNK_SIZE + 1;

			if($xFrom < 0) $xFrom = 0;
			if($xTo > self::CHUNK_SIZE) $xTo = self::CHUNK_SIZE;
			if($zFrom < 0) $zFrom = 0;
			if($zTo > self::CHUNK_SIZE) $zTo = self::CHUNK_SIZE;
			if($yFrom < self::WORLD_MIN_Y + 1) $yFrom = self::WORLD_MIN_Y + 1;
			if($yTo > self::WORLD_MAX_Y - 1) $yTo = self::WORLD_MAX_Y - 1;
			if($xFrom >= $xTo || $zFrom >= $zTo || $yFrom >= $yTo){
				continue;
			}
			if($this->hasLiquid($level, $chunkX, $chunkZ, $xFrom, $xTo, $yFrom, $yTo, $zFrom, $zTo)){
				continue;
			}

			$this->carveCanyonEllipsoid(
				$level,
				$chunkX,
				$chunkZ,
				$x,
				$y,
				$z,
				$horizontalRadius,
				$verticalRadius,
				$xFrom,
				$xTo,
				$yFrom,
				$yTo,
				$zFrom,
				$zTo,
				$widthFactorPerHeight,
				self::LAVA_LEVEL
			);
		}
	}

	public function caves(Random $random, ChunkManager $level, Vector3 $pos, $cave, $tt = false){
		$yaw = isset($cave[0]) ? (float) $cave[0] : $random->nextFloat() * 360.0;
		$length = isset($cave[1]) ? (int) $cave[1] : 96;
		$thickness = isset($cave[4]) ? max(0.75, (float) $cave[4]) : 2.0;
		$horizontalRotation = deg2rad($yaw + 90.0);
		$verticalRotation = $tt ? ($random->nextFloat() - 0.5) * 0.25 : 0.0;
		$this->carveCaveAt($level, $random, $pos->x, $pos->y, $pos->z, $length, $thickness, $horizontalRotation, $verticalRotation);
	}

	public function fdx($x, $y, $z, ChunkManager $level, $liu = false){
		for($i = 1; $i < mt_rand(2, 4); ++$i){
			$this->carveAir($level, $x + $i - 2, $y - 1, $z + 1);
			$this->carveAir($level, $x + $i - 2, $y - 1, $z);
			$this->carveAir($level, $x + $i - 2, $y - 1, $z - 1);
			$this->carveAir($level, $x + $i - 2, $y - 1, $z - 1);
			$this->carveAir($level, $x + $i - 2, $y - 1, $z + 1);
			$this->carveAir($level, $x + $i - 2, $y + 2, $z + 1);
			$this->carveAir($level, $x + $i - 2, $y + 2, $z);
			$this->carveAir($level, $x + $i - 2, $y + 2, $z - 1);
		}
		for($i = 1; $i < mt_rand(3, 6); ++$i){
			$this->carveAir($level, $x + $i - 3, $y + 1, $z + 2);
			$this->carveAir($level, $x + $i - 3, $y + 1, $z + 1);
			$this->carveAir($level, $x + $i - 3, $y + 1, $z);
			$this->carveAir($level, $x + $i - 3, $y + 1, $z - 1);
			$this->carveAir($level, $x + $i - 3, $y + 1, $z - 2);
			$this->carveAir($level, $x + $i - 3, $y, $z + 2);
			$this->carveAir($level, $x + $i - 3, $y, $z + 1);
			$this->carveAir($level, $x + $i - 3, $y, $z);
			$this->carveAir($level, $x + $i - 3, $y, $z - 1);
			$this->carveAir($level, $x + $i - 3, $y, $z - 2);
		}
	}

	public function lavaSpawn(ChunkManager $level, $x, $y, $z){
		for($xx = (int) $x - 20; $xx <= (int) $x + 20; ++$xx){
			for($zz = (int) $z - 20; $zz <= (int) $z + 20; ++$zz){
				for($yy = (int) $y; $yy > (int) $y - 4; --$yy){
					if($yy < self::WORLD_MIN_Y || $yy > self::WORLD_MAX_Y){
						continue;
					}
					if($level->getBlockIdAt($xx, $yy, $zz) === Block::AIR){
						$level->setBlockIdAt($xx, $yy, $zz, Block::LAVA);
						$level->setBlockDataAt($xx, $yy, $zz, 0);
					}
				}
			}
		}
		if($y >= self::WORLD_MIN_Y && $y <= self::WORLD_MAX_Y){
			$level->setBlockIdAt((int) $x, (int) $y, (int) $z, Block::WATER);
			$level->setBlockDataAt((int) $x, (int) $y, (int) $z, 0);
		}
	}

	private function applyCaveFeature(ChunkManager $level, int $chunkX, int $chunkZ, Random $random, int $salt, float $probability, int $caveBound, int $caveMaxY){
		$this->seedRandom($random, ((int) $level->getSeed()) ^ $salt);
		$xSeed = $this->nextLongSeed($random);
		$zSeed = $this->nextLongSeed($random);

		for($sourceChunkX = $chunkX - self::CARVING_RANGE_CHUNKS; $sourceChunkX <= $chunkX + self::CARVING_RANGE_CHUNKS; ++$sourceChunkX){
			for($sourceChunkZ = $chunkZ - self::CARVING_RANGE_CHUNKS; $sourceChunkZ <= $chunkZ + self::CARVING_RANGE_CHUNKS; ++$sourceChunkZ){
				$carvingSeed = $this->mixSeed(((int) $sourceChunkX * $xSeed) ^ ((int) $sourceChunkZ * $zSeed) ^ ((int) $level->getSeed()) ^ $salt);
				$this->seedRandom($random, $carvingSeed);
				$this->carveCaveChunk($level, $random, $sourceChunkX, $sourceChunkZ, $chunkX, $chunkZ, $probability, $caveBound, $caveMaxY);
			}
		}
	}

	private function carveCaveChunk(ChunkManager $level, Random $random, int $sourceChunkX, int $sourceChunkZ, int $targetChunkX, int $targetChunkZ, float $probability, int $caveBound, int $caveMaxY){
		if($random->nextFloat() > $probability){
			return;
		}

		$caveMinY = $this->clampInt(self::WORLD_MIN_Y + self::LAVA_LEVEL, self::WORLD_MIN_Y + 1, self::WORLD_MAX_Y - 1);
		$caveMaxY = $this->clampInt($caveMaxY, $caveMinY, self::WORLD_MAX_Y - 1);
		$maxDistance = self::CARVING_RANGE_CHUNKS * self::CHUNK_SIZE - self::CHUNK_SIZE;
		$caveCount = $random->nextBoundedInt($random->nextBoundedInt($random->nextBoundedInt($caveBound) + 1) + 1);

		for($cave = 0; $cave < $caveCount; ++$cave){
			$x = $sourceChunkX * self::CHUNK_SIZE + $random->nextBoundedInt(self::CHUNK_SIZE);
			$y = $random->nextRange($caveMinY, $caveMaxY);
			$z = $sourceChunkZ * self::CHUNK_SIZE + $random->nextBoundedInt(self::CHUNK_SIZE);
			$horizontalRadiusMultiplier = 0.7 + $random->nextFloat() * 0.7;
			$verticalRadiusMultiplier = 0.8 + $random->nextFloat() * 0.5;
			$floorLevel = -1.0 + $random->nextFloat() * 0.6;

			$tunnels = 1;
			if($random->nextBoundedInt(4) === 0){
				$yScale = 0.1 + $random->nextFloat() * 0.8;
				$thickness = 1.0 + $random->nextFloat() * 6.0;
				$this->createRoom($level, $targetChunkX, $targetChunkZ, $x, $y, $z, $thickness, $yScale, $floorLevel, self::WORLD_MIN_Y, self::WORLD_MAX_Y, self::LAVA_LEVEL);
				$tunnels += $random->nextBoundedInt(4);
			}

			for($i = 0; $i < $tunnels; ++$i){
				$horizontalRotation = $random->nextFloat() * M_PI * 2.0;
				$verticalRotation = ($random->nextFloat() - 0.5) / 4.0;
				$thickness = $this->getTunnelThickness($random);
				$distance = $maxDistance - $random->nextBoundedInt(max(1, (int) ($maxDistance / 4)));
				$this->createTunnel(
					$this->nextLongSeed($random),
					$level,
					$targetChunkX,
					$targetChunkZ,
					$x,
					$y,
					$z,
					$horizontalRadiusMultiplier,
					$verticalRadiusMultiplier,
					$thickness,
					$horizontalRotation,
					$verticalRotation,
					0,
					$distance,
					1.0,
					$floorLevel,
					self::WORLD_MIN_Y,
					self::WORLD_MAX_Y,
					self::LAVA_LEVEL
				);
			}
		}
	}

	private function applyCanyonFeature(ChunkManager $level, int $chunkX, int $chunkZ, Random $random){
		$this->seedRandom($random, ((int) $level->getSeed()) ^ self::SALT_CANYON);
		$xSeed = $this->nextLongSeed($random);
		$zSeed = $this->nextLongSeed($random);

		for($sourceChunkX = $chunkX - self::CARVING_RANGE_CHUNKS; $sourceChunkX <= $chunkX + self::CARVING_RANGE_CHUNKS; ++$sourceChunkX){
			for($sourceChunkZ = $chunkZ - self::CARVING_RANGE_CHUNKS; $sourceChunkZ <= $chunkZ + self::CARVING_RANGE_CHUNKS; ++$sourceChunkZ){
				$carvingSeed = $this->mixSeed(((int) $sourceChunkX * $xSeed) ^ ((int) $sourceChunkZ * $zSeed) ^ ((int) $level->getSeed()) ^ self::SALT_CANYON);
				$this->seedRandom($random, $carvingSeed);
				$this->carveCanyonChunk($level, $random, $sourceChunkX, $sourceChunkZ, $chunkX, $chunkZ);
			}
		}
	}

	private function carveCanyonChunk(ChunkManager $level, Random $random, int $sourceChunkX, int $sourceChunkZ, int $targetChunkX, int $targetChunkZ){
		if($random->nextFloat() > self::CANYON_PROBABILITY){
			return;
		}

		$x = $sourceChunkX * self::CHUNK_SIZE + $random->nextBoundedInt(self::CHUNK_SIZE);
		$y = $this->clampInt(self::CANYON_MIN_Y + $random->nextBoundedInt(self::CANYON_MAX_Y - self::CANYON_MIN_Y + 1), self::WORLD_MIN_Y + 1, self::WORLD_MAX_Y - 1);
		$z = $sourceChunkZ * self::CHUNK_SIZE + $random->nextBoundedInt(self::CHUNK_SIZE);
		$horizontalRotation = $random->nextFloat() * M_PI * 2.0;
		$verticalRotation = $random->nextFloat() * 0.25 - 0.125;
		$thickness = 2.0 + $random->nextFloat() * 4.0;
		$maxDistance = self::CARVING_RANGE_CHUNKS * self::CHUNK_SIZE - self::CHUNK_SIZE;
		$distance = (int) ($maxDistance * (0.75 + $random->nextFloat() * 0.25));

		$this->carveCanyon($level, $random, $targetChunkX, $targetChunkZ, $x, $y, $z, $thickness, $horizontalRotation, $verticalRotation, $distance);
	}

	private function createRoom(ChunkManager $level, int $chunkX, int $chunkZ, float $x, float $y, float $z, float $thickness, float $yScale, float $floorLevel, int $minY, int $maxY, int $lavaLevel){
		$horizontalRadius = 1.5 + sin(M_PI / 2.0) * $thickness;
		$verticalRadius = $horizontalRadius * $yScale;
		$this->carveEllipsoid($level, $chunkX, $chunkZ, $x + 1.0, $y, $z, $horizontalRadius, $verticalRadius, $floorLevel, $lavaLevel);
	}

	private function createTunnel($tunnelSeed, ChunkManager $level, int $chunkX, int $chunkZ, float $x, float $y, float $z, float $horizontalRadiusMultiplier, float $verticalRadiusMultiplier, float $thickness, float $horizontalRotation, float $verticalRotation, int $step, int $distance, float $yScale, float $floorLevel, int $minY, int $maxY, int $lavaLevel){
		$distance = max(1, $distance);
		$random = new Random($this->mixSeed($tunnelSeed));
		$splitPoint = $random->nextBoundedInt(max(1, (int) ($distance / 2))) + (int) ($distance / 4);
		$steep = $random->nextBoundedInt(6) === 0;
		$yRota = 0.0;
		$xRota = 0.0;
		$centerX = $chunkX * self::CHUNK_SIZE + 8;
		$centerZ = $chunkZ * self::CHUNK_SIZE + 8;

		for($currentStep = $step; $currentStep < $distance; ++$currentStep){
			$horizontalRadius = 1.5 + sin(M_PI * $currentStep / $distance) * $thickness;
			$verticalRadius = $horizontalRadius * $yScale;
			$cosX = cos($verticalRotation);
			$x += cos($horizontalRotation) * $cosX;
			$y += sin($verticalRotation);
			$z += sin($horizontalRotation) * $cosX;
			$verticalRotation *= $steep ? 0.92 : 0.7;
			$verticalRotation += $xRota * 0.1;
			$horizontalRotation += $yRota * 0.1;
			$xRota *= 0.9;
			$yRota *= 0.75;
			$xRota += ($random->nextFloat() - $random->nextFloat()) * $random->nextFloat() * 2.0;
			$yRota += ($random->nextFloat() - $random->nextFloat()) * $random->nextFloat() * 4.0;

			if($currentStep === $splitPoint && $thickness > 1.0){
				$this->createTunnel(
					$this->nextLongSeed($random),
					$level,
					$chunkX,
					$chunkZ,
					$x,
					$y,
					$z,
					$horizontalRadiusMultiplier,
					$verticalRadiusMultiplier,
					$random->nextFloat() * 0.5 + 0.5,
					$horizontalRotation - M_PI / 2.0,
					$verticalRotation / 3.0,
					$currentStep,
					$distance,
					1.0,
					$floorLevel,
					$minY,
					$maxY,
					$lavaLevel
				);
				$this->createTunnel(
					$this->nextLongSeed($random),
					$level,
					$chunkX,
					$chunkZ,
					$x,
					$y,
					$z,
					$horizontalRadiusMultiplier,
					$verticalRadiusMultiplier,
					$random->nextFloat() * 0.5 + 0.5,
					$horizontalRotation + M_PI / 2.0,
					$verticalRotation / 3.0,
					$currentStep,
					$distance,
					1.0,
					$floorLevel,
					$minY,
					$maxY,
					$lavaLevel
				);
				return;
			}

			if($random->nextBoundedInt(4) !== 0){
				if(!$this->canReach($centerX, $centerZ, $x, $z, $currentStep, $distance, $thickness)){
					return;
				}
				$this->carveEllipsoid(
					$level,
					$chunkX,
					$chunkZ,
					$x,
					$y,
					$z,
					$horizontalRadius * $horizontalRadiusMultiplier,
					$verticalRadius * $verticalRadiusMultiplier,
					$floorLevel,
					$lavaLevel
				);
			}
		}
	}

	private function carveCanyonEllipsoid(ChunkManager $level, int $chunkX, int $chunkZ, float $x, float $y, float $z, float $horizontalRadius, float $verticalRadius, int $xFrom, int $xTo, int $yFrom, int $yTo, int $zFrom, int $zTo, array $widthFactorPerHeight, int $lavaLevel){
		$chunkXBlock = $chunkX * self::CHUNK_SIZE;
		$chunkZBlock = $chunkZ * self::CHUNK_SIZE;
		$invHorizontalRadius = 1.0 / max(0.1, $horizontalRadius);
		$invVerticalRadius = 1.0 / max(0.1, $verticalRadius);

		for($xx = $xFrom; $xx < $xTo; ++$xx){
			$worldX = $chunkXBlock + $xx;
			$xd = ($worldX + 0.5 - $x) * $invHorizontalRadius;
			$xdSq = $xd * $xd;
			for($zz = $zFrom; $zz < $zTo; ++$zz){
				$worldZ = $chunkZBlock + $zz;
				$zd = ($worldZ + 0.5 - $z) * $invHorizontalRadius;
				$horizontalSq = $xdSq + $zd * $zd;
				if($horizontalSq >= 1.0){
					continue;
				}

				$grassFound = false;
				for($yy = $yTo; $yy > $yFrom; --$yy){
					$yIndex = $yy - self::WORLD_MIN_Y;
					if($yIndex <= 0 || $yIndex >= count($widthFactorPerHeight)){
						continue;
					}
					$yd = ($yy - 0.5 - $y) * $invVerticalRadius;
					$shape = $horizontalSq * $widthFactorPerHeight[$yIndex - 1] + $yd * $yd / 6.0;
					if($shape >= 1.0){
						continue;
					}

					$currentId = $level->getBlockIdAt($worldX, $yy, $worldZ);
					if($currentId === Block::GRASS){
						$grassFound = true;
					}
					if($yy <= $lavaLevel){
						if($currentId !== Block::LAVA){
							$level->setBlockIdAt($worldX, $yy, $worldZ, Block::LAVA);
							$level->setBlockDataAt($worldX, $yy, $worldZ, 0);
						}
					}else{
						if($currentId !== Block::AIR){
							$level->setBlockIdAt($worldX, $yy, $worldZ, Block::AIR);
							$level->setBlockDataAt($worldX, $yy, $worldZ, 0);
						}
						$this->restoreSurfaceIfNeeded($level, $worldX, $yy - 1, $worldZ, $grassFound);
						$this->normalizeCaveExposure($level, $worldX, $yy, $worldZ);
					}
				}
			}
		}
	}

	private function hasLiquid(ChunkManager $level, int $chunkX, int $chunkZ, int $xFrom, int $xTo, int $yFrom, int $yTo, int $zFrom, int $zTo) : bool{
		$chunkXBlock = $chunkX * self::CHUNK_SIZE;
		$chunkZBlock = $chunkZ * self::CHUNK_SIZE;
		for($xx = $xFrom; $xx < $xTo; ++$xx){
			$worldX = $chunkXBlock + $xx;
			for($zz = $zFrom; $zz < $zTo; ++$zz){
				$worldZ = $chunkZBlock + $zz;
				for($yy = $yTo + 1; $yy >= $yFrom - 1; --$yy){
					if($yy < self::WORLD_MIN_Y || $yy > self::WORLD_MAX_Y){
						continue;
					}
					if($this->isLiquid($level->getBlockIdAt($worldX, $yy, $worldZ))){
						return true;
					}
				}
			}
		}
		return false;
	}

	private function canReach(float $centerX, float $centerZ, float $x, float $z, int $currentStep, int $distance, float $thickness) : bool{
		$dx = $x - $centerX;
		$dz = $z - $centerZ;
		$remaining = $distance - $currentStep;
		$maxReach = $thickness + 2.0 + self::CHUNK_SIZE;
		return $dx * $dx + $dz * $dz - $remaining * $remaining <= $maxReach * $maxReach;
	}

	private function initCanyonWidthFactors(Random $random) : array{
		$depth = self::WORLD_MAX_Y - self::WORLD_MIN_Y + 1;
		$widthFactorPerHeight = [];
		$widthFactor = 1.0;
		for($yIndex = 0; $yIndex < $depth; ++$yIndex){
			if($yIndex === 0 || $random->nextBoundedInt(3) === 0){
				$widthFactor = 1.0 + $random->nextFloat() * $random->nextFloat();
			}
			$widthFactorPerHeight[$yIndex] = $widthFactor * $widthFactor;
		}
		return $widthFactorPerHeight;
	}

	private function updateCanyonVerticalRadius(Random $random, float $verticalRadius, int $distance, int $currentStep) : float{
		$verticalMultiplier = 1.0 - abs(0.5 - $currentStep / max(1, $distance)) * 2.0;
		$factor = 1.0 + 0.0 * $verticalMultiplier;
		return $factor * $verticalRadius * (0.75 + $random->nextFloat() * 0.25);
	}

	private function getTunnelThickness(Random $random) : float{
		$thickness = $random->nextFloat() * 2.0 + $random->nextFloat();
		if($random->nextBoundedInt(10) === 0){
			$thickness *= $random->nextFloat() * $random->nextFloat() * 3.0 + 1.0;
		}
		return $thickness;
	}

	private function carveAir(ChunkManager $level, $x, $y, $z){
		$x = (int) $x;
		$y = (int) $y;
		$z = (int) $z;
		if($y < self::WORLD_MIN_Y || $y > self::WORLD_MAX_Y){
			return;
		}
		$level->setBlockIdAt($x, $y, $z, Block::AIR);
		$level->setBlockDataAt($x, $y, $z, 0);
		$this->normalizeCaveExposure($level, $x, $y, $z);
	}

	private function restoreSurfaceIfNeeded(ChunkManager $level, int $x, int $y, int $z, bool $grassFound){
		if(!$grassFound || $y < self::WORLD_MIN_Y || $y > self::WORLD_MAX_Y){
			return;
		}
		if($level->getBlockIdAt($x, $y, $z) !== Block::DIRT){
			return;
		}
		$level->setBlockIdAt($x, $y, $z, $this->getBiomeSurfaceReplacement($level, $x, $y, $z));
		$level->setBlockDataAt($x, $y, $z, 0);
	}

	private function normalizeCaveExposure(ChunkManager $level, int $airX, int $airY, int $airZ){
		foreach([[1, 0, 0], [-1, 0, 0], [0, 1, 0], [0, -1, 0], [0, 0, 1], [0, 0, -1]] as $offset){
			$x = $airX + $offset[0];
			$y = $airY + $offset[1];
			$z = $airZ + $offset[2];
			if($y < self::WORLD_MIN_Y + 1 || $y > self::WORLD_MAX_Y || !$this->isDesertColumn($level, $x, $z)){
				continue;
			}
			$id = $level->getBlockIdAt($x, $y, $z);
			if($id === Block::GRASS || $id === Block::DIRT){
				$level->setBlockIdAt($x, $y, $z, $this->getDesertCaveReplacement($y));
				$level->setBlockDataAt($x, $y, $z, 0);
			}
		}
	}

	private function getBiomeSurfaceReplacement(ChunkManager $level, int $x, int $y, int $z) : int{
		if($this->isDesertColumn($level, $x, $z)){
			return $this->getDesertCaveReplacement($y);
		}
		return Block::GRASS;
	}

	private function getDesertCaveReplacement($y) : int{
		return $y >= 58 ? Block::SANDSTONE : Block::STONE;
	}

	private function isDesertColumn(ChunkManager $level, int $x, int $z) : bool{
		$chunk = $level->getChunk($x >> 4, $z >> 4);
		if($chunk !== null && method_exists($chunk, "getBiomeId")){
			$biome = (int) $chunk->getBiomeId($x & 0x0f, $z & 0x0f);
			return $biome === Biome::DESERT || $biome === Biome::DESERT_HILLS;
		}
		return $this->biome === Biome::DESERT || $this->biome === Biome::DESERT_HILLS;
	}

	private function isLiquid(int $id) : bool{
		return $id === Block::WATER || $id === Block::STILL_WATER || $id === Block::LAVA || $id === Block::STILL_LAVA;
	}

	private function seedRandom(Random $random, $seed){
		$random->setSeed($this->mixSeed($seed));
	}

	private function nextLongSeed(Random $random) : int{
		$high = $random->nextSignedInt();
		$low = $random->nextSignedInt();
		return $this->mixSeed(((int) $high << 16) ^ (int) $low);
	}

	private function mixSeed($seed) : int{
		$seed = (int) $seed;
		$seed ^= ($seed >> 16);
		$seed = ($seed * 1103515245 + 12345) & 0x7fffffff;
		$seed ^= ($seed >> 13);
		$seed = ($seed * 1274126177 + 0x9e3779b9) & 0x7fffffff;
		$seed ^= ($seed >> 16);
		return $seed & 0x7fffffff;
	}

	private function clampInt(int $value, int $min, int $max) : int{
		return max($min, min($max, $value));
	}

	private function clampFloat(float $value, float $min, float $max) : float{
		return max($min, min($max, $value));
	}
}
