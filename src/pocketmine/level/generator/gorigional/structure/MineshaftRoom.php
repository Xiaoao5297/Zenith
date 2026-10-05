<?php

namespace pocketmine\level\generator\gorigional\structure;

use pocketmine\level\generator\gorigional\JavaRandom;

/**
 * 移植自 SCAXE-GO-CE structure.MineshaftRoom。
 */
class MineshaftRoom extends MineshaftPiece{

	public function __construct($x, $z){
		$l = 10;
		$h = 4;
		$w = 10;
		$box = new BoundingBox($x, 50, $z, $x + $l - 1, 50 + $h - 1, $z + $w - 1);
		parent::__construct(0, $box, 0);
	}

	public function buildComponent(StructureComponent $component = null, array &$components, JavaRandom $rnd){
		$c = self::getNextMineshaftComponent($this, $components, $rnd, $this->boundingBox->minX + 5, $this->boundingBox->minY, $this->boundingBox->minZ - 1, 2, 0);
		if($c !== null){
			$components[] = $c;
			$c->buildComponent($this, $components, $rnd);
		}

		$c = self::getNextMineshaftComponent($this, $components, $rnd, $this->boundingBox->minX + 5, $this->boundingBox->minY, $this->boundingBox->maxZ + 1, 0, 0);
		if($c !== null){
			$components[] = $c;
			$c->buildComponent($this, $components, $rnd);
		}

		$c = self::getNextMineshaftComponent($this, $components, $rnd, $this->boundingBox->maxX + 1, $this->boundingBox->minY, $this->boundingBox->minZ + 5, 3, 0);
		if($c !== null){
			$components[] = $c;
			$c->buildComponent($this, $components, $rnd);
		}

		$c = self::getNextMineshaftComponent($this, $components, $rnd, $this->boundingBox->minX - 1, $this->boundingBox->minY, $this->boundingBox->minZ + 5, 1, 0);
		if($c !== null){
			$components[] = $c;
			$c->buildComponent($this, $components, $rnd);
		}
	}

	public function addComponentParts(WorldAccess $wld, JavaRandom $rnd, BoundingBox $box){
		$this->fillWithBlocks($wld, $box, 0, 0, 0, 9, 3, 9, 3, 0, 0, 0, false);
		return true;
	}
}
