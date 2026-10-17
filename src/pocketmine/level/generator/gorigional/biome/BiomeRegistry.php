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

use pocketmine\block\Block;

/**
 * 移植自 SCAXE-GO-CE pkg/level/generator/biome/registry.go。
 */
final class BiomeRegistry{
	/** @var Biome[] */
	private static $biomes = [];

	public static function refreshMesa($seed){
		self::init();
		foreach([37, 38, 39] as $id){
			if(isset(self::$biomes[$id]) && self::$biomes[$id] instanceof MesaBiome){
				self::$biomes[$id]->refreshBands($seed);
			}
		}
	}

	private static function init(){
		if(!empty(self::$biomes)){
			return;
		}

		self::add(new BaseBiome(0, "Ocean", -1.0, 0.1, 0.5, 0.5));
		self::add(new BaseBiome(1, "Plains", 0.125, 0.05, 0.8, 0.4));
		self::add(new BaseBiome(2, "Desert", 0.125, 0.05, 2.0, 0.0));
		self::add(new ExtremeHillsBiome(3, "Extreme Hills", 1.0, 0.5, 0.2, 0.3, 0));
		self::add(new BaseBiome(4, "Forest", 0.1, 0.2, 0.7, 0.8));
		self::add(new BaseBiome(5, "Taiga", 0.2, 0.2, 0.25, 0.8));
		self::add(new SwampBiome(6, "Swamp", -0.2, 0.1, 0.8, 0.9));
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
		self::add(new ExtremeHillsBiome(20, "Extreme Hills Edge", 0.8, 0.3, 0.2, 0.3, 1));
		self::add(new BaseBiome(21, "Jungle", 0.1, 0.2, 0.95, 0.9));
		self::add(new BaseBiome(22, "Jungle Hills", 0.45, 0.3, 0.95, 0.9));
		self::add(new BaseBiome(23, "Jungle Edge", 0.1, 0.2, 0.95, 0.8));
		self::add(new BaseBiome(24, "Deep Ocean", -1.8, 0.1, 0.5, 0.5));
		self::add(new BaseBiome(25, "Stone Beach", 0.1, 0.8, 0.2, 0.3));
		self::add(new BaseBiome(26, "Cold Beach", 0.0, 0.025, 0.05, 0.3));
		self::add(new BaseBiome(27, "Birch Forest", 0.1, 0.2, 0.7, 0.8));
		self::add(new BaseBiome(28, "Birch Forest Hills", 0.45, 0.3, 0.6, 0.6));
		self::add(new BaseBiome(29, "Roofed Forest", 0.1, 0.2, 0.7, 0.8));
		self::add(new TaigaBiome(30, "Cold Taiga", 0.2, 0.2, -0.5, 0.4, 1));
		self::add(new TaigaBiome(31, "Cold Taiga Hills", 0.45, 0.3, -0.5, 0.4, 1));
		self::add(new TaigaBiome(32, "Mega Taiga", 0.2, 0.2, 0.3, 0.8, 2));
		self::add(new TaigaBiome(33, "Mega Taiga Hills", 0.45, 0.3, 0.3, 0.8, 2));
		self::add(new ExtremeHillsBiome(34, "Extreme Hills+", 1.0, 0.5, 0.2, 0.3, 1));
		self::add(new BaseBiome(35, "Savanna", 0.125, 0.05, 1.2, 0.0));
		self::add(new BaseBiome(36, "Savanna Plateau", 1.5, 0.025, 1.0, 0.0));
		self::add(new MesaBiome(37, "Mesa", 0.1, 0.2));
		self::add(new MesaBiome(38, "Mesa Plateau F", 1.5, 0.025));
		self::add(new MesaBiome(39, "Mesa Plateau", 1.5, 0.025));

		self::add(new BaseBiome(129, "Sunflower Plains", 0.125, 0.05, 0.8, 0.4));
		self::add(new BaseBiome(140, "Ice Plains Spikes", 0.425, 0.45, 0.0, 0.5));

		// MCPE Biome 构造器里的表层方块（top block / top meta / filler block）。
		self::surface(2, Block::SAND, 0, Block::SAND);          // Desert
		self::surface(17, Block::SAND, 0, Block::SAND);         // Desert Hills
		self::surface(16, Block::SAND, 0, Block::SAND);         // Beach
		self::surface(25, Block::STONE, 0, Block::STONE);       // Stone Beach
		self::surface(14, Block::MYCELIUM, 0, Block::DIRT);     // Mushroom Island
		self::surface(15, Block::MYCELIUM, 0, Block::DIRT);     // Mushroom Island Shore
		self::surface(37, Block::SAND, 1, Block::HARDENED_CLAY); // Mesa
		self::surface(38, Block::SAND, 1, Block::HARDENED_CLAY); // Mesa Plateau F
		self::surface(39, Block::SAND, 1, Block::HARDENED_CLAY); // Mesa Plateau
		self::surface(140, Block::SNOW_BLOCK, 0, Block::DIRT);  // Ice Plains Spikes

		self::configureDecorators();
		self::registerMutations();
	}

