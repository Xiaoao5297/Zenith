<?php

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
		$this->rand->setSeed($this->worldSeed);
		$r1 = $this->rand->nextLong();
		$r2 = $this->rand->nextLong();

		for($x = $chunkX - $this->range; $x <= $chunkX + $this->range; $x++){
			for($z = $chunkZ - $this->range; $z <= $chunkZ + $this->range; $z++){
				$rX = Int64::mul($x, $r1);
				$rZ = Int64::mul($z, $r2);
				$seed = $rX ^ $rZ ^ $this->worldSeed;
				$this->rand->setSeed($seed);

				$this->recursiveGenerate($chunkX, $chunkZ, $x, $z, $chunk);
			}
		}
	}

	abstract public function recursiveGenerate($chunkX, $chunkZ, $originX, $originZ, FullChunk $chunk);
}
