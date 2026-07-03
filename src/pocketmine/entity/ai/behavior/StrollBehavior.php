<?php

/*
 *
 *    _______                    _
 *   |__   __|                  (_)
 *      | |_   _ _ __ __ _ _ __  _  ___
 *      | | | | | '__/ _` | '_ \| |/ __|
 *      | | |_| | | | (_| | | | | | (__
 *      |_|\__,_|_|  \__,_|_| |_|_|\___|
 *
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * @author TuranicTeam
 * @link https://github.com/TuranicTeam/Turanic
*/

namespace pocketmine\entity\ai\behavior;

use pocketmine\entity\Mob;
use pocketmine\math\Vector3;
use pocketmine\block\Air;

class StrollBehavior extends Behavior{

    public $duration;
    public $timeLeft;
    public $speed;
    public $speedMultiplier;

    public function __construct(Mob $entity, int $duration = 80, float $speed = 0.35, float $speedMultiplier = 1.30){
        parent::__construct($entity);

        $this->duration = $duration;
        $this->speed = $speed;
        $this->speedMultiplier = $speedMultiplier;
        $this->timeLeft = $duration;
    }

    public function getName() : string{
        return "行走";
    }

    public function shouldStart() : bool{
        return mt_rand(0,10) == 0;
    }

    public function canContinue() : bool{
        return $this->timeLeft-- > 0;
    }

    public function onTick(){
        $randomTarget = $this->entity->add(mt_rand(-10, 10), 0, mt_rand(-10, 10));
        $this->entity->getNavigator()->moveTo($randomTarget, $this->speed);
		$this->swimming();
	}

    public function onEnd(){
        $this->entity->getNavigator()->clearPath();
        $this->timeLeft = $this->duration;
        $this->entity->setMotion(new Vector3(0,0,0));
    }
}