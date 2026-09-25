<?php

namespace pocketmine\level\generator\normal\object;

require_once dirname(__DIR__, 2) . "/object/StructureLoot.php";

use pocketmine\item\Item;
use pocketmine\level\Level;
use pocketmine\level\generator\object\StructureLoot;
use pocketmine\utils\Random;

final class WoodlandMansionLoot{
	const CHEST_MARKER = 0x574d;
	const CHEST_MARKER_ID = 0x4d;
	const CHEST_MARKER_DATA = 0x57;

	public static function createItems(int $worldX, int $y, int $worldZ, int $seed) : array{
		$random = new Random($seed ^ Level::chunkHash($worldX, $worldZ) ^ ($y << 8));
		return StructureLoot::createItems(self::getLootPools(), $random);
	}

	private static function getLootPools() : array{
		return [
			StructureLoot::pool(3, 3, self::registeredEntries([
				self::entry(StructureLoot::itemId("LEAD", 420), 0, 1, 1, 20),
				self::entry(Item::GOLDEN_APPLE, 0, 1, 1, 15),
				self::entry(Item::ENCHANTED_GOLDEN_APPLE, 0, 1, 1, 2),
				self::entry(Item::CHAIN_CHESTPLATE, 0, 1, 1, 10),
				self::entry(Item::DIAMOND_HOE, 0, 1, 1, 15),
				self::entry(Item::DIAMOND_CHESTPLATE, 0, 1, 1, 5),
				self::entry(Item::ENCHANTED_BOOK, 0, 1, 1, 10),
			])),
			StructureLoot::pool(4, 4, self::registeredEntries([
				self::entry(Item::IRON_INGOT, 0, 1, 4, 10),
				self::entry(Item::GOLD_INGOT, 0, 1, 4, 5),
				self::entry(Item::BREAD, 0, 1, 1, 20),
				self::entry(Item::WHEAT, 0, 1, 4, 20),
				self::entry(Item::BUCKET, 0, 1, 1, 10),
				self::entry(Item::REDSTONE, 0, 1, 4, 15),
				self::entry(Item::COAL, 0, 1, 4, 15),
				self::entry(Item::MELON_SEEDS, 0, 2, 4, 10),
				self::entry(Item::PUMPKIN_SEEDS, 0, 2, 4, 10),
				self::entry(Item::BEETROOT_SEEDS, 0, 2, 4, 10),
			])),
			StructureLoot::pool(3, 3, self::registeredEntries([
				self::entry(Item::BONE, 0, 1, 8, 10),
				self::entry(Item::GUNPOWDER, 0, 1, 8, 10),
				self::entry(Item::ROTTEN_FLESH, 0, 1, 8, 10),
				self::entry(Item::STRING, 0, 1, 8, 10),
			])),
		];
	}

	private static function registeredEntries(array $entries) : array{
		$result = [];
		foreach($entries as $entry){
			if(StructureLoot::isRegisteredItemId((int) $entry["id"])){
				$result[] = $entry;
			}
		}
		return $result;
	}

	private static function entry(int $id, int $damage, int $minCount, int $maxCount, int $weight) : array{
		return StructureLoot::entry($id, $damage, $minCount, $maxCount, $weight);
	}
}
