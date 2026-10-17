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
use pocketmine\level\generator\gorigional\JavaRandom;

/**
 * 移植自 MCPE 0.14.3 TaigaBiome::buildSurfaceAt。
 *
 * type:
 * - 0: 普通针叶林；
 * - 1: 冷针叶林，使用灰化土；
 * - 2: Mega Taiga，使用灰化土/粗泥。
 */
class TaigaBiome extends BaseBiome{
	/** @var int */
	private $type;

	public function __construct($id, $name, $baseHeight, $heightVariation, $temperature, $rainfall, $type = 0){
		parent::__construct($id, $name, $baseHeight, $heightVariation, $temperature, $rainfall);
		$this->type = $type;
	}

	public function genTerrainBlocks(FullChunk $chunk, JavaRandom $r, $x, $z, $noiseVal){
		if($this->type < 1){
			parent::genTerrainBlocks($chunk, $r, $x, $z, $noiseVal);
			return;
		}

		$oldTop = $this->topBlock;
		$oldMeta = $this->topMeta;
		$oldFiller = $this->fillerBlock;

		if($noiseVal > 1.75){
			$this->topBlock = Block::DIRT;
			$this->topMeta = 1;
			$this->fillerBlock = Block::DIRT;
		}elseif($noiseVal > -0.95){
			$this->topBlock = Block::PODZOL;
			$this->topMeta = 0;
			$this->fillerBlock = Block::DIRT;
		}else{
			$this->topBlock = Block::GRASS;
			$this->topMeta = 0;
			$this->fillerBlock = Block::DIRT;
		}

		parent::genTerrainBlocks($chunk, $r, $x, $z, $noiseVal);

		$this->topBlock = $oldTop;
		$this->topMeta = $oldMeta;
		$this->fillerBlock = $oldFiller;
	}
}
