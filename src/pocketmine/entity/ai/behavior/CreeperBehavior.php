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
    public $speed = 0.25;
    public $speedMultiplier = 0.75;
    public $enemy = null;

    public function getName() : string{
        return "苦力怕爆炸行为";
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
        $this->AimPlayer($this->enemy);

        if($distance >= 1.5){
            $speedFactor = (float) ($this->speed * $this->speedMultiplier * 0.7 * ($this->entity->isInsideOfWater() ? 0.3 : 0.4));
            $level = $this->entity->getLevel();
            $coordinates = $this->entity->getPosition();
            $direction = $this->entity->getDirectionVector();
            $direction->y = 0;

            $blockDown = $level->getBlock($coordinates->add(0, -1, 0));
            if($this->entity->getMotion()->y < 0 and $blockDown instanceof Air){
                return;
            }

            $coord = $coordinates->add($direction->multiply($speedFactor))->add($direction->multiply(0.5));
            $block = $level->getBlock($coord);
            $blockUp = $level->getBlock($coord->add(0, 1, 0));
            $blockUpUp = $level->getBlock($coord->add(0, 2, 0));

            $colliding = $block->isSolid() or ($this->entity->height >= 1 and $blockUp->isSolid());
            if(!$colliding){
                $motion = $direction->multiply($speedFactor);
                $pm = $this->entity->getMotion();
                $pm->y = 0;
                if($pm->length() < $motion->length()){
                    $this->entity->setMotion($pm->add($motion->x - $pm->x, 0, $motion->z - $pm->z));
                }else{
                    $this->entity->setMotion($motion);
                }
            }else{
                if(!$blockUp->isSolid() and !($this->entity->height > 1 and $blockUpUp->isSolid())){
                    $this->entity->motionY = 0.42;
                }
            }
        }

        if($distance <= $this->explodeRadius){
            $this->entity->setSwelled(true);
        }

        $this->swimming();
    }

    public function AimPlayer($player){
        $x = $player->x - $this->entity->x;
        $z = $player->z - $this->entity->z;

        $a = $player->x + 0.5;
        $c = $player->z + 0.5;
        $yaw = -atan2($a - ($this->entity->x + 0.5), $c - ($this->entity->z + 0.5)) * (180 / M_PI);
        $this->entity->yaw = $yaw;
    }

    public function onEnd(){
        $this->enemy = null;
        $this->entity->setSwelled(false);
        $this->entity->setMotion(new Vector3(0, 0, 0));
    }
}
