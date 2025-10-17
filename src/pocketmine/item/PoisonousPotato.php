<?php

/*
 Sunch233
*/

namespace pocketmine\item;

use pocketmine\entity\Effect;

class PoisonousPotato extends Food{
	public function __construct($meta = 0, $count = 1){
		parent::__construct(self::POISONOUS_POTATO, $meta, $count, "POISONOUS POTATO");
	}

	public function getFoodRestore() : int{
		return 1;
	}

	public function getSaturationRestore() : float{
		return 3.2;
	}

	public function getAdditionalEffects() : array{
		return [Effect::getEffect(Effect::POISON)->setDuration(80)];
	}
}
