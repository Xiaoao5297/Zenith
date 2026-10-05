<?php

namespace pocketmine\level\generator\gorigional\biome;

/**
 * 移植自 SCAXE-GO-CE pkg/level/generator/biome/registry.go。
 */
final class BiomeRegistry{
	/** @var Biome[] */
	private static $biomes = [];

	private static function init(){
		if(!empty(self::$biomes)){
			return;
		}

		self::add(new BaseBiome(0, "Ocean", -1.0, 0.1, 0.5, 0.5));
		self::add(new BaseBiome(1, "Plains", 0.125, 0.05, 0.8, 0.4));
		self::add(new BaseBiome(2, "Desert", 0.125, 0.05, 2.0, 0.0));
		self::add(new BaseBiome(3, "Extreme Hills", 1.0, 0.5, 0.2, 0.3));
		self::add(new BaseBiome(4, "Forest", 0.1, 0.2, 0.7, 0.8));
		self::add(new BaseBiome(5, "Taiga", 0.2, 0.2, 0.25, 0.8));
		self::add(new BaseBiome(6, "Swamp", -0.2, 0.1, 0.8, 0.9));
		self::add(new BaseBiome(7, "River", -0.5, 0.0, 0.5, 0.5));
		self::add(new BaseBiome(8, "Hell", 0.1, 0.2, 2.0, 0.0));
		self::add(new BaseBiome(9, "The End", 0.1, 0.2, 0.5, 0.5));
		self::add(new BaseBiome(10, "Frozen Ocean", -1.0, 0.1, 0.0, 0.5));
		self::add(new BaseBiome(11, "Frozen River", -0.5, 0.0, 0.0, 0.5));
		self::add(new BaseBiome(12, "Ice Plains", 0.125, 0.05, 0.0, 0.5));
		self::add(new BaseBiome(13, "Ice Mountains", 0.45, 0.3, 0.0, 0.5));
		self::add(new BaseBiome(14, "Mushroom Island", 0.2, 0.3, 0.9, 1.0));
		self::add(new BaseBiome(15, "Mushroom Island Shore", 0.0, 0.025, 0.9, 1.0));
		self::add(new BaseBiome(16, "Beach", 0.0, 0.025, 0.8, 0.4));
		self::add(new BaseBiome(17, "Desert Hills", 0.45, 0.3, 2.0, 0.0));
		self::add(new BaseBiome(18, "Forest Hills", 0.45, 0.3, 0.7, 0.8));
		self::add(new BaseBiome(19, "Taiga Hills", 0.45, 0.3, 0.25, 0.8));
		self::add(new BaseBiome(20, "Extreme Hills Edge", 0.8, 0.3, 0.2, 0.3));
		self::add(new BaseBiome(21, "Jungle", 0.1, 0.2, 0.95, 0.9));
		self::add(new BaseBiome(22, "Jungle Hills", 0.45, 0.3, 0.95, 0.9));
		self::add(new BaseBiome(23, "Jungle Edge", 0.1, 0.2, 0.95, 0.8));
		self::add(new BaseBiome(24, "Deep Ocean", -1.8, 0.1, 0.5, 0.5));
		self::add(new BaseBiome(25, "Stone Beach", 0.1, 0.8, 0.2, 0.3));
		self::add(new BaseBiome(26, "Cold Beach", 0.0, 0.025, 0.05, 0.3));
		self::add(new BaseBiome(27, "Birch Forest", 0.1, 0.2, 0.7, 0.8));
		self::add(new BaseBiome(28, "Birch Forest Hills", 0.45, 0.3, 0.6, 0.6));
		self::add(new BaseBiome(29, "Roofed Forest", 0.1, 0.2, 0.7, 0.8));
		self::add(new BaseBiome(30, "Cold Taiga", 0.2, 0.2, -0.5, 0.4));
		self::add(new BaseBiome(31, "Cold Taiga Hills", 0.45, 0.3, -0.5, 0.4));
		self::add(new BaseBiome(32, "Mega Taiga", 0.2, 0.2, 0.3, 0.8));
		self::add(new BaseBiome(33, "Mega Taiga Hills", 0.45, 0.3, 0.3, 0.8));
		self::add(new BaseBiome(34, "Extreme Hills+", 1.0, 0.5, 0.2, 0.3));
		self::add(new BaseBiome(35, "Savanna", 0.125, 0.05, 1.2, 0.0));
		self::add(new BaseBiome(36, "Savanna Plateau", 1.5, 0.025, 1.0, 0.0));
		self::add(new MesaBiome(37, "Mesa", 0.1, 0.2));
		self::add(new MesaBiome(38, "Mesa Plateau F", 1.5, 0.025));
		self::add(new MesaBiome(39, "Mesa Plateau", 1.5, 0.025));

		self::add(new BaseBiome(129, "Sunflower Plains", 0.125, 0.05, 0.8, 0.4));
		self::add(new BaseBiome(130, "Desert M", 0.225, 0.25, 2.0, 0.0));
		self::add(new BaseBiome(140, "Ice Plains Spikes", 0.425, 0.45, 0.0, 0.5));
	}

	private static function add(Biome $b){
		self::$biomes[$b->getID()] = $b;
	}

	/**
	 * @return Biome
	 */
	public static function getBiome($id){
		self::init();
		$id = $id & 0xFF;
		if(isset(self::$biomes[$id])){
			return self::$biomes[$id];
		}
		// SCAXE-GO-CE 只注册了 0-39 与 129/130/140，其余变种（id+128）会落到海洋。
		// 这里退回到对应的基础生物群系，避免大片错误海洋地形。
		if($id >= 128 && isset(self::$biomes[$id - 128])){
			return self::$biomes[$id - 128];
		}
		return self::$biomes[0];
	}
}
