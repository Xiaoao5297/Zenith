<?php

namespace pocketmine\level\generator\populator;

use pocketmine\block\Block;
use pocketmine\level\ChunkManager;
use pocketmine\math\Math;
use pocketmine\math\Vector3;
use pocketmine\math\VectorMath;
use pocketmine\utils\Random;

class Cave extends Populator {
	
	private $biome;
	
	public function populate(ChunkManager $level, $chunkX, $chunkZ, Random $random){
		$this->biome = $level->getChunk($chunkX, $chunkZ)->getBiomeId(0, 0);
		if($random->nextBoundedInt(8) == 1){
			$caves[0] = $random->nextRange(1, 360); //旋转角度
			$caves[1] = $random->nextRange(45, 300); //洞穴长度
			$caves[2] = $random->nextRange(1, 5); //分叉数
			$caves[4] = $random->nextRange(1, 5); //洞穴强度
			$caves[3] = [false, true, true];
			
			if($this->biome == 0 or $this->biome == 10){
				$this->caves($random, $level, new Vector3($chunkX * 16, $random->nextRange(30, 55), $chunkZ * 16), $caves);
			}elseif($this->biome == 7 or $this->biome == 11){
				$this->caves($random, $level, new Vector3($chunkX * 16, $random->nextRange(30, 43), $chunkZ * 16), $caves);
			}elseif($this->biome == 24){
				$this->caves($random, $level, new Vector3($chunkX * 16, $random->nextRange(30, 35), $chunkZ * 16), $caves);
			}else{
				$this->caves($random, $level, new Vector3($chunkX * 16, $random->nextRange(30, 80), $chunkZ * 16), $caves);
			}
			//$level->getServer()->getLogger()->info("旋转角度:{$caves[0]} 洞穴长度:{$caves[1]} 分叉数:{$caves[2]} 洞穴强度:{$caves[4]}");
		}
		if($random->nextBoundedInt(5) == 1){
			$caves[0] = $random->nextRange(1, 360); //旋转角度
			$caves[1] = $random->nextRange(50, 300); //洞穴长度
			$caves[2] = $random->nextRange(1, 5); //分叉数
			$caves[4] = $random->nextRange(1, 5); //洞穴强度
			$caves[3] = [false, true, true];
			if($this->biome == 24){
				$this->caves($random, $level, new Vector3($chunkX * 16, $random->nextRange(10, 35), $chunkZ * 16), $caves);
			}else{
				$this->caves($random, $level, new Vector3($chunkX * 16, $random->nextRange(8, 55), $chunkZ * 16), $caves);
			}
		}
	}

    public function chu($v1, $v2) {
        if ($v2 == 0) return 0;
        return $v1 / $v2;
    }

    public function getDirectionVector($yaw, $pitch) {
        $y = -\sin(\deg2rad($pitch));
        $xz = \cos(\deg2rad($pitch));
        $x = -$xz * \sin(\deg2rad($yaw));
        $z = $xz * \cos(\deg2rad($yaw));

        $temporalVector = new Vector3($x, $y, $z);
        return $temporalVector->normalize();
    }

