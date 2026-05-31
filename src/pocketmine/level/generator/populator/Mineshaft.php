<?php

namespace pocketmine\level\generator\populator;

use pocketmine\block\Block;
use pocketmine\level\ChunkManager;
use pocketmine\math\Vector3;
use pocketmine\utils\Random;

class Mineshaft extends Populator{
    /** @var ChunkManager */
    private $level;
    /** @var Random */
    private $random;

	public function populate(ChunkManager $level, $chunkX, $chunkZ, Random $random){
        $this->level = $level;
        $this->random = $random;

        //生成废弃矿井的概率
        if($random->nextBoundedInt(8) == 1){
            //生成废弃矿井的中心位置
            $x = $cx = ($chunkX << 4) + $random->nextBoundedInt(16);
            $z = $cz = ($chunkZ << 4) + $random->nextBoundedInt(16);
            $y = $cy = $random->nextBoundedInt(20) + $random->nextBoundedInt(25) + 10;

            //生成废弃矿井的主轴方向
            $direction = $random->nextBoundedInt(4);

            //生成废弃矿井的主轴长度
            $length = $random->nextBoundedInt(35) + 40;

            //生成废弃矿井的主轴
            for($i = 0; $i < $length; $i++){
				if($i % 5 == 0){
					switch($direction){
						case 0: //x轴正向
							$x = $cx + $i;
							$y = $cy;
							$z = $cz;
							break;
						case 1: //x轴负向
							$x = $cx - $i;
							$y = $cy;
							$z = $cz;
							break;
						case 2: //z轴正向
							$x = $cx;
							$y = $cy;
							$z = $cz + $i;
							break;
						case 3: //z轴负向
							$x = $cx;
							$y = $cy;
							$z = $cz - $i;
							break;
					}
					$this->generateShaft($x, $y, $z, $direction);
					if($this->random->nextRange(0, 3) == 0){
						$this->generateBranch($x, $y, $z, $direction);
					}
				}
            }
        }
    }

    private function generateShaft(int $x, int $y, int $z, int $direction = 0){
        //生成一个3x3x3的空间，用木板和栅栏作为支撑
		switch($direction){
			case 0:
			case 1:
				for($xx = -2; $xx <= 2; $xx++){
					for($yy = -1; $yy <= 1; $yy++){
						for($zz = -1; $zz <= 1; $zz++){
							$this->level->setBlockIdAt($x + $xx, $y + $yy, $z + $zz, Block::AIR);
						}
					}
				}
				
				$this->level->setBlockIdAt($x, $y + 1, $z + 1, Block::PLANKS);
				$this->level->setBlockIdAt($x, $y + 1, $z, Block::PLANKS);
				$this->level->setBlockIdAt($x, $y + 1, $z - 1, Block::PLANKS);
				
				$this->level->setBlockIdAt($x, $y, $z + 1, Block::FENCE);
				$this->level->setBlockIdAt($x, $y - 1, $z + 1, Block::FENCE);
				$this->level->setBlockIdAt($x, $y, $z - 1, Block::FENCE);
				$this->level->setBlockIdAt($x, $y - 1, $z - 1, Block::FENCE);
				break;
			case 2:
			case 3:
				for($xx = -1; $xx <= 1; $xx++){
					for($yy = -1; $yy <= 1; $yy++){
						for($zz = -2; $zz <= 2; $zz++){
							$this->level->setBlockIdAt($x + $xx, $y + $yy, $z + $zz, Block::AIR);
						}
					}
				}
				
				$this->level->setBlockIdAt($x + 1, $y + 1, $z, Block::PLANKS);
				$this->level->setBlockIdAt($x, $y + 1, $z, Block::PLANKS);
				$this->level->setBlockIdAt($x - 1, $y + 1, $z, Block::PLANKS);
				
				$this->level->setBlockIdAt($x + 1, $y, $z, Block::FENCE);
				$this->level->setBlockIdAt($x + 1, $y - 1, $z, Block::FENCE);
				$this->level->setBlockIdAt($x - 1, $y, $z, Block::FENCE);
				$this->level->setBlockIdAt($x - 1, $y - 1, $z, Block::FENCE);
				break;
		}
    }

    private function generateBranch(int $x, int $y, int $z, int $direction){
        //生成一个支路，长度随机，方向与主轴垂直
        $length = $this->random->nextBoundedInt(20) + 15;
        switch($direction){
            case 0: //主轴x轴正向
            case 1: //主轴x轴负向
                $branchDirection = $this->random->nextRange(2, 3); //支路方向z轴正向或负向
                for($i = 0; $i < $length; $i++){
					if($i % 5 == 0){
						if($branchDirection == 0){
							$this->generateShaft($x, $y, $z + $i, $branchDirection);
						}else{
							$this->generateShaft($x, $y, $z - $i, $branchDirection);
						}
					}
                }
                break;
            case 2: //主轴z轴正向
            case 3: //主轴z轴负向
                $branchDirection = $this->random->nextRange(0, 1); //支路方向x轴正向或负向
                for($i = 0; $i < $length; $i++){
					if($i % 5 == 0){
						if($branchDirection == 0){
							$this->generateShaft($x + $i, $y, $z, $branchDirection);
						}else{
							$this->generateShaft($x - $i, $y, $z, $branchDirection);
						}
					}
                }
                break;
        }
    }
}