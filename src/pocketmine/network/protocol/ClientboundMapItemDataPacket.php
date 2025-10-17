<?php

/*
 *    _  __  _                             _____                               
 *   | |/ / (_)____ _ ____   ____ _ ____  / ___/ ___   _____ _   __ ___   _____
 *   |   / / // __ `// __ \ / __ `// __ \ \__ \ / _ \ / ___/| | / // _ \ / ___/
 *  /   | / // /_/ // /_/ // /_/ // /_/ /___/ //  __// /    | |/ //  __// /    
 * /_/|_|/_/ \__,_/ \____/ \__,_/ \____//____/ \___//_/     |___/ \___//_/     
 *                                                                             
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * @author Xiaoao
 * @link https://b23.tv/LQKxdts
 *
 *
*/

namespace pocketmine\network\protocol;

use pocketmine\utils\MapColor;

class ClientboundMapItemDataPacket extends DataPacket {

	const NETWORK_ID = Info::CLIENTBOUND_MAP_ITEM_DATA_PACKET;

	const BITFLAG_TEXTURE_UPDATE = 0x02;
	const BITFLAG_DECORATION_UPDATE = 0x04;

	/** @var int */
	public $mapId;
	/** @var int */
	public $type;
	/** @var int */
	public $scale = 0;

	/** @var array */
	public $decorations = []; //TODO:

	/** @var int */
	public $width = 128;
	/** @var int */
	public $height = 128;
	/** @var int */
	public $xOffset = 0;
	/** @var int */
	public $yOffset = 0;

	/** @var Color[][]|string */
	public $colors;

	/** @var bool */
	public $isColorArray = true;

	public function decode() {
		//TODO:
	}

	public function encode() {
		$this->reset();
		$this->putLong($this->mapId);

		$type = 0x00;
		
		if(count($this->colors) > 0){
			$type |= self::BITFLAG_TEXTURE_UPDATE;
		}
		$this->putInt($type);

		if(($type & self::BITFLAG_TEXTURE_UPDATE) !== 0) {
			$this->putByte($this->scale);

			$this->putInt($this->width);
			$this->putInt($this->height);
			$this->putInt($this->xOffset);
			$this->putInt($this->yOffset);

			if($this->isColorArray) {
				for($y = 0; $y < $this->height; ++$y){
					for($x = 0; $x < $this->width; ++$x) {
						$color = $this->colors[$y][$x];
						
						$this->putByte($color->getR());
						$this->putByte($color->getG());
						$this->putByte($color->getB());
						$this->putByte($color->getA());
					}
				}
			} else {
				$this->put($this->colors);
			}
		}
	}

}