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

use lycore\item\Item;
use lycore\item\Tool;
use lycore\level\Level;
use lycore\math\Vector3;
use lycore\nbt\NBT;
use lycore\nbt\tag\CompoundTag;
use lycore\nbt\tag\ListTag;
use lycore\nbt\tag\IntTag;
use lycore\nbt\tag\StringTag;
use lycore\Player;
use lycore\tile\Dispenser as TileDispenser;
use lycore\tile\Tile;

class Dispenser extends Solid{

	protected $id = self::DISPENSER;

	public function __construct($meta = 0){
		$this->meta = $meta;
	}

	public function canBeActivated() : bool {
		return true;
	}

	public function getHardness() {
		return 3.5;
	}

	public function getName() : string{
		return "Dispenser";
	}

	public function getToolType(){
		return Tool::TYPE_PICKAXE;
	}

	public function place(Item $item, Block $block, Block $target, $face, $fx, $fy, $fz, Player $player = null){
		$dispenser = null;
		if($player instanceof Player){
			$pitch = $player->getPitch();
			if(abs($pitch) >= 45){
				if($pitch < 0) $f = 4;
				else $f = 5;
			} else $f = $player->getDirection();
		} else $f = 0;
		$faces = [
			3 => 3,
			0 => 4,
			2 => 5,
			1 => 2,
			4 => 0,
			5 => 1
		];
		$this->meta = $faces[$f];

		$this->getLevel()->setBlock($block, $this, true, true);
		$nbt = new CompoundTag("", [
			new ListTag("Items", []),
			new StringTag("id", Tile::DISPENSER),
			new IntTag("x", $this->x),
			new IntTag("y", $this->y),
			new IntTag("z", $this->z)
		]);
		$nbt->Items->setTagType(NBT::TAG_Compound);

		if($item->hasCustomName()){
			$nbt->CustomName = new StringTag("CustomName", $item->getCustomName());
		}

		if($item->hasCustomBlockData()){
			foreach($item->getCustomBlockData() as $key => $v){
				$nbt->{$key} = $v;
			}
		}

		Tile::createTile(Tile::DISPENSER, $this->getLevel()->getChunk($this->x >> 4, $this->z >> 4), $nbt);

		return true;
	}

	public function activate(){
		$tile = $this->getLevel()->getTile($this);
		if(!($tile instanceof TileDispenser)){
			$nbt = new CompoundTag("", [
				new ListTag("Items", []),
				new StringTag("id", Tile::DISPENSER),
				new IntTag("x", $this->x),
				new IntTag("y", $this->y),
				new IntTag("z", $this->z)
			]);
			$nbt->Items->setTagType(NBT::TAG_Compound);

			$chunk = $this->getLevel()->getChunk($this->x >> 4, $this->z >> 4);
			if($chunk !== null){
				$tile = Tile::createTile(Tile::DISPENSER, $chunk, $nbt);
			}
		}

		if($tile instanceof TileDispenser){
			$tile->activate();
		}
	}

	public function onUpdate($type){
		if($type === Level::BLOCK_UPDATE_REDSTONE or $type === Level::BLOCK_UPDATE_NORMAL){
			$powered = $this->getLevel()->isBlockPowered($this) || $this->getLevel()->isBlockPowered($this->getSide(Vector3::SIDE_UP));
			$wasPowered = (bool) $this->getLevel()->getBlockTempData($this);
			if($powered and !$wasPowered){
				$this->getLevel()->setBlockTempData($this, true);
				if(!$this->getLevel()->isBlockTickPending($this, $this)){
					$this->getLevel()->scheduleUpdate($this, 4);
				}
			}elseif(!$powered and $wasPowered){
				$this->getLevel()->setBlockTempData($this, null);
			}

			return $type;
		}

		if($type === Level::BLOCK_UPDATE_SCHEDULED){
			$this->activate();

			return Level::BLOCK_UPDATE_SCHEDULED;
		}

		return false;
	}

	public function onActivate(Item $item, Player $player = null){
		if($player instanceof Player){
			$t = $this->getLevel()->getTile($this);
			$dispenser = null;
			if($t instanceof TileDispenser){
				$dispenser = $t;
			}else{
				$nbt = new CompoundTag("", [
					new ListTag("Items", []),
					new StringTag("id", Tile::DISPENSER),
					new IntTag("x", $this->x),
					new IntTag("y", $this->y),
					new IntTag("z", $this->z)
				]);
				$nbt->Items->setTagType(NBT::TAG_Compound);
				$dispenser = Tile::createTile(Tile::DISPENSER, $this->getLevel()->getChunk($this->x >> 4, $this->z >> 4), $nbt);
			}

			if($player->isCreative() and $player->getServer()->limitedCreative){
				return true;
			}

			$player->addWindow($dispenser->getInventory());
		}

		return true;
	}

	public function getDrops(Item $item) : array {
		return [
			[$this->id, 0, 1],
		];
	}

	public function hasComparatorInputOverride(){
		return true;
	}

	public function getComparatorInputOverride(){
		$tile = $this->getLevel()->getTile($this);
		return $tile instanceof TileDispenser ? $this->calculateInventoryComparatorInput($tile->getInventory()) : 0;
	}
}
