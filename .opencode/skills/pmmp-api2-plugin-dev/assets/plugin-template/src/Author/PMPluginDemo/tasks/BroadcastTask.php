<?php

namespace Author\PMPluginDemo\tasks;

use pocketmine\scheduler\PluginTask;
use Author\PMPluginDemo\Main;

class BroadcastTask extends PluginTask {

    /** @var Main */
    private $plugin;

    public function __construct(Main $plugin) {
        parent::__construct($plugin);   // API 2 必须向 PluginTask 传入插件所有者
        $this->plugin = $plugin;
    }

    /**
     * @param int $currentTick
     */
    public function onRun(int $currentTick) : void {
        $msg = $this->plugin->getConfig()->get("broadcast-message", "欢迎来到服务器");
        foreach ($this->plugin->getServer()->getOnlinePlayers() as $player) {
            $player->sendMessage($msg);
        }
    }
}
