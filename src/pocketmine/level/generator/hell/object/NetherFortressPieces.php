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

namespace pocketmine\level\generator\hell\object;

use pocketmine\block\Block;
use pocketmine\entity\Blaze;
use pocketmine\level\ChunkManager;
use pocketmine\level\Level;
use pocketmine\level\generator\hell\populator\NetherFortressPopulator;
use pocketmine\math\Vector3;
use pocketmine\nbt\tag\CompoundTag;
use pocketmine\nbt\tag\IntTag;
use pocketmine\nbt\tag\StringTag;
use pocketmine\tile\MobSpawner as MobSpawnerTile;
use pocketmine\tile\Tile;
use pocketmine\utils\Random;

class PmNetherBoundingBox{

	public $x0;
	public $y0;
	public $z0;
	public $x1;
	public $y1;
	public $z1;

	public function __construct(int $x0, int $y0, int $z0, int $x1, int $y1, int $z1){
		$this->x0 = $x0;
		$this->y0 = $y0;
		$this->z0 = $z0;
		$this->x1 = $x1;
		$this->y1 = $y1;
		$this->z1 = $z1;
	}

	public static function orientBox(int $x, int $y, int $z, int $xOffset, int $yOffset, int $zOffset, int $xLength, int $yLength, int $zLength, int $orientation) : self{
		switch($orientation){
			case PmNetherBridgePiece::NORTH:
				return new self($x + $xOffset, $y + $yOffset, $z - $zLength + 1 + $zOffset, $x + $xLength - 1 + $xOffset, $y + $yLength - 1 + $yOffset, $z + $zOffset);
			case PmNetherBridgePiece::WEST:
				return new self($x - $zLength + 1 + $zOffset, $y + $yOffset, $z + $xOffset, $x + $zOffset, $y + $yLength - 1 + $yOffset, $z + $xLength - 1 + $xOffset);
			case PmNetherBridgePiece::EAST:
				return new self($x + $zOffset, $y + $yOffset, $z + $xOffset, $x + $zLength - 1 + $zOffset, $y + $yLength - 1 + $yOffset, $z + $xLength - 1 + $xOffset);
			case PmNetherBridgePiece::SOUTH:
			default:
				return new self($x + $xOffset, $y + $yOffset, $z + $zOffset, $x + $xLength - 1 + $xOffset, $y + $yLength - 1 + $yOffset, $z + $zLength - 1 + $zOffset);
		}
	}

	public function intersects(self $other) : bool{
		return $this->x1 >= $other->x0 && $this->x0 <= $other->x1 && $this->z1 >= $other->z0 && $this->z0 <= $other->z1 && $this->y1 >= $other->y0 && $this->y0 <= $other->y1;
	}

	public function intersectsChunk(int $x0, int $z0, int $x1, int $z1) : bool{
		return $this->x1 >= $x0 && $this->x0 <= $x1 && $this->z1 >= $z0 && $this->z0 <= $z1;
	}

	public function expand(self $other){
		$this->x0 = min($this->x0, $other->x0);
		$this->y0 = min($this->y0, $other->y0);
		$this->z0 = min($this->z0, $other->z0);
		$this->x1 = max($this->x1, $other->x1);
		$this->y1 = max($this->y1, $other->y1);
		$this->z1 = max($this->z1, $other->z1);
	}

	public function move(int $x, int $y, int $z){
		$this->x0 += $x;
		$this->y0 += $y;
		$this->z0 += $z;
		$this->x1 += $x;
		$this->y1 += $y;
		$this->z1 += $z;
	}

	public function isInside(int $x, int $y, int $z) : bool{
		return $x >= $this->x0 && $x <= $this->x1 && $y >= $this->y0 && $y <= $this->y1 && $z >= $this->z0 && $z <= $this->z1;
	}

	public function getYSpan() : int{
		return $this->y1 - $this->y0 + 1;
	}
}

abstract class PmNetherBridgePiece{

	const NORTH = 0;
	const EAST = 1;
	const SOUTH = 2;
	const WEST = 3;

	/** @var PmNetherBoundingBox */
	protected $boundingBox;
	/** @var int */
	protected $orientation;
	/** @var int */
	protected $genDepth;

	public function __construct(int $genDepth, PmNetherBoundingBox $boundingBox, int $orientation){
		$this->genDepth = $genDepth;
		$this->boundingBox = $boundingBox;
		$this->orientation = $orientation;
	}

	public function getBoundingBox() : PmNetherBoundingBox{
		return $this->boundingBox;
	}

	public function getGenDepth() : int{
		return $this->genDepth;
	}

	public function getOrientation() : int{
		return $this->orientation;
	}

	public function move(int $x, int $y, int $z){
		$this->boundingBox->move($x, $y, $z);
	}

	public function addChildren(PmNetherStartPiece $start, array &$pieces, Random $random){
	}

	abstract public function postProcess(ChunkManager $level, Random $random, PmNetherBoundingBox $chunkBox, int $chunkX, int $chunkZ) : bool;

	public static function findCollisionPiece(array $pieces, PmNetherBoundingBox $boundingBox){
		foreach($pieces as $piece){
			if($piece instanceof self && $piece->getBoundingBox()->intersects($boundingBox)){
				return $piece;
			}
		}

		return null;
	}

	protected function updatePieceWeight(array $weights) : int{
		$success = false;
		$total = 0;
		foreach($weights as $weight){
			if($weight["maxPlaceCount"] > 0 && $weight["placeCount"] < $weight["maxPlaceCount"]){
				$success = true;
			}
			$total += $weight["weight"];
		}

		return $success ? $total : -1;
	}

	protected function generatePiece(PmNetherStartPiece $start, array &$weights, array &$pieces, Random $random, int $x, int $y, int $z, int $orientation, int $genDepth){
		$total = $this->updatePieceWeight($weights);
		if($total > 0 && $genDepth <= 30){
			for($i = 0; $i < 5; ++$i){
				$target = $random->nextBoundedInt($total);

				foreach($weights as $index => $weight){
					$target -= $weight["weight"];
					if($target < 0){
						if(!$weight["allowUnlimited"] && $weight["maxPlaceCount"] !== 0 && $weight["placeCount"] >= $weight["maxPlaceCount"]){
							break;
						}
						if($weight["class"] === $start->previousPieceClass && !$weight["allowInRow"]){
							break;
						}

						$piece = self::createPieceByClass($weight["class"], $pieces, $random, $x, $y, $z, $orientation, $genDepth);
						if($piece !== null){
							$weights[$index]["placeCount"]++;
							$start->previousPieceClass = $weight["class"];
							if($weights[$index]["maxPlaceCount"] !== 0 && $weights[$index]["placeCount"] >= $weights[$index]["maxPlaceCount"]){
								array_splice($weights, $index, 1);
							}

							return $piece;
						}
					}
				}
			}
		}

		return PmNetherBridgeEndFillerPiece::createPiece($pieces, $random, $x, $y, $z, $orientation, $genDepth);
	}

	protected function generateAndAddPiece(PmNetherStartPiece $start, array &$pieces, Random $random, int $x, int $y, int $z, int $orientation, int $genDepth, bool $isCastle){
		if(abs($x - $start->getBoundingBox()->x0) > 112 || abs($z - $start->getBoundingBox()->z0) > 112){
			return PmNetherBridgeEndFillerPiece::createPiece($pieces, $random, $x, $y, $z, $orientation, $genDepth);
		}

		if($isCastle){
			$weights = &$start->availableCastlePieces;
		}else{
			$weights = &$start->availableBridgePieces;
		}

		$piece = $this->generatePiece($start, $weights, $pieces, $random, $x, $y, $z, $orientation, $genDepth + 1);
		if($piece instanceof self){
			$pieces[] = $piece;
			$start->pendingChildren[] = $piece;
		}

		return $piece;
	}

	protected function generateChildForward(PmNetherStartPiece $start, array &$pieces, Random $random, int $horizontalOffset, int $yOffset, bool $isCastle){
		switch($this->orientation){
			case self::NORTH:
			default:
				return $this->generateAndAddPiece($start, $pieces, $random, $this->boundingBox->x0 + $horizontalOffset, $this->boundingBox->y0 + $yOffset, $this->boundingBox->z0 - 1, $this->orientation, $this->genDepth, $isCastle);
			case self::SOUTH:
				return $this->generateAndAddPiece($start, $pieces, $random, $this->boundingBox->x0 + $horizontalOffset, $this->boundingBox->y0 + $yOffset, $this->boundingBox->z1 + 1, $this->orientation, $this->genDepth, $isCastle);
			case self::WEST:
				return $this->generateAndAddPiece($start, $pieces, $random, $this->boundingBox->x0 - 1, $this->boundingBox->y0 + $yOffset, $this->boundingBox->z0 + $horizontalOffset, $this->orientation, $this->genDepth, $isCastle);
			case self::EAST:
				return $this->generateAndAddPiece($start, $pieces, $random, $this->boundingBox->x1 + 1, $this->boundingBox->y0 + $yOffset, $this->boundingBox->z0 + $horizontalOffset, $this->orientation, $this->genDepth, $isCastle);
		}
	}

	protected function generateChildLeft(PmNetherStartPiece $start, array &$pieces, Random $random, int $yOffset, int $horizontalOffset, bool $isCastle){
		switch($this->orientation){
			case self::NORTH:
			case self::SOUTH:
			default:
				return $this->generateAndAddPiece($start, $pieces, $random, $this->boundingBox->x0 - 1, $this->boundingBox->y0 + $yOffset, $this->boundingBox->z0 + $horizontalOffset, self::WEST, $this->genDepth, $isCastle);
			case self::WEST:
			case self::EAST:
				return $this->generateAndAddPiece($start, $pieces, $random, $this->boundingBox->x0 + $horizontalOffset, $this->boundingBox->y0 + $yOffset, $this->boundingBox->z0 - 1, self::NORTH, $this->genDepth, $isCastle);
		}
	}

	protected function generateChildRight(PmNetherStartPiece $start, array &$pieces, Random $random, int $yOffset, int $horizontalOffset, bool $isCastle){
		switch($this->orientation){
			case self::NORTH:
			case self::SOUTH:
			default:
				return $this->generateAndAddPiece($start, $pieces, $random, $this->boundingBox->x1 + 1, $this->boundingBox->y0 + $yOffset, $this->boundingBox->z0 + $horizontalOffset, self::EAST, $this->genDepth, $isCastle);
			case self::WEST:
			case self::EAST:
				return $this->generateAndAddPiece($start, $pieces, $random, $this->boundingBox->x0 + $horizontalOffset, $this->boundingBox->y0 + $yOffset, $this->boundingBox->z1 + 1, self::SOUTH, $this->genDepth, $isCastle);
		}
	}

