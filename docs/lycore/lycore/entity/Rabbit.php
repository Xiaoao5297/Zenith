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

use lycore\event\entity\EntityDamageEvent;
use lycore\item\Item as ItemItem;
use lycore\nbt\tag\ByteTag;
use lycore\network\protocol\AddEntityPacket;
use lycore\level\format\FullChunk;
use lycore\nbt\tag\CompoundTag;
use lycore\Player;

class Rabbit extends Animal{
	const NETWORK_ID = 18;

	const TYPE_BROWN = 0;
	const TYPE_WHITE = 1;
	const TYPE_BLACK = 2;
	const TYPE_BLACK_WHITE = 3;
	const TYPE_GOLD = 4;
	const TYPE_SALT_PEPPER = 5;
	const TYPE_KILLER_BUNNY = 99;

	public $height = 0.5;
	public $width = 0.5;
	public $length = 0.5;

	public $dropExp = [1, 3];

	public function initEntity(){
		$this->setMaxHealth(3);
		parent::initEntity();
	}

	public function usesPm1eGroundAi() : bool{
		return true;
	}

	public function __construct(FullChunk $chunk, CompoundTag $nbt){
		if(!isset($nbt->RabbitType)){
			$nbt->RabbitType = new ByteTag("RabbitType", $this->getRandomRabbitType());
		}
		parent::__construct($chunk, $nbt);

		$this->setDataProperty(self::DATA_RABBIT_TYPE, self::DATA_TYPE_BYTE, $this->getRabbitType());
	}

	public function getRandomRabbitType() : int{
		$arr = [0, 1, 2, 3, 4, 5, 99];
		return $arr[mt_rand(0, count($arr) - 1)];
	}

	public function setRabbitType(int $type){
		$this->namedtag->RabbitType = new ByteTag("RabbitType", $type);
	}

	public function getRabbitType() : int{
		return (int) $this->namedtag["RabbitType"];
	}

	public function getName() : string{
		return "Rabbit";
	}

	public function spawnTo(Player $player){
		$pk = new AddEntityPacket();
		$pk->eid = $this->getId();
		$pk->type = Rabbit::NETWORK_ID;
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

	public function getDrops(){
		$drops = [ItemItem::get(ItemItem::RABBIT_HIDE, 0, mt_rand(0, 2))];

		if($this->getLastDamageCause() === EntityDamageEvent::CAUSE_FIRE){
			$drops[] = ItemItem::get(ItemItem::COOKED_RABBIT, 0, mt_rand(1, 2));
		}else{
			$drops[] = ItemItem::get(ItemItem::RAW_RABBIT, 0, mt_rand(1, 2));
		}

		return $drops;
	}


}
