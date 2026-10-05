<?php

namespace pocketmine\level\generator\gorigional\noise;

use pocketmine\level\generator\gorigional\JavaRandom;

/**
 * 逐位对应 Java 1.7 ImprovedNoise。
 * 移植自 SCAXE-GO-CE pkg/level/generator/gorigional/noise/improved.go。
 */
class ImprovedNoise{
	private static $gradX = [1.0, -1.0, 1.0, -1.0, 1.0, -1.0, 1.0, -1.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, -1.0, 0.0];
	private static $gradY = [1.0, 1.0, -1.0, -1.0, 0.0, 0.0, 0.0, 0.0, 1.0, -1.0, 1.0, -1.0, 1.0, -1.0, 1.0, -1.0];
	private static $gradZ = [0.0, 0.0, 0.0, 0.0, 1.0, 1.0, -1.0, -1.0, 1.0, 1.0, -1.0, -1.0, 0.0, 1.0, 0.0, -1.0];
	private static $grad2X = [1.0, -1.0, 1.0, -1.0, 1.0, -1.0, 1.0, -1.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, -1.0, 0.0];
	private static $grad2Z = [0.0, 0.0, 0.0, 0.0, 1.0, 1.0, -1.0, -1.0, 1.0, 1.0, -1.0, -1.0, 0.0, 1.0, 0.0, -1.0];

	/** @var int[] */
	private $permutations = [];
	/** @var float */
	public $xCoord;
	/** @var float */
	public $yCoord;
	/** @var float */
	public $zCoord;

	public function __construct(JavaRandom $rnd){
		$this->xCoord = $rnd->nextDouble() * 256.0;
		$this->yCoord = $rnd->nextDouble() * 256.0;
		$this->zCoord = $rnd->nextDouble() * 256.0;

		for($i = 0; $i < 256; $i++){
			$this->permutations[$i] = $i;
		}

		for($l = 0; $l < 256; $l++){
			$j = $rnd->nextBoundedInt(256 - $l) + $l;
			$k = $this->permutations[$l];
			$this->permutations[$l] = $this->permutations[$j];
			$this->permutations[$j] = $k;

			$this->permutations[$l + 256] = $this->permutations[$l];
		}
	}

	private static function lerp($t, $a, $b){
		return $a + $t * ($b - $a);
	}

	private static function grad3D($hash, $x, $y, $z){
		$i = $hash & 15;
		return self::$gradX[$i] * $x + self::$gradY[$i] * $y + self::$gradZ[$i] * $z;
	}

	private static function grad2D($hash, $x, $z){
		$i = $hash & 15;
		return self::$grad2X[$i] * $x + self::$grad2Z[$i] * $z;
	}

