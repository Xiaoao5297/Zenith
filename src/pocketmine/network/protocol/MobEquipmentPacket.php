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


class MobEquipmentPacket extends DataPacket{
	const NETWORK_ID = Info::MOB_EQUIPMENT_PACKET;

	public $eid;
	public $item;
	public $slot;
	public $selectedSlot;

	public function decode(){
		$this->eid = $this->getLong();
		$this->item = $this->getSlot();
		$this->slot = $this->getByte();
		$this->selectedSlot = $this->getByte();
	}

	public function encode(){
		$this->reset();
		$this->putLong($this->eid);
		$this->putSlot($this->item);
		$this->putByte($this->slot);
		$this->putByte($this->selectedSlot);
	}

}
