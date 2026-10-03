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

namespace lycore\level\generator\normal\object;

use lycore\block\Block;
use lycore\level\ChunkManager;
use lycore\level\generator\object\PopulatorObject;
use lycore\utils\Random;

class Well extends PopulatorObject{

    /** @var ChunkManager */
    private $level;
    private $overridable = [
        Block::AIR => true,
        Block::SAPLING => true,
        Block::LOG => true,
        Block::LEAVES => true,
        Block::STONE => true,
        Block::DANDELION => true,
        Block::POPPY => true,
        Block::SAND => true,
        Block::SANDSTONE => true,
        Block::LOG2 => true,
        Block::LEAVES2 => true,
        Block::CACTUS => true
    ];

    private $directions = [
        [1, 1],
        [1, -1],
        [-1, -1],
        [-1, 1]
    ];

    /**
     * Checks if a Well is placable
     *
     * @param ChunkManager $level
     * @param int $x
     * @param int $y
     * @param int $z
     * @param Random $random
     * @return bool
     */
    public function canPlaceObject(ChunkManager $level, $x, $y, $z, Random $random){
        $this->level = $level;
        if($y > 128 || $level->getBlockIdAt($x, $y, $z) !== Block::SAND){
            return false;
        }

        for ($xx = $x - 2; $xx <= $x + 2; $xx++){
            for ($zz = $z - 2; $zz <= $z + 2; $zz++){
                if($level->getBlockIdAt($xx, $y - 1, $zz) === Block::AIR && $level->getBlockIdAt($xx, $y - 2, $zz) === Block::AIR){
                    return false;
                }
            }
        }

        for ($xx = $x - 2; $xx <= $x + 2; $xx++){
            for ($yy = $y; $yy <= $y + 4; $yy++){
                for ($zz = $z - 2; $zz <= $z + 2; $zz++){
					$id = $level->getBlockIdAt($xx, $yy, $zz);
                    if (!isset($this->overridable[$id])){
                        return false;
					}
				}
			}
		}
        return true;
    }

    /**
     * Places a well
     *
     * @param ChunkManager $level
     * @param int $x
     * @param int $y
     * @param int $z
     * @param Random $random
     * @return void
     */
    public function placeObject(ChunkManager $level, $x, $y, $z, Random $random){
        $this->level = $level;

        for($dy = -1; $dy <= 0; ++$dy){
            for($dx = -2; $dx <= 2; ++$dx){
                for($dz = -2; $dz <= 2; ++$dz){
                    $this->placeBlock($x + $dx, $y + $dy, $z + $dz, Block::SANDSTONE);
                }
            }
        }

        $this->placeBlock($x, $y, $z, Block::WATER);
        $this->placeBlock($x - 1, $y, $z, Block::WATER);
        $this->placeBlock($x + 1, $y, $z, Block::WATER);
        $this->placeBlock($x, $y, $z - 1, Block::WATER);
        $this->placeBlock($x, $y, $z + 1, Block::WATER);

        for($dx = -2; $dx <= 2; ++$dx){
            for($dz = -2; $dz <= 2; ++$dz){
                if($dx === -2 || $dx === 2 || $dz === -2 || $dz === 2){
                    $this->placeBlock($x + $dx, $y + 1, $z + $dz, Block::SANDSTONE);
                }
            }
        }

        $this->placeBlock($x + 2, $y + 1, $z, Block::STONE_SLAB, 1);
        $this->placeBlock($x - 2, $y + 1, $z, Block::STONE_SLAB, 1);
        $this->placeBlock($x, $y + 1, $z + 2, Block::STONE_SLAB, 1);
        $this->placeBlock($x, $y + 1, $z - 2, Block::STONE_SLAB, 1);

        for($dx = -1; $dx <= 1; ++$dx){
            for($dz = -1; $dz <= 1; ++$dz){
                if($dx === 0 && $dz === 0){
                    $this->placeBlock($x + $dx, $y + 4, $z + $dz, Block::SANDSTONE);
                }else{
                    $this->placeBlock($x + $dx, $y + 4, $z + $dz, Block::STONE_SLAB, 1);
                }
            }
        }

        for($dy = 1; $dy <= 3; ++$dy){
            $this->placeBlock($x - 1, $y + $dy, $z - 1, Block::SANDSTONE);
            $this->placeBlock($x - 1, $y + $dy, $z + 1, Block::SANDSTONE);
            $this->placeBlock($x + 1, $y + $dy, $z - 1, Block::SANDSTONE);
            $this->placeBlock($x + 1, $y + $dy, $z + 1, Block::SANDSTONE);
        }
    }

    /**
     * Places a block
     *
     * @param int $x
     * @param int $y
     * @param int $z
     * @param int $id
     * @param int $meta
     * @return void
     */
    public function placeBlock($x, $y, $z, $id = 0, $meta = 0){
        $this->level->setBlockIdAt($x, $y, $z, $id);
        $this->level->setBlockDataAt($x, $y, $z, $meta);
    }

}
