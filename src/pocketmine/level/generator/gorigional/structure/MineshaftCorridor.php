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

use pocketmine\entity\CaveSpider;
use pocketmine\level\generator\gorigional\JavaRandom;

/**
 * 移植自 Java 1.7.10 StructureMineshaftPieces$Corridor。
 */
class MineshaftCorridor extends MineshaftPiece{
	const AIR = 0;
	const PLANKS = 5;
	const WEB = 30;
	const TORCH = 50;
	const SPAWNER = 52;
	const RAIL = 66;
	const FENCE = 85;

	/** @var bool */
	private $hasRails;
	/** @var bool */
	private $spiderCorridor;
	/** @var bool */
	private $placedSpawner = false;
	/** @var int */
	private $numSections;

	public function __construct($typeId, JavaRandom $rnd, BoundingBox $box, $facing){
		parent::__construct($typeId, $box, $facing);

		$this->hasRails = $rnd->nextBoundedInt(3) === 0;
		$this->spiderCorridor = !$this->hasRails && $rnd->nextBoundedInt(23) === 0;

		if($facing !== 2 && $facing !== 0){
			$this->numSections = intdiv($box->getXSize(), 5);
		}else{
			$this->numSections = intdiv($box->getZSize(), 5);
		}
	}

	public static function getCorridorBoundingBox(array $components, JavaRandom $rnd, $x, $y, $z, $facing){
		$box = new BoundingBox($x, $y, $z, $x, $y + 2, $z);

		for($i1 = $rnd->nextBoundedInt(3) + 2; $i1 > 0; --$i1){
			$j1 = $i1 * 5;

			switch($facing){
				case 0:
					$box->maxX = $x + 2;
					$box->maxZ = $z + ($j1 - 1);
					break;
				case 1:
					$box->minX = $x - ($j1 - 1);
					$box->maxZ = $z + 2;
					break;
				case 2:
					$box->maxX = $x + 2;
					$box->minZ = $z - ($j1 - 1);
					break;
				case 3:
					$box->maxX = $x + ($j1 - 1);
					$box->maxZ = $z + 2;
					break;
			}

			if(self::findIntersecting($components, $box) === null){
				break;
			}
		}

		return $i1 > 0 ? $box : null;
	}

