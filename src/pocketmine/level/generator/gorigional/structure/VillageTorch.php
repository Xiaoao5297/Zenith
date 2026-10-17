<?php

namespace pocketmine\level\generator\gorigional\structure;

use pocketmine\level\generator\gorigional\JavaRandom;

/**
 * 移植自 Java 1.7.10 StructureVillagePieces$Torch。
 */
class VillageTorch extends VillagePiece{
	const AIR = 0;
	const WOOL = 35;
	const TORCH = 50;
	const FENCE = 85;

	public function __construct(VillageWellStart $start = null, $typeId = 0, JavaRandom $rnd = null, BoundingBox $box = null, $facing = 0){
		parent::__construct($start, $typeId);
		$this->coordBaseMode = $facing;
		$this->boundingBox = $box;
	}

	public static function createBox(VillageWellStart $start, array $list, JavaRandom $rnd, $i, $j, $k, $l){
		$box = BoundingBox::getComponentToAddBoundingBox($i, $j, $k, 0, 0, 0, 3, 4, 2, $l);
		return self::findIntersecting($list, $box) !== null ? null : $box;
	}

	public function addComponentParts(WorldAccess $w, JavaRandom $rnd, BoundingBox $box){
		if($this->terrainHeight < 0){
			$this->terrainHeight = $this->getAverageGroundHeight($w, $box);
			if($this->terrainHeight < 0){
				return true;
			}
			$this->boundingBox->offset(0, $this->terrainHeight - $this->boundingBox->maxY + 4 - 1, 0);
		}

		$this->fillWithBlocks($w, $box, 0, 0, 0, 2, 3, 1, self::AIR, 0, self::AIR, 0, false);
		$this->placeBlock($w, self::FENCE, 0, 1, 0, 0, $box);
		$this->placeBlock($w, self::FENCE, 0, 1, 1, 0, $box);
		$this->placeBlock($w, self::FENCE, 0, 1, 2, 0, $box);
		$this->placeBlock($w, self::WOOL, 15, 1, 3, 0, $box);
		$this->placeBlock($w, self::TORCH, 0, 0, 3, 0, $box);
		$this->placeBlock($w, self::TORCH, 0, 1, 3, 1, $box);
		$this->placeBlock($w, self::TORCH, 0, 2, 3, 0, $box);
		$this->placeBlock($w, self::TORCH, 0, 1, 3, -1, $box);
		return true;
	}
}
