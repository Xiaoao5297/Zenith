<?php

namespace pocketmine\level\generator\gorigional\noise;

use pocketmine\level\generator\gorigional\JavaRandom;

/**
 * 移植自 SCAXE-GO-CE pkg/level/generator/gorigional/noise/simplex.go。
 */
class SimplexNoise{
	private static $grad3 = [
		[1, 1, 0], [-1, 1, 0], [1, -1, 0], [-1, -1, 0],
		[1, 0, 1], [-1, 0, 1], [1, 0, -1], [-1, 0, -1],
		[0, 1, 1], [0, -1, 1], [0, 1, -1], [0, -1, -1]
	];

	private static $sqrt3;
	private static $f2;
	private static $g2;

	/** @var float */
	private $xo;
	/** @var float */
	private $yo;
	/** @var float */
	private $zo;
	/** @var int[] */
	private $p = [];

	public function __construct(JavaRandom $r){
		if(self::$sqrt3 === null){
			self::$sqrt3 = sqrt(3.0);
			self::$f2 = 0.5 * (self::$sqrt3 - 1.0);
			self::$g2 = (3.0 - self::$sqrt3) / 6.0;
		}

		$this->xo = $r->nextDouble() * 256.0;
		$this->yo = $r->nextDouble() * 256.0;
		$this->zo = $r->nextDouble() * 256.0;

		for($i = 0; $i < 256; $i++){
			$this->p[$i] = $i;
		}

		for($i = 0; $i < 256; $i++){
			$j = $r->nextBoundedInt(256 - $i) + $i;
			$k = $this->p[$i];
			$this->p[$i] = $this->p[$j];
			$this->p[$j] = $k;
			$this->p[$i + 256] = $this->p[$i];
		}
	}

	private static function fastFloor($x){
		if($x > 0){
			return (int) $x;
		}
		return (int) $x - 1;
	}

	private static function dot(array $g, $x, $y){
		return $g[0] * $x + $g[1] * $y;
	}

	public function add(array &$out, $x, $z, $width, $height, $scaleX, $scaleZ, $scaleMod){
		$idx = 0;
		for($i = 0; $i < $height; $i++){
			$d0 = ($z + $i) * $scaleZ + $this->yo;
			for($j = 0; $j < $width; $j++){
				$d1 = ($x + $j) * $scaleX + $this->xo;

				$d5 = ($d1 + $d0) * self::$f2;
				$l = self::fastFloor($d1 + $d5);
				$i1 = self::fastFloor($d0 + $d5);
				$d6 = ($l + $i1) * self::$g2;
				$d7 = $l - $d6;
				$d8 = $i1 - $d6;
				$d9 = $d1 - $d7;
				$d10 = $d0 - $d8;

				$j1 = 0;
				$k1 = 1;
				if($d9 > $d10){
					$j1 = 1;
					$k1 = 0;
				}

				$d11 = $d9 - $j1 + self::$g2;
				$d12 = $d10 - $k1 + self::$g2;
				$d13 = $d9 - 1.0 + 2.0 * self::$g2;
				$d14 = $d10 - 1.0 + 2.0 * self::$g2;

				$l1 = $l & 255;
				$i2 = $i1 & 255;

				$j2 = $this->p[$l1 + $this->p[$i2]] % 12;
				$k2 = $this->p[$l1 + $j1 + $this->p[$i2 + $k1]] % 12;
				$l2 = $this->p[$l1 + 1 + $this->p[$i2 + 1]] % 12;

				$d15 = 0.5 - $d9 * $d9 - $d10 * $d10;
				$d2 = 0.0;
				if($d15 >= 0){
					$d15 *= $d15;
					$d2 = $d15 * $d15 * self::dot(self::$grad3[$j2], $d9, $d10);
				}

				$d16 = 0.5 - $d11 * $d11 - $d12 * $d12;
				$d3 = 0.0;
				if($d16 >= 0){
					$d16 *= $d16;
					$d3 = $d16 * $d16 * self::dot(self::$grad3[$k2], $d11, $d12);
				}

				$d17 = 0.5 - $d13 * $d13 - $d14 * $d14;
				$d4 = 0.0;
				if($d17 >= 0){
					$d17 *= $d17;
					$d4 = $d17 * $d17 * self::dot(self::$grad3[$l2], $d13, $d14);
				}

				$out[$idx] += 70.0 * ($d2 + $d3 + $d4) * $scaleMod;
				$idx++;
			}
		}
	}

	public function getValue($pX, $pZ){
		$d3 = 0.5 * (self::$sqrt3 - 1.0);
		$d4 = ($pX + $pZ) * $d3;
		$i = self::fastFloor($pX + $d4);
		$j = self::fastFloor($pZ + $d4);
		$d5 = (3.0 - self::$sqrt3) / 6.0;
		$d6 = ($i + $j) * $d5;
		$d7 = $i - $d6;
		$d8 = $j - $d6;
		$d9 = $pX - $d7;
		$d10 = $pZ - $d8;

		$k = 0;
		$l = 0;
		if($d9 > $d10){
			$k = 1;
			$l = 0;
		}else{
			$k = 0;
			$l = 1;
		}

		$d11 = $d9 - $k + $d5;
		$d12 = $d10 - $l + $d5;
		$d13 = $d9 - 1.0 + 2.0 * $d5;
		$d14 = $d10 - 1.0 + 2.0 * $d5;

		$i1 = $i & 255;
		$j1 = $j & 255;
		$k1 = $this->p[$i1 + $this->p[$j1]] % 12;
		$l1 = $this->p[$i1 + $k + $this->p[$j1 + $l]] % 12;
		$i2 = $this->p[$i1 + 1 + $this->p[$j1 + 1]] % 12;

		$d15 = 0.5 - $d9 * $d9 - $d10 * $d10;
		$d0 = 0.0;
		if($d15 >= 0){
			$d15 *= $d15;
			$d0 = $d15 * $d15 * self::dot(self::$grad3[$k1], $d9, $d10);
		}

		$d16 = 0.5 - $d11 * $d11 - $d12 * $d12;
		$d1 = 0.0;
		if($d16 >= 0){
			$d16 *= $d16;
			$d1 = $d16 * $d16 * self::dot(self::$grad3[$l1], $d11, $d12);
		}

		$d17 = 0.5 - $d13 * $d13 - $d14 * $d14;
		$d2 = 0.0;
		if($d17 >= 0){
			$d17 *= $d17;
			$d2 = $d17 * $d17 * self::dot(self::$grad3[$i2], $d13, $d14);
		}

		return 70.0 * ($d0 + $d1 + $d2);
	}
}