	/**
	 * 移植自 Java 1.7.10 BiomeGenBase 静态块注册的变种群系（id = 父 + 128）。
	 * 高度按各 func_150566_k() 的取值给定；null 表示默认父群系 +0.1/+0.2。
	 */
	private static function registerMutations(){
		self::mutate(2, 0.225, 0.25);
		self::mutate(18);
		self::mutate(19);
		self::mutate(3, 1.0, 0.5);
		self::mutate(4, 0.1, 0.4);
		self::mutate(5);
		self::mutate(6);
		self::mutate(21);
		self::mutate(23);
		self::mutate(27);
		self::mutate(28);
		self::mutate(29, null, null, true);
		self::mutate(30);
		self::mutate(31);
		self::mutate(32, 0.2, 0.2);
		self::mutate(34, 1.0, 0.5);
		self::mutate(35, 0.3625, 1.225);
		self::mutate(36, 1.05, 1.2125);
		self::mutate(37, 0.1, 0.2);
		self::mutate(38, 0.45, 0.3);
		self::mutate(39, 0.45, 0.3);

		// Java: field_76773_a[161] = field_76773_a[160]（Mega Taiga Hills M 复用 Mega Taiga M 对象）
		if(isset(self::$biomes[160])){
			self::$biomes[161] = self::$biomes[160];
		}
	}

	private static function mutate($parentID, $minElevation = null, $maxElevation = null, $decorateParent = false){
		if(!isset(self::$biomes[$parentID])){
			return;
		}
		$b = new MutatedBiome(self::$biomes[$parentID], $minElevation, $maxElevation, $decorateParent);
		self::$biomes[$b->getID()] = $b;
	}

	private static function cfg($id, array $v){
		if(!isset(self::$biomes[$id])){
			return;
		}
		$d = self::$biomes[$id]->getDecorator();
		if(isset($v['trees'])){ $d->treesPerChunk = $v['trees']; }
		if(isset($v['extraTree'])){ $d->extraTreeChance = $v['extraTree']; }
		if(isset($v['flowers'])){ $d->flowersPerChunk = $v['flowers']; }
		if(isset($v['grass'])){ $d->grassPerChunk = $v['grass']; }
		if(isset($v['deadBush'])){ $d->deadBushPerChunk = $v['deadBush']; }
		if(isset($v['mushrooms'])){ $d->mushroomsPerChunk = $v['mushrooms']; }
		if(isset($v['reeds'])){ $d->reedsPerChunk = $v['reeds']; }
		if(isset($v['cacti'])){ $d->cactiPerChunk = $v['cacti']; }
		if(isset($v['waterlily'])){ $d->waterlilyPerChunk = $v['waterlily']; }
		if(isset($v['gravel'])){ $d->gravelPatchesPerChunk = $v['gravel']; }
		if(isset($v['sand'])){ $d->sandPatchesPerChunk = $v['sand']; }
		if(isset($v['clay'])){ $d->clayPerChunk = $v['clay']; }
		if(isset($v['bigMush'])){ $d->bigMushroomsPerChunk = $v['bigMush']; }
	}

