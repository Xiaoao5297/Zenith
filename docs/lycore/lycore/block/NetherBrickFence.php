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
use lycore\item\Tool;
use lycore\math\AxisAlignedBB;
use lycore\math\Vector3;

class NetherBrickFence extends Transparent {
	
	protected $id = self::NETHER_BRICK_FENCE;
	
	public function __construct($meta = 0){
		$this->meta = $meta;
	}
	
	public function getBreakTime(Item $item){
		if ($item instanceof Air){
			//Breaking by hand
			return 10;
		}
		else{
			// Other breaktimes are equal to woodfences.
			return parent::getBreakTime($item);
		}
	}
	
	public function getHardness() {
		return 2;
	}
        
	public function getToolType(){
		//Different then the woodfences
		return Tool::TYPE_PICKAXE;
	}
	
	public function getName() : string{
		return "Nether Brick Fence";
	}
	
	public function canConnect(Block $block){
		//TODO: activate comments when the NetherBrickFenceGate class has been created.
		return ($block instanceof NetherBrickFence /* or $block instanceof NetherBrickFenceGate */) ? true : $block->isSolid() and !$block->isTransparent();
	}

	protected function recalculateBoundingBox() {
		$north = $this->canConnect($this->getSide(Vector3::SIDE_NORTH));
		$south = $this->canConnect($this->getSide(Vector3::SIDE_SOUTH));
		$west = $this->canConnect($this->getSide(Vector3::SIDE_WEST));
		$east = $this->canConnect($this->getSide(Vector3::SIDE_EAST));

		$n = $north ? 0 : 0.375;
		$s = $south ? 1 : 0.625;
		$w = $west ? 0 : 0.375;
		$e = $east ? 1 : 0.625;

		return new AxisAlignedBB(
			$this->x + $w,
			$this->y,
			$this->z + $n,
			$this->x + $e,
			$this->y + 1.5,
			$this->z + $s
		);
	}
	
	public function getDrops(Item $item) : array {
		if($item->isPickaxe() >= Tool::TIER_WOODEN){
			return [
				[Item::NETHER_BRICK_FENCE, $this->meta, 1],
			];
		}else{
			return [];
		}
	}
}
