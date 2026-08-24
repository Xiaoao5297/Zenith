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

namespace pocketmine\command\defaults;

use pocketmine\command\Command;
use pocketmine\command\CommandSender;
use pocketmine\event\TranslationContainer;
use pocketmine\level\Level;
use pocketmine\Player;
use pocketmine\utils\TextFormat;

class GameruleCommand extends VanillaCommand{

	private static $knownVanillaRules = [
		"doMobSpawning",
		"doMobLoot",
		"mobGriefing",
		"tnTExplodes",
		"naturalRegeneration",
		"randomTickSpeed",
		"doEntityDrops",
		"keepInventory",
		"doDaylightCycle",
	];

	public function __construct($name){
		parent::__construct(
			$name,
			"Sets or queries a game rule value",
			"/gamerule [rule] [value]"
		);
		$this->setPermission("pocketmine.command.gamerule");
	}

	public function execute(CommandSender $sender, $currentAlias, array $args){
		if(!$this->testPermission($sender)){
			return true;
		}

		$level = $this->getLevelForSender($sender);
		if(!($level instanceof Level)){
			$sender->sendMessage(TextFormat::RED . "No world is available.");
			return true;
		}

		$server = $sender->getServer();
		$rules = $server->getSupportedGamerules();

		if(count($args) === 0){
			$values = [];
			foreach(self::$knownVanillaRules as $rule){
				if(isset($rules[$rule])){
					$values[] = $rule . " = " . $this->formatValue($server->getWorldGamerule($level, $rule));
				}
			}
			$sender->sendMessage("Gamerules for " . $level->getFolderName() . ": " . implode(", ", $values));
			return true;
		}

		$rule = $this->matchRuleName($args[0], $rules);
		if($rule === null){
			$sender->sendMessage(TextFormat::RED . "Unknown gamerule '" . $args[0] . "'.");
			return true;
		}

		if(count($args) === 1){
			$sender->sendMessage($rule . " = " . $this->formatValue($server->getWorldGamerule($level, $rule)));
			return true;
		}

		if(count($args) > 2){
			$sender->sendMessage(new TranslationContainer("commands.generic.usage", [$this->usageMessage]));
			return true;
		}

		$value = $this->parseValue($sender, $rules[$rule], $args[1]);
		if($value === null){
			return true;
		}

		if(!$server->setWorldGamerule($level, $rule, $value)){
			$sender->sendMessage(TextFormat::RED . "Could not set gamerule '" . $rule . "'.");
			return true;
		}

		Command::broadcastCommandMessage($sender, "Gamerule " . $rule . " has been updated to " . $this->formatValue($server->getWorldGamerule($level, $rule)) . " in " . $level->getFolderName());
		return true;
	}

	private function getLevelForSender(CommandSender $sender){
		if($sender instanceof Player){
			return $sender->getLevel();
		}

		return $sender->getServer()->getDefaultLevel();
	}

	private function matchRuleName(string $input, array $rules){
		foreach(array_keys($rules) as $rule){
			if(strcasecmp($rule, $input) === 0){
				return $rule;
			}
		}

		return null;
	}

	private function parseValue(CommandSender $sender, array $definition, string $value){
		if($definition["type"] === "int"){
			if(!preg_match('/^\d+$/', $value)){
				$sender->sendMessage(new TranslationContainer("commands.generic.usage", [$this->usageMessage]));
				return null;
			}

			return (int) $value;
		}

		if(strcasecmp($value, "true") === 0){
			return true;
		}
		if(strcasecmp($value, "false") === 0){
			return false;
		}

		$sender->sendMessage(new TranslationContainer("commands.generic.usage", [$this->usageMessage]));
		return null;
	}

	private function formatValue($value) : string{
		if(is_bool($value)){
			return $value ? "true" : "false";
		}

		return (string) $value;
	}
}
