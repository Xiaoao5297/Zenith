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
 * 要塞起始部件（Stairs2），参考 Java StructureStrongholdPieces$Stairs2。
 */
class StrongholdStairs2 extends StrongholdStairs{
	/** @var StructureComponent[] */
	public $pendingPieces = [];

	public function __construct($type, JavaRandom $rnd, $x, $z){
		$facing = $rnd->nextBoundedInt(4);
		$box = new BoundingBox($x, 64, $z, $x + 4, 74, $z + 4);
		parent::__construct($type, $rnd, $box, $facing);
		$this->entryDoor = 0;
	}
}
