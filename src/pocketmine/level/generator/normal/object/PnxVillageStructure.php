<?php

namespace pocketmine\level\generator\normal\object;

require_once __DIR__ . "/PnxVillagePools.php";
require_once __DIR__ . "/VillageSmithyChestLoot.php";
require_once dirname(__DIR__, 4) . "/utils/Binary.php";
require_once dirname(__DIR__, 4) . "/utils/Utils.php";
require_once dirname(__DIR__, 4) . "/nbt/tag/Tag.php";
require_once dirname(__DIR__, 4) . "/nbt/tag/NamedTag.php";
require_once dirname(__DIR__, 4) . "/nbt/tag/EndTag.php";
require_once dirname(__DIR__, 4) . "/nbt/tag/ByteTag.php";
require_once dirname(__DIR__, 4) . "/nbt/tag/ShortTag.php";
require_once dirname(__DIR__, 4) . "/nbt/tag/IntTag.php";
require_once dirname(__DIR__, 4) . "/nbt/tag/LongTag.php";
require_once dirname(__DIR__, 4) . "/nbt/tag/FloatTag.php";
require_once dirname(__DIR__, 4) . "/nbt/tag/DoubleTag.php";
require_once dirname(__DIR__, 4) . "/nbt/tag/ByteArrayTag.php";
require_once dirname(__DIR__, 4) . "/nbt/tag/StringTag.php";
require_once dirname(__DIR__, 4) . "/nbt/tag/ListTag.php";
require_once dirname(__DIR__, 4) . "/nbt/tag/CompoundTag.php";
require_once dirname(__DIR__, 4) . "/nbt/tag/IntArrayTag.php";
require_once dirname(__DIR__, 4) . "/nbt/NBT.php";

use pocketmine\block\Block;
use pocketmine\level\ChunkManager;
use pocketmine\nbt\NBT;
use pocketmine\nbt\tag\ByteArrayTag;
use pocketmine\nbt\tag\ByteTag;
use pocketmine\nbt\tag\CompoundTag;
use pocketmine\nbt\tag\IntArrayTag;
use pocketmine\nbt\tag\IntTag;
use pocketmine\nbt\tag\ListTag;
use pocketmine\nbt\tag\StringTag;
use pocketmine\utils\Random;

final class PnxVillageStructureRegistry{
	/** @var array<string, array>|null */
	private static $structures = null;
	/** @var array<int, array>|null */
	private static $blockPalette = null;

	public static function get(string $name){
		self::load();
		return self::$structures[$name] ?? null;
	}

	public static function count() : int{
		self::load();
		return count(self::$structures);
	}

	private static function load(){
		if(self::$structures !== null){
			return;
		}
		self::$blockPalette = self::loadBlockPalette();
		self::$structures = [];
		$root = self::readCompressedNbt(self::resourcePath("gamedata/unknown/structures.nbt"));
		if(!($root->village ?? null) instanceof CompoundTag){
			throw new \RuntimeException("PNX village structures are missing");
		}
		self::collectStructures($root->village, "village", self::$structures);
	}

	private static function collectStructures(CompoundTag $tag, string $prefix, array &$structures){
		foreach($tag as $name => $child){
			if(!$child instanceof CompoundTag){
				continue;
			}
			$identifier = $prefix . "/" . (string) $name;
			if(($child->object ?? null) instanceof CompoundTag){
				$structures[$identifier] = self::decodeStructure($child->object);
			}
			self::collectStructures($child, $identifier, $structures);
		}
	}

	private static function decodeStructure(CompoundTag $object) : array{
		$sizeTag = $object->size ?? null;
		$paletteTag = $object->palette ?? null;
		$blocksTag = $object->blocks ?? null;
		if(!$sizeTag instanceof IntArrayTag || !$paletteTag instanceof ListTag || !$blocksTag instanceof ByteArrayTag){
			throw new \RuntimeException("Invalid PNX village structure object");
		}

		$size = array_values($sizeTag->getValue());
		$sizeX = (int) ($size[0] ?? 0);
		$sizeY = (int) ($size[1] ?? 0);
		$palette = [];
		foreach($paletteTag as $entry){
			if($entry instanceof IntTag){
				$hash = (int) $entry->getValue();
				$palette[] = self::$blockPalette[$hash] ?? ["name" => "minecraft:air", "states" => []];
			}
		}

		$blocks = [];
		$blockStatesByIndex = [];
		$raw = $blocksTag->getValue();
		for($index = 0, $len = strlen($raw); $index < $len; ++$index){
			$paletteIndex = ord($raw[$index]) - 1;
			if($paletteIndex < 0 || !isset($palette[$paletteIndex])){
				continue;
			}
			$state = $palette[$paletteIndex];
			$x = $index % $sizeX;
			$y = intdiv($index, $sizeX) % $sizeY;
			$z = intdiv($index, $sizeX * $sizeY);
			$blocks[] = ["x" => $x, "y" => $y, "z" => $z, "state" => $state];
			$blockStatesByIndex[$index] = $state;
		}

		$jigsaws = [];
		if(($object->jigsaw ?? null) instanceof ListTag){
			foreach($object->jigsaw as $entry){
				if(!$entry instanceof CompoundTag){
					continue;
				}
				$pos = ($entry->pos ?? null) instanceof IntArrayTag ? array_values($entry->pos->getValue()) : [0, 0, 0];
				$x = (int) ($pos[0] ?? 0);
				$y = (int) ($pos[1] ?? 0);
				$z = (int) ($pos[2] ?? 0);
				$flat = $x + ($y * $sizeX) + ($z * $sizeX * $sizeY);
				$finalHash = self::compoundInt($entry, "final_state");
				$jigsaws[] = [
					"x" => $x,
					"y" => $y,
					"z" => $z,
					"final_state" => self::$blockPalette[$finalHash] ?? ["name" => "minecraft:air", "states" => []],
					"name" => self::compoundString($entry, "name"),
					"joint" => self::compoundString($entry, "joint"),
					"pool" => self::compoundString($entry, "pool"),
					"target" => self::compoundString($entry, "target"),
					"placement_priority" => self::compoundInt($entry, "placement_priority"),
					"selection_priority" => self::compoundInt($entry, "selection_priority"),
					"block_state" => $blockStatesByIndex[$flat] ?? ["name" => "minecraft:jigsaw", "states" => []],
				];
			}
		}

		return [
			"size" => [$sizeX, $sizeY, (int) ($size[2] ?? 0)],
			"blocks" => $blocks,
			"jigsaws" => $jigsaws,
		];
	}

	private static function loadBlockPalette() : array{
		$root = self::readCompressedNbt(self::resourcePath("gamedata/kaooot/block_palette.nbt"));
		if(!($root->blocks ?? null) instanceof ListTag){
			throw new \RuntimeException("PNX block palette is missing blocks");
		}

		$palette = [];
		foreach($root->blocks as $entry){
			if(!$entry instanceof CompoundTag){
				continue;
			}
			$palette[self::compoundInt($entry, "network_id")] = [
				"name" => self::compoundString($entry, "name"),
				"states" => ($entry->states ?? null) instanceof CompoundTag ? self::compoundToArray($entry->states) : [],
			];
		}
		return $palette;
	}

	private static function readCompressedNbt(string $path) : CompoundTag{
		$bytes = @file_get_contents($path);
		if($bytes === false){
			throw new \RuntimeException("Unable to read PNX village resource: " . $path);
		}
		$raw = gzdecode($bytes);
		if($raw === false){
			throw new \RuntimeException("Unable to decompress PNX village resource: " . $path);
		}
		$nbt = new NBT(NBT::BIG_ENDIAN);
		$nbt->read($raw);
		$data = $nbt->getData();
		if(!$data instanceof CompoundTag){
			throw new \RuntimeException("Unexpected PNX village NBT root");
		}
		return $data;
	}

	private static function compoundToArray(CompoundTag $tag) : array{
		$result = [];
		foreach($tag as $name => $child){
			if($child instanceof IntTag || $child instanceof StringTag || $child instanceof ByteTag){
				$result[(string) $name] = $child->getValue();
			}elseif($child instanceof CompoundTag){
				$result[(string) $name] = self::compoundToArray($child);
			}
		}
		return $result;
	}

