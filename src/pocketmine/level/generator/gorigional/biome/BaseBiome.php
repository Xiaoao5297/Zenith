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


namespace pocketmine\level\generator\gorigional\biome;

use pocketmine\block\Block;
use pocketmine\level\format\FullChunk;
use pocketmine\level\generator\gorigional\JavaRandom;
use pocketmine\level\generator\gorigional\object\BlockPos;
use pocketmine\level\generator\gorigional\object\Decorator;
use pocketmine\level\generator\gorigional\object\ObjectChunkManager;
use pocketmine\level\generator\gorigional\object\BigOakTree;
use pocketmine\level\generator\gorigional\object\BirchTree;
use pocketmine\level\generator\gorigional\object\BlockBlob;
use pocketmine\level\generator\gorigional\object\BlockPatch;
use pocketmine\level\generator\gorigional\object\Bush;
use pocketmine\level\generator\gorigional\object\DarkOakTree;
use pocketmine\level\generator\gorigional\object\DoublePlant;
use pocketmine\level\generator\gorigional\object\JungleBush;
use pocketmine\level\generator\gorigional\object\JungleSmallTree;
use pocketmine\level\generator\gorigional\object\MegaJungleTree;
use pocketmine\level\generator\gorigional\object\MegaPineTree;
use pocketmine\level\generator\gorigional\object\Melon;
use pocketmine\level\generator\gorigional\object\OakTree;
use pocketmine\level\generator\gorigional\object\Ore;
use pocketmine\level\generator\gorigional\object\PineTree;
use pocketmine\level\generator\gorigional\object\SavannaTree;
use pocketmine\level\generator\gorigional\object\SpruceTree;
use pocketmine\level\generator\gorigional\object\SwampTree;
use pocketmine\level\generator\gorigional\object\Vines;
use pocketmine\level\generator\gorigional\noise\PerlinSimplexGenerator;

/**
 * 移植自 SCAXE-GO-CE pkg/level/generator/biome/biome.go 的 BaseBiome。
 */
class BaseBiome implements Biome{
	/** @var PerlinSimplexGenerator|null Java BiomeGenBase.field_150605_ac */
	private static $temperatureNoise = null;
	/** @var int|null 上一列温度噪声的 x 坐标（冻结/积雪会对同一列连续取两次） */
	private static $temperatureCacheX = null;
	/** @var int|null 上一列温度噪声的 z 坐标 */
	private static $temperatureCacheZ = null;
	/** @var float 上一列已乘 4 的温度噪声值 */
	private static $temperatureCacheValue = 0.0;
	/** @var PerlinSimplexGenerator|null Java BiomeGenForest.field_150606_ad */
	private static $forestFlowerNoise = null;
	/** @var int */
	public $id;
	/** @var string */
	public $name;
	/** @var float */
	public $baseHeight;
	/** @var float */
	public $heightVariation;
	/** @var float */
	public $temperature;
	/** @var float */
	public $rainfall;
	/** @var int 表层方块 */
	public $topBlock = Block::GRASS;
	/** @var int 表层方块 meta */
	public $topMeta = 0;
	/** @var int 表层下方填充方块 */
	public $fillerBlock = Block::DIRT;
	/** @var Decorator */
	public $decorator;

	public function __construct($id, $name, $baseHeight, $heightVariation, $temperature, $rainfall){
		$this->id = $id;
		$this->name = $name;
		$this->baseHeight = $baseHeight;
		$this->heightVariation = $heightVariation;
		$this->temperature = $temperature;
		$this->rainfall = $rainfall;
		$this->decorator = new Decorator();
	}

	public function getID(){
		return $this->id;
	}

	public function getMinElevation(){
		return $this->baseHeight;
	}

	public function getMaxElevation(){
		return $this->heightVariation;
	}

	public function getColor(){
		return self::generateBiomeColor($this->temperature, $this->rainfall);
	}

	public function getTopBlock(){
		return $this->topBlock;
	}

	public function getTopMeta(){
		return $this->topMeta;
	}

