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
 * 移植自 Java 1.7.10 StructureVillagePieces$Church。
 */
class VillageChurch extends VillagePiece{
	const AIR = 0;
	const COBBLESTONE = 4;
	const TORCH = 50;
	const WOODEN_DOOR = 64;
	const LADDER = 65;
	const STONE_STAIRS = 67;
	const GLASS_PANE = 102;

	public function __construct(VillageWellStart $start, $typeId, JavaRandom $rnd, BoundingBox $box, $facing){
		parent::__construct($start, $typeId);
		$this->coordBaseMode = $facing;
		$this->boundingBox = $box;
	}

	public static function create(VillageWellStart $start, array $list, JavaRandom $rnd, $i, $j, $k, $l, $i1){
		$box = BoundingBox::getComponentToAddBoundingBox($i, $j, $k, 0, 0, 0, 5, 12, 9, $l);
		return (self::isValidBox($box) && self::findIntersecting($list, $box) === null) ? new self($start, $i1, $rnd, $box, $l) : null;
	}

	protected function getVillagerProfession($i){
		return 2;
	}

	public function addComponentParts(WorldAccess $w, JavaRandom $rnd, BoundingBox $box){
		if($this->terrainHeight < 0){
			$this->terrainHeight = $this->getAverageGroundHeight($w, $box);
			if($this->terrainHeight < 0){
				return true;
			}
			$this->boundingBox->offset(0, $this->terrainHeight - $this->boundingBox->maxY + 12 - 1, 0);
		}

		$this->fillWithBlocks($w, $box, 1, 1, 1, 3, 3, 7, self::AIR, 0, self::AIR, 0, false);
		$this->fillWithBlocks($w, $box, 1, 5, 1, 3, 9, 3, self::AIR, 0, self::AIR, 0, false);
		$this->fillWithBlocks($w, $box, 1, 0, 0, 3, 0, 8, self::COBBLESTONE, 0, self::COBBLESTONE, 0, false);
		$this->fillWithBlocks($w, $box, 1, 1, 0, 3, 10, 0, self::COBBLESTONE, 0, self::COBBLESTONE, 0, false);
		$this->fillWithBlocks($w, $box, 0, 1, 1, 0, 10, 3, self::COBBLESTONE, 0, self::COBBLESTONE, 0, false);
		$this->fillWithBlocks($w, $box, 4, 1, 1, 4, 10, 3, self::COBBLESTONE, 0, self::COBBLESTONE, 0, false);
		$this->fillWithBlocks($w, $box, 0, 0, 4, 0, 4, 7, self::COBBLESTONE, 0, self::COBBLESTONE, 0, false);
		$this->fillWithBlocks($w, $box, 4, 0, 4, 4, 4, 7, self::COBBLESTONE, 0, self::COBBLESTONE, 0, false);
		$this->fillWithBlocks($w, $box, 1, 1, 8, 3, 4, 8, self::COBBLESTONE, 0, self::COBBLESTONE, 0, false);
		$this->fillWithBlocks($w, $box, 1, 5, 4, 3, 10, 4, self::COBBLESTONE, 0, self::COBBLESTONE, 0, false);
		$this->fillWithBlocks($w, $box, 1, 5, 5, 3, 5, 7, self::COBBLESTONE, 0, self::COBBLESTONE, 0, false);
		$this->fillWithBlocks($w, $box, 0, 9, 0, 4, 9, 4, self::COBBLESTONE, 0, self::COBBLESTONE, 0, false);
		$this->fillWithBlocks($w, $box, 0, 4, 0, 4, 4, 4, self::COBBLESTONE, 0, self::COBBLESTONE, 0, false);
		$this->placeBlock($w, self::COBBLESTONE, 0, 0, 11, 2, $box);
		$this->placeBlock($w, self::COBBLESTONE, 0, 4, 11, 2, $box);
		$this->placeBlock($w, self::COBBLESTONE, 0, 2, 11, 0, $box);
		$this->placeBlock($w, self::COBBLESTONE, 0, 2, 11, 4, $box);
		$this->placeBlock($w, self::COBBLESTONE, 0, 1, 1, 6, $box);
		$this->placeBlock($w, self::COBBLESTONE, 0, 1, 1, 7, $box);
		$this->placeBlock($w, self::COBBLESTONE, 0, 2, 1, 7, $box);
		$this->placeBlock($w, self::COBBLESTONE, 0, 3, 1, 6, $box);
		$this->placeBlock($w, self::COBBLESTONE, 0, 3, 1, 7, $box);
		$this->placeBlock($w, self::STONE_STAIRS, $this->getMetadataWithOffset(self::STONE_STAIRS, 3), 1, 1, 5, $box);
		$this->placeBlock($w, self::STONE_STAIRS, $this->getMetadataWithOffset(self::STONE_STAIRS, 3), 2, 1, 6, $box);
		$this->placeBlock($w, self::STONE_STAIRS, $this->getMetadataWithOffset(self::STONE_STAIRS, 3), 3, 1, 5, $box);
		$this->placeBlock($w, self::STONE_STAIRS, $this->getMetadataWithOffset(self::STONE_STAIRS, 1), 1, 2, 7, $box);
		$this->placeBlock($w, self::STONE_STAIRS, $this->getMetadataWithOffset(self::STONE_STAIRS, 0), 3, 2, 7, $box);
		foreach([[0,2,2],[0,3,2],[4,2,2],[4,3,2],[0,6,2],[0,7,2],[4,6,2],[4,7,2],[2,6,0],[2,7,0],[2,6,4],[2,7,4],[0,3,6],[4,3,6],[2,3,8]] as $p){
			$this->placeBlock($w, self::GLASS_PANE, 0, $p[0], $p[1], $p[2], $box);
		}
		$this->placeBlock($w, self::TORCH, 0, 2, 4, 7, $box);
		$this->placeBlock($w, self::TORCH, 0, 1, 4, 6, $box);
		$this->placeBlock($w, self::TORCH, 0, 3, 4, 6, $box);
		$this->placeBlock($w, self::TORCH, 0, 2, 4, 5, $box);
		$i = $this->getMetadataWithOffset(self::LADDER, 4);
		for($j = 1; $j <= 9; $j++){
			$this->placeBlock($w, self::LADDER, $i, 3, $j, 3, $box);
		}
		$this->placeBlock($w, self::AIR, 0, 2, 1, 0, $box);
		$this->placeBlock($w, self::AIR, 0, 2, 2, 0, $box);
		$this->placeDoor($w, $box, $rnd, 2, 1, 0, $this->getMetadataWithOffset(self::WOODEN_DOOR, 1));
		if($this->getBlock($w, 2, 0, -1, $box) === self::AIR && $this->getBlock($w, 2, -1, -1, $box) !== self::AIR){
			$this->placeBlock($w, self::STONE_STAIRS, $this->getMetadataWithOffset(self::STONE_STAIRS, 3), 2, 0, -1, $box);
		}
		for($j = 0; $j < 9; $j++){
			for($k = 0; $k < 5; $k++){
				$this->clearCurrentPositionBlocksUpwards($w, $k, 12, $j, $box);
				$this->replaceAirAndLiquidDownwards($w, self::COBBLESTONE, 0, $k, -1, $j, $box);
			}
		}

		return true;
	}
}
