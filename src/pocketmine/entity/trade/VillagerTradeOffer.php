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

namespace pocketmine\entity\trade;

use pocketmine\inventory\Inventory;
use pocketmine\item\Item;
use pocketmine\nbt\NBT;
use pocketmine\nbt\tag\CompoundTag;
use pocketmine\nbt\tag\IntTag;

class VillagerTradeOffer{
	const FAIL_NONE = 0;
	const FAIL_EXHAUSTED = 1;
	const FAIL_MISSING_ITEMS = 2;
	const FAIL_NO_SPACE = 3;

	/** @var Item */
	private $buyA;
	/** @var Item|null */
	private $buyB;
	/** @var Item */
	private $sell;
	/** @var int */
	private $maxUses;
	/** @var int */
	private $uses;

	public function __construct(Item $buyA, $buyB, Item $sell, int $maxUses, int $uses = 0){
		$this->buyA = clone $buyA;
		$this->buyB = $buyB !== null ? clone $buyB : null;
		$this->sell = clone $sell;
		$this->maxUses = max(1, $maxUses);
		$this->uses = max(0, min($uses, $this->maxUses));
	}

	public function getBuyA() : Item{
		return clone $this->buyA;
	}

	public function getBuyB(){
		return $this->buyB !== null ? clone $this->buyB : null;
	}

	public function getSell() : Item{
		return clone $this->sell;
	}

	public function getMaxUses() : int{
		return $this->maxUses;
	}

	public function getUses() : int{
		return $this->uses;
	}

	public function isExhausted() : bool{
		return $this->uses >= $this->maxUses;
	}

	public function canExecute(Inventory $inventory) : bool{
		return $this->getFailureReason($inventory) === self::FAIL_NONE;
	}

	public function getFailureReason(Inventory $inventory) : int{
		if($this->isExhausted()){
			return self::FAIL_EXHAUSTED;
		}

		if(!$this->hasItemCount($inventory, $this->buyA)){
			return self::FAIL_MISSING_ITEMS;
		}

		if($this->buyB !== null and !$this->hasItemCount($inventory, $this->buyB)){
			return self::FAIL_MISSING_ITEMS;
		}

		if(!$this->canAddSellAfterRemovingCosts($inventory)){
			return self::FAIL_NO_SPACE;
		}

		return self::FAIL_NONE;
	}

	public function execute(Inventory $inventory) : bool{
		if($this->getFailureReason($inventory) !== self::FAIL_NONE){
			return false;
		}

		$this->removeItemCount($inventory, $this->buyA);
		if($this->buyB !== null){
			$this->removeItemCount($inventory, $this->buyB);
		}

		$inventory->addItem($this->sell);
		$this->recordUse();

		return true;
	}

	public function recordUse(){
		if($this->uses < $this->maxUses){
			$this->uses++;
		}
	}

	private function hasItemCount(Inventory $inventory, Item $required) : bool{
		$remaining = $required->getCount();
		foreach($inventory->getContents() as $item){
			if($required->equals($item, true, true)){
				$remaining -= $item->getCount();
				if($remaining <= 0){
					return true;
				}
			}
		}

		return false;
	}

	private function removeItemCount(Inventory $inventory, Item $required){
		$remaining = $required->getCount();
		foreach($inventory->getContents() as $slot => $item){
			if(!$required->equals($item, true, true)){
				continue;
			}

			if($item->getCount() > $remaining){
				$item->setCount($item->getCount() - $remaining);
				$inventory->setItem($slot, $item);
				return;
			}

			$remaining -= $item->getCount();
			$inventory->clear($slot);
			if($remaining <= 0){
				return;
			}
		}
	}

	private function canAddSellAfterRemovingCosts(Inventory $inventory) : bool{
		$slots = [];
		for($i = 0; $i < $inventory->getSize(); ++$i){
			$slots[$i] = $inventory->getItem($i);
		}

		if(!$this->removeItemCountFromSlots($slots, $this->buyA)){
			return false;
		}
		if($this->buyB !== null and !$this->removeItemCountFromSlots($slots, $this->buyB)){
			return false;
		}

		return $this->canAddItemToSlots($slots, $this->sell, $inventory->getMaxStackSize());
	}

	private function removeItemCountFromSlots(array &$slots, Item $required) : bool{
		$remaining = $required->getCount();
		foreach($slots as $slot => $item){
			if(!$required->equals($item, true, true)){
				continue;
			}

			if($item->getCount() > $remaining){
				$item->setCount($item->getCount() - $remaining);
				$slots[$slot] = $item;
				return true;
			}

			$remaining -= $item->getCount();
			$slots[$slot] = Item::get(Item::AIR, 0, 0);
			if($remaining <= 0){
				return true;
			}
		}

		return false;
	}

	private function canAddItemToSlots(array $slots, Item $item, int $inventoryMaxStackSize) : bool{
		$remaining = $item->getCount();
		foreach($slots as $slot){
			if($item->equals($slot, true, true)){
				$remaining -= max(0, min($item->getMaxStackSize(), $inventoryMaxStackSize) - $slot->getCount());
			}elseif($slot->getId() === Item::AIR or $slot->getCount() <= 0){
				$remaining -= min($item->getMaxStackSize(), $inventoryMaxStackSize);
			}

			if($remaining <= 0){
				return true;
			}
		}

		return false;
	}

	public function toNBT(int $index) : CompoundTag{
		$buyA = NBT::putItemHelper($this->buyA);
		$buyA->setName("buyA");
		$sell = NBT::putItemHelper($this->sell);
		$sell->setName("sell");
		$tag = new CompoundTag((string) $index, [
			"buyA" => $buyA,
			"sell" => $sell,
			"maxUses" => new IntTag("maxUses", $this->maxUses),
			"uses" => new IntTag("uses", $this->uses)
		]);

		if($this->buyB !== null){
			$buyB = NBT::putItemHelper($this->buyB);
			$buyB->setName("buyB");
			$tag->buyB = $buyB;
		}

		return $tag;
	}

	public static function fromNBT(CompoundTag $tag) : VillagerTradeOffer{
		$buyA = isset($tag->buyA) && $tag->buyA instanceof CompoundTag ? NBT::getItemHelper($tag->buyA) : Item::get(Item::AIR, 0, 0);
		$buyB = isset($tag->buyB) && $tag->buyB instanceof CompoundTag ? NBT::getItemHelper($tag->buyB) : null;
		$sell = isset($tag->sell) && $tag->sell instanceof CompoundTag ? NBT::getItemHelper($tag->sell) : Item::get(Item::AIR, 0, 0);
		$maxUses = isset($tag->maxUses) ? (int) $tag["maxUses"] : 1;
		$uses = isset($tag->uses) ? (int) $tag["uses"] : 0;

		return new self($buyA, $buyB, $sell, $maxUses, $uses);
	}

	public function fingerprint() : string{
		$parts = [
			$this->itemFingerprint($this->buyA),
			$this->buyB !== null ? $this->itemFingerprint($this->buyB) : "0:0:0",
			$this->itemFingerprint($this->sell),
			$this->maxUses,
			$this->uses
		];

		return implode(">", $parts);
	}

	private function itemFingerprint(Item $item) : string{
		return $item->getId() . ":" . $item->getDamage() . ":" . $item->getCount();
	}
}
