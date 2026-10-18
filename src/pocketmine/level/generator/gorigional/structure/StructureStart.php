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


namespace pocketmine\level\generator\gorigional\structure;

use pocketmine\level\generator\gorigional\JavaRandom;

/**
 * 移植自 SCAXE-GO-CE structure.StructureStart。
 */
class StructureStart{
	/** @var int */
	public $chunkX;
	/** @var int */
	public $chunkZ;
	/** @var StructureComponent[] */
	public $components = [];
	/** @var BoundingBox */
	public $boundingBox;

	public function __construct($chunkX, $chunkZ){
		$this->chunkX = $chunkX;
		$this->chunkZ = $chunkZ;
		$this->boundingBox = new BoundingBox(0, 0, 0, 0, 0, 0);
	}

	public function generateStructure(WorldAccess $w, JavaRandom $rnd, BoundingBox $box){
		foreach($this->components as $index => $c){
			$bb = $c->getBoundingBox();
			if($bb !== null && $bb->intersectsWith($box)){
				if(!$c->addComponentParts($w, $rnd, $box)){
					unset($this->components[$index]);
				}
			}
		}
	}

	/**
	 * 对应 Java StructureStart.func_75067_a：把结构整体下移到指定高度附近。
	 */
	public function placeAtHeight(World $world, JavaRandom $random, $topOffset){
		$j = 63 - $topOffset;
		$k = ($this->boundingBox->maxY - $this->boundingBox->minY + 1) + 1;

		if($k < $j){
			$k += $random->nextBoundedInt($j - $k);
		}

		$l = $k - $this->boundingBox->minY;
		$this->boundingBox->offset(0, $l, 0);

		foreach($this->components as $c){
			$bb = $c->getBoundingBox();
			if($bb !== null){
				$bb->offset(0, $l, 0);
			}
		}
	}

	public function updateBoundingBox(){
		if(empty($this->components)){
			return;
		}
		$minX = 1000000; $minY = 1000000; $minZ = 1000000;
		$maxX = -1000000; $maxY = -1000000; $maxZ = -1000000;

		foreach($this->components as $c){
			$bb = $c->getBoundingBox();
			if($bb->minX < $minX){ $minX = $bb->minX; }
			if($bb->minY < $minY){ $minY = $bb->minY; }
			if($bb->minZ < $minZ){ $minZ = $bb->minZ; }
			if($bb->maxX > $maxX){ $maxX = $bb->maxX; }
			if($bb->maxY > $maxY){ $maxY = $bb->maxY; }
			if($bb->maxZ > $maxZ){ $maxZ = $bb->maxZ; }
		}
		$this->boundingBox = new BoundingBox($minX, $minY, $minZ, $maxX, $maxY, $maxZ);
	}
}