	private static function compoundInt(CompoundTag $tag, string $name) : int{
		$child = $tag->{$name} ?? null;
		return $child instanceof IntTag ? (int) $child->getValue() : 0;
	}

	private static function compoundString(CompoundTag $tag, string $name) : string{
		$child = $tag->{$name} ?? null;
		return $child instanceof StringTag ? (string) $child->getValue() : "";
	}

	private static function resourcePath(string $relative) : string{
		return dirname(__DIR__, 4) . "/resources/" . $relative;
	}
}

final class PnxVillageBlockMapper{
	const ROTATE_NONE = 0;
	const ROTATE_90 = 1;
	const ROTATE_180 = 2;
	const ROTATE_270 = 3;

	private static $explicitSupportedVillageStates = [
		"minecraft:oak_stairs",
		"minecraft:spruce_stairs",
		"minecraft:acacia_stairs",
		"minecraft:smooth_sandstone_stairs",
		"minecraft:spruce_door",
		"minecraft:acacia_door",
		"minecraft:ladder",
		"minecraft:torch",
		"minecraft:chest",
		"minecraft:hay_block",
	];

	public static function toLegacy(array $state, int $stateRotation = self::ROTATE_NONE){
		$state = self::rotateState($state, $stateRotation);
		$name = $state["name"] ?? "minecraft:air";
		$states = $state["states"] ?? [];

		switch($name){
			case "minecraft:air":
				return [Block::AIR, 0];
			case "minecraft:grass_block":
				return [Block::GRASS, 0];
			case "minecraft:grass_path":
				return [Block::GRASS_PATH, 0];
			case "minecraft:dirt":
				return [Block::DIRT, 0];
			case "minecraft:coarse_dirt":
				return [Block::DIRT, 1];
			case "minecraft:farmland":
				return [Block::FARMLAND, min(7, (int) ($states["moisturized_amount"] ?? 0))];
			case "minecraft:sand":
				return [Block::SAND, 0];
			case "minecraft:gravel":
				return [Block::GRAVEL, 0];
			case "minecraft:cobblestone":
				return [Block::COBBLESTONE, 0];
			case "minecraft:mossy_cobblestone":
				return [Block::MOSS_STONE, 0];
			case "minecraft:stone_bricks":
				return [self::blockId("STONE_BRICKS", 98), 0];
			case "minecraft:smooth_stone":
				return [Block::STONE, 0];
			case "minecraft:diorite":
				return [Block::STONE, 3];
			case "minecraft:granite":
				return [Block::STONE, 1];
			case "minecraft:sandstone":
			case "minecraft:cut_sandstone":
				return [Block::SANDSTONE, 0];
			case "minecraft:chiseled_sandstone":
				return [Block::SANDSTONE, 1];
			case "minecraft:smooth_sandstone":
				return [Block::SANDSTONE, 2];
			case "minecraft:hardened_clay":
				return [self::blockId("HARDENED_CLAY", 172), 0];
			case "minecraft:clay":
				return [self::blockId("CLAY_BLOCK", 82), 0];
			case "minecraft:bricks":
			case "minecraft:brick_block":
				return [self::blockId("BRICKS", 45), 0];
			case "minecraft:glass_pane":
			case "minecraft:white_stained_glass_pane":
			case "minecraft:orange_stained_glass_pane":
			case "minecraft:yellow_stained_glass_pane":
				return [Block::GLASS_PANE, 0];
			case "minecraft:bookshelf":
				return [Block::BOOKSHELF, 0];
			case "minecraft:crafting_table":
				return [Block::CRAFTING_TABLE, 0];
			case "minecraft:furnace":
			case "minecraft:blast_furnace":
			case "minecraft:smoker":
				return [Block::FURNACE, self::cardinalMeta($states["minecraft:cardinal_direction"] ?? "south")];
			case "minecraft:chest":
				return [Block::CHEST, self::cardinalMeta($states["minecraft:cardinal_direction"] ?? "south")];
			case "minecraft:cauldron":
				return [self::blockId("CAULDRON_BLOCK", 118), 0];
			case "minecraft:brewing_stand":
				return [self::blockId("BREWING_STAND_BLOCK", 117), 0];
			case "minecraft:stonecutter_block":
				return [self::blockId("STONECUTTER", 245), 0];
			case "minecraft:flower_pot":
				return [self::blockId("FLOWER_POT_BLOCK", 140), 0];
			case "minecraft:iron_bars":
				return [Block::IRON_BARS, 0];
			case "minecraft:torch":
				return [Block::TORCH, self::torchMeta($states["torch_facing_direction"] ?? "top")];
			case "minecraft:ladder":
				return [Block::LADDER, self::facingDirectionMeta((int) ($states["facing_direction"] ?? 2))];
			case "minecraft:wall_sign":
			case "minecraft:spruce_wall_sign":
				return [self::blockId("WALL_SIGN", 68), self::facingDirectionMeta((int) ($states["facing_direction"] ?? 2))];
			case "minecraft:pumpkin":
			case "minecraft:carved_pumpkin":
				return [Block::PUMPKIN, self::pumpkinMeta($states["minecraft:cardinal_direction"] ?? "south")];
			case "minecraft:hay_block":
				return [Block::HAY_BALE, self::axisMeta($states["pillar_axis"] ?? "y", 0)];
			case "minecraft:cactus":
				return [Block::CACTUS, 0];
			case "minecraft:deadbush":
				return [self::blockId("DEAD_BUSH", 32), 0];
			case "minecraft:dandelion":
				return [Block::DANDELION, 0];
			case "minecraft:poppy":
				return [Block::RED_FLOWER, 0];
			case "minecraft:oxeye_daisy":
				return [Block::RED_FLOWER, 8];
			case "minecraft:short_grass":
				return [Block::TALL_GRASS, 1];
			case "minecraft:tall_grass":
				return [Block::DOUBLE_PLANT, 2];
			case "minecraft:fern":
				return [Block::TALL_GRASS, 2];
			case "minecraft:large_fern":
				return [Block::DOUBLE_PLANT, 3];
			case "minecraft:snow":
				return [self::blockId("SNOW_BLOCK", 80), 0];
			case "minecraft:snow_layer":
				return [Block::SNOW_LAYER, max(0, min(7, ((int) ($states["height"] ?? 0)) - 1))];
			case "minecraft:ice":
				return [self::blockId("ICE", 79), 0];
			case "minecraft:packed_ice":
			case "minecraft:blue_ice":
				return [self::blockId("PACKED_ICE", 174), 0];
			case "minecraft:water":
			case "minecraft:flowing_water":
				return [Block::STILL_WATER, 0];
			case "minecraft:flowing_lava":
			case "minecraft:lava":
				return [Block::LAVA, 0];
			case "minecraft:wheat":
				return [Block::WHEAT_BLOCK, max(0, min(7, (int) ($states["growth"] ?? 7)))];
			case "minecraft:melon_block":
				return [self::blockId("MELON_BLOCK", 103), 0];
			case "minecraft:pumpkin_stem":
				return [self::blockId("PUMPKIN_STEM", 104), max(0, min(7, (int) ($states["growth"] ?? 7)))];
			case "minecraft:melon_stem":
				return [self::blockId("MELON_STEM", 105), max(0, min(7, (int) ($states["growth"] ?? 7)))];
			case "minecraft:bed":
				return [self::blockId("BED_BLOCK", 26), ((int) ($states["direction"] ?? 0) & 0x03) | (!empty($states["head_piece_bit"]) ? 0x08 : 0)];
			case "minecraft:cobblestone_wall":
				return [Block::STONE_WALL, 0];
			case "minecraft:granite_wall":
			case "minecraft:diorite_wall":
				return [Block::STONE_WALL, 0];
		}

		$wood = self::woodType($name);
		if($wood !== null){
			return self::mapWood($name, $wood, $states);
		}
		$color = self::colorMeta($name);
		if($color !== null){
			if(substr($name, -7) === "_carpet"){
				return [Block::CARPET, $color];
			}
			if(substr($name, -5) === "_wool"){
				return [Block::WOOL, $color];
			}
			if(substr($name, -11) === "_terracotta" || substr($name, -7) === "_glazed"){
				return [self::blockId("STAINED_CLAY", self::blockId("STAINED_HARDENED_CLAY", 159)), $color];
			}
		}

		// High-version workstations and detail blocks not present in this source tree.
		return null;
	}

