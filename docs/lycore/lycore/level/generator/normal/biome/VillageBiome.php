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

use lycore\block\Block;
use lycore\block\Flower as FlowerBlock;
use lycore\level\generator\populator\Flower;
use lycore\level\generator\populator\Grass;
use lycore\level\generator\populator\LilyPad;
use lycore\level\generator\populator\Sugarcane;
use lycore\level\generator\populator\TallGrass;
use lycore\level\generator\populator\TallSugarcane;
use lycore\level\generator\populator\WaterPit;

class VillageBiome extends GrassyBiome{

	public function __construct(){
		parent::__construct();

		$sugarcane = new Sugarcane();
		$sugarcane->setBaseAmount(6);
		$tallSugarcane = new TallSugarcane();
		$tallSugarcane->setBaseAmount(60);
		$grass = new Grass();
		$grass->setBaseAmount(16);
		$tallGrass = new TallGrass();
		$tallGrass->setBaseAmount(2);
		$waterPit = new WaterPit();
		$waterPit->setBaseAmount(9999);
		$lilyPad = new LilyPad();
		$lilyPad->setBaseAmount(4);

		$flower = new Flower();
		$flower->setBaseAmount(1);
		$flower->addType([Block::DANDELION, 0]);
		$flower->addType([Block::RED_FLOWER, FlowerBlock::TYPE_POPPY]);
		$flower->addType([Block::RED_FLOWER, FlowerBlock::TYPE_AZURE_BLUET]);

		$this->addPopulator($sugarcane);
		$this->addPopulator($tallSugarcane);
		$this->addPopulator($tallGrass);
		$this->addPopulator($grass);
		$this->addPopulator($flower);
		$this->addPopulator($waterPit);
		$this->addPopulator($lilyPad);

		$this->setElevation(61, 68);

		$this->temperature = 0.8;
		$this->rainfall = 0.4;
	}

	public function getName() : string{
		return "Village";
	}
}
