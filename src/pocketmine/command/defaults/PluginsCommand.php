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
use pocketmine\utils\TextFormat;

class PluginsCommand extends VanillaCommand{

	public function __construct($name){
		parent::__construct(
			$name,
			"%pocketmine.command.plugins.description",
			"%pocketmine.command.plugins.usage",
			["pl"]
		);
		$this->setPermission("pocketmine.command.plugins");
	}

	public function execute(CommandSender $sender, $currentAlias, array $args){
		if(!$this->testPermission($sender)){
			return true;
		}
		$this->sendPluginList($sender);
		return true;
	}

	private function sendPluginList(CommandSender $sender){
		$list = "";
		foreach(($plugins = $sender->getServer()->getPluginManager()->getPlugins()) as $plugin){
			if(strlen($list) > 0){
				$list .= TextFormat::WHITE . ", ";
			}
			$list .= $plugin->isEnabled() ? TextFormat::GREEN : TextFormat::RED;
			$list .= $plugin->getDescription()->getFullName();
		}

		$sender->sendMessage(new TranslationContainer("pocketmine.command.plugins.success", [count($plugins), $list]));
	}
}
