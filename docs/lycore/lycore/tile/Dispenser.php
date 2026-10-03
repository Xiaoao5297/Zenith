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

use lycore\block\Air;
use lycore\block\Block;
use lycore\block\Lava;
use lycore\block\Water;
use lycore\entity\AgeableSpawnHelper;
use lycore\entity\Arrow;
use lycore\entity\Egg;
use lycore\entity\Entity;
use lycore\entity\Item as ItemEntity;
use lycore\entity\Minecart;
use lycore\entity\PrimedTNT;
use lycore\entity\Snowball;
use lycore\entity\ThrownExpBottle;
use lycore\entity\ThrownPotion;
use lycore\inventory\DispenserInventory;
use lycore\inventory\InventoryHolder;
use lycore\item\Armor;
use lycore\item\Arrow as ItemArrow;
use lycore\item\Bucket;
use lycore\item\Item;
use lycore\item\SpawnEgg;
use lycore\level\Level;
use lycore\level\format\FullChunk;
use lycore\level\particle\SmokeParticle;
use lycore\math\Vector3;
use lycore\nbt\NBT;
use lycore\nbt\tag\CompoundTag;
use lycore\nbt\tag\DoubleTag;
use lycore\nbt\tag\FloatTag;
use lycore\nbt\tag\ListTag;
use lycore\nbt\tag\IntTag;
use lycore\nbt\tag\ShortTag;
use lycore\nbt\tag\StringTag;

class Dispenser extends Spawnable implements InventoryHolder, Container, Nameable
{
    /** @var DispenserInventory */
    protected $inventory;

