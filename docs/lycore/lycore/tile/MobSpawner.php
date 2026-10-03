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

use lycore\block\Block;
use lycore\entity\Blaze;
use lycore\entity\CaveSpider;
use lycore\entity\Creeper;
use lycore\entity\Enderman;
use lycore\entity\Entity;
use lycore\entity\Ghast;
use lycore\entity\Husk;
use lycore\entity\PigZombie;
use lycore\entity\Skeleton;
use lycore\entity\Silverfish;
use lycore\entity\Spider;
use lycore\entity\Stray;
use lycore\entity\Zombie;
use lycore\item\Item;
use lycore\level\Level;
use lycore\math\Vector3;
use lycore\nbt\tag\CompoundTag;
use lycore\nbt\tag\DoubleTag;
use lycore\nbt\tag\ListTag;
use lycore\nbt\tag\FloatTag;
use lycore\nbt\tag\IntTag;
use lycore\nbt\tag\StringTag;
use lycore\level\format\FullChunk;
use lycore\Player;

class MobSpawner extends Spawnable implements Nameable{
	private const HOSTILE_SPAWN_MAX_LIGHT = 7;

	public function __construct(FullChunk $chunk, CompoundTag $nbt){
		if(!isset($nbt->EntityId)){
			$nbt->EntityId = new IntTag("EntityId", 0);
		}
		parent::__construct($chunk, $nbt);
		$this->lastUpdate = $this->getLevel()->getServer()->getTick();
		if($this->canValidateSpawnerBlock() && !$this->validateSpawnerBlock()){
			return;
		}
		if($this->getEntityId() > 0) $this->scheduleUpdate();
	}

	public function getEntityId(){
		return $this->namedtag["EntityId"];
	}

	public function setEntityId($id){
		$this->namedtag->EntityId = new IntTag("EntityId", $id);
		$this->spawnToAll();
		if($this->chunk instanceof FullChunk){
			$this->chunk->setChanged();
			$this->level->clearChunkCache($this->chunk->getX(), $this->chunk->getZ());
		}
		$this->scheduleUpdate();
	}

	private function isHostileEntityId(int $entityId) : bool{
		switch($entityId){
			case Zombie::NETWORK_ID:
			case Husk::NETWORK_ID:
			case Creeper::NETWORK_ID:
			case Skeleton::NETWORK_ID:
			case Stray::NETWORK_ID:
			case Spider::NETWORK_ID:
			case PigZombie::NETWORK_ID:
			case Enderman::NETWORK_ID:
			case CaveSpider::NETWORK_ID:
			case Ghast::NETWORK_ID:
			case Blaze::NETWORK_ID:
			case Silverfish::NETWORK_ID:
				return true;
			default:
				return false;
		}
	}

	private function canSpawnEntityAt(int $entityId, int $x, int $y, int $z) : bool{
		if(!$this->isHostileEntityId($entityId)){
			return true;
		}

		if(
			$this->getLevel()->getDimension() !== Level::DIMENSION_NORMAL ||
			$this->getLevel()->getFolderName() === $this->getLevel()->getServer()->netherName ||
			$this->getLevel()->getFolderName() === $this->getLevel()->getServer()->enderName
		){
			return true;
		}

		if($y < 0 || $y >= Level::Y_MAX){
			return false;
		}

		return $this->getLevel()->getFullLightAt($x, $y, $z) <= self::HOSTILE_SPAWN_MAX_LIGHT;
	}

	public function getName() : string{
		return isset($this->namedtag->CustomName) ? $this->namedtag->CustomName->getValue() : "Monster Spawner";
	}

	public function hasName(){
		return isset($this->namedtag->CustomName);
	}

	public function setName($str){
		if($str === ""){
			unset($this->namedtag->CustomName);
			return;
		}

		$this->namedtag->CustomName = new StringTag("CustomName", $str);
	}

