<?php

namespace pocketmine\item;

use pocketmine\entity\Effect;

class EnchantedGoldenApple extends Food{
	public function __construct($meta = 0, $count = 1){
		parent::__construct(self::ENCHANTED_GOLDEN_APPLE, $meta, $count, "Enchanted Golden Apple");
	}

	public function getFoodRestore() : int{
		return 4;
	}

	public function getSaturationRestore() : float{
		return 9.6;
	}

	public function getAdditionalEffects() : array{
		return [
			Effect::getEffect(Effect::REGENERATION)->setDuration(600)->setAmplifier(4),
			Effect::getEffect(Effect::DAMAGE_RESISTANCE)->setDuration(6000),
			Effect::getEffect(Effect::FIRE_RESISTANCE)->setDuration(6000),
			Effect::getEffect(Effect::ABSORPTION)->setDuration(2400)
		];
	}
}

