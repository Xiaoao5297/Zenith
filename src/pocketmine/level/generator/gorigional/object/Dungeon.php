<?php

namespace pocketmine\level\generator\gorigional\object;

use pocketmine\block\Block;
use pocketmine\level\generator\gorigional\JavaRandom;

/**
 * 移植自 SCAXE-GO-CE pkg/level/generator/object/dungeon.go。
 */
class Dungeon implements Feature{

	public function generate(ObjectChunkManager $w, JavaRandom $r, BlockPos $pos){
		$j = $r->nextBoundedInt(2) + 2;
		$k = -$j - 1;
		$l = $j + 1;

		$k1 = $r->nextBoundedInt(2) + 2;
		$l1 = -$k1 - 1;
		$i2 = $k1 + 1;
		$j2 = 0;

		for($k2 = $k; $k2 <= $l; $k2++){
			for($l2 = -1; $l2 <= 4; $l2++){
				for($i3 = $l1; $i3 <= $i2; $i3++){
					$target = $pos->add($k2, $l2, $i3);
					$mat = $w->getBlockId($target->x, $target->y, $target->z);
					$isSolid = $mat !== 0 && $mat !== Block::WATER && $mat !== Block::STILL_WATER;

					if($l2 === -1 && !$isSolid){
						return false;
					}
					if($l2 === 4 && !$isSolid){
						return false;
					}

					if(($k2 === $k || $k2 === $l || $i3 === $l1 || $i3 === $i2) && $l2 === 0 && $w->getBlockId($target->x, $target->y, $target->z) === 0 && $w->getBlockId($target->x, $target->y + 1, $target->z) === 0){
						$j2++;
					}
				}
			}
		}

		if($j2 >= 1 && $j2 <= 5){
			for($k3 = $k; $k3 <= $l; $k3++){
				for($i4 = 3; $i4 >= -1; $i4--){
					for($k4 = $l1; $k4 <= $i2; $k4++){
						$target = $pos->add($k3, $i4, $k4);

						if($k3 !== $k && $i4 !== -1 && $k4 !== $l1 && $k3 !== $l && $i4 !== 4 && $k4 !== $i2){
							if($w->getBlockId($target->x, $target->y, $target->z) !== Block::CHEST){
								$above = $w->getBlockId($target->x, $target->y + 1, $target->z);
								$existing = $w->getBlockId($target->x, $target->y, $target->z);
								if($above !== Block::WATER && $above !== Block::STILL_WATER && $existing !== Block::WATER && $existing !== Block::STILL_WATER){
									$w->setBlock($target->x, $target->y, $target->z, 0, 0);
								}
							}
						}elseif($target->y >= 0 && $w->getBlockId($target->x, $target->y - 1, $target->z) === 0){
							$w->setBlock($target->x, $target->y, $target->z, 0, 0);
						}elseif($w->getBlockId($target->x, $target->y, $target->z) !== 0 && $w->getBlockId($target->x, $target->y, $target->z) !== Block::CHEST){
							if($i4 === -1 && $r->nextBoundedInt(4) !== 0){
								$w->setBlock($target->x, $target->y, $target->z, Block::MOSS_STONE, 0);
							}else{
								$w->setBlock($target->x, $target->y, $target->z, Block::COBBLESTONE, 0);
							}
						}
					}
				}
			}

			$w->setBlock($pos->x, $pos->y, $pos->z, Block::MONSTER_SPAWNER, 0);
			return true;
		}
		return false;
	}
}