	public static function rotateState(array $state, int $rotation) : array{
		$rotation &= 3;
		if($rotation === self::ROTATE_NONE){
			return $state;
		}
		for($i = 0; $i < $rotation; ++$i){
			$state = self::rotateStateClockwise($state);
		}
		return $state;
	}

	private static function rotateStateClockwise(array $state) : array{
		$name = $state["name"] ?? "";
		$states = $state["states"] ?? [];
		if(isset($states["torch_facing_direction"])){
			$states["torch_facing_direction"] = self::rotateCardinal($states["torch_facing_direction"]);
		}
		if(isset($states["minecraft:cardinal_direction"])){
			$states["minecraft:cardinal_direction"] = self::rotateCardinal($states["minecraft:cardinal_direction"]);
		}
		if(isset($states["pillar_axis"]) && ($states["pillar_axis"] === "x" || $states["pillar_axis"] === "z")){
			$states["pillar_axis"] = $states["pillar_axis"] === "x" ? "z" : "x";
		}
		if(isset($states["weirdo_direction"])){
			$map = [0 => 2, 1 => 3, 2 => 1, 3 => 0];
			$states["weirdo_direction"] = $map[(int) $states["weirdo_direction"]] ?? 0;
		}
		if(isset($states["direction"])){
			$states["direction"] = self::usesEwsnDirection($name) ?
				self::rotateEwsnDirectionClockwise((int) $states["direction"]) :
				(((int) $states["direction"] + 1) & 3);
		}
		if(isset($states["facing_direction"])){
			$states["facing_direction"] = $name === "minecraft:jigsaw" ?
				self::rotateFacingDirectionCounterClockwise((int) $states["facing_direction"]) :
				self::rotateFacingDirectionClockwise((int) $states["facing_direction"]);
		}
		if(isset($states["rotation"]) && $name === "minecraft:jigsaw"){
			$states["rotation"] = ((int) $states["rotation"] + 1) & 3;
		}
		if(isset($states["wall_post_bit"])){
			$north = $states["wall_connection_type_north"] ?? "none";
			$east = $states["wall_connection_type_east"] ?? "none";
			$south = $states["wall_connection_type_south"] ?? "none";
			$west = $states["wall_connection_type_west"] ?? "none";
			$states["wall_connection_type_east"] = $north;
			$states["wall_connection_type_south"] = $east;
			$states["wall_connection_type_west"] = $south;
			$states["wall_connection_type_north"] = $west;
		}
		$state["states"] = $states;
		return $state;
	}

	private static function usesEwsnDirection(string $name) : bool{
		return $name === "minecraft:trapdoor" || substr($name, -9) === "_trapdoor";
	}

	private static function rotateEwsnDirectionClockwise(int $direction) : int{
		$map = [0 => 2, 2 => 1, 1 => 3, 3 => 0];
		return $map[$direction & 3] ?? 0;
	}

	private static function mapWood(string $name, array $wood, array $states){
		if(strpos($name, "_planks") !== false){
			return [Block::PLANKS, $wood["meta"]];
		}
		if(strpos($name, "_log") !== false || strpos($name, "_wood") !== false){
			return [$wood["log_id"], self::axisMeta($states["pillar_axis"] ?? "y", $wood["log_meta"])];
		}
		if(strpos($name, "_leaves") !== false){
			return [$wood["leaves_id"], $wood["leaves_meta"]];
		}
		if(strpos($name, "_slab") !== false){
			return [Block::WOOD_SLAB, $wood["meta"] | self::slabTopBit($states)];
		}
		if(strpos($name, "_double_slab") !== false){
			return [Block::DOUBLE_WOOD_SLAB, $wood["meta"]];
		}
		if(strpos($name, "_stairs") !== false){
			return [$wood["stairs_id"], self::stairMeta($states)];
		}
		if(strpos($name, "_door") !== false){
			return [$wood["door_id"], self::doorMeta($states)];
		}
		if(strpos($name, "_trapdoor") !== false || $name === "minecraft:trapdoor"){
			return [self::blockId("TRAPDOOR", 96), self::trapdoorMeta($states)];
		}
		if(strpos($name, "_fence_gate") !== false || $name === "minecraft:fence_gate"){
			return [$wood["gate_id"], self::gateMeta($states)];
		}
		if(strpos($name, "_fence") !== false){
			return [Block::FENCE, 0];
		}
		if(strpos($name, "_pressure_plate") !== false){
			return [self::blockId("WOODEN_PRESSURE_PLATE", 72), 0];
		}
		if(strpos($name, "_button") !== false){
			return [self::blockId("WOODEN_BUTTON", 143), 0];
		}
		if(strpos($name, "_sapling") !== false){
			return [Block::SAPLING, $wood["meta"]];
		}
		return null;
	}

	private static function woodType(string $name){
		$types = [
			"oak" => ["meta" => 0, "log_id" => Block::LOG, "log_meta" => 0, "leaves_id" => Block::LEAVES, "leaves_meta" => 0, "stairs_id" => Block::WOOD_STAIRS, "door_id" => Block::WOODEN_DOOR_BLOCK, "gate_id" => self::blockId("FENCE_GATE", 107)],
			"spruce" => ["meta" => 1, "log_id" => Block::LOG, "log_meta" => 1, "leaves_id" => Block::LEAVES, "leaves_meta" => 1, "stairs_id" => self::blockId("SPRUCE_WOOD_STAIRS", 134), "door_id" => self::blockId("SPRUCE_DOOR_BLOCK", 193), "gate_id" => self::blockId("FENCE_GATE_SPRUCE", 183)],
			"birch" => ["meta" => 2, "log_id" => Block::LOG, "log_meta" => 2, "leaves_id" => Block::LEAVES, "leaves_meta" => 2, "stairs_id" => self::blockId("BIRCH_WOOD_STAIRS", 135), "door_id" => self::blockId("BIRCH_DOOR_BLOCK", 194), "gate_id" => self::blockId("FENCE_GATE_BIRCH", 184)],
			"jungle" => ["meta" => 3, "log_id" => Block::LOG, "log_meta" => 3, "leaves_id" => Block::LEAVES, "leaves_meta" => 3, "stairs_id" => self::blockId("JUNGLE_WOOD_STAIRS", 136), "door_id" => self::blockId("JUNGLE_DOOR_BLOCK", 195), "gate_id" => self::blockId("FENCE_GATE_JUNGLE", 185)],
			"acacia" => ["meta" => 4, "log_id" => Block::LOG2, "log_meta" => 0, "leaves_id" => Block::LEAVES2, "leaves_meta" => 0, "stairs_id" => self::blockId("ACACIA_WOOD_STAIRS", 163), "door_id" => self::blockId("ACACIA_DOOR_BLOCK", 196), "gate_id" => self::blockId("FENCE_GATE_ACACIA", 187)],
			"dark_oak" => ["meta" => 5, "log_id" => Block::LOG2, "log_meta" => 1, "leaves_id" => Block::LEAVES2, "leaves_meta" => 1, "stairs_id" => self::blockId("DARK_OAK_WOOD_STAIRS", 164), "door_id" => self::blockId("DARK_OAK_DOOR_BLOCK", 197), "gate_id" => self::blockId("FENCE_GATE_DARK_OAK", 186)],
		];
		foreach($types as $prefix => $type){
			if(strpos($name, "minecraft:" . $prefix . "_") === 0 || strpos($name, "minecraft:stripped_" . $prefix . "_") === 0){
				return $type;
			}
		}
		if($name === "minecraft:wooden_door"){
			return $types["oak"];
		}
		if($name === "minecraft:trapdoor" || $name === "minecraft:fence_gate"){
			return $types["oak"];
		}
		return null;
	}

