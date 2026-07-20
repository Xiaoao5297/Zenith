<?php

namespace Author\PMPluginDemo\commands;

use pocketmine\command\PluginCommand;
use pocketmine\command\CommandSender;
use pocketmine\Player;
use pocketmine\utils\TextFormat;
use Author\PMPluginDemo\Main;

class GreetCommand extends PluginCommand {

    /** @var Main */
    private $plugin;

    public function __construct(Main $plugin) {
        parent::__construct("greet", $plugin);
        $this->setDescription("向玩家问好");
        $this->setUsage("/greet [名字]");
        $this->setAliases(["hi", "hello"]);
        $this->setPermission("pmplugindemo.command.greet");
        $this->plugin = $plugin;
    }

    public function execute(CommandSender $sender, $commandLabel, array $args) : bool {
        if (!$this->testPermission($sender)) {
            return false;
        }
        if (!$sender instanceof Player) {
            $sender->sendMessage(TextFormat::RED . "请在游戏内使用");
            return false;
        }
        $name = isset($args[0]) ? $args[0] : $sender->getName();
        $sender->sendMessage(TextFormat::GREEN . "你好，" . $name . "！");
        return true;
    }
}
