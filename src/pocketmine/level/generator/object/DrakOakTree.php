<?php

namespace pocketmine\level\generator\object;

use pocketmine\block\Block;
use pocketmine\block\Wood2;
use pocketmine\level\ChunkManager;
use pocketmine\utils\Random;

class DrakOakTree extends Tree{

	public function __construct(){
		$this->trunkBlock = Block::WOOD2;
		$this->leafBlock = Block::LEAVES2;
		$this->type = Wood2::DARK_OAK;
	}
	
	public function placeObject(ChunkManager $level, $x, $y, $z, Random $random){
		$this->treeHeight = $random->nextBoundedInt(4) + 7;
		$this->placeTrunk($level, $x, $y, $z, $random, $this->treeHeight - 1);

		for($yy = $y - 3 + $this->treeHeight; $yy <= $y + $this->treeHeight; ++$yy){
			$yOff = $yy - ($y + $this->treeHeight);
			$mid = (int) (2 - $yOff / 2);
			for($xx = $x - $mid; $xx <= $x + $mid; ++$xx){
				$xOff = abs($xx - $x);
				for($zz = $z - $mid; $zz <= $z + $mid; ++$zz){
					$zOff = abs($zz - $z);
					if($xOff === $mid and $zOff === $mid and ($yOff === 0 or $random->nextBoundedInt(2) === 0)){
						continue;
					}
					if(!Block::$solid[$level->getBlockIdAt($xx, $yy, $zz)]){
						$level->setBlockIdAt($xx, $yy, $zz, $this->leafBlock);
						$level->setBlockDataAt($xx, $yy, $zz, $this->type);
					}
				}
			}
		}
	}
	
	protected function placeTrunk(ChunkManager $level, $x, $y, $z, Random $random, $trunkHeight){
		for($xx = 0; $xx <= 1; ++$xx){
			for($zz = 0; $zz <= 1; ++$zz){
				$level->setBlockIdAt($x + $xx, $y - 1, $z + $zz, Block::DIRT);
				for($yy = 0; $yy < $trunkHeight; ++$yy){
					$blockId = $level->getBlockIdAt($x, $y + $yy, $z);
					if(isset($this->overridable[$blockId])){
						$level->setBlockIdAt($x + $xx, $y + $yy, $z + $zz, $this->trunkBlock);
						$level->setBlockDataAt($x + $xx, $y + $yy, $z + $zz, $this->type);
					}
				}
			}
		}
	}
}