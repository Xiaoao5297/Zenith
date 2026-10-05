<?php

namespace pocketmine\level\generator\gorigional\structure;

use pocketmine\level\generator\gorigional\JavaRandom;

/**
 * 移植自 SCAXE-GO-CE structure.MineshaftStairs。
 */
class MineshaftStairs extends MineshaftPiece{

	public function __construct(JavaRandom $rnd, BoundingBox $box, $facing){
		parent::__construct(3, $box, $facing);
	}

	public function buildComponent(StructureComponent $component = null, array &$components, JavaRandom $rnd){

	}

	public function addComponentParts(WorldAccess $wld, JavaRandom $rnd, BoundingBox $box){
		$this->fillWithAir($wld, $box, 0, 0, 0, 4, 4, 4);

		for($i = 0; $i < 5; $i++){
			$this->setBlockState($wld, 53, 0, 1, $i, $i, $box);
		}
		return true;
	}
}
