<?php

namespace pocketmine\level\generator\gorigional\structure;

use pocketmine\level\format\FullChunk;
use pocketmine\level\generator\gorigional\JavaRandom;

/**
 * 移植自 Java 1.7.10 MapGenStructure：两阶段结构生成。
 * 准备阶段（generateChunk）扫描 8 格范围建立 StructureStart 缓存；
 * 生成阶段（populateChunk）用区块填充随机数写出相交的结构。
 */
abstract class MapGenStructure extends MapGenBase{
	/** @var StructureStart[] key = originX:originZ */
	protected $structureMap = [];
	/** @var bool[] 已检查过且没有结构的区块，避免相邻区块重复扫描 */
	protected $checkedChunks = [];
	/** @var World|null */
	protected $world;

	public function prepare(World $world, $chunkX, $chunkZ, FullChunk $chunk){
		$this->world = $world;
		$this->generate($chunkX, $chunkZ, $chunk);
	}

	public function recursiveGenerate($chunkX, $chunkZ, $originX, $originZ, FullChunk $chunk){
		$key = $originX . ':' . $originZ;
		if(isset($this->structureMap[$key])){
			return;
		}
		// 负结果也缓存；否则每个区块都会把 ±8 范围重新算一遍。
		if(isset($this->checkedChunks[$key])){
			return;
		}
		$this->checkedChunks[$key] = true;

		if($this->canSpawnStructureAtCoords($originX, $originZ)){
			$start = $this->getStructureStart($originX, $originZ);
			if($start !== null){
				$this->structureMap[$key] = $start;
			}
		}
	}

	public function generateStructure(World $world, JavaRandom $random, $chunkX, $chunkZ){
		$this->world = $world;

		$k = $chunkX << 4;
		$l = $chunkZ << 4;
		$box = new BoundingBox($k, 0, $l, $k + 15, 255, $l + 15);

		$flag = false;
		foreach($this->structureMap as $start){
			if($start->boundingBox !== null && $start->boundingBox->intersectsWith($box)){
				$start->generateStructure($world, $random, $box);
				$flag = true;
			}
		}

		return $flag;
	}

	abstract protected function canSpawnStructureAtCoords($chunkX, $chunkZ);

	abstract protected function getStructureStart($chunkX, $chunkZ);
}