	public function decorate(ObjectChunkManager $level, JavaRandom $r, BlockPos $pos){
		switch($this->id){
			case 1:
				$this->plainsDecorate($level, $r, $pos);
				break;
			case 4:
			case 18:
			case 27:
				$this->forestDecorate($level, $r, $pos);
				break;
			case 29:
				$this->roofedForestDecorate($level, $r, $pos);
				break;
			case 5:
			case 19:
			case 30:
			case 31:
			case 32:
			case 33:
				$this->taigaDecorate($level, $r, $pos);
				break;
			case 21:
			case 22:
			case 23:
				$this->jungleDecorate($level, $r, $pos);
				break;
			case 35:
			case 36:
				$this->savannaDecorate($level, $r, $pos);
				break;
			case 129:
				$this->sunflowerDecorate($level, $r, $pos);
				break;
			case 3:
			case 20:
			case 34:
				$this->extremeHillsDecorate($level, $r, $pos);
				break;
			case 12:
			case 13:
			case 30:
			case 31:
			case 140:
				$this->decorator->decorate($level, $r, $this, $pos);
				break;
			default:
				$this->decorator->decorate($level, $r, $this, $pos);
		}
	}

	private function forestDecorate(ObjectChunkManager $level, JavaRandom $r, BlockPos $pos, $extraDoublePlants = 0){
		$count = $r->nextBoundedInt(5) - 3 + $extraDoublePlants;
		for($i = 0; $i < $count; $i++){
			$type = $r->nextBoundedInt(3);
			switch($type){
				case 0: $plant = new DoublePlant(DoublePlant::LILAC); break;
				case 1: $plant = new DoublePlant(DoublePlant::ROSE_BUSH); break;
				default: $plant = new DoublePlant(DoublePlant::PEONY); break;
			}
			for($j = 0; $j < 5; $j++){
				$x = $pos->x + $r->nextBoundedInt(16) + 8;
				$z = $pos->z + $r->nextBoundedInt(16) + 8;
				$y = $r->nextBoundedInt(self::getHeightAtForBiome($level, $x, $z) + 32);
				if($plant->generate($level, $r, new BlockPos($x, $y, $z))){
					break;
				}
			}
		}
		$this->decorator->decorate($level, $r, $this, $pos);
	}

	private function roofedForestDecorate(ObjectChunkManager $level, JavaRandom $r, BlockPos $pos){
		for($i = 0; $i < 4; $i++){
			for($j = 0; $j < 4; $j++){
				$x = $pos->x + $i * 4 + 1 + 8 + $r->nextBoundedInt(3);
				$z = $pos->z + $j * 4 + 1 + 8 + $r->nextBoundedInt(3);
				$y = self::getHeightAtForBiome($level, $x, $z);

				if($r->nextBoundedInt(20) === 0){
					$mushroom = new BigMushroom(false);
					$mushroom->generate($level, $r, new BlockPos($x, $y, $z));
				}else{
					$tree = $this->getTreeFeature($r);
					if($tree !== null){
						$tree->generate($level, $r, new BlockPos($x, $y, $z));
					}
				}
			}
		}

		$count = $r->nextBoundedInt(5) - 3;
		for($i = 0; $i < $count; $i++){
			$type = $r->nextBoundedInt(3);
			switch($type){
				case 0: $plant = new DoublePlant(DoublePlant::LILAC); break;
				case 1: $plant = new DoublePlant(DoublePlant::ROSE_BUSH); break;
				default: $plant = new DoublePlant(DoublePlant::PEONY); break;
			}
			for($j = 0; $j < 5; $j++){
				$x = $pos->x + $r->nextBoundedInt(16) + 8;
				$z = $pos->z + $r->nextBoundedInt(16) + 8;
				$y = $r->nextBoundedInt(self::getHeightAtForBiome($level, $x, $z) + 32);
				if($plant->generate($level, $r, new BlockPos($x, $y, $z))){
					break;
				}
			}
		}

		$this->decorator->decorate($level, $r, $this, $pos);
	}