	protected static function createPieceByClass(string $class, array &$pieces, Random $random, int $x, int $y, int $z, int $orientation, int $genDepth){
		switch($class){
			case PmNetherBridgeStraightPiece::class:
				return PmNetherBridgeStraightPiece::createPiece($pieces, $random, $x, $y, $z, $orientation, $genDepth);
			case PmNetherBridgeCrossingPiece::class:
				return PmNetherBridgeCrossingPiece::createPiece($pieces, $x, $y, $z, $orientation, $genDepth);
			case PmNetherRoomCrossingPiece::class:
				return PmNetherRoomCrossingPiece::createPiece($pieces, $x, $y, $z, $orientation, $genDepth);
			case PmNetherStairsRoomPiece::class:
				return PmNetherStairsRoomPiece::createPiece($pieces, $x, $y, $z, $genDepth, $orientation);
			case PmNetherMonsterThronePiece::class:
				return PmNetherMonsterThronePiece::createPiece($pieces, $x, $y, $z, $genDepth, $orientation);
			case PmNetherCastleEntrancePiece::class:
				return PmNetherCastleEntrancePiece::createPiece($pieces, $random, $x, $y, $z, $orientation, $genDepth);
			case PmNetherCastleSmallCorridorPiece::class:
				return PmNetherCastleSmallCorridorPiece::createPiece($pieces, $x, $y, $z, $orientation, $genDepth);
			case PmNetherCastleSmallCorridorCrossingPiece::class:
				return PmNetherCastleSmallCorridorCrossingPiece::createPiece($pieces, $x, $y, $z, $orientation, $genDepth);
			case PmNetherCastleSmallCorridorRightTurnPiece::class:
				return PmNetherCastleSmallCorridorRightTurnPiece::createPiece($pieces, $random, $x, $y, $z, $orientation, $genDepth);
			case PmNetherCastleSmallCorridorLeftTurnPiece::class:
				return PmNetherCastleSmallCorridorLeftTurnPiece::createPiece($pieces, $random, $x, $y, $z, $orientation, $genDepth);
			case PmNetherCastleCorridorStairsPiece::class:
				return PmNetherCastleCorridorStairsPiece::createPiece($pieces, $x, $y, $z, $orientation, $genDepth);
			case PmNetherCastleCorridorTBalconyPiece::class:
				return PmNetherCastleCorridorTBalconyPiece::createPiece($pieces, $x, $y, $z, $orientation, $genDepth);
			case PmNetherCastleStalkRoomPiece::class:
				return PmNetherCastleStalkRoomPiece::createPiece($pieces, $x, $y, $z, $orientation, $genDepth);
		}

		return null;
	}

	protected static function isOkBox($boundingBox) : bool{
		return $boundingBox instanceof PmNetherBoundingBox && $boundingBox->y0 > 10;
	}

	protected function getWorldX(int $x, int $z) : int{
		switch($this->orientation){
			case self::NORTH:
			case self::SOUTH:
				return $this->boundingBox->x0 + $x;
			case self::WEST:
				return $this->boundingBox->x1 - $z;
			case self::EAST:
				return $this->boundingBox->x0 + $z;
			default:
				return $x;
		}
	}

	protected function getWorldY(int $y) : int{
		return $this->boundingBox->y0 + $y;
	}

	protected function getWorldZ(int $x, int $z) : int{
		switch($this->orientation){
			case self::NORTH:
				return $this->boundingBox->z1 - $z;
			case self::SOUTH:
				return $this->boundingBox->z0 + $z;
			case self::WEST:
			case self::EAST:
				return $this->boundingBox->z0 + $x;
			default:
				return $z;
		}
	}

	protected function placeBlock(ChunkManager $level, int $id, int $meta, int $x, int $y, int $z, PmNetherBoundingBox $chunkBox){
		$worldX = $this->getWorldX($x, $z);
		$worldY = $this->getWorldY($y);
		$worldZ = $this->getWorldZ($x, $z);
		if($chunkBox->isInside($worldX, $worldY, $worldZ)){
			$level->setBlockIdAt($worldX, $worldY, $worldZ, $id);
			$level->setBlockDataAt($worldX, $worldY, $worldZ, $meta);
		}
	}

	protected function placeMarkedChest(ChunkManager $level, int $meta, int $x, int $y, int $z, PmNetherBoundingBox $chunkBox){
		$worldX = $this->getWorldX($x, $z);
		$worldY = $this->getWorldY($y);
		$worldZ = $this->getWorldZ($x, $z);
		if(!$chunkBox->isInside($worldX, $worldY, $worldZ)){
			return;
		}

		$level->setBlockIdAt($worldX, $worldY, $worldZ, Block::CHEST);
		$level->setBlockDataAt($worldX, $worldY, $worldZ, $meta);
		if(method_exists($level, "setBlockExtraDataAt")){
			$level->setBlockExtraDataAt($worldX, $worldY, $worldZ, NetherFortressLoot::CHEST_MARKER_ID, NetherFortressLoot::CHEST_MARKER_DATA);
			return;
		}

		$chunk = $level->getChunk($worldX >> 4, $worldZ >> 4);
		if($chunk !== null && method_exists($chunk, "setBlockExtraData")){
			$chunk->setBlockExtraData($worldX & 0x0f, $worldY & 0x7f, $worldZ & 0x0f, NetherFortressLoot::CHEST_MARKER);
		}
	}

	protected function getBlockId(ChunkManager $level, int $x, int $y, int $z, PmNetherBoundingBox $chunkBox) : int{
		$worldX = $this->getWorldX($x, $z);
		$worldY = $this->getWorldY($y);
		$worldZ = $this->getWorldZ($x, $z);
		if(!$chunkBox->isInside($worldX, $worldY, $worldZ)){
			return Block::AIR;
		}

		return $level->getBlockIdAt($worldX, $worldY, $worldZ);
	}

	protected function generateBox(ChunkManager $level, PmNetherBoundingBox $chunkBox, int $x1, int $y1, int $z1, int $x2, int $y2, int $z2, int $outsideId, int $outsideMeta, int $insideId, int $insideMeta, bool $skipAir){
		for($y = $y1; $y <= $y2; ++$y){
			for($x = $x1; $x <= $x2; ++$x){
				for($z = $z1; $z <= $z2; ++$z){
					if(!$skipAir || $this->getBlockId($level, $x, $y, $z, $chunkBox) !== Block::AIR){
						if($y !== $y1 && $y !== $y2 && $x !== $x1 && $x !== $x2 && $z !== $z1 && $z !== $z2){
							$this->placeBlock($level, $insideId, $insideMeta, $x, $y, $z, $chunkBox);
						}else{
							$this->placeBlock($level, $outsideId, $outsideMeta, $x, $y, $z, $chunkBox);
						}
					}
				}
			}
		}
	}

	protected function fillColumnDown(ChunkManager $level, int $id, int $meta, int $x, int $y, int $z, PmNetherBoundingBox $chunkBox){
		$worldX = $this->getWorldX($x, $z);
		$worldY = $this->getWorldY($y);
		$worldZ = $this->getWorldZ($x, $z);
		if($chunkBox->isInside($worldX, $worldY, $worldZ)){
			$blockId = $level->getBlockIdAt($worldX, $worldY, $worldZ);
			while(($blockId === Block::AIR || $blockId === Block::WATER || $blockId === Block::STILL_WATER || $blockId === Block::LAVA || $blockId === Block::STILL_LAVA) && $worldY > 1){
				$level->setBlockIdAt($worldX, $worldY, $worldZ, $id);
				$level->setBlockDataAt($worldX, $worldY, $worldZ, $meta);
				$blockId = $level->getBlockIdAt($worldX, --$worldY, $worldZ);
			}
		}
	}

	protected static function stairMeta(int $direction) : int{
		switch($direction){
			case self::EAST:
				return 0;
			case self::WEST:
				return 1;
			case self::SOUTH:
				return 2;
			case self::NORTH:
			default:
				return 3;
		}
	}

	protected static function oppositeDirection(int $direction) : int{
		switch($direction){
			case self::NORTH:
				return self::SOUTH;
			case self::SOUTH:
				return self::NORTH;
			case self::WEST:
				return self::EAST;
			case self::EAST:
			default:
				return self::WEST;
		}
	}

	protected static function chestMetaForFacing(int $direction) : int{
		switch($direction){
			case self::SOUTH:
				return 4;
			case self::WEST:
				return 2;
			case self::NORTH:
				return 5;
			case self::EAST:
			default:
				return 3;
		}
	}

	protected function createBlazeSpawner(ChunkManager $level, int $x, int $y, int $z, PmNetherBoundingBox $chunkBox){
		$worldX = $this->getWorldX($x, $z);
		$worldY = $this->getWorldY($y);
		$worldZ = $this->getWorldZ($x, $z);
		if(!$chunkBox->isInside($worldX, $worldY, $worldZ)){
			return;
		}

		$level->setBlockIdAt($worldX, $worldY, $worldZ, Block::MONSTER_SPAWNER);
		$level->setBlockDataAt($worldX, $worldY, $worldZ, NetherFortressPopulator::SPAWNER_MARKER_BLAZE);
		if($level instanceof Level){
			$tile = $level->getTile(new Vector3($worldX, $worldY, $worldZ));
			if($tile instanceof MobSpawnerTile){
				$tile->setEntityId(Blaze::NETWORK_ID);
				$level->setBlockDataAt($worldX, $worldY, $worldZ, 0);
				return;
			}

			$chunk = $level->getChunk($worldX >> 4, $worldZ >> 4, true);
			if($chunk !== null){
				$tile = Tile::createTile(Tile::MOB_SPAWNER, $chunk, new CompoundTag("", [
					new StringTag("id", Tile::MOB_SPAWNER),
					new IntTag("x", $worldX),
					new IntTag("y", $worldY),
					new IntTag("z", $worldZ),
					new IntTag("EntityId", Blaze::NETWORK_ID)
				]));
				if($tile instanceof MobSpawnerTile){
					$level->setBlockDataAt($worldX, $worldY, $worldZ, 0);
				}
			}
		}
	}
}

class PmNetherStartPiece extends PmNetherBridgeCrossingPiece{

	public $previousPieceClass = null;
	public $availableBridgePieces = [];
	public $availableCastlePieces = [];
	public $pendingChildren = [];

	public function __construct(Random $random, int $x, int $z){
		parent::__construct(0, new PmNetherBoundingBox($x, 64, $z, $x + 18, 73, $z + 18), $random->nextBoundedInt(4));
		$this->availableBridgePieces = [
			["class" => PmNetherBridgeStraightPiece::class, "weight" => 30, "placeCount" => 0, "maxPlaceCount" => 0, "allowInRow" => true, "allowUnlimited" => true],
			["class" => PmNetherBridgeCrossingPiece::class, "weight" => 10, "placeCount" => 0, "maxPlaceCount" => 4, "allowInRow" => false, "allowUnlimited" => false],
			["class" => PmNetherRoomCrossingPiece::class, "weight" => 10, "placeCount" => 0, "maxPlaceCount" => 4, "allowInRow" => false, "allowUnlimited" => false],
			["class" => PmNetherStairsRoomPiece::class, "weight" => 10, "placeCount" => 0, "maxPlaceCount" => 3, "allowInRow" => false, "allowUnlimited" => false],
			["class" => PmNetherMonsterThronePiece::class, "weight" => 5, "placeCount" => 0, "maxPlaceCount" => 2, "allowInRow" => false, "allowUnlimited" => false],
			["class" => PmNetherCastleEntrancePiece::class, "weight" => 5, "placeCount" => 0, "maxPlaceCount" => 1, "allowInRow" => false, "allowUnlimited" => false],
		];
		$this->availableCastlePieces = [
			["class" => PmNetherCastleSmallCorridorPiece::class, "weight" => 25, "placeCount" => 0, "maxPlaceCount" => 0, "allowInRow" => true, "allowUnlimited" => true],
			["class" => PmNetherCastleSmallCorridorCrossingPiece::class, "weight" => 15, "placeCount" => 0, "maxPlaceCount" => 5, "allowInRow" => false, "allowUnlimited" => false],
			["class" => PmNetherCastleSmallCorridorRightTurnPiece::class, "weight" => 5, "placeCount" => 0, "maxPlaceCount" => 10, "allowInRow" => false, "allowUnlimited" => false],
			["class" => PmNetherCastleSmallCorridorLeftTurnPiece::class, "weight" => 5, "placeCount" => 0, "maxPlaceCount" => 10, "allowInRow" => false, "allowUnlimited" => false],
			["class" => PmNetherCastleCorridorStairsPiece::class, "weight" => 10, "placeCount" => 0, "maxPlaceCount" => 3, "allowInRow" => true, "allowUnlimited" => false],
			["class" => PmNetherCastleCorridorTBalconyPiece::class, "weight" => 7, "placeCount" => 0, "maxPlaceCount" => 2, "allowInRow" => false, "allowUnlimited" => false],
			["class" => PmNetherCastleStalkRoomPiece::class, "weight" => 5, "placeCount" => 0, "maxPlaceCount" => 2, "allowInRow" => false, "allowUnlimited" => false],
		];
	}
}

