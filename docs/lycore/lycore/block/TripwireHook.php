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

use lycore\item\Item;
use lycore\level\Level;
use lycore\math\AxisAlignedBB;
use lycore\math\Vector3;
use lycore\Player;

class TripwireHook extends Flowable{
	const MAX_TRIPWIRE_CIRCUIT_LENGTH = 42;
	const ATTACHED = 0x04;
	const POWERED = 0x08;

	protected $id = self::TRIPWIRE_HOOK;

	public function __construct($meta = 0){
		$this->meta = $meta;
	}

	public function getName() : string{
		return "Tripwire Hook";
	}

	public function getHardness(){
		return 0;
	}

	public function getResistance(){
		return 0;
	}

	public function getBoundingBox(){
		if($this->boundingBox === null){
			$this->boundingBox = new AxisAlignedBB($this->x, $this->y, $this->z, $this->x + 1, $this->y + 1, $this->z + 1);
		}
		return $this->boundingBox;
	}

	public function canPassThrough(){
		return false;
	}

	public function isPowerSource(){
		return true;
	}

	public function getFacing(){
		switch($this->meta & 0x03){
			case 0:
				return Vector3::SIDE_SOUTH;
			case 1:
				return Vector3::SIDE_WEST;
			case 2:
				return Vector3::SIDE_NORTH;
			default:
				return Vector3::SIDE_EAST;
		}
	}

	public function setFacing($face){
		switch($face){
			case Vector3::SIDE_SOUTH:
				$direction = 0;
				break;
			case Vector3::SIDE_WEST:
				$direction = 1;
				break;
			case Vector3::SIDE_NORTH:
				$direction = 2;
				break;
			case Vector3::SIDE_EAST:
				$direction = 3;
				break;
			default:
				return;
		}

		$this->meta = ($this->meta & 0x0c) | $direction;
	}

	public function isAttached(){
		return ($this->meta & self::ATTACHED) !== 0;
	}

	public function isPowered(){
		return ($this->meta & self::POWERED) !== 0;
	}

	public function setAttached($attached){
		$this->setFlag(self::ATTACHED, $attached);
	}

	public function setPowered($powered){
		$this->setFlag(self::POWERED, $powered);
	}

	private function setFlag($flag, $enabled){
		if($enabled){
			$this->meta |= $flag;
		}else{
			$this->meta &= ~$flag;
		}
	}

	public function getWeakPower($side){
		return $this->isPowered() ? 15 : 0;
	}

	public function getStrongPower($side){
		return ($this->isPowered() and $side === $this->getFacing()) ? 15 : 0;
	}

	private function canAttachTo(Block $support){
		return $support->isNormalBlock() or $support instanceof Glass;
	}

	private function isSupported(){
		$support = $this->getSide(Vector3::getOppositeSide($this->getFacing()));
		return $this->canAttachTo($support);
	}

	public function onUpdate($type){
		if($type === Level::BLOCK_UPDATE_NORMAL){
			if(!$this->isSupported()){
				$this->getLevel()->useBreakOn($this);
			}
			return Level::BLOCK_UPDATE_NORMAL;
		}

		if($type === Level::BLOCK_UPDATE_SCHEDULED){
			$this->updateLine(false, true);
			return Level::BLOCK_UPDATE_SCHEDULED;
		}

		return false;
	}

	public function place(Item $item, Block $block, Block $target, $face, $fx, $fy, $fz, Player $player = null){
		if($face === Vector3::SIDE_UP or $face === Vector3::SIDE_DOWN){
			return false;
		}

		$support = $this->getSide(Vector3::getOppositeSide($face));
		if(!$this->canAttachTo($support)){
			return false;
		}

		$this->setFacing($face);
		$this->getLevel()->setBlock($block, $this, true, true);
		$this->updateLine(false, false);
		return true;
	}

	public function onBreak(Item $item){
		$attached = $this->isAttached();
		$powered = $this->isPowered();
		if($attached or $powered){
			$this->updateLine(true, false);
		}

		$this->getLevel()->setBlock($this, new Air(), true, true);
		if($powered){
			$this->getLevel()->updateAroundRedstone($this);
			$this->getLevel()->updateAroundRedstone($this->getSide(Vector3::getOppositeSide($this->getFacing())));
		}
		return true;
	}

	public function updateLine($isHookBroken, $doUpdateAroundHook, $eventDistance = -1, Tripwire $eventBlock = null){
		if(!$this->getLevel()->getServer()->redstoneEnabled){
			return;
		}

		$facing = $this->getFacing();
		$wasConnected = $this->isAttached();
		$wasPowered = $this->isPowered();
		$isConnected = !$isHookBroken;
		$isPowered = false;
		$pairedHookDistance = -1;
		$line = [];

		for($steps = 1; $steps < self::MAX_TRIPWIRE_CIRCUIT_LENGTH; ++$steps){
			$block = $this->getSide($facing, $steps);
			if($block instanceof TripwireHook){
				if($block->getFacing() === Vector3::getOppositeSide($facing)){
					$pairedHookDistance = $steps;
				}
				break;
			}

			if($steps === $eventDistance and $eventBlock !== null){
				$block = $eventBlock;
			}

			if(!($block instanceof Tripwire)){
				$isConnected = false;
				$line[$steps] = null;
				continue;
			}

			$notDisarmed = !$block->isDisarmed();
			$isPowered = $isPowered || ($notDisarmed && $block->isPowered());
			if($steps === $eventDistance){
				$this->getLevel()->scheduleUpdate($this, 10);
				$isConnected = $isConnected && $notDisarmed;
			}
			$line[$steps] = $block;
		}

		$foundPairedHook = $pairedHookDistance > 1;
		$isConnected = $isConnected && $foundPairedHook;
		$isPowered = $isPowered && $isConnected;

		if($foundPairedHook){
			$pairedPos = $this->getSide($facing, $pairedHookDistance);
			$pairedHook = Block::get(self::TRIPWIRE_HOOK, 0);
			$pairedHook->position($pairedPos);
			$pairedHook->setFacing(Vector3::getOppositeSide($facing));
			$pairedHook->setAttached($isConnected);
			$pairedHook->setPowered($isPowered);
			$this->getLevel()->setBlock($pairedPos, $pairedHook, true, true);
			$this->getLevel()->updateAroundRedstone($pairedPos);
			$this->getLevel()->updateAroundRedstone($pairedPos->getSide($facing));
		}

		if(!$isHookBroken){
			$this->setAttached($isConnected);
			$this->setPowered($isPowered);
			$this->getLevel()->setBlock($this, $this, true, true);
			if($doUpdateAroundHook or $wasPowered !== $isPowered){
				$this->getLevel()->updateAroundRedstone($this);
				$this->getLevel()->updateAroundRedstone($this->getSide(Vector3::getOppositeSide($facing)));
			}
		}

		if($wasConnected === $isConnected){
			return;
		}

		for($steps = 1; $steps < $pairedHookDistance; ++$steps){
			if(isset($line[$steps]) and $line[$steps] instanceof Tripwire){
				$wire = $line[$steps];
				$wire->setAttached($isConnected);
				$this->getLevel()->setBlock($this->getSide($facing, $steps), $wire, true, true);
			}
		}
	}

	public function getDrops(Item $item) : array{
		return [
			[Item::TRIPWIRE_HOOK, 0, 1],
		];
	}
}
