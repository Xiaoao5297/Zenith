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

namespace lycore\item;


class ChainHelmet extends Armor{
	public function __construct($meta = 0, $count = 1){
		parent::__construct(self::CHAIN_HELMET, $meta, $count, "Chainmail Helmet");
	}

	public function getArmorTier(){
		return Armor::TIER_CHAIN;
	}

	public function getArmorType(){
		return Armor::TYPE_HELMET;
	}

	public function getMaxDurability(){
		return 166;
	}

	public function getArmorValue(){
		return 1;
	}

	public function isHelmet(){
		return true;
	}
}