class PmNetherFortressStart{

	const MAX_PIECES = 128;

	/** @var PmNetherBridgePiece[] */
	private $pieces = [];
	/** @var PmNetherBoundingBox|null */
	private $boundingBox;
	/** @var Random */
	private $random;

	public function __construct(ChunkManager $level, int $chunkX, int $chunkZ){
		$this->random = new Random($level->getSeed());
		$r1 = $this->random->nextInt();
		$r2 = $this->random->nextInt();
		$this->random->setSeed(($chunkX * $r1) ^ ($chunkZ * $r2) ^ $level->getSeed());

		$start = new PmNetherStartPiece($this->random, ($chunkX << 4) + 2, ($chunkZ << 4) + 2);
		$this->pieces[] = $start;
		$start->addChildren($start, $this->pieces, $this->random);

		while(count($start->pendingChildren) > 0 && count($this->pieces) < self::MAX_PIECES){
			$index = $this->random->nextBoundedInt(count($start->pendingChildren));
			/** @var PmNetherBridgePiece $piece */
			$piece = array_splice($start->pendingChildren, $index, 1)[0];
			$piece->addChildren($start, $this->pieces, $this->random);
		}

		$this->calculateBoundingBox();
		$this->moveInsideHeights($this->random, 48, 70);
	}

	public function isValid() : bool{
		return count($this->pieces) > 0;
	}

	public function getBoundingBox(){
		return $this->boundingBox;
	}

	public function intersectsChunk(int $chunkX, int $chunkZ) : bool{
		return $this->boundingBox instanceof PmNetherBoundingBox && $this->boundingBox->intersectsChunk($chunkX << 4, $chunkZ << 4, ($chunkX << 4) + 15, ($chunkZ << 4) + 15);
	}

	public function postProcess(ChunkManager $level, Random $random, PmNetherBoundingBox $chunkBox, int $chunkX, int $chunkZ){
		foreach($this->pieces as $piece){
			if($piece->getBoundingBox()->intersects($chunkBox)){
				$piece->postProcess($level, $random, $chunkBox, $chunkX, $chunkZ);
			}
		}
	}

	private function calculateBoundingBox(){
		$this->boundingBox = null;
		foreach($this->pieces as $piece){
			if($this->boundingBox === null){
				$box = $piece->getBoundingBox();
				$this->boundingBox = new PmNetherBoundingBox($box->x0, $box->y0, $box->z0, $box->x1, $box->y1, $box->z1);
			}else{
				$this->boundingBox->expand($piece->getBoundingBox());
			}
		}
	}

	private function moveInsideHeights(Random $random, int $min, int $max){
		if(!$this->boundingBox instanceof PmNetherBoundingBox){
			return;
		}

		$range = $max - $min + 1 - $this->boundingBox->getYSpan();
		$y = $range > 1 ? $min + $random->nextBoundedInt($range) : $min;
		$offset = $y - $this->boundingBox->y0;
		$this->boundingBox->move(0, $offset, 0);
		foreach($this->pieces as $piece){
			$piece->move(0, $offset, 0);
		}
	}
}

class PmNetherBridgeStraightPiece extends PmNetherBridgePiece{

	public static function createPiece(array &$pieces, Random $random, int $x, int $y, int $z, int $orientation, int $genDepth){
		$boundingBox = PmNetherBoundingBox::orientBox($x, $y, $z, -1, -3, 0, 5, 10, 19, $orientation);
		return self::isOkBox($boundingBox) && self::findCollisionPiece($pieces, $boundingBox) === null ? new self($genDepth, $boundingBox, $orientation) : null;
	}

	public function addChildren(PmNetherStartPiece $start, array &$pieces, Random $random){
		$this->generateChildForward($start, $pieces, $random, 1, 3, false);
	}

	public function postProcess(ChunkManager $level, Random $random, PmNetherBoundingBox $chunkBox, int $chunkX, int $chunkZ) : bool{
		$this->generateBox($level, $chunkBox, 0, 3, 0, 4, 4, 18, Block::NETHER_BRICKS, 0, Block::NETHER_BRICKS, 0, false);
		$this->generateBox($level, $chunkBox, 1, 5, 0, 3, 7, 18, Block::AIR, 0, Block::AIR, 0, false);
		$this->generateBox($level, $chunkBox, 0, 5, 0, 0, 5, 18, Block::NETHER_BRICKS, 0, Block::NETHER_BRICKS, 0, false);
		$this->generateBox($level, $chunkBox, 4, 5, 0, 4, 5, 18, Block::NETHER_BRICKS, 0, Block::NETHER_BRICKS, 0, false);
		$this->generateBox($level, $chunkBox, 0, 2, 0, 4, 2, 5, Block::NETHER_BRICKS, 0, Block::NETHER_BRICKS, 0, false);
		$this->generateBox($level, $chunkBox, 0, 2, 13, 4, 2, 18, Block::NETHER_BRICKS, 0, Block::NETHER_BRICKS, 0, false);
		$this->generateBox($level, $chunkBox, 0, 0, 0, 4, 1, 3, Block::NETHER_BRICKS, 0, Block::NETHER_BRICKS, 0, false);
		$this->generateBox($level, $chunkBox, 0, 0, 15, 4, 1, 18, Block::NETHER_BRICKS, 0, Block::NETHER_BRICKS, 0, false);
		for($x = 0; $x <= 4; ++$x){
			for($z = 0; $z <= 2; ++$z){
				$this->fillColumnDown($level, Block::NETHER_BRICKS, 0, $x, -1, $z, $chunkBox);
				$this->fillColumnDown($level, Block::NETHER_BRICKS, 0, $x, -1, 18 - $z, $chunkBox);
			}
		}
		$this->generateBox($level, $chunkBox, 0, 1, 1, 0, 4, 1, Block::NETHER_BRICK_FENCE, 0, Block::NETHER_BRICK_FENCE, 0, false);
		$this->generateBox($level, $chunkBox, 0, 3, 4, 0, 4, 4, Block::NETHER_BRICK_FENCE, 0, Block::NETHER_BRICK_FENCE, 0, false);
		$this->generateBox($level, $chunkBox, 0, 3, 14, 0, 4, 14, Block::NETHER_BRICK_FENCE, 0, Block::NETHER_BRICK_FENCE, 0, false);
		$this->generateBox($level, $chunkBox, 0, 1, 17, 0, 4, 17, Block::NETHER_BRICK_FENCE, 0, Block::NETHER_BRICK_FENCE, 0, false);
		$this->generateBox($level, $chunkBox, 4, 1, 1, 4, 4, 1, Block::NETHER_BRICK_FENCE, 0, Block::NETHER_BRICK_FENCE, 0, false);
		$this->generateBox($level, $chunkBox, 4, 3, 4, 4, 4, 4, Block::NETHER_BRICK_FENCE, 0, Block::NETHER_BRICK_FENCE, 0, false);
		$this->generateBox($level, $chunkBox, 4, 3, 14, 4, 4, 14, Block::NETHER_BRICK_FENCE, 0, Block::NETHER_BRICK_FENCE, 0, false);
		$this->generateBox($level, $chunkBox, 4, 1, 17, 4, 4, 17, Block::NETHER_BRICK_FENCE, 0, Block::NETHER_BRICK_FENCE, 0, false);
		return true;
	}
}

class PmNetherBridgeEndFillerPiece extends PmNetherBridgePiece{

	private $selfSeed;

	public function __construct(int $genDepth, Random $random, PmNetherBoundingBox $boundingBox, int $orientation){
		parent::__construct($genDepth, $boundingBox, $orientation);
		$this->selfSeed = $random->nextInt();
	}

	public static function createPiece(array &$pieces, Random $random, int $x, int $y, int $z, int $orientation, int $genDepth){
		$boundingBox = PmNetherBoundingBox::orientBox($x, $y, $z, -1, -3, 0, 5, 10, 8, $orientation);
		return self::isOkBox($boundingBox) && self::findCollisionPiece($pieces, $boundingBox) === null ? new self($genDepth, $random, $boundingBox, $orientation) : null;
	}

	public function postProcess(ChunkManager $level, Random $random, PmNetherBoundingBox $chunkBox, int $chunkX, int $chunkZ) : bool{
		$rand = new Random($this->selfSeed);
		for($x = 0; $x <= 4; ++$x){
			for($y = 3; $y <= 4; ++$y){
				$this->generateBox($level, $chunkBox, $x, $y, 0, $x, $y, $rand->nextBoundedInt(8), Block::NETHER_BRICKS, 0, Block::NETHER_BRICKS, 0, false);
			}
		}
		$this->generateBox($level, $chunkBox, 0, 5, 0, 0, 5, $rand->nextBoundedInt(8), Block::NETHER_BRICKS, 0, Block::NETHER_BRICKS, 0, false);
		$this->generateBox($level, $chunkBox, 4, 5, 0, 4, 5, $rand->nextBoundedInt(8), Block::NETHER_BRICKS, 0, Block::NETHER_BRICKS, 0, false);
		for($x = 0; $x <= 4; ++$x){
			$this->generateBox($level, $chunkBox, $x, 2, 0, $x, 2, $rand->nextBoundedInt(5), Block::NETHER_BRICKS, 0, Block::NETHER_BRICKS, 0, false);
		}
		for($x = 0; $x <= 4; ++$x){
			for($y = 0; $y <= 1; ++$y){
				$this->generateBox($level, $chunkBox, $x, $y, 0, $x, $y, $rand->nextBoundedInt(3), Block::NETHER_BRICKS, 0, Block::NETHER_BRICKS, 0, false);
			}
		}
		return true;
	}
}

class PmNetherBridgeCrossingPiece extends PmNetherBridgePiece{

	public function addChildren(PmNetherStartPiece $start, array &$pieces, Random $random){
		$this->generateChildForward($start, $pieces, $random, 8, 3, false);
		$this->generateChildLeft($start, $pieces, $random, 3, 8, false);
		$this->generateChildRight($start, $pieces, $random, 3, 8, false);
	}

	public static function createPiece(array &$pieces, int $x, int $y, int $z, int $orientation, int $genDepth){
		$boundingBox = PmNetherBoundingBox::orientBox($x, $y, $z, -8, -3, 0, 19, 10, 19, $orientation);
		return self::isOkBox($boundingBox) && self::findCollisionPiece($pieces, $boundingBox) === null ? new self($genDepth, $boundingBox, $orientation) : null;
	}