    public function __construct(FullChunk $chunk, CompoundTag $nbt)
    {
        parent::__construct($chunk, $nbt);
        $this->inventory = new DispenserInventory($this);
        if (!isset($this->namedtag->Items) or !($this->namedtag->Items instanceof ListTag)) {
            $this->namedtag->Items = new ListTag("Items", []);
            $this->namedtag->Items->setTagType(NBT::TAG_Compound);
        }
        for ($i = 0; $i < $this->getSize(); ++$i) {
            $this->inventory->setItem($i, $this->getItem($i));
        }
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

    public function saveNBT()
    {
        $this->namedtag->Items = new ListTag("Items", []);
        $this->namedtag->Items->setTagType(NBT::TAG_Compound);
        for ($index = 0; $index < $this->getSize(); ++$index) {
            $this->setItem($index, $this->inventory->getItem($index));
        }
    }

    public function getSize(){ return 9; }

    protected function getSlotIndex($index)
    {
        foreach ($this->namedtag->Items as $i => $slot) {
            if ((int)$slot["Slot"] === (int)$index) {
                return (int)$i;
            }
        }
        return -1;
    }

    public function getItem($index)
    {
        $i = $this->getSlotIndex($index);
        return $i < 0 ? Item::get(Item::AIR, 0, 0) : NBT::getItemHelper($this->namedtag->Items[$i]);
    }

    public function setItem($index, Item $item)
    {
        $i = $this->getSlotIndex($index);
        $d = NBT::putItemHelper($item, $index);
        if ($item->getId() === Item::AIR || $item->getCount() <= 0) {
            if ($i >= 0) unset($this->namedtag->Items[$i]);
        } elseif ($i < 0) {
            for ($i = 0; $i <= $this->getSize(); ++$i) {
                if (!isset($this->namedtag->Items[$i])) break;
            }
            $this->namedtag->Items[$i] = $d;
        } else {
            $this->namedtag->Items[$i] = $d;
        }
        return true;
    }

    public function getInventory(){ return $this->inventory; }

    public function getName() : string
    {
        return isset($this->namedtag->CustomName) ? $this->namedtag->CustomName->getValue() : "Dispenser";
    }
    public function hasName(){ return isset($this->namedtag->CustomName); }
    public function setName($str)
    {
        if ($str === "") {
            unset($this->namedtag->CustomName);
            return;
        }
        $this->namedtag->CustomName = new StringTag("CustomName", $str);
    }

    /* 朝向向量 */
    public function getMotion()
    {
        $meta = $this->getBlock()->getDamage();
        switch ($meta) {
            case 0: return [0, -1, 0];
            case 1: return [0, 1, 0];
            case 2: return [0, 0, -1];
            case 3: return [0, 0, 1];
            case 4: return [-1, 0, 0];
            case 5: return [1, 0, 0];
            default: return [0, 0, 0];
        }
    }

    /* ========== 核心：只保留水桶+刷怪蛋+原版发射 ========== */
    public function activate()
    {
        if ($this->closed || !($this->getLevel() instanceof Level)) {
            return;
        }

        $itemIndex = [];
        for ($i = 0; $i < $this->getSize(); ++$i) {
            $item = $this->getInventory()->getItem($i);
            if ($item->getId() != Item::AIR) $itemIndex[] = [$i, $item];
        }
        $max = count($itemIndex) - 1;
        if ($max < 0) return;
        $itemArr = $max == 0 ? $itemIndex[0] : $itemIndex[mt_rand(0, $max)];

        /** @var Item $item */
        $item  = $itemArr[1];
        $motion = $this->getMotion();
        $face   = $this->getBlock()->getDamage();
        $front  = $this->getLevel()->getBlock($this->getSide($face));

        /* 1. 水桶/岩浆桶/空桶 */
        if ($item instanceof Bucket) {
            $meta = $item->getDamage();
            if ($meta === Bucket::WATER || $meta === Bucket::LAVA) {
                if ($front instanceof Air || $front->canBeReplaced()) {
                    $front->getLevel()->setBlock($front, $meta === Bucket::WATER ? new Water() : new Lava());
                    $this->getInventory()->setItem($itemArr[0], Item::get(Item::BUCKET, 0, 1));
                    return; // 直接退出，不再扣数
                }
            } elseif ($meta === 0) {
                if ($front instanceof Water && $front->getDamage() === 0) {
                    $front->getLevel()->setBlock($front, new Air());
                    $this->getInventory()->setItem($itemArr[0], Item::get(Item::BUCKET, Bucket::WATER, 1));
                    return;
                } elseif ($front instanceof Lava && $front->getDamage() === 0) {
                    $front->getLevel()->setBlock($front, new Air());
                    $this->getInventory()->setItem($itemArr[0], Item::get(Item::BUCKET, Bucket::LAVA, 1));
                    return;
                }
            }
        }

        /* 3. 原版发射：箭、雪球、鸡蛋、喷溅药水、经验瓶、TNT、火焰弹、船、矿车、盔甲、默认掉落 */
        $itemTag = NBT::putItemHelper($item);
        $itemTag->setName("Item");
        $nbt = new CompoundTag("", [
            "Pos" => new ListTag("Pos", [
                new DoubleTag("", $this->x + $motion[0] * 2 + 0.5),
                new DoubleTag("", $this->y + ($motion[1] > 0 ? $motion[1] : 0.5)),
                new DoubleTag("", $this->z + $motion[2] * 2 + 0.5)
            ]),
            "Motion" => new ListTag("Motion", [
                new DoubleTag("", $motion[0]), new DoubleTag("", $motion[1]), new DoubleTag("", $motion[2])
            ]),
            "Rotation" => new ListTag("Rotation", [
                new FloatTag("", lcg_value() * 360), new FloatTag("", 0)
            ]),
            "Health" => new ShortTag("Health", 5),
            "Item" => $itemTag,
            "PickupDelay" => new ShortTag("PickupDelay", 10)
        ]);

        switch ($item->getId()) {
            case Item::ARROW:
                $arrow = new Arrow($this->chunk, $nbt);
                if($item instanceof ItemArrow){
                    $arrow->setArrowItem($item);
                }
                $arrow->setMotion((new Vector3(...$motion))->multiply(1.5));
                $arrow->spawnToAll();
                $success = true;
                break;
            case Item::SPAWN_EGG:
                $pos = $this->add($motion[0] * 1.2 + 0.5, $motion[1] > 0 ? $motion[1] + 0.5 : 0.5, $motion[2] * 1.2 + 0.5);
                $nbt = new CompoundTag("", [
                    "Pos" => new ListTag("Pos", [
                        new DoubleTag("", $pos->x), new DoubleTag("", $pos->y), new DoubleTag("", $pos->z)
                    ]),
                    "Motion" => new ListTag("Motion", [
                        new DoubleTag("", $motion[0]), new DoubleTag("", $motion[1]), new DoubleTag("", $motion[2])
                    ]),
                    "Rotation" => new ListTag("Rotation", [
                        new FloatTag("", lcg_value() * 360), new FloatTag("", 0)
                    ])
                ]);
                $entity = Entity::createEntity($item->getDamage(), $this->chunk, $nbt);
                if ($entity !== null) {
                    AgeableSpawnHelper::maybeSetBaby($entity, AgeableSpawnHelper::PNX_SPAWN_EGG_BABY_CHANCE);
                    $entity->spawnToAll();
                    $success = true;   // 标记成功，让外层扣 1 个
                    break;
                }
            case Item::SNOWBALL:
                $snowball = new Snowball($this->chunk, $nbt);
                $snowball->setMotion((new Vector3(...$motion))->multiply(1.2));
                $snowball->spawnToAll();
                $success = true;
                break;
            case Item::EGG:
                $egg = new Egg($this->chunk, $nbt);
                $egg->setMotion((new Vector3(...$motion))->multiply(1.2));
                $egg->spawnToAll();
                $success = true;
                break;
            case Item::SPLASH_POTION:
                $potion = new ThrownPotion($this->chunk, $nbt);
                $potion->setMotion((new Vector3(...$motion))->multiply(1.2));
                $potion->spawnToAll();
                $success = true;
                break;
            case Item::ENCHANTING_BOTTLE:
                $exp = new ThrownExpBottle($this->chunk, $nbt);
                $exp->setMotion((new Vector3(...$motion))->multiply(1.2));
                $exp->spawnToAll();
                $success = true;
                break;
            default:
                // 盔甲或其他：直接掉落
                $itemEntity = new ItemEntity($this->chunk, $nbt);
                $itemEntity->setMotion((new Vector3(...$motion))->multiply(0.3));
                $itemEntity->spawnToAll();
                $success = true;
                break;
        }

        /* 公共：消耗 & 粒子 */
        if ($success) {
            $item->setCount($item->getCount() - 1);
            $this->getInventory()->setItem($itemArr[0], $item->getCount() > 0 ? $item : Item::get(Item::AIR));
        }
        for ($i = 1; $i < 6; $i++) {
            $this->getLevel()->addParticle(new SmokeParticle($this->add(
                $motion[0] * $i * 0.2 + 0.5,
                $motion[1] == 0 ? 0.5 : $motion[1] * $i * 0.2,
                $motion[2] * $i * 0.2 + 0.5
            )));
        }
    }

    public function getSpawnCompound()
    {
        $c = new CompoundTag("", [
            new StringTag("id", Tile::DISPENSER),
            new IntTag("x", (int)$this->x),
            new IntTag("y", (int)$this->y),
            new IntTag("z", (int)$this->z)
        ]);
        if ($this->hasName()) {
            $c->CustomName = $this->namedtag->CustomName;
        }
        return $c;
    }
}
