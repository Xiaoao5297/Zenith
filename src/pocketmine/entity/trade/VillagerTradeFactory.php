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

use pocketmine\entity\Villager;
use pocketmine\item\Item;

class VillagerTradeFactory{
	/**
	 * @return VillagerTradeOffer[]
	 */
	public static function generateOffers(int $profession, $seed = null) : array{
		$state = $seed === null ? mt_rand(1, PHP_INT_MAX) : $seed;
		$offers = [];

		foreach(self::templatesForProfession($profession) as $template){
			$buyA = self::itemFromSpec($template["buyA"], $state);
			$buyB = isset($template["buyB"]) ? self::itemFromSpec($template["buyB"], $state) : null;
			$sell = self::itemFromSpec($template["sell"], $state);
			$maxUses = self::randomBetween($template["maxUses"][0], $template["maxUses"][1], $state);
			$offers[] = new VillagerTradeOffer($buyA, $buyB, $sell, $maxUses, 0);
		}

		return $offers;
	}

	/**
	 * @param VillagerTradeOffer[] $offers
	 */
	public static function fingerprint(array $offers) : string{
		return implode("|", array_map(function(VillagerTradeOffer $offer) : string{
			return $offer->fingerprint();
		}, $offers));
	}

	private static function itemFromSpec(array $spec, int &$state) : Item{
		$count = isset($spec["count"]) ? $spec["count"] : 1;
		if(is_array($count)){
			$count = self::randomBetween($count[0], $count[1], $state);
		}

		$damage = isset($spec["damage"]) ? $spec["damage"] : 0;
		if(is_array($damage)){
			$damage = self::randomBetween($damage[0], $damage[1], $state);
		}

		return Item::get($spec["id"], $damage, $count);
	}

	private static function randomBetween(int $min, int $max, int &$state) : int{
		if($max <= $min){
			return $min;
		}

		$state = (int) (($state * 1103515245 + 12345) & 0x7fffffff);
		return $min + ($state % ($max - $min + 1));
	}

	private static function spec(int $id, $count, int $damage = 0) : array{
		return ["id" => $id, "damage" => $damage, "count" => $count];
	}

	private static function offer(array $buyA, array $sell, int $minUses = 6, int $maxUses = 12, array $buyB = null) : array{
		$template = [
			"buyA" => $buyA,
			"sell" => $sell,
			"maxUses" => [$minUses, $maxUses]
		];
		if($buyB !== null){
			$template["buyB"] = $buyB;
		}

		return $template;
	}

	private static function templatesForProfession(int $profession) : array{
		switch($profession){
			case Villager::PROFESSION_LIBRARIAN:
				return [
					self::offer(self::spec(Item::PAPER, [24, 36]), self::spec(Item::EMERALD, 1), 8, 12),
					self::offer(self::spec(Item::BOOK, [8, 12]), self::spec(Item::EMERALD, 1), 8, 12),
					self::offer(self::spec(Item::EMERALD, [3, 4]), self::spec(Item::BOOKSHELF, 1), 6, 10),
					self::offer(self::spec(Item::EMERALD, [10, 12]), self::spec(Item::COMPASS, 1), 3, 6),
					self::offer(self::spec(Item::EMERALD, [10, 12]), self::spec(Item::CLOCK, 1), 3, 6)
				];
			case Villager::PROFESSION_PRIEST:
				return [
					self::offer(self::spec(Item::ROTTEN_FLESH, [36, 40]), self::spec(Item::EMERALD, 1), 8, 12),
					self::offer(self::spec(Item::GOLD_INGOT, [8, 10]), self::spec(Item::EMERALD, 1), 8, 12),
					self::offer(self::spec(Item::EMERALD, [1, 2]), self::spec(Item::REDSTONE, [2, 4]), 8, 12),
					self::offer(self::spec(Item::EMERALD, [1, 3]), self::spec(Item::GLOWSTONE_DUST, [1, 3]), 8, 12),
					self::offer(self::spec(Item::EMERALD, [3, 11]), self::spec(Item::ENCHANTING_BOTTLE, 1), 3, 6)
				];
			case Villager::PROFESSION_BLACKSMITH:
				return [
					self::offer(self::spec(Item::COAL, [16, 24]), self::spec(Item::EMERALD, 1), 8, 12),
					self::offer(self::spec(Item::IRON_INGOT, [7, 9]), self::spec(Item::EMERALD, 1), 8, 12),
					self::offer(self::spec(Item::EMERALD, [7, 10]), self::spec(Item::IRON_SWORD, 1), 3, 6),
					self::offer(self::spec(Item::EMERALD, [10, 12]), self::spec(Item::DIAMOND_PICKAXE, 1), 3, 6),
					self::offer(self::spec(Item::EMERALD, [16, 19]), self::spec(Item::DIAMOND_CHESTPLATE, 1), 3, 6)
				];
			case Villager::PROFESSION_BUTCHER:
				return [
					self::offer(self::spec(Item::RAW_PORKCHOP, [14, 18]), self::spec(Item::EMERALD, 1), 8, 12),
					self::offer(self::spec(Item::RAW_BEEF, [14, 18]), self::spec(Item::EMERALD, 1), 8, 12),
					self::offer(self::spec(Item::RAW_CHICKEN, [14, 18]), self::spec(Item::EMERALD, 1), 8, 12),
					self::offer(self::spec(Item::EMERALD, 1), self::spec(Item::COOKED_PORKCHOP, [5, 7]), 8, 12),
					self::offer(self::spec(Item::EMERALD, 1), self::spec(Item::COOKED_CHICKEN, [6, 8]), 8, 12)
				];
			case Villager::PROFESSION_FARMER:
			default:
				return [
					self::offer(self::spec(Item::WHEAT, [18, 22]), self::spec(Item::EMERALD, 1), 8, 12),
					self::offer(self::spec(Item::POTATO, [15, 19]), self::spec(Item::EMERALD, 1), 8, 12),
					self::offer(self::spec(Item::CARROT, [15, 19]), self::spec(Item::EMERALD, 1), 8, 12),
					self::offer(self::spec(Item::EMERALD, 1), self::spec(Item::BREAD, [2, 4]), 8, 12),
					self::offer(self::spec(Item::EMERALD, [2, 3]), self::spec(Item::PUMPKIN_PIE, [2, 4]), 6, 10)
				];
		}
	}
}
