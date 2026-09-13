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

use pocketmine\block\Block;
use pocketmine\level\ChunkManager;
use pocketmine\level\generator\object\PopulatorObject;
use pocketmine\math\Vector3;
use pocketmine\nbt\NBT;
use pocketmine\nbt\tag\ByteTag;
use pocketmine\nbt\tag\CompoundTag;
use pocketmine\nbt\tag\FloatTag;
use pocketmine\nbt\tag\IntTag;
use pocketmine\nbt\tag\ListTag;
use pocketmine\nbt\tag\StringTag;
use pocketmine\tile\Tile;
use pocketmine\utils\Random;

require_once __DIR__ . "/JungleTempleLoot.php";

class JungleTemple extends PopulatorObject{
	const WIDTH_X = 12;
	const WIDTH_Z = 15;

	const STAIR_W = 0;
	const STAIR_E = 1;
	const STAIR_N = 2;
	const STAIR_S = 3;
	const DISPENSER_N = 3;
	const DISPENSER_E = 4;
	const TRIPWIRE_HOOK_N = 0x04;
	const TRIPWIRE_HOOK_E = 0x05;
	const TRIPWIRE_HOOK_S = 0x06;
	const TRIPWIRE_HOOK_W = 0x07;
	const REPEATER_S_1 = 1;
	const LEVER_S = 3;
	const VINE_N = 4;
	const VINE_S = 8;
	const CHEST_N = 2;
	const CHEST_W = 4;
	const PISTON_W = 1;
	const PISTON_UP = 5;

	private $overridable = [
		Block::AIR => true,
		Block::GRASS => true,
		Block::DIRT => true,
		Block::SAPLING => true,
		Block::LOG => true,
		Block::LEAVES => true,
		Block::VINE => true,
		Block::SNOW_LAYER => true,
		Block::LOG2 => true,
		Block::LEAVES2 => true,
	];

	/** @var ChunkManager */
	private $level;
	/** @var Random */
	private $random;
	private $originX;
	private $originY;
	private $originZ;

	public function canPlaceObject(ChunkManager $level, $x, $y, $z, Random $random){
		$baseY = $y - 3;
		for($xx = $x; $xx <= $x + 11; ++$xx){
			for($yy = $baseY + 1; $yy <= $baseY + 14; ++$yy){
				for($zz = $z; $zz <= $z + 14; ++$zz){
					if(!isset($this->overridable[$level->getBlockIdAt($xx, $yy, $zz)])){
						return false;
					}
				}
			}
		}

		return true;
	}

