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
 * 移植自 lycore\entity\MinecartChest，命名空间改为 pocketmine\entity。
 * 当前核心没有 Entity::syncRiderPositionToVehicle()/preloadRiderChunks()，
 * 也没有 Vehicle::applyRailEffects()/Rail::getRealMeta()，
 * 故移除对应调用，铁轨类型改用 Rail::getDamage()。
 */

namespace pocketmine\entity;

use pocketmine\block\Block;
use pocketmine\block\Rail;
use pocketmine\math\Math;
use pocketmine\math\Vector3;
use pocketmine\event\entity\EntityDamageEvent;
use pocketmine\network\protocol\AddEntityPacket;
use pocketmine\item\Item as ItemItem;
use pocketmine\level\Position;
use pocketmine\Player;
use pocketmine\nbt\NBT;
use pocketmine\nbt\tag\CompoundTag;
use pocketmine\nbt\tag\ListTag;
use pocketmine\network\protocol\EntityEventPacket;
use pocketmine\tile\Hopper as HopperTile;
use pocketmine\tile\Furnace as FurnaceTile;
use pocketmine\inventory\MinecartChestInventory;

class MinecartChest extends Vehicle {
    const NETWORK_ID = 98;

    const TYPE_NORMAL = 1;
    const TYPE_CHEST = 2;
    const TYPE_HOPPER = 3;
    const TYPE_TNT = 4;
    const INVENTORY_SIZE = 27;

    const STATE_INITIAL = 0;
    const STATE_ON_RAIL = 1;
    const STATE_OFF_RAIL = 2;

    public $height = 0.7;
    public $width = 0.98;

    public $drag = 0.1;
    public $gravity = 0.5;

    public $isMoving = false;
    public $moveSpeed = 0.5;
    public $linkplayer = null; // 初始化：避免null未定义

    private $state = MinecartChest::STATE_INITIAL;
    private $direction = -1;
    private $moveVector = [];
    public $motionX = 0.0; // 优化：浮点型初始化更规范
    public $motionY = 0.0;
    public $motionZ = 0.0;
    
    public $chestinventory = null;//虚拟箱子库存

    /**
     * 初始化实体，修复未赋值属性，保留原逻辑
     */
    public function initEntity(){
        $this->setMaxHealth(1);
        $this->setHealth(1);
        // 初始化移动向量，保留原方向映射
        $this->moveVector[Entity::NORTH] = new Vector3(-1, 0, 0);
        $this->moveVector[Entity::SOUTH] = new Vector3(1, 0, 0);
        $this->moveVector[Entity::EAST] = new Vector3(0, 0, -1);
        $this->moveVector[Entity::WEST] = new Vector3(0, 0, 1);
        parent::initEntity();
    }

    /**
     * 获取实体名称，保留原返回值
     * @return string
     */
    public function getName(): string{
        return "Minecart Chest";
    }
    
    /**
     * 获取虚拟箱子库存
     * @return mixed
     */
    public function getTile(){
        return $this->chestinventory;
    }

    /**
     * 修复致命BUG：TYPE_INT → TYPE_CHEST，保留方法结构
     * @return int
     */
    public function getType(): int{
        return self::TYPE_CHEST;
    }

    /**
     * 实体更新主方法，保留所有原逻辑，无修改
     * @param int $currentTick
     * @return bool
     */
    public function onUpdate($currentTick){
        if ($this->closed !== false) {
            return false;
        }

        $tickDiff = $currentTick - $this->lastUpdate;
        if ($tickDiff <= 1) {
            return false;
        }
		
        $this->lastUpdate = $currentTick;

        $this->timings->startTiming();

        $hasUpdate = false;
        
        if ($this->state === MinecartChest::STATE_INITIAL){
            $this->checkIfOnRail();
        }elseif($this->state === MinecartChest::STATE_ON_RAIL){
            $hasUpdate = $this->forwardOnRail($this);
            $this->updateMovement();
        }
        if($this->isAlive()){
            if($this->transferFuelToFurnaceBelow()){
                $hasUpdate = true;
            }elseif($this->transferResultFromFurnaceBelow()){
                $hasUpdate = true;
            }elseif($this->transferToHopperBelow()){
                $hasUpdate = true;
            }
        }
        
        $this->timings->stopTiming();

        return $hasUpdate or !$this->onGround or abs($this->motionX) > 0.00001 or abs($this->motionY) > 0.00001 or abs($this->motionZ) > 0.00001;
    }

