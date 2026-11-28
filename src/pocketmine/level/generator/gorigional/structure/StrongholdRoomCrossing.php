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
 * 大房间/十字房间，参考 Java StructureStrongholdPieces$RoomCrossing。
 */
class StrongholdRoomCrossing extends StrongholdPiece{
	/** @var int */
	private $roomType;

	public function __construct($type, JavaRandom $rnd, BoundingBox $box, $facing){
		parent::__construct($type);
		$this->coordBaseMode = $facing;
		$this->entryDoor = $this->createEntryDoor($rnd);
		$this->roomType = $rnd->nextBoundedInt(5);
		$this->boundingBox = $box;
	}

	public static function create(array $list, JavaRandom $rnd, $x, $y, $z, $facing, $type){
		$box = self::makeBox($x, $y, $z, -4, -1, 0, 11, 7, 11, $facing);
		if(!self::validBox($box) || self::findIntersecting($list, $box) !== null){
			return null;
		}
		return new self($type, $rnd, $box, $facing);
	}

	public function buildComponent(StructureComponent $component = null, array &$components, JavaRandom $rnd){
		$this->createForward($component, $components, $rnd, 4, 1);
		$this->createLeft($component, $components, $rnd, 1, 4);
		$this->createRight($component, $components, $rnd, 1, 4);
	}

	public function addComponentParts(WorldAccess $w, JavaRandom $rnd, BoundingBox $box){
		if($this->isLiquidInStructureBoundingBox($w, $box)){
			return;
		}
		$this->generateShell($w, $rnd, $box, 10, 6, 10);
		$this->generateDoor($w, $box, 4, 1, 0, $this->entryDoor);
		$this->fillWithAir($w, $box, 4, 1, 10, 6, 3, 10);
		$this->fillWithAir($w, $box, 0, 1, 4, 0, 3, 6);
		$this->fillWithAir($w, $box, 10, 1, 4, 10, 3, 6);

		if($this->roomType === 0){
			$this->fillWithBlocks($w, $box, 5, 1, 5, 5, 3, 5, self::STONE_BRICKS, 0, self::STONE_BRICKS, 0, false);
			foreach([[4, 3, 5], [6, 3, 5], [5, 3, 4], [5, 3, 6]] as $p){
				$this->placeBlock($w, self::TORCH, 0, $p[0], $p[1], $p[2], $box);
			}
			foreach([[4, 1, 4], [4, 1, 5], [4, 1, 6], [6, 1, 4], [6, 1, 5], [6, 1, 6], [5, 1, 4], [5, 1, 6]] as $p){
				$this->placeBlock($w, 44, 0, $p[0], $p[1], $p[2], $box);
			}
		}elseif($this->roomType === 1){
			for($i = 0; $i < 5; $i++){
				$this->placeBlock($w, self::STONE_BRICKS, 0, 3, 1, 3 + $i, $box);
				$this->placeBlock($w, self::STONE_BRICKS, 0, 7, 1, 3 + $i, $box);
				$this->placeBlock($w, self::STONE_BRICKS, 0, 3 + $i, 1, 3, $box);
				$this->placeBlock($w, self::STONE_BRICKS, 0, 3 + $i, 1, 7, $box);
			}
			$this->fillWithBlocks($w, $box, 5, 1, 5, 5, 4, 5, self::STONE_BRICKS, 0, self::STONE_BRICKS, 0, false);
			$this->placeBlock($w, self::WATER, 0, 5, 4, 5, $box);
		}elseif($this->roomType === 2){
			for($i = 1; $i <= 9; $i++){
				$this->placeBlock($w, self::COBBLESTONE, 0, 1, 3, $i, $box);
				$this->placeBlock($w, self::COBBLESTONE, 0, 9, 3, $i, $box);
				$this->placeBlock($w, self::COBBLESTONE, 0, $i, 3, 1, $box);
				$this->placeBlock($w, self::COBBLESTONE, 0, $i, 3, 9, $box);
			}
			foreach([[5, 1, 4], [5, 1, 6], [5, 3, 4], [5, 3, 6], [4, 1, 5], [6, 1, 5], [4, 3, 5], [6, 3, 5]] as $p){
				$this->placeBlock($w, self::COBBLESTONE, 0, $p[0], $p[1], $p[2], $box);
			}
			for($i = 1; $i <= 3; $i++){
				foreach([[4, 4], [6, 4], [4, 6], [6, 6]] as $p){
					$this->placeBlock($w, self::COBBLESTONE, 0, $p[0], $i, $p[1], $box);
				}
			}
			$this->placeBlock($w, self::TORCH, 0, 5, 3, 5, $box);
			for($i = 2; $i <= 8; $i++){
				$this->placeBlock($w, self::PLANKS, 0, 2, 3, $i, $box);
				$this->placeBlock($w, self::PLANKS, 0, 3, 3, $i, $box);
				$this->placeBlock($w, self::PLANKS, 0, 7, 3, $i, $box);
				$this->placeBlock($w, self::PLANKS, 0, 8, 3, $i, $box);
				if($i <= 3 || $i >= 7){
					foreach([4, 5, 6] as $x){
						$this->placeBlock($w, self::PLANKS, 0, $x, 3, $i, $box);
					}
				}
			}
			$ladderMeta = $this->getMetadataWithOffset(self::LADDER, 4);
			for($i = 1; $i <= 3; $i++){
				$this->placeBlock($w, self::LADDER, $ladderMeta, 9, $i, 3, $box);
			}
		}

		if($this->roomType === 2){
			$this->generateChest($w, $box, $rnd, 3, 4, 8, 'stronghold_crossing', 1 + $rnd->nextBoundedInt(4));
		}
		return true;
	}
}
