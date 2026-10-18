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
 * 兜底短走廊，参考 Java StructureStrongholdPieces$Corridor。
 * 当权重部件都放不下时使用，避免要塞断开。
 */
class StrongholdCorridor extends StrongholdPiece{

	public function __construct($type, JavaRandom $rnd, BoundingBox $box, $facing){
		parent::__construct($type);
		$this->coordBaseMode = $facing;
		$this->entryDoor = 0;
		$this->boundingBox = $box;
	}

	public static function create(array $list, JavaRandom $rnd, $x, $y, $z, $facing, $type){
		for($length = 7; $length >= 5; $length--){
			$box = self::makeBox($x, $y, $z, -1, -1, 0, 5, 5, $length, $facing);
			if(self::validBox($box) && self::findIntersecting($list, $box) === null){
				return new self($type, $rnd, $box, $facing);
			}
		}
		return null;
	}

	public function buildComponent(StructureComponent $component = null, array &$components, JavaRandom $rnd){
		// 兜底走廊不再派生。
	}

	public function addComponentParts(WorldAccess $w, JavaRandom $rnd, BoundingBox $box){
		$maxZ = ($this->coordBaseMode === 1 || $this->coordBaseMode === 3 ? $this->boundingBox->getXSize() : $this->boundingBox->getZSize()) - 1;
		$this->generateShell($w, $rnd, $box, 4, 4, $maxZ);
		$this->generateDoor($w, $box, 1, 1, 0, 0);
		$this->generateDoor($w, $box, 1, 1, $maxZ, 0);
		return true;
	}
}