	public static function blockId(string $constant, int $fallback) : int{
		$name = Block::class . "::" . $constant;
		return defined($name) ? (int) constant($name) : $fallback;
	}

	private static function colorMeta(string $name){
		$colors = [
			"white" => 0, "orange" => 1, "magenta" => 2, "light_blue" => 3,
			"yellow" => 4, "lime" => 5, "pink" => 6, "gray" => 7,
			"light_gray" => 8, "cyan" => 9, "purple" => 10, "blue" => 11,
			"brown" => 12, "green" => 13, "red" => 14, "black" => 15,
		];
		foreach($colors as $namePrefix => $meta){
			if(strpos($name, "minecraft:" . $namePrefix . "_") === 0){
				return $meta;
			}
		}
		return null;
	}

	private static function rotateCardinal($direction) : string{
		switch((string) $direction){
			case "north": return "east";
			case "east": return "south";
			case "south": return "west";
			case "west": return "north";
		}
		return (string) $direction;
	}

	private static function rotateFacingDirectionClockwise(int $face) : int{
		switch($face & 0x07){
			case 2: return 5 | ($face & ~0x07);
			case 5: return 3 | ($face & ~0x07);
			case 3: return 4 | ($face & ~0x07);
			case 4: return 2 | ($face & ~0x07);
		}
		return $face;
	}

	private static function rotateFacingDirectionCounterClockwise(int $face) : int{
		switch($face & 0x07){
			case 2: return 4 | ($face & ~0x07);
			case 4: return 3 | ($face & ~0x07);
			case 3: return 5 | ($face & ~0x07);
			case 5: return 2 | ($face & ~0x07);
		}
		return $face;
	}

	private static function slabTopBit(array $states) : int{
		return (($states["minecraft:vertical_half"] ?? "bottom") === "top") ? 0x08 : 0;
	}

	private static function stairMeta(array $states) : int{
		$meta = (int) ($states["weirdo_direction"] ?? 0);
		if(!empty($states["upside_down_bit"])){
			$meta |= 0x04;
		}
		return $meta;
	}

	private static function doorMeta(array $states) : int{
		if(!empty($states["upper_block_bit"])){
			return 0x08 | (!empty($states["door_hinge_bit"]) ? 0x01 : 0);
		}
		$meta = self::doorFacingMeta($states["minecraft:cardinal_direction"] ?? "south");
		if(!empty($states["open_bit"])){
			$meta |= 0x04;
		}
		return $meta;
	}

	private static function doorFacingMeta($direction) : int{
		switch((string) $direction){
			case "south": return 0;
			case "west": return 1;
			case "north": return 2;
			case "east": default: return 3;
		}
	}

	private static function trapdoorMeta(array $states) : int{
		$meta = ((int) ($states["direction"] ?? 0)) & 0x03;
		if(!empty($states["open_bit"])){
			$meta |= 0x04;
		}
		if(!empty($states["upside_down_bit"])){
			$meta |= 0x08;
		}
		return $meta;
	}

	private static function gateMeta(array $states) : int{
		return self::doorFacingMeta($states["minecraft:cardinal_direction"] ?? "south") & 0x03;
	}

	private static function axisMeta($axis, int $baseMeta) : int{
		switch((string) $axis){
			case "x": return $baseMeta | 0x04;
			case "z": return $baseMeta | 0x08;
			case "y": default: return $baseMeta;
		}
	}

	private static function torchMeta($direction) : int{
		switch((string) $direction){
			case "west": return 1;
			case "east": return 2;
			case "north": return 3;
			case "south": return 4;
			case "top":
			default: return 0;
		}
	}

	private static function cardinalMeta($direction) : int{
		switch((string) $direction){
			case "north": return 2;
			case "south": return 3;
			case "west": return 4;
			case "east":
			default: return 5;
		}
	}

	private static function facingDirectionMeta(int $face) : int{
		return in_array($face, [2, 3, 4, 5], true) ? $face : 2;
	}

	private static function pumpkinMeta($direction) : int{
		switch((string) $direction){
			case "south": return 0;
			case "west": return 1;
			case "north": return 2;
			case "east":
			default: return 3;
		}
	}
}

final class PnxVillagePlacer{
	const MAX_DEPTH = 7;

	/** @var ChunkManager */
	private $level;
	/** @var int */
	private $originX;
	/** @var int */
	private $originY;
	/** @var int */
	private $originZ;
	/** @var array<string, array> */
	private $pools;
	/** @var int */
	private $villagerMarker;
	/** @var int */
	private $golemMarker;
	/** @var int */
	private $villagerCount = 0;
	/** @var int */
	private $golemCount = 0;
	/** @var int|null */
	private $minChunkX = null;
	/** @var int|null */
	private $maxChunkX = null;
	/** @var int|null */
	private $minChunkZ = null;
	/** @var int|null */
	private $maxChunkZ = null;
	/** @var array<string, int> */
	private $surfaceYCache = [];

	public function place(ChunkManager $level, int $originX, int $originY, int $originZ, string $type, Random $random, int $villagerMarker, int $golemMarker, int $populationChunkRadius = 0) : bool{
		$definition = PnxVillagePools::get($type);
		if(!is_array($definition)){
			return false;
		}
		$this->level = $level;
		$this->originX = $originX;
		$this->originY = $originY;
		$this->originZ = $originZ;
		$this->pools = $definition["pools"];
		$this->villagerMarker = $villagerMarker;
		$this->golemMarker = $golemMarker;
		$this->villagerCount = 0;
		$this->golemCount = 0;
		$this->surfaceYCache = [];
		$this->setPopulationBounds($originX >> 4, $originZ >> 4, $populationChunkRadius);

		$root = $this->placeFromPool([0, 0, 0], PnxVillageBlockMapper::ROTATE_NONE, $definition["entry"], $random, []);
		if($root === null){
			return false;
		}
		$occupied = [$root["box"]];
		$connected = [];
		$pending = [["piece" => $root, "depth" => 0, "priority" => 0]];

		while(count($pending) > 0){
			$current = array_shift($pending);
			if($current["depth"] >= self::MAX_DEPTH){
				continue;
			}
			foreach($this->orderedJigsaws($current["piece"]["source"], $current["piece"]["placed"], $random) as $sourceReference){
				$parentWorld = $this->absolutePos($current["piece"]["position"], $sourceReference["placed"]);
				$parentKey = $this->posKey($parentWorld);
				if(isset($connected[$parentKey])){
					continue;
				}
				$nextPoolKey = $this->normalizeKey($sourceReference["source"]["pool"]);
				if(!isset($this->pools[$nextPoolKey])){
					continue;
				}
				$candidate = $this->findCandidate($nextPoolKey, $current["piece"], $sourceReference, $random, $connected, $occupied);
				if($candidate === null){
					continue;
				}
				$child = $this->placeStructurePiece($candidate["position"], $candidate["rotation"], $candidate["structureName"], $candidate["structure"], $candidate["projection"], $occupied);
				if($child === null){
					continue;
				}
				$connected[$parentKey] = true;
				$connected[$this->posKey($candidate["childJigsawWorld"])] = true;
				$occupied[] = $child["box"];
				if($current["depth"] + 1 < self::MAX_DEPTH){
					$this->insertPending($pending, ["piece" => $child, "depth" => $current["depth"] + 1, "priority" => (int) ($sourceReference["source"]["placement_priority"] ?? 0)]);
				}
			}
		}

		return true;
	}

	private function placeFromPool(array $position, int $rotation, string $poolName, Random $random, array $occupied){
		$entry = $this->selectPoolEntry($poolName, $random);
		if($entry === null || $this->isEmptyPoolEntry($entry["structure"])){
			return null;
		}
		$structureName = $this->normalizeKey($entry["structure"]);
		$structure = PnxVillageStructureRegistry::get($structureName);
		if(!is_array($structure)){
			return null;
		}
		return $this->placeStructurePiece($position, $rotation, $structureName, $structure, $entry["projection"] ?? "rigid", $occupied);
	}

