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

class Well extends VariableAmountPopulator{

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
        $random->setSeed((int) $level->getSeed() ^ Level::chunkHash((int) $chunkX, (int) $chunkZ));

        $x = ((int) $chunkX << 4) + $random->nextBoundedInt(15);
        $z = ((int) $chunkZ << 4) + $random->nextBoundedInt(15);
        $y = $this->getHighestWorkableBlock($x, $z) - 1;
        if(!self::isWellRarityHit((int) $level->getSeed(), $x, $y, $z)){
            return;
        }

        $this->placeWellAt($level, $x, $y, $z, $random);
    }

    public static function isWellRarityHit(int $seed, int $x, int $y, int $z) : bool{
        $random = new Random(0);
        $random->setSeed($seed ^ ($x + $y + $z));
        return $random->nextBoundedInt(500) === 0;
    }

    public function placeWellAt(ChunkManager $level, int $x, int $y, int $z, Random $random) : bool{
        if(!$this->canPopulateOverworldStructure($level)){
            return false;
        }

        $this->level = $level;
        $well = new \lycore\level\generator\normal\object\Well();
        if($y > 0 && $well->canPlaceObject($level, $x, $y, $z, $random)){
            $well->placeObject($level, $x, $y, $z, $random);
            return true;
        }

        return false;
    }

    protected function getHighestWorkableBlock($x, $z){
        for ($y = 127; $y > 0; --$y) {
            $b = $this->level->getBlockIdAt($x, $y, $z);
            if ($b === Block::SAND) {
                break;
            }
        }

        return ++$y;
    }

}
