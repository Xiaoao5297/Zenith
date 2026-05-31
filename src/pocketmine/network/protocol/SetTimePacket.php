<?php


namespace pocketmine\network\protocol;

#include <rules/DataPacket.h>


use pocketmine\level\Level;

class SetTimePacket extends DataPacket{
	const NETWORK_ID = Info::SET_TIME_PACKET;

	public $time;
	public $started = true;

	public function decode(){

	}
/*
	public function encode(){
		$this->reset();
		$this->putInt($this->time);
		$this->putByte($this->started ? 1 : 0);
	}
	*/
	public function encode(){
    $this->reset();
    
    if(in_array($this->protocol, [31, 37, 38, 39])){ // 0.13
            // 0.13可能使用不同的时间格式
            $this->putInt((int) (($this->time / 19200) * 19200));
        } else { // 0.14
            $this->putInt($this->time);
        }
        
        $this->putByte($this->started ? 1 : 0);
    }

}