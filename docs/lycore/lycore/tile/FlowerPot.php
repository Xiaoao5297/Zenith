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

namespace lycore\tile;

use lycore\block\Block;
use lycore\item\Item;
use lycore\level\format\FullChunk;
use lycore\nbt\tag\CompoundTag;
use lycore\nbt\tag\IntTag;
use lycore\nbt\tag\ShortTag;
use lycore\nbt\tag\StringTag;

class FlowerPot extends Spawnable{

	public function __construct(FullChunk $chunk, CompoundTag $nbt){
        parent::__construct($chunk, $nbt);
		if(!isset($nbt->item)){
			$nbt->item = new ShortTag("item", 0);
		}
		if(!isset($nbt->mData)){
			$nbt->mData = new IntTag("mData", 0);
		}
	}

	public function getFlowerPotItem(){
		return $this->namedtag["item"];
	}

	public function getFlowerPotData(){
		return $this->namedtag["mData"];
	}

	public function setFlowerPotData($item, $data){
		$this->namedtag->item = new ShortTag("item", (int) $item);
		$this->namedtag->mData = new IntTag("mData", (int) $data);
		$this->spawnToAll();
		if($this->chunk){
			$this->chunk->setChanged();
			$this->level->clearChunkCache($this->chunk->getX(), $this->chunk->getZ());
		}
		return true;
	}

	public function getSpawnCompound(){
				switch($this->namedtag["item"]){
					case Item::TALL_GRASS:
					case Item::SAPLING:
					case Item::DEAD_BUSH:
					case Item::DANDELION:
					case Item::RED_FLOWER:
					case Item::BROWN_MUSHROOM:
					case Item::RED_MUSHROOM:
					case Item::CACTUS:
						break;
					default:
						$this->namedtag["item"] = 0;
				}
		return new CompoundTag("", [
			new StringTag("id", Tile::FLOWER_POT),
			new IntTag("x", (int) $this->x),
			new IntTag("y", (int) $this->y),
			new IntTag("z", (int) $this->z),
			new ShortTag("item", (int) $this->namedtag["item"]),
			new IntTag("mData", (int) $this->namedtag["mData"])
		]);
	}
}
