<?php

namespace pocketmine\level\generator\gorigional\object;

/**
 * 移植自 SCAXE-GO-CE pkg/level/generator/populator.ChunkManager（装饰所需部分）。
 */
interface ObjectChunkManager{

	public function getBlockId($x, $y, $z);

	public function setBlock($x, $y, $z, $id, $meta);

	public function getHeight($x, $z);
}
