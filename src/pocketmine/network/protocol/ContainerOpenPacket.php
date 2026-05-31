<?php

namespace pocketmine\network\protocol;

#include <rules/DataPacket.h>


class ContainerOpenPacket extends DataPacket{
	const NETWORK_ID = Info::CONTAINER_OPEN_PACKET;

	public $windowid;
	public $type;
	public $slots;
	public $x;
	public $y;
	public $z;
	public $entityId = -1;

	public function decode(){

	}
/*
	public function encode(){
		$this->reset();
		$this->putByte($this->windowid);
		$this->putByte($this->type);
		$this->putShort($this->slots);
		$this->putInt($this->x);
		$this->putInt($this->y);
		$this->putInt($this->z);
		$this->putLong($this->entityId);
	}
	*/
	public function encode(){
        $this->reset();
        $this->putByte($this->windowid);
        $this->putByte($this->type);
        $this->putShort($this->slots);
        $this->putInt($this->x);
        $this->putInt($this->y);
        $this->putInt($this->z);
        
        // 根据协议版本
        if(!in_array($this->protocol, [31, 37, 38, 39])){ // 0.14+
            $this->putLong($this->entityId);
        }
        // 0.13不发送entityId
    }

}