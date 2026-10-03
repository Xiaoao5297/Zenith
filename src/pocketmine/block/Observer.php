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
 * 当前核心没有红石查询引擎（getRedstonePower 等），改用既有
 * RedstoneSource::activate/deactivate 传播模型与 BLOCK_UPDATE_NORMAL 触发。
 */

namespace pocketmine\block;

use pocketmine\item\Item;
use pocketmine\item\Tool;
use pocketmine\level\Level;
use pocketmine\math\Vector3;
use pocketmine\Player;

class Observer extends RedstoneSource{
	const META_FACING_MASK = 0x07;
	const META_POWERED = 0x08;

	protected $id = self::OBSERVER;

	public function __construct($meta = 0){
		$this->meta = $meta & 0x0f;
	}

	public function getName() : string{
		return "Observer";
	}

	public function isSolid(){
		return true;
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

	public function isActivated(Block $from = null){
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
			$this->setFacing(self::playerDirectionToSide($player->getDirection()));
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

	public function onUpdate($type){
		if($type === Level::BLOCK_UPDATE_NORMAL){
			if(!$this->isActivated()){
				$this->setPowered(true);
				$this->getLevel()->setBlock($this, $this, true, false);
				$this->activate();
				$this->getLevel()->scheduleUpdate($this, 2);
			}
			return Level::BLOCK_UPDATE_NORMAL;
		}

		if($type === Level::BLOCK_UPDATE_SCHEDULED){
			if($this->isActivated()){
				$this->setPowered(false);
				$this->getLevel()->setBlock($this, $this, true, false);
				$this->deactivate();
			}
			return Level::BLOCK_UPDATE_SCHEDULED;
		}

		return false;
	}
}
