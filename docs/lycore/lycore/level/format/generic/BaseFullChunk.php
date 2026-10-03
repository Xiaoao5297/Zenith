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

namespace lycore\level\format\generic;

use lycore\block\Block;
use lycore\entity\Entity;
use lycore\level\format\FullChunk;
use lycore\level\format\LevelProvider;
use lycore\level\generator\biome\Biome;
use lycore\level\Level;
use lycore\math\Vector3;
use lycore\nbt\tag\CompoundTag;
use lycore\Player;
use lycore\tile\Tile;


abstract class BaseFullChunk implements FullChunk{

	/** @var Entity[] */
	protected $entities = [];

	/** @var Tile[] */
	protected $tiles = [];

	/** @var Tile[] */
	protected $tileList = [];

	/** @var int[256] */
	protected $biomeColors;

	protected $blocks;

	protected $data;

	protected $skyLight;

	protected $blockLight;

	protected $heightMap;

	protected $NBTtiles;

	protected $NBTentities;

	protected $extraData = [];

	/** @var LevelProvider */
	protected $provider;

	protected $x;
	protected $z;

	protected $hasChanged = false;

	private $isInit = false;

	/**
	 * @param LevelProvider $provider
	 * @param int           $x
	 * @param int           $z
	 * @param string        $blocks
	 * @param string        $data
	 * @param string        $skyLight
	 * @param string        $blockLight
	 * @param int[]         $biomeColors
	 * @param int[]         $heightMap
	 * @param CompoundTag[]    $entities
	 * @param CompoundTag[]    $tiles
	 */
	protected function __construct($provider, $x, $z, $blocks, $data, $skyLight, $blockLight, array $biomeColors = [], array $heightMap = [], array $entities = [], array $tiles = [], array $extraData = []){
		$this->provider = $provider;
		$this->x = (int) $x;
		$this->z = (int) $z;

		$this->blocks = $blocks;
		$this->data = $data;
		$this->skyLight = $skyLight;
		$this->blockLight = $blockLight;

		if(count($biomeColors) === 256){
			$this->biomeColors = $biomeColors;
		}else{
			$this->biomeColors = array_fill(0, 256, 0);
		}

		if(count($heightMap) === 256){
			$this->heightMap = $heightMap;
		}else{
			$this->heightMap = array_fill(0, 256, 127);
		}

		$this->extraData = $extraData;

		$this->NBTtiles = $tiles;
		$this->NBTentities = $entities;
	}

	protected function checkOldBiomes($data){
		if(strlen($data) !== 256){
			return;
		}

		for($x = 0; $x < 16; ++$x){
			for($z = 0; $z < 16; ++$z){
				$biome = Biome::getBiome(ord($data[($z << 4) + $x]));
				$this->setBiomeId($x, $z, $biome->getId());
				$c = $biome->getColor();
				$this->setBiomeColor($x, $z, $c >> 16, ($c >> 8) & 0xff, $c & 0xff);
			}
		}
	}

	public function initChunk(){
		if($this->getProvider() instanceof LevelProvider and !$this->isInit){
			$changed = false;
			if($this->NBTentities !== null){
				$this->getProvider()->getLevel()->timings->syncChunkLoadEntitiesTimer->startTiming();
				foreach($this->NBTentities as $nbt){
					if($nbt instanceof CompoundTag){
						if(!isset($nbt->id)){
							$this->setChanged();
							continue;
						}

						if(($nbt["Pos"][0] >> 4) !== $this->x or ($nbt["Pos"][2] >> 4) !== $this->z){
							$changed = true;
							continue; //Fixes entities allocated in wrong chunks.
						}

						if(($entity = Entity::createEntity($nbt["id"], $this, $nbt)) instanceof Entity){
							$entity->spawnToAll();
						}else{
							$changed = true;
							continue;
						}
					}
				}
				$this->getProvider()->getLevel()->timings->syncChunkLoadEntitiesTimer->stopTiming();

				$this->getProvider()->getLevel()->timings->syncChunkLoadTileEntitiesTimer->startTiming();
				foreach($this->NBTtiles as $nbt){
					if($nbt instanceof CompoundTag){
						if(!isset($nbt->id)){
							$changed = true;
							continue;
						}

						if(($nbt["x"] >> 4) !== $this->x or ($nbt["z"] >> 4) !== $this->z){
							$changed = true;
							continue; //Fixes tiles allocated in wrong chunks.
						}

						if(Tile::createTile($nbt["id"], $this, $nbt) === null){
							$changed = true;
							continue;
						}
					}
				}

				$this->getProvider()->getLevel()->timings->syncChunkLoadTileEntitiesTimer->stopTiming();

				$this->NBTentities = null;
				$this->NBTtiles = null;
			}

			$this->setChanged($changed);

			$this->isInit = true;
		}
	}

