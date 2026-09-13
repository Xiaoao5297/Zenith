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
use pocketmine\utils\Random;

class RuinedPortalLoot{
	const CHEST_MARKER = 0x5250;
	const CHEST_MARKER_ID = 0x50;
	const CHEST_MARKER_DATA = 0x52;

	public static function createItems(int $x, int $y, int $z, int $seed) : array{
		$random = new Random($seed ^ ($x * 83492791) ^ ($y * 19349663) ^ ($z * 2389451) ^ 0x52504c54);
		return StructureLoot::createItems(self::getLootPools(), $random);
	}

	public static function getLootPoolsForTesting() : array{
		return self::getLootPools();
	}

	private static function getLootPools() : array{
		return [
			StructureLoot::pool(4, 8, [
				StructureLoot::entry(Item::OBSIDIAN, 0, 1, 2, 40),
				StructureLoot::entry(Item::FLINT, 0, 1, 4, 40),
				StructureLoot::entry(Item::FLINT_AND_STEEL, 0, 1, 1, 40),
				StructureLoot::entry(Item::GOLDEN_APPLE, 0, 1, 1, 15),
				StructureLoot::entry(Item::GOLD_NUGGET, 0, 4, 24, 15),
				StructureLoot::entry(Item::GOLDEN_SWORD, 0, 1, 1, 15),
				StructureLoot::entry(Item::GOLDEN_AXE, 0, 1, 1, 15),
				StructureLoot::entry(Item::GOLDEN_HOE, 0, 1, 1, 15),
				StructureLoot::entry(Item::GOLDEN_SHOVEL, 0, 1, 1, 15),
				StructureLoot::entry(Item::GOLDEN_PICKAXE, 0, 1, 1, 15),
				StructureLoot::entry(Item::GOLD_BOOTS, 0, 1, 1, 15),
				StructureLoot::entry(Item::GOLD_CHESTPLATE, 0, 1, 1, 15),
				StructureLoot::entry(Item::GOLD_HELMET, 0, 1, 1, 15),
				StructureLoot::entry(Item::GOLD_LEGGINGS, 0, 1, 1, 15),
				StructureLoot::entry(Item::GLISTERING_MELON, 0, 4, 12, 5),
				StructureLoot::entry(StructureLoot::itemId("GOLDEN_HORSE_ARMOR", 418), 0, 1, 1, 5),
				StructureLoot::entry(Item::LIGHT_WEIGHTED_PRESSURE_PLATE, 0, 1, 1, 5),
				StructureLoot::entry(Item::GOLDEN_CARROT, 0, 4, 12, 5),
				StructureLoot::entry(Item::CLOCK, 0, 1, 1, 5),
				StructureLoot::entry(Item::GOLD_INGOT, 0, 2, 8, 5),
				StructureLoot::entry(Item::ENCHANTED_GOLDEN_APPLE, 0, 1, 1, 1),
				StructureLoot::entry(Item::GOLD_BLOCK, 0, 1, 2, 1),
			]),
		];
	}
}
