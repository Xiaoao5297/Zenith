<?php

/*
 *  ______   _____    ______  __   __  ______
 * /  ___/  /  ___|  / ___  \ \ \ / / |  ____|
 * | |___  | |      | |___| |  \ / /  | |____
 * \___  \ | |      |  ___  |   / /   |  ____|
 *  ___| | | |____  | |   | |  / / \  | |____
 * /_____/  \_____| |_|   |_| /_/ \_\ |______|
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * @author Sunch233#3226 QQ2125696621 And KKK
 * @link https://github.com/ScaxeTeam/Scaxe/
 *
*/

namespace pocketmine\item;

use pocketmine\block\Block;
use pocketmine\entity\Entity;
use pocketmine\level\format\FullChunk;
use pocketmine\level\Level;
use pocketmine\nbt\tag\CompoundTag;
use pocketmine\nbt\tag\DoubleTag;
use pocketmine\nbt\tag\ListTag;
use pocketmine\nbt\tag\FloatTag;
use pocketmine\nbt\tag\StringTag;
use pocketmine\Player;
use pocketmine\tile\MobSpawner;

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

			if($entity instanceof Entity){
				if($player->isSurvival()){
				$item = $player->getInventory()->getItemInHand();
				$count = $item->getCount();
				if(--$count <= 0){
					$player->getInventory()->setItemInHand(Item::get(Item::AIR));
					return;
				}

				$item->setCount($count);
				$player->getInventory()->setItemInHand($item);
				}
				$entity->spawnToAll();
				return true;
			}
		}

		return false;
	}
}