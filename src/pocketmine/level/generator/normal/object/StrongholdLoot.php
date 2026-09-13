<?php

namespace pocketmine\level\generator\normal\object;

use pocketmine\item\Item;
use pocketmine\item\enchantment\Enchantment;
use pocketmine\nbt\NBT;
use pocketmine\nbt\tag\ByteTag;
use pocketmine\utils\Random;

class StrongholdLoot{
	const CORRIDOR_CHEST_MARKER = 0x5331;
	const CORRIDOR_CHEST_MARKER_ID = 0x31;
	const CORRIDOR_CHEST_MARKER_DATA = 0x53;
	const CROSSING_CHEST_MARKER = 0x5332;
	const CROSSING_CHEST_MARKER_ID = 0x32;
	const CROSSING_CHEST_MARKER_DATA = 0x53;
	const LIBRARY_CHEST_MARKER = 0x5333;
	const LIBRARY_CHEST_MARKER_ID = 0x33;
	const LIBRARY_CHEST_MARKER_DATA = 0x53;

	public static function createCorridorItems(int $x, int $y, int $z, int $seed) : array{
		$random = new Random($seed ^ ($x * 73428767) ^ ($y * 912931) ^ ($z * 42317861) ^ 0x5348434f);
		$pools = self::getCorridorLootPools();
		return self::createItems(self::legacyEntries($pools[0]["entries"]), self::rolls($pools[0], $random), $random);
	}

	public static function createCrossingItems(int $x, int $y, int $z, int $seed) : array{
		$random = new Random($seed ^ ($x * 2389451) ^ ($y * 19349663) ^ ($z * 83492791) ^ 0x53485258);
		$pools = self::getCrossingLootPools();
		return self::createItems(self::legacyEntries($pools[0]["entries"]), self::rolls($pools[0], $random), $random);
	}

	public static function createLibraryItems(int $x, int $y, int $z, int $seed) : array{
		$random = new Random($seed ^ ($x * 19349663) ^ ($y * 83492791) ^ ($z * 2389451) ^ 0x53484c49);
		$pools = self::getLibraryLootPools();
		return self::createItems(self::legacyEntries($pools[0]["entries"]), self::rolls($pools[0], $random), $random);
	}

	public static function getCorridorLootPoolsForTesting() : array{
		return self::getCorridorLootPools();
	}

	public static function getCrossingLootPoolsForTesting() : array{
		return self::getCrossingLootPools();
	}

	public static function getLibraryLootPoolsForTesting() : array{
		return self::getLibraryLootPools();
	}

	private static function getCorridorLootPools() : array{
		return [[
			"minRolls" => 2,
			"maxRolls" => 3,
			"entries" => [
				self::entry(self::itemId("EMERALD", 388), 0, 1, 3, 15),
				self::entry(self::itemId("DIAMOND", 264), 0, 1, 3, 15),
				self::entry(self::itemId("IRON_INGOT", 265), 0, 1, 5, 50),
				self::entry(self::itemId("GOLD_INGOT", 266), 0, 1, 3, 25),
				self::entry(self::itemId("REDSTONE", 331), 0, 4, 9, 25),
				self::entry(self::itemId("BREAD", 297), 0, 1, 3, 75),
				self::entry(self::itemId("APPLE", 260), 0, 1, 3, 75),
				self::entry(self::itemId("IRON_PICKAXE", 257), 0, 1, 1, 25),
				self::entry(self::itemId("IRON_SWORD", 267), 0, 1, 1, 25),
				self::entry(self::itemId("IRON_HELMET", 306), 0, 1, 1, 25),
				self::entry(self::itemId("IRON_CHESTPLATE", 307), 0, 1, 1, 25),
				self::entry(self::itemId("IRON_LEGGINGS", 308), 0, 1, 1, 25),
				self::entry(self::itemId("IRON_BOOTS", 309), 0, 1, 1, 25),
				self::entry(self::itemId("GOLDEN_APPLE", 322), 0, 1, 1, 5),
				self::entry(self::itemId("LEATHER", 334), 0, 1, 5, 5),
				self::entry(self::itemId("IRON_HORSE_ARMOR", 417), 0, 1, 1, 5),
				self::entry(self::itemId("GOLDEN_HORSE_ARMOR", 418), 0, 1, 1, 5),
				self::entry(self::itemId("DIAMOND_HORSE_ARMOR", 419), 0, 1, 1, 5),
				self::entry(self::itemId("ENCHANTED_BOOK", 403), 0, 1, 1, 6),
			],
		]];
	}

	private static function getCrossingLootPools() : array{
		return [[
			"minRolls" => 1,
			"maxRolls" => 4,
			"entries" => [
				self::entry(self::itemId("IRON_INGOT", 265), 0, 1, 5, 50),
				self::entry(self::itemId("GOLD_INGOT", 266), 0, 1, 3, 25),
				self::entry(self::itemId("REDSTONE", 331), 0, 4, 9, 25),
				self::entry(self::itemId("COAL", 263), 0, 3, 8, 50),
				self::entry(self::itemId("BREAD", 297), 0, 1, 3, 75),
				self::entry(self::itemId("APPLE", 260), 0, 1, 3, 75),
				self::entry(self::itemId("IRON_PICKAXE", 257), 0, 1, 1, 5),
				self::entry(self::itemId("ENCHANTED_BOOK", 403), 0, 1, 1, 6),
				self::entry(self::itemId("DYE", 351), 0, 1, 3, 75),
			],
		]];
	}