	public function postProcess(ChunkManager $level, Random $random, PmNetherBoundingBox $chunkBox, int $chunkX, int $chunkZ) : bool{
		$this->generateBox($level, $chunkBox, 7, 3, 0, 11, 4, 18, Block::NETHER_BRICKS, 0, Block::NETHER_BRICKS, 0, false);
		$this->generateBox($level, $chunkBox, 0, 3, 7, 18, 4, 11, Block::NETHER_BRICKS, 0, Block::NETHER_BRICKS, 0, false);
		$this->generateBox($level, $chunkBox, 8, 5, 0, 10, 7, 18, Block::AIR, 0, Block::AIR, 0, false);
		$this->generateBox($level, $chunkBox, 0, 5, 8, 18, 7, 10, Block::AIR, 0, Block::AIR, 0, false);
		$this->generateBox($level, $chunkBox, 7, 5, 0, 7, 5, 7, Block::NETHER_BRICKS, 0, Block::NETHER_BRICKS, 0, false);
		$this->generateBox($level, $chunkBox, 7, 5, 11, 7, 5, 18, Block::NETHER_BRICKS, 0, Block::NETHER_BRICKS, 0, false);
		$this->generateBox($level, $chunkBox, 11, 5, 0, 11, 5, 7, Block::NETHER_BRICKS, 0, Block::NETHER_BRICKS, 0, false);
		$this->generateBox($level, $chunkBox, 11, 5, 11, 11, 5, 18, Block::NETHER_BRICKS, 0, Block::NETHER_BRICKS, 0, false);
		$this->generateBox($level, $chunkBox, 0, 5, 7, 7, 5, 7, Block::NETHER_BRICKS, 0, Block::NETHER_BRICKS, 0, false);
		$this->generateBox($level, $chunkBox, 11, 5, 7, 18, 5, 7, Block::NETHER_BRICKS, 0, Block::NETHER_BRICKS, 0, false);
		$this->generateBox($level, $chunkBox, 0, 5, 11, 7, 5, 11, Block::NETHER_BRICKS, 0, Block::NETHER_BRICKS, 0, false);
		$this->generateBox($level, $chunkBox, 11, 5, 11, 18, 5, 11, Block::NETHER_BRICKS, 0, Block::NETHER_BRICKS, 0, false);
		$this->generateBox($level, $chunkBox, 7, 2, 0, 11, 2, 5, Block::NETHER_BRICKS, 0, Block::NETHER_BRICKS, 0, false);
		$this->generateBox($level, $chunkBox, 7, 2, 13, 11, 2, 18, Block::NETHER_BRICKS, 0, Block::NETHER_BRICKS, 0, false);
		$this->generateBox($level, $chunkBox, 7, 0, 0, 11, 1, 3, Block::NETHER_BRICKS, 0, Block::NETHER_BRICKS, 0, false);
		$this->generateBox($level, $chunkBox, 7, 0, 15, 11, 1, 18, Block::NETHER_BRICKS, 0, Block::NETHER_BRICKS, 0, false);
		for($x = 7; $x <= 11; ++$x){
			for($z = 0; $z <= 2; ++$z){
				$this->fillColumnDown($level, Block::NETHER_BRICKS, 0, $x, -1, $z, $chunkBox);
				$this->fillColumnDown($level, Block::NETHER_BRICKS, 0, $x, -1, 18 - $z, $chunkBox);
			}
		}
		$this->generateBox($level, $chunkBox, 0, 2, 7, 5, 2, 11, Block::NETHER_BRICKS, 0, Block::NETHER_BRICKS, 0, false);
		$this->generateBox($level, $chunkBox, 13, 2, 7, 18, 2, 11, Block::NETHER_BRICKS, 0, Block::NETHER_BRICKS, 0, false);
		$this->generateBox($level, $chunkBox, 0, 0, 7, 3, 1, 11, Block::NETHER_BRICKS, 0, Block::NETHER_BRICKS, 0, false);
		$this->generateBox($level, $chunkBox, 15, 0, 7, 18, 1, 11, Block::NETHER_BRICKS, 0, Block::NETHER_BRICKS, 0, false);
		for($x = 0; $x <= 2; ++$x){
			for($z = 7; $z <= 11; ++$z){
				$this->fillColumnDown($level, Block::NETHER_BRICKS, 0, $x, -1, $z, $chunkBox);
				$this->fillColumnDown($level, Block::NETHER_BRICKS, 0, 18 - $x, -1, $z, $chunkBox);
			}
		}
		return true;
	}
}

class PmNetherRoomCrossingPiece extends PmNetherBridgePiece{

	public static function createPiece(array &$pieces, int $x, int $y, int $z, int $orientation, int $genDepth){
		$boundingBox = PmNetherBoundingBox::orientBox($x, $y, $z, -2, 0, 0, 7, 9, 7, $orientation);
		return self::isOkBox($boundingBox) && self::findCollisionPiece($pieces, $boundingBox) === null ? new self($genDepth, $boundingBox, $orientation) : null;
	}

	public function addChildren(PmNetherStartPiece $start, array &$pieces, Random $random){
		$this->generateChildForward($start, $pieces, $random, 2, 0, false);
		$this->generateChildLeft($start, $pieces, $random, 0, 2, false);
		$this->generateChildRight($start, $pieces, $random, 0, 2, false);
	}

	public function postProcess(ChunkManager $level, Random $random, PmNetherBoundingBox $chunkBox, int $chunkX, int $chunkZ) : bool{
		$this->generateBox($level, $chunkBox, 0, 0, 0, 6, 1, 6, Block::NETHER_BRICKS, 0, Block::NETHER_BRICKS, 0, false);
		$this->generateBox($level, $chunkBox, 0, 2, 0, 6, 7, 6, Block::AIR, 0, Block::AIR, 0, false);
		$this->generateBox($level, $chunkBox, 0, 2, 0, 1, 6, 0, Block::NETHER_BRICKS, 0, Block::NETHER_BRICKS, 0, false);
		$this->generateBox($level, $chunkBox, 0, 2, 6, 1, 6, 6, Block::NETHER_BRICKS, 0, Block::NETHER_BRICKS, 0, false);
		$this->generateBox($level, $chunkBox, 5, 2, 0, 6, 6, 0, Block::NETHER_BRICKS, 0, Block::NETHER_BRICKS, 0, false);
		$this->generateBox($level, $chunkBox, 5, 2, 6, 6, 6, 6, Block::NETHER_BRICKS, 0, Block::NETHER_BRICKS, 0, false);
		$this->generateBox($level, $chunkBox, 0, 2, 0, 0, 6, 1, Block::NETHER_BRICKS, 0, Block::NETHER_BRICKS, 0, false);
		$this->generateBox($level, $chunkBox, 0, 2, 5, 0, 6, 6, Block::NETHER_BRICKS, 0, Block::NETHER_BRICKS, 0, false);
		$this->generateBox($level, $chunkBox, 6, 2, 0, 6, 6, 1, Block::NETHER_BRICKS, 0, Block::NETHER_BRICKS, 0, false);
		$this->generateBox($level, $chunkBox, 6, 2, 5, 6, 6, 6, Block::NETHER_BRICKS, 0, Block::NETHER_BRICKS, 0, false);
		$this->generateBox($level, $chunkBox, 2, 6, 0, 4, 6, 0, Block::NETHER_BRICKS, 0, Block::NETHER_BRICKS, 0, false);
		$this->generateBox($level, $chunkBox, 2, 5, 0, 4, 5, 0, Block::NETHER_BRICK_FENCE, 0, Block::NETHER_BRICK_FENCE, 0, false);
		$this->generateBox($level, $chunkBox, 2, 6, 6, 4, 6, 6, Block::NETHER_BRICKS, 0, Block::NETHER_BRICKS, 0, false);
		$this->generateBox($level, $chunkBox, 2, 5, 6, 4, 5, 6, Block::NETHER_BRICK_FENCE, 0, Block::NETHER_BRICK_FENCE, 0, false);
		$this->generateBox($level, $chunkBox, 0, 6, 2, 0, 6, 4, Block::NETHER_BRICKS, 0, Block::NETHER_BRICKS, 0, false);
		$this->generateBox($level, $chunkBox, 0, 5, 2, 0, 5, 4, Block::NETHER_BRICK_FENCE, 0, Block::NETHER_BRICK_FENCE, 0, false);
		$this->generateBox($level, $chunkBox, 6, 6, 2, 6, 6, 4, Block::NETHER_BRICKS, 0, Block::NETHER_BRICKS, 0, false);
		$this->generateBox($level, $chunkBox, 6, 5, 2, 6, 5, 4, Block::NETHER_BRICK_FENCE, 0, Block::NETHER_BRICK_FENCE, 0, false);
		for($x = 0; $x <= 6; ++$x){
			for($z = 0; $z <= 6; ++$z){
				$this->fillColumnDown($level, Block::NETHER_BRICKS, 0, $x, -1, $z, $chunkBox);
			}
		}
		return true;
	}
}

class PmNetherStairsRoomPiece extends PmNetherBridgePiece{

	public static function createPiece(array &$pieces, int $x, int $y, int $z, int $genDepth, int $orientation){
		$boundingBox = PmNetherBoundingBox::orientBox($x, $y, $z, -2, 0, 0, 7, 11, 7, $orientation);
		return self::isOkBox($boundingBox) && self::findCollisionPiece($pieces, $boundingBox) === null ? new self($genDepth, $boundingBox, $orientation) : null;
	}

	public function addChildren(PmNetherStartPiece $start, array &$pieces, Random $random){
		$this->generateChildRight($start, $pieces, $random, 6, 2, false);
	}

	public function postProcess(ChunkManager $level, Random $random, PmNetherBoundingBox $chunkBox, int $chunkX, int $chunkZ) : bool{
		$this->generateBox($level, $chunkBox, 0, 0, 0, 6, 1, 6, Block::NETHER_BRICKS, 0, Block::NETHER_BRICKS, 0, false);
		$this->generateBox($level, $chunkBox, 0, 2, 0, 6, 10, 6, Block::AIR, 0, Block::AIR, 0, false);
		$this->generateBox($level, $chunkBox, 0, 2, 0, 1, 8, 0, Block::NETHER_BRICKS, 0, Block::NETHER_BRICKS, 0, false);
		$this->generateBox($level, $chunkBox, 5, 2, 0, 6, 8, 0, Block::NETHER_BRICKS, 0, Block::NETHER_BRICKS, 0, false);
		$this->generateBox($level, $chunkBox, 0, 2, 1, 0, 8, 6, Block::NETHER_BRICKS, 0, Block::NETHER_BRICKS, 0, false);
		$this->generateBox($level, $chunkBox, 6, 2, 1, 6, 8, 6, Block::NETHER_BRICKS, 0, Block::NETHER_BRICKS, 0, false);
		$this->generateBox($level, $chunkBox, 1, 2, 6, 5, 8, 6, Block::NETHER_BRICKS, 0, Block::NETHER_BRICKS, 0, false);
		$this->generateBox($level, $chunkBox, 0, 3, 2, 0, 5, 4, Block::NETHER_BRICK_FENCE, 0, Block::NETHER_BRICK_FENCE, 0, false);
		$this->generateBox($level, $chunkBox, 6, 3, 2, 6, 5, 2, Block::NETHER_BRICK_FENCE, 0, Block::NETHER_BRICK_FENCE, 0, false);
		$this->generateBox($level, $chunkBox, 6, 3, 4, 6, 5, 4, Block::NETHER_BRICK_FENCE, 0, Block::NETHER_BRICK_FENCE, 0, false);
		$this->placeBlock($level, Block::NETHER_BRICKS, 0, 5, 2, 5, $chunkBox);
		$this->generateBox($level, $chunkBox, 4, 2, 5, 4, 3, 5, Block::NETHER_BRICKS, 0, Block::NETHER_BRICKS, 0, false);
		$this->generateBox($level, $chunkBox, 3, 2, 5, 3, 4, 5, Block::NETHER_BRICKS, 0, Block::NETHER_BRICKS, 0, false);
		$this->generateBox($level, $chunkBox, 2, 2, 5, 2, 5, 5, Block::NETHER_BRICKS, 0, Block::NETHER_BRICKS, 0, false);
		$this->generateBox($level, $chunkBox, 1, 2, 5, 1, 6, 5, Block::NETHER_BRICKS, 0, Block::NETHER_BRICKS, 0, false);
		$this->generateBox($level, $chunkBox, 1, 7, 1, 5, 7, 4, Block::NETHER_BRICKS, 0, Block::NETHER_BRICKS, 0, false);
		$this->generateBox($level, $chunkBox, 6, 8, 2, 6, 8, 4, Block::AIR, 0, Block::AIR, 0, false);
		$this->generateBox($level, $chunkBox, 2, 6, 0, 4, 8, 0, Block::NETHER_BRICKS, 0, Block::NETHER_BRICKS, 0, false);
		$this->generateBox($level, $chunkBox, 2, 5, 0, 4, 5, 0, Block::NETHER_BRICK_FENCE, 0, Block::NETHER_BRICK_FENCE, 0, false);
		for($x = 0; $x <= 6; ++$x){
			for($z = 0; $z <= 6; ++$z){
				$this->fillColumnDown($level, Block::NETHER_BRICKS, 0, $x, -1, $z, $chunkBox);
			}
		}
		return true;
	}
}