	public function placeObject(ChunkManager $level, $x, $y, $z, Random $random){
		$this->level = $level;
		$this->random = $random;
		$this->originX = (int) $x;
		$this->originY = (int) $y - 3;
		$this->originZ = (int) $z;

		$stones = [
			[Block::COBBLESTONE, 0],
			[Block::COBBLESTONE, 0],
			[Block::COBBLESTONE, 0],
			[Block::COBBLESTONE, 0],
			[Block::MOSS_STONE, 0],
			[Block::MOSS_STONE, 0],
			[Block::MOSS_STONE, 0],
			[Block::MOSS_STONE, 0],
			[Block::MOSS_STONE, 0],
			[Block::MOSS_STONE, 0],
		];

		$this->fillRandom(0, 0, 0, 11, 0, 14, $stones);
		$this->fillRandom(0, 1, 0, 11, 3, 0, $stones);
		$this->fillRandom(11, 1, 1, 11, 3, 13, $stones);
		$this->fillRandom(0, 1, 1, 0, 3, 13, $stones);
		$this->fillRandom(0, 1, 14, 11, 3, 14, $stones);
		$this->fillRandom(0, 4, 0, 11, 4, 14, $stones);
		$this->fill(4, 4, 0, 7, 4, 0, Block::STONE_BRICK_STAIRS, self::STAIR_N);
		$this->fill(1, 1, 1, 10, 3, 13, Block::AIR);
		$this->fill(5, 4, 7, 6, 4, 9, Block::AIR);

		$this->fillRandom(2, 5, 2, 9, 6, 2, $stones);
		$this->fillRandom(9, 5, 3, 9, 6, 11, $stones);
		$this->fillRandom(2, 5, 12, 9, 6, 12, $stones);
		$this->fillRandom(2, 5, 3, 2, 6, 11, $stones);
		$this->fillRandom(1, 7, 1, 10, 7, 13, $stones);
		$this->fill(3, 5, 3, 8, 6, 11, Block::AIR);
		$this->fill(4, 7, 6, 7, 7, 9, Block::AIR);
		$this->fill(5, 5, 2, 6, 6, 2, Block::AIR);
		$this->fill(5, 6, 12, 6, 6, 12, Block::AIR);

		$this->fillRandom(1, 8, 1, 10, 9, 1, $stones);
		$this->fillRandom(10, 8, 2, 10, 9, 12, $stones);
		$this->fillRandom(1, 8, 13, 10, 9, 13, $stones);
		$this->fillRandom(1, 8, 2, 1, 9, 12, $stones);
		$this->fill(2, 8, 2, 9, 9, 12, Block::AIR);
		$this->fill(5, 9, 1, 6, 9, 1, Block::AIR);
		$this->fill(5, 9, 13, 6, 9, 13, Block::AIR);
		foreach([[10, 9, 5], [10, 9, 9], [1, 9, 5], [1, 9, 9]] as $pos){
			$this->place($pos[0], $pos[1], $pos[2], Block::AIR);
		}

		$this->fillRandom(1, 10, 1, 10, 10, 4, $stones);
		$this->fillRandom(8, 10, 5, 10, 10, 9, $stones);
		$this->fillRandom(1, 10, 5, 3, 10, 9, $stones);
		$this->fillRandom(1, 10, 10, 10, 10, 13, $stones);
		$this->fillRandom(3, 11, 3, 8, 11, 5, $stones);
		$this->fillRandom(7, 11, 6, 8, 11, 8, $stones);
		$this->fillRandom(3, 11, 6, 4, 11, 8, $stones);
		$this->fillRandom(3, 11, 9, 8, 11, 11, $stones);
		$this->fillRandom(4, 12, 4, 7, 12, 10, $stones);
		$this->fill(4, 10, 5, 7, 10, 9, Block::AIR);
		$this->fill(5, 11, 6, 6, 11, 8, Block::AIR);

		$this->placeOutsideDecorations($stones);
		$this->placeInsideDetails($stones);
		$this->supportBaseDownward();

		return true;
	}

	private function placeOutsideDecorations(array $stones){
		foreach([2, 4, 7, 9] as $x){
			$this->fillRandom($x, 8, 0, $x, 9, 0, $stones);
			$this->fillRandom($x, 8, 14, $x, 9, 14, $stones);
		}
		$this->fillRandom(5, 10, 0, 6, 10, 0, $stones);
		for($i = 0; $i < 6; ++$i){
			$z = 2 + ($i << 1);
			$this->fillRandom(11, 8, $z, 11, 9, $z, $stones);
			$this->fillRandom(0, 8, $z, 0, 9, $z, $stones);
		}
		foreach([[11, 10, 5], [11, 10, 9], [0, 10, 5], [0, 10, 9]] as $pos){
			$this->placeRandom($pos[0], $pos[1], $pos[2], $stones);
		}
		foreach([[2, 11, 2], [9, 11, 2], [9, 11, 12], [2, 11, 12]] as $pos){
			$this->fillRandom($pos[0], $pos[1], $pos[2], $pos[0], 13, $pos[2], $stones);
		}
		foreach([[4, 13, 4], [7, 13, 4], [7, 13, 10], [4, 13, 10]] as $pos){
			$this->placeRandom($pos[0], $pos[1], $pos[2], $stones);
		}
		$this->fill(5, 13, 6, 6, 13, 6, Block::STONE_BRICK_STAIRS, self::STAIR_N);
		$this->fillRandom(5, 13, 7, 6, 13, 7, $stones);
		$this->fill(5, 13, 8, 6, 13, 8, Block::STONE_BRICK_STAIRS, self::STAIR_S);
	}

