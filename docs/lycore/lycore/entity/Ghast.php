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

use lycore\event\entity\ProjectileLaunchEvent;
use lycore\item\Item as ItemItem;
use lycore\nbt\tag\CompoundTag;
use lycore\nbt\tag\DoubleTag;
use lycore\nbt\tag\FloatTag;
use lycore\nbt\tag\ListTag;
use lycore\network\protocol\AddEntityPacket;
use lycore\math\Vector3;
use lycore\Player;

class Ghast extends FlyingAnimal{
	const NETWORK_ID = 41;

	public $width = 6;
	public $length = 6;
	public $height = 6;

	private $attackDelay = 0;

	public function getName() : string{
		return "Ghast";
	}

	public function initEntity(){
		$this->setMaxHealth(10);
		parent::initEntity();
	}

	public function onUpdate($currentTick){
		$hasUpdate = parent::onUpdate($currentTick);
		if(!$this->closed && $this->isAlive()){
			if($this->attackDelay < 100){
				++$this->attackDelay;
			}
			$this->attackNearestPlayer();
		}

		return $hasUpdate;
	}

	protected function attackNearestPlayer(){
		$target = null;
		$bestDistance = 4096;

		foreach($this->getLevel()->getPlayers() as $player){
			if(!$player->isAlive() || $player->closed){
				continue;
			}
			if(method_exists($player, "isSurvival") && method_exists($player, "isAdventure")){
				if(!$player->isSurvival() && !$player->isAdventure()){
					continue;
				}
			}
			$distance = $this->distanceSquared($player);
			if($distance <= $bestDistance){
				$bestDistance = $distance;
				$target = $player;
			}
		}
		if(method_exists($this->getLevel(), "getEntities")){
			foreach($this->getLevel()->getEntities() as $entity){
				if(!($entity instanceof Bot) || $entity === $this || $entity->closed || !$entity->isAlive()){
					continue;
				}
				$distance = $this->distanceSquared($entity);
				if($distance <= $bestDistance){
					$bestDistance = $distance;
					$target = $entity;
				}
			}
		}

		if($target instanceof Entity){
			$this->setPm1eFlyFollowTarget($target);
			$this->shootFireball($target);
		}else{
			$this->setPm1eFlyFollowTarget(null);
		}
	}

	protected function shootFireball(Entity $player){
		if($this->attackDelay < 60 || $this->distanceSquared($player) > 4096){
			return false;
		}

		$direction = $this->getGhastFireballDirection($player);
		$origin = $this->getGhastFireballOrigin($direction);
		$nbt = new CompoundTag("", [
			"Pos" => new ListTag("Pos", [
				new DoubleTag("", $origin->x),
				new DoubleTag("", $origin->y),
				new DoubleTag("", $origin->z)
			]),
			"Motion" => new ListTag("Motion", [
				new DoubleTag("", 0),
				new DoubleTag("", 0),
				new DoubleTag("", 0)
			]),
			"Rotation" => new ListTag("Rotation", [
				new FloatTag("", $this->yaw),
				new FloatTag("", $this->pitch)
			]),
			"damage" => new FloatTag("damage", 2)
		]);

		$fireball = Entity::createEntity("Fireball", $this->chunk, $nbt, $this);
		if($fireball instanceof Fireball){
			$fireball->setMotion($direction->multiply(1.0));
			$ev = new ProjectileLaunchEvent($fireball);
			$this->server->getPluginManager()->callEvent($ev);
			if($ev->isCancelled()){
				$fireball->close();
				return false;
			}

			$fireball->spawnToAll();
			$this->attackDelay = 0;
			return true;
		}

		return false;
	}

	private function getGhastFireballDirection(Entity $target) : Vector3{
		$direction = $target->add(0, $target->getEyeHeight(), 0)->subtract($this->add(0, $this->getEyeHeight(), 0))->normalize();
		if($direction->lengthSquared() > 0){
			return $direction;
		}

		return new Vector3(-sin($this->yaw / 180 * M_PI), 0, cos($this->yaw / 180 * M_PI));
	}

	private function getGhastFireballOrigin(Vector3 $direction) : Vector3{
		$origin = new Vector3($this->x, $this->y + $this->getEyeHeight(), $this->z);
		$clearance = 0.6;
		$candidates = [];
		$halfWidth = max($this->width, $this->length) / 2;

		if(abs($direction->x) > 0.0001){
			$candidates[] = ($halfWidth + $clearance) / abs($direction->x);
		}
		if(abs($direction->z) > 0.0001){
			$candidates[] = ($halfWidth + $clearance) / abs($direction->z);
		}
		if($direction->y > 0.0001){
			$candidates[] = (($this->height - $this->getEyeHeight()) + $clearance) / $direction->y;
		}elseif($direction->y < -0.0001){
			$candidates[] = ($this->getEyeHeight() + $clearance) / abs($direction->y);
		}

		$distance = count($candidates) > 0 ? min($candidates) : ($halfWidth + $clearance);
		return $origin->add($direction->x * $distance, $direction->y * $distance, $direction->z * $distance);
	}

	public function spawnTo(Player $player){
		$pk = new AddEntityPacket();
		$pk->eid = $this->getId();
		$pk->type = self::NETWORK_ID;
		$pk->x = $this->x;
		$pk->y = $this->y;
		$pk->z = $this->z;
		$pk->speedX = $this->motionX;
		$pk->speedY = $this->motionY;
		$pk->speedZ = $this->motionZ;
		$pk->yaw = $this->yaw;
		$pk->pitch = $this->pitch;
		$pk->metadata = $this->dataProperties;
		$player->dataPacket($pk);

		parent::spawnTo($player);
	}

	public function getDrops()
	{
		$drops = [];
		$mt = mt_rand(0, 2);
		if($mt > 0){
			$drops[] = ItemItem::get(ItemItem::GHAST_TEAR, 0, $mt);
		}

		return $drops;
	}
}
