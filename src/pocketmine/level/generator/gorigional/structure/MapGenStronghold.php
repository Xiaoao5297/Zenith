<?php

namespace pocketmine\level\generator\gorigional\structure;

use pocketmine\level\generator\gorigional\JavaRandom;

/**
 * 移植自 SCAXE-GO-CE structure.MapGenStronghold。
 */
class MapGenStronghold{
	/** @var int[][] */
	private $structureCoords = [];
	/** @var int */
	private $worldSeed;
	/** @var JavaRandom */
	private $ran;
	/** @var float */
	private $distance = 32.0;
	/** @var int */
	private $spread = 3;
	/** @var StructureStart[] */
	private $structureMap = [];

	public function __construct($seed){
		$this->worldSeed = $seed;
		$this->ran = new JavaRandom($seed);
		$this->generatePositions();
	}

	private function generatePositions(){
		$this->ran->setSeed($this->worldSeed);

		$generated = 0;
		$ring = 0;

		while($generated < 128){
			$d1 = (4.0 * $this->distance + $this->distance * $ring * 6.0) + ($this->ran->nextDouble() - 0.5) * $this->distance * 2.5;

			if($ring === 0){
				$countInRing = $this->spread;
			}else{
				$countInRing = $ring * $this->spread + $this->spread;
			}

			if($generated + $countInRing > 128){
				$countInRing = 128 - $generated;
			}

			$d2 = $this->ran->nextDouble() * M_PI * 2.0;
			$d3 = M_PI * 2.0 / $countInRing;

			for($l = 0; $l < $countInRing; $l++){
				$this->ran->nextDouble();
				$angle = $d2 + $l * $d3;

				$cx = (int) round(cos($angle) * $d1);
				$cz = (int) round(sin($angle) * $d1);

				$this->structureCoords[] = [$cx, $cz];
				$generated++;
			}
			$ring++;
		}
	}

	public function canSpawnStructureAtCoords($chunkX, $chunkZ){
		foreach($this->structureCoords as $pos){
			if($pos[0] === $chunkX && $pos[1] === $chunkZ){
				return true;
			}
		}
		return false;
	}

	public function getStructureStart($chunkX, $chunkZ){
		$key = $chunkX . ':' . $chunkZ;

		if(isset($this->structureMap[$key])){
			return $this->structureMap[$key];
		}

		if($this->canSpawnStructureAtCoords($chunkX, $chunkZ)){
			$start = new StrongholdStart($this->worldSeed, $this->ran, $chunkX, $chunkZ);
			$this->structureMap[$key] = $start;
			return $start;
		}
		return null;
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
						$start->generateStructure($w, $this->ran, $chunkBox);
						$success = true;
					}
				}
			}
		}
		return $success;
	}

	/**
	 * @return int[][]
	 */
	public function getStructureCoords(){
		return $this->structureCoords;
	}
}
