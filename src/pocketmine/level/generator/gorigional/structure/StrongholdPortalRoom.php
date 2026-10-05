<?php

namespace pocketmine\level\generator\gorigional\structure;

use pocketmine\level\generator\gorigional\JavaRandom;

/**
 * 移植自 SCAXE-GO-CE structure.StrongholdPortalRoom。
 */
class StrongholdPortalRoom extends StrongholdPiece{

	public function __construct(JavaRandom $rnd, BoundingBox $box, $facing){
		parent::__construct(10, $box, $facing);
	}

	public function buildComponent(StructureComponent $component = null, array &$components, JavaRandom $rnd){

	}

	public function addComponentParts(WorldAccess $wld, JavaRandom $rnd, BoundingBox $box){
		$this->fillWithBlocks($wld, $box, 0, 0, 0, 10, 7, 15, 98, 0, 98, 0, false);
		$this->fillWithAir($wld, $box, 1, 1, 1, 9, 6, 14);
		return true;
	}
}
