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
use lycore\nbt\NBT;
use lycore\nbt\tag\CompoundTag;
use lycore\nbt\tag\ListTag;
use lycore\nbt\tag\IntTag;
use lycore\nbt\tag\StringTag;
use lycore\network\protocol\ProtocolCompatibility;
use lycore\utils\TextFormat;
use lycore\Player;
use lycore\tile\Furnace;
use lycore\tile\Tile;

class BurningFurnace extends Solid{

	protected $id = self::BURNING_FURNACE;

	public function __construct($meta = 0){
		$this->meta = $meta;
	}

	public function getName() : string{
		return "Burning Furnace";
	}

	public function canBeActivated() : bool {
		return true;
	}

	public function getHardness() {
		return 3.5;
	}

	public function getToolType(){
		return Tool::TYPE_PICKAXE;
	}

	public function getLightLevel(){
		return 13;
	}

	public function place(Item $item, Block $block, Block $target, $face, $fx, $fy, $fz, Player $player = null){
		$faces = [
			0 => 4,
			1 => 2,
			2 => 5,
			3 => 3,
		];
		$this->meta = $faces[$player instanceof Player ? $player->getDirection() : 0];
		$this->getLevel()->setBlock($block, $this, true, true);
		$nbt = new CompoundTag("", [
			new ListTag("Items", []),
			new StringTag("id", Tile::FURNACE),
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

		Tile::createTile("Furnace", $this->getLevel()->getChunk($this->x >> 4, $this->z >> 4), $nbt);

		return true;
	}

	public function onBreak(Item $item){
		$this->getLevel()->setBlock($this, new Air(), true, true);

		return true;
	}

	public function onActivate(Item $item, Player $player = null){
		if($player instanceof Player){
			$t = $this->getLevel()->getTile($this);
			$furnace = false;
			if($t instanceof Furnace){
				$furnace = $t;
			}else{
				$nbt = new CompoundTag("", [
					new ListTag("Items", []),
					new StringTag("id", Tile::FURNACE),
					new IntTag("x", $this->x),
					new IntTag("y", $this->y),
					new IntTag("z", $this->z)
				]);
				$nbt->Items->setTagType(NBT::TAG_Compound);
				$furnace = Tile::createTile("Furnace", $this->getLevel()->getChunk($this->x >> 4, $this->z >> 4), $nbt);
			}

			if(isset($furnace->namedtag->Lock) and $furnace->namedtag->Lock instanceof StringTag){
				if($furnace->namedtag->Lock->getValue() !== $item->getCustomName()){
					return true;
				}
			}

			if($player->isCreative() and $player->getServer()->limitedCreative){
				return true;
			}

			if($this->shouldHandleLegacyMuttonQuickCook($player, $item)){
				return $this->tryHandleLegacyMuttonQuickCook($item, $player, $furnace);
			}

			$player->addWindow($furnace->getInventory());
		}

		return true;
	}

	private function shouldHandleLegacyMuttonQuickCook(Player $player, Item $item) : bool{
		return ProtocolCompatibility::requiresLegacyRedstoneMapping((int) $player->getProtocol()) and $item->getId() === Item::RAW_MUTTON;
	}

	private function tryHandleLegacyMuttonQuickCook(Item $item, Player $player, Furnace $furnace) : bool{
		if(mt_rand(0, 1) === 0){
			$player->sendMessage(TextFormat::YELLOW . "若要煮熟羊肉，直接手持羊肉并点击熔炉即可放入");
		}

		$result = Item::get(Item::COOKED_MUTTON, 0, 1);
		if(!$player->getInventory()->canAddItem($result)){
			$player->sendMessage(TextFormat::RED . "当前背包空间不足，无法放入煮熟的羊肉");
			return true;
		}

		$availableFuel = $this->getLegacyQuickCookAvailableFuel($furnace);
		if($availableFuel < 200){
			$player->sendMessage(TextFormat::RED . "当前熔炉燃料不足以燃烧一块羊肉");
			return true;
		}

		$item->setCount($item->getCount() - 1);
		$player->getInventory()->addItem($result);
		$this->consumeLegacyQuickCookFuel($furnace);
		$player->sendMessage(TextFormat::GREEN . "羊肉已煮熟并放入您的背包");
		return true;
	}

	private function getLegacyQuickCookAvailableFuel(Furnace $furnace) : int{
		$burnTime = (int) $furnace->namedtag["BurnTime"];
		$fuel = $furnace->getInventory()->getFuel();
		$fuelTime = $fuel->getFuelTime();
		if($fuelTime === null or $fuel->getCount() <= 0){
			return $burnTime;
		}

		return $burnTime + ((int) $fuelTime * $fuel->getCount());
	}

	private function consumeLegacyQuickCookFuel(Furnace $furnace){
		if((int) $furnace->namedtag["BurnTime"] >= 200){
			$furnace->namedtag->BurnTime = new \lycore\nbt\tag\ShortTag("BurnTime", (int) $furnace->namedtag["BurnTime"] - 200);
			return;
		}

		$furnace->namedtag->BurnTime = new \lycore\nbt\tag\ShortTag("BurnTime", 0);
		$fuel = $furnace->getInventory()->getFuel();
		if($fuel->getId() === Item::AIR or $fuel->getCount() <= 0){
			return;
		}

		$fuel->setCount($fuel->getCount() - 1);
		if($fuel->getCount() <= 0){
			$fuel = Item::get(Item::AIR, 0, 0);
		}
		$furnace->getInventory()->setFuel($fuel);
	}

	public function getDrops(Item $item) : array {
		$drops = [];
		if($item->isPickaxe() >= 1){
			$drops[] = [Item::FURNACE, 0, 1];
		}

		return $drops;
	}

	public function hasComparatorInputOverride(){
		return true;
	}

	public function getComparatorInputOverride(){
		$tile = $this->getLevel()->getTile($this);
		return $tile instanceof Furnace ? $this->calculateInventoryComparatorInput($tile->getInventory()) : 0;
	}
}
