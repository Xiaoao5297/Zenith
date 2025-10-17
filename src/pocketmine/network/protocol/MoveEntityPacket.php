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


class MoveEntityPacket extends DataPacket{
	const NETWORK_ID = Info::MOVE_ENTITY_PACKET;


	// eid, x, y, z, yaw, pitch
	/** @var array[] */
	public $entities = [];

	public function clean(){
		$this->entities = [];
		return parent::clean();
	}

	public function decode(){

	}

	public function encode(){
		$this->reset();
		$this->putInt(count($this->entities));
		foreach($this->entities as $d){
			$this->putLong($d[0]); //eid
			$this->putFloat($d[1]); //x
			$this->putFloat($d[2]); //y
			$this->putFloat($d[3]); //z
			$this->putFloat($d[4]); //yaw
			$this->putFloat($d[5]); //headYaw
			$this->putFloat($d[6]); //pitch
		}
	}

}
