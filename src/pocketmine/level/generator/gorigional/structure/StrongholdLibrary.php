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
 * 图书馆，参考 Java StructureStrongholdPieces$Library。
 */
class StrongholdLibrary extends StrongholdPiece{
	/** @var bool */
	private $tall;

	public function __construct($type, JavaRandom $rnd, BoundingBox $box, $facing, $tall){
		parent::__construct($type);
		$this->coordBaseMode = $facing;
		$this->entryDoor = 0;
		$this->boundingBox = $box;
		$this->tall = $tall;
	}

	public static function create(array $list, JavaRandom $rnd, $x, $y, $z, $facing, $type){
		$tall = $rnd->nextBoundedInt(2) === 0;
		$height = $tall ? 11 : 6;
		$box = self::makeBox($x, $y, $z, -4, -1, 0, 14, $height, 15, $facing);
		if(!self::validBox($box) || self::findIntersecting($list, $box) !== null){
			return null;
		}
		return new self($type, $rnd, $box, $facing, $tall);
	}

	public function buildComponent(StructureComponent $component = null, array &$components, JavaRandom $rnd){
		$this->createForward($component, $components, $rnd, 4, 1);
	}

	public function addComponentParts(WorldAccess $w, JavaRandom $rnd, BoundingBox $box){
		$maxY = $this->tall ? 10 : 5;
		$this->generateShell($w, $rnd, $box, 13, $maxY, 14);
		$this->generateDoor($w, $box, 4, 1, 0, 0);

		// 两侧书架。
		for($z = 1; $z <= 13; $z++){
			$this->placeBlock($w, self::BOOKSHELF, 0, 1, 1, $z, $box);
			$this->placeBlock($w, self::BOOKSHELF, 0, 12, 1, $z, $box);
			if($this->tall){
				$this->placeBlock($w, self::BOOKSHELF, 0, 1, 2, $z, $box);
				$this->placeBlock($w, self::BOOKSHELF, 0, 12, 2, $z, $box);
			}
		}

		// 中央书桌和隔墙。
		$this->fillWithBlocks($w, $box, 5, 1, 5, 8, 1, 8, self::PLANKS, 0, self::PLANKS, 0, false);
		$this->fillWithBlocks($w, $box, 6, 2, 6, 7, 2, 7, self::BOOKSHELF, 0, self::BOOKSHELF, 0, false);
		if($this->tall){
			$this->fillWithBlocks($w, $box, 3, 1, 3, 3, 3, 11, self::STONE_BRICKS, 0, self::STONE_BRICKS, 0, false);
			$this->fillWithBlocks($w, $box, 10, 1, 3, 10, 3, 11, self::STONE_BRICKS, 0, self::STONE_BRICKS, 0, false);
			for($i = 0; $i < 3; $i++){
				$this->placeBlock($w, self::TORCH, 0, 4, 3, 2 + $i * 5, $box);
				$this->placeBlock($w, self::TORCH, 0, 9, 3, 2 + $i * 5, $box);
			}
		}
		return true;
	}
}
