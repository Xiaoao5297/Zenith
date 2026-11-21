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
 * gorigional 高度场常量回归测试（不启动服务端）。
 *
 * 背景：这一组常量直接决定地形观感。0.14 MCPE 原版（`libminecraftpe` 反编译
 * `RandomLevelSource::getHeights`）用的是 855.515 / 256 / 垂直项 ×12，那是给
 * 128 高客户端做的压缩；而 `docs/remapped_server_1.7.10` 的 Java 版用的是
 * 684.412 / 512 / 垂直项 ×12×128/256。本项目按 1.7.10 对齐，所以这里把值钉死，
 * 防止有人再把 MCPE 的数值改回去。
 *
 * 只需要常量，不加载 Level/Chunk，因此可以直接 require 类文件。
 */

$src = dirname(__DIR__) . "/src/pocketmine";
require_once $src . "/level/generator/Generator.php";
require_once $src . "/level/generator/gorigional/Gorigional.php";

use pocketmine\level\generator\gorigional\Gorigional;

class GorigionalConstantsTest{
	public static function run() : array{
		$out = [];

		$out[] = ["name", Gorigional::NAME === "gorigional", Gorigional::NAME];

		// Java 1.7.10 ChunkProviderGenerate::func_147423_a
		$out[] = ["coordinate_scale", abs(Gorigional::COORDINATE_SCALE - 684.412) < 1e-9, Gorigional::COORDINATE_SCALE];
		$out[] = ["height_scale", abs(Gorigional::HEIGHT_SCALE - 684.412) < 1e-9, Gorigional::HEIGHT_SCALE];
		$out[] = ["lower_limit_scale", abs(Gorigional::LOWER_LIMIT_SCALE - 512.0) < 1e-9, Gorigional::LOWER_LIMIT_SCALE];
		$out[] = ["upper_limit_scale", abs(Gorigional::UPPER_LIMIT_SCALE - 512.0) < 1e-9, Gorigional::UPPER_LIMIT_SCALE];
		$out[] = ["stretch_y", abs(Gorigional::STRETCH_Y - 12.0) < 1e-9, Gorigional::STRETCH_Y];

		// 不再保留 MCPE 的 855.515：常量不存在即视为正确
		$out[] = ["no_mcpe_min_limit_scale", !defined(Gorigional::class . "::MIN_LIMIT_HEIGHT_SCALE"), "MIN_LIMIT_HEIGHT_SCALE 已移除"];

		// Java 的垂直系数是 12 * 128 / 256 = 6.0；换成 128 高后即"截断取下半段"
		$coefficient = Gorigional::STRETCH_Y * Gorigional::MAX_HEIGHT / 256.0;
		$out[] = ["vertical_coefficient", abs($coefficient - 6.0) < 1e-9, $coefficient];

		$out[] = ["max_height", Gorigional::MAX_HEIGHT === 128, Gorigional::MAX_HEIGHT];

		// 17 个采样点（每 8 格一个）才能覆盖 0..127
		$out[] = ["vertical_points", intdiv(Gorigional::MAX_HEIGHT, 8) + 1 === 17, intdiv(Gorigional::MAX_HEIGHT, 8) + 1];

		return $out;
	}
}
