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
		$this->hotbarSlot = ProtocolCompatibility::isProtocol012((int) ($this->protocol ?? 0)) ? 0 : $this->getShort();
		$this->item = $this->getSlot();
	}

	public function encode(){
		$this->reset();
		$this->putByte($this->windowid);
		$this->putShort($this->slot);
		if(!ProtocolCompatibility::isProtocol012((int) ($this->protocol ?? 0))){
			$this->putShort($this->hotbarSlot);
		}
		$this->putSlot($this->item);
	}

}
