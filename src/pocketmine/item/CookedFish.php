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

class CookedFish extends Fish{
	public function __construct($meta = 0, $count = 1){
		Food::__construct(self::COOKED_FISH, $meta, $count, $meta === self::FISH_SALMON ? "Cooked Salmon" : "Cooked Fish");
	}

	public function getFoodRestore() : int{
		return $this->meta === self::FISH_SALMON ? 6 : 5;
	}

	public function getSaturationRestore() : float{
		return $this->meta === self::FISH_SALMON ? 9.6 : 6;
	}
}
