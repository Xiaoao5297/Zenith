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

namespace lycore\command\defaults;

use lycore\command\Command;
use lycore\command\CommandSender;
use lycore\math\Vector3;
use lycore\Player;

abstract class VanillaCommand extends Command{
	const MAX_COORD = 30000000;
	const MIN_COORD = -30000000;

	public function __construct($name, $description = "", $usageMessage = null, array $aliases = []){
		parent::__construct($name, $description, $usageMessage, $aliases);
	}

	protected function getInteger(CommandSender $sender, $value, $min = self::MIN_COORD, $max = self::MAX_COORD){
		$i = (int) $value;

		if($i < $min){
			$i = $min;
		}elseif($i > $max){
			$i = $max;
		}

		return $i;
	}

	protected function getRelativeDouble($original, CommandSender $sender, $input, $min = self::MIN_COORD, $max = self::MAX_COORD){
		$input = (string) $input;
		if($input === ""){
			throw new \InvalidArgumentException("Invalid coordinate: " . $input);
		}

		if($input[0] === "~"){
			$offset = substr($input, 1);
			$value = $offset === "" ? 0 : $this->getDouble($sender, $offset);

			return $this->clampCoordinate($original + $value, $min, $max);
		}

		return $this->getDouble($sender, $input, $min, $max);
	}

	protected function getDouble(CommandSender $sender, $value, $min = self::MIN_COORD, $max = self::MAX_COORD){
		if(!is_numeric($value)){
			throw new \InvalidArgumentException("Invalid coordinate: " . $value);
		}

		return $this->clampCoordinate((double) $value, $min, $max);
	}

	protected function getRelativeVector($base, CommandSender $sender, array $args, $round = false){
		if(count($args) !== 3){
			throw new \InvalidArgumentException("Expected three coordinate arguments");
		}

		$base = $this->asVector3($base);
		$x = $this->getRelativeDouble($base->x, $sender, $args[0]);
		$y = $this->getRelativeDouble($base->y, $sender, $args[1], 0, 128);
		$z = $this->getRelativeDouble($base->z, $sender, $args[2]);

		if($round){
			return new Vector3((int) round($x), (int) round($y), (int) round($z));
		}

		return new Vector3($x, $y, $z);
	}

	protected function getCommandPositionBase(CommandSender $sender, $fallback = null){
		if($sender instanceof Player){
			return $this->asVector3($sender);
		}

		if($fallback !== null){
			return $this->asVector3($fallback);
		}

		$server = $sender->getServer();
		if(is_object($server) and method_exists($server, "getDefaultLevel")){
			$level = $server->getDefaultLevel();
			if(is_object($level) and method_exists($level, "getSafeSpawn")){
				return $this->asVector3($level->getSafeSpawn());
			}
			if(is_object($level) and method_exists($level, "getSpawnLocation")){
				return $this->asVector3($level->getSpawnLocation());
			}
		}

		return new Vector3(0, 0, 0);
	}

	private function asVector3($value){
		if($value instanceof Vector3){
			return $value;
		}

		if(is_object($value)){
			return new Vector3(
				$this->readCoordinate($value, "x"),
				$this->readCoordinate($value, "y"),
				$this->readCoordinate($value, "z")
			);
		}

		throw new \InvalidArgumentException("Invalid coordinate base");
	}

	private function readCoordinate($object, $axis){
		$getter = "get" . strtoupper($axis);
		if(method_exists($object, $getter)){
			return (double) $object->{$getter}();
		}
		if(property_exists($object, $axis)){
			return (double) $object->{$axis};
		}

		throw new \InvalidArgumentException("Invalid coordinate base");
	}

	private function clampCoordinate($i, $min, $max){
		if($i < $min){
			$i = $min;
		}elseif($i > $max){
			$i = $max;
		}

		return $i;
	}
}
