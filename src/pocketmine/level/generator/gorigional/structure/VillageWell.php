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
 * 移植自 Java 1.7.10 StructureVillagePieces$Well。
 */
class VillageWell extends VillagePiece{
	const AIR = 0;
	const COBBLESTONE = 4;
	const FLOWING_WATER = 8;
	const GRAVEL = 13;
	const FENCE = 85;

	public function __construct(VillageWellStart $start = null, $typeId = 0, JavaRandom $rnd = null, $x = 0, $z = 0){
		parent::__construct($start, $typeId);
		if($rnd === null){
			return;
		}
		$this->coordBaseMode = $rnd->nextBoundedInt(4);
		$this->boundingBox = new BoundingBox($x, 64, $z, $x + 6 - 1, 78, $z + 6 - 1);
	}

	public function buildComponent(StructureComponent $component = null, array &$components, JavaRandom $rnd){
		$start = $component;
		$bb = $this->boundingBox;
		VillagePieces::addPath($start, $components, $rnd, $bb->minX - 1, $bb->maxY - 4, $bb->minZ + 1, 1, $this->componentType);
		VillagePieces::addPath($start, $components, $rnd, $bb->maxX + 1, $bb->maxY - 4, $bb->minZ + 1, 3, $this->componentType);
		VillagePieces::addPath($start, $components, $rnd, $bb->minX + 1, $bb->maxY - 4, $bb->minZ - 1, 2, $this->componentType);
		VillagePieces::addPath($start, $components, $rnd, $bb->minX + 1, $bb->maxY - 4, $bb->maxZ + 1, 0, $this->componentType);
	}

	public function addComponentParts(WorldAccess $w, JavaRandom $rnd, BoundingBox $box){
		if($this->terrainHeight < 0){
			$this->terrainHeight = $this->getAverageGroundHeight($w, $box);
			if($this->terrainHeight < 0){
				return true;
			}
			$this->boundingBox->offset(0, $this->terrainHeight - $this->boundingBox->maxY + 3, 0);
		}

		$this->fillWithBlocks($w, $box, 1, 0, 1, 4, 12, 4, self::COBBLESTONE, 0, self::FLOWING_WATER, 0, false);
		$this->placeBlock($w, self::AIR, 0, 2, 12, 2, $box);
		$this->placeBlock($w, self::AIR, 0, 3, 12, 2, $box);
		$this->placeBlock($w, self::AIR, 0, 2, 12, 3, $box);
		$this->placeBlock($w, self::AIR, 0, 3, 12, 3, $box);
		$this->placeBlock($w, self::FENCE, 0, 1, 13, 1, $box);
		$this->placeBlock($w, self::FENCE, 0, 1, 14, 1, $box);
		$this->placeBlock($w, self::FENCE, 0, 4, 13, 1, $box);
		$this->placeBlock($w, self::FENCE, 0, 4, 14, 1, $box);
		$this->placeBlock($w, self::FENCE, 0, 1, 13, 4, $box);
		$this->placeBlock($w, self::FENCE, 0, 1, 14, 4, $box);
		$this->placeBlock($w, self::FENCE, 0, 4, 13, 4, $box);
		$this->placeBlock($w, self::FENCE, 0, 4, 14, 4, $box);
		$this->fillWithBlocks($w, $box, 1, 15, 1, 4, 15, 4, self::COBBLESTONE, 0, self::COBBLESTONE, 0, false);

		for($i = 0; $i <= 5; $i++){
			for($j = 0; $j <= 5; $j++){
				if($j === 0 || $j === 5 || $i === 0 || $i === 5){
					$this->placeBlock($w, self::GRAVEL, 0, $j, 11, $i, $box);
					$this->clearCurrentPositionBlocksUpwards($w, $j, 12, $i, $box);
				}
			}
		}

		return true;
	}
}
