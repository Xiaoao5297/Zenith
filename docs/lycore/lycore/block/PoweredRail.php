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

class PoweredRail extends Rail{

	protected $id = self::POWERED_RAIL;
	/** @var Vector3 [] */
	protected $connected = [];

	public function __construct($meta = 0){
		$this->meta = $meta;//0,1,2,3,4,5
	}

	public function getName() : string{
		return "PoweredRail";
	}

	protected function update(){

		return true;
	}

	public function onUpdate($type){
		if($type === Level::BLOCK_UPDATE_REDSTONE or $type === Level::BLOCK_UPDATE_NORMAL or $type === Level::BLOCK_UPDATE_SCHEDULED){
			if(parent::onUpdate($type) === Level::BLOCK_UPDATE_NORMAL){
				return Level::BLOCK_UPDATE_NORMAL;
			}
			$powered = $this->isRailPowered();
			if($this->isActive() !== $powered){
				$this->setActive($powered);
				$this->getLevel()->updateAround($this->getSide(Vector3::SIDE_DOWN));
				if(in_array($this->getRealMeta(), [self::SLOPED_ASCENDING_NORTH, self::SLOPED_ASCENDING_SOUTH, self::SLOPED_ASCENDING_EAST, self::SLOPED_ASCENDING_WEST], true)){
					$this->getLevel()->updateAround($this->getSide(Vector3::SIDE_UP));
				}
			}
			return $type;
		}

		return false;
	}

	protected function isRailPowered(){
		return $this->getLevel()->isBlockPowered($this) or $this->checkSurrounding($this, true, 0) or $this->checkSurrounding($this, false, 0);
	}

	protected function canPowerRailAt($x, $y, $z, $orientation, $power, $relative){
		$block = $this->getLevel()->getBlock(new Vector3($x, $y, $z));
		if(!($block instanceof static)){
			return false;
		}

		$base = $block->getRealMeta();
		if(($orientation === self::STRAIGHT_EAST_WEST and in_array($base, [self::STRAIGHT_NORTH_SOUTH, self::SLOPED_ASCENDING_NORTH, self::SLOPED_ASCENDING_SOUTH], true)) or
			($orientation === self::STRAIGHT_NORTH_SOUTH and in_array($base, [self::STRAIGHT_EAST_WEST, self::SLOPED_ASCENDING_EAST, self::SLOPED_ASCENDING_WEST], true))){
			return false;
		}

		return $this->getLevel()->isBlockPowered($block) or $this->checkSurrounding($block, $relative, $power + 1);
	}

	protected function checkSurrounding(Vector3 $pos, $relative, $power){
		if($power >= 8){
			return false;
		}

		$dx = (int) floor($pos->x);
		$dy = (int) floor($pos->y);
		$dz = (int) floor($pos->z);
		$block = $this->getLevel()->getBlock(new Vector3($dx, $dy, $dz));
		if(!($block instanceof Rail)){
			return false;
		}

		$base = null;
		$onStraight = true;
		switch($block->getRealMeta()){
			case self::STRAIGHT_EAST_WEST:
				$relative ? ++$dz : --$dz;
				$base = self::STRAIGHT_EAST_WEST;
				break;
			case self::STRAIGHT_NORTH_SOUTH:
				$relative ? --$dx : ++$dx;
				$base = self::STRAIGHT_NORTH_SOUTH;
				break;
			case self::SLOPED_ASCENDING_NORTH:
				if($relative){
					--$dx;
				}else{
					++$dx;
					++$dy;
					$onStraight = false;
				}
				$base = self::STRAIGHT_NORTH_SOUTH;
				break;
			case self::SLOPED_ASCENDING_SOUTH:
				if($relative){
					--$dx;
					++$dy;
					$onStraight = false;
				}else{
					++$dx;
				}
				$base = self::STRAIGHT_NORTH_SOUTH;
				break;
			case self::SLOPED_ASCENDING_EAST:
				if($relative){
					++$dz;
				}else{
					--$dz;
					++$dy;
					$onStraight = false;
				}
				$base = self::STRAIGHT_EAST_WEST;
				break;
			case self::SLOPED_ASCENDING_WEST:
				if($relative){
					++$dz;
					++$dy;
					$onStraight = false;
				}else{
					--$dz;
				}
				$base = self::STRAIGHT_EAST_WEST;
				break;
			default:
				return false;
		}

		return $this->canPowerRailAt($dx, $dy, $dz, $base, $power, $relative) or ($onStraight and $this->canPowerRailAt($dx, $dy - 1, $dz, $base, $power, $relative));
	}

	/**
	 * @param Rail $block
	 * @return bool
	 */
	public function canConnect(Rail $block){
		if($this->distanceSquared($block) > 2){
			return false;
		}
		/** @var Vector3 [] $blocks */
		if(count($blocks = self::check($this)) == 2){
			return false;
		}
		if(isset($blocks[0])){
			$v3 = $blocks[0]->subtract($this);
			$v33 = $block->subtract($this);
			if(abs($v3->x) == abs($v33->z) and abs($v3->z) == abs($v33->x)){
				return false;
			}
		}
		return $blocks;
	}

