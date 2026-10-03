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
use lycore\nbt\NBT;
use lycore\nbt\tag\CompoundTag;
use lycore\nbt\tag\ListTag;
use lycore\nbt\tag\IntTag;
use lycore\nbt\tag\StringTag;
use lycore\Player;
use lycore\level\Level;
use lycore\tile\Hopper as TileHopper;
use lycore\tile\Tile;
use lycore\item\Tool;

class Hopper extends Transparent{

	protected $id = self::HOPPER_BLOCK;

	public function __construct($meta = 0){
		$this->meta = $meta;
	}

	public function canBeActivated(): bool{
		return true;
	}

	public function getToolType(){
		return Tool::TYPE_PICKAXE;
	}

	public function getName() : string{
		return "Hopper";
	}

	public function getHardness(){
		return 3;
	}

	public function onActivate(Item $item, Player $player = null){
		if($player instanceof Player){
			$t = $this->getLevel()->getTile($this);
			if(!($t instanceof TileHopper)){
				$nbt = new CompoundTag("", [
					new ListTag("Items", []),
					new StringTag("id", Tile::HOPPER),
					new IntTag("x", $this->x),
					new IntTag("y", $this->y),
					new IntTag("z", $this->z)
				]);
				$nbt->Items->setTagType(NBT::TAG_Compound);
				$t = Tile::createTile(Tile::HOPPER, $this->getLevel()->getChunk($this->x >> 4, $this->z >> 4), $nbt);
			}
			if($t instanceof TileHopper and $t->hasLock() and !$t->checkLock($item->getCustomName())){
				$player->getServer()->getLogger()->debug($player->getName() . " attempted to open a locked hopper");
				return true;
			}
			if($t instanceof TileHopper){
				$player->addWindow($t->getInventory());
			}
		}
		return true;
	}

	public function activate(){
		$tile = $this->getLevel()->getTile($this);
		if($tile instanceof TileHopper){
			$tile->activate();
		}
	}

	public function deactivate(){
		$tile = $this->getLevel()->getTile($this);
		if($tile instanceof TileHopper){
			$tile->deactivate();
		}
	}

	public function onUpdate($type){
		if($type === Level::BLOCK_UPDATE_REDSTONE or $type === Level::BLOCK_UPDATE_NORMAL){
			if($this->getLevel()->isBlockPowered($this)){
				$this->activate();
			}else{
				$this->deactivate();
			}

			return $type;
		}

		return false;
	}

	public function getTarget(){
		return $this->target;
	}

	public function place(Item $item, Block $block, Block $target, $face, $fx, $fy, $fz, Player $player = null){
		$faces = [
			0 => 0,
			1 => 0,
			2 => 3,
			3 => 2,
			4 => 5,
			5 => 4
		];
		$this->meta = $faces[$face];
		$this->getLevel()->setBlock($block, $this, true, true);

		$nbt = new CompoundTag("", [
			new ListTag("Items", []),
			new StringTag("id", Tile::HOPPER),
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

		$t = Tile::createTile(Tile::HOPPER, $this->getLevel()->getChunk($this->x >> 4, $this->z >> 4), $nbt);

		return true;
	}

	public function hasComparatorInputOverride(){
		return true;
	}

	public function getComparatorInputOverride(){
		$tile = $this->getLevel()->getTile($this);
		return $tile instanceof TileHopper ? $this->calculateInventoryComparatorInput($tile->getInventory()) : 0;
	}

    public function getDrops(Item $item) : array {
        /* ---- 1. 方块本身 ---- */
        if($item->isPickaxe() >= Tool::TIER_IRON){   // 铁镐及以上才掉漏斗
            $drops = [[Item::HOPPER, 0, 1]];
        } else {
            $drops = [];
        }

        /* ---- 2. 把库存全部撒地上 ---- */
        $tile = $this->getLevel()->getTile($this);
        if($tile instanceof TileHopper){
            foreach($tile->getInventory()->getContents() as $content){
                if($content->getId() !== Item::AIR){
                    // 直接丢到世界，坐标中心+0.5 避免卡墙
                    $this->getLevel()->dropItem(
                        $this->add(0.5, 0.5, 0.5),
                        $content
                    );
                }
            }
            // 清空 tile 库存，防止基类再发一次（双份）
            $tile->getInventory()->clearAll();
        }

        return $drops;
    }
}
