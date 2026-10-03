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
use lycore\Player;
use lycore\item\Item;

class FindFoodBehavior extends Behavior {

    const DEFAULT_SPEED = 0.35;
    const DEFAULT_SPEED_MULTIPLIER = 0.75;
    const BASE_SPEED_FACTOR = 0.7;
    const WATER_SPEED_MULTIPLIER = 0.3;
    const LAND_SPEED_MULTIPLIER = 0.4;
    const LOOK_AHEAD_DISTANCE = 0.5;
    const JUMP_VELOCITY = 0.42;
    const MAX_LOOK_DISTANCE = 6.0;
    const MIN_APPROACH_DISTANCE = 0.5;
    
    /** @var float */
    public $speed = null;
    /** @var float */
    public $speedMultiplier = null;
    /** @var int */
    public $foodID = null;
    /** @var Player|null */
    public $targetPlayer = null;
    /** @var float */
    public $lookDistance = self::MAX_LOOK_DISTANCE;

    public function __construct(Mob $entity, int $foodID, 
                               float $speed = self::DEFAULT_SPEED, 
                               float $speedMultiplier = self::DEFAULT_SPEED_MULTIPLIER) {
        parent::__construct($entity);
        $this->speed = $speed;
        $this->speedMultiplier = $speedMultiplier;
        $this->foodID = $foodID;
    }

    public function getName(): string {
        return "觅食";
    }

    public function shouldStart(): bool {
        $closestPlayer = $this->findClosestPlayerWithFood();
        if ($closestPlayer !== null) {
            $this->targetPlayer = $closestPlayer;
            return true;
        }
        return false;
    }

    public function canContinue(): bool {
        if ($this->targetPlayer === null || 
            !$this->targetPlayer->isConnected() || 
            !$this->targetPlayer->isAlive()) {
            return false;
        }
        
        $heldItem = $this->targetPlayer->getItemInHand();
        return $heldItem->getId() === $this->foodID;
    }

    public function onTick() {
        if ($this->targetPlayer === null) {
            return;
        }
        
        $distance = $this->entity->distance($this->targetPlayer);
        
        if ($distance < self::MIN_APPROACH_DISTANCE) {
            $this->entity->setPm1eFollowTarget(null);
            $this->entity->setPm1eStayTime(10);
            return;
        }
        
        $this->entity->setPm1eFollowTarget($this->targetPlayer);
        $this->entity->setPm1eMoveMultiplier($this->speedMultiplier);
        $this->entity->setPm1eStayTime(0);
        $this->handleSwimming();
    }

    public function onEnd() {
        $this->entity->setPm1eFollowTarget(null);
        $this->entity->setPm1eMoveMultiplier(1.0);
        $this->targetPlayer = null;
    }
    
    /**
     * 寻找持有食物的最近玩家
     */
    private function findClosestPlayerWithFood() {
        $closestPlayer = null;
        $closestDistance = 0xffffff;
        
        foreach ($this->entity->getLevel()->getPlayers() as $player) {
            if (!$player->isConnected() || !$player->isAlive()) {
                continue;
            }
            
            $heldItem = $player->getItemInHand();
            if ($heldItem->getId() !== $this->foodID) {
                continue;
            }
            
            $distance = $this->entity->distance($player);
            if ($distance < $this->lookDistance && $distance < $closestDistance) {
                $closestPlayer = $player;
                $closestDistance = $distance;
            }
        }
        
        return $closestPlayer;
    }
    
    /**
     * 处理游泳逻辑
     */
    private function handleSwimming() {
        if (method_exists($this->entity, 'swimming')) {
            $this->entity->swimming();
        }
    }
    
    /**
     * 获取食物ID
     */
    public function getFoodID(): int {
        return $this->foodID;
    }
    
    /**
     * 设置食物ID
     */
    public function setFoodID(int $foodID) {
        $this->foodID = $foodID;
    }
    
    /**
     * 获取视野距离
     */
    public function getLookDistance(): float {
        return $this->lookDistance;
    }
    
    /**
     * 设置视野距离
     */
    public function setLookDistance(float $lookDistance) {
        $this->lookDistance = max(0, $lookDistance);
    }
}
