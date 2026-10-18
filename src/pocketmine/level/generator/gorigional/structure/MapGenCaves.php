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
 * it under the terms of the GNU Lesser General Public License as published by
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
use pocketmine\level\generator\gorigional\JavaMath;
use pocketmine\level\generator\gorigional\JavaRandom;
use pocketmine\level\generator\gorigional\McpeRandom;

/**
 * 移植自 SCAXE-GO-CE pkg/level/generator/gorigional/structure/map_gen_caves.go。
 */
class MapGenCaves extends MapGenBase{
	/** @var int */
	public $maxHeight = 256;

	/** @var array<string, array|null> 洞穴原点缓存：同一套洞穴不重复构造 */
	private $originCache = [];
	/** @var int|null */
	private $multiplierI = null;
	/** @var int|null */
	private $multiplierJ = null;

	public function __construct($seed){
		parent::__construct($seed);
		// MCPE 的洞穴内部随机流是 MT19937，而不是 Java LCG。
		$this->rand = new McpeRandom($seed);
	}

	public function generateChunk($cx, $cz, FullChunk $chunk){
		$this->generate($cx, $cz, $chunk);
	}

	/**
	 * 与 MapGenBase::generate 相同的播种规则，但增加按原点的缓存。
	 * 邻近区块会反复扫描同一批洞穴原点，缓存后不再重复重建 MT 和节点。
	 */
	public function generate($chunkX, $chunkZ, FullChunk $chunk){
		if($this->multiplierI === null){
			$seedRnd = new McpeRandom($this->worldSeed);
			$this->multiplierI = (($seedRnd->nextInt() >> 2) * 2 + 1) & 0xFFFFFFFF;
			$this->multiplierJ = (($seedRnd->nextInt() >> 2) * 2 + 1) & 0xFFFFFFFF;
		}

		for($x = $chunkX - $this->range; $x <= $chunkX + $this->range; $x++){
			for($z = $chunkZ - $this->range; $z <= $chunkZ + $this->range; $z++){
				$key = $x . ':' . $z;
				if(array_key_exists($key, $this->originCache)){
					if($this->originCache[$key] !== null){
						$this->applyCaves($this->originCache[$key], $chunkX, $chunkZ, $chunk);
					}
					continue;
				}

				$seed = ($this->worldSeed ^ ((($x * $this->multiplierI) + ($z * $this->multiplierJ)) & 0xFFFFFFFF)) & 0xFFFFFFFF;
				$this->rand->setSeed($seed);
				$this->recursiveGenerate($chunkX, $chunkZ, $x, $z, $chunk);
			}
		}
	}

	public function recursiveGenerate($chunkX, $chunkZ, $originX, $originZ, FullChunk $chunk){
		$key = $originX . ':' . $originZ;

		// MCPE LargeCaveFeature 只在约 1/15 的区块生成洞穴系统。
		if($this->rand->nextInt() % 15 !== 0){
			$this->originCache[$key] = null;
			return;
		}

		$nodes = $this->rand->nextBoundedInt($this->rand->nextBoundedInt($this->rand->nextBoundedInt(15) + 1) + 1);
		$descriptors = [];

		for($j = 0; $j < $nodes; $j++){
			$rx = $originX * 16 + $this->rand->nextBoundedInt(16);
			$ry = $this->rand->nextBoundedInt($this->rand->nextBoundedInt(120) + 8);
			$rz = $originZ * 16 + $this->rand->nextBoundedInt(16);

			$count = 1;
			if($this->rand->nextBoundedInt(4) === 0){
				$descriptors[] = [
					'room',
					$this->rand->nextInt() >> 1,
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
					$this->rand->nextInt() >> 1,
					$rx, $ry, $rz,
					$size, $f, $f1
				];
			}
		}

		$this->originCache[$key] = $descriptors;
		$this->applyCaves($descriptors, $chunkX, $chunkZ, $chunk);
	}

	private function applyCaves(array $descriptors, $chunkX, $chunkZ, FullChunk $chunk){
		foreach($descriptors as $descriptor){
			if($descriptor[0] === 'room'){
				$this->addTunnel($descriptor[1], $chunkX, $chunkZ, $chunk, $descriptor[2], $descriptor[3], $descriptor[4], $descriptor[5], 0.0, 0.0, -1, -1, 0.5);
			}else{
				$this->addTunnel($descriptor[1], $chunkX, $chunkZ, $chunk, $descriptor[2], $descriptor[3], $descriptor[4], $descriptor[5], $descriptor[6], $descriptor[7], 0, 0, 1.0);
			}
		}
	}

	private function addRoom($seed, $chunkX, $chunkZ, FullChunk $chunk, $x, $y, $z){
		$this->addTunnel($seed, $chunkX, $chunkZ, $chunk, $x, $y, $z, 1.0 + $this->rand->nextFloat() * 6.0, 0.0, 0.0, -1, -1, 0.5);
	}

	public function addTunnel($seed, $chunkX, $chunkZ, FullChunk $chunk, $x, $y, $z, $size, $yaw, $pitch, $currentStep, $tunnelLength, $heightScale){
		$cx = $chunkX * 16 + 8;
		$cz = $chunkZ * 16 + 8;

		$f = 0.0;
		$f1 = 0.0;

		$rnd = new McpeRandom($seed & 0x7FFFFFFF);

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

					for($i2 = $k; $i2 < $l; $i2++){
						$d8 = ($i2 + $chunkX * 16 + 0.5 - $x) / $d2;
						for($k2 = $k1; $k2 < $l1; $k2++){
							$d9 = ($k2 + $chunkZ * 16 + 0.5 - $z) / $d2;
							if($d8 * $d8 + $d9 * $d9 < 1.0){
								for($j2 = $j1; $j2 > $i1; $j2--){
									$d10 = ($j2 - 1 + 0.5 - $y) / $d3;
									if($d10 > -0.7 && $d8 * $d8 + $d10 * $d10 + $d9 * $d9 < 1.0){
										$b = $chunk->getBlockId($i2, $j2, $k2);
										if($b === 1 || $b === 3 || $b === 2){
											$aboveID = $chunk->getBlockId($i2, $j2 + 1, $k2);
											if($aboveID === 8 || $aboveID === 9){
												continue;
											}
											if($j2 < 10){
												$chunk->setBlock($i2, $j2, $k2, 10, 0);
											}else{
												$chunk->setBlock($i2, $j2, $k2, 0, 0);
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
	}
}
