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

use pocketmine\block\Block;

class SpruceDoor extends Item{
	public function __construct($meta = 0, $count = 1){
		$this->block = Block::get(Item::SPRUCE_DOOR_BLOCK);
		parent::__construct(self::SPRUCE_DOOR, 0, $count, "Spruce Door");
	}

	public function getMaxStackSize() : int {
		return 1;
	}
}