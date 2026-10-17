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
 * 移植自 Java 1.7.10 StructureVillagePieces$House4Garden。
 */
class VillageHouse4Garden extends VillagePiece{
	const AIR = 0;
	const COBBLESTONE = 4;
	const PLANKS = 5;
	const LOG = 17;
	const GLASS_PANE = 102;
	const TORCH = 50;
	const OAK_STAIRS = 53;
	const STONE_STAIRS = 67;
	const FENCE = 85;
	const LADDER = 65;

	/** @var bool */
	private $terrace;

	public function __construct(VillageWellStart $start, $typeId, JavaRandom $rnd, BoundingBox $box, $facing){
		parent::__construct($start, $typeId);
		$this->coordBaseMode = $facing;
		$this->boundingBox = $box;
		$this->terrace = $rnd->nextBoolean();
	}

	public static function create(VillageWellStart $start, array $list, JavaRandom $rnd, $i, $j, $k, $l, $i1){
		$box = BoundingBox::getComponentToAddBoundingBox($i, $j, $k, 0, 0, 0, 5, 6, 5, $l);
		return self::findIntersecting($list, $box) !== null ? null : new self($start, $i1, $rnd, $box, $l);
	}

	public function addComponentParts(WorldAccess $w, JavaRandom $rnd, BoundingBox $box){
		if($this->terrainHeight < 0){
			$this->terrainHeight = $this->getAverageGroundHeight($w, $box);
			if($this->terrainHeight < 0){
				return true;
			}
			$this->boundingBox->offset(0, $this->terrainHeight - $this->boundingBox->maxY + 6 - 1, 0);
		}

		$this->fillWithBlocks($w, $box, 0, 0, 0, 4, 0, 4, self::COBBLESTONE, 0, self::COBBLESTONE, 0, false);
		$this->fillWithBlocks($w, $box, 0, 4, 0, 4, 4, 4, self::LOG, 0, self::LOG, 0, false);
		$this->fillWithBlocks($w, $box, 1, 4, 1, 3, 4, 3, self::PLANKS, 0, self::PLANKS, 0, false);

		$this->placeBlock($w, self::COBBLESTONE, 0, 0, 1, 0, $box);
		$this->placeBlock($w, self::COBBLESTONE, 0, 0, 2, 0, $box);
		$this->placeBlock($w, self::COBBLESTONE, 0, 0, 3, 0, $box);
		$this->placeBlock($w, self::COBBLESTONE, 0, 4, 1, 0, $box);
		$this->placeBlock($w, self::COBBLESTONE, 0, 4, 2, 0, $box);
		$this->placeBlock($w, self::COBBLESTONE, 0, 4, 3, 0, $box);
		$this->placeBlock($w, self::COBBLESTONE, 0, 0, 1, 4, $box);
		$this->placeBlock($w, self::COBBLESTONE, 0, 0, 2, 4, $box);
		$this->placeBlock($w, self::COBBLESTONE, 0, 0, 3, 4, $box);
		$this->placeBlock($w, self::COBBLESTONE, 0, 4, 1, 4, $box);
		$this->placeBlock($w, self::COBBLESTONE, 0, 4, 2, 4, $box);
		$this->placeBlock($w, self::COBBLESTONE, 0, 4, 3, 4, $box);

		$this->fillWithBlocks($w, $box, 0, 1, 1, 0, 3, 3, self::PLANKS, 0, self::PLANKS, 0, false);
		$this->fillWithBlocks($w, $box, 4, 1, 1, 4, 3, 3, self::PLANKS, 0, self::PLANKS, 0, false);
		$this->fillWithBlocks($w, $box, 1, 1, 4, 3, 3, 4, self::PLANKS, 0, self::PLANKS, 0, false);

		$this->placeBlock($w, self::GLASS_PANE, 0, 0, 2, 2, $box);
		$this->placeBlock($w, self::GLASS_PANE, 0, 2, 2, 4, $box);
		$this->placeBlock($w, self::GLASS_PANE, 0, 4, 2, 2, $box);

		$this->placeBlock($w, self::PLANKS, 0, 1, 1, 0, $box);
		$this->placeBlock($w, self::PLANKS, 0, 1, 2, 0, $box);
		$this->placeBlock($w, self::PLANKS, 0, 1, 3, 0, $box);
		$this->placeBlock($w, self::PLANKS, 0, 2, 3, 0, $box);
		$this->placeBlock($w, self::PLANKS, 0, 3, 3, 0, $box);
		$this->placeBlock($w, self::PLANKS, 0, 3, 2, 0, $box);
		$this->placeBlock($w, self::PLANKS, 0, 3, 1, 0, $box);

		if($this->getBlock($w, 2, 0, -1, $box) === self::AIR && $this->getBlock($w, 2, -1, -1, $box) !== self::AIR){
			$this->placeBlock($w, self::STONE_STAIRS, $this->getMetadataWithOffset(self::STONE_STAIRS, 3), 2, 0, -1, $box);
		}

		$this->fillWithBlocks($w, $box, 1, 1, 1, 3, 3, 3, self::AIR, 0, self::AIR, 0, false);

		if($this->terrace){
			$this->placeBlock($w, self::FENCE, 0, 0, 5, 0, $box);
			$this->placeBlock($w, self::FENCE, 0, 1, 5, 0, $box);
			$this->placeBlock($w, self::FENCE, 0, 2, 5, 0, $box);
			$this->placeBlock($w, self::FENCE, 0, 3, 5, 0, $box);
			$this->placeBlock($w, self::FENCE, 0, 4, 5, 0, $box);
			$this->placeBlock($w, self::FENCE, 0, 0, 5, 4, $box);
			$this->placeBlock($w, self::FENCE, 0, 1, 5, 4, $box);
			$this->placeBlock($w, self::FENCE, 0, 2, 5, 4, $box);
			$this->placeBlock($w, self::FENCE, 0, 3, 5, 4, $box);
			$this->placeBlock($w, self::FENCE, 0, 4, 5, 4, $box);
			$this->placeBlock($w, self::FENCE, 0, 4, 5, 1, $box);
			$this->placeBlock($w, self::FENCE, 0, 4, 5, 2, $box);
			$this->placeBlock($w, self::FENCE, 0, 4, 5, 3, $box);
			$this->placeBlock($w, self::FENCE, 0, 0, 5, 1, $box);
			$this->placeBlock($w, self::FENCE, 0, 0, 5, 2, $box);
			$this->placeBlock($w, self::FENCE, 0, 0, 5, 3, $box);
		}

		if($this->terrace){
			$i = $this->getMetadataWithOffset(self::LADDER, 3);
			$this->placeBlock($w, self::LADDER, $i, 3, 1, 3, $box);
			$this->placeBlock($w, self::LADDER, $i, 3, 2, 3, $box);
			$this->placeBlock($w, self::LADDER, $i, 3, 3, 3, $box);
			$this->placeBlock($w, self::LADDER, $i, 3, 4, 3, $box);
		}

		$this->placeBlock($w, self::TORCH, 0, 2, 3, 1, $box);

		for($i = 0; $i < 5; $i++){
			for($j = 0; $j < 5; $j++){
				$this->clearCurrentPositionBlocksUpwards($w, $j, 6, $i, $box);
				$this->replaceAirAndLiquidDownwards($w, self::COBBLESTONE, 0, $j, -1, $i, $box);
			}
		}

		return true;
	}
}
