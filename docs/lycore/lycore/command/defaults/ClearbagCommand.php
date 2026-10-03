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
use lycore\event\TranslationContainer;//原注释
use lycore\Player;
use lycore\utils\TextFormat;
use lycore\inventory\Inventory;
use lycore\item\Item;

class ClearbagCommand extends VanillaCommand{

    public function __construct($name){
        parent::__construct(
            $name,
            "清空指定玩家的背包", 
            "/clearbag <玩家名>", 
            []
        );
        $this->setPermission("command.kill");
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
        $targetPlayer->sendMessage(TextFormat::RED . "你的背包已被管理员清空！");
        $sender->sendMessage(TextFormat::GOLD . "成功清空玩家「{$targetPlayer->getName()}」的背包！");

        return true;
    }

} 