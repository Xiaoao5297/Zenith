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
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * @author Xiaoao
 * @link https://github.com/Xiaoao5297/Zenith
 *
 *
*/


namespace pocketmine\level\generator\gorigional\biome;

use pocketmine\level\generator\gorigional\JavaRandom;
use pocketmine\level\generator\gorigional\noise\SimplexNoise;

/**
 * 移植自 SCAXE-GO-CE pkg/level/generator/biome/grass_color_noise.go。
 */
final class GrassColorNoise{
	/** @var SimplexNoise|null */
	private static $noise = null;

	public static function getValue($x, $z){
		if(self::$noise === null){
			self::$noise = new SimplexNoise(new JavaRandom(2345));
		}
		return self::$noise->getValue($x, $z);
	}
}
