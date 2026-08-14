<?php

namespace pocketmine\command\defaults;

use pocketmine\command\Command;
use pocketmine\command\CommandSender;
use pocketmine\event\TranslationContainer;//原注释
use pocketmine\Player;
use pocketmine\utils\TextFormat;
use pocketmine\inventory\Inventory;
use pocketmine\item\Item;

class ClearbagCommand extends VanillaCommand{

    public function __construct($name){
        parent::__construct(
            $name,
            "清空指定玩家的背包", 
            "/clearbag <玩家名>", 
            []
        );
        $this->setPermission("pocketmine.command.clearbag");
    }

    public function execute(CommandSender $sender, $currentAlias, array $args){
        if(!$this->testPermission($sender)){
            return true;
        }

        if(empty($args)){
            $sender->sendMessage(TextFormat::RED . "用法错误！正确格式：/clearbag <玩家名>");
            return false;
        }

        $targetName = array_shift($args);
        $targetPlayer = $sender->getServer()->getPlayer($targetName);
        if($targetPlayer === null || !$targetPlayer->isOnline()){
            $sender->sendMessage(TextFormat::RED . "错误：玩家「{$targetName}」不存在或未在线！");
            return false;
        }

        $targetPlayer->getInventory()->setContents([]);
        $targetPlayer->sendMessage(TextFormat::GRAY . "你的背包已被管理员清空！");
        $sender->sendMessage(TextFormat::GREEN . "成功清空玩家「{$targetPlayer->getName()}」的背包！");

        return true;
    }

} 