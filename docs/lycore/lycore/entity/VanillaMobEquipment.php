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

namespace lycore\entity;

use lycore\item\Armor;
use lycore\item\enchantment\Enchantment;
use lycore\item\Item as ItemItem;
use lycore\item\Tool;

final class VanillaMobEquipment{
	const SLOT_HELMET = 0;
	const SLOT_CHESTPLATE = 1;
	const SLOT_LEGGINGS = 2;
	const SLOT_BOOTS = 3;

	private static $profiles = [
		0 => [
			"armorChance" => 0,
			"weaponChance" => 0,
			"enchantChance" => 0,
			"maxEnchantLevel" => 0,
			"equipmentDropChance" => 0,
			"rareDropChance" => 0,
		],
		1 => [
			"armorChance" => 12,
			"weaponChance" => 18,
			"enchantChance" => 16,
			"maxEnchantLevel" => 2,
			"equipmentDropChance" => 85,
			"rareDropChance" => 25,
		],
		2 => [
			"armorChance" => 22,
			"weaponChance" => 32,
			"enchantChance" => 30,
			"maxEnchantLevel" => 3,
			"equipmentDropChance" => 85,
			"rareDropChance" => 25,
		],
		3 => [
			"armorChance" => 35,
			"weaponChance" => 45,
			"enchantChance" => 45,
			"maxEnchantLevel" => 4,
			"equipmentDropChance" => 85,
			"rareDropChance" => 25,
		],
	];

	private static $armorSets = [
		[
			ItemItem::LEATHER_CAP,
			ItemItem::LEATHER_TUNIC,
			ItemItem::LEATHER_PANTS,
			ItemItem::LEATHER_BOOTS,
		],
		[
			ItemItem::GOLD_HELMET,
			ItemItem::GOLD_CHESTPLATE,
			ItemItem::GOLD_LEGGINGS,
			ItemItem::GOLD_BOOTS,
		],
		[
			ItemItem::CHAIN_HELMET,
			ItemItem::CHAIN_CHESTPLATE,
			ItemItem::CHAIN_LEGGINGS,
			ItemItem::CHAIN_BOOTS,
		],
		[
			ItemItem::IRON_HELMET,
			ItemItem::IRON_CHESTPLATE,
			ItemItem::IRON_LEGGINGS,
			ItemItem::IRON_BOOTS,
		],
		[
			ItemItem::DIAMOND_HELMET,
			ItemItem::DIAMOND_CHESTPLATE,
			ItemItem::DIAMOND_LEGGINGS,
			ItemItem::DIAMOND_BOOTS,
		],
	];

	private function __construct(){
	}

	public static function javaDifficulty($difficulty) : int{
		$difficulty = (int) $difficulty;
		if($difficulty < 0){
			return 0;
		}
		if($difficulty > 3){
			return 3;
		}
		return $difficulty;
	}

	public static function getProfile($difficulty) : array{
		return self::$profiles[self::javaDifficulty($difficulty)];
	}

	public static function emptyArmor() : array{
		return [
			ItemItem::get(ItemItem::AIR, 0, 0),
			ItemItem::get(ItemItem::AIR, 0, 0),
			ItemItem::get(ItemItem::AIR, 0, 0),
			ItemItem::get(ItemItem::AIR, 0, 0),
		];
	}

	public static function generateZombieEquipment($difficulty) : array{
		$profile = self::getProfile($difficulty);
		$weapon = ItemItem::get(ItemItem::AIR, 0, 0);
		if(self::roll($profile["weaponChance"])){
			$weapon = self::randomZombieWeapon($profile);
		}

		return [
			"weapon" => $weapon,
			"armor" => self::randomArmor($profile),
		];
	}

