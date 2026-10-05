<?php

namespace pocketmine\level\generator\gorigional\biome;

use pocketmine\level\format\FullChunk;
use pocketmine\level\generator\gorigional\JavaRandom;
use pocketmine\level\generator\gorigional\object\BlockPos;
use pocketmine\level\generator\gorigional\object\Decorator;
use pocketmine\level\generator\gorigional\object\Feature;
use pocketmine\level\generator\gorigional\object\ObjectChunkManager;

/**
 * 移植自 SCAXE-GO-CE pkg/level/generator/biome/biome.go 的 Biome 接口。
 */
interface Biome{

	public function getID();

	public function getMinElevation();

	public function getMaxElevation();

	public function getColor();

	public function genTerrainBlocks(FullChunk $chunk, JavaRandom $r, $x, $z, $noiseVal);

	public function decorate(ObjectChunkManager $level, JavaRandom $r, BlockPos $pos);

	public function getTreeFeature(JavaRandom $r);

	public function getFlowerType(JavaRandom $r, BlockPos $pos);

	public function getDecorator();
}
