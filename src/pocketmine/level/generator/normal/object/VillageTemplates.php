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

namespace pocketmine\level\generator\normal\object;

require_once __DIR__ . "/VillageSmithyChestLoot.php";

use pocketmine\block\Block;
use pocketmine\level\ChunkManager;
use pocketmine\level\generator\object\PopulatorObject;
use pocketmine\level\generator\biome\Biome;
use pocketmine\utils\Random;

if (!class_exists('pocketmine\\level\\generator\\normal\\object\\PmVillagePiece', false)) {
abstract class PmVillagePiece extends PopulatorObject{

	const NORTH = 0;
	const EAST = 1;
	const SOUTH = 2;
	const WEST = 3;
	const NATURAL_VILLAGE_LADDER_MARKER = 0x4c44;
	const NATURAL_VILLAGE_LADDER_MARKER_ID = 0x44;
	const NATURAL_VILLAGE_LADDER_MARKER_DATA = 0x4c;

	/** @var ChunkManager */
	protected $level;
	protected $x;
	protected $y;
	protected $z;
	protected $orientation;

	protected $replaceable = [
		Block::AIR => true,
		Block::SAPLING => true,
		Block::LEAVES => true,
		Block::LEAVES2 => true,
		Block::TALL_GRASS => true,
		Block::DANDELION => true,
		Block::RED_FLOWER => true,
		Block::DOUBLE_PLANT => true,
		Block::SNOW_LAYER => true,
		Block::WATER_LILY => true,
	];

	public function __construct(int $orientation = self::NORTH){
		$this->orientation = $orientation;
	}

	abstract protected function getSize() : array;

	abstract public function placeObject(ChunkManager $level, $x, $y, $z, Random $random);

	protected function getEntranceOffset() : int{
		list($width, ) = $this->getSize();
		return intdiv(max(0, $width - 1), 2);
	}

	public function getPlacementOriginFromFrontStep(int $x, int $z) : array{
		$entranceOffset = $this->getEntranceOffset();
		switch($this->orientation){
			case self::EAST:
				return ["x" => $x - 1, "z" => $z - $entranceOffset];
			case self::SOUTH:
				return ["x" => $x + $entranceOffset, "z" => $z - 1];
			case self::WEST:
				return ["x" => $x + 1, "z" => $z + $entranceOffset];
			case self::NORTH:
			default:
				return ["x" => $x - $entranceOffset, "z" => $z + 1];
		}
	}

	public function canPlaceObject(ChunkManager $level, $x, $y, $z, Random $random){
		$this->level = $level;
		$this->x = (int) $x;
		$this->y = (int) $y;
		$this->z = (int) $z;

		list($width, $height, $depth) = $this->getSize();
		for($dx = 0; $dx < $width; ++$dx){
			for($dz = 0; $dz < $depth; ++$dz){
				list($wx, $wz) = $this->toWorldXZ($dx, $dz);
				$ground = $level->getBlockIdAt($wx, $this->y, $wz);
				if(!$this->isGround($ground)){
					return false;
				}
				for($dy = 1; $dy <= $height; ++$dy){
					$id = $level->getBlockIdAt($wx, $this->y + $dy, $wz);
					if(!isset($this->replaceable[$id])){
						return false;
					}
				}
			}
		}

		return true;
	}

	public function getWorldBoundsAt(int $x, int $z) : array{
		list($width, , $depth) = $this->getSize();
		$points = [
			[0, 0],
			[$width - 1, 0],
			[0, $depth - 1],
			[$width - 1, $depth - 1],
		];
		$xs = [];
		$zs = [];
		foreach($points as $point){
			list($dx, $dz) = $point;
			switch($this->orientation){
				case self::EAST:
					$xs[] = $x - $dz;
					$zs[] = $z + $dx;
					break;
				case self::SOUTH:
					$xs[] = $x - $dx;
					$zs[] = $z - $dz;
					break;
				case self::WEST:
					$xs[] = $x + $dz;
					$zs[] = $z - $dx;
					break;
				case self::NORTH:
				default:
					$xs[] = $x + $dx;
					$zs[] = $z + $dz;
					break;
			}
		}

		return [min($xs), max($xs), min($zs), max($zs)];
	}

	protected function isGround(int $id) : bool{
		return $id === Block::GRASS || $id === Block::DIRT || $id === Block::SAND || $id === Block::SANDSTONE || $id === Block::GRAVEL || $id === Block::COBBLESTONE || $id === Block::PLANK || $id === Block::STONE || $id === Block::FARMLAND || $id === Block::GRASS_PATH;
	}

	protected function toWorldXZ(int $dx, int $dz) : array{
		switch($this->orientation){
			case self::EAST:
				return [$this->x - $dz, $this->z + $dx];
			case self::SOUTH:
				return [$this->x - $dx, $this->z - $dz];
			case self::WEST:
				return [$this->x + $dz, $this->z - $dx];
			case self::NORTH:
			default:
				return [$this->x + $dx, $this->z + $dz];
		}
	}

	protected function setBlock(int $dx, int $dy, int $dz, int $id, int $meta = 0){
		list($wx, $wz) = $this->toWorldXZ($dx, $dz);
		$this->level->setBlockIdAt($wx, $this->y + $dy, $wz, $id);
		$this->level->setBlockDataAt($wx, $this->y + $dy, $wz, $meta);
	}

	protected function fillBox(int $x1, int $y1, int $z1, int $x2, int $y2, int $z2, int $id, int $meta = 0){
		for($dx = min($x1, $x2); $dx <= max($x1, $x2); ++$dx){
			for($dy = min($y1, $y2); $dy <= max($y1, $y2); ++$dy){
				for($dz = min($z1, $z2); $dz <= max($z1, $z2); ++$dz){
					$this->setBlock($dx, $dy, $dz, $id, $meta);
				}
			}
		}
	}

	protected function hollowBox(int $x1, int $y1, int $z1, int $x2, int $y2, int $z2, int $id, int $meta = 0){
		for($dx = min($x1, $x2); $dx <= max($x1, $x2); ++$dx){
			for($dy = min($y1, $y2); $dy <= max($y1, $y2); ++$dy){
				for($dz = min($z1, $z2); $dz <= max($z1, $z2); ++$dz){
					if($dx === $x1 || $dx === $x2 || $dy === $y1 || $dy === $y2 || $dz === $z1 || $dz === $z2){
						$this->setBlock($dx, $dy, $dz, $id, $meta);
					}
				}
			}
		}
	}

	protected function clearBox(int $x1, int $y1, int $z1, int $x2, int $y2, int $z2){
		$this->fillBox($x1, $y1, $z1, $x2, $y2, $z2, Block::AIR, 0);
	}

	protected function column(int $dx, int $fromY, int $toY, int $dz, int $id, int $meta = 0){
		for($dy = min($fromY, $toY); $dy <= max($fromY, $toY); ++$dy){
			$this->setBlock($dx, $dy, $dz, $id, $meta);
		}
	}

	protected function placeNaturalLadderColumn(int $dx, int $fromY, int $toY, int $dz, int $localSupportSide){
		for($dy = min($fromY, $toY); $dy <= max($fromY, $toY); ++$dy){
			$this->placeNaturalLadder($dx, $dy, $dz, $localSupportSide);
		}
	}

	protected function placeNaturalLadder(int $dx, int $dy, int $dz, int $localSupportSide){
		$this->setBlock($dx, $dy, $dz, Block::LADDER, $this->getLadderMetaForSupportSide($this->rotateHorizontalSide($localSupportSide)));
		$this->markNaturalLadder($dx, $dy, $dz);
	}

	protected function markNaturalLadder(int $dx, int $dy, int $dz){
		list($wx, $wz) = $this->toWorldXZ($dx, $dz);
		$wy = $this->y + $dy;
		if(method_exists($this->level, "setBlockExtraDataAt")){
			$this->level->setBlockExtraDataAt($wx, $wy, $wz, self::NATURAL_VILLAGE_LADDER_MARKER_ID, self::NATURAL_VILLAGE_LADDER_MARKER_DATA);
			return;
		}

		$chunk = $this->level->getChunk($wx >> 4, $wz >> 4);
		if($chunk !== null && method_exists($chunk, "setBlockExtraData")){
			$chunk->setBlockExtraData($wx & 0x0f, $wy & 0x7f, $wz & 0x0f, self::NATURAL_VILLAGE_LADDER_MARKER);
		}
	}

	protected function foundation(int $width, int $depth, int $topId, int $topMeta = 0, int $fillId = Block::DIRT){
		for($dx = 0; $dx < $width; ++$dx){
			for($dz = 0; $dz < $depth; ++$dz){
				$this->setBlock($dx, 0, $dz, $topId, $topMeta);
				for($dy = -1; $dy >= -6; --$dy){
					list($wx, $wz) = $this->toWorldXZ($dx, $dz);
					$id = $this->level->getBlockIdAt($wx, $this->y + $dy, $wz);
					if($id !== Block::AIR && $id !== Block::WATER && $id !== Block::STILL_WATER && $id !== Block::LEAVES && $id !== Block::LEAVES2 && $id !== Block::TALL_GRASS && $id !== Block::DOUBLE_PLANT){
						break;
					}
					$this->setBlock($dx, $dy, $dz, $fillId, 0);
				}
			}
		}
	}

	protected function roofRows(int $width, int $fromZ, int $toZ, int $baseY, int $ridgeId = Block::PLANK){
		$minZ = min($fromZ, $toZ);
		$maxZ = max($fromZ, $toZ);
		$left = $minZ;
		$right = $maxZ;
		$layer = 0;
		while($left <= $right){
			for($dx = 0; $dx < $width; ++$dx){
				$this->placeStairs($dx, $baseY + $layer, $left, Block::WOOD_STAIRS, self::NORTH);
				$this->placeStairs($dx, $baseY + $layer, $right, Block::WOOD_STAIRS, self::SOUTH);
				if($left !== $right){
					for($dz = $left + 1; $dz < $right; ++$dz){
						$this->setBlock($dx, $baseY + $layer, $dz, $ridgeId);
					}
				}
			}
			++$left;
			--$right;
			++$layer;
		}
	}

	protected function placeEntranceNorth(int $x){
		$this->setBlock($x, 1, 0, Block::AIR);
		$this->setBlock($x, 2, 0, Block::AIR);
	}

	protected function placeStairs(int $dx, int $dy, int $dz, int $id, int $localSide){
		$this->setBlock($dx, $dy, $dz, $id, $this->getStairMetaForSide($this->rotateHorizontalSide($localSide)));
	}

	protected function getStairMetaForSide(int $side) : int{
		switch($side){
			case self::NORTH:
				return 2;
			case self::SOUTH:
				return 3;
			case self::WEST:
				return 0;
			case self::EAST:
			default:
				return 1;
		}
	}

	protected function placeDoor(int $dx, int $dy, int $dz, int $localFacingSide){
		$this->setBlock($dx, $dy, $dz, Block::WOODEN_DOOR_BLOCK, $this->getDoorMetaForFacingSide($this->rotateHorizontalSide($localFacingSide)));
		$this->setBlock($dx, $dy + 1, $dz, Block::WOODEN_DOOR_BLOCK, 0x08);
	}

	protected function placeMarkedSmithyChest(int $dx, int $dy, int $dz, int $localFacingSide){
		$this->setBlock($dx, $dy, $dz, Block::CHEST, $this->getChestMetaForFacingSide($this->rotateHorizontalSide($localFacingSide)));
		list($wx, $wz) = $this->toWorldXZ($dx, $dz);
		if(method_exists($this->level, "setBlockExtraDataAt")){
			$this->level->setBlockExtraDataAt($wx, $this->y + $dy, $wz, VillageSmithyChestLoot::CHEST_MARKER_ID, VillageSmithyChestLoot::CHEST_MARKER_DATA);
			return;
		}
		$chunk = $this->level->getChunk($wx >> 4, $wz >> 4);
		if($chunk !== null){
			$chunk->setBlockExtraData($wx & 0x0f, ($this->y + $dy) & 0x7f, $wz & 0x0f, VillageSmithyChestLoot::CHEST_MARKER);
		}
	}

	protected function getChestMetaForFacingSide(int $side) : int{
		switch($side){
			case self::SOUTH:
				return 3;
			case self::NORTH:
				return 2;
			case self::WEST:
				return 4;
			case self::EAST:
			default:
				return 5;
		}
	}

	protected function getDoorMetaForFacingSide(int $side) : int{
		switch($side){
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

	protected function placeTorch(int $dx, int $dy, int $dz){
		$this->setBlock($dx, $dy, $dz, Block::TORCH);
	}

	protected function placeAttachedTorch(int $dx, int $dy, int $dz, int $localSupportSide){
		$this->setBlock($dx, $dy, $dz, Block::TORCH, $this->getTorchMetaForSupportSide($this->rotateHorizontalSide($localSupportSide)));
	}

	protected function rotateHorizontalSide(int $side) : int{
		return ($side + $this->orientation) & 3;
	}

	protected function getTorchMetaForSupportSide(int $side) : int{
		switch($side){
			case self::WEST:
				return 1;
			case self::EAST:
				return 2;
			case self::NORTH:
				return 3;
			case self::SOUTH:
				return 4;
			default:
				return 0;
		}
	}

	protected function getLadderMetaForSupportSide(int $side) : int{
		switch($side){
			case self::SOUTH:
				return 2;
			case self::NORTH:
				return 3;
			case self::EAST:
				return 4;
			case self::WEST:
			default:
				return 5;
		}
	}

	/**
	 * Fix village surface blocks for cold biomes after generation.
	 * - Taiga / Cold Taiga: Grass/Dirt becomes Podzol
	 * - Ice Plains (snowy): Grass top + Snow Layer (aligned with Populator)
	 */
	public static function fixVillageSurface(ChunkManager $level, int $cx, int $cz, int $biomeId){
		$TAIGA = defined('Biome::TAIGA') ? Biome::TAIGA : 5;
		$COLD_TAIGA = defined('Biome::COLD_TAIGA') ? Biome::COLD_TAIGA : 30;
		$ICE_PLAINS = defined('Biome::ICE_PLAINS') ? Biome::ICE_PLAINS : 12;

		if($biomeId !== $TAIGA && $biomeId !== $COLD_TAIGA && $biomeId !== $ICE_PLAINS){
			return;
		}

		$baseX = $cx << 4;
		$baseZ = $cz << 4;
		for($dx = 0; $dx < 16; ++$dx){
			for($dz = 0; $dz < 16; ++$dz){
				$wx = $baseX + $dx;
				$wz = $baseZ + $dz;
				for($y = 255; $y > 0; --$y){
					$id = $level->getBlockIdAt($wx, $y, $wz);
					if($id === Block::AIR || $id === Block::LEAVES || $id === Block::LEAVES2){
						continue;
					}
					if($id === Block::GRASS || $id === Block::DIRT){
						if($biomeId === $TAIGA || $biomeId === $COLD_TAIGA){
							$level->setBlockIdAt($wx, $y, $wz, Block::PODZOL);
							$level->setBlockDataAt($wx, $y, $wz, 1);
						}elseif($biomeId === $ICE_PLAINS){
							$level->setBlockIdAt($wx, $y, $wz, Block::GRASS);
							$level->setBlockDataAt($wx, $y, $wz, 0);
							if($level->getBlockIdAt($wx, $y + 1, $wz) === Block::AIR){
								$level->setBlockIdAt($wx, $y + 1, $wz, Block::SNOW_LAYER);
								$level->setBlockDataAt($wx, $y + 1, $wz, 0);
							}
						}
						break;
					}
					break;
				}
			}
		}
	}
}
} // 包住 abstract class PmVillagePiece 结束

/* ================== 以下 11 个建筑子类，全部用 class_exists 包裹 ================== */

if (!class_exists('pocketmine\\level\\generator\\normal\\object\\PmVillageWell', false)) {
class PmVillageWell extends PmVillagePiece{
	protected function getSize() : array{ return [6, 4, 6]; }
	public function placeObject(ChunkManager $level, $x, $y, $z, Random $random){
		$this->level = $level; $this->x = (int)$x; $this->y = (int)$y; $this->z = (int)$z;
		$this->foundation(6, 6, Block::COBBLESTONE, 0, Block::COBBLESTONE);
		$this->fillBox(1, -11, 1, 4, 1, 4, Block::COBBLESTONE);
		$this->fillBox(2, -11, 2, 3, 0, 3, Block::STILL_WATER);
		$this->clearBox(2, 1, 2, 3, 1, 3);
		foreach([[1,2,1],[1,3,1],[4,2,1],[4,3,1],[1,2,4],[1,3,4],[4,2,4],[4,3,4]] as $f){
			$this->setBlock($f[0], $f[1], $f[2], Block::FENCE);
		}
		$this->fillBox(1, 4, 1, 4, 4, 4, Block::COBBLESTONE);
		for($dx = 0; $dx <= 5; ++$dx){
			for($dz = 0; $dz <= 5; ++$dz){
				if($dx === 0 || $dx === 5 || $dz === 0 || $dz === 5){
					$this->setBlock($dx, 0, $dz, Block::COBBLESTONE);
				}
			}
		}
	}
}
}

if (!class_exists('pocketmine\\level\\generator\\normal\\object\\PmVillageSimpleHouse', false)) {
class PmVillageSimpleHouse extends PmVillagePiece{
	protected function getSize() : array{ return [5, 6, 5]; }
	public function placeObject(ChunkManager $level, $x, $y, $z, Random $random){
		$this->level = $level; $this->x = (int)$x; $this->y = (int)$y; $this->z = (int)$z;
		$terrace = $random->nextBoundedInt(2) === 0;
		$this->foundation(5, 5, Block::COBBLESTONE, 0, Block::COBBLESTONE);
		$this->fillBox(0, 4, 0, 4, 4, 4, Block::WOOD);
		$this->fillBox(1, 4, 1, 3, 4, 3, Block::PLANK);
		foreach([[0,1,0],[0,2,0],[0,3,0],[4,1,0],[4,2,0],[4,3,0],[0,1,4],[0,2,4],[0,3,4],[4,1,4],[4,2,4],[4,3,4]] as $c){
			$this->setBlock($c[0], $c[1], $c[2], Block::COBBLESTONE);
		}
		$this->fillBox(0, 1, 1, 0, 3, 3, Block::PLANK);
		$this->fillBox(4, 1, 1, 4, 3, 3, Block::PLANK);
		$this->fillBox(1, 1, 4, 3, 3, 4, Block::PLANK);
		$this->setBlock(0, 2, 2, Block::GLASS_PANE);
		$this->setBlock(2, 2, 4, Block::GLASS_PANE);
		$this->setBlock(4, 2, 2, Block::GLASS_PANE);
		$this->fillBox(1, 1, 0, 3, 3, 0, Block::PLANK);
		$this->placeEntranceNorth(2);
		$this->placeDoor(2, 1, 0, self::NORTH);
		$this->placeStairs(2, 0, -1, Block::COBBLESTONE_STAIRS, self::NORTH);
		if($terrace){
			for($i = 0; $i <= 4; ++$i){
				$this->setBlock($i, 5, 0, Block::FENCE);
				$this->setBlock($i, 5, 4, Block::FENCE);
				$this->setBlock(0, 5, $i, Block::FENCE);
				$this->setBlock(4, 5, $i, Block::FENCE);
			}
			$this->placeNaturalLadderColumn(3, 1, 4, 3, self::SOUTH);
		}
		$this->placeTorch(2, 3, 1);
		$this->setBlock(2, 1, 1, Block::TORCH);
	}
}
}

if (!class_exists('pocketmine\\level\\generator\\normal\\object\\PmVillageSmallTemple', false)) {
class PmVillageSmallTemple extends PmVillagePiece{
	protected function getSize() : array{ return [5, 12, 9]; }
	public function placeObject(ChunkManager $level, $x, $y, $z, Random $random){
		$this->level = $level; $this->x = (int)$x; $this->y = (int)$y; $this->z = (int)$z;
		$this->foundation(5, 9, Block::COBBLESTONE, 0, Block::COBBLESTONE);
		$this->fillBox(1, 1, 0, 3, 10, 0, Block::COBBLESTONE);
		$this->fillBox(0, 1, 1, 0, 10, 3, Block::COBBLESTONE);
		$this->fillBox(4, 1, 1, 4, 10, 3, Block::COBBLESTONE);
		$this->fillBox(0, 0, 4, 0, 4, 7, Block::COBBLESTONE);
		$this->fillBox(4, 0, 4, 4, 4, 7, Block::COBBLESTONE);
		$this->fillBox(1, 1, 8, 3, 4, 8, Block::COBBLESTONE);
		$this->fillBox(1, 5, 4, 3, 10, 4, Block::COBBLESTONE);
		$this->fillBox(1, 5, 5, 3, 5, 7, Block::COBBLESTONE);
		$this->fillBox(0, 9, 0, 4, 9, 4, Block::COBBLESTONE);
		$this->fillBox(0, 4, 0, 4, 4, 4, Block::COBBLESTONE);
		foreach([[0,11,2],[4,11,2],[2,11,0],[2,11,4]] as $p){ $this->setBlock($p[0], $p[1], $p[2], Block::COBBLESTONE); }
		$this->setBlock(1, 1, 6, Block::COBBLESTONE); $this->setBlock(1, 1, 7, Block::COBBLESTONE); $this->setBlock(2, 1, 7, Block::COBBLESTONE); $this->setBlock(3, 1, 6, Block::COBBLESTONE); $this->setBlock(3, 1, 7, Block::COBBLESTONE);
		$this->placeStairs(1, 1, 5, Block::COBBLESTONE_STAIRS, self::NORTH);
		$this->placeStairs(2, 1, 6, Block::COBBLESTONE_STAIRS, self::NORTH);
		$this->placeStairs(3, 1, 5, Block::COBBLESTONE_STAIRS, self::NORTH);
		$this->placeStairs(1, 2, 7, Block::COBBLESTONE_STAIRS, self::WEST);
		$this->placeStairs(3, 2, 7, Block::COBBLESTONE_STAIRS, self::EAST);
		foreach([[0,2,2],[0,3,2],[4,2,2],[4,3,2],[0,6,2],[0,7,2],[4,6,2],[4,7,2],[2,6,0],[2,7,0],[2,6,4],[2,7,4],[0,3,6],[4,3,6],[2,3,8]] as $p){
			$this->setBlock($p[0], $p[1], $p[2], Block::GLASS_PANE);
		}
		$this->placeTorch(2, 4, 7); $this->placeTorch(1, 4, 6); $this->placeTorch(3, 4, 6); $this->placeTorch(2, 4, 5);
		$this->placeNaturalLadderColumn(3, 1, 9, 3, self::EAST);
		$this->placeEntranceNorth(2);
		$this->placeDoor(2, 1, 0, self::NORTH);
		$this->placeStairs(2, 0, -1, Block::COBBLESTONE_STAIRS, self::NORTH);
	}
}
}

if (!class_exists('pocketmine\\level\\generator\\normal\\object\\PmVillageBookHouse', false)) {
class PmVillageBookHouse extends PmVillagePiece{
	protected function getEntranceOffset() : int{ return 1; }
	protected function getSize() : array{ return [9, 9, 6]; }
	public function placeObject(ChunkManager $level, $x, $y, $z, Random $random){
		$this->level = $level; $this->x = (int)$x; $this->y = (int)$y; $this->z = (int)$z;
		$this->foundation(9, 6, Block::COBBLESTONE, 0, Block::COBBLESTONE);
		$this->fillBox(0, 0, 0, 8, 0, 5, Block::COBBLESTONE);
		$this->fillBox(0, 5, 0, 8, 5, 5, Block::COBBLESTONE);
		$this->fillBox(0, 6, 1, 8, 6, 4, Block::COBBLESTONE);
		$this->fillBox(0, 7, 2, 8, 7, 3, Block::COBBLESTONE);
		$this->clearBox(1, 1, 1, 7, 5, 4);
		$this->fillBox(0, 1, 0, 0, 4, 5, Block::COBBLESTONE);
		$this->fillBox(8, 1, 0, 8, 4, 5, Block::COBBLESTONE);
		$this->fillBox(1, 1, 0, 7, 3, 0, Block::PLANK);
		$this->fillBox(1, 1, 5, 7, 4, 5, Block::PLANK);
		foreach([[4,2,0],[5,2,0],[6,2,0],[4,3,0],[5,3,0],[6,3,0],[0,2,2],[0,2,3],[0,3,2],[0,3,3],[8,2,2],[8,2,3],[8,3,2],[8,3,3],[2,2,5],[3,2,5],[5,2,5],[6,2,5]] as $p){
			$this->setBlock($p[0], $p[1], $p[2], Block::GLASS_PANE);
		}
		for($dz = 1; $dz <= 4; ++$dz){ $this->setBlock(7, 3, $dz, Block::BOOKSHELF); }
		$this->setBlock(1, 1, 0, Block::AIR); $this->setBlock(1, 2, 0, Block::AIR); $this->placeDoor(1, 1, 0, self::NORTH); $this->placeStairs(1, 0, -1, Block::COBBLESTONE_STAIRS, self::NORTH);
		$this->roofRows(9, 0, 5, 6, Block::PLANK);
		$this->setBlock(7, 1, 1, Block::WORKBENCH);
		$this->setBlock(6, 1, 3, Block::FENCE);
		$this->setBlock(4, 1, 3, Block::FENCE);
	}
}
}

if (!class_exists('pocketmine\\level\\generator\\normal\\object\\PmVillageSmallHut', false)) {
class PmVillageSmallHut extends PmVillagePiece{
	protected function getSize() : array{ return [4, 6, 5]; }
	public function placeObject(ChunkManager $level, $x, $y, $z, Random $random){
		$this->level = $level; $this->x = (int)$x; $this->y = (int)$y; $this->z = (int)$z;
		$compoundRoof = $random->nextBoundedInt(2) === 0;
		$tablePos = $random->nextBoundedInt(3);
		$this->foundation(4, 5, Block::COBBLESTONE, 0, Block::COBBLESTONE);
		$this->clearBox(1, 1, 1, 3, 5, 4);
		$this->fillBox(1, 4, 1, 2, 4, 3, $compoundRoof ? Block::WOOD : Block::AIR);
		if(!$compoundRoof){ $this->fillBox(1, 5, 1, 2, 5, 3, Block::WOOD); }
		foreach([[1,4,0],[2,4,0],[1,4,4],[2,4,4],[0,4,1],[0,4,2],[0,4,3],[3,4,1],[3,4,2],[3,4,3]] as $p){ $this->setBlock($p[0], $p[1], $p[2], Block::WOOD); }
		$this->fillBox(0, 1, 0, 0, 3, 0, Block::WOOD);
		$this->fillBox(3, 1, 0, 3, 3, 0, Block::WOOD);
		$this->fillBox(0, 1, 4, 0, 3, 4, Block::WOOD);
		$this->fillBox(3, 1, 4, 3, 3, 4, Block::WOOD);
		$this->fillBox(0, 1, 1, 0, 3, 3, Block::PLANK);
		$this->fillBox(3, 1, 1, 3, 3, 3, Block::PLANK);
		$this->fillBox(1, 1, 0, 2, 3, 0, Block::PLANK);
		$this->fillBox(1, 1, 4, 2, 3, 4, Block::PLANK);
		$this->setBlock(0, 2, 2, Block::GLASS_PANE);
		$this->setBlock(3, 2, 2, Block::GLASS_PANE);
		if($tablePos > 0){ $this->setBlock($tablePos, 1, 3, Block::FENCE); $this->setBlock($tablePos, 2, 3, Block::CARPET, 12); }
		$this->setBlock(1, 1, 0, Block::AIR); $this->setBlock(1, 2, 0, Block::AIR); $this->placeDoor(1, 1, 0, self::NORTH); $this->placeStairs(1, 0, -1, Block::COBBLESTONE_STAIRS, self::NORTH);
	}
}
}

if (!class_exists('pocketmine\\level\\generator\\normal\\object\\PmVillagePigHouse', false)) {
class PmVillagePigHouse extends PmVillagePiece{
	protected function getEntranceOffset() : int{ return 2; }
	protected function getSize() : array{ return [9, 7, 11]; }
	public function placeObject(ChunkManager $level, $x, $y, $z, Random $random){
		$this->level = $level; $this->x = (int)$x; $this->y = (int)$y; $this->z = (int)$z;
		$this->foundation(9, 11, Block::COBBLESTONE, 0, Block::COBBLESTONE);
		$this->clearBox(1, 1, 1, 7, 4, 4);
		$this->clearBox(2, 1, 6, 8, 4, 10);
		$this->fillBox(2, 0, 6, 8, 0, 10, Block::DIRT);
		$this->fillBox(2, 1, 6, 2, 1, 10, Block::FENCE);
		$this->fillBox(8, 1, 6, 8, 1, 10, Block::FENCE);
		$this->fillBox(3, 1, 10, 7, 1, 10, Block::FENCE);
		$this->fillBox(1, 0, 1, 7, 0, 4, Block::PLANK);
		$this->fillBox(0, 0, 0, 0, 3, 5, Block::COBBLESTONE);
		$this->fillBox(8, 0, 0, 8, 3, 5, Block::COBBLESTONE);
		$this->fillBox(1, 0, 0, 7, 1, 0, Block::COBBLESTONE);
		$this->fillBox(1, 0, 5, 7, 1, 5, Block::COBBLESTONE);
		$this->fillBox(1, 2, 0, 7, 3, 0, Block::PLANK);
		$this->fillBox(1, 2, 5, 7, 3, 5, Block::PLANK);
		$this->fillBox(0, 4, 1, 8, 4, 1, Block::PLANK);
		$this->fillBox(0, 4, 4, 8, 4, 4, Block::PLANK);
		$this->fillBox(0, 5, 2, 8, 5, 3, Block::PLANK);
		foreach([[0,4,2],[0,4,3],[8,4,2],[8,4,3]] as $p){ $this->setBlock($p[0], $p[1], $p[2], Block::PLANK); }
		$this->roofRows(9, 0, 5, 4, Block::PLANK);
		foreach([[0,2,1],[0,2,4],[8,2,1],[8,2,4],[2,2,6],[2,2,9]] as $p){ $this->setBlock($p[0], $p[1], $p[2], Block::WOOD); }
		foreach([[0,2,2],[0,2,3],[8,2,2],[8,2,3],[2,2,7],[2,2,8]] as $p){ $this->setBlock($p[0], $p[1], $p[2], Block::GLASS_PANE); }
		$this->setBlock(1, 1, 4, Block::PLANK);
		$this->placeStairs(2, 1, 4, Block::WOOD_STAIRS, self::NORTH);
		$this->placeStairs(1, 1, 3, Block::WOOD_STAIRS, self::WEST);
		$this->fillBox(5, 0, 1, 7, 0, 3, Block::DOUBLE_WOOD_SLAB);
		$this->setBlock(2, 1, 0, Block::AIR); $this->setBlock(2, 2, 0, Block::AIR); $this->placeDoor(2, 1, 0, self::NORTH); $this->placeStairs(2, 0, -1, Block::WOOD_STAIRS, self::NORTH);
		$this->setBlock(6, 1, 5, Block::AIR); $this->setBlock(6, 2, 5, Block::AIR); $this->placeDoor(6, 1, 5, self::SOUTH);
	}
}
}

if (!class_exists('pocketmine\\level\\generator\\normal\\object\\PmVillageDoubleFarmland', false)) {
class PmVillageDoubleFarmland extends PmVillagePiece{
	protected function getSize() : array{ return [13, 4, 9]; }
	public function placeObject(ChunkManager $level, $x, $y, $z, Random $random){
		$this->level = $level; $this->x = (int)$x; $this->y = (int)$y; $this->z = (int)$z;
		$this->foundation(13, 9, Block::DIRT);
		$this->clearBox(0, 1, 0, 12, 4, 8);
		$this->fillBox(1, 0, 1, 2, 0, 7, Block::FARMLAND);
		$this->fillBox(4, 0, 1, 5, 0, 7, Block::FARMLAND);
		$this->fillBox(7, 0, 1, 8, 0, 7, Block::FARMLAND);
		$this->fillBox(10, 0, 1, 11, 0, 7, Block::FARMLAND);
		$this->fillBox(0, 0, 0, 0, 0, 8, Block::WOOD);
		$this->fillBox(6, 0, 0, 6, 0, 8, Block::WOOD);
		$this->fillBox(12, 0, 0, 12, 0, 8, Block::WOOD);
		$this->fillBox(1, 0, 0, 11, 0, 0, Block::WOOD);
		$this->fillBox(1, 0, 8, 11, 0, 8, Block::WOOD);
		$this->fillBox(3, 0, 1, 3, 0, 7, Block::STILL_WATER);
		$this->fillBox(9, 0, 1, 9, 0, 7, Block::STILL_WATER);
		for($z0 = 1; $z0 <= 7; ++$z0){
			foreach([[1,2],[4,5],[7,8],[10,11]] as $pair){
				for($x0 = $pair[0]; $x0 <= $pair[1]; ++$x0){
					$this->setBlock($x0, 1, $z0, Block::WHEAT_BLOCK, $random->nextRange(2, 7));
				}
			}
		}
	}
}
}

if (!class_exists('pocketmine\\level\\generator\\normal\\object\\PmVillageFarmland', false)) {
class PmVillageFarmland extends PmVillagePiece{
	protected function getSize() : array{ return [7, 4, 9]; }
	public function placeObject(ChunkManager $level, $x, $y, $z, Random $random){
		$this->level = $level; $this->x = (int)$x; $this->y = (int)$y; $this->z = (int)$z;
		$this->foundation(7, 9, Block::DIRT);
		$this->clearBox(0, 1, 0, 6, 4, 8);
		$this->fillBox(1, 0, 1, 2, 0, 7, Block::FARMLAND);
		$this->fillBox(4, 0, 1, 5, 0, 7, Block::FARMLAND);
		$this->fillBox(0, 0, 0, 0, 0, 8, Block::WOOD);
		$this->fillBox(6, 0, 0, 6, 0, 8, Block::WOOD);
		$this->fillBox(1, 0, 0, 5, 0, 0, Block::WOOD);
		$this->fillBox(1, 0, 8, 5, 0, 8, Block::WOOD);
		$this->fillBox(3, 0, 1, 3, 0, 7, Block::STILL_WATER);
		for($z0 = 1; $z0 <= 7; ++$z0){
			for($x0 = 1; $x0 <= 2; ++$x0){ $this->setBlock($x0, 1, $z0, Block::WHEAT_BLOCK, $random->nextRange(2, 7)); }
			for($x0 = 4; $x0 <= 5; ++$x0){ $this->setBlock($x0, 1, $z0, Block::CARROT_BLOCK, 7); }
		}
	}
}
}

if (!class_exists('pocketmine\\level\\generator\\normal\\object\\PmVillageSmithy', false)) {
class PmVillageSmithy extends PmVillagePiece{
	protected function getEntranceOffset() : int{ return 7; }
	protected function getSize() : array{ return [10, 6, 7]; }
	public function placeObject(ChunkManager $level, $x, $y, $z, Random $random){
		$this->level = $level; $this->x = (int)$x; $this->y = (int)$y; $this->z = (int)$z;
		$this->foundation(10, 7, Block::COBBLESTONE, 0, Block::COBBLESTONE);
		$this->clearBox(0, 1, 0, 9, 4, 6);
		$this->fillBox(0, 0, 0, 9, 0, 6, Block::COBBLESTONE);
		$this->fillBox(0, 4, 0, 9, 4, 6, Block::COBBLESTONE);
		$this->fillBox(0, 5, 0, 9, 5, 6, Block::STONE_SLAB, 0);
		$this->clearBox(1, 5, 1, 8, 5, 5);
		$this->fillBox(1, 1, 0, 2, 3, 0, Block::PLANK);
		$this->fillBox(0, 1, 0, 0, 4, 0, Block::WOOD);
		$this->fillBox(3, 1, 0, 3, 4, 0, Block::WOOD);
		$this->fillBox(0, 1, 6, 0, 4, 6, Block::WOOD);
		$this->setBlock(3, 3, 1, Block::PLANK);
		$this->fillBox(3, 1, 2, 3, 3, 2, Block::PLANK);
		$this->fillBox(4, 1, 3, 5, 3, 3, Block::PLANK);
		$this->fillBox(0, 1, 1, 0, 3, 5, Block::PLANK);
		$this->fillBox(1, 1, 6, 5, 3, 6, Block::PLANK);
		$this->fillBox(5, 1, 0, 5, 3, 0, Block::FENCE);
		$this->fillBox(9, 1, 0, 9, 3, 0, Block::FENCE);
		$this->fillBox(6, 1, 4, 9, 4, 6, Block::COBBLESTONE);
		$this->setBlock(7, 1, 5, Block::LAVA);
		$this->setBlock(8, 1, 5, Block::LAVA);
		$this->setBlock(9, 2, 5, Block::IRON_BARS);
		$this->setBlock(9, 2, 4, Block::IRON_BARS);
		$this->clearBox(7, 2, 4, 8, 2, 5);
		$this->setBlock(6, 1, 3, Block::COBBLESTONE);
		$this->setBlock(6, 2, 3, Block::FURNACE, 2);
		$this->setBlock(6, 3, 3, Block::FURNACE, 2);
		$this->setBlock(8, 1, 1, Block::STONE_SLAB, 0);
		$this->setBlock(0, 2, 2, Block::GLASS_PANE);
		$this->setBlock(0, 2, 4, Block::GLASS_PANE);
		$this->setBlock(2, 2, 6, Block::GLASS_PANE);
		$this->setBlock(4, 2, 6, Block::GLASS_PANE);
		$this->setBlock(2, 1, 4, Block::FENCE);
		$this->setBlock(2, 2, 4, Block::CARPET, 12);
		$this->setBlock(1, 1, 5, Block::PLANK);
		$this->placeStairs(2, 1, 5, Block::WOOD_STAIRS, self::NORTH);
		$this->placeStairs(1, 1, 4, Block::WOOD_STAIRS, self::WEST);
		$this->placeMarkedSmithyChest(5, 1, 5, self::NORTH);
		$this->placeEntranceNorth(7);
		for($x0 = 6; $x0 <= 8; ++$x0){ $this->placeStairs($x0, 0, -1, Block::COBBLESTONE_STAIRS, self::NORTH); }
	}
}
}

if (!class_exists('pocketmine\\level\\generator\\normal\\object\\PmVillageTwoRoomHouse', false)) {
class PmVillageTwoRoomHouse extends PmVillagePiece{
	protected function getEntranceOffset() : int{ return 2; }
	protected function getSize() : array{ return [9, 7, 12]; }
	public function placeObject(ChunkManager $level, $x, $y, $z, Random $random){
		$this->level = $level; $this->x = (int)$x; $this->y = (int)$y; $this->z = (int)$z;
		$this->foundation(9, 12, Block::PLANK, 0, Block::COBBLESTONE);
		$this->clearBox(1, 1, 1, 7, 4, 4);
		$this->clearBox(2, 1, 6, 8, 4, 10);
		$this->fillBox(2, 0, 5, 8, 0, 10, Block::PLANK);
		$this->fillBox(1, 0, 1, 7, 0, 4, Block::PLANK);
		$this->fillBox(0, 0, 0, 0, 3, 5, Block::COBBLESTONE);
		$this->fillBox(8, 0, 0, 8, 3, 10, Block::COBBLESTONE);
		$this->fillBox(1, 0, 0, 7, 2, 0, Block::COBBLESTONE);
		$this->fillBox(1, 0, 5, 2, 1, 5, Block::COBBLESTONE);
		$this->fillBox(2, 0, 6, 2, 3, 10, Block::COBBLESTONE);
		$this->fillBox(3, 0, 10, 7, 3, 10, Block::COBBLESTONE);
		$this->fillBox(1, 2, 0, 7, 3, 0, Block::PLANK);
		$this->fillBox(1, 2, 5, 2, 3, 5, Block::PLANK);
		$this->fillBox(0, 4, 1, 8, 4, 1, Block::PLANK);
		$this->fillBox(0, 4, 4, 3, 4, 4, Block::PLANK);
		$this->fillBox(0, 5, 2, 8, 5, 3, Block::PLANK);
		foreach([[0,4,2],[0,4,3],[8,4,2],[8,4,3],[8,4,4]] as $p){ $this->setBlock($p[0], $p[1], $p[2], Block::PLANK); }
		$this->roofRows(9, 0, 5, 4, Block::PLANK);
		$this->fillBox(3, 4, 5, 3, 4, 10, Block::PLANK);
		$this->fillBox(7, 4, 2, 7, 4, 10, Block::PLANK);
		$this->fillBox(4, 5, 4, 4, 5, 10, Block::PLANK);
		$this->fillBox(6, 5, 4, 6, 5, 10, Block::PLANK);
		$this->fillBox(5, 6, 3, 5, 6, 10, Block::PLANK);
		foreach([[0,2,1],[0,2,4],[4,2,0],[6,2,0],[8,2,1],[8,2,4],[8,2,6],[8,2,9],[2,2,6],[2,2,9],[4,4,10],[6,4,10]] as $log){
			$this->setBlock($log[0], $log[1], $log[2], Block::WOOD);
		}
		foreach([[0,2,2],[0,2,3],[5,2,0],[8,2,2],[8,2,3],[8,2,7],[8,2,8],[2,2,7],[2,2,8],[5,4,10]] as $glass){
			$this->setBlock($glass[0], $glass[1], $glass[2], Block::GLASS_PANE);
		}
		$this->placeEntranceNorth(2);
		$this->placeDoor(2, 1, 0, self::NORTH);
		$this->placeStairs(2, 0, -1, Block::WOOD_STAIRS, self::NORTH);
	}
}
}

if (!class_exists('pocketmine\\level\\generator\\normal\\object\\PmVillageLightPost', false)) {
class PmVillageLightPost extends PmVillagePiece{
	protected function getSize() : array{ return [3, 4, 2]; }
	public function placeObject(ChunkManager $level, $x, $y, $z, Random $random){
		$this->level = $level; $this->x = (int)$x; $this->y = (int)$y; $this->z = (int)$z;
		for($dx = 0; $dx < 3; ++$dx){
			for($dz = 0; $dz < 2; ++$dz){
				for($dy = -1; $dy >= -6; --$dy){
					list($wx, $wz) = $this->toWorldXZ($dx, $dz);
					$id = $this->level->getBlockIdAt($wx, $this->y + $dy, $wz);
					if($id !== Block::AIR && $id !== Block::WATER && $id !== Block::STILL_WATER && $id !== Block::LEAVES && $id !== Block::LEAVES2 && $id !== Block::TALL_GRASS && $id !== Block::DOUBLE_PLANT){
						break;
					}
					$this->setBlock($dx, $dy, $dz, Block::DIRT, 0);
				}
			}
		}
		$this->clearBox(0, 1, 0, 2, 4, 1);
		$this->column(1, 1, 3, 0, Block::FENCE);
		$this->setBlock(1, 4, 0, Block::WOOL, 15);
		$this->placeAttachedTorch(2, 4, 0, self::WEST);
		$this->placeAttachedTorch(0, 4, 0, self::EAST);
		$this->placeAttachedTorch(1, 4, 1, self::NORTH);
		$this->placeAttachedTorch(1, 4, -1, self::SOUTH);
	}
}
}
