<?php

namespace lycore\level\generator\normal\object;

use lycore\level\generator\object\StructureLoot;
use lycore\utils\Random;

class MineshaftLoot{
	const CHEST_MINECART_MARKER = 0x4d43;
	const CHEST_MINECART_MARKER_ID = 0x43;
	const CHEST_MINECART_MARKER_DATA = 0x4d;
	const INVENTORY_SIZE = 27;

	public static function createItems(int $x, int $y, int $z, int $seed) : array{
		$random = new Random($seed ^ ($x * 73428767) ^ ($y * 912931) ^ ($z * 42317861) ^ 0x4d534346);
		return StructureLoot::createItems(self::getLootPools(), $random, self::INVENTORY_SIZE);
	}

	public static function getLootPoolsForTesting() : array{
		return self::getLootPools();
	}

	private static function getLootPools() : array{
		return [
			StructureLoot::pool(1, 1, [
				self::entry(self::itemId("GOLDEN_APPLE", 322), 0, 1, 1, 20),
				self::entry(self::itemId("ENCHANTED_GOLDEN_APPLE", 466), 0, 1, 1, 1),
				self::entry(self::itemId("NAME_TAG", 421), 0, 1, 1, 30),
				self::entry(self::itemId("ENCHANTED_BOOK", 403), 0, 1, 1, 10),
				self::entry(self::itemId("IRON_PICKAXE", 257), 0, 1, 1, 5),
				self::entry(self::itemId("AIR", 0), 0, 1, 1, 5),
			]),
			StructureLoot::pool(2, 4, [
				self::entry(self::itemId("IRON_INGOT", 265), 0, 1, 5, 10),
				self::entry(self::itemId("GOLD_INGOT", 266), 0, 1, 3, 5),
				self::entry(self::itemId("REDSTONE", 331), 0, 4, 9, 5),
				self::entry(self::itemId("DYE", 351), 4, 4, 9, 5),
				self::entry(self::itemId("DIAMOND", 264), 0, 1, 2, 3),
				self::entry(self::itemId("COAL", 263), 0, 3, 8, 10),
				self::entry(self::itemId("BREAD", 297), 0, 1, 3, 15),
				self::entry(self::itemId("MELON_SEEDS", 362), 0, 2, 4, 10),
				self::entry(self::itemId("PUMPKIN_SEEDS", 361), 0, 2, 4, 10),
				self::entry(self::itemId("BEETROOT_SEEDS", 458), 0, 2, 4, 10),
			]),
			StructureLoot::pool(3, 3, [
				self::entry(self::itemId("RAIL", 66), 0, 4, 8, 20),
				self::entry(self::itemId("POWERED_RAIL", 27), 0, 1, 4, 5),
				self::entry(self::itemId("DETECTOR_RAIL", 28), 0, 1, 4, 5),
				self::entry(self::itemId("ACTIVATOR_RAIL", 126), 0, 1, 4, 5),
				self::entry(self::itemId("TORCH", 50), 0, 1, 16, 15),
			]),
		];
	}

	private static function entry(int $id, int $damage, int $minCount, int $maxCount, int $weight) : array{
		return StructureLoot::entry($id, $damage, $minCount, $maxCount, $weight);
	}

	private static function itemId(string $constant, int $fallback) : int{
		return StructureLoot::itemId($constant, $fallback);
	}
}
