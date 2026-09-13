<?php

namespace pocketmine\level\generator\normal\object;

require_once __DIR__ . "/PillagerOutpostLoot.php";
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

class PillagerOutpost extends PopulatorObject{
	const WIDTH_X = 15;
	const WIDTH_Z = 15;
	const HEIGHT = 21;
	const FEATURE_RANDOM_SALT = 0x5deece66;
	const OUTPOST_STRAY_MARKER_COUNT = 4;
	const MARKER_STRAY = 0x5053;
	const MARKER_STRAY_ID = 0x53;
	const MARKER_STRAY_DATA = 0x50;
	const MARKER_RAIDER_ZOMBIE = 0x505a;
	const MARKER_RAIDER_ZOMBIE_ID = 0x5a;
	const MARKER_RAIDER_ZOMBIE_DATA = 0x50;

	/** @var ChunkManager */
	private $level;
	private $originX;
	private $originY;
	private $originZ;
	/** @var array<int, array{name:string,states:array}> */
	private static $blockPalette;
	/** @var array<string, array> */
	private static $structureCache = [];

	public function placeObject(ChunkManager $level, $x, $y, $z, Random $random){
		$this->level = $level;
		$this->originX = (int) $x;
		$this->originY = (int) $y;
		$this->originZ = (int) $z;

		$this->placeTemplate("watchtower", $this->originX, $this->originY, $this->originZ, true);
		$this->placeOvergrownOverlay($random);
		$watchtower = self::getStructure("watchtower");
		$this->fillBase($this->originX, $this->originY, $this->originZ, (int) $watchtower["size"][0], (int) $watchtower["size"][2]);
		$this->placeSideFeatures();
		$this->markOutpostMobs();
	}

	private function markOutpostMobs(){
		$strays = [
			[$this->originX + 8, $this->originY + 13, $this->originZ + 8],
			[$this->originX + 4, $this->originY + 14, $this->originZ + 4],
			[$this->originX + 11, $this->originY + 14, $this->originZ + 4],
			[$this->originX + 11, $this->originY + 14, $this->originZ + 11],
		];

		foreach($strays as $pos){
			$this->markOutpostMob($pos[0], $pos[1], $pos[2], self::MARKER_STRAY_ID, self::MARKER_STRAY_DATA, self::MARKER_STRAY);
		}

		$this->markOutpostMob($this->originX + 8, $this->originY + 1, $this->originZ + 1, self::MARKER_RAIDER_ZOMBIE_ID, self::MARKER_RAIDER_ZOMBIE_DATA, self::MARKER_RAIDER_ZOMBIE);
	}

	private function placeOvergrownOverlay(Random $random){
		$template = self::getStructure("watchtower_overgrown");
		foreach($template["blocks"] as $entry){
			list($dx, $dy, $dz, $state) = $entry;
			$name = $state["name"];
			if($name === "minecraft:air" || $name === "minecraft:jigsaw" || $name === "minecraft:chest" || $name === "minecraft:wall_banner"){
				continue;
			}
			if($name !== "minecraft:mossy_cobblestone" && $name !== "minecraft:mossy_cobblestone_stairs" && $name !== "minecraft:mossy_cobblestone_slab" && $name !== "minecraft:mossy_cobblestone_wall" && $name !== "minecraft:vine"){
				continue;
			}
			if($random->nextBoundedInt(20) !== 0){
				continue;
			}
			$this->placeStateWorld($this->originX + $dx, $this->originY + $dy, $this->originZ + $dz, $state, false);
		}
	}

	private function placeSideFeatures(){
		$pnxFeatures = self::getPnxSideFeatureTemplates();
		$features = [
			[$pnxFeatures["tents"][0], $this->originX - 16, $this->originZ - 16, null],
			[$pnxFeatures["logs"][0], $this->originX - 16, $this->originZ + 16, [$this->originX - 4, $this->originZ + 18, Block::LOG2, null]],
			[$pnxFeatures["tents"][1], $this->originX + 16, $this->originZ - 16, null],
			[$pnxFeatures["cages"][0], $this->originX + 16, $this->originZ + 16, [$this->originX + 24, $this->originZ + 20, Block::FENCE, null]],
		];

		foreach($features as $feature){
			list($name, $chunkBaseX, $chunkBaseZ, $anchor) = $feature;
			$this->placeRequiredFeature($name, $chunkBaseX, $chunkBaseZ, $anchor);
		}

		$this->level->setBlockIdAt($this->originX + 23, $this->originY, $this->originZ + 20, Block::LOG2);
		$this->level->setBlockDataAt($this->originX + 23, $this->originY, $this->originZ + 20, 1);
	}

