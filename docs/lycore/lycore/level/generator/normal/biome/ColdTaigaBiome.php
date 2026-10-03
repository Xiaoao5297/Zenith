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

use lycore\block\Sapling;
use lycore\block\Block;
use lycore\level\generator\populator\MossStone;
use lycore\level\generator\populator\Tree;

class ColdTaigaBiome extends TaigaBiome{

	public function __construct(){
		parent::__construct();

		$this->setGroundCover([
			Block::get(Block::SNOW_LAYER, 0),
			Block::get(Block::PODZOL, 0),
			Block::get(Block::PODZOL, 0),
			Block::get(Block::MOSS_STONE, 0),
			Block::get(Block::MOSS_STONE, 0),
			Block::get(Block::MOSS_STONE, 0),
		]);
	}

	public function getName() : string{
		return "ColdTaiga";
	}
}