	private function placeStructurePiece(array $position, int $rotation, string $structureName, array $sourceStructure, string $projection, array $occupied){
		$placed = $this->rotateStructure($sourceStructure, $rotation);
		$box = $this->createBox($position, $placed["size"]);
		if(!$this->isWithinPopulationBounds($box)){
			return null;
		}
		if($this->overlaps($box, $occupied)){
			return null;
		}
		$absoluteBlocks = [];
		foreach($placed["blocks"] as $block){
			$worldX = $this->originX + $position[0] + $block["x"];
			$worldY = $this->originY + $position[1] + $block["y"];
			$worldZ = $this->originZ + $position[2] + $block["z"];
			$legacy = $block["legacy"];
			if($legacy === null){
				continue;
			}
			$absoluteBlocks[$this->posKey([$worldX, $worldY, $worldZ])] = ["x" => $worldX, "y" => $worldY, "z" => $worldZ, "id" => $legacy[0], "meta" => $legacy[1], "state" => $block["state"]];
		}
		$absoluteJigsaws = [];
		foreach($placed["jigsaws"] as $jigsaw){
			$absoluteJigsaws[] = $jigsaw + [
				"world_x" => $this->originX + $position[0] + $jigsaw["x"],
				"world_y" => $this->originY + $position[1] + $jigsaw["y"],
				"world_z" => $this->originZ + $position[2] + $jigsaw["z"],
			];
		}

		$this->postProcessPiece($structureName, $absoluteBlocks, $absoluteJigsaws);
		$this->replaceJigsawBlocks($absoluteBlocks, $absoluteJigsaws, $rotation);
		$this->flushBlocks($absoluteBlocks, $structureName);
		$this->markEntityPiece($structureName, $absoluteJigsaws, $absoluteBlocks);

		return [
			"structureName" => $structureName,
			"source" => $sourceStructure,
			"placed" => $placed,
			"position" => $position,
			"rotation" => $rotation,
			"box" => $box,
			"projection" => $projection,
		];
	}

	private function rotateStructure(array $structure, int $rotation) : array{
		$rotation &= 3;
		$size = $structure["size"];
		$newSizeX = ($rotation === PnxVillageBlockMapper::ROTATE_NONE || $rotation === PnxVillageBlockMapper::ROTATE_180) ? $size[0] : $size[2];
		$newSizeZ = ($rotation === PnxVillageBlockMapper::ROTATE_NONE || $rotation === PnxVillageBlockMapper::ROTATE_180) ? $size[2] : $size[0];
		$stateRotation = $this->inverseRotation($rotation);
		$blocks = [];
		foreach($structure["blocks"] as $block){
			$rotated = $this->rotateXZ($size[0], $size[2], $block["x"], $block["z"], $rotation);
			$rotatedState = PnxVillageBlockMapper::rotateState($block["state"], $stateRotation);
			$blocks[] = [
				"x" => $rotated[0],
				"y" => $block["y"],
				"z" => $rotated[1],
				"state" => $rotatedState,
				"legacy" => PnxVillageBlockMapper::toLegacy($block["state"], $stateRotation),
			];
		}
		$jigsaws = [];
		foreach($structure["jigsaws"] as $jigsaw){
			$rotated = $this->rotateXZ($size[0], $size[2], $jigsaw["x"], $jigsaw["z"], $rotation);
			$copy = $jigsaw;
			$copy["x"] = $rotated[0];
			$copy["z"] = $rotated[1];
			$copy["block_state"] = PnxVillageBlockMapper::rotateState($jigsaw["block_state"], $stateRotation);
			$copy["final_state"] = PnxVillageBlockMapper::rotateState($jigsaw["final_state"], $stateRotation);
			$jigsaws[] = $copy;
		}
		return ["size" => [$newSizeX, $size[1], $newSizeZ], "blocks" => $blocks, "jigsaws" => $jigsaws];
	}

	private function inverseRotation(int $rotation) : int{
		switch($rotation & 3){
			case PnxVillageBlockMapper::ROTATE_90: return PnxVillageBlockMapper::ROTATE_270;
			case PnxVillageBlockMapper::ROTATE_270: return PnxVillageBlockMapper::ROTATE_90;
			default: return $rotation & 3;
		}
	}

	private function rotateXZ(int $sizeX, int $sizeZ, int $x, int $z, int $rotation) : array{
		switch($rotation & 3){
			case PnxVillageBlockMapper::ROTATE_90:
				return [$z, $sizeX - 1 - $x];
			case PnxVillageBlockMapper::ROTATE_180:
				return [$sizeX - 1 - $x, $sizeZ - 1 - $z];
			case PnxVillageBlockMapper::ROTATE_270:
				return [$sizeZ - 1 - $z, $x];
			default:
				return [$x, $z];
		}
	}

	private function findCandidate(string $poolName, array $parentPiece, array $sourceReference, Random $random, array $connected, array $occupied){
		foreach($this->candidateEntries($poolName, $random) as $entry){
			if($this->isEmptyPoolEntry($entry["structure"])){
				return null;
			}
			$structureName = $this->normalizeKey($entry["structure"]);
			$structure = PnxVillageStructureRegistry::get($structureName);
			if(!is_array($structure)){
				continue;
			}
			foreach($this->shuffledRotations($random) as $rotation){
				$connection = $this->resolveConnection($parentPiece, $sourceReference, $structure, $entry["projection"] ?? "rigid", $rotation, $random);
				if($connection === null || isset($connected[$this->posKey($connection["childJigsawWorld"])])){
					continue;
				}
				if($this->overlaps($connection["box"], $occupied, $parentPiece["box"])){
					continue;
				}
				return $connection + ["structureName" => $structureName, "structure" => $structure, "projection" => $entry["projection"] ?? "rigid"];
			}
		}
		return null;
	}

	private function resolveConnection(array $parentPiece, array $sourceReference, array $childStructure, string $childProjection, int $childRotation, Random $random){
		$parentOrientation = $this->jigsawOrientation($sourceReference["source"]["block_state"], $parentPiece["rotation"]);
		$parentJoint = $sourceReference["source"]["joint"] !== "" ? $sourceReference["source"]["joint"] : ($this->isHorizontal($parentOrientation["front"]) ? "aligned" : "rollable");
		$parentWorld = $this->absolutePos($parentPiece["position"], $sourceReference["placed"]);
		$rotatedChild = $this->rotateStructure($childStructure, $childRotation);
		foreach($this->orderedJigsaws($childStructure, $rotatedChild, $random) as $childReference){
			if($this->normalizeKey($childReference["source"]["name"]) !== $this->normalizeKey($sourceReference["source"]["target"])){
				continue;
			}
			$childOrientation = $this->jigsawOrientation($childReference["source"]["block_state"], $childRotation);
			if(!$this->canAttach($parentOrientation, $parentJoint, $sourceReference["source"], $childOrientation, $childReference["source"])){
				continue;
			}
			$childWorld = $this->sidePos($parentWorld, $parentOrientation["front"]);
			$childPos = [
				$childWorld[0] - $childReference["placed"]["x"],
				$childWorld[1] - $childReference["placed"]["y"],
				$childWorld[2] - $childReference["placed"]["z"],
			];
			$childPos = $this->applyProjection($parentPiece, $sourceReference, $childReference, $childPos, $childProjection, $parentOrientation);
			return [
				"rotation" => $childRotation,
				"position" => $childPos,
				"childJigsawWorld" => $childWorld,
				"box" => $this->createBox($childPos, $rotatedChild["size"]),
			];
		}
		return null;
	}