	public function buildComponent(StructureComponent $component = null, array &$components, JavaRandom $rnd){
		$i = $this->componentType;
		$j = $rnd->nextBoundedInt(4);
		$bb = $this->boundingBox;

		switch($this->coordBaseMode){
			case 0:
				if($j <= 1){
					self::getNextMineshaftComponent($component, $components, $rnd, $bb->minX, $bb->minY - 1 + $rnd->nextBoundedInt(3), $bb->maxZ + 1, $this->coordBaseMode, $i);
				}elseif($j === 2){
					self::getNextMineshaftComponent($component, $components, $rnd, $bb->minX - 1, $bb->minY - 1 + $rnd->nextBoundedInt(3), $bb->maxZ - 3, 1, $i);
				}else{
					self::getNextMineshaftComponent($component, $components, $rnd, $bb->maxX + 1, $bb->minY - 1 + $rnd->nextBoundedInt(3), $bb->maxZ - 3, 3, $i);
				}
				break;
			case 1:
				if($j <= 1){
					self::getNextMineshaftComponent($component, $components, $rnd, $bb->minX - 1, $bb->minY - 1 + $rnd->nextBoundedInt(3), $bb->minZ, $this->coordBaseMode, $i);
				}elseif($j === 2){
					self::getNextMineshaftComponent($component, $components, $rnd, $bb->minX, $bb->minY - 1 + $rnd->nextBoundedInt(3), $bb->minZ - 1, 2, $i);
				}else{
					self::getNextMineshaftComponent($component, $components, $rnd, $bb->minX, $bb->minY - 1 + $rnd->nextBoundedInt(3), $bb->maxZ + 1, 0, $i);
				}
				break;
			case 2:
				if($j <= 1){
					self::getNextMineshaftComponent($component, $components, $rnd, $bb->minX, $bb->minY - 1 + $rnd->nextBoundedInt(3), $bb->minZ - 1, $this->coordBaseMode, $i);
				}elseif($j === 2){
					self::getNextMineshaftComponent($component, $components, $rnd, $bb->minX - 1, $bb->minY - 1 + $rnd->nextBoundedInt(3), $bb->minZ, 1, $i);
				}else{
					self::getNextMineshaftComponent($component, $components, $rnd, $bb->maxX + 1, $bb->minY - 1 + $rnd->nextBoundedInt(3), $bb->minZ, 3, $i);
				}
				break;
			case 3:
				if($j <= 1){
					self::getNextMineshaftComponent($component, $components, $rnd, $bb->maxX + 1, $bb->minY - 1 + $rnd->nextBoundedInt(3), $bb->minZ, $this->coordBaseMode, $i);
				}elseif($j === 2){
					self::getNextMineshaftComponent($component, $components, $rnd, $bb->maxX - 3, $bb->minY - 1 + $rnd->nextBoundedInt(3), $bb->minZ - 1, 2, $i);
				}else{
					self::getNextMineshaftComponent($component, $components, $rnd, $bb->maxX - 3, $bb->minY - 1 + $rnd->nextBoundedInt(3), $bb->maxZ + 1, 0, $i);
				}
				break;
		}

		if($i < 8){
			if($this->coordBaseMode !== 2 && $this->coordBaseMode !== 0){
				for($l = $bb->minX + 3; $l + 3 <= $bb->maxX; $l += 5){
					$k = $rnd->nextBoundedInt(5);
					if($k === 0){
						self::getNextMineshaftComponent($component, $components, $rnd, $l, $bb->minY, $bb->minZ - 1, 2, $i + 1);
					}elseif($k === 1){
						self::getNextMineshaftComponent($component, $components, $rnd, $l, $bb->minY, $bb->maxZ + 1, 0, $i + 1);
					}
				}
			}else{
				for($l = $bb->minZ + 3; $l + 3 <= $bb->maxZ; $l += 5){
					$k = $rnd->nextBoundedInt(5);
					if($k === 0){
						self::getNextMineshaftComponent($component, $components, $rnd, $bb->minX - 1, $bb->minY, $l, 1, $i + 1);
					}elseif($k === 1){
						self::getNextMineshaftComponent($component, $components, $rnd, $bb->maxX + 1, $bb->minY, $l, 3, $i + 1);
					}
				}
			}
		}
	}