    private function transferToHopperBelow(){
        $hopper = $this->getHopperBelow();
        if(!$hopper instanceof HopperTile){
            return false;
        }

        if(!$hopper->canUpdate()){
            return true;
        }

        if($this->chestinventory !== null){
            $inventory = $this->chestinventory;
            foreach($inventory->getContents() as $item){
                if($item->getId() === ItemItem::AIR or $item->getCount() <= 0){
                    continue;
                }

                $transfer = clone $item;
                $transfer->setCount(1);
                if($hopper->getInventory()->canAddItem($transfer)){
                    $hopper->getInventory()->addItem($transfer);
                    $inventory->removeItem($transfer);
                    $this->syncNamedTagItemsFromInventory($inventory);
                    $hopper->resetCooldownTicks();
                }
                return true;
            }
            return true;
        }

        $this->ensureEntityItemList();
        foreach($this->namedtag->Items as $index => $slot){
            if(!$slot instanceof CompoundTag){
                continue;
            }

            $item = NBT::getItemHelper($slot);
            if($item->getId() === ItemItem::AIR or $item->getCount() <= 0){
                continue;
            }

            $transfer = clone $item;
            $transfer->setCount(1);
            if($hopper->getInventory()->canAddItem($transfer)){
                $hopper->getInventory()->addItem($transfer);
                $item->setCount($item->getCount() - 1);
                if($item->getCount() <= 0){
                    unset($this->namedtag->Items[$index]);
                }else{
                    $slotId = isset($slot->Slot) ? (int) $slot["Slot"] : (int) $index;
                    $this->namedtag->Items[$index] = NBT::putItemHelper($item, $slotId);
                }
                $hopper->resetCooldownTicks();
            }
            return true;
        }

        return true;
    }

    private function transferResultFromFurnaceBelow(){
        $furnace = $this->getFurnaceBelow();
        if(!$furnace instanceof FurnaceTile){
            return false;
        }

        $result = $furnace->getInventory()->getResult();
        if($result->getId() === ItemItem::AIR or $result->getCount() <= 0){
            return true;
        }

        $transfer = clone $result;
        $transfer->setCount(1);

        if($this->chestinventory !== null){
            $inventory = $this->chestinventory;
            if($inventory->canAddItem($transfer)){
                $inventory->addItem($transfer);
                $this->removeOneFurnaceResult($furnace, $result);
                $this->syncNamedTagItemsFromInventory($inventory);
            }
            return true;
        }

        $this->ensureEntityItemList();
        if($this->addItemToEntityItemList($transfer)){
            $this->removeOneFurnaceResult($furnace, $result);
        }

        return true;
    }

    private function removeOneFurnaceResult(FurnaceTile $furnace, ItemItem $result){
        $result->setCount($result->getCount() - 1);
        if($result->getCount() <= 0){
            $result = ItemItem::get(ItemItem::AIR, 0, 0);
        }

        $furnace->getInventory()->setResult($result);
        $furnace->saveNBT();
    }

    private function transferFuelToFurnaceBelow(){
        $furnace = $this->getFurnaceBelow();
        if(!$furnace instanceof FurnaceTile){
            return false;
        }

        if($this->chestinventory !== null){
            $inventory = $this->chestinventory;
            foreach($inventory->getContents() as $item){
                if(!$this->canFuelBeInserted($furnace, $item)){
                    continue;
                }

                $this->insertFuelIntoFurnace($furnace, $item);
                $transfer = clone $item;
                $transfer->setCount(1);
                $inventory->removeItem($transfer);
                $this->syncNamedTagItemsFromInventory($inventory);
                return true;
            }

            return false;
        }

        $this->ensureEntityItemList();
        foreach($this->namedtag->Items as $index => $slot){
            if(!$slot instanceof CompoundTag){
                continue;
            }

            $item = NBT::getItemHelper($slot);
            if(!$this->canFuelBeInserted($furnace, $item)){
                continue;
            }

            $this->insertFuelIntoFurnace($furnace, $item);
            $this->removeOneItemFromEntitySlot($index, $slot, $item);
            return true;
        }

        return false;
    }

