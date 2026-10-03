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

namespace lycore\inventory;

use lycore\level\Level;
use lycore\network\protocol\BlockEventPacket;
use lycore\Player;
use lycore\math\Vector3;
use lycore\block\TrappedChest;
use lycore\block\Block;

use lycore\tile\MinecartChest;

class MinecartChestInventory extends ContainerInventory
{
    public $x = 0;
    public $y = 0;
    public $z = 0; // 初始化坐标属性，避免未定义警告

    /**
     * 构造方法，初始化库存并同步坐标，保留原逻辑
     * @param MinecartChest $tile
     */
    public function __construct(MinecartChest $tile)
    {
        parent::__construct($tile, InventoryType::get(InventoryType::CHEST));
        // 同步Tile坐标到库存，确保坐标非空
        $this->x = (int)$tile->x;
        $this->y = (int)$tile->y;
        $this->z = (int)$tile->z;
    }

    /**
     * 获取库存持有者（Tile），增加类型校验，保留原逻辑
     * @return MinecartChest
     */
    public function getHolder()
    {
        $holder = parent::getHolder();
        return $holder instanceof MinecartChest ? $holder : null;
    }

    /**
     * 玩家打开库存时触发，优化网络包和陷阱箱逻辑，保留原业务
     * @param Player $who
     */
    public function onOpen(Player $who)
    {
        // 前置校验：玩家在线、持有者有效、坐标合法
        if (!$who->isOnline() || ($holder = $this->getHolder()) === null || $this->x === 0 && $this->y === 0 && $this->z === 0) {
            parent::onOpen($who);
            return;
        }

        parent::onOpen($who);

        // 仅当第一个玩家打开时，发送箱子打开的网络包
        if (count($this->getViewers()) === 1) {
            $pk = new BlockEventPacket();
            $pk->x = $this->x;
            $pk->y = $this->y;
            $pk->z = $this->z;
            $pk->case1 = 1;
            $pk->case2 = 2;
            // 安全获取等级并发送区块包，避免空指针
            if (($level = $holder->getLevel()) instanceof Level) {
                $level->addChunkPacket($this->x >> 4, $this->z >> 4, $pk);
            }
        }

        // 陷阱箱激活逻辑：仅当方块是陷阱箱时处理，增加类型校验
        if (($level = $holder->getLevel()) instanceof Level) {
            $block = $level->getBlock(new Vector3($this->x, $this->y, $this->z));
            if ($block instanceof TrappedChest && !$block->isActivated()) {
                $block->activate();
            }
        }
    }

    /**
     * 玩家关闭库存时触发，优化NBT保存和网络包，保留原业务
     * @param Player $who
     */
    public function onClose(Player $who)
    {
        // 前置校验：玩家在线、持有者有效
        if (!$who->isOnline() || ($holder = $this->getHolder()) === null) {
            parent::onClose($who);
            return;
        }

        // 陷阱箱取消激活逻辑：提前处理，与打开逻辑对称
        if (($level = $holder->getLevel()) instanceof Level) {
            $block = $level->getBlock(new Vector3($this->x, $this->y, $this->z));
            if ($block instanceof TrappedChest && $block->isActivated()) {
                // 仅当无其他观察者时，取消陷阱箱激活
                if (count($this->getViewers()) <= 1) {
                    $block->deactivate();
                }
            }
        }

        // 及时保存NBT，保证物品数据持久化（核心优化）
        $holder->saveNBT();

        // 仅当最后一个玩家关闭时，发送箱子关闭的网络包
        if (count($this->getViewers()) === 1) {
            $pk = new BlockEventPacket();
            $pk->x = $this->x;
            $pk->y = $this->y;
            $pk->z = $this->z;
            $pk->case1 = 1;
            $pk->case2 = 0;
            if (($level = $holder->getLevel()) instanceof Level) {
                $level->addChunkPacket($this->x >> 4, $this->z >> 4, $pk);
            }
        }

        parent::onClose($who);
    }
}