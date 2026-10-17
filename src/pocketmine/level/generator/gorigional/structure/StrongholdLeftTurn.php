<?php

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
