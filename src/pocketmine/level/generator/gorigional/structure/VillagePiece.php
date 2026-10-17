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
 * 移植自 Java 1.7.10 StructureVillagePieces$Village。
 * 沙漠村庄会把木头/圆石/木板等替换为砂岩。
 */
abstract class VillagePiece extends StructureComponentBase{
	/** @var int */
	protected $terrainHeight = -1;
	/** @var int */
	protected $villagerCount = 0;
	/** @var bool */
	protected $isDesert = false;

	public function __construct(VillageWellStart $start = null, $typeId = 0){
		$this->componentType = $typeId;
		if($start !== null){
			$this->isDesert = $start->desertVillage;
		}
	}

	public function buildComponent(StructureComponent $component = null, array &$components, JavaRandom $rnd){

	}

	protected function substituteBlock($id){
		if($this->isDesert){
			if($id === 17 || $id === 162 || $id === 4 || $id === 5 || $id === 13){
				return 24;
			}
			if($id === 53 || $id === 67){
				return 128;
			}
		}
		return $id;
	}

	protected function substituteMeta($id, $meta){
		if($this->isDesert){
			if($id === 17 || $id === 162 || $id === 4){
				return 0;
			}
			if($id === 5){
				return 2;
			}
		}
		return $meta;
	}

	public function placeBlock(WorldAccess $w, $id, $meta, $x, $y, $z, BoundingBox $box){
		parent::placeBlock($w, $this->substituteBlock($id), $this->substituteMeta($id, $meta), $x, $y, $z, $box);
	}

	public function replaceAirAndLiquidDownwards(WorldAccess $w, $id, $meta, $x, $y, $z, BoundingBox $box){
		parent::replaceAirAndLiquidDownwards($w, $this->substituteBlock($id), $this->substituteMeta($id, $meta), $x, $y, $z, $box);
	}

	protected function getAverageGroundHeight(WorldAccess $w, BoundingBox $box){
		$total = 0;
		$count = 0;

		for($z = $this->boundingBox->minZ; $z <= $this->boundingBox->maxZ; $z++){
			for($x = $this->boundingBox->minX; $x <= $this->boundingBox->maxX; $x++){
				if($box->resultIsInside($x, 64, $z)){
					$total += max($w->getHeight($x, $z), 63);
					$count++;
				}
			}
		}

		if($count === 0){
			return -1;
		}
		return intdiv($total, $count);
	}

	protected static function isValidBox(BoundingBox $box = null){
		return $box !== null && $box->minY > 10;
	}

	protected function getVillagerProfession($i){
		return 0;
	}

	protected function placeDoor(WorldAccess $w, BoundingBox $box, JavaRandom $rnd, $x, $y, $z, $meta){
		$worldX = $this->getXWithOffset($x, $z);
		$worldY = $this->getYWithOffset($y);
		$worldZ = $this->getZWithOffset($x, $z);

		if($box->resultIsInside($worldX, $worldY, $worldZ)){
			$this->placeBlock($w, 64, $meta, $x, $y, $z, $box);
			$this->placeBlock($w, 64, $meta | 8, $x, $y + 1, $z, $box);
		}
	}

	/**
	 * 对应 Village.func_74891_a：向侧后方派生一个村庄部件。
	 */
	protected function addVillagePiece(VillageWellStart $start, array &$list, JavaRandom $rnd, $i, $j){
		$bb = $this->boundingBox;
		switch($this->coordBaseMode){
			case 0:
				return VillagePieces::addVillagePiece($start, $list, $rnd, $bb->minX - 1, $bb->minY + $i, $bb->minZ + $j, 1, $this->componentType);
			case 1:
				return VillagePieces::addVillagePiece($start, $list, $rnd, $bb->minX + $j, $bb->minY + $i, $bb->minZ - 1, 2, $this->componentType);
			case 2:
				return VillagePieces::addVillagePiece($start, $list, $rnd, $bb->minX - 1, $bb->minY + $i, $bb->minZ + $j, 1, $this->componentType);
			case 3:
				return VillagePieces::addVillagePiece($start, $list, $rnd, $bb->minX + $j, $bb->minY + $i, $bb->minZ - 1, 2, $this->componentType);
		}
		return null;
	}

	/**
	 * 对应 Village.func_74894_b。
	 */
	protected function addVillagePiece2(VillageWellStart $start, array &$list, JavaRandom $rnd, $i, $j){
		$bb = $this->boundingBox;
		switch($this->coordBaseMode){
			case 0:
				return VillagePieces::addVillagePiece($start, $list, $rnd, $bb->maxX + 1, $bb->minY + $i, $bb->minZ + $j, 3, $this->componentType);
			case 1:
				return VillagePieces::addVillagePiece($start, $list, $rnd, $bb->minX + $j, $bb->minY + $i, $bb->maxZ + 1, 0, $this->componentType);
			case 2:
				return VillagePieces::addVillagePiece($start, $list, $rnd, $bb->maxX + 1, $bb->minY + $i, $bb->minZ + $j, 3, $this->componentType);
			case 3:
				return VillagePieces::addVillagePiece($start, $list, $rnd, $bb->minX + $j, $bb->minY + $i, $bb->maxZ + 1, 0, $this->componentType);
		}
		return null;
	}
}
