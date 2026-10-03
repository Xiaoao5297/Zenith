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

namespace lycore\level\generator\normal\biome;

use lycore\level\generator\populator\Sugarcane;
use lycore\level\generator\populator\TallGrass;
use lycore\level\generator\populator\Grass;
use lycore\level\generator\populator\TallSugarcane;
use lycore\block\Block;

class FrozenRiverBiome extends RiverBiome{
	
	public function __construct(){
		parent::__construct();
		$this->removePopulator(Grass::class);
		$this->removePopulator(TallGrass::class);

		$grass = new Grass();
		$grass->setBaseAmount(2);
		$tallGrass = new TallGrass();
		$tallGrass->setBaseAmount(1);

		$this->addPopulator($tallGrass);
		$this->addPopulator($grass);

		$this->setGroundCover([
			Block::get(Block::SNOW_LAYER, 0),
			Block::get(Block::GRASS, 0),
			Block::get(Block::DIRT, 0),
			Block::get(Block::DIRT, 0),
			Block::get(Block::DIRT, 0),
		]);
	}
	
	public function getName() : string{
		return "FrozenRiver";
	}
	
}
