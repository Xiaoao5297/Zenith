<?php

namespace pocketmine\level\generator\gorigional\structure;

use pocketmine\level\generator\gorigional\JavaRandom;

/**
 * 移植自 Java 1.7.10 MapGenScatteredFeature$Start。
 */
class ScatteredFeatureStart extends StructureStart{

	public function __construct(World $world, JavaRandom $rand, $chunkX, $chunkZ){
		parent::__construct($chunkX, $chunkZ);

		$biomeID = $world->getBiome($chunkX * 16 + 8, $chunkZ * 16 + 8);
		$this->createComponents($rand, $chunkX, $chunkZ, $biomeID);
		$this->updateBoundingBox();
	}

	private function createComponents(JavaRandom $rnd, $chunkX, $chunkZ, $biomeID){
		$x = $chunkX * 16;
		$z = $chunkZ * 16;

		switch($biomeID){
			case 21:
			case 22:
				$this->components[] = new JunglePyramid($rnd, $x, $z);
				break;
			case 6:
				$this->components[] = new SwampHut($rnd, $x, $z);
				break;
			default:
				$this->components[] = new DesertPyramid($rnd, $x, $z);
				break;
		}
	}
}
