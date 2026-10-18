<?php

/***
 *  _____                _  __   __
 * /__  /  ___   ____   (_)/ /_ / /_
 *   / /  / _ \ / __ \ / // __// __ \
 *  / /__/  __// / / // // /_ / / / /
 * /____/\___//_/ /_//_/ \__//_/ /_/
 *
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * @author Xiaoao
 * @link https://github.com/Xiaoao5297/Zenith
 *
 *
*/


namespace pocketmine\level\generator\gorigional\structure;

use pocketmine\level\ChunkManager;
use pocketmine\level\Level;
use pocketmine\level\generator\gorigional\Int64;
use pocketmine\level\generator\gorigional\JavaRandom;
use pocketmine\level\generator\gorigional\object\ChunkManagerAdapter;
use pocketmine\nbt\tag\CompoundTag;
use pocketmine\nbt\tag\IntTag;
use pocketmine\nbt\tag\StringTag;
use pocketmine\tile\Chest;
use pocketmine\tile\MobSpawner;
use pocketmine\tile\Tile;

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
	/** @var array[] */
	private $deferredChests = [];
	/** @var array[] */
	private $deferredSpawners = [];

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

	public function setChestContents($x, $y, $z, array $items){
		$chunk = $this->blocks->getChunkAt($x, $z);
		$provider = $chunk === null ? null : $chunk->getProvider();
		if(!($provider instanceof \pocketmine\level\format\LevelProvider)){
			$this->deferredChests[] = [$x, $y, $z, $items];
			return;
		}
		$level = $provider->getLevel();
		if(!($level instanceof Level)){
			$this->deferredChests[] = [$x, $y, $z, $items];
			return;
		}

		$nbt = new CompoundTag("", [
			"id" => new StringTag("id", Tile::CHEST),
			"x" => new IntTag("x", $x),
			"y" => new IntTag("y", $y),
			"z" => new IntTag("z", $z)
		]);
		$tile = Tile::createTile(Tile::CHEST, $chunk, $nbt);
		if(!($tile instanceof Chest)){
			return;
		}
		foreach($items as $item){
			$tile->setItem($item[0], \pocketmine\item\Item::get($item[1], $item[2], $item[3]));
		}
	}

	public function setSpawnerEntity($x, $y, $z, $entityId){
		$chunk = $this->blocks->getChunkAt($x, $z);
		$provider = $chunk === null ? null : $chunk->getProvider();
		if(!($provider instanceof \pocketmine\level\format\LevelProvider)){
			$this->deferredSpawners[] = [$x, $y, $z, $entityId];
			return;
		}
		$level = $provider->getLevel();
		if(!($level instanceof Level)){
			$this->deferredSpawners[] = [$x, $y, $z, $entityId];
			return;
		}

		$nbt = new CompoundTag("", [
			"id" => new StringTag("id", Tile::MOB_SPAWNER),
			"x" => new IntTag("x", $x),
			"y" => new IntTag("y", $y),
			"z" => new IntTag("z", $z)
		]);
		$tile = Tile::createTile(Tile::MOB_SPAWNER, $chunk, $nbt);
		if($tile instanceof MobSpawner){
			$tile->setEntityId($entityId);
		}
	}

	public function takeDeferredTiles(){
		$result = [$this->deferredChests, $this->deferredSpawners];
		$this->deferredChests = [];
		$this->deferredSpawners = [];
		return $result;
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
