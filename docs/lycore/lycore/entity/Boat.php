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

use lycore\nbt\tag\IntTag;
use lycore\network\protocol\AddEntityPacket;
use lycore\Player;
use lycore\math\Vector3;
use lycore\event\entity\EntityDamageEvent;
use lycore\network\protocol\EntityEventPacket;
use lycore\item\Item as ItemItem;
use lycore\level\format\FullChunk;
use lycore\nbt\tag\CompoundTag;
use lycore\block\Water;

class Boat extends Vehicle{
	const NETWORK_ID = 90;

	/** LY Core Player.php 使用的座位高度偏移 */
	const RIDER_SEAT_Y_OFFSET = 0.3;
	/** 兼容别名 */
	const RIDER_OFFSET_Y = 0.3;
	const MAX_RIDER_MOVE_DISTANCE_SQUARED = 16.0;

	/** 船在水面的吃水深度 */
	const WATER_DRAFT = 0.35;
	const AIR_GRAVITY = 0.04;
	const MAX_FALL_SPEED = -0.4;
	const MAX_RISE_SPEED = 0.1;

	public $height = 0.7;
	public $width = 1.6;

	public $gravity = 0.04;
	public $drag = 0.1;

	public function __construct(FullChunk $chunk, CompoundTag $nbt){
		if(!isset($nbt->WoodID)){
			$nbt->WoodID = new IntTag("WoodID", 0);
		}
		parent::__construct($chunk, $nbt);
		$this->setDataProperty(self::DATA_WOOD_ID, self::DATA_TYPE_BYTE, $this->getWoodID());
	}

	public function initEntity(){
		$this->setMaxHealth(10);
		parent::initEntity();
	}

	public function getWoodID() : int{
		return (int) $this->namedtag["WoodID"];
	}

