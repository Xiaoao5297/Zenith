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


namespace pocketmine\level\generator\gorigional\object;

use pocketmine\block\Block;
use pocketmine\level\generator\gorigional\biome\Biome;
use pocketmine\level\generator\gorigional\JavaRandom;

/**
 * 移植自 SCAXE-GO-CE pkg/level/generator/biome/decorator.go。
 * 树木与大型特征（蘑菇、冰刺等）尚未移植，相关数量保持为 0 或返回 null 时跳过。
 */
class Decorator{
	/** @var callable|null 阶段计时回调：($stage, $startTime, $startMemory, $pos) */
	private static $timingCallback = null;

	/** @var int */
	public $treesPerChunk = 0;
	/** @var float */
	public $extraTreeChance = 0.1;
	/** @var int */
	public $flowersPerChunk = 2;
	/** @var int */
	public $grassPerChunk = 1;
	/** @var int */
	public $deadBushPerChunk = 0;
	/** @var int */
	public $mushroomsPerChunk = 0;
	/** @var int */
	public $reedsPerChunk = 0;
	/** @var int */
	public $cactiPerChunk = 0;
	/** @var int */
	public $waterlilyPerChunk = 0;
	/** @var int */
	public $gravelPatchesPerChunk = 1;
	/** @var int */
	public $sandPatchesPerChunk = 3;
	/** @var int */
	public $clayPerChunk = 1;
	/** @var int */
	public $bigMushroomsPerChunk = 0;
	/** @var bool */
	public $generateFalls = true;

	/** @var Ore */
	public $dirtGen;
	/** @var Ore */
	public $gravelOreGen;
	/** @var Ore */
	public $graniteGen;
	/** @var Ore */
	public $dioriteGen;
	/** @var Ore */
	public $andesiteGen;
	/** @var Ore */
	public $coalGen;
	/** @var Ore */
	public $ironGen;
	/** @var Ore */
	public $goldGen;
	/** @var Ore */
	public $redstoneGen;
	/** @var Ore */
	public $diamondGen;
	/** @var Ore */
	public $lapisGen;

	/** @var Sand */
	public $sandGen;
	/** @var Sand */
	public $gravelGen;
	/** @var Clay */
	public $clayGen;
	/** @var Bush */
	public $mushroomBrGen;
	/** @var Bush */
	public $mushroomRdGen;
	/** @var Bush */
	public $flowerYGen;
	/** @var Bush */
	public $flowerRGen;
	/** @var Grass */
	public $grassGen;
	/** @var Reed */
	public $reedGen;
	/** @var Cactus */
	public $cactusGen;
	/** @var Waterlily */
	public $waterlilyGen;
	/** @var Pumpkin */
	public $pumpkinGen;
	/** @var DeadBush */
	public $deadBushGen;
	/** @var Spring */
	public $waterSpringGen;
	/** @var Spring */
	public $lavaSpringGen;

	public function __construct(){
		$this->initOres();

		$this->sandGen = new Sand(Block::SAND, 7);
		$this->gravelGen = new Sand(Block::GRAVEL, 6);
		$this->clayGen = new Clay(4);

		$this->mushroomBrGen = new Bush(Block::BROWN_MUSHROOM, 0);
		$this->mushroomRdGen = new Bush(Block::RED_MUSHROOM, 0);
		$this->flowerYGen = new Bush(Block::DANDELION, 0);
		$this->flowerRGen = new Bush(Block::RED_FLOWER, 0);
		$this->grassGen = new Grass(1);
		$this->reedGen = new Reed();
		$this->cactusGen = new Cactus();
		$this->waterlilyGen = new Waterlily();
		$this->pumpkinGen = new Pumpkin();
		$this->deadBushGen = new DeadBush();
		$this->waterSpringGen = new Spring(Block::WATER);
		$this->lavaSpringGen = new Spring(Block::LAVA);
	}

	private function initOres(){
		$this->dirtGen = new Ore(Block::DIRT, 0, 32);
		$this->gravelOreGen = new Ore(Block::GRAVEL, 0, 32);
		$this->coalGen = new Ore(Block::COAL_ORE, 0, 16);
		$this->ironGen = new Ore(Block::IRON_ORE, 0, 8);
		$this->goldGen = new Ore(Block::GOLD_ORE, 0, 8);
		$this->redstoneGen = new Ore(Block::REDSTONE_ORE, 0, 7);
		$this->diamondGen = new Ore(Block::DIAMOND_ORE, 0, 7);
		$this->lapisGen = new Ore(Block::LAPIS_ORE, 0, 6);
	}

