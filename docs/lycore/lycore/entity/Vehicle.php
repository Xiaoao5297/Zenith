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

namespace lycore\entity;

use lycore\block\Block;
use lycore\block\ActivatorRail;
use lycore\block\PoweredRail;
use lycore\block\Rail;
use lycore\math\Vector3;

abstract class Vehicle extends Entity implements Rideable{

	public function canBeRidden(){
		return true;
	}

	public function applyRailEffects(Block $rail){
		if($rail->getId() === Block::POWERED_RAIL and $rail instanceof PoweredRail){
			if($rail->isActive()){
				$this->accelerateOnPoweredRail($rail);
			}else{
				$this->brakeOnPoweredRail();
			}
		}elseif($rail->getId() === Block::ACTIVATOR_RAIL and $rail instanceof ActivatorRail){
			$this->activateByRail($rail, $rail->isActive());
		}
	}

	protected function activateByRail(ActivatorRail $rail, $active){
	}

	protected function brakeOnPoweredRail(){
		$speed = sqrt($this->motionX * $this->motionX + $this->motionZ * $this->motionZ);
		if($speed < 0.03){
			$this->motionX = 0;
			$this->motionY = 0;
			$this->motionZ = 0;
		}else{
			$this->motionX *= 0.5;
			$this->motionY = 0;
			$this->motionZ *= 0.5;
		}
		if(property_exists($this, "moveSpeed")){
			$this->moveSpeed = max(0.1, $this->moveSpeed * 0.5);
		}
	}

	protected function accelerateOnPoweredRail(PoweredRail $rail){
		$speed = sqrt($this->motionX * $this->motionX + $this->motionZ * $this->motionZ);
		if(property_exists($this, "moveSpeed")){
			$this->moveSpeed = min(1.2, $this->moveSpeed + 0.1);
		}
		if($speed > 0.01){
			$this->motionX += ($this->motionX / $speed) * 0.06;
			$this->motionZ += ($this->motionZ / $speed) * 0.06;
			return;
		}

		switch($rail->getRealMeta()){
			case Rail::STRAIGHT_NORTH_SOUTH:
				if($this->getLevel()->getBlock(new Vector3($rail->x - 1, $rail->y, $rail->z))->isNormalBlock()){
					$this->motionX = 0.02;
				}elseif($this->getLevel()->getBlock(new Vector3($rail->x + 1, $rail->y, $rail->z))->isNormalBlock()){
					$this->motionX = -0.02;
				}
				break;
			case Rail::STRAIGHT_EAST_WEST:
				if($this->getLevel()->getBlock(new Vector3($rail->x, $rail->y, $rail->z - 1))->isNormalBlock()){
					$this->motionZ = 0.02;
				}elseif($this->getLevel()->getBlock(new Vector3($rail->x, $rail->y, $rail->z + 1))->isNormalBlock()){
					$this->motionZ = -0.02;
				}
				break;
		}
	}
}
