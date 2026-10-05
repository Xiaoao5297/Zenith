<?php

namespace pocketmine\level\generator\gorigional\object;

use pocketmine\level\generator\gorigional\JavaRandom;

/**
 * 移植自 SCAXE-GO-CE 各 object 的 Generator 接口。
 */
interface Feature{

	public function generate(ObjectChunkManager $level, JavaRandom $random, BlockPos $pos);
}
