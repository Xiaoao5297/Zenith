<?php

namespace pocketmine\level\generator\gorigional\structure;

use pocketmine\level\generator\gorigional\JavaRandom;
use pocketmine\level\generator\gorigional\layer\GenLayer;

/**
 * 移植自 Java 1.7.10 WorldChunkManager 的生物群系查询部分。
 * getBiome 走 voronoi(1:1) 层；findBiomePosition 走 riverMix(1:4) 层（与 Java 一致）。
 */
class WorldChunkManager{
	/** @var GenLayer */
	private $riverLayer;
	/** @var GenLayer */
	private $voronoiLayer;
	/** @var int[][] */
	private $cache = [];

	public function __construct(GenLayer $riverLayer, GenLayer $voronoiLayer){
		$this->riverLayer = $riverLayer;
		$this->voronoiLayer = $voronoiLayer;
	}

	public function getBiome($x, $z){
		$cx = $x >> 4;
		$cz = $z >> 4;
		$key = $cx . ':' . $cz;

		if(!isset($this->cache[$key])){
			$this->cache[$key] = $this->voronoiLayer->getInts($cx << 4, $cz << 4, 16, 16);
		}

		return $this->cache[$key][($z & 15) * 16 + ($x & 15)] & 0xFF;
	}

	/**
	 * 对应 Java WorldChunkManager.areBiomesViable / MCPE BiomeSource::isBiomeAllowed：
	 * 在给定中心周围按 4 格间隔采样，确认全部属于允许群系。
	 */
	public function isBiomeAllowed($x, $z, $radius, array $allowedIDs){
		for($dx = -$radius; $dx <= $radius; $dx += 4){
			for($dz = -$radius; $dz <= $radius; $dz += 4){
				if(!in_array($this->getBiome($x + $dx, $z + $dz), $allowedIDs, true)){
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * 对应 WorldChunkManager.func_150795_a：在半径 radius 内找允许群系的最近位置。
	 * @param int[] $allowedIDs
	 * @return int[]|null [x, 0, z]
	 */
	public function findBiomePosition($x, $z, $radius, array $allowedIDs, JavaRandom $random){
		$l = ($x - $radius) >> 2;
		$i1 = ($z - $radius) >> 2;
		$j1 = ($x + $radius) >> 2;
		$k1 = ($z + $radius) >> 2;
		$l1 = $j1 - $l + 1;
		$i2 = $k1 - $i1 + 1;

		$ints = $this->riverLayer->getInts($l, $i1, $l1, $i2);
		$position = null;
		$j2 = 0;

		for($k2 = 0; $k2 < $l1 * $i2; $k2++){
			$l2 = ($l + $k2 % $l1) << 2;
			$i3 = ($i1 + intdiv($k2, $l1)) << 2;
			$id = $ints[$k2] & 0xFF;

			if(in_array($id, $allowedIDs, true) && ($position === null || $random->nextBoundedInt($j2 + 1) === 0)){
				$position = [$l2, 0, $i3];
				$j2++;
			}
		}

		return $position;
	}
}
