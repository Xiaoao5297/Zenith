<?php

namespace pocketmine\level\generator\skyworld;

use pocketmine\block\Block;
use pocketmine\level\ChunkManager;
use pocketmine\level\generator\biome\Biome;
use pocketmine\level\generator\biome\BiomeSelector;
use pocketmine\level\generator\Generator;
use pocketmine\level\generator\noise\Simplex;
use pocketmine\level\generator\populator\Populator;
use pocketmine\level\generator\populator\Ore;
use pocketmine\level\generator\object\OreType;
use pocketmine\block\CoalOre;
use pocketmine\block\IronOre;
use pocketmine\block\GoldOre;
use pocketmine\block\DiamondOre;
use pocketmine\block\RedstoneOre;
use pocketmine\block\LapisOre;
use pocketmine\block\EmeraldOre;
use pocketmine\block\Stone;
use pocketmine\block\Dirt;
use pocketmine\block\Gravel;
use pocketmine\math\Vector3 as Vector3;
use pocketmine\utils\Random;

class Skyworld extends Generator
{
    /** @var Populator[] */
    private $populators = [];
    private $level;
    private $random;
    private $waterHeight = 0;
    
    // 空岛核心参数（可根据喜好微调）
    private $emptyHeight = 48; 
    private $emptyAmplitude = 1.2;
    private $density = 0.55;

    /** @var Simplex */
    private $noiseBase;
    /** @var BiomeSelector */
    private $selector;

    public function __construct(array $options = []){}

    public function getName(): string { return "Sky World"; }
    public function getWaterHeight(): int { return $this->waterHeight; }

    public function init(ChunkManager $level, Random $random)
    {
        $this->level = $level;
        $this->random = $random;
        $this->random->setSeed($this->level->getSeed());
        
        $this->noiseBase = new Simplex($this->random, 4, 1 / 4, 1 / 64);

        // 1. 初始化群系选择器（返回 ID）
        $this->selector = new BiomeSelector($this->random, function($temp, $rain){
            if($temp < 0.3) return Biome::ICE_PLAINS;
            if($temp > 0.8) return Biome::DESERT;
            return Biome::PLAINS;
        }, Biome::getBiome(Biome::PLAINS));

        // 注册群系
        $this->selector->addBiome(Biome::getBiome(Biome::PLAINS));
        $this->selector->addBiome(Biome::getBiome(Biome::ICE_PLAINS));
        $this->selector->addBiome(Biome::getBiome(Biome::DESERT));

        // 2. 矿石填充器（矿脉模式生成）
        $ores = new Ore();
        $ores->setOreTypes([
            new OreType(new CoalOre(), 20, 16, 0, 128),
            new OreType(new IronOre(), 20, 9, 0, 64),
            new OreType(new RedstoneOre(), 8, 8, 0, 16),
            new OreType(new LapisOre(), 1, 7, 0, 32),
            new OreType(new GoldOre(), 3, 9, 0, 32),
            new OreType(new DiamondOre(), 1, 8, 0, 16),
            new OreType(new EmeraldOre(), 1, 1, 0, 32),
            new OreType(new Stone(Stone::GRANITE), 10, 33, 0, 128),
            new OreType(new Stone(Stone::DIORITE), 10, 33, 0, 128),
            new OreType(new Stone(Stone::ANDESITE), 10, 33, 0, 128),
            new OreType(new Gravel(), 10, 16, 0, 128)
        ]);
        $this->populators[] = $ores;
    }

    public function generateChunk($chunkX, $chunkZ)
    {
        $this->random->setSeed(0xa6fe78dc ^ ($chunkX << 8) ^ $chunkZ ^ $this->level->getSeed());
        $noise = Generator::getFastNoise3D($this->noiseBase, 16, 128, 16, 4, 8, 4, $chunkX * 16, 0, $chunkZ * 16);
        $chunk = $this->level->getChunk($chunkX, $chunkZ);

        for ($x = 0; $x < 16; ++$x) {
            for ($z = 0; $z < 16; ++$z) {
                // 设置群系
                $biome = $this->selector->pickBiome($chunkX * 16 + $x, $chunkZ * 16 + $z);
                $chunk->setBiomeId($x, $z, $biome->getId());
                
                // --- 硬编码草色修复：(111, 189, 44) 是经典的草绿色 ---
                $chunk->setBiomeColor($x, $z, 111, 189, 44);

                $upperY = -1;
                for ($y = 127; $y >= 0; --$y) {
                    $noiseValue = (abs($this->emptyHeight - $y) / $this->emptyHeight) * $this->emptyAmplitude - $noise[$x][$z][$y];
                    $noiseValue -= 1 - $this->density;

                    // 岛屿分布半径
                    $dist = sqrt(pow($chunkX * 16 + $x, 2) + pow($chunkZ * 16 + $z, 2));

                    if ($noiseValue < 0 && $dist < 600) { 
                        if ($y <= 3) {
                            $chunk->setBlockId($x, $y, $z, Block::BEDROCK);
                        } else {
                            $chunk->setBlockId($x, $y, $z, Block::STONE);
                            if ($upperY === -1) $upperY = $y;
                        }
                    }
                }

                // 地表覆盖：最上面一层草，下面三层泥土
                if ($upperY !== -1) {
                    $chunk->setBlockId($x, $upperY, $z, Block::GRASS);
                    for ($dy = 1; $dy < 4; $dy++) {
                        if ($chunk->getBlockId($x, $upperY - $dy, $z) === Block::STONE) {
                            $chunk->setBlockId($x, $upperY - $dy, $z, Block::DIRT);
                        }
                    }
                }
            }
        }
    }

    public function populateChunk($chunkX, $chunkZ)
    {
        $this->random->setSeed(0xa6fe78dc ^ ($chunkX << 8) ^ $chunkZ ^ $this->level->getSeed());
        
        // 1. 生成矿脉 (成簇生成的关键)
        foreach ($this->populators as $populator) {
            $populator->populate($this->level, $chunkX, $chunkZ, $this->random);
        }

        // 2. 生成植被（树木、草花）
        $chunk = $this->level->getChunk($chunkX, $chunkZ);
        $biome = Biome::getBiome($chunk->getBiomeId(7, 7));
        $biome->populateChunk($this->level, $chunkX, $chunkZ, $this->random);
    }

    public function getSpawn() { return new Vector3(0, 100, 0); }
    public function getSettings() { return []; }
}