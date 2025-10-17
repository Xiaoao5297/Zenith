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

use pocketmine\command\CommandSender;
use pocketmine\event\TranslationContainer;
use pocketmine\Player;
use pocketmine\utils\TextFormat;

class TellCommand extends VanillaCommand{

	public function __construct($name){
		parent::__construct(
			$name,
			"%pocketmine.command.tell.description",
			"%commands.message.usage",
			["w", "whisper", "msg", "m"]
		);
		$this->setPermission("pocketmine.command.tell");
	}

	public function execute(CommandSender $sender, $currentAlias, array $args){
		if(!$this->testPermission($sender)){
			return true;
		}

		if(count($args) < 2){
			$sender->sendMessage(new TranslationContainer("commands.generic.usage", [$this->usageMessage]));

			return false;
		}

		$name = strtolower(array_shift($args));

		$player = $sender->getServer()->getPlayer($name);

		if($player === $sender){
			$sender->sendMessage(new TranslationContainer(TextFormat::RED . "%commands.message.sameTarget"));
			return true;
		}

		if($player instanceof Player){
			$sender->sendMessage("[".$sender->getName()." -> " . $player->getDisplayName() . "] " . implode(" ", $args));
			$player->sendMessage("[" . ($sender instanceof Player ? $sender->getDisplayName() : $sender->getName()) . " -> ".$player->getName()."] " . implode(" ", $args));
		}else{
			$sender->sendMessage(new TranslationContainer("commands.generic.player.notFound"));
		}

		return true;
	}
}
