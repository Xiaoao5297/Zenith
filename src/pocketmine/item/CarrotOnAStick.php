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
 * 从 InCore-II 移植进 Zenith：原文件无许可证头，按 InCore-II 同项目惯例补上 LGPLv3 头。
 * id 沿用 Zenith 已有的 Item::CARROT_ON_A_STICK（398）。
 *
 * 已注册到 Item::$list (创造模式物品列表未添加, 0.14 客户端无此物品)。
 * 骑乘加速/耐久消耗在 Horse::onUpdate / Player 输入路径里，不在本次改动范围。
 */

namespace pocketmine\item;

class CarrotOnAStick extends Item{
	public function __construct($meta = 0, $count = 1){
		parent::__construct(self::CARROT_ON_A_STICK, $meta, $count, "Carrot on a Stick");
	}

	public function getMaxStackSize() : int{
		return 1;
	}

	public function getMaxDurability(){
		return 25;
	}
}
