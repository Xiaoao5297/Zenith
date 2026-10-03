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

class OplistCommand extends VanillaCommand{

	public function __construct($name){
		parent::__construct(
			$name,
			"列出管理员列表",
			"/oplist",
			["ol"]
		);
		$this->setPermission("pocketmine.command.plugins");
	}
	public function execute(CommandSender $sender, $currentAlias, array $args){
	if($sender->getServer()->getConfigString("player-list-ops") == false && !$this->testPermission($sender)){
			return true;
	}
	$arr = $sender->getServer()->OPlist();
	for($i = 0; $arr[$i] != ''; $i ++){
		$sender->sendMessage($arr[$i] . "\n");
	}
	//$sender->sendMessage("hello");
		return true;
	}
}
