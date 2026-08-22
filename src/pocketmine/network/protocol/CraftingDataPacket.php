<?php


namespace pocketmine\network\protocol;

#include <rules/DataPacket.h>


use pocketmine\inventory\FurnaceRecipe;
use pocketmine\inventory\ShapedRecipe;
use pocketmine\inventory\ShapelessRecipe;
use pocketmine\item\enchantment\Enchantment;
use pocketmine\item\enchantment\EnchantmentList;
use pocketmine\utils\BinaryStream;

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

	// 0.14/0.15 新增、0.12/0.13 客户端不认识的物品 ID, 含这些物品的配方不能下发给旧版本客户端, 否则渲染配方书时空指针崩溃
	const ITEMS_013_UNSUPPORTED = [23, 93, 94, 95, 97, 118, 122, 125, 127, 132, 154, 165, 179, 180, 181, 182, 199, 329, 356, 358, 380, 389, 395, 407, 408, 410, 439, 460, 461, 462, 463];

	private function isLegacyCompatible($entry){
		$items = [];
		if($entry instanceof ShapedRecipe){
			$items[] = $entry->getResult();
			foreach($entry->getIngredientMap() as $row){
				foreach($row as $it){
					$items[] = $it;
				}
			}
		}elseif($entry instanceof ShapelessRecipe){
			$items[] = $entry->getResult();
			foreach($entry->getIngredientList() as $it){
				$items[] = $it;
			}
		}elseif($entry instanceof FurnaceRecipe){
			$items[] = $entry->getInput();
			$items[] = $entry->getResult();
		}else{
			return true;
		}

		foreach($items as $it){
			if($it !== null and $it->getId() !== 0 and in_array($it->getId(), self::ITEMS_013_UNSUPPORTED, true)){
				return false;
			}
		}

		return true;
	}

	private function writeEntry($entry, BinaryStream $stream, bool $legacy013){
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

		$legacy013 = ProtocolCompatibility::usesLegacySlotFormat((int) ($this->protocol ?? 0));
		$entries = $this->entries;
		if($legacy013){
			// 0.12/0.13 客户端渲染配方书时遇到不认识的物品会空指针崩溃, 过滤掉含这些物品的配方
			$entries = array_values(array_filter($entries, function($e){
				return $this->isLegacyCompatible($e);
			}));
		}
		$this->putInt(count($entries));

		$writer = new BinaryStream();
		foreach($entries as $d){
			$entryType = $this->writeEntry($d, $writer, $legacy013);
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
