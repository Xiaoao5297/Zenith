<?php

namespace pocketmine\level\generator\gorigional\structure;

use pocketmine\level\generator\gorigional\JavaRandom;

/**
 * 移植自 SCAXE-GO-CE structure.StrongholdStraight。
 */
class StrongholdStraight extends StrongholdPiece{

	public function __construct(JavaRandom $rnd, BoundingBox $box, $facing){
		parent::__construct(1, $box, $facing);
	}

	public function buildComponent(StructureComponent $component = null, array &$components, JavaRandom $rnd){
		$c = self::getNextStrongholdComponent($components, $rnd, $this->boundingBox->minX, $this->boundingBox->minY, $this->boundingBox->minZ, $this->coordBaseMode, $this->componentType + 1);
		if($c !== null){
			$components[] = $c;
			$c->buildComponent($this, $components, $rnd);
		}
	}

	public function addComponentParts(WorldAccess $wld, JavaRandom $rnd, BoundingBox $box){
		$this->fillWithBlocks($wld, $box, 0, 0, 0, 4, 4, 6, 98, 0, 98, 0, false);
		$this->fillWithAir($wld, $box, 1, 1, 0, 3, 3, 6);
		return true;
	}
}
