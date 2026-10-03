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

use lycore\level\generator\normal\populator\VillagePopulator;
use lycore\level\generator\populator\TallGrass;
use lycore\level\generator\populator\Grass;
use lycore\level\generator\populator\AcaciaTree;
use lycore\block\Block;


class SavannaBiome extends GrassyBiome{

	public function __construct(){
		parent::__construct();
		$tree = new AcaciaTree();
		$tree->setBaseAmount(1);
		$Grass = new Grass();
		$Grass->setBaseAmount(12);
		$tallGrass = new TallGrass();
		$tallGrass->setBaseAmount(8);	
		$this->addPopulator($tree);
		$this->addPopulator($tallGrass);
		$this->addPopulator($Grass);
		$this->addPopulator(new VillagePopulator());

		$this->setElevation(62, 68);

		$this->temperature = 1.2;
		$this->rainfall = 0;
	}

	public function getName() : string{
		return "Savanna";
	}
	
	public function getColor(){
		return 0xbfb755;
	}
}
