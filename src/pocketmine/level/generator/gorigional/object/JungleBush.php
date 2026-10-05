<?php

namespace pocketmine\level\generator\gorigional\object;

use pocketmine\block\Block;
use pocketmine\level\generator\gorigional\JavaRandom;

/**
 * 移植自 SCAXE-GO-CE pkg/level/generator/object/shrub_jungle.go。
 */
class JungleBush implements Feature{
	/** @var int */
	private $logBlockID;
	/** @var int */
	private $logMeta;
	/** @var int */
	private $leafBlockID;
	/** @var int */
	private $leafMeta;

	public function __construct($logID, $logMeta, $leafID, $leafMeta){
		$this->logBlockID = $logID;
		$this->logMeta = $logMeta;
		$this->leafBlockID = $leafID;
		$this->leafMeta = $leafMeta;
	}

	public function generate(ObjectChunkManager $level, JavaRandom $random, BlockPos $pos){
		$x = $pos->x;
		$y = $pos->y;
		$z = $pos->z;

		while($y > 0 && $level->getBlockId($x, $y, $z) === 0){
			$y--;
		}
		$y++;

		$soil = $level->getBlockId($x, $y - 1, $z);
		if($soil === Block::GRASS || $soil === Block::DIRT){
			$level->setBlock($x, $y - 1, $z, Block::DIRT, 0);

			$level->setBlock($x, $y, $z, $this->logBlockID, $this->logMeta);

			for($l = $y; $l <= $y + 2; $l++){
				$yOff = $l - $y;
				$radius = 2 - $yOff;

				for($xx = $x - $radius; $xx <= $x + $radius; $xx++){
					for($zz = $z - $radius; $zz <= $z + $radius; $zz++){
						if(abs($xx - $x) !== $radius || abs($zz - $z) !== $radius || $random->nextBoundedInt(2) !== 0){
							$id = $level->getBlockId($xx, $l, $zz);
							if($id === 0 || $id === Block::LEAVES){
								$level->setBlock($xx, $l, $zz, $this->leafBlockID, $this->leafMeta);
							}
						}
					}
				}
			}
			return true;
		}
		return false;
	}
}
