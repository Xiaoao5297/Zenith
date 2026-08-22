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

#ifndef COMPILE
use pocketmine\utils\v84\Binary;

#endif

class SetEntityDataPacketV84 extends DataPacketV84{
	const NETWORK_ID = InfoV84::SET_ENTITY_DATA_PACKET;

	public $eid;
	public $metadata;

	public function decode(){

	}

	public function encode(){
		$this->reset();
		$this->putLong($this->eid);
		$meta = Binary::writeMetadata($this->metadata);
		$this->put($meta);
	}

}