    private function canFuelBeInserted(FurnaceTile $furnace, ItemItem $item){
        if($item->getId() === ItemItem::AIR or $item->getCount() <= 0 or $item->getFuelTime() === null){
            return false;
        }

        $fuelSlot = $furnace->getInventory()->getFuel();
        if($fuelSlot->getId() === ItemItem::AIR or $fuelSlot->getCount() <= 0){
            return true;
        }

        return $fuelSlot->equals($item, true, true) and $fuelSlot->getCount() < $fuelSlot->getMaxStackSize();
    }

    private function insertFuelIntoFurnace(FurnaceTile $furnace, ItemItem $fuel){
        $fuelSlot = $furnace->getInventory()->getFuel();
        if($fuelSlot->getId() === ItemItem::AIR or $fuelSlot->getCount() <= 0){
            $newFuel = clone $fuel;
            $newFuel->setCount(1);
            $furnace->getInventory()->setFuel($newFuel);
        }else{
            $fuelSlot->setCount($fuelSlot->getCount() + 1);
            $furnace->getInventory()->setFuel($fuelSlot);
        }

        $furnace->saveNBT();
    }

    private function removeOneItemFromEntitySlot($index, CompoundTag $slot, ItemItem $item){
        $item->setCount($item->getCount() - 1);
        if($item->getCount() <= 0){
            unset($this->namedtag->Items[$index]);
            return;
        }

        $slotId = isset($slot->Slot) ? (int) $slot["Slot"] : (int) $index;
        $this->namedtag->Items[$index] = NBT::putItemHelper($item, $slotId);
    }

    private function getFurnaceBelow(){
        $level = $this->getLevel();
        if($level === null){
            return null;
        }

        $tile = $level->getTile(new Vector3($this->getFloorX(), $this->getFloorY() - 1, $this->getFloorZ()));
        if($tile instanceof FurnaceTile){
            return $tile;
        }

        return null;
    }

    private function getHopperBelow(){
        $level = $this->getLevel();
        if($level === null){
            return null;
        }

        $x = $this->getFloorX();
        $z = $this->getFloorZ();
        for($offset = 1; $offset <= 2; ++$offset){
            $tile = $level->getTile(new Vector3($x, $this->getFloorY() - $offset, $z));
            if($tile instanceof HopperTile){
                return $tile;
            }
        }

        return null;
    }

    private function ensureEntityItemList(){
        if(!isset($this->namedtag->Items) or !($this->namedtag->Items instanceof ListTag)){
            $this->namedtag->Items = new ListTag("Items", []);
            $this->namedtag->Items->setTagType(NBT::TAG_Compound);
        }
    }

    private function addItemToEntityItemList(ItemItem $item){
        if($item->getId() === ItemItem::AIR or $item->getCount() <= 0){
            return false;
        }

        $occupiedSlots = [];
        foreach($this->namedtag->Items as $index => $slot){
            if(!$slot instanceof CompoundTag){
                continue;
            }

            $slotId = isset($slot->Slot) ? (int) $slot["Slot"] : (int) $index;
            $existing = NBT::getItemHelper($slot);
            if($existing->getId() === ItemItem::AIR or $existing->getCount() <= 0){
                continue;
            }

            $occupiedSlots[$slotId] = true;
            if($existing->equals($item, true, true) and $existing->getCount() < $existing->getMaxStackSize()){
                $existing->setCount($existing->getCount() + 1);
                $this->namedtag->Items[$index] = NBT::putItemHelper($existing, $slotId);
                return true;
            }
        }

        for($slotId = 0; $slotId < self::INVENTORY_SIZE; ++$slotId){
            if(!isset($occupiedSlots[$slotId])){
                $newItem = clone $item;
                $newItem->setCount(1);
                for($index = 0; isset($this->namedtag->Items[$index]); ++$index){
                }
                $this->namedtag->Items[$index] = NBT::putItemHelper($newItem, $slotId);
                return true;
            }
        }

        return false;
    }

    private function syncNamedTagItemsFromInventory($inventory){
        $items = [];
        foreach($inventory->getContents() as $slot => $item){
            if($item->getId() !== ItemItem::AIR and $item->getCount() > 0){
                $items[] = NBT::putItemHelper($item, $slot);
            }
        }

        $this->namedtag->Items = new ListTag("Items", $items);
        $this->namedtag->Items->setTagType(NBT::TAG_Compound);
    }

