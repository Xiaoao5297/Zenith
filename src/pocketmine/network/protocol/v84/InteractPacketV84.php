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


class InteractPacketV84 extends DataPacketV84{
	const NETWORK_ID = InfoV84::INTERACT_PACKET;

	const ACTION_RIGHT_CLICK = 1;
	const ACTION_LEFT_CLICK = 2;
	const ACTION_LEAVE_VEHICLE = 3;
	const ACTION_MOUSEOVER = 4;

	public $action;
	public $eid;
	public $target;

	public function decode(){
		$this->action = $this->getByte();
		$this->target = $this->getLong();
	}

	public function encode(){
		$this->reset();
		$this->putByte($this->action);
		$this->putLong($this->target);
	}

}
