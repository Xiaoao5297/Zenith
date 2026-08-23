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

use pocketmine\anticheat\AntiCheat;
use pocketmine\Player;
use pocketmine\item\Item;

class ItemCheck extends Check{

	/** @var array */
	private $violations = [];

	/** @var array */
	private $bannedItems = [];

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
		Item::SPLASH_POTION => 1
	];

	public function __construct(AntiCheat $antiCheat, array $config){
		parent::__construct($antiCheat, $config);
		$this->loadBannedItems();
	}

	private function loadBannedItems(){
		$banned = $this->getConfig()["banned-items"] ?? [];
		foreach($banned as $id){
			$this->bannedItems[] = (int) $id;
		}
	}

	public function clearPlayerData(string $playerName){
		unset($this->violations[$playerName]);
	}

	public function check(Player $player, Item $item){
		$name = $player->getName();

		if(!$this->enabled) return;

		$illegal = false;
		$reason = "";

		if($this->getConfig()["check-stack"] ?? true){
			if($this->isIllegalStack($item)){
				$illegal = true;
				$reason = "非法堆叠数量: " . $item->getCount();
			}
		}

		if(!$illegal && ($this->getConfig()["check-32k"] ?? true)){
			if($this->is32kWeapon($item)){
				$illegal = true;
				$reason = "32k武器检测";
			}
		}

		if(!$illegal && ($this->getConfig()["check-enchantments"] ?? true)){
			$enchantResult = $this->checkEnchantments($item);
			if($enchantResult !== null){
				$illegal = true;
				$reason = $enchantResult;
			}
		}

		if(!$illegal && ($this->getConfig()["check-nbt"] ?? true)){
			$nbtResult = $this->checkNBT($item);
			if($nbtResult !== null){
				$illegal = true;
				$reason = $nbtResult;
			}
		}

		if(!$illegal && ($this->getConfig()["check-banned"] ?? true)){
			if(in_array($item->getId(), $this->bannedItems)){
				$illegal = true;
				$reason = "禁止物品: " . $item->getName();
			}
		}

		if($illegal){
			$violation = isset($this->violations[$name]) ? $this->violations[$name] + 1 : 1;
			$this->violations[$name] = $violation;

			$this->antiCheat->logCheat($player->getName(), "ItemCheck", $reason);

			$maxViolations = (int) ($this->getConfig()["max-violations"] ?? 3);
			if($violation >= $maxViolations){
				$player->getInventory()->removeItem($item);
				$this->antiCheat->punish($player, "ItemCheck", $violation);
			}else{
				$player->getInventory()->removeItem($item);
				$player->sendMessage($this->antiCheat->getMessage("item-removed", ["item" => $item->getName()]));
			}
		}
	}

	private function isIllegalStack(Item $item){
		$maxStack = isset(self::$MAX_STACK_SIZES[$item->getId()]) ? self::$MAX_STACK_SIZES[$item->getId()] : 64;
		return $item->getCount() > $maxStack;
	}

	private function is32kWeapon(Item $item){
		$enchantments = $item->getEnchantments();
		foreach($enchantments as $enchant){
			if($enchant->getLevel() > 10){
				return true;
			}
		}
		return false;
	}

	private function checkEnchantments(Item $item){
		$enchantments = $item->getEnchantments();

		if(count($enchantments) > 10){
			return "附魔数量过多: " . count($enchantments);
		}

		foreach($enchantments as $enchant){
			$level = $enchant->getLevel();
			$maxLevel = 5;

			if($level > $maxLevel){
				return "非法附魔等级: " . $enchant->getName() . " " . $level;
			}
		}

		return null;
	}

	private function checkNBT(Item $item){
		$nbt = $item->getNamedTag();
		if($nbt === null){
			return null;
		}

		if($nbt->hasTag("RepairCost")){
			$repairCost = $nbt->getInt("RepairCost");
			if($repairCost > 100){
				return "异常修复费用: " . $repairCost;
			}
		}

		if($nbt->hasTag("display")){
			$display = $nbt->getCompoundTag("display");
			if($display !== null && $display->hasTag("Name")){
				$customName = $display->getString("Name");
				if(strlen($customName) > 100){
					return "异常物品名称长度";
				}
			}
			if($display !== null && $display->hasTag("Lore")){
				try{
					$lore = $display->getListTag("Lore");
					if($lore !== null && count($lore) > 10){
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
				if($modifiers !== null && count($modifiers) > 5){
					return "属性修饰符过多: " . count($modifiers);
				}
			}catch(\Exception $e){
				return "异常属性数据";
			}
		}

		return null;
	}
}
