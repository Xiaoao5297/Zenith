<?php

namespace pocketmine\level\generator\normal\object;

use pocketmine\block\Block;
use pocketmine\level\ChunkManager;
use pocketmine\utils\Random;

require_once __DIR__ . "/StrongholdLoot.php";

class Stronghold{
	const NORTH = 0;
	const SOUTH = 1;
	const WEST = 2;
	const EAST = 3;
	const SPAWNER_MARKER_SILVERFISH = 5;
	const NATURAL_STRONGHOLD_LADDER_MARKER = 0x534c;
	const NATURAL_STRONGHOLD_LADDER_MARKER_ID = 0x4c;
	const NATURAL_STRONGHOLD_LADDER_MARKER_DATA = 0x53;

	private $level;

	public function placeStrongholdAtCandidate(ChunkManager $level, int $chunkX, int $chunkZ, Random $random) : array{
		$this->level = $level;
		$direction = $random->nextBoundedInt(4);
		$originX = ($chunkX << 4) + 2;
		$originY = 24 + $random->nextBoundedInt(17);
		$originZ = ($chunkZ << 4) + 2;

		$startStairs = $this->placeStartStairs($originX, $originY, $originZ, $direction);
		list($startExitX, $startExitZ) = $this->localToWorld($startStairs["x"], $startStairs["z"], $direction, 5, 5, 2, 4);
		list($fiveEntryX, $fiveEntryZ) = $this->stepForward($startExitX, $startExitZ, $direction);
		list($fiveOriginX, $fiveOriginZ) = $this->originForLocalWorld($fiveEntryX, $fiveEntryZ, $direction, 10, 11, 5, 0);
		$fiveCrossing = $this->placeFiveCrossing($fiveOriginX, $originY, $fiveOriginZ, $direction);
		list($fiveExitX, $fiveExitZ) = $this->localToWorld($fiveCrossing["x"], $fiveCrossing["z"], $direction, 10, 11, 6, 10);
		list($straightEntryX, $straightEntryZ) = $this->stepForward($fiveExitX, $fiveExitZ, $direction);
		list($straightOriginX, $straightOriginZ) = $this->originForLocalWorld($straightEntryX, $straightEntryZ, $direction, 5, 7, 2, 0);
		$straightCorridor = $this->placeStraightCorridor($straightOriginX, $originY, $straightOriginZ, $direction, 7);
		list($straightExitX, $straightExitZ) = $this->localToWorld($straightCorridor["x"], $straightCorridor["z"], $direction, 5, 7, 2, 6);
		list($crossingEntryX, $crossingEntryZ) = $this->stepForward($straightExitX, $straightExitZ, $direction);
		list($crossingOriginX, $crossingOriginZ) = $this->originForLocalWorld($crossingEntryX, $crossingEntryZ, $direction, 10, 10, 5, 0);
		$crossing = $this->placeCrossing($crossingOriginX, $originY, $crossingOriginZ, $direction);
		list($fiveSideX, $fiveSideZ) = $this->localToWorld($fiveCrossing["x"], $fiveCrossing["z"], $direction, 10, 11, $this->leftSideLocalX($direction), 8);
		list($roomEntryX, $roomEntryZ) = $this->stepLeft($fiveSideX, $fiveSideZ, $direction);
		$roomDirection = $this->leftChildDirection($direction);
		list($roomOriginX, $roomOriginZ) = $this->originForLocalWorld($roomEntryX, $roomEntryZ, $roomDirection, 11, 11, 5, 0);
		$roomCrossing = $this->placeRoomCrossing($roomOriginX, $originY + 4, $roomOriginZ, $roomDirection, 1);
		list($roomExitX, $roomExitZ) = $this->localToWorld($roomCrossing["x"], $roomCrossing["z"], $roomDirection, 11, 11, 5, 10);
		list($chestEntryX, $chestEntryZ) = $this->stepForward($roomExitX, $roomExitZ, $roomDirection);
		list($chestOriginX, $chestOriginZ) = $this->originForLocalWorld($chestEntryX, $chestEntryZ, $roomDirection, 5, 7, 2, 0);
		$chestCorridor = $this->placeChestCorridor($chestOriginX, $roomCrossing["y"], $chestOriginZ, $roomDirection);
		list($chestExitX, $chestExitZ) = $this->localToWorld($chestCorridor["x"], $chestCorridor["z"], $roomDirection, 5, 7, 2, 6);
		list($prisonEntryX, $prisonEntryZ) = $this->stepForward($chestExitX, $chestExitZ, $roomDirection);
		list($prisonOriginX, $prisonOriginZ) = $this->originForLocalWorld($prisonEntryX, $prisonEntryZ, $roomDirection, 9, 11, 2, 0);
		$prisonHall = $this->placePrisonHall($prisonOriginX, $chestCorridor["y"], $prisonOriginZ, $roomDirection);
		list($prisonExitX, $prisonExitZ) = $this->localToWorld($prisonHall["x"], $prisonHall["z"], $roomDirection, 9, 11, 2, 10);
		list($libraryEntryX, $libraryEntryZ) = $this->stepForward($prisonExitX, $prisonExitZ, $roomDirection);
		list($libraryOriginX, $libraryOriginZ) = $this->originForLocalWorld($libraryEntryX, $libraryEntryZ, $roomDirection, 14, 15, 5, 0);
		$library = $this->placeLibrary($libraryOriginX, $prisonHall["y"], $libraryOriginZ, $roomDirection);
		list($roomRightX, $roomRightZ) = $this->localToWorld($roomCrossing["x"], $roomCrossing["z"], $roomDirection, 11, 11, $this->rightSideLocalX($roomDirection, 11), 5);
		list($stairsEntryX, $stairsEntryZ) = $this->stepRight($roomRightX, $roomRightZ, $roomDirection);
		$stairsDirection = $this->rightChildDirection($roomDirection);
		list($stairsOriginX, $stairsOriginZ) = $this->originForLocalWorld($stairsEntryX, $stairsEntryZ, $stairsDirection, 5, 8, 2, 0);
		$straightStairsDown = $this->placeStraightStairsDown($stairsOriginX, $roomCrossing["y"] - 6, $stairsOriginZ, $stairsDirection);
		list($stairsExitX, $stairsExitZ) = $this->localToWorld($straightStairsDown["x"], $straightStairsDown["z"], $stairsDirection, 5, 8, 2, 7);
		list($turnEntryX, $turnEntryZ) = $this->stepForward($stairsExitX, $stairsExitZ, $stairsDirection);
		list($leftTurnOriginX, $leftTurnOriginZ) = $this->originForLocalWorld($turnEntryX, $turnEntryZ, $stairsDirection, 5, 5, 2, 0);
		$leftTurn = $this->placeTurn($leftTurnOriginX, $straightStairsDown["y"], $leftTurnOriginZ, $stairsDirection, "left");
		$leftTurnSideX = ($stairsDirection === self::NORTH || $stairsDirection === self::EAST) ? 0 : 4;
		list($leftTurnSideWorldX, $leftTurnSideWorldZ) = $this->localToWorld($leftTurn["x"], $leftTurn["z"], $stairsDirection, 5, 5, $leftTurnSideX, 2);
		list($rightTurnEntryX, $rightTurnEntryZ) = $this->stepRight($leftTurnSideWorldX, $leftTurnSideWorldZ, $stairsDirection);
		$rightTurnDirection = $this->rightChildDirection($stairsDirection);
		list($rightTurnOriginX, $rightTurnOriginZ) = $this->originForLocalWorld($rightTurnEntryX, $rightTurnEntryZ, $rightTurnDirection, 5, 5, 2, 0);
		$rightTurn = $this->placeTurn($rightTurnOriginX, $leftTurn["y"], $rightTurnOriginZ, $rightTurnDirection, "right");
		list($crossingExitX, $crossingExitZ) = $this->localToWorld($crossing["x"], $crossing["z"], $direction, 10, 10, 5, 9);
		list($portalEntryX, $portalEntryZ) = $this->stepForward($crossingExitX, $crossingExitZ, $direction);
		list($portalOriginX, $portalOriginZ) = $this->originForLocalWorld($portalEntryX, $portalEntryZ, $direction, 11, 16, 5, 0);
		$portal = $this->placePortalRoom($level, $portalOriginX, $originY, $portalOriginZ, $direction, $random, false);
		list($targetX, $targetZ) = $this->localToWorld($portal["x"], $portal["z"], $portal["direction"], 11, 16, 5, 3);
		$targetY = $portal["y"] + 2;

		return [
			"pieceCount" => 14,
			"hasPortalRoom" => true,
			"targetX" => $targetX,
			"targetY" => $targetY,
			"targetZ" => $targetZ,
			"startStairs" => $startStairs,
			"straightCorridor" => $straightCorridor,
			"crossing" => $crossing,
			"portalRoom" => $portal,
			"fiveCrossing" => $fiveCrossing,
			"roomCrossing" => $roomCrossing,
			"library" => $library,
			"chestCorridor" => $chestCorridor,
			"prisonHall" => $prisonHall,
			"straightStairsDown" => $straightStairsDown,
			"leftTurn" => $leftTurn,
			"rightTurn" => $rightTurn,
			"direction" => $direction
		];
	}

