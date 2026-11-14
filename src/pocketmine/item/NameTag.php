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
 * 从 InCore-II 移植进 Zenith：物品 id 常量暂留在本类（Item::NAME_TAG 尚未声明）。
 *
 * 已注册到 Item::$list (创造模式物品列表未添加, 0.14 客户端无此物品)。
 * TODO(B2): 改名交互（玩家手持名牌右键生物 -> 文本框）属于 Player 侧，
 * 由 Player 的所有者接线；实体侧只需 setNameTag()。
 */

namespace pocketmine\item;

class NameTag extends Item{

	/** 名牌物品 id（原版 0.15+）。Item::NAME_TAG 声明前先放在这里。 */
	const NAME_TAG = 421;

	public function __construct($meta = 0, $count = 1){
		parent::__construct(self::NAME_TAG, $meta, $count, "Name Tag");
	}
}