	private function taigaDecorate(ObjectChunkManager $level, JavaRandom $r, BlockPos $pos){
		if($this->id === 32 || $this->id === 33){
			$boulderCount = $r->nextBoundedInt(3);
			for($i = 0; $i < $boulderCount; $i++){
				$x = $pos->x + $r->nextBoundedInt(16) + 8;
				$z = $pos->z + $r->nextBoundedInt(16) + 8;
				$y = self::getHeightAtForBiome($level, $x, $z);
				$boulder = new BlockBlob(Block::MOSS_STONE, 0);
				$boulder->generate($level, $r, new BlockPos($x, $y, $z));
			}
		}

		for($i = 0; $i < 7; $i++){
			$x = $pos->x + $r->nextBoundedInt(16) + 8;
			$z = $pos->z + $r->nextBoundedInt(16) + 8;
			$y = $r->nextBoundedInt(self::getHeightAtForBiome($level, $x, $z) + 32);
			$fern = new DoublePlant(DoublePlant::FERN);
			$fern->generate($level, $r, new BlockPos($x, $y, $z));
		}

		$this->decorator->decorate($level, $r, $this, $pos);
	}

	private function jungleDecorate(ObjectChunkManager $level, JavaRandom $r, BlockPos $pos){
		$this->decorator->decorate($level, $r, $this, $pos);

		$x = $pos->x + $r->nextBoundedInt(16) + 8;
		$z = $pos->z + $r->nextBoundedInt(16) + 8;
		$y = $r->nextBoundedInt(self::getHeightAtForBiome($level, $x, $z) * 2);
		$melon = new Melon();
		$melon->generate($level, $r, new BlockPos($x, $y, $z));

		$vines = new Vines();
		for($i = 0; $i < 50; $i++){
			$x = $pos->x + $r->nextBoundedInt(16) + 8;
			$z = $pos->z + $r->nextBoundedInt(16) + 8;
			$vines->generate($level, $r, new BlockPos($x, 128, $z));
		}
	}

	private function savannaDecorate(ObjectChunkManager $level, JavaRandom $r, BlockPos $pos){
		for($i = 0; $i < 7; $i++){
			$x = $pos->x + $r->nextBoundedInt(16) + 8;
			$z = $pos->z + $r->nextBoundedInt(16) + 8;
			$y = $r->nextBoundedInt(self::getHeightAtForBiome($level, $x, $z) + 32);
			$grass = new DoublePlant(DoublePlant::GRASS);
			$grass->generate($level, $r, new BlockPos($x, $y, $z));
		}

		$this->decorator->decorate($level, $r, $this, $pos);
	}

	private function plainsDecorate(ObjectChunkManager $level, JavaRandom $r, BlockPos $pos){
		$noise = GrassColorNoise::getValue(($pos->x + 8) / 200.0, ($pos->z + 8) / 200.0);
		if($noise < -0.8){
			$this->decorator->flowersPerChunk = 15;
			$this->decorator->grassPerChunk = 5;
		}else{
			$this->decorator->flowersPerChunk = 4;
			$this->decorator->grassPerChunk = 10;
			$this->generateDoublePlants($level, $r, $pos, 2);
		}
		$this->decorator->decorate($level, $r, $this, $pos);
	}

	private function sunflowerDecorate(ObjectChunkManager $level, JavaRandom $r, BlockPos $pos){
		$noise = GrassColorNoise::getValue(($pos->x + 8) / 200.0, ($pos->z + 8) / 200.0);
		if($noise < -0.8){
			$this->decorator->flowersPerChunk = 15;
			$this->decorator->grassPerChunk = 5;
		}else{
			$this->decorator->flowersPerChunk = 4;
			$this->decorator->grassPerChunk = 10;
			$this->generateDoublePlants($level, $r, $pos, 2);
		}
		$this->generateDoublePlants($level, $r, $pos, 0);
		$this->decorator->decorate($level, $r, $this, $pos);
	}

