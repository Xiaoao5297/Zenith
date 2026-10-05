<?php

namespace pocketmine\level\generator\gorigional\structure;

/**
 * 移植自 SCAXE-GO-CE structure.ScatteredFeaturePiece。
 */
abstract class ScatteredFeaturePiece extends StructureComponentBase{
	/** @var int */
	public $width;
	/** @var int */
	public $height;
	/** @var int */
	public $depth;
	/** @var int */
	public $hPos = -1;

	public function __construct($componentType, $x, $y, $z, $width, $height, $depth, $facing){
		$box = BoundingBox::getComponentToAddBoundingBox($x, $y, $z, 0, 0, 0, $width, $height, $depth, $facing);

		$this->componentType = $componentType;
		$this->boundingBox = $box;
		$this->coordBaseMode = $facing;
		$this->width = $width;
		$this->height = $height;
		$this->depth = $depth;
	}

	public function offsetToAverageGroundLevel(WorldAccess $w, BoundingBox $box, $yOffset){
		if($this->hPos >= 0){
			return true;
		}

		$totalHeight = 0;
		$count = 0;

		for($z = $this->boundingBox->minZ; $z <= $this->boundingBox->maxZ; $z++){
			for($x = $this->boundingBox->minX; $x <= $this->boundingBox->maxX; $x++){
				if($box->resultIsInside($x, 64, $z)){
					$y = $this->getTopSolidBlockY($w, $x, $z);

					if($y < 63){
						$y = 63;
					}
					$totalHeight += $y;
					$count++;
				}
			}
		}

		if($count === 0){
			return false;
		}

		$avgY = intdiv($totalHeight, $count);
		$this->hPos = $avgY;

		$this->boundingBox->offset(0, $this->hPos - $this->boundingBox->minY + $yOffset, 0);
		return true;
	}

	private function getTopSolidBlockY(WorldAccess $w, $x, $z){
		for($y = 255; $y >= 0; $y--){
			$id = $w->getBlockId($x, $y, $z);
			if($id !== 0 && $id !== 9 && $id !== 11 && $id !== 10 && $id !== 8){
				return $y;
			}
		}
		return 0;
	}
}