	private function applyProjection(array $parentPiece, array $sourceReference, array $childReference, array $childPos, string $childProjection, array $parentOrientation) : array{
		$sourceRigid = $this->isRigid($parentPiece["projection"]);
		$targetRigid = $this->isRigid($childProjection);
		$deltaY = $sourceReference["placed"]["y"] - $childReference["placed"]["y"] + $this->faceOffset($parentOrientation["front"])[1];
		if($sourceRigid && $targetRigid){
			$childPos[1] = $parentPiece["box"][1] + $deltaY;
			return $childPos;
		}
		$sourceWorld = $this->absolutePos($parentPiece["position"], $sourceReference["placed"]);
		$surfaceY = $this->getPlacementY($sourceWorld[0] + $this->originX, $sourceWorld[2] + $this->originZ);
		$childPos[1] = $surfaceY - $this->originY - $childReference["placed"]["y"];
		return $childPos;
	}

	private function isRigid(string $projection) : bool{
		$normalized = strtolower($this->normalizeKey($projection));
		return $normalized === "" || $normalized === "rigid";
	}

	private function orderedJigsaws(array $sourceStructure, array $placedStructure, Random $random) : array{
		$indices = range(0, max(0, count($sourceStructure["jigsaws"]) - 1));
		for($i = count($indices) - 1; $i > 0; --$i){
			$swap = $this->nextInclusive($random, $i);
			$tmp = $indices[$i];
			$indices[$i] = $indices[$swap];
			$indices[$swap] = $tmp;
		}
		usort($indices, function($a, $b) use ($sourceStructure){
			return (int) ($sourceStructure["jigsaws"][$b]["selection_priority"] ?? 0) <=> (int) ($sourceStructure["jigsaws"][$a]["selection_priority"] ?? 0);
		});
		$result = [];
		foreach($indices as $index){
			if(!isset($sourceStructure["jigsaws"][$index], $placedStructure["jigsaws"][$index])){
				continue;
			}
			$result[] = ["source" => $sourceStructure["jigsaws"][$index], "placed" => $placedStructure["jigsaws"][$index]];
		}
		return $result;
	}

	private function selectPoolEntry(string $poolName, Random $random){
		if(!isset($this->pools[$poolName]) || count($this->pools[$poolName]) === 0){
			return null;
		}
		$total = 0;
		foreach($this->pools[$poolName] as $entry){
			$total += (int) $entry["weight"];
		}
		if($total <= 0){
			return null;
		}
		$target = $random->nextBoundedInt($total);
		foreach($this->pools[$poolName] as $entry){
			if($target < (int) $entry["weight"]){
				return $entry;
			}
			$target -= (int) $entry["weight"];
		}
		return $this->pools[$poolName][0];
	}

	private function candidateEntries(string $poolName, Random $random) : array{
		$entries = [];
		foreach($this->pools[$poolName] ?? [] as $entry){
			for($i = 0; $i < (int) $entry["weight"]; ++$i){
				$entries[] = $entry;
			}
		}
		for($i = count($entries) - 1; $i > 0; --$i){
			$swap = $this->nextInclusive($random, $i);
			$tmp = $entries[$i];
			$entries[$i] = $entries[$swap];
			$entries[$swap] = $tmp;
		}
		return $entries;
	}

	private function shuffledRotations(Random $random) : array{
		$rotations = [PnxVillageBlockMapper::ROTATE_NONE, PnxVillageBlockMapper::ROTATE_90, PnxVillageBlockMapper::ROTATE_180, PnxVillageBlockMapper::ROTATE_270];
		for($i = count($rotations) - 1; $i > 0; --$i){
			$swap = $this->nextInclusive($random, $i);
			$tmp = $rotations[$i];
			$rotations[$i] = $rotations[$swap];
			$rotations[$swap] = $tmp;
		}
		return $rotations;
	}

	private function nextInclusive(Random $random, int $max) : int{
		return $max <= 0 ? 0 : $random->nextBoundedInt($max + 1);
	}

	private function jigsawOrientation(array $state, int $appliedRotation) : array{
		$states = $state["states"] ?? [];
		$front = $this->faceFromIndex((int) ($states["facing_direction"] ?? 2));
		$front = $this->rotateFace($front, $appliedRotation);
		if($this->isHorizontal($front)){
			return ["front" => $front, "top" => "up"];
		}
		$rotation = (int) ($states["rotation"] ?? 0);
		if($front === "down"){
			$top = [1 => "west", 2 => "south", 3 => "east"][$rotation] ?? "north";
		}else{
			$top = [1 => "east", 2 => "south", 3 => "west"][$rotation] ?? "north";
		}
		return ["front" => $front, "top" => $this->rotateFace($top, $appliedRotation)];
	}

	private function canAttach(array $parentOrientation, string $parentJoint, array $parentJigsaw, array $childOrientation, array $childJigsaw) : bool{
		return $parentOrientation["front"] === $this->oppositeFace($childOrientation["front"]) &&
			($parentJoint === "rollable" || $parentOrientation["top"] === $childOrientation["top"]) &&
			$this->normalizeKey($parentJigsaw["target"]) === $this->normalizeKey($childJigsaw["name"]);
	}

	private function postProcessPiece(string $structureName, array &$blocks, array &$jigsaws){
		$this->liftPieceAboveWater($blocks, $jigsaws);
		if(strpos($structureName, "lamp") !== false){
			$this->shiftWholePieceToTerrain($blocks, $jigsaws);
			$offset = $this->lampHeightOffset($structureName);
			if($offset !== 0){
				$this->shiftWholePiece($blocks, $jigsaws, $offset);
			}
			$this->fillSupports($blocks, Block::DIRT, 0);
			foreach($blocks as $key => $block){
				if($block["id"] === Block::AIR){
					unset($blocks[$key]);
				}
			}
			return;
		}
		if(strpos($structureName, "/streets/") !== false || strpos($structureName, "/terminators/") !== false){
			$this->adaptStreetColumnsToTerrain($blocks, $jigsaws);
			return;
		}
		if(strpos($structureName, "/houses/") !== false){
			$this->shiftHousePieceToStreetAlignedTerrain($blocks, $jigsaws);
			$support = $this->usesDirtSupports($structureName) ? [Block::DIRT, 0] : $this->houseSupport($structureName);
			$this->fillSupports($blocks, $support[0], $support[1]);
			return;
		}
		$this->fillSupports($blocks, Block::DIRT, 0);
	}

	private function replaceJigsawBlocks(array &$blocks, array $jigsaws, int $rotation){
		foreach($jigsaws as $jigsaw){
			$key = $this->posKey([$jigsaw["world_x"], $jigsaw["world_y"], $jigsaw["world_z"]]);
			$legacy = PnxVillageBlockMapper::toLegacy($jigsaw["final_state"]);
			if($legacy === null){
				$legacy = [Block::AIR, 0];
			}
			$blocks[$key] = ["x" => $jigsaw["world_x"], "y" => $jigsaw["world_y"], "z" => $jigsaw["world_z"], "id" => $legacy[0], "meta" => $legacy[1], "state" => $jigsaw["final_state"]];
		}
	}

	private function flushBlocks(array $blocks, string $structureName){
		foreach($blocks as $block){
			$y = (int) $block["y"];
			if($y < 0 || $y > 127){
				continue;
			}
			$this->level->setBlockIdAt((int) $block["x"], $y, (int) $block["z"], (int) $block["id"]);
			$this->level->setBlockDataAt((int) $block["x"], $y, (int) $block["z"], (int) $block["meta"]);
			if((int) $block["id"] === Block::CHEST && VillageSmithyChestLoot::hasLootForStructureName($structureName)){
				$this->markExtraWorld(
					(int) $block["x"],
					$y,
					(int) $block["z"],
					VillageSmithyChestLoot::markerIdForStructureName($structureName),
					VillageSmithyChestLoot::markerDataForStructureName($structureName),
					VillageSmithyChestLoot::markerForStructureName($structureName)
				);
			}elseif((int) $block["id"] === Block::LADDER){
				$this->markExtraWorld((int) $block["x"], $y, (int) $block["z"], 0x44, 0x4c, 0x4c44);
			}
		}
	}