    /**
     * 将当前虚拟箱子库存写回实体 NBT（供库存关闭时调用）
     */
    public function saveInventory(){
        if($this->chestinventory !== null){
            $this->syncNamedTagItemsFromInventory($this->chestinventory);
        }
    }

    /**
     * 检测是否在铁轨上，保留原逻辑，无修改
     */
    private function checkIfOnRail(){
        for ($y = -1; $y !== 2 and $this->state === MinecartChest::STATE_INITIAL; $y++) {
            $positionToCheck = $this->temporalVector->setComponents($this->x, $this->y + $y, $this->z);
            $block = $this->level->getBlock($positionToCheck);
            if ($this->isRail($block)) {
                $minecartPosition = $positionToCheck->floor()->add(0.5, 0.75, 0.5);
                $this->setPosition($minecartPosition);    // Move minecart to center of rail
                $this->state = MinecartChest::STATE_ON_RAIL;
            }
        }
        if ($this->state !== MinecartChest::STATE_ON_RAIL) {
            $this->state = MinecartChest::STATE_OFF_RAIL;
        }
    }

    /**
     * 判断是否为铁轨方块，保留原逻辑，无修改
     * @param Block $rail
     * @return bool
     */
    private function isRail(Block $rail){
        return ($rail !== null and in_array($rail->getId(), [Block::RAIL, Block::ACTIVATOR_RAIL, Block::DETECTOR_RAIL, Block::POWERED_RAIL]));
    }

    /**
     * 获取当前所在铁轨，保留原逻辑，无修改
     * @return Block|null
     */
    private function getCurrentRail(){
        $block = $this->getLevel()->getBlock($this);
        if ($this->isRail($block)) {
            return $block;
        }
        // Rail could be one block below descending down
        $down = $this->temporalVector->setComponents($this->x, $this->y - 1, $this->z);
        $block = $this->getLevel()->getBlock($down);
        if ($this->isRail($block)) {
            return $block;
        }
        return null;
    }

    /**
     * 沿铁轨前进，保留原逻辑，无修改
     * @param MinecartChest $player
     * @return bool
     */
    private function forwardOnRail(MinecartChest $player){
        if ($this->direction === -1) {
            $candidateDirection = $player->getDirection();
        } else {
            $candidateDirection = $this->direction;
        }
        $rail = $this->getCurrentRail();
        if ($rail !== null) {
            $railType = $rail->getDamage();
            $nextDirection = $this->getDirectionToMove($railType, $candidateDirection);
            if ($nextDirection !== -1) {
                $this->direction = $nextDirection;
                $moved = $this->checkForVertical($railType, $nextDirection);
                if (!$moved) {
                    return $this->moveIfRail();
                } else {
                    return true;
                }
            } else {
                $this->direction = -1;  // Was not able to determine direction to move, so wait for player to look in valid direction
            }
        } else {
            // Not able to find rail
            $this->state = MinecartChest::STATE_INITIAL;
        }

        return false;
    }

    /**
     * 获取移动方向，保留原逻辑，无修改
     * @param int $railType
     * @param int $candidateDirection
     * @return int
     */
    private function getDirectionToMove($railType, $candidateDirection){
        switch ($railType) {
            case Rail::STRAIGHT_NORTH_SOUTH:
            case Rail::SLOPED_ASCENDING_NORTH:
            case Rail::SLOPED_ASCENDING_SOUTH:
                switch ($candidateDirection) {
                    case Entity::NORTH:
                    case Entity::SOUTH:
                        return $candidateDirection;
                }
                break;
            case Rail::STRAIGHT_EAST_WEST:
            case Rail::SLOPED_ASCENDING_EAST:
            case Rail::SLOPED_ASCENDING_WEST:
                switch ($candidateDirection) {
                    case Entity::WEST:
                    case Entity::EAST:
                        return $candidateDirection;
                }
                break;
            case Rail::CURVED_SOUTH_EAST:
                switch ($candidateDirection) {
                    case Entity::SOUTH:
                    case Entity::EAST:
                        return $candidateDirection;
                    case Entity::NORTH:
                        return $this->checkForTurn($candidateDirection, Entity::EAST);
                    case Entity::WEST:
                        return $this->checkForTurn($candidateDirection, Entity::SOUTH);
                }
                break;
            case Rail::CURVED_SOUTH_WEST:
                switch ($candidateDirection) {
                    case Entity::SOUTH:
                    case Entity::WEST:
                        return $candidateDirection;
                    case Entity::NORTH:
                        return $this->checkForTurn($candidateDirection, Entity::WEST);
                    case Entity::EAST:
                        return $this->checkForTurn($candidateDirection, Entity::SOUTH);
                }
                break;
            case Rail::CURVED_NORTH_WEST:
                switch ($candidateDirection) {
                    case Entity::NORTH:
                    case Entity::WEST:
                        return $candidateDirection;
                    case Entity::SOUTH:
                        return $this->checkForTurn($candidateDirection, Entity::WEST);
                    case Entity::EAST:
                        return $this->checkForTurn($candidateDirection, Entity::NORTH);

                }
                break;
            case Rail::CURVED_NORTH_EAST:
                switch ($candidateDirection) {
                    case Entity::NORTH:
                    case Entity::EAST:
                        return $candidateDirection;
                    case Entity::SOUTH:
                        return $this->checkForTurn($candidateDirection, Entity::EAST);
                    case Entity::WEST:
                        return $this->checkForTurn($candidateDirection, Entity::NORTH);
                }
                break;
        }
        return -1;
    }

