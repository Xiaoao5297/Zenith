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


class BlockEventPacket extends DataPacket{
	const NETWORK_ID = Info::BLOCK_EVENT_PACKET;

	public $x;
	public $y;
	public $z;
	public $case1;
	public $case2;

	public function decode(){

	}

	public function encode(){
		$this->reset();
		$this->putInt($this->x);
		$this->putInt($this->y);
		$this->putInt($this->z);
		$this->putInt($this->case1);
		$this->putInt($this->case2);
	}

}