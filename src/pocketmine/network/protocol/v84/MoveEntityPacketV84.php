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


class MoveEntityPacketV84 extends DataPacketV84{
	const NETWORK_ID = InfoV84::MOVE_ENTITY_PACKET;

	public $eid;
	public $x;
	public $y;
	public $z;
	public $yaw;
	public $headYaw;
	public $pitch;

	public function decode(){
		$this->eid = $this->getLong();
		$this->x = $this->getFloat();
		$this->y = $this->getFloat();
		$this->z = $this->getFloat();
		$this->pitch = $this->getByte()*(360.0/256);
		$this->yaw = $this->getByte()*(360.0/256);
		$this->headYaw = $this->getByte()*(360.0/256);
	}

	public function encode(){
		$this->reset();
		$this->putLong($this->eid);
		$this->putFloat($this->x);
		$this->putFloat($this->y);
		$this->putFloat($this->z);
		$this->putByte($this->pitch/(360.0/256));
		$this->putByte($this->yaw/(360.0/256));
		$this->putByte($this->headYaw/(360.0/256));
	}
}
