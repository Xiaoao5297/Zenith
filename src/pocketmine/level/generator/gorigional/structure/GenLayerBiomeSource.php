<?php

namespace pocketmine\level\generator\gorigional\structure;

use pocketmine\level\generator\gorigional\layer\GenLayer;

/**
 * 移植自 SCAXE-GO-CE gorigional.genLayerAdapter。
 */
class GenLayerBiomeSource implements BiomeSource{
	/** @var GenLayer */
	private $layer;

	public function __construct(GenLayer $layer){
		$this->layer = $layer;
	}

	public function getBiome($x, $z){
		$ints = $this->layer->getInts($x, $z, 1, 1);
		return $ints[0] & 0xFF;
	}
}
