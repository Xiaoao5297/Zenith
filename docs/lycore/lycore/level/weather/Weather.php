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

use lycore\event\level\WeatherChangeEvent;
use lycore\level\Level;
use lycore\math\Vector3;
use lycore\network\protocol\LevelEventPacket;
use lycore\Player;

class Weather{
	const CLEAR = 0;
	const SUNNY = 0;
	const RAIN = 1;
	const RAINY = 1;
	const RAINY_THUNDER = 2;
	const THUNDER = 3;

	private $level;
	private $weatherNow = 0;
	private $strength1;
	private $strength2;
	private $duration;
	private $canCalculate = true;

	/** @var Vector3 */
	private $temporalVector = null;

	private $lastUpdate = 0;

	private $randomWeatherData = [0, 1, 0, 1, 0, 1, 0, 2, 0, 3];

	public function __construct(Level $level, $duration = 1200){
		$this->level = $level;
		$this->weatherNow = self::SUNNY;
		$this->duration = $duration;
		$this->strength1 = 0;
		$this->strength2 = 0;
		$this->lastUpdate = $level->getServer()->getTick();
		$this->temporalVector = new Vector3(0, 0, 0);
		$provider = $level->getProvider();
		if($provider !== null and method_exists($provider, "getWeatherState")){
			$this->setState($provider->getWeatherState());
		}
	}

	public function canCalculate() : bool{
		return $this->canCalculate;
	}

	public function setCanCalculate(bool $canCalc){
		$this->canCalculate = $canCalc;
	}

	public function calcWeather($currentTick){
		if($this->canCalculate()){
			$tickDiff = $currentTick - $this->lastUpdate;
			$this->duration -= $tickDiff;
			if($this->duration <= 0){
				//0晴天1下雨2雷雨3阴天雷
				if($this->weatherNow == self::SUNNY){
					$weather = $this->randomWeatherData[array_rand($this->randomWeatherData)];
					$duration = mt_rand(min($this->level->getServer()->weatherRandomDurationMin, $this->level->getServer()->weatherRandomDurationMax), max($this->level->getServer()->weatherRandomDurationMin, $this->level->getServer()->weatherRandomDurationMax));;
					$this->level->getServer()->getPluginManager()->callEvent($ev = new WeatherChangeEvent($this->level, $weather, $duration));
					if(!$ev->isCancelled()){
						$this->weatherNow = $ev->getWeather();
						$this->strength1 = mt_rand(90000, 110000);
						$this->strength2 = mt_rand(30000, 40000);
						$this->duration = $ev->getDuration();
						$this->changeWeather($this->weatherNow, $this->strength1, $this->strength2);
					}
				}else{
					$weather = self::SUNNY;
					$duration = mt_rand(min($this->level->getServer()->weatherRandomDurationMin, $this->level->getServer()->weatherRandomDurationMax), max($this->level->getServer()->weatherRandomDurationMin, $this->level->getServer()->weatherRandomDurationMax));
					$this->level->getServer()->getPluginManager()->callEvent($ev = new WeatherChangeEvent($this->level, $weather, $duration));
					if(!$ev->isCancelled()){
						$this->weatherNow = $ev->getWeather();
						$this->strength1 = 0;
						$this->strength2 = 0;
						$this->duration = $ev->getDuration();
						$this->changeWeather($this->weatherNow, $this->strength1, $this->strength2);
					}
				}
			}
			if(($this->weatherNow > 0) and ($this->level->getServer()->lightningTime > 0) and is_int($this->duration / $this->level->getServer()->lightningTime)){
				$players = $this->level->getPlayers();
				if(count($players) > 0){
					$p = $players[array_rand($players)];
					$x = $p->x + mt_rand(-64, 64);
					$z = $p->z + mt_rand(-64, 64);
					$y = $this->level->getHighestBlockAt($x, $z);
					$this->level->spawnLightning($this->temporalVector->setComponents($x, $y, $z));
				}
				/*foreach($this->level->getPlayers() as $p){
					if(mt_rand(0, 1) == 1){
						$x = $p->getX() + rand(-100, 100);
						$y = $p->getY() + rand(20, 50);
						$z = $p->getZ() + rand(-100, 100);
						$this->level->sendLighting($x, $y, $z, $p);
					}
				}*/
			}
		}
		$this->lastUpdate = $currentTick;
	}

	public function setWeather(int $wea, int $duration = 12000){
		$this->level->getServer()->getPluginManager()->callEvent($ev = new WeatherChangeEvent($this->level, $wea, $duration));
		if(!$ev->isCancelled()){
			$this->weatherNow = $ev->getWeather();
			$this->strength1 = $this->weatherNow == self::SUNNY ? 0 : mt_rand(90000, 110000);
			$this->strength2 = ($this->weatherNow == self::RAINY_THUNDER or $this->weatherNow == self::THUNDER) ? mt_rand(30000, 40000) : 0;
			$this->duration = $ev->getDuration();
			$this->changeWeather($this->weatherNow, $this->strength1, $this->strength2);
		}
	}

