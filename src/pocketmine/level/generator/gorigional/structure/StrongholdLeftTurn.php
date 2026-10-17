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
 * 左转弯，参考 Java StructureStrongholdPieces$LeftTurn。
 */
class StrongholdLeftTurn extends StrongholdPiece{

	public function __construct($type, JavaRandom $rnd, BoundingBox $box, $facing){
		parent::__construct($type);
		$this->coordBaseMode = $facing;
		$this->entryDoor = $rnd->nextBoundedInt(5) <= 1 ? 0 : 2;
		$this->boundingBox = $box;
	}

	public static function create(array $list, JavaRandom $rnd, $x, $y, $z, $facing, $type){
		$box = self::makeBox($x, $y, $z, -1, -1, 0, 5, 5, 5, $facing);
		if(!self::validBox($box) || self::findIntersecting($list, $box) !== null){
			return null;
		}
		return new static($type, $rnd, $box, $facing);
	}

	public function buildComponent(StructureComponent $component = null, array &$components, JavaRandom $rnd){
		if($this->coordBaseMode !== 2 && $this->coordBaseMode !== 3){
			$this->createRight($component, $components, $rnd, 1, 1);
		}else{
			$this->createLeft($component, $components, $rnd, 1, 1);
		}
	}

	public function addComponentParts(WorldAccess $w, JavaRandom $rnd, BoundingBox $box){
		$this->generateShell($w, $rnd, $box, 4, 4, 4);
		$this->generateDoor($w, $box, 1, 1, 0, $this->entryDoor);

		if($this->coordBaseMode !== 2 && $this->coordBaseMode !== 3){
			$this->fillWithAir($w, $box, 4, 1, 1, 4, 3, 3);
		}else{
			$this->fillWithAir($w, $box, 0, 1, 1, 0, 3, 3);
		}
		return true;
	}
}
