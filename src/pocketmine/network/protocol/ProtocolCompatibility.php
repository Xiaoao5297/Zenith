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

namespace pocketmine\network\protocol;

use pocketmine\entity\Entity;
use pocketmine\inventory\BigShapelessRecipe;
use pocketmine\inventory\FurnaceRecipe;
use pocketmine\inventory\ShapedRecipe;
use pocketmine\inventory\ShapedRecipeFromJson;
use pocketmine\inventory\ShapelessRecipe;
use pocketmine\item\Arrow;
use pocketmine\item\enchantment\Enchantment;
use pocketmine\item\Item;
use pocketmine\nbt\NBT;
use pocketmine\nbt\tag\CompoundTag;
use pocketmine\nbt\tag\StringTag;
use pocketmine\utils\Binary;
use pocketmine\utils\TextFormat;

final class ProtocolCompatibility{
	private const CHUNK_BLOCK_IDS_LENGTH = 32768;
	private const CHUNK_BLOCK_DATA_LENGTH = 16384;
	private const CHUNK_EXTRA_DATA_OFFSET = 83200;
	private const ENTITY_DATA_NAMETAG = 2;
	private const ENTITY_DATA_SHOW_NAMETAG = 3;
	private const ENTITY_DATA_POTION_ID = 16;
	private const ENTITY_DATA_LEAD_HOLDER = 23;
	private const ENTITY_DATA_LEAD = 24;
	private const ENTITY_DATA_CREEPER_SWELL_DIRECTION = 16;
	private const ENTITY_DATA_CREEPER_SWELL = 17;
	private const ENTITY_DATA_CREEPER_SWELL_2 = 18;
	private const ENTITY_DATA_CREEPER_POWERED = 19;
	private const ENTITY_TYPE_ARROW = 80;
	private const ENTITY_TYPE_CREEPER = 33;
	private const ENTITY_TYPE_ZOMBIE = 32;
	private const ENTITY_TYPE_ZOMBIE_VILLAGER = 44;
	private const LEVEL_EVENT_PARTICLE_DESTROY = 2001;

	private const DATA_TYPE_BYTE = 0;
	private const DATA_TYPE_SHORT = 1;
	private const DATA_TYPE_INT = 2;
	private const DATA_TYPE_FLOAT = 3;
	private const DATA_TYPE_STRING = 4;
	private const DATA_TYPE_SLOT = 5;
	private const DATA_TYPE_POS = 6;
	private const DATA_TYPE_LONG = 7;

	private static $restrictedChunkBlockBytes = [];

	private const LEGACY_METADATA_TYPES = [
		0 => self::DATA_TYPE_BYTE,
		1 => self::DATA_TYPE_SHORT,
		2 => self::DATA_TYPE_STRING,
		3 => self::DATA_TYPE_BYTE,
	];

	private const LEGACY_011_METADATA_TYPES = [
		0 => self::DATA_TYPE_BYTE,
		1 => self::DATA_TYPE_SHORT,
		2 => self::DATA_TYPE_STRING,
		3 => self::DATA_TYPE_BYTE,
		4 => self::DATA_TYPE_BYTE,
		7 => self::DATA_TYPE_INT,
		8 => self::DATA_TYPE_BYTE,
		14 => self::DATA_TYPE_BYTE,
		15 => self::DATA_TYPE_BYTE,
		16 => self::DATA_TYPE_BYTE,
		17 => null,
		20 => self::DATA_TYPE_INT,
	];

	private const LEGACY_011_BLOCK_ID_MAP = [
		27 => [66, 0],
		28 => [66, 0],
		88 => [12, 0],
		113 => [85, 0],
		115 => [0, 0],
		116 => [247, 0],
		117 => [120, 0],
		126 => [66, 0],
		140 => [0, 0],
		144 => [0, 0],
		145 => [42, 0],
		146 => [54, 0],
		153 => [87, 0],
		174 => [79, 0],
	];

	private const LEGACY_011_ITEM_ID_MAP = [
		406 => [1, 0],
	];

	private const LEGACY_012_BLOCK_ID_MAP = [
		25 => [5, 0],
		28 => [66, 0],
		69 => [63, 0],
		75 => [50, 0],
		76 => [50, 0],
		77 => [63, 0],
		123 => [89, 0],
		124 => [1, 0],
		126 => [66, 0],
		143 => [63, 0],
		146 => [54, 0],
		151 => [44, 0],
		167 => [96, 0],
		178 => [44, 0],
	];

	private const LEGACY_012_PRESSURE_PLATES = [70, 72, 147, 148];
	private const LEGACY_012_DOORS = [193, 194, 195, 196, 197];
	private const LEGACY_011_MAPPED_INTERACTIVE_BLOCK_IDS = [
		116 => true,
		145 => true,
	];

	private const LEGACY_012_PLACEHOLDER_ITEM_IDS = [
		411 => true,
		412 => true,
		413 => true,
		414 => true,
		415 => true,
	];

	private const LEGACY_012_WOODEN_DOOR_ITEM_IDS = [
		427 => true,
		428 => true,
		429 => true,
		430 => true,
		431 => true,
	];

	private const LEGACY_012_REGISTERED_RECIPE_ITEM_IDS = [
		1 => true,
		2 => true,
		3 => true,
		4 => true,
		5 => true,
		6 => true,
		7 => true,
		8 => true,
		9 => true,
		10 => true,
		11 => true,
		12 => true,
		13 => true,
		14 => true,
		15 => true,
		16 => true,
		17 => true,
		18 => true,
		19 => true,
		20 => true,
		21 => true,
		22 => true,
		24 => true,
		26 => true,
		27 => true,
		30 => true,
		31 => true,
		32 => true,
		35 => true,
		37 => true,
		38 => true,
		39 => true,
		40 => true,
		41 => true,
		42 => true,
		43 => true,
		44 => true,
		45 => true,
		46 => true,
		47 => true,
		48 => true,
		49 => true,
		50 => true,
		51 => true,
		52 => true,
		53 => true,
		54 => true,
		56 => true,
		57 => true,
		58 => true,
		59 => true,
		60 => true,
		61 => true,
		62 => true,
		63 => true,
		64 => true,
		65 => true,
		66 => true,
		67 => true,
		68 => true,
		71 => true,
		73 => true,
		74 => true,
		78 => true,
		79 => true,
		80 => true,
		81 => true,
		82 => true,
		83 => true,
		85 => true,
		86 => true,
		87 => true,
		88 => true,
		89 => true,
		91 => true,
		92 => true,
		96 => true,
		98 => true,
		101 => true,
		102 => true,
		103 => true,
		104 => true,
		105 => true,
		106 => true,
		107 => true,
		108 => true,
		109 => true,
		110 => true,
		111 => true,
		112 => true,
		113 => true,
		114 => true,
		116 => true,
		120 => true,
		121 => true,
		128 => true,
		129 => true,
		133 => true,
		134 => true,
		135 => true,
		136 => true,
		139 => true,
		141 => true,
		142 => true,
		145 => true,
		152 => true,
		155 => true,
		156 => true,
		157 => true,
		158 => true,
		159 => true,
		161 => true,
		162 => true,
		163 => true,
		164 => true,
		170 => true,
		171 => true,
		172 => true,
		173 => true,
		174 => true,
		175 => true,
		183 => true,
		184 => true,
		185 => true,
		186 => true,
		187 => true,
		198 => true,
		243 => true,
		244 => true,
		245 => true,
		246 => true,
		247 => true,
		256 => true,
		257 => true,
		258 => true,
		259 => true,
		260 => true,
		261 => true,
		262 => true,
		263 => true,
		264 => true,
		265 => true,
		266 => true,
		267 => true,
		268 => true,
		269 => true,
		270 => true,
		271 => true,
		272 => true,
		273 => true,
		274 => true,
		275 => true,
		276 => true,
		277 => true,
		278 => true,
		279 => true,
		280 => true,
		281 => true,
		282 => true,
		283 => true,
		284 => true,
		285 => true,
		286 => true,
		287 => true,
		288 => true,
		289 => true,
		290 => true,
		291 => true,
		292 => true,
		293 => true,
		294 => true,
		295 => true,
		296 => true,
		297 => true,
		298 => true,
		299 => true,
		300 => true,
		301 => true,
		302 => true,
		303 => true,
		304 => true,
		305 => true,
		306 => true,
		307 => true,
		308 => true,
		309 => true,
		310 => true,
		311 => true,
		312 => true,
		313 => true,
		314 => true,
		315 => true,
		316 => true,
		317 => true,
		318 => true,
		319 => true,
		320 => true,
		321 => true,
		322 => true,
		323 => true,
		324 => true,
		325 => true,
		328 => true,
		330 => true,
		331 => true,
		332 => true,
		334 => true,
		336 => true,
		337 => true,
		338 => true,
		339 => true,
		340 => true,
		341 => true,
		344 => true,
		345 => true,
		347 => true,
		348 => true,
		349 => true,
		350 => true,
		351 => true,
		352 => true,
		353 => true,
		354 => true,
		355 => true,
		357 => true,
		359 => true,
		360 => true,
		361 => true,
		362 => true,
		363 => true,
		364 => true,
		365 => true,
		366 => true,
		371 => true,
		383 => true,
		388 => true,
		391 => true,
		392 => true,
		393 => true,
		400 => true,
		405 => true,
		406 => true,
		456 => true,
		457 => true,
		458 => true,
		459 => true,
	];

