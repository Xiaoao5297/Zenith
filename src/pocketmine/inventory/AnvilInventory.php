<?php

namespace pocketmine\inventory;

use pocketmine\item\Item;
use pocketmine\level\Position;
use pocketmine\Player;

class AnvilInventory extends ContainerInventory{
	
	const TARGET = 0;
	const SACRIFICE = 1;
	const RESULT = 2;
	private $Customitem;
	
	public function __construct(Position $pos){
		parent::__construct(new FakeBlockMenu($this, $pos), InventoryType::get(InventoryType::ANVIL));
	}

	/**
	 * @return FakeBlockMenu
	 */
	public function getHolder(){
		return $this->holder;
	}
	
	
	public function getResultSlotIndex(){
		return self::RESULT;
	}
	
	public function finishRename(Player $player, $type){
		if(!isset($this->Customitem)){
			return;
		}
		if($this->Customitem->hasCustomName()){
			if($this->have_emoji($this->Customitem->getCustomName())){
				$item = $this->Customitem->setCustomName("§c不允许emoji");
			}
		}
		$level = $player->getXpLevel();
		$count = 1;
		if($level < $count){
			return false;
		}
		$le = $level - $count;
		$player->setExpLevel($le);
		$this->clearAll();
		$player->getInventory()->addItem($this->Customitem);
		$this->Customitem = null;
	}
	
	function have_emoji($str){
		$mat = [];
		preg_match_all('/./u', $str,$mat);
		foreach ($mat[0] as $v){
			if(strlen($v) > 3){
				return true;
			}
		}
		return false;
	}
	
	public function onRename(Player $player, $slot, $sourceItem, $resultItem){
		if(!$resultItem instanceof Item){
			return;
		}
		$target = $this->getItem(self::TARGET);
		if(!$resultItem->equals($target, true, false, true)){
			//Item does not match target item. Everything must match except the tags.
			return false;
		}
		//Server-side construction: never trust NBT submitted by the client.
		//Only the rename (display.Name) is applied on top of the target item;
		//any other client NBT (ench, AttributeModifiers, Lore, RepairCost, ...) is discarded.
		$result = Item::get($target->getId(), $target->getDamage(), $target->getCount());
		if($target->hasCompoundTag()){
			$result->setCompoundTag($target->getCompoundTag());
		}
		if($resultItem->hasCustomName()){
			$result->setCustomName($resultItem->getCustomName());
			$tag = $result->getNamedTag();
			if($tag !== null){
				$result->setNamedTag($tag);
			}
		}
		//$this->clearAll();
		$this->Customitem = $result;
		return true;

	}
	
	public function processSlotChange(Transaction $transaction): bool{
		if($transaction->getSlot() === $this->getResultSlotIndex()){
			return false;
		}
		return true;
	}

	public function onClose(Player $who){
		$who->updateExperience();
		parent::onClose($who);

		$this->getHolder()->getLevel()->dropItem($this->getHolder()->add(0.5, 0.5, 0.5), $this->getItem(1));
		$this->getHolder()->getLevel()->dropItem($this->getHolder()->add(0.5, 0.5, 0.5), $this->getItem(0));
		$who->usingAnvil = false;
		
		$this->clear(0);
		$this->clear(1);
		$this->clear(2);
	}

}