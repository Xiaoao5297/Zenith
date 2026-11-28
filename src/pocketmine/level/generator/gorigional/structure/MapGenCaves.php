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
use pocketmine\level\generator\gorigional\JavaMath;
use pocketmine\level\generator\gorigional\JavaRandom;

/**
 * 移植自 SCAXE-GO-CE pkg/level/generator/gorigional/structure/map_gen_caves.go。
 */
class MapGenCaves extends MapGenBase{
	/** @var int */
	public $maxHeight = 256;

	public function generateChunk($cx, $cz, FullChunk $chunk){
		$this->generate($cx, $cz, $chunk);
	}

	public function recursiveGenerate($chunkX, $chunkZ, $originX, $originZ, FullChunk $chunk){
		// Java MapGenCaves.func_151538_a：1/7 的区块原点拥有洞穴系统。
		$nodes = $this->rand->nextBoundedInt($this->rand->nextBoundedInt($this->rand->nextBoundedInt(15) + 1) + 1);
		if($this->rand->nextBoundedInt(7) !== 0){
			$nodes = 0;
		}
		$descriptors = [];

		for($j = 0; $j < $nodes; $j++){
			$rx = $originX * 16 + $this->rand->nextBoundedInt(16);
			$ry = $this->rand->nextBoundedInt($this->rand->nextBoundedInt(120) + 8);
			$rz = $originZ * 16 + $this->rand->nextBoundedInt(16);

			$count = 1;
			if($this->rand->nextBoundedInt(4) === 0){
				$descriptors[] = [
					'room',
					$this->rand->nextLong(),
					$rx, $ry, $rz,
					1.0 + $this->rand->nextFloat() * 6.0
				];
				$count += $this->rand->nextBoundedInt(4);
			}

			for($k = 0; $k < $count; $k++){
				$f = $this->rand->nextFloat() * M_PI * 2.0;
				$f1 = ($this->rand->nextFloat() - 0.5) * 2.0 / 8.0;
				$size = $this->rand->nextFloat() * 2.0 + $this->rand->nextFloat();

				if($this->rand->nextBoundedInt(10) === 0){
					$size *= ($this->rand->nextFloat() * $this->rand->nextFloat() * 3.0 + 1.0);
				}

				$descriptors[] = [
					'tunnel',
					$this->rand->nextLong(),
					$rx, $ry, $rz,
					$size, $f, $f1
				];
			}
		}

		foreach($descriptors as $descriptor){
			if($descriptor[0] === 'room'){
				$this->addTunnel($descriptor[1], $chunkX, $chunkZ, $chunk, $descriptor[2], $descriptor[3], $descriptor[4], $descriptor[5], 0.0, 0.0, -1, -1, 0.5);
			}else{
				$this->addTunnel($descriptor[1], $chunkX, $chunkZ, $chunk, $descriptor[2], $descriptor[3], $descriptor[4], $descriptor[5], $descriptor[6], $descriptor[7], 0, 0, 1.0);
			}
		}
	}

