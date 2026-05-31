<?php

namespace pocketmine\level\generator\skyworld;

use pocketmine\level\generator\biome\Biome;

class SkyBiome extends Biome
{

    public function getName(): string
    {
        return "Sky";
    }

    public function getColor()
    {
        return 0;
    }

    public function __construct()
    {

    }
}
