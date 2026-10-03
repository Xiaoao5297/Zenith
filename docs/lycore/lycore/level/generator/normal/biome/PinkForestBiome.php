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
use lycore\level\generator\populator\TallGrass;
use lycore\level\generator\populator\Grass;
use lycore\level\generator\populator\Tree;

class PinkForestBiome extends GrassyBiome{

	const TYPE_NORMAL = 0;

	public $type;

	public function __construct($type = self::TYPE_NORMAL){
		parent::__construct();

		$this->type = $type;

		$trees = new Tree(Sapling::PINK);
		$trees->setBaseAmount(5);
		$this->addPopulator($trees);
		
		$Grass = new Grass();
		$Grass->setBaseAmount(18);
		
		$tallGrass = new TallGrass();
		$tallGrass->setBaseAmount(2);

		$this->addPopulator($tallGrass);
		$this->addPopulator($Grass);

		$this->setElevation(63, 81);

		$this->temperature = 0.5;
		$this->rainfall = 0.5;
	}

	public function getName() : string{
		return $this->type === "Pink Forest";
	}
}
