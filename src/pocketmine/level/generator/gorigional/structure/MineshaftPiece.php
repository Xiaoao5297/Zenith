<?php

namespace pocketmine\level\generator\gorigional\structure;

use pocketmine\level\generator\gorigional\JavaRandom;

/**
 * 移植自 SCAXE-GO-CE structure.MineshaftPiece 及辅助函数。
 */
abstract class MineshaftPiece extends StructureComponentBase{

	public function __construct($componentType, BoundingBox $box, $facing){
		$this->componentType = $componentType;
		$this->boundingBox = $box;
		$this->coordBaseMode = $facing;
	}

	public static function createRandomShaftPiece(array &$components, JavaRandom $rnd, $x, $y, $z, $facing, $typeId){
		$i = $rnd->nextBoundedInt(100);
		if($i >= 80){
			$box = self::getCrossingBoundingBox($components, $rnd, $x, $y, $z, $facing);
			if($box !== null){
				return new MineshaftCrossing($rnd, $box, $facing);
			}
		}elseif($i >= 70){
			$box = self::getStairsBoundingBox($components, $rnd, $x, $y, $z, $facing);
			if($box !== null){
				return new MineshaftStairs($rnd, $box, $facing);
			}
		}else{
			$box = self::getCorridorBoundingBox($components, $rnd, $x, $y, $z, $facing);
			if($box !== null){
				return new MineshaftCorridor($rnd, $box, $facing);
			}
		}
		return null;
	}

	public static function getNextMineshaftComponent(StructureComponent $component, array &$components, JavaRandom $rnd, $x, $y, $z, $facing, $typeId){
		if($typeId > 8){
			return null;
		}
		return self::createRandomShaftPiece($components, $rnd, $x, $y, $z, $facing, $typeId + 1);
	}

	public static function getCorridorBoundingBox(array $components, JavaRandom $rnd, $x, $y, $z, $facing){
		$length = 20;
		$box = BoundingBox::getComponentToAddBoundingBox($x, $y, $z, -1, 0, 0, 3, 3, $length, $facing);
		if(self::findIntersecting($components, $box) !== null){
			return null;
		}
		return $box;
	}

	public static function getCrossingBoundingBox(array $components, JavaRandom $rnd, $x, $y, $z, $facing){
		$box = BoundingBox::getComponentToAddBoundingBox($x, $y, $z, -2, 0, 0, 5, 3, 5, $facing);
		if(self::findIntersecting($components, $box) !== null){
			return null;
		}
		return $box;
	}

	public static function getStairsBoundingBox(array $components, JavaRandom $rnd, $x, $y, $z, $facing){
		$box = BoundingBox::getComponentToAddBoundingBox($x, $y, $z, -1, 0, 0, 5, 5, 5, $facing);
		if(self::findIntersecting($components, $box) !== null){
			return null;
		}
		return $box;
	}

	public static function findIntersecting(array $list, BoundingBox $box){
		foreach($list as $c){
			if($c->getBoundingBox() !== null && $c->getBoundingBox()->intersectsWith($box)){
				return $c;
			}
		}
		return null;
	}
}
