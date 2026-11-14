<?php

/**
 * NetherPortalHelper 纯函数离线测试 (A5).
 *
 * 只覆盖不触碰 Level/Server 实例的部分: 坐标缩放数学、内部尺寸校验、常量与
 * "helper 可在无 Level 类的环境下加载". 世界读写路径需要在服务器上人工验证,
 * 见 docs/manual-verification.md.
 */

use pocketmine\utils\NetherPortalHelper;

require_once dirname(__DIR__) . "/src/pocketmine/utils/NetherPortalHelper.php";

class NetherPortalTest{

	public static function run() : array{
		return array_merge(
			self::testConstants(),
			self::testOverworldToNether(),
			self::testNetherToOverworld(),
			self::testInvalidDimension(),
			self::testScaleHandling(),
			self::testInnerSizeBounds(),
			self::testFrameSizeDerivation(),
			self::testDependencyFree()
		);
	}

	private static function testConstants() : array{
		$out = [];
		$out[] = ["scale_is_8", NetherPortalHelper::DEFAULT_NETHER_SCALE === 8, "scale=" . NetherPortalHelper::DEFAULT_NETHER_SCALE];
		$out[] = ["max_portal_size_23", NetherPortalHelper::MAX_PORTAL_SIZE === 23, "max=" . NetherPortalHelper::MAX_PORTAL_SIZE];
		$out[] = ["dimension_normal_is_0", NetherPortalHelper::DIMENSION_NORMAL === 0, "normal=" . NetherPortalHelper::DIMENSION_NORMAL];
		$out[] = ["dimension_nether_is_1", NetherPortalHelper::DIMENSION_NETHER === 1, "nether=" . NetherPortalHelper::DIMENSION_NETHER];
		return $out;
	}

	private static function testOverworldToNether() : array{
		$out = [];
		$coords = NetherPortalHelper::convertCoords(800, 1600, NetherPortalHelper::DIMENSION_NORMAL, 8);
		$out[] = ["overworld_to_nether_x", $coords !== null and $coords[0] === 100, "x=" . ($coords === null ? "null" : $coords[0])];
		$out[] = ["overworld_to_nether_z", $coords !== null and $coords[1] === 200, "z=" . ($coords === null ? "null" : $coords[1])];

		$coords = NetherPortalHelper::convertCoords(0, 0, NetherPortalHelper::DIMENSION_NORMAL, 8);
		$out[] = ["overworld_origin", $coords === [0, 0], "got " . json_encode($coords)];

		//负数必须向下取整 (与原实现 floor 一致), 而不是向零截断
		$coords = NetherPortalHelper::convertCoords(-100, -1, NetherPortalHelper::DIMENSION_NORMAL, 8);
		$out[] = ["overworld_negative_floor_x", $coords !== null and $coords[0] === -13, "x=" . ($coords === null ? "null" : $coords[0])];
		$out[] = ["overworld_negative_floor_z", $coords !== null and $coords[1] === -1, "z=" . ($coords === null ? "null" : $coords[1])];

		return $out;
	}

	private static function testNetherToOverworld() : array{
		$out = [];
		$coords = NetherPortalHelper::convertCoords(12, -7, NetherPortalHelper::DIMENSION_NETHER, 8);
		$out[] = ["nether_to_overworld_x", $coords !== null and $coords[0] === 96, "x=" . ($coords === null ? "null" : $coords[0])];
		$out[] = ["nether_to_overworld_z", $coords !== null and $coords[1] === -56, "z=" . ($coords === null ? "null" : $coords[1])];

		//主世界 100 -> 下界 12 -> 主世界 96, 缩放本身有损, 这是预期行为
		$nether = NetherPortalHelper::convertCoords(100, 100, NetherPortalHelper::DIMENSION_NORMAL, 8);
		$back = NetherPortalHelper::convertCoords($nether[0], $nether[1], NetherPortalHelper::DIMENSION_NETHER, 8);
		$out[] = ["scaling_roundtrip_lossy", $back === [96, 96], "got " . json_encode($back)];

		return $out;
	}

