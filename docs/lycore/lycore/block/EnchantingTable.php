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

use lycore\inventory\EnchantInventory;
use lycore\item\Item;
use lycore\item\Tool;

use lycore\nbt\tag\CompoundTag;
use lycore\nbt\tag\IntTag;
use lycore\nbt\tag\StringTag;
use lycore\network\protocol\ProtocolCompatibility;
use lycore\Player;
use lycore\tile\EnchantTable;
use lycore\tile\Tile;

class EnchantingTable extends Transparent{

	protected $id = self::ENCHANTING_TABLE;

	public function __construct(){

	}

	public function place(Item $item, Block $block, Block $target, $face, $fx, $fy, $fz, Player $player = null){
		$this->getLevel()->setBlock($block, $this, true, true);
		$nbt = new CompoundTag("", [
			new StringTag("id", Tile::ENCHANT_TABLE),
			new IntTag("x", $this->x),
			new IntTag("y", $this->y),
			new IntTag("z", $this->z)
		]);

		if($item->hasCustomName()){
			$nbt->CustomName = new StringTag("CustomName", $item->getCustomName());
		}

		if($item->hasCustomBlockData()){
			foreach($item->getCustomBlockData() as $key => $v){
				$nbt->{$key} = $v;
			}
		}

		Tile::createTile(Tile::ENCHANT_TABLE, $this->getLevel()->getChunk($this->x >> 4, $this->z >> 4), $nbt);

		return true;
	}

	public function canBeActivated() : bool{
		return true;
	}

	public function getHardness(){
		return 5;
	}

	public function getResistance(){
		return 6000;
	}

	public function getName() : string{
		return "Enchanting Table";
	}

	public function getToolType(){
		return Tool::TYPE_PICKAXE;
	}

	public function onActivate(Item $item, Player $player = null){
		if(!$this->getLevel()->getServer()->enchantingTableEnabled) return true;
		if($player instanceof Player){
			if(ProtocolCompatibility::isProtocol011((int) $player->getProtocol())){
				return $player->handleProtocol011EnchantingTableInteraction($this, $item);
			}
			$tile = $this->getLevel()->getTile($this);
			if($tile instanceof EnchantTable){
				$enchantTable = $tile;
			}else{
				$this->getLevel()->setBlock($this, $this, true, true);
				$nbt = new CompoundTag("", [
					new StringTag("id", Tile::ENCHANT_TABLE),
					new IntTag("x", $this->x),
					new IntTag("y", $this->y),
					new IntTag("z", $this->z)
				]);

				if($item->hasCustomName()){
					$nbt->CustomName = new StringTag("CustomName", $item->getCustomName());
				}

				if($item->hasCustomBlockData()){
					foreach($item->getCustomBlockData() as $key => $v){
						$nbt->{$key} = $v;
					}
				}

				$enchantTable = Tile::createTile(Tile::ENCHANT_TABLE, $this->getLevel()->getChunk($this->x >> 4, $this->z >> 4), $nbt);
			}
			if($enchantTable instanceof EnchantTable){
				$player->addWindow($enchantTable->getInventory());
			}
		}

		return true;
	}

	public function getDrops(Item $item) : array{
		if($item->isPickaxe() >= 1){
			return [
				[$this->id, 0, 1],
			];
		}else{
			return [];
		}
	}
}
