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

namespace lycore\level\weather;

use lycore\level\Level;

/**
 * @deprecated
 */
class WeatherManager{
	/** @var Level[] */
	public static $registeredLevel = [];
	
	public static function registerLevel(Level $level){
		self::$registeredLevel[$level->getName()] = $level;
		return true;
	}
	
	public static function unregisterLevel(Level $level){
		if(isset(self::$registeredLevel[$level->getName()])) {
			unset(self::$registeredLevel[$level->getName()]);
			return true;
		}
		return false;
	}
	
	public static function updateWeather(){
		foreach(self::$registeredLevel as $level) {
			$level->getWeather()->calcWeather($level->getServer()->getTick());
		}
	}
	
	public static function isRegistered(Level $level){
		if(isset(self::$registeredLevel[$level->getName()])) return true;
		return false;
	}

}