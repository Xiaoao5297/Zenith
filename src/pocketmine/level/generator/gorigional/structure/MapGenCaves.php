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
 * Java 1.7.10 MapGenCaves 的移植。
 *
 * 旧的实现来自 SCAXE-GO-CE 的 MCPE LargeCaveFeature，两者在"是否生成、生成几个、
 * 隧道怎么拐弯"上都不同。现在按 `docs/remapped_server_1.7.10/net/minecraft/world/gen/MapGenCaves.java`
 * 逐段重写：
 *  - 数量 = nextInt(nextInt(nextInt(15) + 1) + 1)，随后 nextInt(7) != 0 直接清零（约 1/7 保留）；
 *  - 每个洞穴原点用 MapGenBase 的 (x * l) ^ (z * i1) ^ worldSeed 播种 Java LCG；
 *  - 房间、主隧道、分叉、y 裁剪、岩浆层、草方块下方回填都按 Java；
 *  - 挖洞前会先检查包围盒里有没有水（Java 的 flag2），有水就整条放弃。
 *
 * 与 Java 的唯一区别是世界高度：Java 是 0..255，这里是 0..127，
 * 因此 y 的上限从 248 变成 maxHeight - 8。
 */
class MapGenCaves extends MapGenBase{
	/** @var int 世界高度（有效 y 为 0..maxHeight-1） */
	public $maxHeight = 128;

	public function generateChunk($chunkX, $chunkZ, FullChunk $chunk){
		$this->generate($chunkX, $chunkZ, $chunk);
	}

	public function recursiveGenerate($chunkX, $chunkZ, $originX, $originZ, FullChunk $chunk){
		// Java: int i1 = rand.nextInt(rand.nextInt(rand.nextInt(15) + 1) + 1);
		$count = $this->rand->nextBoundedInt($this->rand->nextBoundedInt($this->rand->nextBoundedInt(15) + 1) + 1);

		// Java: if (rand.nextInt(7) != 0) i1 = 0;
		if($this->rand->nextBoundedInt(7) !== 0){
			$count = 0;
		}

		for($i = 0; $i < $count; $i++){
			$d0 = (double) ($originX * 16 + $this->rand->nextBoundedInt(16));
			$d1 = (double) $this->rand->nextBoundedInt($this->rand->nextBoundedInt(120) + 8);
			$d2 = (double) ($originZ * 16 + $this->rand->nextBoundedInt(16));

			$branches = 1;
			if($this->rand->nextBoundedInt(4) === 0){
				$this->addRoom($this->rand->nextLong(), $chunkX, $chunkZ, $chunk, $d0, $d1, $d2);
				$branches += $this->rand->nextBoundedInt(4);
			}

			for($j = 0; $j < $branches; $j++){
				$yaw = $this->rand->nextFloat() * M_PI * 2.0;
				$pitch = ($this->rand->nextFloat() - 0.5) * 2.0 / 8.0;
				$size = $this->rand->nextFloat() * 2.0 + $this->rand->nextFloat();

				if($this->rand->nextBoundedInt(10) === 0){
					$size *= $this->rand->nextFloat() * $this->rand->nextFloat() * 3.0 + 1.0;
				}

				$this->addTunnel($this->rand->nextLong(), $chunkX, $chunkZ, $chunk, $d0, $d1, $d2, $size, $yaw, $pitch, 0, 0, 1.0);
			}
		}
	}

	/**
	 * Java MapGenCaves.func_151542_a：先挖一个球形房间，再从房间里长隧道。
	 */
	private function addRoom($seed, $chunkX, $chunkZ, FullChunk $chunk, $x, $y, $z){
		$this->addTunnel($seed, $chunkX, $chunkZ, $chunk, $x, $y, $z, 1.0 + $this->rand->nextFloat() * 6.0, 0.0, 0.0, -1, -1, 0.5);
	}

