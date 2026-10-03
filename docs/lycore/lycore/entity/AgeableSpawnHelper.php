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

final class AgeableSpawnHelper{
	const PNX_SPAWN_EGG_BABY_CHANCE = 6;
	const PNX_NATURAL_BABY_CHANCE = 6;

	private static $notBabyCapableClasses = [
		"lycore\\entity\\IronGolem",
		"lycore\\entity\\SnowGolem",
		"lycore\\entity\\Lightning"
	];

	private function __construct(){
	}

	public static function canSpawnAsBaby(Entity $entity) : bool{
		if(!($entity instanceof Ageable)){
			return false;
		}

		foreach(self::$notBabyCapableClasses as $class){
			if($entity instanceof $class){
				return false;
			}
		}

		return true;
	}

	public static function maybeSetBaby(Entity $entity, int $chance = self::PNX_SPAWN_EGG_BABY_CHANCE, int $roll = null) : bool{
		if(!self::canSpawnAsBaby($entity) or $chance <= 0){
			return false;
		}

		$roll = $roll === null ? mt_rand(0, $chance - 1) : $roll;
		if($roll % $chance !== 0){
			return false;
		}

		$entity->setBaby(true);
		return true;
	}
}
