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

/*
 * 移植自 lycore\inventory\HorseInventory，命名空间改为 pocketmine\inventory。
 * 当前核心没有 InventoryType::HORSE，改用 CHEST 类型 + 2 格容量，
 * 并在打开包中带上 entityId。
 */

namespace pocketmine\inventory;

use pocketmine\entity\Horse;
use pocketmine\item\Item;
use pocketmine\nbt\NBT;
use pocketmine\nbt\tag\CompoundTag;
use pocketmine\nbt\tag\ListTag;
use pocketmine\network\protocol\ContainerOpenPacket;
use pocketmine\network\protocol\MobArmorEquipmentPacket;
use pocketmine\Player;

class HorseInventory extends CustomInventory{
	/** @var Horse */
	protected $holder;

	public function __construct(Horse $holder){
		parent::__construct($holder, InventoryType::get(InventoryType::CHEST), [], 2);
		$this->maxStackSize = 1;
	}

	public function getName() : string{
		return "Horse";
	}

	public function getSaddle(){
		return $this->getItem(0);
	}

	public function setSaddle(Item $item){
		return $this->setItem(0, $item);
	}

	public function getArmor(){
		return $this->getItem(1);
	}

	public function setArmor(Item $item){
		return $this->setItem(1, $item);
	}

	public function setItem($index, Item $item){
		$index = (int) $index;
		if($index < 0 or $index >= $this->getSize()){
			return false;
		}
		if(!$this->isAllowedInSlot($index, $item)){
			$this->sendSlot($index, $this->getViewers());
			return false;
		}

		return parent::setItem($index, $item);
	}

	private function isAllowedInSlot($index, Item $item) : bool{
		if($item->getId() === Item::AIR or $item->getCount() <= 0){
			return true;
		}

		if($index === 0){
			return $item->getId() === Item::SADDLE and $this->holder->canBeSaddled();
		}
		if($index === 1){
			return Horse::isHorseArmorItem($item) and !$this->holder->isBaby();
		}

		return false;
	}

	public function onSlotChange($index, $before){
		if($index === 0){
			$this->holder->setSaddled($this->getSaddle()->getId() === Item::SADDLE);
			$this->sendArmorToViewers();
		}elseif($index === 1){
			$this->sendArmorToViewers();
		}

		parent::onSlotChange($index, $before);
	}

	public function onOpen(Player $who){
		$this->viewers[spl_object_hash($who)] = $who;

		$pk = new ContainerOpenPacket();
		$pk->windowid = $who->getWindowId($this);
		$pk->type = $this->getType()->getNetworkType();
		$pk->slots = $this->getSize();
		$pk->x = (int) floor($this->holder->x);
		$pk->y = (int) floor($this->holder->y);
		$pk->z = (int) floor($this->holder->z);
		$pk->entityId = $this->holder->getId();
		$who->dataPacket($pk);

		$this->sendContents($who);
		$this->sendArmor($who);
	}

	public function sendArmorToViewers(){
		$targets = $this->holder->getViewers();
		foreach($this->getViewers() as $viewer){
			$targets[] = $viewer;
		}

		foreach($targets as $viewer){
			if($viewer instanceof Player){
				$this->sendArmor($viewer);
			}
		}
	}

	public function sendArmor(Player $player){
		$air = Item::get(Item::AIR, 0, 0);
		$saddle = $this->getSaddle();
		if($saddle->getId() !== Item::SADDLE){
			$saddle = $air;
		}
		$pk = new MobArmorEquipmentPacket();
		$pk->eid = $this->holder->getId();
		$pk->slots = [
			0 => $saddle,
			1 => $this->getArmor(),
			2 => $air,
			3 => $air,
		];
		$player->dataPacket($pk);
	}

	public function loadFromNbt(ListTag $tag = null){
		if($tag === null){
			return;
		}

		foreach($tag as $entry){
			if(!$entry instanceof CompoundTag or !isset($entry->Slot)){
				continue;
			}
			$slot = (int) $entry["Slot"];
			if($slot === 0 or $slot === 1){
				$this->setItem($slot, NBT::getItemHelper($entry));
			}
		}
	}

	public function saveToNbt(){
		$items = [];
		foreach($this->getNbtItems() as $slot => $item){
			$items[] = NBT::putItemHelper($item, $slot);
		}

		return new ListTag("Inventory", $items);
	}

	public function getNbtItems(){
		$items = [];
		$saddle = $this->getSaddle();
		if($saddle->getId() !== Item::AIR and $saddle->getCount() > 0){
			$items[0] = $saddle;
		}

		$armor = $this->getArmor();
		if($armor->getId() !== Item::AIR and $armor->getCount() > 0){
			$items[1] = $armor;
		}

		return $items;
	}

	public function getInventoryDrops(){
		return array_values($this->getNbtItems());
	}
}
