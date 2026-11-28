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
use pocketmine\level\generator\gorigional\noise\PerlinSimplexGenerator;

/**
 * Java 1.7.10 BiomeGenMesa：黏土色带 + 可选高原噪声 + Bryce 变种。
 */
class MesaBiome extends BaseBiome{
	/** @var int[] */
	private $clayBands = [];
	/** @var bool Java field_150626_aH（Bryce 高原噪声） */
	private $useNoise = false;
	/** @var bool Java field_150620_aI（高原顶部草/泥土） */
	private $hasGrass = false;
	/** @var PerlinSimplexGenerator|null Java field_150623_aE */
	private $plateauNoise = null;
	/** @var PerlinSimplexGenerator|null Java field_150624_aF */
	private $secondaryNoise = null;
	/** @var PerlinSimplexGenerator|null Java field_150625_aG */
	private $bandNoise = null;
	/** @var int Java field_150622_aD（上一次生成使用的世界种子） */
	private $noiseSeed = 0;

	public function __construct($id, $name, $baseHeight, $heightVariation){
		parent::__construct($id, $name, $baseHeight, $heightVariation, 2.0, 0.0);
		$this->topBlock = Block::SAND;
		$this->topMeta = 1;
		$this->fillerBlock = Block::STAINED_CLAY;
		$this->useNoise = ($id === 165);
		$this->hasGrass = ($id === 38 || $id === 166);
		$this->clayBands = array_fill(0, 64, 16);
	}

	/**
	 * Java BiomeGenMesa.func_150566_k：按父群系 id 新建带 Bryce 标记的台地实例。
	 */
	public function createMutated($id, $baseHeight, $heightVariation){
		return new MesaBiome($id, "Mesa M", $baseHeight, $heightVariation);
	}

	public function refreshBands($seed){
		if($this->bandNoise !== null && $this->noiseSeed === $seed){
			return;
		}
		$this->clayBands = array_fill(0, 64, 16);
		$r = new JavaRandom($seed);

		// Java func_150619_a：色带噪声和色带生成共用同一个 Random。
		$this->bandNoise = new PerlinSimplexGenerator($r, 1);

		// Java func_150619_a：橙色黏土带。
		for($i = 0; $i < 64; $i++){
			$i += $r->nextBoundedInt(5) + 1;
			if($i < 64){
				$this->clayBands[$i] = 1;
			}
		}

		// 4：黄色黏土。
		$count = $r->nextBoundedInt(4) + 2;
		for($n = 0; $n < $count; $n++){
			$width = $r->nextBoundedInt(3) + 1;
			$start = $r->nextBoundedInt(64);
			for($j = 0; $start + $j < 64 && $j < $width; $j++){
				$this->clayBands[$start + $j] = 4;
			}
		}

		// 12：棕色黏土。
		$count = $r->nextBoundedInt(4) + 2;
		for($n = 0; $n < $count; $n++){
			$width = $r->nextBoundedInt(3) + 2;
			$start = $r->nextBoundedInt(64);
			for($j = 0; $start + $j < 64 && $j < $width; $j++){
				$this->clayBands[$start + $j] = 12;
			}
		}

		// 14：红色黏土。
		$count = $r->nextBoundedInt(4) + 2;
		for($n = 0; $n < $count; $n++){
			$width = $r->nextBoundedInt(3) + 1;
			$start = $r->nextBoundedInt(64);
			for($j = 0; $start + $j < 64 && $j < $width; $j++){
				$this->clayBands[$start + $j] = 14;
			}
		}

		// 0 / 8：白色与浅灰色黏土带。
		$count = $r->nextBoundedInt(3) + 3;
		$pos = 0;
		for($n = 0; $n < $count; $n++){
			$pos += $r->nextBoundedInt(16) + 4;
			if($pos >= 64){
				break;
			}
			$this->clayBands[$pos] = 0;
			if($pos > 1 && $r->nextBoolean()){
				$this->clayBands[$pos - 1] = 8;
			}
			if($pos < 63 && $r->nextBoolean()){
				$this->clayBands[$pos + 1] = 8;
			}
		}

		// Java func_150573_a：高原/次级噪声用 new Random(field_150622_aD) 初始化。
		// 1.7.10 中 field_150622_aD 在噪声创建之后才赋值为世界种子，
		// 因此首次生成（字段初值 0）实际使用 Random(0)。
		$r2 = new JavaRandom($this->noiseSeed);
		$this->plateauNoise = new PerlinSimplexGenerator($r2, 4);
		$this->secondaryNoise = new PerlinSimplexGenerator($r2, 1);
		$this->noiseSeed = $seed;
	}

