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
 * 长直楼梯，参考 Java StructureStrongholdPieces$StairsStraight。
 */
class StrongholdStairsStraight extends StrongholdPiece{

	public function __construct($type, JavaRandom $rnd, BoundingBox $box, $facing){
		parent::__construct($type);
		$this->coordBaseMode = $facing;
		$this->entryDoor = $rnd->nextBoundedInt(5) <= 1 ? 0 : 2;
		$this->boundingBox = $box;
	}

	public static function create(array $list, JavaRandom $rnd, $x, $y, $z, $facing, $type){
		$box = self::makeBox($x, $y, $z, -1, -7, 0, 5, 11, 8, $facing);
		if(!self::validBox($box) || self::findIntersecting($list, $box) !== null){
			return null;
		}
		return new self($type, $rnd, $box, $facing);
	}

	public function buildComponent(StructureComponent $component = null, array &$components, JavaRandom $rnd){
		$this->createForward($component, $components, $rnd, 1, 1);
	}

	public function addComponentParts(WorldAccess $w, JavaRandom $rnd, BoundingBox $box){
		$this->generateShell($w, $rnd, $box, 4, 10, 7);
		$this->generateDoor($w, $box, 1, 7, 0, $this->entryDoor);
		$this->generateDoor($w, $box, 1, 1, 7, 0);

		for($z = 0; $z <= 7; $z++){
			$y = 7 - intdiv($z * 6, 7);
			$y = max(1, $y);
			$this->fillWithAir($w, $box, 1, 1, $z, 3, $y, $z);
			$this->fillWithBlocks($w, $box, 1, $y, $z, 3, $y, $z, self::STONE_BRICK_STAIRS, 0, self::STONE_BRICK_STAIRS, 0, false);
		}
		return true;
	}
}
