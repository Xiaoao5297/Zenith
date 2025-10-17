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


class InteractPacket extends DataPacket{
	const NETWORK_ID = Info::INTERACT_PACKET;

	const ACTION_RIGHT_CLICK = 1;
	const ACTION_LEFT_CLICK = 2;
	const ACTION_LEAVE_VEHICLE = 3;

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
