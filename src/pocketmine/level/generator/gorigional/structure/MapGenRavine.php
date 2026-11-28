<?php

/***
 *  _____                _  __   __
 * /__  /  ___   ____   (_)/ /_ / /_
 *   / /  / _ \ / __ \ / // __// __ \
 *  / /__/  __// / / // // /_ / / / /
 * /____/\___//_/ /_//_/ \__//_/ /_/
 *
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * @author Xiaoao
 * @link https://github.com/Xiaoao5297/Zenith
 *
 *
 */


namespace pocketmine\level\generator\gorigional\structure;

use pocketmine\level\format\FullChunk;
use pocketmine\level\generator\gorigional\biome\BiomeRegistry;
use pocketmine\level\generator\gorigional\Int64;
use pocketmine\level\generator\gorigional\JavaMath;
use pocketmine\level\generator\gorigional\JavaRandom;

/**
 * 移植自 Java 1.7.10 MapGenRavine。
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

				$this->recursiveGenerate($chunkX, $chunkZ, $x, $z, $chunk);
			}
		}
	}

	private function recursiveGenerate($chunkX, $chunkZ, $x, $z, FullChunk $chunk){
		// Java MapGenRavine.func_151538_a：1/50 的区块原点拥有峡谷。
		if($this->rand->nextBoundedInt(50) !== 0){
			return;
		}

		$d0 = $x * 16 + $this->rand->nextBoundedInt(16);
		$d1 = $this->rand->nextBoundedInt($this->rand->nextBoundedInt(40) + 8) + 20;
		$d2 = $z * 16 + $this->rand->nextBoundedInt(16);

		$f = $this->rand->nextFloat() * (M_PI * 2.0);
		$f1 = ($this->rand->nextFloat() - 0.5) * 2.0 / 8.0;
		$f2 = ($this->rand->nextFloat() * 2.0 + $this->rand->nextFloat()) * 2.0;

		$this->addTunnel($this->rand->nextLong(), $chunkX, $chunkZ, $chunk, $d0, $d1, $d2, $f2, $f, $f1, 0, 0, 3.0);
	}

	private function addTunnel($seed, $chunkX, $chunkZ, FullChunk $chunk, $x, $y, $z, $scale, $yaw, $pitch, $startStep, $endStep, $heightMod){
		$r = new JavaRandom($seed);

		$cx = $chunkX * 16 + 8;
		$cz = $chunkZ * 16 + 8;

		$f3 = 0.0;
		$f4 = 0.0;

		if($endStep <= 0){
			$j1 = $this->range * 16 - 16;
			$endStep = $j1 - $r->nextBoundedInt(intdiv($j1, 4));
		}

		$flag = false;
		if($startStep === -1){
			$startStep = intdiv($endStep, 2);
			$flag = true;
		}

		// Java field_75046_d：峡谷每个高度的横向扩张系数。
		$noise = [];
		$f5 = 1.0;
		for($k1 = 0; $k1 < 256; $k1++){
			if($k1 === 0 || $r->nextBoundedInt(3) === 0){
				$f5 = 1.0 + $r->nextFloat() * $r->nextFloat();
			}
			$noise[$k1] = $f5 * $f5;
		}

		for(; $startStep < $endStep; $startStep++){
			$d6 = 1.5 + JavaMath::sin($startStep * M_PI / $endStep) * $scale;
			$d7 = $d6 * $heightMod;

			$d6 *= $r->nextFloat() * 0.25 + 0.75;
			$d7 *= $r->nextFloat() * 0.25 + 0.75;

			$f6 = JavaMath::cos($pitch);
			$f7 = JavaMath::sin($pitch);

			$x += JavaMath::cos($yaw) * $f6;
			$y += $f7;
			$z += JavaMath::sin($yaw) * $f6;

			$pitch *= 0.7;
			$pitch += $f4 * 0.05;
			$yaw += $f3 * 0.05;
			$f4 *= 0.8;
			$f3 *= 0.5;
			$f4 += ($r->nextFloat() - $r->nextFloat()) * $r->nextFloat() * 2.0;
			$f3 += ($r->nextFloat() - $r->nextFloat()) * $r->nextFloat() * 4.0;

			if(!$flag && $r->nextBoundedInt(4) !== 0){
				continue;
			}

			$d8 = $x - $cx;
			$d9 = $z - $cz;
			$d10 = $endStep - $startStep;
			$d11 = $scale + 2.0 + 16.0;

			if($d8 * $d8 + $d9 * $d9 - $d10 * $d10 > $d11 * $d11){
				return;
			}

			if($x < $cx - 16.0 - $d6 * 2.0 || $z < $cz - 16.0 - $d6 * 2.0 || $x > $cx + 16.0 + $d6 * 2.0 || $z > $cz + 16.0 + $d6 * 2.0){
				continue;
			}

			$l1 = (int) floor($x - $d6) - $chunkX * 16 - 1;
			$i2 = (int) floor($x + $d6) - $chunkX * 16 + 1;
			$j2 = (int) floor($y - $d7) - 1;
			$k2 = (int) floor($y + $d7) + 1;
			$l2 = (int) floor($z - $d6) - $chunkZ * 16 - 1;
			$i3 = (int) floor($z + $d6) - $chunkZ * 16 + 1;

			if($l1 < 0){
				$l1 = 0;
			}
			if($i2 > 16){
				$i2 = 16;
			}
			if($j2 < 1){
				$j2 = 1;
			}
			if($k2 > $this->maxHeight - 8){
				$k2 = $this->maxHeight - 8;
			}
			if($l2 < 0){
				$l2 = 0;
			}
			if($i3 > 16){
				$i3 = 16;
			}

			// Java：候选包围盒里只要出现水，整段峡谷不雕刻。
			$hasWater = false;
			for($k3 = $l1; !$hasWater && $k3 < $i2; $k3++){
				for($l3 = $l2; !$hasWater && $l3 < $i3; $l3++){
					for($i4 = $k2 + 1; !$hasWater && $i4 >= $j2 - 1; $i4--){
						if($i4 >= 0 && $i4 < $this->maxHeight){
							$waterID = $chunk->getBlockId($k3, $i4, $l3);
							if($waterID === 8 || $waterID === 9){
								$hasWater = true;
							}
							if($i4 !== $j2 - 1 && $k3 !== $l1 && $k3 !== $i2 - 1 && $l3 !== $l2 && $l3 !== $i3 - 1){
								$i4 = $j2;
							}
						}
					}
				}
			}

			if(!$hasWater){
				for($k3 = $l1; $k3 < $i2; $k3++){
					$d12 = ($k3 + $chunkX * 16 + 0.5 - $x) / $d6;

					for($j3 = $l2; $j3 < $i3; $j3++){
						$d13 = ($j3 + $chunkZ * 16 + 0.5 - $z) / $d6;
						$flag2 = false;

						if($d12 * $d12 + $d13 * $d13 < 1.0){
							for($k4 = $k2 - 1; $k4 >= $j2; $k4--){
								$d14 = ($k4 + 0.5 - $y) / $d7;

								if(($d12 * $d12 + $d13 * $d13) * $noise[$k4] + $d14 * $d14 / 6.0 < 1.0){
									$b = $chunk->getBlockId($k3, $k4, $j3);
									if($b === 2){
										$flag2 = true;
									}
									if($b === 1 || $b === 3 || $b === 2){
										if($k4 < 10){
											$chunk->setBlock($k3, $k4, $j3, 10, 0);
										}else{
											$chunk->setBlock($k3, $k4, $j3, 0, 0);
											if($flag2 && $k4 - 1 >= 0 && $chunk->getBlockId($k3, $k4 - 1, $j3) === 3){
												$topBiome = BiomeRegistry::getBiome($chunk->getBiomeId($k3, $j3));
												$chunk->setBlock($k3, $k4 - 1, $j3, $topBiome->getTopBlock(), $topBiome->getTopMeta());
											}
										}
									}
								}
							}
						}
					}
				}

				if($flag){
					break;
				}
			}
		}
	}
}
