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

class PlayerInputPacketV84 extends DataPacketV84{
	const NETWORK_ID = InfoV84::PLAYER_INPUT_PACKET;

	public $motX;
	public $motY;

	public $jumping;
	public $sneaking;

	public function decode(){
		$this->motX = $this->getFloat();
		$this->motY = $this->getFloat();
		$jumpFlag = $this->getByte();
		$sneakFlag = !$this->feof() ? $this->getByte() : null;
		$this->jumping = (($jumpFlag & 0x80) > 0) || (($jumpFlag & 0x01) > 0);
		$this->sneaking = (($jumpFlag & 0x40) > 0) || (($jumpFlag & 0x02) > 0) || ($sneakFlag !== null && (($sneakFlag & 0x01) > 0));
	}

	public function encode(){

	}

}