	public function getX(){
		return $this->x;
	}

	public function getZ(){
		return $this->z;
	}

	public function setX($x){
		$this->x = $x;
	}

	public function setZ($z){
		$this->z = $z;
	}

	/**
	 * @return LevelProvider
	 *
	 * @deprecated
	 */
	public function getLevel(){
		return $this->getProvider();
	}

	/**
	 * @return LevelProvider
	 */
	public function getProvider(){
		return $this->provider;
	}

	public function setProvider(LevelProvider $provider){
		$this->provider = $provider;
	}

	public function getBiomeId($x, $z){
		return ($this->biomeColors[($z << 4) + $x] & 0xFF000000) >> 24;
	}

	public function setBiomeId($x, $z, $biomeId){
		$this->hasChanged = true;
		$this->biomeColors[($z << 4) + $x] = ($this->biomeColors[($z << 4) + $x] & 0xFFFFFF) | ($biomeId << 24);
	}

	public function getBiomeColor($x, $z){
		$color = $this->biomeColors[($z << 4) + $x] & 0xFFFFFF;

		return [$color >> 16, ($color >> 8) & 0xFF, $color & 0xFF];
	}

	public function setBiomeColor($x, $z, $R, $G, $B){
		$this->hasChanged = true;
		$this->biomeColors[($z << 4) + $x] = ($this->biomeColors[($z << 4) + $x] & 0xFF000000) | (($R & 0xFF) << 16) | (($G & 0xFF) << 8) | ($B & 0xFF);
	}

	public function getHeightMap($x, $z){
		return $this->heightMap[($z << 4) + $x];
	}

	public function setHeightMap($x, $z, $value){
		$this->heightMap[($z << 4) + $x] = $value;
	}

	public function recalculateHeightMap(){
		for($z = 0; $z < 16; ++$z){
			for($x = 0; $x < 16; ++$x){
				$this->recalculateHeightMapColumn($x, $z);
			}
		}
	}

	public function recalculateHeightMapColumn($x, $z){
		$height = $this->getHighestBlockAt($x, $z, false);
		$this->setHeightMap($x, $z, $height);
		return $height;
	}

	public function getBlockExtraData($x, $y, $z){
		if(isset($this->extraData[$index = Level::chunkBlockHash($x, $y, $z)])){
			return $this->extraData[$index];
		}

		return 0;
	}

	public function setBlockExtraData($x, $y, $z, $data){
		if($data === 0){
			unset($this->extraData[Level::chunkBlockHash($x, $y, $z)]);
		}else{
			$this->extraData[Level::chunkBlockHash($x, $y, $z)] = $data;
		}

		$this->setChanged(true);
	}

	public function populateSkyLight(){
		for($z = 0; $z < 16; ++$z){
			for($x = 0; $x < 16; ++$x){
				$light = 15;
				for($y = 127; $y >= 0; --$y){
					$blockId = $this->getBlockId($x, $y, $z);
					$light -= self::getVerticalSkyLightReduction($blockId);
					if($light < 0){
						$light = 0;
					}
					$this->setBlockSkyLightIfChanged($x, $y, $z, $light);
				}

				$this->setHeightMap($x, $z, $this->getHighestBlockAt($x, $z, false));
			}
		}
	}

