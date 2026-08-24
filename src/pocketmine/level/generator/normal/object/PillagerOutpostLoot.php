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
use pocketmine\nbt\NBT;
use pocketmine\nbt\tag\ByteTag;
use pocketmine\utils\Random;

class PillagerOutpostLoot{
	const CHEST_MARKER = 0x504f;
	const CHEST_MARKER_ID = 0x4f;
	const CHEST_MARKER_DATA = 0x50;

	public static function createItems(int $x, int $y, int $z, int $seed) : array{
		$random = new Random($seed ^ ($x * 83492791) ^ ($y * 19349663) ^ ($z * 2389451) ^ 0x504f4c54);
		$items = [];
		$usedSlots = [];

		foreach(self::getLootPools() as $pool){
			$rolls = $pool["minRolls"] + ($pool["maxRolls"] > $pool["minRolls"] ? $random->nextBoundedInt($pool["maxRolls"] - $pool["minRolls"] + 1) : 0);
			for($i = 0; $i < $rolls; ++$i){
				self::addRandomItem($items, $usedSlots, $pool["entries"], $random);
			}
		}

		return $items;
	}

	public static function getLootPoolsForTesting() : array{
		return self::getLootPools();
	}

	private static function getLootPools() : array{
		return [
			[
				"minRolls" => 0,
				"maxRolls" => 1,
				"entries" => [
					self::entry(self::crossbowItemId(), 0, 1, 1, 1),
				],
			],
			[
				"minRolls" => 2,
				"maxRolls" => 3,
				"entries" => [
					self::entry(Item::WHEAT, 0, 3, 5, 15),
					self::entry(Item::POTATO, 0, 2, 5, 10),
					self::entry(Item::CARROT, 0, 3, 5, 10),
				],
			],
			[
				"minRolls" => 1,
				"maxRolls" => 3,
				"entries" => [
					self::entry(Item::LOG2, 1, 2, 3, 5),
					self::entry(Item::STRING, 0, 1, 6, 4),
					self::entry(Item::ARROW, 0, 2, 7, 12),
				],
			],
			[
				"minRolls" => 2,
				"maxRolls" => 3,
				"entries" => [
					self::entry(Item::TRIPWIRE_HOOK, 0, 1, 3, 6),
					self::entry(Item::IRON_INGOT, 0, 1, 3, 6),
					self::entry(self::itemConstant("BOTTLE_O_ENCHANTING", 384), 0, 1, 1, 3),
					self::entry(Item::ENCHANTED_BOOK, 0, 1, 1, 1),
				],
			],
		];
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

	private static function crossbowItemId() : int{
		$constant = Item::class . "::CROSSBOW";
		if(!defined($constant)){
			return Item::BOW;
		}

		$id = (int) constant($constant);
		if(!property_exists(Item::class, "list")){
			return Item::BOW;
		}
		if(Item::$list === null && method_exists(Item::class, "init")){
			Item::init();
		}
		return isset(Item::$list[$id]) && Item::$list[$id] !== null ? $id : Item::BOW;
	}

	private static function itemConstant(string $name, int $fallback) : int{
		$constant = Item::class . "::" . $name;
		return defined($constant) ? (int) constant($constant) : $fallback;
	}

	private static function addRandomItem(array &$items, array &$usedSlots, array $entries, Random $random){
		$totalWeight = 0;
		foreach($entries as $entry){
			$totalWeight += $entry["weight"];
		}

		$weight = $random->nextBoundedInt($totalWeight);
		$chosen = $entries[0];
		foreach($entries as $entry){
			$weight -= $entry["weight"];
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
			$count = $chosen["minCount"] + ($chosen["maxCount"] > $chosen["minCount"] ? $random->nextBoundedInt($chosen["maxCount"] - $chosen["minCount"] + 1) : 0);
			$tag = NBT::putItemHelper(Item::get($chosen["id"], $chosen["damage"], $count), $slot);
			$tag->Slot = new ByteTag("Slot", $slot);
			$items[] = $tag;
			return;
		}
	}
}
