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


class TextPacketV84 extends DataPacketV84{
	const NETWORK_ID = InfoV84::TEXT_PACKET;

	const TYPE_RAW = 0;
	const TYPE_CHAT = 1;
	const TYPE_TRANSLATION = 2;
	const TYPE_POPUP = 3;
	const TYPE_TIP = 4;
	const TYPE_SYSTEM = 5;

	public $type;
	public $source;
	public $message;
	public $parameters = [];

	public function decode(){
		$this->type = $this->getByte();
		switch($this->type){
			case self::TYPE_POPUP:
			case self::TYPE_CHAT:
				$this->source = $this->getString();
			case self::TYPE_RAW:
			case self::TYPE_TIP:
			case self::TYPE_SYSTEM:
				$this->message = $this->getString();
				break;

			case self::TYPE_TRANSLATION:
				$this->message = $this->getString();
				$count = $this->getByte();
				for($i = 0; $i < $count; ++$i){
					$this->parameters[] = $this->getString();
				}
		}
	}

	public function encode(){
		$this->reset();
		$this->putByte($this->type);
		switch($this->type){
			case self::TYPE_POPUP:
			case self::TYPE_CHAT:
				$this->putString($this->source);
			case self::TYPE_RAW:
			case self::TYPE_TIP:
			case self::TYPE_SYSTEM:
				$this->putString($this->message);
				break;

			case self::TYPE_TRANSLATION:
				$this->putString($this->message);
				$this->putByte(count($this->parameters));
				foreach($this->parameters as $p){
					$this->putString($p);
				}
		}
	}

}
