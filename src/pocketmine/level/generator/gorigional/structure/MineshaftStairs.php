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
 * 移植自 Java 1.7.10 StructureMineshaftPieces$Stairs。
 */
class MineshaftStairs extends MineshaftPiece{
	const AIR = 0;

	public function __construct($typeId, JavaRandom $rnd, BoundingBox $box, $facing){
		parent::__construct($typeId, $box, $facing);
	}

	public static function getStairsBoundingBox(array $components, JavaRandom $rnd, $x, $y, $z, $facing){
		$box = new BoundingBox($x, $y - 5, $z, $x, $y + 2, $z);

		switch($facing){
			case 0:
				$box->maxX = $x + 2;
				$box->maxZ = $z + 8;
				break;
			case 1:
				$box->minX = $x - 8;
				$box->maxZ = $z + 2;
				break;
			case 2:
				$box->maxX = $x + 2;
				$box->minZ = $z - 8;
				break;
			case 3:
				$box->maxX = $x + 8;
				$box->maxZ = $z + 2;
				break;
		}

		return self::findIntersecting($components, $box) !== null ? null : $box;
	}

	public function buildComponent(StructureComponent $component = null, array &$components, JavaRandom $rnd){
		$i = $this->componentType;
		$bb = $this->boundingBox;

		switch($this->coordBaseMode){
			case 0:
				self::getNextMineshaftComponent($component, $components, $rnd, $bb->minX, $bb->minY, $bb->maxZ + 1, 0, $i);
				break;
			case 1:
				self::getNextMineshaftComponent($component, $components, $rnd, $bb->minX - 1, $bb->minY, $bb->minZ, 1, $i);
				break;
			case 2:
				self::getNextMineshaftComponent($component, $components, $rnd, $bb->minX, $bb->minY, $bb->minZ - 1, 2, $i);
				break;
			case 3:
				self::getNextMineshaftComponent($component, $components, $rnd, $bb->maxX + 1, $bb->minY, $bb->minZ, 3, $i);
				break;
		}
	}

	public function addComponentParts(WorldAccess $w, JavaRandom $rnd, BoundingBox $box){
		if($this->isLiquidInStructureBoundingBox($w, $box)){
			return false;
		}

		$this->fillWithBlocks($w, $box, 0, 5, 0, 2, 7, 1, self::AIR, 0, self::AIR, 0, false);
		$this->fillWithBlocks($w, $box, 0, 0, 7, 2, 2, 8, self::AIR, 0, self::AIR, 0, false);

		for($i = 0; $i < 5; $i++){
			$this->fillWithBlocks($w, $box, 0, 5 - $i - ($i < 4 ? 1 : 0), 2 + $i, 2, 7 - $i, 2 + $i, self::AIR, 0, self::AIR, 0, false);
		}

		return true;
	}
}
