<?php

/**
 * All the Object populator classes
 */
namespace pocketmine\level\generator\populator;

use pocketmine\level\ChunkManager;
use pocketmine\level\Level;
use pocketmine\utils\Random;

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