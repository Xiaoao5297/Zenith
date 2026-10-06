<?php

namespace pocketmine\level\generator\gorigional\structure;

use pocketmine\level\generator\gorigional\JavaRandom;

/**
 * 移植自 Java 1.7.10 StructureMineshaftPieces$Cross。
 */
class MineshaftCrossing extends MineshaftPiece{
	const AIR = 0;
	const PLANKS = 5;

	/** @var int */
	private $facing;
	/** @var bool */
	private $twoFloors;

	public function __construct($typeId, JavaRandom $rnd, BoundingBox $box, $facing){
		parent::__construct($typeId, $box, -1);
		$this->facing = $facing;
		$this->twoFloors = $box->getYSize() > 3;
	}

	public static function getCrossingBoundingBox(array $components, JavaRandom $rnd, $x, $y, $z, $facing){
		$box = new BoundingBox($x, $y, $z, $x, $y + 2, $z);

		if($rnd->nextBoundedInt(4) === 0){
			$box->maxY += 4;
		}

		switch($facing){
			case 0:
				$box->minX = $x - 1;
				$box->maxX = $x + 3;
				$box->maxZ = $z + 4;
				break;
			case 1:
				$box->minX = $x - 4;
				$box->minZ = $z - 1;
				$box->maxZ = $z + 3;
				break;
			case 2:
				$box->minX = $x - 1;
				$box->maxX = $x + 3;
				$box->minZ = $z - 4;
				break;
			case 3:
				$box->maxX = $x + 4;
				$box->minZ = $z - 1;
				$box->maxZ = $z + 3;
				break;
		}

		return self::findIntersecting($components, $box) !== null ? null : $box;
	}

	public function buildComponent(StructureComponent $component = null, array &$components, JavaRandom $rnd){
		$i = $this->componentType;
		$bb = $this->boundingBox;

		switch($this->facing){
			case 0:
				self::getNextMineshaftComponent($component, $components, $rnd, $bb->minX + 1, $bb->minY, $bb->maxZ + 1, 0, $i);
				self::getNextMineshaftComponent($component, $components, $rnd, $bb->minX - 1, $bb->minY, $bb->minZ + 1, 1, $i);
				self::getNextMineshaftComponent($component, $components, $rnd, $bb->maxX + 1, $bb->minY, $bb->minZ + 1, 3, $i);
				break;
			case 1:
				self::getNextMineshaftComponent($component, $components, $rnd, $bb->minX + 1, $bb->minY, $bb->minZ - 1, 2, $i);
				self::getNextMineshaftComponent($component, $components, $rnd, $bb->minX + 1, $bb->minY, $bb->maxZ + 1, 0, $i);
				self::getNextMineshaftComponent($component, $components, $rnd, $bb->minX - 1, $bb->minY, $bb->minZ + 1, 1, $i);
				break;
			case 2:
				self::getNextMineshaftComponent($component, $components, $rnd, $bb->minX + 1, $bb->minY, $bb->minZ - 1, 2, $i);
				self::getNextMineshaftComponent($component, $components, $rnd, $bb->minX - 1, $bb->minY, $bb->minZ + 1, 1, $i);
				self::getNextMineshaftComponent($component, $components, $rnd, $bb->maxX + 1, $bb->minY, $bb->minZ + 1, 3, $i);
				break;
			case 3:
				self::getNextMineshaftComponent($component, $components, $rnd, $bb->minX + 1, $bb->minY, $bb->minZ - 1, 2, $i);
				self::getNextMineshaftComponent($component, $components, $rnd, $bb->minX + 1, $bb->minY, $bb->maxZ + 1, 0, $i);
				self::getNextMineshaftComponent($component, $components, $rnd, $bb->maxX + 1, $bb->minY, $bb->minZ + 1, 3, $i);
				break;
		}

		if($this->twoFloors){
			if($rnd->nextBoolean()){
				self::getNextMineshaftComponent($component, $components, $rnd, $bb->minX + 1, $bb->minY + 3 + 1, $bb->minZ - 1, 2, $i);
			}
			if($rnd->nextBoolean()){
				self::getNextMineshaftComponent($component, $components, $rnd, $bb->minX - 1, $bb->minY + 3 + 1, $bb->minZ + 1, 1, $i);
			}
			if($rnd->nextBoolean()){
				self::getNextMineshaftComponent($component, $components, $rnd, $bb->maxX + 1, $bb->minY + 3 + 1, $bb->minZ + 1, 3, $i);
			}
			if($rnd->nextBoolean()){
				self::getNextMineshaftComponent($component, $components, $rnd, $bb->minX + 1, $bb->minY + 3 + 1, $bb->maxZ + 1, 0, $i);
			}
		}
	}

