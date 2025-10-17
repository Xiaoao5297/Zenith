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
use pocketmine\command\ConsoleCommandSender;
use pocketmine\event\TranslationContainer;
use pocketmine\Player;
use pocketmine\utils\TextFormat;

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
		}else{
			$player = $sender->getServer()->getPlayerExact($name = $args[1]);
			if($player instanceof Player){
				if(strcasecmp(substr($args[0], -1), "L") == 0){			//Set Experience Level(with "L" after args[0])
					$level = rtrim($args[0], "Ll");
					if(is_numeric($level)){
						$player->addExpLevel($level);
						$sender->sendMessage("成功添加 $level 级经验给 $name");
					}
				}elseif(is_numeric($args[0])){											//Set Experience
					$player->addExperience($args[0]);
					$sender->sendMessage("成功添加 $args[0] 经验给 $name");
				}else{
					$sender->sendMessage("格式错误");
					return false;
				}
			}else{
				$sender->sendMessage(new TranslationContainer(TextFormat::RED . "%commands.generic.player.notFound"));
				return false;
			}
		}
		return false;
	}
}
