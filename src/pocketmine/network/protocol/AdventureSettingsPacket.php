<?php

namespace pocketmine\network\protocol;

#include <rules/DataPacket.h>


class AdventureSettingsPacket extends DataPacket{
	const NETWORK_ID = Info::ADVENTURE_SETTINGS_PACKET;

	public $flags;
	public $userPermission;
	public $globalPermission;

	public function decode(){

	}
/*
	public function encode(){
		$this->reset();
		$this->putInt($this->flags);
		$this->putInt($this->userPermission);
		$this->putInt($this->globalPermission);
	}
*/
    public function encode(){
        $this->reset();
        $this->putInt($this->flags);
        
        // 根据协议版本
        if(in_array($this->protocol, [31, 37, 38, 39])){ // 0.13
            // 0.13只有flags，不发送后面两个字段
        } else { // 0.14
            $this->putInt($this->userPermission);
            $this->putInt($this->globalPermission);
        }
    }
}