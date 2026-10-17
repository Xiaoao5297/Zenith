<?php

namespace pocketmine\level\generator\gorigional\structure;

use pocketmine\level\generator\gorigional\JavaRandom;

/**
 * 向下楼梯，参考 Java StructureStrongholdPieces$Stairs。
 */
class StrongholdStairs extends StrongholdPiece{

	public function __construct($type, JavaRandom $rnd, BoundingBox $box, $facing){
		parent::__construct($type);
		$this->coordBaseMode = $facing;
		$this->entryDoor = $rnd->nextBoundedInt(5) <= 1 ? 0 : 2;
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
		$this->generateShell($w, $rnd, $box, 4, 10, 4);
		$this->generateDoor($w, $box, 1, 7, 0, $this->entryDoor);
		$this->generateDoor($w, $box, 1, 1, 4, 0);

		// 中间 3 格宽的楼梯，从入口处逐级下降。
		for($z = 0; $z <= 4; $z++){
			$y = 7 - $z;
			$this->fillWithAir($w, $box, 1, 1, $z, 3, max(1, $y), $z);
			$this->fillWithBlocks($w, $box, 1, max(1, $y), $z, 3, max(1, $y), $z, self::STONE_BRICK_STAIRS, 0, self::STONE_BRICK_STAIRS, 0, false);
		}

		foreach([[2, 6, 1], [1, 5, 1], [1, 5, 2], [1, 4, 3], [2, 4, 3], [3, 3, 3], [3, 3, 2], [3, 2, 1], [2, 2, 1], [1, 1, 1]] as $p){
			$this->placeBlock($w, self::STONE_BRICKS, 0, $p[0], $p[1], $p[2], $box);
		}
		return true;
	}
}
