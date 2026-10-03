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

namespace lycore\entity;

use lycore\entity\trade\VillagerTradeFactory;
use lycore\entity\trade\VillagerTradeOffer;
use lycore\inventory\VillagerTradeInventory;
use lycore\nbt\tag\ByteTag;
use lycore\nbt\tag\IntTag;
use lycore\nbt\tag\DoubleTag;
use lycore\nbt\tag\FloatTag;
use lycore\nbt\tag\ListTag;
use lycore\level\format\FullChunk;
use lycore\level\sound\VillagerDeathSound;
use lycore\level\sound\ZombieInfectSound;
use lycore\nbt\tag\CompoundTag;
use lycore\network\protocol\AddEntityPacket;
use lycore\event\entity\EntityDamageByEntityEvent;
use lycore\Player;
use lycore\item\Item as ItemItem;
use lycore\nbt\NBT;

class Villager extends Mob implements NPC, Ageable{
	const PROFESSION_FARMER = 0;
	const PROFESSION_LIBRARIAN = 1;
	const PROFESSION_PRIEST = 2;
	const PROFESSION_BLACKSMITH = 3;
	const PROFESSION_BUTCHER = 4;
	//const PROFESSION_GENERIC = 5;

	const NETWORK_ID = 15;

	public $width = 0.6;
	public $length = 0.6;
	public $height = 1.8;
	/** @var VillagerTradeOffer[]|null */
	private $tradeOffers = null;

	public function getName() : string{
		return "Villager";
	}

	public static function getProfessionDisplayName(int $profession) : string{
		switch(min(4, max(0, $profession))){
			case self::PROFESSION_FARMER:
				return "农民";
			case self::PROFESSION_LIBRARIAN:
				return "图书管理员";
			case self::PROFESSION_PRIEST:
				return "牧师";
			case self::PROFESSION_BLACKSMITH:
				return "铁匠";
			case self::PROFESSION_BUTCHER:
				return "屠夫";
			default:
				return "村民";
		}
	}

	public function __construct(FullChunk $chunk, CompoundTag $nbt){
		parent::__construct($chunk, $nbt);

		$this->setDataProperty(self::DATA_PROFESSION_ID, self::DATA_TYPE_BYTE, $this->getProfession());
	}

	public function usesPm1eGroundAi() : bool{
		return true;
	}

	public function initEntity(){
		parent::initEntity();
		if(!isset($this->namedtag->Profession)){
			$this->setProfession(mt_rand(0, 4)); //随机生成村民职业
		}
		self::ensureTradeData($this->namedtag);
		$this->tradeOffers = self::loadTradeOffersFromNBT($this->namedtag);
		if(!isset($this->namedtag->IsBaby)){
			$this->namedtag->IsBaby = new ByteTag("IsBaby", 1);
			$this->setBaby(false);
		}
		$this->setBaby($this->isBaby());
	}

	public function spawnTo(Player $player){
		$pk = new AddEntityPacket();
		$pk->eid = $this->getId();
		$pk->type = Villager::NETWORK_ID;
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

		parent::spawnTo($player);
	}

	public function saveNBT(){
		parent::saveNBT();
		$this->namedtag->Profession = new ByteTag("Profession", $this->getProfession());
		$this->saveTradeOffersToNBT($this->namedtag);
	}

	/**
	 * Sets the villager profession
	 *
	 * @param int $profession
	 */
	public function setProfession(int $profession){
		$this->namedtag->Profession = new ByteTag("Profession", $profession);
	}

	public function getProfession() : int{
		$pro = (int) $this->namedtag["Profession"];
		return min(4, max(0, $pro));
	}

	/**
	 * @return VillagerTradeOffer[]
	 */
	public function getTradeOffers() : array{
		if($this->tradeOffers === null){
			self::ensureTradeData($this->namedtag);
			$this->tradeOffers = self::loadTradeOffersFromNBT($this->namedtag);
		}

		return $this->tradeOffers;
	}

	public function setTradeOffers(array $offers){
		$this->tradeOffers = array_values(array_filter($offers, function($offer){
			return $offer instanceof VillagerTradeOffer;
		}));
		$this->saveTradeOffersToNBT($this->namedtag);
	}

	public function saveTradeOffersToNBT(CompoundTag $nbt = null){
		$nbt = $nbt ?? $this->namedtag;
		$nbt->Offers = self::offersToNBT($this->getTradeOffers());
	}

