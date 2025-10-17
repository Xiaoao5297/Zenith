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


class MobEffectPacket extends DataPacket{
	const NETWORK_ID = Info::MOB_EFFECT_PACKET;

	const EVENT_ADD = 1;
	const EVENT_MODIFY = 2;
	const EVENT_REMOVE = 3;

	public $eid;
	public $eventId;
	public $effectId;
	public $amplifier;
	public $particles = true;
	public $duration;

	public function decode(){

	}

	public function encode(){
		$this->reset();
		$this->putLong($this->eid);
		$this->putByte($this->eventId);
		$this->putByte($this->effectId);
		$this->putByte($this->amplifier);
		$this->putByte($this->particles ? 1 : 0);
		$this->putInt($this->duration);
	}

}
