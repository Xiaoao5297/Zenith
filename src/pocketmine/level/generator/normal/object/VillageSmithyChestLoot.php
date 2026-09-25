<?php

namespace pocketmine\level\generator\normal\object;

use pocketmine\item\Item;
use pocketmine\level\generator\object\StructureLoot;
use pocketmine\utils\Random;

class VillageSmithyChestLoot{
	const CHEST_MARKER = 0x5653;
	const CHEST_MARKER_ID = 0x53;
	const CHEST_MARKER_DATA = 0x56;
	const MARKER_ARMORER = 0x5641;
	const MARKER_BUTCHER = 0x5642;
	const MARKER_CARTOGRAPHER = 0x5643;
	const MARKER_DESERT_HOUSE = 0x5644;
	const MARKER_FLETCHER = 0x5646;
	const MARKER_MASON = 0x564d;
	const MARKER_PLAINS_HOUSE = 0x5650;
	const MARKER_SAVANNA_HOUSE = 0x564e;
	const MARKER_SHEPHERD = 0x5648;
	const MARKER_SNOWY_HOUSE = 0x5657;
	const MARKER_TAIGA_HOUSE = 0x5654;
	const MARKER_TANNERY = 0x5659;
	const MARKER_TEMPLE = 0x564c;
	const MARKER_TOOLSMITH = 0x5655;

	private const CATEGORY_WEAPONSMITH = "weaponsmith";

	private static $markerToCategory = [
		self::MARKER_ARMORER => "armorer",
		self::MARKER_BUTCHER => "butcher",
		self::MARKER_CARTOGRAPHER => "cartographer",
		self::MARKER_DESERT_HOUSE => "desert_house",
		self::MARKER_FLETCHER => "fletcher",
		self::MARKER_MASON => "mason",
		self::MARKER_PLAINS_HOUSE => "plains_house",
		self::MARKER_SAVANNA_HOUSE => "savanna_house",
		self::MARKER_SHEPHERD => "shepherd",
		self::MARKER_SNOWY_HOUSE => "snowy_house",
		self::MARKER_TAIGA_HOUSE => "taiga_house",
		self::MARKER_TANNERY => "tannery",
		self::MARKER_TEMPLE => "temple",
		self::MARKER_TOOLSMITH => "toolsmith",
		self::CHEST_MARKER => self::CATEGORY_WEAPONSMITH,
	];

	public static function createItems(int $x, int $y, int $z, int $seed) : array{
		return self::createItemsForCategory(self::CATEGORY_WEAPONSMITH, $x, $y, $z, $seed);
	}

	public static function createItemsForMarker(int $marker, int $x, int $y, int $z, int $seed) : array{
		return self::createItemsForCategory(self::$markerToCategory[$marker] ?? self::CATEGORY_WEAPONSMITH, $x, $y, $z, $seed);
	}

	public static function getLootPoolsForTesting() : array{
		return self::getCategoryLootPools(self::CATEGORY_WEAPONSMITH);
	}

	public static function getCategoryLootPoolsForTesting(string $category) : array{
		return self::getCategoryLootPools($category);
	}

	public static function isChestMarker(int $marker) : bool{
		return isset(self::$markerToCategory[$marker]);
	}

	public static function markerForCategory(string $category) : int{
		foreach(self::$markerToCategory as $marker => $mappedCategory){
			if($mappedCategory === $category){
				return $marker;
			}
		}
		return self::CHEST_MARKER;
	}

	public static function markerIdForCategory(string $category) : int{
		return self::markerForCategory($category) & 0xff;
	}

	public static function markerDataForCategory(string $category) : int{
		return (self::markerForCategory($category) >> 8) & 0xff;
	}

	public static function markerForStructureName(string $structureName) : int{
		$category = self::categoryForStructureName($structureName);
		return $category === null ? 0 : self::markerForCategory($category);
	}

	public static function markerIdForStructureName(string $structureName) : int{
		return self::markerForStructureName($structureName) & 0xff;
	}

	public static function markerDataForStructureName(string $structureName) : int{
		return (self::markerForStructureName($structureName) >> 8) & 0xff;
	}

