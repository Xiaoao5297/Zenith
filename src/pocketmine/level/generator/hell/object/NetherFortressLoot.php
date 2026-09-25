<?php

namespace pocketmine\level\generator\hell\object;

use pocketmine\item\Item;
use pocketmine\level\generator\object\StructureLoot;
use pocketmine\utils\Random;

class NetherFortressLoot{
	const CHEST_MARKER = 0x4e46;
	const CHEST_MARKER_ID = 0x46;
	const CHEST_MARKER_DATA = 0x4e;

	public static function createItems(int $x, int $y, int $z, int $seed) : array{
		$random = new Random($seed ^ ($x * 83492791) ^ ($y * 19349663) ^ ($z * 2389451) ^ 0x4e464c54);
		return StructureLoot::createItems(self::getLootPools(), $random);
	}

	public static function getLootPoolsForTesting() : array{
		return self::getLootPools();
	}

	private static function getLootPools() : array{
		return [
			StructureLoot::pool(2, 4, [
				StructureLoot::entry(Item::DIAMOND, 0, 1, 3, 5),
				StructureLoot::entry(Item::IRON_INGOT, 0, 1, 5, 5),
				StructureLoot::entry(Item::GOLD_INGOT, 0, 1, 3, 15),
				StructureLoot::entry(Item::GOLDEN_SWORD, 0, 1, 1, 5),
				StructureLoot::entry(Item::GOLD_CHESTPLATE, 0, 1, 1, 5),
				StructureLoot::entry(Item::FLINT_AND_STEEL, 0, 1, 1, 5),
				StructureLoot::entry(Item::NETHER_WART, 0, 3, 7, 5),
				StructureLoot::entry(Item::SADDLE, 0, 1, 1, 10),
				StructureLoot::entry(StructureLoot::itemId("GOLDEN_HORSE_ARMOR", 418), 0, 1, 1, 8),
				StructureLoot::entry(StructureLoot::itemId("IRON_HORSE_ARMOR", 417), 0, 1, 1, 5),
				StructureLoot::entry(StructureLoot::itemId("DIAMOND_HORSE_ARMOR", 419), 0, 1, 1, 3),
				StructureLoot::entry(Item::OBSIDIAN, 0, 2, 4, 2),
			]),
		];
	}
}
