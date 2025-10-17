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


class GoldBoots extends Armor{
	public function __construct($meta = 0, $count = 1){
		parent::__construct(self::GOLD_BOOTS, $meta, $count, "Gold Boots");
	}

	public function getArmorTier(){
		return Armor::TIER_GOLD;
	}

	public function getArmorType(){
		return Armor::TYPE_BOOTS;
	}

	public function getMaxDurability(){
		return 92;
	}

	public function getArmorValue(){
		return 1;
	}

	public function isBoots(){
		return true;
	}
}