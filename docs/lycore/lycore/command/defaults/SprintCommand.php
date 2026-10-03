<?php

namespace lycore\command\defaults;

use lycore\command\CommandSender;
use lycore\network\protocol\ProtocolCompatibility;
use lycore\Player;

class SprintCommand extends VanillaCommand{

	public function __construct($name){
		parent::__construct(
			$name,
			"切换0.11自动疾跑",
			"/sprint"
		);
	}

	public function execute(CommandSender $sender, $commandLabel, array $args){
		if(!($sender instanceof Player)){
			$sender->sendMessage("只能在游戏中使用此命令。");
			return true;
		}

		if(!ProtocolCompatibility::isProtocol011((int) $sender->getProtocol())){
			$sender->sendMessage("该命令仅限0.11玩家使用。");
			return true;
		}

		$enabled = $sender->toggleProtocol011AutoSprint();
		$sender->sendMessage($enabled ? "已开启自动疾跑" : "已关闭自动疾跑");
		return true;
	}
}
