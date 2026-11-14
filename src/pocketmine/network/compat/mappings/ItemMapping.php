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

namespace pocketmine\network\compat\mappings;

use pocketmine\item\Arrow;
use pocketmine\item\enchantment\Enchantment;
use pocketmine\item\Item;
use pocketmine\network\compat\ProtocolCapabilities;
use pocketmine\utils\TextFormat;

/**
 * 物品 ID / 隐藏项 / 替身物品 (surrogate) / legacy 提示文本的协议兼容映射。
 */
final class ItemMapping{

	const LEGACY_011_ITEM_ID_MAP = [
		406 => [1, 0],
	];

	const LEGACY_012_PLACEHOLDER_ITEM_IDS = [
		411 => true,
		412 => true,
		413 => true,
		414 => true,
		415 => true,
	];

	const LEGACY_012_WOODEN_DOOR_ITEM_IDS = [
		427 => true,
		428 => true,
		429 => true,
		430 => true,
		431 => true,
	];

	const LEGACY_013_HIDDEN_ITEM_IDS = [
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

	const LEGACY_HORSE_ARMOR_HIDDEN_ITEM_IDS = [
		416 => true,
		417 => true,
		418 => true,
		419 => true,
	];

	const LEGACY_015_ONLY_HIDDEN_ITEM_IDS = [
		398 => true,
		420 => true,
		421 => true,
		423 => true,
		424 => true,
	];

	const LEGACY_REDSTONE_HIDDEN_ITEM_IDS = [
		29 => true,
		33 => true,
		34 => true,
		251 => true,
	];

	const LEGACY_011_COBBLESTONE_ITEM_IDS = [
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

	const LEGACY_011_BOAT_ITEM_IDS = [
		444 => true,
		445 => true,
		446 => true,
		447 => true,
		448 => true,
	];

	const LEGACY_013_HIDDEN_ITEM_METAS = [
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

	const LEGACY_011_ENCHANTMENT_NAMES = [
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

	const LEGACY_011_ENCHANTMENT_LEVELS = [
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

	const LEGACY_013_COBBLESTONE_ITEM_IDS = [
		23 => true,
		125 => true,
		154 => true,
		199 => true,
		389 => true,
		410 => true,
	];

	private function __construct(){
	}

	public static function isRestrictedItemIdFor013(int $itemId) : bool{
		return self::isRestrictedItemIdForProtocol(37, $itemId);
	}

	public static function isRestrictedItemIdForProtocol(int $protocol, int $itemId) : bool{
		if(ProtocolCapabilities::usesLegacy012Mappings($protocol) and (BlockMapping::needsLegacy012BlockMapping($itemId) or isset(self::LEGACY_012_PLACEHOLDER_ITEM_IDS[$itemId]))){
			return true;
		}

		if(ProtocolCapabilities::isProtocol012($protocol) and isset(self::LEGACY_012_WOODEN_DOOR_ITEM_IDS[$itemId])){
			return true;
		}

		if(ProtocolCapabilities::isProtocol011($protocol)){
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

		if(ProtocolCapabilities::requiresLegacyRedstoneMapping($protocol) and isset(self::LEGACY_REDSTONE_HIDDEN_ITEM_IDS[$itemId])){
			return true;
		}

		if(ProtocolCapabilities::requiresLegacyRedstoneMapping($protocol) and isset(self::LEGACY_HORSE_ARMOR_HIDDEN_ITEM_IDS[$itemId])){
			return true;
		}

		if(ProtocolCapabilities::requiresLegacyRedstoneMapping($protocol) and isset(self::LEGACY_015_ONLY_HIDDEN_ITEM_IDS[$itemId])){
			return true;
		}

		if(ProtocolCapabilities::usesLegacy013Mappings($protocol)){
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
		if(ProtocolCapabilities::requiresLegacyRedstoneMapping($protocol) and isset(self::LEGACY_REDSTONE_HIDDEN_ITEM_IDS[$itemId])){
			return true;
		}

		if(ProtocolCapabilities::requiresLegacyRedstoneMapping($protocol) and isset(self::LEGACY_HORSE_ARMOR_HIDDEN_ITEM_IDS[$itemId])){
			return true;
		}

		if(ProtocolCapabilities::requiresLegacyRedstoneMapping($protocol) and isset(self::LEGACY_015_ONLY_HIDDEN_ITEM_IDS[$itemId])){
			return true;
		}

		return ProtocolCapabilities::usesLegacy013Mappings($protocol) and isset(self::LEGACY_013_HIDDEN_ITEM_IDS[$itemId]);
	}

	public static function isHiddenItemFor013(int $itemId, int $itemMeta = 0) : bool{
		return self::isHiddenItemForProtocol(37, $itemId, $itemMeta);
	}

	public static function isHiddenItemForProtocol(int $protocol, int $itemId, int $itemMeta = 0) : bool{
		if(ProtocolCapabilities::requiresLegacyRedstoneMapping($protocol) and isset(self::LEGACY_REDSTONE_HIDDEN_ITEM_IDS[$itemId])){
			return true;
		}

		if(ProtocolCapabilities::requiresLegacyRedstoneMapping($protocol) and isset(self::LEGACY_HORSE_ARMOR_HIDDEN_ITEM_IDS[$itemId])){
			return true;
		}

		if(ProtocolCapabilities::requiresLegacyRedstoneMapping($protocol) and isset(self::LEGACY_015_ONLY_HIDDEN_ITEM_IDS[$itemId])){
			return true;
		}

		return ProtocolCapabilities::usesLegacy013Mappings($protocol) and (isset(self::LEGACY_013_HIDDEN_ITEM_IDS[$itemId]) or isset(self::LEGACY_013_HIDDEN_ITEM_METAS[$itemId][$itemMeta]));
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
		if(ProtocolCapabilities::requiresLegacyRedstoneMapping($protocol)){
			switch($item->getId()){
				case Item::PISTON:
					return [Item::BOW, 0, 1, "活塞"];
				case Item::STICKY_PISTON:
					return [Item::FISHING_ROD, 0, 2, "粘性活塞"];
				case Item::OBSERVER:
					return [Item::FLINT_AND_STEEL, 0, 3, "侦测器"];
			}
		}

		if(ProtocolCapabilities::isProtocol015($protocol) and $item->getId() === Item::CARROT_ON_A_STICK){
			return self::getLegacyItemSurrogateDefinition($item);
		}

		return null;
	}

	private static function getActualCreativeInventoryItemForProtocol(int $protocol, Item $item) : ?array{
		if(ProtocolCapabilities::requiresLegacyRedstoneMapping($protocol)){
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

		if(ProtocolCapabilities::isProtocol015($protocol)){
			$actual = self::getActualItemFromLegacySurrogate($item);
			if($actual !== null and $actual[0] === Item::CARROT_ON_A_STICK){
				return $actual;
			}
		}

		return null;
	}

	public static function hasLegacyItemSurrogateForProtocol(int $protocol, Item $item) : bool{
		if(ProtocolCapabilities::isProtocol011($protocol) and self::getProtocol011GoldenAppleSurrogateDefinition($item) !== null){
			return true;
		}

		return ProtocolCapabilities::requiresLegacyRedstoneMapping($protocol) and self::getLegacyItemSurrogateDefinition($item) !== null;
	}

	public static function isLegacyItemSurrogateForProtocol(int $protocol, Item $item) : bool{
		if(ProtocolCapabilities::isProtocol011($protocol) and self::getActualProtocol011GoldenAppleSurrogate($item) !== null){
			return true;
		}

		return ProtocolCapabilities::requiresLegacyRedstoneMapping($protocol) and self::getActualItemFromLegacySurrogate($item) !== null;
	}

	public static function mapCreativeInventoryItemForProtocol(int $protocol, Item $item) : Item{
		if(ProtocolCapabilities::requiresLegacyRedstoneMapping($protocol)){
			return self::mapItemForProtocol($protocol, $item, false);
		}

		$surrogateDefinition = self::getCreativeInventorySurrogateDefinitionForProtocol($protocol, $item);
		if($surrogateDefinition !== null){
			return self::createLegacyItemSurrogate($item, $surrogateDefinition);
		}

		return $item;
	}

	public static function normalizeCreativeItemForProtocol(int $protocol, Item $item) : Item{
		if(ProtocolCapabilities::requiresLegacyRedstoneMapping($protocol)){
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
		if(!ProtocolCapabilities::requiresLegacyRedstoneMapping($protocol)){
			return null;
		}

		$normalized = self::normalizeClientItemForProtocol($protocol, $item);
		return self::getLegacySimulatedBlockNameById($normalized->getId(), $normalized->getDamage() === null ? 0 : (int) $normalized->getDamage());
	}

	public static function canLegacyPlayerBreakMappedBlock(int $protocol, int $blockId, int $blockMeta = 0) : bool{
		return ProtocolCapabilities::requiresLegacyRedstoneMapping($protocol) and self::getLegacySimulatedBlockNameById($blockId, $blockMeta) !== null;
	}

	public static function sanitizeItemFor013(int $itemId, int $itemMeta, int $count) : array{
		return self::sanitizeItemForProtocol(37, $itemId, $itemMeta, $count);
	}

	public static function sanitizeItemForProtocol(int $protocol, int $itemId, int $itemMeta, int $count) : array{
		if($itemId === 0){
			return [0, 0, 0];
		}

		$count = max(1, $count);
		if(ProtocolCapabilities::usesLegacy012Mappings($protocol) and BlockMapping::needsLegacy012BlockMapping($itemId)){
			[$mappedId, $mappedMeta] = BlockMapping::mapLegacy012Block($itemId, $itemMeta);
			return [$mappedId, $mappedMeta, $mappedId === 0 ? 0 : $count];
		}

		if(ProtocolCapabilities::usesLegacy012Mappings($protocol) and isset(self::LEGACY_012_PLACEHOLDER_ITEM_IDS[$itemId])){
			return [Item::STONE, 0, $count];
		}

		if(ProtocolCapabilities::isProtocol012($protocol) and isset(self::LEGACY_012_WOODEN_DOOR_ITEM_IDS[$itemId])){
			return [Item::WOODEN_DOOR, 0, $count];
		}

		if(ProtocolCapabilities::isProtocol011($protocol)){
			if(isset(BlockMapping::LEGACY_011_BLOCK_ID_MAP[$itemId])){
				[$mappedId, $mappedMeta] = BlockMapping::LEGACY_011_BLOCK_ID_MAP[$itemId];
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

		if(ProtocolCapabilities::usesLegacy013Mappings($protocol) and isset(self::LEGACY_013_COBBLESTONE_ITEM_IDS[$itemId])){
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

		if(ProtocolCapabilities::requiresLegacyRedstoneMapping($protocol) and $item instanceof Arrow and $item->isTipped()){
			return $item->toLegacyTippedArrowSurrogate();
		}

		if(ProtocolCapabilities::isProtocol011($protocol)){
			$surrogateDefinition = self::getProtocol011GoldenAppleSurrogateDefinition($item);
			if($surrogateDefinition !== null){
				return self::stripProtocol011CustomName(self::createLegacyItemSurrogate($item, $surrogateDefinition));
			}
		}

		if(ProtocolCapabilities::requiresLegacyRedstoneMapping($protocol)){
			$surrogateDefinition = self::getLegacyItemSurrogateDefinition($item);
			if($surrogateDefinition !== null){
				$surrogate = self::createLegacyItemSurrogate($item, $surrogateDefinition);
				return ProtocolCapabilities::isProtocol011($protocol) ? self::stripProtocol011CustomName($surrogate) : $surrogate;
			}
		}

		$meta = $item->getDamage();
		$mapped = $container ?
			self::sanitizeContainerItemForProtocol($protocol, $item->getId(), $meta === null ? 0 : (int) $meta, $item->getCount()) :
			self::sanitizeItemForProtocol($protocol, $item->getId(), $meta === null ? 0 : (int) $meta, $item->getCount());

		if($mapped === [$item->getId(), $meta === null ? 0 : (int) $meta, $item->getCount()]){
			return ProtocolCapabilities::isProtocol011($protocol) ? self::stripProtocol011CustomName($item) : $item;
		}

		$mappedItem = Item::get($mapped[0], $mapped[1], $mapped[2]);
		return ProtocolCapabilities::isProtocol011($protocol) ? self::stripProtocol011CustomName($mappedItem) : $mappedItem;
	}

	public static function stripProtocol011CustomName(Item $item) : Item{
		if(!$item->hasCustomName()){
			return $item;
		}

		$filtered = clone $item;
		$filtered->clearCustomName();
		return $filtered;
	}

	public static function normalizeClientItemForProtocol(int $protocol, Item $item) : Item{
		$arrow = Arrow::fromLegacyTippedArrowSurrogate($item);
		if($arrow instanceof Arrow){
			return $arrow;
		}

		if(ProtocolCapabilities::isProtocol011($protocol)){
			$item = self::stripProtocol011CustomName($item);
		}

		if(ProtocolCapabilities::isProtocol011($protocol)){
			$actual = self::getActualProtocol011GoldenAppleSurrogate($item);
			if($actual !== null){
				return Item::get($actual[0], $actual[1], $item->getCount());
			}
		}

		if(ProtocolCapabilities::requiresLegacyRedstoneMapping($protocol)){
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

		if(!ProtocolCapabilities::requiresLegacyRedstoneMapping($protocol)){
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
		if(!ProtocolCapabilities::requiresLegacyRedstoneMapping($protocol) or ProtocolCapabilities::isProtocol011($protocol)){
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
		if((ProtocolCapabilities::usesLegacy013Mappings($protocol) and self::isHiddenItemForProtocol($protocol, $itemId, $itemMeta)) or
				(ProtocolCapabilities::isProtocol014($protocol) and isset(self::LEGACY_HORSE_ARMOR_HIDDEN_ITEM_IDS[$itemId])) or
				(ProtocolCapabilities::requiresLegacyRedstoneMapping($protocol) and isset(self::LEGACY_015_ONLY_HIDDEN_ITEM_IDS[$itemId]))){
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

	public static function formatLegacy011ItemDetailsMessage(Item $item) : ?string{
		$messages = [];
		$enchantments = self::formatLegacy011EnchantmentsMessage($item);
		if($enchantments !== null){
			$messages[] = $enchantments;
		}

		return count($messages) > 0 ? implode(TextFormat::GRAY . " | ", $messages) : null;
	}

	public static function getLegacyEnchantingGoldenAppleId() : int{
		return defined(Item::class . "::ENCHANTING_GOLDEN_APPLE") ? (int) constant(Item::class . "::ENCHANTING_GOLDEN_APPLE") : Item::ENCHANTED_GOLDEN_APPLE;
	}
}
