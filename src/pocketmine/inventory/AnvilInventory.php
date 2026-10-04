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
 * 移植自 lycore\inventory\AnvilInventory，命名空间改为 pocketmine\inventory。
 * 支持附魔书附魔、同类合并、材料修复与重命名。
 * 修复：纯重命名模式改为基于服务端目标物品克隆并套用客户端名称，
 * 避免客户端结果物品丢失 NBT（例如地图数据）导致物品异常。
 */

namespace pocketmine\inventory;

use pocketmine\nbt\NBT;
use pocketmine\nbt\tag\CompoundTag;
use pocketmine\item\enchantment\Enchantment;
use pocketmine\item\Armor;
use pocketmine\item\Item;
use pocketmine\item\Tool;
use pocketmine\level\Position;
use pocketmine\network\protocol\ContainerSetSlotPacket;
use pocketmine\network\protocol\ProtocolCompatibility;
use pocketmine\Player;

class AnvilInventory extends ContainerInventory{

	const TARGET = 0;
	const SACRIFICE = 1;
	const RESULT = 2;

	const MODE_RENAME = "rename";
	const MODE_BOOK_APPLY = "book_apply";
	const MODE_ITEM_MERGE = "item_merge";
	const MODE_MATERIAL_REPAIR = "material_repair";

	private $pendingMode = null;
	private $pendingResultItem = null;
	private $pendingCost = 0;
	private $pendingAppliedEnchantments = [];
	private $pendingSacrificeCount = 1;
	private $pendingRenameChanged = false;
	private $pendingTargetSnapshot = null;
	private $pendingSacrificeSnapshot = null;
	private $awaitingPreviewAck = false;

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
		if(!$this->hasPendingOperation()){
			return false;
		}

		$resultItem = clone $this->pendingResultItem;
		if($resultItem->hasCustomName()){
			$resultItem->setCustomName($this->filterCustomName($resultItem->getCustomName()));
		}

		$cost = $this->isSacrificeOperationMode($this->pendingMode) ? $this->pendingCost : 1;
		$level = $player->getExpLevel();
		if($level < $cost){
			$this->restorePendingState();
			return false;
		}

		$player->setExpLevel($level - $cost);
		if($this->isSacrificeOperationMode($this->pendingMode)){
			$this->applySacrificeOperationState();
		}else{
			$this->clearAll();
		}

		$player->getInventory()->addItem($resultItem);
		$this->resetPendingOperation();

