<?php

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
