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


namespace pocketmine\level\generator\gorigional\structure;

use pocketmine\level\generator\gorigional\JavaRandom;

/**
 * 结构生成所需的 World 抽象（对应 Java net.minecraft.world.World 的用到的子集）。
 */
interface World extends WorldAccess{

	public function getSeed();

	public function getBiome($x, $z);

	public function getBiomeManager();

	/**
	 * @param int[] $allowedIDs
	 * @return int[]|null
	 */
	public function findBiomePosition($x, $z, $radius, array $allowedIDs, JavaRandom $random);

	public function getRandomAt($x, $z, $salt);
}
