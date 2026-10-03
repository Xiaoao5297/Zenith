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

use lycore\entity\Mob;
use lycore\math\Vector3;

class StrollBehavior extends Behavior {

    const DEFAULT_DURATION = 80;
    const DEFAULT_SPEED = 0.25;
    const DEFAULT_SPEED_MULTIPLIER = 0.75;
    const BASE_SPEED_FACTOR = 0.7;
    const WATER_SPEED_MULTIPLIER = 1.3;
    const LAND_SPEED_MULTIPLIER = 1.4;
    const JUMP_VELOCITY = 0.42;
    
    /** @var int */
    public $duration = 0;
    /** @var int */
    public $timeLeft = 0;
    /** @var float */
    public $speed = 0;
    /** @var float */
    public $speedMultiplier = 0;
    
    /** @var int */
    private $startChance = 10; // 1/10 的几率开始

    public function __construct(Mob $entity, int $duration = self::DEFAULT_DURATION, 
                              float $speed = 0.25, 
                              float $speedMultiplier = 0.75) {
        parent::__construct($entity);
        $this->duration = $duration;
        $this->speed = $speed;
        $this->speedMultiplier = $speedMultiplier;
        $this->reset();
    }

    public function getName(): string {
        return "行走";
    }

    public function shouldStart(): bool {
        return mt_rand(0, $this->startChance) === 0;
    }

    public function canContinue(): bool {
        return $this->timeLeft-- > 0;
    }

    public function onTick() {
        if ($this->isFalling()) {
            $this->timeLeft = $this->duration;
            return;
        }
        
        $direction = $this->entity->getDirectionVector();
        $direction->y = 0;
        
        $speedFactor = $this->calculateSpeedFactor();
        $targetPos = $this->getTargetPosition($direction, $speedFactor);
        
        if ($this->isColliding($targetPos)) {
            $this->handleCollision($targetPos);
        } else {
            $this->moveToDirection($direction, $speedFactor);
        }
        
        $this->handleSwimming();
    }

    public function onEnd() {
        $this->reset();
        return;
    }
    
    /**
     * 重置行为状态
     */
    private function reset() {
        $this->timeLeft = $this->duration;
    }
    
    /**
     * 计算速度因子
     */
    private function calculateSpeedFactor(): float {
        $baseFactor = $this->speed * $this->speedMultiplier * self::BASE_SPEED_FACTOR;
        $environmentMultiplier = $this->entity->isInsideOfWater() 
            ? self::WATER_SPEED_MULTIPLIER 
            : self::LAND_SPEED_MULTIPLIER;
        
        return $baseFactor * $environmentMultiplier;
    }
    
    /**
     * 获取目标位置
     */
    private function getTargetPosition(Vector3 $direction, float $speedFactor): Vector3 {
        $coordinates = new Vector3($this->entity->x, $this->entity->y, $this->entity->z);
        return $coordinates->add($direction->multiply($speedFactor + 0.5));
    }
    
    /**
     * 检查是否正在下落
     */
    private function isFalling(): bool {
        if ($this->entity->getMotion()->y >= 0) {
            return false;
        }
        
        $blockBelow = $this->entity->getLevel()->getBlock(
            $this->entity->floor()->subtract(0, 1, 0)
        );
        
        return $blockBelow->canPassThrough();
    }
    
    /**
     * 检查目标位置是否碰撞
     */
    private function isColliding(Vector3 $targetPos): bool {
        $level = $this->entity->getLevel();
        $block = $level->getBlock($targetPos);
        
        // 检查主要碰撞块
        if (!$block->canPassThrough()) {
            return true;
        }
        
        // 如果实体高度 >= 1，检查上方一个方块
        if ($this->entity->height >= 1) {
            $blockAbove = $level->getBlock($targetPos->add(0, 1, 0));
            if (!$blockAbove->canPassThrough()) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * 处理碰撞
     */
    private function handleCollision(Vector3 $targetPos) {
        $level = $this->entity->getLevel();
        $blockAbove = $level->getBlock($targetPos->add(0, 1, 0));
        $blockTwoAbove = $level->getBlock($targetPos->add(0, 2, 0));
        
        // 尝试跳跃
        if ($blockAbove->canPassThrough() &&
            !($this->entity->height > 1 && !$blockTwoAbove->canPassThrough()) &&
            mt_rand(0, 5) !== 0) {
            $this->entity->motionY = self::JUMP_VELOCITY;
        } else {
            // 无法跳跃则转向
            $this->entity->yaw += 180;
        }
    }
    
    /**
     * 向指定方向移动
     */
    private function moveToDirection(Vector3 $direction, float $speedFactor) {
        $motion = $direction->multiply($speedFactor);
        $currentMotion = $this->entity->getMotion();
        
        // 如果当前速度小于目标速度，则加速
        if ($currentMotion->lengthSquared() < $motion->lengthSquared()) {
            $newMotion = clone $currentMotion;
            $newMotion->x += ($motion->x - $currentMotion->x);
            $newMotion->z += ($motion->z - $currentMotion->z);
            $this->entity->setMotion($newMotion);
        } else {
            $this->entity->setMotion($motion);
        }
    }
    
    /**
     * 处理游泳逻辑
     */
    private function handleSwimming() {
        // 如果有游泳方法则调用
        if (method_exists($this->entity, 'swimming')) {
            $this->entity->swimming();
        }
    }
}