	public function placePortalRoom(ChunkManager $level, int $originX, int $originY, int $originZ, int $direction, Random $random, bool $forceEyes = false) : array{
		$this->level = $level;

		for($x = 0; $x <= 10; ++$x){
			for($y = 0; $y <= 7; ++$y){
				for($z = 0; $z <= 15; ++$z){
					$isShell = $x === 0 || $x === 10 || $y === 0 || $y === 7 || $z === 0 || $z === 15;
					$this->placeLocal($originX, $originY, $originZ, $direction, 11, 16, $x, $y, $z, $isShell ? Block::STONE_BRICKS : Block::AIR, $isShell ? $this->stoneBrickMeta($random) : 0);
				}
			}
		}

		$this->generateGrateDoor($originX, $originY, $originZ, $direction);
		$this->fillLocal($originX, $originY, $originZ, $direction, 11, 16, 1, 6, 1, 1, 6, 14, Block::STONE_BRICKS, 0);
		$this->fillLocal($originX, $originY, $originZ, $direction, 11, 16, 9, 6, 1, 9, 6, 14, Block::STONE_BRICKS, 0);
		$this->fillLocal($originX, $originY, $originZ, $direction, 11, 16, 2, 6, 1, 8, 6, 2, Block::STONE_BRICKS, 0);
		$this->fillLocal($originX, $originY, $originZ, $direction, 11, 16, 2, 6, 14, 8, 6, 14, Block::STONE_BRICKS, 0);

		$this->fillLocal($originX, $originY, $originZ, $direction, 11, 16, 1, 1, 1, 2, 1, 4, Block::STONE_BRICKS, 0);
		$this->fillLocal($originX, $originY, $originZ, $direction, 11, 16, 8, 1, 1, 9, 1, 4, Block::STONE_BRICKS, 0);
		$this->fillLocal($originX, $originY, $originZ, $direction, 11, 16, 1, 1, 1, 1, 1, 3, Block::LAVA, 0);
		$this->fillLocal($originX, $originY, $originZ, $direction, 11, 16, 9, 1, 1, 9, 1, 3, Block::LAVA, 0);

		$this->fillLocal($originX, $originY, $originZ, $direction, 11, 16, 3, 1, 8, 7, 1, 12, Block::STONE_BRICKS, 0);
		$this->fillLocal($originX, $originY, $originZ, $direction, 11, 16, 4, 1, 9, 6, 1, 11, Block::LAVA, 0);

		for($z = 3; $z < 14; $z += 2){
			$this->fillLocal($originX, $originY, $originZ, $direction, 11, 16, 0, 3, $z, 0, 4, $z, Block::IRON_BARS, 0);
			$this->fillLocal($originX, $originY, $originZ, $direction, 11, 16, 10, 3, $z, 10, 4, $z, Block::IRON_BARS, 0);
		}
		for($x = 2; $x < 9; $x += 2){
			$this->fillLocal($originX, $originY, $originZ, $direction, 11, 16, $x, 3, 15, $x, 4, 15, Block::IRON_BARS, 0);
		}

		$this->fillLocal($originX, $originY, $originZ, $direction, 11, 16, 4, 1, 5, 6, 1, 7, Block::STONE_BRICKS, 0);
		$this->fillLocal($originX, $originY, $originZ, $direction, 11, 16, 4, 2, 6, 6, 2, 7, Block::STONE_BRICKS, 0);
		$this->fillLocal($originX, $originY, $originZ, $direction, 11, 16, 4, 3, 7, 6, 3, 7, Block::STONE_BRICKS, 0);
		$stairMeta = $this->stairMeta($direction, self::NORTH);
		for($x = 4; $x <= 6; ++$x){
			$this->placeLocal($originX, $originY, $originZ, $direction, 11, 16, $x, 1, 4, Block::STONE_BRICK_STAIRS, $stairMeta);
			$this->placeLocal($originX, $originY, $originZ, $direction, 11, 16, $x, 2, 5, Block::STONE_BRICK_STAIRS, $stairMeta);
			$this->placeLocal($originX, $originY, $originZ, $direction, 11, 16, $x, 3, 6, Block::STONE_BRICK_STAIRS, $stairMeta);
		}

		$hasEye = [];
		$active = true;
		for($i = 0; $i < 12; ++$i){
			$hasEye[$i] = $forceEyes || $random->nextBoundedInt(100) > 90;
			$active = $active && $hasEye[$i];
		}

		$this->portalFrame($originX, $originY, $originZ, $direction, 4, 3, 8, self::NORTH, $hasEye[0]);
		$this->portalFrame($originX, $originY, $originZ, $direction, 5, 3, 8, self::NORTH, $hasEye[1]);
		$this->portalFrame($originX, $originY, $originZ, $direction, 6, 3, 8, self::NORTH, $hasEye[2]);
		$this->portalFrame($originX, $originY, $originZ, $direction, 4, 3, 12, self::SOUTH, $hasEye[3]);
		$this->portalFrame($originX, $originY, $originZ, $direction, 5, 3, 12, self::SOUTH, $hasEye[4]);
		$this->portalFrame($originX, $originY, $originZ, $direction, 6, 3, 12, self::SOUTH, $hasEye[5]);
		$this->portalFrame($originX, $originY, $originZ, $direction, 3, 3, 9, self::EAST, $hasEye[6]);
		$this->portalFrame($originX, $originY, $originZ, $direction, 3, 3, 10, self::EAST, $hasEye[7]);
		$this->portalFrame($originX, $originY, $originZ, $direction, 3, 3, 11, self::EAST, $hasEye[8]);
		$this->portalFrame($originX, $originY, $originZ, $direction, 7, 3, 9, self::WEST, $hasEye[9]);
		$this->portalFrame($originX, $originY, $originZ, $direction, 7, 3, 10, self::WEST, $hasEye[10]);
		$this->portalFrame($originX, $originY, $originZ, $direction, 7, 3, 11, self::WEST, $hasEye[11]);

		if($active && defined(Block::class . "::END_PORTAL")){
			$portalId = constant(Block::class . "::END_PORTAL");
			$this->fillLocal($originX, $originY, $originZ, $direction, 11, 16, 4, 3, 9, 6, 3, 11, $portalId, 0);
		}

		$this->placeLocal($originX, $originY, $originZ, $direction, 11, 16, 5, 3, 6, Block::MONSTER_SPAWNER, self::SPAWNER_MARKER_SILVERFISH);

		return [
			"x" => $originX,
			"y" => $originY,
			"z" => $originZ,
			"direction" => $direction,
			"activePortalRequested" => $active,
			"placedPortalBlocks" => $active && defined(Block::class . "::END_PORTAL")
		];
	}

