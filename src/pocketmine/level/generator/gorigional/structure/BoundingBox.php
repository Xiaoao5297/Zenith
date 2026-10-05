<?php

namespace pocketmine\level\generator\gorigional\structure;

/**
 * 移植自 SCAXE-GO-CE pkg/level/generator/gorigional/structure/bounding_box.go。
 */
class BoundingBox{
	/** @var int */
	public $minX;
	/** @var int */
	public $minY;
	/** @var int */
	public $minZ;
	/** @var int */
	public $maxX;
	/** @var int */
	public $maxY;
	/** @var int */
	public $maxZ;

	public function __construct($x1, $y1, $z1, $x2, $y2, $z2){
		$this->minX = $x1;
		$this->minY = $y1;
		$this->minZ = $z1;
		$this->maxX = $x2;
		$this->maxY = $y2;
		$this->maxZ = $z2;

		if($this->minX > $this->maxX){
			$t = $this->minX; $this->minX = $this->maxX; $this->maxX = $t;
		}
		if($this->minY > $this->maxY){
			$t = $this->minY; $this->minY = $this->maxY; $this->maxY = $t;
		}
		if($this->minZ > $this->maxZ){
			$t = $this->minZ; $this->minZ = $this->maxZ; $this->maxZ = $t;
		}
	}

	public function intersectsWith(BoundingBox $other){
		return $this->maxX >= $other->minX && $this->minX <= $other->maxX &&
			$this->maxZ >= $other->minZ && $this->minZ <= $other->maxZ &&
			$this->maxY >= $other->minY && $this->minY <= $other->maxY;
	}

	public function resultIsInside($x, $y, $z){
		return $x >= $this->minX && $x <= $this->maxX && $z >= $this->minZ && $z <= $this->maxZ && $y >= $this->minY && $y <= $this->maxY;
	}

	public function offset($x, $y, $z){
		$this->minX += $x;
		$this->minY += $y;
		$this->minZ += $z;
		$this->maxX += $x;
		$this->maxY += $y;
		$this->maxZ += $z;
	}

	public static function getComponentToAddBoundingBox($structureMinX, $structureMinY, $structureMinZ, $xMin, $yMin, $zMin, $xMax, $yMax, $zMax, $facing){
		switch($facing){
			case 0:
				return new BoundingBox($structureMinX + $xMin, $structureMinY + $yMin, $structureMinZ + $zMin, $structureMinX + $xMax - 1 + $xMin, $structureMinY + $yMax - 1 + $yMin, $structureMinZ + $zMax - 1 + $zMin);
			case 2:
				return new BoundingBox($structureMinX + $xMin, $structureMinY + $yMin, $structureMinZ - $zMax + 1 + $zMin, $structureMinX + $xMax - 1 + $xMin, $structureMinY + $yMax - 1 + $yMin, $structureMinZ + $zMin);
			case 1:
				return new BoundingBox($structureMinX - $zMax + 1 + $zMin, $structureMinY + $yMin, $structureMinZ + $xMin, $structureMinX + $zMin, $structureMinY + $yMax - 1 + $yMin, $structureMinZ + $xMax - 1 + $xMin);
			case 3:
				return new BoundingBox($structureMinX + $zMin, $structureMinY + $yMin, $structureMinZ + $xMin, $structureMinX + $zMax - 1 + $zMin, $structureMinY + $yMax - 1 + $yMin, $structureMinZ + $xMax - 1 + $xMin);
		}

		return new BoundingBox($structureMinX + $xMin, $structureMinY + $yMin, $structureMinZ + $zMin, $structureMinX + $xMax - 1 + $xMin, $structureMinY + $yMax - 1 + $yMin, $structureMinZ + $zMax - 1 + $zMin);
	}
}
