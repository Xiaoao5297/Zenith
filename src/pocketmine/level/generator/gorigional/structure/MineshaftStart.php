<?php

namespace pocketmine\level\generator\gorigional\structure;

use pocketmine\level\generator\gorigional\JavaRandom;

/**
 * 移植自 Java 1.7.10 StructureMineshaftStart。
 */
class MineshaftStart extends StructureStart{

	public function __construct(World $world, JavaRandom $rand, $chunkX, $chunkZ){
		parent::__construct($chunkX, $chunkZ);

		$room = new MineshaftRoom(($chunkX << 4) + 2, ($chunkZ << 4) + 2);
		$this->components[] = $room;
		$room->buildComponent(null, $this->components, $rand);

		$this->updateBoundingBox();
		$this->placeAtHeight($world, $rand, 10);
	}
}
