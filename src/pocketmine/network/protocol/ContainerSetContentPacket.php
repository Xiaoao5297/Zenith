<?php

namespace pocketmine\network\protocol;

#include <rules/DataPacket.h>


class ContainerSetContentPacket extends DataPacket{
	const NETWORK_ID = Info::CONTAINER_SET_CONTENT_PACKET;

	const SPECIAL_INVENTORY = 0;
	const SPECIAL_ARMOR = 0x78;
	const SPECIAL_CREATIVE = 0x79;

	public $windowid;
	public $slots = [];
	public $hotbar = [];

	public function clean(){
		$this->slots = [];
		$this->hotbar = [];
		return parent::clean();
	}

	public function decode(){
		$this->windowid = $this->getByte();
		$count = $this->getShort();
		for($s = 0; $s < $count and !$this->feof(); ++$s){
			$this->slots[$s] = $this->getSlot();
		}
		if($this->windowid === self::SPECIAL_INVENTORY){
			$count = $this->getShort();
			for($s = 0; $s < $count and !$this->feof(); ++$s){
				$this->hotbar[$s] = $this->getInt();
			}
		}
	}

	public function encode(){
		$this->reset();
		$this->putByte($this->windowid);
		
		// 根据协议版本处理
		$is013 = in_array($this->protocol, [31, 37, 38, 39]);
		
		if($is013){
			// 0.13版本：使用特定的物品数据格式
			$this->putShort(count($this->slots));
			
			foreach($this->slots as $slot){
				if($slot === null || $slot->getId() === 0){
					// 0.13版本的空槽位格式
					$this->putShort(-1);
					$this->putByte(0);
					$this->putShort(0);
				} else {
					// 0.13版本的物品数据格式
					$itemId = $slot->getId();
					// 确保物品ID在0.13版本的有效范围内
					if($itemId > 255){
						$itemId = 255; // 限制在0.13版本的有效范围内
					}
					
					$this->putShort($itemId);
					$this->putByte($slot->getCount());
					$this->putShort($slot->getDamage() === null ? 0 : $slot->getDamage());
					// 0.13版本不发送NBT数据
				}
			}
			
			// 0.13版本不发送hotbar数据
			$this->putShort(0);
		} else {
			// 0.14版本：正常处理
			$this->putShort(count($this->slots));
			
			foreach($this->slots as $slot){
				if($slot === null || $slot->getId() === 0){
					$this->putShort(0);
				} else {
					$this->putShort($slot->getId());
					$this->putByte($slot->getCount());
					$this->putShort($slot->getDamage() === null ? -1 : $slot->getDamage());
					$nbt = $slot->getCompoundTag();
					$this->putLShort(strlen($nbt));
					$this->put($nbt);
				}
			}
			
			if($this->windowid === self::SPECIAL_INVENTORY and count($this->hotbar) > 0){
				$this->putShort(count($this->hotbar));
				foreach($this->hotbar as $slot){
					$this->putInt($slot);
				}
			}else{
				$this->putShort(0);
			}
		}
	}

}