	private function placeInsideDetails(array $stones){
		for($i = 0; $i < 6; ++$i){
			$z = 2 + ($i << 1);
			$this->fillRandom(1, 3, $z, 3, 3, $z, $stones);
		}
		for($i = 0; $i < 7; ++$i){
			$z = 1 + ($i << 1);
			$this->fillRandom(1, 1, $z, 1, 2, $z, $stones);
		}

		$this->placeRandom(2, 2, 1, $stones);
		$this->place(3, 1, 1, Block::MOSS_STONE);
		$this->fillRandom(4, 2, 1, 5, 2, 1, $stones);
		$this->placeRandom(6, 1, 1, $stones);
		$this->placeRandom(6, 3, 1, $stones);
		$this->fillRandom(7, 2, 1, 9, 2, 1, $stones);
		$this->place(8, 1, 1, Block::MOSS_STONE);
		$this->fillRandom(10, 1, 1, 10, 3, 7, $stones);
		$this->fillRandom(9, 3, 1, 9, 3, 7, $stones);
		foreach([[9, 1, 2], [9, 1, 4], [8, 1, 5], [6, 1, 5], [5, 2, 5], [5, 3, 5], [4, 1, 5]] as $pos){
			$this->place($pos[0], $pos[1], $pos[2], Block::MOSS_STONE);
		}
		$this->fill(7, 2, 5, 7, 3, 5, Block::MOSS_STONE);
		$this->placeRandom(6, 2, 5, $stones);
		$this->fillRandom(7, 1, 6, 7, 3, 11, $stones);
		$this->fillRandom(4, 1, 6, 4, 3, 11, $stones);
		$this->fillRandom(5, 3, 11, 6, 3, 11, $stones);
		$this->fillRandom(8, 3, 11, 10, 3, 11, $stones);
		$this->fillRandom(8, 1, 11, 10, 1, 11, $stones);
		$this->fillRandom(5, 1, 8, 6, 1, 8, $stones);
		$this->fillRandom(6, 1, 7, 6, 2, 7, $stones);
		$this->placeRandom(5, 2, 7, $stones);
		$this->fillRandom(6, 1, 6, 6, 3, 6, $stones);
		$this->fillRandom(5, 2, 6, 5, 3, 6, $stones);
		$this->fillRandom(8, 2, 6, 9, 2, 6, $stones);
		$this->placeRandom(8, 3, 6, $stones);
		$this->fillRandom(9, 1, 7, 9, 2, 7, $stones);
		$this->fillRandom(8, 1, 7, 8, 3, 7, $stones);
		$this->fillRandom(10, 1, 8, 10, 1, 10, $stones);
		$this->place(10, 2, 9, Block::MOSS_STONE);
		$this->fillRandom(8, 1, 8, 8, 1, 10, $stones);
		$this->fill(8, 2, 11, 10, 2, 11, Block::STONE_BRICKS, 3);
		$this->fill(8, 2, 12, 10, 2, 12, Block::LEVER, self::LEVER_S);

		$this->placeDispenser(3, 2, 1, self::DISPENSER_N);
		$this->placeDispenser(9, 2, 3, self::DISPENSER_E);
		$this->place(3, 2, 2, Block::VINE, self::VINE_N);
		$this->fill(8, 2, 3, 8, 3, 3, Block::VINE, self::VINE_S);
		$this->fill(2, 1, 8, 3, 1, 8, Block::TRIPWIRE);
		$this->place(4, 1, 8, Block::TRIPWIRE_HOOK, self::TRIPWIRE_HOOK_E);
		$this->place(1, 1, 8, Block::TRIPWIRE_HOOK, self::TRIPWIRE_HOOK_W);
		$this->fill(5, 1, 1, 5, 1, 7, Block::REDSTONE_WIRE);
		$this->place(4, 1, 1, Block::REDSTONE_WIRE);
		$this->fill(7, 1, 2, 7, 1, 4, Block::TRIPWIRE);
		$this->place(7, 1, 1, Block::TRIPWIRE_HOOK, self::TRIPWIRE_HOOK_N);
		$this->place(7, 1, 5, Block::TRIPWIRE_HOOK, self::TRIPWIRE_HOOK_S);
		$this->fill(8, 1, 6, 9, 1, 6, Block::REDSTONE_WIRE);
		$this->place(9, 1, 5, Block::REDSTONE_WIRE);
		$this->place(9, 2, 4, Block::REDSTONE_WIRE);
		$this->fillStickyPiston(10, 2, 8, 10, 3, 8, self::PISTON_UP);
		$this->placeStickyPiston(9, 2, 8, self::PISTON_W);
		$this->place(10, 3, 9, Block::REDSTONE_WIRE);
		$this->fill(8, 2, 9, 8, 2, 10, Block::REDSTONE_WIRE);
		$this->place(10, 2, 10, Block::UNPOWERED_REPEATER, self::REPEATER_S_1);
		$this->placeChest(8, 1, 3, self::CHEST_W);
		$this->placeChest(9, 1, 10, self::CHEST_N);

		for($i = 0; $i < 4; ++$i){
			$this->fill(5, 4 - $i, 6 + $i, 6, 4 - $i, 6 + $i, Block::STONE_BRICK_STAIRS, self::STAIR_S);
		}
		$this->fillRandom(4, 5, 10, 7, 6, 10, $stones);
		$this->placeRandom(4, 5, 9, $stones);
		$this->placeRandom(7, 5, 9, $stones);
		for($i = 0; $i < 3; ++$i){
			$this->place(7, 5 + $i, 8 + $i, Block::STONE_BRICK_STAIRS, self::STAIR_N);
			$this->place(4, 5 + $i, 8 + $i, Block::STONE_BRICK_STAIRS, self::STAIR_N);
		}
		$this->fillRandom(5, 8, 5, 6, 8, 5, $stones);
		$this->place(7, 8, 5, Block::STONE_BRICK_STAIRS, self::STAIR_E);
		$this->place(4, 8, 5, Block::STONE_BRICK_STAIRS, self::STAIR_W);
	}