	private function placeStartStairs(int $originX, int $originY, int $originZ, int $direction) : array{
		$this->fillOrientedBox($originX, $originY, $originZ, $direction, 5, 11, 5, Block::STONE_BRICKS, 0, true);
		$this->fillLocal($originX, $originY, $originZ, $direction, 5, 5, 1, 7, 0, 3, 9, 0, Block::AIR, 0);
		$this->fillLocal($originX, $originY, $originZ, $direction, 5, 5, 1, 1, 4, 3, 3, 4, Block::AIR, 0);

		foreach([[2, 6, 1], [1, 5, 1], [1, 5, 2], [1, 4, 3], [2, 4, 3], [3, 3, 3], [3, 3, 2], [3, 2, 1], [2, 2, 1], [1, 1, 1], [1, 1, 2]] as $block){
			$this->placeLocal($originX, $originY, $originZ, $direction, 5, 5, $block[0], $block[1], $block[2], Block::STONE_BRICKS, 0);
		}
		foreach([[1, 6, 1], [1, 5, 3], [3, 4, 3], [3, 3, 1], [1, 2, 1], [1, 1, 3]] as $slab){
			$this->placeLocal($originX, $originY, $originZ, $direction, 5, 5, $slab[0], $slab[1], $slab[2], Block::STONE_SLAB, 5);
		}

		return [
			"x" => $originX,
			"y" => $originY,
			"z" => $originZ,
			"direction" => $direction
		];
	}

	private function placeStraightCorridor(int $originX, int $originY, int $originZ, int $direction, int $length) : array{
		$this->fillOrientedBox($originX, $originY, $originZ, $direction, 5, 5, $length, Block::STONE_BRICKS, 0, true);
		$this->fillLocal($originX, $originY, $originZ, $direction, 5, $length, 1, 1, 0, 3, 3, $length - 1, Block::AIR, 0);
		return [
			"x" => $originX,
			"y" => $originY,
			"z" => $originZ,
			"direction" => $direction,
			"length" => $length
		];
	}

	private function placeCrossing(int $originX, int $originY, int $originZ, int $direction) : array{
		$this->fillOrientedBox($originX, $originY, $originZ, $direction, 10, 7, 10, Block::STONE_BRICKS, 0, true);
		$this->fillLocal($originX, $originY, $originZ, $direction, 10, 10, 1, 1, 1, 8, 5, 8, Block::AIR, 0);
		$this->fillLocal($originX, $originY, $originZ, $direction, 10, 10, 4, 1, 0, 6, 3, 0, Block::AIR, 0);
		$this->fillLocal($originX, $originY, $originZ, $direction, 10, 10, 4, 1, 9, 6, 3, 9, Block::AIR, 0);
		for($x = 2; $x <= 7; $x += 5){
			for($z = 2; $z <= 7; $z += 5){
				$this->fillLocal($originX, $originY, $originZ, $direction, 10, 10, $x, 1, $z, $x, 5, $z, Block::STONE_BRICKS, 0);
			}
		}
		$this->placeLocal($originX, $originY, $originZ, $direction, 10, 10, 3, 3, 8, Block::STONE_BRICKS, 0);
		$this->placeMarkedChest($originX, $originY, $originZ, $direction, 10, 10, 3, 4, 8, StrongholdLoot::CROSSING_CHEST_MARKER_ID, StrongholdLoot::CROSSING_CHEST_MARKER_DATA, StrongholdLoot::CROSSING_CHEST_MARKER);
		return [
			"x" => $originX,
			"y" => $originY,
			"z" => $originZ,
			"direction" => $direction
		];
	}

