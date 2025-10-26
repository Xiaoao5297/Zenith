<?php

/* 
 *  _____              _ __  __  
 * /__  /  ___  ____  (_) /_/ /_ 
 *   / /  / _ \/ __ \/ / __/ __ \
 *  / /__/  __/ / / / / /_/ / / /
 * /____/\___/_/ /_/_/\__/_/ /_/ 
 *                               
 * This program is free software: you can redistribute/modify it 
 * under the terms of the GNU LGPL, version 3 or later.
 *
 * @author Xiaoao
 * @link https://b23.tv/LQKxdts
 *
*/

namespace pocketmine\item;


class IronLeggings extends Armor{
	public function __construct($meta = 0, $count = 1){
		parent::__construct(self::IRON_LEGGINGS, $meta, $count, "Iron Leggings");
	}

	public function getArmorTier(){
		return Armor::TIER_IRON;
	}

	public function getArmorType(){
		return Armor::TYPE_LEGGINGS;
	}

	public function getMaxDurability(){
		return 226;
	}

	public function getArmorValue(){
		return 5;
	}

	public function isLeggings(){
		return true;
	}
}