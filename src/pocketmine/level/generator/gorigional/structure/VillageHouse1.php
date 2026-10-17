<?php

namespace pocketmine\level\generator\gorigional\structure;

use pocketmine\level\generator\gorigional\JavaRandom;

/**
 * 移植自 Java 1.7.10 StructureVillagePieces$House1。
 */
class VillageHouse1 extends VillagePiece{
	const AIR = 0;
	const COBBLESTONE = 4;
	const PLANKS = 5;
	const LOG = 17;
	const BOOKSHELF = 47;
	const TORCH = 50;
	const OAK_STAIRS = 53;
	const CRAFTING_TABLE = 58;
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
		$box = BoundingBox::getComponentToAddBoundingBox($i, $j, $k, 0, 0, 0, 9, 9, 6, $l);
		return (self::isValidBox($box) && self::findIntersecting($list, $box) === null) ? new self($start, $i1, $rnd, $box, $l) : null;
	}

	protected function getVillagerProfession($i){
		return 1;
	}

	public function addComponentParts(WorldAccess $w, JavaRandom $rnd, BoundingBox $box){
		if($this->terrainHeight < 0){
			$this->terrainHeight = $this->getAverageGroundHeight($w, $box);
			if($this->terrainHeight < 0){
				return true;
			}
			$this->boundingBox->offset(0, $this->terrainHeight - $this->boundingBox->maxY + 9 - 1, 0);
		}

		$this->fillWithBlocks($w, $box, 1, 1, 1, 7, 5, 4, self::AIR, 0, self::AIR, 0, false);
		$this->fillWithBlocks($w, $box, 0, 0, 0, 8, 0, 5, self::COBBLESTONE, 0, self::COBBLESTONE, 0, false);
		$this->fillWithBlocks($w, $box, 0, 5, 0, 8, 5, 5, self::COBBLESTONE, 0, self::COBBLESTONE, 0, false);
		$this->fillWithBlocks($w, $box, 0, 6, 1, 8, 6, 4, self::COBBLESTONE, 0, self::COBBLESTONE, 0, false);
		$this->fillWithBlocks($w, $box, 0, 7, 2, 8, 7, 3, self::COBBLESTONE, 0, self::COBBLESTONE, 0, false);

		$i = $this->getMetadataWithOffset(self::OAK_STAIRS, 3);
		$j = $this->getMetadataWithOffset(self::OAK_STAIRS, 2);

		for($l = -1; $l <= 2; $l++){
			for($k = 0; $k <= 8; $k++){
				$this->placeBlock($w, self::OAK_STAIRS, $i, $k, 6 + $l, $l, $box);
				$this->placeBlock($w, self::OAK_STAIRS, $j, $k, 6 + $l, 5 - $l, $box);
			}
		}

		$this->fillWithBlocks($w, $box, 0, 1, 0, 0, 1, 5, self::COBBLESTONE, 0, self::COBBLESTONE, 0, false);
		$this->fillWithBlocks($w, $box, 1, 1, 5, 8, 1, 5, self::COBBLESTONE, 0, self::COBBLESTONE, 0, false);
		$this->fillWithBlocks($w, $box, 8, 1, 0, 8, 1, 4, self::COBBLESTONE, 0, self::COBBLESTONE, 0, false);
		$this->fillWithBlocks($w, $box, 2, 1, 0, 7, 1, 0, self::COBBLESTONE, 0, self::COBBLESTONE, 0, false);
		$this->fillWithBlocks($w, $box, 0, 2, 0, 0, 4, 0, self::COBBLESTONE, 0, self::COBBLESTONE, 0, false);
		$this->fillWithBlocks($w, $box, 0, 2, 5, 0, 4, 5, self::COBBLESTONE, 0, self::COBBLESTONE, 0, false);
		$this->fillWithBlocks($w, $box, 8, 2, 5, 8, 4, 5, self::COBBLESTONE, 0, self::COBBLESTONE, 0, false);
		$this->fillWithBlocks($w, $box, 8, 2, 0, 8, 4, 0, self::COBBLESTONE, 0, self::COBBLESTONE, 0, false);
		$this->fillWithBlocks($w, $box, 0, 2, 1, 0, 4, 4, self::PLANKS, 0, self::PLANKS, 0, false);
		$this->fillWithBlocks($w, $box, 1, 2, 5, 7, 4, 5, self::PLANKS, 0, self::PLANKS, 0, false);
		$this->fillWithBlocks($w, $box, 8, 2, 1, 8, 4, 4, self::PLANKS, 0, self::PLANKS, 0, false);
		$this->fillWithBlocks($w, $box, 1, 2, 0, 7, 4, 0, self::PLANKS, 0, self::PLANKS, 0, false);

		foreach([[4,2,0],[5,2,0],[6,2,0],[4,3,0],[5,3,0],[6,3,0],[0,2,2],[0,2,3],[0,3,2],[0,3,3],[8,2,2],[8,2,3],[8,3,2],[8,3,3],[2,2,5],[3,2,5],[5,2,5],[6,2,5]] as $p){
			$this->placeBlock($w, self::GLASS_PANE, 0, $p[0], $p[1], $p[2], $box);
		}

		$this->fillWithBlocks($w, $box, 1, 4, 1, 7, 4, 1, self::PLANKS, 0, self::PLANKS, 0, false);
		$this->fillWithBlocks($w, $box, 1, 4, 4, 7, 4, 4, self::PLANKS, 0, self::PLANKS, 0, false);
		$this->fillWithBlocks($w, $box, 1, 3, 4, 7, 3, 4, self::BOOKSHELF, 0, self::BOOKSHELF, 0, false);
		$this->placeBlock($w, self::PLANKS, 0, 7, 1, 4, $box);
		$this->placeBlock($w, self::OAK_STAIRS, $this->getMetadataWithOffset(self::OAK_STAIRS, 0), 7, 1, 3, $box);
		$l = $this->getMetadataWithOffset(self::OAK_STAIRS, 3);
		$this->placeBlock($w, self::OAK_STAIRS, $l, 6, 1, 4, $box);
		$this->placeBlock($w, self::OAK_STAIRS, $l, 5, 1, 4, $box);
		$this->placeBlock($w, self::OAK_STAIRS, $l, 4, 1, 4, $box);
		$this->placeBlock($w, self::OAK_STAIRS, $l, 3, 1, 4, $box);
		$this->placeBlock($w, self::FENCE, 0, 6, 1, 3, $box);
		$this->placeBlock($w, self::WOODEN_PRESSURE_PLATE, 0, 6, 2, 3, $box);
		$this->placeBlock($w, self::FENCE, 0, 4, 1, 3, $box);
		$this->placeBlock($w, self::WOODEN_PRESSURE_PLATE, 0, 4, 2, 3, $box);
		$this->placeBlock($w, self::CRAFTING_TABLE, 0, 7, 1, 1, $box);

		$this->placeBlock($w, self::AIR, 0, 1, 1, 0, $box);
		$this->placeBlock($w, self::AIR, 0, 1, 2, 0, $box);
		$this->placeDoor($w, $box, $rnd, 1, 1, 0, $this->getMetadataWithOffset(self::WOODEN_DOOR, 1));

		if($this->getBlock($w, 1, 0, -1, $box) === self::AIR && $this->getBlock($w, 1, -1, -1, $box) !== self::AIR){
			$this->placeBlock($w, self::STONE_STAIRS, $this->getMetadataWithOffset(self::STONE_STAIRS, 3), 1, 0, -1, $box);
		}

		for($k = 0; $k < 6; $k++){
			for($i1 = 0; $i1 < 9; $i1++){
				$this->clearCurrentPositionBlocksUpwards($w, $i1, 9, $k, $box);
				$this->replaceAirAndLiquidDownwards($w, self::COBBLESTONE, 0, $i1, -1, $k, $box);
			}
		}

		return true;
	}
}
