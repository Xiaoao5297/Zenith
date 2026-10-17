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
 * 移植自 Java 1.7.10 StructureVillagePieces$Path。
 */
class VillagePath extends VillageRoad{
	/** @var int */
	private $length;

	public function __construct(VillageWellStart $start, $typeId, JavaRandom $rnd, BoundingBox $box, $facing){
		parent::__construct($start, $typeId);
		$this->coordBaseMode = $facing;
		$this->boundingBox = $box;
		$this->length = max($box->getXSize(), $box->getZSize());
	}

	public function buildComponent(StructureComponent $component = null, array &$components, JavaRandom $rnd){
		$flag = false;
		$bb = $this->boundingBox;

		for($i = $rnd->nextBoundedInt(5); $i < $this->length - 8; $i += 2 + $rnd->nextBoundedInt(5)){
			$c = $this->addVillagePiece($component, $components, $rnd, 0, $i);
			if($c !== null){
				$cb = $c->getBoundingBox();
				$i += max($cb->getXSize(), $cb->getZSize());
				$flag = true;
			}
		}

		for($i = $rnd->nextBoundedInt(5); $i < $this->length - 8; $i += 2 + $rnd->nextBoundedInt(5)){
			$c = $this->addVillagePiece2($component, $components, $rnd, 0, $i);
			if($c !== null){
				$cb = $c->getBoundingBox();
				$i += max($cb->getXSize(), $cb->getZSize());
				$flag = true;
			}
		}

		if($flag && $rnd->nextBoundedInt(3) > 0){
			switch($this->coordBaseMode){
				case 0:
					VillagePieces::addPath($component, $components, $rnd, $bb->minX - 1, $bb->minY, $bb->maxZ - 2, 1, $this->componentType);
					break;
				case 1:
					VillagePieces::addPath($component, $components, $rnd, $bb->minX, $bb->minY, $bb->minZ - 1, 2, $this->componentType);
					break;
				case 2:
					VillagePieces::addPath($component, $components, $rnd, $bb->minX - 1, $bb->minY, $bb->minZ, 1, $this->componentType);
					break;
				case 3:
					VillagePieces::addPath($component, $components, $rnd, $bb->maxX - 2, $bb->minY, $bb->minZ - 1, 2, $this->componentType);
					break;
			}
		}

		if($flag && $rnd->nextBoundedInt(3) > 0){
			switch($this->coordBaseMode){
				case 0:
					VillagePieces::addPath($component, $components, $rnd, $bb->maxX + 1, $bb->minY, $bb->maxZ - 2, 3, $this->componentType);
					break;
				case 1:
					VillagePieces::addPath($component, $components, $rnd, $bb->minX, $bb->minY, $bb->maxZ + 1, 0, $this->componentType);
					break;
				case 2:
					VillagePieces::addPath($component, $components, $rnd, $bb->maxX + 1, $bb->minY, $bb->minZ, 3, $this->componentType);
					break;
				case 3:
					VillagePieces::addPath($component, $components, $rnd, $bb->maxX - 2, $bb->minY, $bb->maxZ + 1, 0, $this->componentType);
					break;
			}
		}
	}

	public static function createBox(VillageWellStart $start, array $list, JavaRandom $rnd, $i, $j, $k, $l){
		for($i1 = 7 * $rnd->nextRange(3, 5); $i1 >= 7; $i1 -= 7){
			$box = BoundingBox::getComponentToAddBoundingBox($i, $j, $k, 0, 0, 0, 3, 3, $i1, $l);
			if(self::findIntersecting($list, $box) === null){
				return $box;
			}
		}
		return null;
	}

	public function addComponentParts(WorldAccess $w, JavaRandom $rnd, BoundingBox $box){
		$block = $this->substituteBlock(13);

		for($i = $this->boundingBox->minX; $i <= $this->boundingBox->maxX; $i++){
			for($j = $this->boundingBox->minZ; $j <= $this->boundingBox->maxZ; $j++){
				if($box->resultIsInside($i, 64, $j)){
					$k = $w->getHeight($i, $j) - 1;
					$w->setBlock($i, $k, $j, $block, 0);
				}
			}
		}

		return true;
	}
}
