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

/*
 * 移植自 lycore\inventory\MinecartChestInventory。
 * 改为核心既有的虚拟容器方案：库存持有者使用 FakeBlockMenu，
 * 打开/关闭时通过 UpdateBlockPacket 仅在客户端放置/移除箱子方块，
 * 不再像 lycore 那样在服务端世界真实放置箱子。
 */

namespace pocketmine\inventory;

use pocketmine\block\Block;
use pocketmine\entity\MinecartChest as EntityMinecartChest;
use pocketmine\level\Position;
use pocketmine\nbt\NBT;
use pocketmine\nbt\tag\CompoundTag;
use pocketmine\nbt\tag\IntTag;
use pocketmine\nbt\tag\StringTag;
use pocketmine\network\protocol\BlockEntityDataPacket;
use pocketmine\network\protocol\UpdateBlockPacket;
use pocketmine\Player;
use pocketmine\tile\Tile;

class MinecartChestInventory extends CustomInventory
{
    const INVENTORY_SIZE = 27;

    /** @var EntityMinecartChest|null */
    private $minecart;

    public function __construct(Position $pos, EntityMinecartChest $minecart = null, string $title = "箱子矿车")
    {
        $this->minecart = $minecart;
        parent::__construct(new FakeBlockMenu($this, $pos), InventoryType::get(InventoryType::CHEST), [], self::INVENTORY_SIZE, $title);
    }

    /**
     * @return FakeBlockMenu
     */
    public function getHolder()
    {
        return $this->holder;
    }

    public function getMinecart()
    {
        return $this->minecart;
    }

    public function onOpen(Player $who)
    {
        $holder = $this->getHolder();
        if ($holder instanceof Position) {
            $pk = new UpdateBlockPacket();
            $pk->records[] = [$holder->getX(), $holder->getZ(), $holder->getY(), Block::CHEST, 0, UpdateBlockPacket::FLAG_ALL];
            $who->dataPacket($pk);
            $this->sendChestTitle($who, $holder);
        }

        parent::onOpen($who);
    }

    public function onClose(Player $who)
    {
        parent::onClose($who);

        if($this->minecart !== null){
            $this->minecart->saveInventory();
        }

        $holder = $this->getHolder();
        if ($holder instanceof Position) {
            $pk = new UpdateBlockPacket();
            $pk->records[] = [$holder->getX(), $holder->getZ(), $holder->getY(), Block::AIR, 0, UpdateBlockPacket::FLAG_ALL];
            $who->dataPacket($pk);
        }
    }

    private function sendChestTitle(Player $who, Position $holder)
    {
        $pk = new BlockEntityDataPacket();
        $pk->x = $holder->getX();
        $pk->y = $holder->getY();
        $pk->z = $holder->getZ();

        $nbt = new NBT(NBT::LITTLE_ENDIAN);
        $nbt->setData(new CompoundTag("", [
            new StringTag("id", Tile::CHEST),
            new IntTag("x", $holder->getX()),
            new IntTag("y", $holder->getY()),
            new IntTag("z", $holder->getZ()),
            new StringTag("CustomName", $this->getTitle())
        ]));
        $pk->namedtag = $nbt->write();

        $who->dataPacket($pk);
    }
}
