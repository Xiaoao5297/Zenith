<?php

namespace pocketmine\level\generator\gorigional\biome;

use pocketmine\block\Block;
use pocketmine\level\format\FullChunk;
use pocketmine\level\generator\gorigional\JavaRandom;

/**
 * 移植自 MCPE 0.14.3 ExtremeHillsBiome::buildSurfaceAt。
 *
 * type:
 * - 0: 普通极端丘陵，noise > 1.0 时山顶变为石头；
 * - 1: Extreme Hills Edge / Extreme Hills+，始终使用草/土；
 * - 2: 变种极端丘陵，noise 在 -0.1..0.2 之外时使用砂砾。
 */
class ExtremeHillsBiome extends BaseBiome{
	/** @var int */
	private $type;

	public function __construct($id, $name, $baseHeight, $heightVariation, $temperature, $rainfall, $type){
		parent::__construct($id, $name, $baseHeight, $heightVariation, $temperature, $rainfall);
		$this->type = $type;
	}

	public function genTerrainBlocks(FullChunk $chunk, JavaRandom $r, $x, $z, $noiseVal){
		$this->topBlock = Block::GRASS;
		$this->topMeta = 0;
		$this->fillerBlock = Block::DIRT;

		if($this->type === 0 && $noiseVal > 1.0){
			$this->topBlock = Block::STONE;
			$this->fillerBlock = Block::STONE;
		}elseif($this->type === 2 && ($noiseVal < -0.1 || $noiseVal > 0.2)){
			$this->topBlock = Block::GRAVEL;
			$this->fillerBlock = Block::GRAVEL;
		}

		parent::genTerrainBlocks($chunk, $r, $x, $z, $noiseVal);
	}
}
