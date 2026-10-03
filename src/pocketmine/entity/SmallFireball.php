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
 * 移植自 lycore\entity\SmallFireball，命名空间改为 pocketmine\entity。
 * 当前核心 Projectile 无 onHitEntity 返回值语义，改为命中后置钩子。
 */

namespace pocketmine\entity;

use pocketmine\event\entity\EntityCombustByEntityEvent;
use pocketmine\level\format\FullChunk;
use pocketmine\nbt\tag\CompoundTag;
use pocketmine\network\protocol\AddEntityPacket;
use pocketmine\Player;

class SmallFireball extends Projectile{
	const NETWORK_ID = 94;

	public $width = 0.3125;
	public $length = 0.3125;
	public $height = 0.3125;

	protected $gravity = 0.0;
	protected $drag = 0.01;
	protected $damage = 2;

	public function __construct(FullChunk $chunk, CompoundTag $nbt, Entity $shootingEntity = null){
		parent::__construct($chunk, $nbt, $shootingEntity);
	}

	protected function initEntity(){
		parent::initEntity();
		if(isset($this->namedtag->damage)){
			$this->damage = (float) $this->namedtag["damage"];
		}
	}

	public function onUpdate($currentTick){
		if($this->closed){
			return false;
		}

		$this->timings->startTiming();
		$hasUpdate = parent::onUpdate($currentTick);

		if($this->age > 1200 || $this->isCollided){
			$this->kill();
			$hasUpdate = true;
		}

		$this->timings->stopTiming();
		return $hasUpdate;
	}

	protected function onHitEntity(Entity $entityHit){
		if($entityHit->closed){
			return;
		}

		$combustEvent = new EntityCombustByEntityEvent($this, $entityHit, 5);
		$this->server->getPluginManager()->callEvent($combustEvent);
		if(!$combustEvent->isCancelled()){
			$entityHit->setOnFire($combustEvent->getDuration());
		}
	}

	public function spawnTo(Player $player){
		$pk = new AddEntityPacket();
		$pk->type = self::NETWORK_ID;
		$pk->eid = $this->getId();
		$pk->x = $this->x;
		$pk->y = $this->y;
		$pk->z = $this->z;
		$pk->speedX = $this->motionX;
		$pk->speedY = $this->motionY;
		$pk->speedZ = $this->motionZ;
		$pk->metadata = $this->dataProperties;
		$player->dataPacket($pk);

		parent::spawnTo($player);
	}
}