	public static function setTimingCallback($callback){
		self::$timingCallback = $callback;
	}

	private static function timing($stage, $startTime, $startMemory, BlockPos $pos){
		if(self::$timingCallback !== null){
			call_user_func(self::$timingCallback, $stage, $startTime, $startMemory, $pos->x >> 4, $pos->z >> 4);
		}
	}

	public function decorate(ObjectChunkManager $w, JavaRandom $r, Biome $b, BlockPos $pos){
		$stageStart = microtime(true); $stageMemory = memory_get_usage(false);
		// Java BiomeDecorator.func_76797_b：矿石生成顺序与范围。
		$this->genStandardOre1($w, $r, 20, $this->dirtGen, 0, 256, $pos);
		$this->genStandardOre1($w, $r, 10, $this->gravelOreGen, 0, 256, $pos);
		$this->genStandardOre1($w, $r, 20, $this->coalGen, 0, 128, $pos);
		$this->genStandardOre1($w, $r, 20, $this->ironGen, 0, 64, $pos);
		$this->genStandardOre1($w, $r, 2, $this->goldGen, 0, 32, $pos);
		$this->genStandardOre1($w, $r, 8, $this->redstoneGen, 0, 16, $pos);
		$this->genStandardOre1($w, $r, 1, $this->diamondGen, 0, 16, $pos);
		$this->genStandardOre2($w, $r, 1, $this->lapisGen, 16, 16, $pos);
		self::timing("decorate-ores", $stageStart, $stageMemory, $pos);

		// Java BiomeDecorator 的非矿石特征统一使用 +8 偏移。
		$stageStart = microtime(true); $stageMemory = memory_get_usage(false);
		for($i = 0; $i < $this->sandPatchesPerChunk; $i++){
			$x = $pos->x + $r->nextBoundedInt(16) + 8;
			$z = $pos->z + $r->nextBoundedInt(16) + 8;
			$terrainHeight = $w->getTopSolidOrLiquidBlock($x, $z);
			$this->sandGen->generate($w, $r, new BlockPos($x, $terrainHeight, $z));
		}

		for($i = 0; $i < $this->clayPerChunk; $i++){
			$x = $pos->x + $r->nextBoundedInt(16) + 8;
			$z = $pos->z + $r->nextBoundedInt(16) + 8;
			$terrainHeight = $w->getTopSolidOrLiquidBlock($x, $z);
			$this->clayGen->generate($w, $r, new BlockPos($x, $terrainHeight, $z));
		}

		for($i = 0; $i < $this->gravelPatchesPerChunk; $i++){
			$x = $pos->x + $r->nextBoundedInt(16) + 8;
			$z = $pos->z + $r->nextBoundedInt(16) + 8;
			$terrainHeight = $w->getTopSolidOrLiquidBlock($x, $z);
			$this->gravelGen->generate($w, $r, new BlockPos($x, $terrainHeight, $z));
		}
		self::timing("decorate-patches", $stageStart, $stageMemory, $pos);

		$stageStart = microtime(true); $stageMemory = memory_get_usage(false);
		$trees = $this->treesPerChunk;
		if($r->nextBoundedInt(10) === 0){
			$trees++;
		}
		for($i = 0; $i < $trees; $i++){
			$x = $pos->x + $r->nextBoundedInt(16) + 8;
			$z = $pos->z + $r->nextBoundedInt(16) + 8;
			$terrainHeight = $w->getHeight($x, $z) + 1;

			$feature = $b->getTreeFeature($r);
			if($feature !== null){
				$feature->generate($w, $r, new BlockPos($x, $terrainHeight, $z));
			}
		}

		for($i = 0; $i < $this->bigMushroomsPerChunk; $i++){
			$x = $pos->x + $r->nextBoundedInt(16) + 8;
			$z = $pos->z + $r->nextBoundedInt(16) + 8;
			$terrainHeight = $w->getHeight($x, $z) + 1;
			$mushroom = new BigMushroom($r->nextBoolean());
			$mushroom->generate($w, $r, new BlockPos($x, $terrainHeight, $z));
		}
		self::timing("decorate-trees", $stageStart, $stageMemory, $pos);

		$stageStart = microtime(true); $stageMemory = memory_get_usage(false);
		for($i = 0; $i < $this->flowersPerChunk; $i++){
			$x = $pos->x + $r->nextBoundedInt(16) + 8;
			$z = $pos->z + $r->nextBoundedInt(16) + 8;
			$terrainHeight = $r->nextBoundedInt($w->getHeight($x, $z) + 1 + 32);
			$blockPos = new BlockPos($x, $terrainHeight, $z);

			$gen = $b->getFlowerType($r, $blockPos);
			if($gen !== null){
				$gen->generate($w, $r, $blockPos);
			}
		}

		for($i = 0; $i < $this->grassPerChunk; $i++){
			$x = $pos->x + $r->nextBoundedInt(16) + 8;
			$z = $pos->z + $r->nextBoundedInt(16) + 8;
			$terrainHeight = $r->nextBoundedInt(($w->getHeight($x, $z) + 1) * 2);
			$this->grassGen->generate($w, $r, new BlockPos($x, $terrainHeight, $z));
		}

		for($i = 0; $i < $this->deadBushPerChunk; $i++){
			$x = $pos->x + $r->nextBoundedInt(16) + 8;
			$z = $pos->z + $r->nextBoundedInt(16) + 8;
			$terrainHeight = $r->nextBoundedInt(($w->getHeight($x, $z) + 1) * 2);
			$this->deadBushGen->generate($w, $r, new BlockPos($x, $terrainHeight, $z));
		}

		for($i = 0; $i < $this->waterlilyPerChunk; $i++){
			$x = $pos->x + $r->nextBoundedInt(16) + 8;
			$z = $pos->z + $r->nextBoundedInt(16) + 8;
			$y = $r->nextBoundedInt(($w->getHeight($x, $z) + 1) * 2);
			while($y > 0 && $w->getBlockId($x, $y - 1, $z) === Block::AIR){
				$y--;
			}
			$this->waterlilyGen->generate($w, $r, new BlockPos($x, $y, $z));
		}

		for($i = 0; $i < $this->mushroomsPerChunk; $i++){
			if($r->nextBoundedInt(4) === 0){
				$x = $pos->x + $r->nextBoundedInt(16) + 8;
				$z = $pos->z + $r->nextBoundedInt(16) + 8;
				$terrainHeight = $w->getHeight($x, $z) + 1;
				$this->mushroomBrGen->generate($w, $r, new BlockPos($x, $terrainHeight, $z));
			}
			if($r->nextBoundedInt(8) === 0){
				$x = $pos->x + $r->nextBoundedInt(16) + 8;
				$z = $pos->z + $r->nextBoundedInt(16) + 8;
				$terrainHeight = $r->nextBoundedInt(($w->getHeight($x, $z) + 1) * 2);
				$this->mushroomRdGen->generate($w, $r, new BlockPos($x, $terrainHeight, $z));
			}
		}

		if($r->nextBoundedInt(4) === 0){
			$x = $pos->x + $r->nextBoundedInt(16) + 8;
			$z = $pos->z + $r->nextBoundedInt(16) + 8;
			$terrainHeight = $r->nextBoundedInt(($w->getHeight($x, $z) + 1) * 2);
			$this->mushroomBrGen->generate($w, $r, new BlockPos($x, $terrainHeight, $z));
		}
		if($r->nextBoundedInt(8) === 0){
			$x = $pos->x + $r->nextBoundedInt(16) + 8;
			$z = $pos->z + $r->nextBoundedInt(16) + 8;
			$terrainHeight = $r->nextBoundedInt(($w->getHeight($x, $z) + 1) * 2);
			$this->mushroomRdGen->generate($w, $r, new BlockPos($x, $terrainHeight, $z));
		}

		for($i = 0; $i < $this->reedsPerChunk; $i++){
			$x = $pos->x + $r->nextBoundedInt(16) + 8;
			$z = $pos->z + $r->nextBoundedInt(16) + 8;
			$terrainHeight = $r->nextBoundedInt(($w->getHeight($x, $z) + 1) * 2);
			$this->reedGen->generate($w, $r, new BlockPos($x, $terrainHeight, $z));
		}

		// Java 在 biome 自身芦苇数量之外固定再生成 10 次。
		for($i = 0; $i < 10; $i++){
			$x = $pos->x + $r->nextBoundedInt(16) + 8;
			$z = $pos->z + $r->nextBoundedInt(16) + 8;
			$terrainHeight = $r->nextBoundedInt(($w->getHeight($x, $z) + 1) * 2);
			$this->reedGen->generate($w, $r, new BlockPos($x, $terrainHeight, $z));
		}

		// Java BiomeDecorator：无论群系配置如何，都固定再尝试 10 次芦苇。
		for($i = 0; $i < 10; $i++){
			$x = $r->nextBoundedInt(16);
			$z = $r->nextBoundedInt(16);
			$terrainHeight = $w->getHeight($pos->x + $x, $pos->z + $z);
			$this->reedGen->generate($w, $r, $pos->add($x, $terrainHeight + 1, $z));
		}

		if($r->nextBoundedInt(32) === 0){
			$x = $pos->x + $r->nextBoundedInt(16) + 8;
			$z = $pos->z + $r->nextBoundedInt(16) + 8;
			$terrainHeight = $r->nextBoundedInt(($w->getHeight($x, $z) + 1) * 2);
			$this->pumpkinGen->generate($w, $r, new BlockPos($x, $terrainHeight, $z));
		}

		for($i = 0; $i < $this->cactiPerChunk; $i++){
			$x = $pos->x + $r->nextBoundedInt(16) + 8;
			$z = $pos->z + $r->nextBoundedInt(16) + 8;
			$terrainHeight = $r->nextBoundedInt(($w->getHeight($x, $z) + 1) * 2);
			$this->cactusGen->generate($w, $r, new BlockPos($x, $terrainHeight, $z));
		}
		self::timing("decorate-plants", $stageStart, $stageMemory, $pos);

		if($this->generateFalls){
			$stageStart = microtime(true); $stageMemory = memory_get_usage(false);
			for($i = 0; $i < 50; $i++){
				$x = $pos->x + $r->nextBoundedInt(16) + 8;
				$y = $r->nextBoundedInt($r->nextBoundedInt(248) + 8);
				$z = $pos->z + $r->nextBoundedInt(16) + 8;
				$this->waterSpringGen->generate($w, $r, new BlockPos($x, $y, $z));
			}
			for($i = 0; $i < 20; $i++){
				$x = $pos->x + $r->nextBoundedInt(16) + 8;
				$y = $r->nextBoundedInt($r->nextBoundedInt($r->nextBoundedInt(240) + 8) + 8);
				$z = $pos->z + $r->nextBoundedInt(16) + 8;
				$this->lavaSpringGen->generate($w, $r, new BlockPos($x, $y, $z));
			}
			self::timing("decorate-springs", $stageStart, $stageMemory, $pos);
		}
	}

	private function genStandardOre1(ObjectChunkManager $w, JavaRandom $r, $count, Ore $gen, $minH, $maxH, BlockPos $pos){
		if($maxH < $minH){
			$t = $minH;
			$minH = $maxH;
			$maxH = $t;
		}elseif($maxH === $minH){
			if($minH < 255){
				$maxH++;
			}else{
				$minH--;
			}
		}

		for($i = 0; $i < $count; $i++){
			$x = $r->nextBoundedInt(16);
			$y = $r->nextBoundedInt($maxH - $minH) + $minH;
			$z = $r->nextBoundedInt(16);
			$gen->generate($w, $r, $pos->add($x, $y, $z));
		}
	}

	private function genStandardOre2(ObjectChunkManager $w, JavaRandom $r, $count, Ore $gen, $centerH, $spread, BlockPos $pos){
		for($i = 0; $i < $count; $i++){
			$x = $r->nextBoundedInt(16);
			$y = $r->nextBoundedInt($spread) + $r->nextBoundedInt($spread) + $centerH - $spread;
			$z = $r->nextBoundedInt(16);
			$gen->generate($w, $r, $pos->add($x, $y, $z));
		}
	}
}
