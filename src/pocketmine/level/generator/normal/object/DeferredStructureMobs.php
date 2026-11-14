<?php

/*
 * ██╗███╗   ██╗ ██████╗ ██████╗ ██████╗ ███████╗
 * ██║████╗  ██║██╔════╝██╔═══██╗██╔══██╗██╔════╝
 * ██║██╔██╗ ██║██║     ██║   ██║██████╔╝█████╗
 * ██║██║╚██╗██║██║     ██║   ██║██╔══██╗██╔══╝
 * ██║██║ ╚████║╚██████╗╚██████╔╝██║  ██║███████╗
 * ╚═╝╚═╝  ╚═══╝ ╚═════╝ ╚═════╝ ╚═╝  ╚═╝╚══════╝
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * @Author: The_Bird, GoodCat456872
 *
 * @Links:
 *  > https://github.com/QwQ12222/InCore-II#incore-ii
 *  > https://gitcode.com/bad-cat-factory/InCore-II-InCore/tree/InCore-II
*/

namespace pocketmine\level\generator\normal\object;

use pocketmine\item\Item;
use pocketmine\item\enchantment\Enchantment;
use pocketmine\nbt\tag\ByteTag;
use pocketmine\nbt\tag\CompoundTag;
use pocketmine\nbt\tag\DoubleTag;
use pocketmine\nbt\tag\FloatTag;
use pocketmine\nbt\tag\ListTag;
use pocketmine\nbt\tag\ShortTag;
use pocketmine\nbt\tag\StringTag;

/**
 * 村庄 / 前哨站 / 林地府邸结构延迟刷怪的纯数据构造器。
 *
 * 结构生成器只在区块里写 marker（VillagePopulator::MARKER_*、
 * PillagerOutpost::MARKER_*、WoodlandMansion::MARKER_*），真正的实体由
 * Level::processDeferredStructureContainers() 在区块落地后创建。这里集中放
 * 「不需要 Level 实例」的部分：实体 NBT、村民职业、掠夺者僵尸装备。
 *
 * 移植自 InCore-II 的 Level::createDeferredVillageMobNBT() /
 * createDeferredPillagerOutpostZombieArmor()（LGPLv3）。
 */
class DeferredStructureMobs{

	const DEFAULT_HEALTH = 20;
	const IRON_GOLEM_HEALTH = 100;
	const VILLAGER_PROFESSION_COUNT = 5;

	const RAIDER_ZOMBIE_NAME = "掠夺者";
	const RAIDER_ZOMBIE_ARMOR_PROTECTION_LEVEL = 3;

	/** 村民职业种子常量，与 InCore-II 保持一致 */
	const PROFESSION_SEED_X = 73428767;
	const PROFESSION_SEED_Z = 912931;
	const PROFESSION_SEED_MASK = 0x7fffffff;

	/**
	 * 构造延迟生成的生物 NBT。
	 *
	 * @param float $x 世界坐标（实体中心）
	 * @param float $y
	 * @param float $z
	 * @param int   $health     生命值；铁傀儡 100，其余 20
	 * @param int   $profession 村民职业 0..4；负数表示不是村民，不写 Profession
	 */
	public static function createMobNBT(float $x, float $y, float $z, int $health = self::DEFAULT_HEALTH, int $profession = -1) : CompoundTag{
		$tags = [
			new ListTag("Pos", [
				new DoubleTag("", $x),
				new DoubleTag("", $y),
				new DoubleTag("", $z)
			]),
			new ListTag("Motion", [
				new DoubleTag("", 0),
				new DoubleTag("", 0),
				new DoubleTag("", 0)
			]),
			new ListTag("Rotation", [
				new FloatTag("", 0),
				new FloatTag("", 0)
			]),
			new ShortTag("Health", $health)
		];

		if($profession >= 0){
			$tags[] = new ByteTag("Profession", $profession % self::VILLAGER_PROFESSION_COUNT);
		}

		return new CompoundTag("", $tags);
	}

	/**
	 * 村民职业：由世界种子与判定位置确定，同一位置结果稳定（便于重生成/排障）。
	 */
	public static function createVillagerProfession(int $seed, float $x, float $z) : int{
		$professionSeed = (int) (($seed ^ ((int) floor($x) * self::PROFESSION_SEED_X) ^ ((int) floor($z) * self::PROFESSION_SEED_Z)) & self::PROFESSION_SEED_MASK);

		return $professionSeed % self::VILLAGER_PROFESSION_COUNT;
	}

	/**
	 * 前哨站的「掠夺者」僵尸：20 点生命 + 自定义名称。
	 */
	public static function createRaiderZombieNBT(float $x, float $y, float $z) : CompoundTag{
		$nbt = self::createMobNBT($x, $y, $z);
		$nbt->CustomName = new StringTag("CustomName", self::RAIDER_ZOMBIE_NAME);
		$nbt->CustomNameVisible = new ByteTag("CustomNameVisible", 1);

		return $nbt;
	}

	/**
	 * 掠夺者僵尸的护甲槽位顺序（头盔 → 靴子），与 MobEquipment 槽位 0..3 对应。
	 */
	public static function getRaiderZombieArmorLayout() : array{
		return ["helmet", "chestplate", "leggings", "boots"];
	}

	/**
	 * 构造掠夺者僵尸的四件钻石护甲（保护 III）。
	 *
	 * 只在服务器运行时调用：会触达 Item / Enchantment 的初始化逻辑。
	 *
	 * @return Item[]
	 */
	public static function createRaiderZombieArmor() : array{
		$armor = [];
		foreach(self::getRaiderZombieArmorLayout() as $slot){
			$item = Item::get(self::getArmorItemId($slot), 0, 1);
			$item->addEnchantment(Enchantment::getEnchantment(Enchantment::TYPE_ARMOR_PROTECTION)->setLevel(self::RAIDER_ZOMBIE_ARMOR_PROTECTION_LEVEL));
			$armor[] = $item;
		}

		return $armor;
	}

	private static function getArmorItemId(string $slot) : int{
		switch($slot){
			case "helmet":
				return Item::DIAMOND_HELMET;
			case "chestplate":
				return Item::DIAMOND_CHESTPLATE;
			case "leggings":
				return Item::DIAMOND_LEGGINGS;
			case "boots":
				return Item::DIAMOND_BOOTS;
			default:
				return Item::AIR;
		}
	}

}
