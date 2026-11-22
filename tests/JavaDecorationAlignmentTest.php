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
 * gorigional 群系装饰数量与 Java 1.7.10 对齐的回归测试（不启动服务端）。
 *
 * 参考 `docs/remapped_server_1.7.10/net/minecraft/world/biome/`：
 *  - `BiomeGenPlains/Forest/Swamp/Jungle/Savanna/Taiga/Mesa` 里对
 *    `field_76803_B`（草）/`field_76802_A`（花）/`field_76799_E`（芦苇）等的赋值；
 *  - `BiomeDecorator.func_150513_a` 里固定再尝试 10 次芦苇的循环。
 *
 * 只读 `BiomeRegistry` 的对象与 `Decorator.php` 源码，不生成区块。
 */

$srcDir = dirname(__DIR__) . "/src/pocketmine";
spl_autoload_register(function($class) use ($srcDir){
	if(strpos($class, "pocketmine\\") !== 0){
		return;
	}
	$path = $srcDir . "/" . str_replace("\\", "/", substr($class, 10)) . ".php";
	if(is_file($path)){
		require_once $path;
	}
});

use pocketmine\level\generator\gorigional\biome\BiomeRegistry;

class JavaDecorationAlignmentTest{
	public static function run() : array{
		$out = [];

		$out[] = self::expect("plains_grass", 1, "grassPerChunk", 10);
		$out[] = self::expect("forest_grass", 4, "grassPerChunk", 2);
		$out[] = self::expect("forest_flowers", 4, "flowersPerChunk", 2);
		$out[] = self::expect("forest_hills_grass", 18, "grassPerChunk", 2);
		$out[] = self::expect("forest_hills_flowers", 18, "flowersPerChunk", 2);
		$out[] = self::expect("birch_forest_grass", 27, "grassPerChunk", 2);
		$out[] = self::expect("birch_forest_flowers", 27, "flowersPerChunk", 2);
		$out[] = self::expect("birch_forest_hills_grass", 28, "grassPerChunk", 2);
		$out[] = self::expect("birch_forest_hills_flowers", 28, "flowersPerChunk", 2);
		$out[] = self::expect("swamp_grass", 6, "grassPerChunk", 5);
		$out[] = self::expect("jungle_grass", 21, "grassPerChunk", 25);
		$out[] = self::expect("jungle_hills_grass", 22, "grassPerChunk", 25);
		$out[] = self::expect("jungle_edge_trees", 23, "treesPerChunk", 2);
		$out[] = self::expect("jungle_edge_grass", 23, "grassPerChunk", 25);
		$out[] = self::expect("jungle_edge_flowers", 23, "flowersPerChunk", 4);
		$out[] = self::expect("savanna_grass", 35, "grassPerChunk", 20);
		$out[] = self::expect("savanna_plateau_grass", 36, "grassPerChunk", 20);

		$out[] = self::expect("mesa_reeds", 37, "reedsPerChunk", 3);
		$out[] = self::expect("mesa_dead_bush", 37, "deadBushPerChunk", 20);
		$out[] = self::expect("mesa_cacti", 37, "cactiPerChunk", 5);
		$out[] = self::expect("mesa_flowers", 37, "flowersPerChunk", 0);
		$out[] = self::expect("mesa_plateau_reeds", 39, "reedsPerChunk", 3);
		$out[] = self::expect("mesa_plateau_dead_bush", 39, "deadBushPerChunk", 20);
		$out[] = self::expect("mesa_plateau_cacti", 39, "cactiPerChunk", 5);
		$out[] = self::expect("mesa_plateau_flowers", 39, "flowersPerChunk", 0);
		$out[] = self::expect("mesa_plateau_f_reeds", 38, "reedsPerChunk", 3);
		$out[] = self::expect("mesa_plateau_f_trees", 38, "treesPerChunk", 5);
		$out[] = self::expect("mesa_plateau_f_dead_bush", 38, "deadBushPerChunk", 20);
		$out[] = self::expect("mesa_plateau_f_cacti", 38, "cactiPerChunk", 5);
		$out[] = self::expect("mesa_plateau_f_flowers", 38, "flowersPerChunk", 0);

		$out[] = self::expect("extreme_hills_grass", 3, "grassPerChunk", 1);
		$out[] = self::expect("extreme_hills_edge_grass", 20, "grassPerChunk", 1);
		$out[] = self::expect("extreme_hills_plus_grass", 34, "grassPerChunk", 1);

		$out[] = self::expect("taiga_grass", 5, "grassPerChunk", 1);
		$out[] = self::expect("taiga_mushrooms", 5, "mushroomsPerChunk", 1);
		$out[] = self::expect("taiga_hills_grass", 19, "grassPerChunk", 1);
		$out[] = self::expect("taiga_hills_mushrooms", 19, "mushroomsPerChunk", 1);
		$out[] = self::expect("cold_taiga_grass", 30, "grassPerChunk", 1);
		$out[] = self::expect("cold_taiga_mushrooms", 30, "mushroomsPerChunk", 1);
		$out[] = self::expect("cold_taiga_hills_grass", 31, "grassPerChunk", 1);
		$out[] = self::expect("cold_taiga_hills_mushrooms", 31, "mushroomsPerChunk", 1);

		$out[] = self::expect("mega_taiga_grass", 32, "grassPerChunk", 7);
		$out[] = self::expect("mega_taiga_dead_bush", 32, "deadBushPerChunk", 1);
		$out[] = self::expect("mega_taiga_mushrooms", 32, "mushroomsPerChunk", 3);
		$out[] = self::expect("mega_taiga_hills_grass", 33, "grassPerChunk", 7);
		$out[] = self::expect("mega_taiga_hills_dead_bush", 33, "deadBushPerChunk", 1);
		$out[] = self::expect("mega_taiga_hills_mushrooms", 33, "mushroomsPerChunk", 3);

		$out[] = self::expect("roofed_forest_grass", 29, "grassPerChunk", 2);
		$out[] = self::expect("roofed_forest_flowers", 29, "flowersPerChunk", 2);

		// 未改动的对照群系，防止顺手误伤。
		$out[] = self::expect("desert_reeds_unchanged", 2, "reedsPerChunk", 50);
		$out[] = self::expect("desert_cacti_unchanged", 2, "cactiPerChunk", 10);
		$out[] = self::expect("mushroom_island_big_mush_unchanged", 14, "bigMushroomsPerChunk", 1);
		$out[] = self::expect("sunflower_plains_grass_unchanged", 129, "grassPerChunk", 16);

		$out[] = self::testUnconditionalReedLoop();

		return $out;
	}

