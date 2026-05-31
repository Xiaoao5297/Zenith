<?php

namespace pocketmine\network\protocol;

#include <rules/DataPacket.h>

use pocketmine\item\Item;

class ContainerSetSlotPacket extends DataPacket{
	const NETWORK_ID = Info::CONTAINER_SET_SLOT_PACKET;

	public $windowid;
	public $slot;
	/** @var Item */
	public $item;
	public $hotbarSlot;

	public function decode(){
		$this->windowid = $this->getByte();
		$this->slot = $this->getShort();
		$this->hotbarSlot = $this->getShort();
		$this->item = $this->getSlot();
	}

	public function encode(){
		$this->reset();
		$this->putByte($this->windowid);
		$this->putShort($this->slot);
		
		// 根据协议版本处理
		$is013 = in_array($this->protocol, [31, 37, 38, 39]);
		
		if($is013){
			// 0.13协议可能不使用hotbarSlot
			$this->putShort(0);
			
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
		} else {
			// 0.14协议
			$this->putShort($this->hotbarSlot);
			
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
		}
	}

}