class PmNetherMonsterThronePiece extends PmNetherBridgePiece{

	public static function createPiece(array &$pieces, int $x, int $y, int $z, int $genDepth, int $orientation){
		$boundingBox = PmNetherBoundingBox::orientBox($x, $y, $z, -2, 0, 0, 7, 8, 9, $orientation);
		return self::isOkBox($boundingBox) && self::findCollisionPiece($pieces, $boundingBox) === null ? new self($genDepth, $boundingBox, $orientation) : null;
	}

	public function postProcess(ChunkManager $level, Random $random, PmNetherBoundingBox $chunkBox, int $chunkX, int $chunkZ) : bool{
		$this->generateBox($level, $chunkBox, 0, 2, 0, 6, 7, 7, Block::AIR, 0, Block::AIR, 0, false);
		$this->generateBox($level, $chunkBox, 1, 0, 0, 5, 1, 7, Block::NETHER_BRICKS, 0, Block::NETHER_BRICKS, 0, false);
		$this->generateBox($level, $chunkBox, 1, 2, 1, 5, 2, 7, Block::NETHER_BRICKS, 0, Block::NETHER_BRICKS, 0, false);
		$this->generateBox($level, $chunkBox, 1, 3, 2, 5, 3, 7, Block::NETHER_BRICKS, 0, Block::NETHER_BRICKS, 0, false);
		$this->generateBox($level, $chunkBox, 1, 4, 3, 5, 4, 7, Block::NETHER_BRICKS, 0, Block::NETHER_BRICKS, 0, false);
		$this->generateBox($level, $chunkBox, 1, 2, 0, 1, 4, 2, Block::NETHER_BRICKS, 0, Block::NETHER_BRICKS, 0, false);
		$this->generateBox($level, $chunkBox, 5, 2, 0, 5, 4, 2, Block::NETHER_BRICKS, 0, Block::NETHER_BRICKS, 0, false);
		$this->generateBox($level, $chunkBox, 1, 5, 2, 1, 5, 3, Block::NETHER_BRICKS, 0, Block::NETHER_BRICKS, 0, false);
		$this->generateBox($level, $chunkBox, 5, 5, 2, 5, 5, 3, Block::NETHER_BRICKS, 0, Block::NETHER_BRICKS, 0, false);
		$this->generateBox($level, $chunkBox, 0, 5, 3, 0, 5, 8, Block::NETHER_BRICKS, 0, Block::NETHER_BRICKS, 0, false);
		$this->generateBox($level, $chunkBox, 6, 5, 3, 6, 5, 8, Block::NETHER_BRICKS, 0, Block::NETHER_BRICKS, 0, false);
		$this->generateBox($level, $chunkBox, 1, 5, 8, 5, 5, 8, Block::NETHER_BRICKS, 0, Block::NETHER_BRICKS, 0, false);
		$this->placeBlock($level, Block::NETHER_BRICK_FENCE, 0, 1, 6, 3, $chunkBox);
		$this->placeBlock($level, Block::NETHER_BRICK_FENCE, 0, 5, 6, 3, $chunkBox);
		$this->placeBlock($level, Block::NETHER_BRICK_FENCE, 0, 0, 6, 3, $chunkBox);
		$this->placeBlock($level, Block::NETHER_BRICK_FENCE, 0, 6, 6, 3, $chunkBox);
		$this->generateBox($level, $chunkBox, 0, 6, 4, 0, 6, 7, Block::NETHER_BRICK_FENCE, 0, Block::NETHER_BRICK_FENCE, 0, false);
		$this->generateBox($level, $chunkBox, 6, 6, 4, 6, 6, 7, Block::NETHER_BRICK_FENCE, 0, Block::NETHER_BRICK_FENCE, 0, false);
		$this->placeBlock($level, Block::NETHER_BRICK_FENCE, 0, 0, 6, 8, $chunkBox);
		$this->placeBlock($level, Block::NETHER_BRICK_FENCE, 0, 6, 6, 8, $chunkBox);
		$this->generateBox($level, $chunkBox, 1, 6, 8, 5, 6, 8, Block::NETHER_BRICK_FENCE, 0, Block::NETHER_BRICK_FENCE, 0, false);
		$this->placeBlock($level, Block::NETHER_BRICK_FENCE, 0, 1, 7, 8, $chunkBox);
		$this->generateBox($level, $chunkBox, 2, 7, 8, 4, 7, 8, Block::NETHER_BRICK_FENCE, 0, Block::NETHER_BRICK_FENCE, 0, false);
		$this->placeBlock($level, Block::NETHER_BRICK_FENCE, 0, 5, 7, 8, $chunkBox);
		$this->placeBlock($level, Block::NETHER_BRICK_FENCE, 0, 2, 8, 8, $chunkBox);
		$this->placeBlock($level, Block::NETHER_BRICK_FENCE, 0, 3, 8, 8, $chunkBox);
		$this->placeBlock($level, Block::NETHER_BRICK_FENCE, 0, 4, 8, 8, $chunkBox);
		$this->createBlazeSpawner($level, 3, 5, 5, $chunkBox);
		for($x = 0; $x <= 6; ++$x){
			for($z = 0; $z <= 6; ++$z){
				$this->fillColumnDown($level, Block::NETHER_BRICKS, 0, $x, -1, $z, $chunkBox);
			}
		}
		return true;
	}
}

class PmNetherCastleEntrancePiece extends PmNetherBridgePiece{

	public static function createPiece(array &$pieces, Random $random, int $x, int $y, int $z, int $orientation, int $genDepth){
		$boundingBox = PmNetherBoundingBox::orientBox($x, $y, $z, -5, -3, 0, 13, 14, 13, $orientation);
		return self::isOkBox($boundingBox) && self::findCollisionPiece($pieces, $boundingBox) === null ? new self($genDepth, $boundingBox, $orientation) : null;
	}

	public function addChildren(PmNetherStartPiece $start, array &$pieces, Random $random){
		$this->generateChildForward($start, $pieces, $random, 5, 3, true);
	}

	public function postProcess(ChunkManager $level, Random $random, PmNetherBoundingBox $chunkBox, int $chunkX, int $chunkZ) : bool{
		$this->generateBox($level, $chunkBox, 0, 3, 0, 12, 4, 12, Block::NETHER_BRICKS, 0, Block::NETHER_BRICKS, 0, false);
		$this->generateBox($level, $chunkBox, 0, 5, 0, 12, 13, 12, Block::AIR, 0, Block::AIR, 0, false);
		$this->generateBox($level, $chunkBox, 0, 5, 0, 1, 12, 12, Block::NETHER_BRICKS, 0, Block::NETHER_BRICKS, 0, false);
		$this->generateBox($level, $chunkBox, 11, 5, 0, 12, 12, 12, Block::NETHER_BRICKS, 0, Block::NETHER_BRICKS, 0, false);
		$this->generateBox($level, $chunkBox, 2, 5, 11, 4, 12, 12, Block::NETHER_BRICKS, 0, Block::NETHER_BRICKS, 0, false);
		$this->generateBox($level, $chunkBox, 8, 5, 11, 10, 12, 12, Block::NETHER_BRICKS, 0, Block::NETHER_BRICKS, 0, false);
		$this->generateBox($level, $chunkBox, 5, 9, 11, 7, 12, 12, Block::NETHER_BRICKS, 0, Block::NETHER_BRICKS, 0, false);
		$this->generateBox($level, $chunkBox, 2, 5, 0, 4, 12, 1, Block::NETHER_BRICKS, 0, Block::NETHER_BRICKS, 0, false);
		$this->generateBox($level, $chunkBox, 8, 5, 0, 10, 12, 1, Block::NETHER_BRICKS, 0, Block::NETHER_BRICKS, 0, false);
		$this->generateBox($level, $chunkBox, 5, 9, 0, 7, 12, 1, Block::NETHER_BRICKS, 0, Block::NETHER_BRICKS, 0, false);
		$this->generateBox($level, $chunkBox, 2, 11, 2, 10, 12, 10, Block::NETHER_BRICKS, 0, Block::NETHER_BRICKS, 0, false);
		$this->generateBox($level, $chunkBox, 5, 8, 0, 7, 8, 0, Block::NETHER_BRICK_FENCE, 0, Block::NETHER_BRICK_FENCE, 0, false);
		for($i = 1; $i <= 11; $i += 2){
			$this->generateBox($level, $chunkBox, $i, 10, 0, $i, 11, 0, Block::NETHER_BRICK_FENCE, 0, Block::NETHER_BRICK_FENCE, 0, false);
			$this->generateBox($level, $chunkBox, $i, 10, 12, $i, 11, 12, Block::NETHER_BRICK_FENCE, 0, Block::NETHER_BRICK_FENCE, 0, false);
			$this->generateBox($level, $chunkBox, 0, 10, $i, 0, 11, $i, Block::NETHER_BRICK_FENCE, 0, Block::NETHER_BRICK_FENCE, 0, false);
			$this->generateBox($level, $chunkBox, 12, 10, $i, 12, 11, $i, Block::NETHER_BRICK_FENCE, 0, Block::NETHER_BRICK_FENCE, 0, false);
			$this->placeBlock($level, Block::NETHER_BRICKS, 0, $i, 13, 0, $chunkBox);
			$this->placeBlock($level, Block::NETHER_BRICKS, 0, $i, 13, 12, $chunkBox);
			$this->placeBlock($level, Block::NETHER_BRICKS, 0, 0, 13, $i, $chunkBox);
			$this->placeBlock($level, Block::NETHER_BRICKS, 0, 12, 13, $i, $chunkBox);
			if($i !== 11){
				$this->placeBlock($level, Block::NETHER_BRICK_FENCE, 0, $i + 1, 13, 0, $chunkBox);
				$this->placeBlock($level, Block::NETHER_BRICK_FENCE, 0, $i + 1, 13, 12, $chunkBox);
				$this->placeBlock($level, Block::NETHER_BRICK_FENCE, 0, 0, 13, $i + 1, $chunkBox);
				$this->placeBlock($level, Block::NETHER_BRICK_FENCE, 0, 12, 13, $i + 1, $chunkBox);
			}
		}
		$this->placeBlock($level, Block::NETHER_BRICK_FENCE, 0, 0, 13, 0, $chunkBox);
		$this->placeBlock($level, Block::NETHER_BRICK_FENCE, 0, 0, 13, 12, $chunkBox);
		$this->placeBlock($level, Block::NETHER_BRICK_FENCE, 0, 12, 13, 12, $chunkBox);
		$this->placeBlock($level, Block::NETHER_BRICK_FENCE, 0, 12, 13, 0, $chunkBox);
		for($z = 3; $z <= 9; $z += 2){
			$this->generateBox($level, $chunkBox, 1, 7, $z, 1, 8, $z, Block::NETHER_BRICK_FENCE, 0, Block::NETHER_BRICK_FENCE, 0, false);
			$this->generateBox($level, $chunkBox, 11, 7, $z, 11, 8, $z, Block::NETHER_BRICK_FENCE, 0, Block::NETHER_BRICK_FENCE, 0, false);
		}
		$this->generateBox($level, $chunkBox, 4, 2, 0, 8, 2, 12, Block::NETHER_BRICKS, 0, Block::NETHER_BRICKS, 0, false);
		$this->generateBox($level, $chunkBox, 0, 2, 4, 12, 2, 8, Block::NETHER_BRICKS, 0, Block::NETHER_BRICKS, 0, false);
		$this->generateBox($level, $chunkBox, 4, 0, 0, 8, 1, 3, Block::NETHER_BRICKS, 0, Block::NETHER_BRICKS, 0, false);
		$this->generateBox($level, $chunkBox, 4, 0, 9, 8, 1, 12, Block::NETHER_BRICKS, 0, Block::NETHER_BRICKS, 0, false);
		$this->generateBox($level, $chunkBox, 0, 0, 4, 3, 1, 8, Block::NETHER_BRICKS, 0, Block::NETHER_BRICKS, 0, false);
		$this->generateBox($level, $chunkBox, 9, 0, 4, 12, 1, 8, Block::NETHER_BRICKS, 0, Block::NETHER_BRICKS, 0, false);
		for($x = 4; $x <= 8; ++$x){
			for($l = 0; $l <= 2; ++$l){
				$this->fillColumnDown($level, Block::NETHER_BRICKS, 0, $x, -1, $l, $chunkBox);
				$this->fillColumnDown($level, Block::NETHER_BRICKS, 0, $x, -1, 12 - $l, $chunkBox);
			}
		}
		for($x = 0; $x <= 2; ++$x){
			for($n = 4; $n <= 8; ++$n){
				$this->fillColumnDown($level, Block::NETHER_BRICKS, 0, $x, -1, $n, $chunkBox);
				$this->fillColumnDown($level, Block::NETHER_BRICKS, 0, 12 - $x, -1, $n, $chunkBox);
			}
		}
		$this->generateBox($level, $chunkBox, 5, 5, 5, 7, 5, 7, Block::NETHER_BRICKS, 0, Block::NETHER_BRICKS, 0, false);
		$this->generateBox($level, $chunkBox, 6, 1, 6, 6, 4, 6, Block::AIR, 0, Block::AIR, 0, false);
		$this->placeBlock($level, Block::NETHER_BRICKS, 0, 6, 0, 6, $chunkBox);
		$this->placeBlock($level, Block::LAVA, 0, 6, 5, 6, $chunkBox);
		return true;
	}
}

