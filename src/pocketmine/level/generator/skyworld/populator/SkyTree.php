<?php

namespace pocketmine\level\generator\skyworld\populator;

use pocketmine\block\Block;
use pocketmine\level\ChunkManager;
use pocketmine\level\generator\populator\Populator;
use pocketmine\utils\Random;

class SkyTree extends Populator
{
    /** @var ChunkManager */
    private $level;
    private $randomAmount;
    private $baseAmount;

    public function setRandomAmount($amount)
    {
        $this->randomAmount = $amount;
    }

    public function setBaseAmount($amount)
    {
        $this->baseAmount = $amount;
    }

    public function populate(ChunkManager $level, $chunkX, $chunkZ, Random $random)
    {
        if (mt_rand(0, 100) < 10) {
            $this->level = $level;
            $amount = $random->nextRange(0, $this->randomAmount + 1) + $this->baseAmount;
            for ($i = 0; $i < $amount; ++$i) {
                $x = $random->nextRange($chunkX * 16, $chunkX * 16 + 15);
                $z = $random->nextRange($chunkZ * 16, $chunkZ * 16 + 15);
                $y = $this->getHighestWorkableBlock($x, $z);
                if ($this->level->getBlockIdAt($x, $y, $z) == Block::GRASS) {
                    $level->setBlockIdAt($x, $y, $z, 17);
                    $level->setBlockIdAt($x, $y + 1, $z, 17);
                    $level->setBlockIdAt($x, $y + 2, $z, 17);
                    $level->setBlockIdAt($x, $y + 3, $z, 17);
                    $level->setBlockIdAt($x, $y + 4, $z, 17);
                    $level->setBlockIdAt($x, $y + 5, $z, 17);
                    $level->setBlockIdAt($x, $y + 6, $z, 17);
                    $level->setBlockIdAt($x, $y + 7, $z, 17);
                    $level->setBlockIdAt($x, $y + 8, $z, 18);
                    $level->setBlockIdAt($x + 1, $y + 8, $z, 18);
                    $level->setBlockIdAt($x - 1, $y + 8, $z, 18);
                    $level->setBlockIdAt($x, $y + 8, $z + 1, 18);
                    $level->setBlockIdAt($x, $y + 8, $z - 1, 18);
                    $level->setBlockIdAt($x + 1, $y + 8, $z - 1, 18);
                    $level->setBlockIdAt($x - 1, $y + 8, $z - 1, 18);
                    $level->setBlockIdAt($x - 1, $y + 8, $z + 1, 18);
                    $level->setBlockIdAt($x + 1, $y + 8, $z + 1, 18);
					$level->setBlockIdAt($x + 1, $y + 7, $z, 18);
                    $level->setBlockIdAt($x - 1, $y + 7, $z, 18);
                    $level->setBlockIdAt($x, $y + 7, $z + 1, 18);
                    $level->setBlockIdAt($x, $y + 7, $z - 1, 18);
                    $level->setBlockIdAt($x + 1, $y + 7, $z - 1, 18);
                    $level->setBlockIdAt($x - 1, $y + 7, $z - 1, 18);
                    $level->setBlockIdAt($x - 1, $y + 7, $z + 1, 18);
                    $level->setBlockIdAt($x + 1, $y + 7, $z + 1, 18);
                    $level->setBlockIdAt($x + 1, $y + 6, $z, 18);
                    $level->setBlockIdAt($x - 1, $y + 6, $z, 18);
                    $level->setBlockIdAt($x, $y + 6, $z + 1, 18);
                    $level->setBlockIdAt($x, $y + 6, $z - 1, 18);
                    $level->setBlockIdAt($x + 1, $y + 6, $z - 1, 18);
                    $level->setBlockIdAt($x - 1, $y + 6, $z - 1, 18);
                    $level->setBlockIdAt($x - 1, $y + 6, $z + 1, 18);
                    $level->setBlockIdAt($x + 1, $y + 6, $z + 1, 18);
                }
            }
        }
    }


    private function getHighestWorkableBlock($x, $z)
    {
        for ($y = 127; $y >= 0; --$y) {
            $b = $this->level->getBlockIdAt($x, $y, $z);
            if ($b == Block::GRASS) {
                break;
            }
        }
        return $y === 0 ? -1 : $y;
    }
}
