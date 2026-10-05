<?php

namespace pocketmine\level\generator\gorigional\structure;

use pocketmine\level\generator\gorigional\JavaRandom;

/**
 * 移植自 SCAXE-GO-CE structure.StrongholdPiece 及辅助函数。
 */
abstract class StrongholdPiece extends StructureComponentBase{
	/** @var StrongholdPieceWeight[]|null */
	private static $weights = null;

	public function __construct($componentType, BoundingBox $box, $facing){
		$this->componentType = $componentType;
		$this->boundingBox = $box;
		$this->coordBaseMode = $facing;
	}

	/**
	 * @return StrongholdPieceWeight[]
	 */
	private static function weights(){
		if(self::$weights === null){
			self::$weights = [
				new StrongholdPieceWeight('straight', 175, 0),
				new StrongholdPieceWeight('portal', 20, 1),
			];
		}
		return self::$weights;
	}

	public static function getNextStrongholdComponent(array &$components, JavaRandom $rnd, $x, $y, $z, $facing, $typeId){
		if($typeId > 50){
			return null;
		}

		$weights = self::weights();

		$totalWeight = 0;
		foreach($weights as $pw){
			if($pw->limit === 0 || $pw->instances < $pw->limit){
				$totalWeight += $pw->weight;
			}
		}

		if($totalWeight === 0){
			return null;
		}

		$i = $rnd->nextBoundedInt($totalWeight);
		$selected = null;

		foreach($weights as $pw){
			if($pw->limit === 0 || $pw->instances < $pw->limit){
				$i -= $pw->weight;
				if($i < 0){
					$selected = $pw;
					break;
				}
			}
		}

		if($selected !== null){
			return self::createStrongholdPiece($selected, $components, $rnd, $x, $y, $z, $facing);
		}
		return null;
	}

	private static function createStrongholdPiece(StrongholdPieceWeight $pw, array &$components, JavaRandom $rnd, $x, $y, $z, $facing){
		if($pw->type === 'straight'){
			$box = self::getStraightBoundingBox($components, $rnd, $x, $y, $z, $facing);
			if($box !== null){
				$pw->instances++;
				return new StrongholdStraight($rnd, $box, $facing);
			}
		}elseif($pw->type === 'portal'){
			$box = self::getPortalRoomBoundingBox($components, $rnd, $x, $y, $z, $facing);
			if($box !== null){
				$pw->instances++;
				return new StrongholdPortalRoom($rnd, $box, $facing);
			}
		}

		return null;
	}

	public static function getStraightBoundingBox(array $components, JavaRandom $rnd, $x, $y, $z, $facing){
		$box = BoundingBox::getComponentToAddBoundingBox($x, $y, $z, -1, -1, 0, 5, 5, 7, $facing);
		if(self::findIntersectingComponents($components, $box) !== null){
			return null;
		}
		return $box;
	}

	public static function getPortalRoomBoundingBox(array $components, JavaRandom $rnd, $x, $y, $z, $facing){
		$box = BoundingBox::getComponentToAddBoundingBox($x, $y, $z, -4, -1, 0, 11, 8, 16, $facing);
		if(self::findIntersectingComponents($components, $box) !== null){
			return null;
		}
		return $box;
	}

	public static function findIntersectingComponents(array $list, BoundingBox $box){
		foreach($list as $c){
			if($c->getBoundingBox() !== null && $c->getBoundingBox()->intersectsWith($box)){
				return $c;
			}
		}
		return null;
	}
}
