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
use lycore\Player;
use lycore\math\Vector3;

class RedstoneTorch extends RedstoneSource{

	protected $id = self::REDSTONE_TORCH;
	protected $ignore = "";

	public function __construct($meta = 0){
		$this->meta = $meta;
	}

	public function getLightLevel(){
		return $this->id === self::REDSTONE_TORCH ? 7 : 0;
	}

	private function getTorchTempData(){
		$data = $this->getLevel()->getBlockTempData($this);
		if(is_array($data)){
			return $data + ["lastUpdateTime" => 0, "ignore" => ""];
		}

		return ["lastUpdateTime" => $data, "ignore" => ""];
	}

	public function getLastUpdateTime(){
		$data = $this->getTorchTempData();
		return $data["lastUpdateTime"];
	}

	public function setLastUpdateTimeNow(){
		$this->getLevel()->setBlockTempData($this, [
			"lastUpdateTime" => $this->getLevel()->getServer()->getTick(),
			"ignore" => ""
		]);
	}

	private function setScheduledIgnore($ignore){
		$data = $this->getTorchTempData();
		$this->ignore = $ignore;
		$this->getLevel()->setBlockTempData($this, [
			"lastUpdateTime" => $data["lastUpdateTime"],
			"ignore" => $ignore
		]);
	}

	private function popScheduledIgnore(){
		$data = $this->getTorchTempData();
		$this->getLevel()->setBlockTempData($this, [
			"lastUpdateTime" => $data["lastUpdateTime"],
			"ignore" => ""
		]);
		$this->ignore = "";
		return $data["ignore"];
	}

	public function canCalcTurn(){
		if(!parent::canCalc()) return false;
		if($this->getLevel()->getServer()->getTick() != $this->getLastUpdateTime()) return true;
		return ($this->canScheduleUpdate() ? Level::BLOCK_UPDATE_SCHEDULED : false);
	}

	public function canScheduleUpdate(){
		return $this->getLevel()->getServer()->allowFrequencyPulse;
	}

	public function getFrequency(){
		return $this->getLevel()->getServer()->pulseFrequency;
	}

	public function getPulseTickDelay() : int{
		return $this->canScheduleUpdate() ? $this->getLevel()->getServer()->getRedstonePulseTickDelay() : $this->tickRate();
	}

	public function tickRate() : int{
		return 2;
	}

	public function getName() : string{
		return "Redstone Torch";
	}

	private function getAttachedFace(){
		$faces = [
			1 => Vector3::SIDE_WEST,
			2 => Vector3::SIDE_EAST,
			3 => Vector3::SIDE_NORTH,
			4 => Vector3::SIDE_SOUTH,
			5 => Vector3::SIDE_DOWN,
			6 => Vector3::SIDE_DOWN,
			0 => Vector3::SIDE_DOWN,
		];

		return $faces[$this->meta] ?? Vector3::SIDE_DOWN;
	}

	public function getWeakPower($side){
		if(!$this->isActivated()){
			return 0;
		}

		return Vector3::getOppositeSide($side) === $this->getAttachedFace() ? 0 : $this->maxStrength;
	}

	public function getStrongPower($side){
		return $side === Vector3::SIDE_DOWN ? $this->getWeakPower($side) : 0;
	}

	private function updateAroundRedstoneExcept(Vector3 $pos, array $ignore = []){
		$this->level->updateAroundRedstone($pos, $ignore);
	}

	private function updateAllAroundRedstone(array $ignore = []){
		$this->updateAroundRedstoneExcept($this, $ignore);
		foreach(Block::BLOCK_SIDES as $side){
			if(!in_array($side, $ignore, true)){
				$this->updateAroundRedstoneExcept($this->getSide($side), [Vector3::getOppositeSide($side)]);
			}
		}
	}

	private function getSupportSide(){
		$faces = [
			1 => 4,
			2 => 5,
			3 => 2,
			4 => 3,
			5 => 0,
			6 => 0,
			0 => 0,
		];

		return $faces[$this->meta] ?? Vector3::SIDE_DOWN;
	}

	private function updateAfterPlace(){
		if(!$this->canCalc()){
			return;
		}

		$supportSide = $this->getSupportSide();
		if($this->isPoweredFromSupportSide()){
			$this->id = self::UNLIT_REDSTONE_TORCH;
			$this->getLevel()->setBlock($this, $this, true, false);
			$this->deactivateTorch([$supportSide]);
		}else{
			$this->activate([$supportSide]);
		}
	}

	private function isPoweredFromSupportSide() : bool{
		$supportSide = $this->getSupportSide();
		$side = $this->getSide($supportSide);
		if($side instanceof PistonBase and $side->isGettingPower()){
			return true;
		}

		return $this->getLevel()->isSidePowered($side, $supportSide);
	}

	public function turnOn($ignore = ""){
		$result = $this->canCalcTurn();
		$this->setLastUpdateTimeNow();
		if($result === true){
			if($this->getLevel()->checkAndHandleHighFrequencyRedstoneTransition($this, "torch:on")){
				return true;
			}
			$supportSide = $this->getSupportSide();
			$this->id = self::REDSTONE_TORCH;
			$this->getLevel()->setBlock($this, $this, true);
			$this->activateTorch([$supportSide], [$ignore]);
			return true;
		}elseif($result === Level::BLOCK_UPDATE_SCHEDULED){
			$this->setScheduledIgnore($ignore);
			$this->getLevel()->scheduleUpdate($this, $this->getPulseTickDelay());
			return true;
		}
		return false;
	}

