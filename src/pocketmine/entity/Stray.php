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

/*
 * 移植自 lycore\entity\Stray，命名空间改为 pocketmine\entity。
 */

namespace pocketmine\entity;

use pocketmine\item\Arrow as ItemArrow;
use pocketmine\item\Item as ItemItem;
use pocketmine\item\Potion;

class Stray extends Skeleton{
	const NETWORK_ID = 46;

	public function getName() : string{
		return "Stray";
	}

	public function getProjectileArrowItem(){
		return ItemItem::get(ItemItem::ARROW, ItemArrow::getArrowMetaFromPotionMeta(Potion::SLOWNESS), 1);
	}
}
