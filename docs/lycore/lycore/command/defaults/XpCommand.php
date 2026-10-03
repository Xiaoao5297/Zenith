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
use lycore\Player;
use lycore\utils\TextFormat;

class XpCommand extends VanillaCommand{

	public function __construct($name){
		parent::__construct(
			$name,
			"%pocketmine.command.xp.description",
			"%commands.xp.usage"
		);
		$this->setPermission("pocketmine.command.xp");
	}

	public function execute(CommandSender $sender, $currentAlias, array $args){
		if(!$this->testPermission($sender)){
			return true;
		}

		if(count($args) != 2){
			$sender->sendMessage(new TranslationContainer("commands.generic.usage", [$this->usageMessage]));
			return false;
		}

		$server = $sender->getServer();
		$player = $server->getPlayerExact($name = $args[1]);
		if(!($player instanceof Player)){
			$sender->sendMessage(new TranslationContainer(TextFormat::RED . "%commands.generic.player.notFound"));
			return false;
		}

		$value = $args[0];
		if(strcasecmp(substr($value, -1), "L") === 0){
			$levelValue = substr($value, 0, -1);
			if($levelValue === "" or preg_match('/^[+-]?\d+$/', $levelValue) !== 1){
				$sender->sendMessage("Argument error");
				return false;
			}
			$level = (int) $levelValue;

			$newLevel = $player->getExpLevel() + $level;
			if($newLevel < 0){
				$newLevel = 0;
			}elseif($newLevel > 24791){
				$newLevel = 24791;
			}

			if($newLevel === 0){
				$exp = 0;
			}else{
				$currentBase = $server->getExpectedExperience($player->getExpLevel());
				$currentProgress = max(0, $player->getExp() - $currentBase);
				$base = $server->getExpectedExperience($newLevel);
				$next = $server->getExpectedExperience($newLevel + 1);
				$levelWidth = max(1, $next - $base);
				$exp = $base + min($currentProgress, $levelWidth - 1);
			}

			$player->setExperienceAndLevel($exp, $newLevel);
			Command::broadcastCommandMessage($sender, new TranslationContainer("commands.xp.success.levels", [$level, $name]));
			return true;
		}

		if(preg_match('/^[+-]?\d+$/', $value) !== 1){
			$sender->sendMessage("Argument error");
			return false;
		}

		$amount = (int) $value;
		if($amount < 0){
			$sender->sendMessage(new TranslationContainer("commands.xp.failure.widthdrawXp"));
			return false;
		}

		$player->addExperience($amount);
		Command::broadcastCommandMessage($sender, new TranslationContainer("commands.xp.success", [$amount, $name]));
		return true;
	}
}
