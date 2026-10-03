<?php

/**
 * 移植自 lycore：docs/lycore/lycore/level/generator/normal/object/DesertTempleLoot.php
 * 原文件无独立版权头；命名空间与 use 已调整到本核心（pocketmine\），逻辑保持不变。
 *
 * 兼容性调整：本核心缺少马铠常量（IRON/GOLDEN/DIAMOND_HORSE_ARMOR），
 * 改用 StructureLoot::itemId() 安全取值（缺失时回退到原版物品 ID）。
 *
 * 由 Level::processDeferredStructureContainers() 消费：Temple 写入 CHEST_MARKER，
 * 区块落地后创建箱子并填充本类生成的战利品。
 */

namespace pocketmine\level\generator\normal\object;

use pocketmine\item\Item;
use pocketmine\level\generator\object\StructureLoot;
use pocketmine\utils\Random;

class DesertTempleLoot{
	public static function createItems(int $x, int $y, int $z, int $seed) : array{
		$random = new Random($seed ^ ($x * 73428767) ^ ($y * 912931) ^ ($z * 42317861));
		return StructureLoot::createItems(self::getLootPools(), $random);
	}

	public static function getLootPoolsForTesting() : array{
		return self::getLootPools();
	}

	private static function getLootPools() : array{
		return [
			StructureLoot::pool(2, 4, [
				StructureLoot::entry(Item::DIAMOND, 0, 1, 3, 5),
				StructureLoot::entry(Item::IRON_INGOT, 0, 1, 5, 15),
				StructureLoot::entry(Item::GOLD_INGOT, 0, 2, 7, 15),
				StructureLoot::entry(Item::EMERALD, 0, 1, 3, 15),
				StructureLoot::entry(Item::BONE, 0, 4, 6, 25),
				StructureLoot::entry(Item::SPIDER_EYE, 0, 1, 3, 25),
				StructureLoot::entry(Item::ROTTEN_FLESH, 0, 3, 7, 25),
				StructureLoot::entry(Item::LEATHER, 0, 1, 5, 20),
				StructureLoot::entry(Item::SADDLE, 0, 1, 1, 20),
				StructureLoot::entry(StructureLoot::itemId("IRON_HORSE_ARMOR", 417), 0, 1, 1, 15),
				StructureLoot::entry(StructureLoot::itemId("GOLDEN_HORSE_ARMOR", 418), 0, 1, 1, 10),
				StructureLoot::entry(StructureLoot::itemId("DIAMOND_HORSE_ARMOR", 419), 0, 1, 1, 5),
				StructureLoot::entry(Item::ENCHANTED_BOOK, 0, 1, 1, 20),
				StructureLoot::entry(Item::GOLDEN_APPLE, 0, 1, 1, 20),
				StructureLoot::entry(Item::ENCHANTED_GOLDEN_APPLE, 0, 1, 1, 2),
				StructureLoot::entry(Item::AIR, 0, 1, 1, 15),
			]),
			StructureLoot::pool(4, 4, [
				StructureLoot::entry(Item::BONE, 0, 1, 8, 10),
				StructureLoot::entry(Item::GUNPOWDER, 0, 1, 8, 10),
				StructureLoot::entry(Item::ROTTEN_FLESH, 0, 1, 8, 10),
				StructureLoot::entry(Item::STRING, 0, 1, 8, 10),
				StructureLoot::entry(Item::SAND, 0, 1, 8, 10),
			]),
		];
	}
}