	private function generateDoublePlants(ObjectChunkManager $level, JavaRandom $r, BlockPos $pos, $type){
		for($i = 0; $i < 64; $i++){
			$x = $pos->x + $r->nextBoundedInt(8) - $r->nextBoundedInt(8);
			$y = $pos->y + $r->nextBoundedInt(4) - $r->nextBoundedInt(4);
			$z = $pos->z + $r->nextBoundedInt(8) - $r->nextBoundedInt(8);
			if($y < 1 || $y >= 127){
				continue;
			}
			if($level->getBlockId($x, $y, $z) !== Block::AIR || $level->getBlockId($x, $y + 1, $z) !== Block::AIR){
				continue;
			}
			$below = $level->getBlockId($x, $y - 1, $z);
			if($below !== Block::GRASS && $below !== Block::DIRT){
				continue;
			}
			$level->setBlock($x, $y, $z, Block::DOUBLE_PLANT, $type);
			$level->setBlock($x, $y + 1, $z, Block::DOUBLE_PLANT, $type | 8);
		}
	}

	private function extremeHillsDecorate(ObjectChunkManager $level, JavaRandom $r, BlockPos $pos){
		$this->decorator->decorate($level, $r, $this, $pos);

		$emeraldCount = 3 + $r->nextBoundedInt(6);
		for($i = 0; $i < $emeraldCount; $i++){
			$x = $r->nextBoundedInt(16);
			$y = $r->nextBoundedInt(28) + 4;
			$z = $r->nextBoundedInt(16);

			$tx = $pos->x + $x;
			$ty = $pos->y + $y;
			$tz = $pos->z + $z;
			if($level->getBlockId($tx, $ty, $tz) === Block::STONE){
				$level->setBlock($tx, $ty, $tz, Block::EMERALD_ORE, 0);
			}
		}

		$silverfish = new Ore(Block::MONSTER_EGG_BLOCK, 0, 8);
		for($i = 0; $i < 7; $i++){
			$x = $pos->x + $r->nextBoundedInt(16);
			$y = $pos->y + $r->nextBoundedInt(64);
			$z = $pos->z + $r->nextBoundedInt(16);
			$silverfish->generate($level, $r, new BlockPos($x, $y, $z));
		}
	}

	private static function getHeightAtForBiome(ObjectChunkManager $level, $x, $z){
		for($y = 127; $y >= 0; $y--){
			$bid = $level->getBlockId($x, $y, $z);
			if($bid !== 0 && $bid !== 8 && $bid !== 9 && $bid !== 10 && $bid !== 11){
				return $y + 1;
			}
		}
		return 64;
	}

	public function getTreeFeature(JavaRandom $r){
		switch($this->id){
			case 4:
			case 18:
				if($r->nextBoundedInt(5) === 0){
					return new BirchTree(false);
				}
				if($r->nextBoundedInt(10) === 0){
					return new BigOakTree();
				}
				return new OakTree();
			case 27:
			case 28:
				return new BirchTree(false);
			case 29:
				if($r->nextBoundedInt(3) > 0){
					return new DarkOakTree();
				}
				if($r->nextBoundedInt(5) === 0){
					return new BirchTree(false);
				}
				if($r->nextBoundedInt(10) === 0){
					return new BigOakTree();
				}
				return new OakTree();
			case 5:
			case 19:
			case 30:
			case 31:
				if($r->nextBoundedInt(3) === 0){
					return new PineTree();
				}
				return new SpruceTree();
			case 32:
			case 33:
				$isSpruce = ($this->id === 33);
				if($r->nextBoundedInt(3) === 0){
					if($isSpruce || $r->nextBoundedInt(13) === 0){
						return new MegaPineTree(true);
					}
					return new MegaPineTree(false);
				}
				if($r->nextBoundedInt(3) === 0){
					return new PineTree();
				}
				return new SpruceTree();
			case 6:
				return new SwampTree();
			case 12:
			case 13:
			case 140:
				return new SpruceTree();
			case 21:
			case 22:
				return $this->jungleTree($r, false);
			case 23:
				return $this->jungleTree($r, true);
			case 35:
			case 36:
				if($r->nextBoundedInt(5) > 0){
					return new SavannaTree();
				}
				return new OakTree();
			case 3:
			case 20:
			case 34:
				if($r->nextBoundedInt(3) > 0){
					return new SpruceTree();
				}
				return new OakTree();
		}
		return new OakTree();
	}

