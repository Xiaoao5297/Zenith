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
use lycore\event\TranslationContainer;
use lycore\level\Level;
use lycore\Player;
use lycore\utils\TextFormat;

class TimeCommand extends VanillaCommand{

	public function __construct($name){
		parent::__construct(
			$name,
			"%pocketmine.command.time.description",
			"%pocketmine.command.time.usage"
		);
		$this->setPermission("pocketmine.command.time.add;pocketmine.command.time.set;pocketmine.command.time.start;pocketmine.command.time.stop");
	}

	public function execute(CommandSender $sender, $currentAlias, array $args){
		if(count($args) < 1){
			$sender->sendMessage(new TranslationContainer("commands.generic.usage", [$this->usageMessage]));

			return false;
		}

		$level = $this->getLevelForSender($sender);
		if(!($level instanceof Level)){
			$sender->sendMessage(TextFormat::RED . "No world is available.");
			return true;
		}

		if($args[0] === "start"){
			if(!$sender->hasPermission("pocketmine.command.time.start")){
				$sender->sendMessage(new TranslationContainer(TextFormat::RED . "%commands.generic.permission"));

				return true;
			}
			$level->checkTime();
			$level->startTime();
			$level->checkTime();
			Command::broadcastCommandMessage($sender, "§6重新开始计时");
			return true;
		}elseif($args[0] === "stop"){
			if(!$sender->hasPermission("pocketmine.command.time.stop")){
				$sender->sendMessage(new TranslationContainer(TextFormat::RED . "%commands.generic.permission"));

				return true;
			}
			$level->checkTime();
			$level->stopTime();
			$level->checkTime();
			Command::broadcastCommandMessage($sender, "§c已停止时间");
			return true;
		}elseif($args[0] === "query"){
			if(!$sender->hasPermission("pocketmine.command.time.query")){
				$sender->sendMessage(new TranslationContainer(TextFormat::RED . "%commands.generic.permission"));

				return true;
			}
			$sender->sendMessage(new TranslationContainer("commands.time.query", [$level->getTime()]));
			return true;
		}


		if(count($args) < 2){
			$sender->sendMessage(new TranslationContainer("commands.generic.usage", [$this->usageMessage]));

			return false;
		}

		if($args[0] === "set"){
			if(!$sender->hasPermission("pocketmine.command.time.set")){
				$sender->sendMessage(new TranslationContainer(TextFormat::RED . "%commands.generic.permission"));

				return true;
			}

			if($args[1] === "day"){
				$value = 0;
			}elseif($args[1] === "night"){
				$value = Level::TIME_NIGHT;
			}else{
				$value = $this->getInteger($sender, $args[1], 0);
			}

			$level->checkTime();
			$level->setTime($value);
			$level->checkTime();
			Command::broadcastCommandMessage($sender, new TranslationContainer("commands.time.set", [$value]));
		}elseif($args[0] === "add"){
			if(!$sender->hasPermission("pocketmine.command.time.add")){
				$sender->sendMessage(new TranslationContainer(TextFormat::RED . "%commands.generic.permission"));

				return true;
			}

			$value = $this->getInteger($sender, $args[1], 0);
			$level->checkTime();
			$level->setTime($level->getTime() + $value);
			$level->checkTime();
			Command::broadcastCommandMessage($sender, new TranslationContainer("commands.time.added", [$value]));
		}else{
			$sender->sendMessage(new TranslationContainer("commands.generic.usage", [$this->usageMessage]));
		}

		return true;
	}

	private function getLevelForSender(CommandSender $sender){
		if($sender instanceof Player){
			return $sender->getLevel();
		}

		return $sender->getServer()->getDefaultLevel();
	}
}
