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

namespace pocketmine\inventory;

use pocketmine\block\Block;
use pocketmine\entity\trade\VillagerTradeOffer;
use pocketmine\entity\Villager;
use pocketmine\item\enchantment\Enchantment;
use pocketmine\item\Item;
use pocketmine\level\Position;
use pocketmine\nbt\NBT;
use pocketmine\nbt\tag\CompoundTag;
use pocketmine\nbt\tag\IntTag;
use pocketmine\nbt\tag\StringTag;
use pocketmine\network\protocol\BlockEntityDataPacket;
use pocketmine\network\protocol\UpdateBlockPacket;
use pocketmine\Player;
use pocketmine\scheduler\CallbackTask;
use pocketmine\tile\Tile;

class VillagerTradeInventory extends CustomInventory{
	const TEMPORARY_PLAYER_SLOTS = 2;
	const SLOT_COST = 0;
	const SLOT_CONFIRM = 2;
	const SLOT_PRODUCT = 4;
	const SLOT_PREVIOUS = 7;
	const SLOT_NEXT = 8;

	const CLICK_IGNORED = 0;
	const CLICK_SELECT = 1;
	const CLICK_TRADED = 2;
	const CLICK_CHANGED = 3;
	const CLICK_FAILED = 4;

	/** @var Villager|null */
	private $villager;
	/** @var Inventory */
	private $playerInventory;
	/** @var VillagerTradeOffer[] */
	private $offers;
	/** @var int */
	private $currentTradeIndex = 0;
	/** @var int|null */
	private $selectedSlot = null;
	/** @var int */
	private $lastFailureReason = VillagerTradeOffer::FAIL_NONE;
	/** @var string */
	private $professionDisplayName;
	/** @var array<string, Item[]> */
	private $temporaryPlayerInventoryBackup = [];
	/** @var array<string, int> */
	private $temporaryPlayerInventoryBaseSize = [];

	/**
	 * @param VillagerTradeOffer[] $offers
	 */
	public function __construct($holder, Inventory $playerInventory, array $offers, Villager $villager = null, string $professionDisplayName = "村民", int $currentTradeIndex = 0){
		$this->villager = $villager;
		$this->playerInventory = $playerInventory;
		$this->offers = array_values(array_filter($offers, function($offer){
			return $offer instanceof VillagerTradeOffer;
		}));
		$this->currentTradeIndex = max(0, $currentTradeIndex);
		$this->professionDisplayName = $professionDisplayName !== "" ? $professionDisplayName : "村民";
		$this->normalizeCurrentTradeIndex();

		parent::__construct($holder, InventoryType::get(InventoryType::CHEST), [], 9, $this->createTradeTitle());
		$this->refresh();
	}

	/**
	 * @param VillagerTradeOffer[] $offers
	 */
	public static function fromOffersForTest(array $offers, Inventory $playerInventory, int $currentTradeIndex = 0, string $professionDisplayName = "村民") : VillagerTradeInventory{
		$inventory = new self(new VillagerTradeMenuHolder(), $playerInventory, $offers, null, $professionDisplayName, $currentTradeIndex);
		$inventory->getHolder()->setInventory($inventory);

		return $inventory;
	}

	public static function createForPlayer(Villager $villager, Player $player, int $currentTradeIndex = 0) : VillagerTradeInventory{
		$holder = new VillagerTradeMenuHolder(Position::fromObject($player->add(0, 2), $player->getLevel()));
		$inventory = new self(
			$holder,
			$player->getInventory(),
			$villager->getTradeOffers(),
			$villager,
			Villager::getProfessionDisplayName($villager->getProfession()),
			$currentTradeIndex
		);
		$inventory->getHolder()->setInventory($inventory);

		return $inventory;
	}

	public function reserveTemporaryPlayerInventorySlots(){
		$inventory = $this->playerInventory;
		$key = spl_object_hash($inventory);
		if(isset($this->temporaryPlayerInventoryBaseSize[$key])){
			return;
		}

		$backup = [];
		foreach($this->getTemporaryPlayerInventorySlots($inventory) as $slot){
			$backup[$slot] = clone $inventory->getItem($slot);
			$inventory->clear($slot);
		}

		$this->temporaryPlayerInventoryBackup[$key] = $backup;
		$this->temporaryPlayerInventoryBaseSize[$key] = $inventory->getSize();
	}

