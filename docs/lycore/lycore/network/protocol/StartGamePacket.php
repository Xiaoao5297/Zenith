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

namespace lycore\network\protocol;

#include <rules/DataPacket.h>


class StartGamePacket extends DataPacket{
	const NETWORK_ID = Info::START_GAME_PACKET;

	public $seed;
	public $dimension;
	public $generator;
	public $gamemode;
	public $eid;
	public $spawnX;
	public $spawnY;
	public $spawnZ;
	public $x;
	public $y;
	public $z;
	public $unknown;
	public $protocol = Info::V014_CURRENT_PROTOCOL;

	public function decode(){

	}

	public function encode(){
		$this->reset();
		$this->putInt($this->seed);
		$this->putByte(ChangeDimensionPacket::getClientDimension($this->dimension));
		$this->putInt($this->generator);
		$this->putInt($this->gamemode);
		$this->putLong($this->eid);
		$this->putInt($this->spawnX);
		$this->putInt($this->spawnY);
		$this->putInt($this->spawnZ);
		$this->putFloat($this->x);
		$this->putFloat($this->y);
		$this->putFloat($this->z);
		if(ProtocolCompatibility::usesLegacySlotFormat((int) $this->protocol)){
			$this->putByte(0);
			return;
		}
		$this->putByte(1);
		$this->putByte(1);
		$this->putByte(0);
		$this->putString($this->unknown);
	}

}