	private static function getLibraryLootPools() : array{
		return [[
			"minRolls" => 2,
			"maxRolls" => 10,
			"entries" => [
				self::entry(self::itemId("BOOK", 340), 0, 1, 3, 100),
				self::entry(self::itemId("PAPER", 339), 0, 2, 7, 100),
				self::entry(self::itemId("MAP", 395), 0, 1, 1, 5),
				self::entry(self::itemId("COMPASS", 345), 0, 1, 1, 5),
				self::entry(self::itemId("ENCHANTED_BOOK", 403), 0, 1, 1, 60),
			],
		]];
	}

	private static function entry(int $id, int $damage, int $minCount, int $maxCount, int $weight) : array{
		return [
			"id" => $id,
			"damage" => $damage,
			"minCount" => $minCount,
			"maxCount" => $maxCount,
			"weight" => $weight,
		];
	}

	private static function legacyEntries(array $entries) : array{
		$result = [];
		foreach($entries as $entry){
			$result[] = [$entry["id"], $entry["damage"], $entry["minCount"], $entry["maxCount"], $entry["weight"]];
		}
		return $result;
	}

	private static function rolls(array $pool, Random $random) : int{
		return $pool["minRolls"] + ($pool["maxRolls"] > $pool["minRolls"] ? $random->nextBoundedInt($pool["maxRolls"] - $pool["minRolls"] + 1) : 0);
	}

	private static function itemId(string $constant, int $fallback) : int{
		$name = Item::class . "::" . $constant;
		return defined($name) ? constant($name) : $fallback;
	}

	private static function createItems(array $entries, int $rolls, Random $random) : array{
		$items = [];
		$usedSlots = [];
		for($i = 0; $i < $rolls; ++$i){
			self::addRandomItem($items, $usedSlots, $entries, $random);
		}
		return $items;
	}

	private static function addRandomItem(array &$items, array &$usedSlots, array $entries, Random $random){
		$totalWeight = 0;
		foreach($entries as $entry){
			$totalWeight += $entry[4];
		}

		$weight = $random->nextBoundedInt($totalWeight);
		$chosen = $entries[0];
		foreach($entries as $entry){
			$weight -= $entry[4];
			if($weight < 0){
				$chosen = $entry;
				break;
			}
		}

		for($attempt = 0; $attempt < 27; ++$attempt){
			$slot = $random->nextBoundedInt(27);
			if(isset($usedSlots[$slot])){
				continue;
			}

			$usedSlots[$slot] = true;
			$count = $chosen[2] + ($chosen[3] > $chosen[2] ? $random->nextBoundedInt($chosen[3] - $chosen[2] + 1) : 0);
			$item = $chosen[0] === self::itemId("ENCHANTED_BOOK", 403) ? self::createRandomEnchantedBook($random) : Item::get($chosen[0], $chosen[1], $count);
			$tag = NBT::putItemHelper($item, $slot);
			$tag->Slot = new ByteTag("Slot", $slot);
			$items[] = $tag;
			return;
		}
	}

	private static function createRandomEnchantedBook(Random $random) : Item{
		if(method_exists(Item::class, "createEnchantedBook") && class_exists(Enchantment::class)){
			$enchantmentIds = [
				Enchantment::TYPE_ARMOR_PROTECTION,
				Enchantment::TYPE_ARMOR_FIRE_PROTECTION,
				Enchantment::TYPE_ARMOR_FALL_PROTECTION,
				Enchantment::TYPE_ARMOR_EXPLOSION_PROTECTION,
				Enchantment::TYPE_ARMOR_PROJECTILE_PROTECTION,
				Enchantment::TYPE_ARMOR_THORNS,
				Enchantment::TYPE_WATER_BREATHING,
				Enchantment::TYPE_WATER_SPEED,
				Enchantment::TYPE_WATER_AFFINITY,
				Enchantment::TYPE_WEAPON_SHARPNESS,
				Enchantment::TYPE_WEAPON_SMITE,
				Enchantment::TYPE_WEAPON_ARTHROPODS,
				Enchantment::TYPE_WEAPON_KNOCKBACK,
				Enchantment::TYPE_WEAPON_FIRE_ASPECT,
				Enchantment::TYPE_WEAPON_LOOTING,
				Enchantment::TYPE_MINING_EFFICIENCY,
				Enchantment::TYPE_MINING_SILK_TOUCH,
				Enchantment::TYPE_MINING_DURABILITY,
				Enchantment::TYPE_MINING_FORTUNE,
				Enchantment::TYPE_BOW_POWER,
				Enchantment::TYPE_BOW_KNOCKBACK,
				Enchantment::TYPE_BOW_FLAME,
				Enchantment::TYPE_BOW_INFINITY,
				Enchantment::TYPE_FISHING_FORTUNE,
				Enchantment::TYPE_FISHING_LURE
			];

			$enchantmentId = $enchantmentIds[$random->nextBoundedInt(count($enchantmentIds))];
			$level = 1 + $random->nextBoundedInt(Enchantment::getEnchantMaxLevel($enchantmentId));
			return Item::createEnchantedBook($enchantmentId, $level);
		}
		return Item::get(self::itemId("BOOK", 340), 0, 1);
	}
}