	public function populateNoiseArray(array &$noiseArray, $xOffset, $yOffset, $zOffset, $xSize, $ySize, $zSize, $xScale, $yScale, $zScale, $noiseScale){
		if($ySize === 1){
			$invScale = 1.0 / $noiseScale;
			$idx = 0;

			for($j2 = 0; $j2 < $xSize; $j2++){
				$d17 = $xOffset + $j2 * $xScale + $this->xCoord;
				$i6 = (int) floor($d17);
				if($d17 < $i6){
					$i6--;
				}
				$k2 = $i6 & 255;
				$d17 = $d17 - $i6;
				$d18 = $d17 * $d17 * $d17 * ($d17 * ($d17 * 6.0 - 15.0) + 10.0);

				for($j6 = 0; $j6 < $zSize; $j6++){
					$d19 = $zOffset + $j6 * $zScale + $this->zCoord;
					$k6 = (int) floor($d19);
					if($d19 < $k6){
						$k6--;
					}
					$l6 = $k6 & 255;
					$d19 = $d19 - $k6;
					$d20 = $d19 * $d19 * $d19 * ($d19 * ($d19 * 6.0 - 15.0) + 10.0);

					$i5 = $this->permutations[$k2] + 0;
					$j5 = $this->permutations[$i5] + $l6;
					$j = $this->permutations[$k2 + 1] + 0;
					$k5 = $this->permutations[$j] + $l6;

					$d14 = self::lerp($d18, self::grad2D($this->permutations[$j5], $d17, $d19), self::grad3D($this->permutations[$k5], $d17 - 1.0, 0.0, $d19));
					$d15 = self::lerp($d18, self::grad3D($this->permutations[$j5 + 1], $d17, 0.0, $d19 - 1.0), self::grad3D($this->permutations[$k5 + 1], $d17 - 1.0, 0.0, $d19 - 1.0));
					$d21 = self::lerp($d20, $d14, $d15);
					$noiseArray[$idx] += $d21 * $invScale;
					$idx++;
				}
			}
			return;
		}

		$invScale = 1.0 / $noiseScale;
		$k = -1;

		$d1 = 0.0; $d2 = 0.0; $d3 = 0.0; $d4 = 0.0;

		$idx = 0;

		for($l2 = 0; $l2 < $xSize; $l2++){
			$d5 = $xOffset + $l2 * $xScale + $this->xCoord;
			$i3 = (int) floor($d5);
			if($d5 < $i3){
				$i3--;
			}
			$j3 = $i3 & 255;
			$d5 = $d5 - $i3;
			$d6 = $d5 * $d5 * $d5 * ($d5 * ($d5 * 6.0 - 15.0) + 10.0);

			for($k3 = 0; $k3 < $zSize; $k3++){
				$d7 = $zOffset + $k3 * $zScale + $this->zCoord;
				$l3 = (int) floor($d7);
				if($d7 < $l3){
					$l3--;
				}
				$i4 = $l3 & 255;
				$d7 = $d7 - $l3;
				$d8 = $d7 * $d7 * $d7 * ($d7 * ($d7 * 6.0 - 15.0) + 10.0);

				for($j4 = 0; $j4 < $ySize; $j4++){
					$d9 = $yOffset + $j4 * $yScale + $this->yCoord;
					$k4 = (int) floor($d9);
					if($d9 < $k4){
						$k4--;
					}
					$l4 = $k4 & 255;
					$d9 = $d9 - $k4;
					$d10 = $d9 * $d9 * $d9 * ($d9 * ($d9 * 6.0 - 15.0) + 10.0);

					if($j4 === 0 || $l4 !== $k){
						$k = $l4;
						$l = $this->permutations[$j3] + $l4;
						$i1 = $this->permutations[$l] + $i4;
						$j1 = $this->permutations[$l + 1] + $i4;
						$k1 = $this->permutations[$j3 + 1] + $l4;
						$l1 = $this->permutations[$k1] + $i4;
						$i2 = $this->permutations[$k1 + 1] + $i4;

						$g1 = self::grad3D($this->permutations[$i1], $d5, $d9, $d7);
						$g2 = self::grad3D($this->permutations[$l1], $d5 - 1.0, $d9, $d7);
						$d1 = self::lerp($d6, $g1, $g2);

						$g3 = self::grad3D($this->permutations[$j1], $d5, $d9 - 1.0, $d7);
						$g4 = self::grad3D($this->permutations[$i2], $d5 - 1.0, $d9 - 1.0, $d7);
						$d2 = self::lerp($d6, $g3, $g4);

						$g5 = self::grad3D($this->permutations[$i1 + 1], $d5, $d9, $d7 - 1.0);
						$g6 = self::grad3D($this->permutations[$l1 + 1], $d5 - 1.0, $d9, $d7 - 1.0);
						$d3 = self::lerp($d6, $g5, $g6);

						$g7 = self::grad3D($this->permutations[$j1 + 1], $d5, $d9 - 1.0, $d7 - 1.0);
						$g8 = self::grad3D($this->permutations[$i2 + 1], $d5 - 1.0, $d9 - 1.0, $d7 - 1.0);
						$d4 = self::lerp($d6, $g7, $g8);
					}

					$d11 = self::lerp($d10, $d1, $d2);
					$d12 = self::lerp($d10, $d3, $d4);
					$d13 = self::lerp($d8, $d11, $d12);

					$noiseArray[$idx] += $d13 * $invScale;
					$idx++;
				}
			}
		}
	}
}
