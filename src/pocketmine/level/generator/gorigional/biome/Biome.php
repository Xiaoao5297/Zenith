<?php

/***
 *  _____                _  __   __
 * /__  /  ___   ____   (_)/ /_ / /_
 *   / /  / _ \ / __ \ / // __// __ \
 *  / /__/  __// / / // // /_ / / / /
 * /____/\___//_/ /_//_/ \__//_/ /_/
 *
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * @author Xiaoao
 * @link https://github.com/Xiaoao5297/Zenith
 *
 *
*/


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
