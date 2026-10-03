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

namespace lycore\entity\behavior;

class RandomLookaroundBehavior extends Behavior {

    const START_CHANCE = 3; // 1/3 的几率开始
    const MIN_DURATION = 20;
    const MAX_DURATION = 40;
    const MIN_ROTATION = -180;
    const MAX_ROTATION = 180;
    const ROTATION_SPEED = 10;
    
    /** @var int */
    public $duration = 0;
    /** @var int */
    public $rotation = 0;
    /** @var int */
    private $rotationDirection = 0; // 1 向右，-1 向左

    public function getName(): string {
        return "随机看";
    }

    public function shouldStart(): bool {
        if (mt_rand(0, self::START_CHANCE - 1) !== 0) {
            return false;
        }

        $this->duration = mt_rand(self::MIN_DURATION, self::MAX_DURATION);
        $this->rotation = mt_rand(self::MIN_ROTATION, self::MAX_ROTATION);
        $this->rotationDirection = $this->getSign($this->rotation);
        
        return true;
    }

    public function canContinue(): bool {
        return $this->duration-- > 0 && abs($this->rotation) > 0;
    }

    public function onTick() {
        $this->handleSwimming();
        $this->updateRotation();
        $this->broadcastMovement();
    }

    public function onEnd() {
        // 行为结束，可以在这里添加清理逻辑
    }
    
    /**
     * 更新实体的旋转
     */
    private function updateRotation() {
        $rotationStep = $this->calculateRotationStep();
        $this->entity->yaw += $rotationStep;
        $this->rotation -= $rotationStep;
    }
    
    /**
     * 计算本次tick的旋转角度
     */
    private function calculateRotationStep(): int {
        $absRotation = abs($this->rotation);
        
        // 如果剩余旋转角度小于旋转速度，则只旋转剩余的角度
        if ($absRotation < self::ROTATION_SPEED) {
            return $this->rotation;
        }
        
        return $this->rotationDirection * self::ROTATION_SPEED;
    }
    
    /**
     * 获取数值的符号
     */
    private function getSign(int $value): int {
        if ($value > 0) {
            return 1;
        }
        
        if ($value < 0) {
            return -1;
        }
        
        return 0;
    }
    
    /**
     * 广播实体的移动（旋转）更新
     */
    private function broadcastMovement() {
        $this->entity->getLevel()->addEntityMovement(
            $this->entity->chunk->getX(),
            $this->entity->chunk->getZ(),
            $this->entity->getId(),
            $this->entity->x,
            $this->entity->y + $this->entity->getEyeHeight(),
            $this->entity->z,
            $this->entity->yaw,
            $this->entity->pitch,
            $this->entity->yaw
        );
    }
    
    /**
     * 处理游泳逻辑
     */
    private function handleSwimming() {
        if (method_exists($this->entity, 'swimming')) {
            $this->entity->swimming();
        }
    }
}
