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

class EmojiCommand extends VanillaCommand{

	public function __construct($name){
		parent::__construct(
			$name,
			"列出颜文字列表",
			"/emoji"
		);
		$this->setPermission("");
	}
	public function execute(CommandSender $sender, $currentAlias, array $args){
		$sender->sendMessage("§e****Emoji颜文字列表****");
		$sender->sendMessage("§e格式(在前面加/)  表情  预览");
		$sender->sendMessage("§e ↓↓   ↓↓    ↓↓§f");
		$sender->sendMessage("wx  微笑  /wx");
		$sender->sendMessage("sq  生气  /sq");
		$sender->sendMessage("jy  惊讶  /jy");
		$sender->sendMessage("dy  瞪眼  /dy");
		$sender->sendMessage("sx  伤心  /sx");
		$sender->sendMessage("wh  问候  /wh");
		$sender->sendMessage("wy  无语  /wy");
		return true;
	}
}