	private const PROTOCOL_015_METADATA_TYPES = [
		0 => self::DATA_TYPE_BYTE,
		1 => self::DATA_TYPE_SHORT,
		2 => self::DATA_TYPE_STRING,
		3 => self::DATA_TYPE_BYTE,
		4 => self::DATA_TYPE_BYTE,
		7 => self::DATA_TYPE_INT,
		8 => self::DATA_TYPE_BYTE,
		14 => self::DATA_TYPE_BYTE,
		15 => self::DATA_TYPE_BYTE,
		16 => null,
		17 => null,
		18 => self::DATA_TYPE_BYTE,
		19 => null,
		20 => null,
		21 => self::DATA_TYPE_BYTE,
		23 => self::DATA_TYPE_LONG,
		24 => self::DATA_TYPE_BYTE,
	];

	private const LEGACY_013_BLOCK_ID_MAP = [
		23 => [4, 0],
		36 => [0, 0],
		93 => [44, 0],
		94 => [44, 0],
		95 => [7, 0],
		97 => [1, 0],
		125 => [4, 0],
		131 => [0, 0],
		132 => [0, 0],
		149 => [44, 0],
		150 => [44, 0],
		154 => [1, 0],
		165 => [26, 8],
		199 => [0, 0],
		439 => [0, 0],
	];

	private const LEGACY_REDSTONE_BLOCK_ID_MAP = [
		29 => [4, 0],
		33 => [4, 0],
		34 => [85, 0],
		36 => [0, 0],
		251 => [247, 0],
	];

	private const LEGACY_013_BLOCK_STATE_MAP_IDS = [
		12 => true,
		179 => true,
		180 => true,
		181 => true,
		182 => true,
	];

	private const LEGACY_013_ENTITY_TYPE_MAP = [
		45 => 15,
		96 => 84,
		97 => 84,
		98 => 84,
	];

	private const LEGACY_011_ENTITY_TYPE_MAP = [
		self::ENTITY_TYPE_ZOMBIE_VILLAGER => self::ENTITY_TYPE_ZOMBIE,
	];

	private const LEGACY_013_ENTITY_NAME_MAP = [
		45 => "女巫",
	];

	private const LEGACY_PRE_015_ENTITY_TYPE_MAP = [
		46 => 34,
		47 => 32,
	];

	private const LEGACY_PRE_015_ENTITY_NAME_MAP = [
		46 => "流浪者",
		47 => "尸壳",
	];

	private const LEGACY_013_HIDDEN_ITEM_IDS = [
		93 => true,
		94 => true,
		97 => true,
		122 => true,
		132 => true,
		165 => true,
		179 => true,
		180 => true,
		181 => true,
		182 => true,
		329 => true,
		342 => true,
		356 => true,
		358 => true,
		380 => true,
		395 => true,
		404 => true,
		407 => true,
		408 => true,
		439 => true,
	];

	private const LEGACY_HORSE_ARMOR_HIDDEN_ITEM_IDS = [
		416 => true,
		417 => true,
		418 => true,
		419 => true,
	];

	private const LEGACY_015_ONLY_HIDDEN_ITEM_IDS = [
		398 => true,
		420 => true,
		421 => true,
		423 => true,
		424 => true,
	];

	private const LEGACY_REDSTONE_HIDDEN_ITEM_IDS = [
		29 => true,
		33 => true,
		34 => true,
		251 => true,
	];

	private const LEGACY_011_COBBLESTONE_ITEM_IDS = [
		193 => true,
		194 => true,
		195 => true,
		196 => true,
		197 => true,
		367 => true,
		369 => true,
		370 => true,
		371 => true,
		372 => true,
		373 => true,
		374 => true,
		375 => true,
		376 => true,
		377 => true,
		378 => true,
		379 => true,
		382 => true,
		384 => true,
		390 => true,
		394 => true,
		396 => true,
		397 => true,
		411 => true,
		412 => true,
		413 => true,
		414 => true,
		415 => true,
		427 => true,
		428 => true,
		429 => true,
		430 => true,
		431 => true,
		438 => true,
	];

	private const LEGACY_011_BOAT_ITEM_IDS = [
		444 => true,
		445 => true,
		446 => true,
		447 => true,
		448 => true,
	];

	private const LEGACY_013_HIDDEN_ITEM_METAS = [
		12 => [
			1 => true,
		],
		162 => [
			2 => true,
		],
		332 => [
			1 => true,
		],
		383 => [
			44 => true,
			93 => true,
		],
	];

	private const LEGACY_011_ENCHANTMENT_NAMES = [
		Enchantment::TYPE_ARMOR_PROTECTION => "\u{4fdd}\u{62a4}",
		Enchantment::TYPE_ARMOR_FIRE_PROTECTION => "\u{706b}\u{7130}\u{4fdd}\u{62a4}",
		Enchantment::TYPE_ARMOR_FALL_PROTECTION => "\u{6454}\u{843d}\u{4fdd}\u{62a4}",
		Enchantment::TYPE_ARMOR_EXPLOSION_PROTECTION => "\u{7206}\u{70b8}\u{4fdd}\u{62a4}",
		Enchantment::TYPE_ARMOR_PROJECTILE_PROTECTION => "\u{5f39}\u{5c04}\u{7269}\u{4fdd}\u{62a4}",
		Enchantment::TYPE_ARMOR_THORNS => "\u{8346}\u{68d8}",
		Enchantment::TYPE_WATER_BREATHING => "\u{6c34}\u{4e0b}\u{547c}\u{5438}",
		Enchantment::TYPE_WATER_SPEED => "\u{6df1}\u{6d77}\u{63a2}\u{7d22}\u{8005}",
		Enchantment::TYPE_WATER_AFFINITY => "\u{6c34}\u{4e0b}\u{901f}\u{6398}",
		Enchantment::TYPE_WEAPON_SHARPNESS => "\u{950b}\u{5229}",
		Enchantment::TYPE_WEAPON_SMITE => "\u{4ea1}\u{7075}\u{6740}\u{624b}",
		Enchantment::TYPE_WEAPON_ARTHROPODS => "\u{8282}\u{80a2}\u{6740}\u{624b}",
		Enchantment::TYPE_WEAPON_KNOCKBACK => "\u{51fb}\u{9000}",
		Enchantment::TYPE_WEAPON_FIRE_ASPECT => "\u{706b}\u{7130}\u{9644}\u{52a0}",
		Enchantment::TYPE_WEAPON_LOOTING => "\u{62a2}\u{593a}",
		Enchantment::TYPE_MINING_EFFICIENCY => "\u{6548}\u{7387}",
		Enchantment::TYPE_MINING_SILK_TOUCH => "\u{7cbe}\u{51c6}\u{91c7}\u{96c6}",
		Enchantment::TYPE_MINING_DURABILITY => "\u{8010}\u{4e45}",
		Enchantment::TYPE_MINING_FORTUNE => "\u{65f6}\u{8fd0}",
		Enchantment::TYPE_BOW_POWER => "\u{529b}\u{91cf}",
		Enchantment::TYPE_BOW_KNOCKBACK => "\u{51b2}\u{51fb}",
		Enchantment::TYPE_BOW_FLAME => "\u{706b}\u{77e2}",
		Enchantment::TYPE_BOW_INFINITY => "\u{65e0}\u{9650}",
		Enchantment::TYPE_FISHING_FORTUNE => "\u{6d77}\u{4e4b}\u{7737}\u{987e}",
		Enchantment::TYPE_FISHING_LURE => "\u{9975}\u{9493}",
	];

	private const LEGACY_011_ENCHANTMENT_LEVELS = [
		1 => "\u{4e00}\u{7ea7}",
		2 => "\u{4e8c}\u{7ea7}",
		3 => "\u{4e09}\u{7ea7}",
		4 => "\u{56db}\u{7ea7}",
		5 => "\u{4e94}\u{7ea7}",
		6 => "\u{516d}\u{7ea7}",
		7 => "\u{4e03}\u{7ea7}",
		8 => "\u{516b}\u{7ea7}",
		9 => "\u{4e5d}\u{7ea7}",
		10 => "\u{5341}\u{7ea7}",
	];

	private const LEGACY_013_COBBLESTONE_ITEM_IDS = [
		23 => true,
		125 => true,
		154 => true,
		199 => true,
		389 => true,
		410 => true,
	];

	private const LEGACY_013_HIDDEN_TILE_IDS = [
		"ItemFrame" => true,
		"Hopper" => true,
		"Dropper" => true,
		"Dispenser" => true,
	];

	private function __construct(){
	}

	public static function readPacketHeader(string $buffer) : ?array{
		if($buffer === ""){
			return null;
		}

		$packetPrefix = ord($buffer[0]);
		if($packetPrefix === 0x8e or $packetPrefix === 0xfe){
			if(strlen($buffer) < 2){
				return null;
			}

			return [ord($buffer[1]), 2];
		}

		return [ord($buffer[0]), 1];
	}

	public static function isProtocol013(int $protocol) : bool{
		return in_array($protocol, Info::ACCEPTED_013_PROTOCOLS, true);
	}

	public static function isProtocol012(int $protocol) : bool{
		return in_array($protocol, Info::V012_PROTOCOLS, true);
	}

	public static function isProtocol011(int $protocol) : bool{
		return in_array($protocol, Info::V011_PROTOCOLS, true);
	}

	private static function usesLegacy012Mappings(int $protocol) : bool{
		return self::isProtocol011($protocol) or self::isProtocol012($protocol);
	}

