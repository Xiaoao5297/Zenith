<?php

namespace pocketmine\level\generator\gorigional\object;

use pocketmine\block\Block;
use pocketmine\level\generator\gorigional\JavaRandom;

/**
 * 移植自 SCAXE-GO-CE pkg/level/generator/object/lake.go。
 */
class Lake implements Feature{
	/** @var int */
	private $blockID;

	public function __construct($blockID){
		$this->blockID = $blockID;
	}

	public function generate(ObjectChunkManager $w, JavaRandom $r, BlockPos $pos){
		$x = $pos->x - 8;
		$y = $pos->y;
		$z = $pos->z - 8;

		while($y > 5 && $w->getBlockId($x, $y, $z) === 0){
			$y--;
		}

		if($y <= 4){
			return false;
		}

		$y -= 4;

		$aboolean = array_fill(0, 2048, false);
		$i = $r->nextBoundedInt(4) + 4;

		for($j = 0; $j < $i; $j++){
			$d0 = $r->nextDouble() * 6.0 + 3.0;
			$d1 = $r->nextDouble() * 4.0 + 2.0;
			$d2 = $r->nextDouble() * 6.0 + 3.0;
			$d3 = $r->nextDouble() * (16.0 - $d0 - 2.0) + 1.0 + $d0 / 2.0;
			$d4 = $r->nextDouble() * (8.0 - $d1 - 4.0) + 2.0 + $d1 / 2.0;
			$d5 = $r->nextDouble() * (16.0 - $d2 - 2.0) + 1.0 + $d2 / 2.0;

			for($l = 1; $l < 15; $l++){
				for($i1 = 1; $i1 < 15; $i1++){
					for($j1 = 1; $j1 < 7; $j1++){
						$d6 = ($l - $d3) / ($d0 / 2.0);
						$d7 = ($j1 - $d4) / ($d1 / 2.0);
						$d8 = ($i1 - $d5) / ($d2 / 2.0);
						$d9 = $d6 * $d6 + $d7 * $d7 + $d8 * $d8;

						if($d9 < 1.0){
							$aboolean[($l * 16 + $i1) * 8 + $j1] = true;
						}
					}
				}
			}
		}

		for($k1 = 0; $k1 < 16; $k1++){
			for($l2 = 0; $l2 < 16; $l2++){
				for($k = 0; $k < 8; $k++){
					$flag = !$aboolean[($k1 * 16 + $l2) * 8 + $k] && (($k1 < 15 && $aboolean[(($k1 + 1) * 16 + $l2) * 8 + $k]) ||
						($k1 > 0 && $aboolean[(($k1 - 1) * 16 + $l2) * 8 + $k]) ||
						($l2 < 15 && $aboolean[($k1 * 16 + $l2 + 1) * 8 + $k]) ||
						($l2 > 0 && $aboolean[($k1 * 16 + ($l2 - 1)) * 8 + $k]) ||
						($k < 7 && $aboolean[($k1 * 16 + $l2) * 8 + $k + 1]) ||
						($k > 0 && $aboolean[($k1 * 16 + $l2) * 8 + ($k - 1)]));

					if($flag){
						$mat = $w->getBlockId($x + $k1, $y + $k, $z + $l2);

						if($k >= 4 && ($mat === Block::WATER || $mat === Block::STILL_WATER || $mat === Block::LAVA || $mat === Block::STILL_LAVA)){
							return false;
						}

						if($k < 4 && $mat !== 0 && $mat !== $this->blockID){
							if($mat === 0){
								return false;
							}
						}
					}
				}
			}
		}

		for($l1 = 0; $l1 < 16; $l1++){
			for($i3 = 0; $i3 < 16; $i3++){
				for($i4 = 0; $i4 < 8; $i4++){
					if($aboolean[($l1 * 16 + $i3) * 8 + $i4]){
						$bx = $x + $l1;
						$by = $y + $i4;
						$bz = $z + $i3;

						$existing = $w->getBlockId($bx, $by, $bz);
						if($existing === Block::WATER || $existing === Block::STILL_WATER){
							continue;
						}

						$above = $w->getBlockId($bx, $by + 1, $bz);
						if($above === Block::WATER || $above === Block::STILL_WATER){
							continue;
						}

						$targetBlock = $this->blockID;
						if($i4 >= 4){
							$targetBlock = 0;
						}
						$w->setBlock($bx, $by, $bz, $targetBlock, 0);
					}
				}
			}
		}

		return true;
	}
}