	public function restoreTemporaryPlayerInventorySlots(Player $player = null){
		$inventory = $this->playerInventory;
		$key = spl_object_hash($inventory);
		if(!isset($this->temporaryPlayerInventoryBaseSize[$key])){
			return;
		}

		$temporaryItems = [];
		foreach($this->temporaryPlayerInventoryBackup[$key] as $slot => $originalItem){
			$currentItem = $inventory->getItem($slot);
			if($currentItem->getId() !== Item::AIR and $currentItem->getCount() > 0){
				$temporaryItems[] = clone $currentItem;
			}
			$inventory->clear($slot);
			if($originalItem->getId() !== Item::AIR and $originalItem->getCount() > 0){
				$inventory->setItem($slot, $originalItem);
			}
		}

		$remaining = [];
		foreach($temporaryItems as $item){
			$remaining = array_merge($remaining, $inventory->addItem($item));
		}

		if($player !== null and $player->isOnline() and count($remaining) > 0){
			foreach($remaining as $item){
				$player->getLevel()->dropItem($player->getPosition(), $item);
			}
		}

		unset($this->temporaryPlayerInventoryBackup[$key], $this->temporaryPlayerInventoryBaseSize[$key]);
	}

	private function getTemporaryPlayerInventorySlots(Inventory $inventory) : array{
		$usableSize = $inventory->getSize();
		if($inventory instanceof PlayerInventory){
			$usableSize -= $inventory->getHotbarSize();
		}

		$usableSize = max(0, $usableSize);
		$slots = [];
		for($i = max(0, $usableSize - self::TEMPORARY_PLAYER_SLOTS); $i < $usableSize; ++$i){
			$slots[] = $i;
		}

		return $slots;
	}

	public function getCurrentTradeIndex() : int{
		return $this->currentTradeIndex;
	}

	private function createTradeTitle() : string{
		return $this->professionDisplayName . " §a交易栏 | 列表" . ($this->currentTradeIndex + 1);
	}

	private function normalizeCurrentTradeIndex(){
		if(count($this->offers) === 0){
			$this->currentTradeIndex = 0;
			return;
		}

		if($this->currentTradeIndex >= count($this->offers)){
			$this->currentTradeIndex = count($this->offers) - 1;
		}
	}

	private function updateTradeTitle(){
		$this->title = $this->createTradeTitle();
		$this->sendTitleUpdate($this->getViewers());
	}

	private function sendTitleUpdate($target){
		if($target instanceof Player){
			$target = [$target];
		}

		$holder = $this->getHolder();
		if(!($holder instanceof VillagerTradeMenuHolder)){
			return;
		}

		foreach($target as $player){
			$packet = new BlockEntityDataPacket();
			$packet->x = $holder->getX();
			$packet->y = $holder->getY();
			$packet->z = $holder->getZ();
			$nbt = new NBT();
			$nbt->setData(new CompoundTag("", [
				new StringTag("id", Tile::CHEST),
				new IntTag("x", $holder->getX()),
				new IntTag("y", $holder->getY()),
				new IntTag("z", $holder->getZ()),
				new StringTag("CustomName", $this->getTitle())
			]));
			$packet->namedtag = $nbt->write();
			$player->dataPacket($packet);
		}
	}

	public function handleClick(int $slot) : int{
		if(!in_array($slot, [self::SLOT_CONFIRM, self::SLOT_PREVIOUS, self::SLOT_NEXT], true)){
			$this->selectedSlot = null;
			$this->refresh();
			return self::CLICK_IGNORED;
		}

		if($this->selectedSlot !== $slot){
			$this->selectedSlot = $slot;
			$this->refresh();
			return self::CLICK_SELECT;
		}

		$this->selectedSlot = null;
		switch($slot){
			case self::SLOT_CONFIRM:
				$result = $this->executeCurrentTrade() ? self::CLICK_TRADED : self::CLICK_FAILED;
				break;
			case self::SLOT_PREVIOUS:
				$this->previousTrade();
				$this->updateTradeTitle();
				$result = self::CLICK_CHANGED;
				break;
			case self::SLOT_NEXT:
				$this->nextTrade();
				$this->updateTradeTitle();
				$result = self::CLICK_CHANGED;
				break;
			default:
				$result = self::CLICK_IGNORED;
				break;
		}

		$this->refresh();
		if($this->villager !== null){
			$this->villager->setTradeOffers($this->offers);
		}

		return $result;
	}

	public function handlePlayerClick(Player $player, int $slot) : int{
		$result = $this->handleClick($slot);
		if($slot !== self::SLOT_CONFIRM or $result === self::CLICK_SELECT){
			return $result;
		}

		$player->sendMessage($this->getTradeResultMessage($result));

		$currentTradeIndex = $this->currentTradeIndex;
		$player->removeWindow($this);

		if($this->villager !== null){
			$villager = $this->villager;
			$player->getServer()->getScheduler()->scheduleDelayedTask(new CallbackTask(function($task) use ($player, $villager, $currentTradeIndex){
				if($player->isOnline() and !$villager->closed and $villager->isAlive()){
					$villager->openTradeWindow($player, $currentTradeIndex);
				}
			}), 20);
		}

		return $result;
	}

