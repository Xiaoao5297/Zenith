<?php

namespace pocketmine\network\protocol;

#include <rules/DataPacket.h>


class LoginPacket extends DataPacket{
	const NETWORK_ID = Info::LOGIN_PACKET;

	public $username;
	public $protocol1;
	public $protocol2;
	public $clientId;

	public $clientUUID;
	public $serverAddress;
	public $clientSecret;

	public $skinName = null;
	public $skin = null;

	public function decode(){
		$this->username = $this->getString();
		// 限制用户名长度，防止内存/日志攻击
		if(strlen($this->username) > 32){
			$this->username = substr($this->username, 0, 32);
		}
		$this->protocol1 = $this->getInt();
		$this->protocol2 = $this->getInt();
		$this->clientId = $this->getLong();
		$this->clientUUID = $this->getUUID();
		$this->serverAddress = $this->getString();
		$this->clientSecret = $this->getString();
		if($this->protocol1 == 34){
			$this->skinName = $this->getByte() > 0;
			$this->skin = $this->getString();
		}else{
			$this->skinName = $this->getString();
			$this->skin = $this->getString();
		}
	}

	public function encode(){

	}

}