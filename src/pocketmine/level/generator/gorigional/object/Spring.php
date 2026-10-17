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


namespace pocketmine\level\generator\gorigional\object;

use pocketmine\block\Block;
use pocketmine\level\generator\gorigional\JavaRandom;

/**
 * 移植自 SCAXE-GO-CE pkg/level/generator/object/spring.go。
 */
class Spring implements Feature{
	/** @var int */
	private $blockID;

	public function __construct($blockID){
		$this->blockID = $blockID;
	}

	public function generate(ObjectChunkManager $w, JavaRandom $r, BlockPos $pos){
		if($w->getBlockId($pos->x, $pos->y + 1, $pos->z) !== Block::STONE){
			return false;
		}
		if($w->getBlockId($pos->x, $pos->y - 1, $pos->z) !== Block::STONE){
			return false;
		}

		$state = $w->getBlockId($pos->x, $pos->y, $pos->z);
		if($state !== 0 && $state !== Block::STONE){
			return false;
		}

		$i = 0;
		if($w->getBlockId($pos->x - 1, $pos->y, $pos->z) === Block::STONE){
			$i++;
		}
		if($w->getBlockId($pos->x + 1, $pos->y, $pos->z) === Block::STONE){
			$i++;
		}
		if($w->getBlockId($pos->x, $pos->y, $pos->z - 1) === Block::STONE){
			$i++;
		}
		if($w->getBlockId($pos->x, $pos->y, $pos->z + 1) === Block::STONE){
			$i++;
		}

		$j = 0;
		if($w->getBlockId($pos->x - 1, $pos->y, $pos->z) === 0){
			$j++;
		}
		if($w->getBlockId($pos->x + 1, $pos->y, $pos->z) === 0){
			$j++;
		}
		if($w->getBlockId($pos->x, $pos->y, $pos->z - 1) === 0){
			$j++;
		}
		if($w->getBlockId($pos->x, $pos->y, $pos->z + 1) === 0){
			$j++;
		}

		if($i === 3 && $j === 1){
			$w->setBlock($pos->x, $pos->y, $pos->z, $this->blockID, 0);
			return true;
		}

		return false;
	}
}