	private static function getPnxSideFeatureTemplates() : array{
		return [
			"cages" => ["feature_cage1", "feature_cage2", "feature_cage_with_allays"],
			"logs" => ["feature_logs"],
			"targets" => ["feature_targets"],
			"tents" => ["feature_tent1", "feature_tent2"],
		];
	}

	private function placeRequiredFeature(string $name, int $chunkBaseX, int $chunkBaseZ, $anchor){
		$template = self::getStructure($name);
		$sizeX = (int) $template["size"][0];
		$sizeZ = (int) $template["size"][2];
		$local = $anchor === null ? null : $this->findFeatureLocalForAnchor($template, $chunkBaseX, $chunkBaseZ, $anchor[0], $anchor[1], $anchor[2], $anchor[3]);
		if($local === null){
			$local = $this->defaultFeatureLocal($sizeX, $sizeZ);
		}

		$this->placeFeatureAt($name, $chunkBaseX + $local[0], $chunkBaseZ + $local[1]);
	}

	private function defaultFeatureLocal(int $sizeX, int $sizeZ) : array{
		return [
			max(0, intdiv(max(0, 16 - $sizeX), 2)),
			max(0, intdiv(max(0, 16 - $sizeZ), 2)),
		];
	}

	private function findFeatureLocalForAnchor(array $template, int $chunkBaseX, int $chunkBaseZ, int $targetX, int $targetZ, int $targetId, $targetData){
		$sizeX = (int) $template["size"][0];
		$sizeZ = (int) $template["size"][2];
		$maxX = max(1, 16 - $sizeX);
		$maxZ = max(1, 16 - $sizeZ);

		for($localX = 0; $localX < $maxX; ++$localX){
			for($localZ = 0; $localZ < $maxZ; ++$localZ){
				foreach($template["blocks"] as $entry){
					$legacy = $this->toLegacyBlock($entry[3]);
					if($legacy === null || $legacy[0] !== $targetId){
						continue;
					}
					if($targetData !== null && $legacy[1] !== $targetData){
						continue;
					}
					if($chunkBaseX + $localX + $entry[0] === $targetX && $chunkBaseZ + $localZ + $entry[2] === $targetZ){
						return [$localX, $localZ];
					}
				}
			}
		}

		return null;
	}

	private function placeFeatureAt(string $name, int $x, int $z){
		$template = self::getStructure($name);
		$sizeX = (int) $template["size"][0];
		$sizeZ = (int) $template["size"][2];
		$y = $this->getSurfaceY($x, $z);
		if($y <= 1){
			$y = $this->originY;
		}

		$this->placeTemplate($name, $x, $y, $z, true);
		$this->fillBase($x, $y, $z, $sizeX, $sizeZ);
	}

	private function placeTemplate(string $name, int $x, int $y, int $z, bool $skipAir){
		$template = self::getStructure($name);
		foreach($template["blocks"] as $entry){
			list($dx, $dy, $dz, $state) = $entry;
			$this->placeStateWorld($x + $dx, $y + $dy, $z + $dz, $state, $skipAir);
		}
	}

	private function placeStateWorld(int $x, int $y, int $z, array $state, bool $skipAir){
		$name = $state["name"];
		if($skipAir && $name === "minecraft:air"){
			return;
		}
		if($name === "minecraft:jigsaw" || $name === "minecraft:wall_banner"){
			return;
		}

		$legacy = $this->toLegacyBlock($state);
		if($legacy === null){
			return;
		}
		$this->level->setBlockIdAt($x, $y, $z, $legacy[0]);
		$this->level->setBlockDataAt($x, $y, $z, $legacy[1]);
		if($legacy[0] === Block::CHEST){
			$this->markExtraWorld($x, $y, $z, PillagerOutpostLoot::CHEST_MARKER_ID, PillagerOutpostLoot::CHEST_MARKER_DATA, PillagerOutpostLoot::CHEST_MARKER);
		}
	}

