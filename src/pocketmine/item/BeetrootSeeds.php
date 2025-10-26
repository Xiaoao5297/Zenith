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

use pocketmine\block\Block;

class BeetrootSeeds extends Item{
	public function __construct($meta = 0, $count = 1){
		$this->block = Block::get(Item::BEETROOT_BLOCK);
		parent::__construct(self::BEETROOT_SEEDS, 0, $count, "Beetroot Seeds");
	}
}