	public function canUpdate() : bool{
		if(!$this->canValidateSpawnerBlock() || !$this->validateSpawnerBlock()) return false;
		if($this->getEntityId() === 0) return false;
		if($this->getLevel()->getServer()->isWorldNaturalMobSpawnDisabled($this->getLevel())) return false;
		$hasPlayer = false;
		$count = 0;
		foreach($this->getLevel()->getEntities() as $e){
			if($e instanceof Player){
				if($e->distance($this->getBlock()) <= 15) $hasPlayer = true;
			}
			if($e::NETWORK_ID == $this->getEntityId()) $count++;
		}
		if($hasPlayer and $count < 15) return true; // Spawn limit = 15
		return false;
	}

	public function onUpdate(){
		if($this->closed === true){
			return false;
		}

		$this->timings->startTiming();

		if(!$this->canValidateSpawnerBlock()){
			$this->timings->stopTiming();
			return false;
		}
		if(!$this->validateSpawnerBlock()){
			$this->timings->stopTiming();
			return false;
		}
		if($this->canUpdate()){
			$currentTick = $this->getLevel()->getServer()->getTick();
			$baseTick = $this->getLevel()->getServer()->getTicksPerSecondAverage();
			if(($currentTick - $this->lastUpdate) > $baseTick * 10){//Spawn per 10 seconds
				$this->lastUpdate = $currentTick;
				$up = $this->getLevel()->getBlock($this->getSide(Vector3::SIDE_UP));
				if($up->getId() == Item::AIR && $this->canSpawnEntityAt((int) $this->getEntityId(), (int) $this->x, (int) $this->y + 1, (int) $this->z)){
					$nbt = new CompoundTag("", [
						"Pos" => new ListTag("Pos", [
							new DoubleTag("", $this->x),
							new DoubleTag("", $this->y + 1),
							new DoubleTag("", $this->z)
						]),
						"Motion" => new ListTag("Motion", [
							new DoubleTag("", 0),
							new DoubleTag("", 0),
							new DoubleTag("", 0)
						]),
						"Rotation" => new ListTag("Rotation", [
							new FloatTag("", 0),
							new FloatTag("", 0)
						]),
					]);
					$entity = Entity::createEntity($this->getEntityId(), $this->chunk, $nbt);
					$entity->spawnToAll();
				}
			}
		}

		$this->timings->stopTiming();

		return true;
	}

	public function getSpawnCompound(){
		$c = new CompoundTag("", [
			new StringTag("id", Tile::MOB_SPAWNER),
			new IntTag("x", (int) $this->x),
			new IntTag("y", (int) $this->y),
			new IntTag("z", (int) $this->z),
			new IntTag("EntityId", (int) $this->getEntityId())
		]);

		if($this->hasName()){
			$c->CustomName = $this->namedtag->CustomName;
		}

		return $c;
	}

	private function validateSpawnerBlock() : bool{
		if($this->closed === true){
			return false;
		}

		if(!$this->canValidateSpawnerBlock()){
			return false;
		}

		if($this->y < 0 or $this->y >= Level::Y_MAX){
			$this->removeInvalidSpawnerTile();
			return false;
		}

		if($this->chunk->getBlockId($this->x & 0x0f, $this->y & 0x7f, $this->z & 0x0f) !== Block::MONSTER_SPAWNER){
			$this->removeInvalidSpawnerTile();
			return false;
		}

		return true;
	}

	private function canValidateSpawnerBlock() : bool{
		if(!($this->chunk instanceof FullChunk) or !($this->getLevel() instanceof Level)){
			return false;
		}

		$loadedChunk = $this->getLevel()->getChunk($this->x >> 4, $this->z >> 4, false);
		return $loadedChunk === $this->chunk;
	}

	private function removeInvalidSpawnerTile(){
		if($this->chunk instanceof FullChunk){
			$this->chunk->setChanged();
		}
		$this->close();
	}
}
