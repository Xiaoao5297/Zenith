<?php

namespace pocketmine\level\generator\gorigional\structure;

use pocketmine\level\generator\gorigional\JavaRandom;

/**
 * 移植自 Java 1.7.10 ComponentScatteredFeaturePieces$SwampHut。
 */
class SwampHut extends ScatteredFeaturePiece{
	const AIR = 0;
	const PLANKS = 5;
	const LOG = 17;
	const OAK_STAIRS = 53;
	const CRAFTING_TABLE = 58;
	const FENCE = 85;
	const CAULDRON = 118;
	const SPRUCE_STAIRS = 134;
	const FLOWER_POT = 140;

	/** @var bool */
	private $hasWitch = false;

	public function __construct(JavaRandom $rnd, $x, $z){
		$facing = $rnd->nextBoundedInt(4);
		parent::__construct(2, $x, 64, $z, 7, 5, 9, $facing);
	}

	public function buildComponent(StructureComponent $component = null, array &$components, JavaRandom $rnd){

	}

	public function addComponentParts(WorldAccess $w, JavaRandom $rnd, BoundingBox $box){
		if(!$this->offsetToAverageGroundLevel($w, $box, 0)){
			return false;
		}

		$this->fillWithBlocks($w, $box, 1, 1, 1, 5, 1, 7, self::PLANKS, 1, self::PLANKS, 1, false);
		$this->fillWithBlocks($w, $box, 1, 4, 2, 5, 4, 7, self::PLANKS, 1, self::PLANKS, 1, false);
		$this->fillWithBlocks($w, $box, 2, 1, 0, 4, 1, 0, self::PLANKS, 1, self::PLANKS, 1, false);
		$this->fillWithBlocks($w, $box, 2, 2, 2, 3, 3, 2, self::PLANKS, 1, self::PLANKS, 1, false);
		$this->fillWithBlocks($w, $box, 1, 2, 3, 1, 3, 6, self::PLANKS, 1, self::PLANKS, 1, false);
		$this->fillWithBlocks($w, $box, 5, 2, 3, 5, 3, 6, self::PLANKS, 1, self::PLANKS, 1, false);
		$this->fillWithBlocks($w, $box, 2, 2, 7, 4, 3, 7, self::PLANKS, 1, self::PLANKS, 1, false);

		$this->fillWithBlocks($w, $box, 1, 0, 2, 1, 3, 2, self::LOG, 0, self::LOG, 0, false);
		$this->fillWithBlocks($w, $box, 5, 0, 2, 5, 3, 2, self::LOG, 0, self::LOG, 0, false);
		$this->fillWithBlocks($w, $box, 1, 0, 7, 1, 3, 7, self::LOG, 0, self::LOG, 0, false);
		$this->fillWithBlocks($w, $box, 5, 0, 7, 5, 3, 7, self::LOG, 0, self::LOG, 0, false);

		$this->placeBlock($w, self::FENCE, 0, 2, 3, 2, $box);
		$this->placeBlock($w, self::FENCE, 0, 3, 3, 7, $box);
		$this->placeBlock($w, self::AIR, 0, 1, 3, 4, $box);
		$this->placeBlock($w, self::AIR, 0, 5, 3, 4, $box);
		$this->placeBlock($w, self::AIR, 0, 5, 3, 5, $box);
		$this->placeBlock($w, self::FLOWER_POT, 7, 1, 3, 5, $box);
		$this->placeBlock($w, self::CRAFTING_TABLE, 0, 3, 2, 6, $box);
		$this->placeBlock($w, self::CAULDRON, 0, 4, 2, 6, $box);
		$this->placeBlock($w, self::FENCE, 0, 1, 2, 1, $box);
		$this->placeBlock($w, self::FENCE, 0, 5, 2, 1, $box);

		$i = $this->getMetadataWithOffset(self::OAK_STAIRS, 3);
		$j = $this->getMetadataWithOffset(self::OAK_STAIRS, 1);
		$k = $this->getMetadataWithOffset(self::OAK_STAIRS, 0);
		$l = $this->getMetadataWithOffset(self::OAK_STAIRS, 2);

		$this->fillWithBlocks($w, $box, 0, 4, 1, 6, 4, 1, self::SPRUCE_STAIRS, $i, self::SPRUCE_STAIRS, $i, false);
		$this->fillWithBlocks($w, $box, 0, 4, 2, 0, 4, 7, self::SPRUCE_STAIRS, $k, self::SPRUCE_STAIRS, $k, false);
		$this->fillWithBlocks($w, $box, 6, 4, 2, 6, 4, 7, self::SPRUCE_STAIRS, $j, self::SPRUCE_STAIRS, $j, false);
		$this->fillWithBlocks($w, $box, 0, 4, 8, 6, 4, 8, self::SPRUCE_STAIRS, $l, self::SPRUCE_STAIRS, $l, false);

		for($j1 = 2; $j1 <= 7; $j1 += 5){
			for($i1 = 1; $i1 <= 5; $i1 += 4){
				$this->replaceAirAndLiquidDownwards($w, self::LOG, 0, $i1, -1, $j1, $box);
			}
		}

		if(!$this->hasWitch){
			$this->hasWitch = true;
		}

		return true;
	}
}
