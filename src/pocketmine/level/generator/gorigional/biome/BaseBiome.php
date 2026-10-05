<?php

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
use pocketmine\level\generator\gorigional\object\OakTree;
use pocketmine\level\generator\gorigional\object\PineTree;
use pocketmine\level\generator\gorigional\object\SavannaTree;
use pocketmine\level\generator\gorigional\object\SpruceTree;
use pocketmine\level\generator\gorigional\object\SwampTree;
use pocketmine\level\generator\gorigional\object\Vines;

/**
 * 移植自 SCAXE-GO-CE pkg/level/generator/biome/biome.go 的 BaseBiome。
 */
class BaseBiome implements Biome{
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

	public function decorate(ObjectChunkManager $level, JavaRandom $r, BlockPos $pos){
		switch($this->id){
			case 4:
			case 27:
				$this->forestDecorate($level, $r, $pos);
				break;
			case 29:
				$this->roofedForestDecorate($level, $r, $pos);
				break;
			case 32:
			case 33:
				$this->megaTaigaDecorate($level, $r, $pos);
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
			default:
				$this->decorator->decorate($level, $r, $this, $pos);
		}
	}

	private function forestDecorate(ObjectChunkManager $level, JavaRandom $r, BlockPos $pos){
		$doublePlantCount = $r->nextBoundedInt(5) - 3;
		for($i = 0; $i < $doublePlantCount; $i++){
			$plantType = $r->nextBoundedInt(3);
			switch($plantType){
				case 0: $plant = new DoublePlant(DoublePlant::LILAC); break;
				case 1: $plant = new DoublePlant(DoublePlant::ROSE_BUSH); break;
				default: $plant = new DoublePlant(DoublePlant::PEONY); break;
			}
			for($k = 0; $k < 5; $k++){
				$px = $r->nextBoundedInt(16) + 8;
				$pz = $r->nextBoundedInt(16) + 8;
				$terrainHeight = self::getHeightAtForBiome($level, $pos->x + $px, $pos->z + $pz);
				$py = $r->nextBoundedInt($terrainHeight + 32);
				if($plant->generate($level, $r, $pos->add($px, $py, $pz))){
					break;
				}
			}
		}
		$this->decorator->decorate($level, $r, $this, $pos);
	}

	private function roofedForestDecorate(ObjectChunkManager $level, JavaRandom $r, BlockPos $pos){
		for($i = 0; $i < 4; $i++){
			for($j = 0; $j < 4; $j++){
				$k = $i * 4 + 1 + 8 + $r->nextBoundedInt(3);
				$l = $j * 4 + 1 + 8 + $r->nextBoundedInt(3);

				$treeX = $pos->x + $k;
				$treeZ = $pos->z + $l;
				$treeY = self::getHeightAtForBiome($level, $treeX, $treeZ);

				$treePos = new BlockPos($treeX, $treeY, $treeZ);

				if($r->nextBoundedInt(20) === 0){
					continue;
				}

				$tree = $this->getTreeFeature($r);
				if($tree !== null){
					$tree->generate($level, $r, $treePos);
				}
			}
		}

		$doublePlantCount = $r->nextBoundedInt(5) - 3;
		for($i = 0; $i < $doublePlantCount; $i++){
			$plantType = $r->nextBoundedInt(3);
			switch($plantType){
				case 0: $plant = new DoublePlant(DoublePlant::LILAC); break;
				case 1: $plant = new DoublePlant(DoublePlant::ROSE_BUSH); break;
				default: $plant = new DoublePlant(DoublePlant::PEONY); break;
			}
			for($k = 0; $k < 5; $k++){
				$px = $r->nextBoundedInt(16) + 8;
				$pz = $r->nextBoundedInt(16) + 8;
				$py = $r->nextBoundedInt(128 + 32);
				if($plant->generate($level, $r, $pos->add($px, $py, $pz))){
					break;
				}
			}
		}

		$this->decorator->decorate($level, $r, $this, $pos);
	}

	private function megaTaigaDecorate(ObjectChunkManager $level, JavaRandom $r, BlockPos $pos){
		$boulderCount = $r->nextBoundedInt(3);
		for($i = 0; $i < $boulderCount; $i++){
			$bx = $r->nextBoundedInt(16) + 8;
			$bz = $r->nextBoundedInt(16) + 8;
			$boulder = new BlockBlob(Block::MOSS_STONE, 0);
			$boulder->generate($level, $r, $pos->add($bx, 128, $bz));
		}

		for($i = 0; $i < 7; $i++){
			$fx = $r->nextBoundedInt(16) + 8;
			$fz = $r->nextBoundedInt(16) + 8;
			$fy = $r->nextBoundedInt(160);
			$fern = new DoublePlant(DoublePlant::FERN);
			$fern->generate($level, $r, $pos->add($fx, $fy, $fz));
		}

		$this->decorator->decorate($level, $r, $this, $pos);
	}

	private function jungleDecorate(ObjectChunkManager $level, JavaRandom $r, BlockPos $pos){
		$this->decorator->decorate($level, $r, $this, $pos);

		$x = $r->nextBoundedInt(16) + 8;
		$z = $r->nextBoundedInt(16) + 8;
		$y = $r->nextBoundedInt(256);
		$melon = new BlockPatch(Block::MELON_BLOCK);
		$melon->generate($level, $r, $pos->add($x, $y, $z));

		$vines = new Vines();
		for($i = 0; $i < 50; $i++){
			$vx = $r->nextBoundedInt(16) + 8;
			$vz = $r->nextBoundedInt(16) + 8;
			$vines->generate($level, $r, $pos->add($vx, 128, $vz));
		}
	}