	public static function generateSkeletonEquipment($difficulty) : array{
		$profile = self::getProfile($difficulty);
		$weapon = ItemItem::get(ItemItem::BOW, 0, 1);
		self::damageSpawnedEquipment($weapon);
		if(self::roll($profile["enchantChance"])){
			$weapon = self::enchantBow($weapon, $profile);
		}

		return [
			"weapon" => $weapon,
			"armor" => self::randomArmor($profile),
		];
	}

	public static function generatePigZombieEquipment($difficulty) : array{
		$profile = self::getProfile($difficulty);
		$weapon = ItemItem::get(ItemItem::GOLD_SWORD, 0, 1);
		self::damageSpawnedEquipment($weapon);
		if(self::roll($profile["enchantChance"])){
			$weapon = self::enchantMeleeWeapon($weapon, $profile);
		}

		return [
			"weapon" => $weapon,
			"armor" => self::emptyArmor(),
		];
	}

	public static function applyLootingToCommonDrops(array $drops, int $lootingLevel) : array{
		$lootingLevel = self::clampLooting($lootingLevel);
		if($lootingLevel <= 0){
			return $drops;
		}

		foreach($drops as $item){
			if($item instanceof ItemItem and $item->getId() !== ItemItem::AIR){
				$item->setCount($item->getCount() + mt_rand(0, $lootingLevel));
			}
		}

		return $drops;
	}

	public static function rareDropChance(int $lootingLevel) : int{
		return min(1000, self::$profiles[1]["rareDropChance"] + self::clampLooting($lootingLevel) * 10);
	}

	public static function equipmentDropChance(int $lootingLevel) : int{
		return min(1000, self::$profiles[1]["equipmentDropChance"] + self::clampLooting($lootingLevel) * 10);
	}

	public static function maybeDropEquipment(array $armor, ItemItem $weapon = null, int $lootingLevel = 0) : array{
		$drops = [];
		$chance = self::equipmentDropChance($lootingLevel);
		if($weapon instanceof ItemItem and $weapon->getId() !== ItemItem::AIR and self::roll($chance, 1000)){
			$drops[] = clone $weapon;
		}

		foreach($armor as $piece){
			if($piece instanceof ItemItem and $piece->getId() !== ItemItem::AIR and self::roll($chance, 1000)){
				$drops[] = clone $piece;
			}
		}

		return $drops;
	}

	public static function applyArmorReduction($damage, array $armor, int $cause = null){
		$armorPoints = 0;
		$enchantmentProtection = 0;
		foreach($armor as $piece){
			if(!($piece instanceof ItemItem) or !$piece->isArmor()){
				continue;
			}
			$armorPoints += (int) $piece->getArmorValue();
			$enchantmentProtection += self::getProtectionPoints($piece, $cause);
		}

		if($armorPoints <= 0 and $enchantmentProtection <= 0){
			return $damage;
		}

		$reduced = $damage * (1 - min(20, $armorPoints) * 0.04);
		if($enchantmentProtection > 0){
			$reduced *= (1 - min(20, $enchantmentProtection) * 0.04);
		}
		return max(0, $reduced);
	}

	public static function getWeaponBaseDamage(ItemItem $item = null) : int{
		if(!($item instanceof ItemItem)){
			return 1;
		}

		switch($item->getId()){
			case ItemItem::WOODEN_SWORD:
			case ItemItem::GOLD_SWORD:
				return 4;
			case ItemItem::STONE_SWORD:
				return 5;
			case ItemItem::IRON_SWORD:
				return 6;
			case ItemItem::DIAMOND_SWORD:
				return 7;
			case ItemItem::WOODEN_AXE:
			case ItemItem::GOLD_AXE:
				return 3;
			case ItemItem::STONE_AXE:
			case ItemItem::IRON_AXE:
			case ItemItem::DIAMOND_AXE:
				return 5;
			case ItemItem::WOODEN_SHOVEL:
			case ItemItem::GOLD_SHOVEL:
				return 2;
			case ItemItem::STONE_SHOVEL:
				return 3;
			case ItemItem::IRON_SHOVEL:
				return 4;
			case ItemItem::DIAMOND_SHOVEL:
				return 5;
		}

		return 1;
	}