    /**
     * 处理铁轨转弯，修复WEST分支重复赋值语法错误，保留原业务逻辑
     * @param int $currentDirection
     * @param int $newDirection
     * @return int
     */
    private function checkForTurn($currentDirection, $newDirection){
        switch ($currentDirection) {
            case Entity::NORTH:
                $diff = $this->x - $this->getFloorX();
                if ($diff !== 0 and $diff <= .5) {
                    $dx = ($this->getFloorX() + .5) - $this->x;
                    $this->move($dx, 0, 0);
                    return $newDirection;
                }
                break;
            case Entity::SOUTH:
                $diff = $this->x - $this->getFloorX();
                if ($diff !== 0 and $diff >= .5) {
                    $dx = ($this->getFloorX() + .5) - $this->x;
                    $this->move($dx, 0, 0);
                    return $newDirection;
                }
                break;
            case Entity::EAST:
                $diff = $this->z - $this->getFloorZ();
                if ($diff !== 0 and $diff <= .5) {
                    $dz = ($this->getFloorZ() + .5) - $this->z;
                    $this->move(0, 0, $dz);
                    return $newDirection;
                }
                break;
            case Entity::WEST:
                $diff = $this->z - $this->getFloorZ();
                if ($diff !== 0 and $diff >= .5) {
                    $dz = ($this->getFloorZ() + .5) - $this->z; // 修复：移除重复$dz赋值
                    $this->move(0, 0, $dz);
                    return $newDirection;
                }
                break;
        }

        return $currentDirection;
    }

    /**
     * 处理斜坡铁轨垂直移动，保留原逻辑，无修改
     * @param int $railType
     * @param int $currentDirection
     * @return bool
     */
    private function checkForVertical($railType, $currentDirection){
        switch ($railType) {
            case Rail::SLOPED_ASCENDING_NORTH:
                switch ($currentDirection) {
                    case Entity::NORTH:
                        $diff = $this->x - $this->getFloorX();
                        if ($diff !== 0 and $diff <= .5) {
                            $dx = ($this->getFloorX() - .1) - $this->x;
                            $this->move($dx, -1, 0);
                            return true;
                        }
                        break;
                    case Entity::SOUTH:
                        $diff = $this->x - $this->getFloorX();
                        if ($diff !== 0 and $diff >= .5) {
                            $dx = ($this->getFloorX() + 1) - $this->x;
                            $this->move($dx, 1, 0);
                            return true;
                        }
                        break;
                }
                break;
            case Rail::SLOPED_ASCENDING_SOUTH:
                switch ($currentDirection) {
                    case Entity::SOUTH:
                        $diff = $this->x - $this->getFloorX();
                        if ($diff !== 0 and $diff >= .5) {
                            $dx = ($this->getFloorX() + 1) - $this->x;
                            $this->move($dx, -1, 0);
                            return true;
                        }
                        break;
                    case Entity::NORTH:
                        $diff = $this->x - $this->getFloorX();
                        if ($diff !== 0 and $diff <= .5) {
                            $dx = ($this->getFloorX() - .1) - $this->x;
                            $this->move($dx, 1, 0);
                            return true;
                        }
                        break;
                }
                break;
            case Rail::SLOPED_ASCENDING_EAST:
                switch ($currentDirection) {
                    case Entity::EAST:
                        $diff = $this->z - $this->getFloorZ();
                        if ($diff !== 0 and $diff <= .5) {
                            $dz = ($this->getFloorZ() - .1) - $this->z;
                            $this->move(0, 1, $dz);
                            return true;
                        }
                        break;
                    case Entity::WEST:
                        $diff = $this->z - $this->getFloorZ();
                        if ($diff !== 0 and $diff >= .5) {
                            $dz = ($this->getFloorZ() + 1) - $this->z;
                            $this->move(0, -1, $dz);
                            return true;
                        }
                        break;
                }
                break;
            case Rail::SLOPED_ASCENDING_WEST:
                switch ($currentDirection) {
                    case Entity::WEST:
                        $diff = $this->z - $this->getFloorZ();
                        if ($diff !== 0 and $diff >= .5) {
                            $dz = ($this->getFloorZ() + 1) - $this->z;
                            $this->move(0, 1, $dz);
                            return true;
                        }
                        break;
                    case Entity::EAST:
                        $diff = $this->z - $this->getFloorZ();
                        if ($diff !== 0 and $diff <= .5) {
                            $dz = ($this->getFloorZ() - .1) - $this->z;
                            $this->move(0, -1, $dz);
                            return true;
                        }
                        break;
                }
                break;
        }

        return false;
    }

