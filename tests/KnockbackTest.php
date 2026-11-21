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
 * 击退强度回归测试（不启动服务端）。
 *
 * 默认值 0.6 会让玩家打生物、生物互殴时的击退明显偏大；
 * 上游 PocketMine 与这里统一用 0.4。测试只做反射读取默认参数，
 * 不实例化 Entity，也不需要 Level。
 */

$src = dirname(__DIR__) . "/src/pocketmine";

spl_autoload_register(function($class) use ($src){
	static $prefix = "pocketmine\\";
	if(strncmp($class, $prefix, strlen($prefix)) !== 0){
		return;
	}
	$file = $src . "/" . str_replace("\\", "/", substr($class, strlen($prefix))) . ".php";
	if(is_file($file)){
		require_once $file;
	}
});

class KnockbackTest{
	public static function run() : array{
		$out = [];

		$ctor = new ReflectionMethod(\pocketmine\event\entity\EntityDamageByEntityEvent::class, "__construct");
		$params = $ctor->getParameters();
		$default = $params[4]->getDefaultValue();
		$out[] = ["damage_event_default", abs($default - 0.4) < 1e-9, $default];

		$knock = new ReflectionMethod(\pocketmine\entity\Living::class, "knockBack");
		$base = $knock->getParameters()[4]->getDefaultValue();
		$out[] = ["living_knockback_base", abs($base - 0.4) < 1e-9, $base];

		// 显式传 0.1 的下落实体不应被默认值改动
		$source = file_get_contents(dirname(__DIR__) . "/src/pocketmine/entity/FallingSand.php");
		$out[] = ["falling_sand_explicit", strpos($source, "0.1") !== false, "FallingSand 传 0.1"];

		return $out;
	}
}
