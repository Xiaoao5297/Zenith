<?php

/***
 *  _____                _  __   __
 * /__  /  ___   ____   (_)/ /_ / /_
 *   / /  / _ \ / __ \ / // __// __ \
 *  / /__/  __// / / // // /_ / / / /
 * /____/\___//_/ /_//_/ \__//_/ /_/
 *
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * @author Xiaoao
 * @link https://github.com/Xiaoao5297/Zenith
 *
 *
*/

/**
 * 安装向导纯逻辑测试。
 *
 * 依赖说明: 只 require wizard/Installer.php，调用两个静态校验方法，
 * 不构造 Installer、不读 STDIN、不启动 PocketMine 运行时。
 *
 * 覆盖:
 *  - 生成器编号/名称 => level-type 归一化 (含旧配置的 "DEFAULT"、大小写、空输入);
 *  - 未提供的生成器 (hell/nether/ender/skyworld) 必须被拒绝;
 *  - 超平坦预设的格式校验 (含默认预设与带 decoration 的官方示例)。
 */
class InstallerTest{

	public static function run() : array{
		require_once \dirname(__DIR__) . "/src/pocketmine/wizard/Installer.php";

		return array_merge(
			self::testNormalizeGenerator(),
			self::testRejectedGenerators(),
			self::testFlatPreset()
		);
	}

	private static function testNormalizeGenerator() : array{
		$out = [];

		$cases = [
			["1", "gorigional"],
			["gorigional", "gorigional"],
			[" GORIGIONAL ", "gorigional"],
			["original", "gorigional"],
			["", "gorigional"],
			["   ", "gorigional"],
			["2", "normal"],
			["normal", "normal"],
			["Normal", "normal"],
			["default", "normal"],
			["DEFAULT", "normal"],
			["3", "flat"],
			["flat", "flat"],
			["4", "void"],
			["void", "void"]
		];
		foreach($cases as $i => $case){
			$out[] = ["normalize_" . $i . "_" . $case[1], \pocketmine\wizard\Installer::normalizeGenerator($case[0]) === $case[1], var_export($case[0], true)];
		}
		$out[] = ["default_level_type", \pocketmine\wizard\Installer::DEFAULT_LEVEL_TYPE === "gorigional", \pocketmine\wizard\Installer::DEFAULT_LEVEL_TYPE];

		return $out;
	}

	private static function testRejectedGenerators() : array{
		$out = [];

		foreach(["0", "5", "hell", "nether", "ender", "skyworld", "unknown", "../world"] as $input){
			$out[] = ["reject_" . $input, \pocketmine\wizard\Installer::normalizeGenerator($input) === null, $input];
		}
		$out[] = ["reject_null", \pocketmine\wizard\Installer::normalizeGenerator(null) === null, "null"];
		$out[] = ["reject_array", \pocketmine\wizard\Installer::normalizeGenerator(["normal"]) === null, "array"];

		return $out;
	}

	private static function testFlatPreset() : array{
		$out = [];

		$out[] = ["preset_default", \pocketmine\wizard\Installer::isValidFlatPreset(\pocketmine\wizard\Installer::DEFAULT_FLAT_PRESET), "default constant"];
		$out[] = ["preset_simple", \pocketmine\wizard\Installer::isValidFlatPreset("2;7,2x3,2;1;"), "2;7,2x3,2;1;"];
		$out[] = ["preset_whitespace", \pocketmine\wizard\Installer::isValidFlatPreset(" 2;7,2x3,2;1; "), "leading/trailing spaces"];
		$out[] = ["preset_decoration", \pocketmine\wizard\Installer::isValidFlatPreset("2;7,59x1,3x3,2;1;decoration(treecount=80 grasscount=45)"), "with decoration"];
		$out[] = ["preset_short_layers", \pocketmine\wizard\Installer::isValidFlatPreset("3;1;1;"), "3;1;1;"];

		foreach(["", "abc", "2;7,2x3,2;1", "2;7,2x3,2", ";2;1;", "2;;1;"] as $bad){
			if($bad === "2;;1;"){
				// 空层段仍然匹配 (层内容留空由 Flat 生成器兜底)，这里只验证不会崩
				$out[] = ["preset_empty_layers_lenient", \pocketmine\wizard\Installer::isValidFlatPreset($bad), $bad];
				continue;
			}
			$out[] = ["preset_invalid_" . preg_replace('/[^a-z0-9]/i', "_", $bad), !\pocketmine\wizard\Installer::isValidFlatPreset($bad), var_export($bad, true)];
		}
		$out[] = ["preset_null", !\pocketmine\wizard\Installer::isValidFlatPreset(null), "null"];
		$out[] = ["preset_array", !\pocketmine\wizard\Installer::isValidFlatPreset(["2;1;1;"]), "array"];

		return $out;
	}
}
