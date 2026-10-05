<?php

namespace pocketmine\level\generator\gorigional\structure;

use pocketmine\level\generator\gorigional\JavaRandom;

/**
 * 移植自 SCAXE-GO-CE structure.StructureComponentBase。
 */
abstract class StructureComponentBase implements StructureComponent{
	/** @var BoundingBox */
	public $boundingBox;
	/** @var int */
	public $coordBaseMode;
	/** @var int */
	public $componentType;

	public function getBoundingBox(){
		return $this->boundingBox;
	}

	public function getComponentType(){
		return $this->componentType;
	}

	public function getXWithOffset($x, $z){
		switch($this->coordBaseMode){
			case 0: return $this->boundingBox->minX + $x;
			case 1: return $this->boundingBox->maxX - $z;
			case 2: return $this->boundingBox->maxX - $x;
			case 3: return $this->boundingBox->minX + $z;
		}
		return $x;
	}

	public function getYWithOffset($y){
		if($this->coordBaseMode === -1){
			return $y;
		}
		return $y + $this->boundingBox->minY;
	}

	public function getZWithOffset($x, $z){
		switch($this->coordBaseMode){
			case 0: return $this->boundingBox->minZ + $z;
			case 1: return $this->boundingBox->minZ + $x;
			case 2: return $this->boundingBox->maxZ - $z;
			case 3: return $this->boundingBox->maxZ - $x;
		}
		return $z;
	}

	public function setBlockState(WorldAccess $w, $id, $meta, $x, $y, $z, BoundingBox $box){
		$worldX = $this->getXWithOffset($x, $z);
		$worldY = $this->getYWithOffset($y);
		$worldZ = $this->getZWithOffset($x, $z);

		if($box->resultIsInside($worldX, $worldY, $worldZ)){
			$meta = $this->getMetadataWithOffset($id, $meta);
			$w->setBlock($worldX, $worldY, $worldZ, $id, $meta);
		}
	}

	public function getMetadataWithOffset($id, $meta){
		if($this->coordBaseMode === -1){
			return $meta;
		}

		if(self::isStairs($id)){
			switch($this->coordBaseMode){
				case 0: return $meta;
				case 1:
					switch($meta){
						case 2: return 1;
						case 1: return 3;
						case 3: return 0;
						case 0: return 2;
					}
					break;
				case 2:
					switch($meta){
						case 2: return 3;
						case 3: return 2;
						case 0: return 1;
						case 1: return 0;
					}
					break;
				case 3:
					switch($meta){
						case 2: return 0;
						case 0: return 3;
						case 3: return 1;
						case 1: return 2;
					}
					break;
			}
		}

		if($id === 54 || $id === 130 || $id === 146 || $id === 65 || $id === 61 || $id === 62){
			switch($this->coordBaseMode){
				case 0: return $meta;
				case 1:
					switch($meta){
						case 2: return 5;
						case 3: return 4;
						case 4: return 2;
						case 5: return 3;
					}
					break;
				case 2:
					switch($meta){
						case 2: return 3;
						case 3: return 2;
						case 4: return 5;
						case 5: return 4;
					}
					break;
				case 3:
					switch($meta){
						case 2: return 4;
						case 3: return 5;
						case 4: return 3;
						case 5: return 2;
					}
					break;
			}
		}

		if($id === 17 || $id === 162){
			$axis = $meta & 12;
			if($axis === 4 || $axis === 8){
				if($this->coordBaseMode === 1 || $this->coordBaseMode === 3){
					if($axis === 4){
						return ($meta & 3) | 8;
					}
					if($axis === 8){
						return ($meta & 3) | 4;
					}
				}
			}
		}

		return $meta;
	}

	public static function isStairs($id){
		return $id === 53 || $id === 67 || $id === 108 || $id === 109 || $id === 114 || $id === 128 || $id === 134 || $id === 135 || $id === 136 || $id === 156 || $id === 163 || $id === 164 || $id === 180;
	}

	public function fillWithBlocks(WorldAccess $w, BoundingBox $box, $minX, $minY, $minZ, $maxX, $maxY, $maxZ, $outlineId, $outlineMeta, $insideId, $insideMeta, $keepOld){
		for($y = $minY; $y <= $maxY; $y++){
			for($x = $minX; $x <= $maxX; $x++){
				for($z = $minZ; $z <= $maxZ; $z++){
					if($x !== $minX && $x !== $maxX && $z !== $minZ && $z !== $maxZ && $y !== $minY && $y !== $maxY){
						$this->setBlockState($w, $insideId, $insideMeta, $x, $y, $z, $box);
					}else{
						$this->setBlockState($w, $outlineId, $outlineMeta, $x, $y, $z, $box);
					}
				}
			}
		}
	}

	public function fillWithAir(WorldAccess $w, BoundingBox $box, $minX, $minY, $minZ, $maxX, $maxY, $maxZ){
		$this->fillWithBlocks($w, $box, $minX, $minY, $minZ, $maxX, $maxY, $maxZ, 0, 0, 0, 0, false);
	}

	public function replaceAirAndLiquidDownwards(WorldAccess $w, $id, $meta, $x, $y, $z, BoundingBox $box){
		$worldX = $this->getXWithOffset($x, $z);
		$worldY = $this->getYWithOffset($y);
		$worldZ = $this->getZWithOffset($x, $z);

		if($box->resultIsInside($worldX, $worldY, $worldZ)){
			$bId = $w->getBlockId($worldX, $worldY, $worldZ);

			if($bId === 0 || ($bId >= 8 && $bId <= 11)){
				$w->setBlock($worldX, $worldY, $worldZ, $id, $meta);
			}
		}
	}

	public function clearCurrentPositionBlocksUpwards(WorldAccess $w, $x, $y, $z, BoundingBox $box){
		$worldX = $this->getXWithOffset($x, $z);
		$worldY = $this->getYWithOffset($y);
		$worldZ = $this->getZWithOffset($x, $z);

		if($box->resultIsInside($worldX, $worldY, $worldZ)){
			while($worldY < 128){
				$bId = $w->getBlockId($worldX, $worldY, $worldZ);
				if($bId === 0){
					break;
				}
				$w->setBlock($worldX, $worldY, $worldZ, 0, 0);
				$worldY++;
			}
		}
	}

	public function fillWithRandomizedBlocks(WorldAccess $w, BoundingBox $box, $minX, $minY, $minZ, $maxX, $maxY, $maxZ, $alwaysReplace, JavaRandom $rnd, callable $selector){
		for($y = $minY; $y <= $maxY; $y++){
			for($x = $minX; $x <= $maxX; $x++){
				for($z = $minZ; $z <= $maxZ; $z++){
					$wall = ($x === $minX || $x === $maxX || $z === $minZ || $z === $maxZ || $y === $minY || $y === $maxY);
					$result = $selector($rnd, $x, $y, $z, $wall);
					$id = $result[0];
					$meta = $result[1];

					if($id !== 0){
						$this->setBlockState($w, $id, $meta, $x, $y, $z, $box);
					}
				}
			}
		}
	}
}