	private function placeFiveCrossing(int $originX, int $originY, int $originZ, int $direction) : array{
		$this->fillOrientedBox($originX, $originY, $originZ, $direction, 10, 9, 11, Block::STONE_BRICKS, 0, true);
		$this->fillLocal($originX, $originY, $originZ, $direction, 10, 11, 4, 1, 0, 6, 3, 0, Block::AIR, 0);
		$this->fillLocal($originX, $originY, $originZ, $direction, 10, 11, 5, 1, 10, 7, 3, 10, Block::AIR, 0);
		$this->fillLocal($originX, $originY, $originZ, $direction, 10, 11, 0, 3, 1, 0, 5, 3, Block::AIR, 0);
		$this->fillLocal($originX, $originY, $originZ, $direction, 10, 11, 9, 3, 1, 9, 5, 3, Block::AIR, 0);
		$this->fillLocal($originX, $originY, $originZ, $direction, 10, 11, 0, 5, 7, 0, 7, 9, Block::AIR, 0);
		$this->fillLocal($originX, $originY, $originZ, $direction, 10, 11, 9, 5, 7, 9, 7, 9, Block::AIR, 0);

		$this->fillLocal($originX, $originY, $originZ, $direction, 10, 11, 1, 2, 1, 8, 2, 6, Block::STONE_BRICKS, 0);
		$this->fillLocal($originX, $originY, $originZ, $direction, 10, 11, 4, 1, 5, 4, 4, 9, Block::STONE_BRICKS, 0);
		$this->fillLocal($originX, $originY, $originZ, $direction, 10, 11, 8, 1, 5, 8, 4, 9, Block::STONE_BRICKS, 0);
		$this->fillLocal($originX, $originY, $originZ, $direction, 10, 11, 1, 4, 7, 3, 4, 9, Block::STONE_BRICKS, 0);
		$this->fillLocal($originX, $originY, $originZ, $direction, 10, 11, 1, 3, 5, 3, 3, 6, Block::STONE_BRICKS, 0);

		$this->fillLocal($originX, $originY, $originZ, $direction, 10, 11, 1, 3, 4, 3, 3, 4, Block::STONE_SLAB, 5);
		$this->fillLocal($originX, $originY, $originZ, $direction, 10, 11, 1, 4, 6, 3, 4, 6, Block::STONE_SLAB, 5);
		$this->fillLocal($originX, $originY, $originZ, $direction, 10, 11, 5, 1, 7, 7, 1, 8, Block::STONE_BRICKS, 0);
		$this->fillLocal($originX, $originY, $originZ, $direction, 10, 11, 5, 1, 9, 7, 1, 9, Block::STONE_SLAB, 5);
		$this->fillLocal($originX, $originY, $originZ, $direction, 10, 11, 5, 2, 7, 7, 2, 7, Block::STONE_SLAB, 5);
		$this->fillLocal($originX, $originY, $originZ, $direction, 10, 11, 4, 5, 7, 4, 5, 9, Block::STONE_SLAB, 5);
		$this->fillLocal($originX, $originY, $originZ, $direction, 10, 11, 8, 5, 7, 8, 5, 9, Block::STONE_SLAB, 5);
		$this->fillLocal($originX, $originY, $originZ, $direction, 10, 11, 5, 5, 7, 7, 5, 9, Block::DOUBLE_STONE_SLAB, 0);

		$this->placeLocal($originX, $originY, $originZ, $direction, 10, 11, 6, 5, 6, Block::TORCH, $this->torchMeta($direction, self::SOUTH));

		return [
			"x" => $originX,
			"y" => $originY,
			"z" => $originZ,
			"direction" => $direction
		];
	}

	private function placeChestCorridor(int $originX, int $originY, int $originZ, int $direction) : array{
		$this->fillOrientedBox($originX, $originY, $originZ, $direction, 5, 5, 7, Block::STONE_BRICKS, 0, true);
		$this->fillLocal($originX, $originY, $originZ, $direction, 5, 7, 1, 1, 0, 3, 3, 0, Block::AIR, 0);
		$this->fillLocal($originX, $originY, $originZ, $direction, 5, 7, 1, 1, 6, 3, 3, 6, Block::AIR, 0);
		$this->fillLocal($originX, $originY, $originZ, $direction, 5, 7, 3, 1, 2, 3, 1, 4, Block::STONE_BRICKS, 0);

		$this->placeLocal($originX, $originY, $originZ, $direction, 5, 7, 3, 1, 1, Block::STONE_SLAB, 5);
		$this->placeLocal($originX, $originY, $originZ, $direction, 5, 7, 3, 1, 5, Block::STONE_SLAB, 5);
		$this->placeLocal($originX, $originY, $originZ, $direction, 5, 7, 3, 2, 2, Block::STONE_SLAB, 5);
		$this->placeLocal($originX, $originY, $originZ, $direction, 5, 7, 3, 2, 4, Block::STONE_SLAB, 5);
		for($z = 2; $z <= 4; ++$z){
			$this->placeLocal($originX, $originY, $originZ, $direction, 5, 7, 2, 1, $z, Block::STONE_SLAB, 5);
		}

		$this->placeMarkedChest($originX, $originY, $originZ, $direction, 5, 7, 3, 2, 3, StrongholdLoot::CORRIDOR_CHEST_MARKER_ID, StrongholdLoot::CORRIDOR_CHEST_MARKER_DATA, StrongholdLoot::CORRIDOR_CHEST_MARKER);

		return [
			"x" => $originX,
			"y" => $originY,
			"z" => $originZ,
			"direction" => $direction
		];
	}

