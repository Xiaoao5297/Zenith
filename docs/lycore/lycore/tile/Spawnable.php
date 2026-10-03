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

use lycore\level\format\FullChunk;
use lycore\nbt\NBT;
use lycore\nbt\tag\CompoundTag;
use lycore\network\Network;
use lycore\network\protocol\BlockEntityDataPacket;
use lycore\network\protocol\ProtocolCompatibility;
use lycore\Player;

abstract class Spawnable extends Tile{

	public function spawnTo(Player $player){
		if($this->closed){
			return false;
		}

		if(ProtocolCompatibility::isHiddenTileIdForProtocol((int) $player->getProtocol(), $this->getSaveId())){
			return false;
		}

		if($player->shouldHoldV84BlockEntities()){
			return false;
		}

		$nbt = new NBT(NBT::LITTLE_ENDIAN);
		$nbt->setData($this->getSpawnCompound());
		$pk = new BlockEntityDataPacket();
		$pk->x = $this->x;
		$pk->y = $this->y;
		$pk->z = $this->z;
		$pk->namedtag = $nbt->write();
		$player->dataPacket($pk);

		return true;
	}

	/**
	 * @return CompoundTag
	 */
	public abstract function getSpawnCompound();

	public function __construct(FullChunk $chunk, CompoundTag $nbt){
		parent::__construct($chunk, $nbt);
		$this->spawnToAll();
	}

	public function spawnToAll(){
		if($this->closed){
			return;
		}

		foreach($this->getLevel()->getChunkPlayers($this->chunk->getX(), $this->chunk->getZ()) as $player){
			if($player->spawned === true){
				$this->spawnTo($player);
			}
		}
	}
}
