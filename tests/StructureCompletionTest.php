<?php

use pocketmine\level\generator\normal\object\DeferredStructureMobs;
use pocketmine\level\generator\normal\object\JungleTempleLoot;
use pocketmine\level\generator\normal\populator\Temple;

/**
 * 结构补全（B4）离线测试：只验证纯数据 / 布局逻辑，不生成真实区块。
 *
 * 覆盖：
 *  - 村庄 / 前哨站延迟刷怪的 NBT 数组结构（Pos / Motion / Rotation / Health / Profession）
 *  - 村民职业的确定性推导（与 InCore-II 公式一致）
 *  - 掠夺者僵尸的自定义名称与护甲槽位布局
 *  - 丛林神庙发射器 marker 的 (id, data) 位编码契约
 *  - 沙漠神殿区域候选点的足迹区块几何
 *
 * Dependency note: NBT tag 类只依赖 pocketmine\utils\Binary；populator 只依赖
 * Populator / VariableAmountPopulator；均不启动 PocketMine 运行时。
 */
class StructureCompletionTest{

	public static function run() : array{
		$root = \dirname(__DIR__);
		foreach(["Tag", "NamedTag", "EndTag", "ByteTag", "ShortTag", "IntTag", "LongTag", "FloatTag", "DoubleTag", "ByteArrayTag", "StringTag", "IntArrayTag", "ListTag", "CompoundTag"] as $tag){
			require_once "$root/src/pocketmine/nbt/tag/$tag.php";
		}
		require_once "$root/src/pocketmine/nbt/NBT.php";
		require_once "$root/src/pocketmine/utils/Binary.php";
		require_once "$root/src/pocketmine/level/generator/populator/Populator.php";
		require_once "$root/src/pocketmine/level/generator/populator/VariableAmountPopulator.php";
		require_once "$root/src/pocketmine/level/generator/normal/populator/Temple.php";
		require_once "$root/src/pocketmine/level/generator/normal/object/JungleTempleLoot.php";
		require_once "$root/src/pocketmine/level/generator/normal/object/DeferredStructureMobs.php";

		return \array_merge(
			self::testMobNBTLayout(),
			self::testMobHealth(),
			self::testVillagerProfessionTag(),
			self::testVillagerProfessionFormula(),
			self::testRaiderZombieNBT(),
			self::testRaiderZombieArmorLayout(),
			self::testJungleTempleDispenserMarker(),
			self::testDesertTempleFootprint()
		);
	}

	private static function listValues($list) : array{
		$values = [];
		foreach(array_values($list->getValue()) as $tag){
			$values[] = $tag->getValue();
		}

		return $values;
	}

	private static function testMobNBTLayout() : array{
		$nbt = DeferredStructureMobs::createMobNBT(10.5, 64.0, -3.5);

		$pos = array_map("floatval", self::listValues($nbt->Pos));
		$motion = array_map("floatval", self::listValues($nbt->Motion));
		$rotation = array_map("floatval", self::listValues($nbt->Rotation));

		return [
			["mob_nbt_pos", $pos === [10.5, 64.0, -3.5], "Pos = " . json_encode($pos)],
			["mob_nbt_motion", $motion === [0.0, 0.0, 0.0], "Motion = " . json_encode($motion)],
			["mob_nbt_rotation", $rotation === [0.0, 0.0], "Rotation = " . json_encode($rotation)],
			["mob_nbt_no_profession_by_default", !isset($nbt->Profession), "Profession 标签缺省不写入"],
			["mob_nbt_default_health", (int) $nbt->Health->getValue() === 20, "Health = " . $nbt->Health->getValue()],
		];
	}

	private static function testMobHealth() : array{
		$golem = DeferredStructureMobs::createMobNBT(0.5, 60.0, 0.5, DeferredStructureMobs::IRON_GOLEM_HEALTH);

		return [
			["mob_health_constants", DeferredStructureMobs::DEFAULT_HEALTH === 20 && DeferredStructureMobs::IRON_GOLEM_HEALTH === 100, "默认 20 / 铁傀儡 100"],
			["mob_health_iron_golem", (int) $golem->Health->getValue() === 100, "Health = " . $golem->Health->getValue()],
		];
	}

	private static function testVillagerProfessionTag() : array{
		$nbt = DeferredStructureMobs::createMobNBT(8.5, 70.0, 8.5, 20, 3);
		$clamped = DeferredStructureMobs::createMobNBT(0.5, 70.0, 0.5, 20, 7);

		return [
			["mob_nbt_profession_tag", isset($nbt->Profession) && (int) $nbt->Profession->getValue() === 3, "Profession = " . (isset($nbt->Profession) ? $nbt->Profession->getValue() : "missing")],
			["mob_nbt_profession_clamped", (int) $clamped->Profession->getValue() === 2, "7 % 5 = " . $clamped->Profession->getValue()],
		];
	}