	public static function getLootingLevelFromEntity(Entity $entity = null) : int{
		if(!($entity instanceof Human)){
			return 0;
		}
		return self::clampLooting($entity->getInventory()->getItemInHand()->getEnchantmentLevel(Enchantment::TYPE_WEAPON_LOOTING));
	}

	private static function randomZombieWeapon(array $profile) : ItemItem{
		$id = self::weightedRandom([
			ItemItem::WOODEN_SWORD => 35,
			ItemItem::STONE_SWORD => 30,
			ItemItem::IRON_SWORD => 20,
			ItemItem::IRON_SHOVEL => 10,
			ItemItem::DIAMOND_SWORD => 5,
		]);
		$item = ItemItem::get($id, 0, 1);
		self::damageSpawnedEquipment($item);
		if(self::roll($profile["enchantChance"])){
			$item = self::enchantMeleeWeapon($item, $profile);
		}
		return $item;
	}

	private static function randomArmor(array $profile) : array{
		$armor = self::emptyArmor();
		if(!self::roll($profile["armorChance"])){
			return $armor;
		}

		$set = self::$armorSets[self::weightedIndex([37, 48, 13, 2, 0])];
		if($profile["armorChance"] >= 35 and self::roll(1)){
			$set = self::$armorSets[4];
		}

		$slotsToFill = mt_rand(1, 4);
		for($i = 0; $i < $slotsToFill; ++$i){
			$item = ItemItem::get($set[$i], 0, 1);
			self::damageSpawnedEquipment($item);
			if(self::roll($profile["enchantChance"])){
				$item = self::enchantArmor($item, $profile);
			}
			$armor[$i] = $item;
		}

		return $armor;
	}

	private static function enchantArmor(ItemItem $item, array $profile) : ItemItem{
		$possible = [
			Enchantment::TYPE_ARMOR_PROTECTION => 10,
			Enchantment::TYPE_ARMOR_FIRE_PROTECTION => 5,
			Enchantment::TYPE_ARMOR_EXPLOSION_PROTECTION => 5,
			Enchantment::TYPE_ARMOR_PROJECTILE_PROTECTION => 5,
			Enchantment::TYPE_MINING_DURABILITY => 5,
			Enchantment::TYPE_ARMOR_THORNS => 1,
		];

		if(method_exists($item, "isBoots") and $item->isBoots()){
			$possible[Enchantment::TYPE_ARMOR_FALL_PROTECTION] = 3;
		}
		if(method_exists($item, "isHelmet") and $item->isHelmet()){
			$possible[Enchantment::TYPE_WATER_BREATHING] = 1;
			$possible[Enchantment::TYPE_WATER_AFFINITY] = 1;
		}

		return self::addRandomEnchantments($item, $possible, $profile);
	}

	private static function enchantMeleeWeapon(ItemItem $item, array $profile) : ItemItem{
		return self::addRandomEnchantments($item, [
			Enchantment::TYPE_WEAPON_SHARPNESS => 10,
			Enchantment::TYPE_WEAPON_SMITE => 5,
			Enchantment::TYPE_WEAPON_ARTHROPODS => 5,
			Enchantment::TYPE_WEAPON_KNOCKBACK => 5,
			Enchantment::TYPE_WEAPON_FIRE_ASPECT => 2,
			Enchantment::TYPE_WEAPON_LOOTING => 2,
			Enchantment::TYPE_MINING_DURABILITY => 5,
		], $profile);
	}

	private static function enchantBow(ItemItem $item, array $profile) : ItemItem{
		return self::addRandomEnchantments($item, [
			Enchantment::TYPE_BOW_POWER => 10,
			Enchantment::TYPE_BOW_KNOCKBACK => 2,
			Enchantment::TYPE_BOW_FLAME => 2,
			Enchantment::TYPE_BOW_INFINITY => 1,
			Enchantment::TYPE_MINING_DURABILITY => 5,
		], $profile);
	}

