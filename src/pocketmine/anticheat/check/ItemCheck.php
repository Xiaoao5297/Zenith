<?php

/***
 *  _____                _  __   __
 * /__  /  ___   ____   (_)/ /_ / /_
 *   / /  / _ \ / __ \ / // __// __ \
 *  / /__/  __// / / // // /_ / / / /
 * /____/\___//_/ /_//_/ \__//_/ /_/
 *
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * @author Xiaoao
 * @link https://github.com/Xiaoao5297/Zenith
 *
 *
*/


namespace pocketmine\anticheat\check;

use pocketmine\Player;
use pocketmine\item\Item;

/**
 * 非法物品检测：堆叠、32k、附魔、NBT、禁用物品。
 *
 * 修复了旧版读取 `check-banned`（配置中不存在）的问题，改为读取 `banned-items`；
 * 命中进入统一缓冲，alert 模式下不移除物品。
 */
class ItemCheck extends Check{

	/** @var array */
	private static $MAX_STACK_SIZES = [
		Item::DIAMOND_SWORD => 1,
		Item::IRON_SWORD => 1,
		Item::STONE_SWORD => 1,
		Item::WOODEN_SWORD => 1,
		Item::GOLD_SWORD => 1,
		Item::DIAMOND_PICKAXE => 1,
		Item::IRON_PICKAXE => 1,
		Item::STONE_PICKAXE => 1,
		Item::WOODEN_PICKAXE => 1,
		Item::GOLD_PICKAXE => 1,
		Item::DIAMOND_AXE => 1,
		Item::IRON_AXE => 1,
		Item::STONE_AXE => 1,
		Item::WOODEN_AXE => 1,
		Item::GOLD_AXE => 1,
		Item::DIAMOND_SHOVEL => 1,
		Item::IRON_SHOVEL => 1,
		Item::STONE_SHOVEL => 1,
		Item::WOODEN_SHOVEL => 1,
		Item::GOLD_SHOVEL => 1,
		Item::DIAMOND_HOE => 1,
		Item::IRON_HOE => 1,
		Item::STONE_HOE => 1,
		Item::WOODEN_HOE => 1,
		Item::GOLD_HOE => 1,
		Item::BOW => 1,
		Item::FISHING_ROD => 1,
		Item::SHEARS => 1,
		Item::FLINT_AND_STEEL => 1,
		Item::ENCHANTED_BOOK => 1,
		Item::POTION => 1,
		Item::SPLASH_POTION => 1,
	];

	public function getDisplayName() : string{
		return "Item";
	}

	public function clearPlayerData(string $playerName){
	}

	public function check(Player $player, Item $item){
		if(!$this->enabled) return;
		if($item->getId() === Item::AIR) return;

		$reason = $this->inspect($item);
		if($reason === null){
			$this->decay($player, 0.5);
			return;
		}

		$this->flag($player, $reason . " (" . $item->getName() . ")", 1.0);

		// 仅在惩罚模式下移除非法物品
		if($this->shouldEnforce()){
			$player->getInventory()->removeItem($item);
			$player->sendMessage($this->antiCheat->getMessage("item-removed", ["item" => $item->getName()]));
		}
	}

	private function inspect(Item $item) : ?string{
		if((bool) ($this->config["check-stack"] ?? true) and $this->isIllegalStack($item)){
			return "非法堆叠数量: " . $item->getCount();
		}

		if((bool) ($this->config["check-32k"] ?? true) and $this->is32kWeapon($item)){
			return "32k武器检测";
		}

		if((bool) ($this->config["check-enchantments"] ?? true)){
			$result = $this->checkEnchantments($item);
			if($result !== null){
				return $result;
			}
		}

		if((bool) ($this->config["check-nbt"] ?? true)){
			$result = $this->checkNBT($item);
			if($result !== null){
				return $result;
			}
		}

		if(in_array($item->getId(), (array) ($this->config["banned-items"] ?? []), true)){
			return "禁止物品: " . $item->getName();
		}

		return null;
	}

	private function isIllegalStack(Item $item) : bool{
		$maxStack = self::$MAX_STACK_SIZES[$item->getId()] ?? 64;
		return $item->getCount() > $maxStack;
	}

	private function is32kWeapon(Item $item) : bool{
		foreach($item->getEnchantments() as $enchant){
			if($enchant->getLevel() > 10){
				return true;
			}
		}
		return false;
	}

	private function checkEnchantments(Item $item) : ?string{
		$enchantments = $item->getEnchantments();

		if(count($enchantments) > 10){
			return "附魔数量过多: " . count($enchantments);
		}

		foreach($enchantments as $enchant){
			if($enchant->getLevel() > 5){
				return "非法附魔等级: " . $enchant->getName() . " " . $enchant->getLevel();
			}
		}

		return null;
	}

	private function checkNBT(Item $item) : ?string{
		$nbt = $item->getNamedTag();
		if($nbt === null){
			return null;
		}

		if($nbt->hasTag("RepairCost") and $nbt->getInt("RepairCost") > 100){
			return "异常修复费用: " . $nbt->getInt("RepairCost");
		}

		if($nbt->hasTag("display")){
			$display = $nbt->getCompoundTag("display");
			if($display !== null and $display->hasTag("Name") and strlen($display->getString("Name")) > 100){
				return "异常物品名称长度";
			}
			if($display !== null and $display->hasTag("Lore")){
				try{
					$lore = $display->getListTag("Lore");
					if($lore !== null and count($lore) > 10){
						return "Lore行数过多: " . count($lore);
					}
				}catch(\Exception $e){
					return "异常Lore数据";
				}
			}
		}

		if($nbt->hasTag("AttributeModifiers")){
			try{
				$modifiers = $nbt->getListTag("AttributeModifiers");
				if($modifiers !== null and count($modifiers) > 5){
					return "属性修饰符过多: " . count($modifiers);
				}
			}catch(\Exception $e){
				return "异常属性数据";
			}
		}

		return null;
	}
}
