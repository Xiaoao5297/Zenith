<?php

namespace pocketmine\level\generator\gorigional\object;

use pocketmine\level\ChunkManager;
use pocketmine\level\format\FullChunk;

/**
 * 把核心 ChunkManager 适配为装饰器需要的 ObjectChunkManager。
 * 装饰期间反复访问邻近方块，这里缓存最近使用的区块以避免每次重新查找。
 */
class ChunkManagerAdapter implements ObjectChunkManager{
	/** @var ChunkManager */
	private $manager;

	/** @var int */
	private $lastCx = PHP_INT_MIN;
	/** @var int */
	private $lastCz = PHP_INT_MIN;
	/** @var FullChunk|null */
	private $lastChunk = null;

	public function __construct(ChunkManager $manager){
		$this->manager = $manager;
	}

	private function chunkAt($x, $z){
		$cx = $x >> 4;
		$cz = $z >> 4;
		if($cx !== $this->lastCx || $cz !== $this->lastCz){
			$this->lastChunk = $this->manager->getChunk($cx, $cz);
			$this->lastCx = $cx;
			$this->lastCz = $cz;
		}
		return $this->lastChunk;
	}

	public function getBlockId($x, $y, $z){
		if($y < 0 || $y >= 128){
			return 0;
		}
		$chunk = $this->chunkAt($x, $z);
		if($chunk === null){
			return 0;
		}
		return $chunk->getBlockId($x & 15, $y, $z & 15);
	}

	public function setBlock($x, $y, $z, $id, $meta){
		if($y < 0 || $y >= 128){
			return;
		}
		$chunk = $this->chunkAt($x, $z);
		if($chunk === null){
			return;
		}
		$chunk->setBlock($x & 15, $y, $z & 15, $id, $meta);
	}

	public function getHeight($x, $z){
		$chunk = $this->chunkAt($x, $z);
		if($chunk === null){
			return 0;
		}
		return $chunk->getHighestBlockAt($x & 15, $z & 15);
	}
}