		return true;
	}

	function have_emoji($str){
		$mat = [];
		preg_match_all('/./u', $str, $mat);
		foreach($mat[0] as $v){
			if(strlen($v) > 3){
				return true;
			}
		}
		return false;
	}

	private function filterCustomName($name){
		if($this->have_emoji($name)){
			return "§c不允许emoji";
		}

		return $name;
	}

	public function onRename(Player $player, $slot, $sourceItem, $resultItem){
		if((int) $slot !== self::RESULT){
			return null;
		}
		$target = $this->getItem(self::TARGET);
		$sacrifice = $this->getItem(self::SACRIFICE);
		if($resultItem instanceof Item and $resultItem->getId() !== Item::AIR and $sacrifice->getId() === Item::AIR){
			$resultItem = ProtocolCompatibility::normalizeAnvilClientResultForProtocol((int) $player->getProtocol(), $target, $resultItem);
		}
		if($sacrifice->getId() === Item::ENCHANTED_BOOK){
			if($this->pendingMode === self::MODE_BOOK_APPLY){
				if(!($resultItem instanceof Item) or $resultItem->getId() === Item::AIR){
					return true;
				}
			}else{
				if(!($resultItem instanceof Item)){
					$this->resetPendingOperation();
					return false;
				}
			}

			if($target->getId() === Item::AIR){
				$this->resetPendingOperation();
				return false;
			}
			if($resultItem instanceof Item and $resultItem->getId() !== Item::AIR){
				if($resultItem->getCount() !== $target->getCount() or !$resultItem->deepEquals($target, true, false, true)){
					$this->resetPendingOperation();
					return false;
				}
				$hadPendingBookPreview = $this->pendingMode === self::MODE_BOOK_APPLY and $this->pendingResultItem instanceof Item;
				if($this->awaitingPreviewAck and $this->pendingResultItem instanceof Item and $this->clientResultMatchesExpected($resultItem, $this->pendingResultItem, $target)){
					$this->awaitingPreviewAck = false;
					return 2;
				}
				if($hadPendingBookPreview and $this->clientResultLooksLikePreviewAck($resultItem, $target)){
					if($this->awaitingPreviewAck){
						$this->awaitingPreviewAck = false;
					}
					return 2;
				}
				$prepared = $this->prepareBookApplyOperation($target, $sacrifice, $resultItem);
				if(!$prepared and $hadPendingBookPreview){
					return 2;
				}
				return $prepared;
			}

			return $this->pendingMode === self::MODE_BOOK_APPLY;
		}

		if($this->canMergeItems($target, $sacrifice)){
			if($this->pendingMode === self::MODE_ITEM_MERGE){
				if(!($resultItem instanceof Item) or $resultItem->getId() === Item::AIR){
					return true;
				}
			}else{
				if(!($resultItem instanceof Item)){
					$this->resetPendingOperation();
					return false;
				}
			}

			if($resultItem instanceof Item and $resultItem->getId() !== Item::AIR){
				$hadPendingMergePreview = $this->pendingMode === self::MODE_ITEM_MERGE and $this->pendingResultItem instanceof Item;
				if($this->awaitingPreviewAck and $this->pendingResultItem instanceof Item and $this->clientResultMatchesExpected($resultItem, $this->pendingResultItem, $target)){
					$this->awaitingPreviewAck = false;
					return 2;
				}
				$prepared = $this->prepareItemMergeOperation($target, $sacrifice, $resultItem);
				if(!$prepared and $hadPendingMergePreview){
					return 2;
				}
				return $prepared;
			}

			return $this->pendingMode === self::MODE_ITEM_MERGE;
		}

		if($this->canRepairWithMaterial($target, $sacrifice)){
			if($this->pendingMode === self::MODE_MATERIAL_REPAIR){
				if(!($resultItem instanceof Item) or $resultItem->getId() === Item::AIR){
					return true;
				}
			}else{
				if(!($resultItem instanceof Item)){
					$this->resetPendingOperation();
					return false;
				}
			}

			if($resultItem instanceof Item and $resultItem->getId() !== Item::AIR){
				$hadPendingRepairPreview = $this->pendingMode === self::MODE_MATERIAL_REPAIR and $this->pendingResultItem instanceof Item;
				if($this->awaitingPreviewAck and $this->pendingResultItem instanceof Item and $this->clientResultMatchesExpected($resultItem, $this->pendingResultItem, $target)){
					$this->awaitingPreviewAck = false;
					return 2;
				}
				$prepared = $this->prepareMaterialRepairOperation($target, $sacrifice, $resultItem);
				if(!$prepared and $hadPendingRepairPreview){
					return 2;
				}
				return $prepared;
			}

			return $this->pendingMode === self::MODE_MATERIAL_REPAIR;
		}

		if(!($resultItem instanceof Item)){
			$this->resetPendingOperation();
			return false;
		}
		if($target->getId() === Item::AIR or $resultItem->getId() === Item::AIR){
			$this->resetPendingOperation();
			return false;
		}
		if($sacrifice->getId() !== Item::AIR){
			$this->resetPendingOperation();
			return false;
		}
		if($resultItem->getCount() !== $target->getCount() or !$resultItem->deepEquals($target, true, false, true)){
			// Item does not match target item. Everything must match except the tags.
			$this->resetPendingOperation();
			return false;
		}

		// 以服务端目标物品为基准，仅套用客户端名称，保留目标 NBT（地图等数据不丢失）
		$expectedResult = clone $target;
		if($resultItem->hasCustomName()){
			$expectedResult->setCustomName($resultItem->getCustomName());
		}else{
			$expectedResult->clearCustomName();
		}

		$this->cachePendingOperation(
			self::MODE_RENAME,
			$expectedResult,
			1,
			[],
			$this->isRenameChanged($target, $resultItem),
			$target,
			$sacrifice
		);

		return true;
	}

	public function onSlotChange($index, $before){
		parent::onSlotChange($index, $before);
		if($index !== self::TARGET and $index !== self::SACRIFICE){
			return;
		}

		$hadPreview = $this->isSacrificeOperationMode($this->pendingMode);
		$this->resetPendingOperation();
		$this->refreshAnvilPreview($hadPreview);
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

		$this->resetPendingOperation();
		$this->getHolder()->getLevel()->dropItem($this->getHolder()->add(0.5, 0.5, 0.5), $this->getItem(1));
		$this->getHolder()->getLevel()->dropItem($this->getHolder()->add(0.5, 0.5, 0.5), $this->getItem(0));
		$who->usingAnvil = false;

		$this->clear(0);
		$this->clear(1);
		$this->clear(2);
	}

	public function hasPendingOperation() : bool{
		return $this->pendingMode !== null and $this->pendingResultItem instanceof Item;
	}

	public function isBookApplyPending() : bool{
		return $this->pendingMode === self::MODE_BOOK_APPLY and $this->pendingResultItem instanceof Item;
	}

	public function isResultTakePending() : bool{
		return $this->isSacrificeOperationMode($this->pendingMode) and $this->pendingResultItem instanceof Item;
	}

	public function isAwaitingPreviewAck() : bool{
		return $this->awaitingPreviewAck;
	}

	public function clearPreviewAck(){
		$this->awaitingPreviewAck = false;
	}

	private function resetPendingOperation(){
		$this->pendingMode = null;
		$this->pendingResultItem = null;
		$this->pendingCost = 0;
		$this->pendingAppliedEnchantments = [];
		$this->pendingSacrificeCount = 1;
		$this->pendingRenameChanged = false;
		$this->pendingTargetSnapshot = null;
		$this->pendingSacrificeSnapshot = null;
		$this->awaitingPreviewAck = false;
	}

	private function cachePendingOperation(string $mode, Item $resultItem, int $cost, array $appliedEnchantments, bool $renameChanged, Item $target, Item $sacrifice, int $sacrificeCount = 1){
		$cachedResultItem = clone $resultItem;

		$this->pendingMode = $mode;
		$this->pendingResultItem = $cachedResultItem;
		$this->pendingCost = $cost;
		$this->pendingAppliedEnchantments = $this->cloneEnchantments($appliedEnchantments);
		$this->pendingSacrificeCount = max(0, $sacrificeCount);
		$this->pendingRenameChanged = $renameChanged;
		$this->pendingTargetSnapshot = clone $target;
		$this->pendingSacrificeSnapshot = clone $sacrifice;
	}

	private function isSacrificeOperationMode($mode) : bool{
		return $mode === self::MODE_BOOK_APPLY or $mode === self::MODE_ITEM_MERGE or $mode === self::MODE_MATERIAL_REPAIR;
	}

	private function prepareBookApplyOperation(Item $target, Item $sacrifice, Item $clientResult) : bool{
		if(!$this->canUseAsAnvilEnchantTarget($target) or !$sacrifice->hasEnchantments()){
			return false;
		}

		$expectedResult = clone $target;
		$appliedEnchantments = [];
		foreach($sacrifice->getEnchantments() as $bookEnchantment){
			$applied = $this->resolveBookEnchantment($expectedResult, $bookEnchantment);
			if($applied instanceof Enchantment){
				$expectedResult->addEnchantment($applied);
				$appliedEnchantments[] = $applied;
			}
		}

		if(count($appliedEnchantments) === 0){
			return false;
		}

		$renameChanged = $this->applyClientRenameToExpectedItem($target, $expectedResult, $clientResult);
		$cost = $this->calculateBookApplyCost($appliedEnchantments, $renameChanged);
		if($cost <= 0){
			return false;
		}

		if(!$this->clientResultMatchesExpected($clientResult, $expectedResult, $target)){
			return false;
		}

		$this->cachePendingOperation(
			self::MODE_BOOK_APPLY,
			$expectedResult,
			$cost,
			$appliedEnchantments,
			$renameChanged,
			$target,
			$sacrifice
		);

		return true;
	}

	private function applySacrificeOperationState(){
		$sacrifice = $this->pendingSacrificeSnapshot instanceof Item ? clone $this->pendingSacrificeSnapshot : null;
		$consume = max(1, $this->pendingSacrificeCount);

		$this->clear(self::TARGET);
		$this->sendPreviewResult(null);

		if($sacrifice instanceof Item and $sacrifice->getId() !== Item::AIR and $sacrifice->getCount() > $consume){
			$sacrifice->setCount($sacrifice->getCount() - $consume);
			$this->setItem(self::SACRIFICE, $sacrifice);
		}else{
			$this->clear(self::SACRIFICE);
		}
	}

	private function restorePendingState(){
		$mode = $this->pendingMode;
		$resultItem = $this->pendingResultItem instanceof Item ? clone $this->pendingResultItem : null;
		$cost = $this->pendingCost;
		$appliedEnchantments = $this->cloneEnchantments($this->pendingAppliedEnchantments);
		$sacrificeCount = $this->pendingSacrificeCount;
		$renameChanged = $this->pendingRenameChanged;
		$targetSnapshot = $this->pendingTargetSnapshot instanceof Item ? clone $this->pendingTargetSnapshot : null;
		$sacrificeSnapshot = $this->pendingSacrificeSnapshot instanceof Item ? clone $this->pendingSacrificeSnapshot : null;

		$this->restoreSlotFromSnapshot(self::TARGET, $targetSnapshot);
		$this->restoreSlotFromSnapshot(self::SACRIFICE, $sacrificeSnapshot);
		if($this->isSacrificeOperationMode($mode) and $resultItem instanceof Item and $targetSnapshot instanceof Item and $sacrificeSnapshot instanceof Item){
			$this->cachePendingOperation($mode, $resultItem, $cost, $appliedEnchantments, $renameChanged, $targetSnapshot, $sacrificeSnapshot, $sacrificeCount);
			$this->sendPreviewResult($resultItem);
		}else{
			$this->sendPreviewResult(null);
		}
	}

	private function restoreSlotFromSnapshot(int $slot, $snapshot){
		if($snapshot instanceof Item and $snapshot->getId() !== Item::AIR and $snapshot->getCount() > 0){
			$this->setItem($slot, clone $snapshot);
		}else{
			$this->clear($slot);
		}
	}

	private function canUseAsAnvilEnchantTarget(Item $target) : bool{
		if($target->getId() === Item::AIR or $target->getId() === Item::BOOK){
			return false;
		}
		if($target->getId() === Item::ENCHANTED_BOOK){
			return true;
		}

		if($target->isArmor()){
			return true;
		}

		if($target->isSword() !== false or $target->isAxe() !== false or $target->isPickaxe() !== false or $target->isShovel() !== false or $target->isHoe() !== false or $target->isShears() !== false){
			return true;
		}

		return $target->getId() === Item::BOW or $target->getId() === Item::FISHING_ROD or $target->getId() === Item::FLINT_STEEL;
	}

	private function canMergeItems(Item $target, Item $sacrifice) : bool{
		return $target->getId() !== Item::AIR and
			$sacrifice->getId() !== Item::AIR and
			$target->getId() === $sacrifice->getId() and
			$target->getCount() === 1 and
			$sacrifice->getCount() >= 1 and
			$this->canUseAsAnvilEnchantTarget($target);
	}

	private function prepareItemMergeOperation(Item $target, Item $sacrifice, Item $clientResult) : bool{
		$preview = $this->calculateItemMergePreview($target, $sacrifice, $clientResult);
		if($preview === null){
			return false;
		}
		if(!$this->clientResultMatchesExpected($clientResult, $preview["result"], $target)){
			return false;
		}

		$this->cachePendingOperation(
			self::MODE_ITEM_MERGE,
			$preview["result"],
			$preview["cost"],
			$preview["applied"],
			$preview["rename"],
			$target,
			$sacrifice
		);

		return true;
	}

	private function prepareMaterialRepairOperation(Item $target, Item $sacrifice, Item $clientResult) : bool{
		$preview = $this->calculateMaterialRepairPreview($target, $sacrifice, $clientResult);
		if($preview === null){
			return false;
		}
		if(!$this->clientResultMatchesExpected($clientResult, $preview["result"], $target)){
			return false;
		}

		$this->cachePendingOperation(
			self::MODE_MATERIAL_REPAIR,
			$preview["result"],
			$preview["cost"],
			[],
			$preview["rename"],
			$target,
			$sacrifice,
			$preview["sacrifice"]
		);

		return true;
	}

	private function canRepairWithMaterial(Item $target, Item $sacrifice) : bool{
		if($target->getId() === Item::AIR or $sacrifice->getId() === Item::AIR){
			return false;
		}
		if($target->getCount() !== 1 or $sacrifice->getCount() < 1){
			return false;
		}
		if(!$this->canUseAsAnvilEnchantTarget($target)){
			return false;
		}
		if($target->getDamage() === null or $target->getDamage() <= 0){
			return false;
		}

		$maxDurability = $target->getMaxDurability();
		if($maxDurability === false or $maxDurability <= 0){
			return false;
		}

		return $this->getAnvilRepairMaterialId($target) === $sacrifice->getId();
	}

	private function calculateMaterialRepairPreview(Item $target, Item $sacrifice, Item $clientResult = null){
		if(!$this->canRepairWithMaterial($target, $sacrifice)){
			return null;
		}

		$maxDurability = $target->getMaxDurability();
		$damage = min($maxDurability, max(0, $target->getDamage()));
		$repairPerUnit = max(1, (int) floor($maxDurability / 4));
		$used = 0;
		while($damage > 0 and $used < $sacrifice->getCount()){
			$damage -= min($repairPerUnit, $damage);
			++$used;
		}

		if($used <= 0 or $damage >= $target->getDamage()){
			return null;
		}

		$expectedResult = clone $target;
		$expectedResult->setDamage($damage);
		$cost = $used;
		$renameChanged = false;
		if($clientResult instanceof Item){
			$renameChanged = $this->applyClientRenameToExpectedItem($target, $expectedResult, $clientResult);
			if($renameChanged){
				++$cost;
			}
		}

		return [
			"result" => $expectedResult,
			"applied" => [],
			"cost" => $cost,
			"rename" => $renameChanged,
			"sacrifice" => $used
		];
	}

	private function getAnvilRepairMaterialId(Item $target){
		if($target->isArmor()){
			switch($target->getArmorTier()){
				case Armor::TIER_LEATHER:
					return Item::LEATHER;

				case Armor::TIER_CHAIN:
				case Armor::TIER_IRON:
					return Item::IRON_INGOT;

				case Armor::TIER_DIAMOND:
					return Item::DIAMOND;

				case Armor::TIER_GOLD:
					return Item::GOLD_INGOT;
			}
		}

		$toolTier = $this->getToolTier($target);
		if($toolTier !== false){
			switch($toolTier){
				case Tool::TIER_WOODEN:
					return Item::PLANK;

				case Tool::TIER_STONE:
					return Item::COBBLESTONE;

				case Tool::TIER_IRON:
					return Item::IRON_INGOT;

				case Tool::TIER_DIAMOND:
					return Item::DIAMOND;

				case Tool::TIER_GOLD:
					return Item::GOLD_INGOT;
			}
		}

		switch($target->getId()){
			case Item::BOW:
			case Item::FISHING_ROD:
				return Item::STRING;

			case Item::SHEARS:
			case Item::FLINT_STEEL:
				return Item::IRON_INGOT;
		}

		return false;
	}

	private function getToolTier(Item $target){
		if(($tier = $target->isPickaxe()) !== false){
			return $tier;
		}
		if(($tier = $target->isAxe()) !== false){
			return $tier;
		}
		if(($tier = $target->isSword()) !== false){
			return $tier;
		}
		if(($tier = $target->isShovel()) !== false){
			return $tier;
		}
		if(($tier = $target->isHoe()) !== false){
			return $tier;
		}

		return false;
	}

	private function calculateItemMergePreview(Item $target, Item $sacrifice, Item $clientResult = null){
		if(!$this->canMergeItems($target, $sacrifice)){
			return null;
		}

		$expectedResult = clone $target;
		$changed = false;
		$appliedEnchantments = [];
		$cost = 0;

		$maxDurability = $target->getMaxDurability();
		if($maxDurability !== false and $maxDurability > 0){
			$targetRemaining = max(0, $maxDurability - $target->getDamage());
			$sacrificeRemaining = max(0, $maxDurability - $sacrifice->getDamage());
			$combinedRemaining = min($maxDurability, $targetRemaining + $sacrificeRemaining + (int) floor($maxDurability * 0.12));
			$newDamage = $maxDurability - $combinedRemaining;
			if($newDamage < $expectedResult->getDamage()){
				$expectedResult->setDamage($newDamage);
				$changed = true;
				$cost += 2;
			}
		}

		foreach($sacrifice->getEnchantments() as $sacrificeEnchantment){
			$applied = $this->resolveMergeEnchantment($expectedResult, $sacrificeEnchantment);
			if($applied instanceof Enchantment){
				$expectedResult->addEnchantment($applied);
				$appliedEnchantments[] = $applied;
				$cost += max(1, $this->getBookCostMultiplier($applied->getId())) * $applied->getLevel();
				$changed = true;
			}
		}

		$renameChanged = false;
		if($clientResult instanceof Item){
			$renameChanged = $this->applyClientRenameToExpectedItem($target, $expectedResult, $clientResult);
			if($renameChanged){
				$cost += 1;
				$changed = true;
			}
		}

		if(!$changed or $cost <= 0){
			return null;
		}

		return [
			"result" => $expectedResult,
			"applied" => $appliedEnchantments,
			"cost" => $cost,
			"rename" => $renameChanged
		];
	}

	private function resolveMergeEnchantment(Item $target, Enchantment $sacrificeEnchantment){
		$enchantmentId = $sacrificeEnchantment->getId();
		if($enchantmentId === Enchantment::TYPE_INVALID or !$this->canApplyBookEnchantment($target, $sacrificeEnchantment)){
			return null;
		}

		foreach($target->getEnchantments() as $existing){
			if($existing->getId() !== $enchantmentId and $this->isConflictingEnchantment($enchantmentId, $existing->getId())){
				return null;
			}
		}

		$maxLevel = Enchantment::getEnchantMaxLevel($enchantmentId);
		$sacrificeLevel = min($sacrificeEnchantment->getLevel(), $maxLevel);
		$oldLevel = $target->getEnchantmentLevel($enchantmentId);
		$newLevel = $oldLevel > 0 ? ($oldLevel === $sacrificeLevel ? min($maxLevel, $oldLevel + 1) : max($oldLevel, $sacrificeLevel)) : $sacrificeLevel;
		if($newLevel <= $oldLevel){
			return null;
		}

		return Enchantment::getEnchantment($enchantmentId)->setLevel($newLevel);
	}

	private function resolveBookEnchantment(Item $target, Enchantment $bookEnchantment){
		$enchantmentId = $bookEnchantment->getId();
		if($enchantmentId === Enchantment::TYPE_INVALID or !$this->canApplyBookEnchantment($target, $bookEnchantment)){
			return null;
		}

		foreach($target->getEnchantments() as $existing){
			if($existing->getId() !== $enchantmentId and $this->isConflictingEnchantment($enchantmentId, $existing->getId())){
				return null;
			}
		}

		$maxLevel = Enchantment::getEnchantMaxLevel($enchantmentId);
		$bookLevel = min($bookEnchantment->getLevel(), $maxLevel);
		$oldLevel = $target->getEnchantmentLevel($enchantmentId);
		if($oldLevel > 0){
			$newLevel = $oldLevel === $bookLevel ? min($maxLevel, $oldLevel + 1) : max($oldLevel, $bookLevel);
			if($newLevel <= $oldLevel){
				return null;
			}
		}else{
			$newLevel = $bookLevel;
		}

		return Enchantment::getEnchantment($enchantmentId)->setLevel($newLevel);
	}

	private function canApplyBookEnchantment(Item $target, Enchantment $enchantment) : bool{
		if($target->getId() === Item::ENCHANTED_BOOK){
			return true;
		}

		switch($enchantment->getId()){
			case Enchantment::TYPE_ARMOR_PROTECTION:
			case Enchantment::TYPE_ARMOR_FIRE_PROTECTION:
			case Enchantment::TYPE_ARMOR_EXPLOSION_PROTECTION:
			case Enchantment::TYPE_ARMOR_PROJECTILE_PROTECTION:
			case Enchantment::TYPE_ARMOR_THORNS:
				return $target->isArmor();

			case Enchantment::TYPE_ARMOR_FALL_PROTECTION:
			case Enchantment::TYPE_WATER_SPEED:
				return $target->isBoots();

			case Enchantment::TYPE_WATER_BREATHING:
			case Enchantment::TYPE_WATER_AFFINITY:
				return $target->isHelmet();

			case Enchantment::TYPE_WEAPON_SHARPNESS:
			case Enchantment::TYPE_WEAPON_SMITE:
			case Enchantment::TYPE_WEAPON_ARTHROPODS:
				return $target->isSword() !== false or $target->isAxe() !== false;

			case Enchantment::TYPE_WEAPON_KNOCKBACK:
			case Enchantment::TYPE_WEAPON_FIRE_ASPECT:
			case Enchantment::TYPE_WEAPON_LOOTING:
				return $target->isSword() !== false;

			case Enchantment::TYPE_MINING_EFFICIENCY:
			case Enchantment::TYPE_MINING_SILK_TOUCH:
			case Enchantment::TYPE_MINING_FORTUNE:
				return $this->isMiningEnchantTarget($target);

			case Enchantment::TYPE_MINING_DURABILITY:
				return $this->canUseAsAnvilEnchantTarget($target);

			case Enchantment::TYPE_BOW_POWER:
			case Enchantment::TYPE_BOW_KNOCKBACK:
			case Enchantment::TYPE_BOW_FLAME:
			case Enchantment::TYPE_BOW_INFINITY:
				return $target->getId() === Item::BOW;

			case Enchantment::TYPE_FISHING_FORTUNE:
			case Enchantment::TYPE_FISHING_LURE:
				return $target->getId() === Item::FISHING_ROD;
		}

		return false;
	}

	private function isMiningEnchantTarget(Item $target) : bool{
		return $target->isPickaxe() !== false or $target->isAxe() !== false or $target->isShovel() !== false or $target->isHoe() !== false or $target->isShears() !== false;
	}

	private function isConflictingEnchantment(int $firstId, int $secondId) : bool{
		if($firstId === $secondId){
			return false;
		}
		if($this->isProtectionConflict($firstId, $secondId)){
			return true;
		}
		if($this->isDamageConflict($firstId, $secondId)){
			return true;
		}
		if(($firstId === Enchantment::TYPE_MINING_SILK_TOUCH and $secondId === Enchantment::TYPE_MINING_FORTUNE) or ($firstId === Enchantment::TYPE_MINING_FORTUNE and $secondId === Enchantment::TYPE_MINING_SILK_TOUCH)){
			return true;
		}

		return false;
	}

	private function isProtectionConflict(int $firstId, int $secondId) : bool{
		static $exclusiveProtectionTypes = [
			Enchantment::TYPE_ARMOR_PROTECTION => true,
			Enchantment::TYPE_ARMOR_FIRE_PROTECTION => true,
			Enchantment::TYPE_ARMOR_EXPLOSION_PROTECTION => true,
			Enchantment::TYPE_ARMOR_PROJECTILE_PROTECTION => true,
		];

		return isset($exclusiveProtectionTypes[$firstId]) and isset($exclusiveProtectionTypes[$secondId]);
	}

	private function isDamageConflict(int $firstId, int $secondId) : bool{
		static $exclusiveDamageTypes = [
			Enchantment::TYPE_WEAPON_SHARPNESS => true,
			Enchantment::TYPE_WEAPON_SMITE => true,
			Enchantment::TYPE_WEAPON_ARTHROPODS => true,
		];

		return isset($exclusiveDamageTypes[$firstId]) and isset($exclusiveDamageTypes[$secondId]);
	}

	private function applyClientRenameToExpectedItem(Item $target, Item $expectedResult, Item $clientResult) : bool{
		if($this->clientResultMatchesExpectedWithoutInheritedName($clientResult, $expectedResult, $target)){
			return false;
		}
		$renameChanged = $this->isRenameChanged($target, $clientResult);
		if(!$renameChanged){
			return false;
		}

		if($clientResult->hasCustomName()){
			$expectedResult->setCustomName($clientResult->getCustomName());
		}else{
			$expectedResult->clearCustomName();
		}

		return true;
	}

	private function isRenameChanged(Item $target, Item $result) : bool{
		$targetName = $target->hasCustomName() ? $target->getCustomName() : "";
		$resultName = $result->hasCustomName() ? $result->getCustomName() : "";

		return $targetName !== $resultName;
	}

	private function calculateBookApplyCost(array $appliedEnchantments, bool $renameChanged) : int{
		$cost = 0;
		foreach($appliedEnchantments as $enchantment){
			if(!$enchantment instanceof Enchantment){
				continue;
			}
			$cost += $this->getBookCostMultiplier($enchantment->getId()) * $enchantment->getLevel();
		}

		if($renameChanged){
			$cost += 7;
		}

		return $cost;
	}

	private function refreshAnvilPreview(bool $hadPreview){
		$target = $this->getItem(self::TARGET);
		$sacrifice = $this->getItem(self::SACRIFICE);
		$preview = $this->calculateBookApplyPreview($target, $sacrifice);
		$mode = self::MODE_BOOK_APPLY;
		if($preview === null){
			$preview = $this->calculateMaterialRepairPreview($target, $sacrifice);
			$mode = self::MODE_MATERIAL_REPAIR;
		}
		if($preview === null){
			$preview = $this->calculateItemMergePreview($target, $sacrifice);
			$mode = self::MODE_ITEM_MERGE;
		}
		if($preview === null){
			if($hadPreview){
				$this->sendPreviewResult(null);
			}
			return;
		}

		$this->cachePendingOperation(
			$mode,
			$preview["result"],
			$preview["cost"],
			$preview["applied"],
			isset($preview["rename"]) ? $preview["rename"] : false,
			$target,
			$sacrifice,
			isset($preview["sacrifice"]) ? $preview["sacrifice"] : 1
		);
		$this->sendPreviewResult($preview["result"]);
	}

	private function calculateBookApplyPreview(Item $target, Item $sacrifice){
		if(!$this->canUseAsAnvilEnchantTarget($target) or $sacrifice->getId() !== Item::ENCHANTED_BOOK or !$sacrifice->hasEnchantments()){
			return null;
		}

		$expectedResult = clone $target;
		$appliedEnchantments = [];
		foreach($sacrifice->getEnchantments() as $bookEnchantment){
			$applied = $this->resolveBookEnchantment($expectedResult, $bookEnchantment);
			if($applied instanceof Enchantment){
				$expectedResult->addEnchantment($applied);
				$appliedEnchantments[] = $applied;
			}
		}

		if(count($appliedEnchantments) === 0){
			return null;
		}

		$cost = $this->calculateBookApplyCost($appliedEnchantments, false);
		if($cost <= 0){
			return null;
		}

		return [
			"result" => $expectedResult,
			"applied" => $appliedEnchantments,
			"cost" => $cost
		];
	}

	private function sendPreviewResult($item){
		$this->awaitingPreviewAck = $item instanceof Item and $item->getId() !== Item::AIR and $item->getCount() > 0;
		foreach($this->getViewers() as $viewer){
			if(!$viewer instanceof Player){
				continue;
			}
			$windowId = $viewer->getWindowId($this);
			if($windowId === -1){
				continue;
			}

			$pk = new ContainerSetSlotPacket();
			$pk->windowid = $windowId;
			$pk->slot = self::RESULT;
			$pk->item = ($item instanceof Item and $item->getId() !== Item::AIR and $item->getCount() > 0) ? clone $item : Item::get(Item::AIR, 0, 0);
			$viewer->dataPacket($pk);
		}
	}

	private function getBookCostMultiplier(int $enchantmentId) : int{
		switch($enchantmentId){
			case Enchantment::TYPE_ARMOR_EXPLOSION_PROTECTION:
			case Enchantment::TYPE_WATER_BREATHING:
			case Enchantment::TYPE_WATER_SPEED:
			case Enchantment::TYPE_WATER_AFFINITY:
			case Enchantment::TYPE_WEAPON_FIRE_ASPECT:
			case Enchantment::TYPE_WEAPON_LOOTING:
			case Enchantment::TYPE_MINING_FORTUNE:
			case Enchantment::TYPE_BOW_KNOCKBACK:
			case Enchantment::TYPE_BOW_FLAME:
			case Enchantment::TYPE_FISHING_FORTUNE:
			case Enchantment::TYPE_FISHING_LURE:
				return 2;

			case Enchantment::TYPE_ARMOR_THORNS:
			case Enchantment::TYPE_MINING_SILK_TOUCH:
			case Enchantment::TYPE_BOW_INFINITY:
				return 4;

			default:
				return 1;
		}
	}

	private function clientResultMatchesExpected(Item $clientResult, Item $expectedResult, Item $target = null) : bool{
		if($clientResult->getCount() !== $expectedResult->getCount()){
			return false;
		}

		if(!$clientResult->deepEquals($expectedResult, true, true, true) and !$this->clientResultMatchesExpectedWithoutInheritedName($clientResult, $expectedResult, $target)){
			return false;
		}

		return $this->sameEnchantments($clientResult, $expectedResult);
	}

	private function clientResultMatchesExpectedWithoutInheritedName(Item $clientResult, Item $expectedResult, Item $target = null) : bool{
		if(!$target instanceof Item or !$target->hasCustomName() or !$expectedResult->hasCustomName()){
			return false;
		}
		if($expectedResult->getCustomName() !== $target->getCustomName() or $clientResult->hasCustomName()){
			return false;
		}
		if($clientResult->getId() !== $expectedResult->getId() or $clientResult->getDamage() !== $expectedResult->getDamage()){
			return false;
		}

		$clientTag = $this->getComparableNamedTagWithoutEnchantments($clientResult, true);
		$expectedTag = $this->getComparableNamedTagWithoutEnchantments($expectedResult, true);
		if($clientTag === null or $expectedTag === null){
			return $clientTag === null and $expectedTag === null;
		}

		return NBT::matchTree($clientTag, $expectedTag);
	}

	private function clientResultLooksLikePreviewAck(Item $clientResult, Item $target) : bool{
		if($clientResult->getId() !== $target->getId() or $clientResult->getCount() !== $target->getCount()){
			return false;
		}
		if(!$clientResult->deepEquals($target, true, false, true)){
			return false;
		}

		return $this->sameEnchantments($clientResult, $target);
	}

	private function getComparableNamedTagWithoutEnchantments(Item $item, bool $ignoreDisplayName = false){
		if(!$item->hasCompoundTag()){
			return null;
		}

		$tag = clone $item->getNamedTag();
		if(isset($tag->ench)){
			unset($tag->ench);
		}
		if($ignoreDisplayName and isset($tag->display) and $tag->display instanceof CompoundTag){
			unset($tag->display->Name);
			if($tag->display->getCount() === 0){
				unset($tag->display);
			}
		}

		return $tag->getCount() > 0 ? $tag : null;
	}

	private function sameEnchantments(Item $first, Item $second) : bool{
		return $this->normalizeEnchantments($first) === $this->normalizeEnchantments($second);
	}

	private function normalizeEnchantments(Item $item) : array{
		$normalized = [];
		foreach($item->getEnchantments() as $enchantment){
			$normalized[$enchantment->getId()] = $enchantment->getLevel();
		}
		ksort($normalized);

		return $normalized;
	}

	private function cloneEnchantments(array $enchantments) : array{
		$result = [];
		foreach($enchantments as $enchantment){
			if($enchantment instanceof Enchantment){
				$result[] = clone $enchantment;
			}
		}

		return $result;
	}
}