	private function markEntityPiece(string $structureName, array $jigsaws, array $blocks){
		if(strpos($structureName, "/villagers/") !== false){
			$pos = $this->bestMarkerPosition($jigsaws, $blocks, 2);
			if($pos !== null && $this->storeMobMarker($pos[0], $pos[1], $pos[2], $this->villagerMarker)){
				++$this->villagerCount;
			}
		}
		if(strpos($structureName, "iron_golem") !== false){
			$pos = $this->bestMarkerPosition($jigsaws, $blocks, 3);
			if($pos !== null && $this->storeMobMarker($pos[0], $pos[1], $pos[2], $this->golemMarker)){
				++$this->golemCount;
			}
		}
	}

	private function bestMarkerPosition(array $jigsaws, array $blocks, int $height){
		if(count($jigsaws) > 0){
			$j = $jigsaws[0];
			return [$j["world_x"], $this->findSafeSpawnY($j["world_x"], $j["world_y"], $j["world_z"], $height), $j["world_z"]];
		}
		foreach($blocks as $block){
			if($block["id"] !== Block::AIR){
				return [$block["x"], $this->findSafeSpawnY($block["x"], $block["y"] + 1, $block["z"], $height), $block["z"]];
			}
		}
		return null;
	}

	private function findSafeSpawnY(int $x, int $startY, int $z, int $height) : int{
		for($y = $startY; $y <= min(127, $startY + 16); ++$y){
			$clear = true;
			for($dy = 0; $dy < $height; ++$dy){
				if(!$this->isPassable($this->level->getBlockIdAt($x, $y + $dy, $z))){
					$clear = false;
					break;
				}
			}
			if($clear){
				return $y;
			}
		}
		return $startY;
	}

	private function liftPieceAboveWater(array &$blocks, array &$jigsaws){
		$lowest = $this->lowestSolidY($blocks);
		if($lowest === null){
			return;
		}
		$columns = $this->lowestColumns($blocks, $lowest);
		$delta = 0;
		foreach($columns as $key => $_){
			list($x, $z) = array_map("intval", explode(":", $key));
			$height = $this->getSurfaceY($x, $z);
			$id = $this->level->getBlockIdAt($x, $height, $z);
			if($id === Block::WATER || $id === Block::STILL_WATER){
				$delta = max($delta, $height + 1 - $lowest);
			}
		}
		if($delta > 0){
			$this->shiftWholePiece($blocks, $jigsaws, $delta);
		}
	}

	private function shiftWholePieceToTerrain(array &$blocks, array &$jigsaws){
		$anchor = null;
		foreach($blocks as $block){
			if($block["id"] === Block::AIR){
				continue;
			}
			if($anchor === null || $block["y"] < $anchor["y"]){
				$anchor = $block;
			}
		}
		if($anchor === null){
			return;
		}
		$delta = $this->getPlacementY($anchor["x"], $anchor["z"]) - $anchor["y"];
		if($delta !== 0){
			$this->shiftWholePiece($blocks, $jigsaws, $delta);
		}
	}

	private function shiftHousePieceToStreetAlignedTerrain(array &$blocks, array &$jigsaws){
		$counts = [];
		foreach($jigsaws as $jigsaw){
			$pool = $jigsaw["pool"] ?? "";
			if(strpos($pool, "/streets") === false && strpos($pool, "/zombie/streets") === false){
				continue;
			}
			$delta = $this->getPlacementY($jigsaw["world_x"], $jigsaw["world_z"]) - $jigsaw["world_y"];
			$counts[$delta] = ($counts[$delta] ?? 0) + 1;
		}
		if(count($counts) > 0){
			arsort($counts);
			$delta = (int) array_key_first($counts);
			if($delta !== 0){
				$this->shiftWholePiece($blocks, $jigsaws, $delta);
			}
			return;
		}
		$this->shiftWholePieceToTerrain($blocks, $jigsaws);
	}

	private function shiftWholePiece(array &$blocks, array &$jigsaws, int $deltaY){
		$shifted = [];
		foreach($blocks as $block){
			$block["y"] += $deltaY;
			$shifted[$this->posKey([$block["x"], $block["y"], $block["z"]])] = $block;
		}
		$blocks = $shifted;
		foreach($jigsaws as &$jigsaw){
			$jigsaw["y"] += $deltaY;
			$jigsaw["world_y"] += $deltaY;
		}
		unset($jigsaw);
	}

	private function adaptStreetColumnsToTerrain(array &$blocks, array &$jigsaws){
		$columnHeights = [];
		$adapted = [];
		foreach($blocks as $block){
			$height = $this->getPlacementY($block["x"], $block["z"]) - 1;
			$columnHeights[$block["x"] . ":" . $block["z"]] = $height;
			if($this->isStreet($block["id"])){
				$adapted[$this->posKey([$block["x"], $height + 1, $block["z"]])] = ["x" => $block["x"], "y" => $height + 1, "z" => $block["z"], "id" => Block::AIR, "meta" => 0, "state" => ["name" => "minecraft:air", "states" => []]];
				$adapted[$this->posKey([$block["x"], $height + 2, $block["z"]])] = ["x" => $block["x"], "y" => $height + 2, "z" => $block["z"], "id" => Block::AIR, "meta" => 0, "state" => ["name" => "minecraft:air", "states" => []]];
				$block["y"] = $height;
				$adapted[$this->posKey([$block["x"], $block["y"], $block["z"]])] = $block;
			}
		}
		$blocks = $adapted;
		foreach($jigsaws as &$jigsaw){
			$key = $jigsaw["world_x"] . ":" . $jigsaw["world_z"];
			$height = $columnHeights[$key] ?? ($this->getPlacementY($jigsaw["world_x"], $jigsaw["world_z"]) - 1);
			$jigsaw["world_y"] = $height + 1;
		}
		unset($jigsaw);
	}

	private function fillSupports(array &$blocks, int $supportId, int $supportMeta){
		$lowest = $this->lowestSolidY($blocks);
		if($lowest === null){
			return;
		}
		foreach($this->lowestColumns($blocks, $lowest) as $key => $_){
			list($x, $z) = array_map("intval", explode(":", $key));
			for($y = $lowest - 1; $y >= 0; --$y){
				$id = $this->level->getBlockIdAt($x, $y, $z);
				if(!$this->isReplaceableSupport($id)){
					break;
				}
				$blocks[$this->posKey([$x, $y, $z])] = ["x" => $x, "y" => $y, "z" => $z, "id" => $supportId, "meta" => $supportMeta, "state" => ["name" => "support", "states" => []]];
			}
		}
	}

	private function lowestSolidY(array $blocks){
		$lowest = null;
		foreach($blocks as $block){
			if($block["id"] === Block::AIR){
				continue;
			}
			if($lowest === null || $block["y"] < $lowest){
				$lowest = $block["y"];
			}
		}
		return $lowest;
	}

	private function lowestColumns(array $blocks, int $lowest) : array{
		$columns = [];
		foreach($blocks as $block){
			if($block["id"] !== Block::AIR && $block["y"] === $lowest){
				$columns[$block["x"] . ":" . $block["z"]] = true;
			}
		}
		return $columns;
	}

	private function usesDirtSupports(string $structureName) : bool{
		return strpos($structureName, "_farm_") !== false ||
			strpos($structureName, "_stable_") !== false ||
			strpos($structureName, "_animal_pen_") !== false ||
			strpos($structureName, "_accessory_") !== false;
	}

	private function houseSupport(string $structureName) : array{
		return strpos($structureName, "desert/") !== false ? [Block::SANDSTONE, 0] : [Block::COBBLESTONE, 0];
	}

	private function lampHeightOffset(string $structureName) : int{
		if(strpos($structureName, "desert_lamp") !== false || strpos($structureName, "savanna_lamp_post") !== false || strpos($structureName, "taiga_lamp_post") !== false){
			return 1;
		}
		return 0;
	}

	private function getPlacementY(int $x, int $z) : int{
		$height = $this->getSurfaceY($x, $z);
		$id = $this->level->getBlockIdAt($x, $height, $z);
		if($id === Block::WATER || $id === Block::STILL_WATER){
			return $height + 1;
		}
		while($height > 0 && ($this->isReplaceableTerrainCover($this->level->getBlockIdAt($x, $height, $z)) || $this->isPassable($this->level->getBlockIdAt($x, $height, $z)))){
			--$height;
		}
		return $height + 1;
	}

