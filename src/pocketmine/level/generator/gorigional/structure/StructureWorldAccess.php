<?php

namespace pocketmine\level\generator\gorigional\structure;

use pocketmine\level\ChunkManager;

/**
 * 把核心 ChunkManager 适配为结构生成所需的 WorldAccess。
 */
class StructureWorldAccess implements WorldAccess{
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
}
