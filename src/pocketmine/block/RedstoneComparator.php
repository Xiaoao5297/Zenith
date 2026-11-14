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
 */

namespace pocketmine\block;

use pocketmine\item\Item;
use pocketmine\level\Level;
use pocketmine\nbt\tag\CompoundTag;
use pocketmine\nbt\tag\IntTag;
use pocketmine\nbt\tag\StringTag;
use pocketmine\Player;
use pocketmine\tile\Comparator as ComparatorTile;
use pocketmine\tile\Tile;

abstract class RedstoneComparator extends RedstoneDiode{
	const MODE_COMPARE = 0;
	const MODE_SUBTRACT = 1;

	protected function getDelay(){
		return 2;
	}

	public function getMode(){
		return ($this->meta & 0x04) > 0 ? self::MODE_SUBTRACT : self::MODE_COMPARE;
	}

	protected function getPowered(){
		return Block::get(self::POWERED_COMPARATOR, $this->meta);
	}

	protected function getUnpowered(){
		return Block::get(self::UNPOWERED_COMPARATOR, $this->meta);
	}

	public function isPowered(){
		return $this->isPowered or ($this->meta & 0x08) > 0;
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

	protected function getRedstoneSignal(){
		$tile = $this->getComparatorTile();
		return $tile instanceof ComparatorTile ? $tile->getOutputSignal() : 0;
	}

	protected function calculateInputStrength(){
		$power = parent::calculateInputStrength();
		$inputSide = $this->getInputSide();
		$block = $this->getSide($inputSide);

		if($block->hasComparatorInputOverride()){
			$power = $block->getComparatorInputOverride();
		}elseif($power < 15 and $block->isNormalBlock()){
			$block = $block->getSide($inputSide);
			if($block->hasComparatorInputOverride()){
				$power = $block->getComparatorInputOverride();
			}
		}

		return $power;
	}

	protected function calculateOutput(){
		$input = $this->calculateInputStrength();
		return $this->getMode() === self::MODE_SUBTRACT ? max($input - $this->getPowerOnSides(), 0) : $input;
	}

	public function shouldBePowered(){
		$input = $this->calculateInputStrength();
		if($input >= 15){
			return true;
		}
		if($input === 0){
			return false;
		}

		$sidePower = $this->getPowerOnSides();
		return $sidePower === 0 or $input >= $sidePower;
	}

	public function onActivate(Item $item, Player $player = null){
		$this->meta = $this->getMode() === self::MODE_SUBTRACT ? $this->meta - 4 : $this->meta + 4;
		$this->level->setBlock($this, $this, true, true);
		$this->onChange();
		return true;
	}

	protected function onChange(){
		$output = $this->calculateOutput();
		$tile = $this->getComparatorTile();
		$currentOutput = 0;
		if($tile instanceof ComparatorTile){
			$currentOutput = $tile->getOutputSignal();
			$tile->setOutputSignal($output);
		}

		if($currentOutput !== $output or $this->getMode() === self::MODE_COMPARE){
			$shouldBePowered = $this->shouldBePowered();
			if($this->isPowered() and !$shouldBePowered){
				$this->level->setBlock($this, $this->getUnpowered(), true, true);
			}elseif(!$this->isPowered() and $shouldBePowered){
				$this->level->setBlock($this, $this->getPowered(), true, true);
			}

			$this->level->updateAroundRedstone($this, null);
		}
	}

	public function onUpdate($type){
		if($type === Level::BLOCK_UPDATE_SCHEDULED){
			$this->onChange();
			return Level::BLOCK_UPDATE_SCHEDULED;
		}

		return parent::onUpdate($type);
	}

	public function place(Item $item, Block $block, Block $target, $face, $fx, $fy, $fz, Player $player = null){
		if(parent::place($item, $block, $target, $face, $fx, $fy, $fz, $player)){
			$this->getComparatorTile();
			$this->onUpdate(Level::BLOCK_UPDATE_REDSTONE);
			return true;
		}

		return false;
	}

	public function updateState(){
		if($this->level->isBlockTickPending($this, $this)){
			return;
		}

		$tile = $this->getComparatorTile();
		$power = $tile instanceof ComparatorTile ? $tile->getOutputSignal() : 0;
		if($this->calculateOutput() !== $power or $this->isPowered() !== $this->shouldBePowered()){
			$this->level->scheduleUpdate($this, $this->getPulseTickDelay());
		}
	}

	public function getDrops(Item $item) : array{
		return [
			[Item::COMPARATOR, 0, 1],
		];
	}
}
