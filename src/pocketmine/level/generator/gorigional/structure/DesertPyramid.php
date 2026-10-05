<?php

namespace pocketmine\level\generator\gorigional\structure;

use pocketmine\level\generator\gorigional\JavaRandom;

/**
 * 移植自 SCAXE-GO-CE structure.DesertPyramid。
 */
class DesertPyramid extends ScatteredFeaturePiece{
	/** @var bool[] */
	private $hasPlacedChest = [false, false, false, false];

	public function __construct(JavaRandom $rnd, $x, $z){
		$facing = $rnd->nextBoundedInt(4);
		parent::__construct(0, $x, 64, $z, 21, 15, 21, $facing);
	}

	public function buildComponent(StructureComponent $component = null, array &$components, JavaRandom $rnd){

	}

	public function addComponentParts(WorldAccess $w, JavaRandom $rnd, BoundingBox $box){
		if(!$this->offsetToAverageGroundLevel($w, $box, 0)){
			return false;
		}

		$SANDSTONE = 24;
		$META_SMOOTH = 2;

		$STAINED_CLAY = 159;
		$META_ORANGE = 1;
		$META_BLUE = 11;

		$STONE_PRESSURE_PLATE = 70;
		$TNT = 46;

		$this->fillWithBlocks($w, $box, 0, -4, 0, 20, 0, 20, $SANDSTONE, 0, $SANDSTONE, 0, false);

		for($i = 1; $i <= 9; $i++){
			$this->fillWithBlocks($w, $box, $i, $i, $i, 20 - $i, $i, 20 - $i, $SANDSTONE, 0, $SANDSTONE, 0, false);
			$this->fillWithBlocks($w, $box, $i + 1, $i, $i + 1, 20 - $i - 1, $i, 20 - $i - 1, 0, 0, 0, 0, false);
		}

		for($i2 = 0; $i2 < 21; $i2++){
			for($j = 0; $j < 21; $j++){
				$this->replaceAirAndLiquidDownwards($w, $SANDSTONE, 0, $i2, -5, $j, $box);
			}
		}

		$this->fillWithBlocks($w, $box, 0, 0, 0, 4, 9, 4, $SANDSTONE, 0, 0, 0, false);
		$this->fillWithBlocks($w, $box, 1, 10, 1, 3, 10, 3, $SANDSTONE, 0, $SANDSTONE, 0, false);

		$this->fillWithBlocks($w, $box, 16, 0, 0, 20, 9, 4, $SANDSTONE, 0, 0, 0, false);
		$this->fillWithBlocks($w, $box, 17, 10, 1, 19, 10, 3, $SANDSTONE, 0, $SANDSTONE, 0, false);

		$this->fillWithBlocks($w, $box, 8, 0, 0, 12, 4, 4, $SANDSTONE, 0, 0, 0, false);
		$this->fillWithBlocks($w, $box, 9, 1, 0, 11, 3, 4, 0, 0, 0, 0, false);

		$this->setBlockState($w, $SANDSTONE, $META_SMOOTH, 9, 1, 1, $box);
		$this->setBlockState($w, $SANDSTONE, $META_SMOOTH, 9, 2, 1, $box);
		$this->setBlockState($w, $SANDSTONE, $META_SMOOTH, 9, 3, 1, $box);
		$this->setBlockState($w, $SANDSTONE, $META_SMOOTH, 10, 3, 1, $box);
		$this->setBlockState($w, $SANDSTONE, $META_SMOOTH, 11, 3, 1, $box);
		$this->setBlockState($w, $SANDSTONE, $META_SMOOTH, 11, 2, 1, $box);
		$this->setBlockState($w, $SANDSTONE, $META_SMOOTH, 11, 1, 1, $box);

		$this->fillWithBlocks($w, $box, 4, 1, 1, 8, 3, 3, $SANDSTONE, 0, 0, 0, false);
		$this->fillWithBlocks($w, $box, 4, 1, 2, 8, 2, 2, 0, 0, 0, 0, false);
		$this->fillWithBlocks($w, $box, 12, 1, 1, 16, 3, 3, $SANDSTONE, 0, 0, 0, false);
		$this->fillWithBlocks($w, $box, 12, 1, 2, 16, 2, 2, 0, 0, 0, 0, false);

		for($j2 = 0; $j2 < 21; $j2 += 20){
			$this->setBlockState($w, $SANDSTONE, $META_SMOOTH, $j2, 2, 1, $box);
			$this->setBlockState($w, $STAINED_CLAY, $META_ORANGE, $j2, 2, 2, $box);
			$this->setBlockState($w, $SANDSTONE, $META_SMOOTH, $j2, 2, 3, $box);
			$this->setBlockState($w, $SANDSTONE, $META_SMOOTH, $j2, 3, 1, $box);
			$this->setBlockState($w, $STAINED_CLAY, $META_ORANGE, $j2, 3, 2, $box);
			$this->setBlockState($w, $SANDSTONE, $META_SMOOTH, $j2, 3, 3, $box);
		}

		$this->setBlockState($w, $STAINED_CLAY, $META_BLUE, 10, 0, 10, $box);

		$this->setBlockState($w, $STAINED_CLAY, $META_ORANGE, 10, 0, 9, $box);
		$this->setBlockState($w, $STAINED_CLAY, $META_ORANGE, 10, 0, 11, $box);
		$this->setBlockState($w, $STAINED_CLAY, $META_ORANGE, 9, 0, 10, $box);
		$this->setBlockState($w, $STAINED_CLAY, $META_ORANGE, 11, 0, 10, $box);

		$this->setBlockState($w, $STONE_PRESSURE_PLATE, 0, 10, -11, 10, $box);
		$this->fillWithBlocks($w, $box, 9, -13, 9, 11, -13, 11, $TNT, 0, 0, 0, false);

		for($i = 0; $i < 4; $i++){
			if(!$this->hasPlacedChest[$i]){
				$cx = 10;
				$cz = 10;
				switch($i){
					case 0: $cz -= 2; break;
					case 1: $cx += 2; break;
					case 2: $cz += 2; break;
					case 3: $cx -= 2; break;
				}

				if($box->resultIsInside($this->getXWithOffset($cx, $cz), $this->getYWithOffset(-11), $this->getZWithOffset($cx, $cz))){
					$this->setBlockState($w, 54, 0, $cx, -11, $cz, $box);
					$this->hasPlacedChest[$i] = true;
				}
			}
		}

		return true;
	}
}
