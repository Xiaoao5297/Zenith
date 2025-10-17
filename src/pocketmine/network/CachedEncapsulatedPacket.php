<?php

/* 
 * This file is part of the PocketMine-MP project.
 *  _____              _ __  __  
 * /__  /  ___  ____  (_) /_/ /_ 
 *   / /  / _ \/ __ \/ / __/ __ \
 *  / /__/  __/ / / / / /_/ / / /
 * /____/\___/_/ /_/_/\__/_/ /_/ 
 *                               
 * This program is free software: you can redistribute/modify it 
 * under the terms of the GNU LGPL, version 3 or later.
 *
 * @author Xiaoao
 * @link https://b23.tv/LQKxdts
 *
*/

namespace pocketmine\network;

use raklib\protocol\EncapsulatedPacket;

class CachedEncapsulatedPacket extends EncapsulatedPacket{

	private $internalData = null;

	public function toBinary($internal = false){
		return $this->internalData === null ? ($this->internalData = parent::toBinary($internal)) : $this->internalData;
	}
}