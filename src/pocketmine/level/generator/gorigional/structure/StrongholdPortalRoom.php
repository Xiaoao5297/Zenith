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
 * it under the terms of the GNU General Public License as published by
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
 * 末地传送门房间，参考 Java StructureStrongholdPieces$PortalRoom。
 */
class StrongholdPortalRoom extends StrongholdPiece{

	public function __construct($type, JavaRandom $rnd, BoundingBox $box, $facing){
		parent::__construct($type);
		$this->coordBaseMode = $facing;
		$this->entryDoor = 0;
		$this->boundingBox = $box;
	}

	public static function create(array $list, JavaRandom $rnd, $x, $y, $z, $facing, $type){
		$box = self::makeBox($x, $y, $z, -4, -1, 0, 11, 8, 16, $facing);
		if(!self::validBox($box) || self::findIntersecting($list, $box) !== null){
			return null;
		}
		return new self($type, $rnd, $box, $facing);
	}

	public function buildComponent(StructureComponent $component = null, array &$components, JavaRandom $rnd){
		// Java：传送门房间把自己登记到起始 Stairs2，MapGenStronghold 用它判断要塞是否完整。
		if($component instanceof StrongholdStairs2){
			$component->portalRoom = $this;
		}
	}

	public function addComponentParts(WorldAccess $w, JavaRandom $rnd, BoundingBox $box){
		$this->generateShell($w, $rnd, $box, 10, 7, 15);
		$this->generateDoor($w, $box, 4, 1, 0, 3);

		$i = 6;
		$this->fillRandomBricks($w, $box, 1, $i, 1, 1, $i, 14, $rnd, false);
		$this->fillRandomBricks($w, $box, 9, $i, 1, 9, $i, 14, $rnd, false);
		$this->fillRandomBricks($w, $box, 2, $i, 1, 8, $i, 2, $rnd, false);
		$this->fillRandomBricks($w, $box, 2, $i, 14, 8, $i, 14, $rnd, false);
		$this->fillRandomBricks($w, $box, 1, 1, 1, 2, 1, 4, $rnd, false);
		$this->fillRandomBricks($w, $box, 8, 1, 1, 9, 1, 4, $rnd, false);
		$this->fillWithBlocks($w, $box, 1, 1, 1, 1, 1, 3, self::LAVA, 0, self::LAVA, 0, false);
		$this->fillWithBlocks($w, $box, 9, 1, 1, 9, 1, 3, self::LAVA, 0, self::LAVA, 0, false);
		$this->fillRandomBricks($w, $box, 3, 1, 8, 7, 1, 12, $rnd, false);
		$this->fillWithBlocks($w, $box, 4, 1, 9, 6, 1, 11, self::LAVA, 0, self::LAVA, 0, false);

		for($j = 3; $j < 14; $j += 2){
			$this->fillWithBlocks($w, $box, 0, 3, $j, 0, 4, $j, self::IRON_BARS, 0, self::IRON_BARS, 0, false);
			$this->fillWithBlocks($w, $box, 10, 3, $j, 10, 4, $j, self::IRON_BARS, 0, self::IRON_BARS, 0, false);
		}
		for($j = 2; $j < 9; $j += 2){
			$this->fillWithBlocks($w, $box, $j, 3, 15, $j, 4, 15, self::IRON_BARS, 0, self::IRON_BARS, 0, false);
		}

		$stairsMeta = $this->getMetadataWithOffset(self::STONE_BRICK_STAIRS, 3);
		$this->fillRandomBricks($w, $box, 4, 1, 5, 6, 1, 7, $rnd, false);
		$this->fillRandomBricks($w, $box, 4, 2, 6, 6, 2, 7, $rnd, false);
		$this->fillRandomBricks($w, $box, 4, 3, 7, 6, 3, 7, $rnd, false);
		for($k = 4; $k <= 6; $k++){
			$this->placeBlock($w, self::STONE_BRICK_STAIRS, $stairsMeta, $k, 1, 4, $box);
			$this->placeBlock($w, self::STONE_BRICK_STAIRS, $stairsMeta, $k, 2, 5, $box);
			$this->placeBlock($w, self::STONE_BRICK_STAIRS, $stairsMeta, $k, 3, 6, $box);
		}

		$b0 = 2;
		$b1 = 0;
		$b2 = 3;
		$b3 = 1;
		switch($this->coordBaseMode){
			case 0:
				$b0 = 0;
				$b1 = 2;
				break;
			case 1:
				$b0 = 1;
				$b1 = 3;
				$b2 = 0;
				$b3 = 2;
				break;
			case 3:
				$b0 = 3;
				$b1 = 1;
				$b2 = 0;
				$b3 = 2;
		}

		foreach([[4, 8], [5, 8], [6, 8]] as $p){
			$this->placeBlock($w, self::END_PORTAL_FRAME, $b0 + ($rnd->nextFloat() > 0.9 ? 4 : 0), $p[0], 3, $p[1], $box);
		}
		foreach([[4, 12], [5, 12], [6, 12]] as $p){
			$this->placeBlock($w, self::END_PORTAL_FRAME, $b1 + ($rnd->nextFloat() > 0.9 ? 4 : 0), $p[0], 3, $p[1], $box);
		}
		foreach([[3, 9], [3, 10], [3, 11]] as $p){
			$this->placeBlock($w, self::END_PORTAL_FRAME, $b2 + ($rnd->nextFloat() > 0.9 ? 4 : 0), $p[0], 3, $p[1], $box);
		}
		foreach([[7, 9], [7, 10], [7, 11]] as $p){
			$this->placeBlock($w, self::END_PORTAL_FRAME, $b3 + ($rnd->nextFloat() > 0.9 ? 4 : 0), $p[0], 3, $p[1], $box);
		}

		$worldY = $this->getYWithOffset(3);
		$worldX = $this->getXWithOffset(5, 6);
		$worldZ = $this->getZWithOffset(5, 6);
		if($box->resultIsInside($worldX, $worldY, $worldZ)){
			$this->placeSpawner($w, $box, 5, 3, 6, 39);
		}
		return true;
	}

}