    /**
     * 验证并移动到下一个铁轨，修复空指针致命BUG，保留原业务逻辑
     * @return bool
     */
    private function moveIfRail(){
        $nextMoveVector = $this->moveVector[$this->direction];
        $nextMoveVector = $nextMoveVector->multiply($this->moveSpeed);
        $newVector = $this->add($nextMoveVector->x, $nextMoveVector->y, $nextMoveVector->z);
        $possibleRail = $this->getCurrentRail();
        // 修复：增加$possibleRail非空判断，避免调用null->getId()致命错误
        if ($possibleRail !== null && in_array($possibleRail->getId(), [Block::RAIL, Block::ACTIVATOR_RAIL, Block::DETECTOR_RAIL, Block::POWERED_RAIL])) {
            $this->moveUsingVector($newVector);
            return true;
        }

        return false;
    }

    /**
     * 根据向量移动实体，保留原逻辑，无修改
     * @param Vector3 $desiredPosition
     */
    private function moveUsingVector(Vector3 $desiredPosition){
        $dx = $desiredPosition->x - $this->x;
        $dy = $desiredPosition->y - $this->y;
        $dz = $desiredPosition->z - $this->z;
        $this->move($dx, $dy, $dz);
    }

    /**
     * 获取最近的铁轨，保留原逻辑，无修改
     * @return Block|null
     */
    public function getNearestRail(){
        $minX = Math::floorFloat($this->boundingBox->minX);
        $minY = Math::floorFloat($this->boundingBox->minY);
        $minZ = Math::floorFloat($this->boundingBox->minZ);
        $maxX = Math::ceilFloat($this->boundingBox->maxX);
        $maxY = Math::ceilFloat($this->boundingBox->maxY);
        $maxZ = Math::ceilFloat($this->boundingBox->maxZ);

        $rails = [];

        for ($z = $minZ; $z <= $maxZ; ++$z) {
            for ($x = $minX; $x <= $maxX; ++$x) {
                for ($y = $minY; $y <= $maxY; ++$y) {
                    $block = $this->level->getBlock($this->temporalVector->setComponents($x, $y, $z));
                    if (in_array($block->getId(), [Block::RAIL, Block::ACTIVATOR_RAIL, Block::DETECTOR_RAIL, Block::POWERED_RAIL])) $rails[] = $block;
                }
            }
        }

        $minDistance = PHP_INT_MAX;
        $nearestRail = null;
        foreach ($rails as $rail) {
            $dis = $this->distance($rail);
            if ($dis < $minDistance) {
                $nearestRail = $rail;
                $minDistance = $dis;
            }
        }

        return $nearestRail;
    }

