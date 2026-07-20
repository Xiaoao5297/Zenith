<?php

namespace Author\PMPluginDemo;

use pocketmine\plugin\PluginBase;
use pocketmine\event\Listener;
use pocketmine\command\Command;
use pocketmine\command\CommandSender;
use Author\PMPluginDemo\EventListener;
use Author\PMPluginDemo\commands\GreetCommand;
use Author\PMPluginDemo\tasks\BroadcastTask;

class Main extends PluginBase implements Listener {

    public function onEnable() : void {
        // 1) 保存默认配置（resources/config.yml -> 数据目录/config.yml）
        $this->saveDefaultConfig();

        // 2) 注册事件监听器
        $this->getServer()->getPluginManager()->registerEvents(new EventListener($this), $this);

        // 3) 注册命令（PluginCommand 方式）
        $this->getServer()->getCommandMap()->register($this->getName(), new GreetCommand($this));

        // 4) 调度定时任务：每 30 秒（600 tick）广播一次
        $interval = (int) $this->getConfig()->get("broadcast-interval-seconds", 30);
        $this->getScheduler()->scheduleRepeatingTask(new BroadcastTask($this), $interval * 20);

        $this->getLogger()->info("PMPluginDemo 已启用");
    }

    public function onDisable() : void {
        $this->getScheduler()->cancelAllTasks();
        $this->getLogger()->info("PMPluginDemo 已禁用");
    }

    // 命令方式 A：plugin.yml 中声明 "demo"，在此处处理
    public function onCommand(CommandSender $sender, Command $command, $label, array $args) : bool {
        if ($command->getName() === "demo") {
            $sender->sendMessage("这是 onCommand 处理的 /demo 命令");
            return true;
        }
        return false;
    }
}
