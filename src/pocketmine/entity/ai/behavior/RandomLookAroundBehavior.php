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
 *
*/

namespace pocketmine\entity\ai\behavior;

class RandomLookAroundBehavior extends Behavior{

    public $duration = 0;
    public $startYaw = 0;
    public $targetYaw = 0;
    public $turnsLeft = 0;

    public function getPriority(): int{
        return 8;
    }

    public function getName() : string{
        return "RandomLookAround";
    }

    public function shouldStart() : bool{
        if(rand(0,2) != 0) return false;

        $this->duration = 20 + rand(0,20);
        $this->startYaw = $this->entity->yaw;
        $this->targetYaw = $this->startYaw + rand(-180, 180);
        // 目标角度限制在 [-180, 180]，避免跨 0 抖动
        while($this->targetYaw > 180) $this->targetYaw -= 360;
        while($this->targetYaw < -180) $this->targetYaw += 360;
        $this->turnsLeft = rand(5, 10);

        return true;
    }

    public function canContinue() : bool{
        return $this->duration-- > 0 and $this->turnsLeft > 0;
    }

    public function onTick(){
        $this->swimming();
        // 按剩余转向次数线性逼近目标角度，不会跨 0 反向
        if($this->turnsLeft > 0){
            $diff = $this->targetYaw - $this->entity->yaw;
            while($diff > 180) $diff -= 360;
            while($diff < -180) $diff += 360;
            $step = $diff / $this->turnsLeft;
            $this->entity->yaw += $step;
            $this->turnsLeft--;
        }
        $this->entity->level->addEntityMovement($this->entity->chunk->getX(), $this->entity->chunk->getZ(), $this->entity->getID(), $this->entity->x, $this->entity->y + $this->entity->getEyeHeight(), $this->entity->z, $this->entity->yaw, $this->entity->pitch, $this->entity->yaw);
    }

    public function onEnd(){
    }
}
