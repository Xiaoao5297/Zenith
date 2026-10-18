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
use pocketmine\level\format\FullChunk;
use pocketmine\level\generator\gorigional\McpeRandom;
use pocketmine\level\generator\gorigional\JavaRandom;

/**
 * Mesa 群系表层：MCPE 的 64 层黏土色带 + 红沙台地。
 */
class MesaBiome extends BaseBiome{
	/** @var int[] */
	private $clayBands = [];

	public function __construct($id, $name, $baseHeight, $heightVariation){
		parent::__construct($id, $name, $baseHeight, $heightVariation, 2.0, 0.0);
		$this->refreshBands(12345);
	}

	public function refreshBands($seed){
		$this->clayBands = array_fill(0, 64, 16);
		$r = new McpeRandom($seed & 0xFFFFFFFF);

		// 1 层：稀疏的橙色黏土带。
		for($i = 0; $i < 64; $i++){
			$i += $r->nextBoundedInt(5) + 1;
			if($i < 64){
				$this->clayBands[$i] = 1;
			}
		}

		// 4：黄色黏土。
		$count = ($r->nextInt() & 3) + 2;
		for($n = 0; $n < $count; $n++){
			$width = $r->nextBoundedInt(3);
			$start = $r->nextInt() & 63;
			for($j = 0; $j <= $width && $start + $j < 64; $j++){
				$this->clayBands[$start + $j] = 4;
			}
		}

		// 12：棕色黏土。
		$count = ($r->nextInt() & 3) + 2;
		for($n = 0; $n < $count; $n++){
			$width = $r->nextBoundedInt(3) + 2;
			$start = $r->nextInt() & 63;
			for($j = 0; $j < $width && $start + $j < 64; $j++){
				$this->clayBands[$start + $j] = 12;
			}
		}

		// 14：红色黏土。
		$count = ($r->nextInt() & 3) + 2;
		for($n = 0; $n < $count; $n++){
			$width = $r->nextBoundedInt(3) + 1;
			$start = $r->nextInt() & 63;
			for($j = 0; $j < $width && $start + $j < 64; $j++){
				$this->clayBands[$start + $j] = 14;
			}
		}

		// 0 / 8：白色与浅灰色黏土带，带随机邻接。
		$count = ($r->nextInt() & 3) + 3;
		$pos = 0;
		for($n = 0; $n < $count; $n++){
			$pos += ($r->nextInt() & 15) + 4;
			if($pos >= 64){
				break;
			}
			$this->clayBands[$pos] = 0;
			if($pos > 1 && ($r->nextInt() & 1) === 0){
				$this->clayBands[$pos - 1] = 8;
			}
			if($pos < 63 && ($r->nextInt() & 1) === 0){
				$this->clayBands[$pos + 1] = 8;
			}
		}
	}

	public function genTerrainBlocks(FullChunk $chunk, JavaRandom $r, $x, $z, $noiseVal){
		$chunkX = $x & 15;
		$chunkZ = $z & 15;

		$bedrockLayers = 2 + ($r->nextInt() & 3);
		for($y = 0; $y < $bedrockLayers; $y++){
			$chunk->setBlock($chunkX, $y, $chunkZ, Block::BEDROCK, 0);
		}

		$depth = (int)($noiseVal / 3.0 + 3.0 + $r->nextFloat() * 0.25);
		$run = -1;
		$hasTop = false;

		for($y = 127; $y >= 0; $y--){
			$id = $chunk->getBlockId($chunkX, $y, $chunkZ);
			if($id === Block::AIR){
				$run = -1;
				continue;
			}
			if($id !== Block::STONE){
				continue;
			}

			if($run === -1){
				$hasTop = false;
				$run = $depth + max(0, $y - 63);

				if($y >= 62){
					if($y > 66 + $depth){
						$band = $this->clayBands[$y & 63];
						if($band < 16){
							$chunk->setBlock($chunkX, $y, $chunkZ, Block::STAINED_CLAY, $band);
						}else{
							$chunk->setBlock($chunkX, $y, $chunkZ, Block::HARDENED_CLAY, 0);
						}
					}else{
						$chunk->setBlock($chunkX, $y, $chunkZ, Block::SAND, 1);
						$hasTop = true;
					}
				}else{
					$chunk->setBlock($chunkX, $y, $chunkZ, Block::HARDENED_CLAY, 0);
				}
			}elseif($run > 0){
				$run--;
				if($y < 62){
					$chunk->setBlock($chunkX, $y, $chunkZ, Block::HARDENED_CLAY, 0);
				}elseif($hasTop){
					$chunk->setBlock($chunkX, $y, $chunkZ, Block::STAINED_CLAY, 1);
				}else{
					$band = $this->clayBands[$y & 63];
					if($band < 16){
						$chunk->setBlock($chunkX, $y, $chunkZ, Block::STAINED_CLAY, $band);
					}else{
						$chunk->setBlock($chunkX, $y, $chunkZ, Block::HARDENED_CLAY, 0);
					}
				}
			}
		}
	}
}