	/**
	 * Java MapGenCaves.func_151541_a（隧道 / 房间的椭球雕刻）。
	 *
	 * @param mixed $seed       Java 的 long 种子
	 * @param int   $chunkX     目标区块 X
	 * @param int   $chunkZ     目标区块 Z
	 * @param float $x          世界坐标
	 * @param float $y
	 * @param float $z
	 * @param float $size
	 * @param float $yaw
	 * @param float $pitch
	 * @param int   $currentStep -1 表示从中间开始
	 * @param int   $tunnelLength
	 * @param float $heightScale
	 */
	public function addTunnel($seed, $chunkX, $chunkZ, FullChunk $chunk, $x, $y, $z, $size, $yaw, $pitch, $currentStep, $tunnelLength, $heightScale){
		$rnd = new JavaRandom($seed);

		if($tunnelLength <= 0){
			$max = $this->range * 16 - 16;
			$tunnelLength = $max - $rnd->nextBoundedInt(intdiv($max, 4));
		}

		$isMiddle = false;
		if($currentStep === -1){
			$currentStep = intdiv($tunnelLength, 2);
			$isMiddle = true;
		}

		$branchStep = $rnd->nextBoundedInt(intdiv($tunnelLength, 2)) + intdiv($tunnelLength, 4);
		$wide = $rnd->nextBoundedInt(6) === 0;

		$chunkOriginX = $chunkX * 16 + 8;
		$chunkOriginZ = $chunkZ * 16 + 8;

		$yawDrift = 0.0;
		$pitchDrift = 0.0;

		for(; $currentStep < $tunnelLength; $currentStep++){
			$radius = 1.5 + JavaMath::sin($currentStep * M_PI / $tunnelLength) * $size;
			$radiusY = $radius * $heightScale;

			$cosPitch = JavaMath::cos($pitch);
			$sinPitch = JavaMath::sin($pitch);

			$x += JavaMath::cos($yaw) * $cosPitch;
			$y += $sinPitch;
			$z += JavaMath::sin($yaw) * $cosPitch;

			if($wide){
				$pitch *= 0.92;
			}else{
				$pitch *= 0.7;
			}

			$pitch += $pitchDrift * 0.1;
			$yaw += $yawDrift * 0.1;
			$pitchDrift *= 0.9;
			$yawDrift *= 0.75;
			$pitchDrift += ($rnd->nextFloat() - $rnd->nextFloat()) * $rnd->nextFloat() * 2.0;
			$yawDrift += ($rnd->nextFloat() - $rnd->nextFloat()) * $rnd->nextFloat() * 4.0;

			if(!$isMiddle && $currentStep === $branchStep && $size > 1.0 && $tunnelLength > 0){
				$this->addTunnel($rnd->nextLong(), $chunkX, $chunkZ, $chunk, $x, $y, $z, $rnd->nextFloat() * 0.5 + 0.5, $yaw - M_PI / 2.0, $pitch / 3.0, $currentStep, $tunnelLength, 1.0);
				$this->addTunnel($rnd->nextLong(), $chunkX, $chunkZ, $chunk, $x, $y, $z, $rnd->nextFloat() * 0.5 + 0.5, $yaw + M_PI / 2.0, $pitch / 3.0, $currentStep, $tunnelLength, 1.0);
				return;
			}

			if($isMiddle || $rnd->nextBoundedInt(4) !== 0){
				$dx = $x - $chunkOriginX;
				$dz = $z - $chunkOriginZ;
				$remaining = $tunnelLength - $currentStep;
				$reach = $size + 2.0 + 16.0;

				if($dx * $dx + $dz * $dz - $remaining * $remaining > $reach * $reach){
					return;
				}

				if($x >= $chunkOriginX - 16.0 - $radius * 2.0 && $z >= $chunkOriginZ - 16.0 - $radius * 2.0
					&& $x <= $chunkOriginX + 16.0 + $radius * 2.0 && $z <= $chunkOriginZ + 16.0 + $radius * 2.0){
					$x0 = (int) floor($x - $radius) - $chunkX * 16 - 1;
					$x1 = (int) floor($x + $radius) - $chunkX * 16 + 1;
					$y0 = (int) floor($y - $radiusY) - 1;
					$y1 = (int) floor($y + $radiusY) + 1;
					$z0 = (int) floor($z - $radius) - $chunkZ * 16 - 1;
					$z1 = (int) floor($z + $radius) - $chunkZ * 16 + 1;

					if($x0 < 0){
						$x0 = 0;
					}
					if($x1 > 16){
						$x1 = 16;
					}
					if($y0 < 1){
						$y0 = 1;
					}
					if($y1 > $this->maxHeight - 8){
						$y1 = $this->maxHeight - 8;
					}
					if($z0 < 0){
						$z0 = 0;
					}
					if($z1 > 16){
						$z1 = 16;
					}

					if($this->containsWater($chunk, $x0, $x1, $y0, $y1, $z0, $z1)){
						continue;
					}

					for($bx = $x0; $bx < $x1; $bx++){
						$nx = ($bx + $chunkX * 16 + 0.5 - $x) / $radius;

						for($bz = $z0; $bz < $z1; $bz++){
							$nz = ($bz + $chunkZ * 16 + 0.5 - $z) / $radius;

							if($nx * $nx + $nz * $nz >= 1.0){
								continue;
							}

							$sawGrass = false;

							for($by = $y1 - 1; $by >= $y0; $by--){
								$ny = ($by + 0.5 - $y) / $radiusY;

								if($ny > -0.7 && $nx * $nx + $ny * $ny + $nz * $nz < 1.0){
									$id = $chunk->getBlockId($bx, $by, $bz);

									if($id === 2){ // 草方块
										$sawGrass = true;
									}

									if($id === 1 || $id === 3 || $id === 2){ // 石头 / 泥土 / 草方块
										if($by < 10){
											$chunk->setBlock($bx, $by, $bz, 10, 0); // 岩浆
										}else{
											$chunk->setBlock($bx, $by, $bz, 0, 0);

											if($sawGrass && $chunk->getBlockId($bx, $by - 1, $bz) === 3){
												$chunk->setBlock($bx, $by - 1, $bz, $this->topBlockAt($chunk, $bx, $bz), 0);
											}
										}
									}
								}
							}
						}
					}

					if($isMiddle){
						break;
					}
				}
			}
		}
	}

	/**
	 * Java 的 flag2：包围盒内只要有水（8/9），整条隧道放弃。
	 * Java 只对盒子边界做完整扫描，内部柱子只看最上/最下两格，这里保持一致。
	 */
	private function containsWater(FullChunk $chunk, $x0, $x1, $y0, $y1, $z0, $z1){
		for($x = $x0; $x < $x1; $x++){
			for($z = $z0; $z < $z1; $z++){
				for($y = $y1 + 1; $y >= $y0 - 1; $y--){
					if($y >= 0 && $y < $this->maxHeight){
						$id = $chunk->getBlockId($x, $y, $z);
						if($id === 8 || $id === 9){
							return true;
						}
					}

					if($y !== $y0 - 1 && $x !== $x0 && $x !== $x1 - 1 && $z !== $z0 && $z !== $z1 - 1){
						$y = $y0;
					}
				}
			}
		}

		return false;
	}

	/**
	 * Java 用 world.getBiomeGenForCoords(x, z).topBlock 回填被挖掉草方块后的泥土。
	 */
	private function topBlockAt(FullChunk $chunk, $x, $z){
		$biome = BiomeRegistry::getBiome($chunk->getBiomeId($x & 15, $z & 15) & 0xFF);

		return $biome->topBlock;
	}
}
