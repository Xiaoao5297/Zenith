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

namespace lycore\level\generator\diamonds;

use lycore\block\Block;
use lycore\level\ChunkManager;
use lycore\level\generator\biome\Biome;
use lycore\level\generator\end\EnderPilar;
use lycore\level\generator\Generator;
use lycore\level\generator\noise\Simplex;
use lycore\level\generator\populator\Populator;
use lycore\math\Vector3 as Vector3;
use lycore\utils\Random;
use lycore\level\generator\populator\GroundCover;
use lycore\level\generator\object\OreType;
use lycore\level\generator\populator\Ore;
use lycore\block\CoalOre;
use lycore\block\Diamond;
use lycore\block\DiamondOre;
use lycore\block\Dirt;
use lycore\block\GoldOre;
use lycore\block\Gravel;
use lycore\block\IronOre;
use lycore\block\LapisOre;
use lycore\block\RedstoneOre;
use lycore\block\Stone;

class diamonds extends Generator{

    /** @var Populator[] */
    private $populators = [];
    /** @var ChunkManager */
    private $level;
    /** @var Random */
    private $random;
    private $waterHeight = 0;
    private $emptyHeight = 32;
    private $emptyAmplitude = 1;
    private $density = 0.6;

    /** @var Populator[] */
    private $generationPopulators = [];
    /** @var Simplex */
    private $noiseBase;

    private static $GAUSSIAN_KERNEL = null;
    private static $SMOOTH_SIZE = 2;

    public function __construct(array $options = [])
    {
        if (self::$GAUSSIAN_KERNEL === null) {
            self::generateKernel();
        }
    }

    private static function generateKernel()
    {
        self::$GAUSSIAN_KERNEL = [];

        $bellSize = 1 / self::$SMOOTH_SIZE;
        $bellHeight = 2 * self::$SMOOTH_SIZE;

        for ($sx = -self::$SMOOTH_SIZE; $sx <= self::$SMOOTH_SIZE; ++$sx) {
            self::$GAUSSIAN_KERNEL[$sx + self::$SMOOTH_SIZE] = [];

            for ($sz = -self::$SMOOTH_SIZE; $sz <= self::$SMOOTH_SIZE; ++$sz) {
                $bx = $bellSize * $sx;
                $bz = $bellSize * $sz;
                self::$GAUSSIAN_KERNEL[$sx + self::$SMOOTH_SIZE][$sz + self::$SMOOTH_SIZE] = $bellHeight * exp(-($bx * $bx + $bz * $bz) / 2);
            }
        }
    }

    public function getName(): string
    {
        return "diamonds";
    }

    public function getWaterHeight(): int
    {
        return $this->waterHeight;
    }

    public function getSettings()
    {
        return [];
    }

    public function init(ChunkManager $level, Random $random)
    {
        $this->random = $random;
        $this->level = $level;
    }

    public function generateChunk($chunkX, $chunkZ)
    {
        $chunk = $this->level->getChunk($chunkX, $chunkZ);

        for ($x = 0; $x < 16; ++$x) {
            for ($z = 0; $z < 16; ++$z) {
                for ($y = 0; $y < 128; ++$y) {
                    if($y < 64) {
                        if($y === 0){
                            $chunk->setBlockId($x, $y, $z, Block::BEDROCK);
                            continue;
                        }
                        //这也不对啊
                        //if($this->level->getBlockIdAt($x, $y, $z) !== Block::DIAMOND_BLOCK) {
                            $chunk->setBlockId($x, $y, $z, Block::DIAMOND_ORE);
                        //}
                    }
                }
            }
        }

        foreach ($this->generationPopulators as $populator) {
            $populator->populate($this->level, $chunkX, $chunkZ, $this->random);
        }
    }

    public function populateChunk($chunkX, $chunkZ)
    {
        $this->random->setSeed(0xa6fe78dc ^ ($chunkX << 8) ^ $chunkZ ^ $this->level->getSeed());
        foreach ($this->populators as $populator) {
            $populator->populate($this->level, $chunkX, $chunkZ, $this->random);
        }

        $chunk = $this->level->getChunk($chunkX, $chunkZ);
        $biome = Biome::getBiome(Biome::HELL);
        $biome->populateChunk($this->level, $chunkX, $chunkZ, $this->random);
    }

    public function getSpawn()
    {
        return new Vector3(48, 128, 48);
    }
}