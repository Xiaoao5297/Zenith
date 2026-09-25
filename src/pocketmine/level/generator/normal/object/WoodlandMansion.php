<?php

namespace pocketmine\level\generator\normal\object;

require_once __DIR__ . "/PnxVillageStructure.php";
require_once __DIR__ . "/WoodlandMansionLoot.php";
require_once dirname(__DIR__, 2) . "/object/PopulatorObject.php";

use pocketmine\block\Block;
use pocketmine\level\ChunkManager;
use pocketmine\level\generator\object\PopulatorObject;
use pocketmine\nbt\NBT;
use pocketmine\nbt\tag\ByteArrayTag;
use pocketmine\nbt\tag\ByteTag;
use pocketmine\nbt\tag\CompoundTag;
use pocketmine\nbt\tag\IntArrayTag;
use pocketmine\nbt\tag\IntTag;
use pocketmine\nbt\tag\ListTag;
use pocketmine\nbt\tag\StringTag;
use pocketmine\utils\Random;

final class WoodlandMansionStructureRegistry{
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

	public static function all() : array{
		self::load();
		return self::$structures;
	}

	private static function load(){
		if(self::$structures !== null){
			return;
		}
		self::$blockPalette = self::loadBlockPalette();
		self::$structures = [];
		$root = self::readCompressedNbt(self::resourcePath("gamedata/unknown/structures.nbt"));
		$mansion = $root->woodland_mansion ?? null;
		if(!$mansion instanceof CompoundTag){
			throw new \RuntimeException("PNX woodland_mansion structures are missing");
		}

		foreach($mansion as $name => $container){
			if(!$container instanceof CompoundTag || !($container->object ?? null) instanceof CompoundTag){
				continue;
			}
			self::$structures[(string) $name] = self::decodeStructure($container->object);
		}
	}

