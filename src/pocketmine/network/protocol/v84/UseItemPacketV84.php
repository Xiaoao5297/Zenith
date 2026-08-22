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


class UseItemPacketV84 extends DataPacketV84{
	const NETWORK_ID = InfoV84::USE_ITEM_PACKET;

	public $x;
	public $y;
	public $z;
	public $face;
	public $item;
	public $fx;
	public $fy;
	public $fz;
	public $posX;
	public $posY;
	public $posZ;
	public $slot;

	public function decode(){
		$this->x = $this->getInt();
		$this->y = $this->getInt();
		$this->z = $this->getInt();
		$this->face = $this->getByte();
		$this->fx = $this->getFloat();
		$this->fy = $this->getFloat();
		$this->fz = $this->getFloat();
		$this->posX = $this->getFloat();
		$this->posY = $this->getFloat();
		$this->posZ = $this->getFloat();
		$this->slot = $this->getInt();
		$this->item = $this->getSlot();
	}

	public function encode(){

	}
}