    public function caves(Random $random, ChunkManager $level, Vector3 $pos, $cave, $tt = false) {
        $x = $pos->x;
        $y = $pos->y;
        $z = $pos->z;
        $ls = $cave[1];  //长度
        $cv = $cave[2];  //分叉数
        $lofs = $ls / $cave[2];
        $ls2 = $lofs;
        $yaw = $cave[0];
        if ($cave[0] >= 0 || $cave[0] < 0) {
        } else {
            $yaw = $random->nextRange(0, 100) * 72;
        }
        $pitch = -45;
        $s1 = [$x, $y, $z];
        $s2 = [$x, $y, $z];
        $i = 1;
        for ($u = 0; $u <= $ls; $u += $i) {
            if ($pitch > 12) $pitch = -45;
            $pitch += 5 + $random->nextRange(0, 5);
            //$level->getServer()->getLogger()->info(TextFormat::YELLOW."yaw: $yaw  pitch: $pitch");
            $see = $this->getDirectionVector($yaw, $pitch);
            $s2[0] = $s1[0] + $see->x * $i;
            $s2[1] = $s1[1] - $see->y * $i;
            $s2[2] = $s1[2] + $see->z * $i;
            if ($s2[1] < 10) {
                $s2[1] = 10 + $random->nextRange(0, 10);
            }
            if ($u > $lofs) {
                $cv--;
                if ($cave[3][1] === false) $cv = 0;
                $lofs += $ls2;
                $newPos = new Vector3($s2[0], $s2[1], $s2[2]);
                $this->caves($random, $level, $newPos, [$yaw + 90 * (round($random->nextRange(0, 100) / 100) * 2 - 1), $ls - $u, $cv, [false, $cave[3][1], $cave[3][2]], 0], $tt);
            }

            if ($random->nextRange(0, 100) > 80) {
                $add = $random->nextRange(-10, 10);
            } else {
                $add = $random->nextRange(-45, 45);
            }
            $yaw = $yaw + $add;
            $yaw = $yaw % 360;
            $yaw = $yaw >= 0 ? $yaw : 360 + $yaw;

            $x = $s1[0];
            $y = $s1[1];
            $z = $s1[2];
            $x2 = $s2[0];
            $y2 = $s2[1];
            $z2 = $s2[2];
            $l = max(abs($x - $x2), abs($y - $y2), abs($z - $z2));
            for ($m = 0; $m <= $l; $m++) {
                $v = $level->getBlockIdAt(round($this->chu($x + $m, $l * ($x2 - $x))), round($y + $this->chu($m, $l * ($y2 - $y))), round($z + $this->chu($m, $l * ($z2 - $z))));
                if ($v != 10 and $v != 11){
					$liu = $random->nextRange(0, 200) == 100;
					$this->fdx(round($x + $this->chu($m, $l * ($x2 - $x))), round($y + $this->chu($m, $l * ($y2 - $y))), round($z + $this->chu($m, $l * ($z2 - $z))), $level, $liu);
				}
            }
            $s1 = [$s2[0], $s2[1], $s2[2]];
        }
        /*if ($random->nextRange(0, 10) >= 5 and $s2[1] <= 40) {
            $this->lavaSpawn($level, $s2[0], $s2[1], $s2[2]);
        }*/

    }

    public function lavaSpawn(ChunkManager $level, $x, $y, $z) {
        //$level->getServer()->getLogger()->info("生成岩浆中 "."floor($x)".", "."floor($y)".", ".floor($z));
        for ($xx = $x - 20; $xx <= $x + 20; $xx++) {
            for ($zz = $z - 20; $zz <= $z + 20; $zz++) {
                for ($yy = $y; $yy > $y - 4; $yy--) {
                    $id = $level->getBlockIdAt($xx, $yy, $zz);
                    if ($id == 0) {
                        $level->setBlockIdAt($xx, $yy, $zz, 10);
                        $level->setBlockDataAt($xx, $yy, $zz, 0);
                    }
                }
            }
        }
        $level->setBlockIdAt($x, $y, $z, 8);
    }

    public function fdx($x, $y, $z, ChunkManager $level, $liu = false) {
        for ($i = 1; $i < mt_rand(2, 4); $i++) {
            $level->setBlockIdAt($x + $i - 2, $y - 1, $z + 1, 0);
            $level->setBlockIdAt($x + $i - 2, $y - 1, $z, 0);
            $level->setBlockIdAt($x + $i - 2, $y - 1, $z - 1, 0);
            $level->setBlockIdAt($x + $i - 2, $y - 1, $z - 1, 0);
            $level->setBlockIdAt($x + $i - 2, $y - 1, $z + 1, 0);
            $level->setBlockIdAt($x + $i - 2, $y + 2, $z + 1, 0);
            $level->setBlockIdAt($x + $i - 2, $y + 2, $z, 0);
            $level->setBlockIdAt($x + $i - 2, $y + 2, $z - 1, 0);
        }
        for ($i = 1; $i < mt_rand(3, 6); $i++) {
            $level->setBlockIdAt($x + $i - 3, $y + 1, $z + 2, 0);
            $level->setBlockIdAt($x + $i - 3, $y + 1, $z + 1, 0);
            $level->setBlockIdAt($x + $i - 3, $y + 1, $z, 0);
            $level->setBlockIdAt($x + $i - 3, $y + 1, $z - 1, 0);
            $level->setBlockIdAt($x + $i - 3, $y + 1, $z - 2, 0);
            $level->setBlockIdAt($x + $i - 3, $y, $z + 2, 0);
            $level->setBlockIdAt($x + $i - 3, $y, $z + 1, 0);
            $level->setBlockIdAt($x + $i - 3, $y, $z, 0);
            $level->setBlockIdAt($x + $i - 3, $y, $z - 1, 0);
            $level->setBlockIdAt($x + $i - 3, $y, $z - 2, 0);
        }
        /*if ($liu) {
            $l = (mt_rand(0, 1) == 0) ? 10 : 8;
            $i = mt_rand(3, 6);
            $level->setBlockIdAt($x + $i - 3, $y + 1, $z + 3, $l);
        }*/
    }

}