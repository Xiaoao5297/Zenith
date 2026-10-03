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

#endif


use lycore\entity\Entity;
use lycore\utils\Binary;
use lycore\utils\BinaryStream;
use lycore\utils\Utils;


abstract class DataPacket extends BinaryStream{

	const NETWORK_ID = 0;

	public $isEncoded = false;
	private $channel = 0;

	public function pid(){
		return $this::NETWORK_ID;
	}

	abstract public function encode();

	abstract public function decode();

	public function reset(){
		$this->buffer = chr($this::NETWORK_ID);
		$this->offset = 0;
	}

	/**
	 * @deprecated This adds extra overhead on the network, so its usage is now discouraged. It was a test for the viability of this.
	 */
	public function setChannel($channel){
		$this->channel = (int) $channel;
		return $this;
	}

	public function getChannel(){
		return $this->channel;
	}

	public function getEncapsulatedPacketCacheKey(bool $legacy013) : string{
		return $legacy013 ? "__encapsulatedPacket013" : "__encapsulatedPacket014";
	}

	protected function usesProtocol013SlotFormat() : bool{
		return isset($this->protocol) && ProtocolCompatibility::usesLegacySlotFormat((int) $this->protocol);
	}

	protected function getMetadataFromBuffer() : array{
		$metadata = [];
		while(!$this->feof()){
			$header = $this->getByte();
			if($header === 0x7f){
				break;
			}

			$index = $header & 0x1f;
			$type = $header >> 5;
			switch($type){
				case Entity::DATA_TYPE_BYTE:
					$value = Binary::readByte($this->get(1));
					break;
				case Entity::DATA_TYPE_SHORT:
					$value = $this->getLShort();
					break;
				case Entity::DATA_TYPE_INT:
					$value = $this->getLInt();
					break;
				case Entity::DATA_TYPE_FLOAT:
					$value = $this->getLFloat();
					break;
				case Entity::DATA_TYPE_STRING:
					$value = $this->get($this->getLShort(false));
					break;
				case Entity::DATA_TYPE_SLOT:
					$value = [$this->getLShort(), $this->getByte(), $this->getLShort()];
					break;
				case Entity::DATA_TYPE_POS:
					$value = [$this->getLInt(), $this->getLInt(), $this->getLInt()];
					break;
				case Entity::DATA_TYPE_LONG:
					$value = $this->getLLong();
					break;
				default:
					return [];
			}

			$metadata[$index] = [$type, $value];
		}

		return $metadata;
	}

	public function clearEncapsulatedPacketCache(){
		unset($this->__encapsulatedPacket, $this->__encapsulatedPacket013, $this->__encapsulatedPacket014);
	}

	public function clean(){
		$this->buffer = null;
		$this->isEncoded = false;
		$this->offset = 0;
		$this->clearEncapsulatedPacketCache();
		return $this;
	}

	public function __debugInfo(){
		$data = [];
		foreach($this as $k => $v){
			if($k === "buffer"){
				$data[$k] = bin2hex($v);
			}elseif(is_string($v) or (is_object($v) and method_exists($v, "__toString"))){
				$data[$k] = Utils::printable((string) $v);
			}else{
				$data[$k] = $v;
			}
		}

		return $data;
	}
}
