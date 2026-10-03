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
use lycore\block\Fence;
use lycore\block\NetherBrickFence;
use lycore\event\entity\EntityDamageEvent;
use lycore\item\Item as ItemItem;
use lycore\level\format\FullChunk;
use lycore\level\Level;
use lycore\math\AxisAlignedBB;
use lycore\math\Vector3;
use lycore\nbt\tag\CompoundTag;
use lycore\nbt\tag\DoubleTag;
use lycore\nbt\tag\FloatTag;
use lycore\nbt\tag\ListTag;
use lycore\network\protocol\AddEntityPacket;
use lycore\network\protocol\ProtocolCompatibility;
use lycore\Player;

class LeashKnot extends Entity{
	const NETWORK_ID = 88;

	public $width = 0.375;
	public $length = 0.375;
	public $height = 0.5;
	public $canCollide = false;

	public function initEntity(){
		$this->setMaxHealth(1);
		parent::initEntity();
	}

	public function getName() : string{
		return "Leash Knot";
	}

	public static function isSupportedFence(Block $block) : bool{
		return $block instanceof Fence or $block instanceof NetherBrickFence;
	}

	public static function getOrCreate(Level $level, Block $fence){
		$knot = self::findOnFence($level, $fence);
		if($knot instanceof LeashKnot){
			return $knot;
		}

		$chunk = $level->getChunk($fence->getX() >> 4, $fence->getZ() >> 4, true);
		if(!($chunk instanceof FullChunk)){
			return null;
		}

		$knot = new self($chunk, self::createNBT($fence));
		$knot->spawnToAll();
		return $knot;
	}

	public static function findOnFence(Level $level, Block $fence){
		$bb = new AxisAlignedBB(
			$fence->getX(),
			$fence->getY(),
			$fence->getZ(),
			$fence->getX() + 1,
			$fence->getY() + 1,
			$fence->getZ() + 1
		);

		foreach($level->getNearbyEntities($bb) as $entity){
			if($entity instanceof LeashKnot and $entity->isOnFence($fence)){
				return $entity;
			}
		}

		return null;
	}

	public static function createNBT(Block $fence) : CompoundTag{
		return new CompoundTag("", [
			"Pos" => new ListTag("Pos", [
				new DoubleTag("", $fence->getX() + 0.5),
				new DoubleTag("", $fence->getY() + 0.5),
				new DoubleTag("", $fence->getZ() + 0.5),
			]),
			"Motion" => new ListTag("Motion", [
				new DoubleTag("", 0),
				new DoubleTag("", 0),
				new DoubleTag("", 0),
			]),
			"Rotation" => new ListTag("Rotation", [
				new FloatTag("", 0),
				new FloatTag("", 0),
			]),
		]);
	}

	public function isOnFence(Block $fence) : bool{
		return (int) floor($this->x) === $fence->getX() and (int) floor($this->y) === $fence->getY() and (int) floor($this->z) === $fence->getZ();
	}

	protected function updateMovement(){
	}

	public function onUpdate($currentTick){
		if($this->closed){
			return false;
		}

		$block = $this->level->getBlock(new Vector3((int) floor($this->x), (int) floor($this->y), (int) floor($this->z)));
		if(!self::isSupportedFence($block)){
			$this->close();
			return false;
		}

		return parent::onUpdate($currentTick);
	}

	public function attack($damage, EntityDamageEvent $source){
		$result = parent::attack($damage, $source);
		if(!$source->isCancelled()){
			$this->close();
		}
		return $result;
	}

	public function close(){
		if(!$this->closed and $this->level !== null and !$this->level->getServer()->isWorldNonLivingEntityDropsDisabled($this->level)){
			foreach($this->getDrops() as $item){
				$this->level->dropItem($this, $item);
			}
		}

		parent::close();
	}

	public function getDrops(){
		$count = 0;
		if($this->level !== null){
			foreach($this->level->getEntities() as $entity){
				if($entity !== $this and $entity instanceof Entity and $entity->isLeashedTo($this)){
					++$count;
				}
			}
		}

		return $count > 0 ? [ItemItem::get(ItemItem::LEAD, 0, $count)] : [];
	}

	public function spawnTo(Player $player){
		if(!ProtocolCompatibility::isProtocol015((int) $player->getProtocol())){
			return;
		}

		$pk = new AddEntityPacket();
		$pk->eid = $this->getId();
		$pk->type = self::NETWORK_ID;
		$pk->x = $this->x;
		$pk->y = $this->y;
		$pk->z = $this->z;
		$pk->speedX = 0;
		$pk->speedY = 0;
		$pk->speedZ = 0;
		$pk->yaw = 0;
		$pk->pitch = 0;
		$pk->modifiers = 0;
		$pk->metadata = $this->dataProperties;
		$player->dataPacket($pk);

		parent::spawnTo($player);
	}
}
