<?php

namespace pocketmine\level\generator\gorigional\structure;

/**
 * 移植自 SCAXE-GO-CE structure 的 WorldAccess 接口。
 */
interface WorldAccess{

	public function getBlockId($x, $y, $z);

	public function setBlock($x, $y, $z, $id, $meta);

	/**
	 * @param array $items [[slot, id, meta, count], ...]
	 */
	public function setChestContents($x, $y, $z, array $items);

	public function setSpawnerEntity($x, $y, $z, $entityId);

	public function getHeight($x, $z);
}
