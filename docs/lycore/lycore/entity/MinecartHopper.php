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

namespace lycore\entity;

use lycore\block\Block;
use lycore\block\ActivatorRail;
use lycore\block\Rail;
use lycore\block\Chest;
use lycore\block\Air;
use lycore\math\Math;
use lycore\math\Vector3;
use lycore\event\entity\EntityDamageEvent;
use lycore\network\protocol\AddEntityPacket;
use lycore\network\protocol\BlockEventPacket;
use lycore\item\Item as ItemItem;
use lycore\event\entity\ExplosionPrimeEvent;
use lycore\level\Explosion;
use lycore\level\Level;
use lycore\Player;
use lycore\nbt\NBT;
use lycore\nbt\tag\CompoundTag;
use lycore\nbt\tag\ListTag;
use lycore\nbt\tag\IntTag;
use lycore\nbt\tag\StringTag;
use lycore\network\protocol\EntityEventPacket;
use lycore\tile\Chest as TileChest;
use lycore\tile\Tile;
use lycore\tile\Hopper as HopperTile;
use lycore\tile\Furnace as FurnaceTile;
use lycore\inventory\PlayerInventory;
use lycore\inventory\ChestInventory;
use lycore\inventory\FakeBlockMenu;

class MinecartHopper extends Vehicle {
    const NETWORK_ID = 96;

    const TYPE_NORMAL = 1;
    const TYPE_CHEST = 2;
    const TYPE_HOPPER = 3;
    const TYPE_TNT = 4;

    const STATE_INITIAL = 0;
    const STATE_ON_RAIL = 1;
    const STATE_OFF_RAIL = 2;

    public $height = 0.7;
    public $width = 0.98;

    public $drag = 0.1;
    public $gravity = 0.5;

    public $isMoving = false;
    public $moveSpeed = 0.5;
	private $enabled = true;
    public $linkplayer = null; // 初始化：避免null未定义

    private $state = MinecartHopper::STATE_INITIAL;
    private $direction = -1;
    private $moveVector = [];
    public $motionX = 0.0; // 优化：浮点型初始化更规范
    public $motionY = 0.0;
    public $motionZ = 0.0;
    
    public $chestx = 0;
    public $chesty = 0;
    public $chestz = 0;
    
    public $chestplaced = false;
    public $chestblock = null; // 初始化
    public $chesttile = null;   // 初始化
    public $chestnbt = null;    // 初始化
    public $chestitems = null;  // 初始化
    public $chestitemsarr = null;//初始化
    public $chestinventory = null;//初始化
    
