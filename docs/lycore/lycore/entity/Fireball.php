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

use lycore\level\Explosion;
use lycore\level\format\FullChunk;
use lycore\nbt\tag\CompoundTag;
use lycore\network\protocol\AddEntityPacket;
use lycore\Player;

class Fireball extends SmallFireball implements Explosive{
	const NETWORK_ID = 85;

	public $width = 1.0;
	public $length = 1.0;
	public $height = 1.0;

	protected $damage = 2;

	public function __construct(FullChunk $chunk, CompoundTag $nbt, Entity $shootingEntity = null){
		parent::__construct($chunk, $nbt, $shootingEntity);
	}

	public function onUpdate($currentTick){
		if($this->closed){
			return false;
		}

		$this->timings->startTiming();
		$hasUpdate = parent::onUpdate($currentTick);

		if($this->isCollided){
			$this->explode();
			$this->kill();
			$hasUpdate = true;
		}

		$this->timings->stopTiming();
		return $hasUpdate;
	}

	protected function onHitEntity(Entity $entityHit){
		$hit = parent::onHitEntity($entityHit);
		if($hit){
			$this->explode();
		}
		return $hit;
	}

	public function explode(){
		if($this->closed){
			return;
		}

		$explosion = new Explosion($this, 1, $this);
		$explosion->explodeB();
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
