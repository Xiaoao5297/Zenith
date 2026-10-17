<?php

namespace pocketmine\level\generator\gorigional\structure;

use pocketmine\level\generator\gorigional\JavaRandom;

/**
 * 移植自 Java 1.7.10 StructureVillagePieces$Start（村庄中心井）。
 */
class VillageWellStart extends VillageWell{
	/** @var WorldChunkManager */
	public $biomeManager;
	/** @var bool */
	public $desertVillage;
	/** @var int */
	public $size;
	/** @var VillagePieceWeight|null */
	public $lastPiece = null;
	/** @var VillagePieceWeight[] */
	public $weights = [];
	/** @var StructureComponent[] */
	public $villagePieces = [];
	/** @var StructureComponent[] */
	public $paths = [];

	public function __construct(WorldChunkManager $biomeManager, $typeId, JavaRandom $rnd, $x, $z, array $weights, $size){
		parent::__construct(null, 0, $rnd, $x, $z);
		$this->biomeManager = $biomeManager;
		$this->weights = $weights;
		$this->size = $size;
		$biome = $biomeManager->getBiome($x, $z);
		$this->desertVillage = ($biome === 2 || $biome === 17);
		$this->isDesert = $this->desertVillage;
	}

	public function removeWeight(VillagePieceWeight $w){
		foreach($this->weights as $k => $existing){
			if($existing === $w){
				unset($this->weights[$k]);
				break;
			}
		}
		$this->weights = array_values($this->weights);
	}
}
