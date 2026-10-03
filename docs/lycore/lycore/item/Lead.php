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

use lycore\block\Block;
use lycore\entity\Entity;
use lycore\entity\LeashKnot;
use lycore\level\Level;
use lycore\math\AxisAlignedBB;
use lycore\network\protocol\ProtocolCompatibility;
use lycore\Player;

class Lead extends Item{

	public function __construct($meta = 0, $count = 1){
		parent::__construct(self::LEAD, $meta, $count, "Lead");
	}

	public function canBeActivated() : bool{
		return true;
	}

	public function leashEntity(Player $player, Entity $entity) : bool{
		if(!ProtocolCompatibility::isProtocol015((int) $player->getProtocol())){
			return false;
		}
		if($entity instanceof Player or $entity instanceof LeashKnot or !$entity->isAlive() or $entity->isLeashed()){
			return false;
		}
		if($this->getCount() <= 0){
			return false;
		}

		if(!$entity->setLeashedTo($player)){
			return false;
		}

		$this->consumeLead($player);
		return true;
	}

	public function onActivate(Level $level, Player $player, Block $block, Block $target, $face, $fx, $fy, $fz){
		if(!ProtocolCompatibility::isProtocol015((int) $player->getProtocol()) or !LeashKnot::isSupportedFence($target)){
			return false;
		}

		$knot = null;
		$attached = false;
		$range = 7;
		$bb = new AxisAlignedBB(
			$target->getX() - $range,
			$target->getY() - $range,
			$target->getZ() - $range,
			$target->getX() + $range + 1,
			$target->getY() + $range + 1,
			$target->getZ() + $range + 1
		);

		foreach($level->getNearbyEntities($bb, $player) as $entity){
			if($entity instanceof Entity and $entity->isLeashedTo($player)){
				if($knot === null){
					$knot = LeashKnot::getOrCreate($level, $target);
					if(!($knot instanceof LeashKnot)){
						return false;
					}
				}
				if($entity->setLeashedTo($knot)){
					$attached = true;
				}
			}
		}

		return $attached;
	}

	private function consumeLead(Player $player){
		if($player->isCreative()){
			return;
		}

		$item = $player->getInventory()->getItemInHand();
		if($item->getId() !== self::LEAD){
			$item = $this;
		}
		$item->setCount($item->getCount() - 1);
		$player->getInventory()->setItemInHand($item->getCount() > 0 ? $item : Item::get(Item::AIR, 0, 0));
	}
}
