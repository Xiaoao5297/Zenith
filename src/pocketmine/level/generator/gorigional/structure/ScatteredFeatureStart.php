<?php

namespace pocketmine\level\generator\gorigional\structure;

use pocketmine\level\generator\gorigional\JavaRandom;

/**
 * 移植自 SCAXE-GO-CE structure.ScatteredFeatureStart。
 */
class ScatteredFeatureStart extends StructureStart{

	public function __construct($seed, JavaRandom $rnd, $chunkX, $chunkZ, $biomeID){
		parent::__construct($chunkX, $chunkZ);
		$this->createComponents($rnd, $chunkX, $chunkZ, $biomeID);
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
			case 2:
			case 17:
				$this->components[] = new DesertPyramid($rnd, $x, $z);
				break;
			case 12:
			case 30:
				break;
			default:
				$this->components[] = new DesertPyramid($rnd, $x, $z);
				break;
		}
	}
}