	public function addComponentParts(WorldAccess $w, JavaRandom $rnd, BoundingBox $box){
		if($this->isLiquidInStructureBoundingBox($w, $box)){
			return false;
		}

		$bb = $this->boundingBox;

		if($this->twoFloors){
			$this->fillWithBlocks($w, $box, $bb->minX + 1, $bb->minY, $bb->minZ, $bb->maxX - 1, $bb->minY + 3 - 1, $bb->maxZ, self::AIR, 0, self::AIR, 0, false);
			$this->fillWithBlocks($w, $box, $bb->minX, $bb->minY, $bb->minZ + 1, $bb->maxX, $bb->minY + 3 - 1, $bb->maxZ - 1, self::AIR, 0, self::AIR, 0, false);
			$this->fillWithBlocks($w, $box, $bb->minX + 1, $bb->maxY - 2, $bb->minZ, $bb->maxX - 1, $bb->maxY, $bb->maxZ, self::AIR, 0, self::AIR, 0, false);
			$this->fillWithBlocks($w, $box, $bb->minX, $bb->maxY - 2, $bb->minZ + 1, $bb->maxX, $bb->maxY, $bb->maxZ - 1, self::AIR, 0, self::AIR, 0, false);
			$this->fillWithBlocks($w, $box, $bb->minX + 1, $bb->minY + 3, $bb->minZ + 1, $bb->maxX - 1, $bb->minY + 3, $bb->maxZ - 1, self::AIR, 0, self::AIR, 0, false);
		}else{
			$this->fillWithBlocks($w, $box, $bb->minX + 1, $bb->minY, $bb->minZ, $bb->maxX - 1, $bb->maxY, $bb->maxZ, self::AIR, 0, self::AIR, 0, false);
			$this->fillWithBlocks($w, $box, $bb->minX, $bb->minY, $bb->minZ + 1, $bb->maxX, $bb->maxY, $bb->maxZ - 1, self::AIR, 0, self::AIR, 0, false);
		}

		$this->fillWithBlocks($w, $box, $bb->minX + 1, $bb->minY, $bb->minZ + 1, $bb->minX + 1, $bb->maxY, $bb->minZ + 1, self::PLANKS, 0, self::AIR, 0, false);
		$this->fillWithBlocks($w, $box, $bb->minX + 1, $bb->minY, $bb->maxZ - 1, $bb->minX + 1, $bb->maxY, $bb->maxZ - 1, self::PLANKS, 0, self::AIR, 0, false);
		$this->fillWithBlocks($w, $box, $bb->maxX - 1, $bb->minY, $bb->minZ + 1, $bb->maxX - 1, $bb->maxY, $bb->minZ + 1, self::PLANKS, 0, self::AIR, 0, false);
		$this->fillWithBlocks($w, $box, $bb->maxX - 1, $bb->minY, $bb->maxZ - 1, $bb->maxX - 1, $bb->maxY, $bb->maxZ - 1, self::PLANKS, 0, self::AIR, 0, false);

		for($i = $bb->minX; $i <= $bb->maxX; $i++){
			for($j = $bb->minZ; $j <= $bb->maxZ; $j++){
				if($this->getBlock($w, $i, $bb->minY - 1, $j, $box) === self::AIR){
					$this->placeBlock($w, self::PLANKS, 0, $i, $bb->minY - 1, $j, $box);
				}
			}
		}

		return true;
	}
}