	public function addTunnel($seed, $chunkX, $chunkZ, FullChunk $chunk, $x, $y, $z, $size, $yaw, $pitch, $currentStep, $tunnelLength, $heightScale){
		$cx = $chunkX * 16 + 8;
		$cz = $chunkZ * 16 + 8;

		$f = 0.0;
		$f1 = 0.0;

		$rnd = new JavaRandom($seed);

		if($tunnelLength <= 0){
			$i = $this->range * 16 - 16;
			$tunnelLength = $i - $rnd->nextBoundedInt(intdiv($i, 4));
		}

		$flag = false;
		if($currentStep === -1){
			$currentStep = intdiv($tunnelLength, 2);
			$flag = true;
		}

		$j = $rnd->nextBoundedInt(intdiv($tunnelLength, 2)) + intdiv($tunnelLength, 4);
		$flag1 = $rnd->nextBoundedInt(6) === 0;

		for(; $currentStep < $tunnelLength; $currentStep++){
			$d2 = 1.5 + JavaMath::sin($currentStep * M_PI / $tunnelLength) * $size;
			$d3 = $d2 * $heightScale;
			$f2 = JavaMath::cos($pitch);
			$f3 = JavaMath::sin($pitch);

			$x += JavaMath::cos($yaw) * $f2;
			$y += $f3;
			$z += JavaMath::sin($yaw) * $f2;

			if($flag1){
				$pitch *= 0.92;
			}else{
				$pitch *= 0.7;
			}

			$pitch += $f1 * 0.1;
			$yaw += $f * 0.1;
			$f1 *= 0.9;
			$f *= 0.75;
			$f1 += ($rnd->nextFloat() - $rnd->nextFloat()) * $rnd->nextFloat() * 2.0;
			$f += ($rnd->nextFloat() - $rnd->nextFloat()) * $rnd->nextFloat() * 4.0;

			if(!$flag && $currentStep === $j && $size > 1.0 && $tunnelLength > 0){
				$this->addTunnel($rnd->nextLong(), $chunkX, $chunkZ, $chunk, $x, $y, $z, $rnd->nextFloat() * 0.5 + 0.5, $yaw - M_PI / 2.0, $pitch / 3.0, $currentStep, $tunnelLength, 1.0);
				$this->addTunnel($rnd->nextLong(), $chunkX, $chunkZ, $chunk, $x, $y, $z, $rnd->nextFloat() * 0.5 + 0.5, $yaw + M_PI / 2.0, $pitch / 3.0, $currentStep, $tunnelLength, 1.0);
				return;
			}

			if($flag || $rnd->nextBoundedInt(4) !== 0){
				$d4 = $x - $cx;
				$d5 = $z - $cz;
				$d6 = $tunnelLength - $currentStep;
				$d7 = $size + 2.0 + 16.0;

				if($d4 * $d4 + $d5 * $d5 - $d6 * $d6 > $d7 * $d7){
					return;
				}

				if($x >= $cx - 16.0 - $d2 * 2.0 && $z >= $cz - 16.0 - $d2 * 2.0 && $x <= $cx + 16.0 + $d2 * 2.0 && $z <= $cz + 16.0 + $d2 * 2.0){
					$k = (int) ($x - $d2) - $chunkX * 16 - 1;
					$l = (int) ($x + $d2) - $chunkX * 16 + 1;
					$i1 = (int) ($y - $d3) - 1;
					$j1 = (int) ($y + $d3) + 1;
					$k1 = (int) ($z - $d2) - $chunkZ * 16 - 1;
					$l1 = (int) ($z + $d2) - $chunkZ * 16 + 1;

					if($k < 0){
						$k = 0;
					}
					if($l > 16){
						$l = 16;
					}
					if($i1 < 1){
						$i1 = 1;
					}
					if($j1 > $this->maxHeight - 8){
						$j1 = $this->maxHeight - 8;
					}
					if($k1 < 0){
						$k1 = 0;
					}
					if($l1 > 16){
						$l1 = 16;
					}

					// Java func_151541_a：先扫描整个候选包围盒，只要出现水就整段不雕刻。
					$hasWater = false;
					for($i3 = $k; !$hasWater && $i3 < $l; $i3++){
						for($k3 = $k1; !$hasWater && $k3 < $l1; $k3++){
							for($j3 = $j1 + 1; !$hasWater && $j3 >= $i1 - 1; $j3--){
								if($j3 >= 0 && $j3 < $this->maxHeight){
									$waterID = $chunk->getBlockId($i3, $j3, $k3);
									if($waterID === 8 || $waterID === 9){
										$hasWater = true;
									}
									if($j3 !== $i1 - 1 && $i3 !== $k && $i3 !== $l - 1 && $k3 !== $k1 && $k3 !== $l1 - 1){
										$j3 = $i1;
									}
								}
							}
						}
					}

					if(!$hasWater){
						for($i2 = $k; $i2 < $l; $i2++){
							$d8 = ($i2 + $chunkX * 16 + 0.5 - $x) / $d2;
							for($k2 = $k1; $k2 < $l1; $k2++){
								$d9 = ($k2 + $chunkZ * 16 + 0.5 - $z) / $d2;
								if($d8 * $d8 + $d9 * $d9 < 1.0){
									$flag3 = false;
									for($j2 = $j1; $j2 > $i1; $j2--){
										$d10 = ($j2 - 1 + 0.5 - $y) / $d3;
										if($d10 > -0.7 && $d8 * $d8 + $d10 * $d10 + $d9 * $d9 < 1.0){
											$b = $chunk->getBlockId($i2, $j2, $k2);
											if($b === 2){
												$flag3 = true;
											}
											if($b === 1 || $b === 3 || $b === 2){
												if($j2 < 10){
													$chunk->setBlock($i2, $j2, $k2, 10, 0);
												}else{
													$chunk->setBlock($i2, $j2, $k2, 0, 0);
													if($flag3 && $j2 - 1 >= 0 && $chunk->getBlockId($i2, $j2 - 1, $k2) === 3){
														$topBiome = BiomeRegistry::getBiome($chunk->getBiomeId($i2, $k2));
														$chunk->setBlock($i2, $j2 - 1, $k2, $topBiome->getTopBlock(), $topBiome->getTopMeta());
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
	}
}