class PmNetherCastleStalkRoomPiece extends PmNetherBridgePiece{

	public static function createPiece(array &$pieces, int $x, int $y, int $z, int $orientation, int $genDepth){
		$boundingBox = PmNetherBoundingBox::orientBox($x, $y, $z, -5, -3, 0, 13, 14, 13, $orientation);
		return self::isOkBox($boundingBox) && self::findCollisionPiece($pieces, $boundingBox) === null ? new self($genDepth, $boundingBox, $orientation) : null;
	}

	public function addChildren(PmNetherStartPiece $start, array &$pieces, Random $random){
		$this->generateChildForward($start, $pieces, $random, 5, 3, true);
		$this->generateChildForward($start, $pieces, $random, 5, 11, true);
	}

	public function postProcess(ChunkManager $level, Random $random, PmNetherBoundingBox $chunkBox, int $chunkX, int $chunkZ) : bool{
		$this->generateBox($level, $chunkBox, 0, 3, 0, 12, 4, 12, Block::NETHER_BRICKS, 0, Block::NETHER_BRICKS, 0, false);
		$this->generateBox($level, $chunkBox, 0, 5, 0, 12, 13, 12, Block::AIR, 0, Block::AIR, 0, false);
		$this->generateBox($level, $chunkBox, 0, 5, 0, 1, 12, 12, Block::NETHER_BRICKS, 0, Block::NETHER_BRICKS, 0, false);
		$this->generateBox($level, $chunkBox, 11, 5, 0, 12, 12, 12, Block::NETHER_BRICKS, 0, Block::NETHER_BRICKS, 0, false);
		$this->generateBox($level, $chunkBox, 2, 5, 11, 4, 12, 12, Block::NETHER_BRICKS, 0, Block::NETHER_BRICKS, 0, false);
		$this->generateBox($level, $chunkBox, 8, 5, 11, 10, 12, 12, Block::NETHER_BRICKS, 0, Block::NETHER_BRICKS, 0, false);
		$this->generateBox($level, $chunkBox, 5, 9, 11, 7, 12, 12, Block::NETHER_BRICKS, 0, Block::NETHER_BRICKS, 0, false);
		$this->generateBox($level, $chunkBox, 2, 5, 0, 4, 12, 1, Block::NETHER_BRICKS, 0, Block::NETHER_BRICKS, 0, false);
		$this->generateBox($level, $chunkBox, 8, 5, 0, 10, 12, 1, Block::NETHER_BRICKS, 0, Block::NETHER_BRICKS, 0, false);
		$this->generateBox($level, $chunkBox, 5, 9, 0, 7, 12, 1, Block::NETHER_BRICKS, 0, Block::NETHER_BRICKS, 0, false);
		$this->generateBox($level, $chunkBox, 2, 11, 2, 10, 12, 10, Block::NETHER_BRICKS, 0, Block::NETHER_BRICKS, 0, false);
		for($i = 1; $i <= 11; $i += 2){
			$this->generateBox($level, $chunkBox, $i, 10, 0, $i, 11, 0, Block::NETHER_BRICK_FENCE, 0, Block::NETHER_BRICK_FENCE, 0, false);
			$this->generateBox($level, $chunkBox, $i, 10, 12, $i, 11, 12, Block::NETHER_BRICK_FENCE, 0, Block::NETHER_BRICK_FENCE, 0, false);
			$this->generateBox($level, $chunkBox, 0, 10, $i, 0, 11, $i, Block::NETHER_BRICK_FENCE, 0, Block::NETHER_BRICK_FENCE, 0, false);
			$this->generateBox($level, $chunkBox, 12, 10, $i, 12, 11, $i, Block::NETHER_BRICK_FENCE, 0, Block::NETHER_BRICK_FENCE, 0, false);
			$this->placeBlock($level, Block::NETHER_BRICKS, 0, $i, 13, 0, $chunkBox);
			$this->placeBlock($level, Block::NETHER_BRICKS, 0, $i, 13, 12, $chunkBox);
			$this->placeBlock($level, Block::NETHER_BRICKS, 0, 0, 13, $i, $chunkBox);
			$this->placeBlock($level, Block::NETHER_BRICKS, 0, 12, 13, $i, $chunkBox);
			if($i !== 11){
				$this->placeBlock($level, Block::NETHER_BRICK_FENCE, 0, $i + 1, 13, 0, $chunkBox);
				$this->placeBlock($level, Block::NETHER_BRICK_FENCE, 0, $i + 1, 13, 12, $chunkBox);
				$this->placeBlock($level, Block::NETHER_BRICK_FENCE, 0, 0, 13, $i + 1, $chunkBox);
				$this->placeBlock($level, Block::NETHER_BRICK_FENCE, 0, 12, 13, $i + 1, $chunkBox);
			}
		}
		$this->placeBlock($level, Block::NETHER_BRICK_FENCE, 0, 0, 13, 0, $chunkBox);
		$this->placeBlock($level, Block::NETHER_BRICK_FENCE, 0, 0, 13, 12, $chunkBox);
		$this->placeBlock($level, Block::NETHER_BRICK_FENCE, 0, 12, 13, 12, $chunkBox);
		$this->placeBlock($level, Block::NETHER_BRICK_FENCE, 0, 12, 13, 0, $chunkBox);
		for($z = 3; $z <= 9; $z += 2){
			$this->generateBox($level, $chunkBox, 1, 7, $z, 1, 8, $z, Block::NETHER_BRICK_FENCE, 0, Block::NETHER_BRICK_FENCE, 0, false);
			$this->generateBox($level, $chunkBox, 11, 7, $z, 11, 8, $z, Block::NETHER_BRICK_FENCE, 0, Block::NETHER_BRICK_FENCE, 0, false);
		}
		$stairsNorth = self::stairMeta(self::NORTH);
		for($y = 0; $y <= 6; ++$y){
			$z = $y + 4;
			for($x = 5; $x <= 7; ++$x){
				$this->placeBlock($level, Block::NETHER_BRICKS_STAIRS, $stairsNorth, $x, 5 + $y, $z, $chunkBox);
			}
			if($z >= 5 && $z <= 8){
				$this->generateBox($level, $chunkBox, 5, 5, $z, 7, $y + 4, $z, Block::NETHER_BRICKS, 0, Block::NETHER_BRICKS, 0, false);
			}elseif($z >= 9 && $z <= 10){
				$this->generateBox($level, $chunkBox, 5, 8, $z, 7, $y + 4, $z, Block::NETHER_BRICKS, 0, Block::NETHER_BRICKS, 0, false);
			}
			if($y >= 1){
				$this->generateBox($level, $chunkBox, 5, 6 + $y, $z, 7, 9 + $y, $z, Block::AIR, 0, Block::AIR, 0, false);
			}
		}
		for($x = 5; $x <= 7; ++$x){
			$this->placeBlock($level, Block::NETHER_BRICKS_STAIRS, $stairsNorth, $x, 12, 11, $chunkBox);
		}
		$this->generateBox($level, $chunkBox, 5, 6, 7, 5, 7, 7, Block::NETHER_BRICK_FENCE, 0, Block::NETHER_BRICK_FENCE, 0, false);
		$this->generateBox($level, $chunkBox, 7, 6, 7, 7, 7, 7, Block::NETHER_BRICK_FENCE, 0, Block::NETHER_BRICK_FENCE, 0, false);
		$this->generateBox($level, $chunkBox, 5, 13, 12, 7, 13, 12, Block::AIR, 0, Block::AIR, 0, false);
		$this->generateBox($level, $chunkBox, 2, 5, 2, 3, 5, 3, Block::NETHER_BRICKS, 0, Block::NETHER_BRICKS, 0, false);
		$this->generateBox($level, $chunkBox, 2, 5, 9, 3, 5, 10, Block::NETHER_BRICKS, 0, Block::NETHER_BRICKS, 0, false);
		$this->generateBox($level, $chunkBox, 2, 5, 4, 2, 5, 8, Block::NETHER_BRICKS, 0, Block::NETHER_BRICKS, 0, false);
		$this->generateBox($level, $chunkBox, 9, 5, 2, 10, 5, 3, Block::NETHER_BRICKS, 0, Block::NETHER_BRICKS, 0, false);
		$this->generateBox($level, $chunkBox, 9, 5, 9, 10, 5, 10, Block::NETHER_BRICKS, 0, Block::NETHER_BRICKS, 0, false);
		$this->generateBox($level, $chunkBox, 10, 5, 4, 10, 5, 8, Block::NETHER_BRICKS, 0, Block::NETHER_BRICKS, 0, false);
		$stairsWest = self::stairMeta(self::WEST);
		$this->placeBlock($level, Block::NETHER_BRICKS_STAIRS, $stairsWest, 4, 5, 2, $chunkBox);
		$this->placeBlock($level, Block::NETHER_BRICKS_STAIRS, $stairsWest, 4, 5, 3, $chunkBox);
		$this->placeBlock($level, Block::NETHER_BRICKS_STAIRS, $stairsWest, 4, 5, 9, $chunkBox);
		$this->placeBlock($level, Block::NETHER_BRICKS_STAIRS, $stairsWest, 4, 5, 10, $chunkBox);
		$stairsEast = self::stairMeta(self::EAST);
		$this->placeBlock($level, Block::NETHER_BRICKS_STAIRS, $stairsEast, 8, 5, 2, $chunkBox);
		$this->placeBlock($level, Block::NETHER_BRICKS_STAIRS, $stairsEast, 8, 5, 3, $chunkBox);
		$this->placeBlock($level, Block::NETHER_BRICKS_STAIRS, $stairsEast, 8, 5, 9, $chunkBox);
		$this->placeBlock($level, Block::NETHER_BRICKS_STAIRS, $stairsEast, 8, 5, 10, $chunkBox);
		$this->generateBox($level, $chunkBox, 3, 4, 4, 4, 4, 8, Block::SOUL_SAND, 0, Block::SOUL_SAND, 0, false);
		$this->generateBox($level, $chunkBox, 8, 4, 4, 9, 4, 8, Block::SOUL_SAND, 0, Block::SOUL_SAND, 0, false);
		$this->generateBox($level, $chunkBox, 3, 5, 4, 4, 5, 8, Block::NETHER_WART_BLOCK, 0, Block::NETHER_WART_BLOCK, 0, false);
		$this->generateBox($level, $chunkBox, 8, 5, 4, 9, 5, 8, Block::NETHER_WART_BLOCK, 0, Block::NETHER_WART_BLOCK, 0, false);
		$this->generateBox($level, $chunkBox, 4, 2, 0, 8, 2, 12, Block::NETHER_BRICKS, 0, Block::NETHER_BRICKS, 0, false);
		$this->generateBox($level, $chunkBox, 0, 2, 4, 12, 2, 8, Block::NETHER_BRICKS, 0, Block::NETHER_BRICKS, 0, false);
		$this->generateBox($level, $chunkBox, 4, 0, 0, 8, 1, 3, Block::NETHER_BRICKS, 0, Block::NETHER_BRICKS, 0, false);
		$this->generateBox($level, $chunkBox, 4, 0, 9, 8, 1, 12, Block::NETHER_BRICKS, 0, Block::NETHER_BRICKS, 0, false);
		$this->generateBox($level, $chunkBox, 0, 0, 4, 3, 1, 8, Block::NETHER_BRICKS, 0, Block::NETHER_BRICKS, 0, false);
		$this->generateBox($level, $chunkBox, 9, 0, 4, 12, 1, 8, Block::NETHER_BRICKS, 0, Block::NETHER_BRICKS, 0, false);
		for($x = 4; $x <= 8; ++$x){
			for($z = 0; $z <= 2; ++$z){
				$this->fillColumnDown($level, Block::NETHER_BRICKS, 0, $x, -1, $z, $chunkBox);
				$this->fillColumnDown($level, Block::NETHER_BRICKS, 0, $x, -1, 12 - $z, $chunkBox);
			}
		}
		for($x = 0; $x <= 2; ++$x){
			for($z = 4; $z <= 8; ++$z){
				$this->fillColumnDown($level, Block::NETHER_BRICKS, 0, $x, -1, $z, $chunkBox);
				$this->fillColumnDown($level, Block::NETHER_BRICKS, 0, 12 - $x, -1, $z, $chunkBox);
			}
		}
		return true;
	}
}

