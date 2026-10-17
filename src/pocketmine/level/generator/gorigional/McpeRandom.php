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


namespace pocketmine\level\generator\gorigional;

/**
 * MCPE 0.14.3 使用的 MT19937 随机数。
 *
 * 与 Java 的 48 位 LCG 不同，MCPE 的地形噪声、湖泊、地牢、结构都使用
 * 标准 MT19937。其 nextInt(bound) 是直接取模，没有 Java 的 rejection 逻辑；
 * nextFloat/nextDouble 是 genrand_int32() * 2^-32。
 *
 * 注意：本项目只要求地形观感接近 MCPE 0.14.3，不要求同一 seed
 * 与 MCPE 逐方块 1:1 对齐，因此允许后续在消费顺序上做局部调整。
 */
class McpeRandom extends JavaRandom{
	const N = 624;
	const M = 397;
	const MATRIX_A = 0x9908B0DF;
	const UPPER_MASK = 0x80000000;
	const LOWER_MASK = 0x7FFFFFFF;

	/** @var int[] */
	private $mt = [];
	/** @var int */
	private $mti = self::N;
	/** @var int */
	private $seed = 0;

	public function __construct($seed = null){
		if($seed === null){
			$seed = (int) (microtime(true) * 1000);
		}
		$this->setSeed($seed);
	}

	public function setSeed($seed){
		$this->seed = $seed & 0xFFFFFFFF;
		$this->mt[0] = $this->seed;

		for($i = 1; $i < self::N; $i++){
			$prev = $this->mt[$i - 1];
			$this->mt[$i] = (1812433253 * ($prev ^ ($prev >> 30)) + $i) & 0xFFFFFFFF;
		}

		$this->mti = self::N;
	}

	public function getSeed(){
		return $this->seed;
	}

	private function twist(){
		for($i = 0; $i < self::N; $i++){
			$y = ($this->mt[$i] & self::UPPER_MASK) | ($this->mt[($i + 1) % self::N] & self::LOWER_MASK);
			$this->mt[$i] = $this->mt[($i + self::M) % self::N] ^ ($y >> 1) ^ (($y & 1) !== 0 ? self::MATRIX_A : 0);
		}
		$this->mti = 0;
	}

	/**
	 * 返回 0..2^32-1 的 32 位无符号整数。
	 */
	public function nextInt(){
		if($this->mti >= self::N){
			$this->twist();
		}

		$y = $this->mt[$this->mti++];
		$y ^= ($y >> 11);
		$y ^= ($y << 7) & 0x9D2C5680;
		$y ^= ($y << 15) & 0xEFC60000;
		$y ^= ($y >> 18);

		return $y & 0xFFFFFFFF;
	}

	public function nextBoundedInt($n){
		if($n <= 0){
			return 0;
		}
		return $this->nextInt() % $n;
	}

	public function nextDouble(){
		return $this->nextInt() * 2.3283064365386963E-10;
	}

	public function nextFloat(){
		return $this->nextDouble();
	}

	public function nextBoolean(){
		return ($this->nextInt() & 1) !== 0;
	}

	/**
	 * 供仍按 JavaRandom 类型声明的装饰代码使用。
	 * 两个 32 位 MT 输出拼成有符号 64 位值。
	 */
	public function nextLong(){
		$hi = $this->nextInt();
		$lo = $this->nextInt();
		if($hi >= 0x80000000){
			$hi -= 0x100000000;
		}
		if($lo >= 0x80000000){
			$lo -= 0x100000000;
		}
		return Int64::add(Int64::mul($hi, 0x100000000), $lo);
	}
}
