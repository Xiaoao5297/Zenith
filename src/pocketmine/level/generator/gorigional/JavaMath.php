<?php

namespace pocketmine\level\generator\gorigional;

/**
 * Minecraft MathHelper 的 sin/cos 查表实现。
 * 移植自 SCAXE-GO-CE pkg/math/java/math_helper.go。
 */
final class JavaMath{
	/** @var float[]|null */
	private static $sinTable = null;

	private static function init(){
		if(self::$sinTable !== null){
			return;
		}
		$table = [];
		for($i = 0; $i < 65536; $i++){
			$table[$i] = sin($i * M_PI * 2.0 / 65536.0);
		}
		self::$sinTable = $table;
	}

	public static function sin($value){
		self::init();
		return self::$sinTable[((int) ($value * 10430.378)) & 65535];
	}

	public static function cos($value){
		self::init();
		return self::$sinTable[((int) ($value * 10430.378 + 16384.0)) & 65535];
	}
}
