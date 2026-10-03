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
 * 移植自 lycore\entity\Horse，命名空间改为 pocketmine\entity。
 * 当前核心没有 lycore 的 PM1E 骑乘 AI / 骑手输入管线，
 * 故骑乘使用核心既有的 Entity 链接机制（SetEntityLinkPacket）实现，
 * 保留外观、马鞍/马铠、掉落与 NBT 持久化。
 */

namespace pocketmine\entity;

use pocketmine\inventory\HorseInventory;
use pocketmine\inventory\InventoryHolder;
use pocketmine\item\Item as ItemItem;
use pocketmine\level\format\FullChunk;
use pocketmine\nbt\NBT;
use pocketmine\nbt\tag\ByteTag;
use pocketmine\nbt\tag\CompoundTag;
use pocketmine\nbt\tag\IntTag;
use pocketmine\nbt\tag\ListTag;
use pocketmine\network\protocol\AddEntityPacket;
use pocketmine\Player;

class Horse extends Animal implements InventoryHolder, Rideable{
	const NETWORK_ID = 23;

	const VARIANT_WHITE = 0;
	const VARIANT_CREAMY = 1;
	const VARIANT_CHESTNUT = 2;
	const VARIANT_BROWN = 3;
	const VARIANT_BLACK = 4;
	const VARIANT_GRAY = 5;
	const VARIANT_DARK_BROWN = 6;

	const MARK_NONE = 0;
	const MARK_WHITE = 1;
	const MARK_WHITE_FIELD = 2;
	const MARK_WHITE_DOTS = 3;
	const MARK_BLACK_DOTS = 4;

	const DATA_HORSE_TYPE = 19;
	const DATA_HORSE_VARIANT = 20;
	const DATA_FLAG_SADDLED = 4;

	public $width = 1.4;
	public $length = 1.4;
	public $height = 1.6;
	public $dropExp = [1, 3];

	/** @var HorseInventory */
	protected $inventory;
	protected $variant = self::VARIANT_WHITE;
	protected $markVariant = self::MARK_NONE;

	public function __construct(FullChunk $chunk, CompoundTag $nbt){
		parent::__construct($chunk, $nbt);
	}

	public function getName() : string{
		return "Horse";
	}

	public function initEntity(){
		$this->setMaxHealth(30);
		parent::initEntity();
		$this->stepHeight = 1.0625;
		$this->variant = isset($this->namedtag->Variant) ? max(0, min(self::VARIANT_DARK_BROWN, (int) $this->namedtag["Variant"])) : mt_rand(0, self::VARIANT_DARK_BROWN);
		$this->markVariant = isset($this->namedtag->MarkVariant) ? max(0, min(self::MARK_BLACK_DOTS, (int) $this->namedtag["MarkVariant"])) : mt_rand(0, self::MARK_BLACK_DOTS);
		$this->syncHorseAppearanceData();

		$this->inventory = new HorseInventory($this);
		if(isset($this->namedtag->Inventory) and $this->namedtag->Inventory instanceof ListTag){
			$this->inventory->loadFromNbt($this->namedtag->Inventory);
		}else{
			if(isset($this->namedtag->SaddleItem) and $this->namedtag->SaddleItem instanceof CompoundTag){
				$this->inventory->setSaddle(NBT::getItemHelper($this->namedtag->SaddleItem));
			}
			if(isset($this->namedtag->ArmorItem) and $this->namedtag->ArmorItem instanceof CompoundTag){
				$this->inventory->setArmor(NBT::getItemHelper($this->namedtag->ArmorItem));
			}
		}

		if(isset($this->namedtag->Saddled) and (int) $this->namedtag["Saddled"] > 0 and $this->inventory->getSaddle()->getId() !== ItemItem::SADDLE){
			$this->inventory->setSaddle(ItemItem::get(ItemItem::SADDLE, 0, 1));
		}
	}

	public function getInventory(){
		return $this->inventory;
	}

	public function getVariant() : int{
		return $this->variant;
	}

	public function getMarkVariant() : int{
		return $this->markVariant;
	}

	protected function syncHorseAppearanceData(){
		$this->setDataProperty(self::DATA_HORSE_TYPE, self::DATA_TYPE_BYTE, 0);
		$this->setDataProperty(self::DATA_HORSE_VARIANT, self::DATA_TYPE_INT, $this->variant | ($this->markVariant << 8));
	}

	public function canBeSaddled() : bool{
		return !$this->isBaby();
	}

	public function isSaddled() : bool{
		return $this->getDataFlag(self::DATA_FLAGS, self::DATA_FLAG_SADDLED);
	}