	private function placeStraightStairsDown(int $originX, int $originY, int $originZ, int $direction) : array{
		$this->fillOrientedBox($originX, $originY, $originZ, $direction, 5, 11, 8, Block::STONE_BRICKS, 0, true);
		$this->fillLocal($originX, $originY, $originZ, $direction, 5, 8, 1, 7, 0, 3, 9, 0, Block::AIR, 0);
		$this->fillLocal($originX, $originY, $originZ, $direction, 5, 8, 1, 1, 7, 3, 3, 7, Block::AIR, 0);

		$stairMeta = $this->stairMeta($direction, self::SOUTH);
		for($i = 0; $i < 6; ++$i){
			for($x = 1; $x <= 3; ++$x){
				$this->placeLocal($originX, $originY, $originZ, $direction, 5, 8, $x, 6 - $i, 1 + $i, Block::STONE_BRICK_STAIRS, $stairMeta);
				if($i < 5){
					$this->placeLocal($originX, $originY, $originZ, $direction, 5, 8, $x, 5 - $i, 1 + $i, Block::STONE_BRICKS, 0);
				}
			}
		}

		return [
			"x" => $originX,
			"y" => $originY,
			"z" => $originZ,
			"direction" => $direction
		];
	}

	private function placeTurn(int $originX, int $originY, int $originZ, int $direction, string $type) : array{
		$this->fillOrientedBox($originX, $originY, $originZ, $direction, 5, 5, 5, Block::STONE_BRICKS, 0, true);
		$this->fillLocal($originX, $originY, $originZ, $direction, 5, 5, 1, 1, 0, 3, 3, 0, Block::AIR, 0);

		$northOrEast = $direction === self::NORTH || $direction === self::EAST;
		if($type === "left"){
			$sideX = $northOrEast ? 0 : 4;
		}else{
			$sideX = $northOrEast ? 4 : 0;
		}
		$this->fillLocal($originX, $originY, $originZ, $direction, 5, 5, $sideX, 1, 1, $sideX, 3, 3, Block::AIR, 0);

		return [
			"x" => $originX,
			"y" => $originY,
			"z" => $originZ,
			"direction" => $direction,
			"type" => $type
		];
	}

	private function placeRoomCrossing(int $originX, int $originY, int $originZ, int $direction, int $type) : array{
		$this->fillOrientedBox($originX, $originY, $originZ, $direction, 11, 7, 11, Block::STONE_BRICKS, 0, true);
		$this->fillLocal($originX, $originY, $originZ, $direction, 11, 11, 4, 1, 0, 6, 3, 0, Block::AIR, 0);
		$this->fillLocal($originX, $originY, $originZ, $direction, 11, 11, 4, 1, 10, 6, 3, 10, Block::AIR, 0);
		$this->fillLocal($originX, $originY, $originZ, $direction, 11, 11, 0, 1, 4, 0, 3, 6, Block::AIR, 0);
		$this->fillLocal($originX, $originY, $originZ, $direction, 11, 11, 10, 1, 4, 10, 3, 6, Block::AIR, 0);

		if($type === 1){
			for($i = 0; $i < 5; ++$i){
				$this->placeLocal($originX, $originY, $originZ, $direction, 11, 11, 3, 1, 3 + $i, Block::STONE_BRICKS, 0);
				$this->placeLocal($originX, $originY, $originZ, $direction, 11, 11, 7, 1, 3 + $i, Block::STONE_BRICKS, 0);
				$this->placeLocal($originX, $originY, $originZ, $direction, 11, 11, 3 + $i, 1, 3, Block::STONE_BRICKS, 0);
				$this->placeLocal($originX, $originY, $originZ, $direction, 11, 11, 3 + $i, 1, 7, Block::STONE_BRICKS, 0);
			}

			$this->placeLocal($originX, $originY, $originZ, $direction, 11, 11, 5, 1, 5, Block::STONE_BRICKS, 0);
			$this->placeLocal($originX, $originY, $originZ, $direction, 11, 11, 5, 2, 5, Block::STONE_BRICKS, 0);
			$this->placeLocal($originX, $originY, $originZ, $direction, 11, 11, 5, 3, 5, Block::STONE_BRICKS, 0);

			$this->placeLocal($originX, $originY, $originZ, $direction, 11, 11, 5, 4, 5, Block::WATER, 0);
			$this->placeLocal($originX, $originY, $originZ, $direction, 11, 11, 6, 4, 5, Block::WATER, 1);
			$this->placeLocal($originX, $originY, $originZ, $direction, 11, 11, 4, 4, 5, Block::WATER, 1);
			$this->placeLocal($originX, $originY, $originZ, $direction, 11, 11, 5, 4, 6, Block::WATER, 1);
			$this->placeLocal($originX, $originY, $originZ, $direction, 11, 11, 5, 4, 4, Block::WATER, 1);
			$this->placeLocal($originX, $originY, $originZ, $direction, 11, 11, 6, 1, 4, Block::WATER, 1);
			$this->placeLocal($originX, $originY, $originZ, $direction, 11, 11, 6, 1, 6, Block::WATER, 1);
			$this->placeLocal($originX, $originY, $originZ, $direction, 11, 11, 4, 1, 4, Block::WATER, 1);
			$this->placeLocal($originX, $originY, $originZ, $direction, 11, 11, 4, 1, 6, Block::WATER, 1);
			for($y = 1; $y <= 3; ++$y){
				$this->placeLocal($originX, $originY, $originZ, $direction, 11, 11, 6, $y, 5, Block::WATER, 9);
				$this->placeLocal($originX, $originY, $originZ, $direction, 11, 11, 4, $y, 5, Block::WATER, 9);
				$this->placeLocal($originX, $originY, $originZ, $direction, 11, 11, 5, $y, 6, Block::WATER, 9);
				$this->placeLocal($originX, $originY, $originZ, $direction, 11, 11, 5, $y, 4, Block::WATER, 9);
			}
		}

		return [
			"x" => $originX,
			"y" => $originY,
			"z" => $originZ,
			"direction" => $direction,
			"type" => $type
		];
	}

