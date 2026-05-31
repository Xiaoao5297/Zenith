<?php

namespace pocketmine\network\protocol;

#include <rules/DataPacket.h>


class MobEquipmentPacket extends DataPacket{
	const NETWORK_ID = Info::MOB_EQUIPMENT_PACKET;

	public $eid;
	public $item;
	public $slot;
	public $selectedSlot;

	public function decode(){
		$this->eid = $this->getLong();
		$this->item = $this->getSlot();
		$this->slot = $this->getByte();
		$this->selectedSlot = $this->getByte();
	}

	public function encode(){
		$this->reset();
		$this->putLong($this->eid);
		
		// 根据协议版本处理
		$is013 = in_array($this->protocol, [31, 37, 38, 39]);
		
		if($is013){
			// 0.13版本的物品数据格式
			if($this->item === null || $this->item->getId() === 0){
				// 0.13版本的空槽位格式
				$this->putShort(-1);
				$this->putByte(0);
				$this->putShort(0);
			} else {
				// 0.13版本的物品数据格式
				$itemId = $this->item->getId();
				// 确保物品ID在0.13版本的有效范围内
				if($itemId > 255){
					$itemId = 255; // 限制在0.13版本的有效范围内
				}
				
				$this->putShort($itemId);
				$this->putByte($this->item->getCount());
				$this->putShort($this->item->getDamage() === null ? 0 : $this->item->getDamage());
				// 0.13版本不发送NBT数据
			}
			
			$this->putByte($this->slot);
			// 0.13协议可能不使用selectedSlot
			$this->putByte(0);
		} else {
			// 0.14协议：正常处理
			// 直接处理物品数据
			if($this->item === null || $this->item->getId() === 0){
				$this->putShort(0);
			} else {
				$this->putShort($this->item->getId());
				$this->putByte($this->item->getCount());
				$this->putShort($this->item->getDamage() === null ? -1 : $this->item->getDamage());
				$nbt = $this->item->getCompoundTag();
				$this->putLShort(strlen($nbt));
				$this->put($nbt);
			}
			
			$this->putByte($this->slot);
			$this->putByte($this->selectedSlot);
		}
	}

}
