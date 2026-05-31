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

	private function writeEntry($entry, BinaryStream $stream){
		if($entry instanceof ShapelessRecipe){
			return self::writeShapelessRecipe($entry, $stream);
		}elseif($entry instanceof ShapedRecipe){
			return self::writeShapedRecipe($entry, $stream);
		}elseif($entry instanceof FurnaceRecipe){
			return self::writeFurnaceRecipe($entry, $stream);
		}elseif($entry instanceof EnchantmentList){
			return self::writeEnchantList($entry, $stream);
		}

		return -1;
	}

	private function writeShapelessRecipe(ShapelessRecipe $recipe, BinaryStream $stream){
		$stream->putInt($recipe->getIngredientCount());
		foreach($recipe->getIngredientList() as $item){
			// 根据协议版本使用不同的方法
			$is013 = in_array($this->protocol, [31, 37, 38, 39]);
			
			if($is013){
				// 0.13协议
				$nbt = $item->getCompoundTag();
				$stream->putShort($item->getId());
				$stream->putByte($item->getCount());
				$stream->putShort($item->getDamage() === null ? -1 : $item->getDamage());
				$stream->putShort(strlen($nbt));
				$stream->put($nbt);
			} else {
				// 0.14协议
				$stream->putSlot($item);
			}
		}

		$stream->putInt(1);
		
		// 根据协议版本使用不同的方法
		$is013 = in_array($this->protocol, [31, 37, 38, 39]);
		
		if($is013){
			// 0.13协议
			$nbt = $recipe->getResult()->getCompoundTag();
			$stream->putShort($recipe->getResult()->getId());
			$stream->putByte($recipe->getResult()->getCount());
			$stream->putShort($recipe->getResult()->getDamage() === null ? -1 : $recipe->getResult()->getDamage());
			$stream->putShort(strlen($nbt));
			$stream->put($nbt);
		} else {
			// 0.14协议
			$stream->putSlot($recipe->getResult());
		}

		$stream->putUUID($recipe->getId());

		return CraftingDataPacket::ENTRY_SHAPELESS;
	}

	private static function writeShapedRecipe(ShapedRecipe $recipe, BinaryStream $stream){
		$stream->putInt($recipe->getWidth());
		$stream->putInt($recipe->getHeight());

		for($z = 0; $z < $recipe->getWidth(); ++$z){
			for($x = 0; $x < $recipe->getHeight(); ++$x){
				$stream->putSlot($recipe->getIngredient($x, $z));
			}
		}

		$stream->putInt(1);
		$stream->putSlot($recipe->getResult());

		$stream->putUUID($recipe->getId());

		return CraftingDataPacket::ENTRY_SHAPED;
	}

	private static function writeFurnaceRecipe(FurnaceRecipe $recipe, BinaryStream $stream){
		if($recipe->getInput()->getDamage() !== 0){ //Data recipe
			$stream->putInt(($recipe->getInput()->getId() << 16) | ($recipe->getInput()->getDamage()));
			$stream->putSlot($recipe->getResult());

			return CraftingDataPacket::ENTRY_FURNACE_DATA;
		}else{
			$stream->putInt($recipe->getInput()->getId());
			$stream->putSlot($recipe->getResult());

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
		
		// 根据协议版本处理
		$is013 = in_array($this->protocol, [31, 37, 38, 39]);
		
		if($is013){
			// 0.13协议：发送少量合成数据，使用正确的格式
			$this->putInt(0); // 暂时不发送任何配方，防止崩溃
			$this->putByte(0);
		} else {
			// 0.14协议：正常处理
			$this->putInt(count($this->entries));

			$writer = new BinaryStream();
			foreach($this->entries as $d){
				$entryType = $this->writeEntry($d, $writer);
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

}
