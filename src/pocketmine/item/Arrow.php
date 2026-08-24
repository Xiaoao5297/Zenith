<?php

/*
 *
 *  _____   _____   __   _   _   _____  __    __  _____
 * /  ___| | ____| |  \ | | | | /  ___/ \ \  / / /  ___/
 * | |     | |__   |   \| | | | | |___   \ \/ /  | |___
 * | |  _  |  __|  | |\   | | | \___  \   \  /   \___  \
 * | |_| | | |___  | | \  | | |  ___| |   / /     ___| |
 * \_____/ |_____| |_|  \_| |_| /_____/  /_/     /_____/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * @author iTX Technologies
 * @link https://mcper.cn
 *
 */

namespace pocketmine\item;

class Arrow extends Item {
	public function __construct($meta = 0, $count = 1) {
		parent::__construct(self::ARROW, $meta, $count, "Arrow");
	}

	// 0.14 基础核心无药水箭, 兼容 ProtocolCompatibility 的物品映射调用
	public function isTipped() : bool{
		return false;
	}

	public function toLegacyTippedArrowSurrogate() : Item{
		return Item::get(Item::ARROW, 0, $this->getCount());
	}

}