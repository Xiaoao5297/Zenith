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

abstract class Behavior {

    const DEFAULT_MAX_AIR = 300; // 最大空气值
    const LOW_AIR_THRESHOLD = 175; // 低空气阈值
    const SWIM_UP_FORCE = 0.8; // 正常上浮力度
    const LOW_AIR_SWIM_FORCE = 0.3; // 低空气时上浮力度
    const SWIM_TICK_COOLDOWN = 10; // 游泳冷却时间
    
    const DATA_PROPERTY_AIR = 1; // DATA_AIR 属性ID
    
    /** @var Mob */
    public $entity = null;
    /** @var int */
    public $swimmingTick = 0;
    
    /**
     * 行为基类构造函数
     *
     * @param Mob $entity 应用此行为的实体
     */
    public function __construct(Mob $entity) {
        $this->entity = $entity;
    }

    /**
     * 获取行为名称
     *
     * @return string 行为名称
     */
    public abstract function getName(): string;

    /**
     * 检查行为是否应该开始
     *
     * @return bool 是否开始
     */
    public abstract function shouldStart(): bool;

    /**
     * 行为的主要逻辑，在每次tick时调用
     *
     * @return void
     */
    public abstract function onTick();

    /**
     * 行为结束时的清理逻辑
     *
     * @return void
     */
    public abstract function onEnd();

    /**
     * 检查行为是否应该继续
     *
     * @return bool 是否继续
     */
    public abstract function canContinue(): bool;
    
    /**
     * 处理实体的游泳行为
     * 当实体在水中时，根据其空气值控制其上浮和下潜
     *
     * @return void
     */
    public function swimming() {
        if (!$this->entity->isInsideOfWater()) {
            $this->swimmingTick = 0;
            return;
        }
        
        $airTicks = $this->getEntityAirTicks();
        
        $this->applySwimmingForce($airTicks);
    }
    
    /**
     * 获取实体的空气值
     *
     * @return int 当前空气值
     */
    private function getEntityAirTicks(): int {
        return $this->entity->getDataProperty(self::DATA_PROPERTY_AIR) ?? 0;
    }
    
    /**
     * 根据空气值应用游泳力量
     *
     * @param int $airTicks 当前空气值
     * @return void
     */
    private function applySwimmingForce(int $airTicks) {
        $this->entity->motionY = max($this->entity->motionY, 0.024);
        $this->swimmingTick = 0;
        return;

        if ($airTicks <= self::LOW_AIR_THRESHOLD) {
            // 空气不足，缓慢上浮
            $this->entity->motionY = self::LOW_AIR_SWIM_FORCE;
            $this->swimmingTick = 0;
        } else {
            // 空气充足，正常上浮
            $this->entity->motionY = self::SWIM_UP_FORCE;
            $this->swimmingTick = self::SWIM_TICK_COOLDOWN;
        }
    }
    
    /**
     * 获取游泳冷却时间
     *
     * @return int 剩余的游泳冷却时间
     */
    public function getSwimmingTick(): int {
        return $this->swimmingTick;
    }
    
    /**
     * 设置游泳冷却时间
     *
     * @param int $ticks 冷却时间
     * @return void
     */
    public function setSwimmingTick(int $ticks) {
        $this->swimmingTick = max(0, $ticks);
    }
    
    /**
     * 重置游泳冷却时间
     *
     * @return void
     */
    public function resetSwimmingTick() {
        $this->swimmingTick = 0;
    }
    
    /**
     * 检查实体是否可以上浮
     *
     * @return bool 是否可以上浮
     */
    protected function canSwimUp(): bool {
        return $this->swimmingTick <= 0;
    }
}
