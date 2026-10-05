<?php

namespace pocketmine\level\generator\gorigional\biome;

use pocketmine\level\format\FullChunk;
use pocketmine\level\generator\gorigional\JavaRandom;

/**
 * 移植自 SCAXE-GO-CE pkg/level/generator/biome/biome.go 的 Biome 接口。
 */
interface Biome{

	public function getID();

	public function getMinElevation();

	public function getMaxElevation();

	public function getColor();

	public function genTerrainBlocks(FullChunk $chunk, JavaRandom $r, $x, $z, $noiseVal);
}