	private static function decodeStructure(CompoundTag $object) : array{
		$sizeTag = $object->size ?? null;
		$paletteTag = $object->palette ?? null;
		$blocksTag = $object->blocks ?? null;
		if(!$sizeTag instanceof IntArrayTag || !$paletteTag instanceof ListTag || !$blocksTag instanceof ByteArrayTag){
			throw new \RuntimeException("Invalid PNX woodland mansion structure object");
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
		$raw = $blocksTag->getValue();
		for($index = 0, $len = strlen($raw); $index < $len; ++$index){
			$paletteIndex = ord($raw[$index]) - 1;
			if($paletteIndex < 0 || !isset($palette[$paletteIndex])){
				continue;
			}
			$blocks[] = [
				"x" => $index % $sizeX,
				"y" => intdiv($index, $sizeX) % $sizeY,
				"z" => intdiv($index, $sizeX * $sizeY),
				"state" => $palette[$paletteIndex],
			];
		}

		return [
			"size" => [$sizeX, $sizeY, (int) ($size[2] ?? 0)],
			"blocks" => $blocks,
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
			throw new \RuntimeException("Unable to read PNX woodland mansion resource: " . $path);
		}
		$raw = gzdecode($bytes);
		if($raw === false){
			throw new \RuntimeException("Unable to decompress PNX woodland mansion resource: " . $path);
		}
		$nbt = new NBT(NBT::BIG_ENDIAN);
		$nbt->read($raw);
		$data = $nbt->getData();
		if(!$data instanceof CompoundTag){
			throw new \RuntimeException("Unexpected PNX woodland mansion NBT root");
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

class WoodlandMansion extends PopulatorObject{
	const ROTATE_NONE = PnxVillageBlockMapper::ROTATE_NONE;
	const ROTATE_90 = PnxVillageBlockMapper::ROTATE_90;
	const ROTATE_180 = PnxVillageBlockMapper::ROTATE_180;
	const ROTATE_270 = PnxVillageBlockMapper::ROTATE_270;

	const MIRROR_NONE = 0;
	const MIRROR_LEFT_RIGHT = 1;
	const MIRROR_FRONT_BACK = 2;

	const FACE_NORTH = "north";
	const FACE_SOUTH = "south";
	const FACE_WEST = "west";
	const FACE_EAST = "east";
	const FACE_UP = "up";

	const SPAWNER_MARKER_SPIDER = 7;
	const MARKER_EVOKER = 0x5745;
	const MARKER_EVOKER_ID = 0x45;
	const MARKER_EVOKER_DATA = 0x57;
	const MARKER_VINDICATOR = 0x5756;
	const MARKER_VINDICATOR_ID = 0x56;
	const MARKER_VINDICATOR_DATA = 0x57;
	const MARKER_ALLAY = 0x5741;
	const MARKER_ALLAY_ID = 0x41;
	const MARKER_ALLAY_DATA = 0x57;

	/** @var ChunkManager */
	private $level;

	public static function generatePieces(array $origin, int $rotation, Random $random) : array{
		$grid = new WoodlandMansionGrid($random);
		$placer = new WoodlandMansionPiecePlacer($random);
		$pieces = [];
		$placer->createMansion($origin, $rotation, $pieces, $grid);
		return array_map(function(WoodlandMansionPiece $piece) : array{
			return $piece->toArray();
		}, $pieces);
	}

	public static function getUnmappedBlockNames() : array{
		$unmapped = [];
		foreach(WoodlandMansionStructureRegistry::all() as $structure){
			foreach($structure["blocks"] as $block){
				$state = $block["state"];
				if(self::isSkippableState($state)){
					continue;
				}
				if(self::toLegacy($state, self::ROTATE_NONE, self::MIRROR_NONE) === null){
					$unmapped[$state["name"] ?? ""] = true;
				}
			}
		}
		$names = array_keys($unmapped);
		sort($names);
		return $names;
	}

	public function placeObject(ChunkManager $level, $x, $y, $z, Random $random){
		$this->level = $level;
		$origin = [(int) $x, (int) $y, (int) $z];
		$grid = new WoodlandMansionGrid($random);
		$placer = new WoodlandMansionPiecePlacer($random);
		$pieces = [];
		$placer->createMansion($origin, self::ROTATE_NONE, $pieces, $grid);

		$chests = [];
		$markers = [];
		$spawners = [];
		foreach($pieces as $piece){
			$piece->place($level, $chests, $markers, $spawners);
		}
		$this->afterPlaceFoundationFill($level, $pieces);

		foreach($chests as $pos){
			$this->markExtraWorld($pos[0], $pos[1], $pos[2], WoodlandMansionLoot::CHEST_MARKER_ID, WoodlandMansionLoot::CHEST_MARKER_DATA, WoodlandMansionLoot::CHEST_MARKER);
		}
		foreach($spawners as $pos){
			$level->setBlockDataAt($pos[0], $pos[1], $pos[2], self::SPAWNER_MARKER_SPIDER);
		}
		foreach($this->collectMobSpawnsFromTemplates($pieces, $markers, $random) as $spawn){
			$marker = $this->mobMarker($spawn["type"]);
			if($marker !== null){
				$this->markExtraWorld($spawn["pos"][0], $spawn["pos"][1], $spawn["pos"][2], $marker[0], $marker[1], $marker[2]);
			}
		}

		$bounds = $this->calculateBounds($pieces);
		return [
			"originX" => $origin[0],
			"originY" => $origin[1],
			"originZ" => $origin[2],
			"targetX" => $origin[0],
			"targetY" => min(126, $origin[1] + 1),
			"targetZ" => $origin[2],
			"minX" => $bounds[0],
			"minY" => $bounds[1],
			"minZ" => $bounds[2],
			"maxX" => $bounds[3],
			"maxY" => $bounds[4],
			"maxZ" => $bounds[5],
		];
	}

	private function afterPlaceFoundationFill(ChunkManager $level, array $pieces){
		if(empty($pieces)){
			return;
		}
		$bounds = $this->calculateBounds($pieces);
		$yStart = $bounds[1];
		for($x = $bounds[0]; $x <= $bounds[3]; ++$x){
			for($z = $bounds[2]; $z <= $bounds[5]; ++$z){
				if(!$this->isInsideAnyPiece($pieces, $x, $yStart, $z)){
					continue;
				}
				if($level->getBlockIdAt($x, $yStart, $z) === Block::AIR){
					continue;
				}
				for($y = $yStart - 1; $y > 1; --$y){
					$id = $level->getBlockIdAt($x, $y, $z);
					if(!self::isAirOrLiquid($id)){
						break;
					}
					$level->setBlockIdAt($x, $y, $z, Block::COBBLESTONE);
					$level->setBlockDataAt($x, $y, $z, 0);
				}
			}
		}
	}

	private function calculateBounds(array $pieces) : array{
		$first = $pieces[0]->getBoundingBox();
		$bounds = $first;
		for($i = 1, $count = count($pieces); $i < $count; ++$i){
			$bb = $pieces[$i]->getBoundingBox();
			$bounds[0] = min($bounds[0], $bb[0]);
			$bounds[1] = min($bounds[1], $bb[1]);
			$bounds[2] = min($bounds[2], $bb[2]);
			$bounds[3] = max($bounds[3], $bb[3]);
			$bounds[4] = max($bounds[4], $bb[4]);
			$bounds[5] = max($bounds[5], $bb[5]);
		}
		return $bounds;
	}

	private function isInsideAnyPiece(array $pieces, int $x, int $y, int $z) : bool{
		foreach($pieces as $piece){
			if($piece->contains($x, $y, $z)){
				return true;
			}
		}
		return false;
	}

	private function collectMobSpawnsFromTemplates(array $pieces, array $markers, Random $random) : array{
		$result = [];
		$available = $markers;
		foreach($pieces as $piece){
			$plan = self::getMobSpawnPlan($piece->getTemplateName());
			if($plan === null){
				continue;
			}
			$pieceMarkers = [];
			for($i = count($available) - 1; $i >= 0; --$i){
				$marker = $available[$i];
				if($piece->contains($marker[0], $marker[1], $marker[2])){
					$pieceMarkers[] = $marker;
					array_splice($available, $i, 1);
				}
			}
			$index = 0;
			for($i = 0; $i < $plan["evoker"]; ++$i){
				$pos = $index < count($pieceMarkers) ? $pieceMarkers[$index++] : $this->fallbackSpawnPos($piece, $random, $i);
				$result[] = ["type" => "evoker", "pos" => $pos];
			}
			for($i = 0; $i < $plan["vindicator"]; ++$i){
				$pos = $index < count($pieceMarkers) ? $pieceMarkers[$index++] : $this->fallbackSpawnPos($piece, $random, $i + 7);
				$result[] = ["type" => "vindicator", "pos" => $pos];
			}
			$allayCount = 0;
			if($plan["allay"] > 0 && $random->nextBoolean()){
				$allayCount = 1 + $random->nextBoundedInt($plan["allay"]);
			}
			for($i = 0; $i < $allayCount; ++$i){
				$pos = $index < count($pieceMarkers) ? $pieceMarkers[$index++] : $this->fallbackSpawnPos($piece, $random, $i + 17);
				$result[] = ["type" => "allay", "pos" => $pos];
			}
		}
		return $result;
	}

	private function fallbackSpawnPos(WoodlandMansionPiece $piece, Random $random, int $salt) : array{
		$bb = $piece->getBoundingBox();
		$spanX = max(1, $bb[3] - $bb[0] + 1);
		$spanZ = max(1, $bb[5] - $bb[2] + 1);
		return [
			$bb[0] + self::floorMod($salt + $random->nextBoundedInt(3), $spanX),
			$bb[1] + 1,
			$bb[2] + self::floorMod($salt * 3 + $random->nextBoundedInt(3), $spanZ),
		];
	}

	private static function getMobSpawnPlan(string $templateName){
		if(in_array($templateName, ["1x2_a1", "1x2_a3", "1x2_a8", "1x2_a9", "1x2_b1", "1x2_b2", "1x2_b3", "2x2_a2"], true)){
			return ["evoker" => 0, "vindicator" => 1, "allay" => 0];
		}
		if(in_array($templateName, ["1x2_a2", "1x2_d3", "2x2_b1", "2x2_b2", "2x2_b4"], true)){
			return ["evoker" => 1, "vindicator" => 2, "allay" => 0];
		}
		if($templateName === "2x2_a1"){
			return ["evoker" => 0, "vindicator" => 1, "allay" => 3];
		}
		return null;
	}

	private function mobMarker(string $type){
		switch($type){
			case "evoker":
				return [self::MARKER_EVOKER_ID, self::MARKER_EVOKER_DATA, self::MARKER_EVOKER];
			case "vindicator":
				return [self::MARKER_VINDICATOR_ID, self::MARKER_VINDICATOR_DATA, self::MARKER_VINDICATOR];
			case "allay":
				return [self::MARKER_ALLAY_ID, self::MARKER_ALLAY_DATA, self::MARKER_ALLAY];
		}
		return null;
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

	public static function toLegacy(array $state, int $rotation = self::ROTATE_NONE, int $mirror = self::MIRROR_NONE){
		if(self::isSkippableState($state)){
			return null;
		}
		$state = self::mirrorState($state, $mirror);
		$legacy = PnxVillageBlockMapper::toLegacy($state, $rotation);
		if($legacy !== null){
			return $legacy;
		}

		$state = self::rotateMansionState($state, $rotation);
		$name = $state["name"] ?? "minecraft:air";
		$states = $state["states"] ?? [];
		switch($name){
			case "minecraft:structure_void":
				return null;
			case "minecraft:glass":
				return [Block::GLASS, 0];
			case "minecraft:obsidian":
				return [Block::OBSIDIAN, 0];
			case "minecraft:diamond_block":
				return [Block::DIAMOND_BLOCK, 0];
			case "minecraft:lapis_block":
				return [Block::LAPIS_BLOCK, 0];
			case "minecraft:web":
				return [self::blockId("WEB", 30), 0];
			case "minecraft:brown_mushroom":
				return [Block::BROWN_MUSHROOM, 0];
			case "minecraft:red_mushroom":
				return [Block::RED_MUSHROOM, 0];
			case "minecraft:waterlily":
				return [Block::LILY_PAD, 0];
			case "minecraft:infested_cobblestone":
				return [Block::COBBLESTONE, 0];
			case "minecraft:polished_andesite":
				return [Block::STONE, 6];
			case "minecraft:smooth_stone_slab":
				return [Block::SLAB, 0 | self::slabTopBit($states)];
			case "minecraft:cobblestone_slab":
				return [Block::SLAB, 3 | self::slabTopBit($states)];
			case "minecraft:stone_stairs":
				return [Block::COBBLESTONE_STAIRS, self::stairMeta($states)];
			case "minecraft:smooth_stone_double_slab":
				return [Block::DOUBLE_SLAB, 0];
			case "minecraft:vine":
				return [Block::VINE, (int) ($states["vine_direction_bits"] ?? 0)];
			case "minecraft:rail":
				return [Block::RAIL, self::railMeta($states["rail_direction"] ?? 0)];
			case "minecraft:redstone_wire":
				return [Block::REDSTONE_WIRE, min(15, (int) ($states["redstone_signal"] ?? 0))];
			case "minecraft:lever":
				return [Block::LEVER, self::leverMeta($states)];
			case "minecraft:damaged_anvil":
				return [Block::ANVIL, 8 | (self::doorFacingMeta($states["minecraft:cardinal_direction"] ?? "south") & 0x03)];
			case "minecraft:iron_door":
				return [Block::IRON_DOOR_BLOCK, self::ironDoorMeta($states)];
			case "minecraft:trapped_chest":
				return [Block::CHEST, self::cardinalMeta($states["minecraft:cardinal_direction"] ?? "south")];
			case "minecraft:mob_spawner":
				return [Block::MONSTER_SPAWNER, self::SPAWNER_MARKER_SPIDER];
		}
		return null;
	}

	public static function isSkippableState(array $state) : bool{
		$name = $state["name"] ?? "";
		return $name === "minecraft:structure_block" ||
			$name === "minecraft:structure_void" ||
			$name === "minecraft:wall_banner" ||
			$name === "minecraft:jigsaw";
	}

	public static function mirrorState(array $state, int $mirror) : array{
		if($mirror === self::MIRROR_NONE){
			return $state;
		}
		$states = $state["states"] ?? [];
		foreach(["minecraft:cardinal_direction", "torch_facing_direction", "lever_direction"] as $key){
			if(isset($states[$key])){
				$states[$key] = self::mirrorCardinal((string) $states[$key], $mirror);
			}
		}
		if(isset($states["facing_direction"])){
			$states["facing_direction"] = self::mirrorFacingDirection((int) $states["facing_direction"], $mirror);
		}
		if(isset($states["weirdo_direction"])){
			$states["weirdo_direction"] = self::mirrorWeirdoDirection((int) $states["weirdo_direction"], $mirror);
		}
		if(isset($states["rail_direction"])){
			$states["rail_direction"] = self::mirrorRailDirection((int) $states["rail_direction"], $mirror);
		}
		if(isset($states["direction"])){
			$states["direction"] = self::mirrorTrapdoorDirection((int) $states["direction"], $mirror);
		}
		if(isset($states["wall_post_bit"])){
			$north = $states["wall_connection_type_north"] ?? "none";
			$east = $states["wall_connection_type_east"] ?? "none";
			$south = $states["wall_connection_type_south"] ?? "none";
			$west = $states["wall_connection_type_west"] ?? "none";
			if($mirror === self::MIRROR_FRONT_BACK){
				$states["wall_connection_type_east"] = $west;
				$states["wall_connection_type_west"] = $east;
			}else{
				$states["wall_connection_type_north"] = $south;
				$states["wall_connection_type_south"] = $north;
			}
		}
		$state["states"] = $states;
		return $state;
	}

	public static function rotateMansionState(array $state, int $rotation) : array{
		$state = PnxVillageBlockMapper::rotateState($state, $rotation);
		$rotation &= 3;
		if($rotation === self::ROTATE_NONE){
			return $state;
		}

		$states = $state["states"] ?? [];
		for($i = 0; $i < $rotation; ++$i){
			if(isset($states["lever_direction"])){
				$states["lever_direction"] = self::rotateFaceClockwise((string) $states["lever_direction"]);
			}
			if(isset($states["rail_direction"])){
				$states["rail_direction"] = self::rotateRailDirectionClockwise((int) $states["rail_direction"]);
			}
		}
		$state["states"] = $states;
		return $state;
	}

	public static function horizontalFaces() : array{
		return [self::FACE_NORTH, self::FACE_EAST, self::FACE_SOUTH, self::FACE_WEST];
	}

	public static function rotateBy(int $rotation, int $add) : int{
		return ($rotation + $add) & 3;
	}

	public static function rotateFace(string $face, int $rotation) : string{
		if($face === self::FACE_UP){
			return $face;
		}
		for($i = 0; $i < ($rotation & 3); ++$i){
			$face = self::rotateFaceClockwise($face);
		}
		return $face;
	}

	public static function relative(array $pos, int $rotation, string $face, int $distance) : array{
		$offset = self::faceOffset(self::rotateFace($face, $rotation));
		return [$pos[0] + $offset[0] * $distance, $pos[1] + $offset[1] * $distance, $pos[2] + $offset[2] * $distance];
	}

	public static function up(array $pos, int $amount = 1) : array{
		return [$pos[0], $pos[1] + $amount, $pos[2]];
	}

	public static function add(array $pos, int $x, int $y, int $z) : array{
		return [$pos[0] + $x, $pos[1] + $y, $pos[2] + $z];
	}

	public static function rotateOffset(array $pos, int $rotation) : array{
		switch($rotation & 3){
			case self::ROTATE_90:
				return [-$pos[2], $pos[1], $pos[0]];
			case self::ROTATE_180:
				return [-$pos[0], $pos[1], -$pos[2]];
			case self::ROTATE_270:
				return [$pos[2], $pos[1], -$pos[0]];
			default:
				return $pos;
		}
	}

	public static function getZeroPositionWithTransform(array $zeroPos, int $mirror, int $rotation, int $sizeX, int $sizeZ) : array{
		$x = $zeroPos[0];
		$y = $zeroPos[1];
		$z = $zeroPos[2];
		$maxX = $sizeX - 1;
		$maxZ = $sizeZ - 1;
		$mirrorDeltaX = $mirror === self::MIRROR_FRONT_BACK ? $maxX : 0;
		$mirrorDeltaZ = $mirror === self::MIRROR_LEFT_RIGHT ? $maxZ : 0;
		switch($rotation & 3){
			case self::ROTATE_270:
				return [$x + $mirrorDeltaZ, $y, $z + $maxX - $mirrorDeltaX];
			case self::ROTATE_90:
				return [$x + $maxZ - $mirrorDeltaZ, $y, $z + $mirrorDeltaX];
			case self::ROTATE_180:
				return [$x + $maxX - $mirrorDeltaX, $y, $z + $maxZ - $mirrorDeltaZ];
			default:
				return [$x + $mirrorDeltaX, $y, $z + $mirrorDeltaZ];
		}
	}

	public static function transformLocalVanilla(int $x, int $z, int $mirror, int $rotation) : array{
		$tx = $x;
		$tz = $z;
		$wasMirrored = true;
		if($mirror === self::MIRROR_LEFT_RIGHT){
			$tz = -$tz;
		}elseif($mirror === self::MIRROR_FRONT_BACK){
			$tx = -$tx;
		}else{
			$wasMirrored = false;
		}

		switch($rotation & 3){
			case self::ROTATE_270:
				return [$tz, -$tx];
			case self::ROTATE_90:
				return [-$tz, $tx];
			case self::ROTATE_180:
				return [-$tx, -$tz];
			default:
				return $wasMirrored ? [$tx, $tz] : [$x, $z];
		}
	}

	public static function createBoundingBox(array $position, array $template, int $rotation, int $mirror) : array{
		$maxX = (int) $template["size"][0] - 1;
		$maxZ = (int) $template["size"][2] - 1;
		$c0 = self::transformLocalVanilla(0, 0, $mirror, $rotation);
		$c1 = self::transformLocalVanilla($maxX, 0, $mirror, $rotation);
		$c2 = self::transformLocalVanilla(0, $maxZ, $mirror, $rotation);
		$c3 = self::transformLocalVanilla($maxX, $maxZ, $mirror, $rotation);
		$minX = min($c0[0], $c1[0], $c2[0], $c3[0]);
		$maxTX = max($c0[0], $c1[0], $c2[0], $c3[0]);
		$minZ = min($c0[1], $c1[1], $c2[1], $c3[1]);
		$maxTZ = max($c0[1], $c1[1], $c2[1], $c3[1]);
		return [
			$position[0] + $minX,
			$position[1],
			$position[2] + $minZ,
			$position[0] + $maxTX,
			$position[1] + (int) $template["size"][1] - 1,
			$position[2] + $maxTZ,
		];
	}

	public static function shufflePositions(array &$values, Random $random){
		for($i = count($values) - 1; $i > 0; --$i){
			$j = $random->nextBoundedInt($i + 1);
			$tmp = $values[$i];
			$values[$i] = $values[$j];
			$values[$j] = $tmp;
		}
	}

	public static function faceOffset(string $face) : array{
		switch($face){
			case self::FACE_NORTH:
				return [0, 0, -1];
			case self::FACE_SOUTH:
				return [0, 0, 1];
			case self::FACE_WEST:
				return [-1, 0, 0];
			case self::FACE_EAST:
				return [1, 0, 0];
			case self::FACE_UP:
				return [0, 1, 0];
			default:
				return [0, 0, 0];
		}
	}

	public static function oppositeFace(string $face) : string{
		switch($face){
			case self::FACE_NORTH: return self::FACE_SOUTH;
			case self::FACE_SOUTH: return self::FACE_NORTH;
			case self::FACE_WEST: return self::FACE_EAST;
			case self::FACE_EAST: return self::FACE_WEST;
			default: return $face;
		}
	}

	public static function rotateFaceClockwise(string $face) : string{
		switch($face){
			case self::FACE_NORTH: return self::FACE_EAST;
			case self::FACE_EAST: return self::FACE_SOUTH;
			case self::FACE_SOUTH: return self::FACE_WEST;
			case self::FACE_WEST: return self::FACE_NORTH;
			default: return $face;
		}
	}

	public static function rotateFaceCounterClockwise(string $face) : string{
		switch($face){
			case self::FACE_NORTH: return self::FACE_WEST;
			case self::FACE_WEST: return self::FACE_SOUTH;
			case self::FACE_SOUTH: return self::FACE_EAST;
			case self::FACE_EAST: return self::FACE_NORTH;
			default: return $face;
		}
	}

	public static function floorMod(int $value, int $mod) : int{
		$result = $value % $mod;
		return $result < 0 ? $result + $mod : $result;
	}

	private static function isAirOrLiquid(int $id) : bool{
		return $id === Block::AIR || $id === Block::WATER || $id === Block::STILL_WATER || $id === Block::LAVA || $id === Block::STILL_LAVA;
	}

	private static function blockId(string $constant, int $fallback) : int{
		$name = Block::class . "::" . $constant;
		return defined($name) ? (int) constant($name) : $fallback;
	}

	private static function slabTopBit(array $states) : int{
		return (($states["minecraft:vertical_half"] ?? "bottom") === "top") ? 0x08 : 0;
	}

	private static function railMeta($direction) : int{
		return max(0, min(9, (int) $direction));
	}

	private static function stairMeta(array $states) : int{
		$meta = (int) ($states["weirdo_direction"] ?? 0);
		if(!empty($states["upside_down_bit"])){
			$meta |= 0x04;
		}
		return $meta;
	}

	private static function leverMeta(array $states) : int{
		$meta = 0;
		switch((string) ($states["lever_direction"] ?? "north")){
			case "east": $meta = 1; break;
			case "west": $meta = 2; break;
			case "south": $meta = 3; break;
			case "north": default: $meta = 4; break;
		}
		if(!empty($states["open_bit"])){
			$meta |= 0x08;
		}
		return $meta;
	}

	private static function ironDoorMeta(array $states) : int{
		if(!empty($states["upper_block_bit"])){
			return 0x08 | (!empty($states["door_hinge_bit"]) ? 0x01 : 0);
		}
		$meta = self::doorFacingMeta((string) ($states["minecraft:cardinal_direction"] ?? "south"));
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

	private static function cardinalMeta($direction) : int{
		switch((string) $direction){
			case "north": return 2;
			case "south": return 3;
			case "west": return 4;
			case "east":
			default: return 5;
		}
	}

	private static function mirrorCardinal(string $direction, int $mirror) : string{
		if($mirror === self::MIRROR_FRONT_BACK){
			if($direction === "east") return "west";
			if($direction === "west") return "east";
		}elseif($mirror === self::MIRROR_LEFT_RIGHT){
			if($direction === "north") return "south";
			if($direction === "south") return "north";
		}
		return $direction;
	}

	private static function mirrorFacingDirection(int $face, int $mirror) : int{
		$extra = $face & ~0x07;
		$base = $face & 0x07;
		if($mirror === self::MIRROR_FRONT_BACK){
			if($base === 4) $base = 5;
			elseif($base === 5) $base = 4;
		}elseif($mirror === self::MIRROR_LEFT_RIGHT){
			if($base === 2) $base = 3;
			elseif($base === 3) $base = 2;
		}
		return $base | $extra;
	}

	private static function mirrorWeirdoDirection(int $direction, int $mirror) : int{
		if($mirror === self::MIRROR_FRONT_BACK){
			if($direction === 0) return 1;
			if($direction === 1) return 0;
		}elseif($mirror === self::MIRROR_LEFT_RIGHT){
			if($direction === 2) return 3;
			if($direction === 3) return 2;
		}
		return $direction;
	}

	private static function mirrorRailDirection(int $direction, int $mirror) : int{
		if($mirror === self::MIRROR_FRONT_BACK){
			$map = [2 => 3, 3 => 2, 6 => 9, 9 => 6, 7 => 8, 8 => 7];
			return $map[$direction] ?? $direction;
		}
		$map = [4 => 5, 5 => 4, 6 => 7, 7 => 6, 8 => 9, 9 => 8];
		return $map[$direction] ?? $direction;
	}

	private static function rotateRailDirectionClockwise(int $direction) : int{
		$map = [
			0 => 1,
			1 => 0,
			2 => 5,
			5 => 3,
			3 => 4,
			4 => 2,
			6 => 7,
			7 => 8,
			8 => 9,
			9 => 6,
		];
		return $map[$direction] ?? $direction;
	}

	private static function mirrorTrapdoorDirection(int $direction, int $mirror) : int{
		if($mirror === self::MIRROR_FRONT_BACK){
			$map = [1 => 0, 0 => 1];
			return $map[$direction & 3] ?? ($direction & 3);
		}
		$map = [2 => 3, 3 => 2];
		return $map[$direction & 3] ?? ($direction & 3);
	}
}

abstract class WoodlandMansionFloorRoomCollection{
	abstract public function get1x1(Random $random);
	abstract public function get1x1Secret(Random $random);
	abstract public function get1x2SideEntrance(Random $random, bool $isStairsRoom);
	abstract public function get1x2FrontEntrance(Random $random, bool $isStairsRoom);
	abstract public function get1x2Secret(Random $random);
	abstract public function get2x2(Random $random);
	abstract public function get2x2Secret(Random $random);
}

final class WoodlandMansionFirstFloorRoomCollection extends WoodlandMansionFloorRoomCollection{
	public function get1x1(Random $random){ return "1x1_a" . ($random->nextBoundedInt(5) + 1); }
	public function get1x1Secret(Random $random){ return "1x1_as" . ($random->nextBoundedInt(4) + 1); }
	public function get1x2SideEntrance(Random $random, bool $isStairsRoom){ return "1x2_a" . ($random->nextBoundedInt(9) + 1); }
	public function get1x2FrontEntrance(Random $random, bool $isStairsRoom){ return "1x2_b" . ($random->nextBoundedInt(5) + 1); }
	public function get1x2Secret(Random $random){ return "1x2_s" . ($random->nextBoundedInt(2) + 1); }
	public function get2x2(Random $random){ return "2x2_a" . ($random->nextBoundedInt(4) + 1); }
	public function get2x2Secret(Random $random){ return "2x2_s1"; }
}

class WoodlandMansionSecondFloorRoomCollection extends WoodlandMansionFloorRoomCollection{
	public function get1x1(Random $random){ return "1x1_b" . ($random->nextBoundedInt(5) + 1); }
	public function get1x1Secret(Random $random){ return "1x1_as" . ($random->nextBoundedInt(4) + 1); }
	public function get1x2SideEntrance(Random $random, bool $isStairsRoom){ return $isStairsRoom ? "1x2_c_stairs" : "1x2_c" . ($random->nextBoundedInt(4) + 1); }
	public function get1x2FrontEntrance(Random $random, bool $isStairsRoom){ return $isStairsRoom ? "1x2_d_stairs" : "1x2_d" . ($random->nextBoundedInt(5) + 1); }
	public function get1x2Secret(Random $random){ return "1x2_se1"; }
	public function get2x2(Random $random){ return "2x2_b" . ($random->nextBoundedInt(5) + 1); }
	public function get2x2Secret(Random $random){ return "2x2_s1"; }
}

final class WoodlandMansionThirdFloorRoomCollection extends WoodlandMansionSecondFloorRoomCollection{
}

final class WoodlandMansionSimpleGrid{
	public $width;
	public $height;
	private $valueIfOutside;
	private $grid = [];

	public function __construct(int $width, int $height, int $valueIfOutside){
		$this->width = $width;
		$this->height = $height;
		$this->valueIfOutside = $valueIfOutside;
		for($x = 0; $x < $width; ++$x){
			$this->grid[$x] = array_fill(0, $height, 0);
		}
	}

	public function set($x, $y, $value, $y1 = null, $value2 = null){
		if($value2 !== null){
			$x0 = (int) $x;
			$y0 = (int) $y;
			$x1 = (int) $value;
			$yy1 = (int) $y1;
			for($yy = $y0; $yy <= $yy1; ++$yy){
				for($xx = $x0; $xx <= $x1; ++$xx){
					$this->set($xx, $yy, (int) $value2);
				}
			}
			return;
		}
		$x = (int) $x;
		$y = (int) $y;
		if($x >= 0 && $x < $this->width && $y >= 0 && $y < $this->height){
			$this->grid[$x][$y] = (int) $value;
		}
	}

	public function get(int $x, int $y) : int{
		return $x >= 0 && $x < $this->width && $y >= 0 && $y < $this->height ? $this->grid[$x][$y] : $this->valueIfOutside;
	}

	public function setIf(int $x, int $y, int $ifValue, int $value){
		if($this->get($x, $y) === $ifValue){
			$this->set($x, $y, $value);
		}
	}

	public function edgesTo(int $x, int $y, int $ifValue) : bool{
		return $this->get($x - 1, $y) === $ifValue || $this->get($x + 1, $y) === $ifValue || $this->get($x, $y + 1) === $ifValue || $this->get($x, $y - 1) === $ifValue;
	}
}

final class WoodlandMansionGrid{
	const ROOM_1x1 = 65536;
	const ROOM_1x2 = 131072;
	const ROOM_2x2 = 262144;
	const ROOM_ORIGIN_FLAG = 1048576;
	const ROOM_DOOR_FLAG = 2097152;
	const ROOM_STAIRS_FLAG = 4194304;
	const ROOM_CORRIDOR_FLAG = 8388608;
	const ROOM_TYPE_MASK = 983040;
	const ROOM_ID_MASK = 65535;

	public $baseGrid;
	public $thirdFloorGrid;
	public $floorRooms = [];
	public $entranceX = 7;
	public $entranceY = 4;
	private $random;

	public function __construct(Random $random){
		$this->random = $random;
		$this->baseGrid = new WoodlandMansionSimpleGrid(11, 11, 5);
		$this->baseGrid->set($this->entranceX, $this->entranceY, $this->entranceX + 1, $this->entranceY + 1, 3);
		$this->baseGrid->set($this->entranceX - 1, $this->entranceY, $this->entranceX - 1, $this->entranceY + 1, 2);
		$this->baseGrid->set($this->entranceX + 2, $this->entranceY - 2, $this->entranceX + 3, $this->entranceY + 3, 5);
		$this->baseGrid->set($this->entranceX + 1, $this->entranceY - 2, $this->entranceX + 1, $this->entranceY - 1, 1);
		$this->baseGrid->set($this->entranceX + 1, $this->entranceY + 2, $this->entranceX + 1, $this->entranceY + 3, 1);
		$this->baseGrid->set($this->entranceX - 1, $this->entranceY - 1, 1);
		$this->baseGrid->set($this->entranceX - 1, $this->entranceY + 2, 1);
		$this->baseGrid->set(0, 0, 11, 1, 5);
		$this->baseGrid->set(0, 9, 11, 11, 5);
		$this->recursiveCorridor($this->baseGrid, $this->entranceX, $this->entranceY - 2, WoodlandMansion::FACE_WEST, 6);
		$this->recursiveCorridor($this->baseGrid, $this->entranceX, $this->entranceY + 3, WoodlandMansion::FACE_WEST, 6);
		$this->recursiveCorridor($this->baseGrid, $this->entranceX - 2, $this->entranceY - 1, WoodlandMansion::FACE_WEST, 3);
		$this->recursiveCorridor($this->baseGrid, $this->entranceX - 2, $this->entranceY + 2, WoodlandMansion::FACE_WEST, 3);
		while($this->cleanEdges($this->baseGrid)){
		}

		$this->floorRooms[0] = new WoodlandMansionSimpleGrid(11, 11, 5);
		$this->floorRooms[1] = new WoodlandMansionSimpleGrid(11, 11, 5);
		$this->floorRooms[2] = new WoodlandMansionSimpleGrid(11, 11, 5);
		$this->identifyRooms($this->baseGrid, $this->floorRooms[0]);
		$this->identifyRooms($this->baseGrid, $this->floorRooms[1]);
		$this->floorRooms[0]->set($this->entranceX + 1, $this->entranceY, $this->entranceX + 1, $this->entranceY + 1, self::ROOM_CORRIDOR_FLAG);
		$this->floorRooms[1]->set($this->entranceX + 1, $this->entranceY, $this->entranceX + 1, $this->entranceY + 1, self::ROOM_CORRIDOR_FLAG);
		$this->thirdFloorGrid = new WoodlandMansionSimpleGrid($this->baseGrid->width, $this->baseGrid->height, 5);
		$this->setupThirdFloor();
		$this->identifyRooms($this->thirdFloorGrid, $this->floorRooms[2]);
	}

	public static function isHouse(WoodlandMansionSimpleGrid $grid, int $x, int $y) : bool{
		$value = $grid->get($x, $y);
		return $value === 1 || $value === 2 || $value === 3 || $value === 4;
	}

	public function isRoomId(WoodlandMansionSimpleGrid $grid, int $x, int $y, int $floor, int $roomId) : bool{
		return ($this->floorRooms[$floor]->get($x, $y) & self::ROOM_ID_MASK) === $roomId;
	}

	public function get1x2RoomDirection(WoodlandMansionSimpleGrid $grid, int $x, int $y, int $floorNum, int $roomId){
		foreach(WoodlandMansion::horizontalFaces() as $direction){
			$off = WoodlandMansion::faceOffset($direction);
			if($this->isRoomId($grid, $x + $off[0], $y + $off[2], $floorNum, $roomId)){
				return $direction;
			}
		}
		return null;
	}

	private function recursiveCorridor(WoodlandMansionSimpleGrid $grid, int $x, int $y, string $heading, int $depth){
		if($depth <= 0){
			return;
		}
		$off = WoodlandMansion::faceOffset($heading);
		$grid->set($x, $y, 1);
		$grid->setIf($x + $off[0], $y + $off[2], 0, 1);
		for($attempts = 0; $attempts < 8; ++$attempts){
			$nextDir = WoodlandMansion::horizontalFaces()[$this->random->nextBoundedInt(4)];
			if($nextDir !== WoodlandMansion::oppositeFace($heading) && ($nextDir !== WoodlandMansion::FACE_EAST || !$this->random->nextBoolean())){
				$nextOff = WoodlandMansion::faceOffset($nextDir);
				$nx = $x + $off[0];
				$ny = $y + $off[2];
				if($grid->get($nx + $nextOff[0], $ny + $nextOff[2]) === 0 && $grid->get($nx + $nextOff[0] * 2, $ny + $nextOff[2] * 2) === 0){
					$this->recursiveCorridor($grid, $x + $off[0] + $nextOff[0], $y + $off[2] + $nextOff[2], $nextDir, $depth - 1);
					break;
				}
			}
		}

		$cw = WoodlandMansion::rotateFaceClockwise($heading);
		$ccw = WoodlandMansion::rotateFaceCounterClockwise($heading);
		$cwOff = WoodlandMansion::faceOffset($cw);
		$ccwOff = WoodlandMansion::faceOffset($ccw);
		$grid->setIf($x + $cwOff[0], $y + $cwOff[2], 0, 2);
		$grid->setIf($x + $ccwOff[0], $y + $ccwOff[2], 0, 2);
		$grid->setIf($x + $off[0] + $cwOff[0], $y + $off[2] + $cwOff[2], 0, 2);
		$grid->setIf($x + $off[0] + $ccwOff[0], $y + $off[2] + $ccwOff[2], 0, 2);
		$grid->setIf($x + $off[0] * 2, $y + $off[2] * 2, 0, 2);
		$grid->setIf($x + $cwOff[0] * 2, $y + $cwOff[2] * 2, 0, 2);
		$grid->setIf($x + $ccwOff[0] * 2, $y + $ccwOff[2] * 2, 0, 2);
	}

	private function cleanEdges(WoodlandMansionSimpleGrid $grid) : bool{
		$touched = false;
		for($y = 0; $y < $grid->height; ++$y){
			for($x = 0; $x < $grid->width; ++$x){
				if($grid->get($x, $y) !== 0){
					continue;
				}
				$direct = 0;
				$direct += self::isHouse($grid, $x + 1, $y) ? 1 : 0;
				$direct += self::isHouse($grid, $x - 1, $y) ? 1 : 0;
				$direct += self::isHouse($grid, $x, $y + 1) ? 1 : 0;
				$direct += self::isHouse($grid, $x, $y - 1) ? 1 : 0;
				if($direct >= 3){
					$grid->set($x, $y, 2);
					$touched = true;
				}elseif($direct === 2){
					$diagonal = 0;
					$diagonal += self::isHouse($grid, $x + 1, $y + 1) ? 1 : 0;
					$diagonal += self::isHouse($grid, $x - 1, $y + 1) ? 1 : 0;
					$diagonal += self::isHouse($grid, $x + 1, $y - 1) ? 1 : 0;
					$diagonal += self::isHouse($grid, $x - 1, $y - 1) ? 1 : 0;
					if($diagonal <= 1){
						$grid->set($x, $y, 2);
						$touched = true;
					}
				}
			}
		}
		return $touched;
	}

	private function setupThirdFloor(){
		$potentialRooms = [];
		$floor = $this->floorRooms[1];
		for($y = 0; $y < $this->thirdFloorGrid->height; ++$y){
			for($x = 0; $x < $this->thirdFloorGrid->width; ++$x){
				$roomData = $floor->get($x, $y);
				$roomType = $roomData & self::ROOM_TYPE_MASK;
				if($roomType === self::ROOM_1x2 && ($roomData & self::ROOM_DOOR_FLAG) === self::ROOM_DOOR_FLAG){
					$potentialRooms[] = [$x, $y];
				}
			}
		}
		if(empty($potentialRooms)){
			$this->thirdFloorGrid->set(0, 0, $this->thirdFloorGrid->width, $this->thirdFloorGrid->height, 5);
			return;
		}
		$roomPos = $potentialRooms[$this->random->nextBoundedInt(count($potentialRooms))];
		$roomData = $floor->get($roomPos[0], $roomPos[1]);
		$floor->set($roomPos[0], $roomPos[1], $roomData | self::ROOM_STAIRS_FLAG);
		$roomDir = $this->get1x2RoomDirection($this->baseGrid, $roomPos[0], $roomPos[1], 1, $roomData & self::ROOM_ID_MASK);
		if($roomDir === null){
			$this->thirdFloorGrid->set(0, 0, $this->thirdFloorGrid->width, $this->thirdFloorGrid->height, 5);
			return;
		}
		$off = WoodlandMansion::faceOffset($roomDir);
		$roomEndX = $roomPos[0] + $off[0];
		$roomEndY = $roomPos[1] + $off[2];
		for($y = 0; $y < $this->thirdFloorGrid->height; ++$y){
			for($x = 0; $x < $this->thirdFloorGrid->width; ++$x){
				if(!self::isHouse($this->baseGrid, $x, $y)){
					$this->thirdFloorGrid->set($x, $y, 5);
				}elseif($x === $roomPos[0] && $y === $roomPos[1]){
					$this->thirdFloorGrid->set($x, $y, 3);
					$this->floorRooms[2]->set($x, $y, self::ROOM_CORRIDOR_FLAG);
				}elseif($x === $roomEndX && $y === $roomEndY){
					$this->thirdFloorGrid->set($x, $y, 3);
					$this->floorRooms[2]->set($x, $y, self::ROOM_CORRIDOR_FLAG);
				}
			}
		}
		$potentialCorridors = [];
		foreach(WoodlandMansion::horizontalFaces() as $direction){
			$d = WoodlandMansion::faceOffset($direction);
			if($this->thirdFloorGrid->get($roomEndX + $d[0], $roomEndY + $d[2]) === 0){
				$potentialCorridors[] = $direction;
			}
		}
		if(empty($potentialCorridors)){
			$this->thirdFloorGrid->set(0, 0, $this->thirdFloorGrid->width, $this->thirdFloorGrid->height, 5);
			$floor->set($roomPos[0], $roomPos[1], $roomData);
			return;
		}
		$corridorDir = $potentialCorridors[$this->random->nextBoundedInt(count($potentialCorridors))];
		$d = WoodlandMansion::faceOffset($corridorDir);
		$this->recursiveCorridor($this->thirdFloorGrid, $roomEndX + $d[0], $roomEndY + $d[2], $corridorDir, 4);
		while($this->cleanEdges($this->thirdFloorGrid)){
		}
	}

	private function identifyRooms(WoodlandMansionSimpleGrid $fromGrid, WoodlandMansionSimpleGrid $roomGrid){
		$roomPos = [];
		for($y = 0; $y < $fromGrid->height; ++$y){
			for($x = 0; $x < $fromGrid->width; ++$x){
				if($fromGrid->get($x, $y) === 2){
					$roomPos[] = [$x, $y];
				}
			}
		}
		WoodlandMansion::shufflePositions($roomPos, $this->random);
		$roomId = 10;
		foreach($roomPos as $pos){
			$x = $pos[0];
			$y = $pos[1];
			if($roomGrid->get($x, $y) !== 0){
				continue;
			}
			$x0 = $x; $x1 = $x; $y0 = $y; $y1 = $y; $type = self::ROOM_1x1;
			if($roomGrid->get($x + 1, $y) === 0 && $roomGrid->get($x, $y + 1) === 0 && $roomGrid->get($x + 1, $y + 1) === 0 && $fromGrid->get($x + 1, $y) === 2 && $fromGrid->get($x, $y + 1) === 2 && $fromGrid->get($x + 1, $y + 1) === 2){
				$x1 = $x + 1; $y1 = $y + 1; $type = self::ROOM_2x2;
			}elseif($roomGrid->get($x - 1, $y) === 0 && $roomGrid->get($x, $y + 1) === 0 && $roomGrid->get($x - 1, $y + 1) === 0 && $fromGrid->get($x - 1, $y) === 2 && $fromGrid->get($x, $y + 1) === 2 && $fromGrid->get($x - 1, $y + 1) === 2){
				$x0 = $x - 1; $y1 = $y + 1; $type = self::ROOM_2x2;
			}elseif($roomGrid->get($x - 1, $y) === 0 && $roomGrid->get($x, $y - 1) === 0 && $roomGrid->get($x - 1, $y - 1) === 0 && $fromGrid->get($x - 1, $y) === 2 && $fromGrid->get($x, $y - 1) === 2 && $fromGrid->get($x - 1, $y - 1) === 2){
				$x0 = $x - 1; $y0 = $y - 1; $type = self::ROOM_2x2;
			}elseif($roomGrid->get($x + 1, $y) === 0 && $fromGrid->get($x + 1, $y) === 2){
				$x1 = $x + 1; $type = self::ROOM_1x2;
			}elseif($roomGrid->get($x, $y + 1) === 0 && $fromGrid->get($x, $y + 1) === 2){
				$y1 = $y + 1; $type = self::ROOM_1x2;
			}elseif($roomGrid->get($x - 1, $y) === 0 && $fromGrid->get($x - 1, $y) === 2){
				$x0 = $x - 1; $type = self::ROOM_1x2;
			}elseif($roomGrid->get($x, $y - 1) === 0 && $fromGrid->get($x, $y - 1) === 2){
				$y0 = $y - 1; $type = self::ROOM_1x2;
			}
			$doorX = $this->random->nextBoolean() ? $x0 : $x1;
			$doorY = $this->random->nextBoolean() ? $y0 : $y1;
			$doorFlag = self::ROOM_DOOR_FLAG;
			if(!$fromGrid->edgesTo($doorX, $doorY, 1)){
				$doorX = $doorX === $x0 ? $x1 : $x0;
				$doorY = $doorY === $y0 ? $y1 : $y0;
				if(!$fromGrid->edgesTo($doorX, $doorY, 1)){
					$doorY = $doorY === $y0 ? $y1 : $y0;
					if(!$fromGrid->edgesTo($doorX, $doorY, 1)){
						$doorX = $doorX === $x0 ? $x1 : $x0;
						$doorY = $doorY === $y0 ? $y1 : $y0;
						if(!$fromGrid->edgesTo($doorX, $doorY, 1)){
							$doorFlag = 0;
							$doorX = $x0;
							$doorY = $y0;
						}
					}
				}
			}
			for($ry = $y0; $ry <= $y1; ++$ry){
				for($rx = $x0; $rx <= $x1; ++$rx){
					$roomGrid->set($rx, $ry, ($rx === $doorX && $ry === $doorY ? self::ROOM_ORIGIN_FLAG | $doorFlag : 0) | $type | $roomId);
				}
			}
			++$roomId;
		}
	}
}

final class WoodlandMansionPlacementData{
	public $rotation;
	public $position;
	public $wallType;
}

final class WoodlandMansionPiecePlacer{
	private $random;
	private $startX;
	private $startY;

	public function __construct(Random $random){
		$this->random = $random;
	}

	public function createMansion(array $origin, int $rotation, array &$pieces, WoodlandMansionGrid $mansion){
		$data = new WoodlandMansionPlacementData();
		$data->position = $origin;
		$data->rotation = $rotation;
		$data->wallType = "wall_flat";
		$secondData = new WoodlandMansionPlacementData();
		$this->entrance($pieces, $data);
		$secondData->position = WoodlandMansion::up($data->position, 8);
		$secondData->rotation = $data->rotation;
		$secondData->wallType = "wall_window";

		$baseGrid = $mansion->baseGrid;
		$thirdGrid = $mansion->thirdFloorGrid;
		$this->startX = $mansion->entranceX + 1;
		$this->startY = $mansion->entranceY + 1;
		$endX = $mansion->entranceX + 1;
		$endY = $mansion->entranceY;
		$this->traverseOuterWalls($pieces, $data, $baseGrid, WoodlandMansion::FACE_SOUTH, $this->startX, $this->startY, $endX, $endY);
		$this->traverseOuterWalls($pieces, $secondData, $baseGrid, WoodlandMansion::FACE_SOUTH, $this->startX, $this->startY, $endX, $endY);

		$thirdData = new WoodlandMansionPlacementData();
		$thirdData->position = WoodlandMansion::up($origin, 19);
		$thirdData->rotation = $rotation;
		$thirdData->wallType = "wall_window";
		$done = false;
		for($y = 0; $y < $thirdGrid->height && !$done; ++$y){
			for($x = $thirdGrid->width - 1; $x >= 0 && !$done; --$x){
				if(WoodlandMansionGrid::isHouse($thirdGrid, $x, $y)){
					$thirdData->position = WoodlandMansion::relative($thirdData->position, $rotation, WoodlandMansion::FACE_SOUTH, 8 + ($y - $this->startY) * 8);
					$thirdData->position = WoodlandMansion::relative($thirdData->position, $rotation, WoodlandMansion::FACE_EAST, ($x - $this->startX) * 8);
					$this->traverseWallPiece($pieces, $thirdData);
					$this->traverseOuterWalls($pieces, $thirdData, $thirdGrid, WoodlandMansion::FACE_SOUTH, $x, $y, $x, $y);
					$done = true;
				}
			}
		}

		$this->createRoof($pieces, WoodlandMansion::up($origin, 16), $rotation, $baseGrid, $thirdGrid);
		$this->createRoof($pieces, WoodlandMansion::up($origin, 27), $rotation, $thirdGrid, null);

		$roomCollections = [
			new WoodlandMansionFirstFloorRoomCollection(),
			new WoodlandMansionSecondFloorRoomCollection(),
			new WoodlandMansionThirdFloorRoomCollection(),
		];

		for($floorNum = 0; $floorNum < 3; ++$floorNum){
			$floorOrigin = WoodlandMansion::up($origin, 8 * $floorNum + ($floorNum === 2 ? 3 : 0));
			$rooms = $mansion->floorRooms[$floorNum];
			$grid = $floorNum === 2 ? $thirdGrid : $baseGrid;
			$southPiece = $floorNum === 0 ? "carpet_south_1" : "carpet_south_2";
			$westPiece = $floorNum === 0 ? "carpet_west_1" : "carpet_west_2";

			for($y = 0; $y < $grid->height; ++$y){
				for($x = 0; $x < $grid->width; ++$x){
					$isCorridorCell = $grid->get($x, $y) === 1;
					$isThirdFloorCorridorStart = $floorNum === 2 && $grid->get($x, $y) === 3 && (($rooms->get($x, $y) & WoodlandMansionGrid::ROOM_CORRIDOR_FLAG) === WoodlandMansionGrid::ROOM_CORRIDOR_FLAG);
					if($isCorridorCell){
						$pos = WoodlandMansion::relative($floorOrigin, $rotation, WoodlandMansion::FACE_SOUTH, 8 + ($y - $this->startY) * 8);
						$pos = WoodlandMansion::relative($pos, $rotation, WoodlandMansion::FACE_EAST, ($x - $this->startX) * 8);
						$pieces[] = new WoodlandMansionPiece("corridor_floor", $pos, $rotation);
						if($grid->get($x, $y - 1) === 1 || (($rooms->get($x, $y - 1) & WoodlandMansionGrid::ROOM_CORRIDOR_FLAG) === WoodlandMansionGrid::ROOM_CORRIDOR_FLAG) || ($floorNum === 2 && $grid->get($x, $y - 1) === 3)){
							$pieces[] = new WoodlandMansionPiece("carpet_north", WoodlandMansion::up(WoodlandMansion::relative($pos, $rotation, WoodlandMansion::FACE_EAST, 1)), $rotation);
						}
						if($grid->get($x + 1, $y) === 1 || (($rooms->get($x + 1, $y) & WoodlandMansionGrid::ROOM_CORRIDOR_FLAG) === WoodlandMansionGrid::ROOM_CORRIDOR_FLAG) || ($floorNum === 2 && $grid->get($x + 1, $y) === 3)){
							$pieces[] = new WoodlandMansionPiece("carpet_east", WoodlandMansion::up(WoodlandMansion::relative(WoodlandMansion::relative($pos, $rotation, WoodlandMansion::FACE_SOUTH, 1), $rotation, WoodlandMansion::FACE_EAST, 5)), $rotation);
						}
						if($grid->get($x, $y + 1) === 1 || (($rooms->get($x, $y + 1) & WoodlandMansionGrid::ROOM_CORRIDOR_FLAG) === WoodlandMansionGrid::ROOM_CORRIDOR_FLAG) || ($floorNum === 2 && $grid->get($x, $y + 1) === 3)){
							$pieces[] = new WoodlandMansionPiece($southPiece, WoodlandMansion::relative(WoodlandMansion::relative($pos, $rotation, WoodlandMansion::FACE_SOUTH, 5), $rotation, WoodlandMansion::FACE_WEST, 1), $rotation);
						}
						if($grid->get($x - 1, $y) === 1 || (($rooms->get($x - 1, $y) & WoodlandMansionGrid::ROOM_CORRIDOR_FLAG) === WoodlandMansionGrid::ROOM_CORRIDOR_FLAG) || ($floorNum === 2 && $grid->get($x - 1, $y) === 3)){
							$pieces[] = new WoodlandMansionPiece($westPiece, WoodlandMansion::relative(WoodlandMansion::relative($pos, $rotation, WoodlandMansion::FACE_WEST, 1), $rotation, WoodlandMansion::FACE_NORTH, 1), $rotation);
						}
					}elseif($isThirdFloorCorridorStart){
						$pos = WoodlandMansion::relative($floorOrigin, $rotation, WoodlandMansion::FACE_SOUTH, 8 + ($y - $this->startY) * 8);
						$pos = WoodlandMansion::relative($pos, $rotation, WoodlandMansion::FACE_EAST, ($x - $this->startX) * 8);
						if($grid->get($x, $y + 1) === 1){
							$southPos = WoodlandMansion::relative(WoodlandMansion::relative($pos, $rotation, WoodlandMansion::FACE_SOUTH, 5), $rotation, WoodlandMansion::FACE_WEST, 1);
							$southPos = $this->applyTemplateRotationOffset($southPiece, $southPos, $rotation);
							$southPos = WoodlandMansion::relative(WoodlandMansion::relative($southPos, $rotation, WoodlandMansion::FACE_WEST, 2), $rotation, WoodlandMansion::FACE_SOUTH, 1);
							$pieces[] = new WoodlandMansionPiece($southPiece, $southPos, WoodlandMansion::rotateBy($rotation, WoodlandMansion::ROTATE_180));
						}
						if($grid->get($x - 1, $y) === 1){
							$westPos = WoodlandMansion::relative(WoodlandMansion::relative($pos, $rotation, WoodlandMansion::FACE_WEST, 1), $rotation, WoodlandMansion::FACE_NORTH, 1);
							$westPos = $this->applyTemplateRotationOffset($westPiece, $westPos, $rotation);
							$westPos = WoodlandMansion::relative(WoodlandMansion::relative($westPos, $rotation, WoodlandMansion::FACE_WEST, 2), $rotation, WoodlandMansion::FACE_SOUTH, 1);
							$pieces[] = new WoodlandMansionPiece($westPiece, $westPos, WoodlandMansion::rotateBy($rotation, WoodlandMansion::ROTATE_180));
						}
					}
				}
			}

			$wallPiece = $floorNum === 0 ? "indoors_wall_1" : "indoors_wall_2";
			$doorPiece = $floorNum === 0 ? "indoors_door_1" : "indoors_door_2";
			for($y = 0; $y < $grid->height; ++$y){
				for($x = 0; $x < $grid->width; ++$x){
					$thirdFloorStartRoom = $floorNum === 2 && $grid->get($x, $y) === 3;
					if($grid->get($x, $y) !== 2 && !$thirdFloorStartRoom){
						continue;
					}
					$roomData = $rooms->get($x, $y);
					$roomType = $roomData & WoodlandMansionGrid::ROOM_TYPE_MASK;
					$roomId = $roomData & WoodlandMansionGrid::ROOM_ID_MASK;
					$thirdFloorStartRoom = $thirdFloorStartRoom && (($roomData & WoodlandMansionGrid::ROOM_CORRIDOR_FLAG) === WoodlandMansionGrid::ROOM_CORRIDOR_FLAG);
					$doorDirs = [];
					if(($roomData & WoodlandMansionGrid::ROOM_DOOR_FLAG) === WoodlandMansionGrid::ROOM_DOOR_FLAG){
						foreach(WoodlandMansion::horizontalFaces() as $direction){
							$d = WoodlandMansion::faceOffset($direction);
							if($grid->get($x + $d[0], $y + $d[2]) === 1){
								$doorDirs[] = $direction;
							}
						}
					}
					$doorDir = null;
					if(!empty($doorDirs)){
						$doorDir = $doorDirs[$this->random->nextBoundedInt(count($doorDirs))];
					}elseif(($roomData & WoodlandMansionGrid::ROOM_ORIGIN_FLAG) === WoodlandMansionGrid::ROOM_ORIGIN_FLAG){
						$doorDir = WoodlandMansion::FACE_UP;
					}
					$roomPos = WoodlandMansion::relative($floorOrigin, $rotation, WoodlandMansion::FACE_SOUTH, 8 + ($y - $this->startY) * 8);
					$roomPos = WoodlandMansion::relative($roomPos, $rotation, WoodlandMansion::FACE_EAST, -1 + ($x - $this->startX) * 8);
					if(WoodlandMansionGrid::isHouse($grid, $x - 1, $y) && !$mansion->isRoomId($grid, $x - 1, $y, $floorNum, $roomId)){
						$pieces[] = new WoodlandMansionPiece($doorDir === WoodlandMansion::FACE_WEST ? $doorPiece : $wallPiece, $roomPos, $rotation);
					}
					if($grid->get($x + 1, $y) === 1 && !$thirdFloorStartRoom){
						$pieces[] = new WoodlandMansionPiece($doorDir === WoodlandMansion::FACE_EAST ? $doorPiece : $wallPiece, WoodlandMansion::relative($roomPos, $rotation, WoodlandMansion::FACE_EAST, 8), $rotation);
					}
					if(WoodlandMansionGrid::isHouse($grid, $x, $y + 1) && !$mansion->isRoomId($grid, $x, $y + 1, $floorNum, $roomId)){
						$posx = WoodlandMansion::relative(WoodlandMansion::relative($roomPos, $rotation, WoodlandMansion::FACE_SOUTH, 7), $rotation, WoodlandMansion::FACE_EAST, 7);
						$pieces[] = new WoodlandMansionPiece($doorDir === WoodlandMansion::FACE_SOUTH ? $doorPiece : $wallPiece, $posx, WoodlandMansion::rotateBy($rotation, WoodlandMansion::ROTATE_90));
					}
					if($grid->get($x, $y - 1) === 1 && !$thirdFloorStartRoom){
						$posx = WoodlandMansion::relative(WoodlandMansion::relative($roomPos, $rotation, WoodlandMansion::FACE_NORTH, 1), $rotation, WoodlandMansion::FACE_EAST, 7);
						$pieces[] = new WoodlandMansionPiece($doorDir === WoodlandMansion::FACE_NORTH ? $doorPiece : $wallPiece, $posx, WoodlandMansion::rotateBy($rotation, WoodlandMansion::ROTATE_90));
					}
					if($roomType === WoodlandMansionGrid::ROOM_1x1){
						$this->addRoom1x1($pieces, $roomPos, $rotation, $doorDir, $roomCollections[$floorNum]);
					}elseif($roomType === WoodlandMansionGrid::ROOM_1x2 && $doorDir !== null){
						$roomDir = $mansion->get1x2RoomDirection($grid, $x, $y, $floorNum, $roomId);
						$isStairsRoom = ($roomData & WoodlandMansionGrid::ROOM_STAIRS_FLAG) === WoodlandMansionGrid::ROOM_STAIRS_FLAG;
						$this->addRoom1x2($pieces, $roomPos, $rotation, $roomDir, $doorDir, $roomCollections[$floorNum], $isStairsRoom);
					}elseif($roomType === WoodlandMansionGrid::ROOM_2x2 && $doorDir !== null && $doorDir !== WoodlandMansion::FACE_UP){
						$roomDir = WoodlandMansion::rotateFaceClockwise($doorDir);
						$d = WoodlandMansion::faceOffset($roomDir);
						if(!$mansion->isRoomId($grid, $x + $d[0], $y + $d[2], $floorNum, $roomId)){
							$roomDir = WoodlandMansion::oppositeFace($roomDir);
						}
						$this->addRoom2x2($pieces, $roomPos, $rotation, $roomDir, $doorDir, $roomCollections[$floorNum]);
					}elseif($roomType === WoodlandMansionGrid::ROOM_2x2 && $doorDir === WoodlandMansion::FACE_UP){
						$this->addRoom2x2Secret($pieces, $roomPos, $rotation, $roomCollections[$floorNum]);
					}
				}
			}
		}
	}

	private function traverseOuterWalls(array &$pieces, WoodlandMansionPlacementData $data, WoodlandMansionSimpleGrid $grid, string $gridDirection, int $startX, int $startY, int $endX, int $endY){
		$gridX = $startX;
		$gridY = $startY;
		$startDirection = $gridDirection;
		do{
			$d = WoodlandMansion::faceOffset($gridDirection);
			$ccw = WoodlandMansion::rotateFaceCounterClockwise($gridDirection);
			$ccwOff = WoodlandMansion::faceOffset($ccw);
			if(!WoodlandMansionGrid::isHouse($grid, $gridX + $d[0], $gridY + $d[2])){
				$this->traverseTurn($pieces, $data);
				$gridDirection = WoodlandMansion::rotateFaceClockwise($gridDirection);
				if($gridX !== $endX || $gridY !== $endY || $startDirection !== $gridDirection){
					$this->traverseWallPiece($pieces, $data);
				}
			}elseif(WoodlandMansionGrid::isHouse($grid, $gridX + $d[0], $gridY + $d[2]) && WoodlandMansionGrid::isHouse($grid, $gridX + $d[0] + $ccwOff[0], $gridY + $d[2] + $ccwOff[2])){
				$this->traverseInnerTurn($pieces, $data);
				$gridX += $d[0];
				$gridY += $d[2];
				$gridDirection = $ccw;
			}else{
				$gridX += $d[0];
				$gridY += $d[2];
				if($gridX !== $endX || $gridY !== $endY || $startDirection !== $gridDirection){
					$this->traverseWallPiece($pieces, $data);
				}
			}
		}while($gridX !== $endX || $gridY !== $endY || $startDirection !== $gridDirection);
	}

	private function createRoof(array &$pieces, array $roofOrigin, int $rotation, WoodlandMansionSimpleGrid $grid, $aboveGrid){
		for($y = 0; $y < $grid->height; ++$y){
			for($x = 0; $x < $grid->width; ++$x){
				$position = WoodlandMansion::relative($roofOrigin, $rotation, WoodlandMansion::FACE_SOUTH, 8 + ($y - $this->startY) * 8);
				$position = WoodlandMansion::relative($position, $rotation, WoodlandMansion::FACE_EAST, ($x - $this->startX) * 8);
				$isAbove = $aboveGrid !== null && WoodlandMansionGrid::isHouse($aboveGrid, $x, $y);
				if(WoodlandMansionGrid::isHouse($grid, $x, $y) && !$isAbove){
					$pieces[] = new WoodlandMansionPiece("roof", WoodlandMansion::up($position, 3), $rotation);
					if(!WoodlandMansionGrid::isHouse($grid, $x + 1, $y)){
						$pieces[] = new WoodlandMansionPiece("roof_front", WoodlandMansion::relative($position, $rotation, WoodlandMansion::FACE_EAST, 6), $rotation);
					}
					if(!WoodlandMansionGrid::isHouse($grid, $x - 1, $y)){
						$pieces[] = new WoodlandMansionPiece("roof_front", WoodlandMansion::relative(WoodlandMansion::relative($position, $rotation, WoodlandMansion::FACE_EAST, 0), $rotation, WoodlandMansion::FACE_SOUTH, 7), WoodlandMansion::rotateBy($rotation, WoodlandMansion::ROTATE_180));
					}
					if(!WoodlandMansionGrid::isHouse($grid, $x, $y - 1)){
						$pieces[] = new WoodlandMansionPiece("roof_front", WoodlandMansion::relative($position, $rotation, WoodlandMansion::FACE_WEST, 1), WoodlandMansion::rotateBy($rotation, WoodlandMansion::ROTATE_270));
					}
					if(!WoodlandMansionGrid::isHouse($grid, $x, $y + 1)){
						$pieces[] = new WoodlandMansionPiece("roof_front", WoodlandMansion::relative(WoodlandMansion::relative($position, $rotation, WoodlandMansion::FACE_EAST, 6), $rotation, WoodlandMansion::FACE_SOUTH, 6), WoodlandMansion::rotateBy($rotation, WoodlandMansion::ROTATE_90));
					}
				}
			}
		}
		if($aboveGrid !== null){
			for($y = 0; $y < $grid->height; ++$y){
				for($x = 0; $x < $grid->width; ++$x){
					$pos = WoodlandMansion::relative($roofOrigin, $rotation, WoodlandMansion::FACE_SOUTH, 8 + ($y - $this->startY) * 8);
					$pos = WoodlandMansion::relative($pos, $rotation, WoodlandMansion::FACE_EAST, ($x - $this->startX) * 8);
					if(WoodlandMansionGrid::isHouse($grid, $x, $y) && WoodlandMansionGrid::isHouse($aboveGrid, $x, $y)){
						if(!WoodlandMansionGrid::isHouse($grid, $x + 1, $y)){
							$pieces[] = new WoodlandMansionPiece("small_wall", WoodlandMansion::relative($pos, $rotation, WoodlandMansion::FACE_EAST, 7), $rotation);
						}
						if(!WoodlandMansionGrid::isHouse($grid, $x - 1, $y)){
							$pieces[] = new WoodlandMansionPiece("small_wall", WoodlandMansion::relative(WoodlandMansion::relative($pos, $rotation, WoodlandMansion::FACE_WEST, 1), $rotation, WoodlandMansion::FACE_SOUTH, 6), WoodlandMansion::rotateBy($rotation, WoodlandMansion::ROTATE_180));
						}
						if(!WoodlandMansionGrid::isHouse($grid, $x, $y - 1)){
							$pieces[] = new WoodlandMansionPiece("small_wall", WoodlandMansion::relative(WoodlandMansion::relative($pos, $rotation, WoodlandMansion::FACE_WEST, 0), $rotation, WoodlandMansion::FACE_NORTH, 1), WoodlandMansion::rotateBy($rotation, WoodlandMansion::ROTATE_270));
						}
						if(!WoodlandMansionGrid::isHouse($grid, $x, $y + 1)){
							$pieces[] = new WoodlandMansionPiece("small_wall", WoodlandMansion::relative(WoodlandMansion::relative($pos, $rotation, WoodlandMansion::FACE_EAST, 6), $rotation, WoodlandMansion::FACE_SOUTH, 7), WoodlandMansion::rotateBy($rotation, WoodlandMansion::ROTATE_90));
						}
						if(!WoodlandMansionGrid::isHouse($grid, $x + 1, $y)){
							if(!WoodlandMansionGrid::isHouse($grid, $x, $y - 1)){
								$pieces[] = new WoodlandMansionPiece("small_wall_corner", WoodlandMansion::relative(WoodlandMansion::relative($pos, $rotation, WoodlandMansion::FACE_EAST, 7), $rotation, WoodlandMansion::FACE_NORTH, 2), $rotation);
							}
							if(!WoodlandMansionGrid::isHouse($grid, $x, $y + 1)){
								$pieces[] = new WoodlandMansionPiece("small_wall_corner", WoodlandMansion::relative(WoodlandMansion::relative($pos, $rotation, WoodlandMansion::FACE_EAST, 8), $rotation, WoodlandMansion::FACE_SOUTH, 7), WoodlandMansion::rotateBy($rotation, WoodlandMansion::ROTATE_90));
							}
						}
						if(!WoodlandMansionGrid::isHouse($grid, $x - 1, $y)){
							if(!WoodlandMansionGrid::isHouse($grid, $x, $y - 1)){
								$pieces[] = new WoodlandMansionPiece("small_wall_corner", WoodlandMansion::relative(WoodlandMansion::relative($pos, $rotation, WoodlandMansion::FACE_WEST, 2), $rotation, WoodlandMansion::FACE_NORTH, 1), WoodlandMansion::rotateBy($rotation, WoodlandMansion::ROTATE_270));
							}
							if(!WoodlandMansionGrid::isHouse($grid, $x, $y + 1)){
								$pieces[] = new WoodlandMansionPiece("small_wall_corner", WoodlandMansion::relative(WoodlandMansion::relative($pos, $rotation, WoodlandMansion::FACE_WEST, 1), $rotation, WoodlandMansion::FACE_SOUTH, 8), WoodlandMansion::rotateBy($rotation, WoodlandMansion::ROTATE_180));
							}
						}
					}
				}
			}
		}
		for($y = 0; $y < $grid->height; ++$y){
			for($x = 0; $x < $grid->width; ++$x){
				$pos = WoodlandMansion::relative($roofOrigin, $rotation, WoodlandMansion::FACE_SOUTH, 8 + ($y - $this->startY) * 8);
				$pos = WoodlandMansion::relative($pos, $rotation, WoodlandMansion::FACE_EAST, ($x - $this->startX) * 8);
				$isAbove = $aboveGrid !== null && WoodlandMansionGrid::isHouse($aboveGrid, $x, $y);
				if(WoodlandMansionGrid::isHouse($grid, $x, $y) && !$isAbove){
					if(!WoodlandMansionGrid::isHouse($grid, $x + 1, $y)){
						$p2 = WoodlandMansion::relative($pos, $rotation, WoodlandMansion::FACE_EAST, 6);
						if(!WoodlandMansionGrid::isHouse($grid, $x, $y + 1)){
							$pieces[] = new WoodlandMansionPiece("roof_corner", WoodlandMansion::relative($p2, $rotation, WoodlandMansion::FACE_SOUTH, 6), $rotation);
						}elseif(WoodlandMansionGrid::isHouse($grid, $x + 1, $y + 1)){
							$pieces[] = new WoodlandMansionPiece("roof_inner_corner", WoodlandMansion::relative($p2, $rotation, WoodlandMansion::FACE_SOUTH, 5), $rotation);
						}
						if(!WoodlandMansionGrid::isHouse($grid, $x, $y - 1)){
							$pieces[] = new WoodlandMansionPiece("roof_corner", $p2, WoodlandMansion::rotateBy($rotation, WoodlandMansion::ROTATE_270));
						}elseif(WoodlandMansionGrid::isHouse($grid, $x + 1, $y - 1)){
							$pieces[] = new WoodlandMansionPiece("roof_inner_corner", WoodlandMansion::relative(WoodlandMansion::relative($pos, $rotation, WoodlandMansion::FACE_EAST, 9), $rotation, WoodlandMansion::FACE_NORTH, 2), WoodlandMansion::rotateBy($rotation, WoodlandMansion::ROTATE_90));
						}
					}
					if(!WoodlandMansionGrid::isHouse($grid, $x - 1, $y)){
						$p2 = WoodlandMansion::relative(WoodlandMansion::relative($pos, $rotation, WoodlandMansion::FACE_EAST, 0), $rotation, WoodlandMansion::FACE_SOUTH, 0);
						if(!WoodlandMansionGrid::isHouse($grid, $x, $y + 1)){
							$pieces[] = new WoodlandMansionPiece("roof_corner", WoodlandMansion::relative($p2, $rotation, WoodlandMansion::FACE_SOUTH, 6), WoodlandMansion::rotateBy($rotation, WoodlandMansion::ROTATE_90));
						}elseif(WoodlandMansionGrid::isHouse($grid, $x - 1, $y + 1)){
							$pieces[] = new WoodlandMansionPiece("roof_inner_corner", WoodlandMansion::relative(WoodlandMansion::relative($p2, $rotation, WoodlandMansion::FACE_SOUTH, 8), $rotation, WoodlandMansion::FACE_WEST, 3), WoodlandMansion::rotateBy($rotation, WoodlandMansion::ROTATE_270));
						}
						if(!WoodlandMansionGrid::isHouse($grid, $x, $y - 1)){
							$pieces[] = new WoodlandMansionPiece("roof_corner", $p2, WoodlandMansion::rotateBy($rotation, WoodlandMansion::ROTATE_180));
						}elseif(WoodlandMansionGrid::isHouse($grid, $x - 1, $y - 1)){
							$pieces[] = new WoodlandMansionPiece("roof_inner_corner", WoodlandMansion::relative($p2, $rotation, WoodlandMansion::FACE_SOUTH, 1), WoodlandMansion::rotateBy($rotation, WoodlandMansion::ROTATE_180));
						}
					}
				}
			}
		}
	}

	private function entrance(array &$pieces, WoodlandMansionPlacementData $data){
		$west = WoodlandMansion::faceOffset(WoodlandMansion::rotateFace(WoodlandMansion::FACE_WEST, $data->rotation));
		$pieces[] = new WoodlandMansionPiece("entrance", WoodlandMansion::add($data->position, $west[0] * 9, 0, $west[2] * 9), $data->rotation);
		$data->position = WoodlandMansion::relative($data->position, $data->rotation, WoodlandMansion::FACE_SOUTH, 16);
	}

	private function traverseWallPiece(array &$pieces, WoodlandMansionPlacementData $data){
		$pieces[] = new WoodlandMansionPiece($data->wallType, WoodlandMansion::relative($data->position, $data->rotation, WoodlandMansion::FACE_EAST, 7), $data->rotation);
		$data->position = WoodlandMansion::relative($data->position, $data->rotation, WoodlandMansion::FACE_SOUTH, 8);
	}

	private function traverseTurn(array &$pieces, WoodlandMansionPlacementData $data){
		$data->position = WoodlandMansion::relative($data->position, $data->rotation, WoodlandMansion::FACE_SOUTH, -1);
		$pieces[] = new WoodlandMansionPiece("wall_corner", $data->position, $data->rotation);
		$data->position = WoodlandMansion::relative($data->position, $data->rotation, WoodlandMansion::FACE_SOUTH, -7);
		$data->position = WoodlandMansion::relative($data->position, $data->rotation, WoodlandMansion::FACE_WEST, -6);
		$data->rotation = WoodlandMansion::rotateBy($data->rotation, WoodlandMansion::ROTATE_90);
	}

	private function traverseInnerTurn(array &$pieces, WoodlandMansionPlacementData $data){
		$data->position = WoodlandMansion::relative($data->position, $data->rotation, WoodlandMansion::FACE_SOUTH, 6);
		$data->position = WoodlandMansion::relative($data->position, $data->rotation, WoodlandMansion::FACE_EAST, 8);
		$data->rotation = WoodlandMansion::rotateBy($data->rotation, WoodlandMansion::ROTATE_270);
	}

	private function addRoom1x1(array &$pieces, array $roomPos, int $rotation, $doorDir, WoodlandMansionFloorRoomCollection $rooms){
		$pieceRot = WoodlandMansion::ROTATE_NONE;
		$roomType = $rooms->get1x1($this->random);
		if($doorDir !== WoodlandMansion::FACE_EAST){
			if($doorDir === WoodlandMansion::FACE_NORTH){
				$pieceRot = WoodlandMansion::rotateBy($pieceRot, WoodlandMansion::ROTATE_270);
			}elseif($doorDir === WoodlandMansion::FACE_WEST){
				$pieceRot = WoodlandMansion::rotateBy($pieceRot, WoodlandMansion::ROTATE_180);
			}elseif($doorDir === WoodlandMansion::FACE_SOUTH){
				$pieceRot = WoodlandMansion::rotateBy($pieceRot, WoodlandMansion::ROTATE_90);
			}else{
				$roomType = $rooms->get1x1Secret($this->random);
			}
		}
		$orientation = WoodlandMansion::getZeroPositionWithTransform([1, 0, 0], WoodlandMansion::MIRROR_NONE, $pieceRot, 7, 7);
		$pieceRot = WoodlandMansion::rotateBy($pieceRot, $rotation);
		$orientation = WoodlandMansion::rotateOffset($orientation, $rotation);
		$pieces[] = new WoodlandMansionPiece($roomType, WoodlandMansion::add($roomPos, $orientation[0], 0, $orientation[2]), $pieceRot);
	}

	private function addRoom1x2(array &$pieces, array $roomPos, int $rotation, $roomDir, $doorDir, WoodlandMansionFloorRoomCollection $rooms, bool $isStairsRoom){
		if($roomDir === null || $doorDir === null){
			return;
		}
		if($doorDir === WoodlandMansion::FACE_EAST && $roomDir === WoodlandMansion::FACE_SOUTH){
			$pieces[] = new WoodlandMansionPiece($rooms->get1x2SideEntrance($this->random, $isStairsRoom), WoodlandMansion::relative($roomPos, $rotation, WoodlandMansion::FACE_EAST, 1), $rotation);
		}elseif($doorDir === WoodlandMansion::FACE_EAST && $roomDir === WoodlandMansion::FACE_NORTH){
			$pieces[] = new WoodlandMansionPiece($rooms->get1x2SideEntrance($this->random, $isStairsRoom), WoodlandMansion::relative(WoodlandMansion::relative($roomPos, $rotation, WoodlandMansion::FACE_EAST, 1), $rotation, WoodlandMansion::FACE_SOUTH, 6), $rotation, WoodlandMansion::MIRROR_LEFT_RIGHT);
		}elseif($doorDir === WoodlandMansion::FACE_WEST && $roomDir === WoodlandMansion::FACE_NORTH){
			$pieces[] = new WoodlandMansionPiece($rooms->get1x2SideEntrance($this->random, $isStairsRoom), WoodlandMansion::relative(WoodlandMansion::relative($roomPos, $rotation, WoodlandMansion::FACE_EAST, 7), $rotation, WoodlandMansion::FACE_SOUTH, 6), WoodlandMansion::rotateBy($rotation, WoodlandMansion::ROTATE_180));
		}elseif($doorDir === WoodlandMansion::FACE_WEST && $roomDir === WoodlandMansion::FACE_SOUTH){
			$pieces[] = new WoodlandMansionPiece($rooms->get1x2SideEntrance($this->random, $isStairsRoom), WoodlandMansion::relative($roomPos, $rotation, WoodlandMansion::FACE_EAST, 7), $rotation, WoodlandMansion::MIRROR_FRONT_BACK);
		}elseif($doorDir === WoodlandMansion::FACE_SOUTH && $roomDir === WoodlandMansion::FACE_EAST){
			$pieces[] = new WoodlandMansionPiece($rooms->get1x2SideEntrance($this->random, $isStairsRoom), WoodlandMansion::relative($roomPos, $rotation, WoodlandMansion::FACE_EAST, 1), WoodlandMansion::rotateBy($rotation, WoodlandMansion::ROTATE_90), WoodlandMansion::MIRROR_LEFT_RIGHT);
		}elseif($doorDir === WoodlandMansion::FACE_SOUTH && $roomDir === WoodlandMansion::FACE_WEST){
			$pieces[] = new WoodlandMansionPiece($rooms->get1x2SideEntrance($this->random, $isStairsRoom), WoodlandMansion::relative($roomPos, $rotation, WoodlandMansion::FACE_EAST, 7), WoodlandMansion::rotateBy($rotation, WoodlandMansion::ROTATE_90));
		}elseif($doorDir === WoodlandMansion::FACE_NORTH && $roomDir === WoodlandMansion::FACE_WEST){
			$pieces[] = new WoodlandMansionPiece($rooms->get1x2SideEntrance($this->random, $isStairsRoom), WoodlandMansion::relative(WoodlandMansion::relative($roomPos, $rotation, WoodlandMansion::FACE_EAST, 7), $rotation, WoodlandMansion::FACE_SOUTH, 6), WoodlandMansion::rotateBy($rotation, WoodlandMansion::ROTATE_90), WoodlandMansion::MIRROR_FRONT_BACK);
		}elseif($doorDir === WoodlandMansion::FACE_NORTH && $roomDir === WoodlandMansion::FACE_EAST){
			$pieces[] = new WoodlandMansionPiece($rooms->get1x2SideEntrance($this->random, $isStairsRoom), WoodlandMansion::relative(WoodlandMansion::relative($roomPos, $rotation, WoodlandMansion::FACE_EAST, 1), $rotation, WoodlandMansion::FACE_SOUTH, 6), WoodlandMansion::rotateBy($rotation, WoodlandMansion::ROTATE_270));
		}elseif($doorDir === WoodlandMansion::FACE_SOUTH && $roomDir === WoodlandMansion::FACE_NORTH){
			$pieces[] = new WoodlandMansionPiece($rooms->get1x2FrontEntrance($this->random, $isStairsRoom), WoodlandMansion::relative(WoodlandMansion::relative($roomPos, $rotation, WoodlandMansion::FACE_EAST, 1), $rotation, WoodlandMansion::FACE_NORTH, 8), $rotation);
		}elseif($doorDir === WoodlandMansion::FACE_NORTH && $roomDir === WoodlandMansion::FACE_SOUTH){
			$pieces[] = new WoodlandMansionPiece($rooms->get1x2FrontEntrance($this->random, $isStairsRoom), WoodlandMansion::relative(WoodlandMansion::relative($roomPos, $rotation, WoodlandMansion::FACE_EAST, 7), $rotation, WoodlandMansion::FACE_SOUTH, 14), WoodlandMansion::rotateBy($rotation, WoodlandMansion::ROTATE_180));
		}elseif($doorDir === WoodlandMansion::FACE_WEST && $roomDir === WoodlandMansion::FACE_EAST){
			$pieces[] = new WoodlandMansionPiece($rooms->get1x2FrontEntrance($this->random, $isStairsRoom), WoodlandMansion::relative($roomPos, $rotation, WoodlandMansion::FACE_EAST, 15), WoodlandMansion::rotateBy($rotation, WoodlandMansion::ROTATE_90));
		}elseif($doorDir === WoodlandMansion::FACE_EAST && $roomDir === WoodlandMansion::FACE_WEST){
			$pieces[] = new WoodlandMansionPiece($rooms->get1x2FrontEntrance($this->random, $isStairsRoom), WoodlandMansion::relative(WoodlandMansion::relative($roomPos, $rotation, WoodlandMansion::FACE_WEST, 7), $rotation, WoodlandMansion::FACE_SOUTH, 6), WoodlandMansion::rotateBy($rotation, WoodlandMansion::ROTATE_270));
		}elseif($doorDir === WoodlandMansion::FACE_UP && $roomDir === WoodlandMansion::FACE_EAST){
			$pieces[] = new WoodlandMansionPiece($rooms->get1x2Secret($this->random), WoodlandMansion::relative($roomPos, $rotation, WoodlandMansion::FACE_EAST, 15), WoodlandMansion::rotateBy($rotation, WoodlandMansion::ROTATE_90));
		}elseif($doorDir === WoodlandMansion::FACE_UP && $roomDir === WoodlandMansion::FACE_SOUTH){
			$pieces[] = new WoodlandMansionPiece($rooms->get1x2Secret($this->random), WoodlandMansion::relative(WoodlandMansion::relative($roomPos, $rotation, WoodlandMansion::FACE_EAST, 1), $rotation, WoodlandMansion::FACE_NORTH, 0), $rotation);
		}
	}

	private function addRoom2x2(array &$pieces, array $roomPos, int $rotation, string $roomDir, string $doorDir, WoodlandMansionFloorRoomCollection $rooms){
		$east = 0;
		$south = 0;
		$rot = $rotation;
		$mirror = WoodlandMansion::MIRROR_NONE;
		if($doorDir === WoodlandMansion::FACE_EAST && $roomDir === WoodlandMansion::FACE_SOUTH){
			$east = -7;
		}elseif($doorDir === WoodlandMansion::FACE_EAST && $roomDir === WoodlandMansion::FACE_NORTH){
			$east = -7; $south = 6; $mirror = WoodlandMansion::MIRROR_LEFT_RIGHT;
		}elseif($doorDir === WoodlandMansion::FACE_NORTH && $roomDir === WoodlandMansion::FACE_EAST){
			$east = 1; $south = 14; $rot = WoodlandMansion::rotateBy($rotation, WoodlandMansion::ROTATE_270);
		}elseif($doorDir === WoodlandMansion::FACE_NORTH && $roomDir === WoodlandMansion::FACE_WEST){
			$east = 7; $south = 14; $rot = WoodlandMansion::rotateBy($rotation, WoodlandMansion::ROTATE_270); $mirror = WoodlandMansion::MIRROR_LEFT_RIGHT;
		}elseif($doorDir === WoodlandMansion::FACE_SOUTH && $roomDir === WoodlandMansion::FACE_WEST){
			$east = 7; $south = -8; $rot = WoodlandMansion::rotateBy($rotation, WoodlandMansion::ROTATE_90);
		}elseif($doorDir === WoodlandMansion::FACE_SOUTH && $roomDir === WoodlandMansion::FACE_EAST){
			$east = 1; $south = -8; $rot = WoodlandMansion::rotateBy($rotation, WoodlandMansion::ROTATE_90); $mirror = WoodlandMansion::MIRROR_LEFT_RIGHT;
		}elseif($doorDir === WoodlandMansion::FACE_WEST && $roomDir === WoodlandMansion::FACE_NORTH){
			$east = 15; $south = 6; $rot = WoodlandMansion::rotateBy($rotation, WoodlandMansion::ROTATE_180);
		}elseif($doorDir === WoodlandMansion::FACE_WEST && $roomDir === WoodlandMansion::FACE_SOUTH){
			$east = 15; $mirror = WoodlandMansion::MIRROR_FRONT_BACK;
		}
		$pos = WoodlandMansion::relative($roomPos, $rotation, WoodlandMansion::FACE_EAST, $east);
		$pos = WoodlandMansion::relative($pos, $rotation, WoodlandMansion::FACE_SOUTH, $south);
		$pieces[] = new WoodlandMansionPiece($rooms->get2x2($this->random), $pos, $rot, $mirror);
	}

	private function addRoom2x2Secret(array &$pieces, array $roomPos, int $rotation, WoodlandMansionFloorRoomCollection $rooms){
		$pieces[] = new WoodlandMansionPiece($rooms->get2x2Secret($this->random), WoodlandMansion::relative($roomPos, $rotation, WoodlandMansion::FACE_EAST, 1), $rotation);
	}

	private function applyTemplateRotationOffset(string $templateName, array $pos, int $mansionRotation) : array{
		$template = WoodlandMansionStructureRegistry::get($templateName);
		$localOffset = WoodlandMansion::getZeroPositionWithTransform([0, 0, 0], WoodlandMansion::MIRROR_NONE, WoodlandMansion::ROTATE_180, (int) $template["size"][0], (int) $template["size"][2]);
		$adjusted = WoodlandMansion::relative($pos, $mansionRotation, WoodlandMansion::FACE_EAST, $localOffset[0]);
		return WoodlandMansion::relative($adjusted, $mansionRotation, WoodlandMansion::FACE_SOUTH, $localOffset[2]);
	}
}

final class WoodlandMansionPiece{
	private $templateName;
	private $template;
	private $position;
	private $rotation;
	private $mirror;
	private $boundingBox;

	public function __construct(string $templateName, array $position, int $rotation, int $mirror = WoodlandMansion::MIRROR_NONE){
		$this->templateName = $templateName;
		$this->template = WoodlandMansionStructureRegistry::get($templateName);
		if($this->template === null){
			throw new \RuntimeException("Missing Woodland Mansion structure part: " . $templateName);
		}
		$this->position = $position;
		$this->rotation = $rotation;
		$this->mirror = $mirror;
		$this->boundingBox = WoodlandMansion::createBoundingBox($position, $this->template, $rotation, $mirror);
	}

	public function place(ChunkManager $level, array &$chests, array &$markers, array &$spawners){
		foreach($this->template["blocks"] as $block){
			$state = $block["state"];
			$local = WoodlandMansion::transformLocalVanilla((int) $block["x"], (int) $block["z"], $this->mirror, $this->rotation);
			$wx = $this->position[0] + $local[0];
			$wy = $this->position[1] + (int) $block["y"];
			$wz = $this->position[2] + $local[1];
			if(($state["name"] ?? "") === "minecraft:structure_block"){
				$markers[] = [$wx, $wy, $wz];
				continue;
			}
			$legacy = WoodlandMansion::toLegacy($state, $this->rotation, $this->mirror);
			if($legacy === null){
				continue;
			}
			$id = (int) $legacy[0];
			$data = (int) $legacy[1];
			$level->setBlockIdAt($wx, $wy, $wz, $id);
			$level->setBlockDataAt($wx, $wy, $wz, $data);
			if($id === Block::CHEST){
				$chests[] = [$wx, $wy, $wz];
			}elseif($id === Block::MONSTER_SPAWNER){
				$spawners[] = [$wx, $wy, $wz];
			}
		}
	}

	public function getTemplateName() : string{
		return $this->templateName;
	}

	public function getBoundingBox() : array{
		return $this->boundingBox;
	}

	public function contains(int $x, int $y, int $z) : bool{
		return $x >= $this->boundingBox[0] && $x <= $this->boundingBox[3] &&
			$y >= $this->boundingBox[1] && $y <= $this->boundingBox[4] &&
			$z >= $this->boundingBox[2] && $z <= $this->boundingBox[5];
	}

	public function toArray() : array{
		return [
			"name" => $this->templateName,
			"position" => $this->position,
			"rotation" => $this->rotation,
			"mirror" => $this->mirror,
			"boundingBox" => $this->boundingBox,
		];
	}
}