	private static function usesLegacy013Mappings(int $protocol) : bool{
		return self::usesLegacy012Mappings($protocol) or self::isProtocol013($protocol);
	}

	public static function usesLegacySlotFormat(int $protocol) : bool{
		return self::isProtocol012($protocol) or self::isProtocol013($protocol);
	}

	public static function isLegacy011Protocol(int $protocol) : bool{
		return self::isProtocol011($protocol);
	}

	public static function isProtocol014(int $protocol) : bool{
		return in_array($protocol, Info::V014_PROTOCOLS, true);
	}

	public static function isProtocol015(int $protocol) : bool{
		return in_array($protocol, Info::V015_PROTOCOLS, true);
	}

	public static function requiresLegacyRedstoneMapping(int $protocol) : bool{
		return self::isProtocol011($protocol) or self::isProtocol012($protocol) or self::isProtocol013($protocol) or self::isProtocol014($protocol);
	}

	public static function getRakLibPacketPrefix(int $protocol) : string{
		if(self::isProtocol011($protocol)){
			return "";
		}

		if(self::isProtocol012($protocol)){
			return "";
		}

		if(self::isProtocol013($protocol)){
			return "";
		}

		return self::isProtocol015($protocol) ? chr(0xfe) : chr(0x8e);
	}

	public static function isRestrictedBlockIdFor013(int $blockId) : bool{
		return self::isRestrictedBlockIdForProtocol(37, $blockId);
	}

	private static function getRestrictedChunkBlockBytesForProtocol(int $protocol) : string{
		if(isset(self::$restrictedChunkBlockBytes[$protocol])){
			return self::$restrictedChunkBlockBytes[$protocol];
		}

		$bytes = "";
		for($blockId = 0; $blockId <= 255; ++$blockId){
			if(self::isRestrictedBlockIdForProtocol($protocol, $blockId)){
				$bytes .= chr($blockId);
			}
		}

		return self::$restrictedChunkBlockBytes[$protocol] = $bytes;
	}

	public static function isRestrictedBlockIdForProtocol(int $protocol, int $blockId) : bool{
		if(self::isProtocol011($protocol) and isset(self::LEGACY_011_BLOCK_ID_MAP[$blockId])){
			return true;
		}

		if(self::usesLegacy012Mappings($protocol) and self::needsLegacy012BlockMapping($blockId)){
			return true;
		}

		if(self::requiresLegacyRedstoneMapping($protocol) and isset(self::LEGACY_REDSTONE_BLOCK_ID_MAP[$blockId])){
			return true;
		}

		if(self::usesLegacy013Mappings($protocol)){
			return isset(self::LEGACY_013_BLOCK_ID_MAP[$blockId]) or isset(self::LEGACY_013_BLOCK_STATE_MAP_IDS[$blockId]);
		}

		return false;
	}

	public static function isRestrictedBlockStateFor013(int $blockId, int $blockMeta = 0) : bool{
		return self::isRestrictedBlockStateForProtocol(37, $blockId, $blockMeta);
	}

	public static function isRestrictedBlockStateForProtocol(int $protocol, int $blockId, int $blockMeta = 0) : bool{
		[$mappedId, $mappedMeta] = self::mapBlockForProtocol($protocol, $blockId, $blockMeta);
		return $mappedId !== $blockId or $mappedMeta !== $blockMeta;
	}

	public static function isHiddenBlockIdFor013(int $blockId, int $blockMeta = 0) : bool{
		return self::isHiddenBlockIdForProtocol(37, $blockId, $blockMeta);
	}

	public static function isHiddenBlockIdForProtocol(int $protocol, int $blockId, int $blockMeta = 0) : bool{
		[$mappedId, $mappedMeta] = self::mapBlockForProtocol($protocol, $blockId, $blockMeta);
		return ($mappedId !== $blockId or $mappedMeta !== $blockMeta) and $mappedId === 0;
	}

	public static function canUseSlimeBlockPhysics(int $protocol) : bool{
		return !self::usesLegacy013Mappings($protocol);
	}

	public static function shouldBlockBreakFor013(int $blockId, int $blockMeta = 0) : bool{
		return self::shouldBlockBreakForProtocol(37, $blockId, $blockMeta);
	}

	public static function shouldBlockBreakForProtocol(int $protocol, int $blockId, int $blockMeta = 0) : bool{
		return self::isRestrictedBlockStateForProtocol($protocol, $blockId, $blockMeta);
	}

	public static function mapBlockFor013(int $blockId, int $blockMeta) : array{
		return self::mapBlockForProtocol(37, $blockId, $blockMeta);
	}

	public static function mapBlockForProtocol(int $protocol, int $blockId, int $blockMeta) : array{
		if(self::isProtocol011($protocol) and isset(self::LEGACY_011_BLOCK_ID_MAP[$blockId])){
			return self::LEGACY_011_BLOCK_ID_MAP[$blockId];
		}

		if(self::usesLegacy012Mappings($protocol) and self::needsLegacy012BlockMapping($blockId)){
			return self::mapLegacy012Block($blockId, $blockMeta);
		}

		if(self::requiresLegacyRedstoneMapping($protocol) and isset(self::LEGACY_REDSTONE_BLOCK_ID_MAP[$blockId])){
			return self::LEGACY_REDSTONE_BLOCK_ID_MAP[$blockId];
		}

		if(!self::usesLegacy013Mappings($protocol)){
			return [$blockId, $blockMeta];
		}

		if(isset(self::LEGACY_013_BLOCK_ID_MAP[$blockId])){
			return self::LEGACY_013_BLOCK_ID_MAP[$blockId];
		}

		switch($blockId){
			case 12:
				return ($blockMeta & 0x0f) === 1 ? [12, 0] : [$blockId, $blockMeta];
			case 179:
				return [24, $blockMeta & 0x03];
			case 180:
				return [128, $blockMeta & 0x07];
			case 181:
				return [43, 1];
			case 182:
				return [44, ($blockMeta & 0x08) | 1];
		}

		return [$blockId, $blockMeta];
	}

	private static function needsLegacy012BlockMapping(int $blockId) : bool{
		return isset(self::LEGACY_012_BLOCK_ID_MAP[$blockId]) or
			in_array($blockId, self::LEGACY_012_PRESSURE_PLATES, true) or
			in_array($blockId, self::LEGACY_012_DOORS, true);
	}

	public static function shouldUseLegacyMappedWoodenDoorAnimation(int $protocol, int $blockId) : bool{
		return (self::isProtocol011($protocol) or self::isProtocol012($protocol)) and in_array($blockId, self::LEGACY_012_DOORS, true);
	}

	public static function shouldAllowLegacyMappedBlockActivation(int $protocol, int $blockId) : bool{
		if(self::shouldUseLegacyMappedWoodenDoorAnimation($protocol, $blockId)){
			return true;
		}

		return self::isProtocol011($protocol) and isset(self::LEGACY_011_MAPPED_INTERACTIVE_BLOCK_IDS[$blockId]);
	}

	private static function mapLegacy012Block(int $blockId, int $blockMeta) : array{
		if(in_array($blockId, self::LEGACY_012_DOORS, true)){
			return [64, self::normalizeLegacy012DoorMeta($blockMeta)];
		}

		if(in_array($blockId, self::LEGACY_012_PRESSURE_PLATES, true)){
			return [171, 0];
		}

		if(isset(self::LEGACY_012_BLOCK_ID_MAP[$blockId])){
			return self::LEGACY_012_BLOCK_ID_MAP[$blockId];
		}

		return [$blockId, $blockMeta];
	}

	private static function normalizeLegacy012DoorMeta(int $blockMeta) : int{
		return ($blockMeta & 0x08) === 0 ? ($blockMeta & 0x0f) : (($blockMeta & 0x07) | 0x08);
	}

	public static function mapLevelEventDataForProtocol(int $protocol, int $eventId, int $data) : int{
		if($eventId !== self::LEVEL_EVENT_PARTICLE_DESTROY){
			return $data;
		}

		$blockId = $data & 0xfff;
		$blockMeta = ($data >> 12) & 0x0f;
		[$mappedId, $mappedMeta] = self::mapBlockForProtocol($protocol, $blockId, $blockMeta);
		if($mappedId === $blockId and $mappedMeta === $blockMeta){
			return $data;
		}

		return ($data & ~0xffff) | ($mappedId & 0xfff) | (($mappedMeta & 0x0f) << 12);
	}

	public static function mapEntityTypeFor013(int $entityType) : int{
		return isset(self::LEGACY_013_ENTITY_TYPE_MAP[$entityType]) ? self::LEGACY_013_ENTITY_TYPE_MAP[$entityType] : $entityType;
	}

	public static function mapEntityTypeForProtocol(int $protocol, int $entityType) : int{
		if(self::isProtocol011($protocol) and isset(self::LEGACY_011_ENTITY_TYPE_MAP[$entityType])){
			return self::LEGACY_011_ENTITY_TYPE_MAP[$entityType];
		}

		if(!self::isProtocol015($protocol) && isset(self::LEGACY_PRE_015_ENTITY_TYPE_MAP[$entityType])){
			return self::LEGACY_PRE_015_ENTITY_TYPE_MAP[$entityType];
		}
		if(self::usesLegacy013Mappings($protocol)){
			return self::mapEntityTypeFor013($entityType);
		}
		return $entityType;
	}

