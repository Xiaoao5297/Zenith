<?php

namespace pocketmine\network\protocol;

#include <rules/DataPacket.h>


class FullChunkDataPacket extends DataPacket{
	const NETWORK_ID = Info::FULL_CHUNK_DATA_PACKET;
	
	const ORDER_COLUMNS = 0;
	const ORDER_LAYERED = 1;

	public $chunkX;
	public $chunkZ;
	public $order = self::ORDER_COLUMNS;
	public $data;

	public function decode(){

	}

	public function encode(){
		$this->reset();
        $this->putInt($this->chunkX);
        $this->putInt($this->chunkZ);
        $this->putByte($this->order);
        
        // 根据协议版本处理chunk数据
        $is013 = in_array($this->protocol, [31, 37, 38, 39]);
        
        if($is013){
            // 0.13协议处理
            $this->putInt(strlen($this->data));
            $this->put($this->data);
        } else {
            // 0.14协议处理
            $this->putInt(strlen($this->data));
            $this->put($this->data);
        }
	}

}
