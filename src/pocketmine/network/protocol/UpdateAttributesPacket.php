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

namespace pocketmine\network\protocol;
#include <rules/DataPacket.h>
use pocketmine\entity\Attribute;
class UpdateAttributesPacket extends DataPacket{
	const NETWORK_ID = Info::UPDATE_ATTRIBUTES_PACKET;
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