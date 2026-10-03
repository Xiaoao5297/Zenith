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

declare(strict_types=1);

namespace lycore\level\generator\normal\populator;

use lycore\block\Block;
use lycore\level\ChunkManager;
use lycore\level\generator\populator\VariableAmountPopulator;
use lycore\utils\Random;
use lycore\level\generator\normal\object\Igloo as ObjectIgloo;

class Igloo extends VariableAmountPopulator{

    /** @var  ChunkManager */
    private $level;

    /*
     * Populate the chunk
     * @param $level pocketmine\level\ChunkManager
     * @param $chunkX int
     * @param $chunkZ int
     * @param $random pocketmine\utils\Random
     */
    public function populate(ChunkManager $level, $chunkX, $chunkZ, Random $random){
        if(!$this->canPopulateOverworldStructure($level)){
            return;
        }

        $this->level = $level;
        if ($random->nextBoundedInt(100) > 30)
            return;
        $igloo = new ObjectIgloo();
        $x = $random->nextRange($chunkX << 4, ($chunkX << 4) + 15);
        $z = $random->nextRange($chunkZ << 4, ($chunkZ << 4) + 15);
        $y = $this->getHighestWorkableBlock($x, $z) - 1;
        if ($igloo->canPlaceObject($level, $x, $y, $z, $random))
            $igloo->placeObject($level, $x, $y, $z, $random);
    }

    /*
     * Gets the top block (y) on an x and z axes
     * @param $x int
     * @param $z int
     */
    protected function getHighestWorkableBlock($x, $z){
        for ($y = 127; $y > 0; --$y) {
            $b = $this->level->getBlockIdAt($x, $y, $z);
            if ($b === Block::DIRT or $b === Block::GRASS or $b === Block::PODZOL) {
                break;
            } elseif ($b !== 0 and $b !== Block::SNOW_LAYER) {
                return -1;
            }
        }

        return ++$y;
    }

}
