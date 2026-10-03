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
use lycore\Server;
use lycore\utils\TextFormat;

class PingCommand extends VanillaCommand{

    public function __construct($name){
		parent::__construct(
			$name,
			"PING玩家",
			"/ping (player)"
		);
	}
	
	public function execute(CommandSender $sender, $commandLabel, array $args)
    {
        if (!(isset($args[0]))) {
			if (!($sender instanceof Player)) {
				$sender->sendMessage("§c只能在游戏中使用!");
				return true;
			}
			$ping = $sender->getPing();
			if($ping <= 60){
				$sender->sendMessage("§a当前延迟: " . $ping . "ms - 极速");
			}else if($ping <= 90){
				$sender->sendMessage("§2当前延迟: " . $ping . "ms - 流畅");
			}else if($ping <= 150){
				$sender->sendMessage("§e当前玩家延迟: " . $ping . "ms - 微慢");
			}else if($ping <= 190){
				$sender->sendMessage("§6当前延迟: " . $ping . "ms - 延迟");
			}else if($ping <= 210){
				$sender->sendMessage("§c当前延迟: " . $ping . "ms - 卡顿");
			}else{
				$sender->sendMessage("§4当前延迟: " . $ping . "ms - 土豆");
			}
            return true;
        } else {
            $target = Server::getInstance()->getPlayer($args[0]);

            if ($target == null) {
                return $sender->sendMessage(TextFormat::RED . "找不到该玩家");
            }

            $ping = $target->getPing();
			if($ping <= 60){
				$sender->sendMessage("§a当前玩家" . $target->getName() . "的延迟: " . $ping . "ms - 极速");
			}else if($ping <= 90){
				$sender->sendMessage("§2当前玩家" . $target->getName() . "的延迟: " . $ping . "ms - 流畅");
			}else if($ping <= 150){
				$sender->sendMessage("§e当前玩家" . $target->getName() . "的延迟: " . $ping . "ms - 微慢");
			}else if($ping <= 190){
				$sender->sendMessage("§6当前玩家" . $target->getName() . "的延迟: " . $ping . "ms - 延迟");
			}else if($ping <= 210){
				$sender->sendMessage("§c当前玩家" . $target->getName() . "的延迟: " . $ping . "ms - 卡顿");
			}else{
				$sender->sendMessage("§4当前玩家" . $target->getName() . "的延迟: " . $ping . "ms - 土豆");
			}
        }
        return false;
    }
}
