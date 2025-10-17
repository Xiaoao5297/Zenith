<?php

/*
 *    _  __  _                             _____                               
 *   | |/ / (_)____ _ ____   ____ _ ____  / ___/ ___   _____ _   __ ___   _____
 *   |   / / // __ `// __ \ / __ `// __ \ \__ \ / _ \ / ___/| | / // _ \ / ___/
 *  /   | / // /_/ // /_/ // /_/ // /_/ /___/ //  __// /    | |/ //  __// /    
 * /_/|_|/_/ \__,_/ \____/ \__,_/ \____//____/ \___//_/     |___/ \___//_/     
 *                                                                             
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * @author Xiaoao
 * @link https://b23.tv/LQKxdts
 *
 *
*/

namespace pocketmine\network\protocol;

#include <rules/DataPacket.h>

#ifndef COMPILE
use pocketmine\utils\Binary;

#endif

class AddPlayerPacket extends DataPacket{
	const NETWORK_ID = Info::ADD_PLAYER_PACKET;

	public $uuid;
	public $username;
	public $eid;
	public $x;
	public $y;
	public $z;
	public $speedX;
	public $speedY;
	public $speedZ;
	public $pitch;
	public $yaw;
	public $item;
	public $metadata;

	public function decode(){

	}

	public function encode(){
		$this->reset();
		$this->putUUID($this->uuid);
		$this->putString($this->username);
		$this->putLong($this->eid);
		$this->putFloat($this->x);
		$this->putFloat($this->y);
		$this->putFloat($this->z);
		$this->putFloat($this->speedX);
		$this->putFloat($this->speedY);
		$this->putFloat($this->speedZ);
		$this->putFloat($this->yaw);
		$this->putFloat($this->yaw); //TODO headrot
		$this->putFloat($this->pitch);
		$this->putSlot($this->item);

		$meta = Binary::writeMetadata($this->metadata);
		$this->put($meta);
	}

}
