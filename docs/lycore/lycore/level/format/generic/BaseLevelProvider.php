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

namespace lycore\level\format\generic;

use lycore\level\format\LevelProvider;
use lycore\level\generator\Generator;
use lycore\level\Level;
use lycore\math\Vector3;
use lycore\nbt\NBT;
use lycore\nbt\tag\CompoundTag;
use lycore\nbt\tag\IntTag;
use lycore\nbt\tag\LongTag;
use lycore\nbt\tag\StringTag;
use lycore\utils\LevelException;

abstract class BaseLevelProvider implements LevelProvider{
	/** @var Level */
	protected $level;
	/** @var string */
	protected $path;
	/** @var CompoundTag */
	protected $levelData;

	public function __construct(Level $level, $path){
		$this->level = $level;
		$this->path = $path;
		if(!file_exists($this->path)){
			mkdir($this->path, 0777, true);
		}
		$nbt = new NBT(NBT::BIG_ENDIAN);
		$nbt->readCompressed(file_get_contents($this->getPath() . "level.dat"));
		$levelData = $nbt->getData();
		if($levelData->Data instanceof CompoundTag){
			$this->levelData = $levelData->Data;
		}else{
			throw new LevelException("Invalid level.dat");
		}

		if(!isset($this->levelData->generatorName)){
			$this->levelData->generatorName = new StringTag("generatorName", Generator::getGenerator("DEFAULT"));
		}

		if(!isset($this->levelData->generatorOptions)){
			$this->levelData->generatorOptions = new StringTag("generatorOptions", "");
		}
	}

	public function getPath(){
		return $this->path;
	}

	public function getServer(){
		return $this->level->getServer();
	}

	public function getLevel(){
		return $this->level;
	}

	public function getName() : string{
		return $this->levelData["LevelName"];
	}

	public function getTime(){
		return $this->levelData["Time"];
	}

	public function setTime($value){
		$this->levelData->Time = new IntTag("Time", (int) $value);
	}

	public function initializeIndependentWorldState(int $dimension, int $time, int $weatherDurationMin, int $weatherDurationMax) : int{
		$initialized = isset($this->levelData->LYCoreWorldStateVersion);
		if(!$initialized and $dimension === Level::DIMENSION_NORMAL and $time === 0){
			$time = $this->getWorldStateOffset("time", 1, Level::TIME_FULL - 1);
			$this->setTime($time);
		}

		$this->ensureWeatherStateTags($weatherDurationMin, $weatherDurationMax);
		if(!$initialized){
			$this->levelData->LYCoreWorldStateVersion = new IntTag("LYCoreWorldStateVersion", 1);
		}

		return $time;
	}

	public function getWeatherState() : array{
		$this->ensureWeatherStateTags(6000, 12000);
		return [
			"weather" => (int) $this->levelData["Weather"],
			"duration" => (int) $this->levelData["WeatherDuration"],
			"rainStrength" => (int) $this->levelData["RainStrength"],
			"thunderStrength" => (int) $this->levelData["ThunderStrength"],
		];
	}

	public function setWeatherState(array $state) : void{
		$weather = isset($state["weather"]) ? max(0, min(3, (int) $state["weather"])) : 0;
		$this->levelData->Weather = new IntTag("Weather", $weather);
		$this->levelData->WeatherDuration = new IntTag("WeatherDuration", max(1, isset($state["duration"]) ? (int) $state["duration"] : 12000));
		$this->levelData->RainStrength = new IntTag("RainStrength", max(0, isset($state["rainStrength"]) ? (int) $state["rainStrength"] : 0));
		$this->levelData->ThunderStrength = new IntTag("ThunderStrength", max(0, isset($state["thunderStrength"]) ? (int) $state["thunderStrength"] : 0));
		$this->levelData->LYCoreWorldStateVersion = new IntTag("LYCoreWorldStateVersion", 1);
	}

	private function ensureWeatherStateTags(int $durationMin, int $durationMax) : void{
		if(!isset($this->levelData->Weather)){
			$this->levelData->Weather = new IntTag("Weather", 0);
		}
		if(!isset($this->levelData->WeatherDuration)){
			$this->levelData->WeatherDuration = new IntTag("WeatherDuration", $this->getWorldStateOffset("weather-duration", $durationMin, $durationMax));
		}
		if(!isset($this->levelData->RainStrength)){
			$this->levelData->RainStrength = new IntTag("RainStrength", 0);
		}
		if(!isset($this->levelData->ThunderStrength)){
			$this->levelData->ThunderStrength = new IntTag("ThunderStrength", 0);
		}
	}

	private function getWorldStateOffset(string $salt, int $min, int $max) : int{
		if($max < $min){
			$tmp = $max;
			$max = $min;
			$min = $tmp;
		}
		$span = max(1, $max - $min + 1);
		$key = $this->getName() . ":" . $this->getSeed() . ":" . $salt;
		return $min + ((int) sprintf("%u", crc32($key)) % $span);
	}

	public function getSeed(){
		return $this->levelData["RandomSeed"];
	}

	public function setSeed($value){
		$this->levelData->RandomSeed = new LongTag("RandomSeed", (int) $value);
	}

	public function getSpawn(){
		return new Vector3((float) $this->levelData["SpawnX"], (float) $this->levelData["SpawnY"], (float) $this->levelData["SpawnZ"]);
	}

	public function setSpawn(Vector3 $pos){
		$this->levelData->SpawnX = new IntTag("SpawnX", (int) $pos->x);
		$this->levelData->SpawnY = new IntTag("SpawnY", (int) $pos->y);
		$this->levelData->SpawnZ = new IntTag("SpawnZ", (int) $pos->z);
	}

	public function doGarbageCollection(){

	}

	/**
	 * @return CompoundTag
	 */
	public function getLevelData(){
		return $this->levelData;
	}

	public function saveLevelData(){
		$nbt = new NBT(NBT::BIG_ENDIAN);
		$nbt->setData(new CompoundTag("", [
			"Data" => $this->levelData
		]));
		$buffer = $nbt->writeCompressed();
		file_put_contents($this->getPath() . "level.dat", $buffer);
	}


}
