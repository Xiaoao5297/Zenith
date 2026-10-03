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

namespace lycore\item;

use lycore\item\enchantment\Enchantment;

class Arrow extends Item {
	public function __construct($meta = 0, $count = 1) {
		parent::__construct(self::ARROW, $meta, $count, $this->getNameByMeta((int) $meta));
	}

	public static function getArrowMetaFromPotionMeta(int $potionMeta) : int{
		return $potionMeta + 1;
	}

	public static function getPotionMetaFromArrowMeta(int $arrowMeta) : int{
		return $arrowMeta - 1;
	}

	public function isTipped() : bool{
		return $this->getPotionMeta() !== null;
	}

	public function getPotionMeta(){
		$potionMeta = self::getPotionMetaFromArrowMeta((int) $this->meta);
		return $this->meta > 0 && isset(Potion::$POTION_LIST[$potionMeta]) ? $potionMeta : null;
	}

	public function getNameByMeta(int $meta) : string{
		if($meta <= 0){
			return "Arrow";
		}

		$potionMeta = self::getPotionMetaFromArrowMeta($meta);
		if(!isset(Potion::$POTION_LIST[$potionMeta])){
			return "Arrow";
		}

		switch($potionMeta){
			case Potion::WATER_BOTTLE:
				return "Arrow of Splashing";
			case Potion::MUNDANE:
			case Potion::MUNDANE_EXTENDED:
			case Potion::THICK:
			case Potion::AWKWARD:
				return "Tipped Arrow";
		}

		return str_replace("Potion", "Arrow", (new Potion($potionMeta))->getName());
	}

	private static function getChineseLegacyTippedArrowNameByPotionMeta(int $potionMeta) : string{
		switch($potionMeta){
			case Potion::WATER_BOTTLE:
				return "喷溅箭";
			case Potion::MUNDANE:
			case Potion::MUNDANE_EXTENDED:
			case Potion::THICK:
			case Potion::AWKWARD:
				return "药箭";
			case Potion::NIGHT_VISION:
			case Potion::NIGHT_VISION_T:
				return "夜视箭";
			case Potion::INVISIBILITY:
			case Potion::INVISIBILITY_T:
				return "隐身箭";
			case Potion::LEAPING:
			case Potion::LEAPING_T:
				return "跳跃箭";
			case Potion::LEAPING_TWO:
				return "跳跃II箭";
			case Potion::FIRE_RESISTANCE:
			case Potion::FIRE_RESISTANCE_T:
				return "抗火箭";
			case Potion::SPEED:
			case Potion::SPEED_T:
				return "迅捷箭";
			case Potion::SPEED_TWO:
				return "迅捷II箭";
			case Potion::SLOWNESS:
			case Potion::SLOWNESS_T:
				return "迟缓箭";
			case Potion::WATER_BREATHING:
			case Potion::WATER_BREATHING_T:
				return "水下呼吸箭";
			case Potion::HEALING:
				return "治疗箭";
			case Potion::HEALING_TWO:
				return "治疗II箭";
			case Potion::HARMING:
				return "伤害箭";
			case Potion::HARMING_TWO:
				return "伤害II箭";
			case Potion::POISON:
			case Potion::POISON_T:
				return "剧毒箭";
			case Potion::POISON_TWO:
				return "剧毒II箭";
			case Potion::REGENERATION:
			case Potion::REGENERATION_T:
				return "再生箭";
			case Potion::REGENERATION_TWO:
				return "再生II箭";
			case Potion::STRENGTH:
			case Potion::STRENGTH_T:
				return "力量箭";
			case Potion::STRENGTH_TWO:
				return "力量II箭";
			case Potion::WEAKNESS:
			case Potion::WEAKNESS_T:
				return "虚弱箭";
		}

		return "药箭";
	}

	public function toLegacyTippedArrowSurrogate() : Item{
		if(!$this->isTipped()){
			return clone $this;
		}

		$item = Item::get(Item::ARROW, 0, $this->getCount());
		$name = $this->getLegacyTippedArrowSurrogateName();
		if($this->getCount() > 1){
			$name .= " x" . $this->getCount();
		}
		$item->setCustomName($name);
		$item->addEnchantment(Enchantment::getEnchantment(Enchantment::TYPE_MINING_FORTUNE)->setLevel((int) $this->getDamage()));

		return $item;
	}

	public function getLegacyTippedArrowSurrogateName() : string{
		$potionMeta = $this->getPotionMeta();
		if($potionMeta !== null){
			return self::getChineseLegacyTippedArrowNameByPotionMeta((int) $potionMeta);
		}

		$name = $this->getNameByMeta((int) $this->meta);
		if(substr($name, 0, 9) === "Arrow of "){
			return substr($name, 9) . "箭";
		}
		if(substr($name, -6) === " Arrow"){
			return substr($name, 0, -6) . "箭";
		}

		return $name . "箭";
	}

	public static function fromLegacyTippedArrowSurrogate(Item $item){
		$meta = self::getLegacyTippedArrowSurrogateMeta($item);
		if($meta <= 0){
			return null;
		}

		return Item::get(Item::ARROW, $meta, $item->getCount());
	}

	public static function getLegacyTippedArrowSurrogateMeta(Item $item) : int{
		if($item->getId() !== Item::ARROW or (int) $item->getDamage() !== 0 or !$item->hasEnchantments()){
			return 0;
		}

		foreach($item->getEnchantments() as $enchantment){
			if($enchantment->getId() !== Enchantment::TYPE_MINING_FORTUNE){
				continue;
			}

			$level = (int) $enchantment->getLevel();
			$potionMeta = self::getPotionMetaFromArrowMeta($level);
			return $level > 0 && isset(Potion::$POTION_LIST[$potionMeta]) ? $level : 0;
		}

		return 0;
	}

}
