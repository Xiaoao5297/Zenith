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


class UpdateBlockPacket extends DataPacket{
	const NETWORK_ID = Info::UPDATE_BLOCK_PACKET;

	const FLAG_NONE      = 0b0000;
	const FLAG_NEIGHBORS = 0b0001;
    const FLAG_NETWORK   = 0b0010;
	const FLAG_NOGRAPHIC = 0b0100;
	const FLAG_PRIORITY  = 0b1000;

	const FLAG_ALL = (self::FLAG_NEIGHBORS | self::FLAG_NETWORK);
	const FLAG_ALL_PRIORITY = (self::FLAG_ALL | self::FLAG_PRIORITY);

	public $records = []; //x, z, y, blockId, blockData, flags

	public function decode(){

	}

	public function encode(){
		$this->reset();
		$this->putInt(count($this->records));
		foreach($this->records as $r){
			$this->putInt($r[0]);
			$this->putInt($r[1]);
			$this->putByte($r[2]);
			$this->putByte($r[3]);
			$this->putByte(($r[5] << 4) | $r[4]);
		}
	}

}