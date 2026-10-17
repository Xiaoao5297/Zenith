<?php

namespace pocketmine\level\generator\gorigional\structure;

use pocketmine\level\generator\gorigional\JavaRandom;

/**
 * 移植自 Java 1.7.10 StructureVillagePieces$Hall。
 */
class VillageHall extends VillagePiece{
	const AIR = 0;
	const DIRT = 3;
	const COBBLESTONE = 4;
	const PLANKS = 5;
	const LOG = 17;
	const DOUBLE_STONE_SLAB = 43;
	const TORCH = 50;
	const OAK_STAIRS = 53;
	const WOODEN_DOOR = 64;
	const STONE_STAIRS = 67;
	const WOODEN_PRESSURE_PLATE = 72;
	const FENCE = 85;
	const GLASS_PANE = 102;

	public function __construct(VillageWellStart $start, $typeId, JavaRandom $rnd, BoundingBox $box, $facing){
		parent::__construct($start, $typeId);
		$this->coordBaseMode = $facing;
		$this->boundingBox = $box;
	}

	public static function create(VillageWellStart $start, array $list, JavaRandom $rnd, $i, $j, $k, $l, $i1){
		$box = BoundingBox::getComponentToAddBoundingBox($i, $j, $k, 0, 0, 0, 9, 7, 11, $l);
		return (self::isValidBox($box) && self::findIntersecting($list, $box) === null) ? new self($start, $i1, $rnd, $box, $l) : null;
	}

