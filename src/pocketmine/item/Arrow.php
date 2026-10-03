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
		parent::__construct(self::ARROW, $meta, $count, $this->getNameByMeta($meta));
	}

	public static function getArrowMetaFromPotionMeta(int $potionMeta) : int{
		return $potionMeta + 1;
	}

	public static function getPotionMetaFromArrowMeta(int $arrowMeta) : int{
		return $arrowMeta - 1;
	}

	public function getPotionMeta(){
		$potionMeta = self::getPotionMetaFromArrowMeta((int) $this->meta);
		return $this->meta > 0 && isset(Potion::$POTION_LIST[$potionMeta]) ? $potionMeta : null;
	}

	public function isTipped() : bool{
		return $this->getPotionMeta() !== null;
	}

	public function getNameByMeta(int $meta) : string{
		if($meta <= 0){
			return "Arrow";
		}

		$potionMeta = self::getPotionMetaFromArrowMeta($meta);
		if(!isset(Potion::$POTION_LIST[$potionMeta])){
			return "Arrow";
		}

		return str_replace("Potion", "Arrow", (new Potion($potionMeta))->getName());
	}

	// 0.14 兼容：ProtocolCompatibility 的物品映射仍会调用这两个方法
	public function toLegacyTippedArrowSurrogate() : Item{
		return Item::get(Item::ARROW, 0, $this->getCount());
	}

	public static function fromLegacyTippedArrowSurrogate(Item $item){
		return null;
	}
}