	private function toLegacyBlock(array $state){
		$name = $state["name"];
		$states = $state["states"];
		switch($name){
			case "minecraft:air":
				return [Block::AIR, 0];
			case "minecraft:cobblestone":
				return [Block::COBBLESTONE, 0];
			case "minecraft:mossy_cobblestone":
				return [Block::MOSS_STONE, 0];
			case "minecraft:birch_planks":
				return [Block::PLANKS, 2];
			case "minecraft:dark_oak_planks":
				return [Block::PLANKS, 5];
			case "minecraft:dark_oak_log":
				return [Block::LOG2, $this->axisMeta($states["pillar_axis"] ?? "y", 1)];
			case "minecraft:dark_oak_fence":
				return [Block::FENCE, 0];
			case "minecraft:dark_oak_slab":
				return [Block::WOOD_SLAB, 5 | $this->slabTopBit($states)];
			case "minecraft:cobblestone_slab":
				return [Block::SLAB, 3 | $this->slabTopBit($states)];
			case "minecraft:mossy_cobblestone_slab":
				return [Block::SLAB, 3 | $this->slabTopBit($states)];
			case "minecraft:stone_stairs":
				return [Block::COBBLESTONE_STAIRS, $this->stairMeta($states)];
			case "minecraft:mossy_cobblestone_stairs":
				return [Block::COBBLESTONE_STAIRS, $this->stairMeta($states)];
			case "minecraft:dark_oak_stairs":
				return [Block::DARK_OAK_WOOD_STAIRS, $this->stairMeta($states)];
			case "minecraft:cobblestone_wall":
				return [Block::STONE_WALL, 0];
			case "minecraft:mossy_cobblestone_wall":
				return [Block::STONE_WALL, 1];
			case "minecraft:torch":
				return [Block::TORCH, $this->torchMeta($states["torch_facing_direction"] ?? "top")];
			case "minecraft:vine":
				return [Block::VINE, (int) ($states["vine_direction_bits"] ?? 0)];
			case "minecraft:chest":
				return [Block::CHEST, $this->cardinalMeta($states["minecraft:cardinal_direction"] ?? "south")];
			case "minecraft:crafting_table":
				return [Block::CRAFTING_TABLE, 0];
			case "minecraft:white_wool":
				return [Block::WOOL, 0];
			case "minecraft:pumpkin":
			case "minecraft:carved_pumpkin":
				return [Block::PUMPKIN, $this->pumpkinMeta($states["minecraft:cardinal_direction"] ?? "south")];
			case "minecraft:hay_block":
				return [Block::HAY_BALE, $this->axisMeta($states["pillar_axis"] ?? "y", 0)];
		}

		return null;
	}

	private function fillBase(int $x, int $baseY, int $z, int $sizeX, int $sizeZ){
		for($dx = 0; $dx < $sizeX; ++$dx){
			for($dz = 0; $dz < $sizeZ; ++$dz){
				$worldX = $x + $dx;
				$worldZ = $z + $dz;
				$baseId = $this->level->getBlockIdAt($worldX, $baseY, $worldZ);
				$baseMeta = $this->level->getBlockDataAt($worldX, $baseY, $worldZ);
				if(!$this->isBaseSupportBlock($baseId)){
					continue;
				}
				for($y = $baseY - 1; $y > 1; --$y){
					if(!$this->isReplaceable($this->level->getBlockIdAt($worldX, $y, $worldZ))){
						break;
					}
					$this->level->setBlockIdAt($worldX, $y, $worldZ, $baseId);
					$this->level->setBlockDataAt($worldX, $y, $worldZ, $baseMeta);
				}
			}
		}
	}

	private function isBaseSupportBlock(int $id) : bool{
		return $id === Block::COBBLESTONE ||
			$id === Block::MOSS_STONE ||
			$id === Block::LOG ||
			$id === Block::LOG2 ||
			$id === Block::PLANKS ||
			$id === Block::FENCE;
	}

	private function isReplaceable(int $id) : bool{
		return $id === Block::AIR || $id === Block::WATER || $id === Block::STILL_WATER || $id === Block::LAVA || $id === Block::STILL_LAVA || $id === Block::TALL_GRASS || $id === Block::DOUBLE_PLANT || $id === Block::SNOW_LAYER;
	}

	private function getSurfaceY(int $x, int $z) : int{
		for($y = 127; $y > 0; --$y){
			$id = $this->level->getBlockIdAt($x, $y, $z);
			if(!$this->isReplaceable($id)){
				return $y;
			}
		}
		return -1;
	}

	private function slabTopBit(array $states) : int{
		return (($states["minecraft:vertical_half"] ?? "bottom") === "top") ? 0x08 : 0;
	}

	private function axisMeta($axis, int $baseMeta) : int{
		switch((string) $axis){
			case "x":
				return $baseMeta | 0x04;
			case "z":
				return $baseMeta | 0x08;
			case "y":
			default:
				return $baseMeta;
		}
	}

