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
use pocketmine\level\generator\gorigional\Int64;
use pocketmine\level\generator\gorigional\JavaRandom;

/**
 * 移植自 SCAXE-GO-CE pkg/level/generator/gorigional/structure/map_gen_base.go。
 */
abstract class MapGenBase{
	/** @var int */
	public $range = 8;
	/** @var JavaRandom */
	protected $rand;
	/** @var int */
	protected $worldSeed;

	public function __construct($seed){
		$this->rand = new JavaRandom($seed);
		$this->worldSeed = $seed;
	}

	public function generate($chunkX, $chunkZ, FullChunk $chunk){
		/*
		 * Java 1.7.10 MapGenBase.func_151539_a：
		 * 用世界种子初始化，取两次 nextLong 作为两个乘子；
		 * 每个候选原点用 (x * l) ^ (z * i1) ^ worldSeed 重新播种。
		 */
		$this->rand->setSeed($this->worldSeed);
		$l = $this->rand->nextLong();
		$i1 = $this->rand->nextLong();

		for($x = $chunkX - $this->range; $x <= $chunkX + $this->range; $x++){
			for($z = $chunkZ - $this->range; $z <= $chunkZ + $this->range; $z++){
				$seed = Int64::mul($x, $l) ^ Int64::mul($z, $i1) ^ $this->worldSeed;
				$this->rand->setSeed($seed);

				$this->recursiveGenerate($chunkX, $chunkZ, $x, $z, $chunk);
			}
		}
	}

	abstract public function recursiveGenerate($chunkX, $chunkZ, $originX, $originZ, FullChunk $chunk);
}
