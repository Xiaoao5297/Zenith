<?php

namespace pocketmine\level\generator\gorigional\structure;

use pocketmine\level\generator\gorigional\JavaRandom;

/**
 * 移植自 SCAXE-GO-CE structure.MineshaftCorridor。
 */
class MineshaftCorridor extends MineshaftPiece{

	public function __construct(JavaRandom $rnd, BoundingBox $box, $facing){
		parent::__construct(1, $box, $facing);
	}

	public function buildComponent(StructureComponent $component = null, array &$components, JavaRandom $rnd){
		$facing = $this->coordBaseMode;

		$nextX = 0;
		$nextZ = 0;

		switch($facing){
			case 0:
				$nextX = $this->boundingBox->minX + 1;
				$nextZ = $this->boundingBox->maxZ + 1;
				break;
			case 1:
				$nextX = $this->boundingBox->minX - 1;
				$nextZ = $this->boundingBox->minZ + 1;
				break;
			case 2:
				$nextX = $this->boundingBox->minX + 1;
				$nextZ = $this->boundingBox->minZ - 1;
				break;
			case 3:
				$nextX = $this->boundingBox->maxX + 1;
				$nextZ = $this->boundingBox->minZ + 1;
				break;
		}

		$c = self::getNextMineshaftComponent($this, $components, $rnd, $nextX, $this->boundingBox->minY, $nextZ, $facing, $this->componentType + 1);
		if($c !== null){
			$components[] = $c;
			$c->buildComponent($this, $components, $rnd);
		}
	}

	public function addComponentParts(WorldAccess $wld, JavaRandom $rnd, BoundingBox $box){
		$length = $box->maxX - $box->minX;
		if($length < 10){
			$length = $box->maxZ - $box->minZ;
		}

		$this->fillWithBlocks($wld, $box, 0, 0, 0, 2, 2, $length, 0, 0, 0, 0, false);

		for($i = 0; $i < $length; $i += 4){
			$this->fillWithBlocks($wld, $box, 0, 0, $i, 0, 2, $i, 85, 0, 85, 0, false);
			$this->fillWithBlocks($wld, $box, 2, 0, $i, 2, 2, $i, 85, 0, 85, 0, false);

			$this->fillWithBlocks($wld, $box, 0, 2, $i, 2, 2, $i, 5, 0, 5, 0, false);
		}

		if($rnd->nextFloat() < 0.5){
			$this->setBlockState($wld, 66, 0, 1, 0, 0, $box);
		}

		return true;
	}
}
