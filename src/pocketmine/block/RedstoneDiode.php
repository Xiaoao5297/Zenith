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
 * 移植自 lycore\block\RedstoneDiode，命名空间改为 pocketmine\block。
 * getPulseTickDelay() 简化为 getDelay()（核心无 allowFrequencyPulse 配置）。
 */

namespace pocketmine\block;

use pocketmine\item\Item;
use pocketmine\level\Level;
use pocketmine\math\AxisAlignedBB;
use pocketmine\math\Vector3;
use pocketmine\Player;

abstract class RedstoneDiode extends RedstoneSource{
	protected $isPowered = false;

	public function __construct($meta = 0){
		$this->meta = $meta;
	}

	protected function recalculateBoundingBox(){
		return new AxisAlignedBB($this->x, $this->y, $this->z, $this->x + 1, $this->y + 0.125, $this->z + 1);
	}

	public function canBeFlowedInto(){
		return false;
	}

	public function canPassThrough(){
		return false;
	}

	public function canBeActivated() : bool{
		return true;
	}

	public function isPowerSource(){
		return true;
	}

	public function isPowered(){
		return $this->isPowered;
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

	public function getDirection() : int{
		return $this->getFacing();
	}

	public function getOppositeDirection() : int{
		return Vector3::getOppositeSide($this->getFacing());
	}

	protected static function getMetaFromYaw($yaw){
		$yaw = fmod($yaw, 360);
		if($yaw < 0){
			$yaw += 360;
		}

		return (((int) floor(($yaw * 4 / 360) + 0.5)) + 2) & 0x03;
	}

	protected function getLeftSide(){
		switch($this->getFacing()){
			case Vector3::SIDE_NORTH:
				return Vector3::SIDE_WEST;
			case Vector3::SIDE_SOUTH:
				return Vector3::SIDE_EAST;
			case Vector3::SIDE_EAST:
				return Vector3::SIDE_NORTH;
			default:
				return Vector3::SIDE_SOUTH;
		}
	}

	protected function getRightSide(){
		return Vector3::getOppositeSide($this->getLeftSide());
	}

	abstract protected function getDelay();

	abstract protected function getPowered();

	abstract protected function getUnpowered();

	protected function getPulseTickDelay() : int{
		return $this->getDelay();
	}

	protected function getRedstoneSignal(){
		return 15;
	}

	public function isLocked(){
		return false;
	}

	protected function getInputSide(){
		return $this->getFacing();
	}

	protected function calculateInputStrength(){
		$inputSide = $this->getInputSide();
		$source = $this->getSide($inputSide);
		$power = $this->level->getRedstonePower($source, $this->getFacing());

		if($power >= 15){
			return 15;
		}

		return max($power, $source->getId() === self::REDSTONE_WIRE ? $source->getDamage() : 0);
	}

	protected function getPowerOnSides(){
		return max(
			$this->getPowerOnSide($this->getSide($this->getLeftSide()), $this->getRightSide()),
			$this->getPowerOnSide($this->getSide($this->getRightSide()), $this->getLeftSide())
		);
	}

	protected function getPowerOnSide(Block $block, $sideFromSource){
		if(!$this->isAlternateInput($block)){
			return 0;
		}

		if($block->getId() === self::REDSTONE_BLOCK){
			return 15;
		}

		if($block->getId() === self::REDSTONE_WIRE){
			return $block->getDamage();
		}

		return $this->level->getStrongPower($block, $sideFromSource);
	}

	protected function isAlternateInput(Block $block){
		return $block->isPowerSource();
	}

	public static function isDiode(Block $block){
		return $block instanceof RedstoneDiode;
	}

	public function shouldBePowered(){
		return $this->calculateInputStrength() > 0;
	}

	public function updateState(){
		if($this->isLocked()){
			return;
		}

		$shouldBePowered = $this->shouldBePowered();
		if(($this->isPowered() and !$shouldBePowered) or (!$this->isPowered() and $shouldBePowered)){
			if(!$this->level->isBlockTickPending($this, $this)){
				$this->level->scheduleUpdate($this, $this->getPulseTickDelay());
			}
		}
	}

	public function onUpdate($type){
		if($type === Level::BLOCK_UPDATE_SCHEDULED){
			if(!$this->isLocked()){
				$shouldBePowered = $this->shouldBePowered();
				if($this->isPowered() and !$shouldBePowered){
					if($this->level->checkAndHandleHighFrequencyRedstoneTransition($this, "diode:off")){
						return Level::BLOCK_UPDATE_SCHEDULED;
					}
					$this->level->setBlock($this, $this->getUnpowered(), true, true);
					$this->level->updateAroundRedstone($this->getSide($this->getOppositeDirection()), null);
				}elseif(!$this->isPowered()){
					if($this->level->checkAndHandleHighFrequencyRedstoneTransition($this, "diode:on")){
						return Level::BLOCK_UPDATE_SCHEDULED;
					}
					$this->level->setBlock($this, $this->getPowered(), true, true);
					$this->level->updateAroundRedstone($this->getSide($this->getOppositeDirection()), null);
					if(!$shouldBePowered){
						$this->level->scheduleUpdate($this, $this->getPulseTickDelay());
					}
				}
			}

			return Level::BLOCK_UPDATE_SCHEDULED;
		}

		if($type === Level::BLOCK_UPDATE_NORMAL or $type === Level::BLOCK_UPDATE_REDSTONE){
			$below = $this->getSide(Vector3::SIDE_DOWN);
			if($type === Level::BLOCK_UPDATE_NORMAL and $below instanceof Transparent){
				$this->level->useBreakOn($this);
				return Level::BLOCK_UPDATE_NORMAL;
			}
			$this->updateState();
			return $type;
		}

		return false;
	}

	public function place(Item $item, Block $block, Block $target, $face, $fx, $fy, $fz, Player $player = null){
		$below = $block->getSide(Vector3::SIDE_DOWN);
		if($below instanceof Transparent){
			return false;
		}

		if($player instanceof Player){
			$this->meta = ($this->meta & 0x0c) | self::getMetaFromYaw($player->yaw);
		}

		$this->level->setBlock($block, $this, true, true);
		if($this->shouldBePowered()){
			$this->level->scheduleUpdate($this, $this->getPulseTickDelay());
		}

		return true;
	}

	public function onBreak(Item $item){
		$this->level->setBlock($this, new Air(), true, true);
		$this->level->updateAroundRedstone($this, null);
		return true;
	}

	public function getWeakPower($side){
		return ($this->isPowered() && $side === $this->getFacing()) ? $this->getRedstoneSignal() : 0;
	}

	public function getStrongPower($side){
		return $this->getWeakPower($side);
	}

	public function isActivated(Block $from = null){
		if(!$this->isPowered()){
			return false;
		}
		return !$from instanceof Block || $from->equals($this->getSide($this->getOppositeDirection()));
	}

	public function activate(array $ignore = []){
		$this->updateState();
	}

	public function deactivate(array $ignore = []){
		$this->updateState();
	}
}