	private static function getLegacyMappedEntityNameForProtocol(int $protocol, int $entityType) : ?string{
		if(self::usesLegacy013Mappings($protocol) and isset(self::LEGACY_013_ENTITY_NAME_MAP[$entityType])){
			return self::LEGACY_013_ENTITY_NAME_MAP[$entityType];
		}

		if(!self::isProtocol015($protocol) and isset(self::LEGACY_PRE_015_ENTITY_NAME_MAP[$entityType])){
			return self::LEGACY_PRE_015_ENTITY_NAME_MAP[$entityType];
		}

		return null;
	}

	public static function applyLegacyMappedEntityNameForProtocol(int $protocol, int $entityType, array $metadata) : array{
		$mappedName = self::getLegacyMappedEntityNameForProtocol($protocol, $entityType);
		if($mappedName === null){
			return $metadata;
		}

		$nameProperty = isset($metadata[self::ENTITY_DATA_NAMETAG]) ? $metadata[self::ENTITY_DATA_NAMETAG] : null;
		$hasCustomName = self::metadataPropertyHasType($nameProperty, self::DATA_TYPE_STRING) && (string) $nameProperty[1] !== "";
		if(!$hasCustomName){
			$metadata[self::ENTITY_DATA_NAMETAG] = [self::DATA_TYPE_STRING, $mappedName];
		}
		$metadata[self::ENTITY_DATA_SHOW_NAMETAG] = [self::DATA_TYPE_BYTE, 1];

		return $metadata;
	}

	public static function isRestrictedItemIdFor013(int $itemId) : bool{
		return self::isRestrictedItemIdForProtocol(37, $itemId);
	}

	public static function isRestrictedItemIdForProtocol(int $protocol, int $itemId) : bool{
		if(self::usesLegacy012Mappings($protocol) and (self::needsLegacy012BlockMapping($itemId) or isset(self::LEGACY_012_PLACEHOLDER_ITEM_IDS[$itemId]))){
			return true;
		}

		if(self::isProtocol012($protocol) and isset(self::LEGACY_012_WOODEN_DOOR_ITEM_IDS[$itemId])){
			return true;
		}

		if(self::isProtocol011($protocol)){
			if(isset(self::LEGACY_011_ITEM_ID_MAP[$itemId]) or
					isset(self::LEGACY_011_COBBLESTONE_ITEM_IDS[$itemId]) or
					isset(self::LEGACY_011_BOAT_ITEM_IDS[$itemId])){
				return true;
			}

			if($itemId === Item::ENCHANTED_BOOK or
				$itemId === Item::GOLDEN_APPLE or
				$itemId === Item::ENCHANTED_GOLDEN_APPLE or
				$itemId === self::getLegacyEnchantingGoldenAppleId()){
				return true;
			}
		}

		if(self::requiresLegacyRedstoneMapping($protocol) and isset(self::LEGACY_REDSTONE_HIDDEN_ITEM_IDS[$itemId])){
			return true;
		}

		if(self::requiresLegacyRedstoneMapping($protocol) and isset(self::LEGACY_HORSE_ARMOR_HIDDEN_ITEM_IDS[$itemId])){
			return true;
		}

		if(self::requiresLegacyRedstoneMapping($protocol) and isset(self::LEGACY_015_ONLY_HIDDEN_ITEM_IDS[$itemId])){
			return true;
		}

		if(self::usesLegacy013Mappings($protocol)){
			return isset(self::LEGACY_013_COBBLESTONE_ITEM_IDS[$itemId]) or isset(self::LEGACY_013_HIDDEN_ITEM_IDS[$itemId]);
		}

		return false;
	}

	public static function isMappedItemIdFor013(int $itemId) : bool{
		return isset(self::LEGACY_013_COBBLESTONE_ITEM_IDS[$itemId]);
	}

	public static function isHiddenItemIdFor013(int $itemId) : bool{
		return self::isHiddenItemIdForProtocol(37, $itemId);
	}

	public static function isHiddenItemIdForProtocol(int $protocol, int $itemId) : bool{
		if(self::requiresLegacyRedstoneMapping($protocol) and isset(self::LEGACY_REDSTONE_HIDDEN_ITEM_IDS[$itemId])){
			return true;
		}

		if(self::requiresLegacyRedstoneMapping($protocol) and isset(self::LEGACY_HORSE_ARMOR_HIDDEN_ITEM_IDS[$itemId])){
			return true;
		}

		if(self::requiresLegacyRedstoneMapping($protocol) and isset(self::LEGACY_015_ONLY_HIDDEN_ITEM_IDS[$itemId])){
			return true;
		}

		return self::usesLegacy013Mappings($protocol) and isset(self::LEGACY_013_HIDDEN_ITEM_IDS[$itemId]);
	}

	public static function isHiddenItemFor013(int $itemId, int $itemMeta = 0) : bool{
		return self::isHiddenItemForProtocol(37, $itemId, $itemMeta);
	}

	public static function isHiddenItemForProtocol(int $protocol, int $itemId, int $itemMeta = 0) : bool{
		if(self::requiresLegacyRedstoneMapping($protocol) and isset(self::LEGACY_REDSTONE_HIDDEN_ITEM_IDS[$itemId])){
			return true;
		}

		if(self::requiresLegacyRedstoneMapping($protocol) and isset(self::LEGACY_HORSE_ARMOR_HIDDEN_ITEM_IDS[$itemId])){
			return true;
		}

		if(self::requiresLegacyRedstoneMapping($protocol) and isset(self::LEGACY_015_ONLY_HIDDEN_ITEM_IDS[$itemId])){
			return true;
		}

		return self::usesLegacy013Mappings($protocol) and (isset(self::LEGACY_013_HIDDEN_ITEM_IDS[$itemId]) or isset(self::LEGACY_013_HIDDEN_ITEM_METAS[$itemId][$itemMeta]));
	}

	public static function isHiddenTileIdFor013(string $tileId) : bool{
		return isset(self::LEGACY_013_HIDDEN_TILE_IDS[$tileId]);
	}

	public static function isHiddenTileIdForProtocol(int $protocol, string $tileId) : bool{
		return self::usesLegacy013Mappings($protocol) and self::isHiddenTileIdFor013($tileId);
	}

	public static function filterMetadataFor013(array $metadata) : array{
		return self::filterMetadataByTypeMap($metadata, self::LEGACY_METADATA_TYPES);
	}

	public static function filterMetadataForProtocol(int $protocol, array $metadata) : array{
		if(self::isProtocol011($protocol)){
			$filtered = self::filterMetadataByTypeMap($metadata, self::LEGACY_011_METADATA_TYPES);
		}elseif(self::requiresLegacyRedstoneMapping($protocol)){
			$filtered = self::filterMetadataFor013($metadata);
		}elseif(self::isProtocol015($protocol)){
			$filtered = self::filterMetadataByTypeMap($metadata, self::PROTOCOL_015_METADATA_TYPES);
		}else{
			$filtered = $metadata;
		}

		if(!self::isProtocol015($protocol)){
			if(class_exists(Entity::class, false)){
				unset($filtered[Entity::DATA_LEAD_HOLDER], $filtered[Entity::DATA_LEAD]);
			}else{
				unset($filtered[self::ENTITY_DATA_LEAD_HOLDER], $filtered[self::ENTITY_DATA_LEAD]);
			}
		}

		return $filtered;
	}

	public static function filterEntityMetadataForProtocol(int $protocol, int $entityType, array $metadata) : array{
		$filtered = self::filterMetadataForProtocol($protocol, $metadata);

		if($entityType === self::ENTITY_TYPE_CREEPER){
			$filtered = self::keepCreeperMetadata($filtered, $metadata);
		}

		if(!self::isProtocol015($protocol) and $entityType === self::ENTITY_TYPE_ARROW){
			unset($filtered[self::ENTITY_DATA_POTION_ID]);
		}elseif(self::isProtocol015($protocol) and
				$entityType !== self::ENTITY_TYPE_ARROW and
				$entityType !== 86 and
				isset($filtered[self::ENTITY_DATA_POTION_ID]) and
				self::metadataPropertyHasType($filtered[self::ENTITY_DATA_POTION_ID], self::DATA_TYPE_SHORT)){
			unset($filtered[self::ENTITY_DATA_POTION_ID]);
		}

		return $filtered;
	}

	private static function keepCreeperMetadata(array $filtered, array $metadata) : array{
		foreach([
			self::ENTITY_DATA_CREEPER_SWELL_DIRECTION,
			self::ENTITY_DATA_CREEPER_SWELL,
			self::ENTITY_DATA_CREEPER_SWELL_2,
			self::ENTITY_DATA_CREEPER_POWERED,
		] as $index){
			if(!isset($metadata[$index]) or !self::isValidMetadataProperty($metadata[$index])){
				continue;
			}

			$normalized = self::normalizeMetadataProperty($index, $metadata[$index], self::DATA_TYPE_BYTE);
			if($normalized !== null){
				$filtered[$index] = $normalized;
			}
		}

		return $filtered;
	}

	public static function canSendMobEffectIdForProtocol(int $protocol, int $effectId) : bool{
		if($effectId < 1){
			return false;
		}

		return self::isProtocol015($protocol) ? $effectId <= 23 : $effectId <= 20;
	}

