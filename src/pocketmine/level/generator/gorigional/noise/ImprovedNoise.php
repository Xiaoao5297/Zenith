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

	public function populateNoiseArray(array &$noiseArray, $xOffset, $yOffset, $zOffset, $xSize, $ySize, $zSize, $xScale, $yScale, $zScale, $noiseScale){
		$p = $this->permutations;
		$xc = $this->xCoord;
		$yc = $this->yCoord;
		$zc = $this->zCoord;

		if($ySize === 1){
			$g2X = self::$grad2X;
			$g2Z = self::$grad2Z;
			$gX = self::$gradX;
			$gZ = self::$gradZ;

			$invScale = 1.0 / $noiseScale;
			$idx = 0;

			for($j2 = 0; $j2 < $xSize; $j2++){
				$d17 = $xOffset + $j2 * $xScale + $xc;
				$i6 = (int) floor($d17);
				if($d17 < $i6){
					$i6--;
				}
				$k2 = $i6 & 255;
				$d17 = $d17 - $i6;
				$d18 = $d17 * $d17 * $d17 * ($d17 * ($d17 * 6.0 - 15.0) + 10.0);

				for($j6 = 0; $j6 < $zSize; $j6++){
					$d19 = $zOffset + $j6 * $zScale + $zc;
					$k6 = (int) floor($d19);
					if($d19 < $k6){
						$k6--;
					}
					$l6 = $k6 & 255;
					$d19 = $d19 - $k6;
					$d20 = $d19 * $d19 * $d19 * ($d19 * ($d19 * 6.0 - 15.0) + 10.0);

					$i5 = $p[$k2] + 0;
					$j5 = $p[$i5] + $l6;
					$j = $p[$k2 + 1] + 0;
					$k5 = $p[$j] + $l6;

					$h = $p[$j5] & 15;
					$lo = $g2X[$h] * $d17 + $g2Z[$h] * $d19;
					$h = $p[$k5] & 15;
					$hi = $gX[$h] * ($d17 - 1.0) + $gZ[$h] * $d19;
					$d14 = $lo + $d18 * ($hi - $lo);

					$h = $p[$j5 + 1] & 15;
					$lo = $gX[$h] * $d17 + $gZ[$h] * ($d19 - 1.0);
					$h = $p[$k5 + 1] & 15;
					$hi = $gX[$h] * ($d17 - 1.0) + $gZ[$h] * ($d19 - 1.0);
					$d15 = $lo + $d18 * ($hi - $lo);

					$d21 = $d14 + $d20 * ($d15 - $d14);
					$noiseArray[$idx] += $d21 * $invScale;
					$idx++;
				}
			}
			return;
		}

		$gX = self::$gradX;
		$gY = self::$gradY;
		$gZ = self::$gradZ;

		$invScale = 1.0 / $noiseScale;
		$k = -1;

		$d1 = 0.0; $d2 = 0.0; $d3 = 0.0; $d4 = 0.0;

		$idx = 0;

		for($l2 = 0; $l2 < $xSize; $l2++){
			$d5 = $xOffset + $l2 * $xScale + $xc;
			$i3 = (int) floor($d5);
			if($d5 < $i3){
				$i3--;
			}
			$j3 = $i3 & 255;
			$d5 = $d5 - $i3;
			$d6 = $d5 * $d5 * $d5 * ($d5 * ($d5 * 6.0 - 15.0) + 10.0);

			for($k3 = 0; $k3 < $zSize; $k3++){
				$d7 = $zOffset + $k3 * $zScale + $zc;
				$l3 = (int) floor($d7);
				if($d7 < $l3){
					$l3--;
				}
				$i4 = $l3 & 255;
				$d7 = $d7 - $l3;
				$d8 = $d7 * $d7 * $d7 * ($d7 * ($d7 * 6.0 - 15.0) + 10.0);

				for($j4 = 0; $j4 < $ySize; $j4++){
					$d9 = $yOffset + $j4 * $yScale + $yc;
					$k4 = (int) floor($d9);
					if($d9 < $k4){
						$k4--;
					}
					$l4 = $k4 & 255;
					$d9 = $d9 - $k4;
					$d10 = $d9 * $d9 * $d9 * ($d9 * ($d9 * 6.0 - 15.0) + 10.0);

					if($j4 === 0 || $l4 !== $k){
						$k = $l4;
						$l = $p[$j3] + $l4;
						$i1 = $p[$l] + $i4;
						$j1 = $p[$l + 1] + $i4;
						$k1 = $p[$j3 + 1] + $l4;
						$l1 = $p[$k1] + $i4;
						$i2 = $p[$k1 + 1] + $i4;

						$h = $p[$i1] & 15;
						$g1 = $gX[$h] * $d5 + $gY[$h] * $d9 + $gZ[$h] * $d7;
						$h = $p[$l1] & 15;
						$g2 = $gX[$h] * ($d5 - 1.0) + $gY[$h] * $d9 + $gZ[$h] * $d7;
						$d1 = $g1 + $d6 * ($g2 - $g1);

						$h = $p[$j1] & 15;
						$g3 = $gX[$h] * $d5 + $gY[$h] * ($d9 - 1.0) + $gZ[$h] * $d7;
						$h = $p[$i2] & 15;
						$g4 = $gX[$h] * ($d5 - 1.0) + $gY[$h] * ($d9 - 1.0) + $gZ[$h] * $d7;
						$d2 = $g3 + $d6 * ($g4 - $g3);

						$h = $p[$i1 + 1] & 15;
						$g5 = $gX[$h] * $d5 + $gY[$h] * $d9 + $gZ[$h] * ($d7 - 1.0);
						$h = $p[$l1 + 1] & 15;
						$g6 = $gX[$h] * ($d5 - 1.0) + $gY[$h] * $d9 + $gZ[$h] * ($d7 - 1.0);
						$d3 = $g5 + $d6 * ($g6 - $g5);

						$h = $p[$j1 + 1] & 15;
						$g7 = $gX[$h] * $d5 + $gY[$h] * ($d9 - 1.0) + $gZ[$h] * ($d7 - 1.0);
						$h = $p[$i2 + 1] & 15;
						$g8 = $gX[$h] * ($d5 - 1.0) + $gY[$h] * ($d9 - 1.0) + $gZ[$h] * ($d7 - 1.0);
						$d4 = $g7 + $d6 * ($g8 - $g7);
					}

					$d11 = $d1 + $d10 * ($d2 - $d1);
					$d12 = $d3 + $d10 * ($d4 - $d3);
					$d13 = $d11 + $d8 * ($d12 - $d11);

					$noiseArray[$idx] += $d13 * $invScale;
					$idx++;
				}
			}
		}
	}
}
