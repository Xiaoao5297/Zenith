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

use pocketmine\block\Block;

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
		$this->componentType = $componentType;
		$this->coordBaseMode = $facing;
		$this->width = $width;
		$this->height = $height;
		$this->depth = $depth;

		if($facing === 0 || $facing === 2){
			$this->boundingBox = new BoundingBox($x, $y, $z, $x + $width - 1, $y + $height - 1, $z + $depth - 1);
		}else{
			$this->boundingBox = new BoundingBox($x, $y, $z, $x + $depth - 1, $y + $height - 1, $z + $width - 1);
		}
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
		for($y = 127; $y >= 0; $y--){
			$id = $w->getBlockId($x, $y, $z);
			if($id !== 0 && $id !== 8 && $id !== 9 && $id !== 10 && $id !== 11 && Block::get($id)->isSolid()){
				return $y + 1;
			}
		}
		return 0;
	}
}