	public function turnOff($ignore = ""){
		$result = $this->canCalcTurn();
		$this->setLastUpdateTimeNow();
		if($result === true){
			if($this->getLevel()->checkAndHandleHighFrequencyRedstoneTransition($this, "torch:off")){
				return true;
			}
			$supportSide = $this->getSupportSide();
			$this->id = self::UNLIT_REDSTONE_TORCH;
			$this->getLevel()->setBlock($this, $this, true);
			$this->deactivateTorch([$supportSide], [$ignore]);
			return true;
		}elseif($result === Level::BLOCK_UPDATE_SCHEDULED){
			$this->setScheduledIgnore($ignore);
			$this->getLevel()->scheduleUpdate($this, $this->getPulseTickDelay());
			return true;
		}
		return false;
	}

	public function activateTorch(array $ignore = [], $notCheck = []){
		if($this->canCalc()){
			$this->activated = true;
			/** @var Door $block */

			$sides = [Vector3::SIDE_EAST, Vector3::SIDE_WEST, Vector3::SIDE_SOUTH, Vector3::SIDE_NORTH, Vector3::SIDE_UP, Vector3::SIDE_DOWN];

			foreach($sides as $side){
				if(!in_array($side, $ignore)){
					$block = $this->getSide($side);
					if(!in_array($hash = Level::blockHash($block->x, $block->y, $block->z), $notCheck)){
						$this->activateBlock($block);
					}
				}
			}
			$this->updateAllAroundRedstone($ignore);
			//$this->lastUpdateTime = $this->getLevel()->getServer()->getTick();
		}
	}

	public function activate(array $ignore = []){
		$this->activateTorch($ignore);
	}

	public function deactivate(array $ignore = []){
		$this->deactivateTorch($ignore);
	}

	public function deactivateTorch(array $ignore = [], array $notCheck = []){
		if($this->canCalc()){
			$this->activated = false;
			/** @var Door $block */

			$sides = [Vector3::SIDE_EAST, Vector3::SIDE_WEST, Vector3::SIDE_SOUTH, Vector3::SIDE_NORTH];

			foreach($sides as $side){
				if(!in_array($side, $ignore)){
					$block = $this->getSide($side);
					if(!in_array($hash = Level::blockHash($block->x, $block->y, $block->z), $notCheck)){
						$this->deactivateBlock($block);
					}
				}
			}

			if(!in_array(Vector3::SIDE_DOWN, $ignore)){
				$block = $this->getSide(Vector3::SIDE_DOWN);
				if(!in_array($hash = Level::blockHash($block->x, $block->y, $block->z), $notCheck)){
					if(!$this->checkPower($block)){
						/** @var $block ActiveRedstoneLamp */
						if($block->getId() == Block::ACTIVE_REDSTONE_LAMP) $block->turnOff();
					}

					$block = $this->getSide(Vector3::SIDE_DOWN, 2);
					$this->deactivateBlock($block);
				}
			}
			$this->updateAllAroundRedstone($ignore);
			//$this->lastUpdateTime = $this->getLevel()->getServer()->getTick();
		}
	}

	public function onUpdate($type){
		$supportSide = $this->getSupportSide();
		if($type === Level::BLOCK_UPDATE_NORMAL or $type === Level::BLOCK_UPDATE_REDSTONE){
			$below = $this->getSide(0);
			$side = $this->getDamage();

			if($this->getSide($supportSide)->isTransparent() === true and
				!($side === 0 and ($below->getId() === self::FENCE or
						$below->getId() === self::COBBLE_WALL
					))
			){
				$this->getLevel()->useBreakOn($this);

				return Level::BLOCK_UPDATE_NORMAL;
			}
			if(!$this->getLevel()->isBlockTickPending($this, $this)){
				$this->getLevel()->scheduleUpdate($this, $this->getPulseTickDelay());
			}
			return $type;
		}

		if($type == Level::BLOCK_UPDATE_SCHEDULED){
			$powered = $this->isPoweredFromSupportSide();
			$ignore = $this->popScheduledIgnore();
			if($this->id === self::REDSTONE_TORCH and $powered){
				$this->turnOff($ignore);
			}elseif($this->id === self::UNLIT_REDSTONE_TORCH and !$powered){
				$this->turnOn($ignore);
			}
			return Level::BLOCK_UPDATE_SCHEDULED;
		}

		return false;
	}

	public function onBreak(Item $item){
		$this->getLevel()->setBlock($this, new Air(), true, false);
		$this->deactivate([$this->getSupportSide()]);
		$this->getLevel()->setBlockTempData($this);
	}

	public function place(Item $item, Block $block, Block $target, $face, $fx, $fy, $fz, Player $player = null){
		$below = $this->getSide(0);

		if($target->isTransparent() === false and $face !== 0){
			$faces = [
				1 => 5,
				2 => 4,
				3 => 3,
				4 => 2,
				5 => 1,
			];
			$this->meta = $faces[$face];
			$this->getLevel()->setBlock($block, $this, true, true);
			$this->updateAfterPlace();

			return true;
		}elseif(
			$below->isTransparent() === false or $below->getId() === self::FENCE or
			$below->getId() === self::COBBLE_WALL or
			$below->getId() == Block::INACTIVE_REDSTONE_LAMP or
			$below->getId() == Block::ACTIVE_REDSTONE_LAMP
		){
			$this->meta = 0;
			$this->getLevel()->setBlock($block, $this, true, true);
			$this->updateAfterPlace();

			return true;
		}

		return false;
	}

	public function getDrops(Item $item) : array{
		return [
			[Item::LIT_REDSTONE_TORCH, 0, 1],
		];
	}

	public function isActivated(Block $from = null){
		return $this->id === self::REDSTONE_TORCH;
	}
}
