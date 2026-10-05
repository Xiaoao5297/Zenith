<?php

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
		foreach($this->components as $c){
			$bb = $c->getBoundingBox();
			if($bb !== null && $bb->intersectsWith($box)){
				$c->addComponentParts($w, $rnd, $box);
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
