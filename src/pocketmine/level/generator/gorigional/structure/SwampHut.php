<?php

namespace pocketmine\level\generator\gorigional\structure;

use pocketmine\level\generator\gorigional\JavaRandom;

/**
 * 移植自 SCAXE-GO-CE structure.SwampHut。
 */
class SwampHut extends ScatteredFeaturePiece{
	/** @var bool */
	private $hasWitch = false;

	public function __construct(JavaRandom $rnd, $x, $z){
		$facing = $rnd->nextBoundedInt(4);
		parent::__construct(2, $x, 64, $z, 7, 7, 9, $facing);
	}

	public function buildComponent(StructureComponent $component = null, array &$components, JavaRandom $rnd){

	}

	public function addComponentParts(WorldAccess $w, JavaRandom $rnd, BoundingBox $box){
		if(!$this->offsetToAverageGroundLevel($w, $box, 0)){
			return false;
		}

		$SPRUCE_PLANKS = 5;
		$SPRUCE_PLANKS_META = 1;
		$SPRUCE_LOG = 17;
		$SPRUCE_LOG_META = 1;

		$this->fillWithBlocks($w, $box, 1, 0, 2, 1, 3, 2, $SPRUCE_LOG, $SPRUCE_LOG_META, $SPRUCE_LOG, $SPRUCE_LOG_META, false);
		$this->fillWithBlocks($w, $box, 5, 0, 2, 5, 3, 2, $SPRUCE_LOG, $SPRUCE_LOG_META, $SPRUCE_LOG, $SPRUCE_LOG_META, false);
		$this->fillWithBlocks($w, $box, 1, 0, 7, 1, 3, 7, $SPRUCE_LOG, $SPRUCE_LOG_META, $SPRUCE_LOG, $SPRUCE_LOG_META, false);
		$this->fillWithBlocks($w, $box, 5, 0, 7, 5, 3, 7, $SPRUCE_LOG, $SPRUCE_LOG_META, $SPRUCE_LOG, $SPRUCE_LOG_META, false);

		for($i = 2; $i <= 7; $i += 5){
			for($j = 1; $j <= 5; $j += 4){
				$this->replaceAirAndLiquidDownwards($w, $SPRUCE_LOG, $SPRUCE_LOG_META, $j, -1, $i, $box);
			}
		}

		$this->fillWithBlocks($w, $box, 1, 1, 1, 5, 1, 7, $SPRUCE_PLANKS, $SPRUCE_PLANKS_META, $SPRUCE_PLANKS, $SPRUCE_PLANKS_META, false);
		$this->fillWithBlocks($w, $box, 2, 1, 0, 4, 1, 0, $SPRUCE_PLANKS, $SPRUCE_PLANKS_META, $SPRUCE_PLANKS, $SPRUCE_PLANKS_META, false);

		$this->fillWithBlocks($w, $box, 1, 2, 3, 1, 3, 6, $SPRUCE_PLANKS, $SPRUCE_PLANKS_META, $SPRUCE_PLANKS, $SPRUCE_PLANKS_META, false);
		$this->fillWithBlocks($w, $box, 5, 2, 3, 5, 3, 6, $SPRUCE_PLANKS, $SPRUCE_PLANKS_META, $SPRUCE_PLANKS, $SPRUCE_PLANKS_META, false);
		$this->fillWithBlocks($w, $box, 2, 2, 7, 4, 3, 7, $SPRUCE_PLANKS, $SPRUCE_PLANKS_META, $SPRUCE_PLANKS, $SPRUCE_PLANKS_META, false);
		$this->fillWithBlocks($w, $box, 1, 0, 2, 1, 3, 2, $SPRUCE_LOG, $SPRUCE_LOG_META, $SPRUCE_LOG, $SPRUCE_LOG_META, false);
		$this->fillWithBlocks($w, $box, 5, 0, 2, 5, 3, 2, $SPRUCE_LOG, $SPRUCE_LOG_META, $SPRUCE_LOG, $SPRUCE_LOG_META, false);
		$this->fillWithBlocks($w, $box, 1, 0, 7, 1, 3, 7, $SPRUCE_LOG, $SPRUCE_LOG_META, $SPRUCE_LOG, $SPRUCE_LOG_META, false);
		$this->fillWithBlocks($w, $box, 5, 0, 7, 5, 3, 7, $SPRUCE_LOG, $SPRUCE_LOG_META, $SPRUCE_LOG, $SPRUCE_LOG_META, false);

		$SPRUCE_STAIRS = 134;
		$S_NORTH = 3;
		$S_EAST = 0;
		$S_WEST = 1;
		$S_SOUTH = 2;
		$this->fillWithBlocks($w, $box, 0, 4, 1, 6, 4, 1, $SPRUCE_STAIRS, $S_NORTH, $SPRUCE_STAIRS, $S_NORTH, false);
		$this->fillWithBlocks($w, $box, 0, 4, 2, 0, 4, 7, $SPRUCE_STAIRS, $S_EAST, $SPRUCE_STAIRS, $S_EAST, false);
		$this->fillWithBlocks($w, $box, 6, 4, 2, 6, 4, 7, $SPRUCE_STAIRS, $S_WEST, $SPRUCE_STAIRS, $S_WEST, false);
		$this->fillWithBlocks($w, $box, 0, 4, 8, 6, 4, 8, $SPRUCE_STAIRS, $S_SOUTH, $SPRUCE_STAIRS, $S_SOUTH, false);

		$this->fillWithBlocks($w, $box, 1, 4, 2, 5, 4, 7, $SPRUCE_PLANKS, $SPRUCE_PLANKS_META, $SPRUCE_PLANKS, $SPRUCE_PLANKS_META, false);

		$OAK_FENCE = 85;
		$FLOWER_POT = 140;
		$CRAFTING_TABLE = 58;
		$CAULDRON = 118;

		$this->setBlockState($w, $OAK_FENCE, 0, 2, 3, 2, $box);
		$this->setBlockState($w, $OAK_FENCE, 0, 3, 3, 7, $box);

		$this->setBlockState($w, $FLOWER_POT, 0, 1, 3, 5, $box);
		$this->setBlockState($w, $CRAFTING_TABLE, 0, 3, 2, 6, $box);
		$this->setBlockState($w, $CAULDRON, 0, 4, 2, 6, $box);

		if(!$this->hasWitch){
			$this->hasWitch = true;
		}

		return true;
	}
}
