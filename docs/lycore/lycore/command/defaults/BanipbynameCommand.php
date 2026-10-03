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

class BanipbynameCommand extends VanillaCommand{

	public function __construct($name){
		parent::__construct(
			$name,
			"%pocketmine.command.banipbyname.description",
			"%commands.banipbyname.usage"
		);
		$this->setPermission("pocketmine.command.banipbyname");
	}

	public function execute(CommandSender $sender, $currentAlias, array $args){
		if(!$this->testPermission($sender)){
			return \true;
		}

		if(\count($args) === 0){
			$sender->sendMessage(new TranslationContainer("commands.generic.usage", [$this->usageMessage]));

			return \false;
		}

		$name = \array_shift($args);
		$reason = \implode(" ", $args);
		
		if ($sender->getServer()->getPlayer($name) instanceof Player) $target = $sender->getServer()->getPlayer($name);
		else return \false;

		$sender->getServer()->getIPBans()->addBan($target->getAddress(), $reason, \null, $sender->getName());

		if(($player = $sender->getServer()->getPlayerExact($name)) instanceof Player){
			$player->kick($reason !== "" ? "被封禁踢出游戏，原因: " . $reason : "被封禁踢出游戏");
		}

		Command::broadcastCommandMessage($sender, new TranslationContainer("%commands.banipbyname.success", [$player !== \null ? $player->getName() : $name]));

		return \true;
	}
}
