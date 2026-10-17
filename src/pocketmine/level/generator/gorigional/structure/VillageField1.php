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
 * 移植自 Java 1.7.10 StructureVillagePieces$Field1。
 */
class VillageField1 extends VillagePiece{
	const AIR = 0;
	const DIRT = 3;
	const WATER = 9;
	const LOG = 17;
	const WHEAT = 59;
	const FARMLAND = 60;
	const CARROTS = 141;
	const POTATOES = 142;

	/** @var int */
	private $cropA;
	/** @var int */
	private $cropB;
	/** @var int */
	private $cropC;
	/** @var int */
	private $cropD;

	public function __construct(VillageWellStart $start, $typeId, JavaRandom $rnd, BoundingBox $box, $facing){
		parent::__construct($start, $typeId);
		$this->coordBaseMode = $facing;
		$this->boundingBox = $box;
		$this->cropA = $this->randomCrop($rnd);
		$this->cropB = $this->randomCrop($rnd);
		$this->cropC = $this->randomCrop($rnd);
		$this->cropD = $this->randomCrop($rnd);
	}

	private function randomCrop(JavaRandom $rnd){
		switch($rnd->nextBoundedInt(5)){
			case 0:
				return self::CARROTS;
			case 1:
				return self::POTATOES;
			default:
				return self::WHEAT;
		}
	}

	public static function create(VillageWellStart $start, array $list, JavaRandom $rnd, $i, $j, $k, $l, $i1){
		$box = BoundingBox::getComponentToAddBoundingBox($i, $j, $k, 0, 0, 0, 13, 4, 9, $l);
		return (self::isValidBox($box) && self::findIntersecting($list, $box) === null) ? new self($start, $i1, $rnd, $box, $l) : null;
	}

	public function addComponentParts(WorldAccess $w, JavaRandom $rnd, BoundingBox $box){
		if($this->terrainHeight < 0){
			$this->terrainHeight = $this->getAverageGroundHeight($w, $box);
			if($this->terrainHeight < 0){
				return true;
			}
			$this->boundingBox->offset(0, $this->terrainHeight - $this->boundingBox->maxY + 4 - 1, 0);
		}

		$this->fillWithBlocks($w, $box, 0, 1, 0, 12, 4, 8, self::AIR, 0, self::AIR, 0, false);
		$this->fillWithBlocks($w, $box, 1, 0, 1, 2, 0, 7, self::FARMLAND, 0, self::FARMLAND, 0, false);
		$this->fillWithBlocks($w, $box, 4, 0, 1, 5, 0, 7, self::FARMLAND, 0, self::FARMLAND, 0, false);
		$this->fillWithBlocks($w, $box, 7, 0, 1, 8, 0, 7, self::FARMLAND, 0, self::FARMLAND, 0, false);
		$this->fillWithBlocks($w, $box, 10, 0, 1, 11, 0, 7, self::FARMLAND, 0, self::FARMLAND, 0, false);
		$this->fillWithBlocks($w, $box, 0, 0, 0, 0, 0, 8, self::LOG, 0, self::LOG, 0, false);
		$this->fillWithBlocks($w, $box, 6, 0, 0, 6, 0, 8, self::LOG, 0, self::LOG, 0, false);
		$this->fillWithBlocks($w, $box, 12, 0, 0, 12, 0, 8, self::LOG, 0, self::LOG, 0, false);
		$this->fillWithBlocks($w, $box, 1, 0, 0, 11, 0, 0, self::LOG, 0, self::LOG, 0, false);
		$this->fillWithBlocks($w, $box, 1, 0, 8, 11, 0, 8, self::LOG, 0, self::LOG, 0, false);
		$this->fillWithBlocks($w, $box, 3, 0, 1, 3, 0, 7, self::WATER, 0, self::WATER, 0, false);
		$this->fillWithBlocks($w, $box, 9, 0, 1, 9, 0, 7, self::WATER, 0, self::WATER, 0, false);

		for($i = 1; $i <= 7; $i++){
			$this->placeBlock($w, $this->cropA, $rnd->nextRange(2, 7), 1, 1, $i, $box);
			$this->placeBlock($w, $this->cropA, $rnd->nextRange(2, 7), 2, 1, $i, $box);
			$this->placeBlock($w, $this->cropB, $rnd->nextRange(2, 7), 4, 1, $i, $box);
			$this->placeBlock($w, $this->cropB, $rnd->nextRange(2, 7), 5, 1, $i, $box);
			$this->placeBlock($w, $this->cropC, $rnd->nextRange(2, 7), 7, 1, $i, $box);
			$this->placeBlock($w, $this->cropC, $rnd->nextRange(2, 7), 8, 1, $i, $box);
			$this->placeBlock($w, $this->cropD, $rnd->nextRange(2, 7), 10, 1, $i, $box);
			$this->placeBlock($w, $this->cropD, $rnd->nextRange(2, 7), 11, 1, $i, $box);
		}

		for($i = 0; $i < 9; $i++){
			for($j = 0; $j < 13; $j++){
				$this->clearCurrentPositionBlocksUpwards($w, $j, 4, $i, $box);
				$this->replaceAirAndLiquidDownwards($w, self::DIRT, 0, $j, -1, $i, $box);
			}
		}

		return true;
	}
}