	/**
	 * Java BiomeGenMesa.func_150618_d。
	 */
	private function getBandAt($x, $y, $z){
		$offset = (int) floor($this->bandNoise->getValue($x / 512.0, $z / 512.0) * 2.0 + 0.5);
		return $this->clayBands[($y + $offset + 64) % 64];
	}

	public function genTerrainBlocks(FullChunk $chunk, JavaRandom $r, $x, $z, $noiseVal){
		$chunkX = $x & 15;
		$chunkZ = $z & 15;

		$plateauHeight = 0.0;
		if($this->useNoise){
			$k = ($x & -16) + ($z & 15);
			$l = ($z & -16) + ($x & 15);
			$d2 = min(abs($noiseVal), $this->plateauNoise->getValue($k * 0.25, $l * 0.25));

			if($d2 > 0.0){
				$d3 = 0.001953125;
				$d4 = abs($this->secondaryNoise->getValue($k * $d3, $l * $d3));
				$plateauHeight = $d2 * $d2 * 2.5;
				$d5 = ceil($d4 * 50.0) + 14.0;
				if($plateauHeight > $d5){
					$plateauHeight = $d5;
				}
				$plateauHeight += 64.0;
			}
		}

		$stone = Block::STONE;
		$air = Block::AIR;
		$bedrock = Block::BEDROCK;
		$hardenedClay = Block::HARDENED_CLAY;
		$stainedClay = Block::STAINED_CLAY;

		$depth = (int) ($noiseVal / 3.0 + 3.0 + $r->nextDouble() * 0.25);
		$flag1 = cos($noiseVal / 3.0 * M_PI) > 0.0;
		$run = -1;
		$isTopBand = false;

		$currentBlock = $stainedClay;
		$currentFiller = $this->fillerBlock;

		for($y = 255; $y >= 0; $y--){
			if($y < 128){
				$id = $chunk->getBlockId($chunkX, $y, $chunkZ);
				if($id === $air && $y < (int) $plateauHeight){
					$chunk->setBlock($chunkX, $y, $chunkZ, $stone, 0);
					$id = $stone;
				}
			}else{
				$id = $air;
			}

			if($y <= $r->nextBoundedInt(5)){
				if($y < 128){
					$chunk->setBlock($chunkX, $y, $chunkZ, $bedrock, 0);
				}
				continue;
			}

			if($id === $air){
				$run = -1;
				continue;
			}
			if($id !== $stone){
				continue;
			}

			if($run === -1){
				$isTopBand = false;
				if($depth <= 0){
					$currentBlock = $air;
					$currentFiller = $stone;
				}elseif($y >= 59 && $y <= 64){
					$currentBlock = $stainedClay;
					$currentFiller = $this->fillerBlock;
				}

				if($y < 63 && $currentBlock === $air){
					$currentBlock = Block::STILL_WATER;
				}

				$run = $depth + max(0, $y - 63);
				if($y >= 62){
					if($y < 128){
						if($this->hasGrass && $y > 86 + $depth * 2){
							if($flag1){
								$chunk->setBlock($chunkX, $y, $chunkZ, Block::DIRT, 1);
							}else{
								$chunk->setBlock($chunkX, $y, $chunkZ, Block::GRASS, 0);
							}
						}elseif($y > 66 + $depth){
							$band = 16;
							if($y >= 64 && $y <= 127){
								if(!$flag1){
									$band = $this->getBandAt($x, $y, $z);
								}
							}else{
								$band = 1;
							}

							if($band < 16){
								$chunk->setBlock($chunkX, $y, $chunkZ, $stainedClay, $band);
							}else{
								$chunk->setBlock($chunkX, $y, $chunkZ, $hardenedClay, 0);
							}
						}else{
							$chunk->setBlock($chunkX, $y, $chunkZ, $this->topBlock, $this->topMeta);
							$isTopBand = true;
						}
					}
				}else{
					if($y < 128){
						$chunk->setBlock($chunkX, $y, $chunkZ, $currentFiller, $currentFiller === $stainedClay ? 1 : 0);
					}
				}
			}elseif($run > 0){
				$run--;
				if($y >= 128){
					continue;
				}

				if($isTopBand){
					$chunk->setBlock($chunkX, $y, $chunkZ, $stainedClay, 1);
				}else{
					$band = $this->getBandAt($x, $y, $z);
					if($band < 16){
						$chunk->setBlock($chunkX, $y, $chunkZ, $stainedClay, $band);
					}else{
						$chunk->setBlock($chunkX, $y, $chunkZ, $hardenedClay, 0);
					}
				}
			}
		}
	}
}