	private function getSurfaceY(int $x, int $z) : int{
		$cacheKey = $x . ":" . $z;
		if(isset($this->surfaceYCache[$cacheKey])){
			return $this->surfaceYCache[$cacheKey];
		}
		for($y = $this->getSurfaceScanStartY($x, $z); $y >= 0; --$y){
			if(!$this->isIgnoredSurface($this->level->getBlockIdAt($x, $y, $z))){
				$this->surfaceYCache[$cacheKey] = $y;
				return $y;
			}
		}
		$this->surfaceYCache[$cacheKey] = 0;
		return 0;
	}

	private function getSurfaceScanStartY(int $x, int $z) : int{
		$chunk = $this->level->getChunk($x >> 4, $z >> 4);
		if($chunk !== null && method_exists($chunk, "getHeightMap")){
			$height = (int) $chunk->getHeightMap($x & 0x0f, $z & 0x0f);
			if($height > 0 && $height <= 127){
				return $height;
			}
		}
		if(method_exists($this->level, "getHeightMap")){
			$height = (int) $this->level->getHeightMap($x, $z);
			if($height > 0 && $height <= 127){
				return $height;
			}
		}

		return 127;
	}

	private function isIgnoredSurface(int $id) : bool{
		return $id === Block::AIR || $id === Block::LEAVES || $id === PnxVillageBlockMapper::blockId("LEAVES2", 161) || $id === Block::TALL_GRASS ||
			$id === Block::RED_FLOWER || $id === Block::DANDELION || $id === Block::DOUBLE_PLANT || $id === Block::SNOW_LAYER ||
			$id === Block::WATER_LILY || $id === Block::WATER || $id === Block::STILL_WATER;
	}

	private function isPassable(int $id) : bool{
		return $id === Block::AIR || $id === Block::TALL_GRASS || $id === Block::RED_FLOWER || $id === Block::DANDELION ||
			$id === Block::DOUBLE_PLANT || $id === Block::SNOW_LAYER;
	}

	private function isReplaceableTerrainCover(int $id) : bool{
		return $id === PnxVillageBlockMapper::blockId("SNOW", 78) || $id === Block::SNOW_LAYER;
	}

	private function isReplaceableSupport(int $id) : bool{
		return $id === Block::AIR || $id === Block::WATER || $id === Block::STILL_WATER || $id === Block::TALL_GRASS ||
			$id === Block::DOUBLE_PLANT || $id === Block::SNOW_LAYER || $id === Block::LEAVES || $id === PnxVillageBlockMapper::blockId("LEAVES2", 161);
	}

	private function isStreet(int $id) : bool{
		return $id === Block::GRASS_PATH || $id === Block::GRASS;
	}

	private function createBox(array $position, array $size) : array{
		return [$position[0], $position[1], $position[2], $position[0] + $size[0] - 1, $position[1] + $size[1] - 1, $position[2] + $size[2] - 1];
	}

	private function setPopulationBounds(int $centerChunkX, int $centerChunkZ, int $radius){
		if($radius <= 0){
			$this->minChunkX = null;
			$this->maxChunkX = null;
			$this->minChunkZ = null;
			$this->maxChunkZ = null;
			return;
		}
		$this->minChunkX = $centerChunkX - $radius;
		$this->maxChunkX = $centerChunkX + $radius;
		$this->minChunkZ = $centerChunkZ - $radius;
		$this->maxChunkZ = $centerChunkZ + $radius;
	}

	private function isWithinPopulationBounds(array $box) : bool{
		if($this->minChunkX === null || $this->maxChunkX === null || $this->minChunkZ === null || $this->maxChunkZ === null){
			return true;
		}
		$minChunkX = ($this->originX + (int) $box[0]) >> 4;
		$maxChunkX = ($this->originX + (int) $box[3]) >> 4;
		$minChunkZ = ($this->originZ + (int) $box[2]) >> 4;
		$maxChunkZ = ($this->originZ + (int) $box[5]) >> 4;

		return $minChunkX >= $this->minChunkX &&
			$maxChunkX <= $this->maxChunkX &&
			$minChunkZ >= $this->minChunkZ &&
			$maxChunkZ <= $this->maxChunkZ;
	}

	private function overlaps(array $box, array $occupied, array $ignored = null) : bool{
		foreach($occupied as $other){
			if($ignored !== null && $other === $ignored){
				continue;
			}
			if($other[3] > $box[0] && $other[0] < $box[3] && $other[4] > $box[1] && $other[1] < $box[4] && $other[5] > $box[2] && $other[2] < $box[5]){
				return true;
			}
		}
		return false;
	}

	private function absolutePos(array $structurePosition, array $jigsaw) : array{
		return [$structurePosition[0] + $jigsaw["x"], $structurePosition[1] + $jigsaw["y"], $structurePosition[2] + $jigsaw["z"]];
	}

	private function sidePos(array $pos, string $face) : array{
		$offset = $this->faceOffset($face);
		return [$pos[0] + $offset[0], $pos[1] + $offset[1], $pos[2] + $offset[2]];
	}

	private function faceOffset(string $face) : array{
		switch($face){
			case "down": return [0, -1, 0];
			case "up": return [0, 1, 0];
			case "north": return [0, 0, -1];
			case "south": return [0, 0, 1];
			case "west": return [-1, 0, 0];
			case "east": return [1, 0, 0];
		}
		return [0, 0, 0];
	}

	private function faceFromIndex(int $index) : string{
		switch($index){
			case 0: return "down";
			case 1: return "up";
			case 2: return "north";
			case 3: return "south";
			case 4: return "west";
			case 5: return "east";
		}
		return "north";
	}

	private function rotateFace(string $face, int $rotation) : string{
		for($i = 0; $i < ($rotation & 3); ++$i){
			switch($face){
				case "north": $face = "west"; break;
				case "west": $face = "south"; break;
				case "south": $face = "east"; break;
				case "east": $face = "north"; break;
			}
		}
		return $face;
	}

	private function oppositeFace(string $face) : string{
		switch($face){
			case "down": return "up";
			case "up": return "down";
			case "north": return "south";
			case "south": return "north";
			case "west": return "east";
			case "east": return "west";
		}
		return $face;
	}

	private function isHorizontal(string $face) : bool{
		return $face === "north" || $face === "south" || $face === "east" || $face === "west";
	}

	private function normalizeKey(string $key) : string{
		$pos = strpos($key, ":");
		return $pos === false ? $key : substr($key, $pos + 1);
	}

	private function isEmptyPoolEntry(string $key) : bool{
		$normalized = $this->normalizeKey($key);
		return $normalized === "" || $normalized === "empty";
	}

	private function insertPending(array &$queue, array $pending){
		$index = count($queue);
		for($i = 0; $i < count($queue); ++$i){
			if($pending["priority"] > $queue[$i]["priority"]){
				$index = $i;
				break;
			}
		}
		array_splice($queue, $index, 0, [$pending]);
	}

	private function posKey(array $pos) : string{
		return (int) $pos[0] . ":" . (int) $pos[1] . ":" . (int) $pos[2];
	}

	private function markExtraWorld(int $x, int $y, int $z, int $id, int $data, int $marker){
		if(method_exists($this->level, "setBlockExtraDataAt")){
			$this->level->setBlockExtraDataAt($x, $y, $z, $id, $data);
		}
		$chunk = $this->level->getChunk($x >> 4, $z >> 4);
		if($chunk !== null && method_exists($chunk, "setBlockExtraData")){
			$chunk->setBlockExtraData($x & 0x0f, $y & 0x7f, $z & 0x0f, $marker);
		}
	}

	private function storeMobMarker(int $x, int $y, int $z, int $marker) : bool{
		$chunk = $this->level->getChunk($x >> 4, $z >> 4);
		if($chunk === null || !method_exists($chunk, "setBlockExtraData")){
			return false;
		}
		$chunk->setBlockExtraData($x & 0x0f, $y & 0x7f, $z & 0x0f, $marker);
		return true;
	}
}
