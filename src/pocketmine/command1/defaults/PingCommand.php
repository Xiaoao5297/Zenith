<?php

/*
 *  ______   _____    ______  __   __  ______
 * /  ___/  /  ___|  / ___  \ \ \ / / |  ____|
 * | |___  | |      | |___| |  \ / /  | |____
 * \___  \ | |      |  ___  |   / /   |  ____|
 *  ___| | | |____  | |   | |  / / \  | |____
 * /_____/  \_____| |_|   |_| /_/ \_\ |______|
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * @author Sunch233#3226 QQ2125696621 And KKK
 * @link https://github.com/ScaxeTeam/Scaxe/
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
