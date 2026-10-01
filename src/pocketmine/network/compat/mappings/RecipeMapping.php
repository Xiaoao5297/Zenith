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

namespace pocketmine\network\compat\mappings;

use pocketmine\inventory\BigShapelessRecipe;
use pocketmine\inventory\FurnaceRecipe;
use pocketmine\inventory\ShapedRecipe;
use pocketmine\inventory\ShapedRecipeFromJson;
use pocketmine\inventory\ShapelessRecipe;
use pocketmine\item\Item;
use pocketmine\network\compat\ProtocolCapabilities;

/**
 * 合成 / 熔炼配方及配方物品的协议兼容映射。
 */
final class RecipeMapping{

	const LEGACY_012_REGISTERED_RECIPE_ITEM_IDS = [
		1 => true,
		2 => true,
		3 => true,
		4 => true,
		5 => true,
		6 => true,
		7 => true,
		8 => true,
		9 => true,
		10 => true,
		11 => true,
		12 => true,
		13 => true,
		14 => true,
		15 => true,
		16 => true,
		17 => true,
		18 => true,
		19 => true,
		20 => true,
		21 => true,
		22 => true,
		24 => true,
		26 => true,
		27 => true,
		30 => true,
		31 => true,
		32 => true,
		35 => true,
		37 => true,
		38 => true,
		39 => true,
		40 => true,
		41 => true,
		42 => true,
		43 => true,
		44 => true,
		45 => true,
		46 => true,
		47 => true,
		48 => true,
		49 => true,
		50 => true,
		51 => true,
		52 => true,
		53 => true,
		54 => true,
		56 => true,
		57 => true,
		58 => true,
		59 => true,
		60 => true,
		61 => true,
		62 => true,
		63 => true,
		64 => true,
		65 => true,
		66 => true,
		67 => true,
		68 => true,
		71 => true,
		73 => true,
		74 => true,
		78 => true,
		79 => true,
		80 => true,
		81 => true,
		82 => true,
		83 => true,
		85 => true,
		86 => true,
		87 => true,
		88 => true,
		89 => true,
		91 => true,
		92 => true,
		96 => true,
		98 => true,
		101 => true,
		102 => true,
		103 => true,
		104 => true,
		105 => true,
		106 => true,
		107 => true,
		108 => true,
		109 => true,
		110 => true,
		111 => true,
		112 => true,
		113 => true,
		114 => true,
		116 => true,
		120 => true,
		121 => true,
		128 => true,
		129 => true,
		133 => true,
		134 => true,
		135 => true,
		136 => true,
		139 => true,
		141 => true,
		142 => true,
		145 => true,
		152 => true,
		155 => true,
		156 => true,
		157 => true,
		158 => true,
		159 => true,
		161 => true,
		162 => true,
		163 => true,
		164 => true,
		170 => true,
		171 => true,
		172 => true,
		173 => true,
		174 => true,
		175 => true,
		183 => true,
		184 => true,
		185 => true,
		186 => true,
		187 => true,
		198 => true,
		243 => true,
		244 => true,
		245 => true,
		246 => true,
		247 => true,
		256 => true,
		257 => true,
		258 => true,
		259 => true,
		260 => true,
		261 => true,
		262 => true,
		263 => true,
		264 => true,
		265 => true,
		266 => true,
		267 => true,
		268 => true,
		269 => true,
		270 => true,
		271 => true,
		272 => true,
		273 => true,
		274 => true,
		275 => true,
		276 => true,
		277 => true,
		278 => true,
		279 => true,
		280 => true,
		281 => true,
		282 => true,
		283 => true,
		284 => true,
		285 => true,
		286 => true,
		287 => true,
		288 => true,
		289 => true,
		290 => true,
		291 => true,
		292 => true,
		293 => true,
		294 => true,
		295 => true,
		296 => true,
		297 => true,
		298 => true,
		299 => true,
		300 => true,
		301 => true,
		302 => true,
		303 => true,
		304 => true,
		305 => true,
		306 => true,
		307 => true,
		308 => true,
		309 => true,
		310 => true,
		311 => true,
		312 => true,
		313 => true,
		314 => true,
		315 => true,
		316 => true,
		317 => true,
		318 => true,
		319 => true,
		320 => true,
		321 => true,
		322 => true,
		323 => true,
		324 => true,
		325 => true,
		328 => true,
		330 => true,
		331 => true,
		332 => true,
		334 => true,
		336 => true,
		337 => true,
		338 => true,
		339 => true,
		340 => true,
		341 => true,
		344 => true,
		345 => true,
		347 => true,
		348 => true,
		349 => true,
		350 => true,
		351 => true,
		352 => true,
		353 => true,
		354 => true,
		355 => true,
		357 => true,
		359 => true,
		360 => true,
		361 => true,
		362 => true,
		363 => true,
		364 => true,
		365 => true,
		366 => true,
		371 => true,
		383 => true,
		388 => true,
		391 => true,
		392 => true,
		393 => true,
		400 => true,
		405 => true,
		406 => true,
		456 => true,
		457 => true,
		458 => true,
		459 => true,
	];

	private function __construct(){
	}

	public static function isProtocol012RegisteredRecipeItem(int $itemId, int $itemMeta = 0) : bool{
		if($itemId === Item::AIR){
			return true;
		}

		return isset(self::LEGACY_012_REGISTERED_RECIPE_ITEM_IDS[$itemId]);
	}

