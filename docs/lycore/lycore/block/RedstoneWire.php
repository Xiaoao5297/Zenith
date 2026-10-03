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
use lycore\math\Vector3;
use lycore\Player;

class RedstoneWire extends RedstoneSource{
	const ON = 1;
	const OFF = 2;
	const PLACE = 3;
	const DESTROY = 4;

	protected $id = self::REDSTONE_WIRE;
	private $canProvidePower = true;

	public function __construct($meta = 0){
		$this->meta = $meta;
	}

	public function getName() : string{
		return "Redstone Wire";
	}

	public function getStrength(){
		return $this->meta;
	}

	public function isActivated(Block $from = null){
		return $this->meta > 0;
	}

	public function isPowerSource(){
		return $this->canProvidePower and $this->meta > 0;
	}

	private static function horizontalSides(){
		return [Vector3::SIDE_WEST, Vector3::SIDE_EAST, Vector3::SIDE_NORTH, Vector3::SIDE_SOUTH];
	}

	private static function verticalSides(){
		return [Vector3::SIDE_DOWN, Vector3::SIDE_UP];
	}

	private static function rotateY($side){
		switch($side){
			case Vector3::SIDE_NORTH:
				return Vector3::SIDE_EAST;
			case Vector3::SIDE_EAST:
				return Vector3::SIDE_SOUTH;
			case Vector3::SIDE_SOUTH:
				return Vector3::SIDE_WEST;
			case Vector3::SIDE_WEST:
				return Vector3::SIDE_NORTH;
			default:
				return $side;
		}
	}

	private static function rotateYCCW($side){
		switch($side){
			case Vector3::SIDE_NORTH:
				return Vector3::SIDE_WEST;
			case Vector3::SIDE_WEST:
				return Vector3::SIDE_SOUTH;
			case Vector3::SIDE_SOUTH:
				return Vector3::SIDE_EAST;
			case Vector3::SIDE_EAST:
				return Vector3::SIDE_NORTH;
			default:
				return $side;
		}
	}

	private static function isHorizontal($side){
		return in_array($side, self::horizontalSides(), true);
	}

	protected function canBePlacedOn(Block $block){
		return !($block instanceof Transparent) or $block->getId() === self::INACTIVE_REDSTONE_LAMP or $block->getId() === self::ACTIVE_REDSTONE_LAMP;
	}

	private function getIndirectPower(){
		$power = 0;
		foreach(Block::BLOCK_SIDES as $face){
			$blockPower = $this->getIndirectPowerAt($this->getSide($face), $face);
			if($blockPower >= 15){
				return 15;
			}
			if($blockPower > $power){
				$power = $blockPower;
			}
		}

		return $power;
	}

	private function getIndirectPowerAt(Vector3 $pos, $face){
		$block = $this->level->getBlock($pos);
		if($block->getId() === self::REDSTONE_WIRE){
			return 0;
		}

		if($block->isPowerSource()){
			return $block->getWeakPower($face);
		}

		return $block->isNormalBlock() ? $this->getStrongPowerAt($pos) : $block->getWeakPower($face);
	}

	private function getStrongPowerAt(Vector3 $pos, $direction = null){
		if($direction !== null){
			$block = $this->level->getBlock($pos);
			if($block->getId() === self::REDSTONE_WIRE){
				return 0;
			}

			return $block->getStrongPower($direction);
		}

		$power = 0;
		foreach(Block::BLOCK_SIDES as $side){
			$sidePower = $this->getStrongPowerAt($pos->getSide($side), $side);
			if($sidePower >= 15){
				return 15;
			}
			if($sidePower > $power){
				$power = $sidePower;
			}
		}

		return $power;
	}

	private function getMaxCurrentStrength(Vector3 $pos, $maxStrength){
		if($this->level->getBlockIdAt((int) $pos->x, (int) $pos->y, (int) $pos->z) !== self::REDSTONE_WIRE){
			return $maxStrength;
		}

		return max($this->level->getBlockDataAt((int) $pos->x, (int) $pos->y, (int) $pos->z), $maxStrength);
	}

	private function calculateCurrentChanges($force = false){
		$meta = $this->meta;
		$maxStrength = $meta;

		$this->canProvidePower = false;
		$power = $this->getIndirectPower();
		$this->canProvidePower = true;

		if($power > 0 and $power > $maxStrength - 1){
			$maxStrength = $power;
		}

		$strength = 0;
		foreach(self::horizontalSides() as $face){
			$side = $this->getSide($face);
			$strength = $this->getMaxCurrentStrength($side, $strength);

			$sideIsNormal = $this->level->getBlock($side)->isNormalBlock();
			if($sideIsNormal and !$this->getSide(Vector3::SIDE_UP)->isNormalBlock()){
				$strength = $this->getMaxCurrentStrength($side->getSide(Vector3::SIDE_UP), $strength);
			}elseif(!$sideIsNormal){
				$strength = $this->getMaxCurrentStrength($side->getSide(Vector3::SIDE_DOWN), $strength);
			}
		}

		if($strength > $maxStrength){
			$maxStrength = $strength - 1;
		}elseif($maxStrength > 0){
			--$maxStrength;
		}else{
			$maxStrength = 0;
		}

		if($power > $maxStrength - 1){
			$maxStrength = $power;
		}elseif($power < $maxStrength and $strength <= $maxStrength){
			$maxStrength = max($power, $strength - 1);
		}

		$maxStrength = max(0, min(15, $maxStrength));
		if($meta !== $maxStrength){
			if($this->level->checkAndHandleHighFrequencyRedstoneTransition($this, "wire:" . $maxStrength)){
				return;
			}
			$this->meta = $maxStrength;
			$this->level->setBlock($this, $this, false, false);
			$this->level->updateAroundRedstone($this, null);
			foreach(Block::BLOCK_SIDES as $face){
				$this->level->updateAroundRedstone($this->getSide($face), Vector3::getOppositeSide($face));
			}
		}elseif($force){
			foreach(Block::BLOCK_SIDES as $face){
				$this->level->updateAroundRedstone($this->getSide($face), Vector3::getOppositeSide($face));
			}
		}
	}