	private function placeLibrary(int $originX, int $originY, int $originZ, int $direction) : array{
		$this->fillOrientedBox($originX, $originY, $originZ, $direction, 14, 11, 15, Block::STONE_BRICKS, 0, true);
		$this->fillLocal($originX, $originY, $originZ, $direction, 14, 15, 1, 1, 1, 12, 9, 13, Block::AIR, 0);
		$this->fillLocal($originX, $originY, $originZ, $direction, 14, 15, 4, 1, 0, 6, 3, 0, Block::AIR, 0);
		for($z = 1; $z <= 13; ++$z){
			$id = (($z - 1) % 4 === 0) ? Block::PLANKS : Block::BOOKSHELF;
			$this->fillLocal($originX, $originY, $originZ, $direction, 14, 15, 1, 1, $z, 1, 4, $z, $id, 0);
			$this->fillLocal($originX, $originY, $originZ, $direction, 14, 15, 12, 1, $z, 12, 4, $z, $id, 0);
			$this->fillLocal($originX, $originY, $originZ, $direction, 14, 15, 1, 6, $z, 1, 9, $z, $id, 0);
			$this->fillLocal($originX, $originY, $originZ, $direction, 14, 15, 12, 6, $z, 12, 9, $z, $id, 0);
		}
		for($z = 3; $z <= 11; $z += 2){
			$this->fillLocal($originX, $originY, $originZ, $direction, 14, 15, 3, 1, $z, 4, 3, $z, Block::BOOKSHELF, 0);
			$this->fillLocal($originX, $originY, $originZ, $direction, 14, 15, 6, 1, $z, 7, 3, $z, Block::BOOKSHELF, 0);
			$this->fillLocal($originX, $originY, $originZ, $direction, 14, 15, 9, 1, $z, 10, 3, $z, Block::BOOKSHELF, 0);
		}
		$this->fillLocal($originX, $originY, $originZ, $direction, 14, 15, 1, 5, 1, 3, 5, 13, Block::PLANKS, 0);
		$this->fillLocal($originX, $originY, $originZ, $direction, 14, 15, 10, 5, 1, 12, 5, 13, Block::PLANKS, 0);
		$this->fillLocal($originX, $originY, $originZ, $direction, 14, 15, 4, 5, 1, 9, 5, 2, Block::PLANKS, 0);
		$this->fillLocal($originX, $originY, $originZ, $direction, 14, 15, 4, 5, 12, 9, 5, 13, Block::PLANKS, 0);
		$this->placeLocal($originX, $originY, $originZ, $direction, 14, 15, 9, 5, 11, Block::PLANKS, 0);
		$this->placeLocal($originX, $originY, $originZ, $direction, 14, 15, 8, 5, 11, Block::PLANKS, 0);
		$this->placeLocal($originX, $originY, $originZ, $direction, 14, 15, 9, 5, 10, Block::PLANKS, 0);
		$this->fillLocal($originX, $originY, $originZ, $direction, 14, 15, 3, 6, 3, 3, 6, 11, Block::FENCE, 0);
		$this->fillLocal($originX, $originY, $originZ, $direction, 14, 15, 10, 6, 3, 10, 6, 9, Block::FENCE, 0);
		$this->fillLocal($originX, $originY, $originZ, $direction, 14, 15, 4, 6, 2, 9, 6, 2, Block::FENCE, 0);
		$this->fillLocal($originX, $originY, $originZ, $direction, 14, 15, 4, 6, 12, 7, 6, 12, Block::FENCE, 0);
		$this->placeLocal($originX, $originY, $originZ, $direction, 14, 15, 3, 6, 2, Block::FENCE, 0);
		$this->placeLocal($originX, $originY, $originZ, $direction, 14, 15, 3, 6, 12, Block::FENCE, 0);
		$this->placeLocal($originX, $originY, $originZ, $direction, 14, 15, 10, 6, 2, Block::FENCE, 0);
		for($i = 0; $i <= 2; ++$i){
			$this->placeLocal($originX, $originY, $originZ, $direction, 14, 15, 8 + $i, 6, 12 - $i, Block::FENCE, 0);
			if($i !== 2){
				$this->placeLocal($originX, $originY, $originZ, $direction, 14, 15, 8 + $i, 6, 11 - $i, Block::FENCE, 0);
			}
		}
		$ladderMeta = $this->ladderMeta($direction, self::NORTH);
		for($y = 1; $y <= 7; ++$y){
			$this->placeMarkedLadder($originX, $originY, $originZ, $direction, 14, 15, 10, $y, 13, $ladderMeta);
		}
		foreach([[6, 9, 7], [7, 9, 7], [6, 8, 7], [7, 8, 7], [6, 7, 7], [7, 7, 7], [5, 7, 7], [8, 7, 7], [6, 7, 6], [6, 7, 8], [7, 7, 6], [7, 7, 8]] as $entry){
			$this->placeLocal($originX, $originY, $originZ, $direction, 14, 15, $entry[0], $entry[1], $entry[2], Block::FENCE, 0);
		}
		$this->placeLocal($originX, $originY, $originZ, $direction, 14, 15, 3, 2, 5, Block::PLANKS, 0);
		$this->placeMarkedChest($originX, $originY, $originZ, $direction, 14, 15, 3, 3, 5, StrongholdLoot::LIBRARY_CHEST_MARKER_ID, StrongholdLoot::LIBRARY_CHEST_MARKER_DATA, StrongholdLoot::LIBRARY_CHEST_MARKER);
		$this->placeMarkedChest($originX, $originY, $originZ, $direction, 14, 15, 12, 8, 1, StrongholdLoot::LIBRARY_CHEST_MARKER_ID, StrongholdLoot::LIBRARY_CHEST_MARKER_DATA, StrongholdLoot::LIBRARY_CHEST_MARKER);

		return [
			"x" => $originX,
			"y" => $originY,
			"z" => $originZ,
			"direction" => $direction,
			"tall" => true
		];
	}

	private function placePrisonHall(int $originX, int $originY, int $originZ, int $direction) : array{
		$this->fillOrientedBox($originX, $originY, $originZ, $direction, 9, 5, 11, Block::STONE_BRICKS, 0, true);
		$this->fillLocal($originX, $originY, $originZ, $direction, 9, 11, 1, 1, 1, 7, 3, 9, Block::AIR, 0);
		$this->fillLocal($originX, $originY, $originZ, $direction, 9, 11, 1, 1, 0, 3, 3, 0, Block::AIR, 0);
		$this->fillLocal($originX, $originY, $originZ, $direction, 9, 11, 1, 1, 10, 3, 3, 10, Block::AIR, 0);
		foreach([1, 3, 7, 9] as $z){
			$this->fillLocal($originX, $originY, $originZ, $direction, 9, 11, 4, 1, $z, 4, 3, $z, Block::STONE_BRICKS, 0);
		}
		for($y = 1; $y <= 3; ++$y){
			foreach([[4, 4], [4, 5], [4, 6], [5, 5], [6, 5], [7, 5]] as $bar){
				$this->placeLocal($originX, $originY, $originZ, $direction, 9, 11, $bar[0], $y, $bar[1], Block::IRON_BARS, 0);
			}
		}
		$this->placeLocal($originX, $originY, $originZ, $direction, 9, 11, 4, 3, 2, Block::IRON_BARS, 0);
		$this->placeLocal($originX, $originY, $originZ, $direction, 9, 11, 4, 3, 8, Block::IRON_BARS, 0);
		$doorMeta = $this->doorLowerMeta($direction, self::WEST);
		$this->placeLocal($originX, $originY, $originZ, $direction, 9, 11, 4, 1, 2, Block::IRON_DOOR_BLOCK, $doorMeta);
		$this->placeLocal($originX, $originY, $originZ, $direction, 9, 11, 4, 2, 2, Block::IRON_DOOR_BLOCK, 8);
		$this->placeLocal($originX, $originY, $originZ, $direction, 9, 11, 4, 1, 8, Block::IRON_DOOR_BLOCK, $doorMeta);
		$this->placeLocal($originX, $originY, $originZ, $direction, 9, 11, 4, 2, 8, Block::IRON_DOOR_BLOCK, 8);

		return [
			"x" => $originX,
			"y" => $originY,
			"z" => $originZ,
			"direction" => $direction
		];
	}