	private function supportBaseDownward(){
		for($xx = 0; $xx <= 11; ++$xx){
			for($zz = 0; $zz <= 14; ++$zz){
				for($yy = -1; $yy >= -8; --$yy){
					$worldY = $this->originY + $yy;
					if($worldY <= 1 || $this->level->getBlockIdAt($this->originX + $xx, $worldY, $this->originZ + $zz) !== Block::AIR){
						break;
					}
					$this->place($xx, $yy, $zz, Block::COBBLESTONE);
				}
			}
		}
	}

	private function fillRandom($x1, $y1, $z1, $x2, $y2, $z2, array $palette){
		for($x = min($x1, $x2); $x <= max($x1, $x2); ++$x){
			for($y = min($y1, $y2); $y <= max($y1, $y2); ++$y){
				for($z = min($z1, $z2); $z <= max($z1, $z2); ++$z){
					$this->placeRandom($x, $y, $z, $palette);
				}
			}
		}
	}

	private function placeRandom($x, $y, $z, array $palette){
		$entry = $palette[$this->random->nextBoundedInt(count($palette))];
		$this->place($x, $y, $z, $entry[0], $entry[1]);
	}

	private function fill($x1, $y1, $z1, $x2, $y2, $z2, $id, $meta = 0){
		for($x = min($x1, $x2); $x <= max($x1, $x2); ++$x){
			for($y = min($y1, $y2); $y <= max($y1, $y2); ++$y){
				for($z = min($z1, $z2); $z <= max($z1, $z2); ++$z){
					$this->place($x, $y, $z, $id, $meta);
				}
			}
		}
	}

	private function fillStickyPiston($x1, $y1, $z1, $x2, $y2, $z2, $meta){
		for($x = min($x1, $x2); $x <= max($x1, $x2); ++$x){
			for($y = min($y1, $y2); $y <= max($y1, $y2); ++$y){
				for($z = min($z1, $z2); $z <= max($z1, $z2); ++$z){
					$this->placeStickyPiston($x, $y, $z, $meta);
				}
			}
		}
	}

