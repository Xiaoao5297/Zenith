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
 * 移植自 lycore\block\RedstoneComparator，命名空间改为 pocketmine\block。
 * 当前核心没有 lycore 的红石查询引擎（getRedstonePower/strongPower 等），
 * 故输入强度改由邻居 RedstoneSource::getStrength() 读取，并按核心
 * activate/deactivate 传播模型驱动输出。
 */

namespace pocketmine\block;

use pocketmine\item\Item;
use pocketmine\level\Level;
use pocketmine\math\Vector3;
use pocketmine\nbt\tag\CompoundTag;
use pocketmine\nbt\tag\IntTag;
use pocketmine\nbt\tag\StringTag;
use pocketmine\Player;
use pocketmine\tile\Comparator as ComparatorTile;
use pocketmine\tile\Tile;

abstract class RedstoneComparator extends RedstoneSource{
	const MODE_COMPARE = 0;
	const MODE_SUBTRACT = 1;

	const META_FACING_MASK = 0x03;
	const META_SUBTRACT = 0x04;
	const META_POWERED = 0x08;

	public function __construct($meta = 0){
		$this->meta = $meta;
	}

	public function isSolid(){
		return true;
	}

	public function getHardness(){
		return 0.0;
	}

	public function getMode(){
		return ($this->meta & self::META_SUBTRACT) > 0 ? self::MODE_SUBTRACT : self::MODE_COMPARE;
	}

	public function getFacing(){
		switch($this->meta & self::META_FACING_MASK){
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

	public function isActivated(Block $from = null){
		return ($this->meta & self::META_POWERED) > 0;
	}

	protected function getInputStrength(){
		$source = $this->getSide($this->getOppositeDirection());
		if($source instanceof RedstoneSource){
			return $source->getStrength();
		}
		if($source->getId() === self::REDSTONE_WIRE){
			return $source->getDamage();
		}

		return 0;
	}

	protected function getSideStrength(){
		$left = $this->getSide($this->getLeftSide());
		$right = $this->getSide($this->getRightSide());
		$leftStrength = $left instanceof RedstoneSource ? $left->getStrength() : 0;
		$rightStrength = $right instanceof RedstoneSource ? $right->getStrength() : 0;

		return max($leftStrength, $rightStrength);
	}

	protected function calculateOutput(){
		$input = $this->getInputStrength();
		if($this->getMode() === self::MODE_SUBTRACT){
			return max($input - $this->getSideStrength(), 0);
		}

		return $input;
	}

	protected function getComparatorTile(){
		$tile = $this->level->getTile($this);
		if($tile instanceof ComparatorTile){
			return $tile;
		}

		return Tile::createTile(Tile::COMPARATOR, $this->level->getChunk($this->x >> 4, $this->z >> 4), new CompoundTag("", [
			new StringTag("id", Tile::COMPARATOR),
			new IntTag("x", $this->x),
			new IntTag("y", $this->y),
			new IntTag("z", $this->z),
			new IntTag("OutputSignal", 0),
		]));
	}

	public function updateState(){
		$output = $this->calculateOutput();
		$shouldBePowered = $output > 0;
		$isPowered = $this->isActivated();

		$tile = $this->getComparatorTile();
		if($tile instanceof ComparatorTile){
			$tile->setOutputSignal($output);
		}

		if($isPowered !== $shouldBePowered){
			$this->maxStrength = max(1, $output);
			$this->id = $shouldBePowered ? self::POWERED_COMPARATOR : self::UNPOWERED_COMPARATOR;
			if($shouldBePowered){
				$this->meta |= self::META_POWERED;
			}else{
				$this->meta &= ~self::META_POWERED;
			}
			$this->getLevel()->setBlock($this, $this, true, false);
			if($shouldBePowered){
				$this->activate();
			}else{
				$this->deactivate();
			}
		}
	}

	public function onUpdate($type){
		if($type === Level::BLOCK_UPDATE_NORMAL or $type === Level::BLOCK_UPDATE_REDSTONE or $type === Level::BLOCK_UPDATE_SCHEDULED){
			$this->updateState();
			return $type;
		}

		return false;
	}

	public function onActivate(Item $item, Player $player = null){
		$this->meta ^= self::META_SUBTRACT;
		$this->getLevel()->setBlock($this, $this, true, true);
		$this->updateState();
		return true;
	}

	public function place(Item $item, Block $block, Block $target, $face, $fx, $fy, $fz, Player $player = null){
		if($player instanceof Player){
			$this->meta = ($this->meta & (self::META_SUBTRACT | self::META_POWERED)) | self::getMetaFromYaw($player->yaw);
		}
		$this->getLevel()->setBlock($block, $this, true, true);
		$this->getComparatorTile();
		$this->updateState();
		return true;
	}

	protected static function getMetaFromYaw($yaw){
		$yaw = fmod($yaw, 360);
		if($yaw < 0){
			$yaw += 360;
		}

		return (((int) floor(($yaw * 4 / 360) + 0.5)) + 2) & 0x03;
	}

	public function getDrops(Item $item) : array{
		return [
			[Item::COMPARATOR, 0, 1],
		];
	}
}
