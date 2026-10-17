<?php

namespace pocketmine\level\generator\gorigional\structure;

use pocketmine\level\generator\gorigional\JavaRandom;

/**
 * 移植自 Java 1.7.10 StructureVillagePieces$WoodHut。
 */
class VillageWoodHut extends VillagePiece{
	const AIR = 0;
	const DIRT = 3;
	const COBBLESTONE = 4;
	const PLANKS = 5;
	const LOG = 17;
	const WOODEN_DOOR = 64;
	const STONE_STAIRS = 67;
	const WOODEN_PRESSURE_PLATE = 72;
	const FENCE = 85;
	const GLASS_PANE = 102;

	/** @var bool */
	private $bed;
	/** @var int */
	private $tablePos;

	public function __construct(VillageWellStart $start, $typeId, JavaRandom $rnd, BoundingBox $box, $facing){
		parent::__construct($start, $typeId);
		$this->coordBaseMode = $facing;
		$this->boundingBox = $box;
		$this->bed = $rnd->nextBoolean();
		$this->tablePos = $rnd->nextBoundedInt(3);
	}

	public static function create(VillageWellStart $start, array $list, JavaRandom $rnd, $i, $j, $k, $l, $i1){
		$box = BoundingBox::getComponentToAddBoundingBox($i, $j, $k, 0, 0, 0, 4, 6, 5, $l);
		return (self::isValidBox($box) && self::findIntersecting($list, $box) === null) ? new self($start, $i1, $rnd, $box, $l) : null;
	}

	public function addComponentParts(WorldAccess $w, JavaRandom $rnd, BoundingBox $box){
		if($this->terrainHeight < 0){
			$this->terrainHeight = $this->getAverageGroundHeight($w, $box);
			if($this->terrainHeight < 0){
				return true;
			}
			$this->boundingBox->offset(0, $this->terrainHeight - $this->boundingBox->maxY + 6 - 1, 0);
		}

		$this->fillWithBlocks($w, $box, 1, 1, 1, 3, 5, 4, self::AIR, 0, self::AIR, 0, false);
		$this->fillWithBlocks($w, $box, 0, 0, 0, 3, 0, 4, self::COBBLESTONE, 0, self::COBBLESTONE, 0, false);
		$this->fillWithBlocks($w, $box, 1, 0, 1, 2, 0, 3, self::DIRT, 0, self::DIRT, 0, false);

		if($this->bed){
			$this->fillWithBlocks($w, $box, 1, 4, 1, 2, 4, 3, self::LOG, 0, self::LOG, 0, false);
		}else{
			$this->fillWithBlocks($w, $box, 1, 5, 1, 2, 5, 3, self::LOG, 0, self::LOG, 0, false);
		}

		$this->placeBlock($w, self::LOG, 0, 1, 4, 0, $box);
		$this->placeBlock($w, self::LOG, 0, 2, 4, 0, $box);
		$this->placeBlock($w, self::LOG, 0, 1, 4, 4, $box);
		$this->placeBlock($w, self::LOG, 0, 2, 4, 4, $box);
		$this->placeBlock($w, self::LOG, 0, 0, 4, 1, $box);
		$this->placeBlock($w, self::LOG, 0, 0, 4, 2, $box);
		$this->placeBlock($w, self::LOG, 0, 0, 4, 3, $box);
		$this->placeBlock($w, self::LOG, 0, 3, 4, 1, $box);
		$this->placeBlock($w, self::LOG, 0, 3, 4, 2, $box);
		$this->placeBlock($w, self::LOG, 0, 3, 4, 3, $box);
		$this->fillWithBlocks($w, $box, 0, 1, 0, 0, 3, 0, self::LOG, 0, self::LOG, 0, false);
		$this->fillWithBlocks($w, $box, 3, 1, 0, 3, 3, 0, self::LOG, 0, self::LOG, 0, false);
		$this->fillWithBlocks($w, $box, 0, 1, 4, 0, 3, 4, self::LOG, 0, self::LOG, 0, false);
		$this->fillWithBlocks($w, $box, 3, 1, 4, 3, 3, 4, self::LOG, 0, self::LOG, 0, false);
		$this->fillWithBlocks($w, $box, 0, 1, 1, 0, 3, 3, self::PLANKS, 0, self::PLANKS, 0, false);
		$this->fillWithBlocks($w, $box, 3, 1, 1, 3, 3, 3, self::PLANKS, 0, self::PLANKS, 0, false);
		$this->fillWithBlocks($w, $box, 1, 1, 0, 2, 3, 0, self::PLANKS, 0, self::PLANKS, 0, false);
		$this->fillWithBlocks($w, $box, 1, 1, 4, 2, 3, 4, self::PLANKS, 0, self::PLANKS, 0, false);
		$this->placeBlock($w, self::GLASS_PANE, 0, 0, 2, 2, $box);
		$this->placeBlock($w, self::GLASS_PANE, 0, 3, 2, 2, $box);

		if($this->tablePos > 0){
			$this->placeBlock($w, self::FENCE, 0, $this->tablePos, 1, 3, $box);
			$this->placeBlock($w, self::WOODEN_PRESSURE_PLATE, 0, $this->tablePos, 2, 3, $box);
		}

		$this->placeBlock($w, self::AIR, 0, 1, 1, 0, $box);
		$this->placeBlock($w, self::AIR, 0, 1, 2, 0, $box);
		$this->placeDoor($w, $box, $rnd, 1, 1, 0, $this->getMetadataWithOffset(self::WOODEN_DOOR, 1));

		if($this->getBlock($w, 1, 0, -1, $box) === self::AIR && $this->getBlock($w, 1, -1, -1, $box) !== self::AIR){
			$this->placeBlock($w, self::STONE_STAIRS, $this->getMetadataWithOffset(self::STONE_STAIRS, 3), 1, 0, -1, $box);
		}

		for($i = 0; $i < 5; $i++){
			for($j = 0; $j < 4; $j++){
				$this->clearCurrentPositionBlocksUpwards($w, $j, 6, $i, $box);
				$this->replaceAirAndLiquidDownwards($w, self::COBBLESTONE, 0, $j, -1, $i, $box);
			}
		}

		return true;
	}
}
