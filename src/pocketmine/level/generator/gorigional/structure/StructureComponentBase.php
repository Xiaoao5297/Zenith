<?php

/***
 *  _____                _  __   __
 * /__  /  ___   ____   (_)/ /_ / /_
 *   / /  / _ \ / __ \ / // __// __ \
 *  / /__/  __// / / // // /_ / / / /
 * /____/\___//_/ /_//_/ \__//_/ /_/
 *
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * @author Xiaoao
 * @link https://github.com/Xiaoao5297/Zenith
 *
 *
*/


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

		if($id === 66){
			if($this->coordBaseMode === 1 || $this->coordBaseMode === 3){
				return $meta === 1 ? 0 : 1;
			}
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
					if($keepOld && $this->getBlock($w, $x, $y, $z, $box) === 0){
						continue;
					}
					if($x !== $minX && $x !== $maxX && $z !== $minZ && $z !== $maxZ && $y !== $minY && $y !== $maxY){
						$this->placeBlock($w, $insideId, $insideMeta, $x, $y, $z, $box);
					}else{
						$this->placeBlock($w, $outlineId, $outlineMeta, $x, $y, $z, $box);
					}
				}
			}
		}
	}

	public function placeBlock(WorldAccess $w, $id, $meta, $x, $y, $z, BoundingBox $box){
		$worldX = $this->getXWithOffset($x, $z);
		$worldY = $this->getYWithOffset($y);
		$worldZ = $this->getZWithOffset($x, $z);

		if($box->resultIsInside($worldX, $worldY, $worldZ)){
			$w->setBlock($worldX, $worldY, $worldZ, $id, $meta);
		}
	}

	public function getBlock(WorldAccess $w, $x, $y, $z, BoundingBox $box){
		$worldX = $this->getXWithOffset($x, $z);
		$worldY = $this->getYWithOffset($y);
		$worldZ = $this->getZWithOffset($x, $z);

		if(!$box->resultIsInside($worldX, $worldY, $worldZ)){
			return 0;
		}
		return $w->getBlockId($worldX, $worldY, $worldZ);
	}

	public function randomlyPlaceBlock(WorldAccess $w, BoundingBox $box, JavaRandom $rnd, $chance, $x, $y, $z, $id, $meta){
		if($rnd->nextFloat() < $chance){
			$this->placeBlock($w, $id, $meta, $x, $y, $z, $box);
		}
	}

	public function randomlyFillWithBlocks(WorldAccess $w, BoundingBox $box, JavaRandom $rnd, $chance, $minX, $minY, $minZ, $maxX, $maxY, $maxZ, $outlineId, $insideId, $keepOld){
		for($y = $minY; $y <= $maxY; $y++){
			for($x = $minX; $x <= $maxX; $x++){
				for($z = $minZ; $z <= $maxZ; $z++){
					if($rnd->nextFloat() <= $chance && (!$keepOld || $this->getBlock($w, $x, $y, $z, $box) !== 0)){
						if($x !== $minX && $x !== $maxX && $z !== $minZ && $z !== $maxZ && $y !== $minY && $y !== $maxY){
							$this->placeBlock($w, $insideId, 0, $x, $y, $z, $box);
						}else{
							$this->placeBlock($w, $outlineId, 0, $x, $y, $z, $box);
						}
					}
				}
			}
		}
	}

	public function clearEllipsoid(WorldAccess $w, BoundingBox $box, $minX, $minY, $minZ, $maxX, $maxY, $maxZ, $id, $flag){
		$f = (float) ($maxX - $minX + 1);
		$f1 = (float) ($maxY - $minY + 1);
		$f2 = (float) ($maxZ - $minZ + 1);
		$f3 = (float) $minX + $f / 2.0;
		$f4 = (float) $minZ + $f2 / 2.0;

		for($y = $minY; $y <= $maxY; $y++){
			$f5 = (float) ($y - $minY) / $f1;
			for($x = $minX; $x <= $maxX; $x++){
				$f6 = ((float) $x - $f3) / ($f * 0.5);
				for($z = $minZ; $z <= $maxZ; $z++){
					$f7 = ((float) $z - $f4) / ($f2 * 0.5);
					if(!$flag || $this->getBlock($w, $x, $y, $z, $box) !== 0){
						$f8 = $f6 * $f6 + $f5 * $f5 + $f7 * $f7;
						if($f8 <= 1.05){
							$this->placeBlock($w, $id, 0, $x, $y, $z, $box);
						}
					}
				}
			}
		}
	}

	public function isLiquidInStructureBoundingBox(WorldAccess $w, BoundingBox $box){
		$i = max($this->boundingBox->minX - 1, $box->minX);
		$j = max($this->boundingBox->minY - 1, $box->minY);
		$k = max($this->boundingBox->minZ - 1, $box->minZ);
		$l = min($this->boundingBox->maxX + 1, $box->maxX);
		$i1 = min($this->boundingBox->maxY + 1, $box->maxY);
		$j1 = min($this->boundingBox->maxZ + 1, $box->maxZ);

		for($k1 = $i; $k1 <= $l; $k1++){
			for($l1 = $k; $l1 <= $j1; $l1++){
				if(self::isLiquid($w->getBlockId($k1, $j, $l1)) || self::isLiquid($w->getBlockId($k1, $i1, $l1))){
					return true;
				}
			}
		}
		for($k1 = $i; $k1 <= $l; $k1++){
			for($l1 = $j; $l1 <= $i1; $l1++){
				if(self::isLiquid($w->getBlockId($k1, $l1, $k)) || self::isLiquid($w->getBlockId($k1, $l1, $j1))){
					return true;
				}
			}
		}
		for($k1 = $k; $k1 <= $j1; $k1++){
			for($l1 = $j; $l1 <= $i1; $l1++){
				if(self::isLiquid($w->getBlockId($i, $l1, $k1)) || self::isLiquid($w->getBlockId($l, $l1, $k1))){
					return true;
				}
			}
		}

		return false;
	}

	public static function isLiquid($id){
		return $id === 8 || $id === 9 || $id === 10 || $id === 11;
	}

	/**
	 * @return int[] [x, y, z]
	 */
	public function getChunkPosition(){
		return [
			$this->boundingBox->getCenterX(),
			$this->boundingBox->getCenterY(),
			$this->boundingBox->getCenterZ()
		];
	}

	public function generateChest(WorldAccess $w, BoundingBox $box, JavaRandom $rnd, $x, $y, $z, $loot = null){
		$worldX = $this->getXWithOffset($x, $z);
		$worldY = $this->getYWithOffset($y);
		$worldZ = $this->getZWithOffset($x, $z);

		if($box->resultIsInside($worldX, $worldY, $worldZ) && $w->getBlockId($worldX, $worldY, $worldZ) !== 54){
			$w->setBlock($worldX, $worldY, $worldZ, 54, 0);
			if($loot !== null){
				$w->setChestContents($worldX, $worldY, $worldZ, $this->createLoot($rnd, $loot));
			}
			return true;
		}
		return false;
	}

	/**
	 * 简化版 1.7.10 战利品表，保持原版的物品种类和大致权重。
	 * @return array [[slot, id, meta, count], ...]
	 */
	protected function createLoot(JavaRandom $rnd, $type){
		if($type === 'stronghold'){
			$table = [
				[297, 0, 1, 3, 10],  // bread
				[260, 0, 1, 3, 10],  // apple
				[265, 0, 1, 5, 10],  // iron ingot
				[266, 0, 1, 3, 5],   // gold ingot
				[368, 0, 1, 1, 2],   // ender pearl
				[264, 0, 1, 3, 3],   // diamond
				[331, 0, 4, 9, 5],   // redstone
				[263, 0, 3, 8, 10],  // coal
				[267, 0, 1, 1, 5],   // iron sword
				[257, 0, 1, 1, 5],   // iron pickaxe
				[322, 0, 1, 1, 1],   // golden apple
			];
		}elseif($type === 'jungle'){
			// Java 1.7.10 丛林神庙战利品表，精简为常见物品。
			$table = [
				[264, 0, 1, 3, 3],   // diamond
				[265, 0, 1, 5, 10],  // iron ingot
				[266, 0, 2, 7, 15],  // gold ingot
				[388, 0, 1, 3, 2],   // emerald
				[352, 0, 4, 6, 20],  // bone
				[367, 0, 3, 7, 16],  // rotten flesh
				[403, 0, 0, 1, 3],   // enchanted book
				[419, 0, 1, 1, 1],   // diamond horse armor
				[418, 0, 1, 1, 1],   // gold horse armor
				[417, 0, 1, 1, 1],   // iron horse armor
			];
		}elseif($type === 'desert'){
			// Java 1.7.10 沙漠神殿战利品表，按 MCPE 0.14 可用物品裁剪。
			$table = [
				[264, 0, 1, 3, 5],   // diamond
				[265, 0, 1, 5, 10],  // iron ingot
				[266, 0, 1, 3, 10],  // gold ingot
				[388, 0, 1, 3, 5],   // emerald
				[352, 0, 1, 8, 10],  // bone
				[375, 0, 1, 3, 10],  // spider eye
				[403, 0, 0, 1, 5],   // enchanted book
				[419, 0, 1, 1, 1],   // diamond horse armor
				[418, 0, 1, 1, 2],   // gold horse armor
				[417, 0, 1, 1, 3],   // iron horse armor
				[466, 0, 1, 1, 1],   // enchanted golden apple
				[322, 0, 1, 1, 5],   // golden apple
				[263, 0, 3, 8, 10],  // coal
				[297, 0, 1, 3, 10],  // bread
				[367, 0, 1, 5, 10],  // rotten flesh
			];
		}else{
			return [];
		}

		$totalWeight = 0;
		foreach($table as $entry){
			$totalWeight += $entry[4];
		}

		$result = [];
		$usedSlots = [];
		$stacks = 3 + $rnd->nextBoundedInt(4);
		for($i = 0; $i < $stacks; $i++){
			$slot = $rnd->nextBoundedInt(27);
			if(isset($usedSlots[$slot])){
				continue;
			}
			$usedSlots[$slot] = true;

			$roll = $rnd->nextBoundedInt($totalWeight);
			foreach($table as $entry){
				$roll -= $entry[4];
				if($roll < 0){
					$count = $entry[2] + $rnd->nextBoundedInt($entry[3] - $entry[2] + 1);
					$result[] = [$slot, $entry[0], $entry[1], $count];
					break;
				}
			}
		}
		return $result;
	}

	public function generateDispenser(WorldAccess $w, BoundingBox $box, JavaRandom $rnd, $x, $y, $z, $meta){
		$worldX = $this->getXWithOffset($x, $z);
		$worldY = $this->getYWithOffset($y);
		$worldZ = $this->getZWithOffset($x, $z);

		if($box->resultIsInside($worldX, $worldY, $worldZ) && $w->getBlockId($worldX, $worldY, $worldZ) !== 23){
			$w->setBlock($worldX, $worldY, $worldZ, 23, $this->getMetadataWithOffset(23, $meta));
			return true;
		}
		return false;
	}

	public static function findIntersecting(array $list, BoundingBox $box){
		foreach($list as $c){
			$bb = $c->getBoundingBox();
			if($bb !== null && $bb->intersectsWith($box)){
				return $c;
			}
		}
		return null;
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

	public function fillWithRandomizedBlocks(WorldAccess $w, BoundingBox $box, $minX, $minY, $minZ, $maxX, $maxY, $maxZ, $keepOld, JavaRandom $rnd, callable $selector){
		for($y = $minY; $y <= $maxY; $y++){
			for($x = $minX; $x <= $maxX; $x++){
				for($z = $minZ; $z <= $maxZ; $z++){
					if($keepOld && $this->getBlock($w, $x, $y, $z, $box) === 0){
						continue;
					}
					$wall = ($x === $minX || $x === $maxX || $z === $minZ || $z === $maxZ || $y === $minY || $y === $maxY);
					$result = $selector($rnd, $x, $y, $z, $wall);
					$this->placeBlock($w, $result[0], $result[1], $x, $y, $z, $box);
				}
			}
		}
	}
}
