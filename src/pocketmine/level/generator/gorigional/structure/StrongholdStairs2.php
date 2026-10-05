<?php

namespace pocketmine\level\generator\gorigional\structure;

use pocketmine\level\generator\gorigional\JavaRandom;

/**
 * 移植自 SCAXE-GO-CE structure.StrongholdStairs2。
 */
class StrongholdStairs2 extends StrongholdPiece{

	public function __construct(JavaRandom $rnd, $x, $z){
		$seed = $rnd->nextInt();
		if($seed >= 0x80000000){
			$seed -= 0x100000000;
		}
		$facing = $seed % 4;

		$width = 5;
		$height = 11;
		$depth = 5;
		$y = 64;

		$box = new BoundingBox($x, $y, $z, $x + $width - 1, $y + $height - 1, $z + $depth - 1);
		parent::__construct(0, $box, $facing);
	}

	public function buildComponent(StructureComponent $component = null, array &$components, JavaRandom $rnd){
		switch($this->coordBaseMode){
			case 0:
				$c = self::getNextStrongholdComponent($components, $rnd, $this->boundingBox->minX + 2, $this->boundingBox->minY, $this->boundingBox->maxZ + 1, 0, $this->componentType + 1);
				if($c !== null){ $components[] = $c; $c->buildComponent($this, $components, $rnd); }
				$c = self::getNextStrongholdComponent($components, $rnd, $this->boundingBox->maxX + 1, $this->boundingBox->minY, $this->boundingBox->minZ + 2, 3, $this->componentType + 1);
				if($c !== null){ $components[] = $c; $c->buildComponent($this, $components, $rnd); }
				$c = self::getNextStrongholdComponent($components, $rnd, $this->boundingBox->minX - 1, $this->boundingBox->minY, $this->boundingBox->minZ + 2, 1, $this->componentType + 1);
				if($c !== null){ $components[] = $c; $c->buildComponent($this, $components, $rnd); }
				break;
			case 1:
				$c = self::getNextStrongholdComponent($components, $rnd, $this->boundingBox->minX - 1, $this->boundingBox->minY, $this->boundingBox->minZ + 2, 1, $this->componentType + 1);
				if($c !== null){ $components[] = $c; $c->buildComponent($this, $components, $rnd); }
				$c = self::getNextStrongholdComponent($components, $rnd, $this->boundingBox->minX + 2, $this->boundingBox->minY, $this->boundingBox->maxZ + 1, 0, $this->componentType + 1);
				if($c !== null){ $components[] = $c; $c->buildComponent($this, $components, $rnd); }
				$c = self::getNextStrongholdComponent($components, $rnd, $this->boundingBox->minX + 2, $this->boundingBox->minY, $this->boundingBox->minZ - 1, 2, $this->componentType + 1);
				if($c !== null){ $components[] = $c; $c->buildComponent($this, $components, $rnd); }
				break;
			case 2:
				$c = self::getNextStrongholdComponent($components, $rnd, $this->boundingBox->minX + 2, $this->boundingBox->minY, $this->boundingBox->minZ - 1, 2, $this->componentType + 1);
				if($c !== null){ $components[] = $c; $c->buildComponent($this, $components, $rnd); }
				$c = self::getNextStrongholdComponent($components, $rnd, $this->boundingBox->minX - 1, $this->boundingBox->minY, $this->boundingBox->minZ + 2, 1, $this->componentType + 1);
				if($c !== null){ $components[] = $c; $c->buildComponent($this, $components, $rnd); }
				$c = self::getNextStrongholdComponent($components, $rnd, $this->boundingBox->maxX + 1, $this->boundingBox->minY, $this->boundingBox->minZ + 2, 3, $this->componentType + 1);
				if($c !== null){ $components[] = $c; $c->buildComponent($this, $components, $rnd); }
				break;
			case 3:
				$c = self::getNextStrongholdComponent($components, $rnd, $this->boundingBox->maxX + 1, $this->boundingBox->minY, $this->boundingBox->minZ + 2, 3, $this->componentType + 1);
				if($c !== null){ $components[] = $c; $c->buildComponent($this, $components, $rnd); }
				$c = self::getNextStrongholdComponent($components, $rnd, $this->boundingBox->minX + 2, $this->boundingBox->minY, $this->boundingBox->minZ - 1, 2, $this->componentType + 1);
				if($c !== null){ $components[] = $c; $c->buildComponent($this, $components, $rnd); }
				$c = self::getNextStrongholdComponent($components, $rnd, $this->boundingBox->minX + 2, $this->boundingBox->minY, $this->boundingBox->maxZ + 1, 0, $this->componentType + 1);
				if($c !== null){ $components[] = $c; $c->buildComponent($this, $components, $rnd); }
				break;
		}
	}

	public function addComponentParts(WorldAccess $wld, JavaRandom $rnd, BoundingBox $box){
		$this->fillWithBlocks($wld, $box, 0, 0, 0, 4, 10, 4, 98, 0, 98, 0, false);
		$this->fillWithAir($wld, $box, 1, 1, 1, 3, 10, 3);
		return true;
	}
}