	public function addComponentParts(WorldAccess $w, JavaRandom $rnd, BoundingBox $box){
		if($this->isLiquidInStructureBoundingBox($w, $box)){
			return false;
		}

		$i = $this->numSections * 5 - 1;

		$this->fillWithBlocks($w, $box, 0, 0, 0, 2, 1, $i, self::AIR, 0, self::AIR, 0, false);
		$this->randomlyFillWithBlocks($w, $box, $rnd, 0.8, 0, 2, 0, 2, 2, $i, self::AIR, self::AIR, false);

		if($this->spiderCorridor){
			$this->randomlyFillWithBlocks($w, $box, $rnd, 0.6, 0, 0, 0, 2, 1, $i, self::WEB, self::AIR, false);
		}

		for($k = 0; $k < $this->numSections; $k++){
			$j = 2 + $k * 5;

			$this->fillWithBlocks($w, $box, 0, 0, $j, 0, 1, $j, self::FENCE, 0, self::AIR, 0, false);
			$this->fillWithBlocks($w, $box, 2, 0, $j, 2, 1, $j, self::FENCE, 0, self::AIR, 0, false);

			if($rnd->nextBoundedInt(4) === 0){
				$this->fillWithBlocks($w, $box, 0, 2, $j, 0, 2, $j, self::PLANKS, 0, self::AIR, 0, false);
				$this->fillWithBlocks($w, $box, 2, 2, $j, 2, 2, $j, self::PLANKS, 0, self::AIR, 0, false);
			}else{
				$this->fillWithBlocks($w, $box, 0, 2, $j, 2, 2, $j, self::PLANKS, 0, self::AIR, 0, false);
			}

			$this->randomlyPlaceBlock($w, $box, $rnd, 0.1, 0, 2, $j - 1, self::WEB, 0);
			$this->randomlyPlaceBlock($w, $box, $rnd, 0.1, 2, 2, $j - 1, self::WEB, 0);
			$this->randomlyPlaceBlock($w, $box, $rnd, 0.1, 0, 2, $j + 1, self::WEB, 0);
			$this->randomlyPlaceBlock($w, $box, $rnd, 0.1, 2, 2, $j + 1, self::WEB, 0);
			$this->randomlyPlaceBlock($w, $box, $rnd, 0.05, 0, 2, $j - 2, self::WEB, 0);
			$this->randomlyPlaceBlock($w, $box, $rnd, 0.05, 2, 2, $j - 2, self::WEB, 0);
			$this->randomlyPlaceBlock($w, $box, $rnd, 0.05, 0, 2, $j + 2, self::WEB, 0);
			$this->randomlyPlaceBlock($w, $box, $rnd, 0.05, 2, 2, $j + 2, self::WEB, 0);
			$this->randomlyPlaceBlock($w, $box, $rnd, 0.05, 1, 2, $j - 1, self::TORCH, 0);
			$this->randomlyPlaceBlock($w, $box, $rnd, 0.05, 1, 2, $j + 1, self::TORCH, 0);

			if($rnd->nextBoundedInt(100) === 0){
				$this->generateMinecartChest($w, $box, $rnd, 2, 0, $j - 1);
			}
			if($rnd->nextBoundedInt(100) === 0){
				$this->generateMinecartChest($w, $box, $rnd, 0, 0, $j + 1);
			}

			if($this->spiderCorridor && !$this->placedSpawner){
				$l = $this->getYWithOffset(0);
				$i1 = $j - 1 + $rnd->nextBoundedInt(3);
				$j1 = $this->getXWithOffset(1, $i1);
				$i1 = $this->getZWithOffset(1, $i1);

				if($box->resultIsInside($j1, $l, $i1)){
					$this->placedSpawner = true;
					$w->setBlock($j1, $l, $i1, self::SPAWNER, 0);
					$w->setSpawnerEntity($j1, $l, $i1, CaveSpider::NETWORK_ID);
				}
			}
		}

		for($k = 0; $k <= 2; $k++){
			for($j = 0; $j <= $i; $j++){
				if($this->getBlock($w, $k, -1, $j, $box) === self::AIR){
					$this->placeBlock($w, self::PLANKS, 0, $k, -1, $j, $box);
				}
			}
		}

		if($this->hasRails){
			for($k = 0; $k <= $i; $k++){
				$b = $this->getBlock($w, 1, -1, $k, $box);
				if($b !== self::AIR && !self::isLiquid($b)){
					$this->randomlyPlaceBlock($w, $box, $rnd, 0.7, 1, 0, $k, self::RAIL, $this->getMetadataWithOffset(self::RAIL, 0));
				}
			}
		}

		return true;
	}

	private function generateMinecartChest(WorldAccess $w, BoundingBox $box, JavaRandom $rnd, $x, $y, $z){
		$worldX = $this->getXWithOffset($x, $z);
		$worldY = $this->getYWithOffset($y);
		$worldZ = $this->getZWithOffset($x, $z);

		if($box->resultIsInside($worldX, $worldY, $worldZ) && $w->getBlockId($worldX, $worldY, $worldZ) === self::AIR){
			$this->generateChest($w, $box, $rnd, $x, $y, $z, 'mineshaft');
		}
	}
}
