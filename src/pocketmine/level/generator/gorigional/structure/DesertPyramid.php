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
 * 移植自 Java 1.7.10 ComponentScatteredFeaturePieces$DesertPyramid。
 */
class DesertPyramid extends ScatteredFeaturePiece{
	const AIR = 0;
	const SANDSTONE = 24;
	const WOOL = 35;
	const STONE_SLAB = 44;
	const TNT = 46;
	const PRESSURE_PLATE = 70;
	const SANDSTONE_STAIRS = 128;

	/** @var bool[] */
	private $hasPlacedChest = [false, false, false, false];

	public function __construct(JavaRandom $rnd, $x, $z){
		$facing = $rnd->nextBoundedInt(4);
		parent::__construct(0, $x, 64, $z, 21, 15, 21, $facing);
	}

	public function buildComponent(StructureComponent $component = null, array &$components, JavaRandom $rnd){

	}

	public function addComponentParts(WorldAccess $w, JavaRandom $rnd, BoundingBox $box){
		$width = $this->width;
		$depth = $this->depth;

		$this->fillWithBlocks($w, $box, 0, -4, 0, $width - 1, 0, $depth - 1, self::SANDSTONE, 0, self::SANDSTONE, 0, false);

		for($i = 1; $i <= 9; $i++){
			$this->fillWithBlocks($w, $box, $i, $i, $i, $width - 1 - $i, $i, $depth - 1 - $i, self::SANDSTONE, 0, self::SANDSTONE, 0, false);
			$this->fillWithBlocks($w, $box, $i + 1, $i, $i + 1, $width - 2 - $i, $i, $depth - 2 - $i, self::AIR, 0, self::AIR, 0, false);
		}

		for($i = 0; $i < $width; $i++){
			for($k = 0; $k < $depth; $k++){
				$this->replaceAirAndLiquidDownwards($w, self::SANDSTONE, 0, $i, -5, $k, $box);
			}
		}

		$i = $this->getMetadataWithOffset(self::SANDSTONE_STAIRS, 3);
		$k = $this->getMetadataWithOffset(self::SANDSTONE_STAIRS, 2);
		$j = $this->getMetadataWithOffset(self::SANDSTONE_STAIRS, 0);
		$l = $this->getMetadataWithOffset(self::SANDSTONE_STAIRS, 1);
		$b0 = 1;
		$b1 = 11;

		$this->fillWithBlocks($w, $box, 0, 0, 0, 4, 9, 4, self::SANDSTONE, 0, self::AIR, 0, false);
		$this->fillWithBlocks($w, $box, 1, 10, 1, 3, 10, 3, self::SANDSTONE, 0, self::SANDSTONE, 0, false);
		$this->placeBlock($w, self::SANDSTONE_STAIRS, $i, 2, 10, 0, $box);
		$this->placeBlock($w, self::SANDSTONE_STAIRS, $k, 2, 10, 4, $box);
		$this->placeBlock($w, self::SANDSTONE_STAIRS, $j, 0, 10, 2, $box);
		$this->placeBlock($w, self::SANDSTONE_STAIRS, $l, 4, 10, 2, $box);

		$this->fillWithBlocks($w, $box, $width - 5, 0, 0, $width - 1, 9, 4, self::SANDSTONE, 0, self::AIR, 0, false);
		$this->fillWithBlocks($w, $box, $width - 4, 10, 1, $width - 2, 10, 3, self::SANDSTONE, 0, self::SANDSTONE, 0, false);
		$this->placeBlock($w, self::SANDSTONE_STAIRS, $i, $width - 3, 10, 0, $box);
		$this->placeBlock($w, self::SANDSTONE_STAIRS, $k, $width - 3, 10, 4, $box);
		$this->placeBlock($w, self::SANDSTONE_STAIRS, $j, $width - 5, 10, 2, $box);
		$this->placeBlock($w, self::SANDSTONE_STAIRS, $l, $width - 1, 10, 2, $box);

		$this->fillWithBlocks($w, $box, 8, 0, 0, 12, 4, 4, self::SANDSTONE, 0, self::AIR, 0, false);
		$this->fillWithBlocks($w, $box, 9, 1, 0, 11, 3, 4, self::AIR, 0, self::AIR, 0, false);
		$this->placeBlock($w, self::SANDSTONE, 2, 9, 1, 1, $box);
		$this->placeBlock($w, self::SANDSTONE, 2, 9, 2, 1, $box);
		$this->placeBlock($w, self::SANDSTONE, 2, 9, 3, 1, $box);
		$this->placeBlock($w, self::SANDSTONE, 2, 10, 3, 1, $box);
		$this->placeBlock($w, self::SANDSTONE, 2, 11, 3, 1, $box);
		$this->placeBlock($w, self::SANDSTONE, 2, 11, 2, 1, $box);
		$this->placeBlock($w, self::SANDSTONE, 2, 11, 1, 1, $box);

		$this->fillWithBlocks($w, $box, 4, 1, 1, 8, 3, 3, self::SANDSTONE, 0, self::AIR, 0, false);
		$this->fillWithBlocks($w, $box, 4, 1, 2, 8, 2, 2, self::AIR, 0, self::AIR, 0, false);
		$this->fillWithBlocks($w, $box, 12, 1, 1, 16, 3, 3, self::SANDSTONE, 0, self::AIR, 0, false);
		$this->fillWithBlocks($w, $box, 12, 1, 2, 16, 2, 2, self::AIR, 0, self::AIR, 0, false);

		$this->fillWithBlocks($w, $box, 5, 4, 5, $width - 6, 4, $depth - 6, self::SANDSTONE, 0, self::SANDSTONE, 0, false);
		$this->fillWithBlocks($w, $box, 9, 4, 9, 11, 4, 11, self::AIR, 0, self::AIR, 0, false);
		$this->fillWithBlocks($w, $box, 8, 1, 8, 8, 3, 8, self::SANDSTONE, 2, self::SANDSTONE, 2, false);
		$this->fillWithBlocks($w, $box, 12, 1, 8, 12, 3, 8, self::SANDSTONE, 2, self::SANDSTONE, 2, false);
		$this->fillWithBlocks($w, $box, 8, 1, 12, 8, 3, 12, self::SANDSTONE, 2, self::SANDSTONE, 2, false);
		$this->fillWithBlocks($w, $box, 12, 1, 12, 12, 3, 12, self::SANDSTONE, 2, self::SANDSTONE, 2, false);
		$this->fillWithBlocks($w, $box, 1, 1, 5, 4, 4, 11, self::SANDSTONE, 0, self::SANDSTONE, 0, false);
		$this->fillWithBlocks($w, $box, $width - 5, 1, 5, $width - 2, 4, 11, self::SANDSTONE, 0, self::SANDSTONE, 0, false);
		$this->fillWithBlocks($w, $box, 6, 7, 9, 6, 7, 11, self::SANDSTONE, 0, self::SANDSTONE, 0, false);
		$this->fillWithBlocks($w, $box, $width - 7, 7, 9, $width - 7, 7, 11, self::SANDSTONE, 0, self::SANDSTONE, 0, false);
		$this->fillWithBlocks($w, $box, 5, 5, 9, 5, 7, 11, self::SANDSTONE, 2, self::SANDSTONE, 2, false);
		$this->fillWithBlocks($w, $box, $width - 6, 5, 9, $width - 6, 7, 11, self::SANDSTONE, 2, self::SANDSTONE, 2, false);

		$this->placeBlock($w, self::AIR, 0, 5, 5, 10, $box);
		$this->placeBlock($w, self::AIR, 0, 5, 6, 10, $box);
		$this->placeBlock($w, self::AIR, 0, 6, 6, 10, $box);
		$this->placeBlock($w, self::AIR, 0, $width - 6, 5, 10, $box);
		$this->placeBlock($w, self::AIR, 0, $width - 6, 6, 10, $box);
		$this->placeBlock($w, self::AIR, 0, $width - 7, 6, 10, $box);
		$this->fillWithBlocks($w, $box, 2, 4, 4, 2, 6, 4, self::AIR, 0, self::AIR, 0, false);
		$this->fillWithBlocks($w, $box, $width - 3, 4, 4, $width - 3, 6, 4, self::AIR, 0, self::AIR, 0, false);
		$this->placeBlock($w, self::SANDSTONE_STAIRS, $i, 2, 4, 5, $box);
		$this->placeBlock($w, self::SANDSTONE_STAIRS, $i, 2, 3, 4, $box);
		$this->placeBlock($w, self::SANDSTONE_STAIRS, $i, $width - 3, 4, 5, $box);
		$this->placeBlock($w, self::SANDSTONE_STAIRS, $i, $width - 3, 3, 4, $box);
		$this->fillWithBlocks($w, $box, 1, 1, 3, 2, 2, 3, self::SANDSTONE, 0, self::SANDSTONE, 0, false);
		$this->fillWithBlocks($w, $box, $width - 3, 1, 3, $width - 2, 2, 3, self::SANDSTONE, 0, self::SANDSTONE, 0, false);
		$this->placeBlock($w, self::SANDSTONE_STAIRS, 0, 1, 1, 2, $box);
		$this->placeBlock($w, self::SANDSTONE_STAIRS, 0, $width - 2, 1, 2, $box);
		$this->placeBlock($w, self::STONE_SLAB, 1, 1, 2, 2, $box);
		$this->placeBlock($w, self::STONE_SLAB, 1, $width - 2, 2, 2, $box);
		$this->placeBlock($w, self::SANDSTONE_STAIRS, $l, 2, 1, 2, $box);
		$this->placeBlock($w, self::SANDSTONE_STAIRS, $j, $width - 3, 1, 2, $box);
		$this->fillWithBlocks($w, $box, 4, 3, 5, 4, 3, 18, self::SANDSTONE, 0, self::SANDSTONE, 0, false);
		$this->fillWithBlocks($w, $box, $width - 5, 3, 5, $width - 5, 3, 17, self::SANDSTONE, 0, self::SANDSTONE, 0, false);
		$this->fillWithBlocks($w, $box, 3, 1, 5, 4, 2, 16, self::AIR, 0, self::AIR, 0, false);
		$this->fillWithBlocks($w, $box, $width - 6, 1, 5, $width - 5, 2, 16, self::AIR, 0, self::AIR, 0, false);

		for($i1 = 5; $i1 <= 17; $i1 += 2){
			$this->placeBlock($w, self::SANDSTONE, 2, 4, 1, $i1, $box);
			$this->placeBlock($w, self::SANDSTONE, 1, 4, 2, $i1, $box);
			$this->placeBlock($w, self::SANDSTONE, 2, $width - 5, 1, $i1, $box);
			$this->placeBlock($w, self::SANDSTONE, 1, $width - 5, 2, $i1, $box);
		}

		$this->placeBlock($w, self::WOOL, $b0, 10, 0, 7, $box);
		$this->placeBlock($w, self::WOOL, $b0, 10, 0, 8, $box);
		$this->placeBlock($w, self::WOOL, $b0, 9, 0, 9, $box);
		$this->placeBlock($w, self::WOOL, $b0, 11, 0, 9, $box);
		$this->placeBlock($w, self::WOOL, $b0, 8, 0, 10, $box);
		$this->placeBlock($w, self::WOOL, $b0, 12, 0, 10, $box);
		$this->placeBlock($w, self::WOOL, $b0, 7, 0, 10, $box);
		$this->placeBlock($w, self::WOOL, $b0, 13, 0, 10, $box);
		$this->placeBlock($w, self::WOOL, $b0, 9, 0, 11, $box);
		$this->placeBlock($w, self::WOOL, $b0, 11, 0, 11, $box);
		$this->placeBlock($w, self::WOOL, $b0, 10, 0, 12, $box);
		$this->placeBlock($w, self::WOOL, $b0, 10, 0, 13, $box);
		$this->placeBlock($w, self::WOOL, $b1, 10, 0, 10, $box);

		for($i1 = 0; $i1 <= $width - 1; $i1 += $width - 1){
			$this->placeBlock($w, self::SANDSTONE, 2, $i1, 2, 1, $box);
			$this->placeBlock($w, self::WOOL, $b0, $i1, 2, 2, $box);
			$this->placeBlock($w, self::SANDSTONE, 2, $i1, 2, 3, $box);
			$this->placeBlock($w, self::SANDSTONE, 2, $i1, 3, 1, $box);
			$this->placeBlock($w, self::WOOL, $b0, $i1, 3, 2, $box);
			$this->placeBlock($w, self::SANDSTONE, 2, $i1, 3, 3, $box);
			$this->placeBlock($w, self::WOOL, $b0, $i1, 4, 1, $box);
			$this->placeBlock($w, self::SANDSTONE, 1, $i1, 4, 2, $box);
			$this->placeBlock($w, self::WOOL, $b0, $i1, 4, 3, $box);
			$this->placeBlock($w, self::SANDSTONE, 2, $i1, 5, 1, $box);
			$this->placeBlock($w, self::WOOL, $b0, $i1, 5, 2, $box);
			$this->placeBlock($w, self::SANDSTONE, 2, $i1, 5, 3, $box);
			$this->placeBlock($w, self::WOOL, $b0, $i1, 6, 1, $box);
			$this->placeBlock($w, self::SANDSTONE, 1, $i1, 6, 2, $box);
			$this->placeBlock($w, self::WOOL, $b0, $i1, 6, 3, $box);
			$this->placeBlock($w, self::WOOL, $b0, $i1, 7, 1, $box);
			$this->placeBlock($w, self::WOOL, $b0, $i1, 7, 2, $box);
			$this->placeBlock($w, self::WOOL, $b0, $i1, 7, 3, $box);
			$this->placeBlock($w, self::SANDSTONE, 2, $i1, 8, 1, $box);
			$this->placeBlock($w, self::SANDSTONE, 2, $i1, 8, 2, $box);
			$this->placeBlock($w, self::SANDSTONE, 2, $i1, 8, 3, $box);
		}

		for($i1 = 2; $i1 <= $width - 3; $i1 += $width - 5){
			$this->placeBlock($w, self::SANDSTONE, 2, $i1 - 1, 2, 0, $box);
			$this->placeBlock($w, self::WOOL, $b0, $i1, 2, 0, $box);
			$this->placeBlock($w, self::SANDSTONE, 2, $i1 + 1, 2, 0, $box);
			$this->placeBlock($w, self::SANDSTONE, 2, $i1 - 1, 3, 0, $box);
			$this->placeBlock($w, self::WOOL, $b0, $i1, 3, 0, $box);
			$this->placeBlock($w, self::SANDSTONE, 2, $i1 + 1, 3, 0, $box);
			$this->placeBlock($w, self::WOOL, $b0, $i1 - 1, 4, 0, $box);
			$this->placeBlock($w, self::SANDSTONE, 1, $i1, 4, 0, $box);
			$this->placeBlock($w, self::WOOL, $b0, $i1 + 1, 4, 0, $box);
			$this->placeBlock($w, self::SANDSTONE, 2, $i1 - 1, 5, 0, $box);
			$this->placeBlock($w, self::WOOL, $b0, $i1, 5, 0, $box);
			$this->placeBlock($w, self::SANDSTONE, 2, $i1 + 1, 5, 0, $box);
			$this->placeBlock($w, self::WOOL, $b0, $i1 - 1, 6, 0, $box);
			$this->placeBlock($w, self::SANDSTONE, 1, $i1, 6, 0, $box);
			$this->placeBlock($w, self::WOOL, $b0, $i1 + 1, 6, 0, $box);
			$this->placeBlock($w, self::WOOL, $b0, $i1 - 1, 7, 0, $box);
			$this->placeBlock($w, self::WOOL, $b0, $i1, 7, 0, $box);
			$this->placeBlock($w, self::WOOL, $b0, $i1 + 1, 7, 0, $box);
			$this->placeBlock($w, self::SANDSTONE, 2, $i1 - 1, 8, 0, $box);
			$this->placeBlock($w, self::SANDSTONE, 2, $i1, 8, 0, $box);
			$this->placeBlock($w, self::SANDSTONE, 2, $i1 + 1, 8, 0, $box);
		}

		$this->fillWithBlocks($w, $box, 8, 4, 0, 12, 6, 0, self::SANDSTONE, 2, self::SANDSTONE, 2, false);
		$this->placeBlock($w, self::AIR, 0, 8, 6, 0, $box);
		$this->placeBlock($w, self::AIR, 0, 12, 6, 0, $box);
		$this->placeBlock($w, self::WOOL, $b0, 9, 5, 0, $box);
		$this->placeBlock($w, self::SANDSTONE, 1, 10, 5, 0, $box);
		$this->placeBlock($w, self::WOOL, $b0, 11, 5, 0, $box);

		$this->fillWithBlocks($w, $box, 8, -14, 8, 12, -11, 12, self::SANDSTONE, 2, self::SANDSTONE, 2, false);
		$this->fillWithBlocks($w, $box, 8, -10, 8, 12, -10, 12, self::SANDSTONE, 1, self::SANDSTONE, 1, false);
		$this->fillWithBlocks($w, $box, 8, -9, 8, 12, -9, 12, self::SANDSTONE, 2, self::SANDSTONE, 2, false);
		$this->fillWithBlocks($w, $box, 8, -8, 8, 12, -1, 12, self::SANDSTONE, 0, self::SANDSTONE, 0, false);
		$this->fillWithBlocks($w, $box, 9, -11, 9, 11, -1, 11, self::AIR, 0, self::AIR, 0, false);
		$this->placeBlock($w, self::PRESSURE_PLATE, 0, 10, -11, 10, $box);
		$this->fillWithBlocks($w, $box, 9, -13, 9, 11, -13, 11, self::TNT, 0, self::AIR, 0, false);
		$this->placeBlock($w, self::AIR, 0, 8, -11, 10, $box);
		$this->placeBlock($w, self::AIR, 0, 8, -10, 10, $box);
		$this->placeBlock($w, self::SANDSTONE, 1, 7, -10, 10, $box);
		$this->placeBlock($w, self::SANDSTONE, 2, 7, -11, 10, $box);
		$this->placeBlock($w, self::AIR, 0, 12, -11, 10, $box);
		$this->placeBlock($w, self::AIR, 0, 12, -10, 10, $box);
		$this->placeBlock($w, self::SANDSTONE, 1, 13, -10, 10, $box);
		$this->placeBlock($w, self::SANDSTONE, 2, 13, -11, 10, $box);
		$this->placeBlock($w, self::AIR, 0, 10, -11, 8, $box);
		$this->placeBlock($w, self::AIR, 0, 10, -10, 8, $box);
		$this->placeBlock($w, self::SANDSTONE, 1, 10, -10, 7, $box);
		$this->placeBlock($w, self::SANDSTONE, 2, 10, -11, 7, $box);
		$this->placeBlock($w, self::AIR, 0, 10, -11, 12, $box);
		$this->placeBlock($w, self::AIR, 0, 10, -10, 12, $box);
		$this->placeBlock($w, self::SANDSTONE, 1, 10, -10, 13, $box);
		$this->placeBlock($w, self::SANDSTONE, 2, 10, -11, 13, $box);

		for($i1 = 0; $i1 < 4; $i1++){
			if(!$this->hasPlacedChest[$i1]){
				$dx = 0;
				$dz = 0;
				switch($i1){
					case 0: $dz = -2; break;
					case 1: $dx = 2; break;
					case 2: $dz = 2; break;
					case 3: $dx = -2; break;
				}
				$this->hasPlacedChest[$i1] = $this->generateChest($w, $box, $rnd, 10 + $dx, -11, 10 + $dz, 'desert');
			}
		}

		return true;
	}
}
