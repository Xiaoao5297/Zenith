<?php

namespace pocketmine\level\generator\gorigional\structure;

use pocketmine\level\generator\gorigional\JavaRandom;

/**
 * 移植自 SCAXE-GO-CE structure.JunglePyramid。
 */
class JunglePyramid extends ScatteredFeaturePiece{
	/** @var bool */
	private $placedMainChest = false;
	/** @var bool */
	private $placedHiddenChest = false;
	/** @var bool */
	private $placedTrap1 = false;
	/** @var bool */
	private $placedTrap2 = false;

	public function __construct(JavaRandom $rnd, $x, $z){
		$facing = $rnd->nextBoundedInt(4);
		parent::__construct(1, $x, 64, $z, 12, 10, 15, $facing);
	}

	public function buildComponent(StructureComponent $component = null, array &$components, JavaRandom $rnd){

	}

	public function addComponentParts(WorldAccess $w, JavaRandom $rnd, BoundingBox $box){
		if(!$this->offsetToAverageGroundLevel($w, $box, 0)){
			return false;
		}

		$COBBLE = 4;
		$MOSSY = 48;
		$STONE_STAIRS = 67;
		$VINE = 106;
		$TRIPWIRE_HOOK = 131;
		$TRIPWIRE = 132;
		$REDSTONE = 55;
		$LEVER = 69;
		$STICKY_PISTON = 29;
		$REPEATER = 93;
		$DISPENSER = 23;
		$STONEBRICK = 98;
		$CHISELED = 3;

		$selector = function(JavaRandom $rnd, $x, $y, $z, $wall) use ($COBBLE, $MOSSY){
			if($rnd->nextFloat() < 0.4){
				return [$COBBLE, 0];
			}
			return [$MOSSY, 0];
		};

		$this->fillWithRandomizedBlocks($w, $box, 0, -4, 0, $this->width - 1, 0, $this->depth - 1, false, $rnd, $selector);
		$this->fillWithRandomizedBlocks($w, $box, 2, 1, 2, 9, 2, 2, false, $rnd, $selector);
		$this->fillWithRandomizedBlocks($w, $box, 2, 1, 12, 9, 2, 12, false, $rnd, $selector);
		$this->fillWithRandomizedBlocks($w, $box, 2, 1, 3, 2, 2, 11, false, $rnd, $selector);
		$this->fillWithRandomizedBlocks($w, $box, 9, 1, 3, 9, 2, 11, false, $rnd, $selector);
		$this->fillWithRandomizedBlocks($w, $box, 1, 3, 1, 10, 6, 1, false, $rnd, $selector);
		$this->fillWithRandomizedBlocks($w, $box, 1, 3, 13, 10, 6, 13, false, $rnd, $selector);
		$this->fillWithRandomizedBlocks($w, $box, 1, 3, 2, 1, 6, 12, false, $rnd, $selector);
		$this->fillWithRandomizedBlocks($w, $box, 10, 3, 2, 10, 6, 12, false, $rnd, $selector);
		$this->fillWithRandomizedBlocks($w, $box, 2, 3, 2, 9, 3, 12, false, $rnd, $selector);
		$this->fillWithRandomizedBlocks($w, $box, 2, 6, 2, 9, 6, 12, false, $rnd, $selector);
		$this->fillWithRandomizedBlocks($w, $box, 3, 7, 3, 8, 7, 11, false, $rnd, $selector);
		$this->fillWithRandomizedBlocks($w, $box, 4, 8, 4, 7, 8, 10, false, $rnd, $selector);

		$this->fillWithAir($w, $box, 3, 1, 3, 8, 2, 11);
		$this->fillWithAir($w, $box, 4, 3, 6, 7, 3, 9);
		$this->fillWithAir($w, $box, 2, 4, 2, 9, 5, 12);
		$this->fillWithAir($w, $box, 4, 6, 5, 7, 6, 9);
		$this->fillWithAir($w, $box, 5, 7, 6, 6, 7, 8);
		$this->fillWithAir($w, $box, 5, 1, 2, 6, 2, 2);
		$this->fillWithAir($w, $box, 5, 2, 12, 6, 2, 12);
		$this->fillWithAir($w, $box, 5, 5, 1, 6, 5, 1);
		$this->fillWithAir($w, $box, 5, 5, 13, 6, 5, 13);

		$this->setBlockState($w, 0, 0, 1, 5, 5, $box);
		$this->setBlockState($w, 0, 0, 10, 5, 5, $box);
		$this->setBlockState($w, 0, 0, 1, 5, 9, $box);
		$this->setBlockState($w, 0, 0, 10, 5, 9, $box);

		for($i = 0; $i <= 14; $i += 14){
			$this->fillWithRandomizedBlocks($w, $box, 2, 4, $i, 2, 5, $i, false, $rnd, $selector);
			$this->fillWithRandomizedBlocks($w, $box, 4, 4, $i, 4, 5, $i, false, $rnd, $selector);
			$this->fillWithRandomizedBlocks($w, $box, 7, 4, $i, 7, 5, $i, false, $rnd, $selector);
			$this->fillWithRandomizedBlocks($w, $box, 9, 4, $i, 9, 5, $i, false, $rnd, $selector);
		}

		$this->fillWithRandomizedBlocks($w, $box, 5, 6, 0, 6, 6, 0, false, $rnd, $selector);

		for($l = 0; $l <= 11; $l += 11){
			for($k = 2; $k <= 12; $k += 2){
				$this->fillWithRandomizedBlocks($w, $box, $l, 4, $k, $l, 5, $k, false, $rnd, $selector);
			}
			$this->fillWithRandomizedBlocks($w, $box, $l, 6, 5, $l, 6, 5, false, $rnd, $selector);
			$this->fillWithRandomizedBlocks($w, $box, $l, 6, 9, $l, 6, 9, false, $rnd, $selector);
		}

		$this->fillWithRandomizedBlocks($w, $box, 2, 7, 2, 2, 9, 2, false, $rnd, $selector);
		$this->fillWithRandomizedBlocks($w, $box, 9, 7, 2, 9, 9, 2, false, $rnd, $selector);
		$this->fillWithRandomizedBlocks($w, $box, 2, 7, 12, 2, 9, 12, false, $rnd, $selector);
		$this->fillWithRandomizedBlocks($w, $box, 9, 7, 12, 9, 9, 12, false, $rnd, $selector);
		$this->fillWithRandomizedBlocks($w, $box, 4, 9, 4, 4, 9, 4, false, $rnd, $selector);
		$this->fillWithRandomizedBlocks($w, $box, 7, 9, 4, 7, 9, 4, false, $rnd, $selector);
		$this->fillWithRandomizedBlocks($w, $box, 4, 9, 10, 4, 9, 10, false, $rnd, $selector);
		$this->fillWithRandomizedBlocks($w, $box, 7, 9, 10, 7, 9, 10, false, $rnd, $selector);
		$this->fillWithRandomizedBlocks($w, $box, 5, 9, 7, 6, 9, 7, false, $rnd, $selector);

		$STATE_EAST = 0;
		$STATE_WEST = 1;
		$STATE_SOUTH = 2;
		$STATE_NORTH = 3;

		$this->setBlockState($w, $STONE_STAIRS, $STATE_NORTH, 5, 9, 6, $box);
		$this->setBlockState($w, $STONE_STAIRS, $STATE_NORTH, 6, 9, 6, $box);
		$this->setBlockState($w, $STONE_STAIRS, $STATE_SOUTH, 5, 9, 8, $box);
		$this->setBlockState($w, $STONE_STAIRS, $STATE_SOUTH, 6, 9, 8, $box);
		$this->setBlockState($w, $STONE_STAIRS, $STATE_NORTH, 4, 0, 0, $box);
		$this->setBlockState($w, $STONE_STAIRS, $STATE_NORTH, 5, 0, 0, $box);
		$this->setBlockState($w, $STONE_STAIRS, $STATE_NORTH, 6, 0, 0, $box);
		$this->setBlockState($w, $STONE_STAIRS, $STATE_NORTH, 7, 0, 0, $box);
		$this->setBlockState($w, $STONE_STAIRS, $STATE_NORTH, 4, 1, 8, $box);
		$this->setBlockState($w, $STONE_STAIRS, $STATE_NORTH, 4, 2, 9, $box);
		$this->setBlockState($w, $STONE_STAIRS, $STATE_NORTH, 4, 3, 10, $box);
		$this->setBlockState($w, $STONE_STAIRS, $STATE_NORTH, 7, 1, 8, $box);
		$this->setBlockState($w, $STONE_STAIRS, $STATE_NORTH, 7, 2, 9, $box);
		$this->setBlockState($w, $STONE_STAIRS, $STATE_NORTH, 7, 3, 10, $box);

		$this->fillWithRandomizedBlocks($w, $box, 4, 1, 9, 4, 1, 9, false, $rnd, $selector);
		$this->fillWithRandomizedBlocks($w, $box, 7, 1, 9, 7, 1, 9, false, $rnd, $selector);
		$this->fillWithRandomizedBlocks($w, $box, 4, 1, 10, 7, 2, 10, false, $rnd, $selector);

		$this->fillWithRandomizedBlocks($w, $box, 5, 4, 5, 6, 4, 5, false, $rnd, $selector);
		$this->setBlockState($w, $STONE_STAIRS, $STATE_EAST, 4, 4, 5, $box);
		$this->setBlockState($w, $STONE_STAIRS, $STATE_WEST, 7, 4, 5, $box);

		for($k = 0; $k < 4; $k++){
			$this->setBlockState($w, $STONE_STAIRS, $STATE_SOUTH, 5, 0 - $k, 6 + $k, $box);
			$this->setBlockState($w, $STONE_STAIRS, $STATE_SOUTH, 6, 0 - $k, 6 + $k, $box);
			$this->fillWithAir($w, $box, 5, 0 - $k, 7 + $k, 6, 0 - $k, 9 + $k);
		}

		$this->fillWithAir($w, $box, 1, -3, 12, 10, -1, 13);
		$this->fillWithAir($w, $box, 1, -3, 1, 3, -1, 13);
		$this->fillWithAir($w, $box, 1, -3, 1, 9, -1, 5);

		for($i = 1; $i <= 13; $i += 2){
			$this->fillWithRandomizedBlocks($w, $box, 1, -3, $i, 1, -2, $i, false, $rnd, $selector);
		}
		for($i = 2; $i <= 12; $i += 2){
			$this->fillWithRandomizedBlocks($w, $box, 1, -1, $i, 3, -1, $i, false, $rnd, $selector);
		}

		$this->fillWithRandomizedBlocks($w, $box, 2, -2, 1, 5, -2, 1, false, $rnd, $selector);
		$this->fillWithRandomizedBlocks($w, $box, 7, -2, 1, 9, -2, 1, false, $rnd, $selector);
		$this->fillWithRandomizedBlocks($w, $box, 6, -3, 1, 6, -3, 1, false, $rnd, $selector);
		$this->fillWithRandomizedBlocks($w, $box, 6, -1, 1, 6, -1, 1, false, $rnd, $selector);

		$this->setBlockState($w, $TRIPWIRE_HOOK, 3 | 4, 1, -3, 8, $box);
		$this->setBlockState($w, $TRIPWIRE_HOOK, 1 | 4, 4, -3, 8, $box);
		$this->setBlockState($w, $TRIPWIRE, 4, 2, -3, 8, $box);
		$this->setBlockState($w, $TRIPWIRE, 4, 3, -3, 8, $box);

		$this->setBlockState($w, $REDSTONE, 0, 5, -3, 7, $box);
		$this->setBlockState($w, $REDSTONE, 0, 5, -3, 6, $box);
		$this->setBlockState($w, $REDSTONE, 0, 5, -3, 5, $box);
		$this->setBlockState($w, $REDSTONE, 0, 5, -3, 4, $box);
		$this->setBlockState($w, $REDSTONE, 0, 5, -3, 3, $box);
		$this->setBlockState($w, $REDSTONE, 0, 5, -3, 2, $box);
		$this->setBlockState($w, $REDSTONE, 0, 5, -3, 1, $box);
		$this->setBlockState($w, $REDSTONE, 0, 4, -3, 1, $box);
		$this->setBlockState($w, $MOSSY, 0, 3, -3, 1, $box);

		if(!$this->placedTrap1){
			$this->setBlockState($w, $DISPENSER, 2, 3, -2, 1, $box);
			$this->placedTrap1 = true;
		}

		$this->setBlockState($w, $VINE, 4, 3, -2, 2, $box);

		$this->setBlockState($w, $TRIPWIRE_HOOK, 2 | 4, 7, -3, 1, $box);
		$this->setBlockState($w, $TRIPWIRE_HOOK, 0 | 4, 7, -3, 5, $box);
		$this->setBlockState($w, $TRIPWIRE, 4, 7, -3, 2, $box);
		$this->setBlockState($w, $TRIPWIRE, 4, 7, -3, 3, $box);
		$this->setBlockState($w, $TRIPWIRE, 4, 7, -3, 4, $box);

		$this->setBlockState($w, $REDSTONE, 0, 8, -3, 6, $box);
		$this->setBlockState($w, $REDSTONE, 0, 9, -3, 6, $box);
		$this->setBlockState($w, $REDSTONE, 0, 9, -3, 5, $box);
		$this->setBlockState($w, $MOSSY, 0, 9, -3, 4, $box);
		$this->setBlockState($w, $REDSTONE, 0, 9, -2, 4, $box);

		if(!$this->placedTrap2){
			$this->setBlockState($w, $DISPENSER, 4, 9, -2, 3, $box);
			$this->placedTrap2 = true;
		}

		$this->setBlockState($w, $VINE, 8, 8, -1, 3, $box);
		$this->setBlockState($w, $VINE, 8, 8, -2, 3, $box);

		if(!$this->placedMainChest){
			$this->setBlockState($w, 54, 2, 8, -3, 3, $box);
			$this->placedMainChest = true;
		}

		$this->setBlockState($w, $MOSSY, 0, 9, -3, 2, $box);
		$this->setBlockState($w, $MOSSY, 0, 8, -3, 1, $box);
		$this->setBlockState($w, $MOSSY, 0, 4, -3, 5, $box);
		$this->setBlockState($w, $MOSSY, 0, 5, -2, 5, $box);
		$this->setBlockState($w, $MOSSY, 0, 5, -1, 5, $box);
		$this->setBlockState($w, $MOSSY, 0, 6, -3, 5, $box);
		$this->setBlockState($w, $MOSSY, 0, 7, -2, 5, $box);
		$this->setBlockState($w, $MOSSY, 0, 7, -1, 5, $box);
		$this->setBlockState($w, $MOSSY, 0, 8, -3, 5, $box);
		$this->fillWithRandomizedBlocks($w, $box, 9, -1, 1, 9, -1, 5, false, $rnd, $selector);

		$this->fillWithAir($w, $box, 8, -3, 8, 10, -1, 10);
		$this->setBlockState($w, $STONEBRICK, $CHISELED, 8, -2, 11, $box);
		$this->setBlockState($w, $STONEBRICK, $CHISELED, 9, -2, 11, $box);
		$this->setBlockState($w, $STONEBRICK, $CHISELED, 10, -2, 11, $box);
		$this->setBlockState($w, $LEVER, 12, 8, -2, 12, $box);
		$this->setBlockState($w, $LEVER, 4, 8, -2, 12, $box);
		$this->setBlockState($w, $LEVER, 4, 9, -2, 12, $box);
		$this->setBlockState($w, $LEVER, 4, 10, -2, 12, $box);

		$this->fillWithRandomizedBlocks($w, $box, 8, -3, 8, 8, -3, 10, false, $rnd, $selector);
		$this->fillWithRandomizedBlocks($w, $box, 10, -3, 8, 10, -3, 10, false, $rnd, $selector);
		$this->setBlockState($w, $MOSSY, 0, 10, -2, 9, $box);
		$this->setBlockState($w, $REDSTONE, 0, 8, -2, 9, $box);
		$this->setBlockState($w, $REDSTONE, 0, 8, -2, 10, $box);
		$this->setBlockState($w, $REDSTONE, 0, 10, -1, 9, $box);
		$this->setBlockState($w, $STICKY_PISTON, 1, 9, -2, 8, $box);
		$this->setBlockState($w, $STICKY_PISTON, 4, 10, -2, 8, $box);
		$this->setBlockState($w, $STICKY_PISTON, 4, 10, -1, 8, $box);
		$this->setBlockState($w, $REPEATER, 0, 10, -2, 10, $box);

		if(!$this->placedHiddenChest){
			$this->setBlockState($w, 54, 4, 9, -3, 10, $box);
			$this->placedHiddenChest = true;
		}

		return true;
	}
}