	private static function filterMetadataByTypeMap(array $metadata, array $typeMap) : array{
		$filtered = [];
		foreach($metadata as $index => $property){
			$index = (int) $index;
			if(!array_key_exists($index, $typeMap) or !self::isValidMetadataProperty($property)){
				continue;
			}

			$expectedType = $typeMap[$index];
			if($expectedType === null){
				$filtered[$index] = [(int) $property[0], $property[1]];
				continue;
			}

			$normalized = self::normalizeMetadataProperty($index, $property, $expectedType);
			if($normalized !== null){
				$filtered[$index] = $normalized;
			}
		}

		return $filtered;
	}

	private static function isValidMetadataProperty($property) : bool{
		if(!is_array($property) or !array_key_exists(0, $property) or !array_key_exists(1, $property)){
			return false;
		}

		if(!is_int($property[0]) and !is_float($property[0]) and !is_string($property[0])){
			return false;
		}

		$type = (int) $property[0];
		return $type >= self::DATA_TYPE_BYTE and $type <= self::DATA_TYPE_LONG;
	}

	private static function normalizeMetadataProperty(int $index, array $property, int $expectedType){
		if((int) $property[0] === $expectedType){
			return [$expectedType, $expectedType === self::DATA_TYPE_BYTE ? ((int) $property[1]) & 0xff : $property[1]];
		}

		if($index === 0 and self::isIntegerMetadataType((int) $property[0])){
			return [self::DATA_TYPE_BYTE, ((int) $property[1]) & 0xff];
		}

		return null;
	}

	private static function isIntegerMetadataType(int $type) : bool{
		return $type === self::DATA_TYPE_BYTE or
			$type === self::DATA_TYPE_SHORT or
			$type === self::DATA_TYPE_INT or
			$type === self::DATA_TYPE_LONG;
	}

	private static function metadataPropertyHasType($property, int $type) : bool{
		return self::isValidMetadataProperty($property) and (int) $property[0] === $type;
	}

	private static function getLegacyItemSurrogateDefinition(Item $item) : ?array{
		switch($item->getId()){
			case Item::RAW_MUTTON:
				return [Item::RAW_BEEF, 0, 0, "羊肉"];
			case Item::COOKED_MUTTON:
				return [Item::COOKED_BEEF, 0, 0, "熟羊肉"];
			case Item::CARROT_ON_A_STICK:
				return [Item::FISHING_ROD, 0, 1, "萝卜竿"];
			case Item::PISTON:
				return [Item::FURNACE, 1, 1, "活塞"];
			case Item::STICKY_PISTON:
				return [Item::FURNACE, 2, 2, "粘性活塞"];
			case Item::OBSERVER:
				return [Item::FURNACE, 3, 3, "侦测器"];
		}

		return null;
	}

	private static function getProtocol011GoldenAppleSurrogateDefinition(Item $item) : ?array{
		$meta = $item->getDamage();
		$meta = $meta === null ? 0 : (int) $meta;

		if($item->getId() === Item::GOLDEN_APPLE){
			return [Item::APPLE, 0, 0, $meta > 0 ? "Old Enchanted Golden Apple" : "Golden Apple"];
		}

		if($item->getId() === Item::ENCHANTED_GOLDEN_APPLE or $item->getId() === self::getLegacyEnchantingGoldenAppleId()){
			return [Item::APPLE, 0, 0, "Enchanted Golden Apple"];
		}

		return null;
	}

	private static function getActualProtocol011GoldenAppleSurrogate(Item $item) : ?array{
		if($item->getId() !== Item::APPLE){
			return null;
		}

		switch($item->getCustomName()){
			case "Golden Apple":
				return [Item::GOLDEN_APPLE, 0];
			case "Old Enchanted Golden Apple":
				return [Item::GOLDEN_APPLE, 1];
			case "Enchanted Golden Apple":
				return [Item::ENCHANTED_GOLDEN_APPLE, 0];
		}

		return null;
	}

	private static function getActualItemFromLegacySurrogate(Item $item) : ?array{
		$customName = $item->getCustomName();
		$unbreakingLevel = $item->getEnchantmentLevel(Enchantment::TYPE_MINING_DURABILITY);

		switch($item->getId()){
			case Item::RAW_BEEF:
				return (($unbreakingLevel === 0 or $unbreakingLevel === 1) and $customName === "羊肉") ? [Item::RAW_MUTTON, 0] : null;
			case Item::COOKED_BEEF:
				return (($unbreakingLevel === 0 or $unbreakingLevel === 2) and $customName === "熟羊肉") ? [Item::COOKED_MUTTON, 0] : null;
			case Item::FISHING_ROD:
				return ($unbreakingLevel === 1 and $customName === "萝卜竿") ? [Item::CARROT_ON_A_STICK, 0] : null;
			case Item::CARROT:
				return ($unbreakingLevel === 1 and $customName === "萝卜竿") ? [Item::CARROT_ON_A_STICK, 0] : null;
			case Item::FURNACE:
			case Item::COAL_BLOCK:
				$meta = $item->getDamage() === null ? 0 : (int) $item->getDamage();
				if($unbreakingLevel === 1 and $customName === "活塞"){
					return [Item::PISTON, 0];
				}
				if($unbreakingLevel === 2 and $customName === "粘性活塞"){
					return [Item::STICKY_PISTON, 0];
				}
				if($unbreakingLevel === 3 and $customName === "侦测器"){
					return [Item::OBSERVER, 0];
				}
				if($meta === 1){
					return [Item::PISTON, 0];
				}
				if($meta === 2){
					return [Item::STICKY_PISTON, 0];
				}
				if($meta === 3){
					return [Item::OBSERVER, 0];
				}
				break;
		}

		return null;
	}

	private static function createLegacyItemSurrogate(Item $source, array $definition) : Item{
		[$id, $meta, $unbreakingLevel, $customName] = $definition;

		$surrogate = Item::get($id, $meta, $source->getCount());
		$surrogate->setCustomName($customName);

		if($unbreakingLevel > 0){
			$enchantment = Enchantment::getEnchantment(Enchantment::TYPE_MINING_DURABILITY);
			$enchantment->setLevel($unbreakingLevel);
			$surrogate->addEnchantment($enchantment);
		}

		return $surrogate;
	}

	private static function getCreativeInventorySurrogateDefinitionForProtocol(int $protocol, Item $item) : ?array{
		if(self::requiresLegacyRedstoneMapping($protocol)){
			switch($item->getId()){
				case Item::PISTON:
					return [Item::BOW, 0, 1, "活塞"];
				case Item::STICKY_PISTON:
					return [Item::FISHING_ROD, 0, 2, "粘性活塞"];
				case Item::OBSERVER:
					return [Item::FLINT_AND_STEEL, 0, 3, "侦测器"];
			}
		}

		if(self::isProtocol015($protocol) and $item->getId() === Item::CARROT_ON_A_STICK){
			return self::getLegacyItemSurrogateDefinition($item);
		}

		return null;
	}

	private static function getActualCreativeInventoryItemForProtocol(int $protocol, Item $item) : ?array{
		if(self::requiresLegacyRedstoneMapping($protocol)){
			$customName = $item->getCustomName();
			$unbreakingLevel = $item->getEnchantmentLevel(Enchantment::TYPE_MINING_DURABILITY);

			switch($item->getId()){
				case Item::BOW:
					return ($unbreakingLevel === 1 and $customName === "活塞") ? [Item::PISTON, 0] : null;
				case Item::FISHING_ROD:
					return ($unbreakingLevel === 2 and $customName === "粘性活塞") ? [Item::STICKY_PISTON, 0] : null;
				case Item::FLINT_AND_STEEL:
					return ($unbreakingLevel === 3 and $customName === "侦测器") ? [Item::OBSERVER, 0] : null;
			}
		}

		if(self::isProtocol015($protocol)){
			$actual = self::getActualItemFromLegacySurrogate($item);
			if($actual !== null and $actual[0] === Item::CARROT_ON_A_STICK){
				return $actual;
			}
		}

		return null;
	}

	public static function hasLegacyItemSurrogateForProtocol(int $protocol, Item $item) : bool{
		if(self::isProtocol011($protocol) and self::getProtocol011GoldenAppleSurrogateDefinition($item) !== null){
			return true;
		}

		return self::requiresLegacyRedstoneMapping($protocol) and self::getLegacyItemSurrogateDefinition($item) !== null;
	}

	public static function isLegacyItemSurrogateForProtocol(int $protocol, Item $item) : bool{
		if(self::isProtocol011($protocol) and self::getActualProtocol011GoldenAppleSurrogate($item) !== null){
			return true;
		}

		return self::requiresLegacyRedstoneMapping($protocol) and self::getActualItemFromLegacySurrogate($item) !== null;
	}

	public static function mapCreativeInventoryItemForProtocol(int $protocol, Item $item) : Item{
		if(self::requiresLegacyRedstoneMapping($protocol)){
			return self::mapItemForProtocol($protocol, $item, false);
		}

		$surrogateDefinition = self::getCreativeInventorySurrogateDefinitionForProtocol($protocol, $item);
		if($surrogateDefinition !== null){
			return self::createLegacyItemSurrogate($item, $surrogateDefinition);
		}

		return $item;
	}

	public static function normalizeCreativeItemForProtocol(int $protocol, Item $item) : Item{
		if(self::requiresLegacyRedstoneMapping($protocol)){
			return self::normalizeClientItemForProtocol($protocol, $item);
		}

		$actual = self::getActualCreativeInventoryItemForProtocol($protocol, $item);
		if($actual !== null){
			return Item::get($actual[0], $actual[1], $item->getCount());
		}

		return self::normalizeClientItemForProtocol($protocol, $item);
	}

