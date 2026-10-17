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
 * 移植自 Java 1.7.10 StructureVillagePieces$House2。
 */
class VillageHouse2 extends VillagePiece{
	const AIR = 0;
	const COBBLESTONE = 4;
	const PLANKS = 5;
	const FLOWING_LAVA = 10;
	const LOG = 17;
	const DOUBLE_STONE_SLAB = 43;
	const STONE_SLAB = 44;
	const TORCH = 50;
	const OAK_STAIRS = 53;
	const FURNACE = 61;
	const STONE_STAIRS = 67;
	const WOODEN_PRESSURE_PLATE = 72;
	const FENCE = 85;
	const IRON_BARS = 101;
	const GLASS_PANE = 102;

	/** @var bool */
	private $hasChest = false;

	public function __construct(VillageWellStart $start, $typeId, JavaRandom $rnd, BoundingBox $box, $facing){
		parent::__construct($start, $typeId);
		$this->coordBaseMode = $facing;
		$this->boundingBox = $box;
	}

	public static function create(VillageWellStart $start, array $list, JavaRandom $rnd, $i, $j, $k, $l, $i1){
		$box = BoundingBox::getComponentToAddBoundingBox($i, $j, $k, 0, 0, 0, 10, 6, 7, $l);
		return (self::isValidBox($box) && self::findIntersecting($list, $box) === null) ? new self($start, $i1, $rnd, $box, $l) : null;
	}

	protected function getVillagerProfession($i){
		return 3;
	}

	public function addComponentParts(WorldAccess $w, JavaRandom $rnd, BoundingBox $box){
		if($this->terrainHeight < 0){
			$this->terrainHeight = $this->getAverageGroundHeight($w, $box);
			if($this->terrainHeight < 0){
				return true;
			}
			$this->boundingBox->offset(0, $this->terrainHeight - $this->boundingBox->maxY + 6 - 1, 0);
		}

		$this->fillWithBlocks($w, $box, 0, 1, 0, 9, 4, 6, self::AIR, 0, self::AIR, 0, false);
		$this->fillWithBlocks($w, $box, 0, 0, 0, 9, 0, 6, self::COBBLESTONE, 0, self::COBBLESTONE, 0, false);
		$this->fillWithBlocks($w, $box, 0, 4, 0, 9, 4, 6, self::COBBLESTONE, 0, self::COBBLESTONE, 0, false);
		$this->fillWithBlocks($w, $box, 0, 5, 0, 9, 5, 6, self::STONE_SLAB, 0, self::STONE_SLAB, 0, false);
		$this->fillWithBlocks($w, $box, 1, 5, 1, 8, 5, 5, self::AIR, 0, self::AIR, 0, false);
		$this->fillWithBlocks($w, $box, 1, 1, 0, 2, 3, 0, self::PLANKS, 0, self::PLANKS, 0, false);
		$this->fillWithBlocks($w, $box, 0, 1, 0, 0, 4, 0, self::LOG, 0, self::LOG, 0, false);
		$this->fillWithBlocks($w, $box, 3, 1, 0, 3, 4, 0, self::LOG, 0, self::LOG, 0, false);
		$this->fillWithBlocks($w, $box, 0, 1, 6, 0, 4, 6, self::LOG, 0, self::LOG, 0, false);
		$this->placeBlock($w, self::PLANKS, 0, 3, 3, 1, $box);
		$this->fillWithBlocks($w, $box, 3, 1, 2, 3, 3, 2, self::PLANKS, 0, self::PLANKS, 0, false);
		$this->fillWithBlocks($w, $box, 4, 1, 3, 5, 3, 3, self::PLANKS, 0, self::PLANKS, 0, false);
		$this->fillWithBlocks($w, $box, 0, 1, 1, 0, 3, 5, self::PLANKS, 0, self::PLANKS, 0, false);
		$this->fillWithBlocks($w, $box, 1, 1, 6, 5, 3, 6, self::PLANKS, 0, self::PLANKS, 0, false);
		$this->fillWithBlocks($w, $box, 5, 1, 0, 5, 3, 0, self::FENCE, 0, self::FENCE, 0, false);
		$this->fillWithBlocks($w, $box, 9, 1, 0, 9, 3, 0, self::FENCE, 0, self::FENCE, 0, false);
		$this->fillWithBlocks($w, $box, 6, 1, 4, 9, 4, 6, self::COBBLESTONE, 0, self::COBBLESTONE, 0, false);
		$this->placeBlock($w, self::FLOWING_LAVA, 0, 7, 1, 5, $box);
		$this->placeBlock($w, self::FLOWING_LAVA, 0, 8, 1, 5, $box);
		$this->placeBlock($w, self::IRON_BARS, 0, 9, 2, 5, $box);
		$this->placeBlock($w, self::IRON_BARS, 0, 9, 2, 4, $box);
		$this->fillWithBlocks($w, $box, 7, 2, 4, 8, 2, 5, self::AIR, 0, self::AIR, 0, false);
		$this->placeBlock($w, self::COBBLESTONE, 0, 6, 1, 3, $box);
		$this->placeBlock($w, self::FURNACE, 0, 6, 2, 3, $box);
		$this->placeBlock($w, self::FURNACE, 0, 6, 3, 3, $box);
		$this->placeBlock($w, self::DOUBLE_STONE_SLAB, 0, 8, 1, 1, $box);
		$this->placeBlock($w, self::GLASS_PANE, 0, 0, 2, 2, $box);
		$this->placeBlock($w, self::GLASS_PANE, 0, 0, 2, 4, $box);
		$this->placeBlock($w, self::GLASS_PANE, 0, 2, 2, 6, $box);
		$this->placeBlock($w, self::GLASS_PANE, 0, 4, 2, 6, $box);
		$this->placeBlock($w, self::FENCE, 0, 2, 1, 4, $box);
		$this->placeBlock($w, self::WOODEN_PRESSURE_PLATE, 0, 2, 2, 4, $box);
		$this->placeBlock($w, self::PLANKS, 0, 1, 1, 5, $box);
		$this->placeBlock($w, self::OAK_STAIRS, $this->getMetadataWithOffset(self::OAK_STAIRS, 3), 2, 1, 5, $box);
		$this->placeBlock($w, self::OAK_STAIRS, $this->getMetadataWithOffset(self::OAK_STAIRS, 1), 1, 1, 4, $box);

		if(!$this->hasChest){
			$i = $this->getYWithOffset(1);
			$j = $this->getXWithOffset(5, 5);
			$k = $this->getZWithOffset(5, 5);
			if($box->resultIsInside($j, $i, $k)){
				$this->hasChest = true;
				$this->generateChest($w, $box, $rnd, 5, 1, 5);
			}
		}

		for($i = 6; $i <= 8; $i++){
			if($this->getBlock($w, $i, 0, -1, $box) === self::AIR && $this->getBlock($w, $i, -1, -1, $box) !== self::AIR){
				$this->placeBlock($w, self::STONE_STAIRS, $this->getMetadataWithOffset(self::STONE_STAIRS, 3), $i, 0, -1, $box);
			}
		}

		for($i = 0; $i < 7; $i++){
			for($j = 0; $j < 10; $j++){
				$this->clearCurrentPositionBlocksUpwards($w, $j, 6, $i, $box);
				$this->replaceAirAndLiquidDownwards($w, self::COBBLESTONE, 0, $j, -1, $i, $box);
			}
		}

		return true;
	}
}
