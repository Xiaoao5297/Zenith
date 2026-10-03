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

class DoublePlant extends Flowable{

	protected $id = self::DOUBLE_PLANT;

	public function __construct($meta = 0){
		$this->meta = $meta;
	}

	public function canBeReplaced(){
		return true;
	}

	public function getName() : string{
		static $names = [
			0 => "Sunflower",
			1 => "Lilac",
			2 => "Double Tallgrass",
			3 => "Large Fern",
			4 => "Rose Bush",
			5 => "Peony"
		];
		return $names[$this->meta & 0x07];
	}

	public function onUpdate($type){
		if($type === Level::BLOCK_UPDATE_NORMAL){
			if($this->shouldBreak()){
				$this->breakHalves();

				return Level::BLOCK_UPDATE_NORMAL;
			}
		}

		return false;
	}

	public function place(Item $item, Block $block, Block $target, $face, $fx, $fy, $fz, Player $player = null){
		$down = $this->getSide(0);
		$up = $this->getSide(1);
		if($up->getId() != self::AIR){
			$this->getLevel()->setBlock($block, Block::get(0, 0));
			return false;
		}
		if($down->getId() === self::GRASS or $down->getId() === self::DIRT){
			$this->getLevel()->setBlock($block, $this, false);
			$this->getLevel()->setBlock($up, Block::get($this->id, $this->meta ^ 0x08), false);
			return true;
		}
		return false;
	}

	public function onBreak(Item $item){
		$this->breakHalves();
	}

	public function getDrops(Item $item) : array{
		if(($this->meta & 0x08) === 0x08){
			return [];
		}

		$type = $this->meta & 0x07;
		if($type === 2 || $type === 3){
			if(mt_rand(0, 15) === 0){
				return [[Item::WHEAT_SEEDS, 0, 1]];
			}

			return [];
		}

		return [[Item::DOUBLE_PLANT, $type, 1]];
	}

	private function shouldBreak(){
		if(($this->meta & 0x08) === 0x08){
			$down = $this->getSide(0);
			return !($down instanceof DoublePlant) or $down->getId() !== $this->id or ($down->meta & 0x08) === 0x08;
		}

		return $this->getSide(0)->isTransparent() === true;
	}

	private function breakHalves(){
		$up = $this->getSide(1);
		$down = $this->getSide(0);
		$this->getLevel()->setBlock($this, new Air(), false, false);

		if(($this->meta & 0x08) === 0x08){
			if($down->getId() === $this->id and ($down->meta & 0x08) !== 0x08){
				$this->getLevel()->setBlock($down, new Air(), false, false);
			}
		}else{
			if($up->getId() === $this->id and ($up->meta & 0x08) === 0x08){
				$this->getLevel()->setBlock($up, new Air(), false, false);
			}
		}
	}
}