class PmNetherCastleSmallCorridorPiece extends PmNetherBridgePiece{

	public static function createPiece(array &$pieces, int $x, int $y, int $z, int $orientation, int $genDepth){
		$boundingBox = PmNetherBoundingBox::orientBox($x, $y, $z, -1, 0, 0, 5, 7, 5, $orientation);
		return self::isOkBox($boundingBox) && self::findCollisionPiece($pieces, $boundingBox) === null ? new self($genDepth, $boundingBox, $orientation) : null;
	}

	public function addChildren(PmNetherStartPiece $start, array &$pieces, Random $random){
		$this->generateChildForward($start, $pieces, $random, 1, 0, true);
	}

	public function postProcess(ChunkManager $level, Random $random, PmNetherBoundingBox $chunkBox, int $chunkX, int $chunkZ) : bool{
		$this->generateBox($level, $chunkBox, 0, 0, 0, 4, 1, 4, Block::NETHER_BRICKS, 0, Block::NETHER_BRICKS, 0, false);
		$this->generateBox($level, $chunkBox, 0, 2, 0, 4, 5, 4, Block::AIR, 0, Block::AIR, 0, false);
		$this->generateBox($level, $chunkBox, 0, 2, 0, 0, 5, 4, Block::NETHER_BRICKS, 0, Block::NETHER_BRICKS, 0, false);
		$this->generateBox($level, $chunkBox, 4, 2, 0, 4, 5, 4, Block::NETHER_BRICKS, 0, Block::NETHER_BRICKS, 0, false);
		$this->generateBox($level, $chunkBox, 0, 3, 1, 0, 4, 1, Block::NETHER_BRICK_FENCE, 0, Block::NETHER_BRICK_FENCE, 0, false);
		$this->generateBox($level, $chunkBox, 0, 3, 3, 0, 4, 3, Block::NETHER_BRICK_FENCE, 0, Block::NETHER_BRICK_FENCE, 0, false);
		$this->generateBox($level, $chunkBox, 4, 3, 1, 4, 4, 1, Block::NETHER_BRICK_FENCE, 0, Block::NETHER_BRICK_FENCE, 0, false);
		$this->generateBox($level, $chunkBox, 4, 3, 3, 4, 4, 3, Block::NETHER_BRICK_FENCE, 0, Block::NETHER_BRICK_FENCE, 0, false);
		$this->generateBox($level, $chunkBox, 0, 6, 0, 4, 6, 4, Block::NETHER_BRICKS, 0, Block::NETHER_BRICKS, 0, false);
		for($x = 0; $x <= 4; ++$x){
			for($z = 0; $z <= 4; ++$z){
				$this->fillColumnDown($level, Block::NETHER_BRICKS, 0, $x, -1, $z, $chunkBox);
			}
		}
		return true;
	}
}

class PmNetherCastleSmallCorridorCrossingPiece extends PmNetherBridgePiece{

	public static function createPiece(array &$pieces, int $x, int $y, int $z, int $orientation, int $genDepth){
		$boundingBox = PmNetherBoundingBox::orientBox($x, $y, $z, -1, 0, 0, 5, 7, 5, $orientation);
		return self::isOkBox($boundingBox) && self::findCollisionPiece($pieces, $boundingBox) === null ? new self($genDepth, $boundingBox, $orientation) : null;
	}

	public function addChildren(PmNetherStartPiece $start, array &$pieces, Random $random){
		$this->generateChildForward($start, $pieces, $random, 1, 0, true);
		$this->generateChildLeft($start, $pieces, $random, 0, 1, true);
		$this->generateChildRight($start, $pieces, $random, 0, 1, true);
	}

	public function postProcess(ChunkManager $level, Random $random, PmNetherBoundingBox $chunkBox, int $chunkX, int $chunkZ) : bool{
		$this->generateBox($level, $chunkBox, 0, 0, 0, 4, 1, 4, Block::NETHER_BRICKS, 0, Block::NETHER_BRICKS, 0, false);
		$this->generateBox($level, $chunkBox, 0, 2, 0, 4, 5, 4, Block::AIR, 0, Block::AIR, 0, false);
		$this->generateBox($level, $chunkBox, 0, 2, 0, 0, 5, 0, Block::NETHER_BRICKS, 0, Block::NETHER_BRICKS, 0, false);
		$this->generateBox($level, $chunkBox, 4, 2, 0, 4, 5, 0, Block::NETHER_BRICKS, 0, Block::NETHER_BRICKS, 0, false);
		$this->generateBox($level, $chunkBox, 0, 2, 4, 0, 5, 4, Block::NETHER_BRICKS, 0, Block::NETHER_BRICKS, 0, false);
		$this->generateBox($level, $chunkBox, 4, 2, 4, 4, 5, 4, Block::NETHER_BRICKS, 0, Block::NETHER_BRICKS, 0, false);
		$this->generateBox($level, $chunkBox, 0, 6, 0, 4, 6, 4, Block::NETHER_BRICKS, 0, Block::NETHER_BRICKS, 0, false);
		for($x = 0; $x <= 4; ++$x){
			for($z = 0; $z <= 4; ++$z){
				$this->fillColumnDown($level, Block::NETHER_BRICKS, 0, $x, -1, $z, $chunkBox);
			}
		}
		return true;
	}
}

class PmNetherCastleSmallCorridorRightTurnPiece extends PmNetherBridgePiece{

	private $isNeedingChest;

	public function __construct(int $genDepth, Random $random, PmNetherBoundingBox $boundingBox, int $orientation){
		parent::__construct($genDepth, $boundingBox, $orientation);
		$this->isNeedingChest = $random->nextBoundedInt(3) === 0;
	}

	public static function createPiece(array &$pieces, Random $random, int $x, int $y, int $z, int $orientation, int $genDepth){
		$boundingBox = PmNetherBoundingBox::orientBox($x, $y, $z, -1, 0, 0, 5, 7, 5, $orientation);
		return self::isOkBox($boundingBox) && self::findCollisionPiece($pieces, $boundingBox) === null ? new self($genDepth, $random, $boundingBox, $orientation) : null;
	}

	public function addChildren(PmNetherStartPiece $start, array &$pieces, Random $random){
		$this->generateChildRight($start, $pieces, $random, 0, 1, true);
	}

	public function postProcess(ChunkManager $level, Random $random, PmNetherBoundingBox $chunkBox, int $chunkX, int $chunkZ) : bool{
		$this->generateBox($level, $chunkBox, 0, 0, 0, 4, 1, 4, Block::NETHER_BRICKS, 0, Block::NETHER_BRICKS, 0, false);
		$this->generateBox($level, $chunkBox, 0, 2, 0, 4, 5, 4, Block::AIR, 0, Block::AIR, 0, false);
		$this->generateBox($level, $chunkBox, 0, 2, 0, 0, 5, 4, Block::NETHER_BRICKS, 0, Block::NETHER_BRICKS, 0, false);
		$this->generateBox($level, $chunkBox, 0, 3, 1, 0, 4, 1, Block::NETHER_BRICK_FENCE, 0, Block::NETHER_BRICK_FENCE, 0, false);
		$this->generateBox($level, $chunkBox, 0, 3, 3, 0, 4, 3, Block::NETHER_BRICK_FENCE, 0, Block::NETHER_BRICK_FENCE, 0, false);
		$this->generateBox($level, $chunkBox, 4, 2, 0, 4, 5, 0, Block::NETHER_BRICKS, 0, Block::NETHER_BRICKS, 0, false);
		$this->generateBox($level, $chunkBox, 1, 2, 4, 4, 5, 4, Block::NETHER_BRICKS, 0, Block::NETHER_BRICKS, 0, false);
		$this->generateBox($level, $chunkBox, 1, 3, 4, 1, 4, 4, Block::NETHER_BRICK_FENCE, 0, Block::NETHER_BRICK_FENCE, 0, false);
		$this->generateBox($level, $chunkBox, 3, 3, 4, 3, 4, 4, Block::NETHER_BRICK_FENCE, 0, Block::NETHER_BRICK_FENCE, 0, false);
		if($this->isNeedingChest){
			$this->placeMarkedChest($level, self::chestMetaForFacing(self::oppositeDirection($this->orientation)), 1, 2, 3, $chunkBox);
		}
		$this->generateBox($level, $chunkBox, 0, 6, 0, 4, 6, 4, Block::NETHER_BRICKS, 0, Block::NETHER_BRICKS, 0, false);
		for($x = 0; $x <= 4; ++$x){
			for($z = 0; $z <= 4; ++$z){
				$this->fillColumnDown($level, Block::NETHER_BRICKS, 0, $x, -1, $z, $chunkBox);
			}
		}
		return true;
	}
}

