<?php

/*
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
*/

namespace lycore\item;

use lycore\block\Block;
use lycore\block\Fire;
use lycore\block\Solid;
use lycore\level\Level;
use lycore\math\Vector3;
use lycore\Player;
use lycore\utils\NetherPortalHelper;

class FlintSteel extends Tool{
	public function __construct($meta = 0, $count = 1){
		parent::__construct(self::FLINT_STEEL, $meta, $count, "Flint and Steel");
	}

	public function canBeActivated() : bool{
		return true;
	}

	public function onActivate(Level $level, Player $player, Block $block, Block $target, $face, $fx, $fy, $fz){
		if($target->getId() === Block::OBSIDIAN and $player->getServer()->netherEnabled and $player->getServer()->isNetherPortalWorld($level)){
			if(NetherPortalHelper::createPortal($level, $target)){
				if($player->isSurvival()){
					$this->useOn($block, 2);
					$player->getInventory()->setItemInHand($this);
				}
				return true;
			}
		}

		if($block->getId() === self::AIR and ($target instanceof Solid)){
			$level->setBlock($block, new Fire(), true);

			/** @var Fire $block */
			$block = $level->getBlock($block);
			if($block instanceof Fire and $player->getServer()->netherEnabled and $player->getServer()->isNetherPortalWorld($level) and NetherPortalHelper::createPortal($level, $block)){
				$block = $level->getBlock($block);
			}

			if($block instanceof Fire and ($block->getSide(Vector3::SIDE_DOWN)->isTopFacingSurfaceSolid() or $block->canNeighborBurn())){
				$level->scheduleUpdate($block, $block->getTickRate() + mt_rand(0, 10));
			}

			if($player->isSurvival()){
				$this->useOn($block, 2);
				$player->getInventory()->setItemInHand($this);
			}

			return true;
		}

		return false;
	}
}
