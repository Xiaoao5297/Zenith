<?php

namespace pocketmine\network\protocol;

#include <rules/DataPacket.h>


class MoveEntityPacket extends DataPacket{
	const NETWORK_ID = Info::MOVE_ENTITY_PACKET;


	// 线上顺序：eid, x, y, z, yaw, headYaw, pitch
	// 注意：0.14 客户端实测按 yaw, headYaw, pitch 解释；不要改成 yaw,pitch,headYaw。
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