	public function isBlock(Block $block){
		if($block instanceof AIR){
			return false;
		}
		return $block;
	}

	public function connect(Rail $rail, $force = false){

		if(!$force){
			$connected = $this->canConnect($rail);
			if(!is_array($connected)){
				return false;
			}
			/** @var Vector3 [] $connected */
			$connected[] = $rail;
			switch(count($connected)){
				case  1:
					$v3 = $connected[0]->subtract($this);
					$this->meta = (($v3->y != 1) ? ($v3->x == 0 ? 0 : 1) : ($v3->z == 0 ? ($v3->x / -2) + 2.5 : ($v3->z / 2) + 4.5));
					break;
				case 2:
					$subtract = [];
					foreach($connected as $key => $value){
						$subtract[$key] = $value->subtract($this);
					}
					if(abs($subtract[0]->x) == abs($subtract[1]->z) and abs($subtract[1]->x) == abs($subtract[0]->z)){
						$v3 = $connected[0]->subtract($this)->add($connected[1]->subtract($this));
						$this->meta = $v3->x == 1 ? ($v3->z == 1 ? 6 : 9) : ($v3->z == 1 ? 7 : 8);
					}elseif($subtract[0]->y == 1 or $subtract[1]->y == 1){
						$v3 = $subtract[0]->y == 1 ? $subtract[0] : $subtract[1];
						$this->meta = $v3->x == 0 ? ($v3->x == -1 ? 4 : 5) : ($v3->x == 1 ? 2 : 3);
					}else{
						$this->meta = $subtract[0]->x == 0 ? 0 : 1;
					}
					break;
				default:
					break;
			}
		}
		$this->meta = (int) $this->meta;
		$this->level->setBlock($this, Block::get($this->id, $this->meta), true, true);
		return true;
	}

	public function place(Item $item, Block $block, Block $target, $face, $fx, $fy, $fz, Player $player = null){
		$downBlock = $this->getSide(Vector3::SIDE_DOWN);

		if($downBlock instanceof Rail or !$this->isBlock($downBlock)){//判断是否可以放置
			return false;
		}

		$arrayXZ = [[1, 0], [0, 1], [-1, 0], [0, -1]];
		$arrayY = [0, 1, -1];

		/** @var Vector3 [] $connected */
		$connected = [];
		foreach($arrayXZ as $key => $xz){
			foreach($arrayY as $y){
				$v3 = (new Vector3($xz[0], $y, $xz[1]))->add($this);
				$block = $this->level->getBlock($v3);
				if($block instanceof Rail){
					if($block->connect($this)){
						$connected[] = $v3;
						//感觉这里怪怪的
						if($key <= 1){
							$xz = $arrayXZ[$key + 1];
							foreach($arrayY as $yy){
								$v3 = (new Vector3($xz[0], $yy, $xz[1]))->add($this);
								$block = $this->level->getBlock($v3);
								if($block instanceof Rail){
									if($block->connect($this)){
										$connected[] = $v3;
									}
								}
							}
						}
						break;
					}
				}
			}
			if(count($connected) >= 1){
				break;
			}
		}
		switch(count($connected)){
			case  1:
				$v3 = $connected[0]->subtract($this);
				$this->meta = (($v3->y != 1) ? ($v3->x == 0 ? 0 : 1) : ($v3->z == 0 ? ($v3->x / -2) + 2.5 : ($v3->z / 2) + 4.5));
				break;
			case 2:
				$subtract = [];
				foreach($connected as $key => $value){
					$subtract[$key] = $value->subtract($this);
				}
				if(abs($subtract[0]->x) == abs($subtract[1]->z) and abs($subtract[1]->x) == abs($subtract[0]->z)){
					$v3 = $connected[0]->subtract($this)->add($connected[1]->subtract($this));
					$this->meta = $v3->x == 1 ? ($v3->z == 1 ? 6 : 9) : ($v3->z == 1 ? 7 : 8);
				}elseif($subtract[0]->y == 1 or $subtract[1]->y == 1){
					$v3 = $subtract[0]->y == 1 ? $subtract[0] : $subtract[1];
					$this->meta = $v3->x == 0 ? ($v3->x == -1 ? 4 : 5) : ($v3->x == 1 ? 2 : 3);
				}else{
					$this->meta = $subtract[0]->x == 0 ? 0 : 1;
				}
				break;
			default:
				break;
		}

		$this->meta = (int) $this->meta;
		$this->level->setBlock($this, Block::get($this->id, $this->meta), true, true);
		return true;
	}

	public function getHardness() {
		return 0.7;
	}

	public function canPassThrough(){
		return true;
	}
}
