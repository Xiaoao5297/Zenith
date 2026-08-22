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

namespace pocketmine\network\protocol\v84;

#include <rules/DataPacket.h>


class FullChunkDataPacketV84 extends DataPacketV84{
	const NETWORK_ID = InfoV84::FULL_CHUNK_DATA_PACKET;
	
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
		$this->putInt(strlen($this->data));
		$this->put($this->data);
	}

}
