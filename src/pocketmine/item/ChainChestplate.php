<?php

/*
 *  ______   _____    ______  __   __  ______
 * /  ___/  /  ___|  / ___  \ \ \ / / |  ____|
 * | |___  | |      | |___| |  \ / /  | |____
 * \___  \ | |      |  ___  |   / /   |  ____|
 *  ___| | | |____  | |   | |  / / \  | |____
 * /_____/  \_____| |_|   |_| /_/ \_\ |______|
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * @author Sunch233#3226 QQ2125696621 And KKK
 * @link https://github.com/ScaxeTeam/Scaxe/
 *
*/

namespace pocketmine\item;


class ChainChestplate extends Armor{
	public function __construct($meta = 0, $count = 1){
		parent::__construct(self::CHAIN_CHESTPLATE, $meta, $count, "Chain Chestplate");
	}
	
	public function getArmorTier(){
		return Armor::TIER_CHAIN;
	}

	public function getArmorType(){
		return Armor::TYPE_CHESTPLATE;
	}

	public function getMaxDurability(){
		return 241;
	}

	public function getArmorValue(){
		return 5;
	}

	public function isChestplate(){
		return true;
	}
}