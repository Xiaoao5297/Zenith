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

use lycore\block\Hopper as HopperBlock;
use lycore\entity\Item as DroppedItem;
use lycore\inventory\HopperInventory;
use lycore\inventory\InventoryHolder;
use lycore\item\Item;
use lycore\level\format\FullChunk;
use lycore\math\AxisAlignedBB;
use lycore\math\Vector3;
use lycore\nbt\NBT;
use lycore\nbt\tag\CompoundTag;
use lycore\nbt\tag\IntTag;
use lycore\nbt\tag\ListTag;
use lycore\nbt\tag\StringTag;

class Hopper extends Spawnable implements InventoryHolder, Container, Nameable
{
    /** @var HopperInventory */
    protected $inventory;

    /** @var bool */
    protected $isLocked = false;

    /** @var bool */
    protected $isPowered = false;

    public function __construct(FullChunk $chunk, CompoundTag $nbt)
    {
        parent::__construct($chunk, $nbt);
        $this->inventory = new HopperInventory($this);

        if (!isset($this->namedtag->Items) or !($this->namedtag->Items instanceof ListTag)) {
            $this->namedtag->Items = new ListTag("Items", []);
            $this->namedtag->Items->setTagType(NBT::TAG_Compound);
        }

        for ($i = 0; $i < $this->getSize(); ++$i) {
            $this->inventory->setItem($i, $this->getItem($i));
        }
        $this->namedtag->TransferCooldown = new IntTag("TransferCooldown", 0);

        $this->scheduleUpdate();
    }

    public function close()
    {
        if ($this->closed === false) {
            foreach ($this->getInventory()->getViewers() as $player) {
                $player->removeWindow($this->getInventory());
            }
            parent::close();
        }
    }

    public function activate()
    {
        $this->isPowered = true;
    }

    public function deactivate()
    {
        $this->isPowered = false;
    }

    public function canUpdate()
    {
        return $this->namedtag->TransferCooldown->getValue() === 0 and !$this->isPowered;
    }

    public function resetCooldownTicks()
    {
        $this->namedtag->TransferCooldown->setValue(8);
    }

    private function pickupDroppedItems(): void
    {
        // 1. 范围：漏斗口中心水平 ±0.7、上方 0.6
        $pos = $this->getBlock()->add(0.5, 0.5, 0.5);
        $bb  = new AxisAlignedBB(
            $pos->x - 0.7, $pos->y - 0.1, $pos->z - 0.7,
            $pos->x + 0.7, $pos->y + 0.6, $pos->z + 0.7
        );

        $absorbedAny = false;   // 2. 标记本轮是否吸到过东西

        foreach ($this->getLevel()->getNearbyEntities($bb) as $entity) {
            if (!($entity instanceof DroppedItem) || $entity->closed) {
                continue;
            }

            $item = $entity->getItem();
            if ($item->getId() === Item::AIR || $item->getCount() <= 0) {
                continue;
            }

            // 3. 整叠直接塞
            $left = $this->inventory->addItem(clone $item);

            // 完全没塞进去
            if (!empty($left) && $left[0]->getCount() === $item->getCount()) {
                continue;
            }

            // 4. 至少成功一部分
            $absorbedAny = true;

            if (empty($left)) {
                // 整叠清空
                $entity->close();
            } else {
                // 剩余部分重新掉落
                $remain = $left[0];
                $entity->close();
                $this->getLevel()->dropItem($entity->add(0, 0.1, 0), $remain);
            }
        }

        // 5. 只要吸过任何一叠，才进入 8 tick 冷却
        if ($absorbedAny) {
            $this->resetCooldownTicks();
        }
    }

