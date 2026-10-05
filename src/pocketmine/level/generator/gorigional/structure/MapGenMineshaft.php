<?php

namespace pocketmine\level\generator\gorigional\structure;

use pocketmine\level\generator\gorigional\Int64;
use pocketmine\level\generator\gorigional\JavaRandom;

/**
 * 移植自 SCAXE-GO-CE structure.MapGenMineshaft。
 */
class MapGenMineshaft{
	/** @var int */
	private $worldSeed;
	/** @var JavaRandom */
	private $rand;
	/** @var float */
	private $chance = 0.004;

	public function __construct($seed){
		$this->worldSeed = $seed;
		$this->rand = new JavaRandom($seed);
	}

	public function canSpawnStructureAtCoords($chunkX, $chunkZ){
		$seed = Int64::add(Int64::add(Int64::mul($chunkX, 341873128712), Int64::mul($chunkZ, 132897987541)), $this->worldSeed);
		$this->rand->setSeed($seed);
		return $this->rand->nextDouble() < $this->chance;
	}

	public function getStructureStart($chunkX, $chunkZ){
		return new MineshaftStart($this->worldSeed, $chunkX, $chunkZ);
	}

	public function generateStructure(WorldAccess $w, $chunkX, $chunkZ){
		$x = $chunkX * 16;
		$z = $chunkZ * 16;
		$chunkBox = new BoundingBox($x, 0, $z, $x + 15, 255, $z + 15);

		$success = false;

		for($i = $chunkX - 8; $i <= $chunkX + 8; $i++){
			for($j = $chunkZ - 8; $j <= $chunkZ + 8; $j++){
				if($this->canSpawnStructureAtCoords($i, $j)){
					$start = $this->getStructureStart($i, $j);
					if($start !== null && $start->boundingBox !== null && $start->boundingBox->intersectsWith($chunkBox)){
						$start->generateStructure($w, $this->rand, $chunkBox);
						$success = true;
					}
				}
			}
		}
		return $success;
	}
}
