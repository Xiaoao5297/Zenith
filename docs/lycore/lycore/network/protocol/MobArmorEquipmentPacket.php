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


class MobArmorEquipmentPacket extends DataPacket{
	const NETWORK_ID = Info::MOB_ARMOR_EQUIPMENT_PACKET;

	public $eid;
	public $slots = [];

	public function decode(){
		$this->eid = $this->getLong();
		$this->slots[0] = $this->getSlot($this->usesProtocol013SlotFormat());
		$this->slots[1] = $this->getSlot($this->usesProtocol013SlotFormat());
		$this->slots[2] = $this->getSlot($this->usesProtocol013SlotFormat());
		$this->slots[3] = $this->getSlot($this->usesProtocol013SlotFormat());
	}

	public function encode(){
		$this->reset();
		$this->putLong($this->eid);
		$this->putSlot($this->slots[0], $this->usesProtocol013SlotFormat());
		$this->putSlot($this->slots[1], $this->usesProtocol013SlotFormat());
		$this->putSlot($this->slots[2], $this->usesProtocol013SlotFormat());
		$this->putSlot($this->slots[3], $this->usesProtocol013SlotFormat());
	}

}