	public function setSaddled(bool $saddled) : bool{
		if($saddled and !$this->canBeSaddled()){
			return false;
		}

		$changed = $this->isSaddled() !== $saddled;
		$this->setDataFlag(self::DATA_FLAGS, self::DATA_FLAG_SADDLED, $saddled);
		$this->namedtag->Saddled = new ByteTag("Saddled", $saddled ? 1 : 0);
		return $changed;
	}

	public function canBeRidden() : bool{
		return !$this->isBaby() and $this->isSaddled();
	}

	public function canBeMounted() : bool{
		return !$this->isBaby();
	}

	public function canBeControlledBy($rider) : bool{
		return $this->canBeRidden() and $rider instanceof Player;
	}

	public static function isHorseArmorItem(ItemItem $item) : bool{
		return in_array($item->getId(), [
			ItemItem::LEATHER_HORSE_ARMOR,
			ItemItem::IRON_HORSE_ARMOR,
			ItemItem::GOLDEN_HORSE_ARMOR,
			ItemItem::DIAMOND_HORSE_ARMOR,
		], true);
	}

	public function onInteract(Player $player, ItemItem $item) : bool{
		if($this->isBaby()){
			return false;
		}

		if($item->getId() === ItemItem::SADDLE and !$this->isSaddled()){
			$equipment = clone $item;
			$equipment->setCount(1);
			if($this->inventory->setSaddle($equipment)){
				$this->consumeInteractionItem($player, $item);
				return true;
			}
		}

		if(self::isHorseArmorItem($item) and $this->inventory->getArmor()->getId() === ItemItem::AIR){
			$equipment = clone $item;
			$equipment->setCount(1);
			if($this->inventory->setArmor($equipment)){
				$this->consumeInteractionItem($player, $item);
				return true;
			}
		}

		if($this->canBeMounted()){
			return $this->mountPlayer($player);
		}

		return false;
	}

	private function consumeInteractionItem(Player $player, ItemItem $item){
		if($player->isCreative()){
			return;
		}

		$item->setCount($item->getCount() - 1);
		$player->getInventory()->setItemInHand($item->getCount() > 0 ? $item : ItemItem::get(ItemItem::AIR, 0, 0));
	}

	public function mountPlayer(Player $player) : bool{
		if(!$this->canBeMounted()){
			return false;
		}
		if($this->getLinkedEntity() instanceof Entity and $this->getLinkedEntity() !== $player){
			return false;
		}
		if($player->getLinkedEntity() instanceof Entity and $player->getLinkedEntity() !== $this){
			return false;
		}

		$this->setBehaviorsEnabled(false);
		$player->linkEntity($this);
		$player->setDataFlag(self::DATA_FLAGS, self::DATA_FLAG_RIDING, true);
		return true;
	}

	public function dismountPlayer(Player $player) : bool{
		if($this->getLinkedEntity() !== $player and $player->getLinkedEntity() !== $this){
			return false;
		}

		$player->setLinked(0, $this);
		$player->setDataFlag(self::DATA_FLAGS, self::DATA_FLAG_RIDING, false);
		$this->setBehaviorsEnabled(true);
		return true;
	}

	public function spawnTo(Player $player){
		$pk = new AddEntityPacket();
		$pk->eid = $this->getId();
		$pk->type = self::NETWORK_ID;
		$pk->x = $this->x;
		$pk->y = $this->y;
		$pk->z = $this->z;
		$pk->speedX = $this->motionX;
		$pk->speedY = $this->motionY;
		$pk->speedZ = $this->motionZ;
		$pk->yaw = $this->yaw;
		$pk->pitch = $this->pitch;
		$pk->metadata = $this->dataProperties;
		$player->dataPacket($pk);
		$this->inventory->sendArmor($player);

		parent::spawnTo($player);
	}

	public function getDrops(){
		$drops = [];
		$leather = mt_rand(0, 2);
		if($leather > 0){
			$drops[] = ItemItem::get(ItemItem::LEATHER, 0, $leather);
		}
		foreach($this->inventory->getInventoryDrops() as $item){
			$drops[] = $item;
		}

		return $drops;
	}

	public function saveNBT(){
		parent::saveNBT();
		$this->namedtag->Saddled = new ByteTag("Saddled", $this->isSaddled() ? 1 : 0);
		$this->namedtag->Variant = new IntTag("Variant", $this->variant);
		$this->namedtag->MarkVariant = new IntTag("MarkVariant", $this->markVariant);
		$this->namedtag->Inventory = $this->inventory->saveToNbt();
	}
}
