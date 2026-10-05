<?php

namespace pocketmine\level\generator\gorigional\structure;

use pocketmine\level\format\FullChunk;
use pocketmine\level\generator\gorigional\Int64;
use pocketmine\level\generator\gorigional\JavaRandom;

/**
 * 移植自 SCAXE-GO-CE pkg/level/generator/gorigional/structure/map_gen_ravine.go。
 */
class MapGenRavine{
	/** @var int */
	private $range = 8;
	/** @var int */
	private $worldSeed;
	/** @var JavaRandom */
	private $rand;
	/** @var int */
	public $maxHeight = 256;

	public function __construct($seed){
		$this->worldSeed = $seed;
		$this->rand = new JavaRandom($seed);
	}

	public function generateChunk($chunkX, $chunkZ, FullChunk $chunk){
		$this->rand->setSeed($this->worldSeed);
		$r1 = $this->rand->nextLong();
		$r2 = $this->rand->nextLong();

		for($x = $chunkX - $this->range; $x <= $chunkX + $this->range; $x++){
			for($z = $chunkZ - $this->range; $z <= $chunkZ + $this->range; $z++){
				$seed = Int64::mul($x, $r1) ^ Int64::mul($z, $r2) ^ $this->worldSeed;
				$this->rand->setSeed($seed);

				if($this->rand->nextFloat() < 0.02){
					$this->recursiveGenerate($chunkX, $chunkZ, $x, $z, $chunk);
				}
			}
		}
	}

	private function recursiveGenerate($chunkX, $chunkZ, $x, $z, FullChunk $chunk){
		$d0 = $x * 16 + $this->rand->nextBoundedInt(16);
		$d1 = $this->rand->nextBoundedInt($this->rand->nextBoundedInt(40) + 8) + 20;
		$d2 = $z * 16 + $this->rand->nextBoundedInt(16);

		$f = $this->rand->nextFloat() * (M_PI * 2.0);
		$f1 = ($this->rand->nextFloat() - 0.5) * 2.0 / 8.0;
		$f2 = ($this->rand->nextFloat() * 2.0 + $this->rand->nextFloat()) * 2.0;

		$steps = $this->rand->nextBoundedInt($this->rand->nextBoundedInt(140) + 8);

		$this->addTunnel($this->rand->nextLong(), $chunkX, $chunkZ, $chunk, $d0, $d1, $d2, $f, $f1, $f2, 0, $steps, 3.0);
	}

	private function addTunnel($seed, $chunkX, $chunkZ, FullChunk $chunk, $x, $y, $z, $yaw, $pitch, $scale, $startStep, $endStep, $heightMod){
		$r = new JavaRandom($seed);

		$cxMin = $chunkX * 16;
		$czMin = $chunkZ * 16;

		for($i = $startStep; $i < $endStep; $i++){
			$d0 = 1.5 + sin($i * M_PI / $endStep) * $scale;
			$d1 = $d0 * $heightMod;

			$d0 *= $r->nextFloat() * 0.25 + 0.75;
			$d1 *= $r->nextFloat() * 0.25 + 0.75;

			$x += cos($yaw);
			$z += sin($yaw);
			$y += sin($pitch);

			$pitch *= 0.7;
			$pitch += ($r->nextFloat() - $r->nextFloat()) * 0.05;
			$yaw += ($r->nextFloat() - $r->nextFloat()) * 0.05;

			if($r->nextFloat() < 0.25){
				$r->nextFloat();
				$r->nextFloat();
			}

			if($x < $cxMin - 16.0 - $d0 * 2.0 || $z < $czMin - 16.0 - $d0 * 2.0 || $x > $cxMin + 16.0 + $d0 * 2.0 || $z > $czMin + 16.0 + $d0 * 2.0){
				continue;
			}

			$minX = (int) floor($x - $d0);
			$maxX = (int) floor($x + $d0);
			$minY = (int) floor($y - $d1);
			$maxY = (int) floor($y + $d1);
			$minZ = (int) floor($z - $d0);
			$maxZ = (int) floor($z + $d0);

			if($minX < $chunkX * 16){
				$minX = $chunkX * 16;
			}
			if($maxX > $chunkX * 16 + 15){
				$maxX = $chunkX * 16 + 15;
			}
			if($minZ < $chunkZ * 16){
				$minZ = $chunkZ * 16;
			}
			if($maxZ > $chunkZ * 16 + 15){
				$maxZ = $chunkZ * 16 + 15;
			}
			if($minY < 1){
				$minY = 1;
			}
			if($maxY > $this->maxHeight - 8){
				$maxY = $this->maxHeight - 8;
			}

			for($ix = $minX; $ix <= $maxX; $ix++){
				$relX = $ix + 0.5 - $x;
				for($iz = $minZ; $iz <= $maxZ; $iz++){
					$relZ = $iz + 0.5 - $z;

					if($relX * $relX + $relZ * $relZ < $d0 * $d0){
						for($iy = $minY; $iy <= $maxY; $iy++){
							$relY = $iy + 0.5 - $y;
							if($relX * $relX + $relZ * $relZ < $d0 * $d0 && ($relX * $relX + $relZ * $relZ) * $heightMod + $relY * $relY < $d0 * $d0 * $heightMod){
								$lx = $ix & 15;
								$lz = $iz & 15;

								$id = $chunk->getBlockId($lx, $iy, $lz);

								if($id === 1 || $id === 2 || $id === 3){
									$aboveID = $chunk->getBlockId($lx, $iy + 1, $lz);
									if($aboveID === 8 || $aboveID === 9){
										continue;
									}

									$chunk->setBlock($lx, $iy, $lz, 0, 0);

									if($iy < 10){
										$chunk->setBlock($lx, $iy, $lz, 10, 0);
									}
								}
							}
						}
					}
				}
			}
		}
	}
}
