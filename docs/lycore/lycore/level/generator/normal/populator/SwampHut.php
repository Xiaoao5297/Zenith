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

namespace lycore\level\generator\normal\populator;

use lycore\block\Block;
use lycore\level\ChunkManager;
use lycore\level\generator\populator\VariableAmountPopulator;
use lycore\level\Level;
use lycore\utils\Random;

class SwampHut extends VariableAmountPopulator{

    /** @var ChunkManager */
    private $level;

    /**
     * Populates the chunk
     *
     * @param ChunkManager $level
     * @param int $chunkX
     * @param int $chunkZ
     * @param Random $random
     * @return void
     */
    public function populate(ChunkManager $level, $chunkX, $chunkZ, Random $random){
        if(!$this->canPopulateOverworldStructure($level)){
            return;
        }

        $this->level = $level;
        if ($random->nextBoundedInt(1000) > 25)
            return; // ~1 chance / 1000 due to building limitations.
        $SwampHut = new \lycore\level\generator\normal\object\SwampHut();
        $x = $random->nextRange($chunkX << 4, ($chunkX << 4) + 15);
        $z = $random->nextRange($chunkZ << 4, ($chunkZ << 4) + 15);
        $y = $this->getHighestWorkableBlock($x, $z) - 1;
        if ($SwampHut->canPlaceObject($level, $x, $y, $z, $random))
            $SwampHut->placeObject($level, $x, $y, $z, $random);
    }

    protected function getHighestWorkableBlock($x, $z){
        for ($y = 127; $y > 0; --$y) {
            $b = $this->level->getBlockIdAt($x, $y, $z);
            if ($b === Block::WATER or $b === Block::STILL_WATER) {
                break;
            }
        }

        return ++$y;
    }

}
