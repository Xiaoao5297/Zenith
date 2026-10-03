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
use lycore\level\weather\Weather;
use lycore\math\Vector3;
use lycore\item\Tool;
use lycore\math\AxisAlignedBB;

class Farmland extends Solid{

	protected $id = self::FARMLAND;

	public function __construct($meta = 0){
		$this->meta = $meta;
	}

	public function getName() : string{
		return "Farmland";
	}

	public function getHardness() {
		return 0.6;
	}

	public function getToolType(){
		return Tool::TYPE_SHOVEL;
	}

	protected function recalculateBoundingBox() {
		return new AxisAlignedBB(
			$this->x,
			$this->y,
			$this->z,
			$this->x + 1,
			$this->y + 1,
			$this->z + 1
		);
	}

	public function getDrops(Item $item) : array {
		return [
			[Item::DIRT, 0, 1],
		];
	}

	public function onUpdate($type){
        if($type === Level::BLOCK_UPDATE_NORMAL and $this->getSide(Vector3::SIDE_UP)->isSolid()){
            $this->turnIntoDirt();
            return $type;
        }elseif($type === Level::BLOCK_UPDATE_RANDOM){
            if(!$this->canHydrate()){
                if($this->meta > 0){
                    $this->meta--;
                    $this->level->setBlock($this, $this, false, false);
                }else{
                    $this->turnIntoDirt(false, true);
                }

                return $type;
            }elseif($this->meta < 7){
                $this->meta = 7;
                $this->level->setBlock($this, $this, false, false);

                return $type;
            }
        }

        return false;
    }

	public function turnIntoDirt(bool $update = true, bool $notify = true){
		$up = $this->getSide(Vector3::SIDE_UP);
		if($up instanceof Crops){
			$this->level->useBreakOn($up);
		}

		$this->level->setBlock($this, Block::get(Block::DIRT), $update, $notify);
	}
	
    protected function canHydrate() : bool{
        if($this->level->getWeather()->getWeather() == Weather::RAINY){
            return true;
        }
        $start = $this->add(-4, 0, -4);
        $end = $this->add(4, 1, 4);
        for($y = $start->y; $y <= $end->y; ++$y){
            for($z = $start->z; $z <= $end->z; ++$z){
                for($x = $start->x; $x <= $end->x; ++$x){
                    $id = $this->level->getBlockIdAt($x, $y, $z);
                    if($id === Block::STILL_WATER or $id === Block::FLOWING_WATER){
                        return true;
                    }
                }
            }
        }

        return false;
    }
}
