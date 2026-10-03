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

namespace lycore\tile;

use lycore\inventory\MinecartChestInventory;
use lycore\inventory\InventoryHolder;
use lycore\item\Item;
use lycore\level\format\FullChunk;
use lycore\math\Vector3;
use lycore\nbt\NBT;

use lycore\nbt\tag\CompoundTag;
use lycore\nbt\tag\ListTag;
use lycore\nbt\tag\IntTag;
use lycore\nbt\tag\StringTag;

use lycore\entity\Entity;
use lycore\entity\MinecartChest as EntityMinecartChest;

class MinecartChest extends Spawnable implements InventoryHolder, Container, Nameable
{
    const INVENTORY_SIZE = 27;

    /** @var MinecartChestInventory */
    public $inventory;
    public $namedtag;
    public $linkentity = null;

    public function __construct(FullChunk $chunk, CompoundTag $nbt)
    {
        parent::__construct($chunk, $nbt);
        $this->inventory = new MinecartChestInventory($this);

        $this->inventory->x = (int)$this->x;
        $this->inventory->y = (int)$this->y;
        $this->inventory->z = (int)$this->z;

        if (!isset($this->namedtag->Items) || !($this->namedtag->Items instanceof ListTag)) {
            $this->namedtag->Items = new ListTag("Items", []);
            $this->namedtag->Items->setTagType(NBT::TAG_Compound);
        }

        $this->loadItemsFromNBT();
    }

    public function close()
    {
        if ($this->closed === false) {
            $inventory = $this->getInventory();
            if ($inventory !== null) {
                foreach ($inventory->getViewers() as $player) {
                    $player->removeWindow($inventory);
                }
            }
            parent::close();
        }
    }

    public function saveNBT()
    {
        parent::saveNBT();
        $itemTags = [];
        foreach ($this->inventory->getContents() as $index => $item) {
            if ($item->getId() !== Item::AIR && $item->getCount() > 0) {
                $itemTags[] = NBT::putItemHelper($item, $index);
            }
        }
        $newItems = new ListTag("Items", $itemTags);
        $newItems->setTagType(NBT::TAG_Compound);
        $this->namedtag->Items = $newItems;
        
        // 新增：强制同步实体坐标到NBT，防止漂移
        if ($this->linkentity !== null && $this->linkentity->isAlive()) {
            $this->x = $this->linkentity->x;
            $this->y = $this->linkentity->y;
            $this->z = $this->linkentity->z;
            $this->namedtag->x = (int)$this->x;
            $this->namedtag->y = (int)$this->y;
            $this->namedtag->z = (int)$this->z;
        }
    }

    public function getSize()
    {
        return self::INVENTORY_SIZE;
    }

    public function getItem($index)
    {
        $index = (int)$index;
        if ($index < 0 || $index >= $this->getSize()) {
            return Item::get(Item::AIR, 0, 0);
        }
        return $this->inventory->getItem($index);
    }

    public function setItem($index, Item $item)
    {
        $index = (int)$index;
        if ($index < 0 || $index >= $this->getSize()) {
            return false;
        }
        if ($item->getId() === Item::AIR || $item->getCount() <= 0) {
            $item = Item::get(Item::AIR, 0, 0);
        }
        $this->inventory->setItem($index, $item);
        $this->saveNBT();
        return true;
    }

    public function getInventory()
    {
        return $this->inventory;
    }

    public function getRealInventory()
    {
        return $this->inventory;
    }

    public function getName() : string
    {
        return "箱子矿车";
    }

    public function hasName()
    {
        return isset($this->namedtag->CustomName) && $this->namedtag->CustomName instanceof StringTag;
    }

    public function setName($str)
    {
        $str = (string)$str;
        if ($str === "") {
            unset($this->namedtag->CustomName);
            return;
        }
        $this->namedtag->CustomName = new StringTag("CustomName", $str);
    }

    public function getSpawnCompound()
    {
        $c = new CompoundTag("", [
            new StringTag("id", Tile::MINECART_CHEST),
            new IntTag("x", (int)$this->x),
            new IntTag("y", (int)$this->y),
            new IntTag("z", (int)$this->z)
        ]);
        if ($this->hasName()) {
            $c->CustomName = $this->namedtag->CustomName;
        }
        return $c;
    }

    public function getItems()
    {
        if (!isset($this->namedtag->Items) || !($this->namedtag->Items instanceof ListTag)) {
            $list = new ListTag("Items", []);
            $list->setTagType(NBT::TAG_Compound);
            return $list;
        }
        return $this->namedtag->Items;
    }

    public function getArrayItems()
    {
        $arr = [];
        for ($i = 0; $i < $this->getSize(); $i++) {
            $item = $this->getItem($i);
            if ($item->getId() !== Item::AIR) {
                $arr[$i] = $item;
            }
        }
        return $arr;
    }

    public function setItems(ListTag $slotitems)
    {
        $this->inventory->clearAll();
        $itemTags = [];
        if ($slotitems instanceof ListTag) {
            foreach ($slotitems as $slot) {
                if (!$slot instanceof CompoundTag) {
                    continue;
                }
                $itemTags[] = $slot;
                $index = isset($slot["Slot"]) ? (int)$slot["Slot"] : -1;
                if ($index >= 0 && $index < $this->getSize()) {
                    try {
                        $this->inventory->setItem($index, NBT::getItemHelper($slot));
                    } catch (\Exception $e) {
                        continue;
                    }
                }
            }
        }
        $newItems = new ListTag("Items", $itemTags);
        $newItems->setTagType(NBT::TAG_Compound);
        $this->namedtag->Items = $newItems;
        return true;
    }

    public function setLink(Entity $entity)
    {
        if ($entity instanceof EntityMinecartChest && $entity->isAlive()) {
            $this->linkentity = $entity;
            // 绑定立即同步坐标，防止初始漂移
            $this->x = $entity->x;
            $this->y = $entity->y;
            $this->z = $entity->z;
            return true;
        }
        return false;
    }

    // 新增：阻止实体存活时Tile随区块卸载（核心防丢失）
    public function onChunkUnload()
    {
        return $this->linkentity !== null && $this->linkentity->isAlive() ? false : parent::onChunkUnload();
    }

    // 新增：强制同步Tile与实体坐标（解决位置漂移）
    public function syncPositionWithEntity()
    {
        if ($this->linkentity !== null && $this->linkentity->isAlive()) {
            $this->x = $this->linkentity->x;
            $this->y = $this->linkentity->y;
            $this->z = $this->linkentity->z;
            $this->namedtag->x = (int)$this->x;
            $this->namedtag->y = (int)$this->y;
            $this->namedtag->z = (int)$this->z;
            $this->inventory->x = (int)$this->x;
            $this->inventory->y = (int)$this->y;
            $this->inventory->z = (int)$this->z;
        }
    }

    private function loadItemsFromNBT()
    {
        $this->inventory->clearAll();
        foreach ($this->namedtag->Items as $slot) {
            if (!$slot instanceof CompoundTag) {
                continue;
            }
            $index = isset($slot["Slot"]) ? (int)$slot["Slot"] : -1;
            if ($index >= 0 && $index < $this->getSize()) {
                try {
                    $this->inventory->setItem($index, NBT::getItemHelper($slot));
                } catch (\Exception $e) {
                    continue;
                }
            }
        }
    }

    protected function getSlotIndex($index)
    {
        $index = (int)$index;
        foreach ($this->namedtag->Items as $i => $slot) {
            if ($slot instanceof CompoundTag && isset($slot["Slot"]) && (int)$slot["Slot"] === $index) {
                return (int)$i;
            }
        }
        return -1;
    }
}
