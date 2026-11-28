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
 * 监牢，参考 Java StructureStrongholdPieces$Prison。
 */
class StrongholdPrison extends StrongholdPiece{

	public function __construct($type, JavaRandom $rnd, BoundingBox $box, $facing){
		parent::__construct($type);
		$this->coordBaseMode = $facing;
		$this->entryDoor = 0;
		$this->boundingBox = $box;
	}

	public static function create(array $list, JavaRandom $rnd, $x, $y, $z, $facing, $type){
		$box = self::makeBox($x, $y, $z, -1, -1, 0, 9, 5, 11, $facing);
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
		$this->generateShell($w, $rnd, $box, 8, 4, 10);
		$this->generateDoor($w, $box, 1, 1, 0, $this->entryDoor);

		$this->fillWithAir($w, $box, 1, 1, 10, 3, 3, 10);
		foreach([1, 3, 7, 9] as $z){
			$this->fillRandomBricks($w, $box, 4, 1, $z, 4, 3, $z, $rnd, false);
		}

		$this->fillWithBlocks($w, $box, 4, 1, 4, 4, 3, 6, self::IRON_BARS, 0, self::IRON_BARS, 0, false);
		$this->fillWithBlocks($w, $box, 5, 1, 5, 7, 3, 5, self::IRON_BARS, 0, self::IRON_BARS, 0, false);
		$this->placeBlock($w, self::IRON_BARS, 0, 4, 3, 2, $box);
		$this->placeBlock($w, self::IRON_BARS, 0, 4, 3, 8, $box);

		$doorMeta = $this->getMetadataWithOffset(71, 3);
		$this->placeBlock($w, 71, $doorMeta, 4, 1, 2, $box);
		$this->placeBlock($w, 71, $doorMeta + 8, 4, 2, 2, $box);
		$this->placeBlock($w, 71, $doorMeta, 4, 1, 8, $box);
		$this->placeBlock($w, 71, $doorMeta + 8, 4, 2, 8, $box);
		return true;
	}
}
