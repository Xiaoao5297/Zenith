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

namespace lycore\inventory;

use lycore\network\protocol\Info as ProtocolInfo;

/**
 * Saves all the information regarding default inventory sizes and types
 */
class InventoryType{
	const CHEST = 0;
	const DOUBLE_CHEST = 1;
	const PLAYER = 2;
	const FURNACE = 3;
	const CRAFTING = 4;
	const WORKBENCH = 5;
	const STONECUTTER = 6;
	const BREWING_STAND = 7;
	const ANVIL = 8;
	const ENCHANT_TABLE = 9;
	const DISPENSER = 10;
	const DROPPER = 11;
	const HOPPER = 12;
	const HORSE = 13;

	private static $default = [];

	private $size;
	private $title;
	private $typeId;
	private $typeId013;
	private $typeId014;
	private $typeId015;

	/**
	 * @param $index
	 *
	 * @return InventoryType
	 */
	public static function get($index){
		return isset(static::$default[$index]) ? static::$default[$index] : null;
	}

	public static function init($num = 36){
		if(count(static::$default) > 0){
			return;
		}

		static::$default[static::CHEST] = new InventoryType(27, "Chest", 0);
		static::$default[static::DOUBLE_CHEST] = new InventoryType(27 + 27, "Double Chest", 0);
		static::$default[static::PLAYER] = new InventoryType($num + 4 + 9, "Player", 0); //27 CONTAINER, 4 ARMOR (9 reference HOTBAR slots)
		static::$default[static::FURNACE] = new InventoryType(3, "Furnace", 2);
		static::$default[static::CRAFTING] = new InventoryType(5, "Crafting", 1); //4 CRAFTING slots, 1 RESULT
		static::$default[static::WORKBENCH] = new InventoryType(10, "Crafting", 1); //9 CRAFTING slots, 1 RESULT
		static::$default[static::STONECUTTER] = new InventoryType(10, "Crafting", 1); //9 CRAFTING slots, 1 RESULT
		// Default to the LY Core 0.15 window IDs and remap 0.13 when sending.
		static::$default[static::ENCHANT_TABLE] = new InventoryType(2, "Enchant", 3, 4); //1 INPUT/OUTPUT, 1 LAPIS
		static::$default[static::BREWING_STAND] = new InventoryType(4, "Brewing", 4, 5); //1 INPUT, 3 POTION
		static::$default[static::ANVIL] = new InventoryType(3, "Anvil", 5, 6); //2 INPUT, 1 OUTPUT
		static::$default[static::DISPENSER] = new InventoryType(9, "Dispenser", 6, 6); //9 CONTAINER
		static::$default[static::DROPPER] = new InventoryType(9, "Dropper", 7, 7); //9 CONTAINER
		static::$default[static::HOPPER] = new InventoryType(5, "Hopper", 8, 8); //5 CONTAINER
		static::$default[static::HORSE] = new InventoryType(2, "Horse", 12, null, 0, 0); //1 SADDLE, 1 ARMOR
	}

	/**
	 * @param int    $defaultSize
	 * @param string $defaultTitle
	 * @param int    $typeId
	 * @param int    $typeId013
	 * @param int    $typeId014
	 * @param int    $typeId015
	 */
	private function __construct($defaultSize, $defaultTitle, $typeId = 0, $typeId013 = null, $typeId014 = null, $typeId015 = null){
		$this->size = $defaultSize;
		$this->title = $defaultTitle;
		$this->typeId = $typeId;
		$this->typeId013 = $typeId013;
		$this->typeId014 = $typeId014;
		$this->typeId015 = $typeId015;
	}

	/**
	 * @return int
	 */
	public function getDefaultSize(){
		return $this->size;
	}

	/**
	 * @return string
	 */
	public function getDefaultTitle(){
		return $this->title;
	}

	/**
	 * @return int
	 */
	public function getNetworkType(){
		return $this->typeId;
	}

	public function getNetworkTypeForProtocol($protocol){
		if(in_array($protocol, ProtocolInfo::V012_PROTOCOLS, true) or in_array($protocol, ProtocolInfo::ACCEPTED_013_PROTOCOLS, true)){
			if($this->typeId013 !== null){
				return $this->typeId013;
			}
		}

		if(in_array($protocol, ProtocolInfo::V014_PROTOCOLS, true)){
			if($this->typeId014 !== null){
				return $this->typeId014;
			}
		}

		if(in_array($protocol, ProtocolInfo::V015_PROTOCOLS, true)){
			if($this->typeId015 !== null){
				return $this->typeId015;
			}
		}

		return $this->typeId;
	}
}
