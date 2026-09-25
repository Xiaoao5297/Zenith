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

use pocketmine\item\Item;
use pocketmine\level\generator\object\StructureLoot;
use pocketmine\nbt\NBT;
use pocketmine\utils\Random;

class JungleTempleLoot{
	const CHEST_MARKER = 0x4a54;
	const CHEST_MARKER_ID = 0x54;
	const CHEST_MARKER_DATA = 0x4a;
	const DISPENSER_MARKER = 0x4a44;
	const DISPENSER_MARKER_ID = 0x44;
	const DISPENSER_MARKER_DATA = 0x4a;

	public static function createChestItems(int $x, int $y, int $z, int $seed) : array{
		$random = new Random($seed ^ ($x * 73428767) ^ ($y * 912931) ^ ($z * 42317861) ^ 0x4a54434c);
		return StructureLoot::createItems(self::getLootPools(), $random);
	}

	public static function getLootPoolsForTesting() : array{
		return self::getLootPools();
	}

	private static function getLootPools() : array{
		return [
			StructureLoot::pool(2, 6, [
				StructureLoot::entry(Item::DIAMOND, 0, 1, 3, 15),
				StructureLoot::entry(Item::IRON_INGOT, 0, 1, 5, 50),
				StructureLoot::entry(Item::GOLD_INGOT, 0, 2, 7, 75),
				StructureLoot::entry(Item::EMERALD, 0, 1, 3, 10),
				StructureLoot::entry(Item::BONE, 0, 4, 6, 100),
				StructureLoot::entry(Item::ROTTEN_FLESH, 0, 3, 7, 80),
				StructureLoot::entry(Item::LEATHER, 0, 1, 5, 15),
				StructureLoot::entry(StructureLoot::itemId("IRON_HORSE_ARMOR", 417), 0, 1, 1, 5),
				StructureLoot::entry(StructureLoot::itemId("GOLDEN_HORSE_ARMOR", 418), 0, 1, 1, 5),
				StructureLoot::entry(StructureLoot::itemId("DIAMOND_HORSE_ARMOR", 419), 0, 1, 1, 5),
				StructureLoot::entry(Item::ENCHANTED_BOOK, 0, 1, 1, 6),
			]),
		];
	}

	public static function createDispenserItems(int $x, int $y, int $z, int $seed) : array{
		$random = new Random($seed ^ ($x * 2389451) ^ ($y * 19349663) ^ ($z * 83492791) ^ 0x4a544452);
		$count = 2 + $random->nextBoundedInt(7);
		return [NBT::putItemHelper(Item::get(Item::ARROW, 0, $count), $random->nextBoundedInt(9))];
	}

}
