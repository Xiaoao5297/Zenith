<?php

namespace pocketmine\level\generator\gorigional\structure;

use pocketmine\level\generator\gorigional\JavaRandom;

/**
 * 移植自 Java 1.7.10 StructureVillagePieces$House3。
 */
class VillageHouse3 extends VillagePiece{
	const AIR = 0;
	const COBBLESTONE = 4;
	const PLANKS = 5;
	const LOG = 17;
	const TORCH = 50;
	const OAK_STAIRS = 53;
	const WOODEN_DOOR = 64;
	const STONE_STAIRS = 67;
	const GLASS_PANE = 102;

	public function __construct(VillageWellStart $start, $typeId, JavaRandom $rnd, BoundingBox $box, $facing){
		parent::__construct($start, $typeId);
		$this->coordBaseMode = $facing;
		$this->boundingBox = $box;
	}

	public static function create(VillageWellStart $start, array $list, JavaRandom $rnd, $i, $j, $k, $l, $i1){
		$box = BoundingBox::getComponentToAddBoundingBox($i, $j, $k, 0, 0, 0, 9, 7, 12, $l);
		return (self::isValidBox($box) && self::findIntersecting($list, $box) === null) ? new self($start, $i1, $rnd, $box, $l) : null;
	}

	public function addComponentParts(WorldAccess $w, JavaRandom $rnd, BoundingBox $box){
		if($this->terrainHeight < 0){
			$this->terrainHeight = $this->getAverageGroundHeight($w, $box);
			if($this->terrainHeight < 0){
				return true;
			}
			$this->boundingBox->offset(0, $this->terrainHeight - $this->boundingBox->maxY + 7 - 1, 0);
		}

		$this->fillWithBlocks($w, $box, 1, 1, 1, 7, 4, 4, self::AIR, 0, self::AIR, 0, false);
		$this->fillWithBlocks($w, $box, 2, 1, 6, 8, 4, 10, self::AIR, 0, self::AIR, 0, false);
		$this->fillWithBlocks($w, $box, 2, 0, 5, 8, 0, 10, self::PLANKS, 0, self::PLANKS, 0, false);
		$this->fillWithBlocks($w, $box, 1, 0, 1, 7, 0, 4, self::PLANKS, 0, self::PLANKS, 0, false);
		$this->fillWithBlocks($w, $box, 0, 0, 0, 0, 3, 5, self::COBBLESTONE, 0, self::COBBLESTONE, 0, false);
		$this->fillWithBlocks($w, $box, 8, 0, 0, 8, 3, 10, self::COBBLESTONE, 0, self::COBBLESTONE, 0, false);
		$this->fillWithBlocks($w, $box, 1, 0, 0, 7, 2, 0, self::COBBLESTONE, 0, self::COBBLESTONE, 0, false);
		$this->fillWithBlocks($w, $box, 1, 0, 5, 2, 1, 5, self::COBBLESTONE, 0, self::COBBLESTONE, 0, false);
		$this->fillWithBlocks($w, $box, 2, 0, 6, 2, 3, 10, self::COBBLESTONE, 0, self::COBBLESTONE, 0, false);
		$this->fillWithBlocks($w, $box, 3, 0, 10, 7, 3, 10, self::COBBLESTONE, 0, self::COBBLESTONE, 0, false);
		$this->fillWithBlocks($w, $box, 1, 2, 0, 7, 3, 0, self::PLANKS, 0, self::PLANKS, 0, false);
		$this->fillWithBlocks($w, $box, 1, 2, 5, 2, 3, 5, self::PLANKS, 0, self::PLANKS, 0, false);
		$this->fillWithBlocks($w, $box, 0, 4, 1, 8, 4, 1, self::PLANKS, 0, self::PLANKS, 0, false);
		$this->fillWithBlocks($w, $box, 0, 4, 4, 3, 4, 4, self::PLANKS, 0, self::PLANKS, 0, false);
		$this->fillWithBlocks($w, $box, 0, 5, 2, 8, 5, 3, self::PLANKS, 0, self::PLANKS, 0, false);
		$this->placeBlock($w, self::PLANKS, 0, 0, 4, 2, $box);
		$this->placeBlock($w, self::PLANKS, 0, 0, 4, 3, $box);
		$this->placeBlock($w, self::PLANKS, 0, 8, 4, 2, $box);
		$this->placeBlock($w, self::PLANKS, 0, 8, 4, 3, $box);
		$this->placeBlock($w, self::PLANKS, 0, 8, 4, 4, $box);

		$i = $this->getMetadataWithOffset(self::OAK_STAIRS, 3);
		$j = $this->getMetadataWithOffset(self::OAK_STAIRS, 2);

		for($l = -1; $l <= 2; $l++){
			for($k = 0; $k <= 8; $k++){
				$this->placeBlock($w, self::OAK_STAIRS, $i, $k, 4 + $l, $l, $box);
				if(($l > -1 || $k <= 1) && ($l > 0 || $k <= 3) && ($l > 1 || $k <= 4 || $k >= 6)){
					$this->placeBlock($w, self::OAK_STAIRS, $j, $k, 4 + $l, 5 - $l, $box);
				}
			}
		}

		$this->fillWithBlocks($w, $box, 3, 4, 5, 3, 4, 10, self::PLANKS, 0, self::PLANKS, 0, false);
		$this->fillWithBlocks($w, $box, 7, 4, 2, 7, 4, 10, self::PLANKS, 0, self::PLANKS, 0, false);
		$this->fillWithBlocks($w, $box, 4, 5, 4, 4, 5, 10, self::PLANKS, 0, self::PLANKS, 0, false);
		$this->fillWithBlocks($w, $box, 6, 5, 4, 6, 5, 10, self::PLANKS, 0, self::PLANKS, 0, false);
		$this->fillWithBlocks($w, $box, 5, 6, 3, 5, 6, 10, self::PLANKS, 0, self::PLANKS, 0, false);
		$l = $this->getMetadataWithOffset(self::OAK_STAIRS, 0);

		for($k = 4; $k >= 1; $k--){
			$this->placeBlock($w, self::PLANKS, 0, $k, 2 + $k, 7 - $k, $box);
			for($i1 = 8 - $k; $i1 <= 10; $i1++){
				$this->placeBlock($w, self::OAK_STAIRS, $l, $k, 2 + $k, $i1, $box);
			}
		}

		$k = $this->getMetadataWithOffset(self::OAK_STAIRS, 1);
		$this->placeBlock($w, self::PLANKS, 0, 6, 6, 3, $box);
		$this->placeBlock($w, self::PLANKS, 0, 7, 5, 4, $box);
		$this->placeBlock($w, self::OAK_STAIRS, $k, 6, 6, 4, $box);

		for($i1 = 6; $i1 <= 8; $i1++){
			for($j1 = 5; $j1 <= 10; $j1++){
				$this->placeBlock($w, self::OAK_STAIRS, $k, $i1, 12 - $i1, $j1, $box);
			}
		}

		$this->placeBlock($w, self::LOG, 0, 0, 2, 1, $box);
		$this->placeBlock($w, self::LOG, 0, 0, 2, 4, $box);
		$this->placeBlock($w, self::GLASS_PANE, 0, 0, 2, 2, $box);
		$this->placeBlock($w, self::GLASS_PANE, 0, 0, 2, 3, $box);
		$this->placeBlock($w, self::LOG, 0, 4, 2, 0, $box);
		$this->placeBlock($w, self::GLASS_PANE, 0, 5, 2, 0, $box);
		$this->placeBlock($w, self::LOG, 0, 6, 2, 0, $box);
		$this->placeBlock($w, self::LOG, 0, 8, 2, 1, $box);
		$this->placeBlock($w, self::GLASS_PANE, 0, 8, 2, 2, $box);
		$this->placeBlock($w, self::GLASS_PANE, 0, 8, 2, 3, $box);
		$this->placeBlock($w, self::LOG, 0, 8, 2, 4, $box);
		$this->placeBlock($w, self::PLANKS, 0, 8, 2, 5, $box);
		$this->placeBlock($w, self::LOG, 0, 8, 2, 6, $box);
		$this->placeBlock($w, self::GLASS_PANE, 0, 8, 2, 7, $box);
		$this->placeBlock($w, self::GLASS_PANE, 0, 8, 2, 8, $box);
		$this->placeBlock($w, self::LOG, 0, 8, 2, 9, $box);
		$this->placeBlock($w, self::LOG, 0, 2, 2, 6, $box);
		$this->placeBlock($w, self::GLASS_PANE, 0, 2, 2, 7, $box);
		$this->placeBlock($w, self::GLASS_PANE, 0, 2, 2, 8, $box);
		$this->placeBlock($w, self::LOG, 0, 2, 2, 9, $box);
		$this->placeBlock($w, self::LOG, 0, 4, 4, 10, $box);
		$this->placeBlock($w, self::GLASS_PANE, 0, 5, 4, 10, $box);
		$this->placeBlock($w, self::LOG, 0, 6, 4, 10, $box);
		$this->placeBlock($w, self::PLANKS, 0, 5, 5, 10, $box);

		$this->placeBlock($w, self::AIR, 0, 2, 1, 0, $box);
		$this->placeBlock($w, self::AIR, 0, 2, 2, 0, $box);
		$this->placeBlock($w, self::TORCH, 0, 2, 3, 1, $box);
		$this->placeDoor($w, $box, $rnd, 2, 1, 0, $this->getMetadataWithOffset(self::WOODEN_DOOR, 1));
		$this->fillWithBlocks($w, $box, 1, 0, -1, 3, 2, -1, self::AIR, 0, self::AIR, 0, false);
		if($this->getBlock($w, 2, 0, -1, $box) === self::AIR && $this->getBlock($w, 2, -1, -1, $box) !== self::AIR){
			$this->placeBlock($w, self::STONE_STAIRS, $this->getMetadataWithOffset(self::STONE_STAIRS, 3), 2, 0, -1, $box);
		}

		for($i1 = 0; $i1 < 5; $i1++){
			for($j1 = 0; $j1 < 9; $j1++){
				$this->clearCurrentPositionBlocksUpwards($w, $j1, 7, $i1, $box);
				$this->replaceAirAndLiquidDownwards($w, self::COBBLESTONE, 0, $j1, -1, $i1, $box);
			}
		}

		for($i1 = 5; $i1 < 11; $i1++){
			for($j1 = 2; $j1 < 9; $j1++){
				$this->clearCurrentPositionBlocksUpwards($w, $j1, 7, $i1, $box);
				$this->replaceAirAndLiquidDownwards($w, self::COBBLESTONE, 0, $j1, -1, $i1, $box);
			}
		}

		return true;
	}
}
