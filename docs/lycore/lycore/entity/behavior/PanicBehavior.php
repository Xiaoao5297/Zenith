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

class PanicBehavior extends StrollBehavior {

    const DEFAULT_PANIC_DURATION = 60; // 3秒 (20 ticks/秒)
    const DEFAULT_PANIC_SPEED = 0.27;
    const DEFAULT_SPEED_MULTIPLIER = 0.75;

    public function __construct(Mob $entity, float $speed = self::DEFAULT_PANIC_SPEED, 
                               float $speedMultiplier = self::DEFAULT_SPEED_MULTIPLIER) {
        parent::__construct($entity, self::DEFAULT_PANIC_DURATION, $speed, $speedMultiplier);
    }

    public function getName(): string {
        return "受伤之后行走";
    }

    public function shouldStart(): bool {
        return $this->entity->getLastDamageCause() !== null;
    }
    
    public function onEnd() {
        parent::onEnd();
        $this->resetDamageCause();
    }
    
    /**
     * 重置伤害原因
     */
    private function resetDamageCause() {
        if (method_exists($this->entity, 'resetLastDamageCause')) {
            $this->entity->resetLastDamageCause();
        }
    }
}
