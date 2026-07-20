<?php

namespace Author\PMPluginDemo;

use pocketmine\event\Listener;
use pocketmine\event\player\PlayerJoinEvent;
use pocketmine\event\block\BlockBreakEvent;
use pocketmine\event\EventPriority;
use pocketmine\utils\TextFormat;

class EventListener implements Listener {

    /** @var Main */
    private $plugin;

    public function __construct(Main $plugin) {
        $this->plugin = $plugin;
    }

    /**
     * @EventHandler(priority = EventPriority::NORMAL)
     */
    public function onJoin(PlayerJoinEvent $event) : void {
        $player = $event->getPlayer();
        $msg = $this->plugin->getConfig()->get("join-message", "Welcome!");
        $player->sendMessage(TextFormat::GREEN . $msg);
    }

    /**
     * @EventHandler(priority = EventPriority::HIGHEST)
     */
    public function onBreak(BlockBreakEvent $event) : void {
        $level = $event->getPlayer()->getLevel();
        // 示例：在名为 spawn 的世界中禁止破坏方块
        if ($level !== null && $level->getFolderName() === "spawn") {
            $event->setCancelled();
            $event->getPlayer()->sendMessage(TextFormat::RED . "出生点禁止破坏");
        }
    }
}
