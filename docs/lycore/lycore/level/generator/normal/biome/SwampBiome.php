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
use lycore\level\generator\populator\TallGrass;
use lycore\level\generator\populator\Tree;
use lycore\level\generator\populator\LilyPad;
use lycore\level\generator\normal\populator\SwampHut;
use lycore\block\Sapling;

class SwampBiome extends GrassyBiome{

	public function __construct(){
		parent::__construct();

		$flower = new Flower();
		$flower->setBaseAmount(4);
		$flower->addType([Block::RED_FLOWER, FlowerBlock::TYPE_BLUE_ORCHID]);

		$this->addPopulator($flower);

		$tallGrass = new TallGrass();
		$tallGrass->setBaseAmount(4);
		$this->addPopulator($tallGrass);
		
		$Tree = new Tree(Sapling::OAK);
		$Tree->setBaseAmount(1);
		$this->addPopulator($Tree);
		
		$LilyPad = new LilyPad();
		$LilyPad->setBaseAmount(4);
		$this->addPopulator($LilyPad);
		
		$SwampHut = new SwampHut();
		$this->addPopulator($SwampHut);

		$this->setElevation(62, 63);

		$this->temperature = 0.8;
		$this->rainfall = 0.9;
	}

	public function getName() : string{
		return "Swamp";
	}

	public function getColor(){
		return 0x6a7039;
	}
}
