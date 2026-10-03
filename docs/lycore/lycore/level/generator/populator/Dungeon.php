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

namespace lycore\level\generator\populator;

use lycore\block\Block;
use lycore\level\ChunkManager;
use lycore\utils\Random;

class Dungeon extends Populator{
	const SPAWNER_MARKER_ZOMBIE = 1;
	const SPAWNER_MARKER_SKELETON = 2;
	const SPAWNER_MARKER_CREEPER = 3;

	const WORLD_HEIGHT = 128;

	public function populate(ChunkManager $level, $chunkX, $chunkZ, Random $random){
		if(!$this->canPopulateOverworldStructure($level)){
			return;
		}

		if(Block::$solid === null){
			Block::init();
		}

		$sourceX = $chunkX << 4;
		$sourceZ = $chunkZ << 4;

		for($attempt = 0; $attempt < 8; ++$attempt){
			$x = $sourceX + $random->nextBoundedInt(16) + 8;
			$y = $random->nextBoundedInt(self::WORLD_HEIGHT);
			$z = $sourceZ + $random->nextBoundedInt(16) + 8;

			$xRadius = $random->nextBoundedInt(2) + 2;
			$zRadius = $random->nextBoundedInt(2) + 2;

			if(!$this->canPlaceDungeon($level, $x, $y, $z, $xRadius, $zRadius)){
				continue;
			}

			$this->generateDungeon($level, $x, $y, $z, $xRadius, $zRadius, $random);
		}
	}

	private function canPlaceDungeon(ChunkManager $level, int $x, int $y, int $z, int $xRadius, int $zRadius) : bool{
		if($y < 1 || $y > self::WORLD_HEIGHT - 5){
			return false;
		}

		$x1 = -$xRadius - 1;
		$x2 = $xRadius + 1;
		$z1 = -$zRadius - 1;
		$z2 = $zRadius + 1;
		$openings = 0;

		for($dx = $x1; $dx <= $x2; ++$dx){
			for($dy = -1; $dy <= 4; ++$dy){
				for($dz = $z1; $dz <= $z2; ++$dz){
					$worldX = $x + $dx;
					$worldY = $y + $dy;
					$worldZ = $z + $dz;

					if($dy === -1 && !$this->isSolidBlock($level, $worldX, $worldY, $worldZ)){
						return false;
					}

					if($dy === 4 && !$this->isSolidBlock($level, $worldX, $worldY, $worldZ)){
						return false;
					}

					if(
						($dx === $x1 || $dx === $x2 || $dz === $z1 || $dz === $z2) &&
						$dy === 0 &&
						$level->getBlockIdAt($worldX, $worldY + 1, $worldZ) === Block::AIR
					){
						++$openings;
					}
				}
			}
		}

		return $openings >= 1 && $openings <= 5;
	}

	private function generateDungeon(ChunkManager $level, int $x, int $y, int $z, int $xRadius, int $zRadius, Random $random){
		$x1 = -$xRadius - 1;
		$x2 = $xRadius + 1;
		$z1 = -$zRadius - 1;
		$z2 = $zRadius + 1;

		for($dx = $x1; $dx <= $x2; ++$dx){
			for($dy = 3; $dy >= -1; --$dy){
				for($dz = $z1; $dz <= $z2; ++$dz){
					$worldX = $x + $dx;
					$worldY = $y + $dy;
					$worldZ = $z + $dz;

					if($dx !== $x1 && $dx !== $x2 && $dz !== $z1 && $dz !== $z2 && $dy !== -1){
						$level->setBlockIdAt($worldX, $worldY, $worldZ, Block::AIR);
						$level->setBlockDataAt($worldX, $worldY, $worldZ, 0);
						continue;
					}

					if($worldY >= 0 && !$this->isSolidBlock($level, $worldX, $worldY - 1, $worldZ)){
						$level->setBlockIdAt($worldX, $worldY, $worldZ, Block::AIR);
						$level->setBlockDataAt($worldX, $worldY, $worldZ, 0);
						continue;
					}

					if($this->isSolidBlock($level, $worldX, $worldY, $worldZ)){
						$blockId = $dy === -1 && $random->nextBoundedInt(4) !== 0 ? Block::MOSS_STONE : Block::COBBLESTONE;
						$level->setBlockIdAt($worldX, $worldY, $worldZ, $blockId);
						$level->setBlockDataAt($worldX, $worldY, $worldZ, 0);
					}
				}
			}
		}

		$level->setBlockIdAt($x, $y, $z, Block::MONSTER_SPAWNER);
		$level->setBlockDataAt($x, $y, $z, $this->pickSpawnerMarker($random));
	}

	private function pickSpawnerMarker(Random $random) : int{
		switch($random->nextBoundedInt(3)){
			case 0:
				return self::SPAWNER_MARKER_ZOMBIE;
			case 1:
				return self::SPAWNER_MARKER_SKELETON;
			default:
				return self::SPAWNER_MARKER_CREEPER;
		}
	}

	private function isSolidBlock(ChunkManager $level, int $x, int $y, int $z) : bool{
		if($y < 0 || $y >= self::WORLD_HEIGHT){
			return false;
		}

		return Block::$solid[$level->getBlockIdAt($x, $y, $z)] === true;
	}
}
