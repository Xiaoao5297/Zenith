<?php

namespace pocketmine\level\generator\gorigional\structure;

use pocketmine\level\generator\gorigional\JavaRandom;

/**
 * 五岔路口，参考 Java StructureStrongholdPieces$Crossing。
 */
class StrongholdCrossing extends StrongholdPiece{
	private $leftLow;
	private $leftHigh;
	private $rightLow;
	private $rightHigh;

	public function __construct($type, JavaRandom $rnd, BoundingBox $box, $facing){
		parent::__construct($type);
		$this->coordBaseMode = $facing;
		$this->entryDoor = $rnd->nextBoundedInt(5) <= 1 ? 0 : 2;
		$this->leftLow = $rnd->nextBoundedInt(2) === 0;
		$this->leftHigh = $rnd->nextBoundedInt(2) === 0;
		$this->rightLow = $rnd->nextBoundedInt(2) === 0;
		$this->rightHigh = $rnd->nextBoundedInt(3) > 0;
		$this->boundingBox = $box;
	}

	public static function create(array $list, JavaRandom $rnd, $x, $y, $z, $facing, $type){
		$box = self::makeBox($x, $y, $z, -4, -3, 0, 10, 9, 11, $facing);
		if(!self::validBox($box) || self::findIntersecting($list, $box) !== null){
			return null;
		}
		return new self($type, $rnd, $box, $facing);
	}

	public function buildComponent(StructureComponent $component = null, array &$components, JavaRandom $rnd){
		$i = 3;
		$j = 5;
		if($this->coordBaseMode === 1 || $this->coordBaseMode === 2){
			$i = 8 - $i;
			$j = 8 - $j;
		}

		$this->createForward($component, $components, $rnd, 5, 1);
		if($this->leftLow){
			$this->createLeft($component, $components, $rnd, $i, 1);
		}
		if($this->leftHigh){
			$this->createLeft($component, $components, $rnd, $j, 7);
		}
		if($this->rightLow){
			$this->createRight($component, $components, $rnd, $i, 1);
		}
		if($this->rightHigh){
			$this->createRight($component, $components, $rnd, $j, 7);
		}
	}

	public function addComponentParts(WorldAccess $w, JavaRandom $rnd, BoundingBox $box){
		$this->generateShell($w, $rnd, $box, 9, 8, 10);
		$this->generateDoor($w, $box, 4, 3, 0, $this->entryDoor);

		if($this->leftLow){
			$this->fillWithAir($w, $box, 0, 3, 1, 0, 5, 3);
		}
		if($this->rightLow){
			$this->fillWithAir($w, $box, 9, 3, 1, 9, 5, 3);
		}
		if($this->leftHigh){
			$this->fillWithAir($w, $box, 0, 5, 7, 0, 7, 9);
		}
		if($this->rightHigh){
			$this->fillWithAir($w, $box, 9, 5, 7, 9, 7, 9);
		}

		$this->fillWithAir($w, $box, 5, 1, 10, 7, 3, 10);
		$this->fillWithBlocks($w, $box, 1, 2, 1, 8, 2, 6, self::STONE_BRICK_STAIRS, 0, self::STONE_BRICK_STAIRS, 0, false);
		$this->fillWithBlocks($w, $box, 4, 1, 5, 4, 4, 9, self::STONE_BRICKS, 0, self::STONE_BRICKS, 0, false);
		$this->fillWithBlocks($w, $box, 8, 1, 5, 8, 4, 9, self::STONE_BRICKS, 0, self::STONE_BRICKS, 0, false);
		$this->fillWithBlocks($w, $box, 1, 4, 7, 3, 4, 9, self::STONE_BRICKS, 0, self::STONE_BRICKS, 0, false);
		$this->fillWithBlocks($w, $box, 1, 3, 5, 3, 3, 6, self::STONE_BRICKS, 0, self::STONE_BRICKS, 0, false);
		$this->fillWithBlocks($w, $box, 1, 3, 4, 3, 3, 4, self::STONE_BRICKS, 0, self::STONE_BRICKS, 0, false);
		$this->fillWithBlocks($w, $box, 1, 4, 6, 3, 4, 6, self::STONE_BRICKS, 0, self::STONE_BRICKS, 0, false);
		$this->fillWithBlocks($w, $box, 5, 1, 7, 7, 1, 8, self::STONE_BRICKS, 0, self::STONE_BRICKS, 0, false);
		$this->fillWithBlocks($w, $box, 5, 1, 9, 7, 1, 9, self::STONE_BRICKS, 0, self::STONE_BRICKS, 0, false);
		$this->fillWithBlocks($w, $box, 5, 2, 7, 7, 2, 7, self::STONE_BRICKS, 0, self::STONE_BRICKS, 0, false);
		$this->fillWithBlocks($w, $box, 4, 5, 7, 4, 5, 9, self::STONE_BRICKS, 0, self::STONE_BRICKS, 0, false);
		$this->fillWithBlocks($w, $box, 8, 5, 7, 8, 5, 9, self::STONE_BRICKS, 0, self::STONE_BRICKS, 0, false);
		$this->fillWithBlocks($w, $box, 5, 5, 7, 7, 5, 9, self::STONE_BRICKS, 0, self::STONE_BRICKS, 0, false);
		$this->placeBlock($w, self::TORCH, 0, 6, 5, 6, $box);
		return true;
	}
}
