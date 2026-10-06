<?php

namespace pocketmine\level\generator\gorigional\structure;

use pocketmine\level\generator\gorigional\JavaRandom;

/**
 * 移植自 Java 1.7.10 MapGenStronghold$Start。
 */
class StrongholdStart extends StructureStart{

	public function __construct(World $world, JavaRandom $rand, $chunkX, $chunkZ){
		parent::__construct($chunkX, $chunkZ);

		$stairs = new StrongholdStairs2($rand, ($chunkX << 4) + 2, ($chunkZ << 4) + 2);
		$this->components[] = $stairs;
		$stairs->buildComponent($stairs, $this->components, $rand);

		$this->updateBoundingBox();
		$this->placeAtHeight($world, $rand, 10);
	}
}