	private static function testInvalidDimension() : array{
		$out = [];
		$out[] = ["dimension_end_rejected", NetherPortalHelper::convertCoords(10, 10, 2) === null, "dimension=2"];
		$out[] = ["dimension_negative_rejected", NetherPortalHelper::convertCoords(10, 10, -1) === null, "dimension=-1"];
		return $out;
	}

	private static function testScaleHandling() : array{
		$out = [];
		$out[] = ["scale_one_identity", NetherPortalHelper::convertCoords(123, -45, NetherPortalHelper::DIMENSION_NORMAL, 1) === [123, -45], "scale=1"];
		$out[] = ["scale_zero_clamped", NetherPortalHelper::convertCoords(80, -80, NetherPortalHelper::DIMENSION_NORMAL, 0) === [80, -80], "scale=0 -> 1"];
		$out[] = ["scale_negative_clamped", NetherPortalHelper::convertCoords(40, 40, NetherPortalHelper::DIMENSION_NORMAL, -4) === [40, 40], "scale=-4 -> 1"];
		$out[] = ["scale_16", NetherPortalHelper::convertCoords(160, 160, NetherPortalHelper::DIMENSION_NORMAL, 16) === [10, 10], "scale=16"];
		return $out;
	}

	private static function testInnerSizeBounds() : array{
		$out = [];
		$out[] = ["inner_min_valid", NetherPortalHelper::isValidInnerSize(2, 3), "2x3"];
		$out[] = ["inner_max_valid", NetherPortalHelper::isValidInnerSize(21, 21), "21x21"];
		$out[] = ["inner_narrow_invalid", !NetherPortalHelper::isValidInnerSize(1, 3), "1x3"];
		$out[] = ["inner_short_invalid", !NetherPortalHelper::isValidInnerSize(2, 2), "2x2"];
		$out[] = ["inner_too_wide_invalid", !NetherPortalHelper::isValidInnerSize(22, 3), "22x3"];
		$out[] = ["inner_too_tall_invalid", !NetherPortalHelper::isValidInnerSize(3, 22), "3x22"];
		$out[] = ["inner_zero_invalid", !NetherPortalHelper::isValidInnerSize(0, 0), "0x0"];
		return $out;
	}

	private static function testFrameSizeDerivation() : array{
		$out = [];
		//外框宽度 = 内部宽度 + 2, 合法外框为 4..23 (与原始 FlintSteel 检查一致)
		$outerMin = 2 + 2;
		$outerMax = 21 + 2;
		$out[] = ["frame_outer_min_4", NetherPortalHelper::isValidInnerSize($outerMin - 2, 3) and $outerMin === 4, "outer=" . $outerMin];
		$out[] = ["frame_outer_max_23", NetherPortalHelper::isValidInnerSize($outerMax - 2, 3) and $outerMax === NetherPortalHelper::MAX_PORTAL_SIZE, "outer=" . $outerMax];
		$out[] = ["frame_outer_24_invalid", !NetherPortalHelper::isValidInnerSize($outerMax - 2 + 1, 3), "outer=24"];
		return $out;
	}

	private static function testDependencyFree() : array{
		$out = [];
		//纯函数必须能在没有 Level/Server 运行时的离线测试中工作:
		//convertCoords 的签名里不能出现 Level 类型参数, 否则测试会间接依赖整个 level 包.
		$hasLevelParam = false;
		$method = new \ReflectionMethod(NetherPortalHelper::class, "convertCoords");
		foreach($method->getParameters() as $param){
			$type = $param->getType();
			if($type !== null and stripos((string) $type, "Level") !== false){
				$hasLevelParam = true;
			}
		}
		$out[] = ["convert_coords_has_no_level_param", !$hasLevelParam, "pure coordinate math"];
		$out[] = ["helper_class_loaded", class_exists("pocketmine\\utils\\NetherPortalHelper", false), "helper loaded"];
		return $out;
	}
}