	public static function getLegacySimulatedBlockNameById(int $blockId, int $blockMeta = 0) : ?string{
		switch($blockId){
			case Item::PISTON:
				return "活塞";
			case Item::STICKY_PISTON:
				return "粘性活塞";
			case Item::PISTON_HEAD:
				return "活塞臂";
			case Item::OBSERVER:
				return "侦测器";
		}

		return null;
	}

	public static function getLegacySimulatedBlockNameForItem(int $protocol, Item $item) : ?string{
		if(!self::requiresLegacyRedstoneMapping($protocol)){
			return null;
		}

		$normalized = self::normalizeClientItemForProtocol($protocol, $item);
		return self::getLegacySimulatedBlockNameById($normalized->getId(), $normalized->getDamage() === null ? 0 : (int) $normalized->getDamage());
	}

	public static function canLegacyPlayerBreakMappedBlock(int $protocol, int $blockId, int $blockMeta = 0) : bool{
		return self::requiresLegacyRedstoneMapping($protocol) and self::getLegacySimulatedBlockNameById($blockId, $blockMeta) !== null;
	}

	public static function sanitizeItemFor013(int $itemId, int $itemMeta, int $count) : array{
		return self::sanitizeItemForProtocol(37, $itemId, $itemMeta, $count);
	}

	public static function sanitizeItemForProtocol(int $protocol, int $itemId, int $itemMeta, int $count) : array{
		if($itemId === 0){
			return [0, 0, 0];
		}

		$count = max(1, $count);
		if(self::usesLegacy012Mappings($protocol) and self::needsLegacy012BlockMapping($itemId)){
			[$mappedId, $mappedMeta] = self::mapLegacy012Block($itemId, $itemMeta);
			return [$mappedId, $mappedMeta, $mappedId === 0 ? 0 : $count];
		}

		if(self::usesLegacy012Mappings($protocol) and isset(self::LEGACY_012_PLACEHOLDER_ITEM_IDS[$itemId])){
			return [Item::STONE, 0, $count];
		}

		if(self::isProtocol012($protocol) and isset(self::LEGACY_012_WOODEN_DOOR_ITEM_IDS[$itemId])){
			return [Item::WOODEN_DOOR, 0, $count];
		}

		if(self::isProtocol011($protocol)){
			if(isset(self::LEGACY_011_BLOCK_ID_MAP[$itemId])){
				[$mappedId, $mappedMeta] = self::LEGACY_011_BLOCK_ID_MAP[$itemId];
				return [$mappedId, $mappedMeta, $mappedId === 0 ? 0 : $count];
			}

			if(isset(self::LEGACY_011_ITEM_ID_MAP[$itemId])){
				[$mappedId, $mappedMeta] = self::LEGACY_011_ITEM_ID_MAP[$itemId];
				return [$mappedId, $mappedMeta, $count];
			}

			if(isset(self::LEGACY_011_COBBLESTONE_ITEM_IDS[$itemId])){
				return [Item::COBBLESTONE, 0, $count];
			}

			if(isset(self::LEGACY_011_BOAT_ITEM_IDS[$itemId]) or ($itemId === Item::BOAT and $itemMeta > 0)){
				return [Item::BOAT, 0, $count];
			}

			if($itemId === Item::ENCHANTED_BOOK){
				return [Item::BOOK, 0, $count];
			}

			if($itemId === Item::GOLDEN_APPLE or
					$itemId === Item::ENCHANTED_GOLDEN_APPLE or
					$itemId === self::getLegacyEnchantingGoldenAppleId()){
				return [Item::APPLE, 0, $count];
			}
		}

		if(self::usesLegacy013Mappings($protocol) and isset(self::LEGACY_013_COBBLESTONE_ITEM_IDS[$itemId])){
			return [4, 0, $count];
		}

		if(self::isHiddenItemForProtocol($protocol, $itemId, $itemMeta)){
			return [0, 0, 0];
		}

		return [$itemId, $itemMeta, $count];
	}

	public static function mapItemForProtocol(int $protocol, Item $item, bool $container = false) : Item{
		if($item->getId() === Item::AIR or $item->getCount() <= 0){
			return Item::get(Item::AIR, 0, 0);
		}

		if(self::requiresLegacyRedstoneMapping($protocol) and $item instanceof Arrow and $item->isTipped()){
			return $item->toLegacyTippedArrowSurrogate();
		}

		if(self::isProtocol011($protocol)){
			$surrogateDefinition = self::getProtocol011GoldenAppleSurrogateDefinition($item);
			if($surrogateDefinition !== null){
				return self::stripProtocol011CustomName(self::createLegacyItemSurrogate($item, $surrogateDefinition));
			}
		}

		if(self::requiresLegacyRedstoneMapping($protocol)){
			$surrogateDefinition = self::getLegacyItemSurrogateDefinition($item);
			if($surrogateDefinition !== null){
				$surrogate = self::createLegacyItemSurrogate($item, $surrogateDefinition);
				return self::isProtocol011($protocol) ? self::stripProtocol011CustomName($surrogate) : $surrogate;
			}
		}

		$meta = $item->getDamage();
		$mapped = $container ?
			self::sanitizeContainerItemForProtocol($protocol, $item->getId(), $meta === null ? 0 : (int) $meta, $item->getCount()) :
			self::sanitizeItemForProtocol($protocol, $item->getId(), $meta === null ? 0 : (int) $meta, $item->getCount());

		if($mapped === [$item->getId(), $meta === null ? 0 : (int) $meta, $item->getCount()]){
			return self::isProtocol011($protocol) ? self::stripProtocol011CustomName($item) : $item;
		}

		$mappedItem = Item::get($mapped[0], $mapped[1], $mapped[2]);
		return self::isProtocol011($protocol) ? self::stripProtocol011CustomName($mappedItem) : $mappedItem;
	}

	private static function stripProtocol011CustomName(Item $item) : Item{
		if(!$item->hasCustomName()){
			return $item;
		}

		$filtered = clone $item;
		$filtered->clearCustomName();
		return $filtered;
	}

	public static function isProtocol012RegisteredRecipeItem(int $itemId, int $itemMeta = 0) : bool{
		if($itemId === Item::AIR){
			return true;
		}

		return isset(self::LEGACY_012_REGISTERED_RECIPE_ITEM_IDS[$itemId]);
	}

	private static function mapProtocol012RecipeItem(Item $item){
		if($item->getId() === Item::AIR or $item->getCount() <= 0){
			return Item::get(Item::AIR, 0, 0);
		}

		$itemId = $item->getId();
		$itemMeta = $item->getDamage();
		$itemMeta = $itemMeta === null ? 0 : (int) $itemMeta;
		$count = $item->getCount();

		if($itemId === Item::ENCHANTED_GOLDEN_APPLE or $itemId === self::getLegacyEnchantingGoldenAppleId()){
			return Item::get(Item::GOLDEN_APPLE, 1, $count);
		}

		if($itemId === Item::GOLDEN_APPLE){
			return Item::get(Item::GOLDEN_APPLE, $itemMeta > 0 ? 1 : 0, $count);
		}

		if(isset(self::LEGACY_012_WOODEN_DOOR_ITEM_IDS[$itemId])){
			return Item::get(Item::WOODEN_DOOR, 0, $count);
		}

		if(!self::isProtocol012RegisteredRecipeItem($itemId, $itemMeta)){
			return null;
		}

		if($item->hasCompoundTag()){
			return Item::get($itemId, $itemMeta, $count);
		}

		return $item;
	}

	public static function mapRecipeItemForProtocol(int $protocol, Item $item) : ?Item{
		if($item->getId() === Item::AIR or $item->getCount() <= 0){
			return Item::get(Item::AIR, 0, 0);
		}

		if(self::isProtocol012($protocol)){
			return self::mapProtocol012RecipeItem($item);
		}

		if(self::requiresLegacyRedstoneMapping($protocol) and $item->getId() === Item::CARROT_ON_A_STICK){
			return Item::get(Item::FURNACE, 0, $item->getCount());
		}

		$mappedItem = self::mapItemForProtocol($protocol, $item, false);
		$meta = $mappedItem->getDamage();
		if($mappedItem->getId() === Item::AIR or $mappedItem->getCount() <= 0 or self::isHiddenItemForProtocol($protocol, $mappedItem->getId(), $meta === null ? 0 : (int) $meta)){
			return null;
		}

		if($mappedItem->hasCompoundTag()){
			return Item::get($mappedItem->getId(), $mappedItem->getDamage(), $mappedItem->getCount());
		}

		return $mappedItem;
	}

	private static function mapCraftingRecipeItemForProtocol(int $protocol, Item $item, bool &$modified){
		$mappedItem = self::isProtocol012($protocol) ? self::mapProtocol012RecipeItem($item) : self::mapRecipeItemForProtocol($protocol, $item);
		if($mappedItem === null){
			return null;
		}

		if(!$mappedItem->deepEquals($item, true, true, true)){
			$modified = true;
		}

		return $mappedItem;
	}