class PmNetherCastleSmallCorridorLeftTurnPiece extends PmNetherBridgePiece{

	private $isNeedingChest;

	public function __construct(int $genDepth, Random $random, PmNetherBoundingBox $boundingBox, int $orientation){
		parent::__construct($genDepth, $boundingBox, $orientation);
		$this->isNeedingChest = $random->nextBoundedInt(3) === 0;
	}

	public static function createPiece(array &$pieces, Random $random, int $x, int $y, int $z, int $orientation, int $genDepth){
		$boundingBox = PmNetherBoundingBox::orientBox($x, $y, $z, -1, 0, 0, 5, 7, 5, $orientation);
		return self::isOkBox($boundingBox) && self::findCollisionPiece($pieces, $boundingBox) === null ? new self($genDepth, $random, $boundingBox, $orientation) : null;
	}

	public function addChildren(PmNetherStartPiece $start, array &$pieces, Random $random){
		$this->generateChildLeft($start, $pieces, $random, 0, 1, true);
	}

	public function postProcess(ChunkManager $level, Random $random, PmNetherBoundingBox $chunkBox, int $chunkX, int $chunkZ) : bool{
		$this->generateBox($level, $chunkBox, 0, 0, 0, 4, 1, 4, Block::NETHER_BRICKS, 0, Block::NETHER_BRICKS, 0, false);
		$this->generateBox($level, $chunkBox, 0, 2, 0, 4, 5, 4, Block::AIR, 0, Block::AIR, 0, false);
		$this->generateBox($level, $chunkBox, 4, 2, 0, 4, 5, 4, Block::NETHER_BRICKS, 0, Block::NETHER_BRICKS, 0, false);
		$this->generateBox($level, $chunkBox, 4, 3, 1, 4, 4, 1, Block::NETHER_BRICK_FENCE, 0, Block::NETHER_BRICK_FENCE, 0, false);
		$this->generateBox($level, $chunkBox, 4, 3, 3, 4, 4, 3, Block::NETHER_BRICK_FENCE, 0, Block::NETHER_BRICK_FENCE, 0, false);
		$this->generateBox($level, $chunkBox, 0, 2, 0, 0, 5, 0, Block::NETHER_BRICKS, 0, Block::NETHER_BRICKS, 0, false);
		$this->generateBox($level, $chunkBox, 0, 2, 4, 3, 5, 4, Block::NETHER_BRICKS, 0, Block::NETHER_BRICKS, 0, false);
		$this->generateBox($level, $chunkBox, 1, 3, 4, 1, 4, 4, Block::NETHER_BRICK_FENCE, 0, Block::NETHER_BRICK_FENCE, 0, false);
		$this->generateBox($level, $chunkBox, 3, 3, 4, 3, 4, 4, Block::NETHER_BRICK_FENCE, 0, Block::NETHER_BRICK_FENCE, 0, false);
		if($this->isNeedingChest){
			$this->placeMarkedChest($level, self::chestMetaForFacing(self::oppositeDirection($this->orientation)), 3, 2, 3, $chunkBox);
		}
		$this->generateBox($level, $chunkBox, 0, 6, 0, 4, 6, 4, Block::NETHER_BRICKS, 0, Block::NETHER_BRICKS, 0, false);
		for($x = 0; $x <= 4; ++$x){
			for($z = 0; $z <= 4; ++$z){
				$this->fillColumnDown($level, Block::NETHER_BRICKS, 0, $x, -1, $z, $chunkBox);
			}
		}
		return true;
	}
}

class PmNetherCastleCorridorStairsPiece extends PmNetherBridgePiece{

	public static function createPiece(array &$pieces, int $x, int $y, int $z, int $orientation, int $genDepth){
		$boundingBox = PmNetherBoundingBox::orientBox($x, $y, $z, -1, -7, 0, 5, 14, 10, $orientation);
		return self::isOkBox($boundingBox) && self::findCollisionPiece($pieces, $boundingBox) === null ? new self($genDepth, $boundingBox, $orientation) : null;
	}

	public function addChildren(PmNetherStartPiece $start, array &$pieces, Random $random){
		$this->generateChildForward($start, $pieces, $random, 1, 0, true);
	}

	public function postProcess(ChunkManager $level, Random $random, PmNetherBoundingBox $chunkBox, int $chunkX, int $chunkZ) : bool{
		$stairsSouth = self::stairMeta(self::SOUTH);
		for($i = 0; $i <= 9; ++$i){
			$maxY = max(1, 7 - $i);
			$minY = min(max($maxY + 5, 14 - $i), 13);
			$this->generateBox($level, $chunkBox, 0, 0, $i, 4, $maxY, $i, Block::NETHER_BRICKS, 0, Block::NETHER_BRICKS, 0, false);
			$this->generateBox($level, $chunkBox, 1, $maxY + 1, $i, 3, $minY - 1, $i, Block::AIR, 0, Block::AIR, 0, false);
			if($i <= 6){
				$this->placeBlock($level, Block::NETHER_BRICKS_STAIRS, $stairsSouth, 1, $maxY + 1, $i, $chunkBox);
				$this->placeBlock($level, Block::NETHER_BRICKS_STAIRS, $stairsSouth, 2, $maxY + 1, $i, $chunkBox);
				$this->placeBlock($level, Block::NETHER_BRICKS_STAIRS, $stairsSouth, 3, $maxY + 1, $i, $chunkBox);
			}
			$this->generateBox($level, $chunkBox, 0, $minY, $i, 4, $minY, $i, Block::NETHER_BRICKS, 0, Block::NETHER_BRICKS, 0, false);
			$this->generateBox($level, $chunkBox, 0, $maxY + 1, $i, 0, $minY - 1, $i, Block::NETHER_BRICKS, 0, Block::NETHER_BRICKS, 0, false);
			$this->generateBox($level, $chunkBox, 4, $maxY + 1, $i, 4, $minY - 1, $i, Block::NETHER_BRICKS, 0, Block::NETHER_BRICKS, 0, false);
			if(($i & 1) === 0){
				$this->generateBox($level, $chunkBox, 0, $maxY + 2, $i, 0, $maxY + 3, $i, Block::NETHER_BRICK_FENCE, 0, Block::NETHER_BRICK_FENCE, 0, false);
				$this->generateBox($level, $chunkBox, 4, $maxY + 2, $i, 4, $maxY + 3, $i, Block::NETHER_BRICK_FENCE, 0, Block::NETHER_BRICK_FENCE, 0, false);
			}
			for($x = 0; $x <= 4; ++$x){
				$this->fillColumnDown($level, Block::NETHER_BRICKS, 0, $x, -1, $i, $chunkBox);
			}
		}
		return true;
	}
}

class PmNetherCastleCorridorTBalconyPiece extends PmNetherBridgePiece{

	public static function createPiece(array &$pieces, int $x, int $y, int $z, int $orientation, int $genDepth){
		$boundingBox = PmNetherBoundingBox::orientBox($x, $y, $z, -3, 0, 0, 9, 7, 9, $orientation);
		return self::isOkBox($boundingBox) && self::findCollisionPiece($pieces, $boundingBox) === null ? new self($genDepth, $boundingBox, $orientation) : null;
	}

	public function addChildren(PmNetherStartPiece $start, array &$pieces, Random $random){
		$horizontalOffset = 1;
		if($this->orientation === self::WEST || $this->orientation === self::NORTH){
			$horizontalOffset = 5;
		}
		$this->generateChildLeft($start, $pieces, $random, 0, $horizontalOffset, $random->nextBoundedInt(8) > 0);
		$this->generateChildRight($start, $pieces, $random, 0, $horizontalOffset, $random->nextBoundedInt(8) > 0);
	}

	public function postProcess(ChunkManager $level, Random $random, PmNetherBoundingBox $chunkBox, int $chunkX, int $chunkZ) : bool{
		$this->generateBox($level, $chunkBox, 0, 0, 0, 8, 1, 8, Block::NETHER_BRICKS, 0, Block::NETHER_BRICKS, 0, false);
		$this->generateBox($level, $chunkBox, 0, 2, 0, 8, 5, 8, Block::AIR, 0, Block::AIR, 0, false);
		$this->generateBox($level, $chunkBox, 0, 6, 0, 8, 6, 5, Block::NETHER_BRICKS, 0, Block::NETHER_BRICKS, 0, false);
		$this->generateBox($level, $chunkBox, 0, 2, 0, 2, 5, 0, Block::NETHER_BRICKS, 0, Block::NETHER_BRICKS, 0, false);
		$this->generateBox($level, $chunkBox, 6, 2, 0, 8, 5, 0, Block::NETHER_BRICKS, 0, Block::NETHER_BRICKS, 0, false);
		$this->generateBox($level, $chunkBox, 1, 3, 0, 1, 4, 0, Block::NETHER_BRICK_FENCE, 0, Block::NETHER_BRICK_FENCE, 0, false);
		$this->generateBox($level, $chunkBox, 7, 3, 0, 7, 4, 0, Block::NETHER_BRICK_FENCE, 0, Block::NETHER_BRICK_FENCE, 0, false);
		$this->generateBox($level, $chunkBox, 0, 2, 4, 8, 2, 8, Block::NETHER_BRICKS, 0, Block::NETHER_BRICKS, 0, false);
		$this->generateBox($level, $chunkBox, 1, 1, 4, 2, 2, 4, Block::AIR, 0, Block::AIR, 0, false);
		$this->generateBox($level, $chunkBox, 6, 1, 4, 7, 2, 4, Block::AIR, 0, Block::AIR, 0, false);
		$this->generateBox($level, $chunkBox, 1, 3, 8, 7, 3, 8, Block::NETHER_BRICK_FENCE, 0, Block::NETHER_BRICK_FENCE, 0, false);
		$this->placeBlock($level, Block::NETHER_BRICK_FENCE, 0, 0, 3, 8, $chunkBox);
		$this->placeBlock($level, Block::NETHER_BRICK_FENCE, 0, 8, 3, 8, $chunkBox);
		$this->generateBox($level, $chunkBox, 0, 3, 6, 0, 3, 7, Block::NETHER_BRICK_FENCE, 0, Block::NETHER_BRICK_FENCE, 0, false);
		$this->generateBox($level, $chunkBox, 8, 3, 6, 8, 3, 7, Block::NETHER_BRICK_FENCE, 0, Block::NETHER_BRICK_FENCE, 0, false);
		$this->generateBox($level, $chunkBox, 0, 3, 4, 0, 5, 5, Block::NETHER_BRICKS, 0, Block::NETHER_BRICKS, 0, false);
		$this->generateBox($level, $chunkBox, 8, 3, 4, 8, 5, 5, Block::NETHER_BRICKS, 0, Block::NETHER_BRICKS, 0, false);
		$this->generateBox($level, $chunkBox, 1, 3, 5, 2, 5, 5, Block::NETHER_BRICKS, 0, Block::NETHER_BRICKS, 0, false);
		$this->generateBox($level, $chunkBox, 6, 3, 5, 7, 5, 5, Block::NETHER_BRICKS, 0, Block::NETHER_BRICKS, 0, false);
		$this->generateBox($level, $chunkBox, 1, 4, 5, 1, 5, 5, Block::NETHER_BRICK_FENCE, 0, Block::NETHER_BRICK_FENCE, 0, false);
		$this->generateBox($level, $chunkBox, 7, 4, 5, 7, 5, 5, Block::NETHER_BRICK_FENCE, 0, Block::NETHER_BRICK_FENCE, 0, false);
		for($x = 0; $x <= 8; ++$x){
			for($z = 0; $z <= 5; ++$z){
				$this->fillColumnDown($level, Block::NETHER_BRICKS, 0, $x, -1, $z, $chunkBox);
			}
		}
		return true;
	}
}