	private static function expect($name, $id, $property, $expected) : array{
		$biome = BiomeRegistry::getBiome($id);
		$decorator = $biome->getDecorator();
		$actual = $decorator->{$property};
		return [
			$name,
			$actual === $expected,
			"id=" . $id . " " . $property . "=" . var_export($actual, true) . " expected " . var_export($expected, true)
		];
	}

	private static function testUnconditionalReedLoop() : array{
		$src = file_get_contents(dirname(__DIR__) . "/src/pocketmine/level/generator/gorigional/object/Decorator.php");
		$hasMarker = strpos($src, "固定再尝试 10 次芦苇") !== false;
		$reedCalls = substr_count($src, "reedGen->generate");
		$hasLoop = preg_match('/for\(\$i = 0; \$i < 10; \$i\+\+\)\{[\s\S]{0,300}reedGen->generate/', $src) === 1;

		// Java 的执行顺序：配置芦苇 -> 固定 10 次芦苇 -> 南瓜。位置关系必须成立。
		$configuredPos = strpos($src, 'for($i = 0; $i < $this->reedsPerChunk; $i++){');
		$loopPos = strpos($src, "固定再尝试 10 次芦苇");
		$pumpkinPos = strpos($src, 'if($r->nextBoundedInt(32) === 0){');
		$ordered = $configuredPos !== false && $loopPos !== false && $pumpkinPos !== false
			&& $configuredPos < $loopPos && $loopPos < $pumpkinPos;

		return [
			"unconditional_reed_loop",
			$hasMarker && $reedCalls >= 2 && $hasLoop && $ordered,
			"marker=" . ($hasMarker ? "yes" : "no") . " reedCalls=" . $reedCalls . " loop=" . ($hasLoop ? "yes" : "no") . " ordered=" . ($ordered ? "yes" : "no")
		];
	}
}
