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


class SetEntityLinkPacketV84 extends DataPacketV84{
	const NETWORK_ID = InfoV84::SET_ENTITY_LINK_PACKET;
	
	const TYPE_REMOVE = 0;
	const TYPE_RIDE = 1;
	const TYPE_PASSENGER = 2;


	public $from;
	public $to;
	public $type;

	public function decode(){
		$this->from = $this->getLong();
		$this->to = $this->getLong();
		$this->type = $this->getByte();
	}

	public function encode(){
		$this->reset();
		$this->putLong($this->from);
		$this->putLong($this->to);
		$this->putByte($this->type);
	}

}
