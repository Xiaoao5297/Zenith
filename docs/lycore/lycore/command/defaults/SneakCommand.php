<?php

namespace lycore\command\defaults;

use lycore\command\CommandSender;
use lycore\network\protocol\ProtocolCompatibility;
use lycore\Player;

class SneakCommand extends VanillaCommand{

	public function __construct($name){
		parent::__construct(
			$name,
			"切换0.11蹲下状态",
			"/sneak"
		);
	}

	public function execute(CommandSender $sender, $commandLabel, array $args){
		if(!($sender instanceof Player)){
			$sender->sendMessage("§c只能在游戏中使用此命令。");
			return true;
		}

		if(!ProtocolCompatibility::isProtocol011((int) $sender->getProtocol())){
			$sender->sendMessage("§c该命令仅限0.11玩家使用。");
			return true;
		}

		$enabled = $sender->toggleProtocol011Sneak();
		$sender->sendMessage($enabled ? "§a已切换为蹲下状态" : "§c已取消蹲下状态");
		return true;
	}
}