	private function fillOrientedBox(int $originX, int $originY, int $originZ, int $direction, int $width, int $height, int $depth, int $id, int $meta, bool $hollow){
		for($x = 0; $x < $width; ++$x){
			for($y = 0; $y < $height; ++$y){
				for($z = 0; $z < $depth; ++$z){
					if($hollow && $x > 0 && $x < $width - 1 && $y > 0 && $y < $height - 1 && $z > 0 && $z < $depth - 1){
						$this->placeLocal($originX, $originY, $originZ, $direction, $width, $depth, $x, $y, $z, Block::AIR, 0);
					}else{
						$this->placeLocal($originX, $originY, $originZ, $direction, $width, $depth, $x, $y, $z, $id, $meta);
					}
				}
			}
		}
	}

	private function generateGrateDoor(int $originX, int $originY, int $originZ, int $direction){
		$this->fillLocal($originX, $originY, $originZ, $direction, 11, 16, 4, 1, 0, 6, 3, 0, Block::STONE_BRICKS, 0);
		$this->placeLocal($originX, $originY, $originZ, $direction, 11, 16, 5, 1, 0, Block::AIR, 0);
		$this->placeLocal($originX, $originY, $originZ, $direction, 11, 16, 5, 2, 0, Block::AIR, 0);
		$this->placeLocal($originX, $originY, $originZ, $direction, 11, 16, 4, 1, 0, Block::IRON_BARS, 0);
		$this->placeLocal($originX, $originY, $originZ, $direction, 11, 16, 4, 2, 0, Block::IRON_BARS, 0);
		$this->placeLocal($originX, $originY, $originZ, $direction, 11, 16, 4, 3, 0, Block::IRON_BARS, 0);
		$this->placeLocal($originX, $originY, $originZ, $direction, 11, 16, 5, 3, 0, Block::IRON_BARS, 0);
		$this->placeLocal($originX, $originY, $originZ, $direction, 11, 16, 6, 3, 0, Block::IRON_BARS, 0);
		$this->placeLocal($originX, $originY, $originZ, $direction, 11, 16, 6, 2, 0, Block::IRON_BARS, 0);
		$this->placeLocal($originX, $originY, $originZ, $direction, 11, 16, 6, 1, 0, Block::IRON_BARS, 0);
	}

	private function portalFrame(int $originX, int $originY, int $originZ, int $direction, int $x, int $y, int $z, int $frameDirection, bool $hasEye){
		$rotated = $this->rotateDirection($frameDirection, $direction);
		$this->placeLocal($originX, $originY, $originZ, $direction, 11, 16, $x, $y, $z, Block::END_PORTAL_FRAME, $this->portalFrameMeta($rotated, $hasEye));
	}

	private function portalFrameMeta(int $direction, bool $hasEye) : int{
		switch($direction){
			case self::SOUTH:
				$meta = 0;
				break;
			case self::WEST:
				$meta = 1;
				break;
			case self::NORTH:
				$meta = 2;
				break;
			case self::EAST:
			default:
				$meta = 3;
				break;
		}
		return $hasEye ? ($meta | 0x04) : $meta;
	}

	private function rotateDirection(int $localDirection, int $roomDirection) : int{
		$order = [self::NORTH, self::EAST, self::SOUTH, self::WEST];
		$index = array_search($localDirection, $order, true);
		$turns = 0;
		switch($roomDirection){
			case self::WEST:
				$turns = 1;
				break;
			case self::NORTH:
				$turns = 2;
				break;
			case self::EAST:
				$turns = 3;
				break;
		}
		return $order[($index + $turns) % 4];
	}

	private function fillLocal(int $originX, int $originY, int $originZ, int $direction, int $width, int $depth, int $x0, int $y0, int $z0, int $x1, int $y1, int $z1, int $id, int $meta){
		for($x = $x0; $x <= $x1; ++$x){
			for($y = $y0; $y <= $y1; ++$y){
				for($z = $z0; $z <= $z1; ++$z){
					$this->placeLocal($originX, $originY, $originZ, $direction, $width, $depth, $x, $y, $z, $id, $meta);
				}
			}
		}
	}

	private function placeLocal(int $originX, int $originY, int $originZ, int $direction, int $width, int $depth, int $x, int $y, int $z, int $id, int $meta){
		list($wx, $wz) = $this->localToWorld($originX, $originZ, $direction, $width, $depth, $x, $z);
		$wy = $originY + $y;
		if($wy < 1 || $wy > 126){
			return;
		}
		$this->level->setBlockIdAt($wx, $wy, $wz, $id);
		$this->level->setBlockDataAt($wx, $wy, $wz, $meta);
	}

	private function placeMarkedChest(int $originX, int $originY, int $originZ, int $direction, int $width, int $depth, int $x, int $y, int $z, int $markerId, int $markerData, int $marker){
		$this->placeLocal($originX, $originY, $originZ, $direction, $width, $depth, $x, $y, $z, Block::CHEST, $this->chestMeta($this->oppositeDirection($direction)));
		$this->markLocalBlock($originX, $originY, $originZ, $direction, $width, $depth, $x, $y, $z, $markerId, $markerData, $marker);
	}

