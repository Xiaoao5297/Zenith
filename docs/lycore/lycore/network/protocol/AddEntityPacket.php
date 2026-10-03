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

#ifndef COMPILE
use lycore\utils\Binary;

#endif

class AddEntityPacket extends DataPacket{
	const NETWORK_ID = Info::ADD_ENTITY_PACKET;

	public $eid;
	public $type;
	public $x;
	public $y;
	public $z;
	public $speedX;
	public $speedY;
	public $speedZ;
	public $yaw;
	public $pitch;
	public $modifiers;
	public $metadata = [];
	public $links = [];

	public function decode(){
		$this->eid = $this->getLong();
		$this->type = $this->getInt();
		$this->x = $this->getFloat();
		$this->y = $this->getFloat();
		$this->z = $this->getFloat();
		$this->speedX = $this->getFloat();
		$this->speedY = $this->getFloat();
		$this->speedZ = $this->getFloat();
		$this->yaw = $this->getFloat() / 0.71111;
		$this->pitch = $this->getFloat() / 0.71111;
		$this->modifiers = $this->getInt();
		$this->metadata = $this->getMetadataFromBuffer();
		$this->links = [];
		if(!$this->feof()){
			$count = $this->getShort();
			for($i = 0; $i < $count and !$this->feof(); ++$i){
				$this->links[] = [$this->getLong(), $this->getLong(), $this->getByte()];
			}
		}
	}

	public function encode(){
		$this->reset();
		$this->putLong($this->eid);
		$this->putInt($this->type);
		$this->putFloat($this->x);
		$this->putFloat($this->y);
		$this->putFloat($this->z);
		$this->putFloat($this->speedX);
		$this->putFloat($this->speedY);
		$this->putFloat($this->speedZ);
		if(ProtocolCompatibility::isProtocol012((int) ($this->protocol ?? 0))){
			$this->putFloat($this->yaw);
			$this->putFloat($this->pitch);
			$meta = Binary::writeMetadata($this->metadata);
			$this->put($meta);
			$this->putShort(count($this->links));
			foreach($this->links as $link){
				$this->putLong($link[0]);
				$this->putLong($link[1]);
				$this->putByte($link[2]);
			}
			return;
		}
		$this->putFloat($this->yaw * 0.71111);
		$this->putFloat($this->pitch * 0.71111);
		$this->putInt($this->modifiers);
		$meta = Binary::writeMetadata($this->metadata);
		$this->put($meta);
		$this->putShort(count($this->links));
		foreach($this->links as $link){
			$this->putLong($link[0]);
			$this->putLong($link[1]);
			$this->putByte($link[2]);
		}
	}

}
