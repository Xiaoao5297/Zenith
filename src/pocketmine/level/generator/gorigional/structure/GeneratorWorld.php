<?php

namespace pocketmine\level\generator\gorigional\structure;

use pocketmine\level\ChunkManager;
use pocketmine\level\generator\gorigional\Int64;
use pocketmine\level\generator\gorigional\JavaRandom;
use pocketmine\level\generator\gorigional\object\ChunkManagerAdapter;

/**
 * 把核心 ChunkManager 适配为结构生成所需的 World。
 */
class GeneratorWorld implements World{
	/** @var ChunkManagerAdapter */
	private $blocks;
	/** @var int */
	private $seed;
	/** @var WorldChunkManager */
	private $biomeManager;
	/** @var JavaRandom */
	private $rand;

	public function __construct(ChunkManager $manager, $seed, WorldChunkManager $biomeManager){
		$this->blocks = new ChunkManagerAdapter($manager);
		$this->seed = $seed;
		$this->biomeManager = $biomeManager;
		$this->rand = new JavaRandom($seed);
	}

	public function getSeed(){
		return $this->seed;
	}

	public function getBlockId($x, $y, $z){
		return $this->blocks->getBlockId($x, $y, $z);
	}

	public function setBlock($x, $y, $z, $id, $meta){
		$this->blocks->setBlock($x, $y, $z, $id, $meta);
	}

	public function getHeight($x, $z){
		return $this->blocks->getHeight($x, $z);
	}

	public function getBiome($x, $z){
		return $this->biomeManager->getBiome($x, $z);
	}

	public function getBiomeManager(){
		return $this->biomeManager;
	}

	public function findBiomePosition($x, $z, $radius, array $allowedIDs, JavaRandom $random){
		return $this->biomeManager->findBiomePosition($x, $z, $radius, $allowedIDs, $random);
	}

	public function getRandomAt($x, $z, $salt){
		$seed = Int64::add(Int64::mul($x, 341873128712), Int64::mul($z, 132897987541));
		$seed = Int64::add($seed, $this->seed);
		$seed = Int64::add($seed, $salt);
		$this->rand->setSeed($seed);
		return $this->rand;
	}
}
