<?php

/*
 * ██╗███╗   ██╗ ██████╗ ██████╗ ██████╗ ███████╗
 * ██║████╗  ██║██╔════╝██╔═══██╗██╔══██╗██╔════╝
 * ██║██╔██╗ ██║██║     ██║   ██║██████╔╝█████╗
 * ██║██║╚██╗██║██║     ██║   ██║██╔══██╗██╔══╝
 * ██║██║ ╚████║╚██████╗╚██████╔╝██║  ██║███████╗
 * ╚═╝╚═╝  ╚═══╝ ╚═════╝ ╚═════╝ ╚═╝  ╚═╝╚══════╝
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * @Author: The_Bird, GoodCat456872
 *
 * @Links:
 *  > https://github.com/QwQ12222/InCore-II#incore-ii
 *  > https://gitcode.com/bad-cat-factory/InCore-II-InCore/tree/InCore-II
*/

/*
 * 从 InCore-II 移植进 Zenith：id 沿用 Zenith 已有的 Item::GOLDEN_HORSE_ARMOR（418）。
 *
 * 已注册到 Item::$list (创造模式物品列表未添加, 0.14 客户端无此物品)。
 */

namespace pocketmine\item;

class GoldenHorseArmor extends Item{
	public function __construct($meta = 0, $count = 1){
		parent::__construct(self::GOLDEN_HORSE_ARMOR, $meta, $count, "Golden Horse Armor");
	}

	public function getMaxStackSize() : int{
		return 1;
	}
}
