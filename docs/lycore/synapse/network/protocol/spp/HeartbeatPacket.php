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

class HeartbeatPacket extends DataPacket{
	const NETWORK_ID = Info::HEARTBEAT_PACKET;

	public $tps;
	public $load;
	public $upTime;

	public function encode(){
		$this->reset();
		$this->putFloat($this->tps);
		$this->putFloat($this->load);
		$this->putLong($this->upTime);
	}

	public function decode(){
		$this->tps = $this->getFloat();
		$this->load = $this->getFloat();
		$this->upTime = $this->getLong();
	}
}