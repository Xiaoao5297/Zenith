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

class ConnectPacket extends DataPacket{
	const NETWORK_ID = Info::CONNECT_PACKET;

	public $protocol = Info::CURRENT_PROTOCOL;
	public $maxPlayers;
	public $isMainServer;
	public $description;
	public $encodedPassword;

	public function encode(){
		$this->reset();
		$this->putInt($this->protocol);
		$this->putInt($this->maxPlayers);
		$this->putByte($this->isMainServer ? 1 : 0);
		$this->putString($this->description);
		$this->putString($this->encodedPassword);
	}

	public function decode(){
		$this->protocol = $this->getInt();
		$this->maxPlayers = $this->getInt();
		$this->isMainServer = ($this->getByte() == 1) ? true : false;
		$this->description = $this->getString();
		$this->encodedPassword = $this->getString();
	}

}