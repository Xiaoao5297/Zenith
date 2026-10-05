<?php

namespace pocketmine\level\generator\gorigional\biome;

use pocketmine\block\Block;
use pocketmine\level\format\FullChunk;
use pocketmine\level\generator\gorigional\JavaRandom;
use pocketmine\level\generator\gorigional\object\BlockPos;
use pocketmine\level\generator\gorigional\object\Decorator;
use pocketmine\level\generator\gorigional\object\ObjectChunkManager;

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
		$this->decorator->decorate($level, $r, $this, $pos);
	}

	public function getTreeFeature(JavaRandom $r){
		return null;
	}

	public function getFlowerType(JavaRandom $r, BlockPos $pos){
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