	private function placeStickyPiston($x, $y, $z, $meta){
		if(!defined(Block::class . "::STICKY_PISTON")){
			return;
		}

		$this->place($x, $y, $z, Block::STICKY_PISTON, $meta);
		$this->createPistonArmTile($x, $y, $z, $meta);
	}

	private function createPistonArmTile($x, $y, $z, $meta){
		if(!defined(Tile::class . "::PISTON_ARM") or !class_exists("pocketmine\\tile\\PistonArm")){
			return;
		}

		$wx = $this->originX + $x;
		$wy = $this->originY + $y;
		$wz = $this->originZ + $z;
		$chunk = $this->level->getChunk($wx >> 4, $wz >> 4);
		if($chunk === null || $chunk->getProvider() === null){
			return;
		}

		Tile::createTile(Tile::PISTON_ARM, $chunk, $this->createPistonArmNbt($wx, $wy, $wz, $meta));
	}

	private function createPistonArmNbt($x, $y, $z, $meta) : CompoundTag{
		return new CompoundTag("", [
			new StringTag("id", Tile::PISTON_ARM),
			new IntTag("x", (int) $x),
			new IntTag("y", (int) $y),
			new IntTag("z", (int) $z),
			new ByteTag("isMovable", 1),
			new ByteTag("State", 0),
			new ByteTag("NewState", 0),
			new FloatTag("Progress", 0.0),
			new FloatTag("LastProgress", 0.0),
			new ByteTag("powered", 0),
			new ByteTag("facing", $this->pistonFacingFromMeta($meta)),
			new ByteTag("Sticky", 1),
			new ByteTag("Extending", 0),
			$this->emptyIntList("AttachedBlocks"),
			new ListTag("BreakBlocks", []),
		]);
	}

	private function emptyIntList(string $name) : ListTag{
		$list = new ListTag($name, []);
		$list->setTagType(NBT::TAG_Int);
		return $list;
	}

	private function pistonFacingFromMeta($meta) : int{
		$facing = ((int) $meta) & 0x07;
		if($facing < Vector3::SIDE_DOWN || $facing > Vector3::SIDE_EAST){
			return Vector3::SIDE_NORTH;
		}
		if($facing === Vector3::SIDE_NORTH || $facing === Vector3::SIDE_SOUTH || $facing === Vector3::SIDE_WEST || $facing === Vector3::SIDE_EAST){
			return Vector3::getOppositeSide($facing);
		}
		return $facing;
	}

	private function place($x, $y, $z, $id, $meta = 0){
		$this->level->setBlockIdAt($this->originX + $x, $this->originY + $y, $this->originZ + $z, $id);
		$this->level->setBlockDataAt($this->originX + $x, $this->originY + $y, $this->originZ + $z, $meta);
	}

	private function placeChest($x, $y, $z, $meta){
		$this->place($x, $y, $z, Block::CHEST, $meta);
		$this->markExtra($x, $y, $z, JungleTempleLoot::CHEST_MARKER_ID, JungleTempleLoot::CHEST_MARKER_DATA, JungleTempleLoot::CHEST_MARKER);
	}

	private function placeDispenser($x, $y, $z, $meta){
		$this->place($x, $y, $z, Block::DISPENSER, $meta);
		$this->markExtra($x, $y, $z, JungleTempleLoot::DISPENSER_MARKER_ID, JungleTempleLoot::DISPENSER_MARKER_DATA, JungleTempleLoot::DISPENSER_MARKER);
	}

	private function markExtra($x, $y, $z, $id, $data, $marker){
		$wx = $this->originX + $x;
		$wy = $this->originY + $y;
		$wz = $this->originZ + $z;
		if(method_exists($this->level, "setBlockExtraDataAt")){
			$this->level->setBlockExtraDataAt($wx, $wy, $wz, $id, $data);
			return;
		}

		$chunk = $this->level->getChunk($wx >> 4, $wz >> 4);
		if($chunk !== null && method_exists($chunk, "setBlockExtraData")){
			$chunk->setBlockExtraData($wx & 0x0f, $wy & 0x7f, $wz & 0x0f, $marker);
		}
	}
}
