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
use lycore\network\protocol\Info;
use lycore\plugin\Plugin;
use lycore\utils\TextFormat;

class VersionCommand extends VanillaCommand{

	public function __construct($name){
		parent::__construct(
			$name,
			"%pocketmine.command.version.description",
			"%pocketmine.command.version.usage",
			["ver", "about"]
		);
		$this->setPermission("pocketmine.command.version");
	}

	public function execute(CommandSender $sender, $currentAlias, array $args){
		if(!$this->testPermission($sender)){
			return \true;
		}

		if(\count($args) === 0){
			$sender->sendMessage("§e╔══════════════════════════════════╗");
			$sender->sendMessage("§e║   §bLY Core §7- §dMinecraft PE 服务端核心   §e║");
			$sender->sendMessage("§e╠══════════════════════════════════╣");
			$sender->sendMessage("§e║ §a构建标签  §f│ §bv1.2                 §e║");
			$sender->sendMessage("§e║ §a运行系统  §f│ §b". PHP_OS . str_repeat(" ", 22 - strlen(PHP_OS)) . "§e║");
			$sender->sendMessage("§e║ §a兼容版本  §f│ §60.11 ~ 0.15              §e║");
			$sender->sendMessage("§e║ §a传输协议  §f│ §641~46, 60, 70             §e║");
			$sender->sendMessage("§e║ §cPHP 引擎  §f│ §c".PHP_VERSION." / ".(PHP_INT_SIZE * 8)."bit". str_repeat(" ", 17 - strlen(PHP_VERSION) - strlen((string)(PHP_INT_SIZE * 8))) . "§e║");
			$sender->sendMessage("§e║ §d接口规范  §f│ §dAPI 2.0.0                §e║");
			$sender->sendMessage("§e║ §e项目维护  §f│ §eluoyue                   §e║");
			$sender->sendMessage("§e╚══════════════════════════════════╝");
		}else{
			$pluginName = \implode(" ", $args);
			$exactPlugin = $sender->getServer()->getPluginManager()->getPlugin($pluginName);

			if($exactPlugin instanceof Plugin){
				$this->describeToSender($exactPlugin, $sender);

				return \true;
			}

			$found = \false;
			$pluginName = \strtolower($pluginName);
			foreach($sender->getServer()->getPluginManager()->getPlugins() as $plugin){
				if(\stripos($plugin->getName(), $pluginName) !== \false){
					$this->describeToSender($plugin, $sender);
					$found = \true;
				}
			}

			if(!$found){
				$sender->sendMessage(new TranslationContainer("pocketmine.command.version.noSuchPlugin"));
			}
		}

		return \true;
	}

	private function describeToSender(Plugin $plugin, CommandSender $sender){
		$desc = $plugin->getDescription();
		$sender->sendMessage(TextFormat::DARK_GREEN . $desc->getName() . TextFormat::WHITE . " version " . TextFormat::DARK_GREEN . $desc->getVersion());

		if($desc->getDescription() != \null){
			$sender->sendMessage($desc->getDescription());
		}

		if($desc->getWebsite() != \null){
			$sender->sendMessage("Website: " . $desc->getWebsite());
		}

		if(\count($authors = $desc->getAuthors()) > 0){
			if(\count($authors) === 1){
				$sender->sendMessage("Author: " . \implode(", ", $authors));
			}else{
				$sender->sendMessage("Authors: " . \implode(", ", $authors));
			}
		}
	}
}
