<?php

namespace pocketmine\level\generator\gorigional\structure;

use pocketmine\level\generator\gorigional\JavaRandom;
use pocketmine\level\generator\gorigional\biome\BiomeRegistry;

/**
 * 移植自 Java 1.7.10 MapGenStronghold。
 */
class MapGenStronghold extends MapGenStructure{
	/** @var int[] */
	private $allowedBiomeIDs = [];
	/** @var int[][]|null */
	private $positions = null;
	/** @var float */
	private $distance = 32.0;
	/** @var int */
	private $spread = 3;

	public function __construct($seed){
		parent::__construct($seed);

		foreach(BiomeRegistry::getAllBiomes() as $id => $biome){
			if($biome->getMinElevation() > 0.0){
				$this->allowedBiomeIDs[] = $id;
			}
		}
	}

	protected function canSpawnStructureAtCoords($chunkX, $chunkZ){
		if($this->positions === null){
			$this->generatePositions();
		}

		foreach($this->positions as $p){
			if($p[0] === $chunkX && $p[1] === $chunkZ){
				return true;
			}
		}
		return false;
	}

	private function generatePositions(){
		$this->positions = [];

		$random = new JavaRandom($this->world->getSeed());
		$d0 = $random->nextDouble() * M_PI * 2.0;
		$k = 1;
		$spread = $this->spread;
		$count = 3;

		for($l = 0; $l < $count; $l++){
			$d1 = (1.25 * $k + $random->nextDouble()) * $this->distance * $k;
			$i1 = (int) round(cos($d0) * $d1);
			$j1 = (int) round(sin($d0) * $d1);

			$pos = $this->world->findBiomePosition(($i1 << 4) + 8, ($j1 << 4) + 8, 112, $this->allowedBiomeIDs, $random);
			if($pos !== null){
				$i1 = $pos[0] >> 4;
				$j1 = $pos[2] >> 4;
			}

			$this->positions[] = [$i1, $j1];
			$d0 += 6.283185307179586 * $k / $spread;

			if($l === $spread){
				$k += 2 + $random->nextBoundedInt(5);
				$spread += 1 + $random->nextBoundedInt(2);
			}
		}
	}

	protected function getStructureStart($chunkX, $chunkZ){
		return new StrongholdStart($this->world, $this->rand, $chunkX, $chunkZ);
	}
}
