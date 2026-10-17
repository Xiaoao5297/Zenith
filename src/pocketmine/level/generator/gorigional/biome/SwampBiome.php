<?php

namespace pocketmine\level\generator\gorigional\biome;

use pocketmine\block\Block;
use pocketmine\level\format\FullChunk;
use pocketmine\level\generator\gorigional\JavaRandom;
use pocketmine\level\generator\gorigional\McpeRandom;
use pocketmine\level\generator\gorigional\noise\PerlinSimplexGenerator;

/**
 * 移植自 MCPE 0.14.3 SwampBiome::buildSurfaceAt。
 *
 * 在噪声为正的区域，把海平面附近的低洼地表改成静水，并在浅水处放睡莲。
 */
class SwampBiome extends BaseBiome{
	/** @var PerlinSimplexGenerator */
	private $swampNoise;

	public function __construct($id, $name, $baseHeight, $heightVariation, $temperature, $rainfall){
		parent::__construct($id, $name, $baseHeight, $heightVariation, $temperature, $rainfall);
		// 群系级噪声只需要稳定形状，不参与世界种子逐块对齐。
		$this->swampNoise = new PerlinSimplexGenerator(new McpeRandom(0x5A17), 1);
	}

	public function genTerrainBlocks(FullChunk $chunk, JavaRandom $r, $x, $z, $noiseVal){
		$noise = $this->swampNoise->getValue($x * 0.25, $z * 0.25);

		if($noise > 0.0){
			$chunkX = $x & 15;
			$chunkZ = $z & 15;

			for($y = 127; $y > 0; $y--){
				$id = $chunk->getBlockId($chunkX, $y, $chunkZ);
				if($id === Block::AIR || $id === Block::STILL_WATER || $id === Block::WATER){
					continue;
				}

				if($y === 62 && $id !== Block::STILL_WATER){
					$chunk->setBlock($chunkX, 62, $chunkZ, Block::STILL_WATER, 0);
					if($noise < 0.12){
						$chunk->setBlock($chunkX, 63, $chunkZ, Block::WATER_LILY, 0);
					}
				}
				break;
			}
		}

		parent::genTerrainBlocks($chunk, $r, $x, $z, $noiseVal);
	}
}
