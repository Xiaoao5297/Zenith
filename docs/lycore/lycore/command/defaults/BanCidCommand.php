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

class BanCidCommand extends VanillaCommand{

	public function __construct($name){
		parent::__construct(
			$name,
			"%pocketmine.command.bancid.description",
			"%commands.bancid.usage"
		);
		$this->setPermission("pocketmine.command.bancid");
	}

	public function execute(CommandSender $sender, $currentAlias, array $args){
		if(!$this->testPermission($sender)){
			return true;
		}

		if(count($args) === 0){
			$sender->sendMessage(new TranslationContainer("commands.generic.usage", [$this->usageMessage]));

			return false;
		}

		$cid = array_shift($args);
		$reason = implode(" ", $args);

		$sender->getServer()->getCIDBans()->addBan($cid, $reason, null, $sender->getName());

		$player = null;

		foreach($sender->getServer()->getOnlinePlayers() as $p){
			if($p->getClientId() == $cid) {
				$p->kick($reason !== "" ? "被封禁踢出游戏，原因: " . $reason : "被封禁踢出游戏");
				$player = $p;
				break;
			}
		}

		Command::broadcastCommandMessage($sender, new TranslationContainer("%commands.bancid.success", [$player !== null ? $player->getName() : $cid]));

		return true;
	}
}
