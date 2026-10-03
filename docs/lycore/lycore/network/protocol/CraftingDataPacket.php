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

namespace lycore\network\protocol;

#include <rules/DataPacket.h>


use lycore\inventory\FurnaceRecipe;
use lycore\inventory\ShapedRecipe;
use lycore\inventory\ShapelessRecipe;
use lycore\item\enchantment\Enchantment;
use lycore\item\enchantment\EnchantmentList;
use lycore\utils\BinaryStream;

class CraftingDataPacket extends DataPacket{
	const NETWORK_ID = Info::CRAFTING_DATA_PACKET;

	const ENTRY_SHAPELESS = 0;
	const ENTRY_SHAPED = 1;
	const ENTRY_FURNACE = 2;
	const ENTRY_FURNACE_DATA = 3;
	const ENTRY_ENCHANT_LIST = 4;

	/** @var object[] */
	public $entries = [];
	public $cleanRecipes = false;

	private static function writeEntry($entry, BinaryStream $stream, bool $legacy013){
		if($entry instanceof ShapelessRecipe){
			return self::writeShapelessRecipe($entry, $stream, $legacy013);
		}elseif($entry instanceof ShapedRecipe){
			return self::writeShapedRecipe($entry, $stream, $legacy013);
		}elseif($entry instanceof FurnaceRecipe){
			return self::writeFurnaceRecipe($entry, $stream, $legacy013);
		}elseif($entry instanceof EnchantmentList){
			return self::writeEnchantList($entry, $stream);
		}

		return -1;
	}

	private static function writeShapelessRecipe(ShapelessRecipe $recipe, BinaryStream $stream, bool $legacy013){
		$stream->putInt($recipe->getIngredientCount());
		foreach($recipe->getIngredientList() as $item){
			$stream->putSlot($item, $legacy013);
		}

		$stream->putInt(1);
		$stream->putSlot($recipe->getResult(), $legacy013);

		$stream->putUUID($recipe->getId());

		return CraftingDataPacket::ENTRY_SHAPELESS;
	}

	private static function writeShapedRecipe(ShapedRecipe $recipe, BinaryStream $stream, bool $legacy013){
		$stream->putInt($recipe->getWidth());
		$stream->putInt($recipe->getHeight());

		for($z = 0; $z < $recipe->getWidth(); ++$z){
			for($x = 0; $x < $recipe->getHeight(); ++$x){
				$stream->putSlot($recipe->getIngredient($x, $z), $legacy013);
			}
		}

		$stream->putInt(1);
		$stream->putSlot($recipe->getResult(), $legacy013);

		$stream->putUUID($recipe->getId());

		return CraftingDataPacket::ENTRY_SHAPED;
	}

	private static function writeFurnaceRecipe(FurnaceRecipe $recipe, BinaryStream $stream, bool $legacy013){
		if($recipe->getInput()->getDamage() !== 0){ //Data recipe
			$stream->putInt(($recipe->getInput()->getId() << 16) | ($recipe->getInput()->getDamage()));
			$stream->putSlot($recipe->getResult(), $legacy013);

			return CraftingDataPacket::ENTRY_FURNACE_DATA;
		}else{
			$stream->putInt($recipe->getInput()->getId());
			$stream->putSlot($recipe->getResult(), $legacy013);

			return CraftingDataPacket::ENTRY_FURNACE;
		}
	}

	private static function writeEnchantList(EnchantmentList $list, BinaryStream $stream){

		$stream->putByte($list->getSize());
		for($i = 0; $i < $list->getSize(); ++$i){
			$entry = $list->getSlot($i);
			$stream->putInt($entry->getCost());
			$stream->putByte(count($entry->getEnchantments()));
			foreach($entry->getEnchantments() as $enchantment){
				$stream->putInt($enchantment->getId());
				$stream->putInt($enchantment->getLevel());
			}
			$stream->putString($entry->getRandomName());
		}

		return CraftingDataPacket::ENTRY_ENCHANT_LIST;
	}

	public function addShapelessRecipe(ShapelessRecipe $recipe){
		$this->entries[] = $recipe;
	}

	public function addShapedRecipe(ShapedRecipe $recipe){
		$this->entries[] = $recipe;
	}

	public function addFurnaceRecipe(FurnaceRecipe $recipe){
		$this->entries[] = $recipe;
	}

	public function addEnchantList(EnchantmentList $list){
		$this->entries[] = $list;
	}

	public function clean(){
		$this->entries = [];
		return parent::clean();
	}

	public function decode(){

	}

	public function encode(){
		$this->reset();
		$this->putInt(count($this->entries));

		$writer = new BinaryStream();
		$legacy013 = $this->usesProtocol013SlotFormat();
		foreach($this->entries as $d){
			$entryType = self::writeEntry($d, $writer, $legacy013);
			if($entryType >= 0){
				$this->putInt($entryType);
				$this->putInt(strlen($writer->getBuffer()));
				$this->put($writer->getBuffer());
			}else{
				$this->putInt(-1);
				$this->putInt(0);
			}

			$writer->reset();
		}

		$this->putByte($this->cleanRecipes ? 1 : 0);
	}

}