	private static function addRandomEnchantments(ItemItem $item, array $weightedEnchantments, array $profile) : ItemItem{
		$count = self::roll(20) ? 2 : 1;
		$used = [];
		for($i = 0; $i < $count and count($weightedEnchantments) > 0; ++$i){
			$id = self::weightedRandom($weightedEnchantments);
			unset($weightedEnchantments[$id]);
			if(isset($used[$id]) or self::conflictsWithUsed($id, $used)){
				continue;
			}
			$used[$id] = true;

			$max = min(Enchantment::getEnchantMaxLevel($id), max(1, $profile["maxEnchantLevel"]));
			$level = mt_rand(1, $max);
			$item->addEnchantment(Enchantment::getEnchantment($id)->setLevel($level));
		}
		return $item;
	}

	private static function conflictsWithUsed(int $id, array $used) : bool{
		$protection = [
			Enchantment::TYPE_ARMOR_PROTECTION => true,
			Enchantment::TYPE_ARMOR_FIRE_PROTECTION => true,
			Enchantment::TYPE_ARMOR_EXPLOSION_PROTECTION => true,
			Enchantment::TYPE_ARMOR_PROJECTILE_PROTECTION => true,
		];
		if(isset($protection[$id])){
			foreach($used as $usedId => $_){
				if(isset($protection[$usedId])){
					return true;
				}
			}
		}

		$weaponDamage = [
			Enchantment::TYPE_WEAPON_SHARPNESS => true,
			Enchantment::TYPE_WEAPON_SMITE => true,
			Enchantment::TYPE_WEAPON_ARTHROPODS => true,
		];
		if(isset($weaponDamage[$id])){
			foreach($used as $usedId => $_){
				if(isset($weaponDamage[$usedId])){
					return true;
				}
			}
		}

		return false;
	}

	private static function getProtectionPoints(ItemItem $item, int $cause = null) : int{
		$points = $item->getEnchantmentLevel(Enchantment::TYPE_ARMOR_PROTECTION);
		$points += $item->getEnchantmentLevel(Enchantment::TYPE_ARMOR_FIRE_PROTECTION);
		$points += $item->getEnchantmentLevel(Enchantment::TYPE_ARMOR_EXPLOSION_PROTECTION);
		$points += $item->getEnchantmentLevel(Enchantment::TYPE_ARMOR_PROJECTILE_PROTECTION);
		return min(20, $points);
	}

	private static function damageSpawnedEquipment(ItemItem $item){
		$max = $item->getMaxDurability();
		if(!is_int($max) or $max <= 0){
			return;
		}

		$remaining = mt_rand(1, max(1, (int) ($max * 0.25)));
		$item->setDamage(max(0, $max - $remaining));
	}

	private static function clampLooting(int $level) : int{
		return max(0, min(3, $level));
	}

	private static function roll(int $chance, int $outOf = 100) : bool{
		if($chance <= 0){
			return false;
		}
		if($chance >= $outOf){
			return true;
		}
		return mt_rand(1, $outOf) <= $chance;
	}

	private static function weightedIndex(array $weights) : int{
		$sum = array_sum($weights);
		$roll = mt_rand(1, $sum);
		foreach($weights as $index => $weight){
			$roll -= $weight;
			if($roll <= 0){
				return (int) $index;
			}
		}
		return count($weights) - 1;
	}

	private static function weightedRandom(array $weights) : int{
		$sum = array_sum($weights);
		$roll = mt_rand(1, $sum);
		foreach($weights as $value => $weight){
			$roll -= $weight;
			if($roll <= 0){
				return (int) $value;
			}
		}
		return (int) array_keys($weights)[0];
	}
}
