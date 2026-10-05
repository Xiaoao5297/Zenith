<?php

namespace pocketmine\level\generator\gorigional\object;

use pocketmine\block\Block;
use pocketmine\level\generator\gorigional\biome\Biome;
use pocketmine\level\generator\gorigional\JavaRandom;

/**
 * 移植自 SCAXE-GO-CE pkg/level/generator/biome/decorator.go。
 * 树木与大型特征（蘑菇、冰刺等）尚未移植，相关数量保持为 0 或返回 null 时跳过。
 */
class Decorator{
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
		$this->dirtGen = new Ore(Block::DIRT, 0, 33);
		$this->gravelOreGen = new Ore(Block::GRAVEL, 0, 33);
		$this->graniteGen = new Ore(Block::STONE, 1, 33);
		$this->dioriteGen = new Ore(Block::STONE, 3, 33);
		$this->andesiteGen = new Ore(Block::STONE, 5, 33);
		$this->coalGen = new Ore(Block::COAL_ORE, 0, 17);
		$this->ironGen = new Ore(Block::IRON_ORE, 0, 9);
		$this->goldGen = new Ore(Block::GOLD_ORE, 0, 9);
		$this->redstoneGen = new Ore(Block::REDSTONE_ORE, 0, 8);
		$this->diamondGen = new Ore(Block::DIAMOND_ORE, 0, 8);
		$this->lapisGen = new Ore(Block::LAPIS_ORE, 0, 7);
	}

	public function decorate(ObjectChunkManager $w, JavaRandom $r, Biome $b, BlockPos $pos){
		$this->genStandardOre1($w, $r, 10, $this->dirtGen, 0, 256, $pos);
		$this->genStandardOre1($w, $r, 8, $this->gravelOreGen, 0, 256, $pos);
		$this->genStandardOre1($w, $r, 10, $this->dioriteGen, 0, 80, $pos);
		$this->genStandardOre1($w, $r, 10, $this->graniteGen, 0, 80, $pos);
		$this->genStandardOre1($w, $r, 10, $this->andesiteGen, 0, 80, $pos);
		$this->genStandardOre1($w, $r, 20, $this->coalGen, 0, 128, $pos);
		$this->genStandardOre1($w, $r, 20, $this->ironGen, 0, 64, $pos);
		$this->genStandardOre1($w, $r, 2, $this->goldGen, 0, 32, $pos);
		$this->genStandardOre1($w, $r, 8, $this->redstoneGen, 0, 16, $pos);
		$this->genStandardOre1($w, $r, 1, $this->diamondGen, 0, 16, $pos);
		$this->genStandardOre2($w, $r, 1, $this->lapisGen, 16, 16, $pos);

		for($i = 0; $i < $this->sandPatchesPerChunk; $i++){
			$x = $r->nextBoundedInt(16) + 8;
			$z = $r->nextBoundedInt(16) + 8;
			$terrainHeight = $w->getHeight($pos->x + $x, $pos->z + $z);
			$this->sandGen->generate($w, $r, $pos->add($x, $terrainHeight, $z));
		}

		for($i = 0; $i < $this->clayPerChunk; $i++){
			$x = $r->nextBoundedInt(16) + 8;
			$z = $r->nextBoundedInt(16) + 8;
			$terrainHeight = $w->getHeight($pos->x + $x, $pos->z + $z);
			$this->clayGen->generate($w, $r, $pos->add($x, $terrainHeight, $z));
		}

		for($i = 0; $i < $this->gravelPatchesPerChunk; $i++){
			$x = $r->nextBoundedInt(16) + 8;
			$z = $r->nextBoundedInt(16) + 8;
			$terrainHeight = $w->getHeight($pos->x + $x, $pos->z + $z);
			$this->gravelGen->generate($w, $r, $pos->add($x, $terrainHeight, $z));
		}

		$trees = $this->treesPerChunk;
		if($r->nextFloat() < $this->extraTreeChance){
			$trees++;
		}
		for($i = 0; $i < $trees; $i++){
			$x = $r->nextBoundedInt(16) + 8;
			$z = $r->nextBoundedInt(16) + 8;

			$terrainHeight = $w->getHeight($pos->x + $x, $pos->z + $z);

			$feature = $b->getTreeFeature($r);
			if($feature !== null){
				$feature->generate($w, $r, $pos->add($x, $terrainHeight, $z));
			}
		}

		for($i = 0; $i < $this->flowersPerChunk; $i++){
			$x = $r->nextBoundedInt(16) + 8;
			$z = $r->nextBoundedInt(16) + 8;
			$terrainHeight = $w->getHeight($pos->x + $x, $pos->z + $z);
			$yMax = $terrainHeight + 32;

			if($yMax > 0){
				$y = $r->nextBoundedInt($yMax);
				$blockPos = $pos->add($x, $y, $z);

				$gen = $b->getFlowerType($r, $blockPos);
				if($gen !== null){
					$gen->generate($w, $r, $blockPos);
				}
			}
		}

		for($i = 0; $i < $this->grassPerChunk; $i++){
			$x = $r->nextBoundedInt(16) + 8;
			$z = $r->nextBoundedInt(16) + 8;

			$terrainHeight = $w->getHeight($pos->x + $x, $pos->z + $z);
			$yMax = $terrainHeight * 2;
			if($yMax > 0){
				$y = $r->nextBoundedInt($yMax);
				$this->grassGen->generate($w, $r, $pos->add($x, $y, $z));
			}
		}

		for($i = 0; $i < $this->deadBushPerChunk; $i++){
			$x = $r->nextBoundedInt(16) + 8;
			$z = $r->nextBoundedInt(16) + 8;
			$terrainHeight = $w->getHeight($pos->x + $x, $pos->z + $z);
			$yMax = $terrainHeight * 2;
			if($yMax > 0){
				$y = $r->nextBoundedInt($yMax);
				$this->deadBushGen->generate($w, $r, $pos->add($x, $y, $z));
			}
		}

		for($i = 0; $i < $this->waterlilyPerChunk; $i++){
			$x = $r->nextBoundedInt(16) + 8;
			$z = $r->nextBoundedInt(16) + 8;
			$terrainHeight = $w->getHeight($pos->x + $x, $pos->z + $z);
			$yMax = $terrainHeight * 2;
			if($yMax > 0){
				$y = $r->nextBoundedInt($yMax);
				$this->waterlilyGen->generate($w, $r, $pos->add($x, $y, $z));
			}
		}

		for($i = 0; $i < $this->mushroomsPerChunk; $i++){
			if($r->nextBoundedInt(4) === 0){
				$x = $r->nextBoundedInt(16) + 8;
				$z = $r->nextBoundedInt(16) + 8;
				$terrainHeight = $w->getHeight($pos->x + $x, $pos->z + $z);
				$this->mushroomBrGen->generate($w, $r, $pos->add($x, $terrainHeight, $z));
			}
			if($r->nextBoundedInt(8) === 0){
				$x = $r->nextBoundedInt(16) + 8;
				$z = $r->nextBoundedInt(16) + 8;
				$terrainHeight = $w->getHeight($pos->x + $x, $pos->z + $z);
				$yMax = $terrainHeight * 2;
				if($yMax > 0){
					$y = $r->nextBoundedInt($yMax);
					$this->mushroomRdGen->generate($w, $r, $pos->add($x, $y, $z));
				}
			}
		}

		if($r->nextBoundedInt(4) === 0){
			$x = $r->nextBoundedInt(16) + 8;
			$z = $r->nextBoundedInt(16) + 8;
			$terrainHeight = $w->getHeight($pos->x + $x, $pos->z + $z);
			$yMax = $terrainHeight * 2;
			if($yMax > 0){
				$y = $r->nextBoundedInt($yMax);
				$this->mushroomBrGen->generate($w, $r, $pos->add($x, $y, $z));
			}
		}
		if($r->nextBoundedInt(8) === 0){
			$x = $r->nextBoundedInt(16) + 8;
			$z = $r->nextBoundedInt(16) + 8;
			$terrainHeight = $w->getHeight($pos->x + $x, $pos->z + $z);
			$yMax = $terrainHeight * 2;
			if($yMax > 0){
				$y = $r->nextBoundedInt($yMax);
				$this->mushroomRdGen->generate($w, $r, $pos->add($x, $y, $z));
			}
		}

		for($i = 0; $i < $this->reedsPerChunk; $i++){
			$x = $r->nextBoundedInt(16) + 8;
			$z = $r->nextBoundedInt(16) + 8;
			$terrainHeight = $w->getHeight($pos->x + $x, $pos->z + $z);
			$yMax = $terrainHeight * 2;
			if($yMax > 0){
				$y = $r->nextBoundedInt($yMax);
				$this->reedGen->generate($w, $r, $pos->add($x, $y, $z));
			}
		}

		for($i = 0; $i < 10; $i++){
			$x = $r->nextBoundedInt(16) + 8;
			$z = $r->nextBoundedInt(16) + 8;
			$terrainHeight = $w->getHeight($pos->x + $x, $pos->z + $z);
			$yMax = $terrainHeight * 2;
			if($yMax > 0){
				$y = $r->nextBoundedInt($yMax);
				$this->reedGen->generate($w, $r, $pos->add($x, $y, $z));
			}
		}

		if($r->nextBoundedInt(32) === 0){
			$x = $r->nextBoundedInt(16) + 8;
			$z = $r->nextBoundedInt(16) + 8;
			$terrainHeight = $w->getHeight($pos->x + $x, $pos->z + $z);
			$yMax = $terrainHeight * 2;
			if($yMax > 0){
				$y = $r->nextBoundedInt($yMax);
				$this->pumpkinGen->generate($w, $r, $pos->add($x, $y, $z));
			}
		}

		for($i = 0; $i < $this->cactiPerChunk; $i++){
			$x = $r->nextBoundedInt(16) + 8;
			$z = $r->nextBoundedInt(16) + 8;
			$terrainHeight = $w->getHeight($pos->x + $x, $pos->z + $z);
			$yMax = $terrainHeight * 2;
			if($yMax > 0){
				$y = $r->nextBoundedInt($yMax);
				$this->cactusGen->generate($w, $r, $pos->add($x, $y, $z));
			}
		}

		if($this->generateFalls){
			for($i = 0; $i < 50; $i++){
				$p = $pos->add($r->nextBoundedInt(16) + 8, $r->nextBoundedInt($r->nextBoundedInt(248) + 8), $r->nextBoundedInt(16) + 8);
				$this->waterSpringGen->generate($w, $r, $p);
			}
			for($i = 0; $i < 20; $i++){
				$p = $pos->add($r->nextBoundedInt(16) + 8, $r->nextBoundedInt($r->nextBoundedInt($r->nextBoundedInt(240) + 8) + 8), $r->nextBoundedInt(16) + 8);
				$this->lavaSpringGen->generate($w, $r, $p);
			}
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
