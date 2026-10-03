<?php

/*
 * ██╗   ██╗    ██████╗ ██████╗ ██████╗ ███████╗
 * ██║   ██║   ██╔════╝██╔═══██╗██╔══██╗██╔════╝
 * ██║   ██║   ██║     ██║   ██║██████╔╝█████╗
 * ██║   ██║   ██║     ██║   ██║██╔══██╗██╔══╝
 * ╚██████╔╝██╗╚██████╗╚██████╔╝██║  ██║███████╗
 *  ╚═════╝ ╚═╝ ╚═════╝ ╚═════╝ ╚═╝  ╚═╝╚══════╝
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * @Author: U core
 *
 * @Links:
 *  > LY Core
 *  > LY Core Project
*/
namespace lycore\level\generator\populator;

use lycore\level\ChunkManager;
use lycore\level\Level;
use lycore\utils\Random;

abstract class Populator{
	protected function canPopulateOverworldStructure(ChunkManager $level) : bool{
		if(method_exists($level, "getDimension") && $level->getDimension() !== Level::DIMENSION_NORMAL){
			return false;
		}

		return true;
	}

	protected function canPopulateNetherStructure(ChunkManager $level) : bool{
		if(method_exists($level, "getDimension") && $level->getDimension() !== Level::DIMENSION_NETHER){
			return false;
		}

		return true;
	}

	public abstract function populate(ChunkManager $level, $chunkX, $chunkZ, Random $random);
}
