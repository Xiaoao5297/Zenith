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
 * 带箱子的走廊，参考 Java StructureStrongholdPieces$ChestCorridor。
 */
class StrongholdChestCorridor extends StrongholdPiece{
	private $chestPlaced = false;

	public function __construct($type, JavaRandom $rnd, BoundingBox $box, $facing){
		parent::__construct($type);
		$this->coordBaseMode = $facing;
		$this->entryDoor = $this->createEntryDoor($rnd);
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
		if($this->isLiquidInStructureBoundingBox($w, $box)){
			return;
		}
		$this->generateShell($w, $rnd, $box, 4, 4, 6);
		$this->generateDoor($w, $box, 1, 1, 0, $this->entryDoor);
		$this->generateDoor($w, $box, 1, 1, 6, 0);

		$this->fillWithBlocks($w, $box, 3, 1, 2, 3, 1, 4, self::STONE_BRICKS, 0, self::STONE_BRICKS, 0, false);
		$this->placeBlock($w, 44, 5, 3, 1, 1, $box);
		$this->placeBlock($w, 44, 5, 3, 1, 5, $box);
		$this->placeBlock($w, 44, 5, 3, 2, 2, $box);
		$this->placeBlock($w, 44, 5, 3, 2, 4, $box);
		for($i = 2; $i <= 4; $i++){
			$this->placeBlock($w, 44, 5, 2, 1, $i, $box);
		}

		if(!$this->chestPlaced){
			$worldY = $this->getYWithOffset(2);
			$worldX = $this->getXWithOffset(3, 3);
			$worldZ = $this->getZWithOffset(3, 3);
			if($box->resultIsInside($worldX, $worldY, $worldZ)){
				$this->chestPlaced = true;
				$stacks = 2 + $rnd->nextBoundedInt(2);
				$w->setBlock($worldX, $worldY, $worldZ, 54, 0);
				$w->setChestContents($worldX, $worldY, $worldZ, $this->createLoot($rnd, 'stronghold_chest', $stacks));
			}
		}
		return true;
	}
}
