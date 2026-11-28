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
 * 向下楼梯，参考 Java StructureStrongholdPieces$Stairs。
 */
class StrongholdStairs extends StrongholdPiece{

	public function __construct($type, JavaRandom $rnd, BoundingBox $box, $facing){
		parent::__construct($type);
		$this->coordBaseMode = $facing;
		$this->entryDoor = $this->createEntryDoor($rnd);
		$this->boundingBox = $box;
	}

	public static function create(array $list, JavaRandom $rnd, $x, $y, $z, $facing, $type){
		$box = self::makeBox($x, $y, $z, -1, -7, 0, 5, 11, 5, $facing);
		if(!self::validBox($box) || self::findIntersecting($list, $box) !== null){
			return null;
		}
		return new static($type, $rnd, $box, $facing);
	}

	public function buildComponent(StructureComponent $component = null, array &$components, JavaRandom $rnd){
		$this->createForward($component, $components, $rnd, 1, 1);
	}

	public function addComponentParts(WorldAccess $w, JavaRandom $rnd, BoundingBox $box){
		if($this->isLiquidInStructureBoundingBox($w, $box)){
			return;
		}
		$this->generateShell($w, $rnd, $box, 4, 10, 4);
		$this->generateDoor($w, $box, 1, 7, 0, $this->entryDoor);
		$this->generateDoor($w, $box, 1, 1, 4, 0);

		foreach([[2, 6, 1], [1, 5, 1], [1, 5, 2], [1, 4, 3], [2, 4, 3], [3, 3, 3], [3, 3, 2], [3, 2, 1], [2, 2, 1], [1, 1, 1]] as $p){
			$this->placeBlock($w, self::STONE_BRICKS, 0, $p[0], $p[1], $p[2], $box);
		}
		foreach([[1, 6, 1], [1, 5, 3], [3, 4, 3], [3, 3, 1], [1, 2, 1], [1, 1, 3]] as $p){
			$this->placeBlock($w, 44, 0, $p[0], $p[1], $p[2], $box);
		}
		return true;
	}
}
