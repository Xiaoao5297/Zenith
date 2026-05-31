<?php

namespace pocketmine\network\protocol;

#include <rules/DataPacket.h>


class MovePlayerPacket extends DataPacket{
	const NETWORK_ID = Info::MOVE_PLAYER_PACKET;

	const MODE_NORMAL = 0;
	const MODE_RESET = 1;
	const MODE_ROTATION = 2;

	public $eid;
	public $x;
	public $y;
	public $z;
	public $yaw;
	public $bodyYaw;
	public $pitch;
	public $mode = self::MODE_NORMAL;
	public $onGround;

	public function clean(){
		$this->teleport = false;
		return parent::clean();
	}

	public function decode(){
		$this->eid = $this->getLong();
		$this->x = $this->getFloat();
		$this->y = $this->getFloat();
		$this->z = $this->getFloat();
		$this->yaw = $this->getFloat();
		$this->bodyYaw = $this->getFloat();
		$this->pitch = $this->getFloat();
		$this->mode = $this->getByte();
		$this->onGround = $this->getByte() > 0;
	}

	public function encode(){
		$this->reset();
		$this->putLong($this->eid);
		$this->putFloat($this->x);
		$this->putFloat($this->y);
		$this->putFloat($this->z);
		$this->putFloat($this->yaw);
		$this->putFloat($this->bodyYaw); //TODO
		$this->putFloat($this->pitch);
		$this->putByte($this->mode);
		$this->putByte($this->onGround > 0);
		
		// 根据协议版本添加额外处理
		$is013 = in_array($this->protocol, [31, 37, 38, 39]);
		
		if($is013){
			// 0.13协议可能需要额外的处理
		} else {
			// 0.14协议可能需要额外的处理
		}
	}

}
