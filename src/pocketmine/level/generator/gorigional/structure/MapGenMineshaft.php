<?php

namespace pocketmine\level\generator\gorigional\structure;

/**
 * 移植自 Java 1.7.10 MapGenMineshaft。
 */
class MapGenMineshaft extends MapGenStructure{
	/** @var float */
	private $chance = 0.004;

	protected function canSpawnStructureAtCoords($chunkX, $chunkZ){
		return $this->rand->nextDouble() < $this->chance
			&& $this->rand->nextBoundedInt(80) < max(abs($chunkX), abs($chunkZ));
	}

	protected function getStructureStart($chunkX, $chunkZ){
		return new MineshaftStart($this->world, $this->rand, $chunkX, $chunkZ);
	}
}
