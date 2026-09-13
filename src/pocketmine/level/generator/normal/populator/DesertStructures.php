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

namespace pocketmine\level\generator\normal\populator;

require_once __DIR__ . "/Temple.php";
require_once __DIR__ . "/Well.php";
require_once __DIR__ . "/Fossil.php";

use pocketmine\level\ChunkManager;
use pocketmine\level\generator\biome\Biome;
use pocketmine\level\generator\populator\Populator;
use pocketmine\utils\Random;

class DesertStructures extends Populator{
	/** @var Populator[] */
	private $populators;

	public function __construct(){
		$this->populators = [
			new Temple(),
			new Well(),
			new Fossil(),
		];
	}

	public function populate(ChunkManager $level, $chunkX, $chunkZ, Random $random){
		if(!$this->canPopulateOverworldStructure($level)){
			return;
		}

		if(!$this->isDesertChunk($level, (int) $chunkX, (int) $chunkZ)){
			return;
		}

		foreach($this->populators as $populator){
			$populator->populate($level, (int) $chunkX, (int) $chunkZ, $random);
		}
	}

	private function isDesertChunk(ChunkManager $level, int $chunkX, int $chunkZ) : bool{
		$chunk = $level->getChunk($chunkX, $chunkZ);
		if($chunk !== null && method_exists($chunk, "getBiomeId")){
			return in_array((int) $chunk->getBiomeId(7, 7), [Biome::DESERT, Biome::DESERT_HILLS], true);
		}

		return false;
	}
}