	private function savannaDecorate(ObjectChunkManager $level, JavaRandom $r, BlockPos $pos){
		for($i = 0; $i < 7; $i++){
			$px = $r->nextBoundedInt(16) + 8;
			$pz = $r->nextBoundedInt(16) + 8;

			$terrainHeight = self::getHeightAtForBiome($level, $pos->x + $px, $pos->z + $pz);
			$py = $r->nextBoundedInt($terrainHeight + 32);
			$grass = new DoublePlant(DoublePlant::GRASS);
			$grass->generate($level, $r, $pos->add($px, $py, $pz));
		}

		$this->decorator->decorate($level, $r, $this, $pos);
	}

	private function sunflowerDecorate(ObjectChunkManager $level, JavaRandom $r, BlockPos $pos){
		$this->decorator->decorate($level, $r, $this, $pos);

		$sunflower = new DoublePlant(DoublePlant::SUNFLOWER);
		for($i = 0; $i < 10; $i++){
			$x = $pos->x + $r->nextBoundedInt(16) + 8;
			$z = $pos->z + $r->nextBoundedInt(16) + 8;

			$terrainHeight = $level->getHeight($x, $z);
			$yMax = $terrainHeight + 32;
			if($yMax > 0){
				$y = $r->nextBoundedInt($yMax);
				$sunflower->generate($level, $r, new BlockPos($x, $y, $z));
			}
		}
	}

	private function extremeHillsDecorate(ObjectChunkManager $level, JavaRandom $r, BlockPos $pos){
		$this->decorator->decorate($level, $r, $this, $pos);

		$emeraldCount = $r->nextBoundedInt(3) + 3 + $r->nextBoundedInt(6);
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

		for($i = 0; $i < 7; $i++){
			$x = $r->nextBoundedInt(16);
			$y = $r->nextBoundedInt(64);
			$z = $r->nextBoundedInt(16);

			$tx = $pos->x + $x;
			$ty = $pos->y + $y;
			$tz = $pos->z + $z;
			if($level->getBlockId($tx, $ty, $tz) === Block::STONE){
				$level->setBlock($tx, $ty, $tz, Block::MONSTER_EGG_BLOCK, 0);
			}
		}
	}

	private static function getHeightAtForBiome(ObjectChunkManager $level, $x, $z){
		for($y = 255; $y >= 0; $y--){
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
				if($r->nextBoundedInt(5) === 0){
					return new BirchTree(false);
				}
				if($r->nextBoundedInt(10) === 0){
					return new BigOakTree();
				}
				return new OakTree();
			case 27:
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
				switch($r->nextBoundedInt(4)){
					case 0: return new Bush(Block::RED_FLOWER, 5);
					case 1: return new Bush(Block::RED_FLOWER, 4);
					case 2: return new Bush(Block::RED_FLOWER, 7);
					default: return new Bush(Block::RED_FLOWER, 6);
				}
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

	public function getDecorator(){
		return $this->decorator;
	}

	public function genTerrainBlocks(FullChunk $chunk, JavaRandom $r, $x, $z, $noiseVal){
		$topBlock = Block::GRASS;
		$fillerBlock = Block::DIRT;
		$stone = Block::STONE;
		$air = Block::AIR;
		$bedrock = Block::BEDROCK;

		$seaLevel = 63;

		$chunkX = $x & 15;
		$chunkZ = $z & 15;

		$depth = (int) ($noiseVal / 3.0 + 3.0 + $r->nextDouble() * 0.25);

		$run = -1;
		$currentTop = $topBlock;
		$currentFiller = $fillerBlock;

		for($y = 255; $y >= 0; $y--){
			if($y <= $r->nextBoundedInt(5)){
				$chunk->setBlock($chunkX, $y, $chunkZ, $bedrock, 0);
				continue;
			}

			$id = $chunk->getBlockId($chunkX, $y, $chunkZ);

			if($id === $air){
				$run = -1;
			}elseif($id === $stone){
				if($run === -1){
					if($depth <= 0){
						$currentTop = $air;
						$currentFiller = $stone;
					}elseif($y >= $seaLevel - 4 && $y <= $seaLevel + 1){
						$currentTop = $topBlock;
						$currentFiller = $fillerBlock;
					}

					$run = $depth;

					if($y >= $seaLevel - 1){
						$chunk->setBlock($chunkX, $y, $chunkZ, $currentTop, 0);
					}elseif($y < $seaLevel - 7 - $depth){
						$currentTop = $air;
						$currentFiller = $stone;
						$chunk->setBlock($chunkX, $y, $chunkZ, Block::GRAVEL, 0);
					}else{
						$chunk->setBlock($chunkX, $y, $chunkZ, $currentFiller, 0);
					}
				}elseif($run > 0){
					$run--;
					$chunk->setBlock($chunkX, $y, $chunkZ, $currentFiller, 0);

					if($run === 0 && $currentFiller === Block::SAND && $depth > 1){
						$extraDepth = $r->nextBoundedInt(4);
						if($y - 63 > 0){
							$extraDepth += $y - 63;
						}
						$run = $extraDepth;
					}
				}
			}
		}
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
