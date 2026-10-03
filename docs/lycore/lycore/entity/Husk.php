<?php

namespace lycore\entity;

use lycore\event\entity\EntityDamageByEntityEvent;

class Husk extends Zombie{
	const NETWORK_ID = 47;
	const HUSK_HUNGER_DURATION = 140;

	public $height = 1.9;

	public function getName() : string{
		return "Husk";
	}

	public function onSuccessfulMeleeAttack(Entity $target, EntityDamageByEntityEvent $source){
		if($source->isCancelled() || !($target instanceof Living)){
			return;
		}

		$effect = Effect::getEffect(Effect::HUNGER);
		if($effect instanceof Effect){
			$target->addEffect($effect->setAmplifier(0)->setDuration(self::HUSK_HUNGER_DURATION));
		}
	}
}