	private function jungleTree(JavaRandom $r, $isEdge){
		if($r->nextBoundedInt(10) === 0){
			return new BigOakTree();
		}
		if($r->nextBoundedInt(2) === 0){
			return new JungleBush(Block::LOG, 3, Block::LEAVES, 3);
		}
		if(!$isEdge && $r->nextBoundedInt(3) === 0){
			return new MegaJungleTree();
		}
		return new JungleSmallTree();
	}

	public function getFlowerType(JavaRandom $r, BlockPos $pos){
		if($this->id === 1 || $this->id === 129){
			$noise = GrassColorNoise::getValue($pos->x / 200.0, $pos->z / 200.0);

			if($noise < -0.8){
				$meta = 4 + $r->nextBoundedInt(4);
				return new Bush(Block::RED_FLOWER, $meta);
			}elseif($r->nextBoundedInt(3) > 0){
				switch($r->nextBoundedInt(3)){
					case 0: return new Bush(Block::RED_FLOWER, 0);
					case 1: return new Bush(Block::RED_FLOWER, 3);
					default: return new Bush(Block::RED_FLOWER, 8);
				}
			}

			return new Bush(Block::DANDELION, 0);
		}

		if($this->id === 6){
			return new Bush(Block::RED_FLOWER, 1);
		}

		if($r->nextBoundedInt(3) > 0){
			return $this->decorator->flowerYGen;
		}
		return $this->decorator->flowerRGen;
	}

	public function flowerForestDecorate(ObjectChunkManager $level, JavaRandom $r, BlockPos $pos){
		$this->forestDecorate($level, $r, $pos, 2);
	}

	public function getFlowerForestType(JavaRandom $r, BlockPos $pos){
		if(self::$forestFlowerNoise === null){
			self::$forestFlowerNoise = new PerlinSimplexGenerator(new JavaRandom(2345), 1);
		}
		$d = (1.0 + self::$forestFlowerNoise->getValue($pos->x / 48.0, $pos->z / 48.0)) / 2.0;
		if($d < 0.0){ $d = 0.0; }elseif($d > 0.9999){ $d = 0.9999; }
		$meta = (int)($d * 9);
		if($meta === 1){ $meta = 0; }
		return new Bush(Block::RED_FLOWER, $meta);
	}

	public function getDecorator(){
		return $this->decorator;
	}

