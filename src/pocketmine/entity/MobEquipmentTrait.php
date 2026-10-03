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
 * 装备栏/武器逻辑取自 lycore 各生物（Zombie/Skeleton/PigZombie 等）的重复实现，
 * 抽取为 trait 供这些生物共用；行为与 lycore 保持一致。
 */

namespace pocketmine\entity;

use pocketmine\item\Item as ItemItem;
use pocketmine\network\protocol\MobArmorEquipmentPacket;
use pocketmine\network\protocol\MobEquipmentPacket;
use pocketmine\Player;

trait MobEquipmentTrait{

	/** @var ItemItem[] */
	protected $armor = [];
	/** @var ItemItem|null */
	protected $weapon = null;

	public function getWeapon(){
		return $this->weapon instanceof ItemItem ? $this->weapon : ItemItem::get(ItemItem::AIR, 0, 0);
	}

	public function getArmorContents(){
		return count($this->armor) === 4 ? $this->armor : VanillaMobEquipment::emptyArmor();
	}

	public function setMobEquipment(array $armor, ItemItem $weapon = null){
		$normalizedArmor = VanillaMobEquipment::emptyArmor();
		for($slot = 0; $slot < 4; ++$slot){
			if(isset($armor[$slot]) && $armor[$slot] instanceof ItemItem){
				$normalizedArmor[$slot] = $armor[$slot];
			}
		}

		$this->armor = $normalizedArmor;
		$this->weapon = $weapon instanceof ItemItem ? $weapon : ItemItem::get(ItemItem::AIR, 0, 0);
	}

	protected function sendMobEquipment(Player $player){
		$pk = new MobEquipmentPacket();
		$pk->eid = $this->getId();
		$pk->item = $this->getWeapon();
		$pk->slot = 0;
		$pk->selectedSlot = 0;
		$player->dataPacket($pk);

		$pk = new MobArmorEquipmentPacket();
		$pk->eid = $this->getId();
		$pk->slots = $this->getArmorContents();
		$player->dataPacket($pk);
	}
}
