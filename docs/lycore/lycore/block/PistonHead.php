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

class PistonHead extends Transparent{
	const META_FACING_MASK = 0x07;
	const META_STICKY = 0x08;

	protected $id = self::PISTON_HEAD;

	public function __construct($meta = 0){
		$this->meta = $meta & 0x0f;
	}

	public function getName() : string{
		return $this->isSticky() ? "Sticky Piston Head" : "Piston Head";
	}

	public function getHardness(){
		return 1.5;
	}

	public function getResistance(){
		return 1.5;
	}

	public function isSolid(){
		return false;
	}

	public function getFacing(){
		$facing = $this->meta & self::META_FACING_MASK;
		if($facing < Vector3::SIDE_DOWN or $facing > Vector3::SIDE_EAST){
			return Vector3::SIDE_NORTH;
		}
		return self::isHorizontalFacing($facing) ? Vector3::getOppositeSide($facing) : $facing;
	}

	public function setFacing($facing){
		$stored = self::logicFacingToMeta($facing);
		$this->meta = ($this->meta & self::META_STICKY) | ($stored & self::META_FACING_MASK);
	}

	public function isSticky(){
		return ($this->meta & self::META_STICKY) === self::META_STICKY;
	}

	public function setSticky($sticky){
		if($sticky){
			$this->meta |= self::META_STICKY;
		}else{
			$this->meta &= self::META_FACING_MASK;
		}
	}

	public function canBePushedByPiston(){
		return false;
	}

	public function canBePulledByPiston(){
		return false;
	}

	public function onBreak(Item $item){
		$facing = $this->getFacing();
		$this->getLevel()->setBlock($this, new Air(), true, true);

		$piston = $this->getSide(Vector3::getOppositeSide($facing));
		if($piston instanceof PistonBase and $piston->getFacing() === $facing){
			$piston->onBreak($item);
		}

		return true;
	}

	public function onUpdate($type){
		if($type === Level::BLOCK_UPDATE_NORMAL or $type === Level::BLOCK_UPDATE_SCHEDULED){
			$piston = $this->getSide(Vector3::getOppositeSide($this->getFacing()));
			if(!$piston instanceof PistonBase or $piston->getFacing() !== $this->getFacing() or !$piston->isExtended()){
				$this->getLevel()->setBlock($this, new Air(), true, false);
			}
			return $type;
		}

		return false;
	}

	public function getDrops(Item $item) : array{
		return [
			$this->isSticky() ? [Item::STICKY_PISTON, 0, 1] : [Item::PISTON, 0, 1]
		];
	}

	private static function isHorizontalFacing($facing){
		return $facing === Vector3::SIDE_NORTH or $facing === Vector3::SIDE_SOUTH or $facing === Vector3::SIDE_WEST or $facing === Vector3::SIDE_EAST;
	}

	private static function logicFacingToMeta($facing){
		$facing = (int) $facing;
		if($facing < Vector3::SIDE_DOWN or $facing > Vector3::SIDE_EAST){
			return Vector3::SIDE_NORTH;
		}
		return self::isHorizontalFacing($facing) ? Vector3::getOppositeSide($facing) : $facing;
	}
}