	public static function hasLootForStructureName(string $structureName) : bool{
		return self::categoryForStructureName($structureName) !== null;
	}

	private static function createItemsForCategory(string $category, int $x, int $y, int $z, int $seed) : array{
		$random = new Random($seed ^ ($x * 73428767) ^ ($y * 912931) ^ ($z * 42317861) ^ self::markerForCategory($category));
		return StructureLoot::createItems(self::getCategoryLootPools($category), $random);
	}

	private static function categoryForStructureName(string $structureName){
		$name = strtolower($structureName);
		foreach([
			"weapon_smith" => self::CATEGORY_WEAPONSMITH,
			"weaponsmith" => self::CATEGORY_WEAPONSMITH,
			"tool_smith" => "toolsmith",
			"toolsmith" => "toolsmith",
			"armorer" => "armorer",
			"butcher" => "butcher",
			"cartographer" => "cartographer",
			"fletcher" => "fletcher",
			"mason" => "mason",
			"shepherd" => "shepherd",
			"tannery" => "tannery",
			"temple" => "temple",
		] as $needle => $category){
			if(strpos($name, $needle) !== false){
				return $category;
			}
		}
		if(self::isGenericVillageHouse($name, "desert")){
			return "desert_house";
		}
		if(self::isGenericVillageHouse($name, "savanna")){
			return "savanna_house";
		}
		if(self::isGenericVillageHouse($name, "snowy")){
			return "snowy_house";
		}
		if(self::isGenericVillageHouse($name, "taiga")){
			return "taiga_house";
		}
		if(self::isGenericVillageHouse($name, "plains")){
			return "plains_house";
		}
		return null;
	}

	private static function isGenericVillageHouse(string $structureName, string $biome) : bool{
		return strpos($structureName, "/houses/" . $biome . "_small_house_") !== false ||
			strpos($structureName, "/houses/" . $biome . "_medium_house_") !== false ||
			strpos($structureName, "/houses/" . $biome . "_big_house_") !== false;
	}

