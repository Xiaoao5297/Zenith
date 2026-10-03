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
use lycore\level\generator\biome\Biome;
use lycore\level\generator\populator\VariableAmountPopulator;
use lycore\level\Level;
use lycore\utils\Random;

require_once dirname(__DIR__) . "/object/Temple.php";

class Temple extends VariableAmountPopulator{

    const REGION_SIZE = 32;
    const MIN_DISTANCE = 8;
    const CANDIDATE_SPAN = 24;
    const SALT = 14357617;
    const PNX_LOCAL_CENTER_OFFSET = 10;

    /** @var  Level */
    private $level;

    /** @var array<string, array> */
    private static $regionCandidateCache = [];

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
        $this->level = $level;
        $chunk = $level->getChunk((int) $chunkX, (int) $chunkZ);
        $biomeId = $chunk !== null && method_exists($chunk, "getBiomeId") ? $chunk->getBiomeId(7, 7) : Biome::DESERT;
        if(!self::isDesertTempleBiome($biomeId)){
            return;
        }

        $candidate = self::findRegionTempleCandidate(
            $level->getSeed(),
            self::floorDiv((int) $chunkX, self::REGION_SIZE),
            self::floorDiv((int) $chunkZ, self::REGION_SIZE)
        );
        if($candidate["chunkX"] !== (int) $chunkX || $candidate["chunkZ"] !== (int) $chunkZ){
            return;
        }

