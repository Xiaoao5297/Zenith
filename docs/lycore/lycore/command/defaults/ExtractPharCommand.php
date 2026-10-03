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
use lycore\utils\TextFormat;

class ExtractPharCommand extends VanillaCommand{

	public function __construct($name){
		parent::__construct(
			$name,
			"从 Phar 文件中提取源代码",
			"/extractphar <Phar file Name>"
		);
		$this->setPermission("pocketmine.command.extractphar");
	}

	public function execute(CommandSender $sender, $commandLabel, array $args){
		if(!$this->testPermission($sender)){
			return false;
		}

		if(count($args) === 0){
			$sender->sendMessage(TextFormat::RED . "Usage: ".$this->usageMessage);
			return true;
		}
		if(!isset($args[0]) or !file_exists($args[0])) return \false;
		$folderPath = $sender->getServer()->getPluginPath().DIRECTORY_SEPARATOR . "LY Core" . DIRECTORY_SEPARATOR . basename($args[0]);
		if(file_exists($folderPath)){
			$sender->sendMessage("Phar already exists, overwriting...");
		}else{
			@mkdir($folderPath);
		}
		
		$pharPath = "phar://$args[0]";

		foreach(new \RecursiveIteratorIterator(new \RecursiveDirectoryIterator($pharPath)) as $fInfo){
			$path = $fInfo->getPathname();
			@mkdir(dirname($folderPath . str_replace($pharPath, "", $path)), 0755, true);
			file_put_contents($folderPath . str_replace($pharPath, "", $path), file_get_contents($path));
		}
		$sender->sendMessage("Source Phar $args[0] has been created on $folderPath");
	}
}
