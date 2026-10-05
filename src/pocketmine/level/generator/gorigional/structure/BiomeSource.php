<?php

namespace pocketmine\level\generator\gorigional\structure;

/**
 * 移植自 SCAXE-GO-CE structure.BiomeSource。
 */
interface BiomeSource{

	public function getBiome($x, $z);
}
