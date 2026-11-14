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
namespace pocketmine\network\protocol\v84;
#include <rules/DataPacket.h>
use pocketmine\entity\Attribute;
class UpdateAttributesPacketV84 extends DataPacketV84{
	const NETWORK_ID = InfoV84::UPDATE_ATTRIBUTES_PACKET;
	public $entityId;
	/** @var Attribute[] */
	public $entries = [];
	public function decode(){
	}
	public function encode(){
		$this->reset();
		$this->putLong($this->entityId);
		$this->putShort(count($this->entries));
		foreach($this->entries as $entry){
			$this->putFloat($entry->getMinValue());
			$this->putFloat($entry->getMaxValue());
			$this->putFloat($entry->getValue());
			$this->putString($entry->getName());
		}
	}
}