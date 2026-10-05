<?php

namespace pocketmine\level\generator\gorigional\object;

use pocketmine\block\Block;
use pocketmine\level\generator\gorigional\JavaRandom;

/**
 * 移植自 SCAXE-GO-CE pkg/level/generator/object/double_plant.go。
 */
class DoublePlant implements Feature{
	const SUNFLOWER = 0;
	const LILAC = 1;
	const GRASS = 2;
	const FERN = 3;
	const ROSE_BUSH = 4;
	const PEONY = 5;

	/** @var int */
	private $type;

	public function __construct($t){
		$this->type = $t;
	}

	public function generate(ObjectChunkManager $level, JavaRandom $random, BlockPos $pos){
		$placed = false;
		for($i = 0; $i < 64; $i++){
			$x = $pos->x + $random->nextBoundedInt(8) - $random->nextBoundedInt(8);
			$y = $pos->y + $random->nextBoundedInt(4) - $random->nextBoundedInt(4);
			$z = $pos->z + $random->nextBoundedInt(8) - $random->nextBoundedInt(8);

			if($y >= 254 || $y < 0){
				continue;
			}

			if($level->getBlockId($x, $y, $z) === 0 && $level->getBlockId($x, $y + 1, $z) === 0){
				$soil = $level->getBlockId($x, $y - 1, $z);
				if($soil === Block::GRASS || $soil === Block::DIRT || $soil === Block::FARMLAND){
					$level->setBlock($x, $y, $z, Block::DOUBLE_PLANT, $this->type);
					$level->setBlock($x, $y + 1, $z, Block::DOUBLE_PLANT, 8 | ($this->type & 7));
					$placed = true;
				}
			}
		}
		return $placed;
	}
}
