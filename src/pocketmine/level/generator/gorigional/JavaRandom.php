<?php

namespace pocketmine\level\generator\gorigional;

/**
 * java.util.Random 的 PHP 实现（48 位 LCG）。
 * 移植自 SCAXE-GO-CE pkg/math/rand/random.go。
 */
class JavaRandom{
	const MULTIPLIER = 0x5DEECE66D;
	const ADDEND = 0xB;
	const MASK48 = 0xFFFFFFFFFFFF;

	/** @var int */
	private $seed;

	public function __construct($seed = null){
		if($seed === null){
			$seed = (int) (microtime(true) * 1000);
		}
		$this->setSeed($seed);
	}

	public function setSeed($seed){
		$this->seed = ($seed ^ self::MULTIPLIER) & self::MASK48;
	}

	private function next($bits){
		$this->seed = Int64::add(Int64::mul($this->seed, self::MULTIPLIER), self::ADDEND) & self::MASK48;
		return $this->seed >> (48 - $bits);
	}

	public function nextInt(){
		return $this->next(32);
	}

	public function nextBoundedInt($n){
		if($n <= 0){
			return 0;
		}

		if(($n & -$n) === $n){
			return ($n * $this->next(31)) >> 31;
		}

		do{
			$bits = $this->next(31);
			$val = $bits % $n;
		}while($bits - $val + ($n - 1) < 0);

		return $val;
	}

	public function nextFloat(){
		return $this->next(24) / 16777216.0;
	}

	public function nextDouble(){
		return ($this->next(26) * 134217728.0 + $this->next(27)) / 9007199254740992.0;
	}

	public function nextBoolean(){
		return $this->next(1) !== 0;
	}

	public function nextLong(){
		$hi = $this->next(32);
		$lo = $this->next(32);
		if($hi >= 0x80000000){
			$hi -= 0x100000000;
		}
		if($lo >= 0x80000000){
			$lo -= 0x100000000;
		}
		return Int64::add(Int64::mul($hi, 0x100000000), $lo);
	}

	public function nextRange($start, $end){
		return $start + $this->nextBoundedInt($end - $start + 1);
	}
}
