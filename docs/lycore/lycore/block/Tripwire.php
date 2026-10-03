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

namespace lycore\block;

use lycore\entity\Entity;
use lycore\item\Item;
use lycore\item\Tool;
use lycore\level\Level;
use lycore\math\AxisAlignedBB;
use lycore\math\Vector3;
use lycore\Player;

class Tripwire extends Flowable{
	const POWERED = 0x01;
	const SUSPENDED = 0x02;
	const ATTACHED = 0x04;
	const DISARMED = 0x08;

	protected $id = self::TRIPWIRE;

	public function __construct($meta = 0){
		$this->meta = $meta;
	}

	public function getName() : string{
		return "Tripwire";
	}

	public function getToolType(){
		return Tool::TYPE_SHEARS;
	}

	public function getHardness(){
		return 0;
	}

	public function getResistance(){
		return 0;
	}

	public function hasEntityCollision(){
		return true;
	}

	public function getBoundingBox(){
		if($this->boundingBox === null){
			$this->boundingBox = new AxisAlignedBB($this->x, $this->y, $this->z, $this->x + 1, $this->y + 0.5, $this->z + 1);
		}
		return $this->boundingBox;
	}

	public function canPassThrough(){
		return true;
	}

	public function isPowered(){
		return ($this->meta & self::POWERED) !== 0;
	}

	public function isSuspended(){
		return ($this->meta & self::SUSPENDED) !== 0;
	}

	public function isAttached(){
		return ($this->meta & self::ATTACHED) !== 0;
	}

	public function isDisarmed(){
		return ($this->meta & self::DISARMED) !== 0;
	}

	public function setPowered($powered){
		$this->setFlag(self::POWERED, $powered);
	}

	public function setSuspended($suspended){
		$this->setFlag(self::SUSPENDED, $suspended);
	}

	public function setAttached($attached){
		$this->setFlag(self::ATTACHED, $attached);
	}

	public function setDisarmed($disarmed){
		$this->setFlag(self::DISARMED, $disarmed);
	}

	private function setFlag($flag, $enabled){
		if($enabled){
			$this->meta |= $flag;
		}else{
			$this->meta &= ~$flag;
		}
	}

	public function onEntityCollide(Entity $entity){
		if(!$this->getLevel()->getServer()->redstoneEnabled or !$entity->canTriggerWalking() or $this->isPowered()){
			return;
		}

		$this->setPowered(true);
		$this->getLevel()->setBlock($this, $this, true, false);
		$this->updateHook(false);
		$this->getLevel()->scheduleUpdate($this, 10);
	}

	private function updateHook($scheduleUpdate){
		if(!$this->getLevel()->getServer()->redstoneEnabled){
			return;
		}

		foreach([Vector3::SIDE_EAST, Vector3::SIDE_WEST, Vector3::SIDE_SOUTH, Vector3::SIDE_NORTH] as $side){
			for($distance = 1; $distance < TripwireHook::MAX_TRIPWIRE_CIRCUIT_LENGTH; ++$distance){
				$block = $this->getSide($side, $distance);
				if($block instanceof TripwireHook){
					if($block->getFacing() === Vector3::getOppositeSide($side)){
						$block->updateLine(false, true, $distance, $this);
						if($scheduleUpdate){
							$this->getLevel()->scheduleUpdate($block, 10);
						}
					}
					break;
				}

				if(!($block instanceof Tripwire)){
					break;
				}
			}
		}
	}

	public function onUpdate($type){
		if(!$this->getLevel()->getServer()->redstoneEnabled){
			return false;
		}

		if($type === Level::BLOCK_UPDATE_SCHEDULED){
			if(!$this->isPowered()){
				return Level::BLOCK_UPDATE_SCHEDULED;
			}

			foreach($this->getLevel()->getCollidingEntities($this->getBoundingBox()) as $entity){
				if($entity instanceof Entity and $entity->canTriggerWalking()){
					$this->getLevel()->scheduleUpdate($this, 10);
					return Level::BLOCK_UPDATE_SCHEDULED;
				}
			}

			$this->setPowered(false);
			$this->getLevel()->setBlock($this, $this, true, false);
			$this->updateHook(false);
			return Level::BLOCK_UPDATE_SCHEDULED;
		}

		return false;
	}

	public function place(Item $item, Block $block, Block $target, $face, $fx, $fy, $fz, Player $player = null){
		$this->getLevel()->setBlock($block, $this, true, true);
		$this->updateHook(false);
		return true;
	}

	public function onBreak(Item $item){
		if($item instanceof Tool and $item->isShears()){
			$this->setDisarmed(true);
			$this->getLevel()->setBlock($this, $this, true, false);
			$this->updateHook(false);
		}else{
			$this->setPowered(true);
			$this->updateHook(true);
		}

		$this->getLevel()->setBlock($this, new Air(), true, true);
		return true;
	}

	public function getDrops(Item $item) : array{
		return [
			[Item::STRING, 0, 1],
		];
	}
}