    public function onUpdate()
    {
        if (!($this->getBlock() instanceof HopperBlock)) {
            return false;
        }

        if ($this->isPowered) {
            return true;
        }

        // 每 tick 都先尝试吸掉落物（无冷却限制）
        $this->pickupDroppedItems();

        if (!$this->canUpdate()) {
            $cooldown = $this->namedtag->TransferCooldown->getValue();
            $this->namedtag->TransferCooldown->setValue($cooldown > 0 ? $cooldown - 1 : 0);
            return true;
        }

        // 从上方容器拉取（原有逻辑）
        $source = $this->getLevel()->getTile($this->getBlock()->getSide(Vector3::SIDE_UP));
        if ($source instanceof Tile and $source instanceof InventoryHolder) {
            $inventory = $source->getInventory();
            $item      = clone $inventory->getItem($inventory->firstOccupied());
            $item->setCount(1);
            if ($this->inventory->canAddItem($item)) {
                $this->inventory->addItem($item);
                $inventory->removeItem($item);
                $this->resetCooldownTicks();
                if ($source instanceof Hopper) {
                    $source->resetCooldownTicks();
                }
            }
        }

        // 向朝向容器推送（原有逻辑）
        if (!($this->getLevel()->getTile($this->getBlock()->getSide(Vector3::SIDE_DOWN)) instanceof Hopper)) {
            $target = $this->getLevel()->getTile($this->getBlock()->getSide($this->getBlock()->getDamage()));
            if ($target instanceof Tile and $target instanceof InventoryHolder) {
                $inv = $target->getInventory();
                foreach ($this->inventory->getContents() as $item) {
                    if ($item->getId() === Item::AIR || $item->getCount() < 1) {
                        continue;
                    }
                    $targetItem = clone $item;
                    $targetItem->setCount(1);
                    if ($inv->canAddItem($targetItem)) {
                        $inv->addItem($targetItem);
                        $this->inventory->removeItem($targetItem);
                        $this->resetCooldownTicks();
                        if ($target instanceof Hopper) {
                            $target->resetCooldownTicks();
                        }
                        break;
                    }
                }
            }
        }

        return true;
    }

    /* ===================== 以下均为原版未改动 ===================== */

    public function getInventory()
    {
        return $this->inventory;
    }

    public function getSize()
    {
        return 5;
    }

    public function getItem($index)
    {
        $i = $this->getSlotIndex($index);
        if ($i < 0) {
            return Item::get(Item::AIR, 0, 0);
        } else {
            return NBT::getItemHelper($this->namedtag->Items[$i]);
        }
    }

    public function setItem($index, Item $item)
    {
        $i = $this->getSlotIndex($index);
        $d = NBT::putItemHelper($item, $index);

        if ($item->getId() === Item::AIR || $item->getCount() <= 0) {
            if ($i >= 0) {
                unset($this->namedtag->Items[$i]);
            }
        } elseif ($i < 0) {
            for ($i = 0; $i < $this->getSize(); ++$i) {
                if (!isset($this->namedtag->Items[$i])) {
                    break;
                }
            }
            $this->namedtag->Items[$i] = $d;
        } else {
            $this->namedtag->Items[$i] = $d;
        }
        return true;
    }

    protected function getSlotIndex($index)
    {
        foreach ($this->namedtag->Items as $i => $slot) {
            if ((int) $slot["Slot"] === (int) $index) {
                return (int) $i;
            }
        }
        return -1;
    }

    public function saveNBT()
    {
        $this->namedtag->Items = new ListTag("Items", []);
        $this->namedtag->Items->setTagType(NBT::TAG_Compound);
        for ($index = 0; $index < $this->getSize(); ++$index) {
            $this->setItem($index, $this->inventory->getItem($index));
        }
    }

    public function getName(): string
    {
        return isset($this->namedtag->CustomName) ? $this->namedtag->CustomName->getValue() : "Hopper";
    }

    public function hasName()
    {
        return isset($this->namedtag->CustomName);
    }

    public function setName($str)
    {
        if ($str === "") {
            unset($this->namedtag->CustomName);
            return;
        }
        $this->namedtag->CustomName = new StringTag("CustomName", $str);
    }

    public function hasLock()
    {
        return isset($this->namedtag->Lock);
    }

    public function setLock(string $itemName = "")
    {
        if ($itemName === "") {
            unset($this->namedtag->Lock);
            return;
        }
        $this->namedtag->Lock = new StringTag("Lock", $itemName);
    }

    public function checkLock(string $key)
    {
        return $this->namedtag->Lock->getValue() === $key;
    }

    public function getSpawnCompound()
    {
        $c = new CompoundTag("", [
            new StringTag("id", Tile::HOPPER),
            new IntTag("x", (int) $this->x),
            new IntTag("y", (int) $this->y),
            new IntTag("z", (int) $this->z)
        ]);
        if ($this->hasName()) {
            $c->CustomName = $this->namedtag->CustomName;
        }
        if ($this->hasLock()) {
            $c->Lock = $this->namedtag->Lock;
        }
        return $c;
    }
}
