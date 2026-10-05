<?php

namespace pocketmine\level\generator\gorigional\object;

use pocketmine\block\Block;
use pocketmine\level\generator\gorigional\JavaMath;
use pocketmine\level\generator\gorigional\JavaRandom;

/**
 * 移植自 SCAXE-GO-CE pkg/level/generator/object/ore.go。
 */
class Ore implements Feature{
	/** @var int */
	private $blockID;
	/** @var int */
	private $blockMeta;
	/** @var int */
	private $blockCount;

	public function __construct($id, $meta, $count){
		$this->blockID = $id;
		$this->blockMeta = $meta;
		$this->blockCount = $count;
	}

	public function generate(ObjectChunkManager $w, JavaRandom $r, BlockPos $pos){
		$f = $r->nextFloat() * M_PI;

		$sinF = JavaMath::sin($f);
		$cosF = JavaMath::cos($f);

		$fCount = $this->blockCount / 8.0;

		$d0 = $pos->x + 8 + $sinF * $fCount;
		$d1 = $pos->x + 8 - $sinF * $fCount;
		$d2 = $pos->z + 8 + $cosF * $fCount;
		$d3 = $pos->z + 8 - $cosF * $fCount;

		$d4 = $pos->y + $r->nextBoundedInt(3) - 2;
		$d5 = $pos->y + $r->nextBoundedInt(3) - 2;

		for($i = 0; $i < $this->blockCount; $i++){
			$f1 = $i / $this->blockCount;
			$d6 = $d0 + ($d1 - $d0) * $f1;
			$d7 = $d4 + ($d5 - $d4) * $f1;
			$d8 = $d2 + ($d3 - $d2) * $f1;

			$d9 = $r->nextDouble() * $this->blockCount / 16.0;

			$sinPiF1 = JavaMath::sin(M_PI * $f1);
			$d10 = ($sinPiF1 + 1.0) * $d9 + 1.0;
			$d11 = ($sinPiF1 + 1.0) * $d9 + 1.0;

			$j = JavaMath::floor($d6 - $d10 / 2.0);
			$k = JavaMath::floor($d7 - $d11 / 2.0);
			$l = JavaMath::floor($d8 - $d10 / 2.0);

			$i1 = JavaMath::floor($d6 + $d10 / 2.0);
			$j1 = JavaMath::floor($d7 + $d11 / 2.0);
			$k1 = JavaMath::floor($d8 + $d10 / 2.0);

			for($l1 = $j; $l1 <= $i1; $l1++){
				$d12 = ($l1 + 0.5 - $d6) / ($d10 / 2.0);

				if($d12 * $d12 < 1.0){
					for($i2 = $k; $i2 <= $j1; $i2++){
						$d13 = ($i2 + 0.5 - $d7) / ($d11 / 2.0);

						if($d12 * $d12 + $d13 * $d13 < 1.0){
							for($j2 = $l; $j2 <= $k1; $j2++){
								$d14 = ($j2 + 0.5 - $d8) / ($d10 / 2.0);

								if($d12 * $d12 + $d13 * $d13 + $d14 * $d14 < 1.0){
									if($i2 >= 0 && $i2 <= 255){
										if($w->getBlockId($l1, $i2, $j2) === Block::STONE){
											$w->setBlock($l1, $i2, $j2, $this->blockID, $this->blockMeta);
										}
									}
								}
							}
						}
					}
				}
			}
		}
		return true;
	}
}