	protected function getVillagerProfession($i){
		return $i === 0 ? 4 : 0;
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
		$this->fillWithBlocks($w, $box, 2, 0, 6, 8, 0, 10, self::DIRT, 0, self::DIRT, 0, false);
		$this->placeBlock($w, self::COBBLESTONE, 0, 6, 0, 6, $box);
		$this->fillWithBlocks($w, $box, 2, 1, 6, 2, 1, 10, self::FENCE, 0, self::FENCE, 0, false);
		$this->fillWithBlocks($w, $box, 8, 1, 6, 8, 1, 10, self::FENCE, 0, self::FENCE, 0, false);
		$this->fillWithBlocks($w, $box, 3, 1, 10, 7, 1, 10, self::FENCE, 0, self::FENCE, 0, false);
		$this->fillWithBlocks($w, $box, 1, 0, 1, 7, 0, 4, self::PLANKS, 0, self::PLANKS, 0, false);
		$this->fillWithBlocks($w, $box, 0, 0, 0, 0, 3, 5, self::COBBLESTONE, 0, self::COBBLESTONE, 0, false);
		$this->fillWithBlocks($w, $box, 8, 0, 0, 8, 3, 5, self::COBBLESTONE, 0, self::COBBLESTONE, 0, false);
		$this->fillWithBlocks($w, $box, 1, 0, 0, 7, 1, 0, self::COBBLESTONE, 0, self::COBBLESTONE, 0, false);
		$this->fillWithBlocks($w, $box, 1, 0, 5, 7, 1, 5, self::COBBLESTONE, 0, self::COBBLESTONE, 0, false);
		$this->fillWithBlocks($w, $box, 1, 2, 0, 7, 3, 0, self::PLANKS, 0, self::PLANKS, 0, false);
		$this->fillWithBlocks($w, $box, 1, 2, 5, 7, 3, 5, self::PLANKS, 0, self::PLANKS, 0, false);
		$this->fillWithBlocks($w, $box, 0, 4, 1, 8, 4, 1, self::PLANKS, 0, self::PLANKS, 0, false);
		$this->fillWithBlocks($w, $box, 0, 4, 4, 8, 4, 4, self::PLANKS, 0, self::PLANKS, 0, false);
		$this->fillWithBlocks($w, $box, 0, 5, 2, 8, 5, 3, self::PLANKS, 0, self::PLANKS, 0, false);
		$this->placeBlock($w, self::PLANKS, 0, 0, 4, 2, $box);
		$this->placeBlock($w, self::PLANKS, 0, 0, 4, 3, $box);
		$this->placeBlock($w, self::PLANKS, 0, 8, 4, 2, $box);
		$this->placeBlock($w, self::PLANKS, 0, 8, 4, 3, $box);

		$i = $this->getMetadataWithOffset(self::OAK_STAIRS, 3);
		$j = $this->getMetadataWithOffset(self::OAK_STAIRS, 2);

		for($l = -1; $l <= 2; $l++){
			for($k = 0; $k <= 8; $k++){
				$this->placeBlock($w, self::OAK_STAIRS, $i, $k, 4 + $l, $l, $box);
				$this->placeBlock($w, self::OAK_STAIRS, $j, $k, 4 + $l, 5 - $l, $box);
			}
		}

		$this->placeBlock($w, self::LOG, 0, 0, 2, 1, $box);
		$this->placeBlock($w, self::LOG, 0, 0, 2, 4, $box);
		$this->placeBlock($w, self::LOG, 0, 8, 2, 1, $box);
		$this->placeBlock($w, self::LOG, 0, 8, 2, 4, $box);
		$this->placeBlock($w, self::GLASS_PANE, 0, 0, 2, 2, $box);
		$this->placeBlock($w, self::GLASS_PANE, 0, 0, 2, 3, $box);
		$this->placeBlock($w, self::GLASS_PANE, 0, 8, 2, 2, $box);
		$this->placeBlock($w, self::GLASS_PANE, 0, 8, 2, 3, $box);
		$this->placeBlock($w, self::GLASS_PANE, 0, 2, 2, 5, $box);
		$this->placeBlock($w, self::GLASS_PANE, 0, 3, 2, 5, $box);
		$this->placeBlock($w, self::GLASS_PANE, 0, 5, 2, 0, $box);
		$this->placeBlock($w, self::GLASS_PANE, 0, 6, 2, 5, $box);
		$this->placeBlock($w, self::FENCE, 0, 2, 1, 3, $box);
		$this->placeBlock($w, self::WOODEN_PRESSURE_PLATE, 0, 2, 2, 3, $box);
		$this->placeBlock($w, self::PLANKS, 0, 1, 1, 4, $box);
		$this->placeBlock($w, self::OAK_STAIRS, $this->getMetadataWithOffset(self::OAK_STAIRS, 3), 2, 1, 4, $box);
		$this->placeBlock($w, self::OAK_STAIRS, $this->getMetadataWithOffset(self::OAK_STAIRS, 1), 1, 1, 3, $box);
		$this->fillWithBlocks($w, $box, 5, 0, 1, 7, 0, 3, self::DOUBLE_STONE_SLAB, 0, self::DOUBLE_STONE_SLAB, 0, false);
		$this->placeBlock($w, self::DOUBLE_STONE_SLAB, 0, 6, 1, 1, $box);
		$this->placeBlock($w, self::DOUBLE_STONE_SLAB, 0, 6, 1, 2, $box);
		$this->placeBlock($w, self::AIR, 0, 2, 1, 0, $box);
		$this->placeBlock($w, self::AIR, 0, 2, 2, 0, $box);
		$this->placeBlock($w, self::TORCH, 0, 2, 3, 1, $box);
		$this->placeDoor($w, $box, $rnd, 2, 1, 0, $this->getMetadataWithOffset(self::WOODEN_DOOR, 1));
		if($this->getBlock($w, 2, 0, -1, $box) === self::AIR && $this->getBlock($w, 2, -1, -1, $box) !== self::AIR){
			$this->placeBlock($w, self::STONE_STAIRS, $this->getMetadataWithOffset(self::STONE_STAIRS, 3), 2, 0, -1, $box);
		}
		$this->placeBlock($w, self::AIR, 0, 6, 1, 5, $box);
		$this->placeBlock($w, self::AIR, 0, 6, 2, 5, $box);
		$this->placeBlock($w, self::TORCH, 0, 6, 3, 4, $box);
		$this->placeDoor($w, $box, $rnd, 6, 1, 5, $this->getMetadataWithOffset(self::WOODEN_DOOR, 1));

		for($l = 0; $l < 5; $l++){
			for($k = 0; $k < 9; $k++){
				$this->clearCurrentPositionBlocksUpwards($w, $k, 7, $l, $box);
				$this->replaceAirAndLiquidDownwards($w, self::COBBLESTONE, 0, $k, -1, $l, $box);
			}
		}

		return true;
	}
}