	private function stairMeta(array $states) : int{
		$meta = (int) ($states["weirdo_direction"] ?? 0);
		if(!empty($states["upside_down_bit"])){
			$meta |= 0x04;
		}
		return $meta;
	}

	private function torchMeta($direction) : int{
		switch((string) $direction){
			case "west":
				return 1;
			case "east":
				return 2;
			case "north":
				return 3;
			case "south":
				return 4;
			case "top":
			case "unknown":
			default:
				return 0;
		}
	}

	private function cardinalMeta($direction) : int{
		switch((string) $direction){
			case "north":
				return 2;
			case "south":
				return 3;
			case "west":
				return 4;
			case "east":
			default:
				return 5;
		}
	}

	private function pumpkinMeta($direction) : int{
		switch((string) $direction){
			case "south":
				return 0;
			case "west":
				return 1;
			case "north":
				return 2;
			case "east":
			default:
				return 3;
		}
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

	private function markOutpostMob(int $x, int $y, int $z, int $id, int $data, int $marker){
		$this->markExtraWorld($x, $y, $z, $id, $data, $marker);
	}

	private static function getStructure(string $name) : array{
		if(!isset(self::$structureCache[$name])){
			self::loadStructures();
		}
		return self::$structureCache[$name];
	}

	private static function loadStructures(){
		if(self::$blockPalette === null){
			self::$blockPalette = self::loadBlockPalette();
		}

		$root = self::readCompressedNbt(self::resourcePath("gamedata/unknown/structures.nbt"));
		$outpost = $root->pillager_outpost ?? null;
		if(!$outpost instanceof CompoundTag){
			throw new \RuntimeException("PNX pillager_outpost structures are missing");
		}

		foreach($outpost as $name => $container){
			if(!$container instanceof CompoundTag || !($container->object ?? null) instanceof CompoundTag){
				continue;
			}
			self::$structureCache[(string) $name] = self::decodeStructure($container->object);
		}
	}

	private static function decodeStructure(CompoundTag $object) : array{
		$sizeTag = $object->size ?? null;
		$paletteTag = $object->palette ?? null;
		$blocksTag = $object->blocks ?? null;
		if(!$sizeTag instanceof IntArrayTag || !$paletteTag instanceof ListTag || !$blocksTag instanceof ByteArrayTag){
			throw new \RuntimeException("Invalid PNX outpost structure object");
		}

		$size = $sizeTag->getValue();
		$palette = [];
		foreach($paletteTag as $entry){
			if($entry instanceof IntTag){
				$hash = $entry->getValue();
				$palette[] = self::$blockPalette[$hash] ?? ["name" => "minecraft:air", "states" => []];
			}
		}

		$blocks = [];
		$raw = $blocksTag->getValue();
		$sizeX = (int) $size[0];
		$sizeY = (int) $size[1];
		for($index = 0, $len = strlen($raw); $index < $len; ++$index){
			$paletteIndex = ord($raw[$index]) - 1;
			if($paletteIndex < 0 || !isset($palette[$paletteIndex])){
				continue;
			}
			$blocks[] = [
				$index % $sizeX,
				intdiv($index, $sizeX) % $sizeY,
				intdiv($index, $sizeX * $sizeY),
				$palette[$paletteIndex],
			];
		}

		return ["size" => $size, "blocks" => $blocks];
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
			$hash = self::compoundInt($entry, "network_id");
			$palette[$hash] = [
				"name" => self::compoundString($entry, "name"),
				"states" => ($entry->states ?? null) instanceof CompoundTag ? self::compoundToArray($entry->states) : [],
			];
		}
		return $palette;
	}

	private static function readCompressedNbt(string $path) : CompoundTag{
		$bytes = @file_get_contents($path);
		if($bytes === false){
			throw new \RuntimeException("Unable to read PNX structure resource: " . $path);
		}
		$raw = gzdecode($bytes);
		if($raw === false){
			throw new \RuntimeException("Unable to decompress PNX structure resource: " . $path);
		}
		$nbt = new NBT(NBT::BIG_ENDIAN);
		$nbt->read($raw);
		$data = $nbt->getData();
		if(!$data instanceof CompoundTag){
			throw new \RuntimeException("Unexpected PNX structure NBT root");
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
		if($child instanceof IntTag){
			return $child->getValue();
		}
		return 0;
	}

	private static function compoundString(CompoundTag $tag, string $name) : string{
		$child = $tag->{$name} ?? null;
		if($child instanceof StringTag){
			return $child->getValue();
		}
		return "";
	}

	private static function resourcePath(string $relative) : string{
		return dirname(__DIR__, 4) . "/resources/" . $relative;
	}
}
