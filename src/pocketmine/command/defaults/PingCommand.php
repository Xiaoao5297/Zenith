<?php

/* 
 *  _____              _ __  __  
 * /__  /  ___  ____  (_) /_/ /_ 
 *   / /  / _ \/ __ \/ / __/ __ \
 *  / /__/  __/ / / / / /_/ / / /
 * /____/\___/_/ /_/_/\__/_/ /_/ 
 *                               
 * This program is free software: you can redistribute/modify it 
 * under the terms of the GNU LGPL, version 3 or later.
 *
 * @author Xiaoao
 * @link https://b23.tv/LQKxdts
 *
*/

namespace pocketmine\command\defaults;

use pocketmine\command\Command;
use pocketmine\command\CommandSender;
use pocketmine\event\TranslationContainer;
use pocketmine\Player;
use pocketmine\Server;
use pocketmine\utils\TextFormat;

class PingCommand extends VanillaCommand{

    public function __construct($name){
		parent::__construct(
			$name,
			"get player's ping",
			"/ping (player)"
		);
	}
	
	public function execute(CommandSender $sender, $commandLabel, array $args)
    {

        if (!($sender instanceof Player)) {
            $sender->sendMessage("Only in game!");
            return true;
        }

        if (!(isset($args[0]))) {
            $sender->sendMessage("Ping: " . $sender->getPing() . "ms");
            return true;
        } else {
            $target = Server::getInstance()->getPlayer($args[0]);

            if ($target == null) {
                return $sender->sendMessage(TextFormat::RED . "找不到该玩家");
            }

            $sender->sendMessage($target->getName() . "'s ping: " . $target->getPing() . "ms");
        }
        return false;
    }
}