	private static function testVillagerProfessionFormula() : array{
		$seed = 987654321;
		$x = 10.5;
		$z = 20.5;

		// 独立复算 InCore-II 公式：((seed ^ (floor(x) * 73428767) ^ (floor(z) * 912931)) & 0x7fffffff) % 5
		$expected = (int) ((($seed ^ ((int) floor($x) * 73428767) ^ ((int) floor($z) * 912931)) & 0x7fffffff) % 5);

		$actual = DeferredStructureMobs::createVillagerProfession($seed, $x, $z);
		$repeat = DeferredStructureMobs::createVillagerProfession($seed, $x, $z);
		$floored = DeferredStructureMobs::createVillagerProfession($seed, 10.9, 20.9);

		$inRange = true;
		foreach([-2147483648, -1, 0, 1, 424242, 2147483647] as $sampleSeed){
			foreach([[-100.5, 3.5], [0.0, 0.0], [31.5, -7.5]] as $pos){
				$profession = DeferredStructureMobs::createVillagerProfession($sampleSeed, $pos[0], $pos[1]);
				if($profession < 0 || $profession >= DeferredStructureMobs::VILLAGER_PROFESSION_COUNT){
					$inRange = false;
				}
			}
		}

		return [
			["villager_profession_matches_formula", $actual === $expected, "expected $expected, got $actual"],
			["villager_profession_stable", $repeat === $actual && $floored === $actual, "同位置（含小数部分差异）结果稳定"],
			["villager_profession_range", $inRange && DeferredStructureMobs::VILLAGER_PROFESSION_COUNT === 5, "所有样本落在 0..4"],
		];
	}

	private static function testRaiderZombieNBT() : array{
		$nbt = DeferredStructureMobs::createRaiderZombieNBT(1.5, 70.0, 2.5);

		return [
			["raider_zombie_name", $nbt->CustomName->getValue() === "掠夺者", "CustomName = " . $nbt->CustomName->getValue()],
			["raider_zombie_name_visible", (int) $nbt->CustomNameVisible->getValue() === 1, "CustomNameVisible = " . $nbt->CustomNameVisible->getValue()],
			["raider_zombie_health", (int) $nbt->Health->getValue() === 20, "Health = " . $nbt->Health->getValue()],
		];
	}

	private static function testRaiderZombieArmorLayout() : array{
		$layout = DeferredStructureMobs::getRaiderZombieArmorLayout();

		return [
			["raider_armor_layout", $layout === ["helmet", "chestplate", "leggings", "boots"], implode(",", $layout)],
			["raider_armor_protection", DeferredStructureMobs::RAIDER_ZOMBIE_ARMOR_PROTECTION_LEVEL === 3, "保护 III"],
		];
	}

	private static function testJungleTempleDispenserMarker() : array{
		// Level::setBlockExtraDataAt() 用 ($data << 8) | $id 打包 16 位 marker，
		// 这里验证两个神庙 marker 的自洽性，避免接线时 id/data 写反。
		$dispenserPacked = (JungleTempleLoot::DISPENSER_MARKER_DATA << 8) | JungleTempleLoot::DISPENSER_MARKER_ID;
		$chestPacked = (JungleTempleLoot::CHEST_MARKER_DATA << 8) | JungleTempleLoot::CHEST_MARKER_ID;

		return [
			["jungle_dispenser_marker_pack", $dispenserPacked === JungleTempleLoot::DISPENSER_MARKER, "packed = 0x" . dechex($dispenserPacked)],
			["jungle_chest_marker_pack", $chestPacked === JungleTempleLoot::CHEST_MARKER, "packed = 0x" . dechex($chestPacked)],
			["jungle_markers_distinct", JungleTempleLoot::DISPENSER_MARKER !== JungleTempleLoot::CHEST_MARKER, "发射器 marker 与箱子 marker 不同"],
		];
	}

	private static function testDesertTempleFootprint() : array{
		$originZero = Temple::getTempleFootprintChunksFromOrigin(0, 0);
		$originFar = Temple::getTempleFootprintChunksFromOrigin(20, -20);

		$bounds = [];
		foreach($originFar as $chunkPos){
			$bounds[] = [$chunkPos["chunkX"], $chunkPos["chunkZ"]];
		}

		return [
			["temple_footprint_origin_zero", count($originZero) === 4, "原点 ±10 覆盖 2×2 区块"],
			["temple_footprint_origin_far", count($originFar) === 4 && min(array_column($bounds, 0)) === 0 && max(array_column($bounds, 0)) === 1 && min(array_column($bounds, 1)) === -2 && max(array_column($bounds, 1)) === -1, "x 0..1 / z -2..-1"],
			["temple_floor_div", Temple::floorDiv(-1, 32) === -1 && Temple::floorDiv(31, 32) === 0 && Temple::floorDiv(32, 32) === 1 && Temple::floorDiv(-32, 32) === -1, "负数向下取整"],
			["temple_region_constants", Temple::REGION_SIZE === 32 && Temple::CANDIDATE_SPAN <= Temple::REGION_SIZE, "区域 32 区块，候选跨度 24"],
		];
	}

}
