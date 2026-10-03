<?php

namespace lycore\entity;

use lycore\item\Arrow as ItemArrow;
use lycore\item\Item as ItemItem;
use lycore\item\Potion;

class Stray extends Skeleton{
	const NETWORK_ID = 46;

	public function getName() : string{
		return "Stray";
	}

	public function getProjectileArrowItem(){
		return ItemItem::get(ItemItem::ARROW, ItemArrow::getArrowMetaFromPotionMeta(Potion::SLOWNESS), 1);
	}
}