    public $blk = null; // 初始化

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
        return "Minecart Hopper";
    }
    
    /**
     * 获取Tile，保留原逻辑
     * @return mixed
     */
    public function getTile(){
        return $this->chesttile;
    }

    /**
     * 修复致命BUG：TYPE_INT → TYPE_CHEST，保留方法结构
     * @return int
     */
    public function getType(): int{
        return self::TYPE_HOPPER;
    }

	public function isEnabled(){
		return $this->enabled;
	}

	public function setEnabled($enabled){
		$this->enabled = (bool) $enabled;
	}

	protected function activateByRail(ActivatorRail $rail, $active){
		$this->setEnabled(!$active);
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
		$rider = $this->getLinkedEntity();
		if($rider instanceof Player){
			$this->syncRiderPositionToVehicle($rider, 0.7);
		}
		$chunksReady = $this->preloadRiderChunks(2);
		if(!$chunksReady){
			if($rider instanceof Player){
				$this->syncRiderPositionToVehicle($rider, 0.7);
			}
			$this->timings->stopTiming();
			return true;
		}
        
        if ($this->state === MinecartHopper::STATE_INITIAL){
            $this->checkIfOnRail();
        }elseif($this->state === MinecartHopper::STATE_ON_RAIL){
            $hasUpdate = $this->forwardOnRail($this);
            $this->updateMovement();
        }
        if($this->isAlive() and $this->isEnabled()){
            if($this->transferFuelToFurnaceBelow()){
                $hasUpdate = true;
            }elseif($this->transferToHopperBelow()){
                $hasUpdate = true;
            }
        }
        
		$this->preloadRiderChunks(1);
		if($rider instanceof Player){
			$this->syncRiderPositionToVehicle($rider, 0.7);
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

        if($this->chesttile !== null and !$this->chesttile->closed){
            $inventory = $this->chesttile->getInventory();
            foreach($inventory->getContents() as $item){
                if($item->getId() === ItemItem::AIR or $item->getCount() <= 0){
                    continue;
                }

                $transfer = clone $item;
                $transfer->setCount(1);
                if($hopper->getInventory()->canAddItem($transfer)){
                    $hopper->getInventory()->addItem($transfer);
                    $inventory->removeItem($transfer);
                    $this->chesttile->saveNBT();
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
                $this->removeOneItemFromEntitySlot($index, $slot, $item);
                $hopper->resetCooldownTicks();
            }
            return true;
        }

        return true;
    }

    private function transferFuelToFurnaceBelow(){
        $furnace = $this->getFurnaceBelow();
        if(!$furnace instanceof FurnaceTile){
            return false;
        }

        if($this->chesttile !== null and !$this->chesttile->closed){
            $inventory = $this->chesttile->getInventory();
            foreach($inventory->getContents() as $item){
                if(!$this->canFuelBeInserted($furnace, $item)){
                    continue;
                }

                $this->insertFuelIntoFurnace($furnace, $item);
                $transfer = clone $item;
                $transfer->setCount(1);
                $inventory->removeItem($transfer);
                $this->chesttile->saveNBT();
                $this->syncNamedTagItemsFromInventory($inventory);
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
            if(!$this->canFuelBeInserted($furnace, $item)){
                continue;
            }

            $this->insertFuelIntoFurnace($furnace, $item);
            $this->removeOneItemFromEntitySlot($index, $slot, $item);
            return true;
        }

        return true;
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

    private function removeOneItemFromEntitySlot($index, CompoundTag $slot, ItemItem $item){
        $item->setCount($item->getCount() - 1);
        if($item->getCount() <= 0){
            unset($this->namedtag->Items[$index]);
        }else{
            $slotId = isset($slot->Slot) ? (int) $slot["Slot"] : (int) $index;
            $this->namedtag->Items[$index] = NBT::putItemHelper($item, $slotId);
        }
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
     * 检测是否在铁轨上，保留原逻辑，无修改
     */
    private function checkIfOnRail(){
        for ($y = -1; $y !== 2 and $this->state === MinecartHopper::STATE_INITIAL; $y++) {
            $positionToCheck = $this->temporalVector->setComponents($this->x, $this->y + $y, $this->z);
            $block = $this->level->getBlock($positionToCheck);
            if ($this->isRail($block)) {
                $minecartPosition = $positionToCheck->floor()->add(0.5, 0.75, 0.5);
                $this->setPosition($minecartPosition);    // Move minecart to center of rail
                $this->state = MinecartHopper::STATE_ON_RAIL;
            }
        }
        if ($this->state !== MinecartHopper::STATE_ON_RAIL) {
            $this->state = MinecartHopper::STATE_OFF_RAIL;
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
     * @param MinecartHopper $player
     * @return bool
     */
    private function forwardOnRail(MinecartHopper $player){
        if ($this->direction === -1) {
            $candidateDirection = $player->getDirection();
        } else {
            $candidateDirection = $this->direction;
        }
        $rail = $this->getCurrentRail();
        if ($rail !== null) {
			$this->applyRailEffects($rail);
            $railType = $rail->getRealMeta();
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
            $this->state = MinecartHopper::STATE_INITIAL;
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
        
        $this->chestx = $player->x;
        if($player->y > 6){
            $this->chesty = $player->y - 5;
        }else{
            $this->chesty = $player->y;
        }
        $this->chestz = $player->z;
        
        $this->chestnbt = new CompoundTag("", [
            new ListTag("Items", []),
            new StringTag("id", Tile::MINECART_CHEST),
            new IntTag("x", $this->chestx),
            new IntTag("y", $this->chesty),
            new IntTag("z", $this->chestz)
        ]);
        $this->chestnbt->Items->setTagType(NBT::TAG_Compound);
        $this->chestnbt->CustomName = new StringTag("CustomName", "漏斗矿车");
        $this->getLevel()->setBlock(new Vector3($this->chestx,$this->chesty,$this->chestz), new Chest(), true, true);
        $this->chesttile = Tile::createTile("MinecartChest", $this->getLevel()->getChunk($this->chestx >> 4, $this->chestz >> 4), $this->chestnbt);
        $this->blk = $this->getLevel()->getBlock(new Vector3($this->chestx,$this->chesty,$this->chestz));
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
     * 获取掉落物，保留原逻辑，无修改
     * @return array
     */
    public function getDrops(){
        if(isset($this->chesttile->namedtag->Items)){
            return $this->chesttile->getArrayItems();
        }else{
            return [ItemItem::get(0, 0)];
        }
    }

    /**
     * 获取保存ID，保留原逻辑，无修改
     * @return string
     */
    public function getSaveId(){
        $class = new \ReflectionClass(static::class);
        return $class->getShortName();
    }

    private function ensureChestTile(){
        if($this->chesttile !== null and !$this->chesttile->closed){
            return $this->chesttile;
        }

        $level = $this->getLevel();
        if($level === null or $this->linkplayer === null){
            return null;
        }

        $this->chestx = $this->linkplayer->getFloorX();
        $this->chesty = $this->linkplayer->y > 6 ? $this->linkplayer->getFloorY() - 5 : $this->linkplayer->getFloorY();
        $this->chestz = $this->linkplayer->getFloorZ();
        $items = isset($this->namedtag->Items) && $this->namedtag->Items instanceof ListTag ? $this->namedtag->Items : new ListTag("Items", []);
        $items->setTagType(NBT::TAG_Compound);

        $this->chestnbt = new CompoundTag("", [
            new ListTag("Items", []),
            new StringTag("id", Tile::MINECART_CHEST),
            new IntTag("x", $this->chestx),
            new IntTag("y", $this->chesty),
            new IntTag("z", $this->chestz)
        ]);
        $this->chestnbt->Items->setTagType(NBT::TAG_Compound);
        $this->chestnbt->CustomName = new StringTag("CustomName", "漏斗矿车");

        $level->setBlock(new Vector3($this->chestx, $this->chesty, $this->chestz), new Chest(), true, true);
        $this->chesttile = Tile::createTile(Tile::MINECART_CHEST, $level->getChunk($this->chestx >> 4, $this->chestz >> 4), $this->chestnbt);
        if($this->chesttile !== null){
            $this->chesttile->setItems($items);
        }
        $this->blk = $level->getBlock(new Vector3($this->chestx, $this->chesty, $this->chestz));

        return $this->chesttile;
    }
    
    /**
     * 打开箱子矿车，增加非空防护，保留原业务逻辑
     */
    public function MinecartChestOpen(){
        // 优化：增加$linkplayer和$chesttile非空判断，避免空指针
        if ($this->linkplayer === null || !$this->linkplayer->isOnline()) {
            return;
        }
        $tile = $this->ensureChestTile();
        if($tile === null){
            return;
        }
        $this->linkplayer->addWindow($tile->getInventory());
    }
    
    /**
     * 销毁实体，增加非空防护，保留原回收逻辑
     */
    public function close(){
        if(!$this->closed){
            // 优化：增加物品掉落非空判断
            if(!$this->getLevel()->getServer()->isWorldNonLivingEntityDropsDisabled($this->getLevel())){
                $this->getLevel()->dropItem($this, ItemItem::get(ItemItem::MINECART_WITH_HOPPER, 0, 1));
                foreach($this->getDrops() as $item){
                    if ($item !== null && $item->getId() !== 0) { // 过滤空物品
                        $this->getLevel()->dropItem($this, $item);
                    }
                }
            }
            // 优化：增加坐标有效判断，避免无效方块操作
            if ($this->chestx !== 0 || $this->chesty !== 0 || $this->chestz !== 0) {
                $this->getLevel()->setBlock(new Vector3($this->chestx,$this->chesty,$this->chestz), new Air(), true, true);
            }
        }
        parent::close();
    }

}
