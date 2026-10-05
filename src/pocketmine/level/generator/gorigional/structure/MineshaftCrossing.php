<?php

namespace pocketmine\level\generator\gorigional\structure;

use pocketmine\level\generator\gorigional\JavaRandom;

/**
 * 移植自 SCAXE-GO-CE structure.MineshaftCrossing。
 */
class MineshaftCrossing extends MineshaftPiece{

	public function __construct(JavaRandom $rnd, BoundingBox $box, $facing){
		parent::__construct(2, $box, $facing);
	}

	public function buildComponent(StructureComponent $component = null, array &$components, JavaRandom $rnd){

	}

	public function addComponentParts(WorldAccess $wld, JavaRandom $rnd, BoundingBox $box){
		$this->fillWithAir($wld, $box, 0, 0, 0, 4, 3, 4);
		$this->fillWithBlocks($wld, $box, 0, 0, 0, 4, 0, 4, 5, 0, 5, 0, false);
		return true;
	}
}
