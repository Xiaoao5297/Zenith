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

class PlayerInputPacket extends DataPacket{
	const NETWORK_ID = Info::PLAYER_INPUT_PACKET;

	public $motX;
	public $motY;

	public $jumping;
	public $sneaking;

	public function decode(){
		$this->motX = $this->getFloat();
		$this->motY = $this->getFloat();
		$flags = $this->getByte();
		$this->jumping = (($flags & 0x80) > 0);
		$this->sneaking = (($flags & 0x40) > 0);
	}

	public function encode(){

	}

}
