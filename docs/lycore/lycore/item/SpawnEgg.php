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

namespace lycore\item;

use lycore\block\Block;
use lycore\entity\AgeableSpawnHelper;
use lycore\entity\Entity;
use lycore\level\format\FullChunk;
use lycore\level\Level;
use lycore\nbt\tag\CompoundTag;
use lycore\nbt\tag\DoubleTag;
use lycore\nbt\tag\ListTag;
use lycore\nbt\tag\FloatTag;
use lycore\nbt\tag\StringTag;
use lycore\Player;
use lycore\tile\MobSpawner;

class SpawnEgg extends Item{
	public function __construct($meta = 0, $count = 1){
		parent::__construct(self::SPAWN_EGG, $meta, $count, "Spawn Egg");
	}

	public function canBeActivated() : bool {
		return true;
	}

	public function onActivate(Level $level, Player $player, Block $block, Block $target, $face, $fx, $fy, $fz){
		if($target->getId() == Block::MONSTER_SPAWNER){
			return true;
		}else{
			$entity = null;
			$chunk = $level->getChunk($block->getX() >> 4, $block->getZ() >> 4);

			if(!($chunk instanceof FullChunk)){
				return false;
			}

			$nbt = new CompoundTag("", [
				"Pos" => new ListTag("Pos", [
					new DoubleTag("", $block->getX() + 0.5),
					new DoubleTag("", $block->getY()),
					new DoubleTag("", $block->getZ() + 0.5)
				]),
				"Motion" => new ListTag("Motion", [
					new DoubleTag("", 0),
					new DoubleTag("", 0),
					new DoubleTag("", 0)
				]),
				"Rotation" => new ListTag("Rotation", [
					new FloatTag("", lcg_value() * 360),
					new FloatTag("", 0)
				]),
			]);

			if($this->hasCustomName()){
				$nbt->CustomName = new StringTag("CustomName", $this->getCustomName());
			}

			$entity = Entity::createEntity($this->meta, $chunk, $nbt);
			
			if($this->hasCustomName()){
				$entity->setNameTag($this->getCustomName());
			}
			
			if($entity instanceof Entity){
				if($player->isSurvival()){
					--$this->count;
				}
				AgeableSpawnHelper::maybeSetBaby($entity, AgeableSpawnHelper::PNX_SPAWN_EGG_BABY_CHANCE);
				$entity->spawnToAll();
				return true;
			}
		}

		return false;
	}
}
