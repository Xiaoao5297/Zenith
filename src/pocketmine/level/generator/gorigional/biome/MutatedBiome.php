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

use pocketmine\level\format\FullChunk;
use pocketmine\level\generator\gorigional\JavaRandom;
use pocketmine\level\generator\gorigional\object\BlockPos;
use pocketmine\level\generator\gorigional\object\ObjectChunkManager;

/**
 * 移植自 Java 1.7.10 BiomeGenMutated。
 * 变种群系（id = 父群系 + 128）：默认高度 = 父群系 +0.1/+0.2，
 * 地表与装饰委托父群系（genTerrainBlocks 走父群系；decorate 用父群系的 decorator，biome 传自身）。
 */
class MutatedBiome implements Biome{
	/** @var Biome */
	private $parent;
	/** @var int */
	private $id;
	/** @var float */
	private $minElevation;
	/** @var float */
	private $maxElevation;
	/** @var bool */
	private $decorateParent;

	public function __construct(Biome $parent, $minElevation = null, $maxElevation = null, $decorateParent = false){
		$this->parent = $parent;
		$this->id = $parent->getID() + 128;
		$this->minElevation = $minElevation !== null ? $minElevation : $parent->getMinElevation() + 0.1;
		$this->maxElevation = $maxElevation !== null ? $maxElevation : $parent->getMaxElevation() + 0.2;
		$this->decorateParent = $decorateParent;
	}

	public function getID(){
		return $this->id;
	}

	public function getMinElevation(){
		return $this->minElevation;
	}

	public function getMaxElevation(){
		return $this->maxElevation;
	}

	public function getColor(){
		return $this->parent->getColor();
	}

	public function genTerrainBlocks(FullChunk $chunk, JavaRandom $r, $x, $z, $noiseVal){
		$this->parent->genTerrainBlocks($chunk, $r, $x, $z, $noiseVal);
	}

	public function decorate(ObjectChunkManager $level, JavaRandom $r, BlockPos $pos){
		if($this->decorateParent){
			$this->parent->decorate($level, $r, $pos);
		}else{
			$this->parent->getDecorator()->decorate($level, $r, $this, $pos);
		}
	}

	public function getTreeFeature(JavaRandom $r){
		return $this->parent->getTreeFeature($r);
	}

	public function getFlowerType(JavaRandom $r, BlockPos $pos){
		return $this->parent->getFlowerType($r, $pos);
	}

	public function getDecorator(){
		return $this->parent->getDecorator();
	}
}