	public static function mapCraftingRecipeForProtocol(int $protocol, $recipe){
		$modified = false;

		if($recipe instanceof ShapedRecipe){
			$result = self::mapCraftingRecipeItemForProtocol($protocol, $recipe->getResult(), $modified);
			if($result === null){
				return null;
			}

			$mappedRecipe = new ShapedRecipeFromJson($result, $recipe->getHeight(), $recipe->getWidth());
			for($y = 0; $y < $recipe->getHeight(); ++$y){
				for($x = 0; $x < $recipe->getWidth(); ++$x){
					$ingredient = $recipe->getIngredient($x, $y);
					if($ingredient instanceof Item and $ingredient->getId() !== Item::AIR){
						$mappedIngredient = self::mapCraftingRecipeItemForProtocol($protocol, $ingredient, $modified);
						if($mappedIngredient === null){
							return null;
						}
						if($mappedIngredient->getId() !== Item::AIR){
							$mappedRecipe->addIngredient($x, $y, $mappedIngredient);
						}
					}
				}
			}
		}elseif($recipe instanceof ShapelessRecipe){
			$result = self::mapCraftingRecipeItemForProtocol($protocol, $recipe->getResult(), $modified);
			if($result === null){
				return null;
			}

			$mappedRecipe = $recipe instanceof BigShapelessRecipe ? new BigShapelessRecipe($result) : new ShapelessRecipe($result);
			foreach($recipe->getIngredientList() as $ingredient){
				$mappedIngredient = self::mapCraftingRecipeItemForProtocol($protocol, $ingredient, $modified);
				if($mappedIngredient === null){
					return null;
				}
				if($mappedIngredient->getId() !== Item::AIR){
					$mappedRecipe->addIngredient($mappedIngredient);
				}
			}
		}elseif($recipe instanceof FurnaceRecipe){
			$result = self::mapCraftingRecipeItemForProtocol($protocol, $recipe->getResult(), $modified);
			$input = self::mapCraftingRecipeItemForProtocol($protocol, $recipe->getInput(), $modified);
			if($result === null or $input === null){
				return null;
			}

			$mappedRecipe = new FurnaceRecipe($result, $input);
		}else{
			return $recipe;
		}

		if($recipe->getId() !== null){
			$mappedRecipe->setId($recipe->getId());
		}

		return $modified ? $mappedRecipe : $recipe;
	}

	public static function normalizeRecipeClientItemForProtocol(int $protocol, Item $sourceItem, Item $clientItem) : Item{
		$normalized = self::normalizeClientItemForProtocol($protocol, $clientItem);
		if($normalized !== $clientItem){
			$normalized->setCount($clientItem->getCount());
			return $normalized;
		}

		if(self::requiresLegacyRedstoneMapping($protocol)){
			$mappedRecipeItem = self::mapRecipeItemForProtocol($protocol, $sourceItem);
			if($mappedRecipeItem instanceof Item and $mappedRecipeItem->deepEquals($clientItem, true, true, true)){
				$item = clone $sourceItem;
				$item->setCount($clientItem->getCount());
				return $item;
			}
		}

		return $clientItem;
	}

	public static function normalizeClientItemForProtocol(int $protocol, Item $item) : Item{
		$arrow = Arrow::fromLegacyTippedArrowSurrogate($item);
		if($arrow instanceof Arrow){
			return $arrow;
		}

		if(self::isProtocol011($protocol)){
			$item = self::stripProtocol011CustomName($item);
		}

		if(self::isProtocol011($protocol)){
			$actual = self::getActualProtocol011GoldenAppleSurrogate($item);
			if($actual !== null){
				return Item::get($actual[0], $actual[1], $item->getCount());
			}
		}

		if(self::requiresLegacyRedstoneMapping($protocol)){
			$actual = self::getActualItemFromLegacySurrogate($item);
			if($actual !== null){
				return Item::get($actual[0], $actual[1], $item->getCount());
			}
		}

		return $item;
	}

	private static function normalizeComparableItemForProtocol(int $protocol, Item $item) : Item{
		$normalized = self::normalizeClientItemForProtocol($protocol, $item);
		if($normalized === $item){
			$normalized = clone $item;
		}
		$normalized->setCount($item->getCount());

		return $normalized;
	}

	public static function itemsMatchAfterClientNormalization(int $protocol, Item $serverItem, Item $clientItem, bool $checkCount = false) : bool{
		if($serverItem->deepEquals($clientItem, true, true, $checkCount)){
			return true;
		}

		if(!self::requiresLegacyRedstoneMapping($protocol)){
			return false;
		}

		$normalizedServerItem = self::normalizeComparableItemForProtocol($protocol, $serverItem);
		$normalizedClientItem = self::normalizeComparableItemForProtocol($protocol, $clientItem);

		return $normalizedServerItem->deepEquals($normalizedClientItem, true, true, $checkCount);
	}

	public static function normalizeAnvilClientResultForProtocol(int $protocol, Item $target, Item $clientResult) : Item{
		if($target->getId() === Item::AIR or $clientResult->getId() === Item::AIR){
			return $clientResult;
		}
		if($clientResult->deepEquals($target, true, false, true)){
			return $clientResult;
		}
		if(!self::requiresLegacyRedstoneMapping($protocol) or self::isProtocol011($protocol)){
			return $clientResult;
		}

		$mappedTarget = self::mapItemForProtocol($protocol, $target, false);
		if($mappedTarget->getId() === Item::AIR or !$mappedTarget->deepEquals($clientResult, true, false, true)){
			return $clientResult;
		}

		$normalized = clone $target;
		$normalized->setCount($clientResult->getCount());
		if($clientResult->hasCustomName()){
			if($mappedTarget->hasCustomName() and $clientResult->getCustomName() === $mappedTarget->getCustomName()){
				if($target->hasCustomName()){
					$normalized->setCustomName($target->getCustomName());
				}else{
					$normalized->clearCustomName();
				}
			}else{
				$normalized->setCustomName($clientResult->getCustomName());
			}
		}else{
			$normalized->clearCustomName();
		}

		return $normalized;
	}

	public static function sanitizeContainerItemFor013(int $itemId, int $itemMeta, int $count) : array{
		return self::sanitizeContainerItemForProtocol(37, $itemId, $itemMeta, $count);
	}

	public static function sanitizeContainerItemForProtocol(int $protocol, int $itemId, int $itemMeta, int $count) : array{
		if($itemId === 0){
			return [0, 0, 0];
		}

		$count = max(1, $count);
		if((self::usesLegacy013Mappings($protocol) and self::isHiddenItemForProtocol($protocol, $itemId, $itemMeta)) or
				(self::isProtocol014($protocol) and isset(self::LEGACY_HORSE_ARMOR_HIDDEN_ITEM_IDS[$itemId])) or
				(self::requiresLegacyRedstoneMapping($protocol) and isset(self::LEGACY_015_ONLY_HIDDEN_ITEM_IDS[$itemId]))){
			return [1, 0, $count];
		}

		return self::sanitizeItemForProtocol($protocol, $itemId, $itemMeta, $count);
	}

	public static function itemsMatchAfterProtocolMapping(int $protocol, int $serverItemId, int $serverItemMeta, int $serverCount, int $clientItemId, int $clientItemMeta, int $clientCount) : bool{
		[$mappedId, $mappedMeta, $mappedCount] = self::sanitizeItemForProtocol($protocol, $serverItemId, $serverItemMeta, $serverCount);
		return $mappedId === $clientItemId and $mappedMeta === $clientItemMeta and $mappedCount === $clientCount;
	}

	public static function itemsMatchAfter013Mapping(int $serverItemId, int $serverItemMeta, int $serverCount, int $clientItemId, int $clientItemMeta, int $clientCount) : bool{
		return self::itemsMatchAfterProtocolMapping(37, $serverItemId, $serverItemMeta, $serverCount, $clientItemId, $clientItemMeta, $clientCount);
	}

	public static function formatLegacy011StatusTip(float $food, int $level, float $progress) : string{
		$foodPercent = (int) round(max(0, min(20, $food)) / 20 * 100);
		$expPercent = (int) round(max(0, min(1, $progress)) * 100);

		return "\xc2\xa7f\u{98df}\u{7269}\u{503c}\u{ff1a}\xc2\xa7b{$foodPercent}%  \xc2\xa7fXP\u{503c}\u{ff1a}\xc2\xa7aLv.{$level}\xc2\xa7f|\xc2\xa7b{$expPercent}%";
	}

	private static function getLegacy011EnchantmentName(Enchantment $enchantment) : string{
		if(isset(self::LEGACY_011_ENCHANTMENT_NAMES[$enchantment->getId()])){
			return self::LEGACY_011_ENCHANTMENT_NAMES[$enchantment->getId()];
		}

		return "\u{672a}\u{77e5}\u{9644}\u{9b54}" . $enchantment->getId();
	}

	private static function getLegacy011EnchantmentLevel(int $level) : string{
		return self::LEGACY_011_ENCHANTMENT_LEVELS[$level] ?? $level . "\u{7ea7}";
	}

	public static function formatLegacy011EnchantmentsMessage(Item $item) : ?string{
		$entries = [];
		foreach($item->getEnchantments() as $enchantment){
			if($enchantment->getId() === Enchantment::TYPE_INVALID or $enchantment->getLevel() <= 0){
				continue;
			}

			$entries[] = self::getLegacy011EnchantmentName($enchantment) . " " . self::getLegacy011EnchantmentLevel((int) $enchantment->getLevel());
		}

		if(count($entries) === 0){
			return null;
		}

		return TextFormat::AQUA . "\u{9644}\u{9b54}\u{ff1a}" . TextFormat::WHITE . implode("\u{ff0c}", $entries);
	}

	private static function isValidUtf8(string $text) : bool{
		return preg_match("//u", $text) === 1;
	}