	private static function configureDecorators(){
		self::cfg(1, ['trees' => 0, 'extraTree' => 0.05, 'flowers' => 4, 'grass' => 10]);
		self::cfg(2, ['trees' => -999, 'deadBush' => 2, 'reeds' => 50, 'cacti' => 10]);
		self::cfg(3, ['trees' => 3]);
		self::cfg(4, ['trees' => 10, 'grass' => 2, 'flowers' => 4]);
		self::cfg(5, ['trees' => 10, 'grass' => 1, 'mushrooms' => 1]);
		self::cfg(6, ['trees' => 2, 'flowers' => 1, 'deadBush' => 1, 'mushrooms' => 8, 'reeds' => 10, 'clay' => 1, 'waterlily' => 4, 'sand' => 0, 'gravel' => 0, 'grass' => 5]);
		self::cfg(12, ['trees' => 0, 'extraTree' => 0.05, 'flowers' => 0, 'grass' => 0]);
		self::cfg(13, ['trees' => 0, 'extraTree' => 0.05, 'flowers' => 0, 'grass' => 0]);
		self::cfg(14, ['trees' => 0, 'extraTree' => 0, 'flowers' => 0, 'grass' => 0, 'mushrooms' => 1, 'bigMush' => 1]);
		self::cfg(15, ['trees' => 0, 'extraTree' => 0, 'flowers' => 0, 'grass' => 0, 'mushrooms' => 1, 'bigMush' => 1]);
		self::cfg(16, ['trees' => -999, 'deadBush' => 0, 'reeds' => 0, 'cacti' => 0]);
		self::cfg(17, ['trees' => -999, 'deadBush' => 2, 'reeds' => 50, 'cacti' => 10]);
		self::cfg(18, ['trees' => 10, 'grass' => 2, 'flowers' => 4]);
		self::cfg(19, ['trees' => 10, 'grass' => 1, 'mushrooms' => 1]);
		self::cfg(20, ['trees' => 3]);
		self::cfg(21, ['trees' => 50, 'grass' => 25, 'flowers' => 4]);
		self::cfg(22, ['trees' => 50, 'grass' => 25, 'flowers' => 4]);
		self::cfg(23, ['trees' => 2, 'grass' => 3, 'flowers' => 2]);
		self::cfg(25, ['trees' => -999]);
		self::cfg(26, ['trees' => -999]);
		self::cfg(27, ['trees' => 10, 'grass' => 2, 'flowers' => 4]);
		self::cfg(28, ['trees' => 10, 'grass' => 2, 'flowers' => 4]);
		self::cfg(29, ['trees' => -999, 'grass' => 2, 'flowers' => 0]);
		self::cfg(30, ['trees' => 10, 'grass' => 1, 'mushrooms' => 1]);
		self::cfg(31, ['trees' => 10, 'grass' => 1, 'mushrooms' => 1]);
		self::cfg(32, ['trees' => 10, 'grass' => 7, 'deadBush' => 1, 'mushrooms' => 3]);
		self::cfg(33, ['trees' => 10, 'grass' => 7, 'deadBush' => 1, 'mushrooms' => 3]);
		self::cfg(34, ['trees' => 3]);
		self::cfg(35, ['trees' => 1, 'flowers' => 4, 'grass' => 20]);
		self::cfg(36, ['trees' => 1, 'flowers' => 4, 'grass' => 20]);
		self::cfg(37, ['trees' => -999, 'deadBush' => 20, 'cacti' => 5, 'flowers' => 0]);
		self::cfg(38, ['trees' => 5, 'deadBush' => 20, 'cacti' => 5]);
		self::cfg(39, ['trees' => -999, 'deadBush' => 20, 'cacti' => 5]);
		self::cfg(129, ['trees' => 0, 'extraTree' => 0.05, 'flowers' => 4, 'grass' => 10]);
		self::cfg(140, ['trees' => 0, 'extraTree' => 0.05, 'flowers' => 0, 'grass' => 0]);
	}

	private static function surface($id, $topBlock, $topMeta, $fillerBlock){
		if(!isset(self::$biomes[$id]) || !(self::$biomes[$id] instanceof BaseBiome)){
			return;
		}
		self::$biomes[$id]->topBlock = $topBlock;
		self::$biomes[$id]->topMeta = $topMeta;
		self::$biomes[$id]->fillerBlock = $fillerBlock;
	}

	private static function add(Biome $b){
		self::$biomes[$b->getID()] = $b;
	}

	/**
	 * @return Biome[] id => Biome
	 */
	public static function getAllBiomes(){
		self::init();
		return self::$biomes;
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
