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

namespace lycore\inventory;

use lycore\event\inventory\InventoryTransactionEvent;
use lycore\item\Item;
use lycore\network\protocol\ProtocolCompatibility;
use lycore\Player;
use lycore\Server;

/**
 * This TransactionGroup only allows doing Transaction between one / two inventories
 */
class SimpleTransactionGroup implements TransactionGroup{
	private $creationTime;
	protected $hasExecuted = false;
	/** @var Player */
	protected $source = null;

	/** @var Inventory[] */
	protected $inventories = [];

	/** @var Transaction[] */
	protected $transactions = [];

	/**
	 * @param Player $source
	 */
	public function __construct(Player $source = null){
		$this->creationTime = microtime(true);
		$this->source = $source;
	}

	/**
	 * @return Player
	 */
	public function getSource(){
		return $this->source;
	}

	public function getCreationTime(){
		return $this->creationTime;
	}

	public function getInventories(){
		return $this->inventories;
	}

	public function getTransactions(){
		return $this->transactions;
	}

	/**
	 * @return Item[]
	 */
	protected function getNormalizedTargetItems() : array{
		$targets = [];
		foreach($this->transactions as $hash => $transaction){
			$targets[$hash] = $transaction->getTargetItem();
		}

		if(!$this->source instanceof Player or !ProtocolCompatibility::requiresLegacyRedstoneMapping((int) $this->source->getProtocol())){
			return $targets;
		}

		$candidates = [];
		foreach($this->transactions as $transaction){
			$sourceItem = $transaction->getSourceItem();
			if($sourceItem->getId() !== Item::AIR and $sourceItem->getCount() > 0){
				$candidates[] = clone $sourceItem;
			}
		}

		foreach($targets as $hash => $targetItem){
			if($targetItem->getId() === Item::AIR or $targetItem->getCount() <= 0){
				continue;
			}

			$normalizedTarget = ProtocolCompatibility::normalizeClientItemForProtocol((int) $this->source->getProtocol(), $targetItem);
			if($normalizedTarget !== $targetItem){
				$normalizedTarget->setCount($targetItem->getCount());
				$targets[$hash] = $normalizedTarget;
				$targetItem = $normalizedTarget;
			}

			foreach($candidates as $index => $candidate){
				if($candidate->getCount() < $targetItem->getCount()){
					continue;
				}

				if($candidate->deepEquals($targetItem, $targetItem->getDamage() !== null, true, false)){
					$normalized = clone $candidate;
					$normalized->setCount($targetItem->getCount());
					$targets[$hash] = $normalized;
					$candidate->setCount($candidate->getCount() - $targetItem->getCount());
					if($candidate->getCount() <= 0){
						unset($candidates[$index]);
					}else{
						$candidates[$index] = $candidate;
					}
					continue 2;
				}
			}

			foreach($candidates as $index => $candidate){
				if($candidate->getCount() < $targetItem->getCount()){
					continue;
				}

				$targetMeta = $targetItem->getDamage();
				$candidateMeta = $candidate->getDamage();
				if(ProtocolCompatibility::itemsMatchAfterProtocolMapping(
					(int) $this->source->getProtocol(),
					$candidate->getId(),
					$candidateMeta === null ? 0 : (int) $candidateMeta,
					$candidate->getCount(),
					$targetItem->getId(),
					$targetMeta === null ? 0 : (int) $targetMeta,
					$targetItem->getCount()
				)){
					$normalized = clone $candidate;
					$normalized->setCount($targetItem->getCount());
					$targets[$hash] = $normalized;
					$candidate->setCount($candidate->getCount() - $targetItem->getCount());
					if($candidate->getCount() <= 0){
						unset($candidates[$index]);
					}else{
						$candidates[$index] = $candidate;
					}
					break;
				}
			}
		}

		return $targets;
	}

	public function addTransaction(Transaction $transaction){
		if(isset($this->transactions[spl_object_hash($transaction)])){
			return;
		}
		foreach($this->transactions as $hash => $tx){
			if($tx->getInventory() === $transaction->getInventory() and $tx->getSlot() === $transaction->getSlot()){
				if($transaction->getCreationTime() >= $tx->getCreationTime()){
					unset($this->transactions[$hash]);
				}else{
					return;
				}
			}
		}
		$this->transactions[spl_object_hash($transaction)] = $transaction;
		$this->inventories[spl_object_hash($transaction->getInventory())] = $transaction->getInventory();
	}

	/**
	 * @param Item[] $needItems
	 * @param Item[] $haveItems
	 *
	 * @return bool
	 */
	protected function matchItems(array &$needItems, array &$haveItems, array $normalizedTargetItems = null){
		if($normalizedTargetItems === null){
			$normalizedTargetItems = $this->getNormalizedTargetItems();
		}
		foreach($this->transactions as $key => $ts){
			$targetItem = $normalizedTargetItems[$key];
			if($targetItem->getId() !== Item::AIR){
				$needItems[] = $targetItem;
			}
			$checkSourceItem = $ts->getInventory()->getItem($ts->getSlot());
			$sourceItem = $ts->getSourceItem();
			if(!$checkSourceItem->deepEquals($sourceItem) or $sourceItem->getCount() !== $checkSourceItem->getCount()){
				return false;
			}
			if($sourceItem->getId() !== Item::AIR){
				$haveItems[] = $sourceItem;
			}
		}

		foreach($needItems as $i => $needItem){
			foreach($haveItems as $j => $haveItem){
				if($needItem->deepEquals($haveItem) or ($this->source instanceof Player and ProtocolCompatibility::itemsMatchAfterClientNormalization((int) $this->source->getProtocol(), $needItem, $haveItem))){
					$amount = min($needItem->getCount(), $haveItem->getCount());
					$needItem->setCount($needItem->getCount() - $amount);
					$haveItem->setCount($haveItem->getCount() - $amount);
					if($haveItem->getCount() === 0){
						unset($haveItems[$j]);
					}
					if($needItem->getCount() === 0){
						unset($needItems[$i]);
						break;
					}
				}
			}
		}

		return true;
	}

	protected function hasInventoryChanges(array $normalizedTargetItems) : bool{
		foreach($this->transactions as $key => $transaction){
			$sourceItem = $transaction->getSourceItem();
			$targetItem = $normalizedTargetItems[$key];
			if(!$sourceItem->deepEquals($targetItem) || $sourceItem->getCount() !== $targetItem->getCount()){
				return true;
			}
		}

		return false;
	}

	public function canExecute(){
		$haveItems = [];
		$needItems = [];
		$normalizedTargetItems = $this->getNormalizedTargetItems();

		return $this->hasInventoryChanges($normalizedTargetItems) and $this->matchItems($haveItems, $needItems, $normalizedTargetItems) and count($haveItems) === 0 and count($needItems) === 0 and count($this->transactions) > 0;
	}

	public function execute(){
		if($this->hasExecuted() or !$this->canExecute()){
			return false;
		}

		Server::getInstance()->getPluginManager()->callEvent($ev = new InventoryTransactionEvent($this));
		if($ev->isCancelled()){
			foreach($this->inventories as $inventory){
				if($inventory instanceof PlayerInventory){
					$inventory->sendArmorContents($this->getSource());
				}
				$inventory->sendContents($this->getSource());
			}

			return false;
		}

		$normalizedTargetItems = $this->getNormalizedTargetItems();
		foreach($this->transactions as $hash => $transaction){
			$transaction->getInventory()->setItem($transaction->getSlot(), $normalizedTargetItems[$hash]);
		}

		$this->hasExecuted = true;

		return true;
	}

	public function hasExecuted(){
		return $this->hasExecuted;
	}
}
