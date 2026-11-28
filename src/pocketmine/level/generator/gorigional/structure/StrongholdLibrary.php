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
 * it under the terms of the GNU General Public License as published by
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
 * 图书馆，参考 Java StructureStrongholdPieces$Library。
 */
class StrongholdLibrary extends StrongholdPiece{
	/** @var bool */
	private $tall;

	public function __construct($type, JavaRandom $rnd, BoundingBox $box, $facing){
		parent::__construct($type);
		$this->coordBaseMode = $facing;
		$this->entryDoor = $this->createEntryDoor($rnd);
		$this->boundingBox = $box;
		$this->tall = $box->getYSize() > 6;
	}

	public static function create(array $list, JavaRandom $rnd, $x, $y, $z, $facing, $type){
		$box = self::makeBox($x, $y, $z, -4, -1, 0, 14, 11, 15, $facing);
		if(!self::validBox($box) || self::findIntersecting($list, $box) !== null){
			$box = self::makeBox($x, $y, $z, -4, -1, 0, 14, 6, 15, $facing);
			if(!self::validBox($box) || self::findIntersecting($list, $box) !== null){
				return null;
			}
		}
		return new self($type, $rnd, $box, $facing);
	}

	public function buildComponent(StructureComponent $component = null, array &$components, JavaRandom $rnd){
		$this->createForward($component, $components, $rnd, 4, 1);
	}

	public function addComponentParts(WorldAccess $w, JavaRandom $rnd, BoundingBox $box){
		if($this->isLiquidInStructureBoundingBox($w, $box)){
			return;
		}
		$maxY = $this->tall ? 10 : 5;
		$this->generateShell($w, $rnd, $box, 13, $maxY, 14);
		$this->generateDoor($w, $box, 4, 1, 0, $this->entryDoor);
		$this->randomlyFillWithBlocks($w, $box, $rnd, 0.07, 2, 1, 1, 11, 4, 13, 30, 30, false);

		for($i = 1; $i <= 13; $i++){
			if(($i - 1) % 4 === 0){
				$this->fillWithBlocks($w, $box, 1, 1, $i, 1, 4, $i, self::PLANKS, 0, self::PLANKS, 0, false);
				$this->fillWithBlocks($w, $box, 12, 1, $i, 12, 4, $i, self::PLANKS, 0, self::PLANKS, 0, false);
				$this->placeBlock($w, self::TORCH, 0, 2, 3, $i, $box);
				$this->placeBlock($w, self::TORCH, 0, 11, 3, $i, $box);
				if($this->tall){
					$this->fillWithBlocks($w, $box, 1, 6, $i, 1, 9, $i, self::PLANKS, 0, self::PLANKS, 0, false);
					$this->fillWithBlocks($w, $box, 12, 6, $i, 12, 9, $i, self::PLANKS, 0, self::PLANKS, 0, false);
				}
			}else{
				$this->fillWithBlocks($w, $box, 1, 1, $i, 1, 4, $i, self::BOOKSHELF, 0, self::BOOKSHELF, 0, false);
				$this->fillWithBlocks($w, $box, 12, 1, $i, 12, 4, $i, self::BOOKSHELF, 0, self::BOOKSHELF, 0, false);
				if($this->tall){
					$this->fillWithBlocks($w, $box, 1, 6, $i, 1, 9, $i, self::BOOKSHELF, 0, self::BOOKSHELF, 0, false);
					$this->fillWithBlocks($w, $box, 12, 6, $i, 12, 9, $i, self::BOOKSHELF, 0, self::BOOKSHELF, 0, false);
				}
			}
		}

		for($i = 3; $i < 12; $i += 2){
			$this->fillWithBlocks($w, $box, 3, 1, $i, 4, 3, $i, self::BOOKSHELF, 0, self::BOOKSHELF, 0, false);
			$this->fillWithBlocks($w, $box, 6, 1, $i, 7, 3, $i, self::BOOKSHELF, 0, self::BOOKSHELF, 0, false);
			$this->fillWithBlocks($w, $box, 9, 1, $i, 10, 3, $i, self::BOOKSHELF, 0, self::BOOKSHELF, 0, false);
		}

		if($this->tall){
			$this->fillWithBlocks($w, $box, 1, 5, 1, 3, 5, 13, self::PLANKS, 0, self::PLANKS, 0, false);
			$this->fillWithBlocks($w, $box, 10, 5, 1, 12, 5, 13, self::PLANKS, 0, self::PLANKS, 0, false);
			$this->fillWithBlocks($w, $box, 4, 5, 1, 9, 5, 2, self::PLANKS, 0, self::PLANKS, 0, false);
			$this->fillWithBlocks($w, $box, 4, 5, 12, 9, 5, 13, self::PLANKS, 0, self::PLANKS, 0, false);
			$this->placeBlock($w, self::PLANKS, 0, 9, 5, 11, $box);
			$this->placeBlock($w, self::PLANKS, 0, 8, 5, 11, $box);
			$this->placeBlock($w, self::PLANKS, 0, 9, 5, 10, $box);

			$this->fillWithBlocks($w, $box, 3, 6, 2, 3, 6, 12, self::FENCE, 0, self::FENCE, 0, false);
			$this->fillWithBlocks($w, $box, 10, 6, 2, 10, 6, 10, self::FENCE, 0, self::FENCE, 0, false);
			$this->fillWithBlocks($w, $box, 4, 6, 2, 9, 6, 2, self::FENCE, 0, self::FENCE, 0, false);
			$this->fillWithBlocks($w, $box, 4, 6, 12, 8, 6, 12, self::FENCE, 0, self::FENCE, 0, false);
			$this->placeBlock($w, self::FENCE, 0, 9, 6, 11, $box);
			$this->placeBlock($w, self::FENCE, 0, 8, 6, 11, $box);
			$this->placeBlock($w, self::FENCE, 0, 9, 6, 10, $box);

			$ladderMeta = $this->getMetadataWithOffset(self::LADDER, 3);
			for($i = 1; $i <= 7; $i++){
				$this->placeBlock($w, self::LADDER, $ladderMeta, 10, $i, 13, $box);
			}

			foreach([[6, 9, 7], [7, 9, 7], [6, 8, 7], [7, 8, 7], [6, 7, 7], [7, 7, 7], [5, 7, 7], [8, 7, 7], [6, 7, 6], [6, 7, 8], [7, 7, 6], [7, 7, 8]] as $p){
				$this->placeBlock($w, self::FENCE, 0, $p[0], $p[1], $p[2], $box);
			}
			foreach([[5, 8, 7], [8, 8, 7], [6, 8, 6], [6, 8, 8], [7, 8, 6], [7, 8, 8]] as $p){
				$this->placeBlock($w, self::TORCH, 0, $p[0], $p[1], $p[2], $box);
			}
		}

		$this->generateChest($w, $box, $rnd, 3, 3, 5, 'stronghold_library', 1 + $rnd->nextBoundedInt(4));
		if($this->tall){
			$this->placeBlock($w, self::AIR, 0, 12, 9, 1, $box);
			$this->generateChest($w, $box, $rnd, 12, 8, 1, 'stronghold_library', 1 + $rnd->nextBoundedInt(4));
		}
		return true;
	}
}