	/**
	 * Java BiomeGenBase.func_150560_b 的逐列表层生成。
	 * 注意 Java 从 y=255 循环到 0，即使 y>=128 不可写也会消耗 nextInt(5)；
	 * 这里保持同样的循环与随机流，只跳过 y>=128 的方块写入。
	 */
	public function genTerrainBlocks(FullChunk $chunk, JavaRandom $r, $x, $z, $noiseVal){
		$topBlock = $this->topBlock;
		$topMeta = $this->topMeta;
		$fillerBlock = $this->fillerBlock;
		$stone = Block::STONE;
		$air = Block::AIR;
		$bedrock = Block::BEDROCK;

		$chunkX = $x & 15;
		$chunkZ = $z & 15;

		$depth = (int) ($noiseVal / 3.0 + 3.0 + $r->nextDouble() * 0.25);

		$run = -1;
		$currentTop = $topBlock;
		$currentMeta = $topMeta;
		$currentFiller = $fillerBlock;

		// Java 世界高 256，但当前区块只保留 y<128。y=128..255 仍要按原顺序
		// 每层消耗一次 nextBoundedInt(5)，否则后续随机流会错位；这些层
		// 不读不写方块，因此单独走一个紧凑循环。
		$r->skipBoundedInt5(128);

		for($y = 127; $y >= 0; $y--){
			if($y <= $r->nextBoundedInt(5)){
				$chunk->setBlock($chunkX, $y, $chunkZ, $bedrock, 0);
				continue;
			}

			$id = $chunk->getBlockId($chunkX, $y, $chunkZ);

			if($id === $air){
				$run = -1;
				continue;
			}
			if($id !== $stone){
				continue;
			}

			if($run === -1){
				if($depth <= 0){
					$currentTop = $air;
					$currentMeta = 0;
					$currentFiller = $stone;
				}elseif($y >= 59 && $y <= 64){
					$currentTop = $topBlock;
					$currentMeta = $topMeta;
					$currentFiller = $fillerBlock;
				}

				if($y < 63 && $currentTop === $air){
					if($this->getTemperatureAt($x, $y, $z) < 0.15){
						$currentTop = Block::ICE;
					}else{
						$currentTop = Block::STILL_WATER;
					}
					$currentMeta = 0;
				}

				$run = $depth;

				if($y >= 62){
					$chunk->setBlock($chunkX, $y, $chunkZ, $currentTop, $currentMeta);
				}elseif($y < 56 - $depth){
					$currentTop = $air;
					$currentFiller = $stone;
					$chunk->setBlock($chunkX, $y, $chunkZ, Block::GRAVEL, 0);
				}else{
					$chunk->setBlock($chunkX, $y, $chunkZ, $currentFiller, 0);
				}
			}elseif($run > 0){
				$run--;
				$chunk->setBlock($chunkX, $y, $chunkZ, $currentFiller, 0);

				if($run === 0 && $currentFiller === Block::SAND){
					$run = $r->nextBoundedInt(4) + max(0, $y - 63);
					$currentFiller = Block::SANDSTONE;
				}
			}
		}
	}

	/**
	 * Java BiomeGenBase.func_150564_a：温度随高度递减。
	 */
	public function getTemperatureAt($x, $y, $z){
		if($y > 64){
			if(self::$temperatureNoise === null){
				self::$temperatureNoise = new PerlinSimplexGenerator(new JavaRandom(1234), 1);
			}
			// 冻结/积雪会对同一列连续调用两次（top 与 top+1），缓存一次避免重复计算 Perlin。
			if(self::$temperatureCacheX === $x && self::$temperatureCacheZ === $z){
				$f = self::$temperatureCacheValue;
			}else{
				$f = self::$temperatureNoise->getValue($x / 8.0, $z / 8.0) * 4.0;
				self::$temperatureCacheX = $x;
				self::$temperatureCacheZ = $z;
				self::$temperatureCacheValue = $f;
			}
			return $this->temperature - ($f + $y - 64.0) * 0.05 / 30.0;
		}
		return $this->temperature;
	}

	public static function generateBiomeColor($temperature, $rainfall){
		$x = (1 - $temperature) * 255;
		$z = (1 - $rainfall * $temperature) * 255;

		$c = self::interpolateColor(256, $x, $z, [0x47, 0xd0, 0x33], [0x6c, 0xb4, 0x93], [0xbf, 0xb6, 0x55], [0x80, 0xb4, 0x97]);
		return (0xFF << 24) | ($c[0] << 16) | ($c[1] << 8) | $c[2];
	}

	private static function interpolateColor($size, $x, $z, $c1, $c2, $c3, $c4){
		$l1 = self::lerpColor($c1, $c2, $x / $size);
		$l2 = self::lerpColor($c3, $c4, $x / $size);
		return self::lerpColor($l1, $l2, $z / $size);
	}

	private static function lerpColor($a, $b, $s){
		$invs = 1.0 - $s;
		return [
			(int) ($a[0] * $invs + $b[0] * $s),
			(int) ($a[1] * $invs + $b[1] * $s),
			(int) ($a[2] * $invs + $b[2] * $s)
		];
	}
}
