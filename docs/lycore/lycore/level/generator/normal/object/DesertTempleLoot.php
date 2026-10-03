<?php

namespace lycore\level\generator\normal\object;

use lycore\item\Item;
use lycore\level\generator\object\StructureLoot;
use lycore\utils\Random;

class DesertTempleLoot{
	public static function createItems(int $x, int $y, int $z, int $seed) : array{
		$random = new Random($seed ^ ($x * 73428767) ^ ($y * 912931) ^ ($z * 42317861));
		return StructureLoot::createItems(self::getLootPools(), $random);
	}

	public static function getLootPoolsForTesting() : array{
		return self::getLootPools();
	}

	private static function getLootPools() : array{
		return [
			StructureLoot::pool(2, 4, [
				StructureLoot::entry(Item::DIAMOND, 0, 1, 3, 5),
				StructureLoot::entry(Item::IRON_INGOT, 0, 1, 5, 15),
				StructureLoot::entry(Item::GOLD_INGOT, 0, 2, 7, 15),
				StructureLoot::entry(Item::EMERALD, 0, 1, 3, 15),
				StructureLoot::entry(Item::BONE, 0, 4, 6, 25),
				StructureLoot::entry(Item::SPIDER_EYE, 0, 1, 3, 25),
				StructureLoot::entry(Item::ROTTEN_FLESH, 0, 3, 7, 25),
				StructureLoot::entry(Item::LEATHER, 0, 1, 5, 20),
				StructureLoot::entry(Item::SADDLE, 0, 1, 1, 20),
				StructureLoot::entry(Item::IRON_HORSE_ARMOR, 0, 1, 1, 15),
				StructureLoot::entry(Item::GOLDEN_HORSE_ARMOR, 0, 1, 1, 10),
				StructureLoot::entry(Item::DIAMOND_HORSE_ARMOR, 0, 1, 1, 5),
				StructureLoot::entry(Item::ENCHANTED_BOOK, 0, 1, 1, 20),
				StructureLoot::entry(Item::GOLDEN_APPLE, 0, 1, 1, 20),
				StructureLoot::entry(Item::ENCHANTED_GOLDEN_APPLE, 0, 1, 1, 2),
				StructureLoot::entry(Item::AIR, 0, 1, 1, 15),
			]),
			StructureLoot::pool(4, 4, [
				StructureLoot::entry(Item::BONE, 0, 1, 8, 10),
				StructureLoot::entry(Item::GUNPOWDER, 0, 1, 8, 10),
				StructureLoot::entry(Item::ROTTEN_FLESH, 0, 1, 8, 10),
				StructureLoot::entry(Item::STRING, 0, 1, 8, 10),
				StructureLoot::entry(Item::SAND, 0, 1, 8, 10),
			]),
		];
	}
}
