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
use lycore\block\Air;
use lycore\block\Block;
use lycore\block\Grass;
use lycore\block\Dirt;
use lycore\network\mcpe\protocol\ActorEventPacket;
use lycore\network\mcpe\protocol\types\ActorEvent;

class EatGrassBehavior extends Behavior {

    const MAX_LOOK_DISTANCE = 6.0;
    const START_CHANCE = 400; // 1/400 的几率开始
    const EATING_DURATION = 30; // 1.5秒 (30 ticks)
    const GRASS_BLOCK_ID = 2;
    const DIRT_BLOCK_ID = 3;
    const EAT_ANIMATION_TIME = 30; // 动画在开始时播放
    
    /** @var Player|null */
    public $player = null;
    /** @var int */
    public $timeLeft = 0;
    /** @var bool */
    private $animationPlayed = false;

    public function getName(): string {
        return "吃草";
    }

    public function shouldStart(): bool {
        if ($this->entity->hasWool()) {
            return false;
        }
        
        if (!$this->isStandingOnGrass()) {
            return false;
        }
        
        return mt_rand(0, self::START_CHANCE - 1) === 0;
    }

    public function canContinue(): bool {
        if (!$this->isStandingOnGrass()) {
            return false;
        }
        
        return $this->timeLeft-- > 0;
    }

    public function onTick() {
        if ($this->timeLeft === self::EATING_DURATION && !$this->animationPlayed) {
            $this->playEatGrassAnimation();
            $this->animationPlayed = true;
        }
    }

    public function onEnd() {
        $this->completeEating();
    }
    
    /**
     * 检查实体是否站在草方块上
     */
    private function isStandingOnGrass(): bool {
        $blockBelow = $this->entity->getLevel()->getBlock(
            $this->entity->floor()->subtract(0, 1, 0)
        );
        
        return $blockBelow->getId() === self::GRASS_BLOCK_ID;
    }
    
    /**
     * 播放吃草动画
     */
    private function playEatGrassAnimation() {
        foreach ($this->entity->getViewers() as $player) {
            $pk = ActorEventPacket::create(
                $this->entity->getId(),
                ActorEvent::EAT_GRASS_ANIMATION,
                0
            );
            $player->dataPacket($pk);
        }
    }
    
    /**
     * 完成吃草行为
     */
    private function completeEating() {
        $this->entity->setHasWool(true);
        
        $blockPos = $this->entity->floor()->subtract(0, 1, 0);
        $block = $this->entity->getLevel()->getBlock($blockPos);
        
        if ($block->getId() === self::GRASS_BLOCK_ID) {
            $this->convertGrassToDirt($blockPos);
        }
    }
    
    /**
     * 将草方块转换为泥土
     */
    private function convertGrassToDirt(\lycore\math\Vector3 $position) {
        $dirtBlock = Block::get(self::DIRT_BLOCK_ID);
        $this->entity->getLevel()->setBlock($position, $dirtBlock, false, false);
    }
    
    /**
     * 重置行为状态
     */
    public function reset() {
        $this->timeLeft = 0;
        $this->animationPlayed = false;
        $this->player = null;
    }
}
