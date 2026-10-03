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

use lycore\item\Item as ItemItem;
use lycore\level\Level;

abstract class Monster extends Mob{
	const DAYLIGHT_BURN_SKY_LIGHT = 15;

	public function onUpdate($tick){
		$hasUpdate = parent::onUpdate($tick);
		if(!$this->closed and $this->isAlive()){
			$this->applyDaylightBurning();
		}

		return $hasUpdate;
	}

	protected function shouldBurnInDaylight() : bool{
		return (($this instanceof Zombie || $this instanceof ZombieVillager) && !($this instanceof Husk)) || $this instanceof Skeleton;
	}

	protected function isInBrightDaylight(int $minimumLight) : bool{
		$level = $this->getLevel();
		if(!($level instanceof Level) or $level->getDimension() !== Level::DIMENSION_NORMAL){
			return false;
		}

		$time = $level->getTime() % Level::TIME_FULL;
		if(!(($time >= Level::TIME_DAY and $time < Level::TIME_NIGHT) or $time >= Level::TIME_SUNRISE)){
			return false;
		}

		$x = (int) floor($this->x);
		$y = (int) floor($this->y + ($this->height / 2));
		$z = (int) floor($this->z);
		if($y < 0 or $y >= Level::Y_MAX){
			return false;
		}

		return $level->getFullLightAt($x, $y, $z) >= $minimumLight;
	}

	protected function hasSunBlockingHelmet() : bool{
		if(!method_exists($this, "getArmorContents")){
			return false;
		}

		$armor = $this->getArmorContents();
		if(!isset($armor[0]) or !($armor[0] instanceof ItemItem)){
			return false;
		}

		return $armor[0]->getId() !== ItemItem::AIR;
	}

	protected function applyDaylightBurning(){
		if(!$this->shouldBurnInDaylight() or $this->hasSunBlockingHelmet()){
			return;
		}

		$level = $this->getLevel();
		if(!($level instanceof Level) or $level->getDimension() !== Level::DIMENSION_NORMAL){
			return;
		}

		if($this->hasDaylightBlockingWeather($level)){
			return;
		}

		$time = $level->getTime() % Level::TIME_FULL;
		if(!(($time >= Level::TIME_DAY and $time < Level::TIME_NIGHT) or $time >= Level::TIME_SUNRISE)){
			return;
		}

		$x = (int) floor($this->x);
		$y = (int) floor($this->y + $this->height);
		$z = (int) floor($this->z);
		if($y < 0 or $y >= Level::Y_MAX){
			return;
		}

		if($level->getRealBlockSkyLightAt($x, $y, $z) >= self::DAYLIGHT_BURN_SKY_LIGHT and $this->hasDirectDaylight($level, $x, $y, $z)){
			$this->setOnFire(2);
		}
	}

	protected function hasDaylightBlockingWeather(Level $level) : bool{
		$weather = $level->getWeather();
		return $weather !== null and ($weather->isRainy() or $weather->isRainyThunder());
	}

	protected function hasDirectDaylight(Level $level, int $x, int $y, int $z) : bool{
		for($checkY = $y; $checkY < Level::Y_MAX; ++$checkY){
			if(!$level->getBlock($this->temporalVector->setComponents($x, $checkY, $z))->canPassThrough()){
				return false;
			}
		}

		return true;
	}

}
