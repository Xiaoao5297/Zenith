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

/**
 * 移植自 Java 1.7.10 MapGenMineshaft。
 */
class MapGenMineshaft extends MapGenStructure{
	/** @var float */
	private $chance = 0.004;

	protected function canSpawnStructureAtCoords($chunkX, $chunkZ){
		return $this->rand->nextDouble() < $this->chance
			&& $this->rand->nextBoundedInt(80) < max(abs($chunkX), abs($chunkZ));
	}

	protected function getStructureStart($chunkX, $chunkZ){
		return new MineshaftStart($this->world, $this->rand, $chunkX, $chunkZ);
	}
}
