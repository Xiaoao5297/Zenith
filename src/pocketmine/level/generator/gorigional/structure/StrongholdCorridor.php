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
 * 移植自 Java 1.7.10 StructureStrongholdPieces$Corridor。
 */
class StrongholdCorridor extends StrongholdPiece{
	/** @var int */
	private $steps;

	public function __construct($type, JavaRandom $rnd, BoundingBox $box, $facing){
		parent::__construct($type);
		$this->coordBaseMode = $facing;
		$this->entryDoor = 0;
		$this->boundingBox = $box;
		$this->steps = ($facing !== 2 && $facing !== 0) ? $box->getXSize() : $box->getZSize();
	}

	public static function create(array $list, JavaRandom $rnd, $x, $y, $z, $facing, $type){
		$box = self::makeBox($x, $y, $z, -1, -1, 0, 5, 5, 4, $facing);
		$intersecting = self::findIntersecting($list, $box);
		if($intersecting === null){
			return null;
		}

		if($intersecting->getBoundingBox()->minY === $box->minY){
			for($length = 3; $length >= 1; $length--){
				$shrunk = self::makeBox($x, $y, $z, -1, -1, 0, 5, 5, $length - 1, $facing);
				if(!$intersecting->getBoundingBox()->intersectsWith($shrunk)){
					$box = self::makeBox($x, $y, $z, -1, -1, 0, 5, 5, $length, $facing);
					if($box->minY > 1){
						return new self($type, $rnd, $box, $facing);
					}
					return null;
				}
			}
		}

		return null;
	}

	public function buildComponent(StructureComponent $component = null, array &$components, JavaRandom $rnd){
		// 兜底走廊不再派生。
	}

	public function addComponentParts(WorldAccess $w, JavaRandom $rnd, BoundingBox $box){
		if($this->isLiquidInStructureBoundingBox($w, $box)){
			return;
		}
		for($i = 0; $i < $this->steps; $i++){
			$this->placeBlock($w, self::STONE_BRICKS, 0, 0, 0, $i, $box);
			$this->placeBlock($w, self::STONE_BRICKS, 0, 1, 0, $i, $box);
			$this->placeBlock($w, self::STONE_BRICKS, 0, 2, 0, $i, $box);
			$this->placeBlock($w, self::STONE_BRICKS, 0, 3, 0, $i, $box);
			$this->placeBlock($w, self::STONE_BRICKS, 0, 4, 0, $i, $box);

			for($j = 1; $j <= 3; $j++){
				$this->placeBlock($w, self::STONE_BRICKS, 0, 0, $j, $i, $box);
				$this->placeBlock($w, self::AIR, 0, 1, $j, $i, $box);
				$this->placeBlock($w, self::AIR, 0, 2, $j, $i, $box);
				$this->placeBlock($w, self::AIR, 0, 3, $j, $i, $box);
				$this->placeBlock($w, self::STONE_BRICKS, 0, 4, $j, $i, $box);
			}

			$this->placeBlock($w, self::STONE_BRICKS, 0, 0, 4, $i, $box);
			$this->placeBlock($w, self::STONE_BRICKS, 0, 1, 4, $i, $box);
			$this->placeBlock($w, self::STONE_BRICKS, 0, 2, 4, $i, $box);
			$this->placeBlock($w, self::STONE_BRICKS, 0, 3, 4, $i, $box);
			$this->placeBlock($w, self::STONE_BRICKS, 0, 4, 4, $i, $box);
		}

		return true;
	}
}