        $this->placeTempleAtCandidate($level, (int) $chunkX, (int) $chunkZ, $random);
    }

    public function placeTempleAtCandidate(ChunkManager $level, int $chunkX, int $chunkZ, Random $random){
        if(!$this->canPopulateOverworldStructure($level)){
            return null;
        }

        $this->level = $level;
        $candidate = self::findRegionTempleCandidate(
            (int) $level->getSeed(),
            self::floorDiv($chunkX, self::REGION_SIZE),
            self::floorDiv($chunkZ, self::REGION_SIZE)
        );
        if($candidate["chunkX"] !== $chunkX || $candidate["chunkZ"] !== $chunkZ){
            return null;
        }

        $chunk = $level->getChunk($chunkX, $chunkZ);
        if($chunk !== null && method_exists($chunk, "getBiomeId") && !self::isDesertTempleBiome($chunk->getBiomeId(7, 7))){
            return null;
        }

        $origin = self::getTempleOrigin((int) $level->getSeed(), $chunkX, $chunkZ);
        $x = $origin["x"];
        $z = $origin["z"];

        $existing = $this->findExistingTemple($level, $x, $z);
        if($existing !== null){
            return $existing;
        }

        $temple = new \lycore\level\generator\normal\object\Temple();
        $y = $this->getTempleBaseY($x, $z);
        if($y <= 0){
            return null;
        }
        if(!$temple->canPlaceObject($level, $x, $y, $z, $random)){
            return null;
        }

        $originY = $y - 1;
        $temple->placeObject($level, $x, $originY, $z, $random);
        $placement = self::getTemplePlacement($x, $originY, $z);
        $footprint = self::getTempleFootprintChunksFromOrigin($x, $z);
        $this->markStructureFootprint($level, $footprint);
        $this->processLiveLevelFootprint($level, $footprint);

        return $placement;
    }

    public static function findRegionTempleCandidate(int $seed, int $regionX, int $regionZ) : array{
        $cacheKey = $seed . ":" . $regionX . ":" . $regionZ;
        if(!isset(self::$regionCandidateCache[$cacheKey])){
            $random = new Random(0);
            $random->setSeed(($seed ^ self::SALT) + Level::chunkHash($regionX, $regionZ));
            $chunkX = ($regionX * self::REGION_SIZE) + $random->nextBoundedInt(self::CANDIDATE_SPAN);
            $chunkZ = ($regionZ * self::REGION_SIZE) + $random->nextBoundedInt(self::CANDIDATE_SPAN);
            self::$regionCandidateCache[$cacheKey] = [
                "chunkX" => $chunkX,
                "chunkZ" => $chunkZ,
                "centerX" => ($chunkX << 4) + 8,
                "centerZ" => ($chunkZ << 4) + 8,
            ];
        }

        return self::$regionCandidateCache[$cacheKey];
    }

    public static function getTempleOrigin(int $seed, int $chunkX, int $chunkZ) : array{
        $random = new Random(0);
        $random->setSeed($seed ^ Level::chunkHash($chunkX, $chunkZ));
        return [
            "x" => ($chunkX << 4) + $random->nextBoundedInt(15) + self::PNX_LOCAL_CENTER_OFFSET,
            "z" => ($chunkZ << 4) + $random->nextBoundedInt(15) + self::PNX_LOCAL_CENTER_OFFSET,
        ];
    }

    public static function getTempleFootprintChunks(int $seed, int $chunkX, int $chunkZ) : array{
        $origin = self::getTempleOrigin($seed, $chunkX, $chunkZ);
        return self::getTempleFootprintChunksFromOrigin($origin["x"], $origin["z"]);
    }

    public static function getPopulationRadiusForChunk(int $seed, int $chunkX, int $chunkZ) : int{
        $candidate = self::findRegionTempleCandidate(
            $seed,
            self::floorDiv($chunkX, self::REGION_SIZE),
            self::floorDiv($chunkZ, self::REGION_SIZE)
        );
        if((int) $candidate["chunkX"] !== $chunkX || (int) $candidate["chunkZ"] !== $chunkZ){
            return 1;
        }

        return self::getFootprintPopulationRadius(self::getTempleFootprintChunks($seed, $chunkX, $chunkZ), $chunkX, $chunkZ);
    }

    public static function getTempleFootprintChunksFromOrigin(int $originX, int $originZ) : array{
        $chunks = [];
        $minChunkX = ($originX - 10) >> 4;
        $maxChunkX = ($originX + 10) >> 4;
        $minChunkZ = ($originZ - 10) >> 4;
        $maxChunkZ = ($originZ + 10) >> 4;
        for($chunkX = $minChunkX; $chunkX <= $maxChunkX; ++$chunkX){
            for($chunkZ = $minChunkZ; $chunkZ <= $maxChunkZ; ++$chunkZ){
                $chunks[] = ["chunkX" => $chunkX, "chunkZ" => $chunkZ];
            }
        }

        return $chunks;
    }

    public static function isDesertTempleBiome($biomeId) : bool{
        $biomeId = (int) $biomeId;
        return $biomeId === Biome::DESERT || $biomeId === Biome::DESERT_HILLS;
    }

    public static function floorDiv(int $value, int $divisor) : int{
        $result = intdiv($value, $divisor);
        if($value < 0 && ($value % $divisor) !== 0){
            --$result;
        }

        return $result;
    }

    private static function getTemplePlacement(int $originX, int $originY, int $originZ) : array{
        return [
            "originX" => $originX,
            "originY" => $originY,
            "originZ" => $originZ,
            "targetX" => $originX,
            "targetY" => min(126, $originY + 13),
            "targetZ" => $originZ,
        ];
    }

    private function findExistingTemple(ChunkManager $level, int $originX, int $originZ){
        for($originY = 12; $originY <= 112; ++$originY){
            if(
                $level->getBlockIdAt($originX, $originY, $originZ) === Block::STAINED_HARDENED_CLAY &&
                $level->getBlockIdAt($originX, $originY - 11, $originZ + 2) === Block::CHEST &&
                $level->getBlockIdAt($originX, $originY - 11, $originZ - 2) === Block::CHEST &&
                $level->getBlockIdAt($originX + 2, $originY - 11, $originZ) === Block::CHEST &&
                $level->getBlockIdAt($originX - 2, $originY - 11, $originZ) === Block::CHEST
            ){
                return self::getTemplePlacement($originX, $originY, $originZ);
            }
        }

        return null;
    }

    private function processLiveLevelFootprint(ChunkManager $level, array $chunks){
        if(!($level instanceof Level)){
            return;
        }

        foreach($chunks as $chunkPos){
            $chunk = $level->getChunk((int) $chunkPos["chunkX"], (int) $chunkPos["chunkZ"], false);
            if($chunk !== null && method_exists($level, "processDeferredStructureContainers")){
                $level->processDeferredStructureContainers($chunk);
            }
        }

        if(method_exists($level, "finalizeStructureFootprintChunks")){
            $level->finalizeStructureFootprintChunks($chunks);
        }
    }

    private function markStructureFootprint(ChunkManager $level, array $chunks){
        if(method_exists($level, "markStructureFootprintChunks")){
            $level->markStructureFootprintChunks($chunks);
        }
    }

    private static function getFootprintPopulationRadius(array $chunks, int $centerChunkX, int $centerChunkZ) : int{
        $radius = 1;
        foreach($chunks as $chunkPos){
            $radius = max(
                $radius,
                abs((int) $chunkPos["chunkX"] - $centerChunkX),
                abs((int) $chunkPos["chunkZ"] - $centerChunkZ)
            );
        }
        return $radius;
    }

    protected function getTempleBaseY($x, $z){
        $surface = [];
        for($xx = $x - 10; $xx <= $x + 10; $xx += 5){
            for($zz = $z - 10; $zz <= $z + 10; $zz += 5){
                $y = $this->getHighestWorkableBlock($xx, $zz) - 1;
                if($y <= 0){
                    return -1;
                }
                $surface[] = $y;
            }
        }

        sort($surface);
        return $surface[(int) floor(count($surface) / 2)] + 1;
    }

    protected function getHighestWorkableBlock($x, $z){
        for ($y = 127; $y > 0; --$y) {
            $b = $this->level->getBlockIdAt($x, $y, $z);
            if ($b === Block::SAND || $b === Block::SANDSTONE) {
                break;
            }
        }

        return ++$y;
    }

}
