<?php

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
