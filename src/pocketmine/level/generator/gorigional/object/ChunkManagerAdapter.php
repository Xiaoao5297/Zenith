<?php

namespace pocketmine\level\generator\gorigional\object;

use pocketmine\level\ChunkManager;

/**
 * 把核心 ChunkManager 适配为装饰器需要的 ObjectChunkManager。
 */
class ChunkManagerAdapter implements ObjectChunkManager{
	/** @var ChunkManager */
	private $manager;

	public function __construct(ChunkManager $manager){
		$this->manager = $manager;
	}

	public function getBlockId($x, $y, $z){
		return $this->manager->getBlockIdAt($x, $y, $z);
	}

	public function setBlock($x, $y, $z, $id, $meta){
		$this->manager->setBlockIdAt($x, $y, $z, $id);
		$this->manager->setBlockDataAt($x, $y, $z, $meta);
	}

	public function getHeight($x, $z){
		$chunk = $this->manager->getChunk($x >> 4, $z >> 4);
		if($chunk === null){
			return 0;
		}
		return $chunk->getHighestBlockAt($x & 15, $z & 15);
	}
}
