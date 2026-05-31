<?php
namespace pocketmine\command\defaults;

use pocketmine\command\Command;
use pocketmine\command\CommandSender;
use pocketmine\event\TranslationContainer;
use pocketmine\Player;
use pocketmine\utils\TextFormat;

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
		return true;
	}
}