    /**
     * 生成实体到玩家客户端，优化硬编码为常量，保留原逻辑
     * @param Player $player
     */
    public function spawnTo(Player $player){
        $pk = new AddEntityPacket();
        $pk->eid = $this->getId();
        $pk->type = self::NETWORK_ID; // 优化：替换硬编码98，使用类常量，便于维护
        $pk->x = $this->x;
        $pk->y = $this->y;
        $pk->z = $this->z;
        $pk->speedX = 0;
        $pk->speedY = 0;
        $pk->speedZ = 0;
        $pk->yaw = 0;
        $pk->pitch = 0;
        $pk->metadata = $this->dataProperties;
        $player->dataPacket($pk);

        parent::spawnTo($player);
        
    }

    /**
     * 实体受攻击处理，保留原逻辑，无修改
     * @param int $damage
     * @param EntityDamageEvent $source
     */
    public function attack($damage, EntityDamageEvent $source){
        parent::attack($damage, $source);

        if(!$source->isCancelled()){
            $pk = new EntityEventPacket();
            $pk->eid = $this->id;
            $pk->event = EntityEventPacket::HURT_ANIMATION;
            foreach($this->getLevel()->getPlayers() as $player){
                $player->dataPacket($pk);
            }
        }
    }
    
    /**
     * 获取掉落物：优先取虚拟箱子库存，其次解析实体 NBT
     * @return array
     */
    public function getDrops(){
        if($this->chestinventory !== null){
            $drops = [];
            foreach($this->chestinventory->getContents() as $item){
                if($item->getId() !== ItemItem::AIR and $item->getCount() > 0){
                    $drops[] = $item;
                }
            }
            return $drops;
        }

        if(isset($this->namedtag->Items) and $this->namedtag->Items instanceof ListTag){
            $drops = [];
            foreach($this->namedtag->Items as $slot){
                if($slot instanceof CompoundTag){
                    $item = NBT::getItemHelper($slot);
                    if($item->getId() !== ItemItem::AIR and $item->getCount() > 0){
                        $drops[] = $item;
                    }
                }
            }
            return $drops;
        }

        return [ItemItem::get(0, 0)];
    }

    /**
     * 获取保存ID，保留原逻辑，无修改
     * @return string
     */
    public function getSaveId(){
        $class = new \ReflectionClass(static::class);
        return $class->getShortName();
    }
    
    /**
     * 获取虚拟箱子库存，按需创建并载入实体 NBT 中的物品
     * @return MinecartChestInventory
     */
    public function getInventory(){
        if($this->chestinventory === null){
            $pos = Position::fromObject($this->add(0, 2), $this->getLevel());
            $this->chestinventory = new MinecartChestInventory($pos, $this, "箱子矿车");
            $this->loadInventoryFromNamedTag($this->chestinventory);
        }
        return $this->chestinventory;
    }

    private function loadInventoryFromNamedTag(MinecartChestInventory $inventory){
        $this->ensureEntityItemList();
        foreach($this->namedtag->Items as $slot){
            if(!$slot instanceof CompoundTag){
                continue;
            }
            $index = isset($slot["Slot"]) ? (int) $slot["Slot"] : -1;
            if($index >= 0 and $index < $inventory->getSize()){
                $inventory->setItem($index, NBT::getItemHelper($slot));
            }
        }
    }

    /**
     * 打开箱子矿车：使用核心既有的虚拟容器方案，不在服务端放置真实箱子
     */
    public function MinecartChestOpen(){
        if ($this->linkplayer === null || !$this->linkplayer->isOnline()) {
            return;
        }
        $this->linkplayer->addWindow($this->getInventory());
    }
    
    /**
     * 销毁实体，增加非空防护，保留原回收逻辑
     */
    public function close(){
        if(!$this->closed){
            // 将虚拟库存写回实体 NBT，保证掉落/持久化
            if($this->chestinventory !== null){
                $this->syncNamedTagItemsFromInventory($this->chestinventory);
            }
            // 优化：增加物品掉落非空判断
            if(!$this->getLevel()->getServer()->isWorldNonLivingEntityDropsDisabled($this->getLevel())){
                $this->getLevel()->dropItem($this, ItemItem::get(ItemItem::MINECART_WITH_CHEST, 0, 1));
                foreach($this->getDrops() as $item){
                    if ($item !== null && $item->getId() !== 0) { // 过滤空物品
                        $this->getLevel()->dropItem($this, $item);
                    }
                }
            }
        }
        parent::close();
    }

}
