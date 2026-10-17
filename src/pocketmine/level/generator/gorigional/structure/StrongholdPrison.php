<?php

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
		$this->generateShell($w, $rnd, $box, 8, 4, 10);
		$this->generateDoor($w, $box, 1, 1, 0, 0);

		// 两排牢房和铁栏杆。
		$this->fillWithBlocks($w, $box, 3, 1, 1, 3, 4, 9, self::STONE_BRICKS, 0, self::STONE_BRICKS, 0, false);
		$this->fillWithBlocks($w, $box, 6, 1, 1, 6, 4, 9, self::STONE_BRICKS, 0, self::STONE_BRICKS, 0, false);
		for($z = 2; $z <= 8; $z += 3){
			$this->fillWithBlocks($w, $box, 3, 1, $z, 3, 3, $z + 1, self::IRON_BARS, 0, self::IRON_BARS, 0, false);
			$this->fillWithBlocks($w, $box, 6, 1, $z, 6, 3, $z + 1, self::IRON_BARS, 0, self::IRON_BARS, 0, false);
		}

		foreach([[2, 1, 2], [7, 1, 2], [2, 1, 8], [7, 1, 8]] as $p){
			$this->placeBlock($w, self::MOSSY_STONE_BRICKS, 1, $p[0], $p[1], $p[2], $box);
		}

		if($rnd->nextBoundedInt(3) === 0){
			$this->generateChest($w, $box, $rnd, 2, 1, 5, 'stronghold');
		}
		return true;
	}
}
