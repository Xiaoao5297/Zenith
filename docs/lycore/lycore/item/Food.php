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

namespace lycore\item;

use lycore\entity\Entity;
use lycore\event\entity\EntityEatItemEvent;
use lycore\network\protocol\EntityEventPacket;
use lycore\Player;
use lycore\Server;

abstract class Food extends Item implements FoodSource
{
    public function canBeConsumed() : bool
    {
        return true;
    }

    public function canBeConsumedBy(Entity $entity) : bool
    {
        return $entity instanceof Player && ($entity->getFood() < $entity->getMaxFood()) && $this->canBeConsumed();
    }

    public function getResidue()
    {
        if ($this->getCount() === 1) {
            return Item::get(0);
        } else {
            $new = clone $this;
            $new->count--;
            return $new;
        }
    }

    public function getAdditionalEffects() : array
    {
        return [];
    }

    public function onConsume(Entity $human)
    {
        $pk = new EntityEventPacket();
        $pk->eid = $human->getId();
        $pk->event = EntityEventPacket::USE_ITEM;
        if ($human instanceof Player) {
            $human->dataPacket($pk);
        }
        Server::broadcastPacket($human->getViewers(), $pk);

        Server::getInstance()->getPluginManager()->callEvent($ev = new EntityEatItemEvent($human, $this));

        $human->addSaturation($ev->getSaturationRestore());
        $human->addFood($ev->getFoodRestore());
        foreach ($ev->getAdditionalEffects() as $effect) {
            $human->addEffect($effect);
        }

        $human->getInventory()->setItemInHand($ev->getResidue());
    }
}
