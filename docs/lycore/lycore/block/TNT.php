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

namespace lycore\block;

use lycore\entity\Entity;
use lycore\item\Item;
use lycore\level\Level;
use lycore\level\sound\TNTPrimeSound;
use lycore\math\Vector3;
use lycore\nbt\tag\ByteTag;
use lycore\nbt\tag\CompoundTag;
use lycore\nbt\tag\DoubleTag;
use lycore\nbt\tag\ListTag;
use lycore\nbt\tag\FloatTag;
use lycore\Player;
use lycore\utils\Random;

class TNT extends Solid implements ElectricalAppliance{

	protected $id = self::TNT;

	public function __construct(){

	}

	public function getName() : string{
		return "TNT";
	}

	public function getHardness(){
		return 0;
	}

	public function canBeActivated() : bool{
		return true;
	}

	public function getBurnChance() : int{
		return 15;
	}

	public function getBurnAbility() : int{
		return 100;
	}

	public function prime(){
		if(!$this->getLevel()->getServer()->tntExplosionEnabled){
			return false;
		}

		$this->meta = 1;
		$mot = (new Random())->nextSignedFloat() * M_PI * 2;
		$tnt = Entity::createEntity("PrimedTNT", $this->getLevel()->getChunk($this->x >> 4, $this->z >> 4), new CompoundTag("", [
			"Pos" => new ListTag("Pos", [
				new DoubleTag("", $this->x + 0.5),
				new DoubleTag("", $this->y),
				new DoubleTag("", $this->z + 0.5)
			]),
			"Motion" => new ListTag("Motion", [
				new DoubleTag("", -sin($mot) * 0.02),
				new DoubleTag("", 0.2),
				new DoubleTag("", -cos($mot) * 0.02)
			]),
			"Rotation" => new ListTag("Rotation", [
				new FloatTag("", 0),
				new FloatTag("", 0)
			]),
			"Fuse" => new ByteTag("Fuse", 80)
		]));

		$tnt->spawnToAll();
		$this->level->addSound(new TNTPrimeSound($this));
		return true;
	}

	public function onUpdate($type){
		if($type == Level::BLOCK_UPDATE_REDSTONE or $type == Level::BLOCK_UPDATE_NORMAL){
			if($this->getLevel()->isBlockPowered($this)){
				if($this->prime()){
					$this->getLevel()->setBlock($this, new Air(), true);
				}
			}

			return $type;
		}

		if($type == Level::BLOCK_UPDATE_SCHEDULED){
			$sides = [0, 1, 2, 3, 4, 5];
			foreach($sides as $side){
				$block = $this->getSide($side);
				if($block instanceof RedstoneSource and $block->isActivated($this)){
					if($this->prime()){
						$this->getLevel()->setBlock($this, new Air(), true);
					}
					break;
				}
			}
			return Level::BLOCK_UPDATE_SCHEDULED;
		}
		return false;
	}

	public function place(Item $item, Block $block, Block $target, $face, $fx, $fy, $fz, Player $player = null){
		$this->getLevel()->setBlock($this, $this, true, false);

		$this->getLevel()->scheduleUpdate($this, 40);
	}

	public function onActivate(Item $item, Player $player = null){
		if($item->getId() === Item::FLINT_STEEL){
			if($this->prime()){
				$this->getLevel()->setBlock($this, new Air(), true);
				$item->useOn($this, 2);
				return true;
			}
		}

		return false;
	}
}
