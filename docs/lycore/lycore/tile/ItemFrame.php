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

use lycore\item\Item;
use lycore\level\format\FullChunk;
use lycore\nbt\NBT;
use lycore\nbt\tag\ByteTag;
use lycore\nbt\tag\CompoundTag;
use lycore\nbt\tag\FloatTag;
use lycore\nbt\tag\IntTag;
use lycore\nbt\tag\StringTag;

class ItemFrame extends Spawnable{

	public function __construct(FullChunk $chunk, CompoundTag $nbt){
		if(!isset($nbt->Item)){
			$nbt->Item = NBT::putItemHelper(Item::get(Item::AIR));
			$nbt->Item->setName("Item");
		}
		if(!isset($nbt->ItemRotation)){
			$nbt->ItemRotation = new ByteTag("ItemRotation", 0);
		}
		if(!isset($nbt->ItemDropChance)){
			$nbt->ItemDropChance = new FloatTag("ItemDropChance", 1.0);
		}

		parent::__construct($chunk, $nbt);
	}

	public function getName() : string{
		return "Item Frame";
	}

	public function getItemRotation(){
		return (int) $this->namedtag["ItemRotation"];
	}

	public function setItemRotation(int $itemRotation){
		$this->namedtag->ItemRotation = new ByteTag("ItemRotation", $itemRotation);
		$this->setChanged();
	}

	public function getItem(){
		return NBT::getItemHelper($this->namedtag->Item);
	}

	private function getMapIdFromItem(Item $item){
		if($item->getId() !== Item::FILLED_MAP){
			return null;
		}

		if($item->hasCompoundTag()){
			$tag = $item->getNamedTag();
			if($tag instanceof CompoundTag){
				if(isset($tag->map_uuid)){
					return (string) $tag->map_uuid->getValue();
				}
				if(isset($tag->Map_UUID)){
					return (string) $tag->Map_UUID->getValue();
				}
			}
		}

		$damage = (int) $item->getDamage();
		return $damage > 0 ? (string) $damage : null;
	}

	private function syncMapIdTag(Item $item){
		$mapId = $this->getMapIdFromItem($item);
		if($mapId !== null and $mapId !== ""){
			$this->namedtag->map_uuid = new StringTag("map_uuid", (string) $mapId);
		}else{
			unset($this->namedtag->map_uuid, $this->namedtag->Map_UUID);
		}
	}

	public function setItem(Item $item, bool $setChanged = true){
		$nbtItem = NBT::putItemHelper($item);
		$nbtItem->setName("Item");
		$this->namedtag->Item = $nbtItem;
		$this->syncMapIdTag($item);
		if($setChanged){
			$this->setChanged();
		}
	}

	public function getItemDropChance(){
		return (float) $this->namedtag["ItemDropChance"];
	}

	public function setItemDropChance($chance = 1.0){
		$this->namedtag->ItemDropChance = new FloatTag("ItemDropChance", (float) $chance);
		$this->setChanged();
	}

	private function setChanged(){
		$this->spawnToAll();
		if($this->chunk instanceof FullChunk){
			$this->chunk->setChanged();
			$this->level->clearChunkCache($this->chunk->getX(), $this->chunk->getZ());
		}
	}

	public function getSpawnCompound(){
		if(!isset($this->namedtag->Item)){
			$this->setItem(Item::get(Item::AIR), false);
		}

		$nbtItem = clone $this->namedtag->Item;
		$nbtItem->setName("Item");
		$tags = [
			new StringTag("id", Tile::ITEM_FRAME),
			new IntTag("x", (int) $this->x),
			new IntTag("y", (int) $this->y),
			new IntTag("z", (int) $this->z),
			new ByteTag("ItemRotation", (int) $this->getItemRotation()),
			new FloatTag("ItemDropChance", (float) $this->getItemDropChance())
		];

		if((int) $nbtItem["id"] !== Item::AIR){
			$tags[] = $nbtItem;
		}

		$mapId = $this->getMapIdFromItem($this->getItem());
		if($mapId === null and isset($this->namedtag->map_uuid)){
			$mapId = (string) $this->namedtag->map_uuid->getValue();
		}
		if($mapId === null and isset($this->namedtag->Map_UUID)){
			$mapId = (string) $this->namedtag->Map_UUID->getValue();
		}
		if($mapId !== null and $mapId !== ""){
			$tags[] = new StringTag("map_uuid", (string) $mapId);
		}

		return new CompoundTag("", $tags);
	}
}
