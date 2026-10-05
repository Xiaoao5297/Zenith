<?php

namespace pocketmine\level\generator\gorigional\structure;

/**
 * 移植自 SCAXE-GO-CE structure 的 WorldAccess 接口。
 */
interface WorldAccess{

	public function getBlockId($x, $y, $z);

	public function setBlock($x, $y, $z, $id, $meta);
}
