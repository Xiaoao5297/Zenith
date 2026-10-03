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
 
namespace synapse\network\protocol\spp;

use pocketmine\utils\UUID;

class RedirectPacket extends DataPacket{
	const NETWORK_ID = Info::REDIRECT_PACKET;

	/** @var UUID */
	public $uuid;
	public $direct;
	public $mcpeBuffer;

	public function encode(){
		$this->reset();
		$this->putUUID($this->uuid);
		$this->putByte($this->direct ? 1 : 0);
		$this->putString($this->mcpeBuffer);
	}

	public function decode(){
		$this->uuid = $this->getUUID();
		$this->direct = ($this->getByte() == 1) ? true : false;
		$this->mcpeBuffer = $this->getString();
	}
}