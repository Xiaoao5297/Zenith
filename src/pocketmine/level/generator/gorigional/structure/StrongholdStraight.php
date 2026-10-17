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
 * 直走廊，参考 Java StructureStrongholdPieces$Straight。
 */
class StrongholdStraight extends StrongholdPiece{
	/** @var bool */
	private $left;
	/** @var bool */
	private $right;

	public function __construct($type, JavaRandom $rnd, BoundingBox $box, $facing){
		parent::__construct($type);
		$this->coordBaseMode = $facing;
		$this->entryDoor = $rnd->nextBoundedInt(5) <= 1 ? 0 : ($rnd->nextBoundedInt(5) === 2 ? 1 : ($rnd->nextBoundedInt(5) === 3 ? 3 : 2));
		$this->boundingBox = $box;
		$this->left = $rnd->nextBoundedInt(2) === 0;
		$this->right = $rnd->nextBoundedInt(2) === 0;
	}

	public static function create(array $list, JavaRandom $rnd, $x, $y, $z, $facing, $type){
		$box = self::makeBox($x, $y, $z, -1, -1, 0, 5, 5, 7, $facing);
		if(!self::validBox($box) || self::findIntersecting($list, $box) !== null){
			return null;
		}
		return new self($type, $rnd, $box, $facing);
	}

	public function buildComponent(StructureComponent $component = null, array &$components, JavaRandom $rnd){
		$this->createForward($component, $components, $rnd, 1, 1);
		if($this->left){
			$this->createLeft($component, $components, $rnd, 1, 2);
		}
		if($this->right){
			$this->createRight($component, $components, $rnd, 1, 2);
		}
	}

	public function addComponentParts(WorldAccess $w, JavaRandom $rnd, BoundingBox $box){
		$this->generateShell($w, $rnd, $box, 4, 4, 6);
		$this->generateDoor($w, $box, 1, 1, 0, $this->entryDoor);
		$this->generateDoor($w, $box, 1, 1, 6, 0);

		if($this->left){
			$this->fillWithAir($w, $box, 0, 1, 2, 0, 3, 4);
		}
		if($this->right){
			$this->fillWithAir($w, $box, 4, 1, 2, 4, 3, 4);
		}

		foreach([[1, 2, 1], [3, 2, 1], [1, 2, 5], [3, 2, 5]] as $p){
			if($rnd->nextFloat() < 0.1){
				$this->placeBlock($w, self::TORCH, 0, $p[0], $p[1], $p[2], $box);
			}
		}
		return true;
	}
}