	private static function getCategoryLootPools(string $category) : array{
		switch($category){
			case "armorer":
				return [self::pool(1, 5, [
					self::entry(Item::IRON_INGOT, 0, 1, 3, 2),
					self::entry(Item::BREAD, 0, 1, 4, 4),
					self::entry(Item::IRON_HELMET, 0, 1, 1, 1),
					self::entry(Item::EMERALD, 0, 1, 1, 1),
				])];
			case "butcher":
				return [self::pool(1, 5, [
					self::entry(Item::EMERALD, 0, 1, 1, 1),
					self::entry(Item::RAW_PORKCHOP, 0, 1, 3, 6),
					self::entry(Item::WHEAT, 0, 1, 3, 6),
					self::entry(Item::RAW_BEEF, 0, 1, 3, 6),
					self::entry(Item::RAW_MUTTON, 0, 1, 3, 6),
					self::entry(Item::COAL, 0, 1, 3, 3),
				])];
			case "cartographer":
				return [self::pool(1, 5, [
					self::entry(Item::MAP, 0, 1, 3, 10),
					self::entry(Item::PAPER, 0, 1, 5, 15),
					self::entry(Item::COMPASS, 0, 1, 1, 5),
					self::entry(Item::BREAD, 0, 1, 4, 15),
					self::entry(Item::SAPLING, 0, 1, 2, 5),
				])];
			case "desert_house":
				return [self::pool(3, 8, [
					self::entry(Item::CLAY, 0, 1, 1, 1),
					self::entry(Item::DYE, 2, 1, 1, 1),
					self::entry(Item::CACTUS, 0, 1, 4, 10),
					self::entry(Item::WHEAT, 0, 1, 7, 10),
					self::entry(Item::BREAD, 0, 1, 4, 10),
					self::entry(Item::BOOK, 0, 1, 1, 1),
					self::entry(Item::DEAD_BUSH, 0, 1, 3, 2),
					self::entry(Item::EMERALD, 0, 1, 3, 1),
				])];
			case "fletcher":
				return [self::pool(1, 5, [
					self::entry(Item::EMERALD, 0, 1, 1, 1),
					self::entry(Item::ARROW, 0, 1, 3, 2),
					self::entry(Item::FEATHER, 0, 1, 3, 6),
					self::entry(Item::EGG, 0, 1, 3, 2),
					self::entry(Item::FLINT, 0, 1, 3, 6),
					self::entry(Item::STICK, 0, 1, 3, 6),
				])];
			case "mason":
				return [self::pool(1, 5, [
					self::entry(Item::CLAY, 0, 1, 3, 1),
					self::entry(Item::FLOWER_POT, 0, 1, 1, 1),
					self::entry(Item::STONE, 0, 1, 1, 2),
					self::entry(Item::STONE_BRICKS, 0, 1, 1, 2),
					self::entry(Item::BREAD, 0, 1, 4, 4),
					self::entry(Item::DYE, 11, 1, 1, 1),
					self::entry(Item::EMERALD, 0, 1, 1, 1),
				])];
			case "plains_house":
				return [self::pool(3, 8, [
					self::entry(Item::GOLD_NUGGET, 0, 1, 3, 1),
					self::entry(Item::DANDELION, 0, 1, 1, 2),
					self::entry(Item::POPPY, 0, 1, 1, 1),
					self::entry(Item::POTATO, 0, 1, 7, 10),
					self::entry(Item::BREAD, 0, 1, 4, 10),
					self::entry(Item::APPLE, 0, 1, 5, 10),
					self::entry(Item::BOOK, 0, 1, 1, 1),
					self::entry(Item::FEATHER, 0, 1, 1, 1),
					self::entry(Item::EMERALD, 0, 1, 4, 2),
					self::entry(Item::SAPLING, 0, 1, 2, 5),
				])];
			case "savanna_house":
				return [self::pool(3, 8, [
					self::entry(Item::GOLD_NUGGET, 0, 1, 3, 1),
					self::entry(Item::TALL_GRASS, 1, 1, 1, 5),
					self::entry(Item::TALL_GRASS, 2, 1, 1, 5),
					self::entry(Item::BREAD, 0, 1, 4, 10),
					self::entry(Item::WHEAT_SEEDS, 0, 1, 5, 10),
					self::entry(Item::EMERALD, 0, 1, 4, 2),
					self::entry(Item::SAPLING, 4, 1, 2, 10),
					self::entry(Item::SADDLE, 0, 1, 1, 1),
					self::entry(Item::TORCH, 0, 1, 2, 1),
					self::entry(Item::BUCKET, 0, 1, 1, 1),
				])];
			case "shepherd":
				return [self::pool(1, 5, [
					self::entry(Item::WOOL, 0, 1, 8, 6),
					self::entry(Item::WOOL, 15, 1, 3, 3),
					self::entry(Item::WOOL, 7, 1, 3, 2),
					self::entry(Item::WOOL, 12, 1, 3, 2),
					self::entry(Item::WOOL, 8, 1, 3, 2),
					self::entry(Item::EMERALD, 0, 1, 1, 1),
					self::entry(Item::SHEARS, 0, 1, 1, 1),
					self::entry(Item::WHEAT, 0, 1, 6, 6),
				])];
			case "snowy_house":
				return [self::pool(3, 8, [
					self::entry(Item::SNOW, 0, 1, 1, 4),
					self::entry(Item::POTATO, 0, 1, 7, 10),
					self::entry(Item::BREAD, 0, 1, 4, 10),
					self::entry(Item::BEETROOT_SEEDS, 0, 1, 5, 10),
					self::entry(Item::BEETROOT_SOUP, 0, 1, 1, 1),
					self::entry(Item::FURNACE, 0, 1, 1, 1),
					self::entry(Item::EMERALD, 0, 1, 4, 1),
					self::entry(Item::SNOWBALL, 0, 1, 7, 10),
					self::entry(Item::COAL, 0, 1, 4, 5),
				])];
			case "taiga_house":
				return [self::pool(3, 8, [
					self::entry(Item::TALL_GRASS, 2, 1, 1, 2),
					self::entry(Item::POTATO, 0, 1, 7, 10),
					self::entry(Item::BREAD, 0, 1, 4, 10),
					self::entry(Item::PUMPKIN_SEEDS, 0, 1, 5, 5),
					self::entry(Item::PUMPKIN_PIE, 0, 1, 1, 1),
					self::entry(Item::EMERALD, 0, 1, 4, 2),
					self::entry(Item::SAPLING, 1, 1, 5, 5),
					self::entry(Item::LOG2, 1, 1, 5, 10),
				])];
			case "tannery":
				return [self::pool(1, 5, [
					self::entry(Item::LEATHER, 0, 1, 3, 1),
					self::entry(Item::LEATHER_TUNIC, 0, 1, 1, 2),
					self::entry(Item::LEATHER_BOOTS, 0, 1, 1, 2),
					self::entry(Item::LEATHER_CAP, 0, 1, 1, 2),
					self::entry(Item::BREAD, 0, 1, 4, 5),
					self::entry(Item::LEATHER_PANTS, 0, 1, 1, 2),
					self::entry(Item::SADDLE, 0, 1, 1, 1),
					self::entry(Item::EMERALD, 0, 1, 4, 1),
				])];
			case "temple":
				return [self::pool(3, 8, [
					self::entry(Item::REDSTONE, 0, 1, 4, 2),
					self::entry(Item::BREAD, 0, 1, 4, 7),
					self::entry(Item::ROTTEN_FLESH, 0, 1, 4, 7),
					self::entry(Item::DYE, 4, 1, 4, 1),
					self::entry(Item::GOLD_INGOT, 0, 1, 4, 1),
					self::entry(Item::EMERALD, 0, 1, 4, 1),
				])];
			case "toolsmith":
				return [self::pool(3, 8, [
					self::entry(Item::DIAMOND, 0, 1, 3, 1),
					self::entry(Item::IRON_INGOT, 0, 1, 5, 5),
					self::entry(Item::GOLD_INGOT, 0, 1, 3, 1),
					self::entry(Item::BREAD, 0, 1, 3, 15),
					self::entry(Item::IRON_PICKAXE, 0, 1, 1, 5),
					self::entry(Item::COAL, 0, 1, 3, 1),
					self::entry(Item::STICK, 0, 1, 3, 20),
					self::entry(Item::IRON_SHOVEL, 0, 1, 1, 5),
				])];
			case self::CATEGORY_WEAPONSMITH:
			default:
				return [self::pool(3, 8, [
					self::entry(Item::DIAMOND, 0, 1, 3, 3),
					self::entry(Item::IRON_INGOT, 0, 1, 5, 10),
					self::entry(Item::GOLD_INGOT, 0, 1, 3, 5),
					self::entry(Item::BREAD, 0, 1, 3, 15),
					self::entry(Item::APPLE, 0, 1, 3, 15),
					self::entry(Item::IRON_PICKAXE, 0, 1, 1, 5),
					self::entry(Item::IRON_SWORD, 0, 1, 1, 5),
					self::entry(Item::IRON_CHESTPLATE, 0, 1, 1, 5),
					self::entry(Item::IRON_HELMET, 0, 1, 1, 5),
					self::entry(Item::IRON_LEGGINGS, 0, 1, 1, 5),
					self::entry(Item::IRON_BOOTS, 0, 1, 1, 5),
					self::entry(Item::OBSIDIAN, 0, 3, 7, 5),
					self::entry(Item::SAPLING, 0, 3, 7, 5),
					self::entry(Item::SADDLE, 0, 1, 1, 3),
					self::entry(StructureLoot::itemId("IRON_HORSE_ARMOR", 417), 0, 1, 1, 1),
					self::entry(StructureLoot::itemId("GOLDEN_HORSE_ARMOR", 418), 0, 1, 1, 1),
					self::entry(StructureLoot::itemId("DIAMOND_HORSE_ARMOR", 419), 0, 1, 1, 1),
				])];
		}
	}

	private static function pool(int $minRolls, int $maxRolls, array $entries) : array{
		return StructureLoot::pool($minRolls, $maxRolls, self::registeredEntries($entries));
	}

	private static function entry(int $id, int $damage, int $minCount, int $maxCount, int $weight) : array{
		return StructureLoot::entry($id, $damage, $minCount, $maxCount, $weight);
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
}
