<?php

namespace pocketmine\level\generator\gorigional\structure;

use pocketmine\level\generator\gorigional\JavaRandom;

/**
 * 结构生成所需的 World 抽象（对应 Java net.minecraft.world.World 的用到的子集）。
 */
interface World extends WorldAccess{

	public function getSeed();

	public function getBiome($x, $z);

	/**
	 * @param int[] $allowedIDs
	 * @return int[]|null
	 */
	public function findBiomePosition($x, $z, $radius, array $allowedIDs, JavaRandom $random);

	public function getRandomAt($x, $z, $salt);
}
