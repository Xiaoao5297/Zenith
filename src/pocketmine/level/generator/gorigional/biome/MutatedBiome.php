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
	/** @var MesaBiome|null 变异台地使用独立实例（Java BiomeGenMesa.func_150566_k） */
	private $terrain = null;
	/** @var float */
	public $temperature;
	/** @var float */
	public $rainfall;

	public function __construct(Biome $parent, $minElevation = null, $maxElevation = null, $decorateParent = false){
		$this->parent = $parent;
		$this->id = $parent->getID() + 128;
		$this->minElevation = $minElevation !== null ? $minElevation : $parent->getMinElevation() + 0.1;
		$this->maxElevation = $maxElevation !== null ? $maxElevation : $parent->getMaxElevation() + 0.2;
		$this->decorateParent = $decorateParent;
		$this->temperature = $parent->temperature;
		$this->rainfall = $parent->rainfall;

		// Java 的台地变种不是委托父群系，而是新建 BiomeGenMesa 并带上自己的
		// field_150626_aH / field_150620_aI 标记。
		if($parent instanceof MesaBiome){
			$this->terrain = $parent->createMutated($this->id, $this->minElevation, $this->maxElevation);
		}
	}

	public function getID(){
		return $this->id;
	}

	public function getMinElevation(){
		if($this->terrain !== null){
			return $this->terrain->getMinElevation();
		}
		return $this->minElevation;
	}

	public function getMaxElevation(){
		if($this->terrain !== null){
			return $this->terrain->getMaxElevation();
		}
		return $this->maxElevation;
	}

	public function getColor(){
		return $this->parent->getColor();
	}

	public function getTopBlock(){
		return $this->terrain !== null ? $this->terrain->getTopBlock() : $this->parent->getTopBlock();
	}

	public function getTopMeta(){
		return $this->terrain !== null ? $this->terrain->getTopMeta() : $this->parent->getTopMeta();
	}

	public function genTerrainBlocks(FullChunk $chunk, JavaRandom $r, $x, $z, $noiseVal){
		if($this->terrain !== null){
			$this->terrain->genTerrainBlocks($chunk, $r, $x, $z, $noiseVal);
		}else{
			$this->parent->genTerrainBlocks($chunk, $r, $x, $z, $noiseVal);
		}
	}

	public function refreshMesa($seed){
		if($this->terrain instanceof MesaBiome){
			$this->terrain->refreshBands($seed);
		}
	}

	public function getTemperatureAt($x, $y, $z){
		return $this->parent->getTemperatureAt($x, $y, $z);
	}

	public function decorate(ObjectChunkManager $level, JavaRandom $r, BlockPos $pos){
		if($this->parent instanceof BaseBiome && $this->parent->getID() === 4){
			$this->parent->flowerForestDecorate($level, $r, $pos);
			return;
		}
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
		if($this->parent instanceof BaseBiome && $this->parent->getID() === 4){
			return $this->parent->getFlowerForestType($r, $pos);
		}
		return $this->parent->getFlowerType($r, $pos);
	}

	public function getDecorator(){
		return $this->parent->getDecorator();
	}
}