	public function populateBlockLight(){
		$lightPropagationQueue = new \SplQueue();

		for($y = 0; $y < 128; ++$y){
			for($z = 0; $z < 16; ++$z){
				for($x = 0; $x < 16; ++$x){
					if($this->getBlockLight($x, $y, $z) !== 0){
						$this->setBlockLight($x, $y, $z, 0);
					}
				}
			}
		}

		for($y = 0; $y < 128; ++$y){
			for($z = 0; $z < 16; ++$z){
				for($x = 0; $x < 16; ++$x){
					$light = (int) Block::$light[$this->getBlockId($x, $y, $z)];
					if($light > 0){
						$this->setBlockLight($x, $y, $z, $light);
						$lightPropagationQueue->enqueue(new Vector3($x, $y, $z));
					}
				}
			}
		}

		$visited = [];
		while(!$lightPropagationQueue->isEmpty()){
			/** @var Vector3 $node */
			$node = $lightPropagationQueue->dequeue();
			$current = $this->getBlockLight($node->x, $node->y, $node->z);
			if($current <= 1){
				continue;
			}

			$this->spreadChunkBlockLight($node->x - 1, $node->y, $node->z, $current, $lightPropagationQueue, $visited);
			$this->spreadChunkBlockLight($node->x + 1, $node->y, $node->z, $current, $lightPropagationQueue, $visited);
			$this->spreadChunkBlockLight($node->x, $node->y - 1, $node->z, $current, $lightPropagationQueue, $visited);
			$this->spreadChunkBlockLight($node->x, $node->y + 1, $node->z, $current, $lightPropagationQueue, $visited);
			$this->spreadChunkBlockLight($node->x, $node->y, $node->z - 1, $current, $lightPropagationQueue, $visited);
			$this->spreadChunkBlockLight($node->x, $node->y, $node->z + 1, $current, $lightPropagationQueue, $visited);
		}
	}

	private function setBlockSkyLightIfChanged($x, $y, $z, $light){
		if($this->getBlockSkyLight($x, $y, $z) !== $light){
			$this->setBlockSkyLight($x, $y, $z, $light);
		}
	}

	private function spreadChunkSkyLight($x, $y, $z, $currentLight, \SplQueue $queue, array &$visited){
		if($x < 0 or $x >= 16 or $z < 0 or $z >= 16 or $y < 0 or $y >= 128){
			return;
		}

		$light = $currentLight - self::getSpreadLightReduction($this->getBlockId($x, $y, $z));
		if($light <= $this->getBlockSkyLight($x, $y, $z)){
			return;
		}

		$this->setBlockSkyLight($x, $y, $z, $light);
		if($light > 1 and !isset($visited[$index = Level::blockHash($x, $y, $z)])){
			$visited[$index] = true;
			$queue->enqueue(new Vector3($x, $y, $z));
		}
	}

	private function spreadChunkBlockLight($x, $y, $z, $currentLight, \SplQueue $queue, array &$visited){
		if($x < 0 or $x >= 16 or $z < 0 or $z >= 16 or $y < 0 or $y >= 128){
			return;
		}

		$light = $currentLight - self::getSpreadLightReduction($this->getBlockId($x, $y, $z));
		if($light <= $this->getBlockLight($x, $y, $z)){
			return;
		}

		$this->setBlockLight($x, $y, $z, $light);
		if($light > 1 and !isset($visited[$index = Level::blockHash($x, $y, $z)])){
			$visited[$index] = true;
			$queue->enqueue(new Vector3($x, $y, $z));
		}
	}

	private static function getVerticalSkyLightReduction($blockId) : int{
		if($blockId === Block::AIR){
			return 0;
		}

		return self::getSpreadLightReduction($blockId);
	}

	private static function canSpreadSkyLightFrom($blockId) : bool{
		return $blockId === Block::AIR || ((bool) Block::$transparent[$blockId] && self::getSpreadLightReduction($blockId) < 15);
	}

	private static function getSpreadLightReduction($blockId) : int{
		return max(1, min(15, (int) Block::$lightFilter[$blockId]));
	}