	public function getState() : array{
		return [
			"weather" => (int) $this->weatherNow,
			"duration" => max(0, (int) $this->duration),
			"rainStrength" => max(0, (int) $this->strength1),
			"thunderStrength" => max(0, (int) $this->strength2),
		];
	}

	public function setState(array $state) : void{
		$weather = isset($state["weather"]) ? (int) $state["weather"] : self::SUNNY;
		if($weather < self::SUNNY or $weather > self::THUNDER){
			$weather = self::SUNNY;
		}

		$this->weatherNow = $weather;
		$this->duration = max(1, isset($state["duration"]) ? (int) $state["duration"] : (int) $this->duration);
		$this->strength1 = max(0, isset($state["rainStrength"]) ? (int) $state["rainStrength"] : 0);
		$this->strength2 = max(0, isset($state["thunderStrength"]) ? (int) $state["thunderStrength"] : 0);
		if($this->weatherNow == self::SUNNY){
			$this->strength1 = 0;
			$this->strength2 = 0;
		}elseif($this->weatherNow == self::RAINY and $this->strength1 <= 0){
			$this->strength1 = 100000;
		}elseif($this->weatherNow == self::RAINY_THUNDER){
			if($this->strength1 <= 0){
				$this->strength1 = 100000;
			}
			if($this->strength2 <= 0){
				$this->strength2 = 35000;
			}
		}elseif($this->weatherNow == self::THUNDER and $this->strength2 <= 0){
			$this->strength2 = 35000;
		}
	}

	public function getRandomWeatherData() : array{
		return $this->randomWeatherData;
	}

	public function setRandomWeatherData(array $randomWeatherData){
		$this->randomWeatherData = $randomWeatherData;
	}

	public function getWeather() : int{
		return $this->weatherNow;
	}

	public static function getWeatherFromString($weather){
		if(is_int($weather)){
			if($weather <= 3){
				return $weather;
			}
			return self::SUNNY;
		}
		switch(strtolower($weather)){
			case "clear":
			case "sunny":
			case "fine":
				return self::SUNNY;
			case "rain":
			case "rainy":
				return self::RAINY;
			case "thunder":
				return self::THUNDER;
			case "rain_thunder":
			case "rainy_thunder":
				return self::RAINY_THUNDER;
			default:
				return self::SUNNY;
		}
	}

	/**
	 * @return bool
	 */
	public function isSunny() : bool{
		if($this->getWeather() == self::SUNNY){
			return true;
		}else{
			return false;
		}
	}

	/**
	 * @return bool
	 */
	public function isRainy() : bool{
		if($this->getWeather() == self::RAINY){
			return true;
		}else{
			return false;
		}
	}

	/**
	 * @return bool
	 */
	public function isRainyThunder() : bool{
		if($this->getWeather() == self::RAINY_THUNDER){
			return true;
		}else{
			return false;
		}
	}

	/**
	 * @return bool
	 */
	public function isThunder() : bool{
		if($this->getWeather() == self::THUNDER){
			return true;
		}else{
			return false;
		}
	}

	public function getStrength() : array{
		return [$this->strength1, $this->strength2];
	}

	public function sendWeather(Player $p){
		$p->dataPacket($this->createWeatherPacket(LevelEventPacket::EVENT_STOP_RAIN, $this->strength1));
		$p->dataPacket($this->createWeatherPacket(LevelEventPacket::EVENT_STOP_THUNDER, $this->strength2));
		if($this->weatherNow == self::RAINY){
			$p->dataPacket($this->createWeatherPacket(LevelEventPacket::EVENT_START_RAIN, $this->strength1));
		}elseif($this->weatherNow == self::RAINY_THUNDER){
			$p->dataPacket($this->createWeatherPacket(LevelEventPacket::EVENT_START_RAIN, $this->strength1));
			$p->dataPacket($this->createWeatherPacket(LevelEventPacket::EVENT_START_THUNDER, $this->strength2));
		}elseif($this->weatherNow == self::THUNDER){
			$p->dataPacket($this->createWeatherPacket(LevelEventPacket::EVENT_START_THUNDER, $this->strength2));
		}
		$p->weatherData = [$this->weatherNow, $this->strength1, $this->strength2];
	}

	public function changeWeather(int $wea, int $strength1, int $strength2){
		foreach($this->level->getPlayers() as $p){
			$this->sendWeather($p);
		}
	}

	private function createWeatherPacket(int $eventId, int $data) : LevelEventPacket{
		$pk = new LevelEventPacket;
		$pk->evid = $eventId;
		$pk->x = 0;
		$pk->y = 0;
		$pk->z = 0;
		$pk->data = $data;
		return $pk;
	}

}
	
