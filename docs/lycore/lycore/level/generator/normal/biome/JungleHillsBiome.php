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

use lycore\level\generator\populator\Grass;
use lycore\level\generator\populator\JungleBush;
use lycore\level\generator\populator\TallGrass;
use lycore\level\generator\populator\Tree;
use lycore\level\generator\populator\Melon;
use lycore\block\Block;
use lycore\block\Sapling;

class JungleHillsBiome extends GrassyBiome{

	public function __construct(){
		parent::__construct();
		$tree = new Tree(Sapling::JUNGLE);
		$tree->setBaseAmount(10);
		$bush = new JungleBush();
		$bush->setBaseAmount(3);
		$Grass = new Grass();
		$Grass->setBaseAmount(14);
		$tallGrass = new TallGrass();
		$tallGrass->setBaseAmount(5);
		$Melon = new Melon();
		$Melon->setBaseAmount(1);
		$this->addPopulator($Melon);
		$this->addPopulator($tree);
		$this->addPopulator($bush);
		$this->addPopulator($tallGrass);
		$this->addPopulator($Grass);

		$this->setElevation(63, 90);

		$this->temperature = 1.2;
		$this->rainfall = 0;
	}

	public function getName() : string{
		return "JungleHills";
	}
	
	public function getColor(){
		return 0x59C93C;
	}
}