	public function spawnTo(Player $player){
		$pk = new AddEntityPacket();
		$pk->eid = $this->getId();
		$pk->type = Boat::NETWORK_ID;
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
	 * LY Core Player.php MOVE_PLAYER_PACKET 会调用本方法：
	 * $entity->handleRiderMove($this, $packet->x, $packet->z, $packet->yaw, $packet->pitch);
	 *
	 * 只接受客户端水平位移+朝向；竖直方向由稳定水面逻辑处理，避免视角抖动。
	 */
	public function handleRiderMove(Player $rider, float $x, float $z, float $yaw, float $pitch) : bool{
		if($this->closed or $this->getLinkedEntity() !== $rider or $this->level === null or $rider->getLevel() !== $this->level){
			return false;
		}

		$yaw = fmod($yaw, 360.0);
		$pitch = fmod($pitch, 360.0);
		if($yaw < 0){
			$yaw += 360.0;
		}

		$rider->setRotation($yaw, $pitch);
		$this->yaw = $yaw;
		$this->pitch = 0.0;

		// 区块未就绪时只同步座位，不硬推船
		if(method_exists($this, "preloadRiderChunks") and !$this->preloadRiderChunks(2)){
			$this->motionX = 0.0;
			$this->motionY = 0.0;
			$this->motionZ = 0.0;
			if(method_exists($this, "syncRiderPositionToVehicle")){
				$this->syncRiderPositionToVehicle($rider, self::RIDER_SEAT_Y_OFFSET);
			}
			return true;
		}

		$dx = $x - $this->x;
		$dz = $z - $this->z;
		if(($dx * $dx) + ($dz * $dz) <= self::MAX_RIDER_MOVE_DISTANCE_SQUARED){
			// 骑乘时服务器不做竖直物理，只跟客户端水平移动，从根源消抖
			$this->motionX = 0.0;
			$this->motionY = 0.0;
			$this->motionZ = 0.0;
			$this->move($dx, 0.0, $dz);
			$this->lockToWaterSurface(false);
			$this->updateMovement();
		}

		if(method_exists($this, "syncRiderPositionToVehicle")){
			$this->syncRiderPositionToVehicle($rider, self::RIDER_SEAT_Y_OFFSET);
		}else{
			$rider->setPosition($this->temporalVector->setComponents($this->x, $this->y + self::RIDER_SEAT_Y_OFFSET, $this->z));
		}

		return true;
	}

	/**
	 * 兼容旧接口（若有其它代码调用 onRiderMove）
	 */
	public function onRiderMove($x, $y, $z, $yaw){
		if($this->closed){
			return false;
		}

		$yaw = fmod((float) $yaw, 360.0);
		if($yaw < 0){
			$yaw += 360.0;
		}

		$this->setPositionAndRotation(new Vector3((float) $x, (float) $y, (float) $z), $yaw, 0.0);
		$this->motionX = 0.0;
		$this->motionY = 0.0;
		$this->motionZ = 0.0;
		$this->onGround = false;
		$this->updateMovement();
		return true;
	}

	public function close(){
		if(!$this->closed){
			$canDrop = true;
			if($this->level !== null){
				$server = $this->getLevel()->getServer();
				if(method_exists($server, "isWorldNonLivingEntityDropsDisabled")){
					$canDrop = !$server->isWorldNonLivingEntityDropsDisabled($this->getLevel());
				}
			}
			if($canDrop){
				foreach($this->getDrops() as $item){
					$this->getLevel()->dropItem($this, $item);
				}
			}
		}
		parent::close();
	}

	/**
	 * 在船身附近查找水面高度（方块顶面 y+1）
	 * @return float|null
	 */
	private function findWaterSurfaceY(){
		$blockX = (int) floor($this->x);
		$blockZ = (int) floor($this->z);
		$baseY = (int) floor($this->y);

		for($checkY = $baseY + 1; $checkY >= $baseY - 2; --$checkY){
			if($checkY < 0 or $checkY > 255){
				continue;
			}
			$id = $this->level->getBlockIdAt($blockX, $checkY, $blockZ);
			if($id !== 8 and $id !== 9){
				continue;
			}

			$top = $checkY;
			while($top < 255){
				$above = $this->level->getBlockIdAt($blockX, $top + 1, $blockZ);
				if($above !== 8 and $above !== 9){
					break;
				}
				++$top;
			}
			return (float) ($top + 1);
		}

		$block = $this->level->getBlock(new Vector3($this->x, $this->y, $this->z));
		if($block instanceof Water){
			return (float) ($block->y + 1);
		}
		$below = $this->level->getBlock(new Vector3($this->x, $this->y - 0.2, $this->z));
		if($below instanceof Water){
			return (float) ($below->y + 1);
		}

		return null;
	}

	/**
	 * 把船锁/收敛到稳定吃水高度
	 * @param bool $soft true=用 motion 收敛；false=直接锁高度（骑乘时用，防抖）
	 */
	private function lockToWaterSurface($soft = true){
		$surfaceY = $this->findWaterSurfaceY();
		if($surfaceY === null){
			return false;
		}

		$targetY = $surfaceY - self::WATER_DRAFT;
		$diff = $targetY - $this->y;

		if(!$soft){
			// 骑乘：直接贴稳定高度，不产生竖直速度
			if(abs($diff) > 0.001){
				$this->y = $targetY;
			}
			$this->motionY = 0.0;
			return true;
		}

		if($diff > 0.02){
			$this->motionY = min(self::MAX_RISE_SPEED, max(0.02, $diff * 0.35));
		}elseif($diff < -0.08){
			$this->motionY = max(-0.08, $diff * 0.25);
		}else{
			$this->motionY = 0.0;
			if(abs($diff) > 0.001){
				$this->y = $targetY;
			}
		}
		return true;
	}

	/**
	 * 无乘客时：浮力把船稳定在水面
	 */
	private function applyBuoyancyPhysics(){
		if($this->lockToWaterSurface(true)){
			$this->motionX *= 0.7;
			$this->motionZ *= 0.7;
			return;
		}

		if($this->onGround){
			$this->motionY = 0.0;
			$this->motionX *= 0.5;
			$this->motionZ *= 0.5;
		}else{
			$this->motionY = max(self::MAX_FALL_SPEED, $this->motionY - self::AIR_GRAVITY);
		}
	}

	public function onUpdate($currentTick){
		if($this->closed){
			return false;
		}
		$tickDiff = $currentTick - $this->lastUpdate;
		if($tickDiff <= 0 and !$this->justCreated){
			return true;
		}

		$this->lastUpdate = $currentTick;

		$this->timings->startTiming();

		$hasUpdate = $this->entityBaseTick($tickDiff);

		$rider = $this->getLinkedEntity();
		if($rider instanceof Player){
			// 有玩家驾驶：完全交给 handleRiderMove，服务器不再做竖直物理
			$this->motionX = 0.0;
			$this->motionY = 0.0;
			$this->motionZ = 0.0;
			$this->age = 0;

			// 仅保持贴水面 + 同步座位，避免空 tick 时船慢慢沉
			$this->lockToWaterSurface(false);
			if(method_exists($this, "syncRiderPositionToVehicle")){
				$this->syncRiderPositionToVehicle($rider, self::RIDER_SEAT_Y_OFFSET);
			}
			$this->updateMovement();
		}else{
			$this->applyBuoyancyPhysics();
			$this->move($this->motionX, $this->motionY, $this->motionZ);
			$this->updateMovement();

			// 必须用比较。写成 linkedType = 0 会变成赋值，上船后仍计时回收
			if($this->linkedEntity === null or (int) $this->linkedType === 0){
				if($this->age > 1500){
					$this->close();
					$hasUpdate = true;
					$this->age = 0;
				}
				$this->age++;
			}else{
				$this->age = 0;
			}
		}

		$this->timings->stopTiming();

		return $hasUpdate or !$this->onGround or abs($this->motionX) > 0.00001 or abs($this->motionY) > 0.00001 or abs($this->motionZ) > 0.00001;
	}

	public function getDrops(){
		return [
			ItemItem::get(ItemItem::BOAT, $this->getWoodID(), 1)
		];
	}

	public function getSaveId(){
		$class = new \ReflectionClass(static::class);
		return $class->getShortName();
	}
}
