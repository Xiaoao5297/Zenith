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

namespace lycore\block;

use lycore\entity\Entity;
use lycore\entity\Minecart;
use lycore\entity\MinecartChest;
use lycore\entity\MinecartHopper;
use lycore\entity\MinecartTNT;
use lycore\level\Level;
use lycore\math\AxisAlignedBB;
use lycore\math\Vector3;

class DetectorRail extends PoweredRail{
	const RECHECK_DELAY = 20;

    protected $id = self::DETECTOR_RAIL;

    public function __construct($meta = 0){
        $this->meta = $meta;
    }

    public function getName() : string {
        return "Detector Rail";
    }

	public function isPowerSource(){
		return true;
	}

	public function getStrongPower($side){
		return ($this->isActive() and $side === Vector3::SIDE_UP) ? 15 : 0;
	}

	public function getWeakPower($side){
		return $this->isActive() ? 15 : 0;
	}

	public function hasEntityCollision(){
		return true;
	}

	public function onEntityCollide(Entity $entity){
		$this->updateState();
	}

	public function onUpdate($type){
		if($type === Level::BLOCK_UPDATE_SCHEDULED){
			$this->updateState();
			return $type;
		}
		return Rail::onUpdate($type);
	}

	protected function updateState(){
		$wasPowered = $this->isActive();
		$isPowered = false;
		$bb = new AxisAlignedBB(
			$this->x + 0.125,
			$this->y,
			$this->z + 0.125,
			$this->x + 0.875,
			$this->y + 0.75,
			$this->z + 0.875
		);

		foreach($this->getLevel()->getCollidingEntities($bb) as $entity){
			if($entity instanceof Minecart or $entity instanceof MinecartChest or $entity instanceof MinecartHopper or $entity instanceof MinecartTNT){
				$isPowered = true;
				break;
			}
		}

		if($isPowered !== $wasPowered){
			$this->setActive($isPowered);
			$this->getLevel()->scheduleUpdate($this->getSide(Vector3::SIDE_DOWN), 0);
			$this->getLevel()->updateAroundRedstone($this);
		}

		if($isPowered){
			$this->getLevel()->scheduleUpdate($this, self::RECHECK_DELAY);
		}
	}
}
