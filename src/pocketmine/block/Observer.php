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
 * 移植自 lycore\block\Observer，命名空间改为 pocketmine\block。
 */

namespace pocketmine\block;

use pocketmine\item\Item;
use pocketmine\item\Tool;
use pocketmine\event\redstone\BlockRedstoneEvent;
use pocketmine\event\redstone\RedstoneUpdateEvent;
use pocketmine\level\Level;
use pocketmine\math\Vector3;
use pocketmine\Player;

class Observer extends Solid{
	const META_FACING_MASK = 0x07;
	const META_POWERED = 0x08;

	protected $id = self::OBSERVER;

	public function __construct($meta = 0){
		$this->meta = $meta & 0x0f;
	}

	public function getName() : string{
		return "Observer";
	}

	public function getHardness(){
		return 3.5;
	}

	public function getResistance(){
		return 17.5;
	}

	public function getToolType(){
		return Tool::TYPE_PICKAXE;
	}

	public function canBeBrokenWith(Item $item){
		return $item->isPickaxe() >= Tool::TIER_WOODEN;
	}

	public function getDrops(Item $item) : array{
		if($item->isPickaxe() >= Tool::TIER_WOODEN){
			return [
				[Item::OBSERVER, 0, 1],
			];
		}

		return [];
	}

	public function getFacing(){
		$facing = $this->meta & self::META_FACING_MASK;
		return $facing <= Vector3::SIDE_EAST ? $facing : Vector3::SIDE_NORTH;
	}

	public function setFacing($facing){
		$this->meta = ($this->meta & self::META_POWERED) | ((int) $facing & self::META_FACING_MASK);
	}

	public function isPowered(){
		return ($this->meta & self::META_POWERED) === self::META_POWERED;
	}

	public function setPowered($powered){
		if($powered){
			$this->meta |= self::META_POWERED;
		}else{
			$this->meta &= self::META_FACING_MASK;
		}
	}

	public function place(Item $item, Block $block, Block $target, $face, $fx, $fy, $fz, Player $player = null){
		if($player instanceof Player){
			$eyeY = $player->y + $player->getEyeHeight();
			if(abs($player->getFloorX() - $block->x) <= 1 and abs($player->getFloorZ() - $block->z) <= 1){
				if($eyeY - $block->y > 2){
					$this->setFacing(Vector3::SIDE_DOWN);
				}elseif($block->y - $eyeY > 0){
					$this->setFacing(Vector3::SIDE_UP);
				}else{
					$this->setFacing(self::playerDirectionToSide($player->getDirection()));
				}
			}else{
				$this->setFacing(self::playerDirectionToSide($player->getDirection()));
			}
		}

		$this->getLevel()->setBlock($block, $this, true, true);
		return true;
	}

	private static function playerDirectionToSide($direction){
		switch((int) $direction){
			case 0:
				return Vector3::SIDE_EAST;
			case 1:
				return Vector3::SIDE_SOUTH;
			case 2:
				return Vector3::SIDE_WEST;
			case 3:
				return Vector3::SIDE_NORTH;
			default:
				return Vector3::SIDE_NORTH;
		}
	}

	public function isPowerSource(){
		return true;
	}

	public function getWeakPower($side){
		return $this->getStrongPower($side);
	}

	public function getStrongPower($side){
		return ($this->isPowered() and $side === Vector3::getOppositeSide($this->getFacing())) ? 15 : 0;
	}

	public function onNeighborChange($side){
		if($side !== Vector3::getOppositeSide($this->getFacing()) or !$this->canUseRedstone()){
			return;
		}

		if(!$this->getLevel()->isBlockTickPending($this, $this)){
			$ev = new RedstoneUpdateEvent($this);
			$this->getLevel()->getServer()->getPluginManager()->callEvent($ev);
			if($ev->isCancelled()){
				return;
			}
			$this->getLevel()->scheduleUpdate($this, 1);
		}
	}

	public function onUpdate($type){
		if($type !== Level::BLOCK_UPDATE_SCHEDULED and $type !== Level::BLOCK_UPDATE_MOVED){
			return false;
		}

		$ev = new RedstoneUpdateEvent($this);
		$this->getLevel()->getServer()->getPluginManager()->callEvent($ev);
		if($ev->isCancelled()){
			return false;
		}

		$oldPower = $this->isPowered() ? 15 : 0;
		$this->setPowered(!$this->isPowered());
		$newPower = $this->isPowered() ? 15 : 0;
		$this->getLevel()->getServer()->getPluginManager()->callEvent(new BlockRedstoneEvent($this, $oldPower, $newPower));
		$this->getLevel()->setBlock($this, $this, true, false);
		$output = $this->getSide($this->getFacing());
		$output->onUpdate(Level::BLOCK_UPDATE_REDSTONE);
		$this->getLevel()->updateAroundRedstone($output, null);

		if($this->isPowered()){
			$this->getLevel()->scheduleUpdate($this, 2);
		}

		return $type;
	}

	private function canUseRedstone(){
		if(!$this->isValid()){
			return false;
		}

		$server = $this->getLevel()->getServer();
		return !isset($server->redstoneEnabled) or $server->redstoneEnabled;
	}
}
