<?php

namespace pocketmine\level\generator\object;

use pocketmine\block\Block;
use pocketmine\item\enchantment\Enchantment;
use pocketmine\item\Item;
use pocketmine\nbt\NBT;
use pocketmine\nbt\tag\ByteTag;
use pocketmine\utils\Random;

class StructureLoot{
	public static function pool(int $minRolls, int $maxRolls, array $entries) : array{
		return [
			"minRolls" => $minRolls,
			"maxRolls" => $maxRolls,
			"entries" => $entries,
		];
	}

	public static function entry(int $id, int $damage, int $minCount, int $maxCount, int $weight) : array{
		return [
			"id" => $id,
			"damage" => $damage,
			"minCount" => $minCount,
			"maxCount" => $maxCount,
			"weight" => $weight,
		];
	}

	public static function itemId(string $constant, int $fallback) : int{
		$name = Item::class . "::" . $constant;
		if(!defined($name)){
			return $fallback;
		}

		$id = (int) constant($name);
		return self::isRegisteredItemId($id) ? $id : $fallback;
	}

	public static function isRegisteredItemId(int $id) : bool{
		if($id === Item::AIR){
			return true;
		}

		if(Block::$list === null && method_exists(Block::class, "init")){
			Block::init();
		}
		if(Item::$list === null && method_exists(Item::class, "init")){
			Item::init();
		}

		return (isset(Item::$list[$id]) && Item::$list[$id] !== null) || (isset(Block::$list[$id]) && Block::$list[$id] !== null);
	}

	public static function createItems(array $pools, Random $random, int $inventorySize = 27) : array{
		$items = [];
		$usedSlots = [];
		foreach($pools as $pool){
			$rolls = $pool["minRolls"] + ($pool["maxRolls"] > $pool["minRolls"] ? $random->nextBoundedInt($pool["maxRolls"] - $pool["minRolls"] + 1) : 0);
			for($i = 0; $i < $rolls; ++$i){
				self::addRandomItem($items, $usedSlots, $pool["entries"], $random, $inventorySize);
			}
		}
		return $items;
	}

	private static function addRandomItem(array &$items, array &$usedSlots, array $entries, Random $random, int $inventorySize){
		$chosen = self::chooseEntry($entries, $random);
		if($chosen["id"] === Item::AIR){
			return;
		}

		for($attempt = 0; $attempt < $inventorySize; ++$attempt){
			$slot = $random->nextBoundedInt($inventorySize);
			if(isset($usedSlots[$slot])){
				continue;
			}

			$usedSlots[$slot] = true;
			$count = $chosen["minCount"] + ($chosen["maxCount"] > $chosen["minCount"] ? $random->nextBoundedInt($chosen["maxCount"] - $chosen["minCount"] + 1) : 0);
			$item = $chosen["id"] === Item::ENCHANTED_BOOK ? self::createRandomEnchantedBook($random) : Item::get($chosen["id"], $chosen["damage"], $count);
			$tag = NBT::putItemHelper($item, $slot);
			$tag->Slot = new ByteTag("Slot", $slot);
			$items[] = $tag;
			return;
		}
	}

	private static function chooseEntry(array $entries, Random $random) : array{
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
		return $chosen;
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
				Enchantment::TYPE_FISHING_LURE,
			];

			$enchantmentId = $enchantmentIds[$random->nextBoundedInt(count($enchantmentIds))];
			$level = 1 + $random->nextBoundedInt(Enchantment::getEnchantMaxLevel($enchantmentId));
			return Item::createEnchantedBook($enchantmentId, $level);
		}
		return Item::get(Item::BOOK, 0, 1);
	}
}
