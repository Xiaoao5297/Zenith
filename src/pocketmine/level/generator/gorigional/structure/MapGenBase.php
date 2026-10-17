<?php

namespace pocketmine\level\generator\gorigional\structure;

use pocketmine\level\format\FullChunk;
use pocketmine\level\generator\gorigional\JavaRandom;
use pocketmine\level\generator\gorigional\McpeRandom;

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
		 * MCPE LargeFeature::apply：
		 * - 用世界种子初始化 MT，取两个随机数得到两个奇数乘子；
		 * - 每个候选区块用 worldSeed ^ (x * i + z * j) 作为种子；
		 * - 所有乘法和种子都按 32 位无符号截断。
		 */
		$seedRnd = new McpeRandom($this->worldSeed);
		$i = (($seedRnd->nextInt() >> 2) * 2 + 1) & 0xFFFFFFFF;
		$j = (($seedRnd->nextInt() >> 2) * 2 + 1) & 0xFFFFFFFF;

		for($x = $chunkX - $this->range; $x <= $chunkX + $this->range; $x++){
			for($z = $chunkZ - $this->range; $z <= $chunkZ + $this->range; $z++){
				$seed = ($this->worldSeed ^ ((($x * $i) + ($z * $j)) & 0xFFFFFFFF)) & 0xFFFFFFFF;
				$this->rand->setSeed($seed);

				$this->recursiveGenerate($chunkX, $chunkZ, $x, $z, $chunk);
			}
		}
	}

	abstract public function recursiveGenerate($chunkX, $chunkZ, $originX, $originZ, FullChunk $chunk);
}
