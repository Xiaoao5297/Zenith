<?php

namespace pocketmine\level\generator\gorigional\object;

use pocketmine\block\Block;
use pocketmine\level\generator\gorigional\JavaRandom;

/**
 * 移植自 SCAXE-GO-CE pkg/level/generator/object/tree_savanna.go。
 */
class SavannaTree extends Tree{

	public function __construct(){
		parent::__construct(Block::WOOD2, Block::LEAVES2, 0);
	}

	public function generate(ObjectChunkManager $level, JavaRandom $random, BlockPos $pos){
		$height = $random->nextBoundedInt(3) + $random->nextBoundedInt(3) + 5;
		$flag = true;

		$x = $pos->x;
		$y = $pos->y;
		$z = $pos->z;

		if($y >= 1 && $y + $height + 1 <= 256){
			for($j = $y; $j <= $y + 1 + $height; $j++){
				$k = 1;
				if($j === $y){
					$k = 0;
				}
				if($j >= $y + 1 + $height - 2){
					$k = 2;
				}

				for($l = $x - $k; $l <= $x + $k && $flag; $l++){
					for($i1 = $z - $k; $i1 <= $z + $k && $flag; $i1++){
						if($j >= 0 && $j < 256){
							$id = $level->getBlockId($l, $j, $i1);
							if(!isset($this->overrides[$id])){
								$flag = false;
							}
						}else{
							$flag = false;
						}
					}
				}
			}

			if(!$flag){
				return false;
			}

			$soil = $level->getBlockId($x, $y - 1, $z);
			if(($soil === Block::GRASS || $soil === Block::DIRT) && $y < 256 - $height - 1){
				$level->setBlock($x, $y - 1, $z, Block::DIRT, 0);

				$dir = $random->nextBoundedInt(4);

				$xOffset = 0;
				$zOffset = 0;
				switch($dir){
					case 0: $zOffset = -1; break;
					case 1: $zOffset = 1; break;
					case 2: $xOffset = -1; break;
					case 3: $xOffset = 1; break;
				}

				$k2 = $height - $random->nextBoundedInt(4) - 1;
				$l2 = 3 - $random->nextBoundedInt(3);
				$i3 = $x;
				$k1 = $z;
				$topY = 0;

				for($l1 = 0; $l1 < $height; $l1++){
					$i2 = $y + $l1;
					if($l1 >= $k2 && $l2 > 0){
						$i3 += $xOffset;
						$k1 += $zOffset;
						$l2--;
					}

					$level->setBlock($i3, $i2, $k1, Block::WOOD2, 0);
					$topY = $i2;
				}

				$pos2 = new BlockPos($i3, $topY, $k1);
				$this->generateCanopy($level, $pos2);

				$pos2 = $pos2->up(1);

				$dir1 = $random->nextBoundedInt(4);
				if($dir1 !== $dir){
					$xOffset1 = 0;
					$zOffset1 = 0;
					switch($dir1){
						case 0: $zOffset1 = -1; break;
						case 1: $zOffset1 = 1; break;
						case 2: $xOffset1 = -1; break;
						case 3: $xOffset1 = 1; break;
					}

					$l3 = $k2 - $random->nextBoundedInt(2) - 1;
					$k4 = 1 + $random->nextBoundedInt(3);
					$topYBranch = 0;
					$i3Branch = $x;
					$k1Branch = $z;

					for($l4 = $l3; $l4 < $height && $k4 > 0; $k4--){
						if($l4 >= 1){
							$j2 = $y + $l4;
							$i3Branch += $xOffset1;
							$k1Branch += $zOffset1;

							$level->setBlock($i3Branch, $j2, $k1Branch, Block::WOOD2, 0);
							$topYBranch = $j2;
						}
						$l4++;
					}

					if($topYBranch > 0){
						$pos3 = new BlockPos($i3Branch, $topYBranch, $k1Branch);
						$this->generateSmallCanopy($level, $pos3);
					}
				}

				return true;
			}
		}
		return false;
	}

	private function generateCanopy(ObjectChunkManager $level, BlockPos $pos){
		for($j3 = -3; $j3 <= 3; $j3++){
			for($i4 = -3; $i4 <= 3; $i4++){
				if(abs($j3) !== 3 || abs($i4) !== 3){
					$this->placeLeafAt($level, $pos->add($j3, 0, $i4));
				}
			}
		}

		$pos = $pos->up(1);
		for($k3 = -1; $k3 <= 1; $k3++){
			for($j4 = -1; $j4 <= 1; $j4++){
				$this->placeLeafAt($level, $pos->add($k3, 0, $j4));
			}
		}

		$this->placeLeafAt($level, $pos->east()->east());
		$this->placeLeafAt($level, $pos->west()->west());
		$this->placeLeafAt($level, $pos->south()->south());
		$this->placeLeafAt($level, $pos->north()->north());
	}

	private function generateSmallCanopy(ObjectChunkManager $level, BlockPos $pos){
		for($i5 = -2; $i5 <= 2; $i5++){
			for($k5 = -2; $k5 <= 2; $k5++){
				if(abs($i5) !== 2 || abs($k5) !== 2){
					$this->placeLeafAt($level, $pos->add($i5, 0, $k5));
				}
			}
		}

		$pos = $pos->up(1);
		for($j5 = -1; $j5 <= 1; $j5++){
			for($l5 = -1; $l5 <= 1; $l5++){
				$this->placeLeafAt($level, $pos->add($j5, 0, $l5));
			}
		}
	}

	private function placeLeafAt(ObjectChunkManager $level, BlockPos $pos){
		$id = $level->getBlockId($pos->x, $pos->y, $pos->z);
		if($id === 0 || $id === Block::LEAVES || $id === Block::LEAVES2){
			$level->setBlock($pos->x, $pos->y, $pos->z, Block::LEAVES2, 0);
		}
	}
}
