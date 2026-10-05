<?php

namespace pocketmine\level\generator\gorigional\structure;

use pocketmine\level\generator\gorigional\Int64;
use pocketmine\level\generator\gorigional\JavaRandom;

/**
 * 移植自 SCAXE-GO-CE structure.NewMineshaftStart。
 */
class MineshaftStart extends StructureStart{

	public function __construct($worldSeed, $chunkX, $chunkZ){
		parent::__construct($chunkX, $chunkZ);

		$seed = Int64::add(Int64::add(Int64::mul($chunkX, 341873128712), Int64::mul($chunkZ, 132897987541)), $worldSeed);
		$rnd = new JavaRandom($seed);

		$room = new MineshaftRoom(($chunkX << 4) + 2, ($chunkZ << 4) + 2);
		$this->components[] = $room;

		$room->buildComponent(null, $this->components, $rnd);
		$this->updateBoundingBox();
	}
}