	private function placeMarkedLadder(int $originX, int $originY, int $originZ, int $direction, int $width, int $depth, int $x, int $y, int $z, int $meta){
		$this->placeLocal($originX, $originY, $originZ, $direction, $width, $depth, $x, $y, $z, Block::LADDER, $meta);
		$this->markLocalBlock($originX, $originY, $originZ, $direction, $width, $depth, $x, $y, $z, self::NATURAL_STRONGHOLD_LADDER_MARKER_ID, self::NATURAL_STRONGHOLD_LADDER_MARKER_DATA, self::NATURAL_STRONGHOLD_LADDER_MARKER);
	}

	private function markLocalBlock(int $originX, int $originY, int $originZ, int $direction, int $width, int $depth, int $x, int $y, int $z, int $markerId, int $markerData, int $marker){
		list($wx, $wz) = $this->localToWorld($originX, $originZ, $direction, $width, $depth, $x, $z);
		$wy = $originY + $y;
		if($wy < 1 || $wy > 126){
			return;
		}
		if(method_exists($this->level, "setBlockExtraDataAt")){
			$this->level->setBlockExtraDataAt($wx, $wy, $wz, $markerId, $markerData);
			return;
		}

		$chunk = $this->level->getChunk($wx >> 4, $wz >> 4);
		if($chunk !== null && method_exists($chunk, "setBlockExtraData")){
			$chunk->setBlockExtraData($wx & 0x0f, $wy & 0x7f, $wz & 0x0f, $marker);
		}
	}

	private function oppositeDirection(int $direction) : int{
		switch($direction){
			case self::NORTH:
				return self::SOUTH;
			case self::EAST:
				return self::WEST;
			case self::WEST:
				return self::EAST;
			case self::SOUTH:
			default:
				return self::NORTH;
		}
	}

	private function chestMeta(int $direction) : int{
		switch($direction){
			case self::NORTH:
				return 2;
			case self::EAST:
				return 5;
			case self::WEST:
				return 4;
			case self::SOUTH:
			default:
				return 3;
		}
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

	private function ladderMeta(int $roomDirection, int $localFacingDirection) : int{
		switch($this->rotateDirection($localFacingDirection, $roomDirection)){
			case self::NORTH:
				return 2;
			case self::EAST:
				return 5;
			case self::WEST:
				return 4;
			case self::SOUTH:
			default:
				return 3;
		}
	}

	private function doorLowerMeta(int $roomDirection, int $localDirection) : int{
		switch($this->rotateDirection($localDirection, $roomDirection)){
			case self::SOUTH:
				return 0;
			case self::WEST:
				return 1;
			case self::NORTH:
				return 2;
			case self::EAST:
			default:
				return 3;
		}
	}

	private function stairMeta(int $roomDirection, int $localDirection) : int{
		switch($this->rotateDirection($localDirection, $roomDirection)){
			case self::WEST:
				return 0;
			case self::EAST:
				return 1;
			case self::NORTH:
				return 2;
			case self::SOUTH:
			default:
				return 3;
		}
	}

	private function localToWorld(int $originX, int $originZ, int $direction, int $width, int $depth, int $x, int $z) : array{
		switch($direction){
			case self::NORTH:
				return [$originX + ($width - 1 - $x), $originZ + ($depth - 1 - $z)];
			case self::EAST:
				return [$originX + $z, $originZ + ($width - 1 - $x)];
			case self::WEST:
				return [$originX + ($depth - 1 - $z), $originZ + $x];
			case self::SOUTH:
			default:
				return [$originX + $x, $originZ + $z];
		}
	}

	private function originForLocalWorld(int $worldX, int $worldZ, int $direction, int $width, int $depth, int $x, int $z) : array{
		switch($direction){
			case self::NORTH:
				return [$worldX - ($width - 1 - $x), $worldZ - ($depth - 1 - $z)];
			case self::EAST:
				return [$worldX - $z, $worldZ - ($width - 1 - $x)];
			case self::WEST:
				return [$worldX - ($depth - 1 - $z), $worldZ - $x];
			case self::SOUTH:
			default:
				return [$worldX - $x, $worldZ - $z];
		}
	}

	private function stepForward(int $x, int $z, int $direction) : array{
		switch($direction){
			case self::NORTH:
				return [$x, $z - 1];
			case self::EAST:
				return [$x + 1, $z];
			case self::WEST:
				return [$x - 1, $z];
			case self::SOUTH:
			default:
				return [$x, $z + 1];
		}
	}

	private function stepLeft(int $x, int $z, int $direction) : array{
		switch($direction){
			case self::EAST:
			case self::WEST:
				return [$x, $z - 1];
			case self::NORTH:
			case self::SOUTH:
			default:
				return [$x - 1, $z];
		}
	}

	private function stepRight(int $x, int $z, int $direction) : array{
		switch($direction){
			case self::EAST:
			case self::WEST:
				return [$x, $z + 1];
			case self::NORTH:
			case self::SOUTH:
			default:
				return [$x + 1, $z];
		}
	}

	private function leftChildDirection(int $direction) : int{
		return ($direction === self::NORTH || $direction === self::SOUTH) ? self::WEST : self::NORTH;
	}

	private function rightChildDirection(int $direction) : int{
		return ($direction === self::NORTH || $direction === self::SOUTH) ? self::EAST : self::SOUTH;
	}

	private function leftSideLocalX(int $direction, int $width = 10) : int{
		return ($direction === self::NORTH || $direction === self::EAST) ? $width - 1 : 0;
	}

	private function rightSideLocalX(int $direction, int $width) : int{
		return ($direction === self::NORTH || $direction === self::EAST) ? 0 : $width - 1;
	}

	private function offsetX(int $originX, int $direction, int $right, int $forward) : int{
		switch($direction){
			case self::NORTH:
				return $originX - $right;
			case self::EAST:
				return $originX + $forward;
			case self::WEST:
				return $originX - $forward;
			case self::SOUTH:
			default:
				return $originX + $right;
		}
	}

	private function offsetZ(int $originZ, int $direction, int $right, int $forward) : int{
		switch($direction){
			case self::NORTH:
				return $originZ - $forward;
			case self::EAST:
				return $originZ - $right;
			case self::WEST:
				return $originZ + $right;
			case self::SOUTH:
			default:
				return $originZ + $forward;
		}
	}

	private function stoneBrickMeta(Random $random) : int{
		$chance = $random->nextBoundedInt(100);
		if($chance < 20){
			return 2;
		}
		if($chance < 50){
			return 1;
		}
		return 0;
	}
}
