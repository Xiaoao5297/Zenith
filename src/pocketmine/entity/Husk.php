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

/*
 * 移植自 lycore\entity\Husk，命名空间改为 pocketmine\entity。
 */

namespace pocketmine\entity;

use pocketmine\event\entity\EntityDamageByEntityEvent;

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