	public static function canConnectTo(Block $block, $side = null){
		if($block->getId() === self::REDSTONE_WIRE){
			return true;
		}

		if(RedstoneDiode::isDiode($block)){
			$facing = $block->getFacing();
			return $side === null or $facing === $side or Vector3::getOppositeSide($facing) === $side;
		}

		return $block->isPowerSource() and $side !== null;
	}

	protected static function canConnectUpwardsTo(Block $block){
		return self::canConnectTo($block, null);
	}

	private function isPowerSourceAt($side){
		$sideBlock = $this->getSide($side);
		$sideBlockIsNormal = $sideBlock->isNormalBlock();

		return ($sideBlockIsNormal and !$this->getSide(Vector3::SIDE_UP)->isNormalBlock() and self::canConnectUpwardsTo($sideBlock->getSide(Vector3::SIDE_UP)))
			or self::canConnectTo($sideBlock, $side)
			or (!$sideBlockIsNormal and self::canConnectUpwardsTo($sideBlock->getSide(Vector3::SIDE_DOWN)));
	}

	public function getWeakPower($side){
		if(!$this->canProvidePower){
			return 0;
		}

		$power = $this->meta;
		if($power <= 0){
			return 0;
		}

		if($side === Vector3::SIDE_UP){
			return $power;
		}

		$connected = [];
		foreach(self::horizontalSides() as $face){
			if($this->isPowerSourceAt($face)){
				$connected[$face] = true;
			}
		}

		if(self::isHorizontal($side) and count($connected) === 0){
			return $power;
		}

		if(isset($connected[$side]) and !isset($connected[self::rotateYCCW($side)]) and !isset($connected[self::rotateY($side)])){
			return $power;
		}

		return 0;
	}

	public function getStrongPower($side){
		return $this->canProvidePower ? $this->getWeakPower($side) : 0;
	}

	public function onUpdate($type){
		if($type !== Level::BLOCK_UPDATE_NORMAL and $type !== Level::BLOCK_UPDATE_REDSTONE){
			return false;
		}

		if($type === Level::BLOCK_UPDATE_NORMAL and !$this->canBePlacedOn($this->getSide(Vector3::SIDE_DOWN))){
			$this->level->useBreakOn($this);
			return Level::BLOCK_UPDATE_NORMAL;
		}

		if($this->level->getBlockIdAt((int) $this->x, (int) $this->y, (int) $this->z) !== $this->id){
			return false;
		}

		$this->calculateCurrentChanges(false);
		return Level::BLOCK_UPDATE_REDSTONE;
	}

	public function place(Item $item, Block $block, Block $target, $face, $fx, $fy, $fz, Player $player = null){
		if(!$this->canBePlacedOn($block->getSide(Vector3::SIDE_DOWN))){
			return false;
		}

		$this->level->setBlock($block, $this, true, false);
		$this->calculateCurrentChanges(true);
		foreach(self::verticalSides() as $side){
			$this->level->updateAroundRedstone($this->getSide($side), Vector3::getOppositeSide($side));
		}
		foreach(self::verticalSides() as $side){
			$this->updateAround($this->getSide($side), Vector3::getOppositeSide($side));
		}
		foreach(self::horizontalSides() as $side){
			$near = $this->getSide($side);
			if($near->isNormalBlock()){
				$this->updateAround($near->getSide(Vector3::SIDE_UP), Vector3::SIDE_DOWN);
			}else{
				$this->updateAround($near->getSide(Vector3::SIDE_DOWN), Vector3::SIDE_UP);
			}
		}

		return true;
	}

	private function updateAround(Vector3 $pos, $face){
		if($this->level->getBlockIdAt((int) $pos->x, (int) $pos->y, (int) $pos->z) === self::REDSTONE_WIRE){
			$this->level->updateAroundRedstone($pos, $face);
			foreach(Block::BLOCK_SIDES as $side){
				$this->level->updateAroundRedstone($pos->getSide($side), Vector3::getOppositeSide($side));
			}
		}
	}

	public function calcSignal($strength = 15, $type = self::ON, array $hasUpdated = []){
		if($type === self::DESTROY){
			$this->level->setBlock($this, new Air(), true, false);
			$this->level->updateAroundRedstone($this, null);
			return $hasUpdated;
		}

		$this->calculateCurrentChanges(true);
		return $hasUpdated;
	}

	public function onBreak(Item $item){
		$this->level->setBlock($this, new Air(), true, true);
		$this->level->updateAroundRedstone($this, null);
		foreach(Block::BLOCK_SIDES as $side){
			$this->level->updateAroundRedstone($this->getSide($side), null);
		}

		return true;
	}

	public function getDrops(Item $item) : array{
		return [
			[Item::REDSTONE, 0, 1],
		];
	}
}