	public static function ensureTradeData(CompoundTag $nbt, $seed = null){
		if(isset($nbt->Offers) and $nbt->Offers instanceof ListTag and $nbt->Offers->getCount() > 0){
			return;
		}

		$profession = isset($nbt->Profession) ? min(4, max(0, (int) $nbt["Profession"])) : self::PROFESSION_FARMER;
		$nbt->Offers = self::offersToNBT(VillagerTradeFactory::generateOffers($profession, $seed ?? mt_rand(1, PHP_INT_MAX)));
	}

	/**
	 * @return VillagerTradeOffer[]
	 */
	public static function loadTradeOffersFromNBT(CompoundTag $nbt) : array{
		if(!isset($nbt->Offers) or !($nbt->Offers instanceof ListTag)){
			return [];
		}

		$offers = [];
		foreach($nbt->Offers as $tag){
			if($tag instanceof CompoundTag){
				$offers[] = VillagerTradeOffer::fromNBT($tag);
			}
		}

		return $offers;
	}

	/**
	 * @param VillagerTradeOffer[] $offers
	 */
	public static function offersToNBT(array $offers) : ListTag{
		$list = new ListTag("Offers", []);
		$list->setTagType(NBT::TAG_Compound);
		foreach(array_values($offers) as $index => $offer){
			if($offer instanceof VillagerTradeOffer){
				$list->{$index} = $offer->toNBT($index);
			}
		}

		return $list;
	}

	public function openTradeWindow(Player $player, int $currentTradeIndex = 0) : int{
		if($this->isBaby()){
			return -1;
		}

		if(count($this->getTradeOffers()) === 0){
			self::ensureTradeData($this->namedtag);
			$this->tradeOffers = self::loadTradeOffersFromNBT($this->namedtag);
		}

		return $player->addWindow(VillagerTradeInventory::createForPlayer($this, $player, $currentTradeIndex));
	}

	public function kill(){
		if($this->closed){
			return;
		}

		if(!$this->isAlive()){
			return;
		}

		if($this->transformIntoZombieVillager()){
			return;
		}

		parent::kill();
	}

	protected function transformIntoZombieVillager() : bool{
		$cause = $this->getLastDamageCause();
		if(!($cause instanceof EntityDamageByEntityEvent)){
			return false;
		}

		$damager = $cause->getDamager();
		if(!($damager instanceof Zombie)){
			return false;
		}

		$chunk = $this->getLevel()->getChunk($this->x >> 4, $this->z >> 4, true);
		if($chunk === null){
			return false;
		}

		$nbt = new CompoundTag("", [
			"Pos" => new ListTag("Pos", [
				new DoubleTag(0, $this->x),
				new DoubleTag(1, $this->y),
				new DoubleTag(2, $this->z)
			]),
			"Motion" => new ListTag("Motion", [
				new DoubleTag(0, $this->motionX),
				new DoubleTag(1, $this->motionY),
				new DoubleTag(2, $this->motionZ)
			]),
			"Rotation" => new ListTag("Rotation", [
				new FloatTag(0, $this->yaw),
				new FloatTag(1, $this->pitch)
			]),
			"Profession" => new ByteTag("Profession", $this->getProfession()),
			"IsBaby" => new ByteTag("IsBaby", $this->isBaby() ? 1 : 0)
		]);
		$this->saveTradeOffersToNBT($nbt);

		$soundPlayers = $this->getLevel()->getChunkPlayers($chunk->getX(), $chunk->getZ());
		$zombieVillager = new ZombieVillager($chunk, $nbt);
		$zombieVillager->setHealth($zombieVillager->getMaxHealth());
		$zombieVillager->spawnToAll();
		$this->getLevel()->addSound(new VillagerDeathSound($this), $soundPlayers);
		$this->getLevel()->addSound(new ZombieInfectSound($this), $soundPlayers);

		$this->close();
		return true;
	}

	public function isBaby(){
		return $this->namedtag["IsBaby"] == 0 ? false : true;
	}
	
	public function setBaby(bool $resting){
		$this->setDataProperty(self::DATA_VILLAGER_IS_BABY, self::DATA_TYPE_BYTE, $resting ? 1 : 0);
		$this->namedtag->IsBaby = new ByteTag("IsBaby", $resting ? 1 : 0);
	}
	
	public function getDrops(){
        return [
            ItemItem::get(ItemItem::EMERALD, 0, \mt_rand(0, 2))
        ];
    }
}