	public function refresh(){
		$this->clearAll();
		$this->normalizeCurrentTradeIndex();

		if(count($this->offers) === 0){
			return;
		}

		$offer = $this->offers[$this->currentTradeIndex];
		$this->setItem(self::SLOT_COST, $offer->getBuyA()->setCustomName("交易物"));
		$this->setItem(self::SLOT_CONFIRM, $this->createConfirmItem($offer));
		$this->setItem(self::SLOT_PRODUCT, $offer->getSell()->setCustomName("所得商品"));
		$this->setItem(self::SLOT_PREVIOUS, Item::get(Item::WOOL, 14, 1)->setCustomName("转入上一页"));
		$this->setItem(self::SLOT_NEXT, Item::get(Item::WOOL, 5, 1)->setCustomName("翻到下一页"));
	}

	public function onOpen(Player $who){
		$this->reserveTemporaryPlayerInventorySlots();
		$holder = $this->getHolder();
		if($holder instanceof VillagerTradeMenuHolder){
			$packet = new UpdateBlockPacket();
			$packet->records[] = [$holder->getX(), $holder->getZ(), $holder->getY(), Block::CHEST, 0, UpdateBlockPacket::FLAG_ALL];
			$who->dataPacket($packet);
			$this->sendTitleUpdate($who);
		}

		parent::onOpen($who);
	}

	public function onClose(Player $who){
		parent::onClose($who);
		$this->restoreTemporaryPlayerInventorySlots($who);
		$holder = $this->getHolder();
		if($holder instanceof VillagerTradeMenuHolder){
			$packet = new UpdateBlockPacket();
			$packet->records[] = [$holder->getX(), $holder->getZ(), $holder->getY(), Block::AIR, 0, UpdateBlockPacket::FLAG_ALL];
			$who->dataPacket($packet);
		}
	}

	private function createConfirmItem(VillagerTradeOffer $offer) : Item{
		$item = Item::get(Item::PAPER, 0, 1)->setCustomName($offer->isExhausted() ? "§7[§4✖§7] §c商品已售罄" : "§7[§a✔§7]§a 点击交易");
		$item->addEnchantment(Enchantment::getEnchantment(Enchantment::TYPE_MINING_DURABILITY)->setLevel(1));
		return $item;
	}

	private function executeCurrentTrade() : bool{
		if(!isset($this->offers[$this->currentTradeIndex])){
			$this->lastFailureReason = VillagerTradeOffer::FAIL_MISSING_ITEMS;
			return false;
		}

		$offer = $this->offers[$this->currentTradeIndex];
		$this->lastFailureReason = $offer->getFailureReason($this->playerInventory);
		if($this->lastFailureReason !== VillagerTradeOffer::FAIL_NONE){
			return false;
		}

		$executed = $offer->execute($this->playerInventory);
		if(!$executed){
			$this->lastFailureReason = $offer->getFailureReason($this->playerInventory);
		}

		return $executed;
	}

	private function getTradeResultMessage(int $result) : string{
		if($result === self::CLICK_TRADED){
			return "§a交易成功";
		}

		switch($this->lastFailureReason){
			case VillagerTradeOffer::FAIL_EXHAUSTED:
				return "§c交易失败，该商品已售罄";
			case VillagerTradeOffer::FAIL_NO_SPACE:
				return "§c交易失败，您的背包空间不足！";
			case VillagerTradeOffer::FAIL_MISSING_ITEMS:
			default:
				return "§c交易失败，您没有足够的物品来兑换！";
		}
	}

	private function previousTrade(){
		if(count($this->offers) === 0){
			$this->currentTradeIndex = 0;
			return;
		}

		$this->currentTradeIndex = ($this->currentTradeIndex - 1 + count($this->offers)) % count($this->offers);
	}

	private function nextTrade(){
		if(count($this->offers) === 0){
			$this->currentTradeIndex = 0;
			return;
		}

		$this->currentTradeIndex = ($this->currentTradeIndex + 1) % count($this->offers);
	}
}

class VillagerTradeMenuHolder extends Position implements InventoryHolder{
	/** @var Inventory|null */
	private $inventory;

	public function __construct(Position $position = null){
		if($position !== null){
			parent::__construct($position->x, $position->y, $position->z, $position->level);
		}else{
			parent::__construct();
		}
	}

	public function setInventory(Inventory $inventory){
		$this->inventory = $inventory;
	}

	public function getInventory(){
		return $this->inventory;
	}
}
