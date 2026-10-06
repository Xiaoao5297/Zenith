<?php

namespace pocketmine\level\generator\gorigional\structure;

use pocketmine\level\generator\gorigional\JavaRandom;

/**
 * 移植自 Java 1.7.10 StructureMineshaftPieces 的选择逻辑。
 */
abstract class MineshaftPiece extends StructureComponentBase{

	public function __construct($componentType, BoundingBox $box, $facing){
		$this->componentType = $componentType;
		$this->boundingBox = $box;
		$this->coordBaseMode = $facing;
	}

	/**
	 * 对应 StructureMineshaftPieces.func_78815_a。
	 */
	public static function createRandomShaftPiece(array &$components, JavaRandom $rnd, $x, $y, $z, $facing, $typeId){
		$i = $rnd->nextBoundedInt(100);
		if($i >= 80){
			$box = MineshaftCrossing::getCrossingBoundingBox($components, $rnd, $x, $y, $z, $facing);
			if($box !== null){
				return new MineshaftCrossing($typeId, $rnd, $box, $facing);
			}
		}elseif($i >= 70){
			$box = MineshaftStairs::getStairsBoundingBox($components, $rnd, $x, $y, $z, $facing);
			if($box !== null){
				return new MineshaftStairs($typeId, $rnd, $box, $facing);
			}
		}else{
			$box = MineshaftCorridor::getCorridorBoundingBox($components, $rnd, $x, $y, $z, $facing);
			if($box !== null){
				return new MineshaftCorridor($typeId, $rnd, $box, $facing);
			}
		}
		return null;
	}

	/**
	 * 对应 StructureMineshaftPieces.func_78817_b。
	 */
	public static function getNextMineshaftComponent(StructureComponent $component, array &$components, JavaRandom $rnd, $x, $y, $z, $facing, $typeId){
		if($typeId > 8){
			return null;
		}

		$bb = $component->getBoundingBox();
		if(abs($x - $bb->minX) > 80 || abs($z - $bb->minZ) > 80){
			return null;
		}

		$c = self::createRandomShaftPiece($components, $rnd, $x, $y, $z, $facing, $typeId + 1);
		if($c !== null){
			$components[] = $c;
			$c->buildComponent($component, $components, $rnd);
		}

		return $c;
	}
}
