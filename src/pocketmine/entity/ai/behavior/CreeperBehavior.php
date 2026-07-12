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
use pocketmine\Player;

class CreeperBehavior extends Behavior{

    public $lookDistance = 5.0;
    public $explodeRadius = 2.0;
    public $speed = 0.19;
    public $enemy = null;

    public function getPriority(): int{
        return 1;
    }

    public function getName() : string{
        return "CreeperExplode";
    }

    public function shouldStart() : bool{
        $nearest = null;
        $minDistance = $this->lookDistance;
        foreach($this->entity->level->getPlayers() as $p){
            $dist = $this->entity->distance($p);
            if($p->isSurvival() and $dist < $minDistance){
                $nearest = $p;
                $minDistance = $dist;
            }
        }
        $this->enemy = $nearest;
        return $nearest !== null;
    }

    public function canContinue() : bool{
        if($this->enemy === null or !$this->enemy->isAlive()){
            return false;
        }
        if($this->enemy instanceof Player and !$this->enemy->isConnected()){
            return false;
        }
        return $this->entity->distance($this->enemy) < $this->lookDistance;
    }

    public function onTick(){
        $distance = $this->entity->distance($this->enemy);
        $this->lookAt($this->enemy, false);

        if($distance >= 1.5){
            $this->entity->getNavigator()->moveTo($this->enemy, $this->speed);
        }

        if($distance <= $this->explodeRadius){
            $this->entity->setSwelled(true);
        }

        $this->swimming();
    }

    public function onEnd(){
        $this->enemy = null;
        $this->entity->setSwelled(false);
        $this->entity->getNavigator()->clearPath();
    }
}
