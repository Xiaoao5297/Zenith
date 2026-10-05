<?php

namespace pocketmine\level\generator\gorigional\structure;

use pocketmine\level\generator\gorigional\Int64;
use pocketmine\level\generator\gorigional\JavaRandom;

/**
 * 移植自 SCAXE-GO-CE structure.MapGenScatteredFeature。
 */
class MapGenScatteredFeature{
	/** @var int */
	private $worldSeed;
	/** @var int */
	private $maxDistanceBetweenScatteredFeatures = 32;
	/** @var int */
	private $minDistanceBetweenScatteredFeatures = 8;
	/** @var JavaRandom */
	private $rand;
	/** @var BiomeSource|null */
	private $biomeSource;
	/** @var StructureStart[] */
	private $structureMap = [];

	public function __construct($seed, BiomeSource $biomeSource = null){
		$this->worldSeed = $seed;
		$this->rand = new JavaRandom($seed);
		$this->biomeSource = $biomeSource;
	}

	public function canSpawnStructureAtCoords($chunkX, $chunkZ){
		$i = $chunkX;
		$j = $chunkZ;

		if($chunkX < 0){
			$chunkX -= $this->maxDistanceBetweenScatteredFeatures - 1;
		}
		if($chunkZ < 0){
			$chunkZ -= $this->maxDistanceBetweenScatteredFeatures - 1;
		}

		$k = intdiv($chunkX, $this->maxDistanceBetweenScatteredFeatures);
		$l = intdiv($chunkZ, $this->maxDistanceBetweenScatteredFeatures);

		$seed = Int64::add(Int64::add(Int64::add(Int64::mul($k, 341873128712), Int64::mul($l, 132897987541)), $this->worldSeed), 14357617);
		$this->rand->setSeed($seed);

		$k *= $this->maxDistanceBetweenScatteredFeatures;
		$l *= $this->maxDistanceBetweenScatteredFeatures;

		$k += $this->rand->nextBoundedInt($this->maxDistanceBetweenScatteredFeatures - 8);
		$l += $this->rand->nextBoundedInt($this->maxDistanceBetweenScatteredFeatures - 8);

		if($i === $k && $j === $l){
			if($this->biomeSource !== null){
				$bID = $this->biomeSource->getBiome($i * 16 + 8, $j * 16 + 8);
				return $this->isStructureBiome($bID);
			}
			return true;
		}

		return false;
	}

	private function isStructureBiome($id){
		switch($id){
			case 2:
			case 17:
			case 21:
			case 22:
			case 6:
			case 12:
			case 30:
				return true;
		}
		return false;
	}

	public function getStructureStart($chunkX, $chunkZ){
		$key = $chunkX . ':' . $chunkZ;

		if(isset($this->structureMap[$key])){
			return $this->structureMap[$key];
		}

		if($this->canSpawnStructureAtCoords($chunkX, $chunkZ)){
			$biomeID = 1;
			if($this->biomeSource !== null){
				$biomeID = $this->biomeSource->getBiome($chunkX * 16 + 8, $chunkZ * 16 + 8);
			}

			$start = new ScatteredFeatureStart($this->worldSeed, $this->rand, $chunkX, $chunkZ, $biomeID);
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
		$rangeVal = 8;

		for($i = $chunkX - $rangeVal; $i <= $chunkX + $rangeVal; $i++){
			for($j = $chunkZ - $rangeVal; $j <= $chunkZ + $rangeVal; $j++){
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
