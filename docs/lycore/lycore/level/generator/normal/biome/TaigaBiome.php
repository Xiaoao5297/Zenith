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
use lycore\level\generator\normal\populator\VillagePopulator;
use lycore\level\generator\populator\MossStone;
use lycore\level\generator\populator\Tree;

class TaigaBiome extends SnowyBiome{

	public function __construct(){
		parent::__construct();

		$trees = new Tree(Sapling::SPRUCE);
		$trees->setBaseAmount(10);
		$this->addPopulator($trees);

		$mossStone = new MossStone();
		$mossStone->setBaseAmount(1);

		$this->addPopulator($mossStone);
		$this->addPopulator(new VillagePopulator());

		$this->setElevation(63, 81);

		$this->temperature = 0.05;
		$this->rainfall = 0.8;

		$this->setGroundCover([
			Block::get(Block::PODZOL, 0),
			Block::get(Block::PODZOL, 0),
			Block::get(Block::MOSS_STONE, 0),
			Block::get(Block::MOSS_STONE, 0),
			Block::get(Block::MOSS_STONE, 0),
		]);
	}

	public function getName() : string{
		return "Taiga";
	}
}
