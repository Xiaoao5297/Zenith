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
 * 带箱子的走廊，参考 Java StructureStrongholdPieces$ChestCorridor。
 */
class StrongholdChestCorridor extends StrongholdPiece{
	private $chestPlaced = false;

	public function __construct($type, JavaRandom $rnd, BoundingBox $box, $facing){
		parent::__construct($type);
		$this->coordBaseMode = $facing;
		$this->entryDoor = $rnd->nextBoundedInt(5) <= 1 ? 0 : 2;
		$this->boundingBox = $box;
	}

	public static function create(array $list, JavaRandom $rnd, $x, $y, $z, $facing, $type){
		$box = self::makeBox($x, $y, $z, -1, -1, 0, 5, 5, 7, $facing);
		if(!self::validBox($box) || self::findIntersecting($list, $box) !== null){
			return null;
		}
		return new self($type, $rnd, $box, $facing);
	}

	public function buildComponent(StructureComponent $component = null, array &$components, JavaRandom $rnd){
		$this->createForward($component, $components, $rnd, 1, 1);
	}

	public function addComponentParts(WorldAccess $w, JavaRandom $rnd, BoundingBox $box){
		$this->generateShell($w, $rnd, $box, 4, 4, 6);
		$this->generateDoor($w, $box, 1, 1, 0, $this->entryDoor);
		$this->generateDoor($w, $box, 1, 1, 6, 0);

		$this->fillWithBlocks($w, $box, 3, 1, 2, 3, 1, 4, self::STONE_BRICKS, 0, self::STONE_BRICKS, 0, false);
		$this->placeBlock($w, self::STONE_BRICK_STAIRS, 5, 3, 1, 1, $box);
		$this->placeBlock($w, self::STONE_BRICK_STAIRS, 5, 3, 1, 5, $box);

		if(!$this->chestPlaced){
			$this->chestPlaced = true;
			$this->generateChest($w, $box, $rnd, 3, 2, 3, 'stronghold');
		}
		return true;
	}
}
