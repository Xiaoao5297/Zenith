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

class ChangeDimensionPacket extends DataPacket{
	const NETWORK_ID = Info::CHANGE_DIMENSION_PACKET;

	const DIMENSION_NORMAL = 0;
	const DIMENSION_NETHER = 1;
	const DIMENSION_END = 2;

	public $dimension;
	public $protocol = Info::V014_CURRENT_PROTOCOL;
	public $x = null;
	public $y = null;
	public $z = null;

	public function decode(){

	}

	public static function getClientDimension($dimension){
		// Protocol 70 clients do not have a stable End dimension.
		return $dimension === self::DIMENSION_END ? self::DIMENSION_NORMAL : $dimension;
	}

	public static function hasClientDimensionChanged($oldDimension, $newDimension){
		return self::getClientDimension($oldDimension) !== self::getClientDimension($newDimension);
	}

	public function encode(){
		$this->reset();
		$this->putByte(self::getClientDimension($this->dimension));
		$this->putByte(0);
	}

}
