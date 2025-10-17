<?php

namespace pocketmine\item;

use pocketmine\entity\Effect;

class PufferFish extends Food{
	public function __construct($meta = 0, $count = 1){
		parent::__construct(self::PUFFER_FISH, $meta, $count, "Puffer Fish");
	}

	public function getFoodRestore() : int{
		return 2;
	}

	public function getSaturationRestore() : float{
		return 3.2;
	}

	public function getAdditionalEffects() : array{
		return [Effect::getEffect(Effect::POISON)->setDuration(80)];
	}
}

