<?php

namespace pocketmine\level\generator\gorigional;

/**
 * 64 位整数环绕运算辅助。
 * PHP 整数相乘溢出会变成 float，因此这里用 16 位分肢手动实现 mod 2^64 的乘法与加法，
 * 以对应 Go int64 的环绕语义。移植自 SCAXE-GO-CE 的 int64 运算。
 */
final class Int64{
	const MASK32 = 0xFFFFFFFF;
	const MASK16 = 0xFFFF;

	public static function mul($a, $b){
		$a0 = $a & self::MASK16; $a1 = ($a >> 16) & self::MASK16; $a2 = ($a >> 32) & self::MASK16; $a3 = ($a >> 48) & self::MASK16;
		$b0 = $b & self::MASK16; $b1 = ($b >> 16) & self::MASK16; $b2 = ($b >> 32) & self::MASK16; $b3 = ($b >> 48) & self::MASK16;

		$r0 = $a0 * $b0;
		$r1 = $a0 * $b1 + $a1 * $b0;
		$r2 = $a0 * $b2 + $a1 * $b1 + $a2 * $b0;
		$r3 = $a0 * $b3 + $a1 * $b2 + $a2 * $b1 + $a3 * $b0;

		$c = $r0 >> 16; $r0 &= self::MASK16;
		$r1 += $c; $c = $r1 >> 16; $r1 &= self::MASK16;
		$r2 += $c; $c = $r2 >> 16; $r2 &= self::MASK16;
		$r3 += $c; $r3 &= self::MASK16;

		return $r0 | ($r1 << 16) | ($r2 << 32) | ($r3 << 48);
	}

	public static function add($a, $b){
		$lo = ($a & self::MASK32) + ($b & self::MASK32);
		$hi = (($a >> 32) & self::MASK32) + (($b >> 32) & self::MASK32) + ($lo >> 32);
		return (($hi & self::MASK32) << 32) | ($lo & self::MASK32);
	}

	public static function multiplyAdd($a, $mul, $add){
		return self::add(self::mul($a, $mul), $add);
	}
}