	private static function mapProtocol012RecipeItem(Item $item){
		if($item->getId() === Item::AIR or $item->getCount() <= 0){
			return Item::get(Item::AIR, 0, 0);
		}

		$itemId = $item->getId();
		$itemMeta = $item->getDamage();
		$itemMeta = $itemMeta === null ? 0 : (int) $itemMeta;
		$count = $item->getCount();

		if($itemId === Item::ENCHANTED_GOLDEN_APPLE or $itemId === ItemMapping::getLegacyEnchantingGoldenAppleId()){
			return Item::get(Item::GOLDEN_APPLE, 1, $count);
		}

		if($itemId === Item::GOLDEN_APPLE){
			return Item::get(Item::GOLDEN_APPLE, $itemMeta > 0 ? 1 : 0, $count);
		}

		if(isset(ItemMapping::LEGACY_012_WOODEN_DOOR_ITEM_IDS[$itemId])){
			return Item::get(Item::WOODEN_DOOR, 0, $count);
		}

		if(!self::isProtocol012RegisteredRecipeItem($itemId, $itemMeta)){
			return null;
		}

		if($item->hasCompoundTag()){
			return Item::get($itemId, $itemMeta, $count);
		}

		return $item;
	}

	public static function mapRecipeItemForProtocol(int $protocol, Item $item) : ?Item{
		if($item->getId() === Item::AIR or $item->getCount() <= 0){
			return Item::get(Item::AIR, 0, 0);
		}

		if(ProtocolCapabilities::isProtocol012($protocol)){
			return self::mapProtocol012RecipeItem($item);
		}

		if(ProtocolCapabilities::requiresLegacyRedstoneMapping($protocol) and $item->getId() === Item::CARROT_ON_A_STICK){
			return Item::get(Item::FURNACE, 0, $item->getCount());
		}

		$mappedItem = ItemMapping::mapItemForProtocol($protocol, $item, false);
		$meta = $mappedItem->getDamage();
		if($mappedItem->getId() === Item::AIR or $mappedItem->getCount() <= 0 or ItemMapping::isHiddenItemForProtocol($protocol, $mappedItem->getId(), $meta === null ? 0 : (int) $meta)){
			return null;
		}

		if($mappedItem->hasCompoundTag()){
			return Item::get($mappedItem->getId(), $mappedItem->getDamage(), $mappedItem->getCount());
		}

		return $mappedItem;
	}

	private static function mapCraftingRecipeItemForProtocol(int $protocol, Item $item, bool &$modified){
		$mappedItem = ProtocolCapabilities::isProtocol012($protocol) ? self::mapProtocol012RecipeItem($item) : self::mapRecipeItemForProtocol($protocol, $item);
		if($mappedItem === null){
			return null;
		}

		if(!$mappedItem->deepEquals($item, true, true, true)){
			$modified = true;
		}

		return $mappedItem;
	}

	public static function mapCraftingRecipeForProtocol(int $protocol, $recipe){
		$modified = false;

		if($recipe instanceof ShapedRecipe){
			$result = self::mapCraftingRecipeItemForProtocol($protocol, $recipe->getResult(), $modified);
			if($result === null){
				return null;
			}

			$mappedRecipe = new ShapedRecipeFromJson($result, $recipe->getHeight(), $recipe->getWidth());
			for($y = 0; $y < $recipe->getHeight(); ++$y){
				for($x = 0; $x < $recipe->getWidth(); ++$x){
					$ingredient = $recipe->getIngredient($x, $y);
					if($ingredient instanceof Item and $ingredient->getId() !== Item::AIR){
						$mappedIngredient = self::mapCraftingRecipeItemForProtocol($protocol, $ingredient, $modified);
						if($mappedIngredient === null){
							return null;
						}
						if($mappedIngredient->getId() !== Item::AIR){
							$mappedRecipe->addIngredient($x, $y, $mappedIngredient);
						}
					}
				}
			}
		}elseif($recipe instanceof ShapelessRecipe){
			$result = self::mapCraftingRecipeItemForProtocol($protocol, $recipe->getResult(), $modified);
			if($result === null){
				return null;
			}

			$mappedRecipe = $recipe instanceof BigShapelessRecipe ? new BigShapelessRecipe($result) : new ShapelessRecipe($result);
			foreach($recipe->getIngredientList() as $ingredient){
				$mappedIngredient = self::mapCraftingRecipeItemForProtocol($protocol, $ingredient, $modified);
				if($mappedIngredient === null){
					return null;
				}
				if($mappedIngredient->getId() !== Item::AIR){
					$mappedRecipe->addIngredient($mappedIngredient);
				}
			}
		}elseif($recipe instanceof FurnaceRecipe){
			$result = self::mapCraftingRecipeItemForProtocol($protocol, $recipe->getResult(), $modified);
			$input = self::mapCraftingRecipeItemForProtocol($protocol, $recipe->getInput(), $modified);
			if($result === null or $input === null){
				return null;
			}

			$mappedRecipe = new FurnaceRecipe($result, $input);
		}else{
			return $recipe;
		}

		if($recipe->getId() !== null){
			$mappedRecipe->setId($recipe->getId());
		}

		return $modified ? $mappedRecipe : $recipe;
	}

	public static function normalizeRecipeClientItemForProtocol(int $protocol, Item $sourceItem, Item $clientItem) : Item{
		$normalized = ItemMapping::normalizeClientItemForProtocol($protocol, $clientItem);
		if($normalized !== $clientItem){
			$normalized->setCount($clientItem->getCount());
			return $normalized;
		}

		if(ProtocolCapabilities::requiresLegacyRedstoneMapping($protocol)){
			$mappedRecipeItem = self::mapRecipeItemForProtocol($protocol, $sourceItem);
			if($mappedRecipeItem instanceof Item and $mappedRecipeItem->deepEquals($clientItem, true, true, true)){
				$item = clone $sourceItem;
				$item->setCount($clientItem->getCount());
				return $item;
			}
		}

		return $clientItem;
	}
}