	public function getHighestBlockAt($x, $z, $cache = true){
		if($cache){
			$h = $this->getHeightMap($x, $z);

			if($h !== 0 and $h !== 127){
				return $h;
			}
		}

		$column = $this->getBlockIdColumn($x, $z);
		for($y = 127; $y >= 0; --$y){
			if($column[$y] !== "\x00"){
				$this->setHeightMap($x, $z, $y);
				return $y;
			}
		}

		return 0;
	}

	public function addEntity(Entity $entity){
		$this->entities[$entity->getId()] = $entity;
		if(!($entity instanceof Player) and $this->isInit){
			$this->hasChanged = true;
		}
	}

	public function removeEntity(Entity $entity){
		unset($this->entities[$entity->getId()]);
		if(!($entity instanceof Player) and $this->isInit){
			$this->hasChanged = true;
		}
	}

	public function addTile(Tile $tile){
		$this->tiles[$tile->getId()] = $tile;
		if(isset($this->tileList[$index = (($tile->z & 0x0f) << 12) | (($tile->x & 0x0f) << 8) | ($tile->y & 0xff)]) and $this->tileList[$index] !== $tile){
			$this->tileList[$index]->close();
		}
		$this->tileList[$index] = $tile;
		if($this->isInit){
			$this->hasChanged = true;
		}
	}

	public function removeTile(Tile $tile){
		unset($this->tiles[$tile->getId()]);
		unset($this->tileList[(($tile->z & 0x0f) << 12) | (($tile->x & 0x0f) << 8) | ($tile->y & 0xff)]);
		if($this->isInit){
			$this->hasChanged = true;
		}
	}

	public function getEntities(){
		return $this->entities;
	}

	public function getTiles(){
		return $this->tiles;
	}

	public function getBlockExtraDataArray(){
		return $this->extraData;
	}

	public function getTile($x, $y, $z){
		$index = ($z << 12) | ($x << 8) | $y;
		return isset($this->tileList[$index]) ? $this->tileList[$index] : null;
	}

	public function isLoaded(){
		return $this->getProvider() === null ? false : $this->getProvider()->isChunkLoaded($this->getX(), $this->getZ());
	}

	public function load($generate = true){
		return $this->getProvider() === null ? false : $this->getProvider()->getChunk($this->getX(), $this->getZ(), true) instanceof FullChunk;
	}

	public function unload($save = true, $safe = true){
		$level = $this->getProvider();
		if($level === null){
			return true;
		}
		if($save === true and $this->hasChanged){
			$level->saveChunk($this->getX(), $this->getZ());
		}
		if($safe === true){
			foreach($this->getEntities() as $entity){
				if($entity instanceof Player){
					return false;
				}
			}
		}

		foreach($this->getEntities() as $entity){
			if($entity instanceof Player){
				continue;
			}
			$entity->close();
		}
		foreach($this->getTiles() as $tile){
			$tile->close();
		}
		$this->provider = null;
		return true;
	}

	public function getBlockIdArray(){
		return $this->blocks;
	}

	public function getBlockDataArray(){
		return $this->data;
	}

	public function getBlockSkyLightArray(){
		return $this->skyLight;
	}

	public function getBlockLightArray(){
		return $this->blockLight;
	}

	public function getBiomeIdArray(){
		$ids = "";
		foreach($this->biomeColors as $d){
			$ids .= chr(($d & 0xFF000000) >> 24);
		}
		return $ids;
	}

	public function getBiomeColorArray(){
		return $this->biomeColors;
	}

	public function getHeightMapArray(){
		return $this->heightMap;
	}

	public function hasChanged(){
		return $this->hasChanged;
	}

	public function setChanged($changed = true){
		$this->hasChanged = (bool) $changed;
	}

	public static function fromFastBinary($data, LevelProvider $provider = null){
		return static::fromBinary($data, $provider);
	}

	public function toFastBinary(){
		return $this->toBinary();
	}

	public function isLightPopulated(){
		return true;
	}

	public function setLightPopulated($value = 1){

	}

}
