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

namespace lycore\entity;

use lycore\nbt\tag\ByteTag;
use lycore\item\Item as ItemItem;
use lycore\item\Lead;
use lycore\Player;

abstract class Animal extends Mob implements Ageable{
	

	public function initEntity(){
		parent::initEntity();
		if(!isset($this->namedtag->IsBaby)){
			$this->namedtag->IsBaby = new ByteTag("IsBaby", 1);
			$this->setBaby(false);
		}
		$this->setBaby($this->isBaby());
	}

	public function isBaby(){
		return $this->namedtag["IsBaby"] == 0 ? false : true;
	}
	
	public function setBaby(bool $resting){
		$this->setDataProperty(self::DATA_IS_BABY, self::DATA_TYPE_BYTE, $resting ? 1 : 0);
		$this->namedtag->IsBaby = new ByteTag("IsBaby", $resting ? 1 : 0);
	}
	
	public function isInLove(){
		return $this->getDataProperty(self::DATA_IN_LOVE) === 1;
	}
	
	public function setInLove(bool $resting){
		$this->setDataProperty(self::DATA_IN_LOVE, self::DATA_TYPE_BYTE, $resting ? 1 : 0);
	}

	public function onInteract(Player $player, ItemItem $item) : bool{
		if($item instanceof Lead){
			return $item->leashEntity($player, $this);
		}

		return false;
	}

	public function canBreedWith(ItemItem $item) : bool{
		return in_array($item->getId(), $this->getBreedingItemIds(), true);
	}

	protected function getBreedingItemIds() : array{
		return [];
	}

	protected function replaceInteractionItem(Player $player, ItemItem $source, ItemItem $result) : bool{
		if($player->isCreative()){
			return true;
		}

		$inventory = $player->getInventory();
		if($source->getCount() <= 1){
			$inventory->setItemInHand($result);
			return true;
		}

		$source->setCount($source->getCount() - 1);
		$inventory->setItemInHand($source);
		$leftovers = $inventory->addItem($result);
		if(count($leftovers) > 0){
			$level = $player->getLevel();
			if($level !== null and method_exists($level, "dropItem")){
				foreach($leftovers as $leftover){
					$level->dropItem($player, $leftover);
				}
			}
		}
		return true;
	}
}
