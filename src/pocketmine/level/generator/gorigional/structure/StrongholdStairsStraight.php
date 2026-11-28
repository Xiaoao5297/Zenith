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
 * 长直楼梯，参考 Java StructureStrongholdPieces$StairsStraight。
 */
class StrongholdStairsStraight extends StrongholdPiece{

	public function __construct($type, JavaRandom $rnd, BoundingBox $box, $facing){
		parent::__construct($type);
		$this->coordBaseMode = $facing;
		$this->entryDoor = $this->createEntryDoor($rnd);
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
		if($this->isLiquidInStructureBoundingBox($w, $box)){
			return;
		}
		$this->generateShell($w, $rnd, $box, 4, 10, 7);
		$this->generateDoor($w, $box, 1, 7, 0, $this->entryDoor);
		$this->generateDoor($w, $box, 1, 1, 7, 0);

		$meta = $this->getMetadataWithOffset(67, 2);
		for($j = 0; $j < 6; $j++){
			$this->placeBlock($w, 67, $meta, 1, 6 - $j, 1 + $j, $box);
			$this->placeBlock($w, 67, $meta, 2, 6 - $j, 1 + $j, $box);
			$this->placeBlock($w, 67, $meta, 3, 6 - $j, 1 + $j, $box);
			if($j < 5){
				$this->placeBlock($w, self::STONE_BRICKS, 0, 1, 5 - $j, 1 + $j, $box);
				$this->placeBlock($w, self::STONE_BRICKS, 0, 2, 5 - $j, 1 + $j, $box);
				$this->placeBlock($w, self::STONE_BRICKS, 0, 3, 5 - $j, 1 + $j, $box);
			}
		}
		return true;
	}
}
