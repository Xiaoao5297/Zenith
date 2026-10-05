<?php

namespace pocketmine\level\generator\gorigional\biome;

use pocketmine\block\Block;
use pocketmine\level\format\FullChunk;
use pocketmine\level\generator\gorigional\JavaRandom;

/**
 * 移植自 SCAXE-GO-CE pkg/level/generator/biome/mesa.go。
 */
class MesaBiome extends BaseBiome{
	/** @var int[] */
	private $clayBands = [];

	public function __construct($id, $name, $baseHeight, $heightVariation){
		parent::__construct($id, $name, $baseHeight, $heightVariation, 2.0, 0.0);
		$this->initClayBands();
	}

	private function initClayBands(){
		$this->initClayBandsWithSeed(12345);
	}

	private function initClayBandsWithSeed($seed){
		$this->clayBands = array_fill(0, 64, 0);
		$r = new JavaRandom($seed);

		$l = 0;
		while($l < 64){
			$l += $r->nextBoundedInt(5) + 1;
			if($l < 64){
				$this->clayBands[$l] = 1;
			}
		}

		$yellowCount = $r->nextBoundedInt(4) + 2;
		for($i = 0; $i < $yellowCount; $i++){
			$width = $r->nextBoundedInt(3) + 1;
			$start = $r->nextBoundedInt(64);
			for($j = 0; $j < $width && $start + $j < 64; $j++){
				$this->clayBands[$start + $j] = 4;
			}
		}

		$brownCount = $r->nextBoundedInt(4) + 2;
		for($i = 0; $i < $brownCount; $i++){
			$width = $r->nextBoundedInt(3) + 2;
			$start = $r->nextBoundedInt(64);
			for($j = 0; $j < $width && $start + $j < 64; $j++){
				$this->clayBands[$start + $j] = 12;
			}
		}

		$redCount = $r->nextBoundedInt(4) + 2;
		for($i = 0; $i < $redCount; $i++){
			$width = $r->nextBoundedInt(3) + 1;
			$start = $r->nextBoundedInt(64);
			for($j = 0; $j < $width && $start + $j < 64; $j++){
				$this->clayBands[$start + $j] = 14;
			}
		}

		$whiteCount = $r->nextBoundedInt(3) + 3;
		$pos = 0;
		for($i = 0; $i < $whiteCount; $i++){
			$pos += $r->nextBoundedInt(16) + 4;
			if($pos >= 64){
				break;
			}
			$this->clayBands[$pos] = 0;
			$this->clayBands[$pos] = 8;

			if($pos > 0 && $r->nextBoundedInt(2) === 0){
				$this->clayBands[$pos - 1] = 7;
			}
			if($pos < 63 && $r->nextBoundedInt(2) === 0){
				$this->clayBands[$pos + 1] = 7;
			}
		}
	}

	public function genTerrainBlocks(FullChunk $chunk, JavaRandom $r, $x, $z, $noiseVal){
		$seaLevel = 63;
		$chunkX = $x & 15;
		$chunkZ = $z & 15;

		$stone = Block::STONE;
		$air = Block::AIR;

		$run = -1;

		for($y = 255; $y >= 0; $y--){
			$id = $chunk->getBlockId($chunkX, $y, $chunkZ);

			if($id === $air){
				$run = -1;
			}elseif($id === $stone){
				if($run === -1){
					$run = 3 + (int) abs($noiseVal);

					if($y >= $seaLevel - 1){
						if($y >= $seaLevel + 2 + (int) ($noiseVal * 3)){
							$chunk->setBlock($chunkX, $y, $chunkZ, Block::SAND, 1);
						}else{
							$chunk->setBlock($chunkX, $y, $chunkZ, Block::HARDENED_CLAY, 0);
						}
					}else{
						$chunk->setBlock($chunkX, $y, $chunkZ, Block::STAINED_CLAY, 1);
					}
				}elseif($run > 0){
					$run--;

					$clayBandIndex = $y % 64;
					$clayMeta = $this->clayBands[$clayBandIndex];
					$chunk->setBlock($chunkX, $y, $chunkZ, Block::STAINED_CLAY, $clayMeta);
				}
			}
		}
	}
}
