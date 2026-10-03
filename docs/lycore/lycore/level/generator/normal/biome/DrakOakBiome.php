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
use lycore\level\generator\populator\Mushroom;
use lycore\level\generator\populator\Grass;
use lycore\level\generator\populator\TallGrass;
use lycore\level\generator\populator\Tree;

class DrakOakBiome extends GrassyBiome{ //ROOFED_FOREST

	public function __construct(){
		parent::__construct();
		
		$tree3 = new Tree(Sapling::DARK_OAK);
		$tree3->setBaseAmount(8);
		$this->addPopulator($tree3);
		
		$Mushroom = new Mushroom();
		$Mushroom->setBaseAmount(1);
		$this->addPopulator($Mushroom);
		
		$Grass = new Grass();
		$Grass->setBaseAmount(6);

		$tallGrass = new TallGrass();
		$tallGrass->setBaseAmount(3);
		
		$this->addPopulator($tallGrass);
		$this->addPopulator($Grass);

		$this->setElevation(63, 81);

		$this->temperature = 0.7;
		$this->temperature = 0.8;
		
	}

	public function getName() : string{
		return "ROOFED_FOREST";
	}
	
	public function getColor(){
		return 0x507A32;
	}
}