	private static function filterLegacy011MessageText(string $text) : string{
		$text = trim(str_replace(["\x00", "\r", "\n"], " ", $text));
		if($text === "" or self::isValidUtf8($text)){
			return $text;
		}

		if(function_exists("iconv")){
			foreach(["GB18030", "GBK", "BIG5"] as $encoding){
				$converted = @iconv($encoding, "UTF-8//IGNORE", $text);
				if(is_string($converted)){
					$converted = trim(str_replace(["\x00", "\r", "\n"], " ", $converted));
					if($converted !== "" and self::isValidUtf8($converted)){
						return $converted;
					}
				}
			}

			$converted = @iconv("UTF-8", "UTF-8//IGNORE", $text);
			if(is_string($converted)){
				$converted = trim(str_replace(["\x00", "\r", "\n"], " ", $converted));
				if($converted !== "" and self::isValidUtf8($converted)){
					return $converted;
				}
			}
		}

		return "";
	}

	public static function formatLegacy011ItemDetailsMessage(Item $item) : ?string{
		$messages = [];
		$enchantments = self::formatLegacy011EnchantmentsMessage($item);
		if($enchantments !== null){
			$messages[] = $enchantments;
		}

		return count($messages) > 0 ? implode(TextFormat::GRAY . " | ", $messages) : null;
	}

	private static function getLegacyEnchantingGoldenAppleId() : int{
		return defined(Item::class . "::ENCHANTING_GOLDEN_APPLE") ? (int) constant(Item::class . "::ENCHANTING_GOLDEN_APPLE") : Item::ENCHANTED_GOLDEN_APPLE;
	}

	public static function remapChunkPayloadFor013(string $payload) : string{
		return self::remapChunkPayloadForProtocol(37, $payload);
	}

	public static function remapChunkPayloadForProtocol(int $protocol, string $payload) : string{
		$minimumLength = self::CHUNK_BLOCK_IDS_LENGTH + self::CHUNK_BLOCK_DATA_LENGTH;
		if(strlen($payload) < $minimumLength){
			return $payload;
		}

		$blockIds = substr($payload, 0, self::CHUNK_BLOCK_IDS_LENGTH);
		$blockData = substr($payload, self::CHUNK_BLOCK_IDS_LENGTH, self::CHUNK_BLOCK_DATA_LENGTH);
		$restrictedChunkBlockBytes = self::getRestrictedChunkBlockBytesForProtocol($protocol);
		if($restrictedChunkBlockBytes === "" or strpbrk($blockIds, $restrictedChunkBlockBytes) === false){
			return self::usesLegacy013Mappings($protocol) ? self::stripHiddenTileEntitiesFor013($payload) : $payload;
		}

		$modified = false;

		for($index = 0; $index < self::CHUNK_BLOCK_IDS_LENGTH; ++$index){
			$blockId = ord($blockIds[$index]);
			if(!self::isRestrictedBlockIdForProtocol($protocol, $blockId)){
				continue;
			}

			$dataIndex = $index >> 1;
			$dataByte = ord($blockData[$dataIndex]);
			$blockMeta = ($index & 1) === 0 ? ($dataByte & 0x0f) : ($dataByte >> 4);
			[$mappedId, $mappedMeta] = self::mapBlockForProtocol($protocol, $blockId, $blockMeta);

			if($mappedId !== $blockId){
				$blockIds[$index] = chr($mappedId);
				$modified = true;
			}

			if($mappedMeta !== $blockMeta){
				if(($index & 1) === 0){
					$blockData[$dataIndex] = chr(($dataByte & 0xf0) | ($mappedMeta & 0x0f));
				}else{
					$blockData[$dataIndex] = chr((($mappedMeta & 0x0f) << 4) | ($dataByte & 0x0f));
				}
				$modified = true;
			}
		}

		if($modified){
			$payload = $blockIds . $blockData . substr($payload, $minimumLength);
		}

		return self::usesLegacy013Mappings($protocol) ? self::stripHiddenTileEntitiesFor013($payload) : $payload;
	}

	public static function stripHiddenTileEntitiesFor013(string $payload) : string{
		if(strlen($payload) <= self::CHUNK_EXTRA_DATA_OFFSET + 4){
			return $payload;
		}

		$extraData = substr($payload, self::CHUNK_EXTRA_DATA_OFFSET);
		$extraCount = Binary::readLInt(substr($extraData, 0, 4));
		$tileDataOffset = 4 + ($extraCount * 6);
		if($extraCount < 0 or strlen($extraData) < $tileDataOffset){
			return $payload;
		}

		$tileData = substr($extraData, $tileDataOffset);
		if($tileData === ""){
			return $payload;
		}

		try{
			$nbt = new NBT(NBT::LITTLE_ENDIAN);
			$nbt->read($tileData, true);
			$tags = $nbt->getData();
			if(!is_array($tags)){
				return $payload;
			}

			$filtered = [];
			$modified = false;
			foreach($tags as $tag){
				if($tag instanceof CompoundTag and isset($tag->id) and $tag->id instanceof StringTag and self::isHiddenTileIdFor013($tag->id->getValue())){
					$modified = true;
					continue;
				}
				$filtered[] = $tag;
			}

			if(!$modified){
				return $payload;
			}

			$nbt->setData($filtered);
			$newTileData = $nbt->write();
			if($newTileData === false){
				return $payload;
			}

			return substr($payload, 0, self::CHUNK_EXTRA_DATA_OFFSET + $tileDataOffset) . $newTileData;
		}catch(\Throwable $e){
			return $payload;
		}
	}

	public static function remapPacketBufferFor013(string $buffer) : string{
		return self::remapPacketBufferForProtocol(37, $buffer);
	}

	public static function remapPacketBufferForProtocol(int $protocol, string $buffer) : string{
		$header = self::readPacketHeader($buffer);
		if($header === null){
			return $buffer;
		}

		[$pid, $packetOffset] = $header;
		if($pid === Info::LEVEL_EVENT_PACKET){
			$eventOffset = $packetOffset;
			$dataOffset = $eventOffset + 14;
			if(strlen($buffer) < $dataOffset + 4){
				return $buffer;
			}

			$eventId = Binary::readShort(substr($buffer, $eventOffset, 2));
			$data = Binary::readInt(substr($buffer, $dataOffset, 4));
			$mappedData = self::mapLevelEventDataForProtocol($protocol, $eventId, $data);
			if($mappedData === $data){
				return $buffer;
			}

			return substr($buffer, 0, $dataOffset) . Binary::writeInt($mappedData) . substr($buffer, $dataOffset + 4);
		}

		if($pid !== Info::FULL_CHUNK_DATA_PACKET){
			return $buffer;
		}

		$dataLengthOffset = $packetOffset + 9;
		if(strlen($buffer) < $dataLengthOffset + 4){
			return $buffer;
		}

		$dataLength = Binary::readInt(substr($buffer, $dataLengthOffset, 4));
		$dataOffset = $dataLengthOffset + 4;
		if($dataLength < 0 or strlen($buffer) < $dataOffset + $dataLength){
			return $buffer;
		}

		$data = substr($buffer, $dataOffset, $dataLength);
		$mappedData = self::remapChunkPayloadForProtocol($protocol, $data);
		if($mappedData === $data){
			return $buffer;
		}

		return substr($buffer, 0, $dataLengthOffset) .
			Binary::writeInt(strlen($mappedData)) .
			$mappedData .
			substr($buffer, $dataOffset + $dataLength);
	}

	public static function remapBatchRawPayloadFor013(string $rawPayload) : string{
		return self::remapBatchRawPayloadForProtocol(37, $rawPayload);
	}

	public static function remapBatchRawPayloadForProtocol(int $protocol, string $rawPayload) : string{
		$length = strlen($rawPayload);
		$offset = 0;
		$mappedPayload = "";
		$modified = false;

		while($offset < $length){
			if($length - $offset < 4){
				return $rawPayload;
			}

			$packetLength = Binary::readInt(substr($rawPayload, $offset, 4));
			$offset += 4;
			if($packetLength < 0 or $length - $offset < $packetLength){
				return $rawPayload;
			}

			$packetBuffer = substr($rawPayload, $offset, $packetLength);
			$offset += $packetLength;

			$mappedPacketBuffer = self::remapPacketBufferForProtocol($protocol, $packetBuffer);
			if($mappedPacketBuffer !== $packetBuffer){
				$modified = true;
			}

			$mappedPayload .= Binary::writeInt(strlen($mappedPacketBuffer)) . $mappedPacketBuffer;
		}

		return $modified ? $mappedPayload : $rawPayload;
	}

	public static function remapBatchPayloadFor013(string $payload, int $compressionLevel = -1) : string{
		return self::remapBatchPayloadForProtocol(37, $payload, $compressionLevel);
	}

	public static function remapBatchPayloadForProtocol(int $protocol, string $payload, int $compressionLevel = -1) : string{
		$rawPayload = @zlib_decode($payload, 1024 * 1024 * 64);
		if(!is_string($rawPayload)){
			return $payload;
		}

		$mappedPayload = self::remapBatchRawPayloadForProtocol($protocol, $rawPayload);
		if($mappedPayload === $rawPayload){
			return $payload;
		}

		$compressedPayload = zlib_encode($mappedPayload, ZLIB_ENCODING_DEFLATE, $compressionLevel);
		return is_string($compressedPayload) ? $compressedPayload : $payload;
	}
}
