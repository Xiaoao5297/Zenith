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
 * 移植自 Java 1.7.10 StructureMineshaftStart。
 */
class MineshaftStart extends StructureStart{

	public function __construct(World $world, JavaRandom $rand, $chunkX, $chunkZ){
		parent::__construct($chunkX, $chunkZ);

		$room = new MineshaftRoom(0, $rand, ($chunkX << 4) + 2, ($chunkZ << 4) + 2);
		$this->components[] = $room;
		$room->buildComponent($room, $this->components, $rand);

		$this->updateBoundingBox();
		$this->placeAtHeight($world, $rand, 10);
	}
}
